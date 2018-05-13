package me.shouheng.timepartner.fragments.tpclass;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.List;

import me.shouheng.timepartner.activities.tpclass.ClassViewActivity;
import me.shouheng.timepartner.database.dao.bo.ClassBoDAO;
import me.shouheng.timepartner.database.dao.SortType;
import me.shouheng.timepartner.models.business.intent.Intents;
import me.shouheng.timepartner.models.business.tpclass.ClassBO;
import me.shouheng.timepartner.R;
import me.shouheng.timepartner.adapters.ClassesSimpleAdapter;
import me.shouheng.timepartner.activities.base.SearchActivity;
import me.shouheng.timepartner.widget.listview.PinnedSectionListView;

public class ClassListFragment extends Fragment {
    private PinnedSectionListView listView;
    private TextView tvEmptyTip;

    private ClassesSimpleAdapter adapter;

    private int _TYPE;
    public final static int _TYPE_VALUE = 1, _TYPE_VIEW = 2;

    private ClassBoDAO classBoDAO;

    private boolean isShowingAll = false;

    public static ClassListFragment getInstance(int _type){
        ClassListFragment listFrag = new ClassListFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(Intents.EXTRA_TYPE, _type);
        listFrag.setArguments(arguments);
        return listFrag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _TYPE = getArguments().getInt(Intents.EXTRA_TYPE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.class_list_frag_list, container, false);
        listView = (PinnedSectionListView) view.findViewById(R.id.list);
        tvEmptyTip = (TextView) view.findViewById(R.id.empty_tip);
        initializeAdapter();
        return view;
    }

    @SuppressLint("NewApi")
    private void initializeAdapter() {
        listView.setFastScrollEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            listView.setFastScrollAlwaysVisible(true);
        }

        adapter = new ClassesSimpleAdapter(getActivity(), R.layout.class_list_item, R.id.title);
        classBoDAO = ClassBoDAO.getInstance(getContext());
        List<ClassBO> classBOs = classBoDAO.getAll(SortType.DATE_DESC);
        classBoDAO.close();
        adapter.setClassBOs(classBOs);
        adapter.reloadData();
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);

        if (adapter.getCount() == 0){
            tvEmptyTip.setVisibility(View.VISIBLE);
        } else {
            tvEmptyTip.setVisibility(View.GONE);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Activity mActivity = getActivity();
                if (view.getTag() != null){
                    ClassBO classBO = (ClassBO) view.getTag();
                    if (_TYPE == _TYPE_VALUE){
                        Intent intent = new Intent();
                        intent.putExtra(Intents.EXTRA_ENTITY, classBO.getClassEntity());
                        mActivity.setResult(Activity.RESULT_OK, intent);
                        mActivity.finish();
                    } else {
                        ClassViewActivity.view(mActivity, classBO);
                    }
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_view_type, menu);
        inflater.inflate(R.menu.menu_sole_search, menu);
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
                showOverdueClasses();
                break;
            case R.id.all:
                isShowingAll = true;
                showAllClasses();
                break;
            case R.id.search:
                SearchActivity.startForClass(getActivity());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAllClasses(){
        classBoDAO = ClassBoDAO.getInstance(getActivity());
        adapter.setClassBOs(classBoDAO.getAll());
        classBoDAO.close();
        adapter.reloadData();
        adapter.setNotifyOnChange(true);
        if (adapter.getCount() == 0){
            tvEmptyTip.setVisibility(View.VISIBLE);
        } else {
            tvEmptyTip.setVisibility(View.GONE);
        }
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(R.string.class_list_title);
    }

    private void showOverdueClasses(){
        classBoDAO = ClassBoDAO.getInstance(getActivity());
        adapter.setClassBOs(classBoDAO.getOverdue());
        classBoDAO.close();
        adapter.reloadData();
        adapter.setNotifyOnChange(true);
        if (adapter.getCount() == 0){
            tvEmptyTip.setVisibility(View.VISIBLE);
        } else {
            tvEmptyTip.setVisibility(View.GONE);
        }
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(R.string.cls_list_title);
    }

    public void update(){
        if (isShowingAll){
            showAllClasses();
        } else {
            showOverdueClasses();
        }
    }
}
