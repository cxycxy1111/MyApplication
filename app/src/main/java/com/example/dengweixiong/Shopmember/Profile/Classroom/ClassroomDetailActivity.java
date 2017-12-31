package com.example.dengweixiong.Shopmember.Profile.Classroom;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.dengweixiong.Util.BaseActivity;
import com.example.dengweixiong.Util.JsonHandler;
import com.example.dengweixiong.Util.MethodTool;
import com.example.dengweixiong.Util.NetUtil;
import com.example.dengweixiong.Util.Ref;
import com.example.dengweixiong.myapplication.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ClassroomDetailActivity extends BaseActivity {

    private long sm_id,s_id;
    private long cr_id;
    private String cr_name,req_cr_name;
    private String [] keys = new String[] {"id","name"};
    private EditText et_name;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom_detail);
        initData();
        initToolbar();
        initEditText();
    }

    private void initData() {
        SharedPreferences preferences = getSharedPreferences("sasm",MODE_PRIVATE);
        Intent intent = getIntent();
        sm_id = preferences.getLong("sm_id",0);
        s_id = preferences.getLong("s_id",0);
        cr_id = Long.parseLong(intent.getStringExtra("cr_id"));
        cr_name = intent.getStringExtra("cr_name");
        position = intent.getIntExtra("pos",0);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(cr_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initEditText() {
        et_name = (EditText)findViewById(R.id.et_name_a_classroom_detail);
        String url = "/ClassroomDetailQuery?cr_id=" + cr_id;
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(ClassroomDetailActivity.this, Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                if (resp.contains(Ref.STATUS)) {
                    MethodTool.showToast(ClassroomDetailActivity.this, Ref.UNKNOWN_ERROR);
                }else {
                    ArrayList<Map<String,String>> list = JsonHandler.strToListMap(resp,keys);
                    req_cr_name = list.get(0).get("name");
                    setEditTextText();
                }
            }
        };
        NetUtil.sendHttpRequest(ClassroomDetailActivity.this,url,callback);
    }

    private void setEditTextText() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                et_name.setText(req_cr_name);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_classroom_detail,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                this.finish();
                break;
            case R.id.save_classroom_detail:
                save();
                break;
            case R.id.delete_classroom_detail:
                delete();
                break;
            default:
                break;
        }
        return true;
    }

    private void save() {
        final String new_name = et_name.getText().toString();
        String url = "/ClassroomModify?cr_id=" + cr_id + "&name=" + new_name;
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(ClassroomDetailActivity.this, Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                Map<String,String> map = JsonHandler.strToMap(resp);
                switch (map.get(Ref.STATUS)) {
                    case "no_such_record" :
                        MethodTool.showToast(ClassroomDetailActivity.this,"机构或课室不存在");
                        break;
                    case "exe_suc" :
                        Intent intent = new Intent(ClassroomDetailActivity.this,ClassroomListActivity.class);
                        intent.putExtra("new_name",new_name);
                        intent.putExtra("pos",position);
                        intent.putExtra("cr_id",cr_id);
                        setResult(Ref.RESULTCODE_UPDATE,intent);
                        finish();
                        break;
                    case "exe_fail":
                        MethodTool.showToast(ClassroomDetailActivity.this,"保存失败");
                        break;
                }
            }
        };
        NetUtil.sendHttpRequest(ClassroomDetailActivity.this,url,callback);
    }

    private void delete() {
        String url = "/ClassroomDelete?cr_id=" + cr_id;
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(ClassroomDetailActivity.this, Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                HashMap<String,String> map = JsonHandler.strToMap(resp);
                switch (map.get(Ref.STATUS)) {
                    case "exe_suc" :
                        Intent intent = new Intent(ClassroomDetailActivity.this,ClassroomListActivity.class);
                        int i = position;
                        intent.putExtra("pos",i);
                        setResult(Ref.RESULTCODE_DELETE,intent);
                        finish();
                        break;
                    case "exe_fail" :
                        MethodTool.showToast(ClassroomDetailActivity.this,"删除失败");
                        break;
                    case "no_such_record" :
                        MethodTool.showToast(ClassroomDetailActivity.this,"未找到课室");
                        break;
                    default:
                        break;
                }
            }
        };
        NetUtil.sendHttpRequest(ClassroomDetailActivity.this,url,callback);
    }
}
