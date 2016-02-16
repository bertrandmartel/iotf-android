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
 * Created by akinaru on 15/02/16.
 */
public class IotSingleton {

    private String TAG = IotSingleton.class.getSimpleName();

    private static IotSingleton mInstance;

    private Context mContext;

    private AppHandler mHandler;

    private boolean exit = false;

    private boolean reconnectAuto = true;

    private static int RECONNECT_INTERVAL = 1;

    private IMessageCallback mIotCallback;
    private boolean autoReconnect;

    public static IotSingleton getInstance(Context context) {
        if (mInstance == null)
            mInstance = new IotSingleton(context);
        return mInstance;
    }

    private IMessageCallback mInternalCb;

    private IotSingleton(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public void setupDevice(String orgID, String deviceType, String deviceId, String authenticationToken) {

    }

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

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

    public boolean connect() {

        if (mHandler != null) {
            mHandler.connect();
            return true;
        }
        return false;
    }

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
        return autoReconnect;
    }
}