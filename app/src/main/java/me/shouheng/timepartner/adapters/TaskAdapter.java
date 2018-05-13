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
import me.shouheng.timepartner.models.entities.task.Task;
import me.shouheng.timepartner.R;
import me.shouheng.timepartner.Constants;
import me.shouheng.timepartner.utils.TpColor;
import me.shouheng.timepartner.utils.TpTime;
import me.shouheng.timepartner.widget.fastscroller.BubbleTextGetter;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> implements BubbleTextGetter {

    private Context mContext;

    private List taskEntities = Collections.emptyList();

    private List<Long> dates = new LinkedList<>();

    private static final String TAG = "ExamAdapter";

    public TaskAdapter(Context mContext){
        this.mContext = mContext;
    }

    /**
     * 为当前的适配器添加实体集合
     * @param taskEntities 实体集合 */
    public void setTaskEntities(List<Task> taskEntities){
        this.taskEntities = new LinkedList();
        dates.clear();
        for (Task taskEntity : taskEntities){
            long lDate = TpTime.getDate(taskEntity.getTaskDate());
            if (!dates.contains(lDate)){
                dates.add(lDate);
                this.taskEntities.add(TpTime.getDate(lDate, TpTime.DATE_TYPE_5));
            }
            this.taskEntities.add(taskEntity);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType){
            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list_item_task, parent, false);
                break;
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list_item_section_title, parent, false);
                break;
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list_item_task, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (getItemViewType(position)){
            case 0:
                String sTitle = (String ) taskEntities.get(position);
                holder.mSectionTitle.setText(sTitle);
                break;
            case 1:
                Task taskEntity = (Task) taskEntities.get(position);
                holder.mNameView.setText(taskEntity.getTaskTitle());
                String strTime = TpTime.getExactTime(
                        taskEntity.getTaskTime() + taskEntity.getTaskDate(), TpTime.EXACT_TIME_TYPE_2);
                holder.mTimeView.setText(strTime);

                long clsId = taskEntity.getClsId();
                ClassDAO classDAO = ClassDAO.getInstance(mContext);
                ClassEntity clsEntity = classDAO.get(clsId);
                classDAO.close();
                int pColor;
                String clsName4 = "--";
                if (clsEntity != null){
                    clsName4 = clsEntity.getClsName();
                    String clsColor4 = clsEntity.getClsColor();
                    if (TextUtils.isEmpty(clsColor4)){
                        clsColor4 = TpColor.COLOR_TASK;
                    }
                    pColor = Color.parseColor(clsColor4);
                } else {
                    pColor = Color.parseColor(TpColor.COLOR_TASK);
                }
                holder.mColorView.setBackgroundColor(pColor);
                holder.mSubjectView.setText(clsName4);
                holder.mSubjectView.setTextColor(pColor);
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (taskEntities != null){
            return taskEntities.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (taskEntities.get(position) instanceof String){
            return 0;
        }
        if (taskEntities.get(position) instanceof Task){
            return 1;
        }
        return -1;
    }

    @Override
    public String getTextToShowInBubble(int pos) {
        if (taskEntities == null || taskEntities.size() == 0)
            return "";
        if (getItemViewType(pos) == 0)
            return "#";
        Character ch = ((Task) taskEntities.get(pos)).getTaskTitle().charAt(0);
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
        TextView mSectionTitle;

        ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mColorView = (ImageView) itemView.findViewById(R.id.color);
            mSubjectView = (TextView) itemView.findViewById(R.id.footer1);
            mNameView = (TextView) itemView.findViewById(R.id.title);
            mTimeView = (TextView) itemView.findViewById(R.id.sub_title);
            mSectionTitle = (TextView) itemView.findViewById(R.id.section);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (getItemViewType() == 1){
                if (listener != null){
                    listener.onClick(v, (Task) taskEntities.get(getAdapterPosition()));
                }
            }
        }
    }

    private OnItemClickListener listener;

    public interface OnItemClickListener{
        void onClick(View v, Task taskEntity);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
}
