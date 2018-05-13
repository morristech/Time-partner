package me.shouheng.timepartner.database.dao;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.shouheng.timepartner.models.business.tpclass.ClassDetailBO;
import me.shouheng.timepartner.models.entities.tpclass.ClassDetail;

public class DAOHelper {

    public static void sortClass(List<ClassDetailBO> classDetailBOs){
        Collections.sort(classDetailBOs, new Comparator<ClassDetailBO>() {
            @Override
            public int compare(ClassDetailBO lhs, ClassDetailBO rhs) {
                ClassDetail lde = lhs.getDetailEntity();
                ClassDetail rde = rhs.getDetailEntity();
                if (lde.getStartTime() > rde.getStartTime()){
                    return 1;
                } else if (lde.getStartTime() < lde.getStartTime()){
                    return -1;
                } else {
                    if (lde.getEndTime() > lde.getEndTime()){
                        return 1;
                    } else if (lde.getEndTime() < lde.getEndTime()){
                        return -1;
                    }
                    return 0;
                }
            }
        });
    }
}
