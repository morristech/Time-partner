package me.shouheng.timepartner.database.dao.base;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import me.shouheng.timepartner.database.dao.SortType;
import me.shouheng.timepartner.models.entities.picture.Picture;

/**
 * Created by wangshouheng on 2017/1/22.*/
public class PictureDAO extends BaseDAO<Picture>{

    public static PictureDAO getInstance(Context context){
        return new PictureDAO(context);
    }

    private PictureDAO(Context context) {
        super(context);
    }

    @Override
    public List<Picture> gets(long noteId) {
        return getPictures(db.rawQuery(" SELECT * FROM " + Picture.TABLE_NAME +
                " WHERE " + Picture.Columns.NOTE_ID + " = ? " +
                " AND " + Picture.Columns.ACCOUNT + " = ? " +
                " ORDER BY " + Picture.Columns.ADDED_DATE + " DESC, " +
                Picture.Columns.ADDED_TIME,
                new String[]{String.valueOf(noteId), strAccount}));
    }

    @Override
    public List<Picture> gets(long noteId, SortType sortType) {
        if (sortType == SortType.DATE_DESC){
            return gets(noteId);
        } else {
            return getPictures(db.rawQuery(" SELECT * FROM " + Picture.TABLE_NAME +
                    " WHERE " + Picture.Columns.NOTE_ID + " = ? " +
                    " AND " + Picture.Columns.ACCOUNT + " = ? " +
                    " ORDER BY " + Picture.Columns.ADDED_DATE + " , " +
                    Picture.Columns.ADDED_TIME,
                    new String[]{String.valueOf(noteId), strAccount}));
        }
    }

    @Override
    public Picture get(long pictureId) {
        Picture picture = null;
        Cursor cursor = db.rawQuery(" SELECT * FROM " + Picture.TABLE_NAME +
                " WHERE " + Picture.Columns.PICTURE_ID + " = ? " +
                " AND " + Picture.Columns.ACCOUNT + " = ? ",
                new String[]{String.valueOf(pictureId), strAccount});
        if (cursor.moveToFirst()){
            picture = getPicture(cursor);
        }
        cursor.close();
        return picture;
    }

    @Override
    public List<Picture> getAll() {
        return getPictures(db.rawQuery(" SELECT * FROM " + Picture.TABLE_NAME +
                " WHERE " + Picture.Columns.ACCOUNT + " = ? " +
                " ORDER BY " + Picture.Columns.ADDED_DATE + " DESC ",
                new String[]{strAccount}));
    }

    @Override
    public List<Picture> getAll(SortType sortType) {
        if (sortType == SortType.DATE_DESC){
            return getAll();
        } else {
            return getPictures(db.rawQuery(" SELECT * FROM " + Picture.TABLE_NAME +
                    " WHERE " + Picture.Columns.ACCOUNT + " = ? " +
                    " ORDER BY " + Picture.Columns.ADDED_DATE,
                    new String[]{strAccount}));
        }
    }

    @Override
    public void insert(Picture picture) {
        db.insert(Picture.TABLE_NAME, null, getCV(picture));
    }

    @Override
    public void insert(List<Picture> list) {
        for (Picture picture : list){
            insert(picture);
        }
    }

    @Override
    public void update(Picture picture) {
        db.update(Picture.TABLE_NAME, getCV(picture),
                Picture.Columns.ACCOUNT + " = ? " +
                " AND " + Picture.Columns.PICTURE_ID + " = ? ",
                new String[]{strAccount, String.valueOf(picture.getPictureId())});
    }

    // the list can be EMPTY, so you must add the noteId
    public void update(List<Picture> list, long noteId) {
//        for (Picture picture : list){
//            update(picture);
//        }
        db.beginTransaction();
        db.execSQL(" DELETE FROM " + Picture.TABLE_NAME +
                " WHERE " + Picture.Columns.ACCOUNT + " = ? " +
                " AND " + Picture.Columns.NOTE_ID + " = ? ",
                new String[]{strAccount, String.valueOf(noteId)});
        insert(list);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    @Override
    public void delete(Picture picture) {
        db.delete(Picture.TABLE_NAME,
                Picture.Columns.ACCOUNT + " = ? AND " + Picture.Columns.PICTURE_ID + " = ? ",
                new String[]{strAccount, String.valueOf(picture.getPictureId())});
    }

    @Override
    public void delete(List<Picture> list) {
        for (Picture picture : list){
            delete(picture);
        }
    }

    @Override
    public List<Picture> getScope(long startMillis, long endMillis) {
        return getPictures(db.rawQuery(" SELECT * FROM " + Picture.TABLE_NAME +
                " WHERE " + Picture.Columns.ADDED_DATE + " >= ? " +
                " AND " + Picture.Columns.ADDED_DATE + " <= ? " +
                " AND " + Picture.Columns.ACCOUNT + " = ? " +
                " ORDER BY " + Picture.Columns.ADDED_DATE + " DESC, " +
                Picture.Columns.ADDED_TIME,
                new String[]{String.valueOf(startMillis), String.valueOf(endMillis)}));
    }

    @Override
    public List<Picture> getScope(long startMillis, long endMillis, SortType sortType) {
        if (sortType == SortType.DATE_DESC){
            return getScope(startMillis, endMillis);
        } else {
            return getPictures(db.rawQuery(" SELECT * FROM " + Picture.TABLE_NAME +
                    " WHERE " + Picture.Columns.ADDED_DATE + " >= ? " +
                    " AND " + Picture.Columns.ADDED_DATE + " <= ? " +
                    " AND " + Picture.Columns.ACCOUNT + " = ? " +
                    " ORDER BY " + Picture.Columns.ADDED_DATE + " , " +
                    Picture.Columns.ADDED_TIME,
                    new String[]{String.valueOf(startMillis), String.valueOf(endMillis)}));
        }
    }

    @Override
    public void close() {
        super.close();
    }

    private Picture getPicture(Cursor cursor){
        Picture picture = new Picture();
        picture.setAccount(strAccount);
        picture.setNoteId(cursor.getLong(cursor.getColumnIndex(Picture.Columns.NOTE_ID)));

        picture.setPicturePath(cursor.getString(cursor.getColumnIndex(Picture.Columns.PICTURE_PATH)));
        picture.setPictureId(cursor.getLong(cursor.getColumnIndex(Picture.Columns.PICTURE_ID)));
        picture.setLocationId(cursor.getLong(cursor.getColumnIndex(Picture.Columns.LOCATION_ID)));
        picture.setComment(cursor.getString(cursor.getColumnIndex(Picture.Columns.COMMENT)));

        picture.setAddedDate(cursor.getLong(cursor.getColumnIndex(Picture.Columns.ADDED_DATE)));
        picture.setAddedTime(cursor.getInt(cursor.getColumnIndex(Picture.Columns.ADDED_TIME)));
        picture.setLastModifyDate(cursor.getLong(cursor.getColumnIndex(Picture.Columns.LAST_MODIFY_DATE)));
        picture.setLastModifyTime(cursor.getInt(cursor.getColumnIndex(Picture.Columns.LAST_MODIFY_TIME)));

        picture.setSynced(cursor.getInt(cursor.getColumnIndex(Picture.Columns.SYNCED)));
        return picture;
    }

    private List<Picture> getPictures(Cursor cursor){
        List<Picture> pictures = new ArrayList<>();
        if (cursor.moveToFirst()){
            do {
                pictures.add(getPicture(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return pictures;
    }

    private ContentValues getCV(Picture picture){
        ContentValues values = new ContentValues();
        values.put(Picture.Columns.ACCOUNT, strAccount);

        values.put(Picture.Columns.NOTE_ID, picture.getNoteId());
        values.put(Picture.Columns.PICTURE_ID, picture.getPictureId());
        values.put(Picture.Columns.LOCATION_ID, picture.getLocationId());
        values.put(Picture.Columns.PICTURE_PATH, picture.getPicturePath());
        values.put(Picture.Columns.COMMENT, picture.getComment());

        values.put(Picture.Columns.LAST_MODIFY_DATE, picture.getLastModifyDate());
        values.put(Picture.Columns.LAST_MODIFY_TIME, picture.getLastModifyTime());
        values.put(Picture.Columns.ADDED_DATE, picture.getAddedDate());
        values.put(Picture.Columns.ADDED_TIME, picture.getAddedTime());

        values.put(Picture.Columns.SYNCED, Picture.Columns.SYNCED);
        return values;
    }
}
