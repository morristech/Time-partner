package me.shouheng.timepartner.widget.preference;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import me.shouheng.timepartner.R;

public class BasePreferView extends RelativeLayout implements View.OnClickListener{

    public BasePreferView(Context context) {
        super(context);
        init(context, null);
    }

    public BasePreferView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BasePreferView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs){
        setOnClickListener(this);
        setBackgroundResource(R.drawable.s_rect_black10back);
    }

    @Override
    public void onClick(View v) {
        if (listener != null){
            listener.OnPreferenceClick(this);
        }
    }

    protected OnPreferenceClickListener listener;

    public void setOnPreferenceClickListener(OnPreferenceClickListener listener){
        this.listener = listener;
    }
}
