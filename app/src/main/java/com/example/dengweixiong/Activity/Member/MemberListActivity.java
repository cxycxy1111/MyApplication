package com.example.dengweixiong.Activity.Member;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.dengweixiong.Adapter.SimpleRecylcerViewAdapter;
import com.example.dengweixiong.Util.BaseActivity;
import com.example.dengweixiong.myapplication.R;

public class MemberListActivity
        extends
        BaseActivity
        implements
            SimpleRecylcerViewAdapter.OnItemClickListener{

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private String[] strings = new String[] {"张三","李四","王五","慕容菲菲","邓伟雄"};
    private LinearLayoutManager linearLayoutManager;
    private SimpleRecylcerViewAdapter adapter;
    private static final String TOOLBAR_TITLE = "会员管理";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_list);
        initToolbar();
        initRecyclerView(MemberListActivity.this);
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
        adapter = new SimpleRecylcerViewAdapter(strings);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(this,MemberDetailActivity.class);
        intent.putExtra("toolbar_title",strings[position]);
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
                finish();
                break;
            default:
                break;
        }
        return true;
    }
}
