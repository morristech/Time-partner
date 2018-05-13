package me.shouheng.timepartner.widget.unit;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import me.shouheng.timepartner.R;
import me.shouheng.timepartner.models.entities.location.Location;

public class NoteLocateUnit extends RelativeLayout implements View.OnClickListener{

    private Context mContext;

    private TextView locateDesc;

    private Location location;

    public NoteLocateUnit(Context context) {
        super(context);
        init(context);
    }

    public NoteLocateUnit(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NoteLocateUnit(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.view_unit_locate, this);
        locateDesc = (TextView) findViewById(R.id.locate_describe);
        findViewById(R.id.locate_header).setOnClickListener(this);
    }

    public void setLocation(Location location){
        this.location = location;
        locateDesc.setText(location.getCountry() + " "
                + location.getProvince() + " "
                + location.getCity() + " "
                + location.getDistrict());
    }

    public Location getLocation(){
        return location;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.locate_header:
                onHeaderClick();
                break;
        }
    }

    private void onHeaderClick(){
        new AlertDialog.Builder(mContext)
                .setTitle(R.string.com_tip)
                .setMessage(R.string.n_locate_delete_msg)
                .setPositiveButton(R.string.com_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null){
                            listener.onDelete();
                        }
                    }
                })
                .setNegativeButton(R.string.com_cancel, null).create().show();
    }

    private onDeleteOptionsListener listener;

    public interface onDeleteOptionsListener {
        void onDelete();
    }

    public void setOnDeleteListener(onDeleteOptionsListener listener){
        this.listener = listener;
    }
}
