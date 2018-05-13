package me.shouheng.timepartner.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Collections;
import java.util.List;

import me.shouheng.timepartner.database.dao.base.ClassDAO;
import me.shouheng.timepartner.models.helper.ModelHelper;
import me.shouheng.timepartner.models.business.assignment.AssignmentBO;
import me.shouheng.timepartner.models.business.tpclass.ClassBO;
import me.shouheng.timepartner.models.entities.assignment.Assignment;
import me.shouheng.timepartner.models.entities.tpclass.ClassEntity;
import me.shouheng.timepartner.models.entities.exam.Exam;
import me.shouheng.timepartner.models.business.note.NotePreviewBO;
import me.shouheng.timepartner.models.entities.note.Note;
import me.shouheng.timepartner.models.entities.task.Task;
import me.shouheng.timepartner.R;
import me.shouheng.timepartner.activities.assignment.AssignViewActivity;
import me.shouheng.timepartner.activities.tpclass.ClassViewActivity;
import me.shouheng.timepartner.activities.note.NoteViewer;
import me.shouheng.timepartner.activities.task.TaskViewer;
import me.shouheng.timepartner.utils.TpColor;
import me.shouheng.timepartner.utils.TpFile;
import me.shouheng.timepartner.utils.TpTime;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder>{

    private List searchResults = Collections.emptyList();

    private Context mContext;

    public SearchAdapter(Context context){
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case 1:return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.class_list_item, parent, false));
            case 2:return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.assign_list_item, parent, false));
            case 3:return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.task_list_item_exam, parent, false));
            case 4:return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.task_list_item_task, parent, false));
            case 5:return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.note_list_preview_list_item, parent, false));
            default:
                return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.class_list_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (getItemViewType(position)){
            case 1:
                ClassBO classBO = (ClassBO) searchResults.get(position);
                ClassEntity classEntity = classBO.getClassEntity();

                String mColor = classEntity.getClsColor();
                if (TextUtils.isEmpty(mColor)){
                    mColor = TpColor.COLOR_CLASS;
                }
                int pColor1 = Color.parseColor(mColor);
                holder.mColorView.setBackgroundColor(pColor1);
                holder.mFooterView1.setTextColor(pColor1);
                holder.mFooterView2.setTextColor(pColor1);
                holder.mTitleView.setText(classEntity.getClsName());

                String strNum = classBO.getCount() + " " + mContext.getString(R.string.cls_list_item_detail);
                holder.mSubTitleView.setText(strNum);

                holder.mFooterView1.setText(TpTime.getDate(classEntity.getStartDate(), TpTime.DATE_TYPE_1));
                holder.mFooterView2.setText(TpTime.getDate(classEntity.getEndDate(), TpTime.DATE_TYPE_1));
                break;
            case 2:
                AssignmentBO assignBO = (AssignmentBO) searchResults.get(position);
                Assignment assignEntity = assignBO.getAssignEntity();

                String mColor2 = assignEntity.getAsnColor();
                if (TextUtils.isEmpty(mColor2)){
                    mColor2 = TpColor.COLOR_ASSIGN;
                }
                int pColor2 = Color.parseColor(mColor2);
                holder.mColorView.setBackgroundColor(pColor2);
                holder.mFooterView1.setTextColor(pColor2);
                holder.mFooterView2.setTextColor(pColor2);

                holder.mTitleView.setText(assignEntity.getAsnTitle());

                String strNum2 = assignBO.getCount() + " " + mContext.getString(R.string.asnd_num_tip);
                holder.mSubTitleView.setText(strNum2);

                String strAsnTime = TpTime.getExactTime(assignEntity.getAsnDate() + assignEntity.getAsnTime(),
                        TpTime.EXACT_TIME_TYPE_2);
                holder.mFooterView1.setText(strAsnTime);
                String strProg = assignEntity.getAsnProg() + "%";
                holder.mFooterView2.setText(strProg);
                break;
            case 3:
                Exam examEntity = (Exam) searchResults.get(position);

                holder.mTitleView.setText(examEntity.getExamTitle());
                String strTime = TpTime.getExactTime(
                        examEntity.getExamTime() + examEntity.getExamDate(), TpTime.EXACT_TIME_TYPE_3) + " "
                        +  examEntity.getDuration() + mContext.getString(R.string.com_minute);
                holder.mSubTitleView.setText(strTime);

                long clsId = examEntity.getClsId();
                ClassDAO classDAO = ClassDAO.getInstance(mContext);
                ClassEntity clsEntity = classDAO.get(clsId);
                classDAO.close();
                int pColor3;
                String clsName = "--";
                if (clsEntity != null){
                    clsName = clsEntity.getClsName();
                    String mColor3 = clsEntity.getClsColor();
                    if (TextUtils.isEmpty(mColor3)){
                        mColor3 = TpColor.COLOR_EXAM;
                    }
                    pColor3 = Color.parseColor(mColor3);
                } else {
                    pColor3 = Color.parseColor(TpColor.COLOR_EXAM);
                }
                holder.mColorView.setBackgroundColor(pColor3);
                holder.mFooterView1.setText(clsName);
                holder.mFooterView1.setTextColor(pColor3);
                holder.mFooterView2.setTextColor(pColor3);
                holder.mFooterView3.setTextColor(pColor3);

                String strRoom = examEntity.getExamRoom();
                if (TextUtils.isEmpty(strRoom)) {
                    holder.mFooterView2.setText("--");
                } else {
                    holder.mFooterView2.setText(strRoom);
                }
                String strSeat = examEntity.getExamSeat();
                if (TextUtils.isEmpty(strSeat)){
                    holder.mFooterView3.setText("--");
                } else {
                    holder.mFooterView3.setText(strSeat);
                }
                break;
            case 4:
                Task TPTask = (Task) searchResults.get(position);

                holder.mTitleView.setText(TPTask.getTaskTitle());
                String strTime2 = TpTime.getExactTime(
                        TPTask.getTaskTime() + TPTask.getTaskDate(), TpTime.EXACT_TIME_TYPE_3);
                holder.mSubTitleView.setText(strTime2);

                long clsId2 = TPTask.getClsId();
                ClassDAO classDAO2 = ClassDAO.getInstance(mContext);
                ClassEntity classEntity1 = classDAO2.get(clsId2);
                classDAO2.close();
                int pColor4;
                String clsName4 = "--";
                if (classEntity1 != null){
                    clsName4 = classEntity1.getClsName();
                    String clsColor4 = classEntity1.getClsColor();
                    if (TextUtils.isEmpty(clsColor4)){
                        clsColor4 = TpColor.COLOR_TASK;
                    }
                    pColor4 = Color.parseColor(clsColor4);
                } else {
                    pColor4 = Color.parseColor(TpColor.COLOR_TASK);
                }
                holder.mFooterView1.setText(clsName4);
                holder.mFooterView1.setTextColor(pColor4);
                break;
            case 5:
                Note noteEntity = (Note) searchResults.get(position);
                NotePreviewBO previewEntity = ModelHelper.convertToNotePreview(mContext, noteEntity);

                holder.mTvTC.setText(previewEntity.getTitleAndContent());
                holder.mTvMonth.setText(previewEntity.getMonth());
                holder.mTvDay.setText(previewEntity.getDay());
                holder.mTvLocate.setText(previewEntity.getLocation());

                String imPath = previewEntity.getPreviewImagePath();
                if (TpFile.isFileExist(imPath)){
                    holder.mPicPrev.setVisibility(View.VISIBLE);
                    Glide.with(mContext).load(imPath).crossFade().into(holder.mPicPrev);
                    holder.mTvTotal.setText(previewEntity.getTotal());
                } else {
                    holder.mPicPrev.setVisibility(View.INVISIBLE);
                    holder.mTvTotal.setText("");
                }

                if (previewEntity.isAudioIncluded()){
                    holder.mMediaPrev.setVisibility(View.VISIBLE);
                    holder.mMediaPrev.setImageResource(R.drawable.ic_audio);
                } else {
                    holder.mMediaPrev.setVisibility(View.INVISIBLE);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (null != searchResults){
            return searchResults.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (searchResults.get(position) instanceof ClassBO)
            return 1;
        if (searchResults.get(position) instanceof AssignmentBO)
            return 2;
        if (searchResults.get(position) instanceof Exam)
            return 3;
        if (searchResults.get(position) instanceof Task)
            return 4;
        if (searchResults.get(position) instanceof Note)
            return 5;
        return 0;
    }

    public void updateSearchResults(List searchResults) {
        this.searchResults = searchResults;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        View mView;
        ImageView mColorView;
        TextView mTitleView;
        TextView mSubTitleView;
        TextView mFooterView1;
        TextView mFooterView2;
        TextView mFooterView3;
        ImageView mMediaPrev;
        ImageView mPicPrev;
        TextView mTvMonth;
        TextView mTvDay;
        TextView mTvTotal;
        TextView mTvLocate;
        TextView mTvTC;

        ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mColorView = (ImageView) itemView.findViewById(R.id.color);
            mTitleView = (TextView) itemView.findViewById(R.id.title);
            mSubTitleView = (TextView) itemView.findViewById(R.id.sub_title);
            mFooterView1 = (TextView) itemView.findViewById(R.id.footer1);
            mFooterView2 = (TextView) itemView.findViewById(R.id.footer2);
            mFooterView3 = (TextView) itemView.findViewById(R.id.footer3);
            mMediaPrev = (ImageView) itemView.findViewById(R.id.media_image);
            mPicPrev = (ImageView) itemView.findViewById(R.id.preview_image);
            mTvMonth = (TextView) itemView.findViewById(R.id.month);
            mTvDay = (TextView) itemView.findViewById(R.id.day);
            mTvTotal = (TextView) itemView.findViewById(R.id.total_images);
            mTvLocate = (TextView) itemView.findViewById(R.id.location);
            mTvTC = (TextView) itemView.findViewById(R.id.title_content);
            mView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (getItemViewType()){
                case 1:
                    ClassBO classBO = (ClassBO) searchResults.get(getAdapterPosition());
                    ClassViewActivity.view(mContext, classBO);
                    break;
                case 2:
                    AssignmentBO assignBO = (AssignmentBO) searchResults.get(getAdapterPosition());
                    AssignViewActivity.view(mContext, assignBO);
                    break;
                case 3:
                    Exam examEntity = (Exam) searchResults.get(getAdapterPosition());
                    TaskViewer.view(mContext, examEntity);
                    break;
                case 4:
                    Task taskEntity = (Task) searchResults.get(getAdapterPosition());
                    TaskViewer.view(mContext, taskEntity);
                    break;
                case 5:
                    Note noteEntity = (Note) searchResults.get(getAdapterPosition());
                    NoteViewer.view(mContext, noteEntity.getNoteId());
                    break;
            }
        }
    }
}
