package com.climesoft.climechat.common;

/**
 * Created by Asif on 6/15/2017.
 */

import android.telephony.SmsManager;

public class SmsUtil {

    public final static String STARTING_STRING = "/c$c#/";
    public final static String JOIN_GROUP_STRING = STARTING_STRING + "jG/";
    public final static String MESSAGE_STRING = STARTING_STRING + "msg/";
    public final static String MEMBER_JOINED_STRING = STARTING_STRING + "acpt/";
    public final static String MEMBER_WELCOME_STRING = STARTING_STRING + "wlcm/";
    public final static String REMOVE_MEMBER_STRING = STARTING_STRING + "rm/";
    public final static String YOU_ARE_OUT_STRING = STARTING_STRING + "kick/";
    public final static String LEAVE_GROUP_STRING = STARTING_STRING + "leave/";

    private SmsManager sms = SmsManager.getDefault();

    public boolean sendSms(String phone, String message){
        sms.sendTextMessage(phone, null, message, null, null);
        return true;
    }
}
