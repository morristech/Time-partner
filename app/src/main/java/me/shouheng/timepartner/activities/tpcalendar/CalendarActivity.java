package me.shouheng.timepartner.activities.tpcalendar;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;

import me.shouheng.timepartner.R;
import me.shouheng.timepartner.fragments.calendar.CldFragMonth;
import me.shouheng.timepartner.fragments.calendar.CldFragWeek;
import me.shouheng.timepartner.utils.TpLunar;
import me.shouheng.timepartner.activities.base.BaseActivity;
import me.shouheng.timepartner.utils.TpTime;

public class CalendarActivity extends BaseActivity implements View.OnClickListener{
    private TextView tvYear, tvMonth;
    private int sYear, sMonth, sDay;
    private boolean isShowingMonth = true;
    // 保存状态参数的键
    private String showingTypeKey = "IsMonthCalendar";
    private String yearKey = "KeyYear";
    private String monthKey = "KeyMonth";
    private String dayKey = "KeyDay";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_layout);

        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.footer).setOnClickListener(this);
        tvYear = (TextView) findViewById(R.id.year);
        tvMonth = (TextView) findViewById(R.id.month);

        if (savedInstanceState == null){
            Calendar now = Calendar.getInstance();
            sYear = now.get(Calendar.YEAR);
            sMonth = now.get(Calendar.MONTH) + 1;
            sDay = now.get(Calendar.DAY_OF_MONTH);
            setMonthCld(sYear, sMonth, sDay);
        } else{
            // 使用保存的日期 处理屏幕方向翻转
            sYear = savedInstanceState.getInt(yearKey);
            sMonth = savedInstanceState.getInt(monthKey);
            sDay = savedInstanceState.getInt(dayKey);
            isShowingMonth = savedInstanceState.getBoolean(showingTypeKey);
            // 初始化工具栏
            if (isShowingMonth){
                setMonthCld(sYear, sMonth, sDay);
            } else {
                setWeekCld(sYear, sMonth, sDay);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                onBack();
                break;
            case R.id.footer:
                onClickFooter(v);
                break;
        }
    }

    private void onClickFooter(View v){
        PopupMenu popupM = new PopupMenu(this, v);
        // 根据正在显示的日历的类型弹出不同的菜单
        if (isShowingMonth){
            // 如果是按月显示的日历
            popupM.inflate(R.menu.cld_month);
            popupM.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.this_month:
                            Calendar now = Calendar.getInstance();
                            sYear = now.get(Calendar.YEAR);
                            sMonth = now.get(Calendar.MONTH) + 1;
                            sDay = now.get(Calendar.DAY_OF_MONTH);
                            setMonthCld(sYear, sMonth, sDay);
                            break;
                        case R.id.last_month:
                            sMonth--;
                            if (sMonth == 0){
                                sYear--;
                                sMonth = 12;
                            }
                            setMonthCld(sYear, sMonth, sDay);
                            break;
                        case R.id.next_month:
                            sMonth++;
                            if (sMonth == 13){
                                sYear++;
                                sMonth = 1;
                            }
                            setMonthCld(sYear, sMonth, sDay);
                            break;
                        case R.id.show_as_week:
                            isShowingMonth = false;
                            setWeekCld(sYear, sMonth, sDay);
                            break;
                    }
                    return true;
                }
            });
        } else {
            popupM.inflate(R.menu.cld_week);
            popupM.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.this_week:
                            Calendar now = Calendar.getInstance();
                            sYear = now.get(Calendar.YEAR);
                            sMonth = now.get(Calendar.MONTH) + 1;
                            sDay = now.get(Calendar.DAY_OF_MONTH);
                            setWeekCld(sYear, sMonth, sDay);
                            break;
                        case R.id.last_week:
                            //日的值减7
                            sDay -= 7;
                            if (sDay<1){
                                //说明到了上个月
                                sMonth--;
                                if (sMonth == 0){
                                    //说明到了上一年
                                    sMonth = 12;
                                    sYear --;
                                }
                                int daysOfLastMonth = TpTime.getDaysOfMonth(sYear, sMonth);
                                sDay = daysOfLastMonth + sDay;
                            }
                            //设置日历信息
                            setWeekCld(sYear, sMonth, sDay);
                            break;
                        case R.id.next_week:
                            //当前月份的总天数
                            int daysOfThisMonth = TpTime.getDaysOfMonth(sYear, sMonth);
                            //日期值+7
                            sDay += 7;
                            if (sDay > daysOfThisMonth){
                                //说明到了下个月
                                sMonth ++;
                                if (sMonth == 13){
                                    sMonth = 1;
                                    sYear++;
                                }
                                sDay = sDay - daysOfThisMonth;
                            }
                            //设置日历信息
                            setWeekCld(sYear, sMonth, sDay);
                            break;
                        case R.id.show_as_month:
                            isShowingMonth = true;
                            setMonthCld(sYear, sMonth, sDay);
                            break;
                    }
                    return true;
                }
            });
        }
        popupM.show();
    }

    private void initMonthToolbar(int iYear, int iMonth){
        String strYear =  iYear + " " + TpLunar.getGanZhi(iYear) + " " + TpLunar.getShuXiang(iYear);
        tvYear.setText(strYear);
        String strMonth = iMonth + "";
        tvMonth.setText(strMonth);
    }

    private void initWeekToolbar(int iYear, int iMonth, int iDay){
        String[] weeks = TpTime.getWeekStringsSunday(iYear, iMonth - 1, iDay);
        String strMonth = weeks[0] +"-"+ weeks[6];
        tvMonth.setText(strMonth);
        String strYear =  iYear + " " + TpLunar.getGanZhi(iYear) + " " + TpLunar.getShuXiang(iYear);
        tvYear.setText(strYear);
    }

    private void setMonthCld(int iYear, int iMonth, int iDay){
        isShowingMonth = true;

        CldFragMonth cldFragMonth = new CldFragMonth(iYear, iMonth, iDay);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.layout, cldFragMonth).commit();

        initMonthToolbar(iYear, iMonth);
    }

    private void setWeekCld(int iYear, int iMonth, int iDay){
        isShowingMonth = false;

        CldFragWeek cldFragWeek = new CldFragWeek(iYear, iMonth, iDay);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.layout, cldFragWeek).commit();

        initWeekToolbar(iYear, iMonth, iDay);
    }

    @Override
    public void onBackPressed() {
        onBack();
    }

    private void onBack(){
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putBoolean(showingTypeKey, isShowingMonth);
        //outState.putInt(yearKey, sYear);
        //outState.putInt(monthKey, sMonth);
        //outState.putInt(dayKey, sDay);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (isShowingMonth){
            setMonthCld(sYear, sMonth, sDay);
        } else {
            setWeekCld(sYear, sMonth, sDay);
        }
    }
}
