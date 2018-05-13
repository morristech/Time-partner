package me.shouheng.timepartner.widget.fab;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import me.shouheng.timepartner.R;
import me.shouheng.timepartner.widget.reveal.RevealColorView;

public class TpFloatActionButton extends RelativeLayout implements View.OnClickListener{
    private ArrayList<ImageView> fabList = new ArrayList<>();
    private ArrayList<TextView> tipList = new ArrayList<>();
    private boolean isShowing = false;
    private ImageView fabMenu;
    private ImageView fabMenu1;
    private TextView tvTip1;
    private RevealColorView revealColorView;
    private Point point;
    private FrameLayout frameLayout;

    public TpFloatActionButton(Context context) {
        super(context);
        init(context);
    }

    public TpFloatActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TpFloatActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        // 设置属性
        LayoutInflater.from(context).inflate(R.layout.view_my_float_action_button, this);
        fabMenu = (ImageView) findViewById(R.id.menu);
        fabMenu.setOnClickListener(this);
        revealColorView = (RevealColorView) findViewById(R.id.reveal);
        // 获取自定义浮动按钮
        fabMenu1 = (ImageView) findViewById(R.id.menu1);
        fabMenu1.setOnClickListener(this);
        int[] fabIds = new int[]{R.id.menu2, R.id.menu3, R.id.menu4, R.id.menu5};
        for (int fabId : fabIds){
            ImageView fab = (ImageView) findViewById(fabId);
            fab.setOnClickListener(this);
            fabList.add(fab);
        }
        // 获取浮动按钮的说明
        tvTip1 = (TextView) findViewById(R.id.tip1);
        int[] tipIds = new int[]{R.id.tip2, R.id.tip3, R.id.tip4, R.id.tip5};
        for (int tipId : tipIds){
            TextView tip = (TextView) findViewById(tipId);
            tipList.add(tip);
        }
        // 当前组件的监听事件
        frameLayout = (FrameLayout) findViewById(R.id.frame);
        frameLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowing){
                    hide();
                }
            }
        });
        // 获取屏幕尺寸信息
        WindowManager window = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        point = new Point();
        window.getDefaultDisplay().getRealSize(point);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.menu){
            show();
        } else {
            if (listener != null) {
                int pos = 0; // 按下的位置
                switch (v.getId()){
                    case R.id.menu1:
                        pos = 1;
                        break;
                    case R.id.menu2:
                        pos = 2;
                        break;
                    case R.id.menu3:
                        pos = 3;
                        break;
                    case R.id.menu4:
                        pos = 4;
                        break;
                    case R.id.menu5:
                        pos = 5;
                        break;
                }
                listener.onFabClick(pos);
                hide();
            }
        }
    }

    /**
     * 显示浮动按钮菜单 */
    public void show(){
        isShowing = true;
        // 覆盖屏幕
        frameLayout.setVisibility(VISIBLE);
        revealColorView.reveal(point.x, point.y, Color.argb(200, 255, 255, 255), 0, 300, null);
        // 菜单选择按钮消失
        if (fabMenu != null){
            fabMenu.setVisibility(GONE);
            Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.fab_out);
            fabMenu.startAnimation(anim);
        }
        // Normal菜单显示
        Animation anim1 = AnimationUtils.loadAnimation(getContext(), R.anim.fab_in);
        fabMenu1.startAnimation(anim1);
        fabMenu1.setVisibility(VISIBLE);
        tvTip1.setVisibility(VISIBLE);
        tvTip1.startAnimation(anim1);
        // Mini菜单显示
        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.fab_mini_in);
        long duration = anim.getDuration();
        int len = fabList.size();
        for (int i = 0; i< len; i++){
            // mini菜单
            ImageView fab = fabList.get(i);
            fab.setVisibility(VISIBLE);
            duration += 50; // 显示延时
            anim.setDuration(duration);
            fab.startAnimation(anim);
            // 提示文字
            TextView tip = tipList.get(i);
            tip.setVisibility(VISIBLE);
            tip.startAnimation(anim);
        }
    }

    /**
     * 隐藏菜单 */
    public void hide(){
        isShowing = false;
        // 恢复屏幕
        revealColorView.hide(point.x, point.y, Color.argb(0,0,0,0), 0, 200, null);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                frameLayout.setVisibility(GONE);
            }
        }, 200);
        // 菜单选择按钮显示
        if (fabMenu != null){
            fabMenu.setVisibility(VISIBLE);
            Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.fab_in);
            fabMenu.startAnimation(anim);
        }
        // Normal菜单消失
        Animation anim1 = AnimationUtils.loadAnimation(getContext(), R.anim.fab_out);
        fabMenu1.startAnimation(anim1);
        fabMenu1.setVisibility(GONE);
        tvTip1.setVisibility(GONE);
        tvTip1.startAnimation(anim1);
        // Mini菜单消失
        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.fab_mini_out);
        long duration = anim.getDuration();
        int len = fabList.size();
        for (int i = 0; i< len; i++){
            // mini菜单
            ImageView fab = fabList.get(i);
            fab.setVisibility(GONE);
            duration += 50; // 显示延时
            anim.setDuration(duration);
            fab.startAnimation(anim);
            // 提示文字
            TextView tip = tipList.get(i);
            tip.setVisibility(GONE);
            tip.startAnimation(anim);
        }
    }

    private OnFabClickListener listener;

    public interface OnFabClickListener{
        void onFabClick(int pos);
    }

    public void setOnFabClickListener(OnFabClickListener listener){
        this.listener = listener;
    }
}
