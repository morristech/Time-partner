package me.shouheng.timepartner.database.dao.base;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import me.shouheng.timepartner.database.dao.SortType;
import me.shouheng.timepartner.models.entities.tpclass.ClassDetail;
import me.shouheng.timepartner.models.entities.tpclass.ClassEntity;
import me.shouheng.timepartner.models.entities.exam.Exam;
import me.shouheng.timepartner.models.entities.task.Task;
import me.shouheng.timepartner.utils.TpTime;

public class ClassDAO extends BaseDAO<ClassEntity> {

    public static ClassDAO getInstance(Context context){
        return new ClassDAO(context);
    }

    private ClassDAO(Context context) {
        super(context);
    }

    @Override
    public void update(ClassEntity classEntity) {
        db.update(ClassEntity.TABLE_NAME, getCV(classEntity),
                ClassEntity.Columns.CLS_ID + " = ? AND " + ClassEntity.Columns.ACCOUNT + " = ?",
                new String[]{String.valueOf(classEntity.getClsId()), strAccount});
    }

    @Override
    public void update(List<ClassEntity> list) {
        for (ClassEntity classEntity : list) {
            update(classEntity);
        }
    }

    void recover(ClassEntity classEntity, ClassEntity.TrashType trashType){
        trashOrRecover(0, classEntity, trashType);
    }

    void recover(List<ClassEntity> list, ClassEntity.TrashType trashType){
        for (ClassEntity classEntity : list){
            recover(classEntity, trashType);
        }
    }

    @Override
    public void recover(List<ClassEntity> list) {
        recover(list, ClassEntity.TrashType.WITHOUT_TASK_EXAM);
    }

    @Override
    public void recover(ClassEntity classEntity) {
        recover(classEntity, ClassEntity.TrashType.WITHOUT_TASK_EXAM);
    }

    /**
     * 根据指定的方式将实体放进垃圾箱
     * @param classEntity
     * @param trashType 指定方式 */
    public void trash(ClassEntity classEntity, ClassEntity.TrashType trashType){
        trashOrRecover(1, classEntity, trashType);
    }

    public void trash(List<ClassEntity> list, ClassEntity.TrashType trashType){
        for (ClassEntity classEntity : list){
            trash(classEntity, trashType);
        }
    }

    /**
     * 使用默认的方式，只将指定的实体放到垃圾箱中即可
     * @param list */
    @Override
    public void trash(List<ClassEntity> list) {
        trash(list, ClassEntity.TrashType.WITHOUT_TASK_EXAM);
    }

    @Override
    public void trash(ClassEntity classEntity) {
        trash(classEntity, ClassEntity.TrashType.WITHOUT_TASK_EXAM);
    }

    /**
     * 置为垃圾桶状态或者从垃圾桶中恢复
     * @param type 0 表示在垃圾桶中，1 表示不在垃圾桶中
     * @param classEntity
     * @param trashType */
    private void trashOrRecover(int type, ClassEntity classEntity, ClassEntity.TrashType trashType){
        if (type == 1 || type == 0){
            long classId = classEntity.getClsId();
            db.execSQL(" UPDATE " + ClassEntity.TABLE_NAME +
                    " SET " + ClassEntity.Columns.IN_TRASH + " = ? " +
                    " WHERE " + ClassEntity.Columns.ACCOUNT + " = ? " +
                    " AND " + ClassEntity.Columns.CLS_ID + " = ? ",
                    new String[]{String.valueOf(type), strAccount, String.valueOf(classId)});
            if (trashType == ClassEntity.TrashType.WITH_TASK_EXAM){
                db.execSQL(" UPDATE " + Task.TABLE_NAME +
                        " SET " + Task.Columns.IN_TRASH + " = ? " +
                        " WHERE " + Task.Columns.ACCOUNT + " = ? " +
                        " AND " + Task.Columns.CLASS_ID + " = ? ",
                        new String[]{String.valueOf(type), strAccount, String.valueOf(classId)});
                db.execSQL(" UPDATE " + Exam.TABLE_NAME +
                        " SET " + Exam.Columns.IN_TRASH + " = ? " +
                        " WHERE " + Exam.Columns.ACCOUNT + " = ? " +
                        " AND " + Exam.Columns.CLASS_ID + " = ? ",
                        new String[]{String.valueOf(type), strAccount, String.valueOf(classId)});
            }
        }
    }

    /**
     * 根据指定的方式删除课程信息
     * @param classEntity
     * @param delType 同时删除考试和作业或者不同时删除 */
    public void delete(ClassEntity classEntity, ClassEntity.DelType delType){
        long classId = classEntity.getClsId();
        db.execSQL(" DELETE FROM " + ClassEntity.TABLE_NAME +
                " WHERE " + ClassEntity.Columns.CLS_ID + " = ? " +
                " AND " + ClassEntity.Columns.ACCOUNT + " = ?",
                new String[]{String.valueOf(classId), strAccount});
        db.execSQL(" DELETE FROM " + ClassDetail.TABLE_NAME +
                " WHERE " + ClassDetail.Columns.CLASS_ID + " = ? " +
                " AND " + ClassDetail.Columns.ACCOUNT + " = ?",
                new String[]{String.valueOf(classId), strAccount});
        if (delType == ClassEntity.DelType.WITH_TASK_EXAM) {
            db.execSQL(" DELETE FROM " + Exam.TABLE_NAME +
                    " WHERE " + Exam.Columns.CLASS_ID + " = ? " +
                    " AND " + Exam.Columns.ACCOUNT + " = ? ",
                    new String[]{String.valueOf(classId), strAccount});
            db.execSQL(" DELETE FROM " + Task.TABLE_NAME +
                    " WHERE " + Task.Columns.CLASS_ID + " = ? " +
                    " AND " + Task.Columns.ACCOUNT + " = ? ",
                    new String[]{String.valueOf(classId), strAccount});
        }
    }

    public void delete(List<ClassEntity> list, ClassEntity.DelType delType){
        for (ClassEntity classEntity : list) {
            delete(classEntity, delType);
        }
    }

    @Override
    public void delete(List<ClassEntity> list) {
        for (ClassEntity classEntity : list){
            delete(classEntity);
        }
    }

    /**
     * 使用默认的方式进行删除：不同时删除考试和作业
     * @param classEntity */
    @Override
    public void delete(ClassEntity classEntity) {
        delete(classEntity, ClassEntity.DelType.WITHOUT_TASK_EXAM);
    }

    @Override
    public void insert(List<ClassEntity> list) {
        for (ClassEntity classEntity : list){
            db.insert(ClassEntity.TABLE_NAME, null, getCV(classEntity));
        }
    }

    @Override
    public void insert(ClassEntity classEntity) {
        db.insert(ClassEntity.TABLE_NAME, null, getCV(classEntity));
    }

    /**
     * 获取未结束的课程实体（日期降序）
     * @return
     */
    @Override
    public List<ClassEntity> getOverdue() {
        long millisOfToday = TpTime.millisOfCurrentDate();
        return getEntities(db.rawQuery(" SELECT * " +
                " FROM " + ClassEntity.TABLE_NAME +
                " WHERE " + ClassEntity.Columns.ACCOUNT + " = ? " +
                " AND " + ClassEntity.Columns.CLS_END_DATE + " >= ? " +
                " AND ( " + ClassEntity.Columns.IN_TRASH + " IS NULL " +
                " OR " + ClassEntity.Columns.IN_TRASH + " != 1) " +
                " ORDER BY " + ClassEntity.Columns.CLS_START_DATE + " DESC, " +
                ClassEntity.Columns.CLS_END_DATE + " DESC ",
                new String[]{strAccount, String.valueOf(millisOfToday)}));
    }

    @Override
    public List<ClassEntity> getOverdue(SortType sortType) {
        long millisOfToday = TpTime.millisOfCurrentDate();
        if (sortType == SortType.DATE_DESC) {
            return getOverdue();
        }
        return getEntities(db.rawQuery(" SELECT * " +
                " FROM " + ClassEntity.TABLE_NAME +
                " WHERE " + ClassEntity.Columns.ACCOUNT + " = ? " +
                " AND " + ClassEntity.Columns.CLS_END_DATE + " >= ? " +
                " AND ( " + ClassEntity.Columns.IN_TRASH + " IS NULL " +
                " OR " + ClassEntity.Columns.IN_TRASH + " != 1) " +
                " ORDER BY " + ClassEntity.Columns.CLS_START_DATE + " , " +
                ClassEntity.Columns.CLS_END_DATE,
                new String[]{strAccount, String.valueOf(millisOfToday)}));
    }

    @Override
    public List<ClassEntity> getQuery(String query) {
        return getEntities(db.rawQuery(" SELECT * " +
                " FROM " + ClassEntity.TABLE_NAME +
                " WHERE " + ClassEntity.Columns.ACCOUNT + " = ? " +
                " AND " + ClassEntity.Columns.CLS_NAME + " LIKE ? " +
                " AND ( " + ClassEntity.Columns.IN_TRASH + " IS NULL OR " +
                ClassEntity.Columns.IN_TRASH + " != 1) " +
                " ORDER BY " + ClassEntity.Columns.CLS_START_DATE + " DESC, " +
                ClassEntity.Columns.CLS_END_DATE + " DESC",
                new String[]{strAccount, "%" + query + "%"}));
    }

    @Override
    public List<ClassEntity> getTrash() {
        return getEntities(db.rawQuery(" SELECT * " +
                " FROM " + ClassEntity.TABLE_NAME +
                " WHERE " + ClassEntity.Columns.ACCOUNT + " = ? " +
                " AND " + ClassEntity.Columns.IN_TRASH + " = 1 " +
                " ORDER BY " + ClassEntity.Columns.CLS_START_DATE + " DESC, " +
                ClassEntity.Columns.CLS_END_DATE + " DESC",
                new String[]{strAccount}));
    }

    /**
     * 获取全部的课程信息（日期降序）
     * @return */
    @Override
    public List<ClassEntity> getAll() {
        return getEntities(db.rawQuery(" SELECT * " +
                " FROM " + ClassEntity.TABLE_NAME +
                " WHERE " + ClassEntity.Columns.ACCOUNT + " = ? " +
                " AND ( " + ClassEntity.Columns.IN_TRASH + " IS NULL " +
                " OR " + ClassEntity.Columns.IN_TRASH + " != 1) " +
                " ORDER BY " + ClassEntity.Columns.CLS_START_DATE + " DESC, " +
                ClassEntity.Columns.CLS_END_DATE + " DESC", new String[]{strAccount}));
    }

    @Override
    public List<ClassEntity> getAll(SortType sortType) {
        if (sortType == SortType.DATE_INC) {
            return getEntities(db.rawQuery(" SELECT * " +
                    " FROM " + ClassEntity.TABLE_NAME +
                    " WHERE " + ClassEntity.Columns.ACCOUNT + " = ? " +
                    " AND ( " + ClassEntity.Columns.IN_TRASH + " IS NULL " +
                    " OR " + ClassEntity.Columns.IN_TRASH + " != 1) " +
                    " ORDER BY " + ClassEntity.Columns.CLS_START_DATE + " , " +
                    ClassEntity.Columns.CLS_END_DATE, new String[]{strAccount}));
        } else {
            return getAll();
        }
    }

    /**
     * 指定的时间范围之内的实体集合
     * @param startMillis 开始的毫秒数
     * @param endMillis 结束的毫秒数
     * @return */
    @Override
    public List<ClassEntity> getScope(long startMillis, long endMillis) {
        return getEntities(db.rawQuery("SELECT * " +
                " FROM " + ClassEntity.TABLE_NAME +
                " WHERE " + ClassEntity.Columns.ACCOUNT + " = ? " +
                " AND " + ClassEntity.Columns.CLS_START_DATE + " >= ? " +
                " AND " + ClassEntity.Columns.CLS_END_DATE + " <= ? " +
                " AND ( " + ClassEntity.Columns.IN_TRASH + " IS NULL " +
                " OR " + ClassEntity.Columns.IN_TRASH + " != 1) " +
                " ORDER BY " + ClassEntity.Columns.CLS_START_DATE + " DESC, "
                + ClassEntity.Columns.CLS_END_DATE + " DESC",
                new String[]{strAccount, String.valueOf(startMillis), String.valueOf(endMillis)}));
    }

    /**
     * 包含指定日期
     * @param millisOfDay 日期的毫秒数
     * @return */
    @Override
    public List<ClassEntity> getOfDay(long millisOfDay) {
        return getEntities(db.rawQuery(" SELECT * " +
                " FROM " + ClassEntity.TABLE_NAME +
                " WHERE " + ClassEntity.Columns.ACCOUNT + " = ? " +
                " AND " + ClassEntity.Columns.CLS_START_DATE + " <= ? " +
                " AND " + ClassEntity.Columns.CLS_END_DATE + " >= ? " +
                " AND ( " + ClassEntity.Columns.IN_TRASH + " IS NULL " +
                " OR " + ClassEntity.Columns.IN_TRASH + " != 1) ",
                new String[]{strAccount, String.valueOf(millisOfDay), String.valueOf(millisOfDay)}));
    }

    @Override
    public List<ClassEntity> get(long[] ids) {
        List<ClassEntity> list = new ArrayList<>();
        for (long id : ids){
            ClassEntity classEntity = get(id);
            if (classEntity != null) {
                list.add(classEntity);
            }
        }
        return list;
    }

    @Override
    public ClassEntity get(long id) {
        ClassEntity classEntity = null;
        Cursor cursor = db.rawQuery(" SELECT * " +
                " FROM " + ClassEntity.TABLE_NAME +
                " WHERE " + ClassEntity.Columns.ACCOUNT + " = ? " +
                " AND " + ClassEntity.Columns.CLS_ID + " = ? " +
                " AND ( " + ClassEntity.Columns.IN_TRASH + " IS NULL OR " + ClassEntity.Columns.IN_TRASH + " != 1) ",
                new String[]{strAccount, String.valueOf(id)});
        if (cursor.moveToFirst()){
            classEntity = getEntity(cursor);
        }
        cursor.close();
        return classEntity;
    }

    private ContentValues getCV(ClassEntity classEntity){
        ContentValues values = new ContentValues();
        values.put(ClassEntity.Columns.CLS_ID, classEntity.getClsId());
        values.put(ClassEntity.Columns.CLS_NAME,classEntity.getClsName());
        values.put(ClassEntity.Columns.ACCOUNT, classEntity.getAccount());
        values.put(ClassEntity.Columns.CLS_START_DATE, classEntity.getStartDate());
        values.put(ClassEntity.Columns.CLS_END_DATE, classEntity.getEndDate());
        values.put(ClassEntity.Columns.CLS_COLOR, classEntity.getClsColor());

        values.put(ClassEntity.Columns.ADDED_DATE, classEntity.getAddedDate());
        values.put(ClassEntity.Columns.ADDED_TIME, classEntity.getAddedTime());
        values.put(ClassEntity.Columns.LAST_MODIFY_DATE, classEntity.getLastModifyDate());
        values.put(ClassEntity.Columns.LAST_MODIFY_TIME, classEntity.getLastModifyTime());

        values.put(ClassEntity.Columns.SYNCED, classEntity.getSynced());
        values.put(ClassEntity.Columns.IN_TRASH, classEntity.getInTrash());
        return values;
    }

    private ClassEntity getEntity(Cursor mCursor){
        ClassEntity classEntity = new ClassEntity();
        classEntity.setClsId(mCursor.getLong(mCursor.getColumnIndex(ClassEntity.Columns.CLS_ID)));
        classEntity.setAccount(mCursor.getString(mCursor.getColumnIndex(ClassEntity.Columns.ACCOUNT)));
        classEntity.setClsName(mCursor.getString(mCursor.getColumnIndex(ClassEntity.Columns.CLS_NAME)));
        classEntity.setClsColor(mCursor.getString(mCursor.getColumnIndex(ClassEntity.Columns.CLS_COLOR)));
        classEntity.setStartDate(mCursor.getLong(mCursor.getColumnIndex(ClassEntity.Columns.CLS_START_DATE)));
        classEntity.setEndDate(mCursor.getLong(mCursor.getColumnIndex(ClassEntity.Columns.CLS_END_DATE)));

        classEntity.setAddedDate(mCursor.getLong(mCursor.getColumnIndex(ClassEntity.Columns.ADDED_DATE)));
        classEntity.setAddedTime(mCursor.getInt(mCursor.getColumnIndex(ClassEntity.Columns.ADDED_TIME)));
        classEntity.setLastModifyDate(mCursor.getLong(mCursor.getColumnIndex(ClassEntity.Columns.LAST_MODIFY_DATE)));
        classEntity.setLastModifyTime(mCursor.getInt(mCursor.getColumnIndex(ClassEntity.Columns.LAST_MODIFY_TIME)));

        classEntity.setSynced(mCursor.getInt(mCursor.getColumnIndex(ClassEntity.Columns.SYNCED)));
        classEntity.setInTrash(mCursor.getInt(mCursor.getColumnIndex(ClassEntity.Columns.IN_TRASH)));
        return classEntity;
    }

    private List<ClassEntity> getEntities(Cursor mCursor) {
        List<ClassEntity> list = new ArrayList<>();
        if (mCursor.moveToFirst()){
            do {
                ClassEntity classEntity = getEntity(mCursor);
                list.add(classEntity);
            } while (mCursor.moveToNext());
        }
        mCursor.close();
        return list;
    }
}
