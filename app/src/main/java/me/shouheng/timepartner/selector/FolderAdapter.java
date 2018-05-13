package me.shouheng.timepartner.selector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import me.shouheng.timepartner.R;

/**
 * Created by wangshouheng on 2017/1/23.*/
public class FolderAdapter extends BaseAdapter{

    private Context mContext;

    private LayoutInflater mInflater;

    private List<Folder> mFolders = new ArrayList<>();

    private int lastSelected = 0;

    public FolderAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
//        int mImageSize = this.mContext.getResources().getDimensionPixelOffset(R.dimen.mis_folder_cover_size);
    }

    public void setData(List<Folder> folders) {
        if ((folders != null) && (folders.size() > 0)) {
            this.mFolders = folders;
        } else {
            this.mFolders.clear();
        }
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.mFolders.size() + 1;
    }

    public Folder getItem(int i) {
        if (i == 0) {
            return null;
        }
        return mFolders.get(i - 1);
    }

    public long getItemId(int i) {
        return i;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = this.mInflater.inflate(R.layout.mis_list_item_folder, viewGroup, false);
            holder = new ViewHolder(view);
        } else {
            holder = (ViewHolder)view.getTag();
        }
        if (holder != null) {
            if (i == 0) {
                holder.name.setText(R.string.mis_folder_all);
                holder.path.setText("/sdcard");
                holder.size.setText(String.format("%d", new Object[]{Integer.valueOf(getTotalImageSize())}));
                if (this.mFolders.size() > 0) {
                    Folder f = mFolders.get(0);
                    if (f != null) {
                        Glide.with(mContext).load(f.cover.path).centerCrop().into(holder.cover);
                    } else {
                        holder.cover.setImageResource(R.drawable.mis_default_error);
                    }
                }
            } else {
                holder.bindData(getItem(i));
            }
            if (this.lastSelected == i) {
                holder.indicator.setVisibility(View.VISIBLE);
            } else {
                holder.indicator.setVisibility(View.INVISIBLE);
            }
        }
        return view;
    }

    private int getTotalImageSize() {
        int result = 0;
        if ((mFolders != null) && (mFolders.size() > 0)) {
            for (Folder f : mFolders) {
                result += f.images.size();
            }
        }
        return result;
    }

    public void setSelectIndex(int i) {
        if (this.lastSelected == i) {
            return;
        }
        this.lastSelected = i;
        notifyDataSetChanged();
    }

    public int getSelectIndex() {
        return this.lastSelected;
    }

    class ViewHolder {
        ImageView cover;
        TextView name;
        TextView path;
        TextView size;
        ImageView indicator;

        ViewHolder(View view) {
            this.cover = ((ImageView)view.findViewById(R.id.cover));
            this.name = ((TextView)view.findViewById(R.id.name));
            this.path = ((TextView)view.findViewById(R.id.path));
            this.size = ((TextView)view.findViewById(R.id.size));
            this.indicator = ((ImageView)view.findViewById(R.id.indicator));
            view.setTag(this);
        }

        void bindData(Folder data) {
            if (data == null) {
                return;
            }
            this.name.setText(data.name);
            this.path.setText(data.path);
            if (data.images != null) {
                this.size.setText(String.format("%d", new Object[]{Integer.valueOf(data.images.size())}));
            } else {
                this.size.setText("empty");
            }
            if (data.cover != null) {
                Glide.with(mContext).load(data.cover.path)
                        .placeholder(R.drawable.mis_default_error).centerCrop().into(cover);
            } else {
                this.cover.setImageResource(R.drawable.mis_default_error);
            }
        }
    }
}
