package me.shouheng.timepartner.widget.custom;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.TextView;

public class FadeAppBarLayout extends AppBarLayout
        implements AppBarLayout.OnOffsetChangedListener{
    private Toolbar toolbar;
    private TextView tvTitle;
    private TextView tvSubTitle1;
    private TextView tvSubTitle2;
    private TextView tvSubTitle3;

    public FadeAppBarLayout(Context context) {
        super(context);
        init();
    }

    public FadeAppBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void regFadeToolbar(Toolbar toolbar){
        this.toolbar = toolbar;
        toolbar.setTitleTextColor(Color.argb(0, 255, 255, 255));
        toolbar.setSubtitleTextColor(Color.argb(0, 255, 255, 255));
    }

    public void regFadeTitle(TextView tvTitle){
        this.tvTitle = tvTitle;
    }

    public void regFadeSubtitle(TextView tvSubTitle1){
        this.tvSubTitle1 = tvSubTitle1;
    }

    public void registerTvSubTitle2(TextView tvSubTitle2){
        this.tvSubTitle2 = tvSubTitle2;
    }

    public void registerTvSubTitle3(TextView tvSubTitle3){
        this.tvSubTitle3 = tvSubTitle3;
    }

    private void init(){
        addOnOffsetChangedListener(this);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        // 上升的最大高度
        int maxOffset = getTotalScrollRange() / 2;
        if (maxOffset != 0){
            // 颜色
            int alpha = -verticalOffset * 255 / maxOffset;
            alpha = alpha > 255 ? 255 : alpha;
            // 工具栏
            if (toolbar != null){
                toolbar.setTitleTextColor(Color.argb(alpha, 250, 250, 250));
                toolbar.setSubtitleTextColor(Color.argb(alpha, 250, 250, 250));
            }
            // 标题
            if (tvTitle != null){
                tvTitle.setTextColor(Color.argb(255 - alpha, 250, 250, 250));
            }
            if (tvSubTitle1 != null){
                tvSubTitle1.setTextColor(Color.argb(255 - alpha, 250, 250, 250));
            }
            if (tvSubTitle2 != null){
                tvSubTitle2.setTextColor(Color.argb(255 - alpha, 250, 250, 250));
            }
            if (tvSubTitle3 != null){
                tvSubTitle3.setTextColor(Color.argb(255 - alpha, 250, 250, 250));
            }
        }
    }
}
