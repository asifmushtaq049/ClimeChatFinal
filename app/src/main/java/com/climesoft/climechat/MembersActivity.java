package com.climesoft.climechat;

import android.content.IntentFilter;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.climesoft.climechat.adapters.MembersAdapter;
import com.climesoft.climechat.broadcasters.SMSMemberDeleteBroadcaster;
import com.climesoft.climechat.database.DBActions;
import com.climesoft.climechat.database.DBMeta;

public class MembersActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView memberRecyclerView;
    private MembersAdapter membersAdapter;
    private DBActions dbActions;
    private long groupId;
    private String groupName;
    private ConversationActivity cActivity;
    public static final String MEMBER_REMOVE_FILTER = "com.climesoft.climechat.MEMBER_REMOVED";
    private static MembersActivity instance;

//    private SMSMemberDeleteBroadcaster memberDelBroadcast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);
        toolbar = (Toolbar)findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);
        loadMembers();
        initViews();
    }

    private void loadMembers(){
        groupId = getIntent().getLongExtra("groupId", 0L);
        groupName = getIntent().getStringExtra("groupName");
        dbActions = new DBActions(this);
//        memberDelBroadcast = new SMSMemberDeleteBroadcaster(this);
        memberRecyclerView = (RecyclerView)findViewById(R.id.membersRecyclerView);
        membersAdapter = new MembersAdapter(this, getGroupMembers(), groupId);
        memberRecyclerView.setAdapter(membersAdapter);
        memberRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initViews(){
        setTitle(groupName + "'s Members");
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    public void updateMembers(){
        membersAdapter.changeCursor(getGroupMembers());
    }
    private Cursor getGroupMembers(){
        return  dbActions.getCursor(DBMeta.FETCH_MEMBERS_OF_GROUP + groupId);
    }

    public static MembersActivity getInstance(){
        return instance;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return true;
    }
    @Override
    public void onResume(){
        super.onResume();
        instance = this;
//        registerReceiver(memberDelBroadcast, new IntentFilter(MEMBER_REMOVE_FILTER));
    }
    @Override
    public void onPause(){
        super.onPause();
        instance = null;
//        unregisterReceiver(memberDelBroadcast);
    }
}