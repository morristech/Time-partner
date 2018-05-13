package me.shouheng.timepartner.models.entities.collection;

import me.shouheng.timepartner.models.entities.Entity;
import me.shouheng.timepartner.utils.TpTime;

/**
 * Created by wangshouheng on 2017/1/12. */
public class BaseCollection extends Entity {

    public final static String TABLE_NAME = "note_collection_table";

    public static class Columns extends Entity.Columns{
        public final static String CLN_ID = "cln_id";
        public final static String CLN_TITLE = "cln_title";
        public final static String CLN_COLOR = "cln_color";
    }

    protected long clnId;

    protected String clnTitle;

    protected String clnColor;

    public long getClnId() {
        return clnId;
    }

    public void setClnId(long clnId) {
        this.clnId = clnId;
    }

    public String getClnTitle() {
        return clnTitle;
    }

    public void setClnTitle(String clnTitle) {
        this.clnTitle = clnTitle;
    }

    public String getClnColor() {
        return clnColor;
    }

    public void setClnColor(String clnColor) {
        this.clnColor = clnColor;
    }

    @Override
    public String toString() {
        return Columns.ID + ":" + id + " , " +
                Columns.ACCOUNT + ":" + account + " , " +
                Columns.ADDED_DATE + ":" + TpTime.getFormatDate(addedDate) + " , " +
                Columns.ADDED_TIME + ":" + TpTime.getFormatTime(addedTime) + " , " +
                Columns.CLN_ID + ":" + clnId + " , " +
                Columns.CLN_TITLE + ":" + clnTitle + " , " +
                Columns.CLN_COLOR + ":" + clnColor + " , " +
                Columns.SYNCED + ":" +  synced;
    }
}
