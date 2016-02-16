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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

import fr.bmartel.android.iotf.app.NotificationFilter;
import fr.bmartel.android.iotf.app.R;
import fr.bmartel.android.iotf.app.adapter.PhoneNotificationAdapter;
import fr.bmartel.android.iotf.app.inter.IPhoneNotificationListener;

/**
 * Phone notification dialog
 *
 * @author Bertrand Martel
 */
public class PhoneNotificationDialog extends android.support.v4.app.DialogFragment {

    private IPhoneNotificationListener phoneNotificationListener;

    private List<NotificationFilter> phoneNotificationList;

    private int currentSelection = -1;

    private PhoneNotificationAdapter adapter;

    public PhoneNotificationDialog() {

    }

    public void setPhoneNotificationList(List<NotificationFilter> phoneNotificationList) {
        this.phoneNotificationList = phoneNotificationList;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.phone_notification_dialog, null);

        ListView mList = (ListView) mView.findViewById(R.id.phone_notification_list);
        if (mList != null) {

            adapter = new PhoneNotificationAdapter(phoneNotificationList, getActivity());
            mList.setAdapter(adapter);

            mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
                    view.setSelected(true);
                    currentSelection = position;
                }
            });
        }

        Button deleteButton = (Button) mView.findViewById(R.id.delete_buttton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentSelection != -1) {
                    phoneNotificationListener.deleteFilter(phoneNotificationList.get(currentSelection).getFilter());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });

        Button cancelButton = (Button) mView.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        getDialog().setTitle("Phone notification");

        return mView;
    }

    public void setPhoneNotificationListener(IPhoneNotificationListener phoneNotificationListener) {
        this.phoneNotificationListener = phoneNotificationListener;
    }
}