package com.example.dengweixiong.Login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dengweixiong.Util.JsonHandler;
import com.example.dengweixiong.Util.MethodTool;
import com.example.dengweixiong.Util.NetUtil;
import com.example.dengweixiong.Util.Ref;
import com.example.dengweixiong.myapplication.R;


import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RegistAdministraotrActivity
        extends AppCompatActivity
        implements View.OnClickListener{

    private static final String TAG = "RegistAdminActivity:";
    private static final String TITLE_BAR = "注册机构管理员";
    private EditText et_name,et_loginname,et_password;
    private String shop_name,intro,name,loginname,password;
    private Button btn_submit;
    private long s_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_regist_administrator);
        initView();
        dealWithIntent();
    }

    private void initView() {
        initToolbar();
        initWidgets();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_a_regist_admin);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(TITLE_BAR);
    }

    private void initWidgets() {
        et_name = (EditText)findViewById(R.id.et_name_a_regist_admin);
        et_loginname = (EditText)findViewById(R.id.et_loginname_a_regist_admin);
        et_password = (EditText)findViewById(R.id.et_password_a_regist_admin);
        btn_submit = (Button)findViewById(R.id.btn_submit_a_regist_admin);
        btn_submit.setOnClickListener(this);
    }

    private void dealWithIntent() {
        Intent intent = getIntent();
        s_id = intent.getLongExtra("s_id",0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit_a_regist_admin:
                request();
                break;
            default:
                break;
        }
    }

    private void request() {
        name = et_name.getText().toString();
        loginname = et_loginname.getText().toString();
        password = et_password.getText().toString();
        String url_add_shopmember = "/ShopMemberRegister?login_name=" + loginname + "&pwd=" + password + "&name=" + name + "&s_id=" + s_id;
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                RegistAdministraotrActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RegistAdministraotrActivity.this,Ref.CANT_CONNECT_INTERNET,Toast.LENGTH_LONG).show();
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                HashMap<String,String> map = new HashMap<>();
                String resp = response.body().string();
                map = JsonHandler.strToMap(resp);
                String value = map.get(Ref.STATUS);
                if (value.equals("exe_suc")) {
                    MethodTool.showToast(RegistAdministraotrActivity.this,"注册成功，请前往登录页面登录");
                    Intent intent = new Intent(RegistAdministraotrActivity.this,LoginActivity.class);
                    startActivity(intent);
                } else if (value.equals("exe_fail")) {
                    RegistAdministraotrActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(RegistAdministraotrActivity.this,Ref.OP_ADD_FAIL,Toast.LENGTH_LONG).show();
                        }
                    });
                }else if (value.equals("duplicate")) {
                    RegistAdministraotrActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(RegistAdministraotrActivity.this,"登录名重复",Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        };

        NetUtil.sendHttpRequest(RegistAdministraotrActivity.this,url_add_shopmember,callback);
    }
}
