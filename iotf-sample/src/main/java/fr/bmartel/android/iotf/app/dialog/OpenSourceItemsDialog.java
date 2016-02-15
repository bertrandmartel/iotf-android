package fr.bmartel.android.iotf.app.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import fr.bmartel.android.iotf.app.R;
import fr.bmartel.android.iotf.app.adapter.OpenSourceItemAdapter;

/**
 * Created by akinaru on 09/02/16.
 */
public class OpenSourceItemsDialog extends android.support.v4.app.DialogFragment {

    private Context mContext;

    public OpenSourceItemsDialog() {
    }

    public OpenSourceItemsDialog(Context context) {
        mContext = context;
    }

    private ListView mList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.open_source_list, container, false);

        mList = (ListView) mView.findViewById(R.id.open_source_listview);
        mList.setAdapter(new OpenSourceItemAdapter(mContext));

        getDialog().setTitle(R.string.open_source_items);
        return mView;
    }


}
