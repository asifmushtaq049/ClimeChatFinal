package com.climesoft.climechat.database;

/**
 * Created by Asif on 6/6/2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.ContactsContract.PhoneLookup;
import com.climesoft.climechat.common.CommonUtil;

public class DBActions {

    private static DatabaseHelper handler;
    private static SQLiteDatabase dbReader;
    private static SQLiteDatabase dbWriter;
    private Context context;

    public DBActions(Context context){
        this.context = context;
        handler = DatabaseHelper.getInstance(context);
    }

    public long insert(ContentValues values, String tableName){
        dbWriter = handler.getWritableDatabase();
        return dbWriter.insert(tableName, null, values);
    }
    public long update(String table, ContentValues values, long rowId){
        dbWriter = handler.getWritableDatabase();
        return dbWriter.update(table, values, "id = ?", new String[] { String.valueOf(rowId)});
    }
    public long updatePendingMessage(ContentValues values, long sId, long mId){
        dbWriter = handler.getWritableDatabase();
        return dbWriter.update(DBMeta.TABLE_SMS_STATUS, values, "sid = ? and mid = ?", new String[] { String.valueOf(sId), String.valueOf(mId)});
    }
    public long updateMessage(ContentValues values, long groupId){
        dbWriter = handler.getWritableDatabase();
        return dbWriter.update(DBMeta.TABLE_MESSAGES, values, "gid = ?", new String[] { String.valueOf(groupId)});
    }
    public long updateMember(ContentValues values, String phoneNumber, long groupId){
        String phone = getPhoneFromContact(phoneNumber).replace(" ", "");
        dbWriter = handler.getWritableDatabase();
        return dbWriter.update(DBMeta.TABLE_MEMBERS, values, "phone = ? AND gid = ?", new String[] { String.valueOf(phone), String.valueOf(groupId) });
    }
    public long updateMemberDirect(ContentValues values, String phone, long groupId){
        dbWriter = handler.getWritableDatabase();
        return dbWriter.update(DBMeta.TABLE_MEMBERS, values, "phone = ? AND gid = ?", new String[] { String.valueOf(phone), String.valueOf(groupId) });
    }
    public int delete(long id, String tableName){
        dbWriter = handler.getWritableDatabase();
        return dbWriter.delete(tableName, "id = ?", new String[] { String.valueOf(id) });
    }
    public int deleteMessages(long groupId){
        dbWriter = handler.getWritableDatabase();
        return dbWriter.delete(DBMeta.TABLE_MESSAGES, "gid = ?", new String[] { String.valueOf(groupId) });
    }
    public int deleteMembers(long groupId){
        dbWriter = handler.getWritableDatabase();
        return dbWriter.delete(DBMeta.TABLE_MEMBERS, "gid = ?", new String[] { String.valueOf(groupId) });
    }

    public int deleteMember(long gid, String phone){
        dbWriter = handler.getWritableDatabase();
        return dbWriter.delete(DBMeta.TABLE_MEMBERS, "phone = ? AND gid = ?", new String[] { String.valueOf(phone), String.valueOf(gid) });
    }

    public void deleteMe(long groupId, String phoneNumber){
        Cursor members = getCursor("SELECT * from "+ DBMeta.TABLE_MEMBERS+" where active = 1 AND gid = " + groupId);
        if(members.moveToFirst()){
            do {
                String phone = members.getString(1);
                if(CommonUtil.compareNumbers(phone, phoneNumber)){
                    deleteMember(groupId, phone);
                    return;
                }
            }while (members.moveToNext());
        }
    }
    public int removeMember(long groupId, String phoneNumber){
        Cursor members = getCursor("SELECT * from "+ DBMeta.TABLE_MEMBERS+" where gid = " + String.valueOf(groupId));
        if(members.moveToFirst()){
            do {
                String phone = members.getString(1);
                if(CommonUtil.compareNumbers(phone, phoneNumber)){
                    return deleteMember(groupId, phone);
                }
            }while (members.moveToNext());
        }
        return 0;
    }

    public Cursor rawQuery(String query, String[] args){
        dbReader = handler.getReadableDatabase();
        Cursor c = dbReader.rawQuery(query, args);
        return c;
    }
    public Cursor getCursor(String sql){
        dbWriter = handler.getWritableDatabase();
        return dbWriter.rawQuery(sql, null);
    }

    public void close(){
        handler.close();
    }

    public String getPhoneFromContact(String phoneNumber){
        Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor c = context.getContentResolver().query(uri, new String[]{PhoneLookup.NUMBER}, null, null, null);
        String phone = "";
        if(c.getCount()>0){
            c.moveToFirst();
            phone = c.getString(c.getColumnIndex(PhoneLookup.NUMBER));
        }else{
            phone = phoneNumber;
        }
        c.close();
        return phone;
    }
}