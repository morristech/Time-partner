package me.shouheng.timepartner.widget.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import me.shouheng.timepartner.R;
import me.shouheng.timepartner.activities.base.ShareActivity;
import me.shouheng.timepartner.utils.TpDisp;

public class AboutDialog {
    private Activity activity;

    public AboutDialog(Activity activity){
        this.activity = activity;
    }

    public void show(){

        PackageInfo pInfo = null;
        String version = "";
        try {
            pInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
            version = pInfo.versionName;
            int versionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            TpDisp.showToast(activity, R.string.about_failed_to_get_package_info);
        }
        String dlgTitle = activity.getString(R.string.app_name) + " " + version;

        TextView tv = new TextView(activity);
        tv.setText(R.string.about_dilaog_message);
        //tv.setAutoLinkMask(Linkify.ALL);
        tv.setTextSize(16);
        tv.setTextColor(Color.BLACK);
        int padding = TpDisp.dp2Px(activity, 16);
        tv.setPadding(padding, padding, padding, padding);
        tv.setMovementMethod(LinkMovementMethod.getInstance());

        new AlertDialog.Builder(activity)
                .setTitle(dlgTitle)
                .setView(tv)
                .setPositiveButton(R.string.com_share, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.startActivity(new Intent(activity, ShareActivity.class));
                    }
                })
                .setNegativeButton(R.string.com_confirm, null)
                .create().show();
    }
}
