package me.shouheng.timepartner.database.dao.loader;

import android.content.Context;
import android.util.LongSparseArray;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import me.shouheng.timepartner.database.dao.base.AssignDAO;
import me.shouheng.timepartner.database.dao.base.ClassDAO;
import me.shouheng.timepartner.database.dao.base.CollectionDAO;
import me.shouheng.timepartner.database.dao.base.ExamDAO;
import me.shouheng.timepartner.database.dao.base.NoteDAO;
import me.shouheng.timepartner.database.dao.base.RatingDAO;
import me.shouheng.timepartner.database.dao.base.TaskDAO;
import me.shouheng.timepartner.database.dao.bo.ClassBoDAO;
import me.shouheng.timepartner.models.business.tpclass.ClassBO;
import me.shouheng.timepartner.models.entities.assignment.Assignment;
import me.shouheng.timepartner.models.entities.tpclass.ClassDetail;
import me.shouheng.timepartner.models.entities.tpclass.ClassEntity;
import me.shouheng.timepartner.models.entities.collection.CollectionEntity;
import me.shouheng.timepartner.models.entities.exam.Exam;
import me.shouheng.timepartner.models.entities.note.Note;
import me.shouheng.timepartner.models.entities.rating.Rating;
import me.shouheng.timepartner.models.entities.task.Task;
import me.shouheng.timepartner.utils.TpColor;
import me.shouheng.timepartner.utils.TpTime;

/**
 * 使用方法：
 * Step1 创建一个实例
 * Step2 使用loadXXX方法装载指定类型的数据，
 * Step3 使用get方法来获取需要的数据
 * Step4 使用release释放资源 */
public class MonthDetailsLoader {

    private Context mContext;

    private ClassBoDAO classBoDAO;
    private AssignDAO assignDAO;
    private TaskDAO taskDAO;
    private ExamDAO examDAO;
    private NoteDAO noteDAO;
    private RatingDAO ratingDAO;
    private ClassDAO classDAO;
    private CollectionDAO collectionDAO;

    /**
     * 检索的月份的开始毫秒数  月历第一个日期的毫秒数 */
    private long startQueryMillis;
    /**
     * 检索的月份的结束毫秒数  月历最后一个日期的毫秒数 */
    private long endQueryMillis;

    private LongSparseArray<List<String>> map;

    /**
     * 评分的数据集合 键是数据的日期毫秒数；值是评分的数值的集合 */
    private LongSparseArray<Float> rateMap;

    private static final String TAG = "MonthDataLoader__";

    public static MonthDetailsLoader getInstance(Context context){
        return new MonthDetailsLoader(context);
    }

    private MonthDetailsLoader(Context mContext){
        this.mContext = mContext;
    }

    public void loadGivenMonth(int mYear, int mMonth){
        // 月 1即1月
        map = new LongSparseArray<>();
        rateMap = new LongSparseArray<>();

        initQueryTime(mYear, mMonth);

        load();
    }

    public LongSparseArray<Float> getRateMap(){
        return rateMap;
    }

    public LongSparseArray<List<String>> getMap(){
        return map;
    }

    private void load(){
        open();

        putRatings();
        putClasses();
        putExams();
        putTasks();
        putAssigns();
//        putSubAssigns();
        putNoteNotices();
        putNote();

        release();
    }

    private void open(){
        classBoDAO = ClassBoDAO.getInstance(mContext);
        assignDAO = AssignDAO.getInstance(mContext);
        taskDAO = TaskDAO.getInstance(mContext);
        examDAO = ExamDAO.getInstance(mContext);
        noteDAO = NoteDAO.getInstance(mContext);
        ratingDAO = RatingDAO.getInstance(mContext);
        classDAO = ClassDAO.getInstance(mContext);
        collectionDAO = CollectionDAO.getInstance(mContext);
    }

    private void release(){
        if (classBoDAO != null) classBoDAO.close();
        if (assignDAO != null) assignDAO.close();
        if (taskDAO != null) taskDAO.close();
        if (examDAO != null) examDAO.close();
        if (noteDAO != null) noteDAO.close();
        if (ratingDAO != null) ratingDAO.close();
        if (classDAO != null) classDAO.close();
        if (collectionDAO != null) collectionDAO.close();
    }

    private void putRatings(){
        List<Rating> ratingEntities = ratingDAO.getScope(startQueryMillis, endQueryMillis);
        for (Rating ratingEntity : ratingEntities){
            rateMap.put(ratingEntity.getRateDate(), ratingEntity.getRating());
        }
    }

    private void putClasses(){
        List<ClassBO> classBOs = classBoDAO.getScope(startQueryMillis, endQueryMillis);
        for (ClassBO classBO : classBOs){
            ClassEntity classEntity = classBO.getClassEntity();
            long clsStartMills = classEntity.getStartDate();
            long clsEndMillis = classEntity.getEndDate();
            String clsColor = classEntity.getClsColor();

            // 缩小课程的起止日期，超出日历范围的规划到日历的起止日期, 主要用在下面的循环中
            clsStartMills = clsStartMills < startQueryMillis ? startQueryMillis : clsStartMills;
            clsEndMillis = clsEndMillis > endQueryMillis ? endQueryMillis : clsEndMillis;

            List<ClassDetail> classDetailEntities = classBO.getDetails();
            for (ClassDetail classDetailEntity : classDetailEntities){
                String strWeek = classDetailEntity.getWeek();
                for (long l=clsStartMills;l<=clsEndMillis;l+= TpTime.DAY_MILLIS){
                    Calendar time = Calendar.getInstance();
                    time.setTimeInMillis(l);
                    int weekday = time.get(Calendar.DAY_OF_WEEK) - 1;

                    // 判断指定位置是否存在课程，如果存在课程则指定位置处课程总数+1
                    if (strWeek.charAt(weekday) == '1'){
                        addToMap(l, clsColor);
                    }
                }
            }
        }
    }

    private void putAssigns(){
        List<Assignment> assignEntities = assignDAO.getScope(startQueryMillis, endQueryMillis);
        for (Assignment assignEntity : assignEntities) {
            String asnColor = assignEntity.getAsnColor();
            long asbDate = assignEntity.getAsnDate();

            addToMap(asbDate, asnColor);
        }
    }

    private void putTasks(){
        List<Task> taskEntities = taskDAO.getScope(startQueryMillis, endQueryMillis);
        for (Task taskEntity : taskEntities){
            String taskColor = TpColor.COLOR_TASK;
            long clsId = taskEntity.getClsId();
            ClassEntity classEntity = classDAO.get(clsId);
            if (classEntity != null) {
                taskColor = classEntity.getClsColor();
            }
            long taskDate = taskEntity.getTaskDate();

            addToMap(taskDate, taskColor);
        }
    }

    private void putExams(){
        List<Exam> examEntities = examDAO.getScope(startQueryMillis, endQueryMillis);
        for (Exam examEntity : examEntities){
            String examColor = TpColor.COLOR_EXAM;
            long clsId = examEntity.getClsId();
            ClassEntity classEntity = classDAO.get(clsId);
            if (classEntity != null) {
                examColor = classEntity.getClsColor();
            }
            long examDate = examEntity.getExamDate();

            addToMap(examDate, examColor);
        }
    }

    private void putNoteNotices(){
        List<Note> noteEntities = noteDAO.getNoticeScope(startQueryMillis, endQueryMillis);
        for (Note noteEntity : noteEntities){
            String noteColor = TpColor.COLOR_NOTE;
            long clnId = noteEntity.getClnId();
            CollectionEntity collectionEntity = collectionDAO.get(clnId);
            if (collectionEntity != null) {
                noteColor = collectionEntity.getClnColor();
            }
            long noteDate = noteEntity.getNoteDate();

            addToMap(noteDate, noteColor);
        }
    }

    private void putNote(){
        List<Note> noteEntities = noteDAO.getScope(startQueryMillis, endQueryMillis);
        for (Note noteEntity : noteEntities){
            String noteColor = TpColor.COLOR_NOTE;
            long clnId = noteEntity.getClnId();
            CollectionEntity collectionEntity = collectionDAO.get(clnId);
            if (collectionEntity != null) {
                noteColor = collectionEntity.getClnColor();
            }
            long noteDate = noteEntity.getNoteDate();

            addToMap(noteDate, noteColor);
        }
    }

    private void addToMap(long lDate, String strColor){
        List<String> list = map.get(lDate);
        if (list == null) {
            list = new LinkedList<>();
            list.add(strColor);
            map.put(lDate, list);
        } else {
            list.add(strColor);
        }
    }

    private void initQueryTime(int mYear, int mMonth){
        // 指定日期的第一天的星期数 ( 0 == sunday )
        int weekOfFirstDay = TpTime.zellerWeek(mYear, mMonth, 1);
        // 获取指定日期的第一天的毫秒数
        Calendar t1 = Calendar.getInstance();
        t1.set(mYear, mMonth - 1, 1, 0, 0, 0);
        startQueryMillis = t1.getTimeInMillis() / 1000 * 1000 - weekOfFirstDay * TpTime.DAY_MILLIS;
        // 获取指定日期的最后一天的毫秒数 (获取的时间是最后1天的0:0:0的时间)
        endQueryMillis = startQueryMillis + 42 * TpTime.DAY_MILLIS;
    }
}
