package me.shouheng.timepartner.database.dao.base;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.LinkedList;
import java.util.List;

import me.shouheng.timepartner.models.entities.rating.Rating;
import me.shouheng.timepartner.utils.TpTime;

/**
 * Created by wangshouheng on 2017/1/15.*/
public class RatingDAO extends BaseDAO<Rating> {

    public static RatingDAO getInstance(Context context){
        return new RatingDAO(context);
    }

    private RatingDAO(Context context) {
        super(context);
    }

    @Override
    public void close() {
        super.close();
    }

    public Rating getToday() {
        long queryTime = TpTime.millisOfCurrentDate();
        Cursor mCursor = db.rawQuery(" SELECT * FROM " + Rating.TABLE_NAME +
                " WHERE " + Rating.Columns.RATE_DATE + " = ? " +
                " AND " + Rating.Columns.ACCOUNT + " = ? ",
                new String[]{String.valueOf(queryTime), strAccount});
        if (mCursor.moveToFirst()){
            return getEntity(mCursor);
        }
        return null;
    }

    @Override
    public List<Rating> getScope(long startMillis, long endMillis) {
        Cursor mCursor = db.rawQuery(" SELECT * FROM " + Rating.TABLE_NAME +
                " WHERE " + Rating.Columns.RATE_DATE + " >= ? " +
                " AND " + Rating.Columns.RATE_DATE + " <= ? " +
                " AND " + Rating.Columns.ACCOUNT + " = ?",
                new String[]{String.valueOf(startMillis), String.valueOf(endMillis), strAccount});
        List<Rating> list = new LinkedList<>();
        if (mCursor.moveToFirst()){
            do {
                Rating ratingEntity = getEntity(mCursor);
                list.add(ratingEntity);
            } while (mCursor.moveToNext());
        }
        mCursor.close();
        return list;
    }

    private Rating getEntity(Cursor mCursor){
        Rating ratingEntity = new Rating();
        ratingEntity.setAccount(mCursor.getString(mCursor.getColumnIndex(Rating.Columns.ACCOUNT)));
        ratingEntity.setRateDate(mCursor.getLong(mCursor.getColumnIndex(Rating.Columns.RATE_DATE)));
        ratingEntity.setRateComments(mCursor.getString(mCursor.getColumnIndex(Rating.Columns.RATE_COMMENT)));
        ratingEntity.setRating(mCursor.getFloat(mCursor.getColumnIndex(Rating.Columns.RATING)));

        ratingEntity.setAddedDate(mCursor.getLong(mCursor.getColumnIndex(Rating.Columns.ADDED_DATE)));
        ratingEntity.setAddedTime(mCursor.getInt(mCursor.getColumnIndex(Rating.Columns.ADDED_TIME)));
        ratingEntity.setLastModifyDate(mCursor.getLong(mCursor.getColumnIndex(Rating.Columns.LAST_MODIFY_DATE)));
        ratingEntity.setLastModifyTime(mCursor.getInt(mCursor.getColumnIndex(Rating.Columns.LAST_MODIFY_TIME)));

        ratingEntity.setSynced(mCursor.getInt(mCursor.getColumnIndex(Rating.Columns.SYNCED)));
        return ratingEntity;
    }

    @Override
    public void insert(Rating ratingEntity) {
        ContentValues values = new ContentValues();
        values.put(Rating.Columns.ACCOUNT, strAccount);
        values.put(Rating.Columns.RATE_DATE, ratingEntity.getRateDate());
        values.put(Rating.Columns.RATE_COMMENT, ratingEntity.getRateComments());
        values.put(Rating.Columns.RATING, ratingEntity.getRating());

        values.put(Rating.Columns.ADDED_DATE, ratingEntity.getAddedDate());
        values.put(Rating.Columns.ADDED_TIME, ratingEntity.getAddedTime());
        values.put(Rating.Columns.LAST_MODIFY_DATE, ratingEntity.getLastModifyDate());
        values.put(Rating.Columns.LAST_MODIFY_TIME, ratingEntity.getLastModifyTime());

        values.put(Rating.Columns.SYNCED, ratingEntity.getSynced());
        db.insert(Rating.TABLE_NAME, null, values);
    }

    @Override
    public void trash(Rating ratingEntity) {
        super.trash(ratingEntity);
    }

    @Override
    public void recover(Rating ratingEntity) {
        super.recover(ratingEntity);
    }
}
