package me.shouheng.timepartner.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import me.shouheng.timepartner.managers.UserKeeper;

/**
 * 保存天气和定位信息
 * Created by wangshouheng on 2016/9/18. */
public class TpWeather {

    public static final class Keys {
        public final static String CITY = "currentCity";
        public final static String PM25 = "pm25";
        public final static String LOCATION = "last_location";
        public final static String CONNECTOR = "##";
        public final static String WEATHER_MAP_KEY_DATE = "date";
        public final static String WEATHER_MAP_KEY_WEATHER = "weather";
        public final static String WEATHER_MAP_KEY_WIND = "wind";
        public final static String WEATHER_MAP_KEY_TEMP = "temperature";
        public final static String WEATHER_MAP_KEY_TITLE = "title";
        public final static String WEATHER_MAP_KEY_ZS = "zs";
        public final static String WEATHER_MAP_KEY_TIPT = "tipt";
        public final static String WEATHER_MAP_KEY_DES = "des";
        public final static String WEATHER_KEY_WEARING_STATE = "wearing";
        public final static String WEATHER_KEY_VISITING_STATE = "visiting";
        public final static String WEATHER_KEY_COLD_STATE = "cold";
        public final static String WEATHER_KEY_SPORT_STATE = "sport";
        public final static String WEATHER_KEY_DETAIL_DAY1 = "day1"; //今天
        public final static String WEATHER_KEY_DETAIL_DAY2 = "day2"; //明天
        public final static String WEATHER_KEY_DETAIL_DAY3 = "day3"; //后天
        public final static String WEATHER_KEY_DETAIL_DAY4 = "day4"; //大后天
        public final static String WEATHER_KEY_LAST_UPDATE_TIME = "last_update_time";
    }

    private static String WEATHER_FILE_NAME = "WEATHER";
    private static TpWeather tpWeather;
    private SharedPreferences shared;
    public final static String FILE_NAME_APPENDIX = "WEATHER";

    static {
        // 使用 静态初始化代码块 和 构造函数 初始化文件名
        WEATHER_FILE_NAME = WeatherKey.WEATHER_FILE_NAME_APPENDIX + UserKeeper.getUser().getAccount();
    }

    public static TpWeather getInstance(Context context){
        if (tpWeather == null){
            tpWeather = new TpWeather(context);
        }
        return tpWeather;
    }

    public static void reset(){
        WEATHER_FILE_NAME = WeatherKey.WEATHER_FILE_NAME_APPENDIX + UserKeeper.getUser().getAccount();
        if (tpWeather != null){
            tpWeather = null;
        }
    }

    private TpWeather(Context context){
        shared = context.getSharedPreferences(WEATHER_FILE_NAME, Context.MODE_PRIVATE);
    }

    private void setCityName(String cityName){
        SharedPreferences.Editor editor = shared.edit();
        editor.putString(WeatherKey.WEATHER_KEY_CITY, cityName); // 城市名
        editor.apply();
    }

    public String getCityName(){
        return shared.getString(WeatherKey.WEATHER_KEY_CITY, "");
    }

    private void setPm25(String pm25) {
        SharedPreferences.Editor editor = shared.edit();
        editor.putString(WeatherKey.WEATHER_KEY_PM25, pm25);
        editor.apply();
    }

    public String getPm25() {
        return shared.getString(WeatherKey.WEATHER_KEY_PM25, "");
    }

    public void setLastLocation(String strLocation){
        SharedPreferences.Editor editor = shared.edit();
        editor.putString(WeatherKey.WEATHER_KEY_LAST_LOCATION, strLocation);
        editor.apply();
    }

    public String getLastLocation(){
        return shared.getString(WeatherKey.WEATHER_KEY_LAST_LOCATION, "");
    }

    public HashMap<String, String> getDetailOfDay(String keyDay) {
        // 取指定日期的天气信息，信息字符串被分解之后通过映射表返回
        String detailDay = shared.getString(keyDay, "");
        return parseDailyWeather(detailDay);
    }

    public Map<String, String> getLifeState(String stateKey) {
        // 获取生活信息提示
        String strStates = shared.getString(stateKey, "");
        return parseLifeStates(strStates);
    }

    public void parseJson(String jsonData){
        // 解析获取到的天气信息
        try {
            JSONObject objOut = new JSONObject(jsonData);
            JSONArray arrResult = objOut.getJSONArray("results");
            JSONObject objResult = arrResult.getJSONObject(0);
            // 设置当前城市
            String strCity = objResult.getString("currentCity");
            if (!TextUtils.isEmpty(strCity)){
                setCityName(strCity);
            }
            // 获取pm2.5信息
            String strPm25 = objResult.getString("pm25");
            if (!TextUtils.isEmpty(strPm25)){
                setPm25(strPm25);
            }
            // 获取索引
            JSONArray arrIndex = objResult.getJSONArray("index");
            for (int i=0; i<arrIndex.length(); i++){
                // 将生活信息保存到本都文件中
                //0:wearing, 2:visiting, 3:cold, 4:sport
                String key = "";
                if (i == 0){
                    key = WeatherKey.WEATHER_KEY_WEARING_STATE; }
                else if (i == 2){
                    key = WeatherKey.WEATHER_KEY_VISITING_STATE; }
                else if (i == 3){
                    key = WeatherKey.WEATHER_KEY_COLD_STATE; }
                else if (i == 4){
                    key = WeatherKey.WEATHER_KEY_SPORT_STATE; }
                if (!TextUtils.isEmpty(key)){
                    JSONObject objIndex = arrIndex.getJSONObject(i);
                    String strTitle = objIndex.getString("title");
                    String strZs = objIndex.getString("zs");
                    String strTipt = objIndex.getString("tipt");
                    String strDes = objIndex.getString("des");
                    String formedDetail = formatDetails(strTitle, strZs, strTipt, strDes);
                    setLifeState(key, formedDetail);
                }
            }
            // 获取天气信息
            String strKeys[] = new String[]{
                    WeatherKey.WEATHER_KEY_DETAIL_DAY1,
                    WeatherKey.WEATHER_KEY_DETAIL_DAY2,
                    WeatherKey.WEATHER_KEY_DETAIL_DAY3,
                    WeatherKey.WEATHER_KEY_DETAIL_DAY4};
            JSONArray arrWeather = objResult.getJSONArray("weather_data");
            for (int i=0;i<arrWeather.length();i++){
                JSONObject objWeather = arrWeather.getJSONObject(i);
                String strDate = objWeather.getString("date");
                String strWeather = objWeather.getString("weather");
                String strWind = objWeather.getString("wind");
                String strTemperature = objWeather.getString("temperature");
                String formedDetail = formatDetails(strDate, strWeather, strWind, strTemperature);
                setDetailOfDay(strKeys[i], formedDetail);
            }
            // 设置 上一次 更新天气的时间
            setLastUpdateTime();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setLastUpdateTime(){
        SharedPreferences.Editor editor = shared.edit();
        editor.putLong(WeatherKey.WEATHER_KEY_LAST_UPDATE_TIME, System.currentTimeMillis() / 1000 * 1000);
        editor.apply();
    }

    public boolean isDataOutOfDate(){
        // 获取 上一次 更新的毫秒数
        long lastTime = shared.getLong(WeatherKey.WEATHER_KEY_LAST_UPDATE_TIME, -1);
        if (lastTime == -1){
            // 还没有设置过 天气 数据
            return true;
        } else {
            long unitHour = 3 * 60 * 60 * 1000;
            // 当前的毫秒数 - 上一次更新的毫秒数 > 3小时 即说明 过期
            return (System.currentTimeMillis() / 1000 * 1000 - lastTime) > unitHour;
        }
    }

    private void setLifeState(String stateKey, String stateValue) {
        // 设置生活信息提示字符串，字符串也是使用connector将四个字符串连接组成的
        SharedPreferences.Editor editor = shared.edit();
        editor.putString(stateKey, stateValue);
        editor.apply();
    }

    private Map<String, String> parseLifeStates(String strStates){
        // 从指定的格式化后的字符串中解析生活信息
        Map<String, String> map = new HashMap<>();
        String strs[];
        strs = strStates.split(WeatherKey.WEATHER_KEY_CONNECTOR);
        if (strs.length == 4){
            map.put(WeatherKey.WEATHER_MAP_KEY_TITLE, strs[0]);
            map.put(WeatherKey.WEATHER_MAP_KEY_ZS, strs[1]);
            map.put(WeatherKey.WEATHER_MAP_KEY_TIPT, strs[2]);
            map.put(WeatherKey.WEATHER_MAP_KEY_DES, strs[3]);
        }
        return map;
    }

    private String formatDetails(String ...strs){
        // 格式化天气的详细信息，使用字符将天气信息进行拼接
        int len = strs.length;
        String tempStr = strs[0];
        for (int i = 1;i<len;i++){
            tempStr = tempStr + WeatherKey.WEATHER_KEY_CONNECTOR + strs[i];
        }
        return tempStr;
    }

    private HashMap<String, String> parseDailyWeather(String strDetail){
        // 从指定的字符串中解析日期信息，与formatDetails对应的方法
        HashMap<String, String> map = new HashMap<>();
        String strs[];
        strs = strDetail.split(WeatherKey.WEATHER_KEY_CONNECTOR);
        if (strs.length == 4){
            map.put(WeatherKey.WEATHER_MAP_KEY_DATE, strs[0]);
            map.put(WeatherKey.WEATHER_MAP_KEY_WEATHER, strs[1]);
            map.put(WeatherKey.WEATHER_MAP_KEY_WIND, strs[2]);
            map.put(WeatherKey.WEATHER_MAP_KEY_TEMP, strs[3]);
        }
        return map;
    }

    private void setDetailOfDay(String keyDay, String valueDay) {
        /**设置指定日期的天气信息
         * 连续四天的信息是用一个字符串来分别存储的，中间使用connector进行连接
         * 键值在上面定义了，一个键对应一个日期字符串
         * 该方法在解析json数据的时候被调用 */
        SharedPreferences.Editor editor = shared.edit();
        editor.putString(keyDay, valueDay);
        editor.apply();
    }

}
