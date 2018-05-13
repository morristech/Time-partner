package me.shouheng.timepartner.selector;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.shouheng.timepartner.R;

/**
 * Created by wangshouheng on 2017/1/23. */
public class ImageGridAdapter extends BaseAdapter{

    private Context mContext;

    private static final int TYPE_CAMERA = 0;
    private static final int TYPE_NORMAL = 1;

    private LayoutInflater mInflater;

    private boolean showCamera = true;
    private boolean showSelectIndicator = true;

    private List<Image> mImages = new ArrayList<>();
    private List<Image> mSelectedImages = new ArrayList<>();

    public ImageGridAdapter(Context context, boolean showCamera, int column) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.showCamera = showCamera;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = 0;
        if (Build.VERSION.SDK_INT >= 13) {
            Point size = new Point();
            wm.getDefaultDisplay().getSize(size);
            width = size.x;
        } else {
            width = wm.getDefaultDisplay().getWidth();
        }
        int mGridWidth = (width / column);
    }

    public void showSelectIndicator(boolean b) {
        this.showSelectIndicator = b;
    }

    public void setShowCamera(boolean b) {
        if (this.showCamera == b) {
            return;
        }
        this.showCamera = b;
        notifyDataSetChanged();
    }

    public boolean isShowCamera() {
        return this.showCamera;
    }

    public void select(Image image) {
        if (this.mSelectedImages.contains(image)) {
            this.mSelectedImages.remove(image);
        } else {
            this.mSelectedImages.add(image);
        }
        notifyDataSetChanged();
    }

    public void setDefaultSelected(ArrayList<String> resultList) {
        for (String path : resultList) {
            Image image = getImageByPath(path);
            if (image != null) {
                this.mSelectedImages.add(image);
            }
        }
        if (this.mSelectedImages.size() > 0) {
            notifyDataSetChanged();
        }
    }

    private Image getImageByPath(String path) {
        if ((mImages != null) && (mImages.size() > 0)) {
            for (Image image : mImages) {
                if (image.path.equalsIgnoreCase(path)) {
                    return image;
                }
            }
        }
        return null;
    }

    public void setData(List<Image> images) {
        this.mSelectedImages.clear();
        if ((images != null) && (images.size() > 0)) {
            this.mImages = images;
        } else {
            this.mImages.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (this.showCamera) {
            return position == 0 ? 0 : 1;
        }
        return 1;
    }

    @Override
    public int getCount() {
        return this.showCamera ? this.mImages.size() + 1 : this.mImages.size();
    }

    @Override
    public Image getItem(int i) {
        if (showCamera) {
            if (i == 0) {
                return null;
            }
            return mImages.get(i - 1);
        }
        return mImages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, final ViewGroup viewGroup) {
        if ((isShowCamera()) && (i == 0)) {
            view = this.mInflater.inflate(R.layout.mis_list_item_camera, viewGroup, false);
            return view;
        }
        ViewHolder holder;
        if (view == null) {
            view = this.mInflater.inflate(R.layout.mis_list_item_image, viewGroup, false);
            holder = new ViewHolder(view);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        if (holder != null) {
            holder.bindData(getItem(i));
            holder.indicator.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        listener.onItemClick(i);
                    }
                }
            });
        }
        return view;
    }

    class ViewHolder {
        ImageView image;
        ImageView indicator;
        View mask;

        ViewHolder(View view) {
            this.image = ((ImageView) view.findViewById(R.id.image));
            this.indicator = ((ImageView) view.findViewById(R.id.checkmark));
            this.mask = view.findViewById(R.id.mask);
            view.setTag(this);
        }

        void bindData(Image data) {
            if (data == null) {
                return;
            }
            if (showSelectIndicator) {
                indicator.setVisibility(View.VISIBLE);
                if (mSelectedImages.contains(data)) {
                    this.indicator.setImageResource(R.drawable.mis_btn_selected);
                    this.mask.setVisibility(View.VISIBLE);
                } else {
                    this.indicator.setImageResource(R.drawable.mis_btn_unselected);
                    this.mask.setVisibility(View.GONE);
                }
            } else {
                this.indicator.setVisibility(View.GONE);
            }
            File imageFile = new File(data.path);
            if (imageFile.exists()) {
                Glide.with(mContext).load(imageFile).placeholder(R.drawable.mis_default_error)
                        .centerCrop().into(this.image);
            } else {
                this.image.setImageResource(R.drawable.mis_default_error);
            }
        }
    }

    interface OnIndicatorClickListener{
        public void onItemClick(int position);
    }

    private OnIndicatorClickListener listener;

    public void setOnIndicatorClickListener(OnIndicatorClickListener listener) {
        this.listener = listener;
    }
}
