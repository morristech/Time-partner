package me.shouheng.timepartner.database.dao.base;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.LinkedList;
import java.util.List;

import me.shouheng.timepartner.models.entities.tpclass.ClassDetail;

public class ClassDetailDAO extends BaseDAO<ClassDetail> {

    public static ClassDetailDAO getInstance(Context context){
        return new ClassDetailDAO(context);
    }

    private ClassDetailDAO(Context context) {
        super(context);
    }

    @Override
    public void close() {
        super.close();
    }

    @Override
    public List<ClassDetail> gets(long clsId) {
        List<ClassDetail> list = new LinkedList<>();
        Cursor mCursor = db.rawQuery(" SELECT * FROM " + ClassDetail.TABLE_NAME +
                " WHERE " + ClassDetail.Columns.ACCOUNT + " = ? " +
                " AND " + ClassDetail.Columns.CLASS_ID + " = ? " +
                " ORDER BY " + ClassDetail.Columns.START_TIME + " , " +
                ClassDetail.Columns.END_TIME,
                new String[]{strAccount, String.valueOf(clsId)});
        if (mCursor.moveToFirst()){
            do {
                list.add(getEntity(mCursor));
            } while (mCursor.moveToNext());
        }
        mCursor.close();
        return list;
    }

    private ClassDetail getEntity(Cursor mCursor) {
        ClassDetail classDetailEntity = new ClassDetail();
        classDetailEntity.setAccount(mCursor.getString(mCursor.getColumnIndex(ClassDetail.Columns.ACCOUNT)));
        classDetailEntity.setClassId(mCursor.getLong(mCursor.getColumnIndex(ClassDetail.Columns.CLASS_ID)));
        classDetailEntity.setDetailId(mCursor.getLong(mCursor.getColumnIndex(ClassDetail.Columns.DETAIL_ID)));
        classDetailEntity.setStartTime(mCursor.getInt(mCursor.getColumnIndex(ClassDetail.Columns.START_TIME)));
        classDetailEntity.setEndTime(mCursor.getInt(mCursor.getColumnIndex(ClassDetail.Columns.END_TIME)));
        classDetailEntity.setRoom(mCursor.getString(mCursor.getColumnIndex(ClassDetail.Columns.ROOM)));
        classDetailEntity.setTeacher(mCursor.getString(mCursor.getColumnIndex(ClassDetail.Columns.TEACHER)));
        classDetailEntity.setWeek(mCursor.getString(mCursor.getColumnIndex(ClassDetail.Columns.WEEK)));
        classDetailEntity.setAddedDate(mCursor.getLong(mCursor.getColumnIndex(ClassDetail.Columns.ADDED_DATE)));
        classDetailEntity.setAddedTime(mCursor.getInt(mCursor.getColumnIndex(ClassDetail.Columns.ADDED_TIME)));
        classDetailEntity.setLastModifyDate(mCursor.getLong(mCursor.getColumnIndex(ClassDetail.Columns.LAST_MODIFY_DATE)));
        classDetailEntity.setLastModifyTime(mCursor.getInt(mCursor.getColumnIndex(ClassDetail.Columns.LAST_MODIFY_TIME)));
        classDetailEntity.setSynced(mCursor.getInt(mCursor.getColumnIndex(ClassDetail.Columns.SYNCED)));
        return classDetailEntity;
    }

    @Override
    public void insert(List<ClassDetail> list) {
        for (ClassDetail entity : list){
            ContentValues values = new ContentValues();
            values.put(ClassDetail.Columns.ACCOUNT, entity.getAccount());
            values.put(ClassDetail.Columns.CLASS_ID, entity.getClassId());
            values.put(ClassDetail.Columns.START_TIME, entity.getStartTime());
            values.put(ClassDetail.Columns.END_TIME, entity.getEndTime());
            values.put(ClassDetail.Columns.ROOM, entity.getRoom());
            values.put(ClassDetail.Columns.TEACHER, entity.getTeacher());
            values.put(ClassDetail.Columns.WEEK, entity.getWeek());
            db.insert(ClassDetail.TABLE_NAME, null, values);
            values.clear();
        }
    }

    /**
     * 使用事务进行更新操作，先删后加
     * @param list 传入的集合必须对应同一主课程 */
    @Override
    public void update(List<ClassDetail> list) {
        if (list.size() == 0)
            return;
        db.beginTransaction();
        db.execSQL(" DELETE FROM " + ClassDetail.TABLE_NAME +
                " WHERE " + ClassDetail.Columns.CLASS_ID + " = ? " +
                " AND " + ClassDetail.Columns.ACCOUNT + " = ?",
                new String[]{String.valueOf(list.get(0).getClassId()), strAccount});
        insert(list);
        db.setTransactionSuccessful();
        db.endTransaction();
    }
}
