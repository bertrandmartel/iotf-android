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
package fr.bmartel.android.iotf.app.singleton;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import fr.bmartel.android.iotf.handler.AppHandler;
import fr.bmartel.android.iotf.listener.IMessageCallback;

/**
 * Singleton for Iot Foundation wrapper
 *
 * @author Bertrand Martel
 */
public class IotSingleton {

    private String TAG = IotSingleton.class.getSimpleName();

    private static IotSingleton mInstance;

    private Context mContext;

    /**
     * IoT Foundation Application Handler
     */
    private AppHandler mHandler;

    /**
     * control for auto reconnection
     */
    private boolean exit = false;

    /**
     * define if app will reconnect automatically if connection is lost
     */
    private boolean reconnectAuto = true;

    /**
     * reconnection interval in second
     */
    private static int RECONNECT_INTERVAL = 1;

    /**
     * callback for IoT Foundation wrapper
     */
    private IMessageCallback mIotCallback;


    public static IotSingleton getInstance(Context context) {
        if (mInstance == null)
            mInstance = new IotSingleton(context);
        return mInstance;
    }

    private IMessageCallback mInternalCb;

    private IotSingleton(Context context) {
        this.mContext = context.getApplicationContext();
    }

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    /**
     * Connect as an application with defined connection params
     *
     * @param appID         application ID
     * @param orgID         organization ID
     * @param apiKey        API KEY
     * @param apiToken      API TOKEN
     * @param useSSL        define if SSL is used or plain HTTP
     * @param reconnectAuto define if app will reconnect if connection is lost
     */
    public void setupApplication(String appID, String orgID, String apiKey, String apiToken, boolean useSSL, boolean reconnectAuto) {

        this.reconnectAuto = reconnectAuto;

        disconnect(false);
        if (mIotCallback != null) {
            mHandler.removeCallback(mIotCallback);
        }

        mHandler = new AppHandler(mContext, orgID, appID, apiKey, apiToken);

        mIotCallback = new IMessageCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.i(TAG, "connectionLost");

                if (mInternalCb != null)
                    mInternalCb.connectionLost(cause);

                if (cause != null) {
                    Log.e(TAG, "connection lost : " + cause.getMessage());
                }
                if (!exit && IotSingleton.this.reconnectAuto) {

                    scheduler.schedule(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(TAG, "trying to reconnect");
                            mHandler.connect();
                        }
                    }, RECONNECT_INTERVAL, TimeUnit.SECONDS);

                } else {
                    Log.i(TAG, "not trying to reconnect");
                }
            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                if (mInternalCb != null)
                    mInternalCb.messageArrived(topic, mqttMessage);
                Log.i(TAG, "messageArrived : " + topic + " : " + new String(mqttMessage.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken messageToken) {
                if (mInternalCb != null)
                    mInternalCb.deliveryComplete(messageToken);
                try {
                    Log.i(TAG, "deliveryComplete : " + new String(messageToken.getMessage().getPayload()));
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onConnectionSuccess(IMqttToken token) {
                if (mInternalCb != null)
                    mInternalCb.onConnectionSuccess(token);
                exit = false;
                Log.i(TAG, "subscribe to device events ...");
                mHandler.subscribeDeviceEvents("+", "+", "+");
            }

            @Override
            public void onConnectionFailure(IMqttToken token, Throwable throwable) {
                if (mInternalCb != null)
                    mInternalCb.onConnectionFailure(token, throwable);
            }

            @Override
            public void onDisconnectionSuccess(IMqttToken token) {
                if (mInternalCb != null)
                    mInternalCb.onDisconnectionSuccess(token);
                if (exit) {
                    mHandler.removeCallback(mIotCallback);
                }
            }

            @Override
            public void onDisconnectionFailure(IMqttToken token, Throwable throwable) {
                if (mInternalCb != null)
                    mInternalCb.onDisconnectionFailure(token, throwable);
            }
        };

        mHandler.addIotCallback(mIotCallback);

        mHandler.setSSL(useSSL);
    }

    public void setInternalCb(IMessageCallback callback) {
        mInternalCb = callback;
    }

    /**
     * connect to MQTT server
     *
     * @return
     */
    public boolean connect() {

        if (mHandler != null) {
            mHandler.connect();
            return true;
        }
        return false;
    }

    /**
     * disconnect from MQTT server
     *
     * @param reconnect define if app will reconnect after disconnection
     * @return
     */
    public boolean disconnect(boolean reconnect) {

        if (!reconnect)
            exit = true;
        if (mHandler != null && mHandler.isConnected()) {
            mHandler.disconnect();
            return true;
        }
        return false;
    }

    public boolean isAutoReconnect() {
        return reconnectAuto;
    }
}