package me.shouheng.timepartner.activities.base;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.shouheng.timepartner.database.dao.bo.AssignBoDAO;
import me.shouheng.timepartner.database.dao.bo.AbstractBoDAO;
import me.shouheng.timepartner.database.dao.bo.ClassBoDAO;
import me.shouheng.timepartner.database.dao.base.BaseDAO;
import me.shouheng.timepartner.adapters.SearchAdapter;
import me.shouheng.timepartner.database.dao.base.ExamDAO;
import me.shouheng.timepartner.database.dao.base.NoteDAO;
import me.shouheng.timepartner.database.dao.base.TaskDAO;
import me.shouheng.timepartner.models.business.intent.Intents;
import me.shouheng.timepartner.R;
import me.shouheng.timepartner.Constants;
import me.shouheng.timepartner.utils.TpColor;
import me.shouheng.timepartner.utils.TpDisp;

public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private SearchView mSearchView;

    private String queryString;
    private String strHint;

    private InputMethodManager mImm;

    private List searchResults = Collections.emptyList();

    private SearchAdapter adapter;

    private BaseDAO baseDAO;
    private AbstractBoDAO baseBoDAO;

    public static void startForClass(Context mContext){
        startActivity(mContext, Constants.TYPE_CLASS);
    }

    public static void startForAssign(Context mContext){
        startActivity(mContext, Constants.TYPE_ASSIGN);
    }

    public static void startForExam(Context mContext){
        startActivity(mContext, Constants.TYPE_EXAM);
    }

    public static void startForTask(Context mContext){
        startActivity(mContext, Constants.TYPE_TASK);
    }

    public static void startForNote(Context mContext){
        startActivity(mContext, Constants.TYPE_NOTE);
    }

    private static void startActivity(Context mContext, int _type){
        Intent intent = new Intent(mContext, SearchActivity.class);
        intent.putExtra(Intents.EXTRA_TYPE, _type);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        mContext.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        int pColor;
        switch (getIntent().getIntExtra(Intents.EXTRA_TYPE, 0)){
            case Constants.TYPE_CLASS:
                strHint = getString(R.string.search_hint_for_class);
                baseBoDAO = ClassBoDAO.getInstance(this);
                pColor = Color.parseColor(TpColor.COLOR_CLASS);
                break;
            case Constants.TYPE_ASSIGN:
                strHint = getString(R.string.search_hint_for_assign);
                baseBoDAO = AssignBoDAO.getInstance(this);
                pColor = Color.parseColor(TpColor.COLOR_ASSIGN);
                break;
            case Constants.TYPE_EXAM:
                strHint = getString(R.string.search_hint_for_exam);
                baseDAO = ExamDAO.getInstance(this);
                pColor = Color.parseColor(TpColor.COLOR_EXAM);
                break;
            case Constants.TYPE_TASK:
                strHint = getString(R.string.search_hint_for_task);
                baseDAO = TaskDAO.getInstance(this);
                pColor = Color.parseColor(TpColor.COLOR_TASK);
                break;
            case Constants.TYPE_NOTE:
                strHint = getString(R.string.search_hint_for_note);
                baseDAO = NoteDAO.getInstance(this);
                pColor = Color.parseColor(TpColor.COLOR_NOTE);
                break;
            default:
                pColor = Color.parseColor(TpColor.COLOR_PRIME);
        }
        TpDisp.setStatusBarColor(this, pColor, TpDisp.DEFAULT_COLOR_ALPHA);

        mImm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(pColor);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchAdapter(this);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_activity, menu);
        mSearchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_search));
        mSearchView.setOnQueryTextListener(this);
        // 提示文字
        mSearchView.setQueryHint(strHint);
        // 设置默认的显示方式，true显示icon，false显示编辑
        mSearchView.setIconifiedByDefault(false);
        // true显示icon false显示编辑框
        mSearchView.setIconified(false);
        MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.menu_search),
                new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                finish();
                return false;
            }
        });
        // 展开icon
        menu.findItem(R.id.menu_search).expandActionView();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // 这里的作用是在文本改变的时候也进行搜索
        onQueryTextChange(query);
        // 隐藏软键盘
        hideInputManager();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // 下面的一段代码的作用是 当改变的文字与已有的文字相同的时候就直接返回，不再进行搜索和设置，因为上面的submit中也用到了改方法
        if (newText.equals(queryString)) {
            return true;
        }
        queryString = newText;
        // 检索的内容不为空
        if (!TextUtils.isEmpty(queryString)) {
            this.searchResults = new ArrayList();
            if (baseBoDAO != null){
                searchResults.addAll(baseBoDAO.getQuery(queryString));
            } else {
                searchResults.addAll(baseDAO.getQuery(queryString));
            }
        } else {
            searchResults.clear();
        }
        // 修改Adapter中管理的数据集合，通知Adpater集合中的内容发生了变化
        adapter.updateSearchResults(searchResults);
        adapter.notifyDataSetChanged();
        return true;
    }

    private void hideInputManager() {
        if (mSearchView != null) {
            if (mImm != null) {
                mImm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);
            }
            mSearchView.clearFocus();
        }
    }

    @Override
    protected void onDestroy() {
        if (baseDAO != null){
            baseDAO.close();
        }
        if (baseBoDAO != null){
            baseBoDAO.close();
        }
        super.onDestroy();
    }
}
