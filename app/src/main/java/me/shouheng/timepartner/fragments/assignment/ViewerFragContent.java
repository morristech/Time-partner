package me.shouheng.timepartner.fragments.assignment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.io.Serializable;

import me.shouheng.timepartner.activities.assignment.AssignEditActivity;
import me.shouheng.timepartner.database.dao.base.AssignDAO;
import me.shouheng.timepartner.models.business.intent.Intents;
import me.shouheng.timepartner.models.business.assignment.AssignmentBO;
import me.shouheng.timepartner.models.entities.assignment.Assignment;
import me.shouheng.timepartner.R;
import me.shouheng.timepartner.utils.TpTime;

public class ViewerFragContent extends Fragment implements View.OnClickListener{

    private AssignmentBO assignBO;

    private AssignDAO assignDAO;

    public static ViewerFragContent getInstance(AssignmentBO assignBO){
        ViewerFragContent fragContent =  new ViewerFragContent();
        Bundle arguments = new Bundle();
        if (assignBO != null)
            arguments.putSerializable(Intents.EXTRA_ENTITY, assignBO);
        fragContent.setArguments(arguments);
        return fragContent;
    }

    private ViewerFragContent() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments.containsKey(Intents.EXTRA_ENTITY)){
            Serializable serializable = arguments.getSerializable(Intents.EXTRA_ENTITY);
            if (serializable != null) {
                assignBO = (AssignmentBO) serializable;
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        assignDAO = AssignDAO.getInstance(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.assign_viewer_frag_content, container, false);
        if (assignBO != null) {
            final Assignment assignEntity = assignBO.getAssignEntity();

            TextView tvAsnTime = (TextView) view.findViewById(R.id.start_time);
            final TextView tvProgress = (TextView) view.findViewById(R.id.progress);
            TextView tvContent = (TextView) view.findViewById(R.id.content);
            TextView tvComment = (TextView) view.findViewById(R.id.comment);
            DiscreteSeekBar seekBar = (DiscreteSeekBar) view.findViewById(R.id.seekProgress);

            String strTime = TpTime.getExactTime(assignEntity.getAsnDate() + assignEntity.getAsnTime(),
                    TpTime.EXACT_TIME_TYPE_1);
            String strProgress = String.valueOf(assignEntity.getAsnProg()) + '%';
            tvAsnTime.setText(strTime);
            tvProgress.setText(strProgress);

            String strColor = assignEntity.getAsnColor();
            int pColor = Color.parseColor(strColor);

            String strContent = assignEntity.getAsnContent();
            if (TextUtils.isEmpty(strContent)){
                LinearLayout contentLayout = (LinearLayout) view.findViewById(R.id.content_empty_tip);
                contentLayout.setVisibility(View.VISIBLE);
                TextView tvAddContent = (TextView) view.findViewById(R.id.add_content_tip);
                tvAddContent.setOnClickListener(this);
                tvAddContent.setTextColor(pColor);
            } else {
                tvContent.setText(strContent);
            }

            String strComment = assignEntity.getAsnComment();
            if (TextUtils.isEmpty(strComment)){
                LinearLayout commentLayout = (LinearLayout) view.findViewById(R.id.comment_empty_tip);
                TextView tvAddComment = (TextView) view.findViewById(R.id.add_comment_tip);
                commentLayout.setVisibility(View.VISIBLE);
                tvAddComment.setOnClickListener(this);
                tvAddComment.setTextColor(pColor);
            } else {
                tvComment.setText(strComment);
            }

            seekBar.setIndicatorFormatter("%d0");
            seekBar.setThumbColor(pColor, pColor);
            seekBar.setScrubberColor(pColor);
            seekBar.setProgress(assignEntity.getAsnProg() / 10); // 设置进度
            seekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
                int progress;
                @Override
                public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                    this.progress = value;
                    String strProgress = String.valueOf(progress * 10) + '%';
                    tvProgress.setText(strProgress);
                }

                @Override
                public void onStartTrackingTouch(DiscreteSeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                    assignEntity.setAsnProg(progress * 10);
                    assignDAO.update(assignEntity);
                }
            });
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_content_tip:
            case R.id.add_comment_tip:
                Dialog dlg = new AlertDialog.Builder(getContext())
                        .setTitle(R.string.asnv_dlg_title)
                        .setMessage(R.string.asnv_dlg_msg)
                        .setPositiveButton(R.string.com_confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AssignEditActivity.edit(getContext(), assignBO);
                            }
                        })
                        .setNegativeButton(R.string.com_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {}
                        })
                        .create();
                dlg.show();
                break;
        }
    }


    @Override
    public void onDestroyView() {
        if (assignDAO != null) assignDAO.close();
        super.onDestroyView();
    }
}
