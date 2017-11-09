package com.example.dengweixiong.Activity;

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
import android.view.MenuItem;
import com.example.dengweixiong.Activity.Course.CourseListFragment;
import com.example.dengweixiong.Activity.Course.CourseMainFragment;
import com.example.dengweixiong.Activity.Course.CoursePlanFragment;
import com.example.dengweixiong.Activity.Member.MemberFragment;
import com.example.dengweixiong.Activity.Profile.PersonFragment;
import com.example.dengweixiong.Adapter.CourseViewPagerAdapter;
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

    private String [] title = new String[] {"会员","课程","我的"};
    private List<Fragment> fragments = new ArrayList<>();
    private Toolbar toolbar;
    private ViewPager viewPager;
    private BottomNavigationView bottomNavigationView;
    private CourseViewPagerAdapter adapter;
    FragmentManager fm = this.getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolBar();
        initViewPager();
        initBottomNavigationBar();
        delWithIntent();
    }

//toolbar初始化
    private void initToolBar() {
        toolbar = (Toolbar)findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        toolbar.setTitle("会员");
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
                        toolbar.setTitle("会员");
                        break;
                    case R.id.course_a_main:
                        viewPager.setCurrentItem(1);
                        toolbar.setTitle("课程");
                        break;
                    case R.id.person_a_main:
                        viewPager.setCurrentItem(2);
                        toolbar.setTitle("个人中心");
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    private List<Fragment> initFragments() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(MemberFragment.newInstance("会员"));
        fragments.add(CourseMainFragment.newInstance("课程"));
        fragments.add(PersonFragment.newInstance("个人中心"));
        return fragments;
    }

    private void initViewPager() {
        fragments = initFragments();
        if (viewPager == null) {
            viewPager = (ViewPager)findViewById(R.id.vp_a_main);
        }
        if (adapter == null) {
            adapter = new CourseViewPagerAdapter(fm,title,fragments);
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
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    //Fragment事件处理
    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    //Toolbar Menu处理部分
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    private void delWithIntent() {

    }

}