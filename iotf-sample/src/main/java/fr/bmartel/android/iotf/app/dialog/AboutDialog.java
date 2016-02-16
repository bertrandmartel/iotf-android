package fr.bmartel.android.iotf.app.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import fr.bmartel.android.iotf.app.R;

/**
 * About Dialog
 *
 * @author Bertrand Martel
 */
public class AboutDialog extends AlertDialog {

    public AboutDialog(Context context) {
        super(context);

        LayoutInflater inflater = getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.about_dialog, null);
        setView(dialoglayout);

        TextView copyright = (TextView) dialoglayout.findViewById(R.id.copyright);
        TextView github_link = (TextView) dialoglayout.findViewById(R.id.github_link);

        if (copyright != null && github_link != null) {
            copyright.setText(R.string.copyright);
            github_link.setText(R.string.github_link);
        } else {
            Log.e("e", "error");
        }
        setTitle(R.string.about);
        setButton(DialogInterface.BUTTON_POSITIVE, "Ok",
                (DialogInterface.OnClickListener) null);
    }
}