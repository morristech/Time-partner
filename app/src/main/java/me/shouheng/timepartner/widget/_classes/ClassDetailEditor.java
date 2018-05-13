package me.shouheng.timepartner.widget._classes;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import me.shouheng.timepartner.models.entities.tpclass.ClassDetail;
import me.shouheng.timepartner.R;
import me.shouheng.timepartner.utils.TpDisp;
import me.shouheng.timepartner.utils.TpTime;
import me.shouheng.timepartner.widget.dialog.EdtDialog;
import me.shouheng.timepartner.utils.TpString;
import me.shouheng.timepartner.widget.textview.TimeTextView;

/**
 * 显示课程信息编辑对话框
 * 两种编辑类型：
 * 1.编辑已存在的课程信息，需要传入一个ClassDetailEntity
 * 2.编辑新的课程信息，不需要传入
 * 创建一个实例之后，调用showDialog()方法显示对话框，
 * setEntity必须在showDialog之前调用才能达到效果
 * 然后添加一个监听器，在其中获取编辑的结果即可 */
public class ClassDetailEditor implements View.OnClickListener{
    private Context mContext;
    private Dialog dlg;
    private ClassDetail entity;
    private TextView tvRoomState;
    private TextView tvTeacherState;
    private TextView tvWeeksState;
    private TimeTextView tvStartTime;
    private TimeTextView tvEndTime;

    public ClassDetailEditor(Context mContext){
        this.mContext = mContext;
    }

    public void showDialog(){
        View view = LayoutInflater.from(mContext).inflate(R.layout.class_detail_edit_layout, null);
        tvStartTime = (TimeTextView) view.findViewById(R.id.start_time);
        tvStartTime.setOnTimeTextClickListener(new TimeTextView.OnTimeTextClickListener() {
            @Override
            public void onClick(int hourOfDay, int minute) {
                if (sTimeTextClickListener != null){
                    sTimeTextClickListener.onClick(hourOfDay, minute);
                }
            }
        });

        tvEndTime = (TimeTextView) view.findViewById(R.id.end_time);
        tvEndTime.setOnTimeTextClickListener(new TimeTextView.OnTimeTextClickListener() {
            @Override
            public void onClick(int hourOfDay, int minute) {
                if (eTimeTextClickListener != null){
                    eTimeTextClickListener.onClick(hourOfDay, minute);
                }
            }
        });

        tvRoomState = (TextView) view.findViewById(R.id.location_state);
        tvTeacherState = (TextView) view.findViewById(R.id.teacher_state);
        tvWeeksState = (TextView) view.findViewById(R.id.weeks_state);
        view.findViewById(R.id.btn_cancel).setOnClickListener(this);
        view.findViewById(R.id.btn_confirm).setOnClickListener(this);
        tvWeeksState.setTag("0000000");
        if (entity != null){
            String strStartTime = TpTime.getFormatTime(entity.getStartTime());
            String strEndTime = TpTime.getFormatTime(entity.getEndTime());
            tvStartTime.setText(strStartTime);
            tvEndTime.setText(strEndTime);
            String strRoom = entity.getRoom();
            if (!TextUtils.isEmpty(strRoom)){
                tvRoomState.setText(strRoom);
            }
            String strTeacher = entity.getTeacher();
            if (!TextUtils.isEmpty(strTeacher)){
                tvTeacherState.setText(strTeacher);
            }
            String strWeek =  entity.getWeek();
            if (!TextUtils.isEmpty(strWeek)){
                String pWeek = TpString.getWeekString(mContext, strWeek);
                tvWeeksState.setText(pWeek);
                tvWeeksState.setTag(strWeek);
            }
        }

        int[] clickViewIds = new int[]{
                R.id.location, R.id.weeks, R.id.teacher};
        for (int id : clickViewIds){
            view.findViewById(id).setOnClickListener(this);
        }

        dlg = new AlertDialog.Builder(mContext)
                .setTitle(R.string.class_add_item)
                .setView(view)
                .create();
        dlg.setCancelable(false);
        dlg.show();
    }

    public void setEntity(ClassDetail entity){
        this.entity = entity;
    }

    public void setStartTime(String sTime){
        tvStartTime.setText(sTime);
    }

    public void setEndTime(String eTime){
        tvEndTime.setText(eTime);
    }

    private ClassDetail getEntity(){
        if (entity == null){
            entity = new ClassDetail();
        }

        String strSTime = tvStartTime.getText().toString();
        String strETime = tvEndTime.getText().toString();
        String strRoom = tvRoomState.getText().toString();
        String strTeac = tvTeacherState.getText().toString();
        String strWeek = tvWeeksState.getTag().toString();

        entity.setStartTime(TpTime.getMillisFromTime(strSTime));
        entity.setEndTime(TpTime.getMillisFromTime(strETime));
        entity.setRoom(strRoom);
        entity.setTeacher(strTeac);
        entity.setWeek(strWeek);

        return entity;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.location:
                EdtDialog dialog = new EdtDialog(mContext,
                        EdtDialog.DIALOG_TYPE_NORMAL,
                        tvRoomState.getText().toString(),
                        mContext.getString(R.string.edit_location_title),
                        mContext.getString(R.string.class_location_tip));
                dialog.setPositiveButton(R.string.com_confirm, new EdtDialog.PositiveButtonClickListener() {
                    @Override
                    public void onClick(String content) {
                        if (TextUtils.isEmpty(content)) {
                            makeToast(R.string.edit_empty);
                        } else {
                            tvRoomState.setText(content);
                        }
                    }
                });
                dialog.setNegativeListener(R.string.com_cancel, null);
                dialog.show();
                break;
            case R.id.weeks:
                showWeeksOptions();
                break;
            case R.id.teacher:
                EdtDialog dialog1 = new EdtDialog(mContext,
                        EdtDialog.DIALOG_TYPE_NORMAL,
                        tvTeacherState.getText().toString(),
                        mContext.getString(R.string.edit_teacher_title),
                        mContext.getString(R.string.class_teacher_tip));
                dialog1.setPositiveButton(R.string.com_confirm, new EdtDialog.PositiveButtonClickListener() {
                    @Override
                    public void onClick(String content) {
                        if (TextUtils.isEmpty(content)) {
                            makeToast(R.string.edit_empty);
                        } else {
                            tvTeacherState.setText(content);
                        }
                    }
                });
                dialog1.setNegativeListener(R.string.com_cancel, null);
                dialog1.show();
                break;
            case R.id.btn_confirm:
                onConfirm();
                break;
            case R.id.btn_cancel:
                if (dlg != null && dlg.isShowing()){
                    dlg.dismiss();
                }
                break;
        }
    }

    private void onConfirm(){
        String strWeek = tvWeeksState.getText().toString();
        if (TextUtils.isEmpty(strWeek)){
            makeToast(R.string.class_detail_error_content);
        } else {
            String strSTime = tvStartTime.getText().toString();
            String strETime = tvEndTime.getText().toString();
            int lSTime = TpTime.getMillisFromTime(strSTime);
            int lETime = TpTime.getMillisFromTime(strETime);
            if (lSTime > lETime){
                makeToast(R.string.clse_t_error_msg);
            } else {
                if (listener != null){
                    if (entity == null){
                        listener.onCompleted(true, getEntity());
                    } else {
                        listener.onCompleted(false, getEntity());
                    }
                }
                if (dlg != null && dlg.isShowing()){
                    dlg.dismiss();
                }
            }
        }
    }

    private void showWeeksOptions(){
        final boolean checked[] = new boolean[7];
        for (int i=0;i<7;i++){
            checked[i] = (tvWeeksState.getTag().toString().charAt(i)=='1');
        }
        Dialog dlg = new AlertDialog.Builder(mContext)
                .setTitle(R.string.class_week)
                .setMultiChoiceItems(R.array.week_strings, checked, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        checked[which] = isChecked;
                    }
                })
                .setPositiveButton(R.string.com_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String[] result = TpString.parseWeekString(
                                mContext.getResources().getStringArray(R.array.week_strings),
                                checked);
                        tvWeeksState.setText(result[0]);
                        tvWeeksState.setTag(result[1]);
                    }
                })
                .setNegativeButton(R.string.com_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                })
                .create();
        dlg.show();
    }

    private void makeToast(int strRes){
        TpDisp.showToast(mContext, strRes);
    }

    private OnEditCompletedListener listener;

    /**
     * 自定义回调接口，传出的值的含义是
     * isNew: 是否是新编辑的课程实体
     * entity：返回编辑好的课程实体 */
    public interface OnEditCompletedListener{
        void onCompleted(boolean isNew, ClassDetail entity);
    }

    public void setOnEditCompletedListener(OnEditCompletedListener listener){
        this.listener = listener;
    }

    private OnSTimeTextClickListener sTimeTextClickListener;

    public interface OnSTimeTextClickListener{
        void onClick(int hourOfDay, int minute);
    }

    public void setOnSTimeTextClickListener(OnSTimeTextClickListener listener){
        this.sTimeTextClickListener = listener;
    }

    private OnETimeTextClickListener eTimeTextClickListener;

    public interface OnETimeTextClickListener{
        void onClick(int hourOfDay, int minute);
    }

    public void setOnETimeTextClickListener(OnETimeTextClickListener listener){
        this.eTimeTextClickListener = listener;
    }
}
