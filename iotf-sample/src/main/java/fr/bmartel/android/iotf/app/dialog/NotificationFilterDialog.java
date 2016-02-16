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
 * Phone notificaiton creation Dialog
 *
 * @author Bertrand Martel
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
