package me.shouheng.timepartner.database.dao.base;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.baidu.location.BDLocation;

import java.util.ArrayList;
import java.util.List;

import me.shouheng.timepartner.database.dao.SortType;
import me.shouheng.timepartner.models.entities.location.Location;

/**
 * Created by wangshouheng on 2017/1/22. */
public class LocationDAO extends BaseDAO<Location>{

    public static LocationDAO getInstance(Context context){
        return new LocationDAO(context);
    }

    private LocationDAO(Context context) {
        super(context);
    }

    @Override
    public List<Location> gets(long noteId) {
        return getLocations(db.rawQuery(" SELECT * FROM " + Location.TABLE_NAME +
                " WHERE " + Location.Columns.ACCOUNT + " = ? " +
                " AND " + Location.Columns.NOTE_ID + " = ? " +
                " ORDER BY " + Location.Columns.ADDED_DATE + " DESC, " +
                Location.Columns.ADDED_TIME,
                new String[]{strAccount, String.valueOf(noteId)}));
    }

    @Override
    public List<Location> gets(long noteId, SortType sortType) {
        if (sortType == SortType.DATE_DESC){
            return gets(noteId);
        } else {
            return getLocations(db.rawQuery(" SELECT * FROM " + Location.TABLE_NAME +
                    " WHERE " + Location.Columns.ACCOUNT + " = ? " +
                    " AND " + Location.Columns.NOTE_ID + " = ? " +
                    " ORDER BY " + Location.Columns.ADDED_DATE + " , " +
                    Location.Columns.ADDED_TIME,
                    new String[]{strAccount, String.valueOf(noteId)}));
        }
    }

    @Override
    public Location get(long locationId) {
        Location location = null;
        Cursor cursor = db.rawQuery(" SELECT * FROM " + Location.TABLE_NAME +
                " WHERE " + Location.Columns.LOCATION_ID + " = ? " +
                " AND " + Location.Columns.ACCOUNT + " = ? ",
                new String[]{String.valueOf(locationId), strAccount});
        if (cursor.moveToFirst()){
            location = getLocation(cursor);
        }
        cursor.close();
        return location;
    }

    @Override
    public List<Location> getAll() {
        return getLocations(db.rawQuery(" SELECT * FROM " + Location.TABLE_NAME +
                " WHERE " + Location.Columns.ACCOUNT + " = ? " +
                " ORDER BY " + Location.Columns.ADDED_DATE + " DESC, " +
                Location.Columns.ADDED_TIME,
                new String[]{strAccount}));
    }

    @Override
    public List<Location> getAll(SortType sortType) {
        if (sortType == SortType.DATE_DESC){
            return getAll();
        } else {
            return getLocations(db.rawQuery(" SELECT * FROM " + Location.TABLE_NAME +
                    " WHERE " + Location.Columns.ACCOUNT + " = ? " +
                    " ORDER BY " + Location.Columns.ADDED_DATE + " , " +
                    Location.Columns.ADDED_TIME,
                    new String[]{strAccount}));
        }
    }

    @Override
    public void insert(Location location) {
        db.insert(Location.TABLE_NAME, null, getCV(location));
    }

    @Override
    public void insert(List<Location> list) {
        for (Location location : list){
            insert(location);
        }
    }

    @Override
    public void update(Location location) {
        db.update(Location.TABLE_NAME, getCV(location),
                Location.Columns.ACCOUNT + " = ? AND " + Location.Columns.LOCATION_ID + " = ? ",
                new String[]{strAccount, String.valueOf(location.getLocationId())});
    }

    @Override
    public void update(List<Location> list) {
        for (Location location : list){
            update(location);
        }
    }

    @Override
    public void delete(Location location) {
        db.delete(Location.TABLE_NAME,
                Location.Columns.ACCOUNT + " = ? AND " + Location.Columns.LOCATION_ID + " = ? ",
                new String[]{strAccount, String.valueOf(location.getLocationId())});
    }

    @Override
    public void delete(List<Location> list) {
        for (Location location : list){
            delete(location);
        }
    }

    @Override
    public List<Location> getScope(long startMillis, long endMillis) {
        return getLocations(db.rawQuery(" SELECT * FROM " + Location.TABLE_NAME +
                " WHERE " + Location.Columns.ADDED_DATE + " >= ? " +
                " AND " + Location.Columns.ADDED_DATE + " <= ? " +
                " AND " + Location.Columns.ACCOUNT + " = ? " +
                " ORDER BY " + Location.Columns.ADDED_DATE + " DESC, " +
                Location.Columns.ADDED_TIME,
                new String[]{String.valueOf(startMillis), String.valueOf(endMillis), strAccount}));
    }

    @Override
    public List<Location> getScope(long startMillis, long endMillis, SortType sortType) {
        if (sortType == SortType.DATE_DESC){
            return getScope(startMillis, endMillis);
        } else {
            return getLocations(db.rawQuery(" SELECT * FROM " + Location.TABLE_NAME +
                    " WHERE " + Location.Columns.ADDED_DATE + " >= ? " +
                    " AND " + Location.Columns.ADDED_DATE + " <= ? " +
                    " AND " + Location.Columns.ACCOUNT + " = ? " +
                    " ORDER BY " + Location.Columns.ADDED_DATE + " , " +
                    Location.Columns.ADDED_TIME,
                    new String[]{String.valueOf(startMillis), String.valueOf(endMillis), strAccount}));
        }
    }

    @Override
    public void close() {
        super.close();
    }

    private Location getLocation(Cursor cursor){
        Location location = new Location();
        location.setAccount(cursor.getString(cursor.getColumnIndex(Location.Columns.ACCOUNT)));
        location.setLocationId(cursor.getLong(cursor.getColumnIndex(Location.Columns.LOCATION_ID)));
        location.setNoteId(cursor.getLong(cursor.getColumnIndex(Location.Columns.NOTE_ID)));

        location.setAltitude(cursor.getDouble(cursor.getColumnIndex(Location.Columns.ALTITUDE)));
        location.setLatitude(cursor.getDouble(cursor.getColumnIndex(Location.Columns.LATITUDE)));
        location.setCountry(cursor.getString(cursor.getColumnIndex(Location.Columns.COUNTRY)));
        location.setCountryCode(cursor.getString(cursor.getColumnIndex(Location.Columns.COUNTRYCODE)));
        location.setProvince(cursor.getString(cursor.getColumnIndex(Location.Columns.PROVINCE)));
        location.setCity(cursor.getString(cursor.getColumnIndex(Location.Columns.CITY)));
        location.setCityCode(cursor.getString(cursor.getColumnIndex(Location.Columns.CITYCODE)));
        location.setDistrict(cursor.getString(cursor.getColumnIndex(Location.Columns.DISTRICT)));
        location.setStreet(cursor.getString(cursor.getColumnIndex(Location.Columns.STREET)));
        location.setStreetNumber(cursor.getString(cursor.getColumnIndex(Location.Columns.STREETNUMBER)));

        location.setAddedDate(cursor.getLong(cursor.getColumnIndex(Location.Columns.ADDED_DATE)));
        location.setAddedTime(cursor.getInt(cursor.getColumnIndex(Location.Columns.ADDED_TIME)));

        location.setSynced(cursor.getInt(cursor.getColumnIndex(Location.Columns.SYNCED)));
        return location;
    }

    private List<Location> getLocations(Cursor cursor){
        List<Location> locations = new ArrayList<>();
        if (cursor.moveToFirst()){
            do {
                locations.add(getLocation(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return locations;
    }

    private ContentValues getCV(Location location){
        ContentValues values = new ContentValues();
        values.put(Location.Columns.ACCOUNT, location.getAccount());
        values.put(Location.Columns.LOCATION_ID, location.getLocationId());
        values.put(Location.Columns.NOTE_ID, location.getNoteId());

        values.put(Location.Columns.ALTITUDE, location.getAltitude());
        values.put(Location.Columns.LATITUDE, location.getLatitude());
        values.put(Location.Columns.COUNTRY, location.getCountry());
        values.put(Location.Columns.COUNTRYCODE, location.getCountryCode());
        values.put(Location.Columns.PROVINCE, location.getProvince());
        values.put(Location.Columns.CITY, location.getCity());
        values.put(Location.Columns.CITYCODE, location.getCityCode());
        values.put(Location.Columns.DISTRICT, location.getDistrict());
        values.put(Location.Columns.STREET, location.getStreet());
        values.put(Location.Columns.STREETNUMBER, location.getStreetNumber());

        values.put(Location.Columns.ADDED_DATE, location.getAddedDate());
        values.put(Location.Columns.ADDED_TIME, location.getAddedDate());

        values.put(Location.Columns.SYNCED, location.getSynced());

        return values;
    }
}
