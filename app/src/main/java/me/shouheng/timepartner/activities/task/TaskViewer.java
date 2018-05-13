package me.shouheng.timepartner.activities.task;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.Serializable;

import me.shouheng.timepartner.database.dao.base.ClassDAO;
import me.shouheng.timepartner.fragments.task.ViewerFragment;
import me.shouheng.timepartner.models.business.intent.Intents;
import me.shouheng.timepartner.models.entities.Entity;
import me.shouheng.timepartner.models.entities.tpclass.ClassEntity;
import me.shouheng.timepartner.models.entities.exam.Exam;
import me.shouheng.timepartner.models.entities.task.Task;
import me.shouheng.timepartner.Constants;
import me.shouheng.timepartner.R;
import me.shouheng.timepartner.managers.ActivityManager;
import me.shouheng.timepartner.activities.base.BaseActivity;
import me.shouheng.timepartner.utils.TpDisp;
import me.shouheng.timepartner.widget.custom.FadeAppBarLayout;

public class TaskViewer extends BaseActivity {

    private Task taskEntity;

    private Exam examEntity;

    public static <T extends Entity> void view(Context mContext, T t){
        Intent intent = new Intent(mContext, TaskViewer.class);
        if (t != null)
            intent.putExtra(Intents.EXTRA_ENTITY, t);
        mContext.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_viewer_layout);

        ActivityManager.addTaskActivity(this);

        Intent intent = getIntent();
        if (intent.hasExtra(Intents.EXTRA_ENTITY)){
            Serializable serializable = intent.getSerializableExtra(Intents.EXTRA_ENTITY);
            if (serializable instanceof Exam){
                examEntity = (Exam) serializable;
            } else if (serializable instanceof Task){
                taskEntity = (Task) serializable;
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        FadeAppBarLayout fadeBar = (FadeAppBarLayout) findViewById(R.id.app_bar);
        TextView tvTitle = (TextView) findViewById(R.id.title);
        TextView tvSubject = (TextView) findViewById(R.id.subject);
        fadeBar.regFadeTitle(tvTitle);
        fadeBar.regFadeToolbar(toolbar);
        fadeBar.regFadeSubtitle(tvSubject);

        if (examEntity != null){
            String examTitle = examEntity.getExamTitle();
            tvTitle.setText(examTitle);
            actionBar.setTitle(examTitle);
            tvSubject.setText(R.string.exam);

            long clsId = examEntity.getClsId();
            ClassDAO classDAO = ClassDAO.getInstance(this);
            ClassEntity classEntity = classDAO.get(clsId);
            if (classEntity != null) {
                String mColor = classEntity.getClsColor();
                int pColor = Color.parseColor(mColor);
                String clsName = classEntity.getClsName();
                fadeBar.setBackgroundColor(pColor);
                toolbar.setBackgroundColor(pColor);
                toolbar.setSubtitle(clsName);
                tvSubject.setText(clsName);
                TpDisp.setStatusBarColor(this, pColor, TpDisp.DEFAULT_COLOR_ALPHA);
            }
            classDAO.close();
        } else if (taskEntity != null) {
            String taskTitle = taskEntity.getTaskTitle();
            tvTitle.setText(taskTitle);
            actionBar.setTitle(taskTitle);
            tvSubject.setText(R.string.task);

            long clsId = taskEntity.getClsId();
            ClassDAO classDAO = ClassDAO.getInstance(this);
            ClassEntity classEntity = classDAO.get(clsId);
            classDAO.close();
            if (classEntity != null) {
                String mColor = classEntity.getClsColor();
                int pColor = Color.parseColor(mColor);
                String clsName = classEntity.getClsName();
                fadeBar.setBackgroundColor(pColor);
                toolbar.setBackgroundColor(pColor);
                toolbar.setSubtitle(clsName);
                tvSubject.setText(clsName);
                TpDisp.setStatusBarColor(this, pColor, TpDisp.DEFAULT_COLOR_ALPHA);
            }
        } else {
            finish();
        }

        if (savedInstanceState == null) {
            ViewerFragment fragment = null;
            if (examEntity != null){
                fragment = ViewerFragment.getInstance(examEntity);
            } else if (taskEntity != null){
                fragment = ViewerFragment.getInstance(taskEntity);
            } else {
                finish();
            }
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.item_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.com_viewer, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                ActivityManager.finishAllTasks();
                return true;
            case R.id.edit:
                if (examEntity != null) {
                    TaskEdit.edit(this, Constants.TYPE_EXAM, examEntity);
                } else if (taskEntity != null){
                    TaskEdit.edit(this, Constants.TYPE_TASK, taskEntity);
                } else {
                    break;
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        ActivityManager.finishAllTasks();
    }
}
