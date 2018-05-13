package me.shouheng.timepartner.models.business.tpclass;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import me.shouheng.timepartner.models.Model;
import me.shouheng.timepartner.models.entities.tpclass.ClassDetail;
import me.shouheng.timepartner.models.entities.tpclass.ClassEntity;

public class ClassBO extends Model implements Serializable{

    /**
     * 该课程业务对象中的课程实体 */
    private ClassEntity classEntity;

    /**
     * 每个主课程对应的课程信息 */
    private List<ClassDetail> details = new ArrayList<>();

    /**
     * 在不需要获取详细的信息的情况下，为了减少组装信息过程之中的运行时间
     * 可以直接使用该属性获取数目信息 */
    private long count;

    public ClassEntity getClassEntity() {
        return classEntity;
    }

    public void setClassEntity(ClassEntity classEntity) {
        this.classEntity = classEntity;
    }

    public List<ClassDetail> getDetails() {
        return details;
    }

    public void setDetails(List<ClassDetail> details) {
        this.details = details;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "Class:" + classEntity.toString() + "\n" + "ClassDetails:" + details.toString();
    }
}
