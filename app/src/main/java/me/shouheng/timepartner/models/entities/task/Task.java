package me.shouheng.timepartner.models.entities.task;

import me.shouheng.timepartner.models.entities.Entity;
import me.shouheng.timepartner.utils.TpTime;

public class Task extends Entity {

    public static final String TABLE_NAME = "task_table";

    public static final class Columns extends Entity.Columns{
        public static final String CLASS_ID = "class_id";
        public static final String TASK_ID = "task_id";
        public static final String TASK_TITLE = "task_title";
        public static final String TASK_DATE = "task_date";
        public static final String TASK_TIME = "task_time";
        public static final String TASK_CONTENT = "task_content";
        public static final String TASK_COMMENT = "task_comment";
    }

    private long taskId;

    private long clsId;

    private String taskTitle;

    private long taskDate;

    private int taskTime;

    private String taskContent;

    private String taskComments;

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public long getClsId() {
        return clsId;
    }

    public void setClsId(long clsId) {
        this.clsId = clsId;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public long getTaskDate() {
        return taskDate;
    }

    public void setTaskDate(long taskDate) {
        this.taskDate = taskDate;
    }

    public int getTaskTime() {
        return taskTime;
    }

    public void setTaskTime(int taskTime) {
        this.taskTime = taskTime;
    }

    public String getTaskContent() {
        return taskContent;
    }

    public void setTaskContent(String taskContent) {
        this.taskContent = taskContent;
    }

    public String getTaskComments() {
        return taskComments;
    }

    public void setTaskComments(String taskComments) {
        this.taskComments = taskComments;
    }

    @Override
    public String toString() {
        return  Columns.ID + ":" + id + " , " +
                Columns.ACCOUNT + ":" + account + " , " +
                Columns.CLASS_ID + ":" + clsId + " , " +
                Columns.TASK_ID + ":" + taskId + " , " +
                Columns.TASK_TITLE + ":" + taskTitle + " , " +
                Columns.TASK_DATE + ":" + TpTime.getFormatDate(taskDate) + " , " +
                Columns.TASK_TIME + ":" + TpTime.getFormatTime(taskTime) + " , " +
                Columns.TASK_CONTENT + ":" + taskContent + " , " +
                Columns.TASK_COMMENT + ":" + taskComments + " , " +
                Columns.IN_TRASH + ":" + inTrash + " , " +
                Columns.SYNCED + ":" + synced;
    }

}
