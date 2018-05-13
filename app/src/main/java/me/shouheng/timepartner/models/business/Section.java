package me.shouheng.timepartner.models.business;

/**
 * Created by wangshouheng on 2017/1/14. */
public abstract class Section {

    /**
     * 判断是否是section */
    private boolean isSection;

    /**
     * 获取section的名字 */
    private String sectionName;

    /**
     * 在所有Section中的位置 */
    private int sectionPosition;

    /**
     * 在所有list中的位置 */
    private int listPosition;

    public boolean isSection() {
        return isSection;
    }

    public void setSection(boolean section) {
        isSection = section;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public int getSectionPosition() {
        return sectionPosition;
    }

    public void setSectionPosition(int sectionPosition) {
        this.sectionPosition = sectionPosition;
    }

    public int getListPosition() {
        return listPosition;
    }

    public void setListPosition(int listPosition) {
        this.listPosition = listPosition;
    }

    @Override
    public String toString() {
        return "isSection:" + isSection + "\n" +
                "sectionName:" + sectionName + "\n" +
                "sectionPosition:" + sectionPosition + "\n" +
                "listPosition:" + listPosition;
    }
}
