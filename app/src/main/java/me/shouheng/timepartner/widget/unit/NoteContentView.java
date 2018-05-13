package me.shouheng.timepartner.widget.unit;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import me.shouheng.timepartner.R;

public class NoteContentView extends LinearLayout implements View.OnClickListener{
    private TextView tvTitle;
    private TextView tvContent;
    private TextView tvLocation;
    private Context mContext;
    private boolean isContentExpand = false;

    public NoteContentView(Context context) {
        super(context);
        init(context);
    }

    public NoteContentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NoteContentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        this.mContext = context;
        LayoutInflater.from(context).inflate(R.layout.note_list_content_view, this);
        tvTitle = (TextView) findViewById(R.id.note_title);
        tvContent = (TextView) findViewById(R.id.note_content);
        tvLocation = (TextView) findViewById(R.id.note_location);
        tvContent.setOnClickListener(this);
    }

    public void setTitle(String strTitle){
        tvTitle.setText(strTitle);
    }

    public void setContent(String strContent){
        tvContent.setText(strContent);
    }

    public void setLocation(String strLocation){
        tvLocation.setText(strLocation);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.note_content:
                if (isContentExpand){
                    tvContent.setMaxLines(5);
                    isContentExpand = false;
                } else {
                    isContentExpand = true;
                    tvContent.setMaxLines(Integer.MAX_VALUE);
                }
                break;
        }
    }
}
