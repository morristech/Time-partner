package me.shouheng.timepartner.managers;


import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

public class AudioManager {
    private Context mContext;

    public AudioManager(Context context){
        mContext = context;
    }

    /**
     * API版本低于19时执行的方法
     * @param intent 启动该活动时传入的Intent对象 */
    public String handleAudioBeforeKitKat(Intent intent){
        Uri uri = intent.getData();
        return getAudioPath(uri, null);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public String handleAudioOnKitKat(Intent intent){
        String audioPath = null;
        Uri uri = intent.getData();
        if(DocumentsContract.isDocumentUri(mContext, uri)){
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];
                String selection = MediaStore.Audio.Media._ID + "=" + id;
                audioPath = getAudioPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
                // 这里在 getAudioPath 中添入的两个参数，一个是用于Query的uri参数，
                // 一个用于Query的selection参数，即在数据中搜索id为指定值的文件路径
                // （大概是因为MediaStore中保存的是文件的路径）
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(docId));
                audioPath = getAudioPath(contentUri, null);
            }
        }else if("content".equalsIgnoreCase(uri.getScheme())){
            audioPath = getAudioPath(uri,null);
        }
        return audioPath;
    }

    /**
     * 从指定的URI中获取文件selection的路径：(基于ContentProvider的访问技术)
     * @param uri URI路径
     * @param selection 选定的路径
     * @return 选择的完整路径 */
    private String getAudioPath(Uri uri, String selection){
        String pathName = null;
        Cursor cursor = mContext.getContentResolver()
                .query(uri, null, selection, null, null);
        if(cursor!=null){
            if(cursor.moveToFirst()){   //MediaStore.Images.Media.DATA
                pathName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            }
            cursor.close();
        }
        return pathName;
    }
}
