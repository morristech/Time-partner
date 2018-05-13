package me.shouheng.timepartner.models.business.assignment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import me.shouheng.timepartner.models.Model;
import me.shouheng.timepartner.models.entities.assignment.Assignment;
import me.shouheng.timepartner.models.entities.assignment.SubAssignment;

public class AssignmentBO extends Model implements Serializable{

    /**
     * 该日程业务对象中包含的日程实体 */
    private Assignment assignEntity;

    /**
     * 该主日程中包含的子日程的集合 */
    private List<SubAssignment> subEntities = new ArrayList<>();

    /**
     * 该主日程中包含的子日程的总数 */
    private int count;

    public Assignment getAssignEntity() {
        return assignEntity;
    }

    public void setAssignEntity(Assignment assignEntity) {
        this.assignEntity = assignEntity;
    }

    public List<SubAssignment> getSubEntities() {
        return subEntities;
    }

    public void setSubEntities(List<SubAssignment> subEntities) {
        this.subEntities = subEntities;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        String result = "";
        if (subEntities.size() != 0){
            count = subEntities.size();
        }
        for (SubAssignment subEntity : subEntities){
            result = result + subEntity.toString() + "\n";
        }
        return "AssignEntity:" + assignEntity.toString() + "\n" +
                "Count:" + count + "\n" +
                "List:" + result;
    }
}
