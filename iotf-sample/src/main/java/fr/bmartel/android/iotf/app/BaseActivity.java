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

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import fr.bmartel.android.iotf.app.constant.StorageConst;
import fr.bmartel.android.iotf.app.dialog.AboutDialog;
import fr.bmartel.android.iotf.app.dialog.OpenSourceItemsDialog;
import fr.bmartel.android.iotf.app.inter.IBaseActivity;

/**
 * Common abstract activity
 *
 * @author Bertrand Martel
 */
public abstract class BaseActivity extends AppCompatActivity implements IBaseActivity {

    protected Toolbar toolbar = null;

    protected DrawerLayout mDrawer = null;

    protected ActionBarDrawerToggle drawerToggle;

    protected NavigationView nvDrawer;

    private NotificationManager notifyMgr = null;

    private Ringtone ringtone;

    private PowerManager.WakeLock screenLock;

    protected SharedPreferences sharedpreferences;

    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        sharedpreferences = getSharedPreferences(StorageConst.STORAGE_PROFILE, Context.MODE_PRIVATE);

        notifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);

        } catch (Exception e) {
            e.printStackTrace();
            ringtone = null;
        }

        screenLock = ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
    }

    protected void initNv() {

        toolbar = (Toolbar) findViewById(R.id.toolbar_item);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle(getToolbarTitle());
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.toolbar_menu, menu);


        return super.onCreateOptionsMenu(menu);
    }

    protected ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);
    }

    /**
     * Build listener for navigation view
     *
     * @param navigationView
     */
    protected void setupDrawerContent(NavigationView navigationView) {

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem, mDrawer, BaseActivity.this);
                        return true;
                    }
                });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (menu.findItem(R.id.disconnect_button) != null)
            menu.findItem(R.id.disconnect_button).setVisible(isShowingDisconnectBtn());
        if (menu.findItem(R.id.notification_button) != null)
            menu.findItem(R.id.notification_button).setVisible(isShowingNotificationBtn());
        if (menu.findItem(R.id.edit_phone_notification) != null)
            menu.findItem(R.id.edit_phone_notification).setVisible(isShowingEditPhoneNotification());
        displayDisconnect(menu.findItem(R.id.disconnect_button));
        return true;
    }

    /**
     * Hides the soft keyboard
     */
    protected void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * process menu item selected
     *
     * @param menuItem
     * @param mDrawer
     * @param context
     */
    protected void selectDrawerItem(MenuItem menuItem, DrawerLayout mDrawer, Context context) {

        switch (menuItem.getItemId()) {
            case R.id.report_bugs: {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "kiruazoldik92@gmail.com", null));
                intent.putExtra(Intent.EXTRA_SUBJECT, "iotf Issue");
                intent.putExtra(Intent.EXTRA_TEXT, "Your error report here...");
                context.startActivity(Intent.createChooser(intent, "Report a problem"));
                break;
            }
            case R.id.open_source_components: {
                OpenSourceItemsDialog d = new OpenSourceItemsDialog();
                android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
                d.show(manager, "open_source_components");
                break;
            }
            case R.id.about_app: {
                AboutDialog dialog = new AboutDialog(context);
                dialog.show();
                break;
            }
        }
        mDrawer.closeDrawers();
    }

    /**
     * create a notification with defined message
     *
     * @param message notification message
     */
    protected void triggerNotification(String message) {

        Intent intent = new Intent(this, ConnectActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        android.support.v4.app.NotificationCompat.Builder n = new android.support.v4.app.NotificationCompat.Builder(this)
                .setContentTitle("iotf notification")
                .setContentText(message)
                .setSmallIcon(R.drawable.app_icon)
                .setContentIntent(pIntent)
                .setVibrate(new long[]{1000, 1000})
                .setAutoCancel(true);


        screenLock.acquire();

        if (ringtone != null)
            ringtone.play();

        notifyMgr.notify(0, n.build());
    }

    /**
     * Convert notification list to Json string
     *
     * @param filterList
     * @return
     */
    protected String convertNotificationFilterListToJsonArrayStr(List<NotificationFilter> filterList) {
        JSONArray array = new JSONArray();
        try {
            for (int i = 0; i < filterList.size(); i++) {
                JSONObject item = new JSONObject();
                item.put(StorageConst.STORAGE_NOTIFICATION_BODY, filterList.get(i).getFilter());
                item.put(StorageConst.STORAGE_NOTIFICATION_MESSAGE, filterList.get(i).getNotificationMessage());
                array.put(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return array.toString();
    }
}
