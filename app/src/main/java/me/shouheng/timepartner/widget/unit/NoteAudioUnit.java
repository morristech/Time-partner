package me.shouheng.timepartner.widget.unit;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;

import me.shouheng.timepartner.R;
import me.shouheng.timepartner.utils.TpDisp;
import me.shouheng.timepartner.utils.TpFile;

public class NoteAudioUnit extends RelativeLayout implements View.OnClickListener,
        MediaPlayer.OnCompletionListener{
    private Context mContext;
    private TextView tvAudioName;
    private TextView tvAudioDisc;
    private ImageView audioFooter;
    private ImageView audioHeader;
    private String audioPath;
    private String audioName;
    private String audioDisc;
    private MediaPlayer mPlayer;
    private boolean isFirstTime = true;
    private boolean isPrepared = false;

    public NoteAudioUnit(Context context) {
        super(context);
        init(context);
    }

    public NoteAudioUnit(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NoteAudioUnit(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * 初始化
     * @param context 上下文 */
    private void init(Context context){
        mContext = context;
        // 获取组件
        LayoutInflater.from(context).inflate(R.layout.view_unit_audio, this);
        tvAudioName = (TextView) findViewById(R.id.audio_name);
        tvAudioDisc = (TextView) findViewById(R.id.audio_describe);
        audioFooter = (ImageView) findViewById(R.id.audio_footer);
        audioHeader = (ImageView) findViewById(R.id.audio_header);
        audioFooter.setOnClickListener(this);
        audioHeader.setOnClickListener(this);
    }

    /**
     * 设置音频文件路径
     * @param audioPath 音频路径 */
    public void setAudioPath(String audioPath){
        this.audioPath = audioPath;
        audioName = mContext.getString(R.string.audio_name);
        if (audioPath.contains("/")){
            audioName = audioPath.substring(audioPath.lastIndexOf('/') + 1);
        }
        audioDisc = mContext.getString(R.string.audio_describe);
        tvAudioName.setText(audioName);
        tvAudioDisc.setText(audioDisc);
        // 要初始化播放组件
        isFirstTime = true;
        if (mPlayer != null && mPlayer.isPlaying()){
            mPlayer.stop();
            mPlayer.reset();
        }
        audioFooter.setImageResource(R.drawable.ic_audio_play);
    }

    /**
     * 获取音频路径
     * @return 音频路径 */
    public String getAudioPath(){
        return audioPath;
    }

    /**
     * 设置音频描述
     * @param audioDisc 音频描述字符串 */
    public void setTvAudioDisc(String audioDisc){
        this.audioDisc = audioDisc;
        tvAudioDisc.setText(audioDisc);
    }

    /**
     * 获取音频的名称
     * @return 名称 */
    public String getAudioName(){
        return audioName;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.audio_header:
                Dialog dlg = new AlertDialog.Builder(mContext)
                        .setTitle(R.string.com_tip)
                        .setMessage(R.string.audio_delete_for_sure)
                        .setPositiveButton(R.string.com_confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (listener != null){
                                    listener.onDelete(NoteAudioUnit.this);
                                }
                                // 释放资源
                                release();
                            }
                        })
                        .setNegativeButton(R.string.com_cancel, null)
                        .create();
                dlg.show();
                break;
            case R.id.audio_footer:
                if (isFirstTime){
                    isFirstTime = false;
                    mPlayer = new MediaPlayer();
                    mPlayer.setOnCompletionListener(this);
                    prepare();
                }
                if (isPrepared){
                    if(mPlayer.isPlaying()){
                        // 暂停
                        mPlayer.pause();
                        audioFooter.setImageResource(R.drawable.ic_audio_play);
                    }else {
                        // 播放
                        mPlayer.start();
                        audioFooter.setImageResource(R.drawable.ic_audio_pause);
                    }
                }
                break;
        }
    }

    private void prepare(){
        if (TextUtils.isEmpty(audioPath)){
            TpDisp.showToast(mContext, R.string.audio_play_null);
        } else if (!TpFile.isFileExist(audioPath)){
            TpDisp.showToast(mContext, R.string.audio_play_none_exist);
        } else {
            isPrepared = true;
            mPlayer.reset();
            try {
                mPlayer.setDataSource(audioPath);
                mPlayer.prepare();
                //int duration = mPlayer.getDuration();
                //String strTotalProg = StringUtils.formatDuration(duration);
                //tvTotalProg.setText(strTotalProg);
                //progBar.setMax(duration);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //progBar.setProgress(0);
            //btnPlay.setImageResource(R.drawable.ic_audio_play);
            //tvTempProg.setText("0:00");
        }
    }

    /**
     * 暂停正在播放 */
    public void stop(){
        if (mPlayer != null) {
            if (mPlayer.isPlaying()) {
                mPlayer.pause();
                audioFooter.setImageResource(R.drawable.ic_audio_play);
            }
        }
    }

    /**
     * 释放资源 */
    public void release(){
        if (mPlayer != null){
            if (mPlayer.isPlaying()){
                mPlayer.stop();
            }
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        audioFooter.setImageResource(R.drawable.ic_audio_play);
    }

    /**
     * 设置头部是否可以点击
     * @param enable 是否可以点击 */
    public void setHeaderEnable(boolean enable){
        audioHeader.setEnabled(enable);
    }

    public interface OnDeleteListener{
        void onDelete(View v);
    }

    private OnDeleteListener listener;

    public void setOnDeleteListener(OnDeleteListener listener){
        this.listener = listener;
    }
}
