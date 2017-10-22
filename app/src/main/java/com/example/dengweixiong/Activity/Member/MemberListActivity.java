package com.example.dengweixiong.Activity.Member;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.dengweixiong.Activity.MainActivity;
import com.example.dengweixiong.Adapter.SimpleRecylcerViewAdapter;
import com.example.dengweixiong.Util.BaseActivity;
import com.example.dengweixiong.Util.JsonHandler;
import com.example.dengweixiong.Util.MethodTool;
import com.example.dengweixiong.Util.NetUtil;
import com.example.dengweixiong.myapplication.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MemberListActivity
        extends
            BaseActivity
        implements
            SimpleRecylcerViewAdapter.OnItemClickListener{

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private List<Map<String,String>> list = new ArrayList<>();
    private ArrayList<String> name_list = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private SimpleRecylcerViewAdapter adapter;
    private static final String TOOLBAR_TITLE = "会员管理";
    private static final String TAG = "MemberListActivity:";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_list);
        initToolbar();
        initData();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int i = data.getIntExtra("m_id",-1);
        list.remove(i);
        name_list.remove(i);
        adapter.notifyDataSetChanged();
    }

    //初始化toolbar
    private void initToolbar() {
        toolbar = (Toolbar)findViewById(R.id.toolbar_activity_member_list);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(TOOLBAR_TITLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

//初始化recyclerview
    private void initRecyclerView(Context context) {
        recyclerView = (RecyclerView)findViewById(R.id.recyclerview_activity_member_list);
        linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        adapter = new SimpleRecylcerViewAdapter(name_list);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void initData() {
        long shop_id = MethodTool.preGetLong(MemberListActivity.this,"sasm","s_id");
        String url = "/QueryMemberList?shop_id=" + shop_id;
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String [] keys = new String[] {"id","name"};
                String resp = response.body().string();
                list = JsonHandler.strToListMap(resp,keys);
                Log.d(TAG, String.valueOf(list.size()));
                for (int i = 0;i < list.size();i++) {
                    name_list.add(list.get(i).get("name"));
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    initRecyclerView(MemberListActivity.this);
                    }
                });
            }
        };
        NetUtil.sendHttpRequest(MemberListActivity.this,url,callback);
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(this,MemberDetailActivity.class);
        intent.putExtra("toolbar_title",name_list.get(position));
        intent.putExtra("m_id",String.valueOf(list.get(position).get("id")));
        intent.putExtra("position",position);
        startActivity(intent);
    }

    //创建菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_member_list,menu);
        MenuItem menuItem = menu.findItem(R.id.search_main);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        MenuItemCompat.setOnActionExpandListener(menuItem,expandListener);
        return true;
    }

//搜索条
    MenuItemCompat.OnActionExpandListener expandListener = new MenuItemCompat.OnActionExpandListener() {
        @Override
        public boolean onMenuItemActionExpand(MenuItem item) {
            return true;
        }

        @Override
        public boolean onMenuItemActionCollapse(MenuItem item) {
            return true;
        }
    };
//菜单选中事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(MemberListActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return true;
    }
}
