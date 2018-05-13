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
 * Created by wangshouheng on 2017/1/12. */
public class CollectionDAO extends BaseDAO<CollectionEntity> {

    public static CollectionDAO getInstance(Context context){
        return new CollectionDAO(context);
    }

    private CollectionDAO(Context context) {
        super(context);
    }

    @Override
    public void close() {
        super.close();
    }

    @Override
    public CollectionEntity get(long clnId) {
        Cursor mCursor = db.rawQuery(" SELECT *, " +
                    " ( SELECT COUNT(*) FROM " + Note.TABLE_NAME +
                    " WHERE " + Note.TABLE_NAME + "." + Note.Columns.CLN_ID +
                    " = " + CollectionEntity.TABLE_NAME + "." + CollectionEntity.Columns.CLN_ID +
                    " AND " + Note.TABLE_NAME + "." + Note.Columns.ACCOUNT + " = ? " +
                    " AND ( " + Note.TABLE_NAME + "." + Note.Columns.IN_TRASH +
                    " IS NULL OR " + Note.TABLE_NAME + "." + Note.Columns.IN_TRASH + " != 1) ) " +
                    " AS " + CollectionEntity.Columns.COUNT +
                " FROM " + CollectionEntity.TABLE_NAME +
                " WHERE " + CollectionEntity.Columns.ACCOUNT + " = ? " +
                " AND " + CollectionEntity.Columns.CLN_ID + " = ? " +
                " GROUP BY " + CollectionEntity.Columns.CLN_ID +
                " ORDER BY " + CollectionEntity.Columns.ADDED_DATE + " DESC, " +
                CollectionEntity.Columns.ADDED_TIME + " DESC ",
                new String[]{strAccount, strAccount, String.valueOf(clnId)});
        CollectionEntity collectionEntity = null;
        if (mCursor.moveToFirst()){
            collectionEntity = getEntity(mCursor);
        }
        mCursor.close();
        return collectionEntity;
    }

    @Override
    public List<CollectionEntity> getAll() {
        Cursor mCursor = db.rawQuery(" SELECT *, " + " ( SELECT COUNT(*) FROM " + Note.TABLE_NAME +
                " WHERE " + Note.TABLE_NAME + "." + Note.Columns.CLN_ID +
                " = " + CollectionEntity.TABLE_NAME + "." + CollectionEntity.Columns.CLN_ID +
                " AND " + Note.TABLE_NAME + "." + Note.Columns.ACCOUNT + " = ? " +
                " AND ( " + Note.TABLE_NAME + "." + Note.Columns.IN_TRASH +
                " IS NULL OR " + Note.TABLE_NAME + "." + Note.Columns.IN_TRASH + " != 1) ) " +
                " AS " + CollectionEntity.Columns.COUNT +
                " FROM " + CollectionEntity.TABLE_NAME +
                " WHERE " + CollectionEntity.Columns.ACCOUNT + " = ? " +
                " GROUP BY " + CollectionEntity.Columns.CLN_ID +
                " ORDER BY " + CollectionEntity.Columns.ADDED_DATE + " DESC, " +
                CollectionEntity.Columns.ADDED_TIME + " DESC ", // 如果不使用GROUP BY子句，将得到所有结果的总和
                new String[]{strAccount, strAccount});
        return getEntities(mCursor);
    }

    @Override
    public List<CollectionEntity> getAll(SortType sortType) {
        if (sortType == SortType.DATE_DESC){
            return getAll();
        } else {
            Cursor mCursor = db.rawQuery(" SELECT *, " + " ( SELECT COUNT(*) FROM " + Note.TABLE_NAME +
                    " WHERE " + Note.TABLE_NAME + "." + Note.Columns.CLN_ID +
                    " = " + CollectionEntity.TABLE_NAME + "." + CollectionEntity.Columns.CLN_ID +
                    " AND " + Note.TABLE_NAME + "." + Note.Columns.ACCOUNT + " = ? " +
                    " AND ( " + Note.TABLE_NAME + "." + Note.Columns.IN_TRASH +
                    " IS NULL OR " + Note.TABLE_NAME + "." + Note.Columns.IN_TRASH + " != 1) ) " +
                    " AS " + CollectionEntity.Columns.COUNT +
                    " FROM " + CollectionEntity.TABLE_NAME +
                    " WHERE " + CollectionEntity.Columns.ACCOUNT + " = ? " +
                    " GROUP BY " + CollectionEntity.Columns.CLN_ID +
                    " ORDER BY " + CollectionEntity.Columns.ADDED_DATE + " , " +
                    CollectionEntity.Columns.ADDED_TIME, // 如果不使用GROUP BY子句，将得到所有结果的总和
                    new String[]{strAccount, strAccount});
            return getEntities(mCursor);
        }
    }

    @Override
    public void delete(CollectionEntity collectionEntity) {
        db.execSQL(" DELETE FROM " + CollectionEntity.TABLE_NAME +
                " WHERE " + CollectionEntity.Columns.ACCOUNT + " = ? " +
                " AND " + CollectionEntity.Columns.CLN_ID + " = ?",
                new String[]{strAccount, String.valueOf(collectionEntity.getClnId())});
    }

    @Override
    public void insert(CollectionEntity collectionEntity) {
        db.insert(CollectionEntity.TABLE_NAME, null, getCV(collectionEntity));
    }

    @Override
    public void update(CollectionEntity collectionEntity) {
        db.update(CollectionEntity.TABLE_NAME, getCV(collectionEntity),
                CollectionEntity.Columns.CLN_ID + " = ? AND " + CollectionEntity.Columns.ACCOUNT + " = ?",
                new String[]{String.valueOf(collectionEntity.getClnId()), strAccount});
    }

    private ContentValues getCV(CollectionEntity collectionEntity){
        ContentValues values = new ContentValues();
        values.put(CollectionEntity.Columns.ACCOUNT, collectionEntity.getAccount());
        values.put(CollectionEntity.Columns.ADDED_DATE, collectionEntity.getAddedDate());
        values.put(CollectionEntity.Columns.ADDED_TIME, collectionEntity.getAddedTime());
        values.put(CollectionEntity.Columns.CLN_COLOR, collectionEntity.getClnColor());
        values.put(CollectionEntity.Columns.CLN_ID, collectionEntity.getClnId());
        values.put(CollectionEntity.Columns.CLN_TITLE, collectionEntity.getClnTitle());
        values.put(CollectionEntity.Columns.ADDED_DATE, collectionEntity.getAddedDate());
        values.put(CollectionEntity.Columns.ADDED_TIME, collectionEntity.getAddedTime());
        values.put(CollectionEntity.Columns.LAST_MODIFY_DATE, collectionEntity.getLastModifyDate());
        values.put(CollectionEntity.Columns.LAST_MODIFY_TIME, collectionEntity.getLastModifyTime());
        values.put(CollectionEntity.Columns.SYNCED, collectionEntity.getSynced());
        return values;
    }

    private CollectionEntity getEntity(Cursor mCursor){
        CollectionEntity collectionEntity = new CollectionEntity();
        collectionEntity.setAccount(mCursor.getString(mCursor.getColumnIndex(CollectionEntity.Columns.ACCOUNT)));
        collectionEntity.setAddedDate(mCursor.getLong(mCursor.getColumnIndex(CollectionEntity.Columns.ADDED_DATE)));
        collectionEntity.setAddedTime(mCursor.getInt(mCursor.getColumnIndex(CollectionEntity.Columns.ADDED_TIME)));
        collectionEntity.setClnColor(mCursor.getString(mCursor.getColumnIndex(CollectionEntity.Columns.CLN_COLOR)));
        collectionEntity.setClnId(mCursor.getLong(mCursor.getColumnIndex(CollectionEntity.Columns.CLN_ID)));
        collectionEntity.setSynced(mCursor.getInt(mCursor.getColumnIndex(CollectionEntity.Columns.SYNCED)));
        collectionEntity.setCount(mCursor.getInt(mCursor.getColumnIndex(CollectionEntity.Columns.COUNT)));
        collectionEntity.setAddedDate(mCursor.getLong(mCursor.getColumnIndex(CollectionEntity.Columns.ADDED_DATE)));
        collectionEntity.setAddedTime(mCursor.getInt(mCursor.getColumnIndex(CollectionEntity.Columns.ADDED_TIME)));
        collectionEntity.setLastModifyDate(mCursor.getLong(mCursor.getColumnIndex(CollectionEntity.Columns.LAST_MODIFY_DATE)));
        collectionEntity.setLastModifyTime(mCursor.getInt(mCursor.getColumnIndex(CollectionEntity.Columns.LAST_MODIFY_TIME)));
        collectionEntity.setClnTitle(mCursor.getString(mCursor.getColumnIndex(CollectionEntity.Columns.CLN_TITLE)));
        return collectionEntity;
    }

    private List<CollectionEntity> getEntities(Cursor mCursor){
        List<CollectionEntity> list = new ArrayList<>();
        if (mCursor.moveToFirst()){
            do {
                list.add(getEntity(mCursor));
            } while (mCursor.moveToNext());
        }
        mCursor.close();
        return list;
    }
}
