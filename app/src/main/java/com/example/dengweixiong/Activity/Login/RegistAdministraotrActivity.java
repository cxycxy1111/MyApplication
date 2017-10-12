package com.example.dengweixiong.Activity.Login;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dengweixiong.Activity.MainActivity;
import com.example.dengweixiong.Util.NetUtil;
import com.example.dengweixiong.myapplication.R;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class RegistAdministraotrActivity
        extends AppCompatActivity
        implements View.OnClickListener{

    private static final String TITLE_BAR = "注册机构管理员";
    private EditText et_name,et_loginname,et_password;
    private String shop_name,intro,name,loginname,password;
    private Button btn_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist_administrator);
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
        shop_name = intent.getStringExtra("shop_name");
        intro = intent.getStringExtra("intro");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit_a_regist_admin:
                name = et_name.getText().toString();
                loginname = et_loginname.getText().toString();
                password = et_password.getText().toString();
                String add = "/ShopMemberRegister?login_name=" + loginname + "&pwd=" + password + "&name=" + name + "&s_id=" + s_id;
                okhttp3.Callback callback_1 = new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                    }
                };

                okhttp3.Callback callback_2 = new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),"无法连接网络",Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Intent intent = new Intent(RegistAdministraotrActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                };
                NetUtil.sendHttpRequest(add,callback_2);
                break;
            default:
                break;
        }
    }
}
