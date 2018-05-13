package me.shouheng.timepartner.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import me.shouheng.timepartner.R;
import me.shouheng.timepartner.utils.TpTime;
import me.shouheng.timepartner.widget.textview.DateTextView;
import me.shouheng.timepartner.widget.textview.TimeTextView;

public class DateTimePickerDialog {
    private Context mContext;
    private int titleRes = R.string.n_alarm_setting_title;
    private DateTextView tvNDate;
    private TimeTextView tvNTime;
    private String strDate;
    private String strTime;

    public DateTimePickerDialog(Context context){
        mContext = context;
    }

    public void setStrTitle(int titleRes){
        this.titleRes = titleRes;
    }

    public void setDate(String strDate){
        this.strDate = strDate;
    }

    public void setTime(String strTime){
        this.strTime = strTime;
    }

    public void setDate(long miilis){
        this.strDate = TpTime.getFormatDate(miilis);
        tvNDate.setText(strDate);
    }

    public void setTime(int millis){
        this.strTime = TpTime.getFormatTime(millis);
        tvNTime.setText(strTime);
    }

    public void show(){
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.view_notice_edit_layout, null);
        tvNTime = (TimeTextView) view.findViewById(R.id.notice_time);
        tvNDate = (DateTextView) view.findViewById(R.id.notice_date);
        tvNDate.setOnDateTextClickListener(new DateTextView.OnDateTextClickListener() {
            @Override
            public void onClick(int year, int month, int day) {
                if (dateClickListener != null){
                    dateClickListener.onClick();
                }
            }
        });
        tvNTime.setOnTimeTextClickListener(new TimeTextView.OnTimeTextClickListener() {
            @Override
            public void onClick(int hourOfDay, int minute) {
                if (timeClickListener != null){
                    timeClickListener.onClick();
                }
            }
        });
        if (!TextUtils.isEmpty(strTime)){
            tvNTime.setText(strTime);
        }
        if (!TextUtils.isEmpty(strDate)){
            tvNDate.setText(strDate);
        }
        Dialog dlg = new AlertDialog.Builder(mContext)
                .setView(view)
                .setPositiveButton(R.string.com_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null){
                            listener.onConfirm(tvNDate.getText().toString(),
                                    tvNTime.getText().toString());
                        }
                    }
                })
                .setNegativeButton(R.string.com_cancel, null)
                .create();
        if (titleRes != -1){
            dlg.setTitle(titleRes);
        }
        dlg.show();
    }

    private OnConfirmListener listener;

    public interface OnConfirmListener{
        void onConfirm(String strDate, String strTime);
    }

    public void setOnConfirmListener(OnConfirmListener listener){
        this.listener = listener;
    }

    private OnDateClickListener dateClickListener;

    public interface OnDateClickListener{
        void onClick();
    }

    public void setOnDateClickListener(OnDateClickListener listener){
        dateClickListener = listener;
    }

    private OnTimeClickListener timeClickListener;

    public interface OnTimeClickListener{
        void onClick();
    }

    public void setOnTimeClickListener(OnTimeClickListener listener){
        timeClickListener = listener;
    }
}
