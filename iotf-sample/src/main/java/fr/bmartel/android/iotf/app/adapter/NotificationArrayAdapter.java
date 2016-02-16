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
package fr.bmartel.android.iotf.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import fr.bmartel.android.iotf.app.R;
import fr.bmartel.android.iotf.app.model.IncomingMessage;

/**
 * @author Bertrand Martel
 */
public class NotificationArrayAdapter extends BaseAdapter {

    List<IncomingMessage> notificationList = new ArrayList<>();

    private SimpleDateFormat sf = new SimpleDateFormat("HH:mm:ss");

    private Context mContext;

    public NotificationArrayAdapter(List<IncomingMessage> objects, Context context) {

        this.notificationList = objects;
        mContext = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        try {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.notification_item, null);
                holder = new ViewHolder();

                holder.date = (TextView) convertView.findViewById(R.id.date);
                holder.topic = (TextView) convertView.findViewById(R.id.topic);
                holder.message = (TextView) convertView.findViewById(R.id.message);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.date.setText(sf.format(notificationList.get(position).getDate()));
            holder.topic.setText(notificationList.get(position).getTopic());
            holder.message.setText(notificationList.get(position).getMessage());

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
        public TextView date;
        public TextView topic;
        public TextView message;
    }

}