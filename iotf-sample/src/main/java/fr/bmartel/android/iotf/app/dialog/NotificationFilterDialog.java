package fr.bmartel.android.iotf.app.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import fr.bmartel.android.iotf.app.R;
import fr.bmartel.android.iotf.app.inter.INotificationFilter;

/**
 * Created by akinaru on 16/02/16.
 */
public class NotificationFilterDialog extends android.support.v4.app.DialogFragment {

    private INotificationFilter notificationListener;

    public NotificationFilterDialog() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.notification_filter_dialog, container, false);

        Button setupPhoneNotif = (Button) mView.findViewById(R.id.button_setup_filter);

        final EditText notificationMessage = (EditText) mView.findViewById(R.id.notification_message);

        final EditText message_body = (EditText) mView.findViewById(R.id.message_body);

        setupPhoneNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!message_body.getText().toString().equals("")) {
                    Log.i("notif", "set message body filter");
                    notificationListener.setMessageBodyFilter(notificationMessage.getText().toString(), message_body.getText().toString());
                }
                dismiss();
            }
        });

        getDialog().setTitle("Phone notification");

        return mView;
    }

    public void setNotificationFilterListener(INotificationFilter notificationFilter) {
        this.notificationListener = notificationFilter;
    }
}
