package fr.bmartel.android.iotf.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.bmartel.android.iotf.app.NotificationFilter;
import fr.bmartel.android.iotf.app.R;

/**
 * Created by akinaru on 16/02/16.
 */
public class PhoneNotificationAdapter extends BaseAdapter {

    List<NotificationFilter> notificationList = new ArrayList<>();

    private Context mContext;

    public PhoneNotificationAdapter(List<NotificationFilter> objects, Context context) {

        this.notificationList = objects;
        mContext = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        try {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.phone_notification_item, parent,false);
                holder = new ViewHolder();

                holder.filter = (TextView) convertView.findViewById(R.id.notif_filter);
                holder.notificationMessage = (TextView) convertView.findViewById(R.id.notif_message);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.filter.setText(notificationList.get(position).getFilter());
            holder.notificationMessage.setText(notificationList.get(position).getNotificationMessage());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public int getCount() {
        return notificationList.size();
    }

    @Override
    public Object getItem(int position) {
        return notificationList.get(position);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    public static class ViewHolder {
        public TextView filter;
        public TextView notificationMessage;
    }

}