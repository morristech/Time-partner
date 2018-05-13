package me.shouheng.timepartner.database.dao.base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import me.shouheng.timepartner.database.DatabaseHelper;
import me.shouheng.timepartner.database.dao.SortType;
import me.shouheng.timepartner.managers.UserKeeper;

public abstract class BaseDAO<E>{

    SQLiteDatabase db;

    String strAccount;

    public BaseDAO(Context context){
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(context, DatabaseHelper.Keys.NAME, null, DatabaseHelper.Keys.VERSION);
        db = dbHelper.getWritableDatabase();
        strAccount = UserKeeper.getUser(context).getAccount();
    }

    public void close(){
        if (db != null){
            db.close();
        }
    }

    public E get(long id){return null;}

    /**
     * 通过外键进行检索
     * @param id 外键
     * @return 检索到的集合 */
    public List<E> gets(long id){return null;}

    public List<E> gets(long id, SortType sortType) {return null;}

    public List<E> get(long[] ids){return null;}

    public List<E> getOfDay(long millisOfDay){return null;}

    public List<E> getOfDay(long millisOfDay, SortType sortType) {return null;}

    public List<E> getScope(long startMillis, long endMillis){return null;}

    public List<E> getScope(long startMillis, long endMillis, SortType sortType) {return null;}

    public List<E> getAll(SortType sortType){return null;}

    public List<E> getAll(){return null;}

    public List<E> getOverdue(SortType sortType){return null;}

    public List<E> getOverdue(){return null;}

    public List<E> getQuery(String query) {return null;}

    public  List<E> getTrash(){return null;}

    public void insert(E e){}

    public void insert(List<E> list){}

    public void delete(E e){}

    public void delete(List<E> list){}

    public void trash(E e){}

    public void trash(List<E> list){}

    public void recover(E e){}

    public void recover(List<E> list){}

    public void update(E e){}

    public void update(List<E> list){}
}
