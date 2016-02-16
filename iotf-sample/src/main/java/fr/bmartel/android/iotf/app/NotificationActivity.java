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

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.bmartel.android.iotf.app.adapter.NotificationArrayAdapter;
import fr.bmartel.android.iotf.app.constant.StorageConst;
import fr.bmartel.android.iotf.app.dialog.NotificationFilterDialog;
import fr.bmartel.android.iotf.app.dialog.PhoneNotificationDialog;
import fr.bmartel.android.iotf.app.inter.INotificationFilter;
import fr.bmartel.android.iotf.app.inter.IPhoneNotificationListener;
import fr.bmartel.android.iotf.app.model.IncomingMessage;
import fr.bmartel.android.iotf.app.singleton.IotSingleton;
import fr.bmartel.android.iotf.listener.IMessageCallback;

/**
 * @author Bertrand Martel
 */
public class NotificationActivity extends BaseActivity implements INotificationFilter, IPhoneNotificationListener {

    private static final String TAG = NotificationActivity.class.getSimpleName();

    private ListView notificationListview;
    private NotificationArrayAdapter notificationAdapter;

    private ArrayList<IncomingMessage> notificationItems = new ArrayList<>();

    private List<NotificationFilter> messageBodyFilterList = new ArrayList<>();

    private boolean init = true;

    private FragmentManager fragmentManager;

    private final static String DEFAULT_NOTIFICATION_MESSAGE = "IoT notification";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        initNv();

        String filterList = sharedpreferences.getString(StorageConst.STORAGE_FILTER_LIST, "[]");

        try {
            JSONArray filterListObj = new JSONArray(filterList);
            for (int i = 0; i < filterListObj.length(); i++) {

                JSONObject item = (JSONObject) filterListObj.get(i);

                if (item.has(StorageConst.STORAGE_NOTIFICATION_BODY) && item.has(StorageConst.STORAGE_NOTIFICATION_MESSAGE)) {
                    messageBodyFilterList.add(new NotificationFilter(item.getString(StorageConst.STORAGE_NOTIFICATION_BODY),
                            item.getString(StorageConst.STORAGE_NOTIFICATION_MESSAGE)));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        fragmentManager = getSupportFragmentManager();

        final TextView noNotificationTv = (TextView) findViewById(R.id.no_notification_tv);

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

                final IncomingMessage message = new IncomingMessage(new Date(),
                        topic.substring(topic.indexOf("id/") + 6, topic.indexOf("/evt")),
                        new String(mqttMessage.getPayload()));

                if (init) {
                    init = false;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            noNotificationTv.setVisibility(View.GONE);
                            notificationListview.setVisibility(View.VISIBLE);
                            initListview();
                        }
                    });

                }

                notificationItems.add(0, message);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        notificationAdapter.notifyDataSetChanged();

                        for (int i = 0; i < messageBodyFilterList.size(); i++) {
                            if (message.getMessage().contains(messageBodyFilterList.get(i).getFilter())) {
                                Log.i(TAG, "launch phone notification : filter found in message body");
                                triggerNotification(messageBodyFilterList.get(i).getNotificationMessage());
                                return;
                            }
                        }
                    }
                });
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken messageToken) {

            }

            @Override
            public void onConnectionSuccess(IMqttToken token) {

            }

            @Override
            public void onConnectionFailure(IMqttToken token, Throwable cause) {

            }

            @Override
            public void onDisconnectionSuccess(IMqttToken token) {
                Toast.makeText(NotificationActivity.this, "disconnected from server", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onDisconnectionFailure(IMqttToken token, Throwable cause) {

            }
        });

        notificationListview = (ListView) findViewById(R.id.notification_list);


    }

    private void initListview() {

        notificationItems = new ArrayList<>();

        notificationAdapter = new NotificationArrayAdapter(notificationItems, this);

        notificationListview.setAdapter(notificationAdapter);

        notificationListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                /*
                IncomingMessage message = (IncomingMessage) notificationAdapter.getItem(position);

                Log.i(TAG, "message selected : " + message.getMessage());

                mSelectedDeviceId = message.getDeviceId();
                mSelectedMessageBody = message.getMessage();
                */
            }
        });
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
            case R.id.notification_button:
                NotificationFilterDialog notificationDialog = new NotificationFilterDialog();
                notificationDialog.setNotificationFilterListener(NotificationActivity.this);
                notificationDialog.show(fragmentManager, "notification_filter_dialog");
                return true;
            case R.id.edit_phone_notification:
                PhoneNotificationDialog phoneDialog = new PhoneNotificationDialog();
                phoneDialog.setPhoneNotificationList(messageBodyFilterList);
                phoneDialog.setPhoneNotificationListener(NotificationActivity.this);
                phoneDialog.show(fragmentManager, "notification_filter_dialog");
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
    public boolean isShowingNotificationBtn() {
        return true;
    }

    @Override
    public boolean isShowingDisconnectBtn() {
        return true;
    }

    @Override
    public boolean isShowingEditPhoneNotification() {
        return true;
    }

    @Override
    public void setMessageBodyFilter(String notificationMessage, String filter) {
        Log.i(TAG, "add " + filter + " " + notificationMessage);
        if (notificationMessage == null || notificationMessage.equals(""))
            notificationMessage = DEFAULT_NOTIFICATION_MESSAGE;

        messageBodyFilterList.add(new NotificationFilter(filter, notificationMessage));

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(StorageConst.STORAGE_FILTER_LIST, convertNotificationFilterListToJsonArrayStr(messageBodyFilterList));
        editor.commit();
    }

    @Override
    public void deleteFilter(String filter) {
        Log.i(TAG, "remove filter " + filter);
        for (int i = messageBodyFilterList.size() - 1; i >= 0; i--) {
            if (messageBodyFilterList.get(i).getFilter().equals(filter)) {
                messageBodyFilterList.remove(i);
            }
        }
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(StorageConst.STORAGE_FILTER_LIST, convertNotificationFilterListToJsonArrayStr(messageBodyFilterList));
        editor.commit();

    }

    @Override
    public void displayDisconnect(MenuItem menuItem) {
        menuItem.setIcon(R.drawable.disconnect2);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(NotificationActivity.this, "disconnected from server", Toast.LENGTH_SHORT).show();
    }
}
