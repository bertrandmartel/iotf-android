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
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import fr.bmartel.android.iotf.app.constant.StorageConst;
import fr.bmartel.android.iotf.app.menu.MenuUtils;
import fr.bmartel.android.iotf.app.singleton.IotSingleton;
import fr.bmartel.android.iotf.listener.IMessageCallback;

/**
 * @author Bertrand Martel
 */
public class ConnectActivity extends MainActivityAbstr {

    private static final String TAG = ConnectActivity.class.getSimpleName();

    private Button mButtonConnect;

    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_connect);

        SharedPreferences sharedpreferences = getSharedPreferences(StorageConst.STORAGE_PROFILE, Context.MODE_PRIVATE);

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
        initNv();

        EditText organization = (EditText) findViewById(R.id.organization);
        EditText api_key = (EditText) findViewById(R.id.api_key);
        EditText api_token = (EditText) findViewById(R.id.api_token);
        organization.setText(organizationId);
        api_key.setText(apiKey);
        api_token.setText(apiToken);
        CheckBox sslCheckbox = (CheckBox) findViewById(R.id.ssl);
        sslCheckbox.setChecked(ssl);
        mButtonConnect = (Button) findViewById(R.id.button_connect);

        final String finalOrganizationId = organizationId;
        final String finalApiKey = apiKey;
        final String finalApiToken = apiToken;
        mButtonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IotSingleton.getInstance(ConnectActivity.this).setupApplication("MyActivity", finalOrganizationId, finalApiKey, finalApiToken);
                IotSingleton.getInstance(ConnectActivity.this).connect();
            }
        });

        final TextView error_log = (TextView) findViewById(R.id.error_log);

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
            public void onConnectionSuccess() {
                //go to notification activity
            }

            @Override
            public void onConnectionFailure() {
                //display error message
                error_log.setText("connection failure");
            }

            @Override
            public void onDisconnectionSuccess() {

            }

            @Override
            public void onDisconnectionFailure() {

            }
        });

        if (fromJsonFile)
            mButtonConnect.performClick();
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

    protected void initNv() {

        toolbar = (Toolbar) findViewById(R.id.toolbar_item);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Connection configuration");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.inflateMenu(R.menu.toolbar_menu);

        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();

        mDrawer.setDrawerListener(drawerToggle);

        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        //nvDrawer.setVisibility(View.GONE);
        //mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        // Setup drawer view
        setupDrawerContent(nvDrawer);
    }

    protected ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);
    }


    protected void setupDrawerContent(NavigationView navigationView) {

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        MenuUtils.selectDrawerItem(menuItem, mDrawer, ConnectActivity.this);
                        return true;
                    }
                });
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
    }
}
