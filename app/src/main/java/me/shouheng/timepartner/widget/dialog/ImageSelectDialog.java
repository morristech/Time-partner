package me.shouheng.timepartner.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import java.io.File;

import me.shouheng.timepartner.R;
import me.shouheng.timepartner.Constants;
import me.shouheng.timepartner.utils.TpFile;

public class ImageSelectDialog implements View.OnClickListener{
    private Activity activity;
    private Dialog dlg;

    public ImageSelectDialog(Activity mContext){
        this.activity = mContext;
    }

    public void show(){
        View vi = LayoutInflater.from(activity).inflate(R.layout.dialog_pic_src_selector, null);
        vi.findViewById(R.id.from_camera).setOnClickListener(this);
        vi.findViewById(R.id.from_album).setOnClickListener(this);
        dlg = new AlertDialog.Builder(activity)
                .setTitle(R.string.bg_use_image_title)
                .setView(vi)
                .setPositiveButton(R.string.dlg_img_cancel, null)
                .create();
        dlg.show();
    }

    @Override
    public void onClick(View v) {
        dlg.dismiss();
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.from_camera:
                File bgName = TpFile.createTempBGFile(activity);//创建临时文件，用于存储拍摄的图片
                Uri bgUri = Uri.fromFile(bgName);
                intent.setAction("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, bgUri);
                activity.startActivityForResult(intent, Constants.REQUEST_CODE_TAKE_PHOTO);
                break;
            case R.id.from_album:
                intent.setAction("android.intent.action.GET_CONTENT");
                intent.setType("image/*");
                activity.startActivityForResult(intent, Constants.REQUEST_CODE_CHOOSE_PHOTO);
                break;
        }
    }
}
