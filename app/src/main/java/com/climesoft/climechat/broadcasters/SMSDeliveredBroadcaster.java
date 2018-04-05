package com.climesoft.climechat.broadcasters;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

import com.climesoft.climechat.ConversationActivity;
import com.climesoft.climechat.common.CommonUtil;
import com.climesoft.climechat.database.DBActions;
import com.climesoft.climechat.database.DBMeta;

/**
 * Created by Asif on 6/14/2017.
 */

public class SMSDeliveredBroadcaster extends BroadcastReceiver {
    public ConversationActivity activity;
    DBActions dbActions;


    public SMSDeliveredBroadcaster(ConversationActivity activity){
        this.activity = activity;
        dbActions = new DBActions(this.activity);
    }

    @Override
    public void onReceive(Context context, Intent arg1) {
        switch (getResultCode()) {
            case Activity.RESULT_OK:
                updateSender(arg1, 2);
                break;
            case Activity.RESULT_CANCELED:
                break;
        }

    }
    private void updateSender(Intent intent, int senderValue){
        long id = intent.getLongExtra("smsid", 0L);
        if(id > 0){
            ContentValues values = new ContentValues();
            values.put("sender", senderValue);
            dbActions.update(DBMeta.TABLE_MESSAGES, values, id);
            activity.updateMessages();
        }
    }
}