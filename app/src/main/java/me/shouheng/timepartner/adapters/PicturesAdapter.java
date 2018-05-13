package me.shouheng.timepartner.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.shouheng.timepartner.R;
import me.shouheng.timepartner.activities.base.PictureActivity;
import me.shouheng.timepartner.models.entities.picture.Picture;
import me.shouheng.timepartner.models.entities.picture.Pictures;
import me.shouheng.timepartner.utils.TpDisp;
import me.shouheng.timepartner.widget.SquareImageView;

public class PicturesAdapter extends RecyclerView.Adapter<PicturesAdapter.ViewHolder> {

    private Pictures pictures;

    private Context mContext;

    public void setPictures(Pictures pictures){
        this.pictures = pictures;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        int padding = TpDisp.dp2Px(mContext, 2.5f);
        SquareImageView squareImageView = new SquareImageView(mContext);
        squareImageView.setPadding(padding, padding, padding, padding);
        parent.addView(squareImageView);
        return new ViewHolder(squareImageView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageView imageView = holder.imageView;

        Picture picture = pictures.getPictures().get(position);
        final String picPath = picture.getPicturePath();

        Glide.with(mContext)
                .load(picPath)
                .centerCrop()
                .crossFade()
                .into(imageView);
    }

    @Override
    public int getItemCount() {
        return pictures.getPictures().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        SquareImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = (SquareImageView) itemView;
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Picture current = pictures.getPictures().get(getAdapterPosition());
            PictureActivity.startForMultiple(mContext, pictures, current);
        }
    }
}
