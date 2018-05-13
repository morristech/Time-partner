package me.shouheng.timepartner.database.dao.loader;

import android.content.Context;

import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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
import me.shouheng.timepartner.utils.TpTime;

/**
 * Created by wangshouheng on 2017/1/16. */
public class WeekDetailsLoader {

    private List dailyDetailEntities = Collections.emptyList();

    private Context mContext;

    private ClassBoDAO classBoDAO;
    private AssignBoDAO assignBoDAO;
    private ExamDAO examDAO;
    private TaskDAO taskDAO;
    private NoteDAO noteDAO;

    public static WeekDetailsLoader getInstance(Context mContext){
        return new WeekDetailsLoader(mContext);
    }

    private WeekDetailsLoader(Context mContext){
        this.mContext = mContext;
    }

    public List loadOfThisWeek(){
        Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        return loadGivenDate(mYear, mMonth + 1, mDay);
    }

    public List loadGivenDate(int mYear, int mMonth, int mDay){
        long[] millis = TpTime.getWeekSundays(mYear, mMonth - 1, mDay);
        long millisOfWeekStart = millis[0];
        long millisOfWeekEnd = millis[1];

        return loadGivenDate(millisOfWeekStart, millisOfWeekEnd);
    }

    public List loadGivenDate(long millisOfWeekStart, long millisOfWeekEnd){
        openDatabase();

        dailyDetailEntities = new LinkedList<>();
        loadClasses(millisOfWeekStart, millisOfWeekEnd);
        loadExams(millisOfWeekStart, millisOfWeekEnd);
        loadTasks(millisOfWeekStart, millisOfWeekEnd);
        loadNotes(millisOfWeekStart, millisOfWeekEnd);
        loadNoteNotices(millisOfWeekStart, millisOfWeekEnd);
        loadAssigns(millisOfWeekStart, millisOfWeekEnd);

        closeDatabase();

        return dailyDetailEntities;
    }

    private void openDatabase(){
        assignBoDAO = AssignBoDAO.getInstance(mContext);
        classBoDAO = ClassBoDAO.getInstance(mContext);
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

    private void loadClasses(long millisOfWeekStart, long millisOfWeekEnd){
        List<ClassBO> classBOs = classBoDAO.getScope(millisOfWeekStart, millisOfWeekEnd);
        for (ClassBO classBO : classBOs){
            List<ClassDetail> detailEntities = classBO.getDetails();
            ClassEntity classEntity = classBO.getClassEntity();
            for (ClassDetail classDetailEntity : detailEntities){
                ClassDetailBO classDetailBO = new ClassDetailBO();
                classDetailBO.setDetailEntity(classDetailEntity);
                classDetailBO.setClassEntity(classEntity);
                dailyDetailEntities.add(classDetailBO);
            }
        }
    }

    private void loadAssigns(long millisOfWeekStart, long millisOfWeekEnd){
        List<AssignmentBO> assignBOs = assignBoDAO.getScope(millisOfWeekStart, millisOfWeekEnd);
        dailyDetailEntities.add(assignBOs);
    }

    private void loadTasks(long millisOfWeekStart, long millisOfWeekEnd){
        List<Task> taskEntities = taskDAO.getScope(millisOfWeekStart, millisOfWeekEnd);
        for (Task taskEntity : taskEntities){
            TaskBO taskBO = new TaskBO();
            long clsId = taskEntity.getClsId();
            ClassBO classBO = classBoDAO.get(clsId);
            if (classBO != null) {
                ClassEntity classEntity = classBO.getClassEntity();
                taskBO.setClassEntity(classEntity);
            }
            taskBO.setTaskEntity(taskEntity);
            dailyDetailEntities.add(taskBO);
        }
    }

    private void loadNotes(long millisOfWeekStart, long millisOfWeekEnd){
        List<Note> noteEntities = noteDAO.getScope(millisOfWeekStart, millisOfWeekEnd);
        for (Note noteEntity : noteEntities) {
            CollectionDAO collectionDAO = CollectionDAO.getInstance(mContext);
            long clnId = noteEntity.getClnId();
            CollectionEntity collectionEntity = collectionDAO.get(clnId);
            NoteBO noteBO = new NoteBO();
            noteBO.setCollection(collectionEntity);
            noteBO.setNote(noteEntity);
            dailyDetailEntities.add(noteBO);
        }
    }

    private void loadNoteNotices(long millisOfWeekStart, long millisOfWeekEnd){
        List<Note> noteEntities = noteDAO.getNoticeScope(millisOfWeekStart, millisOfWeekEnd);
        for (Note noteEntity : noteEntities) {
            CollectionDAO collectionDAO = CollectionDAO.getInstance(mContext);
            long clnId = noteEntity.getClnId();
            CollectionEntity collectionEntity = collectionDAO.get(clnId);
            NoteBO noteBO = new NoteBO();
            noteBO.setCollection(collectionEntity);
            noteBO.setNote(noteEntity);
            dailyDetailEntities.add(noteBO);
        }
    }

    private void loadExams(long millisOfWeekStart, long millisOfWeekEnd){
        List<Exam> examEntities = examDAO.getScope(millisOfWeekStart, millisOfWeekEnd);
        for (Exam examEntity : examEntities){
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
