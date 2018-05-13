package me.shouheng.timepartner.widget.textview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import me.shouheng.timepartner.R;

public class TimeTextView extends TextView {
    private int millisOfTime;
    private OnTimeTextClickListener listener;

    public TimeTextView(Context context) {
        super(context);
        init(context);
    }

    public TimeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TimeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(final Context mContext){
        millisOfTime = currentTime();
        setText(formatTime(millisOfTime));
        setTextSize(16);
        setTextColor(Color.BLACK);
        setBackgroundResource(R.drawable.s_rect_black10back);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int time[] = getTimeArray(getText().toString());
                if (listener != null){
                    listener.onClick(time[0], time[1]);
                }
            }
        });
    }

    public void setTime(String strTime){
        setText(strTime);
        int[] arr = getTimeArray(strTime);
        this.millisOfTime = getMillis(arr[0], arr[1]);
    }

    public void setTime(int iTime){
        millisOfTime = iTime;
        setText(formatTime(iTime));
    }

    public int getMillisOfTime(){
        return millisOfTime;
    }

    private int currentTime(){
        Calendar now = Calendar.getInstance();
        return (now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)) * 60 * 1000;
    }

    private int[] getTimeArray(String time){
        String []timeStrArray = time.split(":");
        return new int[]{
                Integer.parseInt(timeStrArray[0]),
                Integer.parseInt(timeStrArray[1])};
    }

    private int getMillis(int hour, int minute){
        return ((hour * 60 + minute) * 60) * 1000;
    }

    private String formatTime(int iTime){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        long millis = 16 * 60 * 60 * 1000 + (long) iTime;
        Date date = new Date(millis);
        return sdf.format(date);
    }

    public interface OnTimeTextClickListener{
        void onClick(int hourOfDay, int minute);
    }

    public void setOnTimeTextClickListener(OnTimeTextClickListener listener){
        this.listener = listener;
    }
}
