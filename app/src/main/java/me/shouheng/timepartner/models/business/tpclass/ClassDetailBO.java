package me.shouheng.timepartner.models.business.tpclass;

import me.shouheng.timepartner.models.entities.tpclass.ClassDetail;
import me.shouheng.timepartner.models.entities.tpclass.ClassEntity;

public class ClassDetailBO {

    /**
     * 业务对象中的课程信息实体 */
    private ClassDetail detailEntity;

    /**
     * 该课程详细信息对应的课程实体 */
    private ClassEntity classEntity;

    public ClassDetail getDetailEntity() {
        return detailEntity;
    }

    public void setDetailEntity(ClassDetail detailEntity) {
        this.detailEntity = detailEntity;
    }

    public ClassEntity getClassEntity() {
        return classEntity;
    }

    public void setClassEntity(ClassEntity classEntity) {
        this.classEntity = classEntity;
    }

    @Override
    public String toString() {
        return "Detail:" + detailEntity.toString() + "\n" + "Class:" + classEntity.toString();
    }

}
