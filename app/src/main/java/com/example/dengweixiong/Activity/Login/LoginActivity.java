package com.example.dengweixiong.Activity.Login;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.dengweixiong.Activity.MainActivity;
import com.example.dengweixiong.Adapter.CourseViewPagerAdapter;
import com.example.dengweixiong.Fragment.CoursePlanFragment;
import com.example.dengweixiong.Fragment.RegistFragment;
import com.example.dengweixiong.Fragment.SignInFragment;
import com.example.dengweixiong.Util.BaseActivity;
import com.example.dengweixiong.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity
        extends BaseActivity
        implements TabLayout.OnTabSelectedListener,
            RegistFragment.OnFragmentInteractionListener,
            SignInFragment.OnFragmentInteractionListener {

    private TabLayout tabLayout;
    private static final String TOOLBAR_TITLE = "注册新机构与登录";
    private ViewPager viewPager;
    private List<Fragment> fragments = new ArrayList<>();
    private CourseViewPagerAdapter adapter_vp;
    private String [] str_title = {"注册新机构","登录"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        initToolbar();
        initTabLayout();
        initViewPager();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_a_login);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(TOOLBAR_TITLE);
    }

    private void initTabLayout() {
        tabLayout = (TabLayout)findViewById(R.id.tl_a_login);
        tabLayout.addTab(tabLayout.newTab().setText("注册新机构"));
        tabLayout.addTab(tabLayout.newTab().setText("登录"));
        tabLayout.addOnTabSelectedListener(this);

    }

    private void initViewPager() {
        viewPager = (ViewPager)findViewById(R.id.vp_a_login);
        adapter_vp = new CourseViewPagerAdapter(getSupportFragmentManager(),str_title,getFragments());
        viewPager.setAdapter(adapter_vp);
        tabLayout.setupWithViewPager(viewPager);
    }

    private List<Fragment> getFragments() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(RegistFragment.newInstance("注册新机构"));
        fragments.add(SignInFragment.newInstance("登录"));
        return fragments;
    }

    //TABLAYOUT处理
    @Override
    public void onTabSelected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
