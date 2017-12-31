package com.example.dengweixiong.Shopmember.Profile.Shopmember;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.dengweixiong.Util.BaseActivity;
import com.example.dengweixiong.Util.JsonHandler;
import com.example.dengweixiong.Util.MethodTool;
import com.example.dengweixiong.Util.NetUtil;
import com.example.dengweixiong.Util.Ref;
import com.example.dengweixiong.myapplication.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ShopmemberDetailActivity extends BaseActivity {

    private String name;
    private int type,position;
    private long id,sm_id,s_id;
    private String new_name;
    private EditText et_name,et_user_name;
    private RelativeLayout relativeLayout_delete;
    private String [] keys = new String[] {"id","type","name","user_name"};
    private Map<String,String> map = new HashMap<>();
    private List<Map<String,String>> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopmember_detail);
        initData();
        initView();
    }

    private void initData() {
        Intent intent = getIntent();
        id = Long.parseLong(intent.getStringExtra("id"));
        type = Integer.parseInt(intent.getStringExtra("type"));
        position = intent.getIntExtra("pos",-1);
        name = intent.getStringExtra("name");
        SharedPreferences preferences = getSharedPreferences("sasm",MODE_PRIVATE);
        sm_id = preferences.getLong("sm_id",0);
        s_id = preferences.getLong("s_id",0);
    }

    private void initView() {
        initToolBar();
        initEditText();
        initLinearLayout();
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(name);
    }

    private void initEditText() {
        initEditTextData();
    }

    private void initLinearLayout(){
        relativeLayout_delete = (RelativeLayout)findViewById(R.id.rl_delete_a_shopmember_detail);
        relativeLayout_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(ShopmemberDetailActivity.this);
                builder.setTitle("确定要删除吗？");
                builder.setMessage("删除后教师将不能登录、参加排课等");
                builder.setCancelable(false);
                builder.setNegativeButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        delete();
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("不删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });
    }

    private void initEditTextData() {
        et_name = (EditText)findViewById(R.id.et_name_a_shopmember_detail);
        et_user_name = (EditText)findViewById(R.id.et_user_name_a_shopmember_detail);
        String url = "/ShopmemberQueryDetail?sm_id=" + id + "&s_id=" + s_id;
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(ShopmemberDetailActivity.this, Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                list = JsonHandler.strToListMap(resp,keys);
                map = list.get(0);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        et_name.setText(map.get("name"));
                        et_user_name.setText(map.get("user_name"));
                    }
                });
            }
        };
        NetUtil.sendHttpRequest(this,url,callback);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_shopmember_detail,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(Ref.RESULTCODE_NULL,null);
                this.finish();
                break;
            case R.id.save_shopmember_detail:
                save();
        }

        return true;
    }

    private void save() {
        new_name = et_name.getText().toString();
        String url = "/ShopmemberModify?sm_id=" + id + "&lmu_id=" + sm_id + "&name=" + new_name;
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(ShopmemberDetailActivity.this, Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                if(resp.equals("") | resp.equals(null)) {
                    MethodTool.showToast(ShopmemberDetailActivity.this, Ref.UNKNOWN_ERROR);
                } else {
                    Map<String,String> map = JsonHandler.strToMap(resp);
                    String value = map.get("stat");
                    if (value.equals(Ref.STAT_INST_NOT_MATCH)) {
                        MethodTool.showToast(ShopmemberDetailActivity.this,"机构不匹配");
                    }else if (value.equals(Ref.STAT_EXE_SUC)) {
                        MethodTool.showToast(ShopmemberDetailActivity.this,"保存成功");
                        Intent intent = new Intent(ShopmemberDetailActivity.this,ShopmemberListActivity.class);
                        intent.putExtra("name",new_name);
                        intent.putExtra("id",id);
                        intent.putExtra("type",type);
                        intent.putExtra("pos",position);
                        setResult(Ref.RESULTCODE_UPDATE,intent);
                        finish();
                    }else if (value.equals(Ref.STAT_EXE_FAIL)) {
                        MethodTool.showToast(ShopmemberDetailActivity.this,"保存失败");
                    }else if (value.equals(Ref.STAT_NSR)) {
                        MethodTool.showToast(ShopmemberDetailActivity.this,"记录不存在");
                    }
                }
            }
        };
        NetUtil.sendHttpRequest(ShopmemberDetailActivity.this,url,callback);
    }

    private void delete(){
        final String url = "/ShopmemberDelete?id=" + id + "&lmu_id=" + sm_id;
        final Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(ShopmemberDetailActivity.this, Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                Map<String,String> map = JsonHandler.strToMap(resp);
                switch (map.get("stat")) {
                    case "institution_not_match" :
                        MethodTool.showToast(ShopmemberDetailActivity.this,"机构不匹配");
                        break;
                    case "exe_suc" :
                        MethodTool.showToast(ShopmemberDetailActivity.this,"删除成功");
                        Intent intent = new Intent(ShopmemberDetailActivity.this,ShopmemberListActivity.class);
                        intent.putExtra("pos",position);
                        setResult(Ref.RESULTCODE_DELETE,intent);
                        finish();
                        break;
                    case "exe_fail" :
                        MethodTool.showToast(ShopmemberDetailActivity.this,"删除失败");
                        break;
                    default:
                        break;
                }
            }
        };
        NetUtil.sendHttpRequest(ShopmemberDetailActivity.this,url,callback);
    }
}
