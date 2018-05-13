package me.shouheng.timepartner.widget.custom;

import android.animation.Animator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import me.shouheng.timepartner.R;
import me.shouheng.timepartner.widget.reveal.RevealColorView;

public class TPRippleButton extends FrameLayout implements View.OnTouchListener{
    private TextView textView;
    private RevealColorView reveal;
    private Point lastTouchedPoint;
    private int daleyMillis = 800;
    private int animMillis = 400;
    private int bgColor = Color.TRANSPARENT, rippleColor = Color.LTGRAY;

    public TPRippleButton(Context context) {
        super(context);
        init(context, null);
    }

    public TPRippleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TPRippleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs){
        LayoutInflater.from(context).inflate(R.layout.widget_tp_ripple_button, this);
        textView = (TextView) findViewById(R.id.text);
        reveal = (RevealColorView) findViewById(R.id.reveal);
        textView.setOnTouchListener(this);
        if (attrs == null){
            return;
        }
        // 设置XML属性
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TPRippleButton );
        final int N = a.getIndexCount();
        for (int i = 0; i < N; ++i) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.TPRippleButton_tprAnimMillis:
                    animMillis = a.getInt(attr, 400);
                    break;
                case R.styleable.TPRippleButton_tprDelayMillis:
                    daleyMillis = a.getInt(attr, 400);
                    break;
                case R.styleable.TPRippleButton_tprBackColor:
                    bgColor = a.getColor(attr, Color.TRANSPARENT);
                    break;
                case R.styleable.TPRippleButton_tprRippleColor:
                    rippleColor = a.getColor(attr, Color.LTGRAY);
                    break;
                case R.styleable.TPRippleButton_tprTitle:
                    String title = a.getString(attr);
                    textView.setText(title);
                    break;
                case R.styleable.TPRippleButton_tprTitleSize:
                    float dm = a.getDimension(attr, 12);
                    textView.setText((int) dm);
                    break;
                case R.styleable.TPRippleButton_tprTitleColor:
                    int textColor = a.getColor(attr, Color.TRANSPARENT);
                    textView.setTextColor(textColor);
                    break;
            }
        }
        //让对象的styleable属性能够反复使用
        a.recycle();
    }

    @Override
    public boolean onTouch(final View v, MotionEvent event) {
        lastTouchedPoint = new Point();
        lastTouchedPoint.x = (int) event.getX();
        lastTouchedPoint.y = (int) event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(!deviceHaveRippleSupport()) {
                    textView.setBackgroundColor(bgColor);
                } else {
                    reveal.reveal(lastTouchedPoint.x, lastTouchedPoint.y, rippleColor, 0, animMillis, new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {}

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            reveal.reveal(lastTouchedPoint.x, lastTouchedPoint.y, bgColor, 0, animMillis, null);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {}

                        @Override
                        public void onAnimationRepeat(Animator animation) {}
                    });
                }
                return true;
            case MotionEvent.ACTION_UP:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (listener != null){
                            listener.onClick(v);
                        }
                    }
                }, 900);
                return true;
        }
        return false;
    }

    private OnRippleClickListener listener;

    public void setOnRippleClickListener(OnRippleClickListener listener){
        this.listener = listener;
    }

    public interface OnRippleClickListener{
        void onClick(View v);
    }

    private boolean deviceHaveRippleSupport() {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }
}
