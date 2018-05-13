package me.shouheng.timepartner.database.dao.base;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import me.shouheng.timepartner.database.dao.SortType;
import me.shouheng.timepartner.models.entities.task.Task;
import me.shouheng.timepartner.utils.TpTime;

/**
 * Created by wangshouheng on 2017/1/11.*/
public class TaskDAO extends BaseDAO<Task> {

    public static TaskDAO getInstance(Context context){
        return new TaskDAO(context);
    }

    private TaskDAO(Context context) {
        super(context);
    }

    @Override
    public void close() {
        super.close();
    }

    @Override
    public Task get(long id) {
        Cursor cursor = db.rawQuery(" SELECT * FROM " + Task.TABLE_NAME +
                " WHERE " + Task.Columns.TASK_ID + " = ? " +
                " AND " + Task.Columns.ACCOUNT + " = ? " +
                " AND ( " + Task.Columns.IN_TRASH + " IS NULL " +
                " OR " + Task.Columns.IN_TRASH + " != 1) ",
                new String[]{String.valueOf(id), strAccount});
        Task taskEntity = null;
        if (cursor.moveToFirst()){
            do {
                taskEntity = getEntity(cursor);
            } while (cursor.moveToNext());
        }
        return taskEntity;
    }

    @Override
    public List<Task> get(long[] ids) {
        List<Task> list = new ArrayList<>();
        for (long id : ids){
            Task taskEntity = get(id);
            if (taskEntity != null) {
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
    public List<Task> gets(long classId) {
        return getEntities(db.rawQuery(" SELECT * FROM " + Task.TABLE_NAME +
                " WHERE " + Task.Columns.CLASS_ID + " = ? " +
                " AND " + Task.Columns.ACCOUNT + " = ? " +
                " AND ( " + Task.Columns.IN_TRASH + " IS NULL " +
                " OR " + Task.Columns.IN_TRASH + " != 1) " +
                " ORDER BY " + Task.Columns.TASK_DATE + " DESC, " +
                Task.Columns.TASK_TIME + " DESC ",
                new String[]{String.valueOf(classId), strAccount}));
    }

    @Override
    public List<Task> getOfDay(long millisOfDay) {
        return getEntities(db.rawQuery(" SELECT * FROM " + Task.TABLE_NAME +
                " WHERE " + Task.Columns.ACCOUNT + " = ? " +
                " AND " + Task.Columns.TASK_DATE + " = ? " + // 根据考试日期
                " AND ( " + Task.Columns.IN_TRASH + " IS NULL " +
                " OR " + Task.Columns.IN_TRASH + " != 1) " +
                " ORDER BY " + Task.Columns.TASK_DATE + " , " +
                Task.Columns.TASK_TIME,
                new String[]{strAccount, String.valueOf(millisOfDay)}));
    }

    @Override
    public List<Task> getOfDay(long millisOfDay, SortType sortType) {
        if (sortType == SortType.DATE_INC) {
            return getOfDay(millisOfDay);
        } else {
            return getEntities(db.rawQuery(" SELECT * FROM " + Task.TABLE_NAME +
                    " WHERE " + Task.Columns.ACCOUNT + " = ? " +
                    " AND " + Task.Columns.TASK_DATE + " = ? " + // 根据考试日期
                    " AND ( " + Task.Columns.IN_TRASH + " IS NULL " +
                    " OR " + Task.Columns.IN_TRASH + " != 1) " +
                    " ORDER BY " + Task.Columns.TASK_DATE + " DESC, " +
                    Task.Columns.TASK_TIME + " DESC ",
                    new String[]{strAccount, String.valueOf(millisOfDay)}));
        }
    }

    @Override
    public List<Task> getScope(long startMillis, long endMillis) {
        return getEntities(db.rawQuery("SELECT * FROM " + Task.TABLE_NAME +
                " WHERE " + Task.Columns.ACCOUNT + " = ? " +
                " AND " + Task.Columns.TASK_DATE + " >= ? " +
                " AND " + Task.Columns.TASK_DATE + " <= ? " +
                " AND ( " + Task.Columns.IN_TRASH + " IS NULL " +
                " OR " + Task.Columns.IN_TRASH + " != 1) " +
                " ORDER BY " + Task.Columns.TASK_DATE + " , " +
                Task.Columns.TASK_TIME ,
                new String[]{strAccount, String.valueOf(startMillis), String.valueOf(endMillis)}));
    }

    @Override
    public List<Task> getScope(long startMillis, long endMillis, SortType sortType) {
        if (sortType == SortType.DATE_DESC) {
            return getEntities(db.rawQuery("SELECT * FROM " + Task.TABLE_NAME +
                    " WHERE " + Task.Columns.ACCOUNT + " = ? " +
                    " AND " + Task.Columns.TASK_DATE + " <= ? " +
                    " AND " + Task.Columns.TASK_TIME + " >= ? " +
                    " AND ( " + Task.Columns.IN_TRASH + " IS NULL " +
                    " OR " + Task.Columns.IN_TRASH + " != 1) " +
                    " ORDER BY " + Task.Columns.TASK_DATE + " DESC, " +
                    Task.Columns.TASK_TIME + " DESC ",
                    new String[]{strAccount, String.valueOf(endMillis), String.valueOf(startMillis)}));
        } else {
            return getScope(startMillis, endMillis);
        }
    }

    @Override
    public List<Task> getAll(SortType sortType) {
        if (sortType == SortType.DATE_DESC) {
            return getAll();
        } else {
            return getEntities(db.rawQuery(" SELECT * FROM " + Task.TABLE_NAME +
                    " WHERE " + Task.Columns.ACCOUNT + " = ? " +
                    " AND ( " + Task.Columns.IN_TRASH + " IS NULL " +
                    " OR " + Task.Columns.IN_TRASH + " != 1) " +
                    " ORDER BY " + Task.Columns.TASK_DATE + " , " +
                    Task.Columns.TASK_TIME,
                    new String[]{strAccount}));
        }
    }

    @Override
    public List<Task> getAll() {
        return getEntities(db.rawQuery(" SELECT * FROM " + Task.TABLE_NAME +
                " WHERE " + Task.Columns.ACCOUNT + " = ? " +
                " AND ( " + Task.Columns.IN_TRASH + " IS NULL " +
                " OR " + Task.Columns.IN_TRASH + " != 1) " +
                " ORDER BY " + Task.Columns.TASK_DATE + " DESC, " +
                Task.Columns.TASK_TIME + " DESC ",
                new String[]{strAccount}));
    }

    @Override
    public List<Task> getOverdue(SortType sortType) {
        if (sortType == SortType.DATE_DESC){
            return getOverdue();
        } else {
            long millisOfCurrentDate = TpTime.millisOfCurrentDate();
            return getEntities(db.rawQuery(" SELECT * FROM " + Task.TABLE_NAME +
                    " WHERE " + Task.Columns.ACCOUNT + " = ? " +
                    " AND " + Task.Columns.TASK_DATE + " >= ? " +
                    " AND ( " + Task.Columns.IN_TRASH + " IS NULL " +
                    " OR " + Task.Columns.IN_TRASH + " != 1) " +
                    " ORDER BY " + Task.Columns.TASK_DATE + " , " +
                    Task.Columns.TASK_TIME,
                    new String[]{strAccount, String.valueOf(millisOfCurrentDate)}));
        }
    }

    @Override
    public List<Task> getOverdue() {
        long millisOfCurrentDate = TpTime.millisOfCurrentDate();
        return getEntities(db.rawQuery(" SELECT * FROM " + Task.TABLE_NAME +
                " WHERE " + Task.Columns.ACCOUNT + " = ? " +
                " AND " + Task.Columns.TASK_DATE + " >= ? " +
                " AND ( " + Task.Columns.IN_TRASH + " IS NULL " +
                " OR " + Task.Columns.IN_TRASH + " != 1) " +
                " ORDER BY " + Task.Columns.TASK_DATE + " DESC, " +
                Task.Columns.TASK_TIME + " DESC ",
                new String[]{strAccount, String.valueOf(millisOfCurrentDate)}));
    }

    @Override
    public List<Task> getQuery(String query) {
        return getEntities(db.rawQuery("SELECT * FROM " + Task.TABLE_NAME +
                " WHERE " + Task.Columns.ACCOUNT + " = ? " +
                " AND " + Task.Columns.TASK_TITLE + " LIKE ?" +
                " AND ( " + Task.Columns.IN_TRASH + " IS NULL " +
                " OR " + Task.Columns.IN_TRASH + " != 1) " +
                " ORDER BY " + Task.Columns.TASK_DATE + " DESC, " +
                Task.Columns.TASK_TIME + " DESC ",
                new String[]{strAccount, "%" + query + "%"}));
    }

    @Override
    public List<Task> getTrash() {
        return getEntities(db.rawQuery(" SELECT * FROM " + Task.TABLE_NAME +
                " WHERE " + Task.Columns.ACCOUNT + " = ? " +
                " AND " + Task.Columns.IN_TRASH + " = 1 " +
                " ORDER BY " + Task.Columns.TASK_DATE + " DESC, " +
                Task.Columns.TASK_TIME + " DESC",
                new String[]{strAccount}));
    }

    @Override
    public void insert(Task taskEntity) {
        db.insert(Task.TABLE_NAME, null, getCV(taskEntity));
    }

    @Override
    public void delete(Task taskEntity) {
        db.execSQL(" DELETE FROM " + Task.TABLE_NAME +
                " WHERE " + Task.Columns.TASK_ID + " = ? " +
                " AND " + Task.Columns.ACCOUNT + " = ? ",
                new String[]{String.valueOf(taskEntity.getTaskId()), strAccount});
    }

    @Override
    public void delete(List<Task> list) {
        for (Task taskEntity : list){
            delete(taskEntity);
        }
    }

    @Override
    public void trash(Task taskEntity) {
        trashOrRecover(taskEntity, 1);
    }

    @Override
    public void trash(List<Task> list) {
        for (Task taskEntity : list){
            trash(taskEntity);
        }
    }

    @Override
    public void recover(Task taskEntity) {
        trashOrRecover(taskEntity, 0);
    }

    @Override
    public void recover(List<Task> list) {
        for (Task taskEntity : list){
            recover(taskEntity);
        }
    }

    private Task getEntity(Cursor mCursor){
        Task taskEntity = new Task();
        taskEntity.setClsId(mCursor.getLong(mCursor.getColumnIndex(Task.Columns.CLASS_ID)));
        taskEntity.setAccount(mCursor.getString(mCursor.getColumnIndex(Task.Columns.ACCOUNT)));
        taskEntity.setTaskTitle(mCursor.getString(mCursor.getColumnIndex(Task.Columns.TASK_TITLE)));
        taskEntity.setTaskContent(mCursor.getString(mCursor.getColumnIndex(Task.Columns.TASK_CONTENT)));
        taskEntity.setTaskComments(mCursor.getString(mCursor.getColumnIndex(Task.Columns.TASK_COMMENT)));
        taskEntity.setTaskDate(mCursor.getLong(mCursor.getColumnIndex(Task.Columns.TASK_DATE)));
        taskEntity.setTaskTime(mCursor.getInt(mCursor.getColumnIndex(Task.Columns.TASK_TIME)));

        taskEntity.setAddedDate(mCursor.getLong(mCursor.getColumnIndex(Task.Columns.ADDED_DATE)));
        taskEntity.setAddedTime(mCursor.getInt(mCursor.getColumnIndex(Task.Columns.ADDED_TIME)));
        taskEntity.setLastModifyDate(mCursor.getLong(mCursor.getColumnIndex(Task.Columns.LAST_MODIFY_DATE)));
        taskEntity.setLastModifyTime(mCursor.getInt(mCursor.getColumnIndex(Task.Columns.LAST_MODIFY_TIME)));

        taskEntity.setInTrash(mCursor.getInt(mCursor.getColumnIndex(Task.Columns.IN_TRASH)));
        taskEntity.setSynced(mCursor.getInt(mCursor.getColumnIndex(Task.Columns.SYNCED)));
        return taskEntity;
    }

    private List<Task> getEntities(Cursor mCursor){
        List<Task> list = new ArrayList<>();
        if (mCursor.moveToFirst()){
            do{
                list.add(getEntity(mCursor));
            } while (mCursor.moveToNext());
        }
        mCursor.close();
        return list;
    }

    private void trashOrRecover(Task taskEntity , int type){
        if (type != 0 && type != 1)
            return;
        db.execSQL(" UPDATE " + Task.TABLE_NAME +
                " SET " + Task.Columns.IN_TRASH + " = ? " +
                " WHERE " + Task.Columns.ACCOUNT + " = ? " +
                " AND " + Task.Columns.TASK_ID + " = ? ",
                new String[]{String.valueOf(type), strAccount, String.valueOf(taskEntity)});
    }

    private ContentValues getCV(Task taskEntity){
        ContentValues values = new ContentValues();
        values.put(Task.Columns.ACCOUNT, strAccount);
        values.put(Task.Columns.CLASS_ID, taskEntity.getClsId());
        values.put(Task.Columns.TASK_ID, taskEntity.getTaskId());
        values.put(Task.Columns.TASK_TITLE, taskEntity.getTaskTitle());
        values.put(Task.Columns.TASK_DATE, taskEntity.getTaskDate());
        values.put(Task.Columns.TASK_TIME, taskEntity.getTaskTime());

        values.put(Task.Columns.ADDED_DATE, taskEntity.getAddedDate());
        values.put(Task.Columns.ADDED_TIME, taskEntity.getAddedTime());
        values.put(Task.Columns.LAST_MODIFY_DATE, taskEntity.getLastModifyDate());
        values.put(Task.Columns.LAST_MODIFY_TIME, taskEntity.getLastModifyTime());

        values.put(Task.Columns.TASK_CONTENT, taskEntity.getTaskContent());
        values.put(Task.Columns.TASK_COMMENT, taskEntity.getTaskComments());
        return values;
    }
}
