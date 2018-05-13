package me.shouheng.timepartner.activities.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import ooo.oxo.library.widget.PullBackLayout;
import uk.co.senab.photoview.PhotoView;
import me.shouheng.timepartner.database.dao.base.LocationDAO;
import me.shouheng.timepartner.models.business.intent.Intents;
import me.shouheng.timepartner.R;
import me.shouheng.timepartner.models.entities.location.Location;
import me.shouheng.timepartner.models.entities.picture.Picture;
import me.shouheng.timepartner.models.entities.picture.Pictures;
import me.shouheng.timepartner.utils.TpDisp;
import me.shouheng.timepartner.utils.SystemUiVisibilityUtil;
import me.shouheng.timepartner.utils.TpFile;
import me.shouheng.timepartner.utils.TpLocation;

public class PictureActivity extends AppCompatActivity implements PullBackLayout.Callback{
    private final static String _MULTIPLE = "_MULTIPLE";
    private final static String _SINGLE = "_SINGLE";
    private final static String _SINGLE_EDIT = "_SINGLE_EDIT";

    /**
     * 显示单图或者单图编辑的时候传入的对象 */
    private Picture picture;

    /**
     * 显示多图或者多图编辑时，传入的当前显示的图片 */
    private Picture current;
    /**
     * 显示多图或者多图编辑时，合法的图片的集合（指定路径的文件存在） */
    private List<Picture> legalPictures = new ArrayList<>();

    private ViewPager vpImages;
    private TextView pagerIndex;
    private int numCount;
    private List<ImageView> listImages = new ArrayList<>();
    private PagerTitleStrip pagerTitleStrip;
    private ColorDrawable mBackground;
    private boolean mIsStatusBarHidden;

    public static void startForSingle(Context mContext, Picture picture){
        Intent intent = new Intent(mContext, PictureActivity.class);
        intent.putExtra(Intents.EXTRA_TYPE, _SINGLE);
        intent.putExtra(Intents.EXTRA_PICTURE, picture);
        mContext.startActivity(intent);
    }

    public static void startForMultiple(Context mContext, Pictures pictures, Picture current){
        Intent intent = new Intent(mContext, PictureActivity.class);
        intent.putExtra(Intents.EXTRA_TYPE, _MULTIPLE);
        intent.putExtra(Intents.EXTRA_PICTURES, pictures);
        intent.putExtra(Intents.EXTRA_CURRENT_PICTURE, current);
        mContext.startActivity(intent);
    }

    public static void startForSingleEdit(Context context, Picture picture){
        Intent intent = new Intent(context, PictureActivity.class);
        intent.putExtra(Intents.EXTRA_TYPE, _SINGLE_EDIT);
        intent.putExtra(Intents.EXTRA_PICTURE, picture);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_picture_layout);

        initBackground();

        String openType = getIntent().getStringExtra(Intents.EXTRA_TYPE);
        switch (openType){
            case _SINGLE:
                showSingle();
                break;
            case _MULTIPLE:
                showMultiple();
                break;
            case _SINGLE_EDIT:
                showPictureEdit();
                break;
        }
    }

    private void initBackground() {
        mBackground = new ColorDrawable(Color.BLACK);
        TpDisp.getRootView(this).setBackgroundDrawable(mBackground);
    }

    private void showSingle(){
        Intent intent = getIntent();
        if (!intent.hasExtra(Intents.EXTRA_PICTURE))   return;
        picture = (Picture) intent.getSerializableExtra(Intents.EXTRA_PICTURE);
        String picPath = picture.getPicturePath();
        if (TextUtils.isEmpty(picPath) || !TpFile.isFileExist(picPath)) return;

        ((ViewStub) findViewById(R.id.show_single)).inflate();

        PullBackLayout pullBackLayout = (PullBackLayout) findViewById(R.id.pull_back);
        pullBackLayout.setCallback(this);
        TextView tvTitle = (TextView) findViewById(R.id.title);
        PhotoView ivPhoto = (PhotoView) findViewById(R.id.detail);

        tvTitle.setText(TpFile.getName(picPath));
        Glide.with(this).load(picPath).crossFade()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE).into(ivPhoto);
    }

    private void showPictureEdit(){
        Intent intent = getIntent();
        if (!intent.hasExtra(Intents.EXTRA_PICTURE))   return;
        picture = (Picture) intent.getSerializableExtra(Intents.EXTRA_PICTURE);
        String picPath = picture.getPicturePath();
        if (TextUtils.isEmpty(picPath) || !TpFile.isFileExist(picPath)) return;

        ((ViewStub) findViewById(R.id.show_single_edit)).inflate();

        PullBackLayout pullBackLayout = (PullBackLayout) findViewById(R.id.pull_back);
        pullBackLayout.setCallback(this);
        TextView tvTitle = (TextView) findViewById(R.id.title);
        PhotoView ivPhoto = (PhotoView) findViewById(R.id.detail);

        tvTitle.setText(TpFile.getName(picPath));
        Glide.with(this).load(picPath).crossFade()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE).into(ivPhoto);

        findViewById(R.id.edit_picture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopEditWindow();
            }
        });
    }

    private void showPopEditWindow(){
        LocationDAO locationDAO = LocationDAO.getInstance(this);
        Location location = locationDAO.get(picture.getLocationId());

        View contentView = LayoutInflater.from(this)
                .inflate(R.layout.picture_activity_single_edit_pop_view, null);
        TextView tvLocation = (TextView) contentView.findViewById(R.id.picture_locator);
        if (location != null) {
            tvLocation.setText(location.getProvince() + " " + location.getCity() + " " + location.getDistrict());
        }
        tvLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TpLocation tpLocation = new TpLocation(PictureActivity.this);
                tpLocation.setOnFinishListener(new TpLocation.OnFinishListener() {
                    @Override
                    public void onCompleted(BDLocation location) {
                        TpDisp.showToast(PictureActivity.this, "location got");
                        // TODO 获得并组装位置信息，将该值传递给指定的图片实体
                    }
                });
                tpLocation.start();
            }
        });

        EditText editComment = (EditText) contentView.findViewById(R.id.picture_editor);
        String comment = picture.getComment();
        if (comment != null) {
            editComment.setText(comment);
        }

        Point point = TpDisp.getWindowSize(this);
        PopupWindow popupWindow = new PopupWindow(this);
        popupWindow.setWidth(point.x);
        popupWindow.setHeight(((int)(point.y * 0.5)));
        popupWindow.setContentView(contentView);
        popupWindow.showAsDropDown(findViewById(R.id.footer));
    }

    private void showMultiple(){
        ((ViewStub) findViewById(R.id.show_multiple)).inflate();

        PullBackLayout pullBackLayout = (PullBackLayout) findViewById(R.id.pull_back);
        pullBackLayout.setCallback(this);
        vpImages = (ViewPager) findViewById(R.id.viewPager);
        pagerTitleStrip = (PagerTitleStrip) findViewById(R.id.pagerTitleStrip);
        pagerTitleStrip.setTextColor(Color.WHITE);
        pagerTitleStrip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        pagerIndex = (TextView) findViewById(R.id.pager_index);

        Intent intent = getIntent();
        if (!intent.hasExtra(Intents.EXTRA_PICTURES))   return;
        current = (Picture) intent.getSerializableExtra(Intents.EXTRA_CURRENT_PICTURE);
        if (!intent.hasExtra(Intents.EXTRA_PICTURES))   return;
        showMultiImages((Pictures) intent.getSerializableExtra(Intents.EXTRA_PICTURES));
    }

    private void showMultiImages(Pictures pictures){
        if (pictures == null) return;

        numCount = pictures.getPictures().size();
        if (numCount == 0 || numCount > 9)// 解析出 0 个图片 或者 超出 9 个
            return;
        numCount = 0;

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        int padding = TpDisp.dp2Px(this, 5);

        for (Picture picture : pictures.getPictures()){
            String path = picture.getPicturePath();
            if (TpFile.isFileExist(path)){
                legalPictures.add(picture);
                numCount++;
                PhotoView iv = new PhotoView(this);
                iv.setLayoutParams(params);
                iv.setPadding(padding, padding, padding, padding);
                Glide.with(this).load(path).diskCacheStrategy(DiskCacheStrategy.ALL).crossFade().into(iv);
                listImages.add(iv);
            }
        }

        PagerAdapter adapter = new PagerAdapter() {

            @Override
            public int getCount() {
                return numCount;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return TpFile.getName(legalPictures.get(position).getPicturePath());
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(listImages.get(position));
            }

            @Override
            public Object instantiateItem(View container, int position) {
                ((ViewPager) container).addView(listImages.get(position));
                return listImages.get(position);
            }
        };

        vpImages.setAdapter(adapter);
        final int index = legalPictures.indexOf(current);
        vpImages.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            int selected = index;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                /** 这里的三个参数：
                 * position: 最终的位置，当拖拽最终停下来的时候的位置
                 * positionOffset：偏移的比例，处于[0,1]之间
                 * positionOffsetPixels：偏移的像素 */
                int alpha = (int)(Math.abs(positionOffset - 0.5) / 0.5 * 255);
                alpha = alpha > 255 ? 255 : alpha;
                alpha = alpha < 0 ? 0 : alpha;
                String index = (selected + 1) + " / " + numCount;
                pagerIndex.setText(index);
                pagerIndex.setTextColor(Color.argb(alpha, 255, 255, 255));
            }

            @Override
            public void onPageSelected(int position) {
                selected = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        if (index != -1){
            vpImages.setCurrentItem(index);
        }
    }

    private void hideOrShowStatusBar() {
        if (mIsStatusBarHidden) {
            SystemUiVisibilityUtil.enter(PictureActivity.this);
        } else {
            SystemUiVisibilityUtil.exit(PictureActivity.this);
        }
        mIsStatusBarHidden = !mIsStatusBarHidden;
    }

    @Override
    public void onPullStart() {
        mIsStatusBarHidden = true;
        hideOrShowStatusBar();
    }

    @Override
    public void onPull(float v) {
        v = Math.min(1f, v * 3f);
        int alpha = (int) (0xff * (1f - v));
        mBackground.setAlpha(alpha);
        int pColor = Color.argb(alpha, 0xff, 0xff, 0xff);
        if (pagerTitleStrip != null){
            pagerTitleStrip.setTextColor(pColor);
        }
    }

    @Override
    public void onPullCancel() {
        if (pagerTitleStrip != null){
            pagerTitleStrip.setTextColor(Color.WHITE);
        }
    }

    @Override
    public void onPullComplete() {
        supportFinishAfterTransition();
    }

    @Override
    public void supportFinishAfterTransition() {
        super.supportFinishAfterTransition();
    }
}
