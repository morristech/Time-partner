package me.shouheng.timepartner.activities.assignment;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import me.shouheng.timepartner.R;
import me.shouheng.timepartner.fragments.assignment.AssignsListFrag;
import me.shouheng.timepartner.activities.base.BaseActivity;

public class AssignActivity extends BaseActivity implements View.OnClickListener {

    private AssignsListFrag listFrag;

    public static void start(Context mContext){
        Intent intent = new Intent(mContext, AssignActivity.class);
        mContext.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assign_dash_layout);
        initViews();
    }

    private void initViews(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.assign_title);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setSubtitle(R.string.asn_overdue);
        }

        listFrag = new AssignsListFrag();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout, listFrag);
        transaction.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:
                AssignEditActivity.edit(this, null);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        listFrag.update();
    }
}
