package me.shouheng.timepartner.fragments.task;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.Serializable;

import me.shouheng.timepartner.database.dao.base.ExamDAO;
import me.shouheng.timepartner.models.business.intent.Intents;
import me.shouheng.timepartner.models.entities.tpclass.ClassEntity;
import me.shouheng.timepartner.models.entities.exam.Exam;
import me.shouheng.timepartner.R;
import me.shouheng.timepartner.adapters.ExamAdapter;
import me.shouheng.timepartner.activities.base.SearchActivity;
import me.shouheng.timepartner.activities.task.TaskViewer;
import me.shouheng.timepartner.widget.fastscroller.FastScroller;

public class DashFragExam extends Fragment {
    private boolean mTwoPane;
    private boolean isShowingAll = false;
    private ExamAdapter adapter;
    private ExamDAO examDao;
    private TextView tvEmptyTip;
    public static final int SHOW_ALL = 1;
    public static final int SHOW_OVERDUE = 2;
    private static final int SHOW_OF_CLASS = 3;

    /**
     * 显示全部/未完成考试信息
     * @param type 全部Or未完成
     * @return 碎片实体 */
    public static DashFragExam getInstance(int type){
        DashFragExam fragExam = new DashFragExam();
        Bundle arguments = new Bundle();
        arguments.putInt(Intents.EXTRA_TYPE, type);
        fragExam.setArguments(arguments);
        return fragExam;
    }

    /**
     * 获取指定课程的全部考试信息
     * @param classEntity 考试信息
     * @return 碎片实体 */
    public static DashFragExam getInstance(ClassEntity classEntity){
        DashFragExam fragExam = new DashFragExam();
        Bundle arguments = new Bundle();
        if (classEntity != null){
            arguments.putSerializable(Intents.EXTRA_ENTITY, classEntity);
            arguments.putInt(Intents.EXTRA_TYPE, SHOW_OF_CLASS);
        }
        fragExam.setArguments(arguments);
        return fragExam;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.task_dash_frag_list, container, false);
        if (view.findViewById(R.id.item_detail_container) != null) {
            mTwoPane = true;
        }
        tvEmptyTip = (TextView) view.findViewById(R.id.empty_tip);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.item_list);
        FastScroller fastScroller = (FastScroller) view.findViewById(R.id.fastscroller);
        fastScroller.setRecyclerView(recyclerView);
        setupRecyclerView(recyclerView);

        return view;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        adapter = new ExamAdapter(getActivity());
        examDao = ExamDAO.getInstance(getContext());

        Bundle arguments = getArguments();
        switch (arguments.getInt(Intents.EXTRA_TYPE)){
            case SHOW_ALL:
                showAllExams();
                break;
            case SHOW_OVERDUE:
                showOverdueExams();
                break;
            case SHOW_OF_CLASS:
                Serializable serializable = arguments.getSerializable(Intents.EXTRA_ENTITY);
                if (serializable instanceof ClassEntity){
                    ClassEntity classEntity = (ClassEntity) arguments.getSerializable(Intents.EXTRA_ENTITY);
                    showExamsOfClass(classEntity.getClsId());
                }
                break;
        }

        recyclerView.setAdapter(adapter);

        if (adapter.getItemCount() == 0){
            tvEmptyTip.setText(R.string.exam_no_tip);
            tvEmptyTip.setVisibility(View.VISIBLE);
        } else {
            tvEmptyTip.setVisibility(View.GONE);
        }

        adapter.setOnItemClickListener(new ExamAdapter.OnItemClickListener() {
            @Override
            public void onClick(View v, Exam examEntity) {
                if (mTwoPane) {
                    ViewerFragment fragment = ViewerFragment.getInstance(examEntity);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, fragment)
                            .commit();
                } else {
                    TaskViewer.view(getContext(), examEntity);
                }
            }
        });
    }

    private void showAllExams(){
        adapter.setExamEntities(examDao.getAll());
        adapter.notifyDataSetChanged();

        if (adapter.getItemCount() != 0){
            tvEmptyTip.setVisibility(View.GONE);
        } else {
            tvEmptyTip.setVisibility(View.VISIBLE);
        }
    }

    private void showOverdueExams(){
        adapter.setExamEntities(examDao.getOverdue());
        adapter.notifyDataSetChanged();

        if (adapter.getItemCount() != 0){
            tvEmptyTip.setVisibility(View.GONE);
        } else {
            tvEmptyTip.setVisibility(View.VISIBLE);
        }
    }

    private void showExamsOfClass(long clsId){
        adapter.setExamEntities(examDao.gets(clsId));
        adapter.notifyDataSetChanged();

        if (adapter.getItemCount() != 0){
            tvEmptyTip.setVisibility(View.GONE);
        } else {
            tvEmptyTip.setVisibility(View.VISIBLE);
        }
    }

    public void update(){
        if (isShowingAll){
            showAllExams();
        } else {
            showOverdueExams();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_view_type, menu);
        if (getArguments().getInt(Intents.EXTRA_TYPE) != SHOW_OF_CLASS){
            inflater.inflate(R.menu.menu_sole_search, menu);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.overdue_only:
                isShowingAll = false;
                showOverdueExams();
                break;
            case R.id.all:
                isShowingAll = true;
                showAllExams();
                break;
            case R.id.search:
                SearchActivity.startForExam(getActivity());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        if (examDao != null){
            examDao.close();
        }
        super.onDestroy();
    }
}
