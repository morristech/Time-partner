package me.shouheng.timepartner.activities.tpclass;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import me.shouheng.timepartner.fragments.tpclass.ClassDetailFragment;
import me.shouheng.timepartner.database.dao.bo.ClassBoDAO;
import me.shouheng.timepartner.models.business.intent.Intents;
import me.shouheng.timepartner.models.business.tpclass.ClassBO;
import me.shouheng.timepartner.models.entities.tpclass.ClassEntity;
import me.shouheng.timepartner.R;
import me.shouheng.timepartner.activities.base.BaseActivity;
import me.shouheng.timepartner.fragments.task.DashFragExam;
import me.shouheng.timepartner.fragments.task.DashFragTask;
import me.shouheng.timepartner.managers.ActivityManager;
import me.shouheng.timepartner.utils.TpColor;
import me.shouheng.timepartner.utils.TpDisp;
import me.shouheng.timepartner.utils.TpTime;
import me.shouheng.timepartner.widget.custom.FadeAppBarLayout;
import me.shouheng.timepartner.widget.materialtab.MaterialTab;
import me.shouheng.timepartner.widget.materialtab.MaterialTabHost;
import me.shouheng.timepartner.widget.materialtab.MaterialTabListener;

public class ClassViewActivity extends BaseActivity implements View.OnClickListener, MaterialTabListener {

    private MaterialTabHost tabHost;

    private ClassBO classBO;

    private ViewPager pager;

    private List<Fragment> fragList;

    public static void view(Context mContext, ClassBO classBO){
        Intent intent = new Intent(mContext, ClassViewActivity.class);
        if (classBO != null)
            intent.putExtra(Intents.EXTRA_ENTITY, classBO);
        mContext.startActivity(intent);
    }

    public static void view(Context mContext, ClassEntity classEntity){
        Intent intent = new Intent(mContext, ClassViewActivity.class);
        if (classEntity != null)
            intent.putExtra(Intents.EXTRA_ENTITY, classEntity);
        mContext.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.class_viewer_layout);

        ActivityManager.addClassActivity(this);

        Intent intent = getIntent();
        if (intent.hasExtra(Intents.EXTRA_ENTITY)){
            Serializable serializable = intent.getSerializableExtra(Intents.EXTRA_ENTITY);
            if (serializable instanceof ClassBO){
                classBO = (ClassBO) serializable;
            } else if (serializable instanceof ClassEntity){
                ClassBoDAO classBoDAO = ClassBoDAO.getInstance(this);
                long clsId = ((ClassEntity) serializable).getClsId();
                classBO = classBoDAO.get(clsId);
                classBoDAO.close();
            }
            if (classBO != null){
                initValues(classBO);
            }
        }
    }

    private void initValues(ClassBO classBO) {
        ClassEntity classEntity = classBO.getClassEntity();

        String clsName = classEntity.getClsName();
        final TextView tvClsName = (TextView) findViewById(R.id.name);
        final TextView tvClsDate = (TextView) findViewById(R.id.date);
        tvClsName.setText(clsName);

        String clsDate = TpTime.getDate(classEntity.getStartDate(), TpTime.DATE_TYPE_3)
                + " - " + TpTime.getDate(classEntity.getEndDate(), TpTime.DATE_TYPE_3);
        tvClsDate.setText(clsDate);

        int pColor = Color.parseColor(TpColor.COLOR_CLASS);
        String mColor = classEntity.getClsColor();
        if (!TextUtils.isEmpty(mColor)){
            pColor = Color.parseColor(mColor);
            TpDisp.setStatusBarColor(this, pColor, TpDisp.DEFAULT_COLOR_ALPHA);
        }

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(pColor);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(clsName);
            actionBar.setSubtitle(clsDate);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        FadeAppBarLayout barLayout = (FadeAppBarLayout) findViewById(R.id.bar);
        barLayout.setBackgroundColor(pColor);
        barLayout.regFadeToolbar(toolbar);
        barLayout.regFadeTitle(tvClsName);
        barLayout.regFadeSubtitle(tvClsDate);

        tabHost = (MaterialTabHost) this.findViewById(R.id.tabHost);
        tabHost.setPrimaryColor(pColor);
        pager = (ViewPager) this.findViewById(R.id.pager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                tabHost.setSelectedNavigationItem(position);
            }
        });
        for (int i = 0; i < adapter.getCount(); i++) {
            tabHost.addTab(tabHost.newTab()
                            .setText(adapter.getPageTitle(i))
                            .setTabListener(this)
            );
        }
        fragList = new ArrayList<>();

        ClassDetailFragment fragDetails = ClassDetailFragment.setInstance(classBO);
        fragList.add(fragDetails);

        DashFragTask fragTask = DashFragTask.getInstance(classEntity);
        DashFragExam fragExam = DashFragExam.getInstance(classEntity);
        fragList.add(fragTask);
        fragList.add(fragExam);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.com_viewer, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ActivityManager.finishAllClasses();
                break;
            case R.id.edit:
                ClassEditActivity.edit(this, classBO);
                break;
            case R.id.share:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {}
    }

    @Override
    public void onBackPressed() {
        ActivityManager.finishAllClasses();
    }

    @Override
    public void onTabSelected(MaterialTab tab) {
        pager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(MaterialTab tab) {}

    @Override
    public void onTabUnselected(MaterialTab tab) {}

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {
        String titles[] = new String[]{
                getString(R.string.cls_tab3),
                getString(R.string.cls_tab1),
                getString(R.string.cls_tab2)};

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public Fragment getItem(int num) {
            return fragList.get(num);
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
