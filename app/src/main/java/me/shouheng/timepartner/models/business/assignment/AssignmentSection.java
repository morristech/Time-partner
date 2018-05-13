package me.shouheng.timepartner.models.business.assignment;

import me.shouheng.timepartner.models.business.Section;

/**
 * Created by wangshouheng on 2017/1/14. */
public class AssignmentSection extends Section {

    private AssignmentBO assignBO;

    public AssignmentBO getAssignBO() {
        return assignBO;
    }

    public void setAssignBO(AssignmentBO assignBO) {
        this.assignBO = assignBO;
    }
}
