package me.shouheng.timepartner.models.business.exam;

import me.shouheng.timepartner.models.entities.tpclass.ClassEntity;
import me.shouheng.timepartner.models.entities.exam.Exam;

public class ExamBO {

    private Exam examEntity;

    /**
     * 该考试信息对应的课程实体 */
    private ClassEntity classEntity;

    public Exam getExamEntity() {
        return examEntity;
    }

    public void setExamEntity(Exam examEntity) {
        this.examEntity = examEntity;
    }

    public ClassEntity getClassEntity() {
        return classEntity;
    }

    public void setClassEntity(ClassEntity classEntity) {
        this.classEntity = classEntity;
    }

    @Override
    public String toString() {
        return "Exam:" + examEntity + "\nClass:" + classEntity;
    }
}
