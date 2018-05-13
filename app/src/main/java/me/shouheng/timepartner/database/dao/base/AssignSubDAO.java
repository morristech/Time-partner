package me.shouheng.timepartner.database.dao.base;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.LinkedList;
import java.util.List;

import me.shouheng.timepartner.database.dao.SortType;
import me.shouheng.timepartner.models.entities.assignment.SubAssignment;

/**
 * Created by wangshouheng on 2017/1/14. */
public class AssignSubDAO extends BaseDAO<SubAssignment> {

    public static AssignSubDAO getInstance(Context context){
        return new AssignSubDAO(context);
    }

    private AssignSubDAO(Context context) {
        super(context);
    }

    @Override
    public void close() {
        super.close();
    }

    @Override
    public List<SubAssignment> gets(long asnId) {
        return getEntities(db.rawQuery(" SELECT * FROM " + SubAssignment.TABLE_NAME +
                " WHERE " + SubAssignment.Columns.ASSIGN_ID + " = ? " +
                " AND " + SubAssignment.Columns.ACCOUNT + " = ? ",
                new String[]{String.valueOf(asnId), strAccount}));
    }

    @Override
    public List<SubAssignment> gets(long asnId, SortType sortType) {
        if (sortType == SortType.DATE_DESC){
            return getEntities(db.rawQuery(" SELECT * FROM " + SubAssignment.TABLE_NAME +
                    " WHERE " + SubAssignment.Columns.ASSIGN_ID + " = ? " +
                    " AND " + SubAssignment.Columns.ACCOUNT + " = ? ",
                    new String[]{String.valueOf(asnId), strAccount}));
        } else {
            return gets(asnId);
        }
    }

    private SubAssignment getEntity(Cursor cursor){
        SubAssignment subEntity = new SubAssignment();
        subEntity.setAsnId(cursor.getLong(cursor.getColumnIndex(SubAssignment.Columns.SUB_ID)));
        subEntity.setSubContent(cursor.getString(cursor.getColumnIndex(SubAssignment.Columns.SUB_CONTENT)));
        subEntity.setAccount(cursor.getString(cursor.getColumnIndex(SubAssignment.Columns.ACCOUNT)));
        subEntity.setSubCompleted(cursor.getInt(cursor.getColumnIndex(SubAssignment.Columns.SUB_COMPLETED)));
        subEntity.setSynced(cursor.getInt(cursor.getColumnIndex(SubAssignment.Columns.SYNCED)));
        subEntity.setAddedDate(cursor.getLong(cursor.getColumnIndex(SubAssignment.Columns.ADDED_DATE)));
        subEntity.setAddedTime(cursor.getInt(cursor.getColumnIndex(SubAssignment.Columns.ADDED_TIME)));
        subEntity.setLastModifyDate(cursor.getLong(cursor.getColumnIndex(SubAssignment.Columns.LAST_MODIFY_DATE)));
        subEntity.setLastModifyTime(cursor.getInt(cursor.getColumnIndex(SubAssignment.Columns.LAST_MODIFY_TIME)));
        return subEntity;
    }

    private List<SubAssignment> getEntities(Cursor mCursor){
        List<SubAssignment> subEntities = new LinkedList<>();
        if (mCursor.moveToFirst()){
            do {
                SubAssignment subEntity = getEntity(mCursor);
                subEntities.add(subEntity);
            } while (mCursor.moveToNext());
        }
        mCursor.close();
        return subEntities;
    }

    @Override
    public void insert(List<SubAssignment> list) {
        for (SubAssignment assignSubEntity : list){
            db.insert(SubAssignment.TABLE_NAME, null, getCV(assignSubEntity));
        }
    }

    private ContentValues getCV(SubAssignment assignSubEntity){
        ContentValues values = new ContentValues();
        values.put(SubAssignment.Columns.ACCOUNT, strAccount);
        values.put(SubAssignment.Columns.ASSIGN_ID, assignSubEntity.getAsnId());
        values.put(SubAssignment.Columns.SUB_CONTENT, assignSubEntity.getSubContent());
        values.put(SubAssignment.Columns.SUB_COMPLETED, assignSubEntity.getSubCompleted());
        values.put(SubAssignment.Columns.ADDED_DATE, assignSubEntity.getAddedDate());
        values.put(SubAssignment.Columns.ADDED_TIME, assignSubEntity.getAddedTime());
        values.put(SubAssignment.Columns.LAST_MODIFY_DATE, assignSubEntity.getLastModifyDate());
        values.put(SubAssignment.Columns.LAST_MODIFY_TIME, assignSubEntity.getLastModifyTime());
        values.put(SubAssignment.Columns.SYNCED, assignSubEntity.getSynced());
        return values;
    }

    @Override
    public void update(SubAssignment assignSubEntity) {
        long subId = assignSubEntity.getSubId();
        db.update(SubAssignment.TABLE_NAME, getCV(assignSubEntity),
                SubAssignment.Columns.ACCOUNT + " = ? " +
                        " AND " + SubAssignment.Columns.SUB_ID  + " = ? ",
                new String[]{strAccount, String.valueOf(subId)});
    }

    @Override
    public void update(List<SubAssignment> list) {
        if (list.size() == 0)
            return;
        long asnId = list.get(0).getAsnId();
        db.beginTransaction();
        db.execSQL(" DELETE FROM " + SubAssignment.TABLE_NAME +
                " WHERE " + SubAssignment.Columns.ASSIGN_ID + " = ? " +
                " AND " + SubAssignment.Columns.ACCOUNT + " = ?",
                new String[]{String.valueOf(asnId), strAccount});
        insert(list);
        db.setTransactionSuccessful();
        db.endTransaction();
    }
}
