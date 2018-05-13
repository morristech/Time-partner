package me.shouheng.timepartner.fragments.assignment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

import me.shouheng.timepartner.database.dao.base.AssignSubDAO;
import me.shouheng.timepartner.models.business.intent.Intents;
import me.shouheng.timepartner.models.business.assignment.AssignmentBO;
import me.shouheng.timepartner.models.entities.assignment.SubAssignment;
import me.shouheng.timepartner.R;
import me.shouheng.timepartner.adapters.AssignsSubAdapter;

public class ViewerFragSub extends Fragment{

    private AssignmentBO assignBO;

    private AssignSubDAO assignSubDAO;

    public static ViewerFragSub getInstance(AssignmentBO assignBO){
        ViewerFragSub fragSub =  new ViewerFragSub();
        Bundle arguments = new Bundle();
        if (assignBO != null)
            arguments.putSerializable(Intents.EXTRA_ENTITY, assignBO);
        fragSub.setArguments(arguments);
        return fragSub;
    }

    private ViewerFragSub(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments.containsKey(Intents.EXTRA_ENTITY)) {
            Serializable serializable = arguments.getSerializable(Intents.EXTRA_ENTITY);
            if (serializable instanceof AssignmentBO) {
                assignBO = (AssignmentBO) serializable;
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        assignSubDAO = AssignSubDAO.getInstance(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.assign_viewer_frag_list_sub, container, false);
        RecyclerView listItem = (RecyclerView) view.findViewById(R.id.item_list);
        listItem.setLayoutManager(new LinearLayoutManager(getActivity()));

        List<SubAssignment> subAssignEntities = assignBO.getSubEntities();
        AssignsSubAdapter adapter = new AssignsSubAdapter();
        adapter.setSubAssignEntities(subAssignEntities);
        listItem.setAdapter(adapter);

        if (adapter.getItemCount() == 0){
            TextView tvEmpty = (TextView) view.findViewById(R.id.empty_tip);
            tvEmpty.setVisibility(View.VISIBLE);
        }

        adapter.setOnCheckboxClickListener(new AssignsSubAdapter.OnCheckboxClickListener() {
            @Override
            public void onClick(long subId, boolean isChecked) {
                SubAssignment subEntity = assignSubDAO.get(subId);
                if (subEntity != null){
                    subEntity.setSubCompleted(isChecked ? 1 : 0);
                    assignSubDAO.update(subEntity);
                }
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        if (assignSubDAO != null)
            assignSubDAO.close();
        super.onDestroyView();
    }
}
