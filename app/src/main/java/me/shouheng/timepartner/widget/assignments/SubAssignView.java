package me.shouheng.timepartner.widget.assignments;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import me.shouheng.timepartner.models.entities.assignment.SubAssignment;
import me.shouheng.timepartner.R;

public class SubAssignView extends CardView implements View.OnClickListener{
    private int mColor;

    private SubAssignment subEntity;

    public SubAssignView(Context context) {
        super(context);
        init(context);
    }

    public SubAssignView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SubAssignView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        LayoutInflater.from(context).inflate(R.layout.assign_edit_sub_list_item, this);
    }

    public void setSubEntity(SubAssignment subEntity){
        this.subEntity = subEntity;
        updateByEntity(subEntity);
    }

    public SubAssignment getSubEntity(){
        return subEntity;
    }

    private void updateByEntity(SubAssignment subEntity){
        if (subEntity != null){
            TextView tvSubContent = (TextView) findViewById(R.id.sub_content);
            String subContent = subEntity.getSubContent();
            tvSubContent.setText(subContent);
            findViewById(R.id.sub_footer).setOnClickListener(this);
        }
    }

    public void setColor(String color){
        mColor = Color.parseColor(color);
        ((TextView) findViewById(R.id.text4)).setTextColor(mColor);
    }

    public void setColor(int color){
        mColor = color;
        ((TextView) findViewById(R.id.text4)).setTextColor(mColor);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sub_footer:
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
        };
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
