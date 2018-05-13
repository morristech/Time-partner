package me.shouheng.timepartner.activities.tpclass;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import me.shouheng.timepartner.fragments.tpclass.ClassListFragment;
import me.shouheng.timepartner.models.business.intent.Intents;
import me.shouheng.timepartner.R;
import me.shouheng.timepartner.activities.base.BaseActivity;

public class ClassListActivity extends BaseActivity {
    private static final int _FOR_VALUE = 1, _FOR_VIEW = 2;
    private ClassListFragment clsListFrag;

    public static void start(Context mContext){
        Intent intent = new Intent(mContext, ClassListActivity.class);
        intent.putExtra(Intents.EXTRA_TYPE, _FOR_VIEW);
        mContext.startActivity(intent);
    }

    public static void startForValue(Activity activity, int requestCode){
        Intent intent = new Intent(activity, ClassListActivity.class);
        intent.putExtra(Intents.EXTRA_TYPE, _FOR_VALUE);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.class_list_layout);
        initViews();
    }

    private void initViews(){
        int _TYPE = getIntent().getIntExtra(Intents.EXTRA_TYPE, 0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.class_title2);

        if (_TYPE == _FOR_VALUE){
            clsListFrag = ClassListFragment.getInstance(ClassListFragment._TYPE_VALUE);
        } else {
            clsListFrag = ClassListFragment.getInstance(ClassListFragment._TYPE_VIEW);
        }
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout, clsListFrag);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        clsListFrag.update();
    }
}
