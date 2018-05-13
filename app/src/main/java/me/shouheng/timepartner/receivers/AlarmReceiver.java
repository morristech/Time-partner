package me.shouheng.timepartner.receivers;

import android.app.Notification;
import android.support.v4.app.NotificationCompat;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.RemoteViews;

import java.util.Calendar;

import me.shouheng.timepartner.models.business.alarm.Alarm;
import me.shouheng.timepartner.R;
import me.shouheng.timepartner.activities.assignment.AssignViewActivity;
import me.shouheng.timepartner.activities.tpclass.ClassViewActivity;
import me.shouheng.timepartner.activities.note.NoteViewer;
import me.shouheng.timepartner.activities.task.TaskViewer;
import me.shouheng.timepartner.managers.TpAlarmManager;
import me.shouheng.timepartner.utils.TpPrefer;
import me.shouheng.timepartner.utils.TpTime;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * 1.在文件(AlarmUtils中定义)中使用当天的毫秒数作为键，
 * 布尔值作为值来判断当天的闹钟是否已经全部添加；
 * 2.每次登录的时候先检查本地文件，看当天是否已经添加过全部闹钟，
 * 如果添加过就不做处理，否则添加全部闹钟；
 * 3.退出登录的时候(切换账号之类)，清空文件中的标记，同时取消全部闹钟；
 * 4.卸载程序会自动取消全部闹钟，不做处理。
 * 5.每天的闹钟设置到第二天0:00的时候唤醒，添加并设置第二天的闹钟。
 * 6.新添加项目的时候，先判断时间。大于当前并且小于明天则添加闹钟
 * 7.闹钟具有提醒方式和提前提醒的时间，注意设置。
 */
public class AlarmReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        // 获取时间信息
        Calendar now = Calendar.getInstance();
        int mYear = now.get(Calendar.YEAR);
        int mMonth = now.get(Calendar.MONTH);
        int mDay = now.get(Calendar.DAY_OF_MONTH);
        int mHour = now.get(Calendar.HOUR_OF_DAY); // 24H
        int mMinute = now.get(Calendar.MINUTE);
        int dailyNoticeHour = TpPrefer.getInstance(context).getDailyNoticeTime();
        // 弹出通知
        if (dailyNoticeHour == mHour && mMinute == 0){
            // 到了每天开始的提醒时间，弹出通知
            String strTitle = TpTime.getCurrentDate()
                    + context.getString(R.string.begining_of_day_title);
            sendDefaultNotification(context, "",
                    strTitle,
                    context.getString(R.string.begining_od_day_info),
                    R.drawable.tp_icon_noticification);
        }
        if (mHour == 23 && mMinute == 11){
            // 每天11点11分提醒
            sendDefaultNotification(context, "",
                    context.getString(R.string.mflower_content_title),
                    context.getString(R.string.mflower_content_info1),
                    R.drawable.tp_rose);
        }
        if (mHour == 0 && mMinute == 0){
            // 设置明天的全部提醒
            TpAlarmManager.setAlarmsToday(context);
            sendDefaultNotification(context, "", "TEST", "0:00", R.drawable.tp_icon_noticification);
        }
        // 发送事务的通知
        sendCustomNotification(context, intent);

    }

    /**
     * 发出一个默认通知
     * @param context 上下文
     * @param strTicker 提醒
     * @param strTitle 标题
     * @param strContent 内容
     * @param imRes 图标*/
    private void sendDefaultNotification(Context context, String strTicker,
            String strTitle, String strContent, int imRes){
        NotificationManager manager =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        Notification notification =
                wrapNotification(context, strTicker, strTitle, strContent, imRes);
        notification.defaults = Notification.DEFAULT_ALL;
        manager.notify(TpTime.getShortId(), notification);
    }

    /**
     * 发出一个静音通知
     * @param context 上下文
     * @param strTicker 提示
     * @param strTitle 标题
     * @param strContent 内容
     * @param imRes 图标 */
    private void sendQuiteNotification(Context context, String strTicker,
            String strTitle, String strContent, int imRes){
        Notification notification =
                wrapNotification(context, strTicker, strTitle, strContent, imRes);
        NotificationManager manager =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        manager.notify(TpTime.getShortId(), notification);
    }

    /**
     * 发出一个振动通知
     * @param context 上下文
     * @param strTicker 提示
     * @param strTitle 标题
     * @param strContent 内容
     * @param imRes 图标 */
    private void sendVibrateNotification(Context context, String strTicker,
                                       String strTitle, String strContent, int imRes){
        Notification notification =
                wrapNotification(context, strTicker, strTitle, strContent, imRes);
        NotificationManager manager =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notification.vibrate = new long[]{500, 500, 500, 500};
        manager.notify(TpTime.getShortId(), notification);
    }

    /**
     * 包装弹出式通知
     * @param context 上下文
     * @param strTicker 提示
     * @param strTitle 标题
     * @param strContent 内容
     * @param imRes 图标
     * @return 返回包装结果 */
    private Notification wrapNotification(Context context, String strTicker,
                                          String strTitle, String strContent, int imRes){
        return new Notification.Builder(context)
                .setSmallIcon(imRes)
                .setTicker(strTicker)
                .setContentTitle(strTitle)
                .setContentInfo(strContent)
                .build();
    }

    /**
     * 发送一个自定义的通知
     * @param mContext 上下文 */
    private void sendCustomNotification(Context mContext, Intent intent){
        // 获取收到的Intent中包含的id信息
        // 对于一个不是四个事务的Intent，获取到的值是不存在的
        long itemId = intent.getLongExtra(TpAlarmManager.KEY_ITEM_ID, -1);
        if (itemId == -1){
            return;
        }
        // 获取通知管理类
        NotificationManager manager =
                (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
        // 获取布局
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(),
                R.layout.notification_view_layout);
        // 获取用于点击布局时的Intent
        Intent mIntent = new Intent();
        int resId = -1;
        int cdRes = -1;
//        mIntent.putExtra(Constants.KEY_ID, itemId);
        switch (intent.getIntExtra(TpAlarmManager.KEY_TYPE, -1)){
            case Alarm.TYPE_CLASS:
                resId = R.drawable.ic_book;
                cdRes = R.string.content_describe_text_class;
                mIntent.setClass(mContext, ClassViewActivity.class);
                break;
            case Alarm.TYPE_ASSIGN:
                resId = R.drawable.ic_assign;
                cdRes = R.string.content_describe_text_assign;
                mIntent.setClass(mContext, AssignViewActivity.class);
                break;
            case Alarm.TYPE_NOTE:
                resId = R.drawable.ic_write;
                cdRes = R.string.content_describe_text_note;
                mIntent.setClass(mContext, NoteViewer.class);
                break;
            case Alarm.TYPE_EXAM:
                resId = R.drawable.ic_exam;
                cdRes = R.string.content_describe_text_exam;
                mIntent.setClass(mContext, TaskViewer.class);
                break;
            case Alarm.TYPE_TASK:
                resId = R.drawable.ic_task;
                cdRes = R.string.content_describe_text_task;
                mIntent.setClass(mContext, TaskViewer.class);
                break;
        }
        // 构建PendingIntent用来设置点击通知时的事件
        PendingIntent pIntent = PendingIntent.getActivity(
                mContext,
                mIntent.getIntExtra(TpAlarmManager.KEY_NOTICE_TIME, 0),
                mIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        // 为布局中的指定组件添加单击事件
        remoteViews.setOnClickPendingIntent(R.id.custom_show_detail, pIntent);
        remoteViews.setOnClickPendingIntent(R.id.full, pIntent);
        remoteViews.setTextViewText(
                R.id.custom_title, intent.getStringExtra(TpAlarmManager.KEY_TITLE));
        remoteViews.setTextViewText(
                R.id.custom_subtitle, intent.getStringExtra(TpAlarmManager.KEY_SUB_TITLE));
        remoteViews.setTextViewText(
                R.id.custom_footer1, intent.getStringExtra(TpAlarmManager.KEY_FOOTER1));
        remoteViews.setTextViewText(
                R.id.custom_footer2, intent.getStringExtra(TpAlarmManager.KEY_FOOTER2));
        remoteViews.setTextViewText(
                R.id.custom_content, intent.getStringExtra(TpAlarmManager.KEY_CONTENT));
        if (resId != -1){
            remoteViews.setImageViewResource(R.id.custom_icon, resId);
        }
        if (cdRes != -1){
            remoteViews.setTextViewText(
                    R.id.custom_icon_content_describe, mContext.getString(cdRes));
        }
        try {
            int pColor = Color.parseColor(intent.getStringExtra(TpAlarmManager.KEY_COLOR));
            remoteViews.setTextColor(R.id.custom_footer1, pColor);
        }catch (Exception e){}
        // 设置通知
        Notification notification = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.drawable.tp_icon_noticification)
                .build();
        notification.bigContentView = remoteViews;
        // 使用默认的设置
        notification.defaults = Notification.DEFAULT_ALL;
        // 触发通知，使用当前的毫秒作为id防止被覆盖
        manager.notify(TpTime.getShortId(), notification);
    }
}
