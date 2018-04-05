package com.climesoft.climechat.common;

/**
 * Created by Asif on 6/4/2017.
 */
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneNumberUtils;
import android.text.format.DateUtils;
import android.util.Base64;
import android.widget.Toast;

import com.climesoft.climechat.MainActivity;
import com.climesoft.climechat.NotificationActivity;
import com.climesoft.climechat.R;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CommonUtil {
    public static final String GROUP_CREATED = "GROUP_CREATED";
    public static final String DEFAULT_DATE_FORMAT = "dd MMM yyyy hh:mm:ss a";

    public static void goToActivity(Context context, Class<?> activity){
        Intent intent = new Intent(context, activity);
        context.startActivity(intent);
    }
    public static void goToActivityWithData(Context context, Class<?> activity, Bundle bundle){
        Intent intent = new Intent(context, activity);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
    public static void goToActivityForResult(Activity context, Class<?> activity, int requestCode){
        Intent intent = new Intent(context, activity);
        context.startActivityForResult(intent, requestCode);
    }

    public static boolean compareNumbers(String phone1, String phone2){
        return PhoneNumberUtils.compare(phone1, phone2);
    }

//    private boolean comparePhone(String phone1, String phone2){
//        PhoneNumberUtil pnu = PhoneNumberUtil.getInstance();
//        MatchType mt = pnu.isNumberMatch("+11234567890", "34567890");
//        if( mt == MatchType.NSN_MATCH || mt == MatchType.EXACT_MATCH )
//        {
//            Toast.makeText(getApplicationContext(), "are Same" , Toast.LENGTH_LONG).show();
//        }
//    }

    public static long getDateTimeMilli(){
        Calendar c = Calendar.getInstance();
        return c.getTime().getTime();
    }

    public static String dateMilliToFormat(long milliSeconds, String dateFormat)
    {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        return formatter.format(new Date(milliSeconds));
    }

    public static String getRelativeDateTime(long due){
        long now = System.currentTimeMillis();
        String lastMessageDate = (String) DateUtils.getRelativeTimeSpanString(due, now, 0L, DateUtils.FORMAT_ABBREV_ALL);
        return lastMessageDate;
    }
    public static void showMessage(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    public static void showLongMessage(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
    public static long generatePrimaryKey(){
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int date = c.get(Calendar.DATE);
        int hours = c.get(Calendar.HOUR);
        int minutes = c.get(Calendar.MINUTE);
        int seconds = c.get(Calendar.SECOND);
        int milliSeconds = c.get(Calendar.MILLISECOND);
        String key = ""+milliSeconds+year+month+date+hours+minutes+seconds;
        return Long.parseLong(key);
    }

    public static void showGroupJoinNotification(Context context, String text){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle("Join ClimeChat Group!");
        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        mBuilder.setContentText(text);
        mBuilder.setAutoCancel(true);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, NotificationActivity.class), 0);
        mBuilder.setContentIntent(contentIntent);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }

    public static void showMessageNotification(Context context, String groupName){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle(groupName + " New Message!");
        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        mBuilder.setContentText("You have received new message in " + groupName);
        mBuilder.setAutoCancel(true);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);
        mBuilder.setContentIntent(contentIntent);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }

    public static void showWelcomeNotification(Context context, String groupName){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle("Welcome to " + groupName);
        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        mBuilder.setContentText("You have joined " + groupName + " group");
        mBuilder.setAutoCancel(true);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);
        mBuilder.setContentIntent(contentIntent);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }

    public static String encodeString(String text) throws UnsupportedEncodingException {
        byte[] data = text.getBytes("UTF-8");
        return Base64.encodeToString(data, Base64.DEFAULT);
    }

    public static String decodeString(String text) throws UnsupportedEncodingException {
        byte[] data = Base64.decode(text, Base64.DEFAULT);
        return (new String(data, "UTF-8"));
    }
}
