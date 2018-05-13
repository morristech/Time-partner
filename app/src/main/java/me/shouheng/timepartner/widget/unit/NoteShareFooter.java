package me.shouheng.timepartner.widget.unit;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.support.v7.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import me.shouheng.timepartner.R;

public class NoteShareFooter extends RelativeLayout implements View.OnClickListener{
    private Context mContext;
    private TextView tvTimeInfo;
    private ImageView ivLike;
    private int liked;

    public NoteShareFooter(Context context) {
        super(context);
        init(context);
    }

    public NoteShareFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NoteShareFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * 初始化
     * @param mContext 上下文 */
    private void init(Context mContext){
        this.mContext = mContext;
        // 设置组件
        LayoutInflater.from(mContext).inflate(R.layout.note_list_content_footer, this);
        tvTimeInfo = (TextView) findViewById(R.id.time_info);
        ivLike = (ImageView) findViewById(R.id.favorite);
        ivLike.setOnClickListener(this);
        findViewById(R.id.footer).setOnClickListener(this);
    }

    /**
     * 设置是假显示
     * @param strTime 详细时间字符串 */
    public void setTime(String strTime){
        tvTimeInfo.setText(strTime);
    }

    public int getLiked() {
        return liked;
    }

    public void setLiked(int liked) {
        this.liked = liked;
        if (liked == 1){
            ivLike.setImageResource(R.drawable.ic_favorite_solid);
        } else {
            ivLike.setImageResource(R.drawable.ic_favorite);
        }
    }

    private void switchLikeState(){
        if (liked == 1){
            setLiked(0);
        } else {
            setLiked(1);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.favorite:
                switchLikeState();
                if (listener != null){
                    listener.onClick(OnShareClickListener._SHARE);
                }
                break;
            case R.id.footer:
                // 弹出菜单
                PopupMenu pop = new PopupMenu(mContext, v);
                pop.inflate(R.menu.note_viewer);
                pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.share:
                                if (listener != null){
                                    listener.onClick(OnShareClickListener._SHARE);
                                }
                                break;
                            case R.id.alarm:
                                if (listener != null){
                                    listener.onClick(OnShareClickListener._ALARM);
                                }
                                break;
                        }
                        return true;
                    }
                });
                pop.show();
                break;
        }
    }

    public interface OnShareClickListener{
        int _SHARE = 0, _ALARM = 1, _LIKE = 2;
        void onClick(int type);
    }

    private OnShareClickListener listener;

    public void setOnShareClickListener(OnShareClickListener listener){
        this.listener = listener;
    }
}
