package me.shouheng.timepartner.activities.base;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import me.shouheng.timepartner.managers.UserKeeper;
import me.shouheng.timepartner.R;
import me.shouheng.timepartner.Constants;
import me.shouheng.timepartner.utils.TpDisp;

public abstract class BaseActivity extends AppCompatActivity{

    protected final long openDelay = 500;

    private static final String TAG = "BaseActivity__";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UserKeeper.init(getApplicationContext());
    }

    protected void makeToast(int res){
        TpDisp.showToast(this, res);
    }

    protected void postStartActivity(final Intent intent){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
            }
        }, openDelay);
    }

    protected void postStartActivityForResult(final Intent intent, final int requestCode){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivityForResult(intent, requestCode);
            }
        }, openDelay);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "onRequestPermissionsResult: " + "permission granted!");
            if (callback != null){
                callback.onGetPermission();
            }
        } else {
            Log.d(TAG, "onRequestPermissionsResult: " + "denied!");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                Log.d(TAG, "onRequestPermissionsResult: " + "M");
                if (!shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
                    showPermissionSettingDialog(requestCode);
                } else {
                    TpDisp.showToast(this, R.string.permission_denied_toast);
                }
            } else {
                TpDisp.showToast(this, R.string.permission_denied_toast);
            }
        }
    }

    private void showPermissionSettingDialog(int requestCode){
        String body = "";
        switch (requestCode){
            case Constants.STORAGE_PERMISSION_REQUEST_CODE:
                body = getString(R.string.permission_storage);
                break;
            case Constants.LOCATION_PERMISSION_REQUEST_CODE:
                body = getString(R.string.permission_location);
                break;
            case Constants.MICROPHONE_PERMISSION_REQUEST_CODE:
                body = getString(R.string.permission_location);
                break;
            case Constants.PHONE_PERMISSION_REQUEST_CODE:
                body = getString(R.string.permission_phone);
                break;
        }
        String msg = getString(R.string.permission_set_dialog_msg_body1)
                + body
                + getString(R.string.permission_set_dialog_msg_body2);
        Dialog dlg = new AlertDialog.Builder(this)
                .setTitle(R.string.permission_set_dilog_title)
                .setMessage(msg)
                .setPositiveButton(R.string.permission_set_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", "me.shouheng.timepartner", null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.com_cancel, null)
                .create();
        dlg.show();
    }

    private OnGetPermissionCallback callback;

    public void checkPermission(String permission, OnGetPermissionCallback callback){
        this.callback = callback;
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "checkPermission: " + "request permission!");
            int requestCode = -1;
            switch (permission) {
                case Manifest.permission.READ_EXTERNAL_STORAGE:
                case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                    requestCode = Constants.STORAGE_PERMISSION_REQUEST_CODE;
                    break;
                case Manifest.permission.CALL_PHONE:
                case Manifest.permission.READ_PHONE_STATE:
                case Manifest.permission.READ_CALL_LOG:
                    requestCode = Constants.PHONE_PERMISSION_REQUEST_CODE;
                    break;
                case Manifest.permission.ACCESS_COARSE_LOCATION:
                case Manifest.permission.ACCESS_FINE_LOCATION:
                    requestCode = Constants.LOCATION_PERMISSION_REQUEST_CODE;
                    break;
                case Manifest.permission.RECORD_AUDIO:
                    requestCode = Constants.MICROPHONE_PERMISSION_REQUEST_CODE;
                    break;
            }
            if (requestCode != -1){
                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            }
        } else {
            Log.d(TAG, "onClick: " + "permission granted!");
            if (callback != null){
                callback.onGetPermission();
            }
        }
    }

    public interface OnGetPermissionCallback{
        void onGetPermission();
    }
}
