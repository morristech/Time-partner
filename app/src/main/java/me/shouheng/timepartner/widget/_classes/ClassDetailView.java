package me.shouheng.timepartner.widget._classes;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import me.shouheng.timepartner.models.entities.tpclass.ClassDetail;
import me.shouheng.timepartner.R;
import me.shouheng.timepartner.utils.TpString;
import me.shouheng.timepartner.utils.TpTime;

/**
 * 用于显示课程信息
 * 需要调用setEntity()方法来为该组件绑定值（不设置就没有值显示）
 * setColor()和updateColor()主要用于设置和更新当前界面的颜色（需要从外部传入）*/
public class ClassDetailView extends CardView implements View.OnClickListener{
    private Context mContext;

    private int pColor;

    private ClassDetail entity;

    public ClassDetailView(Context context) {
        super(context);
        init(context);
    }

    public ClassDetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ClassDetailView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mContext = context;
        pColor = Color.parseColor("#2E7D32");
        LayoutInflater.from(context).inflate(R.layout.class_edit_detail_list_item, this);
    }

    public void setEntity(ClassDetail entity){
        this.entity = entity;
        updateByEntity(entity);
    }

    private void updateByEntity(ClassDetail entity){
        TextView tvStartTime = (TextView) findViewById(R.id.detail_start_time);
        TextView tvEndTime = (TextView) findViewById(R.id.detail_end_time);
        TextView tvTeacher = (TextView) findViewById(R.id.detail_teacher);
        TextView tvRoom = (TextView) findViewById(R.id.detail_room);
        TextView tvWeek = (TextView) findViewById(R.id.detail_week);

        String strWeek = entity.getWeek(); // 还没有解析过的星期字符串
        String strTeacher = entity.getTeacher();
        String strRoom = entity.getRoom();
        int startTime = entity.getStartTime();
        int endTime = entity.getEndTime();

        tvStartTime.setText(TpTime.getFormatTime(startTime));
        tvEndTime.setText(TpTime.getFormatTime(endTime));
        tvTeacher.setText(strTeacher);
        tvRoom.setText(strRoom);
        tvWeek.setText(TpString.getWeekString(mContext, strWeek)); // 使用解析之后的星期字符串
        tvWeek.setTag(strWeek);
        tvWeek.setTextColor(pColor);

        ImageView footer = (ImageView) findViewById(R.id.detail_footer);
        footer.setOnClickListener(this);
    }

    public ClassDetail getEntity(){
        return entity;
    }

    public void setColor(String strColor){
        pColor = Color.parseColor(strColor);
        ((TextView) findViewById(R.id.detail_week)).setTextColor(pColor);
    }

    public void setColor(int colorRes){
        pColor = colorRes;
        ((TextView) findViewById(R.id.detail_week)).setTextColor(colorRes);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.detail_footer:
                Dialog dlg = new AlertDialog.Builder(getContext())
                        .setTitle(R.string.class_options)
                        .setItems(R.array.footer_options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case 0:
                                        if (editListener != null){
                                            editListener.onEditClick();
                                        }
                                        break;
                                    case 1:
                                        if (deleteListener != null){
                                            deleteListener.onDeleteClick();
                                        }
                                        break;
                                }
                            }
                        })
                        .create();
                dlg.show();
                break;
        }
    }

    private OnEditListener editListener;

    public interface OnEditListener{
        void onEditClick();
    }

    public void setEditListener(OnEditListener editListener){
        this.editListener = editListener;
    }

    private OnDeleteListener deleteListener;

    public interface OnDeleteListener{
        void onDeleteClick();
    }

    public void setDeleteListener(OnDeleteListener deleteListener){
        this.deleteListener = deleteListener;
    }
}
