package com.climesoft.climechat;

import android.content.ContentValues;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.climesoft.climechat.common.CommonUtil;
import com.climesoft.climechat.database.DBActions;
import com.climesoft.climechat.database.DBMeta;

public class CreateGroupActivity extends AppCompatActivity {

    private DBActions dbActions;
    private TextView groupNameField;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        dbActions = new DBActions(this);
        groupNameField = (TextView)findViewById(R.id.enterGroupName);
        Toolbar toolbar = (Toolbar)findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public void createGroup(View view){
        try{
            String groupName = groupNameField.getText().toString();
            if(groupName.trim().isEmpty()){
                return;
            }
            if(!groupName.isEmpty()){
                ContentValues values = new ContentValues();
                values.put("id", CommonUtil.generatePrimaryKey());
                values.put("name", groupName);
                values.put("admin", "1");
                values.put("date", CommonUtil.getDateTimeMilli());
                values.put("last_activity", CommonUtil.getDateTimeMilli());
                dbActions.insert(values, DBMeta.TABLE_GROUPS);
                goToGroups();
            }
        }catch(Exception e){
            CommonUtil.showMessage(this, "Something went wrong!");
        }
    }

    public void goToGroups(){
        Intent data = new Intent();
        data.putExtra(CommonUtil.GROUP_CREATED, "OK");
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch(menuItem.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return true;
    }
}