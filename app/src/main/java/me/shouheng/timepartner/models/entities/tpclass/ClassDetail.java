package me.shouheng.timepartner.models.entities.tpclass;

import me.shouheng.timepartner.models.entities.Entity;
import me.shouheng.timepartner.utils.TpTime;

// 星期必须用字符串，因为比如0110000，用整数的话会被解析成11就错了
// 考虑到实体类在各个组件之间交换比较频繁，所以使用时间的字符串和周次的TAG值
public class ClassDetail extends Entity {

    public final static String TABLE_NAME = "class_detail_table";

    public final static class Columns extends Entity.Columns{
        public final static String DETAIL_ID = "class_detail_id";
        public final static String CLASS_ID = "class_id";
        public final static String START_TIME = "start_time";
        public final static String END_TIME = "end_time";
        public final static String ROOM = "room";
        public final static String TEACHER = "teacher";
        public final static String WEEK = "week";
    }

    private long detailId;

    private long classId;

    private String room; // 解析之后的字符串

    private String teacher; // 解析之后的字符串

    private String week; // TAG值

    private int startTime;

    private int endTime;

    public long getClassId() {
        return classId;
    }

    public void setClassId(long classId) {
        this.classId = classId;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public long getDetailId() {
        return detailId;
    }

    public void setDetailId(long detailId) {
        this.detailId = detailId;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return "id:" + id + " , " +
                "class_detail_id:" + detailId + " , " +
                "account:" + account + " , " +
                "class_id:" + classId + " , " +
                "start_time:" + TpTime.getFormatTime(startTime) + " , " +
                "end_time:" + TpTime.getFormatTime(endTime) + " , " +
                "room:" + room + " , " +
                "teacher:" + teacher + " , " +
                "week:" + week + " , " +
                "synced:" + synced;
    }

}
