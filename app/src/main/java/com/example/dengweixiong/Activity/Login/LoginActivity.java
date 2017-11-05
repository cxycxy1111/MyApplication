package com.example.dengweixiong.Activity.Login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.example.dengweixiong.Activity.MainActivity;
import com.example.dengweixiong.Adapter.CourseViewPagerAdapter;
import com.example.dengweixiong.Util.BaseActivity;
import com.example.dengweixiong.Util.JsonHandler;
import com.example.dengweixiong.Util.MethodTool;
import com.example.dengweixiong.Util.NetUtil;
import com.example.dengweixiong.Util.Reference;
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

    private TabLayout tabLayout;
    private Context context;
    private static final String TOOLBAR_TITLE = "注册新机构与登录";
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

    private void initFragmentManager() {
        manager = getSupportFragmentManager();
    }

    private void initLoginnameAndPassword() {
        SharedPreferences preferences = getSharedPreferences("login_data",MODE_PRIVATE);
        login_name = preferences.getString("login_name",null);
        password = preferences.getString("password",null);
    }

    private void initView() {
        initToolbar();
        initTabLayout();
        initViewPager();
        createProgressBar();
        autoLogin();
    }

    private void autoLogin() {
        String url = "/ShopMemberLogin?user_name=" + login_name + "&password=" + password;
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.hideView(LoginActivity.this,progressBar);
                MethodTool.showToast(LoginActivity.this,Reference.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Map<String,String> map = new HashMap<>();
                String resp = response.body().string();
                map = JsonHandler.strToMap(resp);
                ArrayList<String> keys = MethodTool.getKeys(map);
                ArrayList<String> values = MethodTool.getValues(map,keys);
                if (keys.get(0).equals(Reference.STATUS)) {
                    if (values.get(0).equals("not_match")) {
                        MethodTool.hideView(LoginActivity.this,progressBar);
                        MethodTool.showToast(LoginActivity.this,"登录名与密码不匹配");
                    } else if (values.get(0).equals("no_such_record")){
                        MethodTool.hideView(LoginActivity.this,progressBar);
                        MethodTool.showToast(LoginActivity.this,"登录名与密码不匹配");
                    } else {
                        MethodTool.hideView(LoginActivity.this,progressBar);
                        MethodTool.showToast(LoginActivity.this,Reference.UNKNOWN_ERROR);
                    }
                }else if (keys.get(0).equals("data")) {
                    MethodTool.hideView(LoginActivity.this,progressBar);
                    MethodTool.jumpToActivity(LoginActivity.this,MainActivity.class);
                }else {
                    MethodTool.hideView(LoginActivity.this,progressBar);
                    MethodTool.showToast(LoginActivity.this,"内部错误，无法完成请求");
                }
            }
        };
        NetUtil.sendHttpRequest(LoginActivity.this,url,callback);
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
