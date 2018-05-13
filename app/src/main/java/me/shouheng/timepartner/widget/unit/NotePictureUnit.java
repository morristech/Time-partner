package me.shouheng.timepartner.widget.unit;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import me.shouheng.timepartner.R;
import me.shouheng.timepartner.activities.base.PictureActivity;
import me.shouheng.timepartner.managers.ImageManager;
import me.shouheng.timepartner.models.entities.picture.Picture;
import me.shouheng.timepartner.models.entities.picture.Pictures;
import me.shouheng.timepartner.utils.TpDisp;
import me.shouheng.timepartner.utils.TpFile;
import me.shouheng.timepartner.widget.SquareImageView;

/**
 * 下面的类能够自动过滤掉那些已经不存在的图片文件路径 */
public class NotePictureUnit extends RelativeLayout{
    private Context mContext;
    private LinearLayout picList;

    private Pictures pictures;
    private List<ImageView> listDelFooters = new LinkedList<>();

    private boolean isShowingFooter = false;

    private static final String TAG = "NotePictureUnit__";

    public NotePictureUnit(Context context) {
        super(context);
        init(context);
    }

    public NotePictureUnit(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NotePictureUnit(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mContext = context;

        LayoutInflater.from(context).inflate(R.layout.view_unit_pic, this);
        picList = (LinearLayout) findViewById(R.id.picture_list);

        findViewById(R.id.pic_header).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                for (ImageView view : listDelFooters){
                    if (isShowingFooter){
                        view.setVisibility(GONE);
                    } else {
                        view.setVisibility(VISIBLE);
                    }
                }
                isShowingFooter = !isShowingFooter;
            }
        });
    }

    public void addPicture(Picture picture){
        if (pictures == null){
            pictures = new Pictures();
            pictures.setPictures(new ArrayList<Picture>());
        }
        if (pictures.getPictures().size() == 9) {
            TpDisp.showToast(mContext, R.string.n_picture_enough);
            return;
        }
        showPicture(picture);
        pictures.getPictures().add(picture);
    }

    public void setPictures(Pictures pictures){
        if (pictures == null)   return;
        if (pictures.getPictures().size() == 0) return;

        this.pictures = pictures;

        for (Picture picture : pictures.getPictures()){
            String picPath = picture.getPicturePath();
            if (!TextUtils.isEmpty(picPath) && TpFile.isFileExist(picPath)){
                showPicture(picture);
            }
        }
    }

    public Pictures getPictures(){
        return pictures;
    }

    private void showPicture(final Picture picture){
        if (picture == null) return;

        String picPath = picture.getPicturePath();
        if (TextUtils.isEmpty(picPath) || !TpFile.isFileExist(picPath)) return;

        final View view = LayoutInflater.from(mContext)
                .inflate(R.layout.view_unit_pic_list_item, null);
        SquareImageView iv = (SquareImageView) view.findViewById(R.id.picture);
        Glide.with(mContext).load(picPath).centerCrop().into(iv);
        picList.addView(view);

        iv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureActivity.startForSingleEdit(mContext, picture);
            }
        });

        ImageView delFooter = (ImageView) view.findViewById(R.id.delete_footer);
        delFooter.setVisibility(GONE);
        delFooter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                picList.removeView(view);
                pictures.getPictures().remove(picture);
            }
        });
        listDelFooters.add(delFooter);
    }
}
