package com.climesoft.climechat.broadcasters;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.climesoft.climechat.ConversationActivity;
import com.climesoft.climechat.MainActivity;
import com.climesoft.climechat.R;
import com.climesoft.climechat.common.CommonUtil;
import com.climesoft.climechat.common.SmsUtil;
import com.climesoft.climechat.database.DBActions;
import com.climesoft.climechat.database.DBInfo;
import com.climesoft.climechat.database.DBMeta;


public class SMSReceivedBroadcaster extends BroadcastReceiver{

    private final SmsManager sms = SmsManager.getDefault();
    private final ConversationActivity conversationActivity = ConversationActivity.getInstance();
    private final MainActivity mainActivity = MainActivity.getInstance();


    @Override
    public void onReceive(Context context, Intent intent){
        try{
            final Bundle bundle = intent.getExtras();
            DBActions dbActions = new DBActions(context);
            DBInfo dbInfo = new DBInfo(context);
            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                if(pdusObj == null) {
                    return;
                }
                for (Object i : pdusObj) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[])i);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();
                    if(message.startsWith(SmsUtil.STARTING_STRING)){
                        if(message.startsWith(SmsUtil.JOIN_GROUP_STRING)){
                            String detail = message.substring(SmsUtil.JOIN_GROUP_STRING.length());
                            String groupId = detail.split("/")[0];
                            if(!dbInfo.isGroupAvailable(Long.parseLong(groupId))){
                                String groupName = detail.split("/")[1];
                                String body = context.getString(R.string.you_are_invited_to_join) + " {" + groupName + "} Group";
                                ContentValues values = new ContentValues();
                                values.put("phone", phoneNumber);
                                values.put("body", body);
                                values.put("date", CommonUtil.getDateTimeMilli());
                                values.put("response", 0);
                                values.put("gid", groupId);
                                dbActions.insert(values, DBMeta.TABLE_NOTIFICATIONS);
                                CommonUtil.showGroupJoinNotification(context, body);
                            }
                        }
                        if(message.startsWith(SmsUtil.MEMBER_JOINED_STRING)){
                            String n = message.substring(SmsUtil.MEMBER_JOINED_STRING.length());
                            String groupId = n.split("/")[0];
                            if(dbInfo.isAlreadyMember(phoneNumber, Long.parseLong(groupId))){
                                ContentValues values = new ContentValues();
                                values.put("active", 1);
                                dbActions.updateMember(values, phoneNumber, Long.parseLong(groupId));
                                updateCommunication();
                                Cursor allMembers = dbActions.getCursor(DBMeta.FETCH_ACTIVE_MEMBERS_OF_GROUP_RECEIVER + groupId);
                                String membersList = "";
                                if(allMembers.moveToFirst()){
                                    do{
                                        membersList = membersList + allMembers.getString(1) + ";" + allMembers.getString(2) + ",";
                                    }while(allMembers.moveToNext());
                                    allMembers.close();
                                }
                                String groupName = dbInfo.getGroupNameById(Long.parseLong(groupId));
                                String shareMembers = SmsUtil.MEMBER_WELCOME_STRING + groupId + "/" + groupName + "/" + membersList;
                                Cursor members = dbActions.getCursor(DBMeta.FETCH_ACTIVE_MEMBERS_OF_GROUP_RECEIVER + groupId);
                                String joinNumber = "";
                                if(members.moveToFirst()){
                                    do {
                                        String phone = members.getString(1);
                                        if(!CommonUtil.compareNumbers(phone,phoneNumber)) {
                                            sms.sendTextMessage(phone, null, shareMembers.replace(phone + ";1,", ""), null, null);
                                        }else{
                                            joinNumber = phone;
                                        }
                                    }while (members.moveToNext());
                                    sms.sendTextMessage(joinNumber, null, shareMembers.replace(joinNumber+";1,", ""), null, null);
                                    members.close();
                                }
                            }
                        }
                        if(message.startsWith(SmsUtil.MEMBER_WELCOME_STRING)){
                            String detail = message.substring(SmsUtil.MEMBER_WELCOME_STRING.length());
                            String[] data = detail.split("/");
                            String groupId = data[0];
                            String groupName = data[1];
                            ContentValues groupValues = new ContentValues();
                            groupValues.put("id", groupId);
                            groupValues.put("name", groupName);
                            groupValues.put("admin", phoneNumber);
                            groupValues.put("date", CommonUtil.getDateTimeMilli());
                            groupValues.put("last_activity", CommonUtil.getDateTimeMilli());
                            dbActions.insert(groupValues, DBMeta.TABLE_GROUPS);
                            if(data.length >= 3){
                                String membersList = data[2];
                                String[] members = membersList.split(",");
                                long groupLongId = Long.parseLong(groupId);
                                for(String member : members){
                                    if(!dbInfo.isAlreadyMember(member, groupLongId)) {
                                        ContentValues values = new ContentValues();
                                        values.put("phone", member.split(";")[0]);
                                        values.put("active", member.split(";")[1]);
                                        values.put("cid", 0);
                                        values.put("join_date", CommonUtil.getDateTimeMilli());
                                        values.put("gid", groupId);
                                        dbActions.insert(values, DBMeta.TABLE_MEMBERS);
                                    }
                                }
                            }
                            if(!dbInfo.isAlreadyMember(phoneNumber, Long.parseLong(groupId))) {
                                ContentValues values = new ContentValues();
                                values.put("phone", phoneNumber);
                                values.put("active", 1);
                                values.put("cid", 0);
                                values.put("join_date", CommonUtil.getDateTimeMilli());
                                values.put("gid", groupId);
                                dbActions.insert(values, DBMeta.TABLE_MEMBERS);
                            }
                            CommonUtil.showWelcomeNotification(context, dbInfo.getGroupNameById(Long.parseLong(groupId)));
                            updateGroups();
                        }

                        if(message.startsWith(SmsUtil.MESSAGE_STRING)){
                            String n = message.substring(SmsUtil.MESSAGE_STRING.length());
                            String groupId = n.split("/")[0];
                            String msgBody = n.substring(groupId.length()+1);
                            if(dbInfo.isMember(phoneNumber, Long.parseLong(groupId))){
                                ContentValues values = new ContentValues();
                                values.put("body", msgBody);
                                values.put("sender", phoneNumber);
                                values.put("date", CommonUtil.getDateTimeMilli());
                                values.put("seen", 0);
                                values.put("gid", groupId);
                                dbActions.insert(values, DBMeta.TABLE_MESSAGES);
                                ContentValues gValues = new ContentValues();
                                gValues.put("last_activity", CommonUtil.getDateTimeMilli());
                                dbActions.update(DBMeta.TABLE_GROUPS, gValues, Long.parseLong(groupId));
                                updateConversation();
                                CommonUtil.showMessageNotification(context, dbInfo.getGroupNameById(Long.parseLong(groupId)));
                            }else{
                                return;
                            }
                        }

                    if(message.startsWith(SmsUtil.REMOVE_MEMBER_STRING)){
                        String detail = message.substring(SmsUtil.REMOVE_MEMBER_STRING.length());
                        String groupId = detail.split("/")[0];
                        String phone = detail.split("/")[1];
                        if(dbInfo.isGroupAdmin(phoneNumber, groupId)){
                            int v = dbActions.removeMember(Long.parseLong(groupId), phone);
                            updateCommunication();
                        }
                    }
                    if(message.startsWith(SmsUtil.LEAVE_GROUP_STRING)){
                        String detail = message.substring(SmsUtil.LEAVE_GROUP_STRING.length());
                        String groupId = detail.split("/")[0];
                        dbActions.deleteMe(Long.parseLong(groupId), phoneNumber);
                        updateCommunication();
                    }
                    if(message.startsWith(SmsUtil.YOU_ARE_OUT_STRING)){
                        String groupId = message.substring(SmsUtil.YOU_ARE_OUT_STRING.length());
                        if(dbInfo.isGroupAdmin(phoneNumber, groupId)) {
                            dbActions.deleteMembers(Long.parseLong(groupId));
                            updateCommunication();
                        }
                    }
                        abortBroadcast();
                    }
                }
            }
        }catch(Exception e){
        }
    }

    private void playNotificationSound(Context context){
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(context.getApplicationContext(), notification);
        r.play();
    }
    private void updateConversation(){
        if(conversationActivity != null){
            conversationActivity.updateMessages();
        }
    }
    private void updateCommunication(){
        if(conversationActivity != null){
            conversationActivity.activeCommunication();
        }
    }
    private void updateGroups(){
        if(mainActivity != null){
            mainActivity.updateGroups();
        }
    }
}