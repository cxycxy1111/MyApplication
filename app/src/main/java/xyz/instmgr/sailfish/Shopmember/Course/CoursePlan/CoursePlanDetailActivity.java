package xyz.instmgr.sailfish.Shopmember.Course.CoursePlan;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.alfred.alfredtools.*;
import xyz.instmgr.sailfish.Adapter.ViewPagerAdapter;
import xyz.instmgr.sailfish.R;
import xyz.instmgr.sailfish.Util.Ref;
import xyz.instmgr.sailfish.Util.SharePreferenceManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoursePlanDetailActivity
        extends BaseActivity
        implements CoursePlanDetailBasicFragment.OnFragmentInteractionListener,
        CoursePlanDetailTeacherFragment.OnFragmentInteractionListener,HttpResultListener {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
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
        getMenuInflater().inflate(R.menu.menu_courseplan_detail,menu);
        return true;
    }

    private void initData(){
        str_s_id = SharePreferenceManager.getSharePreferenceValue(this,"sasm","s_id",2);
        str_sm_id = SharePreferenceManager.getSharePreferenceValue(this,"sasm","sm_id",2);
        Intent intent = getIntent();
        str_course_name = intent.getStringExtra("course_name");
        str_time = intent.getStringExtra("time");
        str_cp_id = intent.getStringExtra("cp_id");
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(str_course_name);
        String subtitle = str_time;
        subtitle = subtitle.substring(0,str_time.length()-5);
        getSupportActionBar().setSubtitle(subtitle);
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
        adapter = new ViewPagerAdapter(fm,strs_title,fragments);
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
            case R.id.delete_courseplan_detail:
                showRemoveAlert();
                break;
            case R.id.view_book_and_attend_courseplan_detail:
                Intent intent = new Intent(CoursePlanDetailActivity.this,CoursePlanBookAndAttendActivity.class);
                intent.putExtra("cp_id",str_cp_id);
                intent.putExtra("c_name",str_course_name);
                startActivity(intent);
            default:break;
        }
        return true;
    }

    private void showRemoveAlert() {
        final AlertDialog.Builder builder= new AlertDialog.Builder(CoursePlanDetailActivity.this);
        builder.setTitle("提示");
        builder.setMessage("是否确认删除该排课？");
        builder.setCancelable(false);
        builder.setPositiveButton("不删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeCoursePlan();
            }
        });
        builder.show();
    }

    private void removeCoursePlan() {
        String url = "/CoursePlanRemove?id=" + str_cp_id;
        HttpCallback callback = new HttpCallback(this,this,0);
        NetUtil.reqSendGet(this,url,callback);
    }

    @Override
    public void onRespStatus(String body, int source) {
        NetRespStatType respStatType = NetRespStatType.dealWithRespStat(body);
        switch (respStatType) {
            case NSR:
                ViewHandler.toastShow(CoursePlanDetailActivity.this,Ref.OP_NSR);
                CoursePlanDetailActivity.this.finish();
                break;
            case NST_NOT_MATCH:
                ViewHandler.toastShow(CoursePlanDetailActivity.this,Ref.OP_INST_NOT_MATCH);
                CoursePlanDetailActivity.this.finish();
                break;
            case FAIL:
                ViewHandler.toastShow(CoursePlanDetailActivity.this,Ref.OP_DELETE_FAIL);
                break;
            case SUCCESS:
                ViewHandler.toastShow(CoursePlanDetailActivity.this,Ref.OP_DELETE_SUCCESS);
                CoursePlanDetailActivity.this.finish();
                break;
            case STATUS_AUTHORIZE_FAIL:
                ViewHandler.exitAcitivityDueToAuthorizeFail(CoursePlanDetailActivity.this);
                break;
            default:break;
        }
    }

    @Override
    public void onRespMapList(String body, int source) throws IOException {

    }

    @Override
    public void onRespError(int source) {

    }

    @Override
    public void onReqFailure(Object object, int source) {

    }

    @Override
    public void onRespSessionExpired(int source) {
        ViewHandler.alertShowAndExitApp(CoursePlanDetailActivity.this);
    }
}
