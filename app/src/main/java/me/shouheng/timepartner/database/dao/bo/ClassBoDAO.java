package me.shouheng.timepartner.database.dao.bo;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import me.shouheng.timepartner.database.dao.base.ClassDAO;
import me.shouheng.timepartner.database.dao.base.ClassDetailDAO;
import me.shouheng.timepartner.database.dao.SortType;
import me.shouheng.timepartner.models.business.tpclass.ClassBO;
import me.shouheng.timepartner.models.entities.tpclass.ClassDetail;
import me.shouheng.timepartner.models.entities.tpclass.ClassEntity;

/**
 * Created by wangshouheng on 2017/1/14. */
public class ClassBoDAO extends AbstractBoDAO<ClassBO> {

    private ClassDAO classDAO;

    private ClassDetailDAO classDetailDAO;

    public static ClassBoDAO getInstance(Context context){
        return new ClassBoDAO(context);
    }

    private ClassBoDAO(Context context){
        classDAO = ClassDAO.getInstance(context);
        classDetailDAO = ClassDetailDAO.getInstance(context);
    }

    @Override
    public void close() {
        if (classDAO != null){
            classDAO.close();
        }
        if (classDetailDAO != null){
            classDetailDAO.close();
        }
    }

    @Override
    public void trash(ClassBO classBO) {
        classDAO.trash(classBO.getClassEntity());
    }

    public void trash(ClassBO classBO, ClassEntity.TrashType trashType){
        classDAO.trash(classBO.getClassEntity(), trashType);
    }

    @Override
    public void recover(ClassBO classBO) {
        classDAO.recover(classBO.getClassEntity());
    }

    @Override
    public void insert(ClassBO classBO) {
        classDAO.insert(classBO.getClassEntity());
        classDetailDAO.insert(classBO.getDetails());
    }

    public void delete(ClassBO classBO) {
        classDAO.delete(classBO.getClassEntity());
        classDetailDAO.delete(classBO.getDetails());
    }

    public void delete(ClassBO classBO, ClassEntity.DelType delType){
        classDAO.delete(classBO.getClassEntity(), delType);
        classDetailDAO.delete(classBO.getDetails());
    }

    @Override
    public void update(ClassBO classBO) {
        classDAO.update(classBO.getClassEntity());
        classDetailDAO.update(classBO.getDetails());
    }

    /**
     * @param classId 课程id
     * @return */
    @Override
    public ClassBO get(long classId) {
        ClassEntity classEntity = classDAO.get(classId);
        if (classEntity == null){
            return null;
        }
        return getClassBO(classEntity);
    }

    @Override
    public List<ClassBO> getAll() {
        return getAll(SortType.DATE_DESC);
    }

    @Override
    public List<ClassBO> getAll(SortType sortType) {
        return getClassBOs(classDAO.getAll(sortType));
    }

    @Override
    public List<ClassBO> getOverdue() {
        return getOverdue(SortType.DATE_DESC);
    }

    @Override
    public List<ClassBO> getOverdue(SortType sortType) {
        return getClassBOs(classDAO.getOverdue(sortType));
    }

    @Override
    public List<ClassBO> getOfDay(long millisOfDay) {
        return getClassBOs(classDAO.getOfDay(millisOfDay));
    }

    @Override
    public List<ClassBO> getOfDay(long millisOfDay, SortType sortType) {
        return getClassBOs(classDAO.getOfDay(millisOfDay, sortType));
    }

    @Override
    public List<ClassBO> getScope(long startMillis, long endMillis) {
        return getClassBOs(classDAO.getScope(startMillis, endMillis));
    }

    @Override
    public List<ClassBO> getTrash() {
        return getClassBOs(classDAO.getTrash());
    }

    @Override
    public List<ClassBO> getQuery(String query) {
        return getClassBOs(classDAO.getQuery(query));
    }

    private ClassBO getClassBO(ClassEntity classEntity){
        ClassBO classBO = new ClassBO();
        long clsId = classEntity.getClsId();
        List<ClassDetail> details = classDetailDAO.gets(clsId);
        classBO.setClassEntity(classEntity);
        classBO.setDetails(details);
        classBO.setCount(details.size());
        return classBO;
    }

    private List<ClassBO> getClassBOs(List<ClassEntity> classEntities){
        List<ClassBO> classBOs = new ArrayList<>();
        for (ClassEntity classEntity : classEntities){
            classBOs.add(getClassBO(classEntity));
        }
        return classBOs;
    }
}
