/**
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2016 Bertrand Martel
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package fr.bmartel.android.iotf.listener;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * interface between applications and iotf-library
 *
 * @author Bertrand Martel
 */
public interface IMessageCallback {

    /**
     * This method is called when the connection to the server is lost.
     *
     * @param cause the reason behind the loss of connection.
     */
    void connectionLost(Throwable cause);

    /**
     * This method is called when a message arrives from the server.
     *
     * @param topic       name of the topic on the message was published to
     * @param mqttMessage the actual message
     * @throws Exception
     */
    void messageArrived(String topic, MqttMessage mqttMessage) throws Exception;

    /**
     * Called when delivery for a message has been completed, and all acknowledgments have been received.
     *
     * @param messageToken he delivery token associated with the message.
     */
    void deliveryComplete(IMqttDeliveryToken messageToken);

    /**
     * Called when connection is established
     *
     * @param iMqttToken token for this connection
     */
    void onConnectionSuccess(IMqttToken iMqttToken);

    /**
     * Called when connection has failed
     *
     * @param iMqttToken token when failure occured
     * @param throwable  exception
     */
    void onConnectionFailure(IMqttToken iMqttToken, Throwable throwable);

    /**
     * Called when disconnection is successfull
     *
     * @param iMqttToken token for this connection
     */
    void onDisconnectionSuccess(IMqttToken iMqttToken);

    /**
     * Called when disconnection failed
     *
     * @param iMqttToken token when failure occured
     * @param throwable  exception
     */
    void onDisconnectionFailure(IMqttToken iMqttToken, Throwable throwable);
}
