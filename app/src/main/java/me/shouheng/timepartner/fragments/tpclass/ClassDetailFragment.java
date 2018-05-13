package me.shouheng.timepartner.fragments.tpclass;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;

import me.shouheng.timepartner.activities.tpclass.ClassEditActivity;
import me.shouheng.timepartner.models.business.intent.Intents;
import me.shouheng.timepartner.models.business.tpclass.ClassBO;
import me.shouheng.timepartner.R;
import me.shouheng.timepartner.adapters.ClassDetailsAdapter;

public class ClassDetailFragment extends Fragment implements ClassDetailsAdapter.OnEditClickListener{

    private ClassBO classBO;

    public static ClassDetailFragment setInstance(ClassBO classBO){
        ClassDetailFragment fragDetails = new ClassDetailFragment();
        Bundle arguments = new Bundle();
        if (classBO != null)
            arguments.putSerializable(Intents.EXTRA_ENTITY, classBO);
        fragDetails.setArguments(arguments);
        return fragDetails;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments.containsKey(Intents.EXTRA_ENTITY)){
            Serializable serializable = arguments.getSerializable(Intents.EXTRA_ENTITY);
            if (serializable instanceof ClassBO){
                classBO = (ClassBO) serializable;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.class_viewer_frag_list_detail, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.item_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ClassDetailsAdapter adapter = new ClassDetailsAdapter(getActivity());
        adapter.setClassBO(classBO);
        adapter.setOnEditClickListener(this);

        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onClick() {
        final Context mContext = getActivity();
        Dialog dlg = new AlertDialog.Builder(mContext)
                .setTitle(R.string.clsv_edit_dlg_title)
                .setMessage(R.string.clsv_edit_dlg_msg)
                .setPositiveButton(R.string.com_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ClassEditActivity.edit(mContext, classBO);
                    }
                })
                .setNegativeButton(R.string.com_cancel, null)
                .create();
        dlg.show();
    }
}
