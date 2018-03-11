package xyz.institutionmanage.sailfish.Member.CoursePlan;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import xyz.institutionmanage.sailfish.R;
import xyz.institutionmanage.sailfish.Shopmember.Adapter.CourseViewPagerAdapter;
import xyz.institutionmanage.sailfish.Util.BaseActivity;

public class MCoursePlanDetailActivity
        extends BaseActivity
        implements MCoursePlanDetailInfoFragment.OnFragmentInteractionListener,
        MCoursePlanDetailTeacherFragment.OnFragmentInteractionListener{

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private CourseViewPagerAdapter adapter;
    private List<Fragment> fragments = new ArrayList<>();
    private String cp_id;
    private String c_name;
    private String start_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mcourse_plan_detail);

        initDatas();
        initViews();
    }

    private void initDatas(){
        cp_id = getIntent().getStringExtra("cp_id");
        c_name = getIntent().getStringExtra("c_name");
        start_time = getIntent().getStringExtra("start_time");
        start_time = start_time.substring(0,start_time.length()-5);
        fragments.add(MCoursePlanDetailInfoFragment.newInstance(cp_id));
        fragments.add(MCoursePlanDetailTeacherFragment.newInstance(cp_id));
    }

    private void initViews() {
        toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        toolbar.setTitle(c_name);
        toolbar.setSubtitle(start_time);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tabLayout = (TabLayout)findViewById(R.id.tabbar_a_mcourse_plan_detail);
        viewPager = (ViewPager)findViewById(R.id.vp_a_mcourse_plan_detail);
        adapter = new CourseViewPagerAdapter(getSupportFragmentManager(),new String[] {"基本信息","带课教师"},fragments);
        if (tabLayout != null) {
            tabLayout.addTab(tabLayout.newTab().setText("基本信息"));
            tabLayout.addTab(tabLayout.newTab().setText("带课教师"));
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
        if (viewPager.getAdapter() == null) {
            viewPager.setAdapter(adapter);
        }
        tabLayout.setupWithViewPager(viewPager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                MCoursePlanDetailActivity.this.finish();
                break;
            default:break;
        }
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
