package me.shouheng.timepartner.managers;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * 活动管理类
 * 如果程序从某个活动处意外崩溃的话，那么这里的存储的值会被清除，
 * 就无法达到理想的效果，但是活动的栈中还是会原来活动的
 * 意外崩溃会让程序返回到上一个栈 */
public class ActivityManager {
    //普通类的集合
    private static List<Activity> activities = new ArrayList<>();
    //与“设置”相关的类的集合
    private static List<Activity> sActivities = new ArrayList<>();
    //与“随手记”相关的类的集合
    private static List<Activity> noteActivities = new ArrayList<>();
    //与“课程”相关的活动的集合
    private static List<Activity> classActivities = new ArrayList<>();
    //与“课程”相关的活动的集合
    private static List<Activity> assignActivities = new ArrayList<>();
    //与“作业考试”相关的活动的集合
    private static List<Activity> taskActivities = new ArrayList<>();


    /**
     * 向活动管理集合中添加一个活动
     * @param activity 要添加的活动 */
    public static void addActivity(Activity activity){
        activities.add(activity);
    }

    /**
     * 结束活动管理集合内的所有活动 */
    public static void finishAll(){
        for(Activity activity:activities){
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
    }

    /**
     * 从活动管理集合中移除指定的活动
     * @param activity 要移除的活动 */
    public static void removeActivity(Activity activity){
        if (activities.contains(activity)){
            activities.remove(activity);
        }
    }

    /**
     * 向设置活动的集合中添加一个活动
     * @param activity 要添加的活动 */
    public static void addSettingActivity(Activity activity){
        sActivities.add(activity);
    }

    /**
     * 结束所有的设置类活动 */
    public static void finishAllSetting(){
        for(Activity activity:sActivities){
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
    }

    /**
     * 向设置活动的集合中添加一个活动
     * @param activity 要添加的活动 */
    public static void addNoteActivity(Activity activity){
        noteActivities.add(activity);
    }

    /**
     * 结束所有的设置类活动 */
    public static void finishAllNotes(){
        for(Activity activity:noteActivities){
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
    }

    /**
     * 从随手记的活动中移除指定的活动
     * @param activity 要移除的活动 */
    public static void removeActivityFromNotes(Activity activity){
        if (noteActivities.contains(activity)){
            noteActivities.remove(activity);
        }
    }

    /**
     * 添加课程活动
     * @param activity 要添加的活动*/
    public static void addClassActivity(Activity activity){
        classActivities.add(activity);
    }

    /**
     * 关闭所有的课程活动 */
    public static void finishAllClasses(){
        for(Activity activity : classActivities){
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
    }

    /**
     * 从课程的活动中移除指定的活动
     * @param activity 要移除的活动 */
    public static void removeActivityFromClasses(Activity activity){
        if (classActivities.contains(activity)){
            classActivities.remove(activity);
        }
    }

    /**
     * 添加任务活动
     * @param activity 要添加的活动*/
    public static void addAssignActivity(Activity activity){
        assignActivities.add(activity);
    }

    /**
     * 关闭所有的任务活动 */
    public static void finishAllAssigns(){
        for(Activity activity : assignActivities){
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
    }

    /**
     * 从任务的活动中移除指定的活动
     * @param activity 要移除的活动 */
    public static void removeActivityFromAssigns(Activity activity){
        if (assignActivities.contains(activity)){
            assignActivities.remove(activity);
        }
    }

    /**
     * 添加任务活动
     * @param activity 要添加的活动*/
    public static void addTaskActivity(Activity activity){
        taskActivities.add(activity);
    }

    /**
     * 关闭所有的任务活动 */
    public static void finishAllTasks(){
        for(Activity activity : taskActivities){
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
    }

    /**
     * 从任务的活动中移除指定的活动
     * @param activity 要移除的活动 */
    public static void removeActivityFromTasks(Activity activity){
        if (taskActivities.contains(activity)){
            taskActivities.remove(activity);
        }
    }
}
