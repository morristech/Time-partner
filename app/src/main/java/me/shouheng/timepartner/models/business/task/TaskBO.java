package me.shouheng.timepartner.models.business.task;

import me.shouheng.timepartner.models.entities.tpclass.ClassEntity;
import me.shouheng.timepartner.models.entities.task.Task;

public class TaskBO {

    private Task taskEntity;

    /**
     * 该作业信息对应的课程实体 */
    private ClassEntity classEntity;

    public Task getTaskEntity() {
        return taskEntity;
    }

    public void setTaskEntity(Task taskEntity) {
        this.taskEntity = taskEntity;
    }

    public ClassEntity getClassEntity() {
        return classEntity;
    }

    public void setClassEntity(ClassEntity classEntity) {
        this.classEntity = classEntity;
    }

    @Override
    public String toString() {
        return "Task:" + taskEntity + "\n" +
                "Class:" + classEntity;
    }
}
