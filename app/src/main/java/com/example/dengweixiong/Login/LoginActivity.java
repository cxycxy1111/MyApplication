package com.example.dengweixiong.Login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.dengweixiong.Shopmember.Adapter.CourseViewPagerAdapter;
import com.example.dengweixiong.Shopmember.Main.ShopmemberMainActivity;
import com.example.dengweixiong.Util.BaseActivity;
import com.example.dengweixiong.Util.Enum.EnumRespStatType;
import com.example.dengweixiong.Util.Enum.EnumRespType;
import com.example.dengweixiong.Util.JsonHandler;
import com.example.dengweixiong.Util.MethodTool;
import com.example.dengweixiong.Util.NetUtil;
import com.example.dengweixiong.Util.Ref;
import com.example.dengweixiong.Util.SharePreferenceManager;
import com.example.dengweixiong.myapplication.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity
        extends BaseActivity
        implements TabLayout.OnTabSelectedListener,
        RegistShopFragment.OnFragmentInteractionListener,
        SignInFragment.OnFragmentInteractionListener {

    private String sm_type;
    private String sm_id,s_id;
    private String[] keys = new String[] {"id","name","type","user_name"};
    private Map<String,String> map = new HashMap<>();
    private TabLayout tabLayout;
    private Context context;
    private ViewPager viewPager;
    private List<Fragment> fragments = new ArrayList<>();
    private FragmentManager manager;
    private CourseViewPagerAdapter adapter_vp;
    private String [] str_title = {"注册新机构","登录"};
    private String login_name,password;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initFragments();
        initFragmentManager();
        initLoginnameAndPassword();
        initView();
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

    private void initFragmentManager() {
        manager = getSupportFragmentManager();
    }

    private void initLoginnameAndPassword() {
        sm_type = SharePreferenceManager.getSharePreferenceValue(this,"sasm","sm_type",1);
        s_id = SharePreferenceManager.getSharePreferenceValue(this,"sasm","s_id",2);
        sm_id = SharePreferenceManager.getSharePreferenceValue(this,"sasm","sm_id",2);
        login_name = SharePreferenceManager.getSharePreferenceValue(this,"login_data","login_name",3);
        password = SharePreferenceManager.getSharePreferenceValue(this,"login_data","password",3);

    }

    private void initView() {
        initToolbar();
        initTabLayout();
        initViewPager();
        createProgressBar();
        checkInfoIsPrepared();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_a_login);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.title_a_login);
    }

    private void initTabLayout() {
        tabLayout = (TabLayout)findViewById(R.id.tl_a_login);
        tabLayout.addTab(tabLayout.newTab().setText("注册新机构"));
        tabLayout.addTab(tabLayout.newTab().setText("登录"));
        tabLayout.addOnTabSelectedListener(this);
    }

    private void initViewPager() {
        viewPager = (ViewPager)findViewById(R.id.vp_a_login);
        adapter_vp = new CourseViewPagerAdapter(manager,str_title,fragments);
        viewPager.setAdapter(adapter_vp);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initFragments() {
        fragments.add(RegistShopFragment.newInstance("注册新机构"));
        fragments.add(SignInFragment.newInstance("登录"));
    }

    /**
     * 创建进度条
     */

    private void createProgressBar(){
        context=this;
        FrameLayout rootFrameLayout = (FrameLayout) findViewById(android.R.id.content);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity= Gravity.CENTER;
        progressBar=new ProgressBar(context);
        progressBar.setLayoutParams(layoutParams);
        progressBar.setVisibility(View.VISIBLE);
        rootFrameLayout.addView(progressBar);
    }

    private void checkInfoIsPrepared() {
        if (sm_type.equals("") || s_id.equals("") ||sm_id.equals("") || login_name.equals("")||password.equals("")) {
            Toast.makeText(this,"自动登录失败",Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }else {
            autoLogin();
        }
    }

    private void autoLogin() {
        String url = "/ShopMemberLogin?user_name=" + login_name + "&password=" + password;
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.hideView(LoginActivity.this,progressBar);
                MethodTool.showToast(LoginActivity.this, Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                EnumRespType respType = EnumRespType.dealWithResponse(resp);
                switch (respType) {
                    case RESP_STAT:
                        EnumRespStatType respStatType = EnumRespStatType.dealWithRespStat(resp);
                        switch (respStatType) {
                            case NOT_MATCH:
                                MethodTool.hideView(LoginActivity.this,progressBar);
                                MethodTool.showToast(LoginActivity.this,"登录名与密码不匹配");
                                break;
                            case NSR:
                                MethodTool.hideView(LoginActivity.this,progressBar);
                                MethodTool.showToast(LoginActivity.this,"登录名与密码不匹配");
                                break;
                            case SESSION_EXPIRED:
                                MethodTool.showExitAppAlert(LoginActivity.this);
                                break;
                            default:break;
                        }
                        break;
                    case RESP_MAPLIST:
                        String[] keys = new String[] {"id","shop_id","type","user_name"};
                        List<Map<String,String>> mapList = JsonHandler.strToListMap(resp,keys);
                        map = mapList.get(0);
                        storeLoginMessage();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                Intent intent = new Intent(LoginActivity.this,ShopmemberMainActivity.class);
                                startActivity(intent);
                            }
                        });
                        break;
                    case RESP_ERROR:
                        MethodTool.hideView(LoginActivity.this,progressBar);
                        MethodTool.showToast(LoginActivity.this,Ref.UNKNOWN_ERROR);
                        break;
                    default:break;
                }
            }
        };
        NetUtil.sendHttpRequest(LoginActivity.this,url,callback);
    }

    /**
     * 储存登录信息
     */
    private void storeLoginMessage() {
        SharedPreferences.Editor editor_sasm = getSharedPreferences("sasm",Context.MODE_PRIVATE).edit();
        editor_sasm.clear().apply();
        SharedPreferences.Editor editor_login_data = getSharedPreferences("login_data",Context.MODE_PRIVATE).edit();
        editor_login_data.clear().apply();

        SharePreferenceManager.storeSharePreferenceInt(this,"sasm","sm_type",Integer.valueOf(String.valueOf(map.get("type"))));
        SharePreferenceManager.storeSharePreferenceLong(this,"sasm","sm_id",Integer.valueOf(String.valueOf(map.get("id"))));
        SharePreferenceManager.storeSharePreferenceLong(this,"sasm","s_id",Integer.valueOf(String.valueOf(map.get("shop_id"))));
        SharePreferenceManager.storeSharePreferenceString(this,"login_data","login_name",login_name);
        SharePreferenceManager.storeSharePreferenceString(this,"login_data","password",password);
    }
}
