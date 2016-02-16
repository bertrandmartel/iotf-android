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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import fr.bmartel.android.iotf.app.constant.StorageConst;
import fr.bmartel.android.iotf.app.singleton.IotSingleton;
import fr.bmartel.android.iotf.app.utils.RandomString;
import fr.bmartel.android.iotf.listener.IMessageCallback;

/**
 * @author Bertrand Martel
 */
public class ConnectActivity extends BaseActivity {

    private static final String TAG = ConnectActivity.class.getSimpleName();

    private RandomString randomId = new RandomString(30);

    private EditText mOrganizationEditText;

    private EditText mApikeyEditText;

    private EditText mApitokenEditText;

    private CheckBox mSslCheckbox;

    private CheckBox mReconnectCheckbox;

    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_connect);

        final Intent intent = getIntent();

        String defaultOrgId = "";
        String defaultApiKey = "";
        String defaultApiToken = "";

        if (BuildConfig.BLUEMIX_IOT_ORG != null)
            defaultOrgId = BuildConfig.BLUEMIX_IOT_ORG;
        if (BuildConfig.BLUEMIX_API_KEY != null)
            defaultApiKey = BuildConfig.BLUEMIX_API_KEY;
        if (BuildConfig.BLUEMIX_API_TOKEN != null)
            defaultApiToken = BuildConfig.BLUEMIX_API_TOKEN;

        String organizationId = sharedpreferences.getString(StorageConst.STORAGE_ORGANIZATION_ID, defaultOrgId);
        String apiKey = sharedpreferences.getString(StorageConst.STORAGE_API_KEY, defaultApiKey);
        String apiToken = sharedpreferences.getString(StorageConst.STORAGE_API_TOKEN, defaultApiToken);
        boolean ssl = sharedpreferences.getBoolean(StorageConst.STORAGE_USE_SSL, true);

        boolean fromJsonFile = false;

        if (Intent.ACTION_VIEW.equals(intent.getAction())) {

            String jsonContent = loadFile(intent);

            try {
                JSONObject json = new JSONObject(jsonContent);

                if (json.has(StorageConst.STORAGE_API_KEY) && json.has(StorageConst.STORAGE_API_TOKEN) &&
                        json.has(StorageConst.STORAGE_ORGANIZATION_ID) && json.has(StorageConst.STORAGE_USE_SSL)) {

                    organizationId = json.getString(StorageConst.STORAGE_ORGANIZATION_ID);
                    apiKey = json.getString(StorageConst.STORAGE_API_KEY);
                    apiToken = json.getString(StorageConst.STORAGE_API_TOKEN);
                    ssl = json.getBoolean(StorageConst.STORAGE_USE_SSL);

                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(StorageConst.STORAGE_ORGANIZATION_ID, organizationId);
                    editor.putString(StorageConst.STORAGE_API_KEY, apiKey);
                    editor.putString(StorageConst.STORAGE_API_TOKEN, apiToken);
                    editor.putBoolean(StorageConst.STORAGE_USE_SSL, ssl);
                    editor.commit();

                    fromJsonFile = true;

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollview);

        mOrganizationEditText = (EditText) scrollView.findViewById(R.id.organization);
        mApikeyEditText = (EditText) scrollView.findViewById(R.id.api_key);
        mApitokenEditText = (EditText) scrollView.findViewById(R.id.api_token);
        mOrganizationEditText.setText(organizationId);
        mApikeyEditText.setText(apiKey);
        mApitokenEditText.setText(apiToken);
        mSslCheckbox = (CheckBox) scrollView.findViewById(R.id.ssl);
        mSslCheckbox.setChecked(ssl);
        mReconnectCheckbox = (CheckBox) scrollView.findViewById(R.id.reconnect);
        mReconnectCheckbox.setChecked(true);

        if (fromJsonFile) {
            connect();
        }

        initNv();
    }

    private String loadFile(Intent intent) {
        try {
            BufferedReader buffer = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(intent.getData())));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = buffer.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    // Make sure this is the method with just `Bundle` as the signature
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    public void onResume() {
        super.onResume();

        hideSoftKeyboard();

        IotSingleton.getInstance(this).setInternalCb(new IMessageCallback() {

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken messageToken) {

            }

            @Override
            public void onConnectionSuccess(IMqttToken token) {
                //go to notification activity
                Toast.makeText(ConnectActivity.this, "connnected to server", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(ConnectActivity.this, NotificationActivity.class);
                startActivity(i);
            }

            @Override
            public void onConnectionFailure(IMqttToken token, Throwable throwable) {
                //display error message
                String errorMessage = "connection error";
                if (throwable != null && throwable.getMessage() != null) {
                    errorMessage = throwable.getMessage();
                }
                Toast.makeText(ConnectActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDisconnectionSuccess(IMqttToken token) {

            }

            @Override
            public void onDisconnectionFailure(IMqttToken token, Throwable throwable) {

            }
        });
    }

    @Override
    public String getToolbarTitle() {
        return "IoT Foundation connection configuration";
    }

    @Override
    public boolean isShowingDisconnectBtn() {
        return true;
    }

    @Override
    public boolean isShowingNotificationBtn() {
        return false;
    }

    @Override
    public boolean isShowingEditPhoneNotification() {
        return false;
    }

    @Override
    public void displayDisconnect(MenuItem menuItem) {
        menuItem.setIcon(R.drawable.disconnect3);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.disconnect_button:
                connect();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void connect() {
        IotSingleton.getInstance(ConnectActivity.this).setupApplication(randomId.nextString(), mOrganizationEditText.getText().toString(),
                mApikeyEditText.getText().toString(), mApitokenEditText.getText().toString(), mSslCheckbox.isChecked(), mReconnectCheckbox.isChecked());
        IotSingleton.getInstance(ConnectActivity.this).connect();
    }
}
