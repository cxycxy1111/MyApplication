package com.example.dengweixiong.Activity.Course;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.dengweixiong.myapplication.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CourseDetailActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ListView listView;
    private SimpleAdapter adapter;
    private String [] left = {"课程名称","每节课时长","最高预约人数"};
    private String [] left_supportedcard;
    private String [] right = {"会员课","40分钟","30人"};
    private String [] right_supportedcard;
    private String [] res = {"main","hint"};
    private int [] id = {R.id.tv_main_text_list_with_hint,R.id.tv_hint_text_list_with_hint};
    private int [] id_supportedcard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);
        initToolbar();
        initListView();
    }

    private void initToolbar() {
        toolbar = (Toolbar)findViewById(R.id.tb_a_course_detail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getIntent().getStringExtra("name"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initListView() {
        adapter = new SimpleAdapter(this,initData(),R.layout.tile_list_with_hint,res,id);
        listView = (ListView)findViewById(R.id.lv_a_course_detail);
        listView.setAdapter(adapter);
    }

    private ArrayList<HashMap<String,Object>> initData(){
        ArrayList<HashMap<String,Object>> mapList = new ArrayList<>();
        for (int i = 0;i < left.length;i++) {
            HashMap<String,Object> map = new HashMap<>();
            map.put("main",left[i]);
            map.put("hint",right[i]);
            mapList.add(map);
        }
        return mapList;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
        }
        return true;
    }
}
