package com.example.dengweixiong.Activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Menu;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.example.dengweixiong.Fragment.CourseFragment;
import com.example.dengweixiong.Fragment.MemberFragment;
import com.example.dengweixiong.Fragment.MessageFragment;
import com.example.dengweixiong.Fragment.PersonFragment;
import com.example.dengweixiong.Util.BaseActivity;
import com.example.dengweixiong.myapplication.R;
import java.util.ArrayList;

public class MainActivity extends BaseActivity implements View.OnClickListener,BottomNavigationBar.OnTabSelectedListener{

    private BottomNavigationBar bottomNavigationBar;
    private ArrayList<Fragment> fragmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initBottomNavigationBar();
        fragmentList = getFragments();
        setDefaultFragment();
        //变量

        //初始化控件

        //设置监听事件

    }

    protected void onDestroy() {
        super.onDestroy();
    }

    public void onClick(View view){

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.refresh_main:
                break;
            case R.id.add_new_story_main:
                Intent intent = new Intent(MainActivity.this,AddNewStoryActivity.class);
                startActivity(intent);
                break;
            case R.id.add_new_trend_main:
                Intent intent1 = new Intent(MainActivity.this,AddNewTrendActivity.class);
                startActivity(intent1);
                break;
            case R.id.view_personal_profile_main:
                Intent intent2 = new Intent(MainActivity.this,ViewPersonalProfileActivity.class);
                startActivity(intent2);
                break;
            default:
                break;
        }
        return true;
    }

//    private void initToolBar(String title) {
//        toolbar = (Toolbar)findViewById(R.id.main_toobar);
//        toolbar.setTitle(title);
//        setSupportActionBar(toolbar);
//        toolbar.setOnMenuItemClickListener(onMenuItemClickListener);
//    }


//    初始化BottomNavigationBar
    public void initBottomNavigationBar() {
        bottomNavigationBar = (BottomNavigationBar)findViewById(R.id.main_bottomNavBar);
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        bottomNavigationBar.addItem(new BottomNavigationItem(R.mipmap.icon,"会员")).setActiveColor(R.color.colorPrimary)
                .addItem(new BottomNavigationItem(R.mipmap.icon,"消息")).setActiveColor(R.color.colorPrimary)
                .addItem(new BottomNavigationItem(R.mipmap.icon,"课程")).setActiveColor(R.color.colorPrimary)
                .addItem(new BottomNavigationItem(R.mipmap.icon,"个人")).setActiveColor(R.color.colorPrimary)
                .setFirstSelectedPosition(0).initialise();
    }

    public ArrayList<Fragment> getFragments() {
        fragmentList = new ArrayList<>();
        fragmentList.add(MemberFragment.newInstance("会员","会员"));
        fragmentList.add(MessageFragment.newInstance("消息","消息"));
        fragmentList.add(CourseFragment.newInstance("课程","课程"));
        fragmentList.add(PersonFragment.newInstance("个人","个人"));
        return fragmentList;
    }

    public void setDefaultFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_frameLayout,MemberFragment.newInstance("会员","会员"));
    }

    @Override
    public void onTabSelected(int i) {
        if (fragmentList!= null) {
            if (i < fragmentList.size()) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment fragment = fragmentList.get(i);
                if (fragment.isAdded()) {
                    fragmentTransaction.replace(R.id.main_frameLayout,fragment);
                } else {
                    fragmentTransaction.add(R.id.main_frameLayout,fragment);
                }
                fragmentTransaction.commitAllowingStateLoss();
            }
        }
    }

    @Override
    public void onTabUnselected(int i) {
        if (fragmentList != null) {
            if (i < fragmentList.size()) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment fragment = fragmentList.get(i);
                fragmentTransaction.remove(fragment);
                fragmentTransaction.commitAllowingStateLoss();
            }
        }
    }

    @Override
    public void onTabReselected(int i) {

    }

}
