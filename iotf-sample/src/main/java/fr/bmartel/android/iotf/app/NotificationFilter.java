package fr.bmartel.android.iotf.app;

/**
 * Created by akinaru on 16/02/16.
 */
public class NotificationFilter {

    private String mFilter = "";

    private String mNotificationMessage = "";

    public NotificationFilter(String filter, String notificationMessage) {
        mFilter = filter;
        mNotificationMessage = notificationMessage;
    }

    public String getFilter() {
        return mFilter;
    }

    public String getNotificationMessage() {
        return mNotificationMessage;
    }

}
