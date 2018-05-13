package me.shouheng.timepartner.widget.unit;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import me.shouheng.timepartner.R;
import me.shouheng.timepartner.utils.TpTime;

public class NoteAlarmUnit extends RelativeLayout implements View.OnClickListener{
    private Context mContext;
    private TextView tvAlarmDate;
    private TextView tvAlarmTime;
    private long lDate;
    private int lTime;
    private String strDate;
    private String strTime;

    public NoteAlarmUnit(Context context) {
        super(context);
        init(context);
    }

    public NoteAlarmUnit(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NoteAlarmUnit(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * 初始化
     * @param context 上下文 */
    private void init(Context context){
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.view_unit_alarm, this);
        ImageView alarmHeader = (ImageView) findViewById(R.id.alarm_header);
        tvAlarmDate = (TextView) findViewById(R.id.alarm_date);
        tvAlarmTime = (TextView) findViewById(R.id.alarm_time);
        alarmHeader.setOnClickListener(this);
    }

    /**
     * 设置日期 毫秒
     * @param lDate 日期毫秒 */
    public void setAlarmDate(long lDate){
        this.lDate = lDate;
        this.strDate = TpTime.getFormatDate(lDate);
        tvAlarmDate.setText(strDate);
    }

    public void setAlarmDate(String strDate){
        setAlarmDate(TpTime.getMillisFromDate(strDate));
    }

    /**
     * 设置时间 毫秒
     * @param lTime 时间 毫秒 */
    public void setAlarmTime(int lTime){
        this.lTime = lTime;
        this.strTime = TpTime.getFormatTime(lTime);
        tvAlarmTime.setText(strTime);
    }

    public void setAlarmTime(String strTime){
        setAlarmTime(TpTime.getMillisFromTime(strTime));
    }

    /**
     * 获取日期 毫秒
     * @return 日期 */
    public long getAlarmDate(){
        return lDate;
    }

    /**
     * 获取时间 毫毛
     * @return 时间 */
    public int getAlarmTime(){
        return lTime;
    }

    public String getStrDate(){
        return strDate;
    }

    public String getStrTime(){
        return strTime;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.alarm_header:
                Dialog dlg = new AlertDialog.Builder(mContext)
                        .setTitle(R.string.class_options)
                        .setItems(R.array.view_unit_header_options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case 0:
                                        // 编辑
                                        if (listener != null){
                                            listener.onSelected(OPTIONS_TYPE_REEDIT);
                                        }
                                        break;
                                    case 1:
                                        // 删除
                                        if (listener != null){
                                            listener.onSelected(OPTIONS_TYPE_DELETE);
                                        }
                                        break;
                                }
                            }
                        })
                        .create();
                dlg.show();
                break;
        }
    }

    public static final int OPTIONS_TYPE_REEDIT  = 0;
    public static final int OPTIONS_TYPE_DELETE  = 1;

    private OnOptionsSelectedListener listener;

    public interface OnOptionsSelectedListener {
        void onSelected(int type);
    }

    public void setOnOptionsSelectedListener(OnOptionsSelectedListener listener){
        this.listener = listener;
    }
}
