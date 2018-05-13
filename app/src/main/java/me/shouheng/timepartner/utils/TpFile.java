package me.shouheng.timepartner.utils;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

import me.shouheng.timepartner.managers.UserKeeper;

public class TpFile {
    private static final String TAG = "FileUtils__";
    public final static String COM_PIC_APPENDIX = ".png";
    private final static String BLUR_ICON_NAME_APPENDIX = "BLUR_ICON_NAME" + COM_PIC_APPENDIX;

    public static void saveBlurIcon(Context context, Bitmap image) {
        if (image == null) {
            return;
        }
        File pictureFile = new File(getParent(context),
                UserKeeper.getLegalAccount() + BLUR_ICON_NAME_APPENDIX);
        if (isFileExist(pictureFile)){
            pictureFile.delete();
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        }
    }

    public static File getBlurIcon(Context context){
        return new File(getParent(context),
                UserKeeper.getLegalAccount() + BLUR_ICON_NAME_APPENDIX);
    }

    public static File createTempIconFile(Context mContext){
        File tempFile = new File(getParent(mContext),
                UserKeeper.getLegalAccount() + "temp_icon" + COM_PIC_APPENDIX);
        try {
            if (tempFile.exists()){
                tempFile.delete();
            }
            tempFile.createNewFile();
        } catch (IOException e) {}
        return tempFile;
    }

    public static File getTempIconFile(Context context){
        return new File(getParent(context), UserKeeper.getLegalAccount() + "temp_icon" + COM_PIC_APPENDIX);
    }

    public static void setTempIconFormal(Context context){
        File realFile = new File(getParent(context), UserKeeper.getLegalAccount() + COM_PIC_APPENDIX);
        if (realFile.exists()){
            realFile.delete();
        }
        File tempIconFile = new File(getParent(context), UserKeeper.getLegalAccount() + "temp_icon" + COM_PIC_APPENDIX);
        tempIconFile.renameTo(realFile);
    }

    public static void renameImage(String oldPath, String newPath){
        File oldFile = new File(oldPath);
        if (!isFileExist(oldFile)) {
            return;
        }
        File newFile = new File(newPath);
        if (isFileExist(newFile)){
            newFile.delete();
            try {
                newFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileInputStream fi = new FileInputStream(oldFile);
            BufferedInputStream bi = new BufferedInputStream(fi);
            FileOutputStream fo = new FileOutputStream(newFile);
            BufferedOutputStream bo = new BufferedOutputStream(fo);
            int in;
            while ((in = bi.read())!= -1){
                bo.write(in);
            }
            fi.close();
            bi.close();
            fo.flush();
            bo.flush();
            fo.close();
            bo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isFileExist(File file){
        return file != null && file.exists();
    }

    public static boolean isFileExist(String path){
        return !TextUtils.isEmpty(path) && (new File(path)).exists();
    }

    public static File createTempBGFile(Context context){
        File tempFile = new File(getParent(context), UserKeeper.getLegalAccount() + "temp_bg" + COM_PIC_APPENDIX);
        try {
            if (tempFile.exists()){
                tempFile.delete();
            }
            tempFile.createNewFile();
        } catch (IOException e) {}
        return tempFile;
    }

    public static File getTempBGFile(Context context){
        return new File(getParent(context), UserKeeper.getLegalAccount() + "temp_bg" + COM_PIC_APPENDIX);
    }

    public static void setTempBGFileAsFormal(Context context){
        File bgFile = new File(getParent(context), UserKeeper.getLegalAccount() + "bg" + COM_PIC_APPENDIX);
        if (bgFile.exists()){
            bgFile.delete();
        }
        File tempBgFile = new File(getParent(context), UserKeeper.getLegalAccount() + "temp_bg" + COM_PIC_APPENDIX);
        tempBgFile.renameTo(bgFile);
    }

    /**
     * 获取背景文件 */
    public static File getBGFile(Context context){
        return new File(getParent(context), UserKeeper.getLegalAccount() + "bg" + COM_PIC_APPENDIX);
    }

    public static boolean isExternalStorageWritable(){
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static File getParent(Context mContext){
        if (isExternalStorageWritable()){
            return mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        }else {
            return mContext.getFilesDir();
        }
    }

    public static String getFileName(String suffix){
        if (suffix.contains(".")){
            return System.currentTimeMillis() + suffix;
        } else {
            return System.currentTimeMillis() + "." + suffix;
        }
    }

    /**
     * 从文件路径中获取文件名称
     * @param filePath 文件路径
     * @return 文件名称 */
    public static String getName(String filePath){
        String fileName = "--";
        if (filePath.contains("/")){
            fileName = filePath.substring(filePath.lastIndexOf('/') + 1);
        }
        return fileName;
    }

    public static String getIconPath(Context context){
        return new File(getParent(context), UserKeeper.getLegalAccount() + COM_PIC_APPENDIX).getPath();
    }

    public static File getIcon(Context context){
        File file = new File(getParent(context), UserKeeper.getLegalAccount() + COM_PIC_APPENDIX);
        if (!isFileExist(file)){
            return null;
        }
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getPath(), options);
        if (options.outHeight == 0 || options.outWidth == 0){
            return null;
        }
        return file;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String handleImageOnKitKat(Context mContext, Intent intent){
        String imagePath = null;
        Uri uri = intent.getData();
        if(DocumentsContract.isDocumentUri(mContext,uri)){
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID+"="+id;
                imagePath = getImagePath(mContext, MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
                //这里在getImagePath中添入的两个参数，一个是用于Query的uri参数，一个用于Query的selection参数，即在数据中搜索id为指定值的文件路径（大概是因为MediaStore中保存的是文件的路径）
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contetnUri =
                        ContentUris.withAppendedId(
                                Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(mContext, contetnUri,null);
            }
        }else if("content".equalsIgnoreCase(uri.getScheme())){
            imagePath = getImagePath(mContext, uri,null);
        }
        return imagePath;
    }

    public static String handleImageBeforeKitKat(Context mContext, Intent intent){
        Uri uri = intent.getData();
        return getImagePath(mContext, uri,null);
    }

    private static String getImagePath(Context mContext, Uri uri, String selection){
        String pathName = null;
        Cursor cursor = mContext.getContentResolver().query(uri,null,selection,null,null);
        if(cursor!=null){
            if(cursor.moveToFirst()){
                pathName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return pathName;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    private static Bitmap decodeSampledBitmapFromFile(String pathName, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathName, options);
    }

    static class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private String pathName;
        private int xLength, yLength;

        public BitmapWorkerTask(ImageView imageView,int xLength, int yLength) {
            imageViewReference = new WeakReference<ImageView>(imageView);
            this.xLength = xLength;
            this.yLength = yLength;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            pathName = params[0];
            return decodeSampledBitmapFromFile(pathName, xLength, yLength);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    public void loadBitmap(String pathName, ImageView imageView, int xLength, int yLength) {
        BitmapWorkerTask task = new BitmapWorkerTask(imageView, xLength, yLength);
        task.execute(pathName);
    }

    public static void loadBitmap(String pathName, ImageView imageView) {
        BitmapWorkerTask task = new BitmapWorkerTask(imageView, 100, 100);
        task.execute(pathName);
    }
}
