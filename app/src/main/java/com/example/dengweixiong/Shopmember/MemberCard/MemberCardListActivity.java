package com.example.dengweixiong.Shopmember.MemberCard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import com.example.dengweixiong.Shopmember.Main.ShopmemberMainActivity;
import com.example.dengweixiong.Shopmember.Member.MemberDetailActivity;
import com.example.dengweixiong.Util.BaseActivity;
import com.example.dengweixiong.myapplication.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemberCardListActivity
        extends BaseActivity
        implements Spinner.OnItemClickListener{

    private Toolbar toolbar;
    private Spinner spinner;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private SimpleAdapter member_card_adapter;
    private String[] from = {"main","hint"};
    private int[] to = {R.id.tv_main_text_list_with_hint,R.id.tv_hint_text_list_with_hint};
    private String[] strings = {"会员卡1","会员卡2","会员卡3"};
    private String[] strings_2 = {"1次","200元",""};
    private String [] name = {"张三","李四"};
    private String source;
    List<Map<String,Object>> mapList = new ArrayList<Map<String, Object>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_card_list);
        source = getIntent().getStringExtra("source");
        initToolbar();
        initSpinner();
        initListView();
    }

    private void initToolbar() {
        toolbar = (Toolbar)findViewById(R.id.toolbar_a_member_card_list);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("会员卡列表");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initSpinner() {
        spinner = (Spinner)findViewById(R.id.spinner_member_a_member_card_list);
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,name);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void initListView() {
        mapList = initData(strings,strings_2);
        member_card_adapter = new SimpleAdapter(this,mapList,R.layout.tile_list_with_hint,from,to);
        listView = (ListView)findViewById(R.id.lv_a_member_card_list);
        listView.setAdapter(member_card_adapter);
        listView.setOnItemClickListener(this);
    }

    private List<Map<String,Object>> initData(String [] strings,String [] strings_2) {
        List<Map<String,Object>> temp = new ArrayList<Map<String, Object>>();
        for (int i=0;i<strings.length;i++) {
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("hint",strings_2[i]);
            map.put("main",strings[i]);
            temp.add(map);
        }
        return temp;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                if (source == "MemberFragment") {
                    intent = new Intent(this,ShopmemberMainActivity.class);
                    startActivity(intent);
                } else if (source == "MemberDetailActivity") {
                    intent = new Intent(this,MemberDetailActivity.class);
                    startActivity(intent);
                }
                this.finish();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent;
        if (parent == listView) {
            switch (position) {
                case 0:
                    intent = new Intent(this,MemberCardDetailActivity.class);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    }
}
