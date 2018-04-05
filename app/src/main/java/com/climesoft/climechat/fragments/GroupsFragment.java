package com.climesoft.climechat.fragments;


import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.climesoft.climechat.R;
import com.climesoft.climechat.adapters.MainGroupsAdapter;
import com.climesoft.climechat.database.DBActions;
import com.climesoft.climechat.database.DBMeta;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends Fragment {
    private DBActions dbActions;
    private MainGroupsAdapter adapter;
    RecyclerView groupsRecyclerView;

    public GroupsFragment() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        dbActions = new DBActions(getActivity());
        View view = inflater.inflate(R.layout.fragment_main_groups, container, false);
        groupsRecyclerView = (RecyclerView)view.findViewById(R.id.groupsRecyclerView);
        adapter = new MainGroupsAdapter(getActivity(), getGroups());
        groupsRecyclerView.setAdapter(adapter);
        groupsRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        DividerItemDecoration verticalDecoration = new DividerItemDecoration(groupsRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        Drawable verticalDivider = ContextCompat.getDrawable(getActivity(), R.drawable.vertical_divider);
        verticalDecoration.setDrawable(verticalDivider);
        groupsRecyclerView.addItemDecoration(verticalDecoration);
        return view;

    }
    private void fetchMessages(){
        Cursor cursor = dbActions.getCursor("SELECT * from messages");
        if (cursor.moveToFirst()) {
            do {
                Log.d("Conversation ID: ", cursor.getString(cursor.getColumnIndex("id")));
            } while (cursor.moveToNext());
        }
    }
    public void populateAdapter(){
        if(adapter != null){
            adapter.changeCursor(getGroups());
            adapter.notifyDataSetChanged();
        }
    }
    public Cursor getGroups(){
        Cursor groups = dbActions.getCursor(DBMeta.FETCH_GROUPS_DESC_ORDER_BY_LAST_ACTIVITY);
        return groups;
    }

    @Override
    public void onResume(){
        super.onResume();
        populateAdapter();
    }
    public interface CommunicateToGroupFragment{
        void updateGroups();
    }
}