package me.shouheng.timepartner.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;

import de.hdodenhof.circleimageview.CircleImageView;
import me.shouheng.timepartner.R;
import me.shouheng.timepartner.utils.TpColor;
import me.shouheng.timepartner.utils.TpDisp;

public class ColorPickerDialog {
    private Context mContext;
    private Dialog colorDlg;
    private int columns = 5; // 要显示的列数
    private String dlgTitle = ""; // 对话框的标题

    public ColorPickerDialog(Context mContext){
        this.mContext = mContext;
    }

    public ColorPickerDialog setColumns(int columns){
        this.columns = columns;
        return this;
    }

    public ColorPickerDialog setTitle(int titleRes){
        dlgTitle = mContext.getString(titleRes);
        return this;
    }

    public ColorPickerDialog setTitle(String  strTitle){
        dlgTitle = strTitle;
        return this;
    }

    public void show(){
        GridView gridView = new GridView(mContext);
        int padding = TpDisp.dp2Px(mContext, 16);
        gridView.setPadding(padding, padding, padding, padding);
        gridView.setNumColumns(columns);
        gridView.setAdapter(new ColorAdapter());
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listener != null){
                    listener.onSelected(position);
                }
                if (colorDlg != null){
                    colorDlg.dismiss();
                }
            }
        });
        colorDlg = new AlertDialog.Builder(mContext)
                .setView(gridView)
                .setPositiveButton(R.string.com_cancel, null)
                .create();
        if (!TextUtils.isEmpty(dlgTitle)){
            colorDlg.setTitle(dlgTitle);
        }
        colorDlg.show();
    }

    private class ColorAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return TpColor.arrColorRes.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CircleImageView imageView = null;
            if (convertView == null){
                imageView = new CircleImageView(mContext);
                imageView.setImageResource(TpColor.arrColorRes[position]);
                imageView.setMinimumHeight(TpDisp.dp2Px(mContext, 45));
                int padding = TpDisp.dp2Px(mContext, 2);
                imageView.setPadding(padding, padding * 2, padding, padding * 2);
            } else {
                imageView = (CircleImageView) convertView;
            }
            return imageView;
        }
    }

    private OnColorSelectedListener listener;

    public interface OnColorSelectedListener{
        void onSelected(int position);
    }

    public ColorPickerDialog setOnColorSelectedListener(OnColorSelectedListener listener){
        this.listener = listener;
        return this;
    }
}
