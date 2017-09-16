package com.example.dengweixiong.Activity.Member;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.dengweixiong.Adapter.SimpleRecylcerViewAdapter;
import com.example.dengweixiong.myapplication.R;

public class MemberDetailActivity
        extends AppCompatActivity
        implements SimpleRecylcerViewAdapter.OnItemClickListener{

    Toolbar toolbar;
    private String toolbar_title;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;
    private String [] strings = {"查看会员卡","重置密码","删除会员卡"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_detail);
        initToolBar();
        initActions();
    }

    private void initToolBar () {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        toolbar_title = (String)bundle.get("toolbar_title");
        toolbar = (Toolbar)findViewById(R.id.toolbar_activity_member_detail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(toolbar_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initActions() {
        recyclerView = (RecyclerView)findViewById(R.id.recyclerview_activity_member_detail);
        linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        SimpleRecylcerViewAdapter simpleRecylcerViewAdapter = new SimpleRecylcerViewAdapter(strings);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(simpleRecylcerViewAdapter);
        simpleRecylcerViewAdapter.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_member_detail,menu);
        return true;
    }

    @Override
    public void onItemClick(View view, int positon) {
        Intent intent;
        switch (positon) {
            case 0:
                intent = new Intent(this,MemberCardListActivity.class);
                intent.putExtra("source","MemberDetailActivity");
                startActivity(intent);
                break;
            case 1:
                intent = new Intent(MemberDetailActivity.this,ResetMemberPwdActivity.class);
                startActivity(intent);
                break;
            case 2:
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.save_member_detail:
                this.finish();
                break;
            default:
                break;
        }
        return true;
    }
}
