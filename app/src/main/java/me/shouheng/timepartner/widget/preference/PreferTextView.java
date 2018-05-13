package me.shouheng.timepartner.widget.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import me.shouheng.timepartner.R;

public class PreferTextView extends BasePreferView{

    public PreferTextView(Context context) {
        super(context);
        init(context, null);
    }

    public PreferTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PreferTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs){
        LayoutInflater.from(context).inflate(R.layout.tp_widget_preference_text_view, this);
        setOnClickListener(this);
        if (attrs == null)
            return;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BasePreferView );
        final int N = a.getIndexCount();
        for (int i = 0; i < N; ++i) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.BasePreferView_preference_title:
                    setTitle(a.getString(attr));
                    break;
                case R.styleable.BasePreferView_preference_detail:
                    setDetail(a.getString(attr));
                    break;
            }
        }
        a.recycle();
    }

    public void setTitle(String strTitle){
        ((TextView)findViewById(R.id.preference_title)).setText(strTitle);
    }

    public void setTitle(int titleRes){
        ((TextView)findViewById(R.id.preference_title)).setText(titleRes);
    }

    public void setDetail(String strDetail){
        ((TextView)findViewById(R.id.preference_detail)).setText(strDetail);
    }

    public void setDetail(int detailRes){
        ((TextView)findViewById(R.id.preference_detail)).setText(detailRes);
    }
}
