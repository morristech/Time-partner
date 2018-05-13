package me.shouheng.timepartner.models.entities.note;

import me.shouheng.timepartner.models.entities.Entity;
import me.shouheng.timepartner.utils.TpTime;

public class BaseNote extends Entity {

    public static final String TABLE_NAME = "note_table";

    public static class Columns extends Entity.Columns {
        public static final String CLN_ID = "cln_id";
        public static final String NOTE_ID = "note_id";
        public static final String NOTE_TITLE = "note_title";
        public static final String NOTE_CONTENT = "note_content";
        public static final String RCD_PATH = "rcd_path";
        public static final String NOTICE_DATE = "notice_date";
        public static final String NOTICE_TIME = "notice_time";
        public static final String IN_TRASH = "in_trash";
        public static final String LIKED = "liked";
    }

    private long noteId;

    private long clnId;

    private String noteTitle;

    private String noteContent;

    private String recordPath;

    private long noteDate;

    private int noteTime;

    private int liked; // 0 false; 1 true

    public long getNoteId() {
        return noteId;
    }

    public void setNoteId(long noteId) {
        this.noteId = noteId;
    }

    public long getClnId() {
        return clnId;
    }

    public void setClnId(long clnId) {
        this.clnId = clnId;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public String getRecordPath() {
        return recordPath;
    }

    public void setRecordPath(String recordPath) {
        this.recordPath = recordPath;
    }

    public long getNoteDate() {
        return noteDate;
    }

    public void setNoteDate(long noteDate) {
        this.noteDate = noteDate;
    }

    public int getNoteTime() {
        return noteTime;
    }

    public void setNoteTime(int noteTime) {
        this.noteTime = noteTime;
    }

    public int getLiked() {
        return liked;
    }

    public void setLiked(int liked) {
        this.liked = liked;
    }

    @Override
    public String toString() {
        return Columns.ID + ":" + id + ", " +
                Columns.ACCOUNT + ":" + account + ", " +
                Columns.NOTE_TITLE + ":" + noteTitle + ", " +
                Columns.ADDED_DATE + ":" + TpTime.getFormatDate(addedDate) + ", " +
                Columns.ADDED_TIME + ":" + TpTime.getFormatTime(addedTime) + ", " +
                Columns.CLN_ID + ":" + clnId + ", " +
                Columns.IN_TRASH + ":" + inTrash + ", " +
                Columns.LIKED + ":" + liked + ", " +
                Columns.NOTE_CONTENT + ":" + noteContent + ", " +
                Columns.SYNCED + ":" + synced + ", " +
                Columns.NOTE_ID + ":" + noteId + ", " +
                Columns.RCD_PATH + ":" + recordPath + ", " +
                Columns.NOTICE_DATE + ":" + TpTime.getFormatDate(noteDate) + ", " +
                Columns.NOTICE_TIME + ":" + TpTime.getFormatTime(noteTime);
    }
}
