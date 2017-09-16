package com.example.dengweixiong.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.example.dengweixiong.Activity.Course.AddNewCourseActivity;
import com.example.dengweixiong.Activity.Course.AddNewCoursePlanActivity;
import com.example.dengweixiong.Activity.MainActivity;
import com.example.dengweixiong.Adapter.CourseViewPagerAdapter;
import com.example.dengweixiong.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class CourseMainFragment
        extends Fragment
        implements
                Toolbar.OnMenuItemClickListener,
                TabLayout.OnTabSelectedListener{
    private static final String ARG_PARAM1 = "param1";

    private String mParam1;
    private static final String TAG = "Nothing to Tell";
    private OnFragmentInteractionListener mListener;
    private List<Fragment> fragments = new ArrayList<>();
    private View view1,view2;
    private List<View> viewList = new ArrayList<>();
    private String [] str_title = {"课程列表","排课列表"};
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private CourseViewPagerAdapter adapter_vp;

    public CourseMainFragment() {
    }

    public static CourseMainFragment newInstance(String param1) {
        CourseMainFragment fragment = new CourseMainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course,container,false);
        MainActivity m = (MainActivity)getActivity();
        initView(m,view);
        return view;
    }

    private void initView(MainActivity m,View view) {
        initToolbar(m);
        initTabLayout(view);
        initViewPager(view);
    }

    private void initToolbar(MainActivity m) {
        m.getSupportActionBar().setTitle("课程");
        setHasOptionsMenu(true);
    }

    private void initTabLayout(View view) {
        tabLayout = (TabLayout)view.findViewById(R.id.tl_f_courseMain);
        tabLayout.addTab(tabLayout.newTab().setText("课程列表"));
        tabLayout.addTab(tabLayout.newTab().setText("排课列表"));
        tabLayout.addOnTabSelectedListener(this);

    }

    private void initViewPager(View view) {
        viewPager = (ViewPager) view.findViewById(R.id.vp_f_courseMain);
        adapter_vp = new CourseViewPagerAdapter(getChildFragmentManager(),str_title,getFragments());
        viewPager.setAdapter(adapter_vp);
        tabLayout.setupWithViewPager(viewPager);
    }

    private List<Fragment> getFragments() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(com.example.dengweixiong.Fragment.CourseListFragment.newInstance("课程列表"));
        fragments.add(CoursePlanFragment.newInstance("排课列表"));
        return fragments;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
    //处理Toolbar Menu事件
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_fragment_course,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MainActivity mainActivity = (MainActivity)getActivity();
        switch (item.getItemId()) {
            case R.id.menu_fragment_course_add_new_course:
                Intent intent = new Intent(mainActivity, AddNewCourseActivity.class);
                mainActivity.startActivity(intent);
                break;
            case R.id.menu_fragment_course_add_new_course_plan:
                Intent intent1 = new Intent(mainActivity, AddNewCoursePlanActivity.class);
                mainActivity.startActivity(intent1);
                break;
            default:
                break;
        }
        return true;
    }
//处理TabLayout的点击事件
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
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

}
