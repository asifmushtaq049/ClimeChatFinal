package com.climesoft.climechat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.climesoft.climechat.common.CommonUtil;
import com.climesoft.climechat.database.DBInfo;
import com.climesoft.climechat.database.DBMeta;

public class AboutGroupActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private long groupId;
    private String groupName;

    private TextView groupNameView;
    private TextView groupMembersView;
    private TextView groupDateView;
    private TextView groupActivityView;
    private DBInfo dbInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_group);
        initFields();
        setupToolbar();
        initValues();
    }

    private void initFields(){
        toolbar = (Toolbar)findViewById(R.id.about_group_toolbar);
        groupId = getIntent().getLongExtra("groupId", 0L);
        groupName = getIntent().getStringExtra("groupName");
        groupNameView = (TextView)findViewById(R.id.groupName);
        groupMembersView = (TextView)findViewById(R.id.groupMembers);
        groupDateView = (TextView)findViewById(R.id.groupDate);
        groupActivityView = (TextView)findViewById(R.id.groupActivity);
        dbInfo = new DBInfo(this);
    }

    private void setupToolbar(){
        setSupportActionBar(toolbar);
        setTitle("About Group");
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initValues(){
        groupNameView.setText(groupName);
        groupMembersView.setText(String.valueOf(dbInfo.countGroupMembers(groupId)) + " members");
        groupDateView.setText(CommonUtil.dateMilliToFormat(dbInfo.getDate(groupId, DBMeta.TABLE_GROUPS), "dd MMM, yyyy - hh:mm:ss a"));
        groupActivityView.setText(CommonUtil.getRelativeDateTime(dbInfo.getGroupLastActivity(groupId)));
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
}
