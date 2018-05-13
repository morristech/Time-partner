package me.shouheng.timepartner.database.dao.loader;

import android.content.Context;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import me.shouheng.timepartner.database.dao.DAOHelper;
import me.shouheng.timepartner.database.dao.base.CollectionDAO;
import me.shouheng.timepartner.database.dao.base.ExamDAO;
import me.shouheng.timepartner.database.dao.base.NoteDAO;
import me.shouheng.timepartner.database.dao.base.TaskDAO;
import me.shouheng.timepartner.database.dao.bo.AssignBoDAO;
import me.shouheng.timepartner.database.dao.bo.ClassBoDAO;
import me.shouheng.timepartner.models.business.assignment.AssignmentBO;
import me.shouheng.timepartner.models.business.tpclass.ClassBO;
import me.shouheng.timepartner.models.business.tpclass.ClassDetailBO;
import me.shouheng.timepartner.models.business.exam.ExamBO;
import me.shouheng.timepartner.models.business.note.NoteBO;
import me.shouheng.timepartner.models.business.task.TaskBO;
import me.shouheng.timepartner.models.entities.tpclass.ClassDetail;
import me.shouheng.timepartner.models.entities.tpclass.ClassEntity;
import me.shouheng.timepartner.models.entities.collection.CollectionEntity;
import me.shouheng.timepartner.models.entities.exam.Exam;
import me.shouheng.timepartner.models.entities.note.Note;
import me.shouheng.timepartner.models.entities.task.Task;
import me.shouheng.timepartner.R;
import me.shouheng.timepartner.utils.TpTime;

public class DayDetailsLoader {

    private List dailyDetailEntities = Collections.emptyList();

    private Context mContext;

    private ClassBoDAO classBoDAO;
    private AssignBoDAO assignBoDAO;
    private ExamDAO examDAO;
    private TaskDAO taskDAO;
    private NoteDAO noteDAO;

    public static DayDetailsLoader getInstance(Context mContext){
        return new DayDetailsLoader(mContext);
    }

    private DayDetailsLoader(Context mContext){
        this.mContext = mContext;
    }

    public List loadOfToday(){
        return loadGivenDate(TpTime.millisOfCurrentDate());
    }

    public List loadGivenDate(long millisOfDay){
        openDatabase();
        dailyDetailEntities = new LinkedList<>();
        loadClasses(millisOfDay);
        loadExams(millisOfDay);
        loadTasks(millisOfDay);
        loadNotes(millisOfDay);
        loadAssigns(millisOfDay);
        closeDatabase();
        return dailyDetailEntities;
    }

    private void openDatabase(){
        classBoDAO = ClassBoDAO.getInstance(mContext);
        assignBoDAO = AssignBoDAO.getInstance(mContext);
        examDAO = ExamDAO.getInstance(mContext);
        taskDAO = TaskDAO.getInstance(mContext);
        noteDAO = NoteDAO.getInstance(mContext);
    }

    private void closeDatabase(){
        if (classBoDAO != null) classBoDAO.close();
        if (assignBoDAO != null) assignBoDAO.close();
        if (taskDAO != null) taskDAO.close();
        if (examDAO != null) examDAO.close();
        if (noteDAO != null) noteDAO.close();
    }

    private void loadClasses(long millisOfDay){
        List<ClassDetailBO> tempList = new LinkedList<>();
        List<ClassBO> classBOs = classBoDAO.getOfDay(millisOfDay);
        boolean isFirstClass = true;

        for (ClassBO classBO : classBOs){
            List<ClassDetail> detailEntities = classBO.getDetails();
            ClassEntity classEntity = classBO.getClassEntity();
            int weekDay = TpTime.weekOfDay(millisOfDay);
            for (ClassDetail detailEntity : detailEntities){
                String clsWeek = detailEntity.getWeek();
                if (clsWeek.charAt(weekDay) == '1'){
                    if (isFirstClass){
                        isFirstClass = false;
                        dailyDetailEntities.add(mContext.getString(R.string.cld_section2));
                    }
                    ClassDetailBO classDetailBO = new ClassDetailBO();
                    classDetailBO.setClassEntity(classEntity);
                    classDetailBO.setDetailEntity(detailEntity);
                    tempList.add(classDetailBO);
                }
            }
        }
        // 排序
        DAOHelper.sortClass(tempList);
        dailyDetailEntities.addAll(tempList);
    }

    // 日程的时间为指定日期 不包含未完成的日期
    private void loadAssigns(long millisOfDay){
        List<AssignmentBO> assignBOs = assignBoDAO.getOfDay(millisOfDay);
        boolean isFirstAssign = true;

        for (AssignmentBO assignBO : assignBOs){
            if (isFirstAssign){
                isFirstAssign = false;
                dailyDetailEntities.add(mContext.getString(R.string.cld_section3));
            }
            dailyDetailEntities.add(assignBO);
        }
    }

    private void loadTasks(long millisOfDay){
        List<Task> taskEntities = taskDAO.getOfDay(millisOfDay);
        boolean isFirstTask = true;

        for (Task taskEntity : taskEntities){
            if (isFirstTask){
                isFirstTask = false;
                dailyDetailEntities.add(mContext.getString(R.string.cld_section4));
            }

            TaskBO taskBO = new TaskBO();
            taskBO.setTaskEntity(taskEntity);
            long clsId = taskEntity.getClsId();
            ClassBO classBO = classBoDAO.get(clsId);
            if (classBO != null) {
                ClassEntity classEntity = classBO.getClassEntity();
                taskBO.setClassEntity(classEntity);
            }

            dailyDetailEntities.add(taskBO);
        }
    }

    private void loadNotes(long millisOfDay){
        List<Note> noteEntities = noteDAO.getOfDay(millisOfDay);
        boolean isFirstNote = true;

        for (Note noteEntity : noteEntities){
            if (isFirstNote){
                isFirstNote = false;
                dailyDetailEntities.add(mContext.getString(R.string.cld_section6));
            }
            long clnId = noteEntity.getClnId();
            CollectionDAO collectionDAO = CollectionDAO.getInstance(mContext);
            CollectionEntity collectionEntity = collectionDAO.get(clnId);
            NoteBO noteBO = new NoteBO();
            noteBO.setCollection(collectionEntity);
            noteBO.setNote(noteEntity);
            dailyDetailEntities.add(noteBO);
        }
    }

    private void loadExams(long millisOfDay){
        List<Exam> examEntities = examDAO.getOfDay(millisOfDay);
        boolean isFirstExam = true;

        for (Exam examEntity : examEntities){
            if (isFirstExam){
                isFirstExam = false;
                dailyDetailEntities.add(mContext.getString(R.string.cld_section5));
            }

            ExamBO examBO = new ExamBO();
            examBO.setExamEntity(examEntity);
            long clsId = examEntity.getClsId();
            ClassBO classBO = classBoDAO.get(clsId);
            if (classBO != null) {
                ClassEntity classEntity = classBO.getClassEntity();
                examBO.setClassEntity(classEntity);
            }

            dailyDetailEntities.add(examBO);
        }
    }
}
