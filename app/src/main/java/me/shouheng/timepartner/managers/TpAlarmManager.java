package me.shouheng.timepartner.managers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import me.shouheng.timepartner.models.business.alarm.Alarm;
import me.shouheng.timepartner.utils.TpPrefer;
import me.shouheng.timepartner.utils.TpTime;

public class TpAlarmManager {
    private static final String BROAD_ACTION = "com.simple.nasty.timepartner";
    private static final String REM_FILE_NAME = "SIMPLE_NASTY_ALARM_FILE";
    public static final String KEY_TITLE = "TITLE";
    public static final String KEY_SUB_TITLE = "SUB_TITLE";
    public static final String KEY_FOOTER1 = "FOOTER1";
    public static final String KEY_FOOTER2 = "FOOTER2";
    public static final String KEY_CONTENT = "CONTENT";
    public static final String KEY_COLOR = "COLOR";
    public static final String KEY_TYPE = "TYPE";
    public static final String KEY_ITEM_ID = "ITEM_ID";
    public static final String KEY_NOTICE_TIME= "NOTICE_TIME";
    private static final String TAG = "AlarmUtils__";

    /**
     * 设置所有的闹钟处于 已全部被取消 状态
     * @param context 上下文
     */
    public static void cancelAlarmsToday(Context context){
        SharedPreferences shared = context.getSharedPreferences(REM_FILE_NAME, Context.MODE_APPEND);
        SharedPreferences.Editor editor = shared.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * 设置今天的全部闹钟
     * @param context 上下文
     */
    public static void setAlarmsToday(Context context){
        Log.d(TAG, "setAlarmsToday: ");
        if (!areAlarmsAdded(context)){
            Log.d(TAG, "setAlarmsToday: " + "OnReceiver");
            // 今天的闹钟还没有添加过
            // 单位 毫秒
            long unitMinute = 60 * 1000;
            long unitHour = 60 * 60 * 1000;
            long unitDay = 24 * 60 * 60 * 1000;
            // 定义闹钟服务
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            // 今天的毫秒 今天的闹钟的设置都是基于这个毫秒的
            long millisToday = TpTime.millisOfCurrentDate();

            // 设置每天的开始的提醒时间
            int dailyNoticeHour = TpPrefer.getInstance(context).getDailyNoticeTime();
            long dailyNoticeTime = millisToday + dailyNoticeHour * unitHour;
            Intent intent1 = new Intent(BROAD_ACTION);
            PendingIntent p1 = PendingIntent.getBroadcast(context,
                    dailyNoticeHour * 60 * 60, intent1, PendingIntent.FLAG_CANCEL_CURRENT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                am.setExact(AlarmManager.RTC_WAKEUP, dailyNoticeTime, p1);
            } else {
                am.set(AlarmManager.RTC_WAKEUP, dailyNoticeTime, p1);
            }

            // 23:11的提醒
            long soleTimeNotice = millisToday + 23 * unitHour + 11 * unitMinute;
            Intent intent2 = new Intent(BROAD_ACTION);
            PendingIntent p2 = PendingIntent.getBroadcast(context,
                    23 * 60 * 60 + 11 * 60, intent2,
                    PendingIntent.FLAG_CANCEL_CURRENT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                am.setExact(AlarmManager.RTC_WAKEUP, soleTimeNotice, p2);
            } else {
                am.set(AlarmManager.RTC_WAKEUP, soleTimeNotice, p2);
            }

            // 闹钟的更新时间提醒： 0:00时的提醒 在这个时间里添加新的一天的全部闹钟
            long updateTimeNotice = millisToday + unitDay;
            Intent intent3 = new Intent(BROAD_ACTION);
            PendingIntent p3 = PendingIntent.getBroadcast(context,
                    24 * 60 * 60 , intent3,
                    PendingIntent.FLAG_CANCEL_CURRENT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                am.setExact(AlarmManager.RTC_WAKEUP, updateTimeNotice, p3);
            } else {
                am.set(AlarmManager.RTC_WAKEUP, updateTimeNotice, p3);
            }

            // 添加每天的全部提醒
            addDailyNotice(context);

            // 设置今天的全部闹钟已经被添加过的标识
            setAlarmsAdded(context);
        }
    }

    /**
     * 为项目设置闹钟 在添加新的项目的时候调用该方法
     * @param context 上下文
     * @param alarmEntity 闹钟实体
     */
    public static void setAlarm(Context context, Alarm alarmEntity){
        if (isAlarmToday(alarmEntity)){
            // 时间和日期都满足要求
            long alarmMillis = alarmEntity.getEntityTime() + TpTime.millisOfCurrentDate();
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            // 包装 Intent 用于发送闹钟
            Intent intent = wrapIntent(alarmEntity);
            PendingIntent p = PendingIntent.getBroadcast(context,
                    alarmEntity.getEntityTime(), intent,
                    PendingIntent.FLAG_CANCEL_CURRENT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                am.setExact(AlarmManager.RTC_WAKEUP, alarmMillis, p);
            } else {
                am.set(AlarmManager.RTC_WAKEUP, alarmMillis, p);
            }
        }
    }

    /**
     * 取消指定的闹钟 ！匹配的准则是Intent
     * @param context 上下文
     */
    public static void cancelAlarm(Context context, Alarm alarmEntity){
        if (isAlarmToday(alarmEntity)){
            // 包装 Intent 用于发送闹钟
            Intent intent = wrapIntent(alarmEntity);
            PendingIntent pi = PendingIntent.getBroadcast(context,
                    alarmEntity.getEntityTime(),
                    intent, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            am.cancel(pi);
        }
    }

    /**
     * 判断今天的全部闹钟是否都已经添加
     * @param context 上下文
     * @return 是否全部的闹钟都已经添加
     */
    private static boolean areAlarmsAdded(Context context){
        SharedPreferences pref = context.getSharedPreferences(REM_FILE_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean(String.valueOf(TpTime.millisOfCurrentDate()), false);
    }

    /**
     * 设置今天的闹钟处于为“已设置”：TRUE
     * @param context 上下文
     */
    private static void setAlarmsAdded(Context context){
        SharedPreferences shared = context.getSharedPreferences(REM_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putBoolean(String.valueOf(TpTime.millisOfCurrentDate()), true);
        editor.apply();
    }

    /**
     * 添加每天的提醒时间
     * @param context 上下文
     */
    private static void addDailyNotice(Context context){
//        AlarmLoader loader = new AlarmLoader(context);
//        List<Alarm> alarmEntities = loader.get();
//        loader.closeDatabase();
//        if (alarmEntities != null) {
//            for (Alarm alarmEntity : alarmEntities){
//                // 判断、设置并发送闹钟
//                setAlarm(context, alarmEntity);
//            }
//        }
    }

    /**
     * 取消今天的全部闹钟
     * @param context 上下文
     */
    private static void cancelDailyNotice(Context context){
//        AlarmLoader loader = new AlarmLoader(context);
//        List<Alarm> alarmEntities = loader.get();
//        loader.closeDatabase();
//        if (alarmEntities != null) {
//            for (Alarm alarmEntity : alarmEntities){
//                // 判断、设置并发送闹钟
//                cancelAlarm(context, alarmEntity);
//            }
//        }
    }

    /**
     * 包装闹钟实体成一个Intent，因为Intent作为区分一个闹钟的标识，
     * 所以在添加和取消的时候，Intent包装方式和内容必须相同
     * @param alarmEntity 闹钟实体
     * @return 包装完毕的Intent
     */
    private static Intent wrapIntent(Alarm alarmEntity){
        Intent intent = new Intent(BROAD_ACTION);
        intent.putExtra(KEY_TITLE, alarmEntity.getStrTitle());
        intent.putExtra(KEY_SUB_TITLE, alarmEntity.getStrSubTitle());
        intent.putExtra(KEY_FOOTER1, alarmEntity.getStrFooter1());
        intent.putExtra(KEY_FOOTER2, alarmEntity.getStrFooter2());
        intent.putExtra(KEY_CONTENT, alarmEntity.getStrContent());
        intent.putExtra(KEY_COLOR, alarmEntity.getStrColor());
        intent.putExtra(KEY_TYPE, alarmEntity.getType());
        intent.putExtra(KEY_ITEM_ID, alarmEntity.getEntityId());
        intent.putExtra(KEY_NOTICE_TIME, alarmEntity.getEntityTime());
        return intent;
    }

    /**
     * 判断闹钟是否是今天的闹钟 闹钟是否已经被触发过
     * @return 是否是今天的闹钟
     */
    private static boolean isAlarmToday(Alarm alarmEntity){
        // 检查闹钟实体的日期：闹钟的日期应该等于今天
        // 一天的毫秒数
        long unitDay = 24 * 60 * 60 * 1000;
        // 今天的开始毫秒数
        long millisToday = TpTime.millisOfCurrentDate();
        // 今天的结束毫秒数
        long millisEndToday = millisToday + unitDay;
        // 检测闹钟的时间：时间大于今天结束的时间 或者 时间小于当前时间 则退出
        long alarmMillis = alarmEntity.getEntityTime() + millisToday;
        return alarmEntity.getEntityDate() == millisToday
                && alarmEntity.getEntityTime() < unitDay
                && alarmMillis >= System.currentTimeMillis();
    }
}
