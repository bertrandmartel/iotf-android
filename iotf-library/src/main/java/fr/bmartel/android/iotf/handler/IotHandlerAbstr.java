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
package fr.bmartel.android.iotf.handler;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.security.ProviderInstaller;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import fr.bmartel.android.iotf.R;
import fr.bmartel.android.iotf.constant.ConnectionState;
import fr.bmartel.android.iotf.constant.MessageFormat;
import fr.bmartel.android.iotf.constant.MqttConst;
import fr.bmartel.android.iotf.constant.QosPolicy;
import fr.bmartel.android.iotf.listener.IMessageCallback;

/**
 * Generic handler for all handler type
 *
 * @author Bertrand Martel
 */
public abstract class IotHandlerAbstr implements IHandler {

    protected final static String TAG = DeviceHandler.class.getSimpleName();

    /**
     * MQTT client
     */
    private MqttAndroidClient mClient = null;

    /**
     * client ID used to authenticate
     */
    protected String mClientId = "";

    /**
     * org_id is your unique organization ID, assigned when you sign up with the service. It will be a 6 character alphanumeric string
     */
    protected String mOrgId = "";

    /**
     * username used for authentication
     */
    protected String mUsername = "";

    /**
     * password used for authentication
     */
    protected String mPassword = "";

    /**
     * Android context
     */
    private Context mContext = null;

    /**
     * callback for MQTT events
     */
    private MqttCallback mClientCb = null;

    /**
     * callback for MQTT connection
     */
    private IMqttActionListener mConnectionCb = null;

    /**
     * list of message callbacks
     */
    private List<IMessageCallback> mMessageCallbacksList = new ArrayList<>();

    /**
     * default format used when publishing / subscribing
     */
    protected MessageFormat mMessageFormat = MessageFormat.JSON;

    /**
     * QOS policy (0 to 2)
     */
    protected QosPolicy mQosDefault = QosPolicy.QOS_POLICY_AT_MOST_ONCE;

    /**
     * Sets whether the client and server should remember state across restarts and reconnects
     */
    protected boolean mCleanSessionDefault = false;

    /**
     * Sets the connection timeout value (in seconds)
     */
    protected int mTimeoutDefault = 30;

    /**
     * Sets the "keep alive" interval (in seconds)
     */
    protected int mKeepAliveDefault = 60;

    /**
     * define if ssl is used
     */
    protected boolean mUseSsl = false;

    /**
     * connection state
     */
    private boolean connected = false;

    /**
     * define if current action is connecting or not
     */
    private ConnectionState connectionState = ConnectionState.NONE;

    /**
     * Default handler has only organization ID
     *
     * @param orgId org_id is your unique organization ID, assigned when you sign up with the service. It will be a 6 character alphanumeric string
     */
    public IotHandlerAbstr(Context context, String orgId) {

        this.mContext = context;
        this.mOrgId = orgId;
        this.mClientCb = new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                connected = false;
                for (int i = 0; i < mMessageCallbacksList.size(); i++) {
                    mMessageCallbacksList.get(i).connectionLost(cause);
                }
            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                for (int i = 0; i < mMessageCallbacksList.size(); i++) {
                    mMessageCallbacksList.get(i).messageArrived(topic, mqttMessage);
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                for (int i = 0; i < mMessageCallbacksList.size(); i++) {
                    mMessageCallbacksList.get(i).deliveryComplete(token);
                }
            }
        };
    }

    /**
     * Test validity of device type / device ID
     *
     * @param data deviceType or deviceId
     * @return true if deviceType/deviceId is valid
     */
    protected boolean isValid(String data) {
        if (!data.matches("[-a-zA-Z0-9_.]+") || data.length() > 36 || data.isEmpty())
            return false;
        return true;
    }

    @Override
    public boolean isConnected() {
        if (mClient == null)
            return false;
        else
            return connected;
    }

    @Override
    public void connect() {

        if (!isConnected()) {

            MqttConnectOptions options = new MqttConnectOptions();

            String serverURI = "";

            options.setCleanSession(mCleanSessionDefault);
            options.setConnectionTimeout(mTimeoutDefault);
            options.setKeepAliveInterval(mKeepAliveDefault);

            options.setUserName(mUsername);
            options.setPassword(mPassword.toCharArray());

            if (mUseSsl) {

                Log.d(TAG, "using ssl");

                serverURI = "ssl://" + mOrgId + "." + MqttConst.URL_SUFFIX + ":" + MqttConst.PORT_ENCRYPTED2;

                try {
                    ProviderInstaller.installIfNeeded(mContext);

                    SSLContext sslContext;
                    KeyStore ks = KeyStore.getInstance("bks");
                    ks.load(mContext.getResources().openRawResource(R.raw.iot), "password".toCharArray());
                    TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
                    tmf.init(ks);
                    TrustManager[] tm = tmf.getTrustManagers();
                    sslContext = SSLContext.getInstance("TLSv1.2");
                    sslContext.init(null, tm, null);

                    options.setSocketFactory(sslContext.getSocketFactory());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                serverURI = "tcp://" + mOrgId + "." + MqttConst.URL_SUFFIX + ":" + MqttConst.PORT_UNENCRYPTED;
            }

            mClient = new MqttAndroidClient(mContext, serverURI, mClientId);
            mClient.setCallback(mClientCb);

            mConnectionCb = new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken iMqttToken) {

                    if (connectionState == ConnectionState.CONNECTING) {
                        Log.i(TAG, "connection success");
                        connected = true;
                        connectionState = ConnectionState.NONE;
                        for (int i = 0; i < mMessageCallbacksList.size(); i++) {
                            mMessageCallbacksList.get(i).onConnectionSuccess(iMqttToken);
                        }
                    } else if (connectionState == ConnectionState.DISCONNECTING) {
                        Log.i(TAG, "disconnection success");
                        connected = true;
                        connectionState = ConnectionState.NONE;
                        for (int i = 0; i < mMessageCallbacksList.size(); i++) {
                            mMessageCallbacksList.get(i).onDisconnectionSuccess(iMqttToken);
                        }
                    }
                }

                @Override
                public void onFailure(IMqttToken iMqttToken, Throwable throwable) {

                    if (connectionState == ConnectionState.CONNECTING) {
                        Log.e(TAG, "connection failure : " + iMqttToken.getException().getMessage());
                        connected = false;
                        connectionState = ConnectionState.NONE;
                        for (int i = 0; i < mMessageCallbacksList.size(); i++) {
                            mMessageCallbacksList.get(i).onConnectionFailure(iMqttToken, throwable);
                        }
                    } else if (connectionState == ConnectionState.DISCONNECTING) {
                        Log.e(TAG, "disconnection failure : " + iMqttToken.getException().getMessage());
                        connected = false;
                        connectionState = ConnectionState.NONE;
                        for (int i = 0; i < mMessageCallbacksList.size(); i++) {
                            mMessageCallbacksList.get(i).onDisconnectionFailure(iMqttToken, throwable);
                        }
                    }
                }
            };

            try {
                connectionState = ConnectionState.CONNECTING;
                mClient.connect(options, mContext, mConnectionCb);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "cant connect - already connected");
        }
    }

    @Override
    public void disconnect() {

        if (isConnected()) {
            try {
                connectionState = ConnectionState.DISCONNECTING;
                mClient.disconnect(mContext, mConnectionCb);

            } catch (MqttException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "cant disconnect - already disconnected");
        }
    }

    @Override
    public void addIotCallback(IMessageCallback callback) {
        mMessageCallbacksList.add(callback);
    }

    @Override
    public void removeCallback(IMessageCallback callback) {
        mMessageCallbacksList.remove(callback);
    }

    @Override
    public void setMessageFormat(MessageFormat format) {
        mMessageFormat = format;
    }

    @Override
    public void setQos(QosPolicy qos) {
        mQosDefault = qos;
    }

    @Override
    public void setKeepAlive(int keepAlive) {
        mKeepAliveDefault = keepAlive;
    }

    @Override
    public void setConnectionTimeout(int timeout) {
        mTimeoutDefault = timeout;
    }

    @Override
    public void setCleanSession(boolean resetSession) {
        mCleanSessionDefault = resetSession;
    }

    @Override
    public void setSSL(boolean useSSL) {
        mUseSsl = useSSL;
    }

    /**
     * Publish a message to MQTT server
     *
     * @param topic      message topic
     * @param message    message body
     * @param isRetained define if message should be retained on MQTT server
     * @param qos        define quality of service (check QosPolicy enum)
     * @param listener   completion listener (null allowed)
     * @return
     */
    protected IMqttDeliveryToken publishMessage(String topic, String message, boolean isRetained, QosPolicy qos, IMqttActionListener listener) {

        if (isConnected()) {

            MqttMessage mqttMessage = new MqttMessage(message.getBytes());
            mqttMessage.setRetained(isRetained);
            mqttMessage.setQos(qos.getValue());

            try {
                return mClient.publish(topic, mqttMessage, mContext, listener);
            } catch (MqttPersistenceException e) {
                e.printStackTrace();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "cant publish message. Not connected");
        }
        return null;
    }

    /**
     * Subscribe to topic
     *
     * @param topic    topic to subscribe
     * @param qos      define quality of service (check QosPolicy enum)
     * @param listener completion listener (null allowed)
     * @return
     */
    protected void subscribe(String topic, QosPolicy qos, IMqttActionListener listener) {

        if (isConnected()) {
            try {
                mClient.subscribe(topic, qos.getValue(), mContext, listener);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "cant publish message. Not connected");
        }
    }

    /**
     * Unsubscribe a topic
     *
     * @param topic    topic to unsubscribe
     * @param listener completion listener (null allowed)
     */
    protected void unsubscribe(String topic, IMqttActionListener listener) {

        if (isConnected()) {
            try {
                mClient.unsubscribe(topic, mContext, listener);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "cant publish message. Not connected");
        }
    }
}
