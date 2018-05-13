package me.shouheng.timepartner.utils;

import android.graphics.Color;
import android.text.TextUtils;

import me.shouheng.timepartner.R;

public class TpColor {

    public final static String COLOR_TASK = "#0288D1";
    public final static String COLOR_EXAM = "#0288D1";
    public final static String COLOR_NOTE = "#395373";
    public final static String COLOR_ASSIGN = "#6200EA";
    public final static String COLOR_CLASS = "#4CAF50";
    public final static String COLOR_PRIME = "#2E3CA6";

    public final static String[] arrColorStr = new String[]{
            "#37474F","#6D4C41","#827717","#9E9E9E","#4CAF50",
            "#64DD17","#AEEA00","#673AB7","#6200EA","#AA00FF",
            "#7C4DFF","#304FFE","#C51162","#D50000","#E91E63",
            "#E65100","#FF3D00","#FFAB00","#FFD600","#0091EA",
            "#0277BD","#009688","#26A69A","#00BFA5","#00B8D4"};

    public final static int[] arrColorRes = new int[]{
            R.color.color_1,  R.color.color_2,  R.color.color_3,  R.color.color_4,  R.color.color_5,
            R.color.color_6,  R.color.color_7,  R.color.color_8,  R.color.color_9,  R.color.color_10,
            R.color.color_11, R.color.color_12, R.color.color_13, R.color.color_14, R.color.color_15,
            R.color.color_16, R.color.color_17, R.color.color_18, R.color.color_19, R.color.color_20,
            R.color.color_21, R.color.color_22, R.color.color_23, R.color.color_24, R.color.color_25};

    public static int getColorResource(int index){
        if (index >= 0 && index<arrColorRes.length){
            return arrColorRes[index];
        }
        return -1;
    }

    public static int getColorResource(String colorStr){
        if (TextUtils.isEmpty(colorStr)){
            return arrColorRes[0];
        }
        for (int i = 0;i<arrColorStr.length;i++){
            if (arrColorStr[i].equals(colorStr)){
                return arrColorRes[i];
            }
        }
        return arrColorRes[0];
    }

    public static String getColorString(int position){
        if (position >= 0 && position<arrColorRes.length){
            return arrColorStr[position];
        }
        return null;
    }

    public static int parseColor(String colorStr, String defaultColorStr){
        int parsedColor = Color.parseColor(defaultColorStr);
        try {
            parsedColor = Color.parseColor(colorStr);
        } catch (IllegalArgumentException e){
            return parsedColor;
        }
        return parsedColor;
    }
}
