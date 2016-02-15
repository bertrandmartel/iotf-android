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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import fr.bmartel.android.iotf.app.menu.MenuUtils;

/**
 * @author Bertrand Martel
 */
public class ConnectActivity extends MainActivityAbstr {

    private static final String TAG = ConnectActivity.class.getSimpleName();

    private Button mButtonConnect;

    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_connect);

        final Intent intent = getIntent();
        final String action = intent.getAction();

        if (Intent.ACTION_VIEW.equals(action)) {
            Uri uri2 = intent.getData();
            String uri = uri2.getEncodedPath() + "  complete: " + uri2.toString();
            Log.i(TAG, "decoded : " + uri);
        } else {
            Log.d(TAG, "intent was something else: " + action);
        }

        initNv();

        EditText organization = (EditText) findViewById(R.id.organization);
        EditText api_key = (EditText) findViewById(R.id.api_key);
        EditText api_token = (EditText) findViewById(R.id.api_token);
        organization.setText(BuildConfig.BLUEMIX_IOT_ORG);
        api_key.setText(BuildConfig.BLUEMIX_API_KEY);
        api_token.setText(BuildConfig.BLUEMIX_API_TOKEN);

        mButtonConnect = (Button) findViewById(R.id.button_connect);
        mButtonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "click");
            }
        });
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
