package com.example.dengweixiong.Shopmember.Profile.Shopmember;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dengweixiong.Util.ActivityManager;
import com.example.dengweixiong.Util.BaseActivity;
import com.example.dengweixiong.Util.Enum.EnumRespStatType;
import com.example.dengweixiong.Util.Enum.EnumRespType;
import com.example.dengweixiong.Util.MethodTool;
import com.example.dengweixiong.Util.NetUtil;
import com.example.dengweixiong.Util.Ref;
import com.example.dengweixiong.Util.SharePreferenceManager;
import com.example.dengweixiong.myapplication.R;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ResetShopmemberPasswordActivity extends BaseActivity {

    private long id;
    private String sm_id,s_id;
    private EditText et_password,et_password_repeat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_shopmember_password);
        initData();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_general);
        et_password = (EditText)findViewById(R.id.et_newpwd_a_reset_shopmember_password);
        et_password_repeat = (EditText)findViewById(R.id.et_repeat_newpwd_a_reset_shopmember_password);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("重设密码");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_reset_shopmember_password,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ResetShopmemberPasswordActivity.this.finish();
                break;
            case R.id.modify_reset_shopmember_password:
                dealWithRequestParamater();
                break;
            default:break;
        }
        return true;
    }

    private void dealWithRequestParamater() {
        String new_password = et_password.getText().toString();
        String new_password_repeat = et_password_repeat.getText().toString();
        if (new_password.equals("") || new_password_repeat.equals("")) {
            Toast.makeText(ResetShopmemberPasswordActivity.this,Ref.OP_EMPTY_ESSENTIAL_INFO,Toast.LENGTH_SHORT).show();
        }else if (!new_password.equals(new_password_repeat)) {
            Toast.makeText(ResetShopmemberPasswordActivity.this,"两次输入的密码不一致，请核对",Toast.LENGTH_SHORT).show();
        }else {
            modifyPassword(new_password);
        }
    }

    private void modifyPassword(String password) {
        String url = "/shopmemberResetPassword?sm_id=" + id + "&pwd=" + password;
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(ResetShopmemberPasswordActivity.this, Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                switch (EnumRespType.dealWithResponse(resp)) {
                    case RESP_STAT:
                        switch (EnumRespStatType.dealWithRespStat(resp)) {
                            case EXE_SUC:
                                MethodTool.showToast(ResetShopmemberPasswordActivity.this,Ref.OP_MODIFY_SUCCESS);
                                if (Long.parseLong(sm_id) == id) {
                                    SharedPreferences preferences_sasm = getSharedPreferences("sasm", Activity.MODE_PRIVATE);
                                    SharedPreferences.Editor editor_sasm = preferences_sasm.edit();
                                    editor_sasm.clear();
                                    editor_sasm.clear().commit();
                                    SharedPreferences preferences_login = getSharedPreferences("login_data",Activity.MODE_PRIVATE);
                                    SharedPreferences.Editor editor_login = preferences_login.edit();
                                    editor_login.clear().commit();
                                    MethodTool.showToast(ResetShopmemberPasswordActivity.this,"你已修改你的登录密码，需要重新登录");
                                    Intent intent = new Intent();
                                    intent.setAction("exit_app");
                                    ActivityManager.removeAllActivity();
                                    //sendBroadcast(intent);
                                }else {
                                    ResetShopmemberPasswordActivity.this.finish();
                                }
                                break;
                            case EXE_FAIL:
                                MethodTool.showToast(ResetShopmemberPasswordActivity.this,Ref.OP_MODIFY_FAIL);
                                break;
                            case NSR:
                                MethodTool.showToast(ResetShopmemberPasswordActivity.this,Ref.OP_NSR);
                                break;
                            case SESSION_EXPIRED:
                                MethodTool.showExitAppAlert(ResetShopmemberPasswordActivity.this);
                                break;
                            case AUTHORIZE_FAIL:
                                MethodTool.showAuthorizeFailToast(ResetShopmemberPasswordActivity.this);
                                break;
                            default:break;
                        }
                        break;
                    case RESP_ERROR:
                        MethodTool.showToast(ResetShopmemberPasswordActivity.this,Ref.UNKNOWN_ERROR);
                        break;
                    default:break;
                }
            }
        };
        NetUtil.sendHttpRequest(ResetShopmemberPasswordActivity.this,url,callback);
    }

    private void initData() {
        sm_id = SharePreferenceManager.getSharePreferenceValue(ResetShopmemberPasswordActivity.this,"sasm","sm_id",2);
        s_id = SharePreferenceManager.getSharePreferenceValue(ResetShopmemberPasswordActivity.this,"sasm","s_id",2);
        id = getIntent().getLongExtra("id",0);
    }

}
