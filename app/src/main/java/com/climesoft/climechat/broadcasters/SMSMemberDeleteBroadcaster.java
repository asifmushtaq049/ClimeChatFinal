package com.climesoft.climechat.broadcasters;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.climesoft.climechat.ConversationActivity;
import com.climesoft.climechat.MembersActivity;
import com.climesoft.climechat.R;
import com.climesoft.climechat.common.CommonUtil;
import com.climesoft.climechat.database.DBActions;
import com.climesoft.climechat.database.DBInfo;
import com.climesoft.climechat.database.DBMeta;

/**
 * Created by Asif on 6/26/2017.
 */

public class SMSMemberDeleteBroadcaster extends BroadcastReceiver {
    public MembersActivity activity;
    private DBActions dbActions;
    private DBInfo dbInfo;
    private static int count = 0;

    public SMSMemberDeleteBroadcaster(){

    }

    public SMSMemberDeleteBroadcaster(MembersActivity activity){
//        this.activity = activity;
//        dbActions = new DBActions(this.activity);
//        dbInfo = new DBInfo(this.activity);
    }

    @Override
    public void onReceive(Context context, Intent arg1) {
        this.activity = MembersActivity.getInstance();
        dbActions = new DBActions(context);
        dbInfo = new DBInfo(context);

        switch (getResultCode()) {
            case Activity.RESULT_OK:
                updateSender(arg1, 1);
                Log.d("DeleteBroadcast: ", "Ok Called");
                break;
            default:
                updateSender(arg1, 0);
                Log.d("DeleteBroadcast: ", "False Called");
                break;
        }

    }
    private void updateSender(Intent intent, int status){
        String phone = intent.getStringExtra("phoneNumber");
        long mid = intent.getLongExtra("mid", 0L);
        long groupId = intent.getLongExtra("groupId", 0L);
        int from = intent.getIntExtra("from", 0);
        ProgressBar pBar = null;
        if(this.activity != null){
            pBar = (ProgressBar)activity.findViewById(R.id.member_deleting);
        }
        if(from == 1){
            ContentValues values = new ContentValues();
            values.put("phone", phone);
            values.put("mid", mid);
            values.put("status", status);
            values.put("gid", groupId);
            dbActions.insert(values, DBMeta.TABLE_MEMBER_STATUS);
            ++count;
            if(count == dbInfo.countGroupMembers(groupId)){
                ContentValues val = new ContentValues();
                if(dbInfo.countRemovedMember(phone, groupId) < dbInfo.countGroupMembers(groupId)){
                    val.put("status", -1);
                    if(this.activity != null){
                        CommonUtil.showMessage(activity, "Something went wrong!");
                    }
                }
                if(dbInfo.countRemovedMember(phone, groupId) >= dbInfo.countGroupMembers(groupId)){
                    val.put("status", 1);
                    dbActions.deleteMember(groupId, phone);
                }
                if(pBar != null){
                    pBar.setVisibility(View.GONE);
                }
                if(this.activity != null){
                    activity.updateMembers();
                }
                count = 0;
            }
        }
        if(from == 2){
            ContentValues values = new ContentValues();
            values.put("phone", phone);
            values.put("mid", mid);
            values.put("status", status);
            values.put("gid", groupId);
            dbActions.insert(values, DBMeta.TABLE_MEMBER_STATUS);
            if(status == 1){
                dbActions.deleteMember(groupId, phone);
                if(pBar != null){
                    pBar.setVisibility(View.GONE);
                }
                if(this.activity != null){
                    activity.updateMembers();
                }
            }else if(status == 0){
                if(this.activity != null){
                    CommonUtil.showMessage(activity, "Something went wrong!");
                }
                if(pBar != null){
                    pBar.setVisibility(View.GONE);
                }
            }
        }
    }
}