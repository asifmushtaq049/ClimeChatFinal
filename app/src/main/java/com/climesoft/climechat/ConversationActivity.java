package com.climesoft.climechat;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.climesoft.climechat.adapters.ConversationAdapter;
import com.climesoft.climechat.broadcasters.SMSGroupLeaveBroadcaster;
import com.climesoft.climechat.broadcasters.SMSResendBroadcaster;
import com.climesoft.climechat.broadcasters.SMSSentBroadcaster;
import com.climesoft.climechat.common.CommonUtil;
import com.climesoft.climechat.common.SmsUtil;
import com.climesoft.climechat.database.DBActions;
import com.climesoft.climechat.database.DBInfo;
import com.climesoft.climechat.database.DBMeta;
import com.climesoft.climechat.broadcasters.SMSDeliveredBroadcaster;

import org.jetbrains.annotations.Contract;

import java.io.UnsupportedEncodingException;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class ConversationActivity extends AppCompatActivity {
    private DBActions dbActions;
    private RecyclerView conversationRecyclerView;
    private ConversationAdapter adapter;
    private Toolbar toolbar;
    private long groupId;
    private String groupName;
    private EditText sendMessageField;
    private ImageButton sendMessageButton;
    private ProgressBar leavingProgress;
    private final int REQUEST_CODE_PICK_CONTACT = 1;
    private DBInfo dbInfo;
    private SmsUtil smsUtil = new SmsUtil();
    private String SENT = "com.climesoft.climechat.SMS_SENT";
    private String SMS_RESEND = "com.climesoft.climechat.SMS_RESEND";
    private String FILTER_LEAVE = "com.climesoft.climechat.SMS_LEAVE";
//    private SMSSentBroadcaster smsSentBroadcaster;
//    private SMSResendBroadcaster smsResendBroadcaster;
//    private SMSGroupLeaveBroadcaster smsLeaveBroadcaster;
    private static ConversationActivity instance = null;
    private long lastMessage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        initFields();
        activeCommunication();
        initMessages();
        setupTitle();
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    private void initFields(){
        groupId = getIntent().getLongExtra("groupId", 0L);
        toolbar = (Toolbar)findViewById(R.id.mainToolbar);
        sendMessageField = (EditText)findViewById(R.id.sendMessageField);
        sendMessageButton = (ImageButton)findViewById(R.id.sendMessageButton);
        leavingProgress = (ProgressBar)findViewById(R.id.leaving_progress);
//        smsSentBroadcaster = new SMSSentBroadcaster(this);
//        smsResendBroadcaster = new SMSResendBroadcaster(this);
//        smsLeaveBroadcaster = new SMSGroupLeaveBroadcaster(this);
        dbActions = new DBActions(this);
        dbInfo = new DBInfo(this);
    }
    public void activeCommunication(){
        if(dbInfo.haveMembers(groupId)){
            activeSender();
        }else{
            deActiveSender();
        }
    }
    private void deActiveSender(){
        sendMessageField.setEnabled(false);
        sendMessageButton.setEnabled(false);
        sendMessageField.setClickable(false);
        sendMessageButton.setClickable(false);
    }
    private void activeSender(){
        sendMessageField.setEnabled(true);
        sendMessageButton.setEnabled(true);
        sendMessageField.setClickable(true);
        sendMessageButton.setClickable(true);
    }
    private void initMessages(){
        conversationRecyclerView = (RecyclerView)findViewById(R.id.conversationRecyclerView);
        adapter = new ConversationAdapter(this, getGroupMessages(), groupId);
        conversationRecyclerView.setAdapter(adapter);
        conversationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        conversationRecyclerView.setItemAnimator(new SlideInUpAnimator());
        scrollToEnd();
    }
    private void scrollToEnd(){
        if(adapter.getItemCount() > 0){
            conversationRecyclerView.smoothScrollToPosition(adapter.getItemCount()-1);
        }
    }
    private void setupTitle(){
        groupName = dbInfo.getGroupNameById(groupId);
        setTitle(groupName);
    }
    public Cursor getGroupMessages(){
        return dbActions.getCursor(DBMeta.FETCH_MESSAGES_OF_GROUP + groupId);
    }
    public void sendMessage(View view){
        long dateInMilli = CommonUtil.getDateTimeMilli();
        if(( dateInMilli - lastMessage)/1000 < 4){
            CommonUtil.showMessage(this, "Wait 3 sec!");
            return;
        }
        String message = sendMessageField.getText().toString();
        if(message.isEmpty()){
            return;
        }
        ContentValues values = new ContentValues();
        values.put("body", message);
        values.put("sender", 0);
        values.put("date", dateInMilli);
        values.put("seen", 1);
        values.put("gid", groupId);
        long smsId = dbActions.insert(values, DBMeta.TABLE_MESSAGES);
        ContentValues gValues = new ContentValues();
        gValues.put("last_activity", dateInMilli);
        dbActions.update(DBMeta.TABLE_GROUPS, gValues, groupId);
        sendMessageField.getText().clear();
        updateMessages();
        sendMessageToAll(SmsUtil.MESSAGE_STRING + groupId + "/" + message, smsId);
        lastMessage = dateInMilli;
    }
    private void sendMessageToAll(String msgParam, long smsId){
        // Sending side
        String msg = msgParam;
        Intent sentIntent = new Intent(SENT);
        sentIntent.putExtra("sid", smsId);
        sentIntent.putExtra("groupId", groupId);
//        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), PendingIntent.FLAG_UPDATE_CURRENT);
        SmsManager smsManager = SmsManager.getDefault();
        Cursor members = dbInfo.getActiveMembers(groupId);
        if(members.moveToFirst()){
            do {
                String phone = members.getString(2);
                long mid = members.getLong(0);
                sentIntent.putExtra("mid", mid);
                PendingIntent sentPI = PendingIntent.getBroadcast(this, (int)smsId, sentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                smsManager.sendTextMessage(phone, null, msg, sentPI, null);
            }while (members.moveToNext());
        }
    }
    public void resendMessage(long smsId, String msg){
        Cursor pendingMembers = dbInfo.getPendingMembers(smsId);
        Intent sentIntent = new Intent(SMS_RESEND);
        sentIntent.putExtra("sid", smsId);
        SmsManager smsManager = SmsManager.getDefault();
        if(pendingMembers.moveToFirst()){
            do {
                long mid = pendingMembers.getLong(3);
                String phone = dbInfo.getPhoneByMemberId(mid);
                Log.d("Resend Mid: ", ""+mid);
                Log.d("Resend Phone: ", ""+phone);
                Log.d("Resend Message: ", ""+msg);
                sentIntent.putExtra("mid", mid);
                PendingIntent resendPI = PendingIntent.getBroadcast(this, 0, sentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                smsManager.sendTextMessage(phone, null, msg, resendPI, null);
            }while (pendingMembers.moveToNext());
        }
    }
    public void updateMessages(){
        adapter.changeCursor(getGroupMessages());
        adapter.notifyItemInserted(adapter.getItemCount()-1);
        seenAllMessages();
        scrollToEnd();
    }
    private Cursor getMembers(){
        Cursor members = dbActions.getCursor(DBMeta.FETCH_MEMBERS_OF_GROUP + groupId);
        return members;
    }

    private void seenAllMessages(){
        ContentValues values = new ContentValues();
        values.put("seen", 1);
        dbActions.updateMessage(values, groupId);
    }

    public static ConversationActivity getInstance(){
        return instance;
    }

    @Override
    public void onResume(){
        super.onResume();
        activeCommunication();
        updateMessages();
        instance = this;
//        registerReceiver(smsSentBroadcaster, new IntentFilter(SENT));
//        registerReceiver(smsResendBroadcaster, new IntentFilter(SMS_RESEND));
//        registerReceiver(smsLeaveBroadcaster, new IntentFilter(FILTER_LEAVE));
    }

    @Override
    public void onPause(){
        super.onPause();
        instance = null;
//        unregisterReceiver(smsSentBroadcaster);
//        unregisterReceiver(smsResendBroadcaster);
//        unregisterReceiver(smsLeaveBroadcaster);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_activity_conversations, menu);
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(!dbInfo.isGroupAdmin(groupId)){
            menu.findItem(R.id.addMember).setVisible(false);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Bundle data = new Bundle();
        data.putLong("groupId", groupId);
        data.putString("groupName", groupName);
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.addMember:
                pickMember();
                return true;
            case R.id.viewMembers:
                CommonUtil.goToActivityWithData(this, MembersActivity.class, data);
                return true;
            case R.id.aboutGroup:
                CommonUtil.goToActivityWithData(this, AboutGroupActivity.class, data);
                return true;
            case R.id.leaveGroup:
                leaveGroup();
                return true;
        }
        return true;
    }
    public void pickMember(){
        if(dbInfo.countGroupMembers(groupId) <=5 ){
            Intent contactsIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            contactsIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
            startActivityForResult(contactsIntent, REQUEST_CODE_PICK_CONTACT);
        }else{
            CommonUtil.showMessage(this, "Maximum 5 members are allowed!");
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK)
        {
            if(requestCode == REQUEST_CODE_PICK_CONTACT )
            {
                String[] projection = { ContactsContract.CommonDataKinds.Phone.CONTACT_ID, ContactsContract.CommonDataKinds.Phone.NUMBER};
                Cursor cursor = getContentResolver().query(data.getData(), projection,
                        null, null, null);
                if(cursor != null) {
                    if (cursor.moveToFirst()) {
                        int idColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
                        String id = cursor.getString(idColumnIndex);
                        int numberColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        String number = cursor.getString(numberColumnIndex);
                        addMemberToDb(id, number);
                    }
                    cursor.close();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void addMemberToDb(String contactId, String phone){
        if(!dbInfo.isAlreadyMember(phone, groupId)) {
            ContentValues values = new ContentValues();
            values.put("phone", phone.replace(" ", ""));
            values.put("active", 0);
            values.put("cid", contactId);
            values.put("join_date", CommonUtil.getDateTimeMilli());
            values.put("gid", groupId);
            dbActions.insert(values, DBMeta.TABLE_MEMBERS);
            inviteMember(phone);
            activeCommunication();
            CommonUtil.showMessage(this, "Selected Member Invited!");
        }
        else{
            CommonUtil.showMessage(this, phone + " Already Joined!");
        }
    }
    public void inviteMember(String member){
            String invitationCode = SmsUtil.JOIN_GROUP_STRING + groupId + "/" + groupName;
            smsUtil.sendSms(member, invitationCode);
    }

    public void leaveGroup(){
        if(dbInfo.haveMembers(groupId)){
            leavingProgress.setVisibility(View.VISIBLE);
            sendMessageToAllForLeave(SmsUtil.LEAVE_GROUP_STRING + groupId);
        }
    }
    private void sendMessageToAllForLeave(String msgParam){
        Intent sentIntent = new Intent(FILTER_LEAVE);
        sentIntent.putExtra("gid", groupId);
        SmsManager smsManager = SmsManager.getDefault();
        Cursor members = dbInfo.getActiveMembers(groupId);
        if(members.moveToFirst()){
            do {
                String phone = members.getString(2);
                long mid = members.getLong(0);
                sentIntent.putExtra("mid", mid);
                PendingIntent sentPI = PendingIntent.getBroadcast(this, (int)mid, sentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                smsManager.sendTextMessage(phone, null, msgParam, sentPI, null);
            }while (members.moveToNext());
        }
    }
}