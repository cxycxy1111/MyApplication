package com.example.dengweixiong.Activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.example.dengweixiong.Activity.Course.CourseListFragment;
import com.example.dengweixiong.Activity.Course.CourseMainFragment;
import com.example.dengweixiong.Activity.Course.CoursePlanFragment;
import com.example.dengweixiong.Activity.Member.MemberFragment;
import com.example.dengweixiong.Activity.Profile.PersonFragment;
import com.example.dengweixiong.Util.BaseActivity;
import com.example.dengweixiong.myapplication.R;
import java.util.ArrayList;
import java.util.List;

public class MainActivity
        extends
        BaseActivity
        implements BottomNavigationBar.OnTabSelectedListener,
            MemberFragment.OnFragmentInteractionListener,
            CourseMainFragment.OnFragmentInteractionListener,
            PersonFragment.OnFragmentInteractionListener,
            Toolbar.OnMenuItemClickListener,
            MenuItem.OnActionExpandListener,
            CourseListFragment.OnFragmentInteractionListener,
            CoursePlanFragment.OnFragmentInteractionListener{


    int lastPosition = 0;
    private List<Fragment> fragments = new ArrayList<>();
    private BottomNavigationBar bottomNavigationBar;
    private Toolbar toolbar;
    private static final String TAG = "current is:";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolBar();
        initBottomNavigationBar();
        delWithIntent();
    }

    //toolbar初始化

    private void initToolBar() {
        toolbar = (Toolbar)findViewById(R.id.toolbar_main);
        toolbar.setOnMenuItemClickListener(this);
        setSupportActionBar(toolbar);
    }

//BottomNavigationBar初始化
    private void initBottomNavigationBar() {
        bottomNavigationBar = (BottomNavigationBar)findViewById(R.id.bottom_navigation_bar);
        bottomNavigationBar.setTabSelectedListener(this);
        bottomNavigationBar.clearAll();
        bottomNavigationBar.setBackgroundResource(R.color.colorPrimary);
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        bottomNavigationBar.addItem(
                new BottomNavigationItem(R.mipmap.icon,"会员").setActiveColorResource(R.color.colorPrimaryDark))
                .addItem(new BottomNavigationItem(R.mipmap.icon,"课程").setActiveColorResource(R.color.colorPrimaryDark))
                .addItem(new BottomNavigationItem(R.mipmap.icon,"个人中心").setActiveColorResource(R.color.colorPrimaryDark))
                .setFirstSelectedPosition(lastPosition).initialise();
        fragments = getFragments();
        setDefaultFragment();
    }

    private List<Fragment> getFragments() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(MemberFragment.newInstance("会员"));
        fragments.add(CourseMainFragment.newInstance("课程"));
        fragments.add(PersonFragment.newInstance("个人中心"));
        return fragments;
    }

    private void setDefaultFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        MemberFragment memberFragment = MemberFragment.newInstance("会员");
        ft.add(R.id.fragment,memberFragment);
        ft.commit();
    }

    //BottomNavigationBar事件
    @Override
    public void onTabSelected(int i) {
        FragmentManager fm = this.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment,fragments.get(i));
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onTabUnselected(int i) {

    }

    @Override
    public void onTabReselected(int i) {

    }

    //Fragment事件处理
    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    //Toolbar Menu处理部分
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        MenuItem menuItem = menu.findItem(R.id.search_main);
        SearchView searchView = (SearchView)MenuItemCompat.getActionView(menuItem);
        MenuItemCompat.setOnActionExpandListener(menuItem,expandListener);
        return true;
    }

    MenuItemCompat.OnActionExpandListener expandListener = new MenuItemCompat.OnActionExpandListener() {
        @Override
        public boolean onMenuItemActionExpand(MenuItem item) {
            bottomNavigationBar.hide(true);
            return true;
        }

        @Override
        public boolean onMenuItemActionCollapse(MenuItem item) {
            bottomNavigationBar.show(true);
            return true;
        }
    };

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return false;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        return false;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

    private void delWithIntent() {

    }

}