package me.shouheng.timepartner.managers;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import me.shouheng.timepartner.R;
import me.shouheng.timepartner.utils.TpFile;

public class RecordManager {
    private MediaRecorder recorder;
    private String filePath;
    private Context mContext;
    private Boolean isStopped;
    private static final String TAG = "RecordManager00";

    /**
     * 该类的构造函数
     * @param context 上下文*/
    public RecordManager(Context context){
        mContext = context;
        isStopped = false;
    }

    public void start(){
        // 设置
        setup();
        try {
            // 准备
            recorder.prepare();
        } catch (IOException e) {
            makeToast(mContext.getString(R.string.com_record_error2),e);
        }
        // 开始
        recorder.start();
    }

    public void stop(){
        if (recorder != null){
            // 停止
            recorder.stop();
            recorder.release();
            recorder = null;
        }
        isStopped = true;
    }

    public void save(){
        makeToast(mContext.getString(R.string.record_save_tip1) + filePath, null);
    }

    public void dispose(){
        File file = new File(filePath);
        file.delete();
    }

    /**
     * 将录音文件的时长转换成标准格式：分钟:秒
     * @param duration 时长
     * @return 返回字符串*/
    public String formatDuration(int duration){
        int seconds = duration / 1000;//毫秒转换成秒
        int minute = seconds / 60;//秒数的“分钟”位的值
        int second = seconds % 60;//秒数的“秒”位的值
        StringBuilder sb = new StringBuilder("");
        if (minute == 0){
            sb.append("00");
        }else if (minute < 10 && minute > 0){
            sb.append("0");
            sb.append(minute);
        }else {
            sb.append(minute);
        }
        sb.append(":");
        if (second == 0){
            sb.append("01");
        }else if (second > 0 && second < 10){
            sb.append("0");
            sb.append(second);
        }else {
            sb.append(second);
        }
        return sb.toString();
    }

    /**
     * 获取录音的文件最终的名字
     * @return */
    public String getFileName(){
        int index = 0;
        if (filePath.contains("/")){
            index = filePath.lastIndexOf('/');
        }
        return filePath.substring(index + 1);
    }

    /**
     * 获取完整路径
     * @return 完整文件路径*/
    public String getFilePath(){
        return filePath;
    }

    /**
     * 判断录音是否结束
     * @return 录音是否结束*/
    public boolean isStop(){
        return isStopped;
    }

    private void setup(){
        try {
            // 初始化
            recorder = new MediaRecorder();
            // 音频源
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            // 输出格式
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            // 音频编码
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            // 文件路径
            filePath = preparedPath();
            // 输出文件
            recorder.setOutputFile(filePath);
        }catch (Exception e){
            makeToast(mContext.getString(R.string.com_record_error1),e);
        }
    }

    private String preparedPath(){
        File fileParent = null;
        try {
            // 根路径
            if (TpFile.isExternalStorageWritable()){
                fileParent = mContext.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
            }else {
                fileParent = mContext.getFilesDir();
                makeToast(mContext.getString(R.string.com_record_error3),null);
            }
        }catch (Exception e){
            makeToast(mContext.getString(R.string.com_record_error5), e);
        }
        if (fileParent != null){
            // 完整路径
            return (new File(fileParent, TpFile.getFileName(".mp3"))).getPath();
        }
        makeToast(mContext.getString(R.string.com_record_error5), null);
        return null;
    }

    /**
     * 显示错误信息
     * @param strErr 包含错误的字符串*/
    private void makeToast(String strErr, Exception e){
        Toast.makeText(mContext, strErr, Toast.LENGTH_SHORT).show();
    }
}
