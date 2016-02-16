package fr.bmartel.android.iotf.app.inter;

import android.view.MenuItem;

/**
 * Created by akinaru on 15/02/16.
 */
public interface IBaseActivity {

    String getToolbarTitle();

    boolean isShowingDisconnectBtn();

    boolean isShowingNotificationBtn();

    boolean isShowingEditPhoneNotification();

    void displayDisconnect(MenuItem menuItem);
}
