package com.climesoft.climechat.adapters;

/**
 * Created by Asif on 6/12/2017.
 */

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.climesoft.climechat.R;
import com.climesoft.climechat.common.ContactUtil;
import com.climesoft.climechat.database.DBInfo;
import com.climesoft.climechat.listeners.CABMembersMenuListener;
import com.climesoft.climechat.model.Member;

import java.util.ArrayList;

public class MembersAdapter extends CursorRecyclerViewAdapter<MembersAdapter.ViewHolder>{

    private RecyclerView mRecyclerView;
    private Context context;
    private ArrayList<String> selectedItems = new ArrayList<>();
    private long groupId;
    private DBInfo dbInfo;

    public MembersAdapter(Context context,Cursor cursor, long groupId){
        super(context,cursor);
        this.context = context;
        this.groupId = groupId;
        this.dbInfo = new DBInfo(context);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @Override
    public int getItemViewType(int position){
        return dbInfo.getMemberStatusByPosition(position, groupId);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        switch(viewType){
            case 0:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_members_pending_item, parent, false);
                break;
            case 1:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_members_active_item, parent, false);
                break;
        }
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        Member member = Member.Companion.fromCursor(cursor);
        viewHolder.view.setSelected(viewHolder.cabMenuListener.contains(
                                                                        viewHolder.cabMenuListener.getPhoneByPosition(
                                                                                viewHolder.getAdapterPosition()))
                                                                        );
        viewHolder.memberName.setText(ContactUtil.getContactNameByPhone(context, member.getPhone()));
        viewHolder.memberPhone.setText(member.getPhone());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView memberName;
        public TextView memberPhone;
        public View view;
        CABMembersMenuListener cabMenuListener = new CABMembersMenuListener(mRecyclerView, selectedItems, groupId);
        public ViewHolder(View v) {
            super(v);
            this.view = v;
            memberName = (TextView)view.findViewById(R.id.memberName);
            memberPhone = (TextView)view.findViewById(R.id.memberPhone);
            if(dbInfo.isGroupAdmin(groupId)){
                view.setOnClickListener(cabMenuListener);
                view.setOnLongClickListener(cabMenuListener);
            }
        }
    }
}