package me.shouheng.timepartner.models.entities.picture;

import me.shouheng.timepartner.models.entities.Entity;
import me.shouheng.timepartner.models.entities.location.Location;

/**
 * Created by wangshouheng on 2017/1/21. */
public class Picture extends Entity{

    public final static String TABLE_NAME = "picture_table";

    public final static class Columns extends Entity.Columns{
        public static final String NOTE_ID = "note_id";

        public final static String PICTURE_ID = "picture_id";
        public final static String LOCATION_ID = "location_id";
        public final static String PICTURE_PATH = "picture_path";
        public final static String COMMENT = "comment";
    }

    private long noteId;

    private long pictureId;

    private long locationId;

    private String picturePath;

    private String comment;

    public long getNoteId() {
        return noteId;
    }

    public void setNoteId(long noteId) {
        this.noteId = noteId;
    }

    public long getPictureId() {
        return pictureId;
    }

    public void setPictureId(long pictureId) {
        this.pictureId = pictureId;
    }

    public long getLocationId() {
        return locationId;
    }

    public void setLocationId(long locationId) {
        this.locationId = locationId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    @Override
    public String toString() {
        return "noteId:" + noteId + "," +
                Columns.ACCOUNT + ":" + account + "," +
                "pictureId:" + pictureId + "," +
                "locationId:" + locationId + "," +
                "picturePath:" + picturePath + "," +
                Columns.ADDED_DATE + ":" + addedDate + "," +
                Columns.ADDED_TIME + ":" + addedTime + "," +
                "comment:" + comment;
    }
}
