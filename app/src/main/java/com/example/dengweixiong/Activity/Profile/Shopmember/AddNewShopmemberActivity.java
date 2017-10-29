package com.example.dengweixiong.Activity.Profile.Shopmember;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

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

public class AddNewShopmemberActivity extends BaseActivity {

    private List<String> type = new ArrayList<>();
    private int selectedType = 0;
    private long s_id,sm_id;
    private EditText et_name,et_user_name,et_pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_shopmember);
        initData();
        initView();
    }

    private void initData() {
        type.add("管理员");type.add("内部教师");type.add("外聘教师");

        SharedPreferences preferences = getSharedPreferences("sasm",MODE_PRIVATE);
        s_id = preferences.getLong("s_id",0);
        sm_id = preferences.getLong("sm_id",0);
    }

    private void initView(){
        initToolbar();
        initSpinner();
        initEditText();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("新增教师");
    }

    private void initSpinner() {
        Spinner spinner = (Spinner)findViewById(R.id.sp_type_add_new_shopmember);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,type);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        selectedType = 1;
                        break;
                    case 1:
                        selectedType = 2;
                        break;
                    case 2:
                        selectedType = 3;
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedType = 1;
            }
        });
    }

    private void initEditText(){
        et_name = (EditText)findViewById(R.id.et_name_add_new_shopmember);
        et_user_name = (EditText)findViewById(R.id.et_user_name_add_new_shopmember);
        et_pwd = (EditText)findViewById(R.id.et_pwd_add_new_shopmember);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_new_shopmember,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                setResult(Reference.RESULTCODE_NULL);
                finish();
                break;
            case R.id.save_add_new_shopmember :
                save();
                break;
            default:
                break;
        }
        return true;
    }

    private void save() {
        final String name = et_name.getText().toString();
        String user_name = et_user_name.getText().toString();
        String pwd = et_pwd.getText().toString();
        String url = "/AddNewShopMember?shop_id=" + s_id +
                "&shopmember_id=" + sm_id +
                "&name=" + name +
                "&user_name=" + user_name +
                "&password=" + pwd +
                "&type=" + selectedType;
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(AddNewShopmemberActivity.this,Reference.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                if (resp.contains(Reference.STATUS)) {
                    Map<String,String> map =JsonHandler.strToMap(resp);
                    String v = map.get(Reference.STATUS);
                    if (map.get("stat").equals(Reference.EXE_FAIL)) {
                        MethodTool.showToast(AddNewShopmemberActivity.this,"新增失败");
                    }else if (map.get("stat").equals(Reference.DUPLICATE)) {
                        MethodTool.showToast(AddNewShopmemberActivity.this,"登录名重复");
                    }else {

                    }
                }else if (resp.contains(Reference.DATA)) {
                    Map<String,String> map =JsonHandler.strToMap(resp);
                    String v = map.get(Reference.DATA);
                    Intent intent = new Intent(AddNewShopmemberActivity.this,ShopmemberListActivity.class);
                    intent.putExtra("id",v);
                    intent.putExtra("name",name);
                    intent.putExtra("type",selectedType);
                    setResult(Reference.RESULTCODE_ADD,intent);
                    finish();
                }else {
                    MethodTool.showToast(AddNewShopmemberActivity.this,Reference.UNKNOWN_ERROR);
                }
            }
        };
        NetUtil.sendHttpRequest(this,url,callback);
    }
}