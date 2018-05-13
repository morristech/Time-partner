package me.shouheng.timepartner.widget.custom;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.LongSparseArray;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import me.shouheng.timepartner.R;
import me.shouheng.timepartner.utils.TpDisp;
import me.shouheng.timepartner.utils.TpTime;

/**
 * 获取实例 设置好之后使用 draw() 方法进行绘制
 * 设置包括设置内部的 数据集合 和 要显示的年月 */
public class TpCalendar extends RelativeLayout implements View.OnTouchListener{
    private Context mContext;
    // 日期信息
    private int mYear;
    private int mMonth;
    private int mDay;
    private int weekOfFirstDay;         // 本月第一天是周几
    private int daysOfThisMonth;        // 本月的总天数
    // 上个月.
    private int yearOfLastMonth;        // 上个月是几几年
    private int lastMonth;              // 上个月是几月
    private int daysOfLastMonth;        // 上个月有几天
    // 下个月
    private int yearOfNextMonth;        // 下个月是几几年
    private int nextMonth;
    // 线
    private int lineWidth; // 单位 px
    private int lineColor;
    private int textSize = 12;
    // 屏幕的尺寸信息
    private int screenWidth;
    private int screenHeight;
    private int unitWidth;
    private int unitHeight;
    private int unitPad;
    // 日历编号  全局 用于记录组件当前的位置
    private int rowId = 0;
    private int colId = 0;
    private int addedNum = 0; // 记录已经加入的记录的个数，等于42时停止添加
    // 加载的数据集合 键是数据的日期毫秒数；值是数据的颜色字符串集合
    private LongSparseArray<List<String>> map = new LongSparseArray<>();
    // 评分的数据集合 键是数据的日期毫秒数；值是评分的数值的集合
    private LongSparseArray<Float> rateMap = new LongSparseArray<>();
    // 按下的背景的位置填充
    private TextView tvBack;
    private int sColor = getResources().getColor(R.color.calendar_selection);
    private static final String TAG = "TpCalendar__";
    private long[] arrDate = new long[42];

    public TpCalendar(Context context) {
        super(context);
        mContext = context;
    }

    public TpCalendar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public TpCalendar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public void setLineWidth(int dpValue){
        this.lineWidth = TpDisp.dp2Px(mContext, dpValue);
    }

    /**
     * @param mMonth 月 1即1月 */
    public void setDate(int mYear, int mMonth, int mDay){
        this.mYear = mYear;
        this.mMonth = mMonth;
        this.mDay = mDay;
    }

    public void setMap(LongSparseArray<List<String>> map){
        this.map = map;
    }

    public void setRateMap(LongSparseArray<Float> rateMap){
        this.rateMap = rateMap;
    }

    public void draw(){
        // 初始化信息
        init();
        // 画线
        drawLines();
        // 绘制上个月的数据
        drawLastMonth();
        // 绘制本月的数据
        drawThisMonth();
        // 添加下个月的数据
        drawNextMonth();
    }

    private void drawLines(){
        int hLineNum = 6;
        int dLineNum = 6;
        // 画竖线
        for (int i=1;i<=dLineNum;i++){
            addView(getLine(i*unitWidth, 0, lineWidth, screenHeight));
        }
        // 画横线
        for (int i=1;i<=hLineNum;i++){
            addView(getLine(0, i*unitHeight, screenWidth, lineWidth));
        }
    }

    private void drawLastMonth(){
        for (int day=daysOfLastMonth - weekOfFirstDay + 1;day<=daysOfLastMonth;day++){
            // 添加日期和评分
            addDateView(yearOfLastMonth, lastMonth, day, lineColor);
            // 编号增加
            increaseId();
        }
    }

    private void drawThisMonth(){
        for (int day = 1;day<=daysOfThisMonth;day++){
            // 添加日期和评分
            addDateView(mYear, mMonth, day, Color.BLACK);
            // 编号增加
            increaseId();
        }
    }

    private void drawNextMonth(){
        int day = 1;
        while (true){
            // 添加日期和评分
            addDateView(yearOfNextMonth, nextMonth, day++, lineColor);
            // 编号增加
            increaseId();
            // 退出条件
            if (addedNum == 42){
                break;
            }
        }
    }

    private void addDateView(int mYear, int mMonth, int mDay, int mColor){
        String dateText = "";
        if (screenWidth > screenHeight){
            dateText = String.valueOf(mDay) + "  " + TpTime.getStringDate(mContext, mYear, mMonth, mDay);
        } else {
            dateText = String.valueOf(mDay) + "\n" + TpTime.getStringDate(mContext, mYear, mMonth, mDay);
        }
        // 添加日期
        TextView dateView = getPreparedTextView();
        dateView.setText(dateText);
        dateView.setTextColor(mColor);
        addView(dateView);
        // 添加评分
        long millis = TpTime.getMillisFromDate(mYear, mMonth - 1, mDay);
        Float rate = rateMap.get(millis);
        if (rate != null) {
            TextView rateView = getPreparedTextView();
            rateView.setGravity(Gravity.END);
            rateView.setText(String.valueOf(rate));
            rateView.setTextColor(Color.RED);
            addView(rateView);
        }
        // 添加详细信息
        addDetails(millis);
        // 使用数组记录指定位置的日期的毫秒数
        int index = rowId * 7 + colId;
        if (index >= 0 && index < 42){
            arrDate[index] = millis;
        }
        // 设置今天
        if (mMonth == this.mMonth && mDay == this.mDay){
            bgMoveTo(rowId, colId);
        }
    }

    private void increaseId(){
        // 列编号自增
        colId++;
        // 当列达到最后一个时 行号增加
        if (colId == 7){
            colId = 0;
            rowId++;
            if (rowId == 6){
                rowId = 0;
            }
        }
        addedNum++;
    }

    private void addDetails(long millis){
        // 每个item标记显示的宽度
        int itemWidth = TpDisp.dp2Px(mContext, 8);
        // 每个item标记显示的高度
        int itemHeight = TpDisp.dp2Px(mContext, 14);
        // 每行item的总数
        int itemNum = (unitWidth - itemWidth) / (itemWidth + unitPad);
        // 计算将item填入之后剩余的空间，为将标记添加到中间位置需要除以2
        int itemSpec = ((unitWidth - itemWidth) % (itemWidth + unitPad)) / 2;
        // 下面两个变量记录内部的显示组件在x个y方向上的偏移
        int xOffset = itemSpec;
        int yOffset = TpDisp.sp2Px(mContext, 30f) + 2 * unitPad; //默认从偏移的位置开始
        if (screenWidth > screenHeight){
            yOffset = TpDisp.sp2Px(mContext, 15f) + 2 * unitPad; //默认从偏移的位置开始
        }
        // 记录已经添加了的标记的个数，用于换行处理
        int itemId = 0;
        // 获取颜色资源
        List<String> colorList = map.get(millis);
        if (colorList != null) {
            for (String strColor : colorList){
                // 添加组件
                int pColor = Color.parseColor(strColor);
                ViewGroup.LayoutParams params = new LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                TextView tv = new TextView(mContext);
                tv.setBackgroundColor(pColor);
                tv.setX(colId * unitWidth + xOffset);
                tv.setY(rowId * unitHeight + yOffset);
                tv.setWidth(itemWidth);
                tv.setHeight(itemHeight);
                tv.setLayoutParams(params);
                xOffset = xOffset + unitPad + itemWidth;
                {
                    itemId ++;
                    if (itemNum + 1 == itemId){
                        itemId = 0;
                        xOffset = itemSpec;
                        yOffset = yOffset + itemHeight + unitPad;
                    }
                }
                addView(tv);
            }
        }
    }

    private TextView getPreparedTextView(){
        ViewGroup.LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView dateView = new TextView(mContext);
        dateView.setX(colId * unitWidth);
        dateView.setY(rowId * unitHeight);
        dateView.setWidth(unitWidth);
        dateView.setPadding(unitPad, unitPad, unitPad, unitPad);
        dateView.setTextSize(textSize);
        dateView.setLayoutParams(params);
        return dateView;
    }

    private TextView getLine(float x, float y, int w, int h){
        TextView line = new TextView(mContext);
        line.setX(x);
        line.setY(y);
        line.setWidth(w);
        line.setHeight(h);
        line.setBackgroundColor(lineColor);
        return line;
    }

    private void bgMoveTo(int rowId, int colId){
        tvBack.setX(colId * unitWidth);
        tvBack.setY(rowId * unitHeight);
    }

    private void init(){
        setBackgroundColor(getResources().getColor(android.R.color.background_light));
        // 获取屏幕尺寸信息
        WindowManager window = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        window.getDefaultDisplay().getRealSize(point);
        screenHeight = point.y;
        screenWidth = point.x;
        unitWidth = screenWidth / 7;
        unitHeight = screenHeight / 6;
        unitPad = TpDisp.dp2Px(mContext, 1f);
        // 线
        lineWidth = TpDisp.dp2Px(mContext, 0.5f);
        lineColor = mContext.getResources().getColor(R.color.gray);
        // 本月
        weekOfFirstDay = TpTime.zellerWeek(mYear, mMonth, 1);
        daysOfThisMonth = TpTime.getDaysOfMonth(mYear, mMonth);
        // 添加上个月的数据......
        yearOfLastMonth = mYear;        //用于TAG的年份
        lastMonth = mMonth - 1;  //用于TAG的月份
        // 上个月遗留的日期
        if (lastMonth == 0){
            lastMonth = 12;
            yearOfLastMonth = mYear - 1;
            daysOfLastMonth = TpTime.getDaysOfMonth(yearOfLastMonth, lastMonth);
        } else {
            daysOfLastMonth = TpTime.getDaysOfMonth(mYear, lastMonth);
        }
        // 下个月
        yearOfNextMonth = mYear;
        nextMonth = mMonth + 1;
        if (nextMonth == 13){
            nextMonth = 1;
            yearOfNextMonth = mYear + 1;
        }
        // 设置点击监听事件
        tvBack = new TextView(mContext);
        tvBack.setWidth(unitWidth);
        tvBack.setHeight(unitHeight);
        tvBack.setBackgroundColor(sColor);
        addView(tvBack);
        mGestureDetector = new GestureDetector(mContext, new MyOnGestureListener());
        setOnTouchListener(this);
    }

    private GestureDetector mGestureDetector;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return true;
    }

    private class MyOnGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            int x = (int) e.getX();
            int y = (int) e.getY();
            int colId = x / unitWidth;
            int rowId = y / unitHeight;
            bgMoveTo(rowId, colId);
            // 回调返回日期的毫秒
            if (listener != null){
                int index = rowId * 7 + colId;
                if (index >= 0 && index < 42){
                    listener.onSelected(arrDate[index]);
                }
            }
            return false;
        }
    }

    private OnDaySelectedListener listener;

    public interface OnDaySelectedListener{
        void onSelected(long millis);
    }

    public void setOnDaySelectedListener(OnDaySelectedListener listener){
        this.listener = listener;
    }
}
