package com.climesoft.climechat.database;

import android.content.Context;
import android.database.Cursor;
import android.telephony.PhoneNumberUtils;
import android.util.Log;

import com.climesoft.climechat.common.CommonUtil;


public class DBInfo {
    private DBActions dbActions;
    private Context context;

    public DBInfo(Context context){
        this.context = context;
        dbActions = new DBActions(this.context);
    }

    public String getGroupNameById(long id){
        String groupName = null;
        String query = "SELECT name from "+ DBMeta.TABLE_GROUPS+" where id = ?";
        Cursor c = dbActions.rawQuery(query, new String[] { String.valueOf(id) });
        if (c.moveToFirst()) {
            groupName = c.getString(0);
        }
        return groupName;
    }

    public Cursor getActiveMembers(long groupId){
        return dbActions.getCursor(DBMeta.FETCH_ACTIVE_MEMBERS_OF_GROUP + groupId);
    }

    public Cursor getGroupMembers(long groupId){
        return dbActions.getCursor(DBMeta.FETCH_MEMBERS_OF_GROUP + groupId);
    }

    public Cursor getPendingMembers(long smsId){
        return dbActions.getCursor(DBMeta.FETCH_PENDING_SMS_MEMBERS + smsId);
    }

    public int getPendingMembersCount(long smsId){
        Cursor members = dbActions.getCursor(DBMeta.FETCH_PENDING_SMS_MEMBERS + smsId);
        return members.getCount();
    }

    public boolean haveMembers(long groupId){
        Cursor members = getActiveMembers(groupId);
        return members.moveToFirst();
    }
    public int getMemberStatusByPosition(int position, long groupId){
        Cursor cursor = dbActions.getCursor(DBMeta.FETCH_MEMBERS_OF_GROUP + groupId);
        if(cursor.moveToPosition(position)){
            return cursor.getInt(cursor.getColumnIndexOrThrow("active"));
        }
        return 0;
    }
    public int getMessageSenderByPosition(int position, long groupId){
        Cursor cursor = dbActions.getCursor(DBMeta.FETCH_MESSAGES_OF_GROUP + groupId);
        if(cursor.moveToPosition(position)){
            return cursor.getInt(cursor.getColumnIndexOrThrow("sender"));
        }
        return 0;
    }
    public String getPhoneByPosition(int position, long groupId){
        Cursor cursor = dbActions.getCursor(DBMeta.FETCH_MEMBERS_OF_GROUP + groupId);
        if (cursor.moveToPosition(position)) {
            return cursor.getString(cursor.getColumnIndexOrThrow("phone"));
        }
        return "";
    }

    public String getGroupIdByNotificationId(long id){
        String groupId = null;
        String query = "SELECT gid from notification where id = ?";
        Cursor c = dbActions.rawQuery(query, new String[] { String.valueOf(id) });
        if (c.moveToFirst()) {
            groupId = c.getString(0);
        }
        return groupId;
    }

    public String getPhoneByNotificationId(long id){
        String phone = null;
        String query = "SELECT phone from notification where id = ?";
        Cursor c = dbActions.rawQuery(query, new String[] { String.valueOf(id) });
        if (c.moveToFirst()) {
            phone = c.getString(0);
        }
        return phone;
    }

    public String getPhoneByMemberId(long id){
        String phone = null;
        String query = "SELECT phone from "+ DBMeta.TABLE_MEMBERS+" where id = ?";
        Cursor c = dbActions.rawQuery(query, new String[] { String.valueOf(id) });
        if (c.moveToFirst()) {
            phone = c.getString(0);
        }
        return phone;
    }

    public boolean isGroupAdmin(long id){
        String admin = "";
        String query = "SELECT admin from groups where id = ?";
        Cursor c = dbActions.rawQuery(query, new String[] { String.valueOf(id) });
        if (c.moveToFirst()) {
            admin = c.getString(0);
        }
        return admin.equals("1");
    }

    public boolean isGroupAdmin(String phoneNumber, String gid){
        String admin = "";
        String query = "SELECT admin from groups where id = ?";
        Cursor c = dbActions.rawQuery(query, new String[] { String.valueOf(gid) });
        if (c.moveToFirst()) {
            admin = c.getString(0);
        }
        return PhoneNumberUtils.compare(phoneNumber, admin);
    }

    public boolean isMember(String phone, long id){
        String query = "SELECT * from members where active = 1 AND gid = ?";
        Cursor c = dbActions.rawQuery(query, new String[]{String.valueOf(id)});
        while(c.moveToNext()){
            if(PhoneNumberUtils.compare(phone, c.getString(1))){
                return true;
            }
        }
        return false;
    }

    public boolean isAlreadyMember(String member, long groupId){
        String query = "SELECT * from members where gid = ?";
        Cursor members = dbActions.rawQuery(query, new String[]{String.valueOf(groupId)});
        if(members.moveToFirst()){
            do {
                String phone = members.getString(1);
                if(CommonUtil.compareNumbers(phone, member)){
                    return true;
                }
            }while (members.moveToNext());
        }
        return false;
    }

//    public long getMemberIdByPhone(String member, long groupId){
//        String query = "SELECT * from members where gid = ?";
//        Cursor members = dbActions.rawQuery(query, new String[]{String.valueOf(groupId)});
//        if(members.moveToFirst()){
//            do {
//                String phone = members.getString(1);
//                if(CommonUtil.compareNumbers(phone, member)){
//                    return true;
//                }
//            }while (members.moveToNext());
//        }
//        return false;
//    }

    public boolean isGroupAvailable(long gid){
        String query = "SELECT * from groups where id = ?";
        Cursor groups = dbActions.rawQuery(query, new String[]{String.valueOf(gid)});
        return groups.moveToFirst();
    }

    public int countGroupMembers(long gid){
        String query = "SELECT count(*) from members where gid = ?";
        Cursor groups = dbActions.rawQuery(query, new String[]{String.valueOf(gid)});
        groups.moveToFirst();
        return groups.getInt(0);
    }

    public int countSentSmsStatus(long sid){
        String query = "SELECT count(*) from sms_status where sid = ? and status = 1";
        Cursor groups = dbActions.rawQuery(query, new String[]{String.valueOf(sid)});
        groups.moveToFirst();
        return groups.getInt(0);
    }

    public int countLeaveStatus(long sid){
        String query = "SELECT count(*) from leave_status where gid = ? and status = 1";
        Cursor groups = dbActions.rawQuery(query, new String[]{String.valueOf(sid)});
        groups.moveToFirst();
        return groups.getInt(0);
    }

    public int countRemovedMember(String phoneNumber, long groupId){
        String query = "SELECT count(*) from member_status where phone = ? and gid = ? and status = 1";
        Cursor groups = dbActions.rawQuery(query, new String[]{String.valueOf(phoneNumber), String.valueOf(groupId)});
        groups.moveToFirst();
        return groups.getInt(0);
    }

    public int countUnSeenSms(long groupId){
        String query = "SELECT count(*) from messages where seen = 0 and gid = ?";
        Cursor groups = dbActions.rawQuery(query, new String[]{String.valueOf(groupId)});
        groups.moveToFirst();
        return groups.getInt(0);
    }

    public boolean isSmsReadyToSent(long sid){
        String query = "SELECT count(*) from sms_status where sid = ?";
        Cursor groups = dbActions.rawQuery(query, new String[]{String.valueOf(sid)});
        groups.moveToFirst();
        return groups.getInt(0) > 0;
    }

    public long getDate(long id, String table){
        String query = "SELECT * from "+ table +" where id = ?";
        Cursor groups = dbActions.rawQuery(query, new String[]{String.valueOf(id)});
        if(groups.moveToFirst()){
            return groups.getLong(groups.getColumnIndex("date"));
        }else{
            return 0L;
        }
    }

    public long getGroupLastActivity(long id){
        String query = "SELECT * from groups where id = ?";
        Cursor groups = dbActions.rawQuery(query, new String[]{String.valueOf(id)});
        if(groups.moveToFirst()){
            return groups.getLong(groups.getColumnIndex("last_activity"));
        }else{
            return 0L;
        }
    }
}