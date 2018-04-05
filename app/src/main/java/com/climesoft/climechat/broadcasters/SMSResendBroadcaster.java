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

public class SMSResendBroadcaster extends BroadcastReceiver {
    public ConversationActivity activity;
    private DBActions dbActions;
    private DBInfo dbInfo;
    private static int count = 0;
    private static int pendingMembers = 0;

    public SMSResendBroadcaster(){

    }

    public SMSResendBroadcaster(ConversationActivity activity){
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
        if(pendingMembers == 0){
            pendingMembers = dbInfo.getPendingMembersCount(sid);
        }
        ContentValues values = new ContentValues();
        values.put("status", status);
        dbActions.updatePendingMessage(values, sid, mid);
        ++count;
        if(count == pendingMembers){
            ContentValues val = new ContentValues();
            if(0 < dbInfo.getPendingMembersCount(sid)){
                val.put("status", -1);
            }
            if(0 == dbInfo.getPendingMembersCount(sid)){
                val.put("status", 1);
            }
            dbActions.update(DBMeta.TABLE_MESSAGES, val, sid);
            if(this.activity != null){
                activity.updateMessages();
            }
            count = 0;
            pendingMembers = 0;
        }
    }
}