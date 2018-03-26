package xyz.institutionmanage.sailfish.Shopmember.Course.CoursePlan;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.institutionmanage.sailfish.Adapter.ViewPagerAdapter;
import xyz.institutionmanage.sailfish.R;
import xyz.institutionmanage.sailfish.Util.BaseActivity;

public class CoursePlanBookAndAttendActivity extends BaseActivity
        implements CoursePlanBookFragment.OnFragmentInteractionListener, CoursePlanAttendanceFragment.OnFragmentInteractionListener{

    private String str_cp_id,str_c_name;
    private String[] strs_title = new String[]{"已预订","已签到"};
    private ViewPagerAdapter adapter;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FragmentManager fm = this.getSupportFragmentManager();
    private List<Fragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_plan_book_and_attend);
        initDataFromPreviousActivity();
        initViewComponents();
    }

    private void initDataFromPreviousActivity() {
        str_c_name = getIntent().getStringExtra("c_name");
        str_cp_id = getIntent().getStringExtra("cp_id");
    }

    private void initViewComponents() {
        toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        tabLayout = (TabLayout)findViewById(R.id.tl_a_course_plan_book_adn_attend);
        viewPager = (ViewPager)findViewById(R.id.vp_a_course_plan_book_adn_attend);

        toolbar.setTitle(str_c_name);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tabLayout.addTab(tabLayout.newTab().setText("预订会员"));
        tabLayout.addTab(tabLayout.newTab().setText("签到会员"));
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

        initFragments();
        adapter = new ViewPagerAdapter(fm,strs_title,fragments);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(0);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initFragments() {
        Map<String,String> map = new HashMap<>();
        map.put("cp_id",str_cp_id);
        fragments.add(CoursePlanBookFragment.newInstance(str_cp_id));
        fragments.add(CoursePlanAttendanceFragment.newInstance(str_cp_id));
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
                CoursePlanBookAndAttendActivity.this.finish();
                break;
            default:break;
        }
        return true;
    }
}
