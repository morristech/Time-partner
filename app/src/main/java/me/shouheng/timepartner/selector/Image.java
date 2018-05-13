package me.shouheng.timepartner.selector;

import android.text.TextUtils;

/**
 * Created by wangshouheng on 2017/1/23.*/
public class Image {

    public String path;
    public String name;
    public long time;

    public Image(String path, String name, long time) {
        this.path = path;
        this.name = name;
        this.time = time;
    }

    public boolean equals(Object o) {
        try {
            Image other = (Image)o;
            return TextUtils.equals(this.path, other.path);
        }
        catch (ClassCastException e) {
            e.printStackTrace();
        }
        return super.equals(o);
    }
}
