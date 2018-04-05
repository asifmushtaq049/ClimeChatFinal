package com.climesoft.climechat.broadcasters;

/**
 * Created by Asif on 7/14/2017.
 */

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ProgressBar;

import com.climesoft.climechat.ConversationActivity;
import com.climesoft.climechat.R;
import com.climesoft.climechat.common.CommonUtil;
import com.climesoft.climechat.database.DBActions;
import com.climesoft.climechat.database.DBInfo;
import com.climesoft.climechat.database.DBMeta;

public class SMSGroupLeaveBroadcaster extends BroadcastReceiver {
    public ConversationActivity activity = null;
    private DBActions dbActions;
    private DBInfo dbInfo;
    private static int count = 0;
    private static int totalMembers = 0;

    public SMSGroupLeaveBroadcaster(){

    }

    public SMSGroupLeaveBroadcaster(ConversationActivity activity){
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
        long gid = intent.getLongExtra("gid", 0L);
        long mid = intent.getLongExtra("mid", 0L);
        ContentValues values = new ContentValues();
        values.put("gid", gid);
        values.put("mid", mid);
        values.put("status", status);
        dbActions.insert(values, DBMeta.TABLE_LEAVE_STATUS);
        if(totalMembers == 0){
            totalMembers = dbInfo.countGroupMembers(gid);
        }
        if(status == 1){
            dbActions.removeMember(gid, dbInfo.getPhoneByMemberId(mid));
        }
        ++count;
        if(count == totalMembers){
            ProgressBar leavingProgress = null;
            if(this.activity != null){
                leavingProgress = (ProgressBar)activity.findViewById(R.id.leaving_progress);
            }
            if(dbInfo.countLeaveStatus(gid) < dbInfo.countGroupMembers(gid)){
                if(this.activity != null){
                    CommonUtil.showMessage(activity, "Something went wrong!");
                }
            }
            if(dbInfo.countLeaveStatus(gid) >= dbInfo.countGroupMembers(gid)){
                if(this.activity != null){
                    activity.activeCommunication();
                }
            }
            if(leavingProgress != null){
                leavingProgress.setVisibility(View.GONE);
            }
            count = 0;
            totalMembers = 0;
        }
    }
}