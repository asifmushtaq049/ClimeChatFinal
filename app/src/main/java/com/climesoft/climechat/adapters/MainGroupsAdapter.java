package com.climesoft.climechat.adapters;

/**
 * Created by Asif on 6/6/2017.
 */

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.climesoft.climechat.R;
import com.climesoft.climechat.database.DBInfo;
import com.climesoft.climechat.listeners.CABGroupsMenuListener;
import com.climesoft.climechat.common.CommonUtil;
import com.climesoft.climechat.model.Group;

import java.util.ArrayList;

public class MainGroupsAdapter extends CursorRecyclerViewAdapter<MainGroupsAdapter.ViewHolder>{

    private RecyclerView mRecyclerView;
    private Context context;
    private ArrayList<Integer> selectedItems = new ArrayList<>();
    private DBInfo dbInfo;

    public MainGroupsAdapter(Context context,Cursor cursor){
        super(context,cursor);
        this.context = context;
        this.dbInfo = new DBInfo(context);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_groups_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        Group group = Group.Companion.fromCursor(cursor);
        viewHolder.view.setSelected(viewHolder.cabMenuListener.contains(viewHolder.getAdapterPosition()));
        viewHolder.textViewGroupName.setText(group.getName());
        String date = CommonUtil.getRelativeDateTime(group.getLastActivity());
        viewHolder.textViewLastMessage.setText(date);
        int notifications = dbInfo.countUnSeenSms(group.getId());
        if(notifications > 0){
            viewHolder.textViewNewNotifications.setVisibility(View.VISIBLE);
            viewHolder.textViewNewNotifications.setText(String.valueOf(notifications));
        }else{
            viewHolder.textViewNewNotifications.setVisibility(View.GONE);
            viewHolder.textViewNewNotifications.setText("0");
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewGroupName;
        public TextView textViewLastMessage;
        public TextView textViewNewNotifications;
        public View view;
        CABGroupsMenuListener cabMenuListener = new CABGroupsMenuListener(mRecyclerView, selectedItems);
        public ViewHolder(View v) {
            super(v);
            this.view = v;
            textViewGroupName = (TextView)view.findViewById(R.id.groupNameTitle);
            textViewLastMessage = (TextView)view.findViewById(R.id.groupLastMessage);
            textViewNewNotifications = (TextView)view.findViewById(R.id.newNotifications);
            view.setOnClickListener(cabMenuListener);
            view.setOnLongClickListener(cabMenuListener);
        }
    }
}