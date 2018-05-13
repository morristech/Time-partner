package me.shouheng.timepartner.managers;

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
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

import me.shouheng.timepartner.utils.TpFile;

public class ImageManager{
    private Context mContext;
    private File imageParent;   //一般图片存储的父路径

    //创建文件时必须同时指定文件类型，即必须在传入的名称中加入.jpg

    public ImageManager(Context context){
        mContext = context;
        if (TpFile.isExternalStorageWritable()){
            imageParent = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        }else {
            imageParent = mContext.getFilesDir();
        }
    }

    public File createImageFile(String imageName){
        File imageFile = new File(imageParent, imageName);
        try {
            imageFile.createNewFile();
        } catch (IOException e) {

        }
        return imageFile;
    }

    /**
     * 获取指定名称的图片的存储路径，这里与上面的区别是，这里只获取一个路径而不创建
     * @param name 图片的名称
     * @return 文件路径
     */
    public File getImageByName(String name){
        return new File(imageParent, name);
    }

    /**
     * 在当前版本的API中处理
     * @param intent 启动该活动时传入的Intent对象 */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public String handleImageOnKitKat(Intent intent){
        String imagePath = null;
        Uri uri = intent.getData();
        if(DocumentsContract.isDocumentUri(mContext,uri)){
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID+"="+id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
                //这里在getImagePath中添入的两个参数，一个是用于Query的uri参数，一个用于Query的selection参数，即在数据中搜索id为指定值的文件路径（大概是因为MediaStore中保存的是文件的路径）
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contetnUri =
                        ContentUris.withAppendedId(
                                Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contetnUri,null);
            }
        }else if("content".equalsIgnoreCase(uri.getScheme())){
            imagePath = getImagePath(uri,null);
        }
        return imagePath;
    }

    /**
     * API版本低于19时执行的方法
     * @param intent 启动该活动时传入的Intent对象 */
    public String handleImageBeforeKitKat(Intent intent){
        Uri uri = intent.getData();
        return getImagePath(uri,null);
    }

    /**
     * 从指定的URI中获取文件selection的路径：(基于ContentProvider的访问技术)
     * @param uri 图片的URI路径
     * @param selection 选定的图片的路径
     * @return 选择的图片的完整路径 */
    private String getImagePath(Uri uri, String selection){
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

    public String renameImage(String oldName,String newName){
        //传入的可能是一个文件名或者完整的路径
        //获取原文件路径
        File oldFile;
        oldFile = new File(oldName);
        if (!oldFile.exists()) {
            oldFile = new File(imageParent, oldName);
        }

        //设置新的文件
        File newFile = new File(imageParent, newName);
        if (newFile.exists()){
            newFile.delete();
            try {
                newFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //复制原文件到新文件
        if (oldFile.exists()){
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
        }else {
        }

        return newFile.getPath();
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

    class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
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

    public void loadBitmap(String pathName, ImageView imageView) {
        BitmapWorkerTask task = new BitmapWorkerTask(imageView, 100, 100);
        task.execute(pathName);
    }
}
