package me.shouheng.timepartner.models.entities.user;

public class User {

    public final static String TABLE_NAME = "user_table";

    public static final class Columns {
        public final static String UID = "uid";
        public final static String NAME = "name";
        public final static String ACCOUNT = "account";
        public final static String TOKEN = "token";
    }

    private long uid;

    private String name;

    private String account;

    private long token;

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public long getToken() {
        return token;
    }

    public void setToken(long token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return Columns.UID + ":" + uid + " , " +
                Columns.NAME + ":" + name + " , " +
                Columns.ACCOUNT + ":" + account + " , " +
                Columns.TOKEN + ":" + token;
    }

}
