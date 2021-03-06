package xyz.instmgr.sailfish.Shopmember.Main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.*;
import com.alfred.alfredtools.BaseFragment;
import xyz.instmgr.sailfish.Adapter.ViewPagerAdapter;
import xyz.instmgr.sailfish.R;
import xyz.instmgr.sailfish.Shopmember.Course.Course.AddCourseActivity;
import xyz.instmgr.sailfish.Shopmember.Course.CoursePlan.AddCoursePlanActivity;
import xyz.instmgr.sailfish.Shopmember.Course.CoursePlan.Batch.AddCoursePlanBatchActivity;

import java.util.ArrayList;
import java.util.List;

public class CourseMainFragment
        extends BaseFragment
        implements Toolbar.OnMenuItemClickListener,
        TabLayout.OnTabSelectedListener,
        CourseListFragment.OnFragmentInteractionListener,
        CoursePlanListFragment.OnFragmentInteractionListener,
        MyCoursePlanListFragment.OnFragmentInteractionListener{
    private static final String ARG_PARAM1 = "param1";

    private String mParam1;
    private static final String TAG = "CourseMainFragment";
    private OnFragmentInteractionListener mListener;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter_vp;
    private static final int REQUEST_ADD_NEW_COURSE = 1;
    private List<Fragment> fragments = new ArrayList<>();
    private View view1,view2;
    private List<View> viewList = new ArrayList<>();
    private String [] str_title = {"课程列表","排课列表","我的排课"};
    private TabLayout tabLayout = null;
    private FragmentManager manager;
    private static ShopmemberMainActivity context;

    public CourseMainFragment() {
    }

    public static CourseMainFragment newInstance(String param1,ShopmemberMainActivity shopmemberMainActivity) {
        context = shopmemberMainActivity;
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
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main_course,container,false);
        initView(view);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    private void initView(View view) {
        //initToolbar();
        initFragments();
        initFragmentManager();
        initTabLayout(view);
        initViewPager(view);
    }

    private void initToolbar() {
        context.getSupportActionBar().setTitle("课程");
    }

    private void initFragments() {
        fragments.add(CourseListFragment.newInstance("课程列表"));
        fragments.add(CoursePlanListFragment.newInstance("排课列表"));
        fragments.add(MyCoursePlanListFragment.newInstance("我的排课"));
    }

    private void initFragmentManager() {
        manager = getChildFragmentManager();
    }

    private void initTabLayout(View view) {
        if (tabLayout == null) {
            tabLayout = (TabLayout)view.findViewById(R.id.tl_f_courseMain);
            tabLayout.addTab(tabLayout.newTab().setText("课程列表"));
            tabLayout.addTab(tabLayout.newTab().setText("排课列表"));
            tabLayout.addTab(tabLayout.newTab().setText("我的排课"));
            tabLayout.addOnTabSelectedListener(this);
        }
    }

    private void initViewPager(View view) {
        if (viewPager == null) {
            viewPager = (ViewPager) view.findViewById(R.id.vp_f_courseMain);
        }
        if (adapter_vp == null) {
            adapter_vp = new ViewPagerAdapter(manager, str_title, fragments);
        }
        if (viewPager.getAdapter() == null) {
            viewPager.setAdapter(adapter_vp);
            tabLayout.setupWithViewPager(viewPager,true);
        }
        viewPager.setOffscreenPageLimit(3);
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

    @Override
    public void onFragmentInteraction(Uri uri) {

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


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_main,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.add_new_course_main:
                intent = new Intent(getActivity(),AddCourseActivity.class);
                startActivityForResult(intent,this.REQUEST_ADD_NEW_COURSE);
                return true;
            case R.id.add_new_courseplan_main:
                intent = new Intent(getActivity(),AddCoursePlanActivity.class);
                startActivity(intent);
                return true;
            case R.id.add_new_courseplan_batch_main:
                intent = new Intent(getActivity(), AddCoursePlanBatchActivity.class);
                startActivity(intent);
                return true;
            default:return true;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
