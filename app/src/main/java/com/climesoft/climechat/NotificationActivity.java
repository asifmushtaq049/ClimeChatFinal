package com.climesoft.climechat;

import android.content.IntentFilter;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.climesoft.climechat.adapters.NotificationsAdapter;
import com.climesoft.climechat.broadcasters.SMSGroupJoinBroadcaster;
import com.climesoft.climechat.database.DBActions;
import com.climesoft.climechat.database.DBMeta;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class NotificationActivity extends AppCompatActivity {
    private RecyclerView notificationsRecyclerView;
    private NotificationsAdapter adapter;
    private DBActions dbActions;
    public static final String RESPONSE = "com.climesoft.climechat.NOTIFICATION_RESPONSE";
    public static NotificationActivity instance;
//    private SMSGroupJoinBroadcaster broadCaster;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        Toolbar toolbar = (Toolbar)findViewById(R.id.mainToolbar);
//        broadCaster = new SMSGroupJoinBroadcaster(this);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        initNotifications();
    }

    private void initNotifications(){
        dbActions = new DBActions(this);
        notificationsRecyclerView = (RecyclerView)findViewById(R.id.notificationsRecyclerView);
        adapter = new NotificationsAdapter(this, getNotifications());
        notificationsRecyclerView.setAdapter(adapter);
        notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        notificationsRecyclerView.setItemAnimator(new SlideInUpAnimator());
    }
    private Cursor getNotifications(){
            return dbActions.getCursor(DBMeta.FETCH_NOTIFICATIONS_DESC_ORDER_BY_DATE);
    }

    public void updateNotifications(){
        adapter.changeCursor(getNotifications());
        adapter.notifyDataSetChanged();
    }

    public static NotificationActivity getInstance(){
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
//        registerReceiver(broadCaster, new IntentFilter(RESPONSE));
    }

    @Override
    public void onPause(){
        super.onPause();
        instance = null;
//        unregisterReceiver(broadCaster);
    }
}