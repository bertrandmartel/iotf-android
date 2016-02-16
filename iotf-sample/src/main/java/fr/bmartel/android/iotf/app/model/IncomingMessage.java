package fr.bmartel.android.iotf.app.model;

import java.util.Date;

/**
 * Created by akinaru on 15/02/16.
 */
public class IncomingMessage {

    private String mTopic;

    private String mMessage;

    private Date mDate;

    public IncomingMessage(Date date, String topic, String message) {
        mDate = date;
        mTopic = topic;
        mMessage = message;
    }

    public String getTopic() {
        return mTopic;
    }

    public String getMessage() {
        return mMessage;
    }

    public Date getDate() {
        return mDate;
    }

    public String getDeviceId() {
        return "";
    }
}
