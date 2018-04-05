package com.climesoft.climechat.database;

/**
 * Created by Asif on 6/10/2017.
 */

public class DBMeta {
    public final static String TABLE_GROUPS = DatabaseHelper.TABLE_GROUPS;
    public final static String TABLE_MESSAGES = DatabaseHelper.TABLE_MESSAGES;
    public final static String TABLE_MEMBERS = DatabaseHelper.TABLE_MEMBERS;
    public final static String TABLE_NOTIFICATIONS = DatabaseHelper.TABLE_NOTIFICATIONS;
    public final static String TABLE_SMS_STATUS = DatabaseHelper.TABLE_SMS_STATUS;
    public final static String TABLE_MEMBER_STATUS = DatabaseHelper.TABLE_MEMBER_STATUS;
    public final static String TABLE_LEAVE_STATUS = DatabaseHelper.TABLE_LEAVE_STATUS;

    public final static String FETCH_GROUPS_DESC_ORDER_BY_DATE = "SELECT id _id, * from "+TABLE_GROUPS+" ORDER BY date DESC";
    public final static String FETCH_GROUPS_DESC_ORDER_BY_LAST_ACTIVITY = "SELECT id _id, * from "+TABLE_GROUPS+" ORDER BY last_activity DESC";
    public final static String FETCH_MESSAGES_OF_GROUP = "SELECT id _id, * from "+TABLE_MESSAGES+" where gid = ";
    public final static String FETCH_MEMBERS_OF_GROUP = "SELECT id _id, * from "+TABLE_MEMBERS+" where gid = ";
    public final static String FETCH_ACTIVE_MEMBERS_OF_GROUP = "SELECT id _id, * from "+TABLE_MEMBERS+" where active = 1 AND gid = ";
    public final static String FETCH_ACTIVE_MEMBERS_OF_GROUP_RECEIVER = "SELECT * from "+TABLE_MEMBERS+" where active = 1 AND gid = ";
    public final static String FETCH_NOTIFICATIONS_DESC_ORDER_BY_DATE = "SELECT id _id, * from "+TABLE_NOTIFICATIONS+" ORDER BY date DESC";
    public final static String FETCH_PENDING_SMS_MEMBERS = "SELECT id _id, * from "+TABLE_SMS_STATUS+" WHERE status = 0 AND sid = ";

}