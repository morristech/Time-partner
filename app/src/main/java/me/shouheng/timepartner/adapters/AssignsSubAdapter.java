package me.shouheng.timepartner.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import me.shouheng.timepartner.models.entities.assignment.SubAssignment;
import me.shouheng.timepartner.R;

public class AssignsSubAdapter extends RecyclerView.Adapter<AssignsSubAdapter.ViewHolder> {

    private List<SubAssignment> subAssignEntities;

    public void setSubAssignEntities(List<SubAssignment> subAssignEntities){
        this.subAssignEntities = subAssignEntities;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.assign_viewer_frag_list_sub_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final SubAssignment subEntity = subAssignEntities.get(position);
        String strContent = subEntity.getSubContent();
        holder.tvContent.setText(strContent);
        if (subEntity.getSubCompleted() == 1){
            holder.checkBox.setChecked(true);
        } else {
            holder.checkBox.setChecked(false);
        }
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox) v;
                boolean isChecked = checkBox.isChecked();
                long subId = subEntity.getSubId();
                subEntity.setSubCompleted(isChecked ? 1 : 0);

                if (listener != null){
                    listener.onClick(subId, isChecked);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (subAssignEntities != null){
            return subAssignEntities.size();
        } else {
            return 0;
        }
    }

    private OnCheckboxClickListener listener;

    public interface OnCheckboxClickListener{
        void onClick(long subId, boolean isChecked);
    }

    public void setOnCheckboxClickListener(OnCheckboxClickListener listener){
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvTitle;
        TextView tvTime;
        TextView tvContent;
        CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.sub_title);
            tvTime = (TextView) itemView.findViewById(R.id.sub_time);
            tvContent = (TextView) itemView.findViewById(R.id.sub_content);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
        }
    }
}
