package me.shouheng.timepartner.database.dao.loader;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import me.shouheng.timepartner.database.dao.base.ExamDAO;
import me.shouheng.timepartner.database.dao.base.NoteDAO;
import me.shouheng.timepartner.database.dao.base.TaskDAO;
import me.shouheng.timepartner.database.dao.bo.AssignBoDAO;
import me.shouheng.timepartner.database.dao.bo.ClassBoDAO;
import me.shouheng.timepartner.models.Model;
import me.shouheng.timepartner.models.business.assignment.AssignmentBO;
import me.shouheng.timepartner.models.business.tpclass.ClassBO;
import me.shouheng.timepartner.models.entities.exam.Exam;
import me.shouheng.timepartner.models.entities.note.Note;
import me.shouheng.timepartner.models.entities.task.Task;
import me.shouheng.timepartner.R;

/**
 * Created by wangshouheng on 2017/1/15. */
public class TrashLoader {

    private List trashEntities = Collections.emptyList();

    private ClassBoDAO classBoDAO;
    private AssignBoDAO assignBoDAO;
    private TaskDAO taskDAO;
    private ExamDAO examDAO;
    private NoteDAO noteDAO;

    private Context mContext;

    public static TrashLoader getInstance(Context context){
        return new TrashLoader(context);
    }

    private TrashLoader(Context mContext){
        this.mContext = mContext;
        classBoDAO = ClassBoDAO.getInstance(mContext);
        assignBoDAO = AssignBoDAO.getInstance(mContext);
        taskDAO = TaskDAO.getInstance(mContext);
        examDAO = ExamDAO.getInstance(mContext);
        noteDAO = NoteDAO.getInstance(mContext);
    }

    public void close(){
        if (classBoDAO != null) {
            classBoDAO.close();
        }
        if (assignBoDAO != null) {
            assignBoDAO.close();
        }
        if (taskDAO != null) {
            taskDAO.close();
        }
        if (examDAO != null) {
            examDAO.close();
        }
        if (noteDAO != null) {
            noteDAO.close();
        }
    }

    public List get(){
        load();
        return trashEntities;
    }

    public <T extends Model> void recover(T t){
        if (t instanceof ClassBO)
            classBoDAO.recover((ClassBO) t);
        if (t instanceof AssignmentBO)
            assignBoDAO.recover((AssignmentBO) t);
        if (t instanceof Task)
            taskDAO.recover((Task) t);
        if (t instanceof Exam)
            examDAO.recover((Exam) t);
        if (t instanceof Note)
            noteDAO.recover((Note) t);
    }

    public <T extends Model> void del(T t){
        if (t instanceof ClassBO)
            classBoDAO.delete((ClassBO) t);
        if (t instanceof AssignmentBO)
            assignBoDAO.delete((AssignmentBO) t);
        if (t instanceof Task)
            taskDAO.delete((Task) t);
        if (t instanceof Exam)
            examDAO.delete((Exam) t);
        if (t instanceof Note)
            noteDAO.delete((Note) t);
    }

    private void load(){
        trashEntities = new ArrayList();

        trashEntities.add(mContext.getString(R.string.class_title2));
        List<ClassBO> classBOs = classBoDAO.getTrash();
        trashEntities.addAll((Collection) classBOs);

        trashEntities.add(mContext.getString(R.string.assign_title));
        List<AssignmentBO> assignBOs = assignBoDAO.getTrash();
        trashEntities.addAll((Collection) assignBOs);

        trashEntities.add(mContext.getString(R.string.task));
        List<Task> taskEntities = taskDAO.getTrash();
        trashEntities.addAll((Collection) taskEntities);

        trashEntities.add(mContext.getString(R.string.exam));
        List<Exam> examEntities = examDAO.getTrash();
        trashEntities.addAll((Collection) examEntities);

        trashEntities.add(mContext.getString(R.string.n_title));
        List<Note> noteEntities = noteDAO.getTrash();
        trashEntities.addAll((Collection) noteEntities);
    }
}
