package com.example.dengweixiong.Shopmember.Course.CoursePlan;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.dengweixiong.Shopmember.Adapter.CourseViewPagerAdapter;
import com.example.dengweixiong.Util.BaseActivity;
import com.example.dengweixiong.Util.MethodTool;
import com.example.dengweixiong.Util.NetUtil;
import com.example.dengweixiong.Util.Ref;
import com.example.dengweixiong.myapplication.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CoursePlanDetailActivity
        extends BaseActivity
        implements CoursePlanDetailBasicFragment.OnFragmentInteractionListener,
        CoursePlanDetailTeacherFragment.OnFragmentInteractionListener{

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private CourseViewPagerAdapter adapter;
    private FragmentManager fm = this.getSupportFragmentManager();
    private String str_sm_id,str_s_id,str_course_name,str_time,str_cp_id;
    private String[] strs_title = new String[]{"排课详情","教授教师"};
    private List<Fragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_plan_detail);
        initData();
        initToolBar();
        initTabLayout();
        initViewPager();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    private void initData(){
        str_s_id = MethodTool.getSharePreferenceValue(this,"sasm","s_id",2);
        str_sm_id = MethodTool.getSharePreferenceValue(this,"sasm","sm_id",2);
        Intent intent = getIntent();
        str_course_name = intent.getStringExtra("course_name");
        str_time = intent.getStringExtra("time");
        str_cp_id = intent.getStringExtra("cp_id");
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(str_course_name);
        getSupportActionBar().setSubtitle(str_time);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initTabLayout() {
        tabLayout = (TabLayout)findViewById(R.id.tl_a_courseplan_detail);
        tabLayout.addTab(tabLayout.newTab().setText("排课详情"));
        tabLayout.addTab(tabLayout.newTab().setText("教授教师"));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void initViewPager() {
        viewPager = (ViewPager)findViewById(R.id.vp_a_courseplan_detail);
        initFragments();
        adapter = new CourseViewPagerAdapter(fm,strs_title,fragments);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initFragments() {
        Map<String,String> map = new HashMap<>();
        map.put("s_id",str_s_id);
        map.put("sm_id",str_sm_id);
        map.put("course_name",str_course_name);
        map.put("cp_id",str_cp_id);
        fragments.add(CoursePlanDetailBasicFragment.newInstance(map));
        fragments.add(CoursePlanDetailTeacherFragment.newInstance(map));
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();break;
            default:break;
        }
        return true;
    }
}
