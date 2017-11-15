package com.example.dengweixiong.Activity.Profile.Classroom;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.dengweixiong.Adapter.RVSimpleAdapter;
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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ClassroomListActivity extends BaseActivity {

    private ArrayList<String> strings = new ArrayList<>();
    private ArrayList<Map<String,String>> list = new ArrayList<>();
    private String [] keys = {"id","name"};
    private Map<String,String> tempMap = new HashMap<>();
    private static final int CLASSROOMLIST = 1;
    RVSimpleAdapter adapter;

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
                if (resp.contains(Reference.STATUS)) {
                    if (m.get("stat") == "no_such_record"){
                        MethodTool.showToast(ClassroomListActivity.this,"没有课室");
                    }else {
                        MethodTool.showToast(ClassroomListActivity.this,Reference.UNKNOWN_ERROR);
                    }
                }else {
                    list = JsonHandler.strToListMap(resp,keys);
                    MethodTool.sortListMap(list);
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
                adapter = new RVSimpleAdapter(strings);
                adapter.setOnItemClickListener(new RVSimpleAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(ClassroomListActivity.this,ClassroomDetailActivity.class);
                        Object s = list.get(position).get("id");
                        intent.putExtra("cr_id",String.valueOf(s));
                        intent.putExtra("cr_name",list.get(position).get("name"));
                        intent.putExtra("pos",position);
                        startActivityForResult(intent,1);
                    }
                });
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(layoutManager);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int p;
        switch (requestCode) {
            case 1 :
                if (resultCode == Reference.RESULTCODE_UPDATE) {
                    p = data.getIntExtra("pos",-1);
                    Map<String,String> map = new HashMap<>();
                    map.put("name",data.getStringExtra("new_name"));
                    map.put("id",String.valueOf(data.getLongExtra("cr_id",0)));
                    list.set(p,map);
                    strings.set(p,data.getStringExtra("new_name"));
                    MethodTool.sort(strings);
                    break;
                } else if (resultCode == Reference.RESULTCODE_DELETE) {
                    p = data.getIntExtra("pos",-1);
                    list.remove(p);
                    strings.remove(p);
                    break;
                }
                break;
            case 2:
                if (resultCode == Reference.RESULTCODE_ADD) {
                    long new_cr_id = data.getLongExtra("cr_id",0);
                    String new_cr_name = data.getStringExtra("cr_name");
                    Map<String,String> map = new HashMap<>();
                    map.put("id", String.valueOf(new_cr_id));
                    map.put("name",new_cr_name);
                    list.add(map);
                    MethodTool.sortListMap(list);
                    strings.add(new_cr_name);
                    MethodTool.sort(strings);
                }
            default:
                break;
        }
        adapter.notifyDataSetChanged();
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
                startActivityForResult(new Intent(ClassroomListActivity.this,AddNewClassroomActivity.class),2);
                break;
            default:
                break;
        }
        return true;
    }
}
