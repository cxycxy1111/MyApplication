package com.example.dengweixiong.Shopmember.Profile.Shopmember;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.dengweixiong.Shopmember.Adapter.RVSectionAdapter;
import com.example.dengweixiong.Util.BaseActivity;
import com.example.dengweixiong.Util.Enum.EnumRespStatType;
import com.example.dengweixiong.Util.Enum.EnumRespType;
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

public class ShopmemberListActivity extends BaseActivity {

    private RVSectionAdapter adapter;
    private long s_id,sm_id;
    private List<Map<String,String>> full_list = new ArrayList<>();
    private List<Map<String,String>> admin_list = new ArrayList<>();
    private List<Map<String,String>> inner_list = new ArrayList<>();
    private List<Map<String,String>> outer_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopmember_list);
        initData();
        initView();
    }

    private void initData() {
        SharedPreferences preferences = getSharedPreferences("sasm",MODE_PRIVATE);
        s_id = preferences.getLong("s_id",0);
    }

    private void initView() {
        initToolbar();
        initAdminList();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("教师管理");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initAdminList() {
        String url = "/QueryShopmemberList?s_id=" + s_id + "&type=1";
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(ShopmemberListActivity.this, Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                EnumRespType respType = EnumRespType.dealWithResponse(resp);
                switch (respType) {
                    case RESP_MAPLIST:
                        String [] keys = new String[] {"id","name","type"};
                        List<Map<String,String>> list = JsonHandler.strToListMap(resp,keys);
                        if (list.size() !=0 ) {
                            Map<String,String> header = new HashMap<>();
                            header.put("type","管理员");
                            full_list.add(header);
                        }
                        for (int i = 0;i < list.size();i++) {
                            full_list.add(list.get(i));
                            admin_list.add(list.get(i));
                        }
                        initInnerList();
                        break;
                    case RESP_STAT:
                        EnumRespStatType respStatType = EnumRespStatType.dealWithRespStat(resp);
                        switch (respStatType) {
                            case NSR:
                                MethodTool.showToast(ShopmemberListActivity.this,"暂无管理员");
                                break;
                            default:break;
                        }
                    case RESP_ERROR:
                        MethodTool.showToast(ShopmemberListActivity.this,"");
                        initInnerList();
                    default:break;
                }
            }
        };
        NetUtil.sendHttpRequest(this,url,callback);
    }

    //获取内部教师
    private void initInnerList() {
        String url = "/QueryShopmemberList?s_id=" + s_id + "&type=2";
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(ShopmemberListActivity.this, Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                EnumRespType respType = EnumRespType.dealWithResponse(resp);
                switch (respType) {
                    case RESP_MAPLIST:
                        String [] keys = new String[] {"id","name","type"};
                        List<Map<String,String>> list = JsonHandler.strToListMap(resp,keys);
                        if (list.size() !=0 ) {
                            Map<String,String> header = new HashMap<>();
                            header.put("type","内部教师");
                            full_list.add(header);
                        }
                        for (int i = 0;i < list.size();i++) {
                            full_list.add(list.get(i));
                            inner_list.add(list.get(i));
                        }
                        initOuterList();
                        break;
                    case RESP_STAT:
                        EnumRespStatType respStatType = EnumRespStatType.dealWithRespStat(resp);
                        switch (respStatType) {
                            case NSR:
                                MethodTool.showToast(ShopmemberListActivity.this,"暂无内部教师");
                                break;
                            default:break;
                        }
                        break;
                    default:break;
                }
            }
        };
        NetUtil.sendHttpRequest(this,url,callback);
    }

    private void initOuterList() {
        String url = "/QueryShopmemberList?s_id=" + s_id + "&type=3";
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(ShopmemberListActivity.this,Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                EnumRespType respType = EnumRespType.dealWithResponse(resp);
                switch (respType) {
                    case RESP_MAPLIST:
                        String [] keys = new String[] {"id","name","type"};
                        List<Map<String,String>> list = JsonHandler.strToListMap(resp,keys);
                        if (list.size() != 0) {
                            Map<String,String> header = new HashMap<>();
                            header.put("type","外聘教师");
                            full_list.add(header);
                        }
                        for (int i = 0;i < list.size();i++) {
                            full_list.add(list.get(i));
                            outer_list.add(list.get(i));
                        }
                        initRecyclerView();
                        break;
                    case RESP_STAT:
                        EnumRespStatType respStatType = EnumRespStatType.dealWithRespStat(resp);
                        switch (respStatType) {
                            case NSR:
                                MethodTool.showToast(ShopmemberListActivity.this,"暂无外聘教师");
                                initRecyclerView();
                                break;
                            default:break;
                        }
                        break;
                    case RESP_ERROR:
                        MethodTool.showToast(ShopmemberListActivity.this,Ref.UNKNOWN_ERROR);
                        break;
                }

            }
        };
        NetUtil.sendHttpRequest(ShopmemberListActivity.this,url,callback);
    }

    private void initRecyclerView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            adapter = new RVSectionAdapter(full_list,ShopmemberListActivity.this);
            RecyclerView recyclerView = (RecyclerView)findViewById(R.id.rv_a_shopmember_list);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ShopmemberListActivity.this,LinearLayoutManager.VERTICAL,false);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(adapter);
            adapter.setOnItemClickListener(new RVSectionAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Intent intent = new Intent(ShopmemberListActivity.this,ShopmemberDetailActivity.class);
                    intent.putExtra("id",String.valueOf(full_list.get(position).get("id")));
                    intent.putExtra("name",full_list.get(position).get("name"));
                    intent.putExtra("type",String.valueOf(full_list.get(position).get("type")));
                    intent.putExtra("pos",position);
                    startActivityForResult(intent, Ref.REQCODE_QUERYDETAIL);
                }
            });
            }
        });

    }

    private void fullListReorder() {
        if (admin_list.size() != 0) {
            Map<String, String> header = new HashMap<>();
            header.put("type", "管理员");
            full_list.add(header);
        }
        for (int i = 0; i < admin_list.size(); i++) {
            full_list.add(admin_list.get(i));
        }
        if (inner_list.size() != 0) {
            Map<String, String> header = new HashMap<>();
            header.put("type", "内部教师");
            full_list.add(header);
        }
        for (int i = 0; i < inner_list.size(); i++) {
            full_list.add(inner_list.get(i));
        }
        if (outer_list.size() != 0) {
            Map<String, String> header = new HashMap<>();
            header.put("type", "外聘教师");
            full_list.add(header);
        }
        for (int i = 0; i < outer_list.size(); i++) {
            full_list.add(outer_list.get(i));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_shopmember_list,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home :
                finish();
                break;
            case R.id.add_a_shopmember_list :
                Intent intent = new Intent(ShopmemberListActivity.this,AddNewShopmemberActivity.class);
                startActivityForResult(intent, Ref.REQCODE_ADD);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int pos;
        //处理教师姓名更新
        if (requestCode == Ref.REQCODE_QUERYDETAIL) {
            if (resultCode == Ref.RESULTCODE_UPDATE) {
                pos = data.getIntExtra("pos", -1);
                full_list.clear();
                String type = String.valueOf(data.getIntExtra("type", 0));
                String new_name = data.getStringExtra("name");
                String id = String.valueOf(data.getLongExtra("id", 0));
                Map<String, String> map = new HashMap<>();
                map.put("id", id);
                map.put("name", new_name);
                map.put("type", type);
                //处理内部教师更新
                if (type.equals(Ref.TEACHER_ADMIN)) {
                    admin_list.set(pos - 1,map);
                    MethodTool.sortListMap(admin_list,"name");
                    fullListReorder();
                }
                if (type.equals(Ref.TEACHER_INNER)) {
                    inner_list.set(pos -admin_list.size() - 2, map);
                    MethodTool.sortListMap(inner_list,"name");
                    fullListReorder();
                //处理外聘教师更新
                } else if (type.equals(Ref.TEACHER_OUTER)) {
                    outer_list.set(pos - admin_list.size() - inner_list.size() - 3, map);
                    MethodTool.sortListMap(outer_list,"name");
                    fullListReorder();
                }
            //处理教师删除
            } else if (resultCode == Ref.RESULTCODE_DELETE) {
                pos = data.getIntExtra("pos", -1);
                full_list.remove(pos);
            }else if (resultCode == Ref.RESULTCODE_NULL) {

            }
        }else if (requestCode == Ref.REQCODE_ADD) {
            if (resultCode == Ref.RESULTCODE_ADD) {
                full_list.clear();
                String name = data.getStringExtra("name");
                String id = data.getStringExtra("id");
                int type = data.getIntExtra("type",0);
                Map<String,String> map = new HashMap<>();
                map.put("name",name);map.put("id",id);map.put("type",String.valueOf(type));
                if (String.valueOf(type).equals(Ref.TEACHER_ADMIN)) {
                    admin_list.add(map);
                    MethodTool.sortListMap(admin_list,"name");
                }else if (String.valueOf(type).equals(Ref.TEACHER_INNER)) {
                    inner_list.add(map);
                    MethodTool.sortListMap(inner_list,"name");
                }else if (String.valueOf(type).equals(Ref.TEACHER_OUTER)) {
                    outer_list.add(map);
                    MethodTool.sortListMap(outer_list,"name");
                }
                fullListReorder();
            }else if (resultCode == Ref.RESULTCODE_NULL) {

            }
        }
        adapter.notifyDataSetChanged();
    }

}

