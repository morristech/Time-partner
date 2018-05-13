package me.shouheng.timepartner.widget.assignments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import me.shouheng.timepartner.models.entities.assignment.SubAssignment;
import me.shouheng.timepartner.R;
import me.shouheng.timepartner.utils.TpTime;

public class SubAssignEditor{
    private EditText editSubContent;
    private Dialog dlg;

    private Context mContext;

    private SubAssignment subEntity;

    public SubAssignEditor(Context mContext){
        this.mContext = mContext;
    }

    public void showDialog(){
        View view = LayoutInflater.from(mContext).inflate(R.layout.assign_sub_edit_layout, null);
        editSubContent = (EditText) view.findViewById(R.id.sub_content);

        if (subEntity != null){
            editSubContent.setText(subEntity.getSubContent());
        }

        dlg = new AlertDialog.Builder(mContext)
                .setView(view)
                .setTitle(R.string.assign_add_sub)
                .setNegativeButton(R.string.com_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dlg != null && dlg.isShowing()){
                            dlg.dismiss();
                        }
                    }
                })
                .setPositiveButton(R.string.com_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onConfirm();
                    }
                })
                .create();
        dlg.setCancelable(false);
        dlg.show();
    }

    public void setEntity(SubAssignment subEntity){
        this.subEntity = subEntity;
    }

    private void onConfirm(){
        String subContent = editSubContent.getText().toString();

        if (subEntity == null){
            subEntity = new SubAssignment();
            subEntity.setSubCompleted(0);
            subEntity.setSubId(TpTime.getLongId());
        }
        subEntity.setSubContent(subContent);

        if (subEntity == null){
            listener.onConfirm(true, subEntity);
        } else {
            listener.onConfirm(false, subEntity);
        }

        if (dlg != null && dlg.isShowing()){
            dlg.dismiss();
        }
    }

    private OnConfirmListener listener;

    public interface OnConfirmListener{
        void onConfirm(boolean isNew, SubAssignment entity);
    }

    public void setOnConfirmListener(OnConfirmListener listener){
        this.listener = listener;
    }
}
