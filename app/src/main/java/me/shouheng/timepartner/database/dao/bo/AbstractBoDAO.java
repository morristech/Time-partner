package me.shouheng.timepartner.database.dao.bo;

import java.util.List;

import me.shouheng.timepartner.database.dao.SortType;

/**
 * Created by wangshouheng on 2017/1/14. */
public abstract class AbstractBoDAO<T>{

    public void close(){}

    public void trash(T t){}

    public void recover(T t){}

    public void insert(T t){}

    public void delete(T t){}

    public void update(T t){}

    public T get(long id){return null;}

    public List<T> getAll(){return null;}

    public List<T> getAll(SortType sortType){return null;}

    public List<T> getOverdue(){return null;}

    public List<T> getOverdue(SortType sortType){return null;}

    public List<T> getOfDay(long millisOfDay){return null;}

    public List<T> getOfDay(long millisOfDay, SortType sortType){return null;}

    public List<T> getScope(long startMillis, long endMillis){return null;}

    public List<T> getScope(long startMillis, long endMillis, SortType sortType){return null;}

    public List<T> getTrash(){return null;}

    public List<T> getQuery(String query){return null;}
}
