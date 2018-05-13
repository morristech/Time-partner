package me.shouheng.timepartner.models.entities.location;

import com.baidu.location.BDLocation;

import me.shouheng.timepartner.models.entities.Entity;

/**
 * Created by wangshouheng on 2017/1/21. */
public class Location extends Entity{

    public final static String TABLE_NAME = "location_table";

    public final static class Columns extends Entity.Columns{
        public static final String NOTE_ID = "note_id";

        public final static String LOCATION_ID = "location_id";
        public final static String LATITUDE = "latitude";
        public final static String ALTITUDE = "altitude";
        public final static String COUNTRY = "country";
        public final static String COUNTRYCODE = "countryCode";
        public final static String PROVINCE = "province";
        public final static String CITY = "city";
        public final static String CITYCODE = "cityCode";
        public final static String DISTRICT = "district";
        public final static String STREET = "street";
        public final static String STREETNUMBER = "streetNumber";

    }

    private long noteId;
    private long locationId;
    private double latitude;
    private double altitude;
    private String country;
    private String countryCode;
    private String province;
    private String city;
    private String cityCode;
    private String district;
    private String street;
    private String streetNumber;

    public long getNoteId() {
        return noteId;
    }

    public void setNoteId(long noteId) {
        this.noteId = noteId;
    }

    public long getLocationId() {
        return locationId;
    }

    public void setLocationId(long locationId) {
        this.locationId = locationId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    @Override
    public String toString() {
        return "noteId:" + noteId + "," +
                "locationId:" + locationId + "," +
                "latitude:" + latitude + "," +
                "altitude:" + altitude + "," +
                "country:" + country + "," +
                "countryCode:" + countryCode + "," +
                "province:" + province + "," +
                "city:" + city + "," +
                "cityCode:" + cityCode + "," +
                "district:" + district + "," +
                "street:" + street + "," +
                "streetNumber:" + streetNumber;
    }
}
