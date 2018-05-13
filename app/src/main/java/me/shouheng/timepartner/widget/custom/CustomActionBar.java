package me.shouheng.timepartner.widget.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import me.shouheng.timepartner.R;

public class CustomActionBar extends RelativeLayout implements View.OnClickListener{
    private TextView tvTitle;

    public CustomActionBar(Context context) {
        super(context);
        init(context, null);
    }

    public CustomActionBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CustomActionBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs){
        LayoutInflater.from(context).inflate(R.layout.view_custom_action_bar, this);
        findViewById(R.id.head_button).setOnClickListener(this);
        findViewById(R.id.back_button).setOnClickListener(this);
        tvTitle = (TextView) findViewById(R.id.title);
        if (attrs == null){
            return;
        }
        // 设置XML属性
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomActionBar );
        final int N = a.getIndexCount();
        String strTitle = "";
        for (int i = 0; i < N; ++i) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.CustomActionBar_action_bar_title:  //获取属性
                    strTitle = a.getString(attr);  //设置属性
                    tvTitle.setText(strTitle);
                    break;
                case R.styleable.CustomActionBar_action_bar_background:
                    int color = a.getColor(attr, getResources().getColor(R.color.colorPrimary));
                    setActionBarColor(color);
                    break;
            }
        }
        //让对象的styleable属性能够反复使用
        a.recycle();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.head_button:
                if (onHeadButtonClickListener != null){
                    onHeadButtonClickListener.onClick();
                }
                break;
            case R.id.back_button:
                if (onBackButtonClickListener != null){
                    onBackButtonClickListener.onClick();
                }
                break;
        }
    }

    public void setActionBarColor(int mColor){
        setBackgroundColor(mColor);
    }

    public void setTitle(String strTitle){
        tvTitle.setText(strTitle);
    }

    public void setTitle(int titleRes){
        tvTitle.setText(titleRes);
    }

    private OnHeadButtonClickListener onHeadButtonClickListener;

    public interface OnHeadButtonClickListener{
        void onClick();
    }

    public void setOnHeadButtonClickListener(OnHeadButtonClickListener listener){
        this.onHeadButtonClickListener = listener;
    }

    private OnBackButtonClickListener onBackButtonClickListener;

    public interface OnBackButtonClickListener{
        void onClick();
    }

    public void setOnBackButtonClickListener(OnBackButtonClickListener listener){
        this.onBackButtonClickListener = listener;
    }
}
