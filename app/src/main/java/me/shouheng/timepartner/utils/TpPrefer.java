package me.shouheng.timepartner.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import me.shouheng.timepartner.managers.UserKeeper;

public class TpPrefer {
    private static String PREFER_FILE_NAME = "SETTING";
    private static TpPrefer tpPrefer;
    private SharedPreferences shared;

    static {
        //PREFER_FILE_NAME = PreferKey.FILE_NAME_APPENDIX + UserManager.getUser().getAccount();
    }

    // 当使用新账户的时候必须调用该方法 清理保存的 tpPrefer 重置 FILE_NAME
    public static void reset(){
        PREFER_FILE_NAME = PreferKey.FILE_NAME_APPENDIX + UserKeeper.getUser().getAccount();;
        if (tpPrefer != null){
            tpPrefer = null;
        }
    }

    public static TpPrefer getInstance(Context context){
        PREFER_FILE_NAME = PreferKey.FILE_NAME_APPENDIX + UserKeeper.getUser(context).getAccount();
        if (tpPrefer == null){
            tpPrefer = new TpPrefer(context);
        }
        return tpPrefer;
    }

    private TpPrefer(Context context){
        shared = context.getSharedPreferences(PREFER_FILE_NAME, Context.MODE_PRIVATE);
    }

    public void setBackground(String bg, int mode){
        SharedPreferences.Editor editor = shared.edit();
        editor.putInt(PreferKey.KEY_BG_MODE, mode); // 类型
        editor.putString(PreferKey.KEY_KEY_BG, bg); // 内容
        editor.apply();
    }

    public Bundle getBackground(){
        Bundle bundle = new Bundle();
        bundle.putInt(PreferKey.KEY_BG_MODE, shared.getInt(PreferKey.KEY_BG_MODE, PreferKey.BG_MODE_COLOR));
        bundle.putString(PreferKey.KEY_KEY_BG, shared.getString(PreferKey.KEY_KEY_BG, PreferKey.DEFAULT_BG_COLOR));
        return bundle;
    }

    public void setSaying(String saying){
        SharedPreferences.Editor editor = shared.edit();
        editor.putString(PreferKey.KEY_SAYING, saying);
        editor.apply();
    }

    public String getSaying(){
        return shared.getString(PreferKey.KEY_SAYING, "Welcome to TimePartner.");
    }

    public void setShowWeather(boolean isShow){
        SharedPreferences.Editor editor = shared.edit();
        editor.putBoolean(PreferKey.KEY_SHOW_WEATHER, isShow);
        editor.apply();
    }

    public boolean getShowWeather(){
        return shared.getBoolean(PreferKey.KEY_SHOW_WEATHER, true);
    }

    public void setNoticeMode(int noticeMode){
        SharedPreferences.Editor editor = shared.edit();
        editor.putInt(PreferKey.KEY_NOTICE_MODE, noticeMode);
        editor.apply();
    }

    public int getNoticeMode(){
        return shared.getInt(PreferKey.KEY_NOTICE_MODE, PreferKey.NOTICE_MODE1);
    }

    public void setShowLunar(boolean isShow){
        SharedPreferences.Editor editor = shared.edit();
        editor.putBoolean(PreferKey.KEY_SHOW_LUNAR, isShow);
        editor.apply();
    }

    public boolean getShowLunar(){
        return shared.getBoolean(PreferKey.KEY_SHOW_LUNAR, true);
    }

    public void setDailyNoticeTime(int daily){
        SharedPreferences.Editor editor = shared.edit();
        editor.putInt(PreferKey.KEY_DAILY_NOTICE, daily);
        editor.apply();
    }

    public int getDailyNoticeTime(){
        return shared.getInt(PreferKey.KEY_DAILY_NOTICE, 8);
    }

    public void setNoticeTime(long time){
        // 设置每次提醒的时间 整点提醒或者在之前的一段时间内提醒 单位: 毫秒
        SharedPreferences.Editor editor = shared.edit();
        editor.putLong(PreferKey.KEY_NOTICE_TIME, time);
        editor.apply();
    }

    public long getNoticeTime(){
        return shared.getLong(PreferKey.KEY_NOTICE_TIME, 15 * 60 * 1000);// 默认提前15分钟提醒
    }

    public void setShowTimeLine(boolean isShow){
        SharedPreferences.Editor editor = shared.edit();
        editor.putBoolean(PreferKey.KEY_TIME_LINE, isShow);
        editor.apply();
    }

    public boolean getShowTimeLine(){
        return shared.getBoolean(PreferKey.KEY_TIME_LINE, true);
    }

    public void setCldHourHeight(int num){
        SharedPreferences.Editor editor = shared.edit();
        editor.putInt(PreferKey.KEY_CLD_HOUR_HEIGHT, num);
        editor.apply();
    }

    public int getCldHourHeight(){
        return shared.getInt(PreferKey.KEY_CLD_HOUR_HEIGHT, 7);
    }

    public void setCldStartTime(int startTime){
        SharedPreferences.Editor editor = shared.edit();
        editor.putInt(PreferKey.KEY_CLD_START_TIME, startTime);
        editor.apply();
    }

    public int getCldStartTime(){
        return shared.getInt(PreferKey.KEY_CLD_START_TIME, 0);// 默认开始时间是0点
    }

    public void setCldEndTime(int startTime){
        SharedPreferences.Editor editor = shared.edit();
        editor.putInt(PreferKey.KEY_CLD_END_TIME, startTime);
        editor.apply();
    }

    public int getCldEndTime(){
        return shared.getInt(PreferKey.KEY_CLD_END_TIME, 23);//默认结束时间为23点
    }

    public void setClsHourHeight(int num){
        SharedPreferences.Editor editor = shared.edit();
        editor.putInt(PreferKey.KEY_CLS_HOUR_HEIGHT, num);
        editor.apply();
    }

    public int getClsHourHeight(){
        return shared.getInt(PreferKey.KEY_CLS_HOUR_HEIGHT, 8);
    }

    public void setClsStartTime(int startTime){
        SharedPreferences.Editor editor = shared.edit();
        editor.putInt(PreferKey.KEY_CLS_START_TIME, startTime);
        editor.apply();
    }

    public int getClsStartTime(){
        return shared.getInt(PreferKey.KEY_CLS_START_TIME, 0);
    }

    public void setClsEndTime(int startTime){
        SharedPreferences.Editor editor = shared.edit();
        editor.putInt(PreferKey.KEY_CLS_END_TIME, startTime);
        editor.apply();
    }

    public int getClsEndTime(){
        return shared.getInt(PreferKey.KEY_CLS_END_TIME, 23);
    }

    public void setClnGrid(boolean isClnGrid){
        SharedPreferences.Editor editor = shared.edit();
        editor.putBoolean(PreferKey.KEY_CLN_SHOW_TYPE, isClnGrid);
        editor.apply();
    }

    public boolean isClnGrid(){
        return shared.getBoolean(PreferKey.KEY_CLN_SHOW_TYPE, false);
    }
}
