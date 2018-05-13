package me.shouheng.timepartner.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import me.shouheng.timepartner.models.business.tpclass.ClassBO;
import me.shouheng.timepartner.models.entities.tpclass.ClassDetail;
import me.shouheng.timepartner.Constants;
import me.shouheng.timepartner.R;
import me.shouheng.timepartner.utils.TpColor;
import me.shouheng.timepartner.utils.TpString;

/**
 * 用于浏览界面课程信息列表 */
public class ClassDetailsAdapter extends RecyclerView.Adapter<ClassDetailsAdapter.ViewHolder> implements View.OnClickListener{

    private Context mContext;

    private ClassBO classBO;;

    private String clsColor = TpColor.COLOR_CLASS;

    public ClassDetailsAdapter(Context mContext){
        this.mContext = mContext;
    }

    public void setClassBO(ClassBO classBO){
        this.classBO = classBO;
        clsColor = classBO.getClassEntity().getClsColor();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.class_viewer_frag_list_detail_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ClassDetail classDetail = classBO.getDetails().get(position);

        String clsTime = classDetail.getStartTime() + " -- " + classDetail.getEndTime();
        holder.tvTime.setText(clsTime);

        String strTeacher = classDetail.getTeacher();

        int pColor = Color.parseColor(clsColor);
        if (TextUtils.isEmpty(strTeacher)){
            holder.tvTeacher.setText(R.string.clsv_add_teacher);
            holder.tvTeacher.setTextColor(pColor);
            holder.tvTeacher.setOnClickListener(this);
        } else {
            holder.tvTeacher.setText(strTeacher);
        }

        String strRoom = classDetail.getRoom();
        if (TextUtils.isEmpty(strTeacher)){
            holder.tvRoom.setText(R.string.clsv_add_room);
            holder.tvRoom.setTextColor(pColor);
            holder.tvRoom.setOnClickListener(this);
        } else {
            holder.tvRoom.setText(strRoom);
        }

        String strWeek = TpString.getWeekString(mContext,
                classDetail.getWeek());
        holder.tvWeek.setTextColor(pColor);
        holder.tvWeek.setText(strWeek);
    }

    @Override
    public int getItemCount() {
        if (classBO != null){
            return classBO.getDetails().size();
        }
        return 0;
    }

    @Override
    public void onClick(View v) {
        if (listener != null){
            listener.onClick();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvTime;
        TextView tvWeek;
        TextView tvTeacher;
        TextView tvRoom;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTime = (TextView) itemView.findViewById(R.id.time);
            tvWeek = (TextView) itemView.findViewById(R.id.week);
            tvTeacher = (TextView) itemView.findViewById(R.id.teacher);
            tvRoom = (TextView) itemView.findViewById(R.id.room);
        }
    }

    private OnEditClickListener listener;

    public interface OnEditClickListener{
        void onClick();
    }

    public void setOnEditClickListener(OnEditClickListener listener){
        this.listener = listener;
    }
}
