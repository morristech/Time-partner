package me.shouheng.timepartner.fragments.assignment;

import android.annotation.SuppressLint;
import android.app.Fragment;
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

import me.shouheng.timepartner.activities.assignment.AssignViewActivity;
import me.shouheng.timepartner.database.dao.bo.AssignBoDAO;
import me.shouheng.timepartner.models.business.assignment.AssignmentBO;
import me.shouheng.timepartner.R;
import me.shouheng.timepartner.adapters.AssignsSimpleAdapter;
import me.shouheng.timepartner.activities.base.SearchActivity;
import me.shouheng.timepartner.widget.listview.PinnedSectionListView;

public class AssignsListFrag extends Fragment{

    private PinnedSectionListView listView;

    private AssignsSimpleAdapter adapter;

    private TextView tvEmptyTip;

    private AssignBoDAO assignBoDAO;

    private boolean isShowingAll = false;

    public static AssignsListFrag getInstance(){
        return new AssignsListFrag();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.assign_dash_frag_list, container, false);
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

        adapter = new AssignsSimpleAdapter(getActivity(), R.layout.assign_list_item, R.id.title);
        assignBoDAO = AssignBoDAO.getInstance(getContext());
        adapter.setAssignBOs(assignBoDAO.getAll());
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
                if (view.getTag() != null){
                    AssignViewActivity.view(getActivity(), (AssignmentBO) view.getTag());
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
                showOverdueAssigns();
                isShowingAll = false;
                break;
            case R.id.all:
                showAllAssigns();
                isShowingAll = true;
                break;
            case R.id.search:
                SearchActivity.startForAssign(getActivity());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAllAssigns(){
        adapter.setAssignBOs(assignBoDAO.getAll());
        adapter.reloadData();
        adapter.setNotifyOnChange(true);
        if (adapter.getCount() == 0){
            tvEmptyTip.setVisibility(View.VISIBLE);
        } else {
            tvEmptyTip.setVisibility(View.GONE);
        }
        ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle(R.string.asn_all);
    }

    private void showOverdueAssigns(){
        adapter.setAssignBOs(assignBoDAO.getOverdue());
        adapter.reloadData();
        adapter.setNotifyOnChange(true);
        if (adapter.getCount() == 0){
            tvEmptyTip.setVisibility(View.VISIBLE);
        } else {
            tvEmptyTip.setVisibility(View.GONE);
        }
        ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle(R.string.asn_overdue);
    }

    public void update(){
        if (isShowingAll){
            showAllAssigns();
        } else {
            showOverdueAssigns();
        }
    }

    @Override
    public void onDestroy() {
        if (assignBoDAO != null){
            assignBoDAO.close();
        }
        super.onDestroy();
    }
}
