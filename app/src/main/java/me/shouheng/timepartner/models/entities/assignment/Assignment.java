package me.shouheng.timepartner.models.entities.assignment;

import me.shouheng.timepartner.models.entities.Entity;
import me.shouheng.timepartner.utils.TpTime;

public class Assignment extends Entity {

    public static final String TABLE_NAME = "assign_table";

    public static final class Columns extends Entity.Columns{
        public final static String ASSIGN_ID = "assign_id";
        public final static String ASSIGN_TITLE = "assign_title";
        public final static String ASSIGN_CONTENT = "assign_content";
        public final static String ASSIGN_DATE = "assign_date";
        public final static String ASSIGN_TIME = "assign_time";
        public final static String ASSIGN_PROGRESS = "assign_progress";
        public final static String ASSIGN_COMMENT = "assign_comment";
        public final static String ASSIGN_COLOR = "assign_color";
    }

    private long asnId;

    private String asnTitle;

    private String asnContent;

    private long asnDate;

    private int asnTime;

    private int asnProg;

    private String asnComment;

    private String asnColor;

    public long getAsnId() {
        return asnId;
    }

    public void setAsnId(long asnId) {
        this.asnId = asnId;
    }

    public String getAsnTitle() {
        return asnTitle;
    }

    public void setAsnTitle(String asnTitle) {
        this.asnTitle = asnTitle;
    }

    public String getAsnContent() {
        return asnContent;
    }

    public void setAsnContent(String asnContent) {
        this.asnContent = asnContent;
    }

    public long getAsnDate() {
        return asnDate;
    }

    public void setAsnDate(long asnDate) {
        this.asnDate = asnDate;
    }

    public int getAsnTime() {
        return asnTime;
    }

    public void setAsnTime(int asnTime) {
        this.asnTime = asnTime;
    }

    public int getAsnProg() {
        return asnProg;
    }

    public void setAsnProg(int asnProg) {
        this.asnProg = asnProg;
    }

    public String getAsnComment() {
        return asnComment;
    }

    public void setAsnComment(String asnComment) {
        this.asnComment = asnComment;
    }

    public String getAsnColor() {
        return asnColor;
    }

    public void setAsnColor(String asnColor) {
        this.asnColor = asnColor;
    }

    @Override
    public String toString() {
        return "id:" + id + " , " +
                "account:" + account + " , " +
                "assign_id:" + asnId + " , " +
                "assign_title:" + asnTitle + " , " +
                "assign_content:" + asnContent + " , " +
                "assign_date:" + TpTime.getFormatDate(asnDate) + " , " +
                "assign_time:" + TpTime.getFormatTime(asnTime) + " , " +
                "assign_progress:" + asnProg + " , " +
                "assign_comment:" + asnComment + " , " +
                "assign_color:" + asnColor + " , " +
                "in_trash" + inTrash + " , " +
                "synced:" + synced;
    }
}
