package me.shouheng.timepartner.widget.custom;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import me.shouheng.timepartner.R;
import me.shouheng.timepartner.managers.RecordManager;

public class RecordView extends RelativeLayout implements View.OnTouchListener{
    private RelativeLayout record;
    private LinearLayout recording;
    private AnimationDrawable animDraw;
    private RecordManager rdManager;
    private Context mContext;
    private String filePath;
    private String fileName;

    public RecordView(Context context) {
        super(context);
        init(context);
    }

    public RecordView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RecordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * 初始化组件
     * @param context 上下文 */
    private void init(Context context){
        mContext = context;
        // 获取并初始化组件
        LayoutInflater.from(context).inflate(R.layout.view_record, this);
        record = (RelativeLayout) findViewById(R.id.record);
        record.setVisibility(GONE);
        ImageView recordButton = (ImageView) findViewById(R.id.record_button);
        recordButton.setOnTouchListener(this);
        recording = (LinearLayout) findViewById(R.id.recording);
        recording.setVisibility(GONE);
        ImageView recordImage = (ImageView) findViewById(R.id.record_image);
        // 获取信息
        recordImage.setImageResource(R.drawable.record_anim);
        animDraw = (AnimationDrawable) recordImage.getDrawable();
        // 录音管理类
        rdManager = new RecordManager(context);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()){
            case R.id.record_button:
                // 录音
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    // 开始
                    recording.setVisibility(View.VISIBLE);
                    animDraw.start();
                    rdManager.start();
                }else if (event.getAction() == MotionEvent.ACTION_UP){
                    // 结束
                    recording.setVisibility(View.GONE);
                    animDraw.stop();
                    rdManager.stop();
                    Dialog recordDlg = new AlertDialog.Builder(mContext)
                            .setTitle(R.string.record_dlg_title)
                            .setMessage(R.string.record_dlg_content)
                            .setPositiveButton(R.string.com_save, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 保存
                                    rdManager.save();
                                    fileName = rdManager.getFileName();
                                    filePath = rdManager.getFilePath();
                                    onRecordEnd();
                                    if (listener!=null){
                                        listener.onRecordEnd(filePath, fileName);
                                    }
                                }
                            })
                            .setNegativeButton(R.string.com_cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 删除
                                    rdManager.dispose();
                                    onRecordEnd();
                                }
                            })
                            .create();
                    recordDlg.show();
                }
                break;
        }
        return true;
    }

    /**
     * 显示录音按钮 */
    public void show(){
        record.setVisibility(VISIBLE);
    }

    /**
     * 隐藏录音组件 */
    public void hide(){
        record.setVisibility(GONE);
    }

    /**
     * 获取录音组件是否正在显示
     * @return 是否正在显示 */
    public boolean isShowing(){
        return record.getVisibility() == VISIBLE;
    }

    /**
     * 录音完毕 */
    private void onRecordEnd(){
        record.setVisibility(View.GONE);
        // 释放资源
        if (rdManager != null && !rdManager.isStop()){
            rdManager.stop();
        }
    }

    private OnRecordEndListener listener;

    public interface OnRecordEndListener{
        void onRecordEnd(String filePath, String fileName);
    }

    public void setRecordEndListener(OnRecordEndListener listener){
        this.listener = listener;
    }
}
