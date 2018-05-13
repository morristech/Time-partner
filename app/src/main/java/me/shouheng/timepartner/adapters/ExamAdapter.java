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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import me.shouheng.timepartner.database.dao.base.ClassDAO;
import me.shouheng.timepartner.models.entities.tpclass.ClassEntity;
import me.shouheng.timepartner.models.entities.exam.Exam;
import me.shouheng.timepartner.R;
import me.shouheng.timepartner.Constants;
import me.shouheng.timepartner.utils.TpColor;
import me.shouheng.timepartner.utils.TpTime;
import me.shouheng.timepartner.widget.fastscroller.BubbleTextGetter;

public class ExamAdapter extends RecyclerView.Adapter<ExamAdapter.ViewHolder> implements BubbleTextGetter{

    private Context mContext;

    private List examEntities = Collections.emptyList();

    private List<Long> dates = new LinkedList<>();

    public ExamAdapter(Context mContext){
        this.mContext = mContext;
    }

    public void setExamEntities(List<Exam> examEntities){
        this.examEntities = new LinkedList();
        dates.clear();
        for (Exam examEntity : examEntities){
            long lDate = TpTime.getDate(examEntity. getExamDate());
            if (!dates.contains(lDate)){
                dates.add(lDate);
                this.examEntities.add(TpTime.getDate(lDate, TpTime.DATE_TYPE_5));
            }
            this.examEntities.add(examEntity);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType){
            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list_item_exam, parent, false);
                break;
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list_item_section_title, parent, false);
                break;
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list_item_exam, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (getItemViewType(position)){
            case 0:
                String sTitle = (String ) examEntities.get(position);
                holder.mSectionTitle.setText(sTitle);
                break;
            case 1:
                Exam examEntity = (Exam) examEntities.get(position);
                holder.mNameView.setText(examEntity.getExamTitle());
                String strTime = TpTime.getExactTime(
                        examEntity.getExamTime() + examEntity.getExamDate(), TpTime.EXACT_TIME_TYPE_2) + " "
                        +  examEntity.getDuration() + mContext.getString(R.string.com_minute);
                holder.mTimeView.setText(strTime);

                long clsId = examEntity.getClsId();
                ClassDAO classDAO = ClassDAO.getInstance(mContext);
                ClassEntity clsEntity = classDAO.get(clsId);
                classDAO.close();
                int pColor;
                String clsName = "--";
                if (clsEntity != null){
                    clsName = clsEntity.getClsName();
                    String mColor3 = clsEntity.getClsColor();
                    if (TextUtils.isEmpty(mColor3)){
                        mColor3 = TpColor.COLOR_EXAM;
                    }
                    pColor = Color.parseColor(mColor3);
                } else {
                    pColor = Color.parseColor(TpColor.COLOR_EXAM);
                }
                holder.mColorView.setBackgroundColor(pColor);
                holder.mSubjectView.setText(clsName);
                holder.mSubjectView.setTextColor(pColor);
                holder.mRoomView.setTextColor(pColor);
                holder.mSeatView.setTextColor(pColor);

                String strRoom = examEntity.getExamRoom();
                if (TextUtils.isEmpty(strRoom)) {
                    holder.mRoomView.setText("--");
                } else {
                    holder.mRoomView.setText(strRoom);
                }
                String strSeat = examEntity.getExamSeat();
                if (TextUtils.isEmpty(strSeat)){
                    holder.mSeatView.setText("--");
                } else {
                    holder.mSeatView.setText(strSeat);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (examEntities != null){
            return examEntities.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (examEntities.get(position) instanceof String){
            return 0;
        }
        if (examEntities.get(position) instanceof Exam){
            return 1;
        }
        return -1;
    }

    @Override
    public String getTextToShowInBubble(int pos) {
        if (examEntities == null || examEntities.size() == 0)
            return "";
        if (getItemViewType(pos) == 0)
            return "#";
        Character ch = ((Exam) examEntities.get(pos)).getExamTitle().charAt(0);
        if (Character.isDigit(ch)) {
            return "#"; // 如果是数字的话，则返回#
        } else
            return Character.toString(ch);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        View mView;
        ImageView mColorView;
        TextView mNameView;
        TextView mTimeView;
        TextView mSubjectView;
        TextView mRoomView;
        TextView mSeatView;
        TextView mSectionTitle;

        ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mColorView = (ImageView) itemView.findViewById(R.id.color);
            mNameView = (TextView) itemView.findViewById(R.id.title);
            mTimeView = (TextView) itemView.findViewById(R.id.sub_title);
            mSubjectView = (TextView) itemView.findViewById(R.id.footer1);
            mRoomView = (TextView) itemView.findViewById(R.id.footer2);
            mSeatView = (TextView) itemView.findViewById(R.id.footer3);
            mSectionTitle = (TextView) itemView.findViewById(R.id.section);

            mView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (getItemViewType() == 1){
                if (listener != null){
                    listener.onClick(v, (Exam) examEntities.get(getAdapterPosition()));
                }
            }
        }
    }

    private OnItemClickListener listener;

    public interface OnItemClickListener{
        void onClick(View v, Exam examEntity);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
}
