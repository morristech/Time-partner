package me.shouheng.timepartner.database.dao.base;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.LinkedList;
import java.util.List;

import me.shouheng.timepartner.database.dao.SortType;
import me.shouheng.timepartner.models.entities.assignment.Assignment;
import me.shouheng.timepartner.models.entities.assignment.SubAssignment;
import me.shouheng.timepartner.utils.TpTime;

/**
 * Created by wangshouheng on 2017/1/14. */
public class AssignDAO extends BaseDAO<Assignment> {

    public static AssignDAO getInstance(Context context){
        return new AssignDAO(context);
    }

    private AssignDAO(Context context) {
        super(context);
    }

    @Override
    public void close() {
        super.close();
    }

    @Override
    public Assignment get(long asnId) {
        Cursor cursor = db.rawQuery(" SELECT * FROM " + Assignment.TABLE_NAME +
                " WHERE " + Assignment.Columns.ASSIGN_ID + " = ? " +
                " AND " + Assignment.Columns.ACCOUNT + " = ?",
                new String[]{String.valueOf(asnId), strAccount});
        Assignment assignEntity = null;
        if (cursor.moveToFirst()){
            assignEntity = getEntity(cursor);
        }
        cursor.close();
        return assignEntity;
    }

    @Override
    public List<Assignment> get(long[] ids) {
        List<Assignment> assignEntities = new LinkedList<>();
        for (long id : ids){
            Assignment assignEntity = get(id);
            if (assignEntity != null) {
                assignEntities.add(get(id));
            }
        }
        return assignEntities;
    }

    @Override
    public List<Assignment> getOfDay(long millisOfDay) {
        return getEntities(db.rawQuery(" SELECT * FROM " + Assignment.TABLE_NAME +
                " WHERE " + Assignment.Columns.ACCOUNT + " = ? " +
                " AND " + Assignment.Columns.ASSIGN_DATE + " = ? " +
                " AND ( " + Assignment.Columns.IN_TRASH + " IS NULL " +
                " OR " + Assignment.Columns.IN_TRASH + " != 1) " +
                " ORDER BY " + Assignment.Columns.ASSIGN_TIME + " DESC ",
                new String[]{strAccount, String.valueOf(millisOfDay)}));
    }

    @Override
    public List<Assignment> getOfDay(long millisOfDay, SortType sortType) {
        if (sortType == SortType.DATE_DESC){
            return getOfDay(millisOfDay);
        } else {
            return getEntities(db.rawQuery(" SELECT * FROM " + Assignment.TABLE_NAME +
                    " WHERE " + Assignment.Columns.ACCOUNT + " = ? " +
                    " AND " + Assignment.Columns.ASSIGN_DATE + " = ? " +
                    " AND ( " + Assignment.Columns.IN_TRASH + " IS NULL " +
                    " OR " + Assignment.Columns.IN_TRASH + " != 1) " +
                    " ORDER BY " + Assignment.Columns.ASSIGN_TIME,
                    new String[]{strAccount, String.valueOf(millisOfDay)}));
        }
    }

    @Override
    public List<Assignment> getScope(long startMillis, long endMillis) {
        return getEntities(db.rawQuery("SELECT * FROM " + Assignment.TABLE_NAME +
                " WHERE " + Assignment.Columns.ACCOUNT + " = ? " +
                " AND " + Assignment.Columns.ASSIGN_DATE + " >= ? " +
                " AND " + Assignment.Columns.ASSIGN_DATE + " <= ? " +
                " AND ( " + Assignment.Columns.IN_TRASH + " IS NULL " +
                " OR " + Assignment.Columns.IN_TRASH + " != 1) " +
                " ORDER BY " + Assignment.Columns.ASSIGN_DATE + " , " +
                Assignment.Columns.ASSIGN_TIME,
                new String[]{strAccount, String.valueOf(startMillis), String.valueOf(endMillis)}));
    }

    @Override
    public List<Assignment> getScope(long startMillis, long endMillis, SortType sortType) {
        if (sortType == SortType.DATE_DESC){
            return getEntities(db.rawQuery("SELECT * FROM " + Assignment.TABLE_NAME +
                    " WHERE " + Assignment.Columns.ACCOUNT + " = ? " +
                    " AND " + Assignment.Columns.ASSIGN_DATE + " <= ? " +
                    " AND " + Assignment.Columns.ASSIGN_DATE + " >= ? " +
                    " AND ( " + Assignment.Columns.IN_TRASH + " IS NULL " +
                    " OR " + Assignment.Columns.IN_TRASH + " != 1) " +
                    " ORDER BY " + Assignment.Columns.ASSIGN_DATE + " DESC, " +
                    Assignment.Columns.ASSIGN_TIME,
                    new String[]{strAccount, String.valueOf(endMillis), String.valueOf(startMillis)}));
        } else {
            return getScope(startMillis, endMillis);
        }
    }

    @Override
    public List<Assignment> getAll(SortType sortType) {
        if (sortType == SortType.DATE_DESC){
            return getAll();
        } else {
            return getEntities(db.rawQuery("SELECT * FROM " + Assignment.TABLE_NAME +
                    " WHERE " + Assignment.Columns.ACCOUNT + " = ? " +
                    " AND ( " + Assignment.Columns.IN_TRASH + " IS NULL " +
                    " OR " + Assignment.Columns.IN_TRASH + " != 1) " +
                    " ORDER BY " + Assignment.Columns.ASSIGN_DATE + " , " +
                    Assignment.Columns.ASSIGN_TIME, // 日期（升序），时间（升序）
                    new String[]{strAccount}));
        }
    }

    @Override
    public List<Assignment> getAll() {
        return getEntities(db.rawQuery("SELECT * FROM " + Assignment.TABLE_NAME +
                " WHERE " + Assignment.Columns.ACCOUNT + " = ? " +
                " AND ( " + Assignment.Columns.IN_TRASH + " IS NULL " +
                " OR " + Assignment.Columns.IN_TRASH + " != 1) " +
                " ORDER BY " + Assignment.Columns.ASSIGN_DATE + " DESC, " +
                Assignment.Columns.ASSIGN_TIME, // 日期（降序），时间（升序）
                new String[]{strAccount}));
    }

    @Override
    public List<Assignment> getOverdue(SortType sortType) {
        if (sortType == SortType.DATE_DESC){
            return getOverdue();
        } else {
            long queryMillis = TpTime.millisOfCurrentDate();
            return getEntities(db.rawQuery(" SELECT * FROM " + Assignment.TABLE_NAME +
                    " WHERE " + Assignment.Columns.ACCOUNT + " = ? " +
//                " AND " + AssignEntity.Columns.ASSIGN_DATE + " < ? " +
                    " AND " + Assignment.Columns.ASSIGN_PROGRESS + " < 100 " +
                    " AND ( " + Assignment.Columns.IN_TRASH + " IS NULL " +
                    " OR " + Assignment.Columns.IN_TRASH + " != 1 ) " +
                    " ORDER BY " + Assignment.Columns.ASSIGN_DATE + " , " +
                    Assignment.Columns.ASSIGN_TIME,
                    new String[]{strAccount, String.valueOf(queryMillis)}));
        }
    }

    /**
     * 进度小于100就是未完成
     * @return List */
    @Override
    public List<Assignment> getOverdue() {
//        long queryMillis = TpTime.millisOfCurrentDate();
        return getEntities(db.rawQuery(" SELECT * FROM " + Assignment.TABLE_NAME +
                " WHERE " + Assignment.Columns.ACCOUNT + " = ? " +
//                " AND " + AssignEntity.Columns.ASSIGN_DATE + " < ? " +
                " AND " + Assignment.Columns.ASSIGN_PROGRESS + " < 100 " +
                " AND ( " + Assignment.Columns.IN_TRASH + " IS NULL " +
                " OR " + Assignment.Columns.IN_TRASH + " != 1 ) " +
                " ORDER BY " + Assignment.Columns.ASSIGN_DATE + " DESC , " +
                Assignment.Columns.ASSIGN_TIME,
                new String[]{strAccount/*, String.valueOf(queryMillis)*/}));
    }

    @Override
    public List<Assignment> getQuery(String query) {
        return getEntities(db.rawQuery(" SELECT * FROM " + Assignment.TABLE_NAME +
                " WHERE " + Assignment.Columns.ACCOUNT + " = ? " +
                " AND " + Assignment.Columns.ASSIGN_TITLE + " LIKE ? " +
                " AND ( " + Assignment.Columns.IN_TRASH + " IS NULL " +
                " OR " + Assignment.Columns.IN_TRASH + " != 1) " +
                " ORDER BY " + Assignment.Columns.ASSIGN_DATE + " DESC," +
                Assignment.Columns.ASSIGN_TIME,
                new String[]{strAccount, "%" + query + "%"}));
    }

    @Override
    public List<Assignment> getTrash() {
        return getEntities(db.rawQuery(" SELECT * FROM " + Assignment.TABLE_NAME +
                " WHERE " + Assignment.Columns.ACCOUNT + " = ? " +
                " AND " + Assignment.Columns.IN_TRASH + " = 1 " +
                " ORDER BY " + Assignment.Columns.ASSIGN_DATE + " DESC, " +
                Assignment.Columns.ASSIGN_TIME ,
                new String[]{strAccount}));
    }

    /**
     * 注意！该方法中没有对cursor进行moveToFirst检查，所以在使用之前必须先检查
     * 这是因为要在getEntities中使用该方法时避免出现死循环
     * @param cursor
     * @return */
    private Assignment getEntity(Cursor cursor){
        Assignment assignEntity = new Assignment();
        assignEntity.setAsnTitle(cursor.getString(cursor.getColumnIndex(Assignment.Columns.ASSIGN_TITLE)));
        assignEntity.setAsnId(cursor.getLong(cursor.getColumnIndex(Assignment.Columns.ASSIGN_ID)));
        assignEntity.setAccount(cursor.getString(cursor.getColumnIndex(Assignment.Columns.ACCOUNT)));
        assignEntity.setAsnColor(cursor.getString(cursor.getColumnIndex(Assignment.Columns.ASSIGN_COLOR)));
        assignEntity.setAsnDate(cursor.getLong(cursor.getColumnIndex(Assignment.Columns.ASSIGN_DATE)));
        assignEntity.setAsnTime(cursor.getInt(cursor.getColumnIndex(Assignment.Columns.ASSIGN_TIME)));
        assignEntity.setAsnProg(cursor.getInt(cursor.getColumnIndex(Assignment.Columns.ASSIGN_PROGRESS)));
        assignEntity.setAsnComment(cursor.getString(cursor.getColumnIndex(Assignment.Columns.ASSIGN_COMMENT)));
        assignEntity.setAsnContent(cursor.getString(cursor.getColumnIndex(Assignment.Columns.ASSIGN_CONTENT)));

        assignEntity.setAddedDate(cursor.getLong(cursor.getColumnIndex(Assignment.Columns.ADDED_DATE)));
        assignEntity.setAddedTime(cursor.getInt(cursor.getColumnIndex(Assignment.Columns.ADDED_TIME)));
        assignEntity.setLastModifyDate(cursor.getLong(cursor.getColumnIndex(Assignment.Columns.LAST_MODIFY_DATE)));
        assignEntity.setLastModifyTime(cursor.getInt(cursor.getColumnIndex(Assignment.Columns.LAST_MODIFY_TIME)));

        assignEntity.setInTrash(cursor.getInt(cursor.getColumnIndex(Assignment.Columns.IN_TRASH)));
        assignEntity.setSynced(cursor.getInt(cursor.getColumnIndex(Assignment.Columns.SYNCED)));
        return assignEntity;
    }

    private List<Assignment> getEntities(Cursor cursor){
        List<Assignment> assignEntities = new LinkedList<>();
        if (cursor.moveToFirst()){
            do {
                assignEntities.add(getEntity(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return assignEntities;
    }

    @Override
    public void insert(Assignment assignEntity) {
        db.insert(Assignment.TABLE_NAME, null, getCV(assignEntity));
    }

    @Override
    public void insert(List<Assignment> list) {
        for (Assignment assignEntity : list){
            insert(assignEntity);
        }
    }

    private ContentValues getCV(Assignment assignEntity){
        ContentValues values = new ContentValues();
        values.put(Assignment.Columns.ACCOUNT, strAccount);
        values.put(Assignment.Columns.ASSIGN_ID, assignEntity.getAsnId());
        values.put(Assignment.Columns.ASSIGN_TITLE, assignEntity.getAsnTitle());
        values.put(Assignment.Columns.ASSIGN_CONTENT, assignEntity.getAsnContent());
        values.put(Assignment.Columns.ASSIGN_COMMENT, assignEntity.getAsnComment());
        values.put(Assignment.Columns.ASSIGN_PROGRESS, assignEntity.getAsnProg());
        values.put(Assignment.Columns.ASSIGN_DATE, assignEntity.getAsnDate());
        values.put(Assignment.Columns.ASSIGN_TIME, assignEntity.getAsnTime());
        values.put(Assignment.Columns.ASSIGN_COLOR, assignEntity.getAsnColor());

        values.put(Assignment.Columns.ADDED_DATE, assignEntity.getAddedDate());
        values.put(Assignment.Columns.ADDED_TIME, assignEntity.getAddedTime());
        values.put(Assignment.Columns.LAST_MODIFY_DATE, assignEntity.getLastModifyDate());
        values.put(Assignment.Columns.LAST_MODIFY_TIME, assignEntity.getLastModifyTime());

        values.put(Assignment.Columns.SYNCED, 0);
        return values;
    }

    @Override
    public void delete(Assignment assignEntity) {
        long asnId = assignEntity.getAsnId();
        db.execSQL(" DELETE FROM " + Assignment.TABLE_NAME +
                " WHERE " + Assignment.Columns.ASSIGN_ID + " = ? " +
                " AND " + Assignment.Columns.ACCOUNT + " = ? ",
                new String[]{String.valueOf(asnId), strAccount});
        db.execSQL(" DELETE FROM " + SubAssignment.TABLE_NAME +
                " WHERE " + Assignment.Columns.ASSIGN_ID + " = ? " +
                " AND " + Assignment.Columns.ACCOUNT + " = ? ",
                new String[]{String.valueOf(asnId), strAccount});
    }

    @Override
    public void delete(List<Assignment> list) {
        for (Assignment assignEntity : list){
            delete(assignEntity);
        }
    }

    @Override
    public void trash(Assignment assignEntity) {
        trashOrRecover(1, assignEntity);
    }

    @Override
    public void trash(List<Assignment> list) {
        for (Assignment assignEntity : list){
            trash(assignEntity);
        }
    }

    @Override
    public void recover(Assignment assignEntity) {
        trashOrRecover(0, assignEntity);
    }

    @Override
    public void recover(List<Assignment> list) {
        for (Assignment assignEntity : list){
            recover(assignEntity);
        }
    }

    private void trashOrRecover(int type, Assignment assignEntity){
        if (type != 1 && type != 0)
            return;
        long asnId = assignEntity.getAsnId();
        db.execSQL("UPDATE " + Assignment.TABLE_NAME +
                " SET " + Assignment.Columns.ASSIGN_ID + " = ? " +
                " WHERE " + Assignment.Columns.ACCOUNT + " = ? " +
                " AND " + Assignment.Columns.ASSIGN_ID + " = ? ",
                new String[]{String.valueOf(type), strAccount, String.valueOf(asnId)});
    }

    @Override
    public void update(Assignment assignEntity) {
        long asnId = assignEntity.getAsnId();
        db.update(Assignment.TABLE_NAME, getCV(assignEntity),
                Assignment.Columns.ASSIGN_ID + " = ? AND " + Assignment.Columns.ACCOUNT + " = ?",
                new String[]{String.valueOf(asnId), strAccount});
    }

    @Override
    public void update(List<Assignment> list) {
        for (Assignment assignEntity : list){
            update(assignEntity);
        }
    }
}
