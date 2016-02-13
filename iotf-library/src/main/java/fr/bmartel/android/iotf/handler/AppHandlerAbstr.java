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

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;

import fr.bmartel.android.iotf.constant.MqttConst;
import fr.bmartel.android.iotf.constant.QosPolicy;
import fr.bmartel.android.iotf.listener.IAppHandler;

/**
 * Abstract Application Handler for Application and Scalable Application
 *
 * @author Bertrand Martel
 */
public abstract class AppHandlerAbstr extends IotHandlerAbstr implements IAppHandler {

    /**
     * app_id is a user-defined unique string identifier for this client
     */
    protected String mAppId = "";

    /**
     * api key generated on https://<your orgid>.internetofthings.ibmcloud.com/dashboard
     */
    protected String mApiKey = "";

    /**
     * authentication token used with api key above
     */
    protected String mAuthenticationToken = "";

    /**
     * Create an Application handler
     *
     * @param orgId               org_id is your unique organization ID, assigned when you sign up with the service. It will be a 6 character alphanumeric string
     * @param appId               app_id is a user-defined unique string identifier for this client
     * @param apiKey              api key generated on https://<your orgid>.internetofthings.ibmcloud.com/dashboard
     * @param authenticationToken authentication token used with api key above
     */
    public AppHandlerAbstr(Context context, String orgId, String appId, String apiKey, String authenticationToken) {
        super(context, orgId);
        mAppId = appId;
        mApiKey = apiKey;
        mAuthenticationToken = authenticationToken;
        buildClientId();
        buildAuthParams();
    }

    @Override
    public void buildAuthParams() {
        mUsername = mApiKey;
        mPassword = mAuthenticationToken;
    }

    /**
     * An application can publish events as if they came from any registered device
     *
     * @param deviceType         device model number
     * @param deviceId           device serial number
     * @param eventId            event identifier
     * @param message            message body
     * @param isRetained         define if message should be retained on MQTT server
     * @param qos                define quality of service (check QosPolicy enum)
     * @param completionListener completion listener (null allowed)
     */
    public IMqttDeliveryToken publishDeviceEvents(String deviceType, String deviceId, String eventId, String message, boolean isRetained, QosPolicy qos, IMqttActionListener completionListener) {
        String topic = MqttConst.TOPIC_PREFIX + "/type/" + deviceType + "/id/" + deviceId + "/evt/" + eventId + "/fmt/" + mMessageFormat.getValue();
        return super.publishMessage(topic, message, isRetained, qos, completionListener);
    }

    /**
     * An application can publish events as if they came from any registered device
     *
     * @param deviceType device model number
     * @param deviceId   device serial number
     * @param eventId    event identifier
     * @param message    message body
     */
    public IMqttDeliveryToken publishDeviceEvents(String deviceType, String deviceId, String eventId, String message) {
        return publishDeviceEvents(deviceType, deviceId, eventId, message, false, mQosDefault, null);
    }

    /**
     * An application can publish a command to any registered device
     *
     * @param deviceType         device model number
     * @param deviceId           device serial number
     * @param commandId          command identifier
     * @param message            message body
     * @param isRetained         define if message should be retained on MQTT server
     * @param qos                define quality of service (check QosPolicy enum)
     * @param completionListener completion listener (null allowed)
     */
    public IMqttDeliveryToken publishDeviceCommands(String deviceType, String deviceId, String commandId, String message, boolean isRetained, QosPolicy qos, IMqttActionListener completionListener) {
        String topic = MqttConst.TOPIC_PREFIX + "/type/" + deviceType + "/id/" + deviceId + "/cmd/" + commandId + "/fmt/" + mMessageFormat.getValue();
        return super.publishMessage(topic, message, isRetained, qos, completionListener);
    }

    /**
     * An application can publish a command to any registered device
     *
     * @param deviceType device model number
     * @param deviceId   device serial number
     * @param commandId  command identifier
     * @param message    message body
     */
    public IMqttDeliveryToken publishDeviceCommands(String deviceType, String deviceId, String commandId, String message) {
        return publishDeviceCommands(deviceType, deviceId, commandId, message, false, mQosDefault, null);
    }


    /**
     * An application can subscribe to events from one or more devices
     *
     * @param deviceType         device model number
     * @param deviceId           device serial number
     * @param eventId            event identifier
     * @param qos                define quality of service (check QosPolicy enum)
     * @param completionListener completion listener (null allowed)
     */
    public void subscribeDeviceEvents(String deviceType, String deviceId, String eventId, QosPolicy qos, IMqttActionListener completionListener) {
        String topic = MqttConst.TOPIC_PREFIX + "/type/" + deviceType + "/id/" + deviceId + "/evt/" + eventId + "/fmt/" + mMessageFormat.getValue();
        super.subscribe(topic, qos, completionListener);
    }

    /**
     * An application can subscribe to events from one or more devices
     *
     * @param deviceType device model number
     * @param deviceId   device serial number
     * @param eventId    event identifier
     */
    public void subscribeDeviceEvents(String deviceType, String deviceId, String eventId) {
        subscribeDeviceEvents(deviceType, deviceId, eventId, mQosDefault, null);
    }

    /**
     * Unsubscribe from device events topic
     *
     * @param deviceType         device model number
     * @param deviceId           device serial number
     * @param eventId            event identifier
     * @param completionListener completion listener (null allowed)
     */
    public void unsubscribeDeviceEvents(String deviceType, String deviceId, String eventId, IMqttActionListener completionListener) {
        String topic = MqttConst.TOPIC_PREFIX + "/type/" + deviceType + "/id/" + deviceId + "/evt/" + eventId + "/fmt/" + mMessageFormat.getValue();
        super.unsubscribe(topic, completionListener);
    }

    /**
     * Unsubscribe from device events topic
     *
     * @param deviceType device model number
     * @param deviceId   device serial number
     * @param eventId    event identifier
     */
    public void unsubscribeDeviceEvents(String deviceType, String deviceId, String eventId) {
        unsubscribeDeviceEvents(deviceType, deviceId, eventId, null);
    }

    /**
     * An application can subscribe to commands being sent to one or more devices
     *
     * @param deviceType         device model number
     * @param deviceId           device serial number
     * @param commandId          command identifier
     * @param qos                define quality of service (check QosPolicy enum)
     * @param completionListener completion listener (null allowed)
     */
    public void subscribeDeviceCommands(String deviceType, String deviceId, String commandId, QosPolicy qos, IMqttActionListener completionListener) {
        String topic = MqttConst.TOPIC_PREFIX + "/type/" + deviceType + "/id/" + deviceId + "/cmd/" + commandId + "/fmt/" + mMessageFormat.getValue();
        super.subscribe(topic, qos, completionListener);
    }


    /**
     * An application can subscribe to commands being sent to one or more devices
     *
     * @param deviceType device model number
     * @param deviceId   device serial number
     * @param commandId  command identifier
     */
    public void subscribeDeviceCommands(String deviceType, String deviceId, String commandId) {
        subscribeDeviceCommands(deviceType, deviceId, commandId, mQosDefault, null);
    }

    /**
     * Unsubscribe device commands topic
     *
     * @param deviceType         device model number
     * @param deviceId           device serial number
     * @param commandId          command identifier
     * @param completionListener completion listener (null allowed)
     */
    public void unsubscribeDeviceCommands(String deviceType, String deviceId, String commandId, IMqttActionListener completionListener) {
        String topic = MqttConst.TOPIC_PREFIX + "/type/" + deviceType + "/id/" + deviceId + "/cmd/" + commandId + "/fmt/" + mMessageFormat.getValue();
        super.unsubscribe(topic, completionListener);
    }

    /**
     * Unsubscribe device commands topic
     *
     * @param deviceType device model number
     * @param deviceId   device serial number
     * @param commandId  command identifier
     */
    public void unsubscribeDeviceCommands(String deviceType, String deviceId, String commandId) {
        unsubscribeDeviceCommands(deviceType, deviceId, commandId, null);
    }

    /**
     * An application can subscribe to monitor status of one or more devices
     *
     * @param deviceType         device model number
     * @param deviceId           device serial number
     * @param qos                define quality of service (check QosPolicy enum)
     * @param completionListener completion listener (null allowed)
     */
    public void subscribeDeviceStatus(String deviceType, String deviceId, QosPolicy qos, IMqttActionListener completionListener) {
        String topic = MqttConst.TOPIC_PREFIX + "/type/" + deviceType + "/id/" + deviceId + "/mon";
        super.subscribe(topic, qos, completionListener);
    }

    /**
     * An application can subscribe to monitor status of one or more devices
     *
     * @param deviceType device model number
     * @param deviceId   device serial number
     */
    public void subscribeDeviceStatus(String deviceType, String deviceId) {
        subscribeDeviceStatus(deviceType, deviceId, mQosDefault, null);
    }

    /**
     * Unsubscribe device status topic
     *
     * @param deviceType         device model number
     * @param deviceId           device serial number
     * @param completionListener completion listener (null allowed)
     */
    public void unsubscribeDeviceStatus(String deviceType, String deviceId, IMqttActionListener completionListener) {
        String topic = MqttConst.TOPIC_PREFIX + "/type/" + deviceType + "/id/" + deviceId + "/mon";
        super.unsubscribe(topic, completionListener);
    }

    /**
     * Unsubscribe device status topic
     *
     * @param deviceType device model number
     * @param deviceId   device serial number
     */
    public void unsubscribeDeviceStatus(String deviceType, String deviceId) {
        unsubscribeDeviceStatus(deviceType, deviceId, null);
    }

    /**
     * An application can subscribe to monitor status of one or more applications
     *
     * @param appId              application identifier
     * @param qos                define quality of service (check QosPolicy enum)
     * @param completionListener completion listener (null allowed)
     */
    public void subscribeApplicationStatus(String appId, QosPolicy qos, IMqttActionListener completionListener) {
        String topic = MqttConst.TOPIC_PREFIX + "/app/" + appId + "/mon";
        super.subscribe(topic, qos, completionListener);
    }

    /**
     * An application can subscribe to monitor status of one or more applications
     *
     * @param appId application identifier
     */
    public void subscribeApplicationStatus(String appId) {
        subscribeApplicationStatus(appId, mQosDefault, null);
    }

    /**
     * Unsubscribe application status topic
     *
     * @param appId              application id
     * @param completionListener completion listener (null allowed)
     */
    public void unsubscribeApplicationStatus(String appId, IMqttActionListener completionListener) {
        String topic = MqttConst.TOPIC_PREFIX + "/app/" + appId + "/mon";
        super.unsubscribe(topic, completionListener);
    }

    /**
     * Unsubscribe application status topic
     *
     * @param appId application id
     */
    public void unsubscribeApplicationStatus(String appId) {
        unsubscribeApplicationStatus(appId, null);
    }

}
