package me.shouheng.timepartner.database.dao.bo;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import me.shouheng.timepartner.database.dao.base.AssignDAO;
import me.shouheng.timepartner.database.dao.base.AssignSubDAO;
import me.shouheng.timepartner.database.dao.SortType;
import me.shouheng.timepartner.models.business.assignment.AssignmentBO;
import me.shouheng.timepartner.models.entities.assignment.Assignment;
import me.shouheng.timepartner.models.entities.assignment.SubAssignment;

/**
 * Created by wangshouheng on 2017/1/14. */
public class AssignBoDAO extends AbstractBoDAO<AssignmentBO> {

    private AssignDAO assignDAO;

    private AssignSubDAO assignSubDAO;

    public static AssignBoDAO getInstance(Context context){
        return new AssignBoDAO(context);
    }

    private AssignBoDAO(Context context){
        assignDAO = AssignDAO.getInstance(context);
        assignSubDAO = AssignSubDAO.getInstance(context);
    }

    @Override
    public void close() {
        super.close();
    }

    @Override
    public void trash(AssignmentBO assignBO) {
        assignDAO.trash(assignBO.getAssignEntity());
    }

    @Override
    public void recover(AssignmentBO assignBO) {
        assignDAO.recover(assignBO.getAssignEntity());
    }

    @Override
    public void insert(AssignmentBO assignBO) {
        assignDAO.insert(assignBO.getAssignEntity());
        assignSubDAO.insert(assignBO.getSubEntities());
    }

    @Override
    public void delete(AssignmentBO assignBO) {
        assignDAO.delete(assignBO.getAssignEntity());
    }

    @Override
    public void update(AssignmentBO assignBO) {
        assignDAO.update(assignBO.getAssignEntity());
        assignSubDAO.update(assignBO.getSubEntities());
    }

    @Override
    public AssignmentBO get(long id) {
        AssignmentBO assignBO = new AssignmentBO();
        Assignment assignEntity = assignDAO.get(id);
        List<SubAssignment> subEntities = assignSubDAO.gets(id);
        assignBO.setCount(subEntities.size());
        assignBO.setAssignEntity(assignEntity);
        assignBO.setSubEntities(subEntities);
        return assignBO;
    }

    @Override
    public List<AssignmentBO> getAll() {
       return getAll(SortType.DATE_DESC);
    }

    @Override
    public List<AssignmentBO> getAll(SortType sortType) {
        List<Assignment> assignEntities = assignDAO.getAll(sortType);
        return getAssignBOs(assignEntities);
    }

    @Override
    public List<AssignmentBO> getOverdue() {
        return getOverdue(SortType.DATE_DESC);
    }

    @Override
    public List<AssignmentBO> getOverdue(SortType sortType) {
        List<Assignment> assignEntities = assignDAO.getOverdue(sortType);
        return getAssignBOs(assignEntities);
    }

    @Override
    public List<AssignmentBO> getOfDay(long millisOfDay) {
        List<Assignment> assignEntities = assignDAO.getOfDay(millisOfDay);
        return getAssignBOs(assignEntities);
    }

    @Override
    public List<AssignmentBO> getOfDay(long millisOfDay, SortType sortType) {
        List<Assignment> assignEntities = assignDAO.getOfDay(millisOfDay, sortType);
        return getAssignBOs(assignEntities);
    }

    @Override
    public List<AssignmentBO> getScope(long startMillis, long endMillis) {
        List<Assignment> assignEntities = assignDAO.getScope(startMillis, endMillis);
        return getAssignBOs(assignEntities);
    }

    @Override
    public List<AssignmentBO> getTrash() {
        List<Assignment> assignEntities = assignDAO.getTrash();
        return getAssignBOs(assignEntities);
    }

    @Override
    public List<AssignmentBO> getQuery(String query) {
        List<Assignment> assignEntities = assignDAO.getQuery(query);
        return getAssignBOs(assignEntities);
    }

    private List<AssignmentBO> getAssignBOs(List<Assignment> assignEntities){
        List<AssignmentBO> assignBOs = new ArrayList<>();
        for (Assignment assignEntity : assignEntities){
            AssignmentBO assignBO = new AssignmentBO();
            assignBO.setAssignEntity(assignEntity);
            long asnId = assignEntity.getAsnId();
            List<SubAssignment> subEntities = assignSubDAO.gets(asnId);
            assignBO.setSubEntities(subEntities);
            assignBO.setCount(subEntities.size());
            assignBOs.add(assignBO);
        }
        return assignBOs;
    }
}
