package me.shouheng.timepartner.utils;

import android.content.Context;

import me.shouheng.timepartner.R;

public class TpString {

    /**
     * 将录音文件的时长转换成标准格式：03:00
     * @param duration 时长
     * @return 返回字符串*/
    public static String formatDuration(int duration){
        int seconds = duration / 1000; // 毫秒转换成秒
        int minute = seconds / 60; // 秒数的“分钟”位的值
        int second = seconds % 60; // 秒数的“秒”位的值
        StringBuilder sb = new StringBuilder();
        if (minute == 0){
            sb.append("00");
        }else if (minute < 10 && minute > 0){
            sb.append("0");
            sb.append(minute);
        }else {
            sb.append(minute);
        }
        sb.append(":");
        if (second == 0){
            sb.append("00");
        }else if (second > 0 && second < 10){
            sb.append("0");
            sb.append(second);
        }else {
            sb.append(second);
        }
        return sb.toString();
    }

    /**
     * 将字符串中的 数字 全部提取出来连接成字符串
     * @param str 字符串
     * @return 解析结果 */
    public static String parseNumber(String str){
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<str.length();i++){
            if ("1234567890".indexOf(str.charAt(i))!=-1){
                sb.append(str.charAt(i));
            }
        }
        return sb.toString();
    }

    /**
     * 将字符串中的 字母、数字、下划线 提取出来并拼接成字符串
     * @param str 字符串
     * @return 解析结果 */
    public static String parseChars(String str){
        StringBuilder sb = new StringBuilder();
        for (int i = 0;i<str.length();i++){
            if (("1234567890qwertyuiopasdfghjklzxcvbnm" +
                    "xyzQWERTYUIOPASDFGHJKLZXCVBNM_").indexOf(str.charAt(i))!=-1){
                sb.append(str.charAt(i));
            }
        }
        return sb.toString();
    }

    /**
     * 获取实时温度信息：根据传入的字符串的形式，将其中的温度值解析出来
     * @param strDate example 周日 09月14日 (实时：21℃)
     * @return 解析实时温度 */
    public static String getRealTimeTemp(String strDate){
        int startIndex = strDate.indexOf("：");
        int lastIndex = strDate.indexOf("℃");
        if (startIndex == -1 || lastIndex == -1){
            return "--";
        }
        return strDate.substring(startIndex + 1, lastIndex); //包含第1个不含最后一个
    }

    /**
     * 获取上次更新的日期
     * @return 字符串 */
    public static String getLastUpdateDate(String strDate){
        return strDate.substring(0, strDate.indexOf("("));
    }

    /**
     * 从日期字符串中获取周次信息
     * @param strDate 日期字符串
     * @return */
    public static String getWeekFromDate(String strDate){
        int index = strDate.indexOf("周");
        return strDate.substring(index, index + 2);
    }

    /**
     * 解析天气字符串并返回与天气信息对应的图片资源的id
     * @param strWeather
     * @return */
    public static int getWeatherImage(String strWeather){
        // 当天气为 A转B 时，按照后面的那个天气的信息进行设置
        if (strWeather.contains("转")){
            String temp[] = strWeather.split("转");
            strWeather = temp[1];
        }
        switch (strWeather){
            case "晴":
                return R.drawable.ic_weather_sunny;
            case "多云":
                return R.drawable.ic_weather_cloudy;
            case "阴":
                return R.drawable.ic_weather_overcast;
            case "阵雨":
            case "小雨":
                return R.drawable.ic_weather_light_rain;
            case "中雨":
            case "小雨转中雨":
                return R.drawable.ic_weather_moderate_rain;
            case "大雨":
            case "暴雨":
            case "大暴雨":
            case "特大暴雨":
            case "中雨转大雨":
            case "大雨转暴雨":
            case "暴雨转大暴雨":
            case "大暴雨转特大暴雨":
                return R.drawable.ic_weather_heavy_rain;
            case "雷阵雨":
            case "雷阵雨伴有冰雹":
                return R.drawable.ic_weather_thunder_rain;
            case "雨夹雪":
                return R.drawable.ic_weather_sleet;
            case "冻雨":
                return R.drawable.ic_weather_hail;
            case "阵雪":
            case "小雪":
                return R.drawable.ic_weather_light_snow;
            case "中雪":
            case "小雪转中雪":
                return R.drawable.ic_weather_snow;
            case "大雪":
            case "暴雪":
            case "中雪转大雪":
            case "大雪转暴雪":
                return R.drawable.ic_weather_sandstorm;
            case "雾":
            case "霾":
                return R.drawable.ic_weather_foggy;
            case "浮尘":
            case "扬沙":
            case "沙尘暴":
            case "强沙尘暴":
                return R.drawable.ic_weather_sand;
        }
        return -1;
    }

    /**
     * 获取当前的天气
     * @param strTemp 天气字符串
     * @return 解析结果 */
    public static String getTemperature(String strTemp){
        String strs[] = strTemp.split(" ~ ");
        if (strs.length > 2){
            return strs[0] + "℃/" + strs[1];
        }
        return strTemp;
    }

    /**
     * 从二进制字符中解析出星期字符串
     * @param mContext 上下文，用于获取字符串的值
     * @param strWeek 二进制字符串
     * @return 星期字符串 */
    public static String getWeekString(Context mContext, String strWeek){
        final int weeks[] = new int[]{
                R.string.class_week7, R.string.class_week1,
                R.string.class_week2, R.string.class_week3,
                R.string.class_week4, R.string.class_week5,
                R.string.class_week6};
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<weeks.length; i++){
            if (strWeek.charAt(i) == '1'){
                sb.append(mContext.getString(weeks[i]));
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    /**
     * 从多选对话框的选择结果中解析星期字符串
     * @param arrWeekStrs 星期字符串数组
     * @param checked 多选对话框选择结果
     * @return 星期字符串[0]和对应的二进制字符串[1] */
    public static String[] parseWeekString(String[] arrWeekStrs, boolean[] checked){
        // 星期字符串和对应二进制
        StringBuilder weekStr = new StringBuilder();
        StringBuilder weekTag = new StringBuilder();
        // 解析选项
        int num = 0;
        for (int i = 0;i<checked.length;i++){
            if (checked[i]){
                if (num==0){
                    weekStr.append(arrWeekStrs[i]);
                }else{
                    weekStr.append("/").append(arrWeekStrs[i]);
                }
                if (++num>3){
                    weekStr.append("\n");
                    num = 0;
                }
                weekTag.append("1");
            }else {
                weekTag.append("0");
            }
        }
        return new String[]{weekStr.toString(), weekTag.toString()};
    }
}
