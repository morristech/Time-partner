package me.shouheng.timepartner.database.dao.base;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import me.shouheng.timepartner.database.dao.SortType;
import me.shouheng.timepartner.models.entities.collection.CollectionEntity;
import me.shouheng.timepartner.models.entities.note.Note;

/**
 * Created by wangshouheng on 2017/1/12.*/
public class NoteDAO extends BaseDAO<Note> {

    public static NoteDAO getInstance(Context context){
        return new NoteDAO(context);
    }

    private NoteDAO(Context context) {
        super(context);
    }

    @Override
    public void close() {
        super.close();
    }

    @Override
    public Note get(long id) {
        Cursor mCursor = db.rawQuery(" SELECT n.*, " +
                " c." + CollectionEntity.Columns.CLN_COLOR + " AS " + Note.Columns.NOTE_COLOR +
                " FROM " + Note.TABLE_NAME + " AS n, " + CollectionEntity.TABLE_NAME + " AS c " +
                " WHERE c." + CollectionEntity.Columns.ACCOUNT + " = ? " +
                " AND n." + Note.Columns.ACCOUNT + " = ? " +
                " AND n." + Note.Columns.NOTE_ID + " = ? " +
                " AND n." + Note.Columns.CLN_ID + " = c." + CollectionEntity.Columns.CLN_ID +
                " AND ( " + Note.Columns.IN_TRASH + " IS NULL OR "
                + Note.Columns.IN_TRASH + " != 1) ",
                new String[]{strAccount, strAccount, String.valueOf(id)});
        Note noteEntity = null;
        if (mCursor.moveToFirst()){
            noteEntity = getEntity(mCursor);
        }
        mCursor.close();
        return noteEntity;
    }

    @Override
    public List<Note> gets(long clnId) {
        return getEntities(db.rawQuery("SELECT n.*, " +
                " c." + CollectionEntity.Columns.CLN_COLOR + " AS " + Note.Columns.NOTE_COLOR +
                " FROM " + Note.TABLE_NAME + " AS n, " + CollectionEntity.TABLE_NAME + " AS c " +
                " WHERE c." + CollectionEntity.Columns.ACCOUNT + " = ? " +
                " AND n." + Note.Columns.ACCOUNT + " = ? " +
                " AND n." + Note.Columns.CLN_ID +"  = ? " +
                " AND c." + CollectionEntity.Columns.CLN_ID + " = n." + Note.Columns.CLN_ID +
                " AND ( " + Note.Columns.IN_TRASH + " IS NULL OR " + Note.Columns.IN_TRASH + " != 1) " +
                " ORDER BY n." + Note.Columns.NOTICE_DATE + " DESC, " +
                " n." + Note.Columns.NOTICE_TIME + " DESC ",
                new String[]{strAccount, strAccount, String.valueOf(clnId)}));
    }

    public List<Note> gets(long clnId, SortType sortType){
        if (sortType == SortType.DATE_DESC){
            return gets(clnId);
        } else {
            return getEntities(db.rawQuery("SELECT n.*, " +
                    " c." + CollectionEntity.Columns.CLN_COLOR + " AS " + Note.Columns.NOTE_COLOR +
                    " FROM " + Note.TABLE_NAME + " AS n, " + CollectionEntity.TABLE_NAME + " AS c " +
                    " WHERE c." + CollectionEntity.Columns.ACCOUNT + " = ? " +
                    " AND n." + Note.Columns.ACCOUNT + " = ? " +
                    " AND n." + Note.Columns.CLN_ID +"  = ? " +
                    " AND c." + CollectionEntity.Columns.CLN_ID + " = n." + Note.Columns.CLN_ID +
                    " AND ( " + Note.Columns.IN_TRASH + " IS NULL OR " + Note.Columns.IN_TRASH + " != 1) " +
                    " ORDER BY n." + Note.Columns.NOTICE_DATE + " , " +
                    " n." + Note.Columns.NOTICE_TIME,
                    new String[]{strAccount, strAccount, String.valueOf(clnId)}));
        }
    }

    @Override
    public List<Note> get(long[] ids) {
        List<Note> list = new ArrayList<>();
        for (long id : ids){
            Note noteEntity = get(id);
            if (noteEntity != null) {
                list.add(noteEntity);
            }
        }
        return list;
    }

    @Override
    public List<Note> getOfDay(long millisOfDay) {
        Cursor mCursor = db.rawQuery("SELECT n.*, " +
                " c." + CollectionEntity.Columns.CLN_COLOR + " AS " + Note.Columns.NOTE_COLOR +
                " FROM " + Note.TABLE_NAME + " AS n, " + CollectionEntity.TABLE_NAME + " AS c " +
                " WHERE c." + CollectionEntity.Columns.ACCOUNT + " = ? AND n." + Note.Columns.ACCOUNT + " = ? " +
                " AND n." + Note.Columns.NOTICE_DATE + " = ? " +
                " AND c." + CollectionEntity.Columns.CLN_ID + " = n." + Note.Columns.CLN_ID +
                " AND ( " + Note.Columns.IN_TRASH + " IS NULL OR " + Note.Columns.IN_TRASH + " != 1) " +
                " ORDER BY n." + Note.Columns.ADDED_DATE + " DESC, n." + Note.Columns.ADDED_TIME,
                new String[]{strAccount, strAccount, String.valueOf(millisOfDay)});
        return getEntities(mCursor);
    }

    @Override
    public List<Note> getOfDay(long millisOfDay, SortType sortType) {
        if (sortType == SortType.DATE_DESC){
            return getOfDay(millisOfDay);
        } else {
            Cursor mCursor = db.rawQuery("SELECT n.*," +
                    " c." + CollectionEntity.Columns.CLN_COLOR + " AS " + Note.Columns.NOTE_COLOR +
                    " FROM " + Note.TABLE_NAME + " AS n, " + CollectionEntity.TABLE_NAME + " AS c " +
                    " WHERE c." + CollectionEntity.Columns.ACCOUNT + " = ? AND n." + Note.Columns.ACCOUNT + " = ? " +
                    " AND n." + Note.Columns.NOTICE_DATE + " = ? " +
                    " AND c." + CollectionEntity.Columns.CLN_ID + " = n." + Note.Columns.CLN_ID +
                    " AND ( " + Note.Columns.IN_TRASH + " IS NULL OR " + Note.Columns.IN_TRASH + " != 1) " +
                    " ORDER BY n." + Note.Columns.ADDED_DATE + " , n." + Note.Columns.ADDED_TIME,
                    new String[]{strAccount, strAccount, String.valueOf(millisOfDay)});
            return getEntities(mCursor);
        }
    }

    /**
     * 搜索“添加时间”在指定起止日期之内的记录
     * @param startMillis 开始日期毫秒
     * @param endMillis 结束日期毫秒
     * @return list */
    @Override
    public List<Note> getScope(long startMillis, long endMillis) {
        Cursor mCursor = db.rawQuery(" SELECT n.*," +
                " c." + CollectionEntity.Columns.CLN_COLOR + " AS " + Note.Columns.NOTE_COLOR +
                " FROM " + Note.TABLE_NAME + " AS n, " + CollectionEntity.TABLE_NAME + " AS c " +
                " WHERE c." + CollectionEntity.Columns.ACCOUNT + " = ? " +
                " AND n." + Note.Columns.ACCOUNT + " = ? " +
                " AND n." + Note.Columns.ADDED_DATE + " <= ? AND n." + Note.Columns.ADDED_DATE + " >= ? " + // “添加时间”在指定时间范围内的记录
                " AND c." + CollectionEntity.Columns.CLN_ID + " = n." + Note.Columns.CLN_ID +
                " AND ( " + Note.Columns.IN_TRASH + " IS NULL OR " + Note.Columns.IN_TRASH + " != 1) " +
                " ORDER BY n." + Note.Columns.ADDED_DATE + " DESC, n." + Note.Columns.ADDED_TIME+ " DESC ",
                new String[]{strAccount, strAccount, String.valueOf(endMillis), String.valueOf(startMillis)});
        return getEntities(mCursor);
    }

    /**
     * 搜索“提醒时间”在指定的时间范围之内的记录
     * @param startMillis 开始日期的毫秒
     * @param endMillis 结束日期的毫秒
     * @return list */
    public List<Note> getNoticeScope(long startMillis, long endMillis) {
        Cursor mCursor = db.rawQuery("SELECT n.*," +
                " c." + CollectionEntity.Columns.CLN_COLOR + " AS " + Note.Columns.NOTE_COLOR +
                " FROM " + Note.TABLE_NAME + " AS n, " + CollectionEntity.TABLE_NAME + " AS c " +
                " WHERE c." + CollectionEntity.Columns.ACCOUNT + " = ? " +
                " AND n." + Note.Columns.ACCOUNT + " = ? " +
                " AND n." + Note.Columns.NOTICE_DATE + " <= ? AND n." + Note.Columns.NOTICE_DATE + " >= ? " + // “提醒时间”在指定时间范围内的记录
                " AND c." + CollectionEntity.Columns.CLN_ID + " = n." + Note.Columns.CLN_ID +
                " AND ( " + Note.Columns.IN_TRASH + " IS NULL OR " + Note.Columns.IN_TRASH + " != 1) " +
                " ORDER BY n." + Note.Columns.NOTICE_DATE+ ", n." + Note.Columns.NOTICE_TIME + " DESC ",
                new String[]{strAccount, strAccount, String.valueOf(endMillis), String.valueOf(startMillis)});
        return getEntities(mCursor);
    }

    @Override
    public List<Note> getScope(long startMillis, long endMillis, SortType sortType) {
        if (sortType == SortType.DATE_DESC){
            return getScope(startMillis, endMillis);
        } else {
            Cursor mCursor = db.rawQuery("SELECT n.*," +
                    " c." + CollectionEntity.Columns.CLN_COLOR + " AS " + Note.Columns.NOTE_COLOR +
                    " FROM " + Note.TABLE_NAME + " AS n, " + CollectionEntity.TABLE_NAME + " AS c " +
                    " WHERE c." + CollectionEntity.Columns.ACCOUNT + " = ? " +
                    " AND n." + Note.Columns.ACCOUNT + " = ? " +
                    " AND n." + Note.Columns.ADDED_DATE + " <= ? AND n." + Note.Columns.ADDED_DATE + " >= ? " + // “添加时间”在指定时间范围内的记录
                    " AND c." + CollectionEntity.Columns.CLN_ID + " = n." + Note.Columns.CLN_ID +
                    " AND ( " + Note.Columns.IN_TRASH + " IS NULL OR " + Note.Columns.IN_TRASH + " != 1) " +
                    " ORDER BY n." + Note.Columns.ADDED_DATE + ", n." + Note.Columns.ADDED_TIME,
                    new String[]{strAccount, strAccount, String.valueOf(endMillis), String.valueOf(startMillis)});
            return getEntities(mCursor);
        }
    }

    @Override
    public List<Note> getQuery(String query) {
        Cursor mCursor = db.rawQuery("SELECT n.*," +
                " c." + CollectionEntity.Columns.CLN_COLOR + " AS " + Note.Columns.NOTE_COLOR +
                " FROM " + Note.TABLE_NAME + " AS n, " + CollectionEntity.TABLE_NAME + " AS c " +
                " WHERE c." + CollectionEntity.Columns.ACCOUNT + " = ? " +
                " AND n." + Note.Columns.ACCOUNT + " = ? " +
                " AND n." + Note.Columns.NOTE_TITLE + " LIKE ? " +
                " AND c." + CollectionEntity.Columns.CLN_ID + " = n." + Note.Columns.CLN_ID +
                " AND ( " + Note.Columns.IN_TRASH + " IS NULL OR " + Note.Columns.IN_TRASH + " != 1) ",
                new String[]{strAccount, strAccount, "%" + query + "%"});
        return getEntities(mCursor);
    }

    /**
     * 获取存储在垃圾箱中的实体集合
     * @return 该集合不包含 Color 字段 */
    @Override
    public List<Note> getTrash() {
        Cursor mCursor = db.rawQuery(" SELECT * FROM " + Note.TABLE_NAME +
                " WHERE " + Note.Columns.IN_TRASH + " = ? " +
                " AND " + Note.Columns.ACCOUNT + " = ? " +
                " ORDER BY " + Note.Columns.ADDED_DATE + " DESC," +
                Note.Columns.ADDED_TIME + " DESC ",
                new String[]{String.valueOf(1), strAccount});
        return getBaseEntities(mCursor);
    }

    @Override
    public void insert(Note noteEntity) {
        db.insert(Note.TABLE_NAME, null, getCV(noteEntity));
    }

    @Override
    public void insert(List<Note> list) {
        for (Note noteEntity : list){
            insert(noteEntity);
        }
    }

    @Override
    public void delete(Note noteEntity) {
        db.execSQL(" DELETE FROM " + Note.TABLE_NAME +
                " WHERE " + Note.Columns.NOTE_ID + " = ? " +
                " AND " + Note.Columns.ACCOUNT + " = ? ",
                new String[]{String.valueOf(noteEntity.getNoteId()), strAccount});
    }

    @Override
    public void delete(List<Note> list) {
        for (Note noteEntity : list){
            delete(noteEntity);
        }
    }

    @Override
    public void trash(Note noteEntity) {
        trashOrRecover(noteEntity, 1);
    }

    @Override
    public void trash(List<Note> list) {
        for (Note noteEntity : list){
            trash(noteEntity);
        }
    }

    @Override
    public void recover(Note noteEntity) {
        trashOrRecover(noteEntity, 0);
    }

    @Override
    public void recover(List<Note> list) {
        for (Note noteEntity : list){
            recover(noteEntity);
        }
    }

    private void trashOrRecover(Note noteEntity, int type){
        db.execSQL(" UPDATE " + Note.TABLE_NAME +
                " SET " + Note.Columns.IN_TRASH + " = ? " +
                " WHERE " + Note.Columns.ACCOUNT + " = ? " +
                " AND " + Note.Columns.NOTE_ID + " = ? ",
                new String[]{String.valueOf(type), strAccount, String.valueOf(noteEntity.getNoteId())});
    }

    @Override
    public void update(Note noteEntity) {
        db.update(Note.TABLE_NAME, getCV(noteEntity),
                Note.Columns.NOTE_ID + " = ? AND " + Note.Columns.ACCOUNT + " = ?",
                new String[]{String.valueOf(noteEntity.getNoteId()), strAccount});
    }

    @Override
    public void update(List<Note> list) {
        for (Note noteEntity : list){
            update(noteEntity);
        }
    }

    private ContentValues getCV(Note noteEntity){
        ContentValues values = new ContentValues();
        values.put(Note.Columns.CLN_ID, noteEntity.getClnId());
        values.put(Note.Columns.NOTE_ID, noteEntity.getNoteId());
        values.put(Note.Columns.ACCOUNT, strAccount);

        values.put(Note.Columns.NOTE_TITLE, noteEntity.getNoteTitle());
        values.put(Note.Columns.NOTE_CONTENT, noteEntity.getNoteContent());
        values.put(Note.Columns.RCD_PATH, noteEntity.getRecordPath());
        values.put(Note.Columns.NOTICE_DATE, noteEntity.getNoteDate());
        values.put(Note.Columns.NOTICE_TIME, noteEntity.getNoteTime());

        values.put(Note.Columns.ADDED_DATE, noteEntity.getAddedDate());
        values.put(Note.Columns.ADDED_TIME, noteEntity.getAddedTime());
        values.put(Note.Columns.LAST_MODIFY_DATE, noteEntity.getLastModifyDate());
        values.put(Note.Columns.LAST_MODIFY_TIME, noteEntity.getLastModifyTime());

        values.put(Note.Columns.LIKED, noteEntity.getLiked());
        values.put(Note.Columns.IN_TRASH, noteEntity.getInTrash());
        return values;
    }

    /**
     * getBaseEntity和getEntity的不同之处在于：
     *     在组装值的时候的方式是不同的：
     *         getColumnIndex中添入的索引是不同的
     * @param mCursor 游标
     * @return 集合实体 */
    private Note getBaseEntity(Cursor mCursor){
        Note noteEntity = new Note();
        noteEntity.setNoteId(mCursor.getLong(mCursor.getColumnIndex(Note.Columns.NOTE_ID)));
        noteEntity.setClnId(mCursor.getLong(mCursor.getColumnIndex(Note.Columns.CLN_ID)));
        noteEntity.setAccount(mCursor.getString(mCursor.getColumnIndex(Note.Columns.ACCOUNT)));

        noteEntity.setNoteTitle(mCursor.getString(mCursor.getColumnIndex(Note.Columns.NOTE_TITLE)));
        noteEntity.setNoteContent(mCursor.getString(mCursor.getColumnIndex(Note.Columns.NOTE_CONTENT)));
        noteEntity.setRecordPath(mCursor.getString(mCursor.getColumnIndex(Note.Columns.RCD_PATH)));
        noteEntity.setNoteDate(mCursor.getLong(mCursor.getColumnIndex(Note.Columns.NOTICE_DATE)));
        noteEntity.setNoteTime(mCursor.getInt(mCursor.getColumnIndex(Note.Columns.NOTICE_TIME)));

        noteEntity.setAddedDate(mCursor.getLong(mCursor.getColumnIndex(Note.Columns.ADDED_DATE)));
        noteEntity.setAddedTime(mCursor.getInt(mCursor.getColumnIndex(Note.Columns.ADDED_TIME)));
        noteEntity.setLastModifyDate(mCursor.getLong(mCursor.getColumnIndex(Note.Columns.LAST_MODIFY_DATE)));
        noteEntity.setLastModifyTime(mCursor.getInt(mCursor.getColumnIndex(Note.Columns.LAST_MODIFY_TIME)));

        noteEntity.setLiked(mCursor.getInt(mCursor.getColumnIndex(Note.Columns.LIKED)));
        noteEntity.setInTrash(mCursor.getInt(mCursor.getColumnIndex(Note.Columns.IN_TRASH)));
        return noteEntity;
    }

    private Note getEntity(Cursor mCursor){
        Note noteEntity = new Note();
        noteEntity.setClnId(mCursor.getLong(mCursor.getColumnIndex(Note.Columns.CLN_ID)));
        noteEntity.setNoteId(mCursor.getLong(mCursor.getColumnIndex(Note.Columns.NOTE_ID)));
        noteEntity.setAccount(mCursor.getString(mCursor.getColumnIndex(Note.Columns.ACCOUNT)));

        noteEntity.setNoteTitle(mCursor.getString(mCursor.getColumnIndex(Note.Columns.NOTE_TITLE)));
        noteEntity.setNoteContent(mCursor.getString(mCursor.getColumnIndex(Note.Columns.NOTE_CONTENT)));
        noteEntity.setRecordPath(mCursor.getString(mCursor.getColumnIndex(Note.Columns.RCD_PATH)));
        noteEntity.setNoteDate(mCursor.getLong(mCursor.getColumnIndex(Note.Columns.NOTICE_DATE)));
        noteEntity.setNoteTime(mCursor.getInt(mCursor.getColumnIndex(Note.Columns.NOTICE_TIME)));

        noteEntity.setAddedDate(mCursor.getLong(mCursor.getColumnIndex(Note.Columns.ADDED_DATE)));
        noteEntity.setAddedTime(mCursor.getInt(mCursor.getColumnIndex(Note.Columns.ADDED_TIME)));
        noteEntity.setLastModifyDate(mCursor.getLong(mCursor.getColumnIndex(Note.Columns.LAST_MODIFY_DATE)));
        noteEntity.setLastModifyTime(mCursor.getInt(mCursor.getColumnIndex(Note.Columns.LAST_MODIFY_TIME)));

        noteEntity.setLiked(mCursor.getInt(mCursor.getColumnIndex(Note.Columns.LIKED)));
        noteEntity.setInTrash(mCursor.getInt(mCursor.getColumnIndex(Note.Columns.IN_TRASH)));

        noteEntity.setNoteColor(mCursor.getString(mCursor.getColumnIndex(Note.Columns.NOTE_COLOR)));
        return noteEntity;
    }

    private List<Note> getEntities(Cursor mCursor){
        List<Note> noteEntities = new ArrayList<>();
        if (mCursor.moveToFirst()){
            do {
                noteEntities.add(getEntity(mCursor));
            } while (mCursor.moveToNext());
        }
        return noteEntities;
    }

    private List<Note> getBaseEntities(Cursor mCursor){
        List<Note> noteEntities = new ArrayList<>();
        if (mCursor.moveToFirst()){
            do {
                noteEntities.add(getBaseEntity(mCursor));
            } while (mCursor.moveToNext());
        }
        return noteEntities;
    }
}
