package me.shouheng.timepartner.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import me.shouheng.timepartner.models.Model;
import me.shouheng.timepartner.models.business.assignment.AssignmentBO;
import me.shouheng.timepartner.models.business.tpclass.ClassBO;
import me.shouheng.timepartner.models.entities.assignment.Assignment;
import me.shouheng.timepartner.models.entities.tpclass.ClassEntity;
import me.shouheng.timepartner.models.entities.exam.Exam;
import me.shouheng.timepartner.models.entities.note.Note;
import me.shouheng.timepartner.models.entities.task.Task;
import me.shouheng.timepartner.R;
import me.shouheng.timepartner.Constants;
import me.shouheng.timepartner.utils.TpTime;
import me.shouheng.timepartner.widget.fastscroller.BubbleTextGetter;

public class TrashAdapter extends RecyclerView.Adapter<TrashAdapter.ViewHolder> implements BubbleTextGetter {

    private List trashEntities = Collections.emptyList();

    private static final String TAG = "TrashAdapter__";

    private Context mContext;

    public void setTrashEntities(List trashEntities){
        this.trashEntities = trashEntities;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view;
        if (viewType == Constants.TYPE_STRING){
            view = LayoutInflater.from(mContext).inflate(R.layout.trash_list_item_section_title, parent, false);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.trash_list_item, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int pColor;
        switch (getItemViewType(position)){
            case Constants.TYPE_CLASS:
                ClassBO classBO = (ClassBO) trashEntities.get(position);
                ClassEntity classEntity = classBO.getClassEntity();

                pColor = mContext.getResources().getColor(R.color.class_prime);
                try {
                    pColor = Color.parseColor(classEntity.getClsColor());
                } catch (Exception e){
                    Log.d(TAG, "onBindViewHolder: " + e.toString());
                } finally {
                    holder.tvTitle.setText(classEntity.getClsName());
                    String strSubTitle = classBO.getCount() + mContext.getString(R.string.cls_list_item_detail);
                    holder.tvSubtitle.setText(strSubTitle);
                    holder.tvFooter1.setText(TpTime.getDate(classEntity.getStartDate(), TpTime.DATE_TYPE_1));
                    holder.tvFooter2.setText(TpTime.getDate(classEntity.getEndDate(), TpTime.DATE_TYPE_1));
                    holder.tvFooter1.setTextColor(pColor);
                    holder.tvFooter2.setTextColor(pColor);
                    holder.ivColor.setBackgroundColor(pColor);
                }
                break;
            case Constants.TYPE_ASSIGN:
                AssignmentBO assignBO = (AssignmentBO) trashEntities.get(position);
                Assignment assignEntity = assignBO.getAssignEntity();

                pColor = mContext.getResources().getColor(R.color.assign_prime);
                try {
                    pColor = Color.parseColor(assignEntity.getAsnColor());
                } catch (Exception e){
                    Log.d(TAG, "onBindViewHolder: " + e.toString());
                } finally {
                    holder.tvTitle.setText(assignEntity.getAsnTitle());
                    String strSubTitle = assignBO.getCount() + mContext.getString(R.string.assign_num_appendix);
                    holder.tvSubtitle.setText(strSubTitle);
                    holder.tvFooter1.setText(TpTime.getDate(assignEntity.getAsnDate(), TpTime.DATE_TYPE_1));
                    holder.tvFooter2.setText(TpTime.getTime(assignEntity.getAsnTime()));
                    holder.tvFooter1.setTextColor(pColor);
                    holder.tvFooter2.setTextColor(pColor);
                    holder.ivColor.setBackgroundColor(pColor);
                }
                break;
            case Constants.TYPE_EXAM:
                Exam examEntity = (Exam) trashEntities.get(position);
                pColor = mContext.getResources().getColor(R.color.task_prime);
                try {
                    //pColor = Color.parseColor(examEntity.getAsnColor());
                } catch (Exception e){
                    Log.d(TAG, "onBindViewHolder: " + e.toString());
                } finally {// 为组件添加值
                    holder.tvTitle.setText(examEntity.getExamTitle());
                    //String strSubTitle = examEntity.getSubsNum()
                    //        + mContext.getString(R.string.assign_num_appendix);
                    //holder.tvSubtitle.setText(strSubTitle);
                    holder.tvFooter1.setText(TpTime.getDate(examEntity.getExamDate(), TpTime.DATE_TYPE_1));
                    holder.tvFooter2.setText(TpTime.getTime(examEntity.getExamTime()));
                    holder.tvFooter1.setTextColor(pColor);
                    holder.tvFooter2.setTextColor(pColor);
                    holder.ivColor.setBackgroundColor(pColor);
                }
                break;
            case Constants.TYPE_TASK:
                Task taskEntity = (Task) trashEntities.get(position);
                pColor = mContext.getResources().getColor(R.color.task_prime);
                try {
                    //pColor = Color.parseColor(taskEntity.getAccount());
                } catch (Exception e){
                    Log.d(TAG, "onBindViewHolder: " + e.toString());
                } finally {// 为组件添加值
                    holder.tvTitle.setText(taskEntity.getTaskTitle());
                    //String strSubTitle = assignEntity.getSubsNum()
                    //        + mContext.getString(R.string.assign_num_appendix);
                    //holder.tvSubtitle.setText(strSubTitle);
                    holder.tvFooter1.setText(TpTime.getDate(taskEntity.getTaskDate(), TpTime.DATE_TYPE_1));
                    holder.tvFooter2.setText(TpTime.getTime(taskEntity.getTaskTime()));
                    holder.tvFooter1.setTextColor(pColor);
                    holder.tvFooter2.setTextColor(pColor);
                    holder.ivColor.setBackgroundColor(pColor);
                }
                break;
            case Constants.TYPE_NOTE:
                Note noteEntity = (Note) trashEntities.get(position);
                pColor = mContext.getResources().getColor(R.color.note_prime);
                try {
                    //pColor = Color.parseColor(noteEntity.getClnColor());
                } catch (Exception e){
                    Log.d(TAG, "onBindViewHolder: " + e.toString());
                } finally {// 为组件添加值
                    //long cln = noteEntity.getClnId();
                    holder.tvTitle.setText(noteEntity.getNoteTitle());
                    //holder.tvSubtitle.setText(strSubTitle);
                    holder.tvFooter1.setText(TpTime.getDate(noteEntity.getNoteDate(), TpTime.DATE_TYPE_1));
                    holder.tvFooter2.setText(TpTime.getTime(noteEntity.getNoteTime()));
                    holder.tvFooter1.setTextColor(pColor);
                    holder.tvFooter2.setTextColor(pColor);
                    holder.ivColor.setBackgroundColor(pColor);
                }
                break;
            case Constants.TYPE_STRING:
                holder.tvSection.setText((String) trashEntities.get(position));
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (null != trashEntities){
            return trashEntities.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (trashEntities.get(position) instanceof ClassBO){
            return Constants.TYPE_CLASS;
        } else if (trashEntities.get(position) instanceof AssignmentBO){
            return Constants.TYPE_ASSIGN;
        } else if (trashEntities.get(position) instanceof Task){
            return Constants.TYPE_TASK;
        } else if (trashEntities.get(position) instanceof Exam){
            return Constants.TYPE_EXAM;
        } else if (trashEntities.get(position) instanceof Note){
            return Constants.TYPE_NOTE;
        }
        return Constants.TYPE_STRING;
    }

    @Override
    public String getTextToShowInBubble(int pos) {
        Character ch;
        switch (getItemViewType(pos)){
            case Constants.TYPE_CLASS:
                ClassBO classBO = (ClassBO) trashEntities.get(pos);
                ClassEntity classEntity = classBO.getClassEntity();

                ch = classEntity.getClsName().charAt(0);
                if (Character.isDigit(ch)) {
                    return "#"; // 如果是数字的话，则返回#
                } else{
                    return Character.toString(ch);
                }
            case Constants.TYPE_ASSIGN:
                AssignmentBO assignBO = (AssignmentBO) trashEntities.get(pos);
                Assignment assignEntity = assignBO.getAssignEntity();

                ch = assignEntity.getAsnTitle().charAt(0);
                if (Character.isDigit(ch)) {
                    return "#"; // 如果是数字的话，则返回#
                } else{
                    return Character.toString(ch);
                }
            case Constants.TYPE_EXAM:
                Exam examEntity = (Exam) trashEntities.get(pos);
                ch = examEntity.getExamTitle().charAt(0);
                if (Character.isDigit(ch)) {
                    return "#"; // 如果是数字的话，则返回#
                } else{
                    return Character.toString(ch);
                }
            case Constants.TYPE_TASK:
                Task taskEntity = (Task) trashEntities.get(pos);
                ch = taskEntity.getTaskTitle().charAt(0);
                if (Character.isDigit(ch)) {
                    return "#"; // 如果是数字的话，则返回#
                } else{
                    return Character.toString(ch);
                }
            case Constants.TYPE_NOTE:
                Note noteEntity = (Note) trashEntities.get(pos);
                ch = noteEntity.getNoteTitle().charAt(0);
                if (Character.isDigit(ch)) {
                    return "#"; // 如果是数字的话，则返回#
                } else{
                    return Character.toString(ch);
                }
            case Constants.TYPE_STRING:
                String str = (String) trashEntities.get(pos);
                ch = str.charAt(0);
                if (Character.isDigit(ch)) {
                    return "#"; // 如果是数字的话，则返回#
                } else{
                    return Character.toString(ch);
                }
        }
        return "";
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        View view;
        ImageView ivColor;
        TextView tvTitle;
        TextView tvSubtitle;
        TextView tvFooter1;
        TextView tvFooter2;
        TextView tvSection;

        ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            ivColor = (ImageView) itemView.findViewById(R.id.color);
            tvTitle = (TextView) itemView.findViewById(R.id.main1);
            tvSubtitle = (TextView) itemView.findViewById(R.id.main2);
            tvFooter1 = (TextView) itemView.findViewById(R.id.sub1);
            tvFooter2 = (TextView) itemView.findViewById(R.id.sub2);
            tvSection = (TextView) itemView.findViewById(R.id.section_title);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (getItemViewType()){
                case Constants.TYPE_CLASS:
                    if (listener != null)
                        listener.onItemSelected(v, (ClassBO) trashEntities.get(getAdapterPosition()));
                    break;
                case Constants.TYPE_ASSIGN:
                    if (listener != null)
                        listener.onItemSelected(v, (AssignmentBO) trashEntities.get(getAdapterPosition()));
                    break;
                case Constants.TYPE_EXAM:
                    if (listener != null)
                        listener.onItemSelected(v, (Exam) trashEntities.get(getAdapterPosition()));
                    break;
                case Constants.TYPE_NOTE:
                    if (listener != null)
                        listener.onItemSelected(v, (Note) trashEntities.get(getAdapterPosition()));
                    break;
                case Constants.TYPE_TASK:
                    if (listener != null)
                        listener.onItemSelected(v, (Task) trashEntities.get(getAdapterPosition()));
                    break;
            }
        }
    }

    private OnItemSelectedListener listener;

    public interface OnItemSelectedListener{
        void onItemSelected(View view, Model entity);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener){
        this.listener = listener;
    }
}
