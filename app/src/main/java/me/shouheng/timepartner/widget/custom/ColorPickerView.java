package me.shouheng.timepartner.widget.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import de.hdodenhof.circleimageview.CircleImageView;
import me.shouheng.timepartner.R;
import me.shouheng.timepartner.utils.TpColor;
import me.shouheng.timepartner.widget.dialog.ColorPickerDialog;

public class ColorPickerView extends CircleImageView implements View.OnClickListener{
    private Context mContext;
    private String dlgTitle = "";

    public ColorPickerView(Context context) {
        super(context);
        init(context, null);
    }

    public ColorPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ColorPickerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs){
        mContext = context;
        setOnClickListener(this);
        if (attrs == null)
            return;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorPickerView );
        final int N = a.getIndexCount();
        for (int i = 0; i < N; ++i) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.ColorPickerView_dialog_title:  //获取属性
                    dlgTitle = a.getString(attr);  //设置属性
                    break;
            }
        }
        a.recycle();
    }

    @Override
    public void onClick(View v) {
        new ColorPickerDialog(mContext)
                .setTitle(dlgTitle).setOnColorSelectedListener(new ColorPickerDialog.OnColorSelectedListener() {
            @Override
            public void onSelected(int position) {
                String strColor = TpColor.getColorString(position);
                int resColor = TpColor.getColorResource(position);
                setImageResource(resColor);
                if (listener != null){
                    listener.onColorSelected(resColor, strColor);
                }
            }
        }).show();
    }

    private OnColorSelectedListener listener;

    public interface OnColorSelectedListener{
        void onColorSelected(int resColor, String strColor);
    }

    public void setOnColorSelectedListener(OnColorSelectedListener listener){
        this.listener = listener;
    }
}
