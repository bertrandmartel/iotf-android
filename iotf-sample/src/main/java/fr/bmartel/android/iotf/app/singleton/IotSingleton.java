package fr.bmartel.android.iotf.app.singleton;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import fr.bmartel.android.iotf.handler.AppHandler;
import fr.bmartel.android.iotf.listener.IMessageCallback;

/**
 * Created by akinaru on 15/02/16.
 */
public class IotSingleton {

    private String TAG = IotSingleton.class.getSimpleName();

    private IotSingleton mInstance;

    private Context mContext;

    private AppHandler mHandler;

    private boolean exit = false;

    private IMessageCallback mIotCallback;

    public IotSingleton getInstance(Context context) {
        if (mInstance == null)
            mInstance = new IotSingleton(context);
        return mInstance;
    }

    private IotSingleton(Context context) {
        this.mContext = context;
    }

    public void setupDevice(String orgID, String deviceType, String deviceId, String authenticationToken) {

    }

    public void setupApplication(String appID, String orgID, String apiKey, String apiToken) {

        exit = true;
        disconnect();
        if (mIotCallback != null) {
            mHandler.removeCallback(mIotCallback);
        }

        mHandler = new AppHandler(mContext, orgID, appID, apiKey, apiToken);

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
                exit = false;
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

    public boolean connect() {

        if (mHandler != null) {
            mHandler.connect();
            return true;
        }
        return false;
    }

    public boolean disconnect() {

        if (mHandler != null && mHandler.isConnected()) {
            mHandler.disconnect();
            return true;
        }
        return false;
    }
}