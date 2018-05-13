package me.shouheng.timepartner.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import me.shouheng.timepartner.R;
import me.shouheng.timepartner.utils.TpString;

public class EdtDialog {
    private int dialogType = 101;
    public static final int DIALOG_TYPE_NORMAL = 101;
    public static final int DIALOG_TYPE_DIGITAL = 103;
    private Dialog dlg;
    private AlertDialog.Builder dialog; // 该类管理的对话框
    private EditText editText;  // 该类管理的对话框内的EditText （输入内容）

    public EdtDialog(Context context, String strContent, String strTitle, String strHint){
        this(context, DIALOG_TYPE_NORMAL, strContent, strTitle, strHint);
    }

    public EdtDialog(Context context, int dialogType, String strContent, String strTitle, String strHint){
        dialog = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_edit, null);
        editText = (EditText) view.findViewById(R.id.edit_content);
        editText.setText(strContent);
        TextInputLayout inputLayout = (TextInputLayout) view.findViewById(R.id.text_in_layout);
        inputLayout.setHint(strHint);
        if (!TextUtils.isEmpty(strTitle)){
            dialog.setTitle(strTitle);
        }
        switch (dialogType){
            case DIALOG_TYPE_NORMAL:
                this.dialogType = dialogType;
                break;
            case DIALOG_TYPE_DIGITAL:
                this.dialogType = dialogType;
                break;
        }
        dialog.setView(view);
    }

    public EdtDialog setSingleLine(boolean isSingleLine){
        if (isSingleLine){
            editText.setSingleLine(true);
            editText.setLines(1);
        }
        return this;
    }

    public EdtDialog setPositiveButton(int btnRes, final PositiveButtonClickListener listener){
        positiveListener = listener;
        dialog.setPositiveButton(btnRes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (positiveListener != null){
                    if (dialogType == DIALOG_TYPE_DIGITAL){
                        // 返回解析的数字
                        positiveListener.onClick(
                                TpString.parseNumber(editText.getText().toString()));
                    } else {
                        positiveListener.onClick(editText.getText().toString());
                    }
                }
            }
        });
        return this;
    }

    public EdtDialog setNegativeListener(int btnRes, NegativeButtonClickListener listener){
        negativeListener = listener;
        dialog.setNegativeButton(btnRes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (negativeListener != null){
                    negativeListener.onClick(editText.getText().toString());
                }
            }
        });
        return this;
    }

    public void show(){
        dlg = dialog.create();
        dlg.show();
    }

    public void dismiss(){
        if (dlg != null && dlg.isShowing()){
            dlg.dismiss();
        }
    }

    private PositiveButtonClickListener positiveListener;

    public interface PositiveButtonClickListener{
        void onClick(String content);
    }

    private NegativeButtonClickListener negativeListener;

    public interface NegativeButtonClickListener{
        void onClick(String content);
    }
}
