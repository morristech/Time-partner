package me.shouheng.timepartner.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import me.shouheng.timepartner.models.entities.assignment.Assignment;
import me.shouheng.timepartner.models.entities.assignment.SubAssignment;
import me.shouheng.timepartner.models.entities.location.Location;
import me.shouheng.timepartner.models.entities.picture.Picture;
import me.shouheng.timepartner.models.entities.tpclass.ClassDetail;
import me.shouheng.timepartner.models.entities.tpclass.ClassEntity;
import me.shouheng.timepartner.models.entities.collection.CollectionEntity;
import me.shouheng.timepartner.models.entities.exam.Exam;
import me.shouheng.timepartner.models.entities.note.Note;
import me.shouheng.timepartner.models.entities.rating.Rating;
import me.shouheng.timepartner.models.entities.task.Task;
import me.shouheng.timepartner.models.entities.user.User;

public class DatabaseHelper extends SQLiteOpenHelper{

    public final static class Keys {
        /** 数据库版本 */
        public static final int VERSION = 1;
        /** 数据库名字 */
        public static final String NAME = "TimePartner.db";
    }

    private final class CreateTable {

        static final String USER = " CREATE TABLE " + User.TABLE_NAME +" ( " +
                User.Columns.UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                User.Columns.ACCOUNT + " TEXT, " +
                User.Columns.NAME + " TEXT, " +
                User.Columns.TOKEN + " INTEGER) ";

        static final String NOTE = " CREATE TABLE " + Note.TABLE_NAME + " ( " +
                Note.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Note.Columns.ACCOUNT + " TEXT, " +
                Note.Columns.CLN_ID + " INTEGER, " +
                Note.Columns.NOTE_ID + " INTEGER, " +
                Note.Columns.NOTE_TITLE + " TEXT, " +
                Note.Columns.NOTE_CONTENT + " TEXT, " +
                Note.Columns.RCD_PATH + " TEXT, " +
                Note.Columns.NOTICE_DATE + " INTEGER, " +
                Note.Columns.NOTICE_TIME + " INTEGER, " +
                Note.Columns.IN_TRASH + " INTEGER, " +
                Note.Columns.LIKED + " INTEGER, " +
                Note.Columns.ADDED_DATE + " INTEGER, " +
                Note.Columns.ADDED_TIME + " INTEGER, " +
                Note.Columns.LAST_MODIFY_DATE + " INTEGER, " +
                Note.Columns.LAST_MODIFY_TIME + " INTEGER, " +
                Note.Columns.SYNCED + " INTEGER) ";

        static final String COLLECTION = " CREATE TABLE " + CollectionEntity.TABLE_NAME + " ( " +
                CollectionEntity.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CollectionEntity.Columns.ACCOUNT + " TEXT, " +
                CollectionEntity.Columns.CLN_ID + " INTEGER, " +
                CollectionEntity.Columns.CLN_TITLE + " TEXT, " +
                CollectionEntity.Columns.CLN_COLOR + " TEXT," +
                CollectionEntity.Columns.ADDED_DATE + " INTEGER, " +
                CollectionEntity.Columns.ADDED_TIME + " INTEGER, " +
                CollectionEntity.Columns.LAST_MODIFY_DATE + " INTEGER, " +
                CollectionEntity.Columns.LAST_MODIFY_TIME + " INTEGER, " +
                CollectionEntity.Columns.SYNCED + " INTEGER) ";

        static final String TASK = " CREATE TABLE " + Task.TABLE_NAME + " ( " +
                Task.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Task.Columns.ACCOUNT + " TEXT," +
                Task.Columns.CLASS_ID + " INTEGER," +
                Task.Columns.TASK_ID + " INTEGER," +
                Task.Columns.TASK_TITLE + " TEXT," +
                Task.Columns.TASK_DATE + " INTEGER," +
                Task.Columns.TASK_TIME + " INTEGER," +
                Task.Columns.TASK_CONTENT + " TEXT," +
                Task.Columns.TASK_COMMENT + " TEXT," +
                Task.Columns.ADDED_DATE + " INTEGER, " +
                Task.Columns.ADDED_TIME + " INTEGER, " +
                Task.Columns.LAST_MODIFY_DATE + " INTEGER, " +
                Task.Columns.LAST_MODIFY_TIME + " INTEGER, " +
                Task.Columns.IN_TRASH + " INTEGER," +
                Task.Columns.SYNCED + " INTEGER)";

        static final String EXAM = " CREATE TABLE " + Exam.TABLE_NAME + " ( " +
                Exam.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Exam.Columns.ACCOUNT + " TEXT, " +
                Exam.Columns.CLASS_ID + " INTEGER, " +
                Exam.Columns.EXAM_ID + " INTEGER, " +
                Exam.Columns.EXAM_TITLE + " TEXT, " +
                Exam.Columns.EXAM_DATE + " INTEGER, " +
                Exam.Columns.EXAM_TIME + " INTEGER, " +
                Exam.Columns.EXAM_CONTENT + " TEXT, " +
                Exam.Columns.ROOM + " TEXT, " +
                Exam.Columns.SEAT + " TEXT, " +
                Exam.Columns.DURATION + " INTEGER, " +
                Exam.Columns.EXAM_COMMENT + " TEXT, " +

                Exam.Columns.ADDED_DATE + " INTEGER, " +
                Exam.Columns.ADDED_TIME + " INTEGER, " +
                Exam.Columns.LAST_MODIFY_DATE + " INTEGER, " +
                Exam.Columns.LAST_MODIFY_TIME + " INTEGER, " +

                Exam.Columns.IN_TRASH + " INTEGER, " +
                Exam.Columns.SYNCED + " INTEGER) ";

        static final String CLASS = " CREATE TABLE " + ClassEntity.TABLE_NAME + " ( " +
                ClassEntity.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ClassEntity.Columns.ACCOUNT + " TEXT, " +
                ClassEntity.Columns.CLS_ID + " INTEGER, " +
                ClassEntity.Columns.CLS_NAME + " TEXT, " +
                ClassEntity.Columns.CLS_START_DATE + " INTEGER, " +
                ClassEntity.Columns.CLS_END_DATE + " INTEGER, " +
                ClassEntity.Columns.CLS_COLOR + " TEXT, " +
                ClassEntity.Columns.IN_TRASH + " INTEGER, " +

                ClassEntity.Columns.ADDED_DATE + " INTEGER, " +
                ClassEntity.Columns.ADDED_TIME + " INTEGER, " +
                ClassEntity.Columns.LAST_MODIFY_DATE + " INTEGER, " +
                ClassEntity.Columns.LAST_MODIFY_TIME + " INTEGER, " +

                ClassEntity.Columns.SYNCED + " INTEGER) ";

        static final String CLASS_DETAIL = " CREATE TABLE " + ClassDetail.TABLE_NAME + " ( " +
                ClassDetail.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ClassDetail.Columns.ACCOUNT + " TEXT, " +
                ClassDetail.Columns.DETAIL_ID + " INTEGER, " +
                ClassDetail.Columns.CLASS_ID + " INTEGER, " +
                ClassDetail.Columns.START_TIME + " INTEGER, " +
                ClassDetail.Columns.END_TIME + " INTEGER, " +
                ClassDetail.Columns.ROOM + " TEXT, " +
                ClassDetail.Columns.TEACHER + " TEXT, " +
                ClassDetail.Columns.WEEK + " TEXT, " + //星期必须用字符串，因为比如0110000，用整数的话会被解析成11就错了

                ClassDetail.Columns.ADDED_DATE + " INTEGER, " +
                ClassDetail.Columns.ADDED_TIME + " INTEGER, " +
                ClassDetail.Columns.LAST_MODIFY_DATE + " INTEGER, " +
                ClassDetail.Columns.LAST_MODIFY_TIME + " INTEGER, " +

                ClassDetail.Columns.SYNCED + " INTEGER) ";

        static final String ASSIGN = " CREATE TABLE " + Assignment.TABLE_NAME + " ( " +
                Assignment.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Assignment.Columns.ACCOUNT + " TEXT, " +
                Assignment.Columns.ASSIGN_ID + " INTEGER, " +
                Assignment.Columns.ASSIGN_TITLE + " TEXT, " +
                Assignment.Columns.ASSIGN_CONTENT + " TEXT, " +
                Assignment.Columns.ASSIGN_DATE + " INTEGER, " +
                Assignment.Columns.ASSIGN_TIME + " INTEGER, " +
                Assignment.Columns.ASSIGN_PROGRESS + " INTEGER, " +
                Assignment.Columns.ASSIGN_COMMENT + " TEXT, " +
                Assignment.Columns.ASSIGN_COLOR + " TEXT, " +
                Assignment.Columns.IN_TRASH + " INTEGER, " +

                Assignment.Columns.ADDED_DATE + " INTEGER, " +
                Assignment.Columns.ADDED_TIME + " INTEGER, " +
                Assignment.Columns.LAST_MODIFY_DATE + " INTEGER, " +
                Assignment.Columns.LAST_MODIFY_TIME + " INTEGER, " +

                Assignment.Columns.SYNCED + " INTEGER) ";

        static final String SUB_ASSIGN = " CREATE TABLE " + SubAssignment.TABLE_NAME + " ( " +
                SubAssignment.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SubAssignment.Columns.ACCOUNT + " TEXT, " +
                SubAssignment.Columns.ASSIGN_ID + " INTEGER, " +
                SubAssignment.Columns.SUB_ID + " INTEGER, " +
                SubAssignment.Columns.SUB_CONTENT + " TEXT, " +
                SubAssignment.Columns.SUB_COMPLETED + " INTEGER, " +

                SubAssignment.Columns.ADDED_DATE + " INTEGER, " +
                SubAssignment.Columns.ADDED_TIME + " INTEGER, " +
                SubAssignment.Columns.LAST_MODIFY_DATE + " INTEGER, " +
                SubAssignment.Columns.LAST_MODIFY_TIME + " INTEGER, " +

                SubAssignment.Columns.SYNCED + " INTEGER) ";//如果默认为0就是未完成

        static final String DAILY_RATING = " CREATE TABLE " + Rating.TABLE_NAME + " ( " +
                Rating.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Rating.Columns.ACCOUNT + " TEXT, " +
                Rating.Columns.RATE_DATE + " INTEGER, " +
                Rating.Columns.RATING + " INTEGER, " +
                Rating.Columns.RATE_COMMENT + " TEXT, " +

                Rating.Columns.ADDED_DATE + " INTEGER, " +
                Rating.Columns.ADDED_TIME + " INTEGER, " +
                Rating.Columns.LAST_MODIFY_DATE + " INTEGER, " +
                Rating.Columns.LAST_MODIFY_TIME + " INTEGER, " +

                Rating.Columns.SYNCED + " INTEGER) ";

        static final String LOCATION = " CREATE TABLE " + Location.TABLE_NAME + " ( " +
                Location.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Location.Columns.ACCOUNT + " TEXT, " +
                Location.Columns.NOTE_ID + " INTEGER, " +
                Location.Columns.LOCATION_ID + " INTEGER, " +

                Location.Columns.ALTITUDE + " INTEGER, " +
                Location.Columns.LATITUDE + " INTEGER, " +
                Location.Columns.COUNTRY + " TEXT, " +
                Location.Columns.COUNTRYCODE + " TEXT, " +
                Location.Columns.PROVINCE + " TEXT, " +
                Location.Columns.CITY + " TEXT, " +
                Location.Columns.CITYCODE + " TEXT, " +
                Location.Columns.DISTRICT + " TEXT, " +
                Location.Columns.STREET + " TEXT, " +
                Location.Columns.STREETNUMBER + " TEXT, " +

                Location.Columns.ADDED_DATE + " INTEGER, " +
                Location.Columns.ADDED_TIME + " INTEGER, " +

                Location.Columns.SYNCED + " INTEGER) ";

        static final String PICTURE = " CREATE TABLE " + Picture.TABLE_NAME + " ( " +
                Picture.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Picture.Columns.ACCOUNT + " TEXT, " +
                Picture.Columns.NOTE_ID + " INTEGER, " +
                Picture.Columns.PICTURE_ID + " INTEGER," +

                Picture.Columns.LOCATION_ID + " INTEGER, " +
                Picture.Columns.PICTURE_PATH + " TEXT, " +
                Picture.Columns.COMMENT + " TEXT, " +

                Picture.Columns.ADDED_DATE + " INTEGER, " +
                Picture.Columns.ADDED_TIME + " INTEGER, " +
                Picture.Columns.LAST_MODIFY_DATE + " INTEGER, " +
                Picture.Columns.LAST_MODIFY_TIME + " INTEGER, " +

                Picture.Columns.SYNCED + " INTEGER) ";
    }

    private static DatabaseHelper databaseHelper;

    public static DatabaseHelper getInstance(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        if (databaseHelper == null) {
            return new DatabaseHelper(context, name, factory, version);
        } else {
            return databaseHelper;
        }
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CreateTable.USER);
        db.execSQL(CreateTable.COLLECTION);
        db.execSQL(CreateTable.NOTE);
        db.execSQL(CreateTable.TASK);
        db.execSQL(CreateTable.EXAM);
        db.execSQL(CreateTable.CLASS);
        db.execSQL(CreateTable.CLASS_DETAIL);
        db.execSQL(CreateTable.ASSIGN);
        db.execSQL(CreateTable.SUB_ASSIGN);
        db.execSQL(CreateTable.DAILY_RATING);
        db.execSQL(CreateTable.LOCATION);
        db.execSQL(CreateTable.PICTURE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion){
            case 1:
            default:
        }
    }
}
