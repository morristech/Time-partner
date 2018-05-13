package me.shouheng.timepartner.widget.unit;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.shouheng.timepartner.R;
import me.shouheng.timepartner.adapters.PicturesAdapter;
import me.shouheng.timepartner.models.entities.picture.Picture;
import me.shouheng.timepartner.models.entities.picture.Pictures;
import me.shouheng.timepartner.utils.TpFile;

public class MultiPicturesView extends LinearLayout{

    private Context mContext;

    private Pictures pictures;

    private int picCount;

    private static final String TAG = "MultiPicturesView__";

    public MultiPicturesView(Context context) {
        super(context);
        init(context);
    }

    public MultiPicturesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MultiPicturesView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context mContext){
        this.mContext = mContext;
    }

    /**
     * get the legal images number
     * @return number */
    public int getPicCount(){
        return picCount;
    }

    /**
     * set the images path
     * @param pictures size>0
     * @return legal image paths number
     * FALSE if parsed images count less than 0 or more than 9, otherwise TRUE*/
    public boolean setPictures(Pictures pictures) {
        Log.d(TAG, "setPictures: " + pictures);
        this.pictures = pictures;

        picCount = 0;
        Pictures legalPictures = new Pictures();
        for (Picture picture : pictures.getPictures()){
            String picPath = picture.getPicturePath();
            if (TpFile.isFileExist(picPath)){
                picCount++;
                legalPictures.getPictures().add(picture);
            }
        }
        if (picCount == 0)  return false;

        LayoutInflater.from(mContext).inflate(R.layout.note_viewer_pictures, this);
        RecyclerView rv = (RecyclerView) findViewById(R.id.pic_list);
        PicturesAdapter adapter = new PicturesAdapter();
        adapter.setPictures(legalPictures);
        rv.setLayoutManager(new GridLayoutManager(mContext, 3, LinearLayoutManager.VERTICAL, false));
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(adapter);
        return true;
    }

    public Pictures getPictures() {
        return pictures;
    }
}
