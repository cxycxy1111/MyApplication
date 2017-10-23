package com.example.dengweixiong.Activity.Profile.Classroom;

import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.TimeZoneNames;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.dengweixiong.Adapter.SimpleRecylcerViewAdapter;
import com.example.dengweixiong.Bean.Classroom;
import com.example.dengweixiong.Util.BaseActivity;
import com.example.dengweixiong.Util.JsonHandler;
import com.example.dengweixiong.Util.MethodTool;
import com.example.dengweixiong.Util.NetUtil;
import com.example.dengweixiong.Util.Reference;
import com.example.dengweixiong.myapplication.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.RunnableFuture;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ClassroomListActivity extends BaseActivity {

    private ArrayList<String> strings = new ArrayList<>();
    private ArrayList<Map<String,String>> list = new ArrayList<>();
    private String [] keys = {"id","name"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom_list);
        initView();
        initData();
    }

    private void initView() {
        initToolbar();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("课室管理");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initData() {
        SharedPreferences preferences = getSharedPreferences("sasm",MODE_PRIVATE);
        long s_id = preferences.getLong("s_id",0);
        String url = "/ClassroomListQuery?s_id=" + s_id;
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(ClassroomListActivity.this,Reference.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                HashMap<String,String> m = new HashMap<>();
                if (resp.contains("stat")) {
                    if (m.get("stat") == "no_such_record"){
                        MethodTool.showToast(ClassroomListActivity.this,"没有课室");
                    }else {
                        MethodTool.showToast(ClassroomListActivity.this,Reference.UNKNOWN_ERROR);
                    }
                }else {
                    list = JsonHandler.strToListMap(resp,keys);
                    for (int i = 0;i < list.size();i++) {
                        strings.add(list.get(i).get("name"));
                    }
                    initRecyclerView();
                }
            }
        };
        NetUtil.sendHttpRequest(ClassroomListActivity.this,url,callback);
    }

    private void initRecyclerView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinearLayoutManager layoutManager = new LinearLayoutManager(ClassroomListActivity.this,LinearLayoutManager.VERTICAL,false);
                RecyclerView recyclerView = (RecyclerView)findViewById(R.id.rv_a_ClassroomList);
                SimpleRecylcerViewAdapter adapter = new SimpleRecylcerViewAdapter(strings);
                adapter.setOnItemClickListener(new SimpleRecylcerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(ClassroomListActivity.this,ClassroomDetailActivity.class);
                        Object s = list.get(position).get("id");
                        intent.putExtra("cr_id",String.valueOf(s));
                        intent.putExtra("cr_name",list.get(position).get("name"));
                        startActivity(intent);
                    }
                });
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(layoutManager);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_classroom_list,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.add_a_classroom_list :
                startActivity(new Intent(ClassroomListActivity.this,AddNewClassroomActivity.class));
                break;
            default:
                break;
        }
        return true;
    }
}
