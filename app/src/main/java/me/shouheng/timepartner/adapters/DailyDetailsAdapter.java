package me.shouheng.timepartner.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import me.shouheng.timepartner.database.dao.bo.ClassBoDAO;
import me.shouheng.timepartner.models.business.assignment.AssignmentBO;
import me.shouheng.timepartner.models.business.tpclass.ClassBO;
import me.shouheng.timepartner.models.business.tpclass.ClassDetailBO;
import me.shouheng.timepartner.models.business.exam.ExamBO;
import me.shouheng.timepartner.models.business.note.NoteBO;
import me.shouheng.timepartner.models.business.task.TaskBO;
import me.shouheng.timepartner.models.entities.assignment.Assignment;
import me.shouheng.timepartner.models.entities.assignment.SubAssignment;
import me.shouheng.timepartner.models.entities.tpclass.ClassDetail;
import me.shouheng.timepartner.models.entities.tpclass.ClassEntity;
import me.shouheng.timepartner.models.entities.collection.CollectionEntity;
import me.shouheng.timepartner.models.entities.exam.Exam;
import me.shouheng.timepartner.models.entities.note.Note;
import me.shouheng.timepartner.models.entities.task.Task;
import me.shouheng.timepartner.R;
import me.shouheng.timepartner.Constants;
import me.shouheng.timepartner.activities.assignment.AssignViewActivity;
import me.shouheng.timepartner.activities.tpclass.ClassViewActivity;
import me.shouheng.timepartner.activities.note.NoteViewer;
import me.shouheng.timepartner.activities.task.TaskViewer;
import me.shouheng.timepartner.utils.TpColor;
import me.shouheng.timepartner.utils.TpTime;

public class DailyDetailsAdapter extends RecyclerView.Adapter<DailyDetailsAdapter.ViewHolder> {

    private Context mContext;

    private List detailList = Collections.emptyList();

    private static final String TAG = "DailyDetailsAdapter_";

    public DailyDetailsAdapter(Context mContext){
        this.mContext = mContext;
    }

    public void addDetails(List list){
        this.detailList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == Constants.TYPE_STRING){
            view = LayoutInflater.from(mContext).inflate(R.layout.dash_list_item_section_title, parent, false);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.dash_main_list_item, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int pColor;
        switch (getItemViewType(position)){
            case Constants.TYPE_STRING:
                holder.tvSection.setText((String) detailList.get(position));
                break;
            case Constants.TYPE_CLASS:
                ClassDetailBO classDetailBO = (ClassDetailBO) detailList.get(position);
                ClassEntity classEntity = classDetailBO.getClassEntity();
                ClassDetail classDetailEntity = classDetailBO.getDetailEntity();
                String clsColor = classEntity.getClsColor();
                String clsName = classEntity.getClsName();
                String clsRoom = classDetailEntity.getRoom();
                String clsTeacher = classDetailEntity.getTeacher();
                String clsTime = classDetailEntity.getStartTime() + "-" + classDetailEntity.getEndTime();
                pColor = Color.parseColor(clsColor);
                holder.ivColor.setBackgroundColor(pColor);
                holder.tvTitle.setText(clsName);
                holder.tvSubtitle.setText(clsRoom);
                holder.tvFooter1.setText(clsTeacher);
                holder.tvFooter2.setText(clsTime);
                holder.tvFooter1.setTextColor(pColor);
                holder.tvFooter2.setTextColor(pColor);
                break;
            case Constants.TYPE_ASSIGN:
                AssignmentBO assignBO = (AssignmentBO) detailList.get(position);
                Assignment assignEntity = assignBO.getAssignEntity();
                List<SubAssignment> subEntities = assignBO.getSubEntities();

                String asnProg = assignEntity.getAsnProg() + "%";
                String asnColor = assignEntity.getAsnColor();
                String asnName = assignEntity.getAsnColor();
                String asnDate = TpTime.getFormatDate(assignEntity.getAsnDate());
                String asnTime = TpTime.getFormatTime(assignEntity.getAsnTime());
                pColor = Color.parseColor(asnColor);

                holder.ivColor.setBackgroundColor(pColor);
                holder.tvTitle.setText(asnName);
                holder.tvSubtitle.setText(asnProg);
                holder.tvFooter1.setText(asnDate);
                holder.tvFooter2.setText(asnTime);
                holder.tvFooter1.setTextColor(pColor);
                holder.tvFooter2.setTextColor(pColor);
                break;
            case Constants.TYPE_TASK:
                TaskBO taskBO = (TaskBO) detailList.get(position);
                Task taskEntity = taskBO.getTaskEntity();
                ClassEntity taskClass = taskBO.getClassEntity();

                String strTaskColor = TpColor.COLOR_TASK;
                if (taskClass != null) {
                    strTaskColor = taskClass.getClsColor();
                    String clsName1 = taskClass.getClsName();
                    holder.tvSubtitle.setText(clsName1);
                }
                pColor = TpColor.parseColor(strTaskColor, TpColor.COLOR_TASK);
                String taskName = taskEntity.getTaskTitle();
                String taskDate = TpTime.getFormatDate(taskEntity.getTaskDate());
                String taskTime = TpTime.getFormatTime(taskEntity.getTaskTime());

                holder.ivColor.setBackgroundColor(pColor);
                holder.tvTitle.setText(taskName);
                holder.tvFooter1.setText(taskDate);
                holder.tvFooter2.setText(taskTime);
                holder.tvFooter1.setTextColor(pColor);
                holder.tvFooter2.setTextColor(pColor);
                break;
            case Constants.TYPE_EXAM:
                ExamBO examBO = (ExamBO) detailList.get(position);
                Exam examEntity = examBO.getExamEntity();
                ClassEntity examClass = examBO.getClassEntity();

                String strExamColor = TpColor.COLOR_EXAM;
                if (examClass != null) {
                    strExamColor = examClass.getClsColor();
                    String examClassName = examClass.getClsName();
                    holder.tvSubtitle.setText(examClassName);
                    holder.tvSubtitle.setText(examClassName);
                }
                pColor = TpColor.parseColor(strExamColor, TpColor.COLOR_EXAM);
                holder.ivColor.setBackgroundColor(pColor);
                String examName = examEntity.getExamTitle();
                String examDate = TpTime.getFormatDate(examEntity.getExamDate());
                String examTime = TpTime.getFormatTime(examEntity.getExamTime());

                holder.tvTitle.setText(examName);
                holder.tvFooter1.setText(examDate);
                holder.tvFooter2.setText(examTime);
                holder.tvFooter1.setTextColor(pColor);
                holder.tvFooter2.setTextColor(pColor);
                break;
            case Constants.TYPE_NOTE:
                NoteBO noteBO = (NoteBO) detailList.get(position);
                Note noteEntity = noteBO.getNote();

                CollectionEntity collectionEntity = noteBO.getCollection();
                String clnColor = collectionEntity.getClnColor();
                String clnNmae = collectionEntity.getClnTitle();
                String noteName = noteEntity.getNoteTitle();
                String noteDate = TpTime.getFormatDate(noteEntity.getNoteDate());
                String noteTime = TpTime.getFormatTime(noteEntity.getNoteTime());
                pColor = Color.parseColor(clnColor);

                holder.ivColor.setBackgroundColor(pColor);
                holder.tvTitle.setText(noteName);
                holder.tvSubtitle.setText(clnNmae);
                holder.tvFooter1.setText(noteDate);
                holder.tvFooter2.setText(noteTime);
                holder.tvFooter1.setTextColor(pColor);
                holder.tvFooter2.setTextColor(pColor);
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (detailList != null){
            return detailList.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        Object item = detailList.get(position);
        if (item instanceof String){
            return Constants.TYPE_STRING;
        } else if (item instanceof ClassDetailBO){
            return Constants.TYPE_CLASS;
        } else if (item instanceof AssignmentBO){
            return Constants.TYPE_ASSIGN;
        } else if (item instanceof TaskBO){
            return Constants.TYPE_TASK;
        } else if (item instanceof ExamBO){
            return Constants.TYPE_EXAM;
        } else if (item instanceof NoteBO){
            return Constants.TYPE_NOTE;
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView ivColor;
        TextView tvTitle;
        TextView tvSubtitle;
        TextView tvFooter1;
        TextView tvFooter2;
        LinearLayout list;
        TextView tvSection;

        ViewHolder(View itemView) {
            super(itemView);
            ivColor = (ImageView) itemView.findViewById(R.id.color);
            tvTitle = (TextView) itemView.findViewById(R.id.main1);
            tvSubtitle = (TextView) itemView.findViewById(R.id.main2);
            tvFooter1 = (TextView) itemView.findViewById(R.id.sub1);
            tvFooter2 = (TextView) itemView.findViewById(R.id.sub2);
            list = (LinearLayout) itemView.findViewById(R.id.list);
            tvSection = (TextView) itemView.findViewById(R.id.section);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int itemType = getItemViewType();
            switch (itemType){
                case Constants.TYPE_STRING:
                    break;
                case Constants.TYPE_CLASS:
                    ClassDetailBO classDetailBO = (ClassDetailBO) detailList.get(getAdapterPosition());
                    ClassEntity classEntity = classDetailBO.getClassEntity();
                    long clsId = classEntity.getClsId();
                    ClassBoDAO classBoDAO = ClassBoDAO.getInstance(mContext);
                    ClassBO classBO = classBoDAO.get(clsId);
                    if (classBO != null){
                        ClassViewActivity.view(mContext, classBO);
                    }
                    break;
                case Constants.TYPE_ASSIGN:
                    AssignmentBO assignBO = (AssignmentBO) detailList.get(getAdapterPosition());
                    AssignViewActivity.view(mContext, assignBO);
                    break;
                case Constants.TYPE_TASK:
                    TaskBO taskBO = (TaskBO) detailList.get(getAdapterPosition());
                    Task taskEntity = taskBO.getTaskEntity();
                    TaskViewer.view(mContext, taskEntity);
                    break;
                case Constants.TYPE_EXAM:
                    ExamBO examBO = (ExamBO) detailList.get(getAdapterPosition());
                    Exam examEntity = examBO.getExamEntity();
                    TaskViewer.view(mContext, examEntity);
                    break;
                case Constants.TYPE_NOTE:
                    NoteBO noteBO = (NoteBO) detailList.get(getAdapterPosition());
                    Note noteEntity = noteBO.getNote();
                    NoteViewer.view(mContext, noteEntity.getNoteId());
                    break;
            }
        }
    }
}
