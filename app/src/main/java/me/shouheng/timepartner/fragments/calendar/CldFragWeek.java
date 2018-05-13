package me.shouheng.timepartner.fragments.calendar;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

import me.shouheng.timepartner.database.dao.loader.WeekDetailsLoader;
import me.shouheng.timepartner.models.business.assignment.AssignmentBO;
import me.shouheng.timepartner.models.business.tpclass.ClassDetailBO;
import me.shouheng.timepartner.models.business.exam.ExamBO;
import me.shouheng.timepartner.models.business.note.NoteBO;
import me.shouheng.timepartner.models.business.task.TaskBO;
import me.shouheng.timepartner.models.entities.assignment.Assignment;
import me.shouheng.timepartner.models.entities.tpclass.ClassDetail;
import me.shouheng.timepartner.models.entities.tpclass.ClassEntity;
import me.shouheng.timepartner.models.entities.collection.CollectionEntity;
import me.shouheng.timepartner.models.entities.exam.Exam;
import me.shouheng.timepartner.models.entities.note.Note;
import me.shouheng.timepartner.models.entities.task.Task;
import me.shouheng.timepartner.R;
import me.shouheng.timepartner.activities.assignment.AssignViewActivity;
import me.shouheng.timepartner.activities.tpclass.ClassViewActivity;
import me.shouheng.timepartner.activities.note.NoteViewer;
import me.shouheng.timepartner.activities.task.TaskViewer;
import me.shouheng.timepartner.utils.TpColor;
import me.shouheng.timepartner.utils.TpDisp;
import me.shouheng.timepartner.utils.TpPrefer;
import me.shouheng.timepartner.utils.TpTime;

public class CldFragWeek extends Fragment {
    private Context mContext;

    private RelativeLayout weekDetail;

    private WeekDetailsLoader detailsLoader;

    private int mYear, mMonth, mDay;

    private int lineWidth;
    private int heightOfHour;
    private int widthOfDay;

    private int textSize = 12;

    private int screenWidth;
    private int screenHeight;

    private int bgColor;  // 背景颜色
    private int timeLineColor ;  // 时间线的颜色

    private int dailyStartTime = 0;  // 每天的开始时间
    private int dailyEndTime = 23;  // 每天的结束时间
    private int dailyHours = 24;  // 每日的时间长度

    private long millisOfWeekStart;
    private long millisOfWeekEnd;

    private View mView;

    public CldFragWeek(int mYear, int mMonth, int mDay){
        // 1 -> Jan
        this.mYear = mYear;
        this.mMonth = mMonth;
        this.mDay = mDay;
        long[] millis = TpTime.getWeekSundays(mYear, mMonth - 1, mDay);
        millisOfWeekStart = millis[0];
        millisOfWeekEnd = millis[1];
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initInfo();

        mView = inflater.inflate(R.layout.calendar_frag_week, container, false);
        weekDetail = (RelativeLayout) mView.findViewById(R.id.week_detail_panel);

        detailsLoader = WeekDetailsLoader.getInstance(mContext);

        setDateBar();

        initBoard();

        //setTimeLine();// 时间线

        addDetails();

        return mView;
    }

    private void initBoard(){
        // 星期栏
        String[] strWeeks = getActivity().getResources().getStringArray(R.array.week_strings);
        int wFlag = 0;
        LinearLayout weekBar = (LinearLayout) mView.findViewById(R.id.week_bar);
        for (String str : strWeeks){
            TextView tvWeek = new TextView(mContext);
            tvWeek.setText(str);
            tvWeek.setWidth(widthOfDay);
            tvWeek.setTextSize(textSize);
            weekBar.addView(tvWeek);
            if (wFlag%6 == 0) tvWeek.setTextColor(Color.RED);
            wFlag++;
        }

        // 时间栏
        LinearLayout timeBar = (LinearLayout) mView.findViewById(R.id.time_bar);
        for (int time = dailyStartTime; time<= dailyEndTime; time++){
            TextView tvTime = new TextView(mContext);
            tvTime.setText(String.valueOf(time));
            tvTime.setHeight(heightOfHour);
            tvTime.setGravity(Gravity.CENTER);
            tvTime.setTextSize(textSize);
            timeBar.addView(tvTime);
        }

        // 竖直线
        for (int i=1;i<7;i++){
            TextView tvVLine = new TextView(mContext);
            tvVLine.setWidth(lineWidth);
            tvVLine.setHeight(dailyHours * heightOfHour);
            tvVLine.setX(i*widthOfDay);
            tvVLine.setY(0);
            tvVLine.setBackgroundColor(bgColor);
            weekDetail.addView(tvVLine);
        }

        // 水平线
        int flag = 0;
        for (int i=dailyStartTime; i<=dailyEndTime; i++){
            TextView tvHLine = new TextView(mContext);
            tvHLine.setHeight(lineWidth);
            tvHLine.setWidth(screenWidth);
            tvHLine.setX(0);
            tvHLine.setY((++flag) * heightOfHour);
            tvHLine.setBackgroundColor(bgColor);
            weekDetail.addView(tvHLine);
        }

        // 时间线
        // 设置背景时间线
        long[] millis = TpTime.getWeekSundays(mYear, mMonth - 1, mDay);
        long millisToday = TpTime.millisOfCurrentDate();
        int tlFlag = 0;
        for (long date = millis[0];date<=millis[1];date+= TpTime.DAY_MILLIS){
            if (date == millisToday){
                int tlColor = Color.parseColor("#20FF3D00");
                int tlColor2 = Color.parseColor("#AAFF3D00");

                TextView tvBG = new TextView(mContext);
                tvBG.setX(widthOfDay * tlFlag + 2 * lineWidth);
                tvBG.setY(0);
                tvBG.setWidth(widthOfDay - 2 * lineWidth);
                tvBG.setHeight(heightOfHour * (dailyEndTime + 1 - dailyStartTime));
                tvBG.setBackgroundColor(tlColor);
                weekDetail.addView(tvBG);

                Calendar now = Calendar.getInstance();
                int hour = now.get(Calendar.HOUR_OF_DAY);
                int minute = now.get(Calendar.MINUTE);
                // 时间线的分钟
                int finalMinute = hour * 60 + minute;
                // 判断时间线的分钟是否超出日历能显示的最大值
                boolean isLarger = finalMinute > dailyEndTime * 60;
                if (!isLarger){
                    // 时间线的分钟在日历能显示的范围之内，则加入水平时间线，否则不加入
                    // 相当于时间线的高度
                    int posY = (finalMinute - dailyStartTime * 60) * heightOfHour / 60;
                    TextView timeHLine = new TextView(mContext);
                    timeHLine.setX(0);
                    timeHLine.setY(posY);
                    timeHLine.setWidth(screenWidth);
                    timeHLine.setHeight(lineWidth * 3);
                    timeHLine.setBackgroundColor(tlColor2);
                    weekDetail.addView(timeHLine);
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
            tlFlag++;
        }
    }

    private void initInfo(){
        mContext = getActivity();
        timeLineColor = getResources().getColor(R.color.calendar_prime);

        // 设置每天的开始时间和结束时间
        dailyStartTime = TpPrefer.getInstance(mContext).getCldStartTime();
        dailyEndTime = TpPrefer.getInstance(mContext).getCldEndTime();
        dailyHours = dailyEndTime + 1 - dailyStartTime;

        // 日历的背景颜色
        bgColor = Color.parseColor("#E0E0E0");

        // 这里获取的是整个屏幕的尺寸
        Point point = TpDisp.getWindowSize(getActivity());
        screenWidth = point.x;
        screenHeight = point.y;

        // 线的宽度为0.5dp
        lineWidth = TpDisp.dp2Px(mContext, 0.5f);

        // 一个小时的高度
        heightOfHour = screenHeight / TpPrefer.getInstance(mContext).getCldHourHeight();

        // 每天的宽度
        widthOfDay = screenWidth / 7;
    }

    private void setDateBar(){
        // 用于填充日期栏
        LinearLayout dateBar = (LinearLayout) mView.findViewById(R.id.date_bar);
        TextView tvPadWeekBar = new TextView(mContext);
        tvPadWeekBar.setText("00");
        tvPadWeekBar.setTextSize(12);
        tvPadWeekBar.setTextColor(Color.TRANSPARENT);
        dateBar.addView(tvPadWeekBar);
        // 设置日期栏
        String strTimes[] = TpTime.getWeekStringsSunday(mYear, mMonth - 1, mDay);
        for (String str : strTimes){
            TextView tvTime = new TextView(mContext);
            tvTime.setText(str);
            tvTime.setTextSize(12);
            tvTime.setTextColor(timeLineColor);
            tvTime.setWidth(screenWidth / 7);
            dateBar.addView(tvTime);
        }
    }

    private void addDetails(){
        List detailsList = detailsLoader.loadGivenDate(mYear, mMonth, mDay);
        for (int i=0;i<detailsList.size();i++){
            Object obj = detailsList.get(i);
            if (obj instanceof ClassDetailBO){
                addClasses((ClassDetailBO) obj);
            } else if (obj instanceof AssignmentBO){
                addAssigns((AssignmentBO) obj);
            } else if (obj instanceof TaskBO){
                addTasks((TaskBO) obj);
            } else if (obj instanceof ExamBO){
                addExams((ExamBO) obj);
            } else if (obj instanceof NoteBO){
                addNotes((NoteBO) obj);
            }
        }
    }

    private void addClasses(ClassDetailBO classDetailBO){
        final ClassEntity classEntity = classDetailBO.getClassEntity();
        ClassDetail detailEntity = classDetailBO.getDetailEntity();

        String clsName = classEntity.getClsName();
        String clsColor = classEntity.getClsColor();
        String strWeek = detailEntity.getWeek();
        for (int i=0;i<strWeek.length();i++){
            if (strWeek.charAt(i) == '1'){
                // 获取数据库信息(下面的计算都是以分钟为单位的)
                int millisMinute = 60 * 1000;
                // 获取课程的起止日期
                int clsStartTime = detailEntity.getStartTime();
                int clsEndTime = detailEntity.getEndTime();
                clsStartTime /= millisMinute;
                clsEndTime /= millisMinute;
                // 其他课程信息
                String strRoom = detailEntity.getRoom();
                String strTeacher = detailEntity.getTeacher();
                // 构建用于显示的字符串
                String info = clsName + "\n" +
                        TpTime.getFormatTime(detailEntity.getStartTime()) + "--" +
                        TpTime.getFormatTime(detailEntity.getEndTime()) + "\n"
                        + strTeacher + "\n" + strRoom;
                // x:X坐标 y:Y坐标 width:宽度 height:高度
                // 计算X坐标：
                int x, y, width, height = 0;
                x = i * widthOfDay + lineWidth;
                // 计算Y坐标：（用比计算）
                int tempY = clsStartTime - this.dailyStartTime * 60;
                boolean lower = tempY < 0;
                tempY = tempY > 0 ? tempY : 0;
                y = heightOfHour * tempY / 60;
                // 计算宽度WIDTH：
                width = widthOfDay - lineWidth;
                if (clsEndTime > (this.dailyEndTime + 1) * 60){
                    // 课程的高度高出课程表的高度
                    // 设置课程的高度为课程表的高度-课程开始时间的高度
                    height = heightOfHour * ((this.dailyEndTime + 1) * 60 - clsStartTime) / 60;
                    // 课程信息的底部的坐标超出课程的界限
                    if (lower){
                        // 上面获取的高度值减去底部的高度
                        height -= (this.dailyStartTime * 60 - clsStartTime) * heightOfHour / 60;
                    }
                } else {
                    height = heightOfHour * (clsEndTime - clsStartTime) / 60;
                    if (lower){
                        height -= (this.dailyStartTime * 60 - clsStartTime) * heightOfHour / 60;
                    }
                }
                // 设置课程信息显示组件
                final int pColor = Color.parseColor(clsColor);
                TextView tvClsDetail = new TextView(mContext);
                tvClsDetail.setBackgroundColor(pColor);
                tvClsDetail.setX(x);
                tvClsDetail.setY(y);
                tvClsDetail.setWidth(width);
                tvClsDetail.setHeight(height);
                tvClsDetail.setTextSize(textSize);
                tvClsDetail.setText(info);
                tvClsDetail.setPadding(1, 1, 1, 1);
                tvClsDetail.setGravity(Gravity.CENTER);
                tvClsDetail.setTextColor(Color.WHITE);
                weekDetail.addView(tvClsDetail);

                tvClsDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ClassViewActivity.view(mContext, classEntity);
                    }
                });
            }
        }
    }

    private void addAssigns(AssignmentBO assignBO){
        Assignment assignEntity = assignBO.getAssignEntity();

        String asnColor = assignEntity.getAsnColor();
        String asnName = assignEntity.getAsnTitle();
        long asnDate = assignEntity.getAsnDate();
        int asnTime = assignEntity.getAsnTime();
        int pColor = Color.parseColor(asnColor);
        asnName = getString(R.string.assign_title) + ":\n" + asnName;

        addItem(asnDate, asnTime, pColor, asnName, assignBO);
    }

    private void addTasks(TaskBO taskBO){
        Task taskEntity = taskBO.getTaskEntity();
        ClassEntity classEntity = taskBO.getClassEntity();

        String taskName = taskEntity.getTaskTitle();
        taskName = getString(R.string.task) + ":\n" + taskName;
        long taskDate = taskEntity.getTaskDate();
        int taskTime = taskEntity.getTaskTime();
        String taskColor = TpColor.COLOR_TASK;
        if (classEntity != null) taskColor = classEntity.getClsColor();
        int pColor = Color.parseColor(taskColor);

        addItem(taskDate, taskTime, pColor, taskName, taskBO);
    }

    private void addExams(ExamBO examBO) {
        Exam examEntity = examBO.getExamEntity();
        ClassEntity classEntity = examBO.getClassEntity();

        String examName = examEntity.getExamTitle();
        examName = getString(R.string.exam) + ":\n" + examName;
        long examDate = examEntity.getExamDate();
        int examTime = examEntity.getExamTime();
        String examColor = TpColor.COLOR_EXAM;
        if (classEntity != null)
            examColor = classEntity.getClsColor();
        int pColor = Color.parseColor(examColor);

        addItem(examDate, examTime, pColor, examName, examBO);
    }

    private void addNotes(NoteBO noteBO){
        Note noteEntity = noteBO.getNote();
        CollectionEntity collectionEntity = noteBO.getCollection();

        String noteName = noteEntity.getNoteTitle();
        noteName = getString(R.string.n_title) + ":\n" +  noteName;
        long noteNDate = noteEntity.getNoteDate();
        int noteNTime = noteEntity.getNoteTime();
        String noteColor = TpColor.COLOR_NOTE;
        if (collectionEntity != null)
            noteColor = collectionEntity.getClnColor();
        int pColor = Color.parseColor(noteColor);

        addItem(noteNDate, noteNTime, pColor, noteName, noteBO);
    }

    private <T>void addItem(final long itemDate, int itemTime, final int pColor, String itemName, final T t){
        // 解析用于显示的字符串
        String strDate = TpTime.getDate(itemDate, TpTime.DATE_TYPE_2);
        String strTime = TpTime.getTime(itemTime);
        itemName = itemName + "\n" + strDate + "\n" + strTime;
        // 一分钟的毫秒数 下面的计算以分钟为单位
        int millisMinute = 60 * 1000;
        // 一天的毫秒数
        int millisOfDay = 86400000;//24 * 60 * 60 * 1000;
        // 计算日程的时间：错误居然存在这里！！即没有把时间转换成分钟
        itemTime /= millisMinute;
        int week = (int)((itemDate - millisOfWeekStart) / millisOfDay);
        // x:X坐标 y:Y坐标 width:宽度 height:高度
        // 计算X坐标：
        int x, y, width;
        x = week * widthOfDay + lineWidth;
        // 计算Y坐标：（用比计算）
        int tempY = itemTime - dailyStartTime * 60;
        boolean lower = tempY < 0; // 项目的开始时间小于每天的开始时间
        tempY = lower ? 0 : tempY;
        y = heightOfHour * tempY / 60;
        // 计算宽度WIDTH：
        width = widthOfDay - lineWidth;
        // 设置日程信息显示组件
        TextView tvNote = new TextView(mContext);
        tvNote.setBackgroundColor(pColor);
        tvNote.setX(x);
        tvNote.setY(y);
        tvNote.setWidth(width);
        tvNote.setTextSize(textSize);
        tvNote.setText(itemName);
        tvNote.setPadding(1, 1, 1, 1);
        tvNote.setGravity(Gravity.CENTER);
        tvNote.setTextColor(Color.WHITE);
        // 当时间不小于日历的最小显示时间时才显示该组件
        if (!lower && itemTime < (dailyEndTime + 1) * 60)
            weekDetail.addView(tvNote);
        // 设置监听事件
        tvNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (t instanceof AssignmentBO){
                    AssignViewActivity.view(mContext, (AssignmentBO) t);
                } else if (t instanceof TaskBO) {
                    Task taskEntity = ((TaskBO) t).getTaskEntity();
                    TaskViewer.view(mContext, taskEntity);
                } else if (t instanceof ExamBO) {
                    Exam examEntity = ((ExamBO) t).getExamEntity();
                    TaskViewer.view(mContext, examEntity);
                } else if (t instanceof NoteBO) {
                    Note noteEntity = ((NoteBO) t).getNote();
                    NoteViewer.view(mContext, noteEntity.getNoteId());
                }
            }
        });
    }
}
