package me.shouheng.timepartner.activities.task;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import me.shouheng.timepartner.fragments.task.DashFragExam;
import me.shouheng.timepartner.fragments.task.DashFragTask;
import me.shouheng.timepartner.models.business.intent.Intents;
import me.shouheng.timepartner.Constants;
import me.shouheng.timepartner.R;
import me.shouheng.timepartner.utils.TpTime;
import me.shouheng.timepartner.widget.fab.FABBaseDialog;
import me.shouheng.timepartner.widget.materialtab.MaterialTab;
import me.shouheng.timepartner.widget.materialtab.MaterialTabHost;
import me.shouheng.timepartner.widget.materialtab.MaterialTabListener;

public class TaskDashboard extends AppCompatActivity implements MaterialTabListener {
    private FABBaseDialog fabBaseDialog;    //tab
    private MaterialTabHost tabHost;
    private ViewPager pager;
    private List<Fragment> fragList;
    private int titles[] = new int[]{R.string.task, R.string.exam};
    public final static String _TYPE_EXAM = "_EXAM",_TYPE_TASK = "_TASK";
    DashFragTask fragTask;
    DashFragExam fragExam;

    public static void start(Context mContext, String type){
        Intent intent = new Intent(mContext, TaskDashboard.class);
        intent.putExtra(Intents.EXTRA_TYPE, type);
        mContext.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_dash_layout);
        initViews();
        initTab();
    }

    private void initTab(){
        tabHost = (MaterialTabHost) this.findViewById(R.id.tabHost);
        pager = (ViewPager) this.findViewById(R.id.pager );
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // 当页面发生变化的时候，标题跟随着变化
                tabHost.setSelectedNavigationItem(position);
                ActionBar actionbar = TaskDashboard.this.getSupportActionBar();
                if (actionbar != null) {
                    actionbar.setTitle(titles[position]);
                }
            }
        });
        // 添加标题资源
        for (int i = 0; i < adapter.getCount(); i++) {
            tabHost.addTab(tabHost.newTab()
                    .setText(adapter.getPageTitle(i))
                    .setTabListener(this)
            );
        }
        if (getIntent().getStringExtra(Intents.EXTRA_TYPE).equals(_TYPE_EXAM)){
            pager.setCurrentItem(1, false);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(titles[1]);
            }
        } else {
            pager.setCurrentItem(0, false);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(titles[0]);
            }
        }
    }

    private void initViews(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setSubtitle(TpTime.getCurrentDate());
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fabBaseDialog = new FABBaseDialog(this, fab, R.layout.task_dash_fab_dialog);
        fabBaseDialog.setOnClickListener(
                new int[]{R.id.add_task, R.id.add_exam},
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.add_task:
                        TaskEdit.edit(TaskDashboard.this, Constants.TYPE_TASK, null);
                        break;
                    case R.id.add_exam:
                        TaskEdit.edit(TaskDashboard.this, Constants.TYPE_EXAM, null);;
                        break;
                }
                fabBaseDialog.dismiss();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabBaseDialog.showAsDialog();
            }
        });

        fragList = new ArrayList<>();
        fragTask = DashFragTask.getInstance(DashFragTask.SHOW_OVERDUE);
        fragExam = DashFragExam.getInstance(DashFragExam.SHOW_OVERDUE);
        fragList.add(fragTask);
        fragList.add(fragExam);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.share:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (pager.getCurrentItem() == 0){
            fragTask.update();
        } else {
            fragExam.update();
        }
    }

    @Override
    public void onTabSelected(MaterialTab tab) {
        pager.setCurrentItem(tab.getPosition());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            int pos = tab.getPosition();
            actionBar.setTitle(titles[pos]);
        }
    }

    @Override
    public void onTabReselected(MaterialTab tab) {}

    @Override
    public void onTabUnselected(MaterialTab tab) {}

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public Fragment getItem(int num) {
            return fragList.get(num);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getString(titles[position]);
        }
    }
}
