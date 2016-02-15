package fr.bmartel.android.iotf.app.menu;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;

import fr.bmartel.android.iotf.app.R;
import fr.bmartel.android.iotf.app.dialog.AboutDialog;
import fr.bmartel.android.iotf.app.dialog.OpenSourceItemsDialog;

/**
 * Created by akinaru on 08/02/16.
 */
public class MenuUtils {

    public static void selectDrawerItem(MenuItem menuItem, DrawerLayout mDrawer, Context context) {

        switch (menuItem.getItemId()) {
            case R.id.report_bugs: {
                Log.i("test", "reports");
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "kiruazoldik92@gmail.com", null));
                intent.putExtra(Intent.EXTRA_SUBJECT, "RFdroid Issue");
                intent.putExtra(Intent.EXTRA_TEXT, "Your error report here...");
                context.startActivity(Intent.createChooser(intent, "Report a problem"));
                break;
            }
            case R.id.open_source_components: {
                OpenSourceItemsDialog d = new OpenSourceItemsDialog(context);
                d.show();
                break;
            }
            case R.id.about_app: {
                AboutDialog dialog = new AboutDialog(context);
                dialog.show();
                break;
            }
        }
        mDrawer.closeDrawers();
    }
}
