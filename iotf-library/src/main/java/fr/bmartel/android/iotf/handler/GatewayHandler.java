/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Bertrand Martel
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package fr.bmartel.android.iotf.handler;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;

import fr.bmartel.android.iotf.constant.MqttConst;
import fr.bmartel.android.iotf.constant.QosPolicy;

/**
 * Gateway handler
 * <p/>
 * https://docs.internetofthings.ibmcloud.com/gateways/mqtt.html
 *
 * @author Bertrand Martel
 */
public class GatewayHandler extends IotHandlerAbstr {

    /**
     * typeId is intended to be used as an identifier of the type of gateway connecting, it may be useful to think of this as analogous to a model number
     */
    private String mTypeId = "";

    /**
     * deviceId must uniquely identify a gateway device across all gateways of a specific type, it may be useful to think of this as analogous to a serial number
     */
    private String mDeviceId = "";

    /**
     * token used for authentication
     */
    private String mAuthenticationToken = "";

    /**
     * Gateway Handler
     *
     * @param orgId               org_id is your unique organization ID, assigned when you sign up with the service. It will be a 6 character alphanumeric string
     * @param typeId              typeId is intended to be used as an identifier of the type of gateway connecting, it may be useful to think of this as analogous to a model number
     * @param deviceId            deviceId must uniquely identify a gateway device across all gateways of a specific type, it may be useful to think of this as analogous to a serial number
     * @param authenticationToken authentication token used with api key above
     */
    public GatewayHandler(Context context, String orgId, String typeId, String deviceId, String authenticationToken) {
        super(context, orgId);

        if (!isValid(typeId)) {
            Log.e(TAG, "Device type has incorrect syntax : Maximum length of 36 characters | Must comprise only alpha-numeric characters (a-z, A-Z, 0-9) and the following special characters : - / _ / .");
            return;
        }
        if (!isValid(deviceId)) {
            Log.e(TAG, "Device ID has incorrect syntax : Maximum length of 36 characters | Must comprise only alpha-numeric characters (a-z, A-Z, 0-9) and the following special characters : - / _ / .");
            return;
        }

        mTypeId = typeId;
        mDeviceId = deviceId;
        mAuthenticationToken = authenticationToken;
        buildClientId();
    }

    public void buildClientId() {
        mClientId = MqttConst.GATEWAY_PREFIX + ":" + mOrgId + ":" + mTypeId + ":" + mDeviceId;
    }

    public void buildAuthParams() {
        mUsername = MqttConst.USERNAME_TOKEN;
        mPassword = mAuthenticationToken;
    }

    /**
     * Publish an event for this Gateway
     *
     * @param eventId            event identifier
     * @param message            message body
     * @param qos                define quality of service (check QosPolicy enum)
     * @param completionListener completion listener (null allowed)
     */
    public IMqttDeliveryToken publishOwnEvents(String eventId, String message, QosPolicy qos, IMqttActionListener completionListener) {
        String topic = MqttConst.TOPIC_PREFIX + "/type/" + mTypeId + "/id/" + mDeviceId + "/evt/" + eventId + "/fmt/" + mMessageFormat.getValue();
        return super.publishMessage(topic, message, false, qos, completionListener);
    }

    /**
     * Publish an event for this Gateway
     *
     * @param eventId event identifier
     * @param message message body
     */
    public IMqttDeliveryToken publishOwnEvents(String eventId, String message) {
        return publishOwnEvents(eventId, message, mQosDefault, null);
    }

    /**
     * Publish an event on behalf of a device
     *
     * @param eventId            event identifier
     * @param message            message body
     * @param qos                define quality of service (check QosPolicy enum)
     * @param completionListener completion listener (null allowed)
     */
    public IMqttDeliveryToken publishDeviceEvents(String deviceType, String deviceId, String eventId, String message, QosPolicy qos, IMqttActionListener completionListener) {
        String topic = MqttConst.TOPIC_PREFIX + "/type/" + deviceType + "/id/" + deviceId + "/evt/" + eventId + "/fmt/" + mMessageFormat.getValue();
        return super.publishMessage(topic, message, false, qos, completionListener);
    }

    /**
     * Publish an event on behalf of a device
     *
     * @param eventId event identifier
     * @param message message body
     */
    public IMqttDeliveryToken publishDeviceEvents(String deviceType, String deviceId, String eventId, String message) {
        return publishDeviceEvents(deviceType, deviceId, eventId, message, mQosDefault, null);
    }

    /**
     * Subscribe to command topic for a specific device (or for the gateway itself)
     *
     * @param deviceType         device model number
     * @param deviceId           device serial number
     * @param commandId          command identifier
     * @param qos                define quality of service (check QosPolicy enum)
     * @param completionListener completion listener (null allowed)
     */
    public void subscribeCommands(String deviceType, String deviceId, String commandId, QosPolicy qos, IMqttActionListener completionListener) {
        String topic = MqttConst.TOPIC_PREFIX + "/type/" + deviceType + "/id/" + deviceId + "/cmd/" + commandId + "/fmt/" + mMessageFormat.getValue();
        super.subscribe(topic, qos, completionListener);
    }

    /**
     * Subscribe to command topic for a specific device (or for the gateway itself)
     *
     * @param deviceType device model number
     * @param deviceId   device serial number
     * @param commandId  command identifier
     */
    public void subscribeCommands(String deviceType, String deviceId, String commandId) {
        subscribeCommands(deviceType, deviceId, commandId, mQosDefault, null);
    }

    /**
     * Unsubscribe command topic
     *
     * @param deviceType         device model number
     * @param deviceId           device serial number
     * @param commandId          command identifier
     * @param completionListener completion listener (null allowed)
     */
    public void unsubscribeCommands(String deviceType, String deviceId, String commandId, IMqttActionListener completionListener) {
        String topic = MqttConst.TOPIC_PREFIX + "/type/" + deviceType + "/id/" + deviceId + "/cmd/" + commandId + "/fmt/" + mMessageFormat.getValue();
        super.unsubscribe(topic, completionListener);
    }

    /**
     * Unsubscribe command topic
     *
     * @param deviceType device model number
     * @param deviceId   device serial number
     * @param commandId  command identifier
     */
    public void unsubscribeCommands(String deviceType, String deviceId, String commandId) {
        unsubscribeCommands(deviceType, deviceId, commandId, null);
    }

}
