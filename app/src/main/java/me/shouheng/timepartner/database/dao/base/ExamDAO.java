package me.shouheng.timepartner.database.dao.base;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import me.shouheng.timepartner.database.dao.SortType;
import me.shouheng.timepartner.models.entities.exam.Exam;
import me.shouheng.timepartner.utils.TpTime;

public class ExamDAO extends BaseDAO<Exam> {

    public static ExamDAO getInstance(Context context){
        return new ExamDAO(context);
    }

    private ExamDAO(Context context) {
        super(context);
    }

    @Override
    public void close() {
        super.close();
    }

    @Override
    public Exam get(long id) {
        Cursor cursor = db.rawQuery(" SELECT * FROM " + Exam.TABLE_NAME +
                " WHERE " + Exam.Columns.EXAM_ID + " = ? " +
                " AND " + Exam.Columns.ACCOUNT + " = ? " +
                " AND ( " + Exam.Columns.IN_TRASH + " IS NULL " +
                " OR " + Exam.Columns.IN_TRASH + " != 1) ",
                new String[]{String.valueOf(id), strAccount});
        Exam examEntity = null;
        if (cursor.moveToFirst()){
            examEntity = getEntity(cursor);
        }
        cursor.close();
        return examEntity;
    }

    @Override
    public List<Exam> get(long[] ids) {
        List<Exam> list = new ArrayList<>();
        for (long id : ids){
            Exam examEntity = get(id);
            if (examEntity != null) {
                list.add(get(id));
            }
        }
        return list;
    }

    /**
     * 通过课程的id获取全部的作业信息
     * @param classId 课程id
     * @return 全部作业信息 */
    @Override
    public List<Exam> gets(long classId) {
        return getEntities(db.rawQuery(" SELECT * FROM " + Exam.TABLE_NAME +
                " WHERE " + Exam.Columns.CLASS_ID + " = ? " +
                " AND " + Exam.Columns.ACCOUNT + " = ? " +
                " AND ( " + Exam.Columns.IN_TRASH + " IS NULL " +
                " OR " + Exam.Columns.IN_TRASH + " != 1) " +
                " ORDER BY " + Exam.Columns.EXAM_DATE + " DESC, " +
                Exam.Columns.EXAM_TIME + " DESC ",
                new String[]{String.valueOf(classId), strAccount}));
    }

    @Override
    public List<Exam> getOfDay(long millisOfDay) {
        return getEntities(db.rawQuery(" SELECT * FROM " + Exam.TABLE_NAME +
                " WHERE " + Exam.Columns.ACCOUNT + " = ? " +
                " AND " + Exam.Columns.EXAM_DATE + " = ? " + // 根据考试日期
                " AND ( " + Exam.Columns.IN_TRASH + " IS NULL " +
                " OR " + Exam.Columns.IN_TRASH + " != 1) " +
                " ORDER BY " + Exam.Columns.EXAM_DATE + " , " +
                Exam.Columns.EXAM_TIME,
                new String[]{strAccount, String.valueOf(millisOfDay)}));
    }

    @Override
    public List<Exam> getOfDay(long millisOfDay, SortType sortType) {
        if (sortType == SortType.DATE_INC) {
            return getOfDay(millisOfDay);
        } else {
            return getEntities(db.rawQuery(" SELECT * FROM " + Exam.TABLE_NAME +
                    " WHERE " + Exam.Columns.ACCOUNT + " = ? " +
                    " AND " + Exam.Columns.EXAM_DATE + " = ? " + // 根据考试日期
                    " AND ( " + Exam.Columns.IN_TRASH + " IS NULL " +
                    " OR " + Exam.Columns.IN_TRASH + " != 1) " +
                    " ORDER BY " + Exam.Columns.EXAM_DATE + " DESC, " +
                    Exam.Columns.EXAM_TIME + " DESC ",
                    new String[]{strAccount, String.valueOf(millisOfDay)}));
        }
    }

    @Override
    public List<Exam> getScope(long startMillis, long endMillis) {
        return getEntities(db.rawQuery("SELECT * FROM " + Exam.TABLE_NAME +
                " WHERE " + Exam.Columns.ACCOUNT + " = ? " +
                " AND " + Exam.Columns.EXAM_DATE + " >= ? " +
                " AND " + Exam.Columns.EXAM_DATE + " <= ? " +
                " AND ( " + Exam.Columns.IN_TRASH + " IS NULL " +
                " OR " + Exam.Columns.IN_TRASH + " != 1) " +
                " ORDER BY " + Exam.Columns.EXAM_DATE + " , " +
                Exam.Columns.EXAM_TIME ,
                new String[]{strAccount, String.valueOf(startMillis), String.valueOf(endMillis)}));
    }

    @Override
    public List<Exam> getScope(long startMillis, long endMillis, SortType sortType) {
        if (sortType == SortType.DATE_DESC) {
            return getEntities(db.rawQuery("SELECT * FROM " + Exam.TABLE_NAME +
                    " WHERE " + Exam.Columns.ACCOUNT + " = ? " +
                    " AND " + Exam.Columns.EXAM_DATE + " <= ? " +
                    " AND " + Exam.Columns.EXAM_TIME + " >= ? " +
                    " AND ( " + Exam.Columns.IN_TRASH + " IS NULL " +
                    " OR " + Exam.Columns.IN_TRASH + " != 1) " +
                    " ORDER BY " + Exam.Columns.EXAM_DATE + " DESC, " +
                    Exam.Columns.EXAM_TIME + " DESC ",
                    new String[]{strAccount, String.valueOf(endMillis), String.valueOf(startMillis)}));
        } else {
            return getScope(startMillis, endMillis);
        }
    }

    @Override
    public List<Exam> getAll(SortType sortType) {
        if (sortType == SortType.DATE_DESC) {
            return getAll();
        } else {
            return getEntities(db.rawQuery(" SELECT * FROM " + Exam.TABLE_NAME +
                    " WHERE " + Exam.Columns.ACCOUNT + " = ? " +
                    " AND ( " + Exam.Columns.IN_TRASH + " IS NULL " +
                    " OR " + Exam.Columns.IN_TRASH + " != 1) " +
                    " ORDER BY " + Exam.Columns.EXAM_DATE + " , " +
                    Exam.Columns.EXAM_TIME,
                    new String[]{strAccount}));
        }
    }

    @Override
    public List<Exam> getAll() {
        return getEntities(db.rawQuery(" SELECT * FROM " + Exam.TABLE_NAME +
                " WHERE " + Exam.Columns.ACCOUNT + " = ? " +
                " AND ( " + Exam.Columns.IN_TRASH + " IS NULL " +
                " OR " + Exam.Columns.IN_TRASH + " != 1) " +
                " ORDER BY " + Exam.Columns.EXAM_DATE + " DESC, " +
                Exam.Columns.EXAM_TIME + " DESC ",
                new String[]{strAccount}));
    }

    @Override
    public List<Exam> getOverdue(SortType sortType) {
        if (sortType == SortType.DATE_DESC){
            return getOverdue();
        } else {
            long millisOfCurrentDate = TpTime.millisOfCurrentDate();
            return getEntities(db.rawQuery(" SELECT * FROM " + Exam.TABLE_NAME +
                    " WHERE " + Exam.Columns.ACCOUNT + " = ? " +
                    " AND " + Exam.Columns.EXAM_DATE + " >= ? " +
                    " AND ( " + Exam.Columns.IN_TRASH + " IS NULL " +
                    " OR " + Exam.Columns.IN_TRASH + " != 1) " +
                    " ORDER BY " + Exam.Columns.EXAM_DATE + " , " +
                    Exam.Columns.EXAM_TIME,
                    new String[]{strAccount, String.valueOf(millisOfCurrentDate)}));
        }
    }

    @Override
    public List<Exam> getOverdue() {
        long millisOfCurrentDate = TpTime.millisOfCurrentDate();
        return getEntities(db.rawQuery(" SELECT * FROM " + Exam.TABLE_NAME +
                " WHERE " + Exam.Columns.ACCOUNT + " = ? " +
                " AND " + Exam.Columns.EXAM_DATE + " >= ? " +
                " AND ( " + Exam.Columns.IN_TRASH + " IS NULL " +
                " OR " + Exam.Columns.IN_TRASH + " != 1) " +
                " ORDER BY " + Exam.Columns.EXAM_DATE + " DESC, " +
                Exam.Columns.EXAM_TIME + " DESC ",
                new String[]{strAccount, String.valueOf(millisOfCurrentDate)}));
    }

    @Override
    public List<Exam> getQuery(String query) {
        return getEntities(db.rawQuery("SELECT * FROM " + Exam.TABLE_NAME +
                " WHERE " + Exam.Columns.ACCOUNT + " = ? " +
                " AND " + Exam.Columns.EXAM_TITLE + " LIKE ?" +
                " AND ( " + Exam.Columns.IN_TRASH + " IS NULL " +
                " OR " + Exam.Columns.IN_TRASH + " != 1) " +
                " ORDER BY " + Exam.Columns.EXAM_DATE + " DESC, " +
                Exam.Columns.EXAM_TIME + " DESC ",
                new String[]{strAccount, "%" + query + "%"}));
    }

    @Override
    public List<Exam> getTrash() {
        return getEntities(db.rawQuery(" SELECT * FROM " + Exam.TABLE_NAME +
                " WHERE " + Exam.Columns.ACCOUNT + " = ? " +
                " AND " + Exam.Columns.IN_TRASH + " = 1 " +
                " ORDER BY " + Exam.Columns.EXAM_DATE + " DESC, " +
                Exam.Columns.EXAM_TIME + " DESC",
                new String[]{strAccount}));
    }

    @Override
    public void insert(Exam examEntity) {
        db.insert(Exam.TABLE_NAME, null, getCV(examEntity));
    }

    @Override
    public void delete(Exam examEntity) {
        db.execSQL(" DELETE FROM " + Exam.TABLE_NAME +
                " WHERE " + Exam.Columns.EXAM_ID + " = ? " +
                " AND " + Exam.Columns.ACCOUNT + " = ? ",
                new String[]{String.valueOf(examEntity.getExamId()), strAccount});
    }

    @Override
    public void delete(List<Exam> list) {
        for (Exam examEntity : list){
            delete(examEntity);
        }
    }

    @Override
    public void trash(Exam examEntity) {
        trashOrRecover(examEntity, 1);
    }

    @Override
    public void trash(List<Exam> list) {
        for (Exam examEntity : list){
            trash(examEntity);
        }
    }

    @Override
    public void recover(Exam examEntity) {
        trashOrRecover(examEntity, 0);
    }

    @Override
    public void recover(List<Exam> list) {
        for (Exam examEntity : list){
            recover(examEntity);
        }
    }

    private Exam getEntity(Cursor mCursor){
        Exam examEntity = new Exam();
        examEntity.setClsId(mCursor.getLong(mCursor.getColumnIndex(Exam.Columns.CLASS_ID)));
        examEntity.setAccount(mCursor.getString(mCursor.getColumnIndex(Exam.Columns.ACCOUNT)));
        examEntity.setExamTitle(mCursor.getString(mCursor.getColumnIndex(Exam.Columns.EXAM_TITLE)));
        examEntity.setExamContent(mCursor.getString(mCursor.getColumnIndex(Exam.Columns.EXAM_CONTENT)));
        examEntity.setExamComments(mCursor.getString(mCursor.getColumnIndex(Exam.Columns.EXAM_COMMENT)));
        examEntity.setExamRoom(mCursor.getString(mCursor.getColumnIndex(Exam.Columns.ROOM)));
        examEntity.setExamSeat(mCursor.getString(mCursor.getColumnIndex(Exam.Columns.SEAT)));
        examEntity.setExamDate(mCursor.getLong(mCursor.getColumnIndex(Exam.Columns.EXAM_DATE)));
        examEntity.setExamTime(mCursor.getInt(mCursor.getColumnIndex(Exam.Columns.EXAM_TIME)));

        examEntity.setAddedDate(mCursor.getLong(mCursor.getColumnIndex(Exam.Columns.ADDED_DATE)));
        examEntity.setAddedTime(mCursor.getInt(mCursor.getColumnIndex(Exam.Columns.ADDED_TIME)));
        examEntity.setLastModifyDate(mCursor.getLong(mCursor.getColumnIndex(Exam.Columns.LAST_MODIFY_DATE)));
        examEntity.setLastModifyTime(mCursor.getInt(mCursor.getColumnIndex(Exam.Columns.LAST_MODIFY_TIME)));

        examEntity.setDuration(mCursor.getInt(mCursor.getColumnIndex(Exam.Columns.DURATION)));
        examEntity.setInTrash(mCursor.getInt(mCursor.getColumnIndex(Exam.Columns.IN_TRASH)));
        examEntity.setSynced(mCursor.getInt(mCursor.getColumnIndex(Exam.Columns.SYNCED)));
        return examEntity;
    }

    private List<Exam> getEntities(Cursor mCursor){
        List<Exam> list = new ArrayList<>();
        if (mCursor.moveToFirst()){
            do{
                list.add(getEntity(mCursor));
            } while (mCursor.moveToNext());
        }
        mCursor.close();
        return list;
    }

    private void trashOrRecover(Exam examEntity , int type){
        if (type != 0 && type != 1)
            return;
        db.execSQL(" UPDATE " + Exam.TABLE_NAME +
                " SET " + Exam.Columns.IN_TRASH + " = ? " +
                " WHERE " + Exam.Columns.ACCOUNT + " = ? " +
                " AND " + Exam.Columns.EXAM_ID + " = ? ",
                new String[]{String.valueOf(type), strAccount, String.valueOf(examEntity)});
    }

    private ContentValues getCV(Exam examEntity){
        ContentValues values = new ContentValues();
        values.put(Exam.Columns.ACCOUNT, strAccount);
        values.put(Exam.Columns.CLASS_ID, examEntity.getClsId());
        values.put(Exam.Columns.EXAM_ID, examEntity.getExamId());
        values.put(Exam.Columns.EXAM_TITLE, examEntity.getExamTitle());
        values.put(Exam.Columns.EXAM_DATE, examEntity.getExamDate());
        values.put(Exam.Columns.EXAM_TIME, examEntity.getExamTime());

        values.put(Exam.Columns.ADDED_DATE, examEntity.getAddedDate());
        values.put(Exam.Columns.ADDED_TIME, examEntity.getAddedTime());
        values.put(Exam.Columns.LAST_MODIFY_DATE, examEntity.getLastModifyDate());
        values.put(Exam.Columns.LAST_MODIFY_TIME, examEntity.getLastModifyTime());

        values.put(Exam.Columns.EXAM_CONTENT, examEntity.getExamContent());
        values.put(Exam.Columns.SEAT, examEntity.getExamSeat());
        values.put(Exam.Columns.ROOM, examEntity.getExamRoom());
        values.put(Exam.Columns.DURATION, examEntity.getDuration());
        values.put(Exam.Columns.EXAM_COMMENT, examEntity.getExamComments());
        return values;
    }
}
