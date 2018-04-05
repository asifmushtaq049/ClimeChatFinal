package com.climesoft.climechat.database;

/**
 * Created by Asif on 6/6/2017.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper dbInstance;
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "climechat";

    // GROUPS_TABLE
    public static final String TABLE_GROUPS = "groups";
    private static final String GROUP_ID = "id";
    private static final String GROUP_NAME = "name";
    private static final String GROUP_ADMIN = "admin";
    private static final String GROUP_LAST_ACTIVITY = "last_activity";
    private static final String GROUP_DATE = "date";

    // MESSAGES_TABLE
    public static final String TABLE_MESSAGES = "messages";
    private static final String MESSAGES_ID = "id";
    private static final String MESSAGES_BODY = "body";
    private static final String MESSAGES_SENDER = "sender";
    private static final String MESSAGES_DATE = "date";
    private static final String MESSAGES_SEEN = "seen";
    private static final String MESSAGES_STATUS = "status";
    private static final String MESSAGES_GROUP_ID = "gid";

    // MEMBERS TABLE
    public static final String TABLE_MEMBERS = "members";
    private static final String MEMBERS_ID = "id";
    private static final String MEMBERS_PHONE = "phone";
    private static final String MEMBERS_ACTIVE = "active";
    private static final String MEMBERS_CONTACT_ID = "cid";
    private static final String MEMBERS_JOIN_DATE = "join_date";
    private static final String MEMBERS_STATUS = "status";
    private static final String MEMBERS_GROUP_ID = "gid";

    // NOTIFICATION TABLE
    public static final String TABLE_NOTIFICATIONS = "notifications";
    private static final String NOTIFICATION_ID = "id";
    private static final String NOTIFICATION_BODY = "body";
    private static final String NOTIFICATION_PHONE = "phone";
    private static final String NOTIFICATION_DATE = "date";
    private static final String NOTIFICATION_RESPONSE = "response";
    private static final String NOTIFICATION_GROUP_ID = "gid";

    // SMS STATUS TABLE
    public static final String TABLE_SMS_STATUS = "sms_status";
    private static final String SMS_STATUS_ID = "id";
    private static final String SMS_STATUS_SMS_ID = "sid";
    private static final String SMS_STATUS_MEMBER_ID = "mid";
    private static final String SMS_STATUS_STATUS = "status";

    // MEMBER STATUS TABLE
    public static final String TABLE_MEMBER_STATUS = "member_status";
    private static final String MEMBER_STATUS_ID = "id";
    private static final String MEMBER_STATUS_PHONE = "phone";
    private static final String MEMBER_STATUS_MEMBER_ID = "mid";
    private static final String MEMBER_STATUS_GROUP_ID = "gid";
    private static final String MEMBER_STATUS_STATUS = "status";

    public static final String TABLE_LEAVE_STATUS = "leave_status";
    private static final String LEAVE_STATUS_ID = "id";
    private static final String LEAVE_STATUS_GROUP_ID = "gid";
    private static final String LEAVE_STATUS_MEMBER_ID = "mid";
    private static final String LEAVE_STATUS_STATUS = "status";

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DatabaseHelper getInstance(Context context) {
        if (dbInstance == null) {
            dbInstance = new DatabaseHelper(context);
        }
        return dbInstance;
    }

    // Create Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_GROUPS_TABLE = "CREATE TABLE " + TABLE_GROUPS + "("
                + GROUP_ID + " INTEGER PRIMARY KEY NOT NULL,"
                + GROUP_NAME + " VARCHAR NOT NULL,"
                + GROUP_ADMIN + " VARCHAR NOT NULL, "
                + GROUP_LAST_ACTIVITY + " DATETIME NOT NULL DEFAULT 0, "
                + GROUP_DATE + " DATETIME NOT NULL DEFAULT 0"
                + ")";
        String CREATE_MESSAGES_TABLE = "CREATE TABLE " + TABLE_MESSAGES + "("
                + MESSAGES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + MESSAGES_BODY + " VARCHAR NOT NULL,"
                + MESSAGES_SENDER + " VARCHAR NOT NULL, "
                + MESSAGES_DATE + " DATETIME NOT NULL,"
                + MESSAGES_SEEN + " INTEGER DEFAULT 0,"
                + MESSAGES_STATUS + " INTEGER DEFAULT 0,"
                + MESSAGES_GROUP_ID + " INTEGER NOT NULL,"
                + "FOREIGN KEY(gid) REFERENCES groups(id) ON DELETE CASCADE"
                + ")";

        String CREATE_MEMBERS_TABLE = "CREATE TABLE " + TABLE_MEMBERS + "("
                + MEMBERS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + MEMBERS_PHONE + " VARCHAR NOT NULL,"
                + MEMBERS_ACTIVE + " INTEGER NOT NULL DEFAULT 0, "
                + MEMBERS_CONTACT_ID + " INTEGER NOT NULL DEFAULT 0, "
                + MEMBERS_JOIN_DATE + " DATETIME NOT NULL, "
                + MEMBERS_STATUS + " INTEGER NOT NULL DEFAULT 0, "
                + MEMBERS_GROUP_ID + " INTEGER NOT NULL, "
                + "FOREIGN KEY(gid) REFERENCES groups(id) ON DELETE CASCADE"
                + ")";

        String CREATE_NOTIFICATION_TABLE = "CREATE TABLE " + TABLE_NOTIFICATIONS + "("
                + NOTIFICATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + NOTIFICATION_PHONE + " VARCHAR NOT NULL,"
                + NOTIFICATION_BODY + " VARCHAR NOT NULL,"
                + NOTIFICATION_DATE + " DATETIME NOT NULL,"
                + NOTIFICATION_RESPONSE + " INTEGER DEFAULT 0,"
                + NOTIFICATION_GROUP_ID + " INTEGER NOT NULL"
                + ")";

        String CREATE_SMS_STATUS_TABLE = "CREATE TABLE " + TABLE_SMS_STATUS + "("
                + SMS_STATUS_ID + " INTEGER PRIMARY KEY NOT NULL,"
                + SMS_STATUS_SMS_ID + " INTEGER NOT NULL,"
                + SMS_STATUS_MEMBER_ID + " INTEGER NOT NULL, "
                + SMS_STATUS_STATUS + " INTEGER NOT NULL, "
                + "FOREIGN KEY(sid) REFERENCES messages(id) ON DELETE CASCADE,"
                + "FOREIGN KEY(mid) REFERENCES members(id) ON DELETE CASCADE"
                + ")";

        String CREATE_MEMBER_STATUS_TABLE = "CREATE TABLE " + TABLE_MEMBER_STATUS + "("
                + MEMBER_STATUS_ID + " INTEGER PRIMARY KEY NOT NULL,"
                + MEMBER_STATUS_PHONE + " INTEGER NOT NULL,"
                + MEMBER_STATUS_MEMBER_ID + " INTEGER NOT NULL, "
                + MEMBER_STATUS_STATUS + " INTEGER NOT NULL DEFAULT 0, "
                + MEMBER_STATUS_GROUP_ID + " INTEGER NOT NULL, "
                + "FOREIGN KEY(gid) REFERENCES groups(id) ON DELETE CASCADE,"
                + "FOREIGN KEY(mid) REFERENCES members(id) ON DELETE CASCADE"
                + ")";

        String CREATE_LEAVE_STATUS_TABLE = "CREATE TABLE " + TABLE_LEAVE_STATUS + "("
                + LEAVE_STATUS_ID + " INTEGER PRIMARY KEY NOT NULL,"
                + LEAVE_STATUS_GROUP_ID + " INTEGER NOT NULL,"
                + LEAVE_STATUS_MEMBER_ID + " INTEGER NOT NULL, "
                + LEAVE_STATUS_STATUS + " INTEGER NOT NULL, "
                + "FOREIGN KEY(gid) REFERENCES groups(id) ON DELETE CASCADE,"
                + "FOREIGN KEY(mid) REFERENCES members(id) ON DELETE CASCADE"
                + ")";

        db.execSQL(CREATE_GROUPS_TABLE);
        db.execSQL(CREATE_MESSAGES_TABLE);
        db.execSQL(CREATE_MEMBERS_TABLE);
        db.execSQL(CREATE_NOTIFICATION_TABLE);
        db.execSQL(CREATE_SMS_STATUS_TABLE);
        db.execSQL(CREATE_MEMBER_STATUS_TABLE);
        db.execSQL(CREATE_LEAVE_STATUS_TABLE);
    }

    // Upgrade Database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEMBERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SMS_STATUS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEMBER_STATUS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LEAVE_STATUS);
        onCreate(db);
    }

    // Downgrading Database
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEMBERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SMS_STATUS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEMBER_STATUS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LEAVE_STATUS);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        String query = "PRAGMA foreign_keys = ON;";
        db.execSQL(query);
    }
}