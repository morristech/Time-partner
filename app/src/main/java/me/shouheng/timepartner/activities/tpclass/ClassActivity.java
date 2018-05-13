package me.shouheng.timepartner.activities.tpclass;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import me.shouheng.timepartner.database.dao.bo.ClassBoDAO;
import me.shouheng.timepartner.models.business.tpclass.ClassBO;
import me.shouheng.timepartner.models.entities.tpclass.ClassDetail;
import me.shouheng.timepartner.R;
import me.shouheng.timepartner.activities.base.BaseActivity;
import me.shouheng.timepartner.utils.TpColor;
import me.shouheng.timepartner.utils.TpDisp;
import me.shouheng.timepartner.utils.TpPrefer;
import me.shouheng.timepartner.utils.TpShare;
import me.shouheng.timepartner.utils.TpTime;
import me.shouheng.timepartner.widget.dialog.EdtDialog;

public class ClassActivity extends BaseActivity implements View.OnClickListener {
    private ActionBar actionBar;
    private RelativeLayout boardFG;
    private LinearLayout dateBar;

    private int widthWeek;      // "星期"宽度(pixel)
    private int heightHour;     // "小时"高度(pixel)
    private int startTime;      // "最小时间"(h)
    private int endTime;        // "最大时间"(h)
    private int lineWidth;      // 线的宽度(pixel)
    private int screenWidth;
    private int screenHeight;

    //该课程表所基于的时间值( 0表示1月 )
    private int iYear;
    private int iMonth;
    private int iDay;

    private long duration = 400;

    private List<View> listViews = new LinkedList<>();

    public static void start(Context mContext){
        Intent intent = new Intent(mContext, ClassActivity.class);
        mContext.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.class_dash_layout);

        initActionBar();

        initBoard();

        Calendar now = Calendar.getInstance();
        iYear = now.get(Calendar.YEAR);
        iMonth = now.get(Calendar.MONTH);
        iDay = now.get(Calendar.DAY_OF_MONTH);
        initData(iYear, iMonth, iDay);
    }

    private void initActionBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.class_title));
            actionBar.setDisplayHomeAsUpEnabled(true);

            Calendar now = Calendar.getInstance();
            setSubTitle(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        }
    }

    /**
     * 初始化工具栏的副标题
     * @param mYear 年
     * @param mMonth 注意!!!  0表示1月
     * @param mDay 日 */
    private void setSubTitle(int mYear, int mMonth, int mDay){
        long seTime[] = TpTime.getWeekMondays(mYear, mMonth, mDay);
        long weekStart = seTime[0];
        long weekEnd = seTime[1];
        String subtitle = TpTime.getDate(weekStart, TpTime.DATE_TYPE_3) + " - "
                + TpTime.getDate(weekEnd, TpTime.DATE_TYPE_3);
        actionBar.setSubtitle(subtitle);
    }

    private void initBoard(){
        LinearLayout weekBar = (LinearLayout)findViewById(R.id.class_week_bar);
        LinearLayout timeBar = (LinearLayout)findViewById(R.id.class_time_bar);
        boardFG = (RelativeLayout)findViewById(R.id.class_board_fg);
        LinearLayout vBoard = (LinearLayout)findViewById(R.id.class_board_bg_v);
        LinearLayout hBoard = (LinearLayout)findViewById(R.id.class_board_bg_h);
        dateBar = (LinearLayout)findViewById(R.id.date_bar);
        // 获取浮动按钮并设置监听事件
        findViewById(R.id.fab).setOnClickListener(this);

        // 初始化课程表的范围值
        int bgColor = getResources().getColor(R.color.gray);
        int sumWeek = 7;
        startTime = TpPrefer.getInstance(this).getClsStartTime();
        endTime = TpPrefer.getInstance(this).getClsEndTime();

        // 获取屏幕尺寸
        Point point = TpDisp.getWindowSize(this);
        screenHeight = point.y;
        screenWidth = point.x;
        widthWeek = (int)(point.x / (sumWeek - 0));
        heightHour = point.y / TpPrefer.getInstance(this).getClsHourHeight();
        lineWidth = TpDisp.sp2Px(this, 0.5f);

        // 星期栏
        TextView tvPad = new TextView(this);
        tvPad.setText("00");
        tvPad.setTextSize(12);
        tvPad.setTextColor(Color.TRANSPARENT);
        weekBar.addView(tvPad);
        String[] strs = getResources().getStringArray(R.array.week_strings_monday);
        int flag = 0;
        for (String str : strs){
            TextView tvWeek = new TextView(this);
            tvWeek.setText(str);
            tvWeek.setWidth(widthWeek);
            tvWeek.setTextSize(12);
            flag++;
            if (flag == 6 || flag == 7){
                tvWeek.setTextColor(Color.parseColor(TpColor.COLOR_CLASS));
            }
            weekBar.addView(tvWeek);
        }

        // 时间栏
        for (int i = startTime; i<= endTime; i++){
            //绘制左侧时间栏
            TextView tvTime = new TextView(this);
            tvTime.setHeight(heightHour);
            if (i < 10){
                tvTime.setText(String.valueOf(i));
            } else {
                tvTime.setText(String.valueOf(i));
            }
            tvTime.setGravity(Gravity.CENTER_VERTICAL);
            tvTime.setTextSize(12);
            timeBar.addView(tvTime);
            //画横线
            //用于填充的组件
            TextView tvVPad = new TextView(this);
            tvVPad.setHeight(heightHour - lineWidth);
            vBoard.addView(tvVPad);
            TextView hLine = new TextView(this);
            hLine.setWidth(point.x);
            hLine.setHeight(lineWidth);
            hLine.setBackgroundColor(bgColor);
            vBoard.addView(hLine);
        }

        // 画竖线
        for (int i = 0;i<sumWeek;i++){
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            TextView tvHPad = new TextView(this);
            tvHPad.setLayoutParams(params);
            tvHPad.setWidth(widthWeek - lineWidth);
            hBoard.addView(tvHPad);
            TextView textLine = new TextView(this);
            textLine.setWidth(lineWidth);
            textLine.setLayoutParams(params);
            textLine.setBackgroundColor(bgColor);
            hBoard.addView(textLine);
        }
    }

    /**
     * 根据指定的日期，初始化界面数据
     * @param mYear 年
     * @param mMonth 注意!!!  0表示1月
     * @param mDay 日 */
    private void initData(int mYear, int mMonth, int mDay){
        // 设置日期栏
        dateBar.removeAllViews();
        TextView tvPad = new TextView(this);
        tvPad.setText("00");
        tvPad.setTextSize(12);
        tvPad.setTextColor(Color.TRANSPARENT);
        dateBar.addView(tvPad);
        String strTimes[] = TpTime.getWeekStringsMonday(mYear, mMonth, mDay);
        for (String str : strTimes){
            TextView tvTime = new TextView(this);
            tvTime.setText(str);
            tvTime.setTextSize(12);
            tvTime.setTextColor(Color.parseColor(TpColor.COLOR_CLASS));
            tvTime.setWidth(TpDisp.getWindowWidth(this) / 7);
            dateBar.addView(tvTime);
        }

        // 移除界面内全部的课程组件
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.class_out);
        for (final View view : listViews){
            // 先让组件全部不可见，然后再从组件中移除
            view.startAnimation(anim);
            view.setVisibility(View.GONE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // 不能使用removeAll 因为在这段时间里可能又添加了新的组件
                    boardFG.removeView(view);
                }
            }, anim.getDuration());
        }

        // 设置背景时间线
        long[] millis = TpTime.getWeekMondays(mYear, mMonth, mDay);
        long millisToday = TpTime.millisOfCurrentDate();
        int flag = 0;
        for (long date = millis[0];date<=millis[1];date+= TpTime.DAY_MILLIS){
            if (date == millisToday){
                int tlColor = Color.parseColor("#2099e49c");
                int tlColor2 = Color.parseColor("#99e49c");

                TextView tvBG = new TextView(this);
                tvBG.setX(widthWeek * flag);
                tvBG.setY(0);
                tvBG.setWidth(widthWeek - 2 * lineWidth);
                tvBG.setHeight(heightHour * (endTime + 1 - startTime));
                tvBG.setBackgroundColor(tlColor);
                boardFG.addView(tvBG);
                listViews.add(tvBG);

                Calendar now = Calendar.getInstance();
                int hour = now.get(Calendar.HOUR_OF_DAY);
                int minute = now.get(Calendar.MINUTE);
                // 时间线的分钟
                int finalMinute = hour * 60 + minute;
                // 判断时间线的分钟是否超出日历能显示的最大值
                boolean isLarger = finalMinute > endTime * 60;
                if (!isLarger){
                    // 时间线的分钟在日历能显示的范围之内，则加入水平时间线，否则不加入
                    // 相当于时间线的高度
                    int posY = (finalMinute - startTime * 60) * heightHour / 60;
                    TextView timeHLine = new TextView(this);
                    timeHLine.setX(0);
                    timeHLine.setY(posY);
                    timeHLine.setWidth(screenWidth);
                    timeHLine.setHeight(lineWidth * 3);
                    timeHLine.setBackgroundColor(tlColor2);
                    boardFG.addView(timeHLine);
                    listViews.add(timeHLine);
                    //timeLine.addView(timeHLine);
                    /* 增加一个时间显示文本组件
                    TextView tvTime = new TextView(this);
                    tvTime.setText(TPTime.getCurrentTime());
                    tvTime.setTextColor(tlColor);
                    tvTime.setTextSize(12);
                    tvTime.setX(0);
                    if (hour > endTime - 1){ // Now 8:40 endTime 9:00  >7:00
                        // 如果当前的小时大于倒数第一小时  比如最大时间是23时，大于22即更改显示位置
                        int offY = TPDisp.sp2Px(this, 15);
                        tvTime.setY(posY - offY);
                    } else {
                        tvTime.setY(posY);
                    }
                    tvTime.setPadding(1,1,1,1);
                    //timeLine.addView(tvTime);
                    boardFG.addView(tvTime);*/
                }
                break;
            }
            flag++;
        }

        // 获取指定日期的范围的开始日期和结束日期的毫秒数
        long seTime[] = TpTime.getWeekMondays(mYear, mMonth, mDay);
        // 从数据库中获取指定时间范围内的全部课程
        ClassBoDAO classBoDAO;
        classBoDAO = ClassBoDAO.getInstance(this);
        List<ClassBO> classBOs = classBoDAO.getScope(seTime[0], seTime[1]);
        classBoDAO.close();
        for (ClassBO classBO : classBOs){
            addClassBOs(classBO);
        }
    }

    /**
     * 获取课程的详细信息
     * @param classBO 添加课程信息到课程表中 */
    private void addClassBOs(ClassBO classBO){
        String clsName = classBO.getClassEntity().getClsName();
        final String clsColor = classBO.getClassEntity().getClsColor();
        final int pColor = Color.parseColor(clsColor);
        List<ClassDetail> classDetailEntities = classBO.getDetails();

        for (ClassDetail entity : classDetailEntities){
            // 该字符串是7位由0和1组成的数字，哪一位为1则表示那一天被选中 开始的星期是周日
            String strWeek = entity.getWeek();
            for (int i = 0;i<strWeek.length();i++){
                if (strWeek.charAt(i)=='1'){
                    // 获取数据库信息(下面的计算都是以分钟为单位的)
                    int millisMinute = 60 * 1000;
                    // 设置课程的开始时间（分钟）
                    int dStartTime = entity.getStartTime();
                    dStartTime /= millisMinute;
                    // 设置课程的结束时间（分钟）
                    int dEndTime = entity.getEndTime();
                    dEndTime /= millisMinute;
                    // 其他课程信息
                    String dRoom = entity.getRoom();
                    String teacher = entity.getTeacher();
                    // 构建用于显示的字符串
                    String info = clsName + "\n" +
                            TpTime.getFormatTime(dStartTime * millisMinute) +
                            "-" +
                            TpTime.getFormatTime(dEndTime * millisMinute) +
                            "\n" + teacher + "\n" + dRoom;
                    // x:X坐标 y:Y坐标 width:宽度 height:高度
                    // 计算X坐标：
                    int x, y, width, height = 0;
                    if (i == 0){
                        x = 6 * widthWeek;
                    } else {
                        x = (i-1) * widthWeek;
                    }
                    // 计算Y坐标：（用比计算）
                    // 使课程的开始显示时间（分钟） >= 时间表显示的最小值（分钟）
                    // 如果课程信息的开始时间小于课程表的开始时间，那么将课程信息的开始坐标
                    // 设置为0，这时注意设置开始的位置为0之后，课程信息的高度也发生了变化
                    // 所以，这里定义一个变量lowerY用于标识课程信息的开始时间是否低于课程表的
                    // 最小时间. 如果课程的最小时间小于课程表的最小时间，那么有课程高度的计算
                    // 公式为：高度 = 课程高度 - (课程表最小时间高度 - 课程最小时间高度)
                    int tempY = dStartTime - this.startTime * 60;
                    boolean lower = tempY < 0;
                    tempY = tempY > 0 ? tempY : 0;
                    y = heightHour * tempY / 60;
                    // 计算宽度WIDTH：
                    width = widthWeek - lineWidth;
                    // 计算高度HEIGHT：
                    // 应当使：课程的结束时间 <= 课程表的最大时间
                    // 注意这里的endTime要加1，是因为比如endTime的最大值为23时，实际课程表
                    // 最大高度为24*heightHour
                    if (dEndTime > (this.endTime + 1) * 60){
                        //课程的高度高出课程表的高度
                        //设置课程的高度为课程表的高度-课程开始时间的高度
                        height = heightHour * ((this.endTime + 1) * 60 - dStartTime) / 60;
                        //课程信息的底部的坐标超出课程的界限
                        if (lower){
                            //上面获取的高度值减去底部的高度
                            height -= (this.startTime * 60 - dStartTime) * heightHour / 60;
                        }
                    } else {
                        height = heightHour * (dEndTime - dStartTime) / 60;
                        if (lower){
                            height -= (this.startTime * 60 - dStartTime) * heightHour / 60;
                        }
                    }
                    //设置课程信息显示组件
                    //Animation anim = AnimationUtils.loadAnimation(this, R.anim.class_in);
                    TextView tvClsDetail = getDetailTextView(
                            x, y, pColor, width, height, info, classBO);
                    boardFG.addView(tvClsDetail);
                    listViews.add(tvClsDetail);
                    //anim.setDuration(duration);
                    //if (duration <= 1000){
                    //    duration += 50;
                    //}
                    //tvClsDetail.startAnimation(anim);
                }
            }
        }
    }

    /**
     * 获取一个用于显示课程信息的TextView组件
     * @param xPos x坐标位置
     * @param yPos y坐标位置
     * @param pColor 颜色
     * @param width 宽度
     * @param height 高度
     * @param info 组件内的信息
     * @param classBO 课程
     * @return 设置完毕的组件 */
    private TextView getDetailTextView(int xPos, int yPos, final int pColor, int width, int height, String info, final ClassBO classBO){
        TextView tvClsDetail = new TextView(this);
        tvClsDetail.setBackgroundColor(pColor);
        tvClsDetail.setX(xPos);
        tvClsDetail.setY(yPos);
        tvClsDetail.setWidth(width);
        tvClsDetail.setHeight(height);
        tvClsDetail.setTextSize(12);
        tvClsDetail.setText(info);
        tvClsDetail.setPadding(1, 1, 1, 1);
        tvClsDetail.setGravity(Gravity.CENTER);
        tvClsDetail.setTextColor(Color.parseColor("#FFFFFF"));
        // 添加监听事件
        tvClsDetail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        v.setBackgroundColor(Color.argb(200, Color.red(pColor), Color.green(pColor), Color.blue(pColor)));
                        break;
                    case MotionEvent.ACTION_UP:
                        ClassViewActivity.view(ClassActivity.this, classBO);
                    case MotionEvent.ACTION_MOVE:
                        v.setBackgroundColor(pColor);
                        break;
                }
                return true;
            }
        });
        return tvClsDetail;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:
                ClassEditActivity.edit(this, null);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.class_dashboard, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int millisOfDay = 24 * 60 * 60 * 1000;
        switch (item.getItemId()){
            case R.id.class_back:
                //返回本周：使用当前日期
                Calendar now = Calendar.getInstance();
                iYear = now.get(Calendar.YEAR);
                iMonth = now.get(Calendar.MONTH);
                iDay = now.get(Calendar.DAY_OF_MONTH);
                initData(iYear, iMonth, iDay);
                setSubTitle(iYear, iMonth, iDay);
                break;
            case R.id.class_above:
                // 获取指定日期的毫秒数
                Calendar time = Calendar.getInstance();
                time.set(iYear, iMonth, iDay);
                long tm1 = time.getTimeInMillis() - millisOfDay * 7;
                time.setTimeInMillis(tm1);
                iYear = time.get(Calendar.YEAR);
                iMonth = time.get(Calendar.MONTH);
                iDay = time.get(Calendar.DAY_OF_MONTH);
                initData(iYear, iMonth, iDay);
                setSubTitle(iYear, iMonth, iDay);
                break;
            case R.id.class_next:
                //获取指定日期的毫秒数
                Calendar time2 = Calendar.getInstance();
                time2.set(iYear, iMonth, iDay);
                long tm2 = time2.getTimeInMillis() + millisOfDay * 7;
                time2.setTimeInMillis(tm2);
                iYear = time2.get(Calendar.YEAR);
                iMonth = time2.get(Calendar.MONTH);
                iDay = time2.get(Calendar.DAY_OF_MONTH);
                initData(iYear, iMonth, iDay);
                setSubTitle(iYear, iMonth, iDay);
                break;
            case R.id.class_all:
                ClassListActivity.start(this);
                break;
            case R.id.class_share:
                // 分享课程
                final EdtDialog dialog = new EdtDialog(
                        this,
                        EdtDialog.DIALOG_TYPE_NORMAL,
                        "",
                        getString(R.string.share_item_dlg_title),
                        getString(R.string.share_item_dlg_hint));
                dialog.setNegativeListener(R.string.com_cancel, null);
                dialog.setPositiveButton(R.string.com_share, new EdtDialog.PositiveButtonClickListener() {
                    @Override
                    public void onClick(String content) {
                        dialog.dismiss();
                        TpShare.shareScreen(ClassActivity.this, content);
                    }
                });
                dialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        duration = 400;
        initData(iYear, iMonth, iDay);
        setSubTitle(iYear, iMonth, iDay);
    }
}
