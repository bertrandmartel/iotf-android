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
package fr.bmartel.android.iotf.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import fr.bmartel.android.iotf.handler.AppHandler;
import fr.bmartel.android.iotf.listener.IMessageCallback;

/**
 * @author Bertrand Martel
 */
public class NotificationActivity extends AppCompatActivity {

    private static final String TAG = NotificationActivity.class.getSimpleName();

    private AppHandler mHandler;

    private boolean exit = false;

    private IMessageCallback mIotCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        mHandler = new AppHandler(this, BuildConfig.BLUEMIX_IOT_ORG, getPackageName(), BuildConfig.BLUEMIX_API_KEY, BuildConfig.BLUEMIX_API_TOKEN);

        mIotCallback = new IMessageCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.i(TAG, "connectionLost");
                if (cause != null) {
                    Log.e(TAG, "connection lost : " + cause.getMessage());
                }
                if (!exit) {
                    Log.i(TAG, "trying to reconnect");
                    mHandler.connect();
                } else {
                    Log.i(TAG, "not trying to reconnect");
                }
            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.i(TAG, "messageArrived : " + topic + " : " + new String(mqttMessage.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken messageToken) {
                try {
                    Log.i(TAG, "deliveryComplete : " + new String(messageToken.getMessage().getPayload()));
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onConnectionSuccess() {
                Log.i(TAG, "subscribe to device events ...");
                mHandler.subscribeDeviceEvents("+", "+", "+");
            }

            @Override
            public void onConnectionFailure() {

            }

            @Override
            public void onDisconnectionSuccess() {

                if (exit) {
                    mHandler.removeCallback(mIotCallback);
                }
            }

            @Override
            public void onDisconnectionFailure() {

            }
        };

        mHandler.addIotCallback(mIotCallback);

        mHandler.setSSL(true);
    }

    public void onResume() {
        super.onResume();
        Log.d(TAG, "connecting");
        mHandler.connect();
    }

    public void onPause() {
        exit = true;
        Log.d(TAG, "disconnecting");
        mHandler.disconnect();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "application finished");
        super.onDestroy();
    }

}
