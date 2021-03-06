package xyz.instmgr.sailfish.Member.Discovery.Course;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.alfred.alfredtools.BaseActivity;
import xyz.instmgr.sailfish.Adapter.ViewPagerAdapter;
import xyz.instmgr.sailfish.R;

import java.util.ArrayList;

public class MCourseDetailActivity
        extends BaseActivity
        implements MCourseDetailInfoFragment.OnFragmentInteractionListener,
        MCourseDetailSupportCardFragment.OnFragmentInteractionListener,TabLayout.OnTabSelectedListener{

    private long c_id;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_course_detail);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        setSupportActionBar(toolbar);
        fragmentManager = this.getSupportFragmentManager();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initData();
        initViews();

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    private void initData() {
        Intent intent = getIntent();
        c_id = intent.getLongExtra("c_id",0);
        String c_name = intent.getStringExtra("c_name");
        getSupportActionBar().setTitle(c_name);
    }

    private void initViews() {
        toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        setSupportActionBar(toolbar);
        tabLayout = (TabLayout)findViewById(R.id.tl_a_m_course_detail);
        viewPager = (ViewPager)findViewById(R.id.vp_a_m_course_detail);
        if (tabLayout != null) {
            tabLayout.addTab(tabLayout.newTab().setText("课程详情"));
            tabLayout.addTab(tabLayout.newTab().setText("所支持卡列表"));
            tabLayout.addOnTabSelectedListener(this);
        }
        String [] titles = new String[]{"课程详情","所支持卡列表"};
        adapter = new ViewPagerAdapter(fragmentManager,titles,initFragments());
        if (viewPager.getAdapter() == null) {
            viewPager.setAdapter(adapter);
        }
        tabLayout.setupWithViewPager(viewPager);
    }

    private ArrayList<Fragment> initFragments() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(MCourseDetailInfoFragment.newInstance(c_id));
        fragments.add(MCourseDetailSupportCardFragment.newInstance(c_id));
        return fragments;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            default:break;
        }
        return true;
    }
}
