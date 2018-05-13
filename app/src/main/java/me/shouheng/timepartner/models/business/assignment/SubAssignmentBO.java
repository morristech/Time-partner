package me.shouheng.timepartner.models.business.assignment;

import me.shouheng.timepartner.models.entities.assignment.Assignment;
import me.shouheng.timepartner.models.entities.assignment.SubAssignment;

public class SubAssignmentBO {

    /**
     * 该子日程对应的主日程信息 */
    private Assignment assignEntity;

    /**
     * 该业务对象中包含的子日程 */
    private SubAssignment subEntity;

    public Assignment getAssignEntity() {
        return assignEntity;
    }

    public void setAssignEntity(Assignment assignEntity) {
        this.assignEntity = assignEntity;
    }

    public SubAssignment getSubEntity() {
        return subEntity;
    }

    public void setSubEntity(SubAssignment subEntity) {
        this.subEntity = subEntity;
    }

    @Override
    public String toString() {
        return subEntity.toString() + "\n" + assignEntity.toString();
    }
}
