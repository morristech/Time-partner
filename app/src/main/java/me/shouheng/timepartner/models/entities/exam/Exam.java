package me.shouheng.timepartner.models.entities.exam;

import me.shouheng.timepartner.models.entities.Entity;
import me.shouheng.timepartner.utils.TpTime;

public class Exam extends Entity {

    public final static String TABLE_NAME = "exam_table";

    public final static class Columns extends Entity.Columns{
        public final static String CLASS_ID = "class_id";
        public final static String EXAM_ID = "exam_id";
        public final static String EXAM_TITLE = "exam_title";
        public final static String EXAM_DATE = "exam_date";
        public final static String EXAM_TIME = "exam_time";
        public final static String EXAM_CONTENT = "exam_content";
        public final static String ROOM = "exam_room";
        public final static String SEAT = "exam_seat";
        public final static String EXAM_COMMENT = "exam_comment";
        public final static String DURATION = "duration";
    }

    private long examId;

    private long clsId;

    private String examTitle;

    private long examDate;

    private int examTime;

    private String examContent;

    private String examRoom;

    private String examSeat;

    private long duration;

    private String examComments;

    public long getExamId() {
        return examId;
    }

    public void setExamId(long examId) {
        this.examId = examId;
    }

    public long getClsId() {
        return clsId;
    }

    public void setClsId(long clsId) {
        this.clsId = clsId;
    }

    public String getExamTitle() {
        return examTitle;
    }

    public void setExamTitle(String examTitle) {
        this.examTitle = examTitle;
    }

    public long getExamDate() {
        return examDate;
    }

    public void setExamDate(long examDate) {
        this.examDate = examDate;
    }

    public int getExamTime() {
        return examTime;
    }

    public void setExamTime(int examTime) {
        this.examTime = examTime;
    }

    public String getExamContent() {
        return examContent;
    }

    public void setExamContent(String examContent) {
        this.examContent = examContent;
    }

    public String getExamRoom() {
        return examRoom;
    }

    public void setExamRoom(String examRoom) {
        this.examRoom = examRoom;
    }

    public String getExamSeat() {
        return examSeat;
    }

    public void setExamSeat(String examSeat) {
        this.examSeat = examSeat;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getExamComments() {
        return examComments;
    }

    public void setExamComments(String examComments) {
        this.examComments = examComments;
    }

    @Override
    public String toString() {
        return "id;" + id + " , " +
        "account:" + account + " , " +
        "class_id;" + clsId + " , " +
        "exam_id:" + examId + " , " +
        "exam_title:" + examTitle + " , " +
        "exam_date:" + TpTime.getDate(examDate) + " , " +
        "exam_time:" + TpTime.getTime(examTime) + " , " +
        "exam_content:" + examContent + " , " +
        "exam_room:" + examRoom + " , " +
        "exam_seat:" + examSeat + " , " +
        "exam_comment:" + examComments + " , " +
        "in_trash:" + inTrash + " , " +
        "duration:" + duration + " , " +
        "synced:" + synced;
    }
}
