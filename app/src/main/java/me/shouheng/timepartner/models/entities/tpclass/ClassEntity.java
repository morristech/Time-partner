package me.shouheng.timepartner.models.entities.tpclass;

import me.shouheng.timepartner.models.entities.Entity;
import me.shouheng.timepartner.utils.TpTime;

public class ClassEntity extends Entity {

    public final static String TABLE_NAME = "class_table";

    public enum TrashType {
        WITH_TASK_EXAM, WITHOUT_TASK_EXAM
    }

    public enum DelType {
        WITH_TASK_EXAM, WITHOUT_TASK_EXAM
    }

    public final static class Columns extends Entity.Columns{
        public static final String CLS_ID = "class_id";
        public static final String CLS_NAME = "class_name";
        public static final String CLS_START_DATE = "class_start_date";
        public static final String CLS_END_DATE = "class_end_date";
        public static final String CLS_COLOR = "class_color";
    }

    private long clsId;

    private String clsName;

    private long startDate;

    private long endDate;

    private String clsColor;

    public long getClsId() {
        return clsId;
    }

    public void setClsId(long clsId) {
        this.clsId = clsId;
    }

    public String getClsName() {
        return clsName;
    }

    public void setClsName(String clsName) {
        this.clsName = clsName;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public String getClsColor() {
        return clsColor;
    }

    public void setClsColor(String clsColor) {
        this.clsColor = clsColor;
    }

    @Override
    public String toString() {
        return  "id:" + id + " , " +
                "account:" + account + " , " +
                "clsId:"+ clsId + " , " +
                "name:" + clsName + " , " +
                "startDate:" + TpTime.getFormatDate(startDate) + " , " +
                "endDate:" + TpTime.getFormatDate(endDate) + " , " +
                "clsColor:" + clsColor + " , " +
                "inTrash:" + inTrash + " , " +
                "synced:" + synced;
    }

}
