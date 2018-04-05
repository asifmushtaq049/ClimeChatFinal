package com.climesoft.climechat;

/**
 * Created by Asif on 6/4/2017.
 */

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.view.ViewPager;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.climesoft.climechat.adapters.MainPagerAdapter;
import com.climesoft.climechat.common.CommonUtil;
import com.climesoft.climechat.fragments.GroupsFragment;

public class MainActivity extends AppCompatActivity implements GroupsFragment.CommunicateToGroupFragment {

    private Toolbar mainToolbar;
    private ViewPager mainViewPager;
    private TabLayout mainTabLayout;
    private FloatingActionButton createGroupFloatingButton;
    private static MainActivity instance;
    private int CREATE_NEW_GROUP_REQUEST = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainToolbar = (Toolbar)findViewById(R.id.mainToolbar);
        mainViewPager = (ViewPager)findViewById(R.id.mainViewPager);
        mainTabLayout = (TabLayout)findViewById(R.id.mainTabLayout);
        setTabListener();
        createGroupFloatingButton = (FloatingActionButton)findViewById(R.id.floatingCreateGroup);
        mainViewPager.setAdapter(new MainPagerAdapter(getSupportFragmentManager(), this));
        mainTabLayout.setupWithViewPager(mainViewPager);
        setSupportActionBar(mainToolbar);

    }

    public void setTabListener(){
        mainTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab){
                switch(tab.getPosition()){
                    case 0:
                        createGroupFloatingButton.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        createGroupFloatingButton.setVisibility(View.GONE);
                        break;
                }
            }
            public void onTabUnselected(TabLayout.Tab tab){
            }
            public void onTabReselected(TabLayout.Tab tab){
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CREATE_NEW_GROUP_REQUEST) {
            if(resultCode == RESULT_OK){
                if(data.hasExtra(CommonUtil.GROUP_CREATED)){
                    String result = data.getStringExtra(CommonUtil.GROUP_CREATED);
                    if(result.equals("OK")){
                        updateGroups();
                        CommonUtil.showMessage(this, "Group has been created!");
                    }
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch(menuItem.getItemId()){
            case R.id.createNewGroup:
                openCreateGroup(null);
                return true;
            case R.id.notifications:
                CommonUtil.goToActivity(this, NotificationActivity.class);
                return true;
        }
        return true;
    }
    public void openCreateGroup(View view){
        CommonUtil.goToActivityForResult(this, CreateGroupActivity.class, CREATE_NEW_GROUP_REQUEST);
    }

    @Override
    public void onResume(){
        super.onResume();
        instance = this;
    }

    @Override
    public void onPause(){
        super.onPause();
        instance = null;
    }

    public static MainActivity getInstance(){
        return instance;
    }

    public void updateGroups(){
        GroupsFragment groupsFragment = (GroupsFragment)findFragmentByPosition(0);
        if(groupsFragment != null){
            groupsFragment.populateAdapter();
        }
    }

    public Fragment findFragmentByPosition(int position) {
        Fragment page = getSupportFragmentManager()
                .findFragmentByTag("android:switcher:" + R.id.mainViewPager + ":" + position);
        return page;
    }
}