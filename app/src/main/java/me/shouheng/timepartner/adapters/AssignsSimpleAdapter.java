package me.shouheng.timepartner.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import me.shouheng.timepartner.models.business.assignment.AssignmentBO;
import me.shouheng.timepartner.models.business.assignment.AssignmentSection;
import me.shouheng.timepartner.models.entities.assignment.Assignment;
import me.shouheng.timepartner.R;
import me.shouheng.timepartner.utils.TpTime;
import me.shouheng.timepartner.widget.listview.PinnedSectionListView;

public class AssignsSimpleAdapter extends ArrayAdapter<AssignmentSection> implements PinnedSectionListView.PinnedSectionListAdapter {

    private int resId; // 组件布局资源

    private List<String> titleList = new ArrayList<>();  // section集合

    private Map<String, List<AssignmentBO>> map = new HashMap<>();  // 每个section对应的list集合

    private List<AssignmentBO> assignBOs = new LinkedList<>();

    private final int _TYPE_ITEM = 0, _TYPE_SECTION = 1;

    public AssignsSimpleAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
        this.resId = resource;
    }

    public void setAssignBOs(List<AssignmentBO> assignBOs) {
        this.assignBOs = assignBOs;
    }

    public void reloadData() {
        clearData();

        for (AssignmentBO assignBO : assignBOs){
            String secTitle = TpTime.getDate(assignBO.getAssignEntity().getAsnDate(), TpTime.DATE_TYPE_5);
            if (map.containsKey(secTitle)) {
                map.get(secTitle).add(assignBO);
            } else {
                titleList.add(secTitle);
                List<AssignmentBO> assignEntities = new LinkedList<>();  // 构建section内部集合
                assignEntities.add(assignBO);
                map.put(secTitle, assignEntities);
            }
        }

        int sectionPosition = 0, listPosition = 0;
        for (int i = 0;i < map.size();i++) {
            AssignmentSection sectionBO = new AssignmentSection();
            sectionBO.setSection(true);
            sectionBO.setSectionName(titleList.get(i));
            sectionBO.setSectionPosition(sectionPosition);
            sectionBO.setListPosition(listPosition++);
            add(sectionBO);

            List<AssignmentBO> assignBOs = map.get(titleList.get(i));
            int length = assignBOs.size();
            for (int j = 0; j < length; j++) {
                AssignmentSection assignSectionBO = new AssignmentSection();
                AssignmentBO assignBO = assignBOs.get(j);
                assignSectionBO.setSection(false);
                assignSectionBO.setAssignBO(assignBO);
                assignSectionBO.setListPosition(listPosition++);
                assignSectionBO.setSectionPosition(sectionPosition);
                add(assignSectionBO);
            }
            sectionPosition++;
        }
    }

    private void clearData(){
        if (map != null){
            map.clear();
        }
        if (titleList != null){
            titleList.clear();
        }
        clear();
    }

    protected void prepareSections(int sectionsNumber) { }

    protected void onSectionAdded(Assignment section, int sectionPosition) { }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AssignmentSection assignSectionBO = getItem(position);

        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resId, null);
        } else {
            view = convertView;
        }

        switch (getItemViewType(position)){
            case _TYPE_ITEM:
                ImageView ivColor = (ImageView)view.findViewById(R.id.color);
                TextView tvTitle = (TextView)view.findViewById(R.id.title);
                TextView tvTime = (TextView)view.findViewById(R.id.footer1);
                TextView tvSubNum = (TextView)view.findViewById(R.id.sub_title);
                TextView tvProgress = (TextView)view.findViewById(R.id.footer2);

                AssignmentBO assignBO = assignSectionBO.getAssignBO();
                Assignment assignEntity = assignBO.getAssignEntity();
                String mColor = assignEntity.getAsnColor();
                if (!TextUtils.isEmpty(mColor)){
                    int pColor = Color.parseColor(mColor);
                    ivColor.setBackgroundColor(pColor);
                    tvTime.setTextColor(pColor);
                }

                String strAsnTime = TpTime.getExactTime(assignEntity.getAsnDate() + assignEntity.getAsnTime(),
                        TpTime.EXACT_TIME_TYPE_2);
                tvTitle.setText(assignEntity.getAsnTitle());
                tvTime.setText(strAsnTime);
                String strNum = assignBO.getCount() + " " + getContext().getString(R.string.asnd_num_tip);
                tvSubNum.setText(strNum);
                String strProg = assignEntity.getAsnProg() + "%";
                tvProgress.setText(strProg);
                view.setTag(assignBO);
                break;
            case _TYPE_SECTION:
                view.findViewById(R.id.item).setVisibility(View.GONE);
                TextView tvSection = (TextView) view.findViewById(R.id.section);
                tvSection.setVisibility(View.VISIBLE);
                tvSection.setText(assignSectionBO.getSectionName());
                view.setTag(null);
                break;
        }

        return view;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).isSection() ? _TYPE_SECTION : _TYPE_ITEM;
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType == _TYPE_SECTION;
    }
}
