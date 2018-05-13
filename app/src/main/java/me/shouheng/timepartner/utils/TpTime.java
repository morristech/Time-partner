package me.shouheng.timepartner.utils;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TpTime {
    private static int daysOfMonth[] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    //公历节日的编号：月份*100+节日
    //1-01 2-14 3-15 4-01 5-01 5-04 6-01 7-01 8-01 9-10 10-01 11-11 12-25
    private static int festIds[][] = new int[][]{
            {101}, {214}, {315}, {401},
            {501, 504}, {601}, {701}, {801},
            {910}, {1001}, {1111}, {1224, 1225}};
    private static String festStrs[][] = new String [][]{
            {"元旦"}, {"情人节"}, {"消费者日"}, {"愚人节"},
            {"劳动节", "青年节"}, {"儿童节"}, {"建党节"}, {"建军节"},
            {"教师节"}, {"国庆节"}, {"光棍节"}, {"平安夜", "圣诞节"}};
    private final static long millisOff = 16 * 60 * 60 * 1000; // 偏移的毫秒数 使时间处于00:00
    public final static long MIN_MILLIS = 60000, HOUR_MILLIS = 3600000, DAY_MILLIS = 86400000;
    public final static int DATE_TYPE_1 = 1;
    public final static int DATE_TYPE_2 = 2;
    public final static int DATE_TYPE_3 = 3;
    public final static int DATE_TYPE_4 = 4;
    public final static int DATE_TYPE_5 = 5;
    public final static int EXACT_TIME_TYPE_1 = 1;
    public final static int EXACT_TIME_TYPE_2 = 2;
    public final static int EXACT_TIME_TYPE_3 = 3;
    public final static int EXACT_TIME_TYPE_4 = 4;
    private final static String timeFormat = "hh:mma";



    public static int getShortId(){
        // int型id 精确到毫秒
        Calendar now = Calendar.getInstance();
        return now.get(Calendar.HOUR_OF_DAY) * 3600000 +
                now.get(Calendar.MINUTE) * 60000 +
                now.get(Calendar.SECOND) * 1000 +
                now.get(Calendar.MILLISECOND);
    }

    public static long getLongId(){
        // long型id 精确到毫秒
        return System.currentTimeMillis();
    }



    /**
     * 获取农历和节日信息，根据节日和农历的优先级进行设置
     * @param mYear 年份
     * @param mMonth 月份 1即1月
     * @param mDay 日
     * @return 节日或者农历字符串 */
    public static String getStringDate(Context context, int mYear, int mMonth, int mDay) {
        String strDate = "";
        // 添加农历日期和节日（优先级 节日>日期）
        if (TpPrefer.getInstance(context).getShowLunar()){
            TpLunar tpLunar = TpLunar.getInstance(mYear, mMonth, mDay);
            strDate = tpLunar.getDate();
        }
        // 添加节气
        // 添加公历节日
        int dateId = mMonth * 100 + mDay;
        int month = mMonth - 1;
        for (int i=0;i<festIds[month].length;i++){
            if (dateId == festIds[month][i]){
                strDate = festStrs[month][i];
            }
        }
        return strDate;
    }

    public static int getDaysOfMonth(int year, int month){
        final int MONTHS_YEAR = 12;
        if ((month<1) || (month>MONTHS_YEAR)){
            return 0;
        }
        int days = daysOfMonth[month-1];
        if ((month == 2) && isLeapYear(year)){
            days++;
        }
        return days;
    }

    public static int zellerWeek(int year, int month, int day){
        // 计算给定日期的星期值 结果0表示星期日
        int m = month;
        int d = day;
        if (month<=2){
            year--;
            m = month + 12;
        }
        int y = year % 100;
        int c = year / 100;
        int w = (y + y/4 + c/4 - 2*c + (13*(m+1)/5) + d -1)%7;
        if (w<0) w += 7;
        return w;
    }

    public static boolean isLeapYear(int year){
        return ((year%4 == 0) && (year%100 != 0))||(year%400 == 0);
    }



    public static long millisOfCurrentDate(){
        Calendar now = Calendar.getInstance();
        now.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        return now.getTimeInMillis() / 1000 * 1000;
    }

    public static int millisOfCurrentTime(){
        Calendar now = Calendar.getInstance();
        return (now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)) * 60 * 1000;
    }

    public static long getMillisFromDate(int year, int month, int day){
        // month=0表示1月
        Calendar time = Calendar.getInstance();
        time.set(year, month, day, 0, 0, 0);
        return time.getTimeInMillis() / 1000 * 1000;
    }

    public static long getMillisFromDate(String strDate){
        int arrDate[] = getDateArray(strDate);
        return TpTime.getMillisFromDate(arrDate[0], arrDate[1] - 1, arrDate[2]);
    }

    public static int getMillisFromTime(int hour, int minute){
        return ((hour * 60 + minute) * 60) * 1000;
    }

    public static int getMillisFromTime(String time){
        int arrTime[] = getTimeArray(time, 2);
        return TpTime.getMillisFromTime(arrTime[0], arrTime[1]);
    }



    public static int weekOfDay(long millisOfDay){
        // 0->Sunday 1->Monday
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millisOfDay);
        return TpTime.zellerWeek(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
    }

    public static int weekOfToday(){
        // 0->Sunday 1->Monday
        Calendar now = Calendar.getInstance();
        return now.get(Calendar.DAY_OF_WEEK) - 1;
    }



    public static String getCurrentTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mma");
        Date date = new Date(System.currentTimeMillis());
        return sdf.format(date);
    }

    public static String getFormatTime(int iTime){
        // 获取格式化的时间
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        long millis = millisOff + (long) iTime;
        Date date = new Date(millis);
        return sdf.format(date);
    }

    public static String getTime(int iTime){
        // "hh:mm a" 03:40下午
        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);
        long millis = millisOff + (long) iTime;
        Date date = new Date(millis);
        return sdf.format(date);
    }

    public static String getExactTime(long millis, int type){
        String format = "MMM dd  E  yyyy  hh:mma"; // 2016 11月10 星期四 07:40下午
        switch (type){
            case EXACT_TIME_TYPE_1:
                break;
            case EXACT_TIME_TYPE_2:
                format = "MMM dd  hh:mma"; // 11月 10, 09:00下午
                break;
            case EXACT_TIME_TYPE_3:
                format = "MMM dd  yyyy  hh:mm"; // 2016 11月10 23:11
                break;
            case EXACT_TIME_TYPE_4:
                format = "MMM dd  yyyy  hh:mma"; // 2016 11月10 23:11下午
                break;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date d = new Date(millis);
        return sdf.format(d);
    }



    public static String[] getDashDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("MMMdd-E-yyyy");
        Date date = new Date(System.currentTimeMillis());
        String str =  sdf.format(date);
        return str.split("-");
    }

    public static String getFormatDate(long lDate){
        // 获取格式化的日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(lDate);
        return sdf.format(date);
    }

    public static long getDate(long lDate){
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(lDate);
        now.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), 1, 0, 0, 0);
        return now.getTimeInMillis();
    }

    public static String getDate(long lDate, int type){
        String format = "MMM dd  yyyy";// 11月 08, 2016
        switch (type){
            case DATE_TYPE_1:
                break;
            case DATE_TYPE_2:
                format = "MMM dd"; // 11月 08
                break;
            case DATE_TYPE_3:
                format = "yyyy MMM dd"; // 2016 11月 08
                break;
            case DATE_TYPE_4:
                format = "MMM-dd"; // 11月-08
                break;
            case DATE_TYPE_5:
                format = "yyyy MMM"; // 2016 11月
                break;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = new Date(lDate);
        return sdf.format(date);
    }

    public static String getCurrentDate(){
        return getDate(System.currentTimeMillis(), DATE_TYPE_1);
    }

    public static int[] getDateArray(String strDate){
        String[] strs = strDate.split("-");
        return new int[]{
                Integer.parseInt(strs[0]),
                Integer.parseInt(strs[1]),
                Integer.parseInt(strs[2])};
    }

    public static int[] getTimeArray(String time, int mode){
        int []timeArray = new int[3];
        String []timeStrArray = time.split(":");
        switch (mode){
            case 2:
                //从字符串获取 时、分 数据
                timeArray[0] = Integer.parseInt(timeStrArray[0]);
                timeArray[1] = Integer.parseInt(timeStrArray[1]);
                break;
            case 3:
                //从字符串获取 时、分、秒 数据
                timeArray[0] = Integer.parseInt(timeStrArray[0]);
                timeArray[1] = Integer.parseInt(timeStrArray[1]);
                timeArray[2] = Integer.parseInt(timeStrArray[2]);
                break;
        }
        return timeArray;
    }



    public static long[] getWeekMondays(int mYear, int mMonth, int mDay){
        /** 传入的0表示1月
         * 注意这里使用了Calendar来获取时间，所以只适用于Calendar的getMiliis方法允许的时间范围内
         * 获取对于课程的基于指定日期的周次（每周的开始星期是星期一，结束星期是星期日）
         * @return 返回一个数组包含该周内的所有时间
         * 注意这里的时间是比如周一的0:00和下周日的0:00之间的时间
         * 注意如果是下周日的话，注意有时候可能获取不到周日内的数据
         * 这是在使用 日期+时间 的情况下会出现这种情况，如果是将日期和时间分别存储，然后
         * 先对日期进行判断，然后再判断时间的话，就不会发生这种情况 */
        // 获取给定日期的星期
        int weekDay = zellerWeek(mYear, mMonth + 1, mDay) + 1;
        // 当前周向前和向后移动的天数，用于计算该周的开始时间和结束时间
        int forwardOff = 0, postOff = 0 ;
        if (weekDay == 1){
            //如果是星期日，则前移6天，后移0天（因为要获取下周周日的0:0:00的时间）
            forwardOff = -6;
            // postOff = 1;
        } else {
            //如果不是星期日（>=2）计算要移动的天数
            forwardOff = 2 - weekDay;
            postOff = 8 - weekDay;
        }
        // 获取指定日期的毫秒数
        long nowLong = getMillisFromDate(mYear, mMonth, mDay);
        long weekStart = nowLong + forwardOff * DAY_MILLIS;
        long weekEnd = nowLong + postOff * DAY_MILLIS;
        // 返回该周的开始日期的毫秒数和结束日期的毫秒数
        return new long[]{weekStart, weekEnd};
    }

    public static String[] getWeekStringsMonday(int mYear, int mMonth, int mDay){
        // 获取 周一 .. 周六 周日 的日期
        long[] temp = getWeekMondays(mYear, mMonth, mDay);
        String[] strs = new String[7];
        int flag = 0;
        for (long sm = temp[0];sm<=temp[1];sm+= DAY_MILLIS){
            strs[flag++] = getDate(sm, DATE_TYPE_2);
        }
        return strs;
    }

    public static long[] getWeekSundays(int mYear, int mMonth, int mDay){
        // 获取指定日期的所在的周的起止时间的毫秒  !!! 0表示1月
        int weekDay = zellerWeek(mYear, mMonth + 1, mDay) + 1;
        int forwardOff = 0, postOff = 0 ;
        if (weekDay == 1){
            //如果是星期日，则前移0天，后移1天（因为要获取下周周日的0:0:00的时间）
            forwardOff = 0;
            postOff = 6;
        } else {
            //如果不是星期日（>=2）计算要移动的天数
            forwardOff = 1 - weekDay;
            postOff = 7 - weekDay;
        }
        // 获取指定日期的毫秒数
        long nowLong = getMillisFromDate(mYear, mMonth, mDay);
        long weekStart = nowLong + forwardOff * DAY_MILLIS;
        long weekEnd = nowLong + postOff * DAY_MILLIS;
        // 返回该周的开始日期的毫秒数和结束日期的毫秒数
        return new long[]{weekStart, weekEnd};
    }

    public static String[] getWeekStringsSunday(int mYear, int mMonth, int mDay){
        // 获取 周日 周一 .. 周六 的日期
        long[] temp = getWeekSundays(mYear, mMonth, mDay);
        String[] strs = new String[7];
        int flag = 0;
        for (long sm = temp[0];sm<=temp[1];sm+= DAY_MILLIS){
            strs[flag++] = getDate(sm, DATE_TYPE_2);
        }
        return strs;
    }
}
