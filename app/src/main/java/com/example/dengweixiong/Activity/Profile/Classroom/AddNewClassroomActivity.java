package com.example.dengweixiong.Activity.Profile.Classroom;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.dengweixiong.Activity.Member.AddNewMemberActivity;
import com.example.dengweixiong.Util.BaseActivity;
import com.example.dengweixiong.Util.JsonHandler;
import com.example.dengweixiong.Util.MethodTool;
import com.example.dengweixiong.Util.NetUtil;
import com.example.dengweixiong.Util.Reference;
import com.example.dengweixiong.myapplication.R;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AddNewClassroomActivity extends BaseActivity {

    private long sm_id,s_id;
    private long cr_id;
    private EditText et_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_classroom);
        initData();
        initToolBar();
        initView();
    }

    private void initData() {
        SharedPreferences preferences = getSharedPreferences("sasm",MODE_PRIVATE);
        sm_id = preferences.getLong("sm_id",0);
        s_id = preferences.getLong("s_id",0);
    }

    private void initView() {
        initToolBar();
        initEditText();
    }

    private void initToolBar(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("新增课室");
    }

    private void initEditText() {
        et_name = (EditText)findViewById(R.id.et_name_a_addNewClassroom);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_new_classroom,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.add_a_add_new_classroom :
                String cr_name = et_name.getText().toString();
                String url = "/ClassroomAdd?s_id=" + s_id + "&name=" + cr_name;
                Callback callback = new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        MethodTool.showToast(AddNewClassroomActivity.this,Reference.CANT_CONNECT_INTERNET);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();
                        Map<String,String> map = JsonHandler.strToMap(resp);
                        switch (map.get("stat")) {
                            case "exe_suc" :
                                MethodTool.showToast(AddNewClassroomActivity.this,"新增成功");
                                AddNewClassroomActivity.this.finish();
                                break;
                            case "duplicate" :
                                MethodTool.showToast(AddNewClassroomActivity.this,"教室名重复");
                                break;
                            case "exe_fail" :
                                MethodTool.showToast(AddNewClassroomActivity.this,"新增失败");
                                break;
                            case "no_such_record" :
                                MethodTool.showToast(AddNewClassroomActivity.this,"机构不存在");
                                AddNewClassroomActivity.this.finish();
                                break;
                            default:
                                break;
                        }
                    }
                };
                NetUtil.sendHttpRequest(AddNewClassroomActivity.this,url,callback);
                break;
            default:
                break;
        }
        return true;
    }
}
