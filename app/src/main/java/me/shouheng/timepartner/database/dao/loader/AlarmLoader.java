package me.shouheng.timepartner.database.dao.loader;

public class AlarmLoader {
//    private List alarms = Collections.emptyList();
//    private List<Alarm> list = new LinkedList<>();
//    private DatabaseManager dbManager;
//    private long millisOfDay;
//
//    public AlarmLoader(Context mContext){
//        dbManager = new DatabaseManager(mContext);
//    }
//
//    /**
//     * 获取装载完毕的集合
//     * @return 装载的集合 */
//    public List<Alarm> get(){
//        // 装载今天的全部闹钟信息
//        millisOfDay = TpTime.millisOfCurrentDate();
//        // 装载
//        load();
//        return list;
//    }
//
//    /**
//     * 释放资源
//     */
//    public void closeDatabase(){
//        if (dbManager != null){
//            dbManager.close();
//        }
//    }
//
//    /**
//     * 装载需要的闹钟实体 */
//    private void load(){
//        loadClasses();
//        loadExams();
//        loadTasks();
//        loadNotes();
//        loadAssigns();
//        loadSubAssigns();
//    }
//
//    /**
//     * 添加课程信息
//     */
//    private void loadClasses(){
//        // 课程
//        List<ClassEntity> list = dbManager.getClassesOfDay(millisOfDay);
//        if (list != null){
//            for (ClassEntity tpClass : list){
//                // 课程信息
//                long clsId = tpClass.getClsId();
//                List<ClassDetailEntity> classDetails = dbManager.getClassDetails(clsId);
//                // 包装并获取包装完毕的闹钟实体
//                List<Alarm> alarmEntities = wrapClassEntity(tpClass, classDetails);
//                for (Alarm alarmEntity : alarmEntities){
//                    this.list.add(alarmEntity);
//                }
//            }
//        }
//    }
//
//    /**
//     * 将课程实体包装成闹钟实体
//     * @param TPClass 课程实体
//     * @param detailEntities 课程信息实体
//     * @return 闹钟实体 */
//    public static List<Alarm> wrapClassEntity(
//            ClassEntity TPClass, List<ClassDetailEntity> detailEntities){
//        long clsStartDate = TPClass.getStartDate();
//        long clsEndDate = TPClass.getEndDate();
//        List<Alarm> list = new LinkedList<>();
//        // 判断课程的日期是否在今天的范围内 在上面的方法中是无需判断的，
//        // 这样做仅仅是为了提高代码的适用性
//        long millisOfToDay = TpTime.millisOfCurrentDate();
//        if (millisOfToDay > clsEndDate || millisOfToDay < clsStartDate){
//            return list;
//        }
//        if (detailEntities != null){
//            Calendar c = Calendar.getInstance();
//            c.setTimeInMillis(millisOfToDay);
//            int weekDay = TpTime.zellerWeek(
//                    c.get(Calendar.YEAR),
//                    c.get(Calendar.MONTH) + 1,
//                    c.get(Calendar.DAY_OF_MONTH));
//            for (ClassDetailEntity detailEntity : detailEntities){
//                String clsWeek = detailEntity.getWeek();
//                if (clsWeek.charAt(weekDay) == '1'){
//                    // 信息
//                    String strSubTitle = TpTime.getTime(detailEntity.getlStartTime())
//                            + "-" + TpTime.getTime(detailEntity.getlEndTime());
//                    Alarm alarmEntity = new Alarm(
//                            Alarm.TYPE_CLASS,
//                            TPClass.getClsName(),
//                            strSubTitle,
//                            detailEntity.getTeacher(),
//                            detailEntity.getRoom(),
//                            "",
//                            TPClass.getClsColor(),
//                            detailEntity.getClassId(),
//                            millisOfToDay,
//                            detailEntity.getlStartTime());
//                    list.add(alarmEntity);
//                }
//            }
//        }
//        return list;
//    }
//
//    /**
//     * 添加作业信息 */
//    private void loadTasks(){
//        List<TaskEntity> TPTaskEntities = dbManager.getTasksOfDay(millisOfDay);
//        if (TPTaskEntities != null){
//            for (TaskEntity TPTask : TPTaskEntities){
//                // 包装作业实体
//                long clsId = TPTask.getClsId();
//                ClassEntity TPClass = dbManager.getClass(clsId);
//                Alarm alarmEntity = wrapTaskEntity(TPTask, TPClass);
//                if (alarmEntity != null) {
//                    this.list.add(alarmEntity);
//                }
//            }
//        }
//    }
//
//    /**
//     * 包装作业实体成为 闹钟实体
//     * @param TPTask 作业实体
//     * @param TPClass 课程实体
//     * @return 闹钟实体 */
//    public static Alarm wrapTaskEntity(
//            TaskEntity TPTask, ClassEntity TPClass){
//        String clsColor = "#26A69A";
//        if (TPClass != null) {
//            clsColor = TPClass.getClsColor();
//        }
//        String clsName = "";
//        if (TPClass != null) {
//            clsName = TPClass.getClsName();
//        }
//        String strSubTitle = TpTime.getDate(TPTask.getTaskDate(), TpTime.DATE_TYPE_1)
//                + " " + TpTime.getTime(TPTask.getTaskTime());
//        return new Alarm(
//                Alarm.TYPE_TASK,
//                TPTask.getTaskTitle(),
//                strSubTitle,
//                clsName,
//                "",
//                TPTask.getTaskComments(),
//                clsColor,
//                TPTask.getTaskId(),
//                TPTask.getTaskDate(),
//                TPTask.getTaskTime());
//    }
//
//    /**
//     * 添加考试信息 */
//    private void loadExams(){
//        List<ExamEntity> TPExamEntities = dbManager.getExamsOfDay(millisOfDay);
//        if (TPExamEntities != null){
//            for (ExamEntity TPExam : TPExamEntities){
//                // 包装作业实体
//                long clsId = TPExam.getClsId();
//                ClassEntity TPClass = dbManager.getClass(clsId);
//                Alarm alarmEntity = wrapExamEntity(TPExam, TPClass);
//                if (alarmEntity != null) {
//                    this.list.add(alarmEntity);
//                }
//            }
//        }
//    }
//
//    /**
//     * 将考试的实体包装成为闹钟实体
//     * @param TPExam 考试实体
//     * @param TPClass 课程实体
//     * @return 闹钟实体 */
//    public static Alarm wrapExamEntity(
//            ExamEntity TPExam, ClassEntity TPClass){
//        String clsColor = "#26A69A";
//        if (TPClass != null) {
//            clsColor = TPClass.getClsColor();
//        }
//        String clsName = "";
//        if (TPClass != null) {
//            clsName = TPClass.getClsName();
//        }
//        return new Alarm(
//                Alarm.TYPE_EXAM,
//                TPExam.getExamTitle(),
//                TpTime.getDate(TPExam.getExamDate(), TpTime.DATE_TYPE_1)
//                        + " " + TpTime.getTime(TPExam.getExamTime()),
//                clsName,
//                TPExam.getDuration() + "'",
//                TPExam.getExamRoom() + " " + TPExam.getExamSeat(),
//                clsColor,
//                TPExam.getExamId(),
//                TPExam.getExamDate(),
//                TPExam.getExamTime());
//    }
//
//    /**
//     * 添加随手记信息 */
//    private void loadNotes(){
//        List<NoteEntity> TPNoteEntities = dbManager.getNotesOfDay(millisOfDay);
//        if (TPNoteEntities != null){
//            for (NoteEntity TPNote : TPNoteEntities){
//                long cln = TPNote.getClnId();
//                CollectionEntity TPCollection = dbManager.getCollection(cln);
//                Alarm alarmEntity = wrapNoteEntity(TPNote, TPCollection);
//                this.list.add(alarmEntity);
//            }
//        }
//    }
//
//    /**
//     * 包装随手记实体为闹钟实体
//     * @param TPNote 随手记实体
//     * @param TPCollection 集合实体
//     * @return 结果 */
//    public static Alarm wrapNoteEntity(
//            NoteEntity TPNote, CollectionEntity TPCollection){
//        return new Alarm(
//                Alarm.TYPE_NOTE,
//                TPNote.getNoteTitle(),
//                TpTime.getDate(TPNote.getNoteDate(), TpTime.DATE_TYPE_1)
//                        + " " + TpTime.getTime(TPNote.getNoteTime()),
//                TPCollection.getStrTitle(),
//                TPNote.getLocation(),
//                TPNote.getNoteContent(),
//                TPCollection.getStrColor(),
//                TPNote.getNoteId(),
//                TPNote.getNoteDate(),
//                TPNote.getNoteTime());
//    }
//
//    /**
//     * 添加日程到适配器中 */
//    private void loadAssigns(){
//        List<AssignEntity> assignEntities = dbManager.getAssignsOfDay(millisOfDay);
//        if (assignEntities != null){
//            for (AssignEntity TPAssign : assignEntities){
//                Alarm alarmEntity = wrapAssignEntity(TPAssign);
//                if (alarmEntity != null){
//                    this.list.add(alarmEntity);
//                }
//            }
//        }
//    }
//
//    /**
//     * 将日程实体包装成为闹钟实体
//     * @param TPAssign 日程实体
//     * @return 闹钟实体 */
//    public static Alarm wrapAssignEntity(
//            AssignEntity TPAssign){
//        if (TPAssign.getAsnProg() == 100){
//            return null;
//        }
//        String strProg = TPAssign.getAsnProg() + "%";
//        return new Alarm(
//                Alarm.TYPE_ASSIGN,
//                TPAssign.getAsnTitle(),
//                TpTime.getDate(TPAssign.getAsnDate(), TpTime.DATE_TYPE_1)
//                        + " " + TpTime.getTime(TPAssign.getAsnTime()),
//                strProg,
//                "",
//                TPAssign.getAsnComment(),
//                TPAssign.getAsnColor(),
//                TPAssign.getId(),
//                TPAssign.getAsnDate(),
//                TPAssign.getAsnTime());
//    }
//
//    /**
//     * 添加子日程的提醒 */
//    private void loadSubAssigns(){
//        List<AssignSubEntity> subAssignEntities = dbManager.getSubAssignsOfDay(millisOfDay);
//        if (subAssignEntities != null){
//            for (AssignSubEntity TPSubAssign : subAssignEntities){
//                // 包装子日程实体
//                long asnId = TPSubAssign.getAsnId();
//                AssignEntity TPAssign = dbManager.getAssign(asnId);
//                Alarm alarmEntity = wrapSubAssignEntity(TPSubAssign, TPAssign);
//                if (alarmEntity != null) {
//                    this.list.add(alarmEntity);
//                }
//            }
//        }
//    }
//
//    /**
//     * 将子日程包装成为闹钟实体
//     * @param TPSubAssign 子日程实体
//     * @param TPAssign 闹钟实体
//     * @return 闹钟实体 */
//    public static Alarm wrapSubAssignEntity(
//            AssignSubEntity TPSubAssign, AssignEntity TPAssign){
//        if (TPSubAssign.getIsCompleted()){
//            return null;
//        }
//        String asnColor = "#AA00FF";
//        String asnName = "";
//        if (TPAssign != null) {
//            asnColor = TPAssign.getAsnColor();
//            asnName = TPAssign.getAsnTitle();
//        }
//        return new Alarm(
//                Alarm.TYPE_ASSIGN,
//                TPSubAssign.getSubTitle(),
//                TpTime.getDate(TPSubAssign.getlSubDate(), TpTime.DATE_TYPE_1) + " "
//                        + TpTime.getTime(TPSubAssign.getiSubTime()),
//                asnName,
//                "",
//                TPSubAssign.getSubContent(),
//                asnColor,
//                TPSubAssign.getAsnId(),
//                TPSubAssign.getlSubDate(),
//                TPSubAssign.getiSubTime());
//    }
}
