package com.example.dengweixiong.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.dengweixiong.Activity.Course.AddNewCourseActivity;
import com.example.dengweixiong.Activity.Course.AddNewCoursePlanActivity;
import com.example.dengweixiong.Activity.Course.CourseListFragment;
import com.example.dengweixiong.Activity.Course.CourseMainFragment;
import com.example.dengweixiong.Activity.Course.CoursePlanFragment;
import com.example.dengweixiong.Activity.Member.MemberFragment;
import com.example.dengweixiong.Activity.Message.MessageListActivity;
import com.example.dengweixiong.Activity.Profile.PersonFragment;
import com.example.dengweixiong.Adapter.CourseViewPagerAdapter;
import com.example.dengweixiong.Adapter.MainActivityPagerAdapter;
import com.example.dengweixiong.Util.BaseActivity;
import com.example.dengweixiong.myapplication.R;
import java.util.ArrayList;
import java.util.List;

public class MainActivity
        extends
        BaseActivity
        implements MemberFragment.OnFragmentInteractionListener,
            CourseMainFragment.OnFragmentInteractionListener,
            PersonFragment.OnFragmentInteractionListener,
            CourseListFragment.OnFragmentInteractionListener,
            CoursePlanFragment.OnFragmentInteractionListener{

    private String [] title = new String[] {"会员","课程","个人中心"};
    private List<Fragment> fragments = new ArrayList<>();
    private Toolbar toolbar;
    private ViewPager viewPager;
    private BottomNavigationView bottomNavigationView;
    private MainActivityPagerAdapter adapter;
    private static final int REQUEST_ADD_NEW_COURSE = 1;
    private FragmentManager fm = this.getSupportFragmentManager();
    private int selected_position;
    private MenuItem prevMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolBar();
        initViewPager();
        initBottomNavigationBar();
    }

    //toolbar初始化
    private void initToolBar() {
        toolbar = (Toolbar)findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title[0]);
    }

    //BottomNavigationBar初始化
    private void initBottomNavigationBar() {
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bnb_a_main);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.member_a_main:
                        viewPager.setCurrentItem(0);
 //                       getSupportActionBar().setTitle(title[0]);
                        selected_position = 0;
                        break;
                    case R.id.course_a_main:
                        viewPager.setCurrentItem(1);
 //                       getSupportActionBar().setTitle(title[1]);
                        selected_position = 1;
                        break;
                    case R.id.person_a_main:
                        viewPager.setCurrentItem(2);
 //                       getSupportActionBar().setTitle(title[2]);
                        selected_position = 2;
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    private List<Fragment> initFragments() {
        if (fragments == null) {
            fragments = new ArrayList<>();
        }
        if (fragments.size() == 0) {
            fragments.add(MemberFragment.newInstance("会员",this));
            fragments.add(CourseMainFragment.newInstance("课程",this));
            fragments.add(PersonFragment.newInstance("个人中心",this));
        }
        return fragments;
    }

    private void initViewPager() {
        fragments = initFragments();
        if (viewPager == null) {
            viewPager = (ViewPager)findViewById(R.id.vp_a_main);
        }
        if (adapter == null) {
            adapter = new MainActivityPagerAdapter(fm,fragments);
        }
        if (viewPager.getAdapter() == null) {
            viewPager.setAdapter(adapter);
        }
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
 //               getSupportActionBar().setTitle(title[position]);
                selected_position = position;
                if(prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                }else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.message_main:
                intent = new Intent(MainActivity.this, MessageListActivity.class);
                startActivity(intent);
                break;
            case R.id.add_new_course_main:
                intent = new Intent(MainActivity.this,AddNewCourseActivity.class);
                startActivityForResult(intent,this.REQUEST_ADD_NEW_COURSE);
                break;
            case R.id.add_new_courseplan_main:
                intent = new Intent(MainActivity.this,AddNewCoursePlanActivity.class);
                startActivity(intent);
                break;
            default:break;
        }
        super.onOptionsItemSelected(item);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                break;
            default:break;
        }
    }

    //Fragment事件处理
    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}