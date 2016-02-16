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
 * Created by akinaru on 16/02/16.
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