package com.climesoft.climechat.broadcasters;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

import com.climesoft.climechat.ConversationActivity;
import com.climesoft.climechat.database.DBActions;
import com.climesoft.climechat.database.DBInfo;
import com.climesoft.climechat.database.DBMeta;

public class SMSSentBroadcaster extends BroadcastReceiver {
    public ConversationActivity activity;
    private DBActions dbActions;
    private DBInfo dbInfo;
    private static int count = 0;

    public SMSSentBroadcaster(){

    }

    public SMSSentBroadcaster(ConversationActivity activity){
//        this.activity = activity;
//        dbActions = new DBActions(this.activity);
//        dbInfo = new DBInfo(this.activity);
    }

    @Override
    public void onReceive(Context context, Intent arg1) {
        this.activity = ConversationActivity.getInstance();
        dbActions = new DBActions(context);
        dbInfo = new DBInfo(context);
        switch (getResultCode()) {
            case Activity.RESULT_OK:
                updateSender(arg1, 1);
                break;
            default:
                updateSender(arg1, 0);
                break;
        }
    }
    private void updateSender(Intent intent, int status){
        long sid = intent.getLongExtra("sid", 0L);
        long mid = intent.getLongExtra("mid", 0L);
        long groupId = intent.getLongExtra("groupId", 0L);
        ContentValues values = new ContentValues();
        values.put("sid", sid);
        values.put("mid", mid);
        values.put("status", status);
        dbActions.insert(values, DBMeta.TABLE_SMS_STATUS);
        ++count;
        if(count == dbInfo.countGroupMembers(groupId)){
            ContentValues val = new ContentValues(); // empty values going because cndition no working.
            if(dbInfo.countSentSmsStatus(sid) < dbInfo.countGroupMembers(groupId)){
                val.put("status", -1);
            }
            if(dbInfo.countSentSmsStatus(sid) >= dbInfo.countGroupMembers(groupId)){
                val.put("status", 1);
            }
            dbActions.update(DBMeta.TABLE_MESSAGES, val, sid);
            if(this.activity != null){
                activity.updateMessages();
            }
            count = 0;
        }
    }
}