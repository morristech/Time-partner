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

import me.shouheng.timepartner.database.dao.base.TaskDAO;
import me.shouheng.timepartner.models.business.intent.Intents;
import me.shouheng.timepartner.models.entities.tpclass.ClassEntity;
import me.shouheng.timepartner.models.entities.task.Task;
import me.shouheng.timepartner.R;
import me.shouheng.timepartner.adapters.TaskAdapter;
import me.shouheng.timepartner.activities.base.SearchActivity;
import me.shouheng.timepartner.activities.task.TaskViewer;
import me.shouheng.timepartner.widget.fastscroller.FastScroller;

public class DashFragTask extends Fragment{
    private boolean mTwoPane;
    private boolean isShowingAll = false;
    private TaskDAO taskDAO;
    private TextView tvEmptyTip;
    private TaskAdapter adapter;
    public static final int SHOW_ALL = 1;
    public static final int SHOW_OVERDUE = 2;
    private static final int SHOW_OF_CLASS = 3;

    /**
     * 显示全部的作业信息
     * @param type 显示类型，未完成Or全部
     * @return 碎片实例 */
    public static DashFragTask getInstance(int type){
        DashFragTask fragTask = new DashFragTask();
        Bundle arguments = new Bundle();
        arguments.putInt(Intents.EXTRA_TYPE, type);
        fragTask.setArguments(arguments);
        return fragTask;
    }

    /**
     * 显示指定课程的作业信息
     * @param classEntity 课程
     * @return 作业实例 */
    public static DashFragTask getInstance(ClassEntity classEntity){
        DashFragTask fragTask = new DashFragTask();
        Bundle arguments = new Bundle();
        arguments.putInt(Intents.EXTRA_TYPE, SHOW_OF_CLASS);
        arguments.putSerializable(Intents.EXTRA_ENTITY, classEntity);
        fragTask.setArguments(arguments);
        return fragTask;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
        adapter = new TaskAdapter(getActivity());
        taskDAO = TaskDAO.getInstance(getContext());

        Bundle arguments = getArguments();
        switch (arguments.getInt(Intents.EXTRA_TYPE)){
            case SHOW_ALL:
                showAllTasks();
                break;
            case SHOW_OVERDUE:
                showOverdueTasks();
                break;
            case SHOW_OF_CLASS:
                ClassEntity classEntity = (ClassEntity) arguments.getSerializable(Intents.EXTRA_ENTITY);
                showTasksClass(classEntity);
                break;
        }

        recyclerView.setAdapter(adapter);

        if (adapter.getItemCount() == 0){
            tvEmptyTip.setText(R.string.task_no_tip);
            tvEmptyTip.setVisibility(View.VISIBLE);
        } else {
            tvEmptyTip.setVisibility(View.GONE);
        }

        adapter.setOnItemClickListener(new TaskAdapter.OnItemClickListener() {
            @Override
            public void onClick(View v, Task taskEntity) {
                if (mTwoPane) {
                    ViewerFragment fragment = ViewerFragment.getInstance(taskEntity);
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.item_detail_container, fragment)
                            .commit();
                } else {
                    TaskViewer.view(getContext(), taskEntity);
                }
            }
        });
    }

    private void showAllTasks(){
        adapter.setTaskEntities(taskDAO.getAll());
        adapter.notifyDataSetChanged();
        if (adapter.getItemCount() != 0){
            tvEmptyTip.setVisibility(View.GONE);
        } else {
            tvEmptyTip.setVisibility(View.VISIBLE);
        }
    }

    private void showOverdueTasks(){
        adapter.setTaskEntities(taskDAO.getOverdue());
        adapter.notifyDataSetChanged();
        if (adapter.getItemCount() != 0){
            tvEmptyTip.setVisibility(View.GONE);
        } else {
            tvEmptyTip.setVisibility(View.VISIBLE);
        }
    }

    public void update(){
        if (isShowingAll){
            showAllTasks();
        } else {
            showOverdueTasks();
        }
    }

    private void showTasksClass(ClassEntity classEntity){
        long clsId = classEntity.getClsId();
        adapter.setTaskEntities(taskDAO.gets(clsId));
        adapter.notifyDataSetChanged();
        if (adapter.getItemCount() != 0){
            tvEmptyTip.setVisibility(View.GONE);
        } else {
            tvEmptyTip.setVisibility(View.VISIBLE);
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
                showOverdueTasks();
                break;
            case R.id.all:
                isShowingAll = true;
                showAllTasks();
                break;
            case R.id.search:
                SearchActivity.startForTask(getActivity());
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDestroy() {
        if (taskDAO != null){
            taskDAO.close();
        }
        super.onDestroy();
    }
}
