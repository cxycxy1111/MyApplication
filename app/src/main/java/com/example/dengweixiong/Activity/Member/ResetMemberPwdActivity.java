package com.example.dengweixiong.Activity.Member;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dengweixiong.Util.BaseActivity;
import com.example.dengweixiong.Util.JsonHandler;
import com.example.dengweixiong.Util.MethodTool;
import com.example.dengweixiong.Util.NetUtil;
import com.example.dengweixiong.Util.Reference;
import com.example.dengweixiong.myapplication.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ResetMemberPwdActivity
        extends BaseActivity {

    private Toolbar toolbar;
    private EditText et_newpwd,et_newpwd_repeat;
    private String newpwd,newpwd_repeat;
    private long s_id,sm_id,m_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pwd);
        initView();
    }

    private void initView() {
        initToolbar();
        initEditText();
    }

    private void initToolbar() {
        toolbar = (Toolbar)findViewById(R.id.tb_activity_reset_pwd_member);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("重置密码");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initEditText() {
        et_newpwd = (EditText)findViewById(R.id.et_newpwd_a_reset_pwd);
        et_newpwd_repeat = (EditText)findViewById(R.id.et_repeat_newpwd_a_reset_pwd);
    }

    private void initData() {
        s_id = getSharedPreferences("sasm",MODE_PRIVATE).getLong("s_id",0);
        sm_id = getSharedPreferences("sasm",MODE_PRIVATE).getLong("sm_id",0);
        m_id = getIntent().getLongExtra("m_id",0);
        newpwd = et_newpwd.getText().toString();
        newpwd_repeat = et_newpwd_repeat.getText().toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_reset_pwd,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.submit_reset_pwd:
                boolean b = dealWithSubmitAction();
                if (b == false) {
                    break;
                }else {
                    break;
                }
            case android.R.id.home:
                this.finish();
                break;
            default:
                break;
        }
        return true;
    }

    private boolean dealWithSubmitAction() {
        initData();
        if (newpwd == null | newpwd.equals("")) {
            Toast.makeText(this,"密码不能为空",Toast.LENGTH_LONG).show();
            return false;
        }
        if ((newpwd_repeat == null | newpwd_repeat.equals("")) && (newpwd != null | !newpwd.equals(""))) {
            Toast.makeText(this,"请重复输入密码",Toast.LENGTH_LONG).show();
            return false;
        }
        if (!newpwd_repeat.equals(newpwd)) {
            Toast.makeText(this,"密码不一致",Toast.LENGTH_LONG).show();
            return false;
        }
        String url = "/MemberResetPassword?m_id=" + m_id + "&sm_id=" + sm_id + "&newpwd=" + newpwd;
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(ResetMemberPwdActivity.this,Reference.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Map<String,String> map = JsonHandler.strToMap(response);
                ArrayList<String> keys = MethodTool.getKeys(map);
                ArrayList<String> values = MethodTool.getValues(map,keys);
                if (values.get(0).equals("no_such_record")) {
                    MethodTool.showToast(ResetMemberPwdActivity.this,"会员不存在");
                }else if (values.get(0).equals("exe_suc")) {
                    MethodTool.showToast(ResetMemberPwdActivity.this,"修改成功");
                }else if (values.get(0).equals("exe_fail")) {
                    MethodTool.showToast(ResetMemberPwdActivity.this,"修改失败");
                }else {
                    MethodTool.showToast(ResetMemberPwdActivity.this, Reference.UNKNOWN_ERROR);
                }
            }
        };
        NetUtil.sendHttpRequest(ResetMemberPwdActivity.this,url,callback);
        return true;
    }
}
