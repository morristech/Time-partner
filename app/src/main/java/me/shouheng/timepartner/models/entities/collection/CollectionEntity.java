package me.shouheng.timepartner.models.entities.collection;

import com.sina.weibo.sdk.api.share.Base;

import me.shouheng.timepartner.utils.TpTime;

public class CollectionEntity extends BaseCollection {

    public static class Columns extends BaseCollection.Columns{
        public final static String COUNT = "note_count";
    }

    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return BaseCollection.Columns.ID + ":" + id + " , " +
                BaseCollection.Columns.ACCOUNT + ":" + account + " , " +
                Columns.COUNT + ":" + count + " , " +
                BaseCollection.Columns.ADDED_DATE + ":" + TpTime.getFormatDate(addedDate) + " , " +
                BaseCollection.Columns.ADDED_TIME + ":" + TpTime.getFormatTime(addedTime) + " , " +
                BaseCollection.Columns.CLN_ID + ":" + clnId + " , " +
                BaseCollection.Columns.CLN_TITLE + ":" + clnTitle + " , " +
                BaseCollection.Columns.CLN_COLOR + ":" + clnColor + " , " +
                BaseCollection.Columns.SYNCED + ":" +  synced;
    }
}
