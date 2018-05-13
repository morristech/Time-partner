package me.shouheng.timepartner.models.entities.picture;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangshouheng on 2017/1/22.*/
public class Pictures implements Serializable{

    List<Picture> pictures = new ArrayList<>();

    public List<Picture> getPictures() {
        return pictures;
    }

    public void setPictures(List<Picture> pictures) {
        this.pictures = pictures;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Picture picture : pictures){
            sb.append(picture.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}
