package me.shouheng.timepartner.activities.assignment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import me.shouheng.timepartner.fragments.assignment.ViewerFragContent;
import me.shouheng.timepartner.fragments.assignment.ViewerFragSub;
import me.shouheng.timepartner.models.business.intent.Intents;
import me.shouheng.timepartner.models.business.assignment.AssignmentBO;
import me.shouheng.timepartner.models.entities.assignment.Assignment;
import me.shouheng.timepartner.R;
import me.shouheng.timepartner.managers.ActivityManager;
import me.shouheng.timepartner.activities.base.BaseActivity;
import me.shouheng.timepartner.utils.TpColor;
import me.shouheng.timepartner.utils.TpDisp;
import me.shouheng.timepartner.widget.custom.FadeAppBarLayout;
import me.shouheng.timepartner.widget.materialtab.MaterialTab;
import me.shouheng.timepartner.widget.materialtab.MaterialTabHost;
import me.shouheng.timepartner.widget.materialtab.MaterialTabListener;

/**
 初始化日程的进度值
 这么做是因为，在编辑日程的时候我们没有设置“日程进度”这一项的值
 虑到，打开日程编辑活动的类型不同，有时候是从浏览打开的，这时候进度可能不为0
 而如果我们在日程编辑中将其设置为0，就会导致日程进度认为设置为了0
 而在这里，因为getInt的默认值为0，就是说如果我们没有设置过日程进度，那么获取的是0，
 如果设置过日程进度那么获取的就是日程进度，这样再将其设置到日程进度，我们
 就完成了日程进度的初始化工作*/
public class AssignViewActivity extends BaseActivity implements MaterialTabListener {
    private String strColor = TpColor.COLOR_ASSIGN;
    private String strName;

    private AssignmentBO assignBO;

    private List<Fragment> fragList = new ArrayList<>();
    private ViewPager pager;

    public static void view(Context mContext, AssignmentBO assignBO){
        Intent intent = new Intent(mContext, AssignViewActivity.class);
        if (assignBO != null)
            intent.putExtra(Intents.EXTRA_ENTITY, assignBO);
        mContext.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assign_viewer_layout);
        ActivityManager.addAssignActivity(this);

        Intent intent = getIntent();
        if (intent.hasExtra(Intents.EXTRA_ENTITY)){
            initViews();
            Serializable serializable = intent.getSerializableExtra(Intents.EXTRA_ENTITY);
            if (serializable instanceof AssignmentBO){
                assignBO = (AssignmentBO) serializable;
                initValues(assignBO);
            }
        }
    }

    private void initValues(AssignmentBO assignBO){
        Assignment assignEntity = assignBO.getAssignEntity();
        strName = assignEntity.getAsnTitle();
        strColor = assignEntity.getAsnColor();
        ViewerFragContent fragContent = ViewerFragContent.getInstance(assignBO);
        fragList.add(fragContent);
        ViewerFragSub fragSub = ViewerFragSub.getInstance(assignBO);
        fragList.add(fragSub);
    }

    private void initViews(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(strName);
        toolbar.setSubtitle(R.string.assignment);
        toolbar.setBackgroundColor(Color.parseColor(strColor));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        int pColor = Color.parseColor(strColor);
        TpDisp.setStatusBarColor(this, pColor, TpDisp.DEFAULT_COLOR_ALPHA);

        TextView tvAsnName = (TextView) findViewById(R.id.name);
        TextView tvAssign = (TextView) findViewById(R.id.assign);
        tvAsnName.setText(strName);
        FadeAppBarLayout bar = (FadeAppBarLayout)findViewById(R.id.bar);
        bar.setBackgroundColor(pColor);
        bar.regFadeToolbar(toolbar);
        bar.regFadeTitle(tvAsnName);
        bar.regFadeSubtitle(tvAssign);

        final MaterialTabHost tabHost = (MaterialTabHost)findViewById(R.id.tabHost);
        tabHost.setPrimaryColor(Color.parseColor(strColor));
        pager = (ViewPager) this.findViewById(R.id.pager );
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
                onBack();
                break;
            case R.id.share:
                break;
            case R.id.edit:
                AssignEditActivity.edit(this, assignBO);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onBack(){
        ActivityManager.finishAllAssigns();
    }

    @Override
    public void onBackPressed() {
        onBack();
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
                getString(R.string.asnv_tab1),
                getString(R.string.asnv_tab2)};

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
}
