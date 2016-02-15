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
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.Date;

import fr.bmartel.android.iotf.app.adapter.NotificationArrayAdapter;
import fr.bmartel.android.iotf.app.model.IncomingMessage;
import fr.bmartel.android.iotf.app.singleton.IotSingleton;
import fr.bmartel.android.iotf.listener.IMessageCallback;

/**
 * @author Bertrand Martel
 */
public class NotificationActivity extends BaseActivity {

    private static final String TAG = NotificationActivity.class.getSimpleName();

    private ListView notificationListview;

    private NotificationArrayAdapter notificationAdapter;

    private ArrayList<IncomingMessage> notificationItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        initNv();
        IotSingleton.getInstance(this).setInternalCb(new IMessageCallback() {
            @Override
            public void connectionLost(Throwable cause) {

                if (!IotSingleton.getInstance(NotificationActivity.this).isAutoReconnect()) {
                    Toast.makeText(NotificationActivity.this, "disconnected from server", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {

                notificationItems.add(0, new IncomingMessage(new Date(), topic.substring(topic.indexOf("id/") + 6, topic.indexOf("/evt")), new String(mqttMessage.getPayload())));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notificationAdapter.notifyDataSetChanged();
                        triggerNotification("nouveau");
                    }
                });
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken messageToken) {

            }

            @Override
            public void onConnectionSuccess() {

            }

            @Override
            public void onConnectionFailure() {

            }

            @Override
            public void onDisconnectionSuccess() {
                Toast.makeText(NotificationActivity.this, "disconnected from server", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onDisconnectionFailure() {

            }
        });

        notificationListview = (ListView) findViewById(R.id.notification_list);

        notificationItems = new ArrayList<>();

        notificationAdapter = new NotificationArrayAdapter(notificationItems);

        notificationListview.setAdapter(notificationAdapter);

    }

    // Make sure this is the method with just `Bundle` as the signature
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.disconnect_button:
                IotSingleton.getInstance(this).disconnect(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "application finished");
        IotSingleton.getInstance(this).disconnect(false);
    }

    @Override
    public String getToolbarTitle() {
        return "Notifications";
    }

    @Override
    public boolean isShowingDisconnectBtn() {
        return true;
    }
}
