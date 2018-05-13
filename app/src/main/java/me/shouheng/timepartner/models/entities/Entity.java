package me.shouheng.timepartner.models.entities;

import java.io.Serializable;

import me.shouheng.timepartner.models.Model;

/**
 * Created by wangshouheng on 2017/1/13. */
public class Entity extends Model implements Serializable{

    public static class Columns {
        public static final String ID = "id";
        public final static String ACCOUNT = "account";
        public final static String ADDED_DATE = "added_date";
        public final static String ADDED_TIME = "added_time";
        public final static String LAST_MODIFY_DATE = "last_modify_date";
        public final static String LAST_MODIFY_TIME = "last_modify_time";
        public static final String IN_TRASH = "in_trash";
        public final static String SYNCED = "synced";
    }

    protected long id;

    protected String account;

    protected long addedDate;

    protected int addedTime;

    protected long lastModifyDate;

    protected int lastModifyTime;

    protected int synced;

    protected int inTrash;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public int getSynced() {
        return synced;
    }

    public void setSynced(int synced) {
        this.synced = synced;
    }

    public long getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(long addedDate) {
        this.addedDate = addedDate;
    }

    public int getAddedTime() {
        return addedTime;
    }

    public void setAddedTime(int addedTime) {
        this.addedTime = addedTime;
    }

    public long getLastModifyDate() {
        return lastModifyDate;
    }

    public void setLastModifyDate(long lastModifyDate) {
        this.lastModifyDate = lastModifyDate;
    }

    public int getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(int lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public int getInTrash() {
        return inTrash;
    }

    public void setInTrash(int inTrash) {
        this.inTrash = inTrash;
    }
}
