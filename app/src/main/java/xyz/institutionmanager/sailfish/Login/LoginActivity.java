package xyz.institutionmanager.sailfish.Login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;
import com.alfred.alfredtools.*;
import xyz.institutionmanager.sailfish.Adapter.ViewPagerAdapter;
import xyz.institutionmanager.sailfish.R;
import xyz.institutionmanager.sailfish.Shopmember.Main.ShopmemberMainActivity;
import xyz.institutionmanager.sailfish.Util.Ref;
import xyz.institutionmanager.sailfish.Util.SharePreferenceManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity
        extends BaseActivity
        implements TabLayout.OnTabSelectedListener,
        RegistShopFragment.OnFragmentInteractionListener,
        SignInFragment.OnFragmentInteractionListener,
        MemberSignInFragment.OnFragmentInteractionListener,HttpResultListener {

    private String sm_type;
    private String sm_id, s_id;
    private String[] keys = new String[]{"id", "name", "type", "user_name"};
    private Map<String, String> map = new HashMap<>();
    private TabLayout tabLayout;
    private Context context;
    private ViewPager viewPager;
    private List<Fragment> fragments = new ArrayList<>();
    private FragmentManager manager;
    private ViewPagerAdapter adapter_vp;
    private String[] str_title = {"注册新机构", "教师登录","会员登录"};
    private String[] str_title_2 = {"注册新机构", "教师登录"};
    private String login_name, password;

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
        sm_type = SharePreferenceManager.getSharePreferenceValue(this, "sasm", "sm_type", 1);
        s_id = SharePreferenceManager.getSharePreferenceValue(this, "sasm", "s_id", 2);
        sm_id = SharePreferenceManager.getSharePreferenceValue(this, "sasm", "sm_id", 2);
        login_name = SharePreferenceManager.getSharePreferenceValue(this, "login_data", "login_name", 3);
        password = SharePreferenceManager.getSharePreferenceValue(this, "login_data", "password", 3);
    }

    private void initView() {
        initToolbar();
        initTabLayout();
        initViewPager();
        //createProgressBar();
        //checkInfoIsPrepared();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_general);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.title_a_login);
    }

    private void initTabLayout() {
        tabLayout = (TabLayout) findViewById(R.id.tl_a_login);
        tabLayout.addTab(tabLayout.newTab().setText("注册新机构"));
        tabLayout.addTab(tabLayout.newTab().setText("教师登录"));
        tabLayout.addTab(tabLayout.newTab().setText("会员登录"));
        tabLayout.addOnTabSelectedListener(this);
    }

    private void initViewPager() {
        viewPager = (ViewPager) findViewById(R.id.vp_a_login);
        adapter_vp = new ViewPagerAdapter(manager, str_title, fragments);
        viewPager.setAdapter(adapter_vp);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initFragments() {
        fragments.add(RegistShopFragment.newInstance("注册新机构"));
        fragments.add(SignInFragment.newInstance("教师登录"));
        fragments.add(MemberSignInFragment.newInstance("会员登录"));
    }

    private void checkInfoIsPrepared() {
        if (sm_type.equals("") || s_id.equals("") || sm_id.equals("") || login_name.equals("") || password.equals("")) {
            Toast.makeText(this, "自动登录失败", Toast.LENGTH_SHORT).show();
        } else {
            autoLogin();
        }
    }

    private void autoLogin() {
        String deviceBrand = Build.BRAND;
        String systemVersion = Build.VERSION.RELEASE;
        String systemModel = Build.MODEL;
        HashMap<String,Object> map_1 = new HashMap<>();
        map_1.put("user_name",login_name);
        map_1.put("password",password);
        map_1.put("system_version",systemVersion);
        map_1.put("system_model",systemModel);
        map_1.put("device_brand",deviceBrand);
        try {
            map_1.put("app_version",getPackageManager().getPackageInfo(this.getPackageName(),0).versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String url = "/ShopMemberLogin";
        HttpCallback callback = new HttpCallback(this,this,1);
        NetUtil.reqSendGet(this,url,callback);
    }

    @Override
    public void onRespStatus(String body, int source) {
        switch (NetRespStatType.dealWithRespStat(body)) {
            case STATUS_NOT_MATCH:
                ViewHandler.toastShow(LoginActivity.this,"登录名与密码不匹配");
                break;
            case NSR:
                ViewHandler.toastShow(LoginActivity.this,"登录名与密码不匹配");
                break;
            default:break;
        }
    }

    @Override
    public void onRespMapList(String body, int source) throws IOException {
        String[] keys = new String[] {"id","shop_id","type","user_name"};
        List<Map<String,String>> mapList = JsonUtil.strToListMap(body,keys);
        map = mapList.get(0);
        storeLoginMessage();
        Intent intent = new Intent(LoginActivity.this,ShopmemberMainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRespError(int source) {
        ViewHandler.toastShow(LoginActivity.this,Ref.UNKNOWN_ERROR);
    }

    @Override
    public void onReqFailure(Object object, int source) {

    }

    @Override
    public void onRespSessionExpired(int source) {
        ViewHandler.alertShowAndExitApp(this);
    }


    /**
     * 储存登录信息
     */
    private void storeLoginMessage() {
        SharedPreferences.Editor editor_sasm = getSharedPreferences("sasm",Context.MODE_PRIVATE).edit();
        editor_sasm.clear().apply();
        String int_tmp_is_remember_password = SharePreferenceManager.getSharePreferenceValue(this,"login_data","is_remember_password",1);
        SharedPreferences.Editor editor_login_data = getSharedPreferences("login_data",Context.MODE_PRIVATE).edit();
        editor_login_data.clear().apply();

        SharePreferenceManager.storeSharePreferenceInt(this,"sasm","sm_type",Integer.valueOf(String.valueOf(map.get("type"))));
        SharePreferenceManager.storeSharePreferenceLong(this,"sasm","sm_id",Integer.valueOf(String.valueOf(map.get("id"))));
        SharePreferenceManager.storeSharePreferenceLong(this,"sasm","s_id",Integer.valueOf(String.valueOf(map.get("shop_id"))));
        SharePreferenceManager.storeSharePreferenceString(this,"login_data","login_name",login_name);
        SharePreferenceManager.storeSharePreferenceString(this,"login_data","password",password);
        if (int_tmp_is_remember_password.equals("0")) {
            SharePreferenceManager.storeSharePreferenceInt(this,"login_data","is_remember_password",0);
        }else {
            SharePreferenceManager.storeSharePreferenceInt(this,"login_data","is_remember_password",1);
        }
    }
}
