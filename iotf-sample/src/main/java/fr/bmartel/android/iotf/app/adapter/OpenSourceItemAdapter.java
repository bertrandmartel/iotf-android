package fr.bmartel.android.iotf.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import fr.bmartel.android.iotf.app.R;

/**
 * Created by akinaru on 09/02/16.
 */
public class OpenSourceItemAdapter extends BaseAdapter {

    private static final String[][] COMPONENTS = new String[][]{

            {"Eclipse Paho client", "http://www.eclipse.org/paho/"}
    };

    private Context mContext;

    public OpenSourceItemAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return COMPONENTS.length;
    }

    @Override
    public Object getItem(int position) {
        return COMPONENTS[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        try {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.open_source_items, parent, false);

            TextView title = (TextView) convertView.findViewById(R.id.title);
            TextView url = (TextView) convertView.findViewById(R.id.url);

            title.setText(COMPONENTS[position][0]);
            url.setText(COMPONENTS[position][1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

}