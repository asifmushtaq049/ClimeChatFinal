package com.climesoft.climechat.adapters;

/**
 * Created by Asif on 6/6/2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.climesoft.climechat.ConversationActivity;
import com.climesoft.climechat.R;
import com.climesoft.climechat.common.CommonUtil;
import com.climesoft.climechat.common.ContactUtil;
import com.climesoft.climechat.common.SmsUtil;
import com.climesoft.climechat.database.DBInfo;
import com.climesoft.climechat.database.DBMeta;
import com.climesoft.climechat.model.Group;
import com.climesoft.climechat.model.Message;

public class ConversationAdapter extends CursorRecyclerViewAdapter<RecyclerView.ViewHolder>{

    private DBInfo dbInfo;
    private long groupId;
    private Context context;

    public ConversationAdapter(Context context,Cursor cursor, long groupId){
        super(context,cursor);
        this.context = context;
        this.dbInfo = new DBInfo(context);
        this.groupId = groupId;
    }
    @Override
    public int getItemViewType(int position) {
        return dbInfo.getMessageSenderByPosition(position, groupId);
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch(viewType){
            case 1:
            case -1:
            case 0:
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_conversation_message_send, parent, false);
                return new SenderViewHolder(itemView);
            default:
                View itemView2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_conversation_message_receive, parent, false);
                return new ReceiverViewHolder(itemView2);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Cursor cursor) {
        final Message message = Message.Companion.fromCursor(cursor);
        if(viewHolder.getItemViewType() == 1 || viewHolder.getItemViewType() == 0 || viewHolder.getItemViewType() == -1){
            final SenderViewHolder sVH = (SenderViewHolder)viewHolder;
            sVH.messageBody.setText(message.getBody());
            sVH.messageDate.setText(CommonUtil.getRelativeDateTime(message.getDate()));


            if(message.getSender().equals("0")){
                sVH.messageFrom.setText("Me ");
            }
            if(message.getStatus() == 0){
                sVH.progressBar.setVisibility(View.VISIBLE);
                sVH.checkImage.setVisibility(View.GONE);
            }
            if(message.getStatus() == 1){
                sVH.progressBar.setVisibility(View.GONE);
                sVH.checkImage.setVisibility(View.VISIBLE);
                sVH.checkImage.setImageResource(R.drawable.icon_check);
                sVH.messageBody.setBackgroundResource(R.drawable.message_send_background);
            }
            if(message.getStatus() == -1){
                sVH.progressBar.setVisibility(View.GONE);
                sVH.checkImage.setVisibility(View.VISIBLE);
                sVH.messageBody.setBackgroundResource(R.drawable.message_failed_background);
                sVH.checkImage.setImageResource(R.drawable.icon_pending);
                sVH.checkImage.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        ((ConversationActivity)context).resendMessage(message.getId(), SmsUtil.MESSAGE_STRING + message.getGid() + "/" + message.getBody());
                        sVH.progressBar.setVisibility(View.VISIBLE);
                        sVH.checkImage.setVisibility(View.GONE);
                    }
                });
            }
        }else{
            ReceiverViewHolder sVH = (ReceiverViewHolder)viewHolder;
            sVH.messageBody.setText(message.getBody());
            sVH.messageDate.setText(CommonUtil.getRelativeDateTime(message.getDate()));
            if(!message.getSender().equals("0")){
                sVH.messageFrom.setText(ContactUtil.getContactNameByPhone(context, message.getSender()) + " ");
            }
        }
    }

    public static class SenderViewHolder extends RecyclerView.ViewHolder {
        public TextView messageBody;
        public TextView messageDate;
        public ProgressBar progressBar;
        public ImageView checkImage;
        public TextView messageFrom;
        public SenderViewHolder(View view) {
            super(view);
            messageBody = (TextView)view.findViewById(R.id.messageBody);
            messageDate = (TextView)view.findViewById(R.id.messageDate);
            progressBar = (ProgressBar)view.findViewById(R.id.waitingSMSSend);
            checkImage = (ImageView)view.findViewById(R.id.checkImage);
            messageFrom = (TextView)view.findViewById(R.id.from);
        }
    }
    public static class ReceiverViewHolder extends RecyclerView.ViewHolder {
        public TextView messageBody;
        public TextView messageDate;
        public TextView messageFrom;

        public ReceiverViewHolder(View view) {
            super(view);
            messageBody = (TextView)view.findViewById(R.id.messageBody);
            messageDate = (TextView)view.findViewById(R.id.messageDate);
            messageFrom = (TextView)view.findViewById(R.id.from);
        }
    }
}