package me.shouheng.timepartner.adapters;

import android.content.Context;
import android.graphics.Color;
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

import me.shouheng.timepartner.models.business.tpclass.ClassBO;
import me.shouheng.timepartner.models.business.tpclass.ClassSection;
import me.shouheng.timepartner.models.entities.tpclass.ClassEntity;
import me.shouheng.timepartner.R;
import me.shouheng.timepartner.utils.TpTime;
import me.shouheng.timepartner.widget.listview.PinnedSectionListView;

/**
 * 创建实体之后 依次调用setClassEntities() 和 reloadData() 方法
 * 分别用来 为适配器添加数据 重新装载数据
 * 修改数据的时候，只需要调用 setXXX() 然后再调用 reload() 即可 */
public class ClassesSimpleAdapter extends ArrayAdapter<ClassSection> implements PinnedSectionListView.PinnedSectionListAdapter  {

    private int resId; //组件布局资源

    private List<String> titleList = new ArrayList<>();  //section集合

    private Map<String, List<ClassBO>> map = new HashMap<>();  //每个section对应的list集合

    private List<ClassBO> classBOs = new LinkedList<>();

    private final int _TYPE_SECTION = 0;
    private final int _TYPE_ITEM = 1;

    public ClassesSimpleAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
        this.resId = resource;
    }

    public void setClassBOs(List<ClassBO> classBOs){
        this.classBOs = classBOs;
    }

    public void reloadData() {
        clearData();

        for (ClassBO classBO : classBOs){
            ClassEntity classEntity = classBO.getClassEntity();

            String secTitle = TpTime.getDate(classEntity.getStartDate(), TpTime.DATE_TYPE_5)
                    + " - "
                    + TpTime.getDate(classEntity.getEndDate(), TpTime.DATE_TYPE_5);

            if (map.containsKey(secTitle)) {
                map.get(secTitle).add(classBO);
            } else {
                titleList.add(secTitle);
                List<ClassBO> classBOs = new LinkedList<>();  // 构建section内部集合
                classBOs.add(classBO);
                map.put(secTitle, classBOs);
            }
        }

        int sectionPosition = 0, listPosition = 0;
        for (int i = 0;i < map.size();i++) {
            ClassSection sectionBO = new ClassSection();
            sectionBO.setSection(true);
            sectionBO.setSectionName(titleList.get(i));
            sectionBO.setSectionPosition(sectionPosition);
            sectionBO.setListPosition(listPosition++);
            add(sectionBO);

            List<ClassBO> classBOs = map.get(titleList.get(i));
            int length = classBOs.size();
            for (int j = 0; j < length; j++) {
                ClassSection classSectionBO = new ClassSection();
                ClassBO classBO = classBOs.get(j);
                classSectionBO.setSection(false);
                classSectionBO.setClassBO(classBO);
                classSectionBO.setListPosition(listPosition++);
                classSectionBO.setSectionPosition(sectionPosition);
                add(classSectionBO);
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

    protected void onSectionAdded(ClassEntity section, int sectionPosition) { }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ClassSection classSectionBO = getItem(position);
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resId, null);
        } else {
            view = convertView;
        }
        switch (getItemViewType(position)){
            case _TYPE_ITEM:
                ImageView ivColor = (ImageView) view.findViewById(R.id.color);
                TextView tvTitle = (TextView) view.findViewById(R.id.title);
                TextView tvDetailNum = (TextView) view.findViewById(R.id.sub_title);
                TextView tvSDate = (TextView) view.findViewById(R.id.footer1);
                TextView tvEDate = (TextView) view.findViewById(R.id.footer2);

                ClassBO classBO = classSectionBO.getClassBO();
                ClassEntity classEntity = classBO.getClassEntity();
                String mColor = classEntity.getClsColor();
                if (!TextUtils.isEmpty(mColor)){
                    int pColor = Color.parseColor(mColor);
                    ivColor.setBackgroundColor(pColor);
                    tvSDate.setTextColor(pColor);
                    tvEDate.setTextColor(pColor);
                }

                tvTitle.setText(classEntity.getClsName());

                String strNum = classBO.getCount() + " " + getContext().getString(R.string.cls_list_item_detail);
                tvDetailNum.setText(strNum);

                tvSDate.setText(TpTime.getDate(classEntity.getStartDate(), TpTime.DATE_TYPE_1));
                tvEDate.setText(TpTime.getDate(classEntity.getEndDate(), TpTime.DATE_TYPE_1));

                view.setTag(classBO);
                break;
            case _TYPE_SECTION:
                view.findViewById(R.id.item).setVisibility(View.GONE);
                TextView tvSection = (TextView) view.findViewById(R.id.section);
                tvSection.setVisibility(View.VISIBLE);
                tvSection.setText(classSectionBO.getSectionName());
                view.setTag(null);

                break;
        }

        return view;
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
