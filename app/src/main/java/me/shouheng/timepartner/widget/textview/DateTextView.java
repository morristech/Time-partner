package me.shouheng.timepartner.widget.textview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import me.shouheng.timepartner.R;

public class DateTextView extends TextView{
    private long millisOfDate;
    private OnDateTextClickListener listener;

    public DateTextView(Activity activity) {
        super(activity);
        init(activity);
    }

    public DateTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DateTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setDate(long millisOfDate){
        this.millisOfDate = millisOfDate;
        setText(formatDate(millisOfDate));
    }

    public void setDate(String strDate){
        int[] dateArray = getDateArray(strDate);
        this.millisOfDate = getMillis(dateArray[0], dateArray[1] - 1, dateArray[2]);
        setText(strDate);
    }

    public long getMillisOfDate(){
        return millisOfDate;
    }

    private void init(final Context activity){
        millisOfDate = currentDate();
        setText(formatDate(System.currentTimeMillis()));
        setTextSize(16);
        setTextColor(Color.BLACK);
        setBackgroundResource(R.drawable.s_rect_black10back);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] dateArray = getDateArray(getText().toString());
                if (listener != null){
                    listener.onClick(dateArray[0], dateArray[1] - 1, dateArray[2]);
                }
            }
        });
    }

    private int[] getDateArray(String strDate){
        String[] strs = strDate.split("-");
        return new int[]{
                Integer.parseInt(strs[0]),
                Integer.parseInt(strs[1]),
                Integer.parseInt(strs[2])};
    }

    private String formatDate(long lDate){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(lDate);
        return sdf.format(date);
    }

    private long currentDate(){
        Calendar now = Calendar.getInstance();
        now.set(now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        return now.getTimeInMillis() / 1000 * 1000;
    }

    private long getMillis(int year, int month, int day){
        Calendar time = Calendar.getInstance();
        time.set(year, month, day, 0, 0, 0);
        return time.getTimeInMillis() / 1000 * 1000;
    }

    public interface OnDateTextClickListener{
        void onClick(int year, int month, int day);
    }

    public void setOnDateTextClickListener(OnDateTextClickListener listener){
        this.listener = listener;
    }
}
