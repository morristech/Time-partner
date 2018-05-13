package me.shouheng.timepartner.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Environment;

import me.shouheng.timepartner.R;

public class TpShare {

    /**
     * 截取屏幕并分享
     * @param activity 上下文 */
    public static void shareScreen(Activity activity, String shareContent){
        File fileShare = getShareFile(activity);
        TpShot.shoot(activity, fileShare);
        share(activity, fileShare, shareContent, R.string.share_item_chooser_title);
    }

    /**
     * 分享APP */
	public static void shareApp(Context mContext, String shareContent) {
        // 从资源中获取图片
        Bitmap pic = BitmapFactory.decodeResource(
                mContext.getResources(), R.drawable.tp_share);
        File shareFile = getShareFile(mContext);
        try {
            shareFile.deleteOnExit();
            FileOutputStream fOut = new FileOutputStream(shareFile);
            BufferedOutputStream bOut = new BufferedOutputStream(fOut);
            pic.compress(CompressFormat.JPEG, 100, bOut);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
        }
        // 进行分享
        share(mContext, shareFile, shareContent, R.string.share_app_chooser_title);
	}

    /**
     * 分享的通用设置
     * @param shareFile 分享的文件的路径
     * @param shareContent 分享的文字内容
     * @param titleRes 分享的标题 */
    private static void share(
            Context mContext, File shareFile, String shareContent, int titleRes){
        // 创建意图打开分享设备
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("image/*");
        intent.putExtra("sms_body", shareContent);
        intent.putExtra("android.intent.extra.TEXT", shareContent);
        intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(shareFile));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // 打开接收分享的设备
        mContext.startActivity(
                Intent.createChooser(intent,
                        mContext.getString(titleRes)));
    }

    /**
     * 获取要分享的图片的路径
     * @param mContext 上下文
     * @return 父路径 */
    private static File getShareFile(Context mContext){
        String fileName = TpFile.getFileName(".png");
        File imageParent;
        if (TpFile.isExternalStorageWritable()){
            imageParent = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        }else {
            imageParent = mContext.getFilesDir();
        }
        File shareFile = new File(imageParent, fileName);
        shareFile.deleteOnExit();
        return shareFile;
    }
}
