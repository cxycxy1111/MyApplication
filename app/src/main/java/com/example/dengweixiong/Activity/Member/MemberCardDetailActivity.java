package com.example.dengweixiong.Activity.Member;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.dengweixiong.Util.BaseActivity;
import com.example.dengweixiong.myapplication.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemberCardDetailActivity
        extends BaseActivity
        implements ListView.OnItemClickListener{

    private Toolbar toolbar;
    private ListView listView;
    private ListView listView_action;
    private static final String TOOLBAR_TITLE = "会员卡详情";
    private SimpleAdapter adapter;
    private SimpleAdapter adapter_action;
    private String [] from={"left","right"};
    private String [] from_action={"left"};
    private int [] to = {R.id.tv_main_text_list_with_hint,R.id.tv_hint_text_list_with_hint};
    private int[] to_action = {R.id.tv_action};
    private String [] left = {"会员卡类型","卡内余额","生效时间","失效时间"};
    private String [] left_action = {"充值","扣费"};
    private String [] right = {"余额卡","200元","2016-10-08","2017-10-08"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_card_detail);
        initToolbar();
        initListView();
        initListViewAction();
    }

    private void initToolbar() {
        toolbar = (Toolbar)findViewById(R.id.tb_a_member_card_detail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(TOOLBAR_TITLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initListView() {
        adapter = new SimpleAdapter(this,initData(left,right),R.layout.tile_list_with_hint,from,to);
        listView = (ListView)findViewById(R.id.lv_a_member_card_detail);
        listView.setAdapter(adapter);
    }

    private void initListViewAction() {
        adapter_action = new SimpleAdapter(this,initDataAction(left_action),R.layout.tile_action,from_action,to_action);
        listView_action = (ListView)findViewById(R.id.lv_action_a_member_card_detail);
        listView_action.setAdapter(adapter_action);
        listView_action.setOnItemClickListener(this);
    }

    private List<Map<String,Object>> initData(String[] str_1,String[] str_2) {
        List<Map<String,Object>> mapList = new ArrayList<>();
        for (int i = 0;i < str_1.length;i++) {
            Map<String,Object> map = new HashMap<>();
            map.put("left",str_1[i]);
            map.put("right",str_2[i]);
            mapList.add(map);
        }
        return mapList;
    }

    private List<Map<String,Object>> initDataAction(String[] str_1) {
        List<Map<String,Object>> mapList = new ArrayList<>();
        for (int i = 0;i<str_1.length;i++) {
            Map<String,Object> map = new HashMap<>();
            map.put("left",str_1[i]);
            mapList.add(map);
        }
        return mapList;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent == listView_action) {
            Intent intent;
            switch (position) {
                case 0:
                    intent = new Intent(this,ChargeActivity.class);
                    startActivity(intent);
                    this.finish();
                    break;
                case 1:
                    intent = new Intent(this,DeductionActivity.class);
                    startActivity(intent);
                    this.finish();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            default:
                break;
        }
        return true;
    }
}
