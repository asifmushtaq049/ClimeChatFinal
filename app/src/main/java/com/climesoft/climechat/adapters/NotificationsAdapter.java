package com.climesoft.climechat.adapters;

/**
 * Created by Asif on 6/16/2017.
 */

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.climesoft.climechat.ConversationActivity;
import com.climesoft.climechat.NotificationActivity;
import com.climesoft.climechat.R;
import com.climesoft.climechat.broadcasters.SMSDeliveredBroadcaster;
import com.climesoft.climechat.broadcasters.SMSGroupJoinBroadcaster;
import com.climesoft.climechat.broadcasters.SMSSentBroadcaster;
import com.climesoft.climechat.common.CommonUtil;
import com.climesoft.climechat.common.SmsUtil;
import com.climesoft.climechat.database.DBActions;
import com.climesoft.climechat.database.DBMeta;
import com.climesoft.climechat.model.Notification;


public class NotificationsAdapter extends CursorRecyclerViewAdapter<NotificationsAdapter.ViewHolder>{

    private RecyclerView mRecyclerView;
    private Context context;
    private DBActions dbActions;

    public NotificationsAdapter(Context context,Cursor cursor){
        super(context,cursor);
        this.context = context;
        this.dbActions = new DBActions(context);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_notifications_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, Cursor cursor) {
        final Notification notification = Notification.Companion.fromCursor(cursor);
        viewHolder.notificationTitle.setText(notification.getBody());
        viewHolder.notificationTime.setText(CommonUtil.getRelativeDateTime(notification.getDate()));

        final long notificationId = notification.getId();
        final String phone = notification.getPhone();
        final long groupId = notification.getGroupId();
        int notificationResponse = notification.getResponse();

        if(notificationResponse == 0){
            viewHolder.respondingButton.setVisibility(View.GONE);
            viewHolder.acceptButton.setVisibility(View.VISIBLE);
            viewHolder.rejectButton.setVisibility(View.VISIBLE);
            viewHolder.acceptButton.setText("Accept");
            viewHolder.rejectButton.setText("Reject");
            viewHolder.view.setBackgroundResource(R.color.white);
        }else if(notificationResponse == 1){
            viewHolder.rejectButton.setVisibility(View.GONE);
            viewHolder.respondingButton.setVisibility(View.GONE);
            viewHolder.acceptButton.setVisibility(View.VISIBLE);
            viewHolder.acceptButton.setText("Accepted");
            viewHolder.view.setBackgroundResource(R.color.notify_accept_color);
            viewHolder.acceptButton.setEnabled(false);
            viewHolder.acceptButton.setClickable(false);
        }else if(notificationResponse == -1){
            viewHolder.acceptButton.setVisibility(View.GONE);
            viewHolder.respondingButton.setVisibility(View.GONE);
            viewHolder.rejectButton.setVisibility(View.VISIBLE);
            viewHolder.rejectButton.setText("Rejected");
            viewHolder.view.setBackgroundResource(R.color.notify_reject_color);
            viewHolder.rejectButton.setEnabled(false);
            viewHolder.rejectButton.setClickable(false);
        } else if(notificationResponse == -2){
            viewHolder.acceptButton.setVisibility(View.GONE);
            viewHolder.rejectButton.setVisibility(View.GONE);
            viewHolder.respondingButton.setVisibility(View.VISIBLE);
            viewHolder.view.setBackgroundResource(R.color.white);
        }

        viewHolder.acceptButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                ContentValues values = new ContentValues();
                values.put("response", -2);
                dbActions.update(DBMeta.TABLE_NOTIFICATIONS, values, notificationId);
                ((NotificationActivity)context).updateNotifications();
                sendResponse(notificationId, phone, groupId);
            }
        });
        viewHolder.rejectButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                ContentValues values = new ContentValues();
                values.put("response", -1);
                dbActions.update(DBMeta.TABLE_NOTIFICATIONS, values, notificationId);
                ((NotificationActivity)context).updateNotifications();
            }
        });
    }

    public void sendResponse(long notificationId, String phone, long groupId){
        Intent responseIntent = new Intent(NotificationActivity.RESPONSE);
        responseIntent.putExtra("notificationId", notificationId);
        PendingIntent responsePI = PendingIntent.getBroadcast(context, 0, responseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phone, null, SmsUtil.MEMBER_JOINED_STRING + groupId, responsePI, null);
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView notificationTitle;
        public TextView notificationTime;
        public Button acceptButton;
        public Button rejectButton;
        public Button respondingButton;
        public View view;
        public ViewHolder(final View v) {
            super(v);
            this.view = v;
            notificationTitle = (TextView)view.findViewById(R.id.notificationTitle);
            notificationTime = (TextView)view.findViewById(R.id.notificationTime);
            acceptButton = (Button)view.findViewById(R.id.acceptButton);
            rejectButton = (Button)view.findViewById(R.id.rejectButton);
            respondingButton = (Button)view.findViewById(R.id.respondingButton);
        }
    }
}