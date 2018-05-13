package me.shouheng.timepartner.fragments.task;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.Serializable;

import me.shouheng.timepartner.database.dao.base.ClassDAO;
import me.shouheng.timepartner.models.business.intent.Intents;
import me.shouheng.timepartner.models.entities.Entity;
import me.shouheng.timepartner.models.entities.tpclass.ClassEntity;
import me.shouheng.timepartner.models.entities.exam.Exam;
import me.shouheng.timepartner.models.entities.task.Task;
import me.shouheng.timepartner.R;
import me.shouheng.timepartner.Constants;
import me.shouheng.timepartner.activities.task.TaskEdit;
import me.shouheng.timepartner.utils.TpColor;
import me.shouheng.timepartner.utils.TpTime;

public class ViewerFragment extends Fragment implements View.OnClickListener{

    private String mColor = TpColor.COLOR_TASK;

    private boolean isExam = false;

    private Exam examEntity;

    private Task taskEntity;

    public static <T extends Entity> ViewerFragment getInstance(T t){
        ViewerFragment fragment = new ViewerFragment();
        Bundle bundle = new Bundle();
        if (t != null){
            bundle.putSerializable(Intents.EXTRA_ENTITY, t);
        }
        fragment.setArguments(bundle);
        return fragment;
    }

    private ViewerFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle.containsKey(Intents.EXTRA_ENTITY)){
            Serializable serializable = bundle.getSerializable(Intents.EXTRA_ENTITY);
            if (serializable instanceof Exam){
                examEntity = (Exam) serializable;
                isExam = true;
            } else if (serializable instanceof Task){
                taskEntity = (Task) serializable;
                isExam = false;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (isExam){
            View rootView = inflater.inflate(R.layout.task_viewer_frag_exam, container, false);

            String strTime = TpTime.getExactTime(examEntity.getExamTime() + examEntity.getExamDate(),
                    TpTime.EXACT_TIME_TYPE_1);
            String strDuration = examEntity.getDuration()
                    + getContext().getString(R.string.com_minute);
            ((TextView) rootView.findViewById(R.id.time)).setText(strTime);
            ((TextView) rootView.findViewById(R.id.duration)).setText(strDuration);

            if (!TextUtils.isEmpty(examEntity.getExamRoom())){
                ((TextView) rootView.findViewById(R.id.room)).setText(examEntity.getExamRoom());
            }

            if (!TextUtils.isEmpty(examEntity.getExamSeat())){
                ((TextView) rootView.findViewById(R.id.seat)).setText(examEntity.getExamSeat());
            }

            long clsId = examEntity.getClsId();
            ClassDAO classDAO = ClassDAO.getInstance(getContext());
            ClassEntity classEntity = classDAO.get(clsId);
            classDAO.close();
            if (classEntity != null) {
                mColor = classEntity.getClsColor();
            }
            int pColor = Color.parseColor(mColor);

            TextView tvContentTip = (TextView) rootView.findViewById(R.id.content_empty_tip);
            tvContentTip.setTextColor(pColor);
            tvContentTip.setOnClickListener(this);
            String strCon = examEntity.getExamContent();
            if (!TextUtils.isEmpty(strCon)){
                tvContentTip.setVisibility(View.GONE);
                ((TextView) rootView.findViewById(R.id.content)).setText(strCon);
            }

            String strCom = examEntity.getExamComments();
            TextView tvCommentTip = (TextView) rootView.findViewById(R.id.comment_empty_tip);
            tvCommentTip.setTextColor(pColor);
            tvCommentTip.setOnClickListener(this);
            if (!TextUtils.isEmpty(strCom)){
                tvCommentTip.setVisibility(View.GONE);
                ((TextView) rootView.findViewById(R.id.comment)).setText(strCon);
            }

            return rootView;
        } else {
            View rootView = inflater.inflate(R.layout.task_viewer_frag_task, container, false);

            String strTime = TpTime.getExactTime(
                    taskEntity.getTaskTime() + taskEntity.getTaskDate(),
                    TpTime.EXACT_TIME_TYPE_1);
            ((TextView) rootView.findViewById(R.id.time)).setText(strTime);

            long clsId = taskEntity.getClsId();
            ClassDAO classDAO = ClassDAO.getInstance(getContext());
            ClassEntity classEntity = classDAO.get(clsId);
            classDAO.close();
            if (classEntity != null) {
                mColor = classEntity.getClsColor();
            }
            int pColor = Color.parseColor(mColor);

            String strCon = taskEntity.getTaskContent();
            TextView tvContentTip = (TextView) rootView.findViewById(R.id.content_empty_tip);
            if (TextUtils.isEmpty(strCon)){
                tvContentTip.setTextColor(pColor);
                tvContentTip.setOnClickListener(this);
            } else {
                tvContentTip.setVisibility(View.GONE);
                ((TextView) rootView.findViewById(R.id.content)).setText(strCon);
            }

            String strCom = taskEntity.getTaskComments();
            TextView tvCommentTip = (TextView) rootView.findViewById(R.id.comment_empty_tip);
            if (TextUtils.isEmpty(strCom)){
                tvCommentTip.setTextColor(pColor);
                tvCommentTip.setOnClickListener(this);
            } else {
                tvCommentTip.setVisibility(View.GONE);
                ((TextView) rootView.findViewById(R.id.comment)).setText(strCon);
            }

            return rootView;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.content_empty_tip:
            case R.id.comment_empty_tip:
                final Context mContext = getContext();
                Dialog dlg = new AlertDialog.Builder(mContext)
                        .setTitle(R.string.clsv_edit_dlg_title)
                        .setMessage(R.string.clsv_edit_dlg_msg)
                        .setPositiveButton(R.string.com_confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (examEntity != null){
                                    TaskEdit.edit(mContext, Constants.TYPE_EXAM, examEntity);
                                } else if (taskEntity != null){
                                    TaskEdit.edit(mContext, Constants.TYPE_TASK, taskEntity);
                                }
                            }
                        })
                        .setNegativeButton(R.string.com_cancel, null)
                        .create();
                dlg.show();
                break;
        }
    }
}
