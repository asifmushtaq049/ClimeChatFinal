package com.climesoft.climechat.broadcasters;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.widget.Button;

import com.climesoft.climechat.ConversationActivity;
import com.climesoft.climechat.NotificationActivity;
import com.climesoft.climechat.R;
import com.climesoft.climechat.common.CommonUtil;
import com.climesoft.climechat.database.DBActions;
import com.climesoft.climechat.database.DBMeta;

/**
 * Created by Asif on 6/15/2017.
 */

public class SMSGroupJoinBroadcaster extends BroadcastReceiver {
    public NotificationActivity activity = null;
    DBActions dbActions;
//    private Button acceptButton;
//    private Button rejectButton;

    public SMSGroupJoinBroadcaster(){

    }

    public SMSGroupJoinBroadcaster(NotificationActivity activity){
//        this.activity = activity;
//        dbActions = new DBActions(this.activity);
//        acceptButton = (Button)activity.findViewById(R.id.acceptButton);
//        rejectButton = (Button)activity.findViewById(R.id.rejectButton);
    }

    @Override
    public void onReceive(Context context, Intent arg1) {
        this.activity = NotificationActivity.getInstance();
        dbActions = new DBActions(context);
        switch (getResultCode()) {
            case Activity.RESULT_OK:
                updateResponse(arg1, 1);
                break;
            default:
                CommonUtil.showMessage(context, "Something went wrong! Try again");
                updateResponse(arg1, 0);
                break;
        }
    }
    private void updateResponse(Intent intent, int senderValue){
        long id = intent.getLongExtra("notificationId", 0L);
        if(id > 0){
            ContentValues values = new ContentValues();
            values.put("response", senderValue);
            dbActions.update(DBMeta.TABLE_NOTIFICATIONS, values, id);
            if(this.activity != null){
                activity.updateNotifications();
            }
        }
    }
}