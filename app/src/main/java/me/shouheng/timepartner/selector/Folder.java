package me.shouheng.timepartner.selector;

import android.text.TextUtils;

import java.util.List;

/**
 * Created by wangshouheng on 2017/1/23.*/
public class Folder {

    public String name;
    public String path;
    public Image cover;
    public List<Image> images;

    public boolean equals(Object o) {
        try {
            Folder other = (Folder)o;
            return TextUtils.equals(other.path, this.path);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return super.equals(o);
    }
}
