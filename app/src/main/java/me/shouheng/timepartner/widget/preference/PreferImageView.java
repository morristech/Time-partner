package me.shouheng.timepartner.widget.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import me.shouheng.timepartner.R;

public class PreferImageView extends BasePreferView{

    public PreferImageView(Context context) {
        super(context);
        init(context, null);
    }

    public PreferImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PreferImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs){
        LayoutInflater.from(context).inflate(R.layout.tp_widger_preference_imageview, this);
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

    public void setImageRes(int imgRes){
        ((ImageView)findViewById(R.id.prefer_imageview)).setImageResource(imgRes);
    }

    public ImageView getImageView(){
        return (ImageView)findViewById(R.id.prefer_imageview);
    }
}
