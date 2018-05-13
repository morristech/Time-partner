package me.shouheng.timepartner.models.entities.assignment;

import me.shouheng.timepartner.models.entities.Entity;

public class SubAssignment extends Entity {

    public final static String TABLE_NAME = "assign_sub_table";

    public final static class Columns extends Entity.Columns{
        public static final String SUB_ID = "sub_id";
        public static final String ASSIGN_ID = "assign_id";
        public static final String SUB_CONTENT = "sub_content";
        public static final String SUB_COMPLETED = "sub_completed";
    }

    private long asnId;

    private long subId;

    private String subContent;

    private int subCompleted;

    public long getAsnId() {
        return asnId;
    }

    public void setAsnId(long asnId) {
        this.asnId = asnId;
    }

    public long getSubId() {
        return subId;
    }

    public void setSubId(long subId) {
        this.subId = subId;
    }

    public String getSubContent() {
        return subContent;
    }

    public void setSubContent(String subContent) {
        this.subContent = subContent;
    }

    public int getSubCompleted() {
        return subCompleted;
    }

    public void setSubCompleted(int subCompleted) {
        this.subCompleted = subCompleted;
    }

    @Override
    public String toString() {
        return Columns.ID + ":" + id +  " , " +
                Columns.ACCOUNT + ":" + account +  " , " +
                Columns.SUB_ID + ":" + subId +  " , " +
                Columns.ASSIGN_ID + ":" + asnId +  " , " +
                Columns.SUB_CONTENT + ":" + subContent +  " , " +
                Columns.SUB_COMPLETED + ":" + subCompleted +  " , " +
                Columns.SYNCED;
    }
}
