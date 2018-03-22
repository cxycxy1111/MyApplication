package xyz.institutionmanage.sailfish.Member.Main;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import xyz.institutionmanage.sailfish.Adapter.MainActivityPagerAdapter;
import xyz.institutionmanage.sailfish.R;
import xyz.institutionmanage.sailfish.Util.BaseActivity;

public class MemberMainActivity
        extends BaseActivity
        implements MBookFragment.OnFragmentInteractionListener,
            MDiscoveryFragment.OnFragmentInteractionListener,
            MMyFragment.OnFragmentInteractionListener,
            BottomNavigationView.OnNavigationItemSelectedListener,
            ViewPager.OnPageChangeListener{

    private ViewPager viewPager;
    private Toolbar toolbar;
    private MainActivityPagerAdapter adapter;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private MenuItem prevMenuItem;
    private int selected_position = 0;
    private BottomNavigationView navigation;
    private String title[] = new String[] {"我的预订","发现","我的"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_main);
        initViews();
        initViewPager();
    }


    private void initViews() {
        if (navigation == null) {
            navigation = (BottomNavigationView) findViewById(R.id.navigation);
        }
        navigation.setOnNavigationItemSelectedListener(this);
        if (viewPager == null) {
            viewPager = (ViewPager)findViewById(R.id.activity_m_main_vp);
        }
        toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        setSupportActionBar(toolbar);
        if (selected_position == 0) {
            getSupportActionBar().setTitle("我的预订");
        }
    }

    private ArrayList<Fragment> initFragments() {
        if (fragments != null && fragments.size() == 0) {
            MBookFragment mBookFragment = MBookFragment.newInstance("我的预订");
            MDiscoveryFragment mDiscoveryFragment = MDiscoveryFragment.newInstance("发现");
            MMyFragment mMyFragment = MMyFragment.newInstance("我");
            fragments.add(mBookFragment);
            fragments.add(mDiscoveryFragment);
            fragments.add(mMyFragment);
        }
        return fragments;
    }

    private void initViewPager(){
        if (adapter == null) {
            adapter = new MainActivityPagerAdapter(getSupportFragmentManager(),initFragments());
        }
        if (viewPager.getAdapter() == null) {
            viewPager.setAdapter(adapter);
        }
        viewPager.setOffscreenPageLimit(3);
        viewPager.addOnPageChangeListener(this);
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    //NavigationView点击事件
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                viewPager.setCurrentItem(0);
                selected_position = 0;
                getSupportActionBar().setTitle("我的预订");
                return true;
            case R.id.navigation_dashboard:
                viewPager.setCurrentItem(1);
                selected_position = 1;
                getSupportActionBar().setTitle("发现");
                return true;
            case R.id.navigation_notifications:
                viewPager.setCurrentItem(2);
                selected_position = 2;
                getSupportActionBar().setTitle("我");
                return true;
        }
        return false;
    }

    //ViewPager事件
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        getSupportActionBar().setTitle(title[position]);
        selected_position = position;
        if(prevMenuItem != null) {
            prevMenuItem.setChecked(false);
        }else {
            navigation.getMenu().getItem(0).setChecked(false);
        }
        navigation.getMenu().getItem(position).setChecked(true);
        prevMenuItem = navigation.getMenu().getItem(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*
        getMenuInflater().inflate(R.menu.menu_m_main,menu);
        return true;
         */
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.sumbit_m_main:
                Toast.makeText(this, "nihao", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return false;
        }
        */
        return false;
    }
}
