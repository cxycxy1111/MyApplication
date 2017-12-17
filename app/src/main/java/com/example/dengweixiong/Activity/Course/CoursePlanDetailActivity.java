package com.example.dengweixiong.Activity.Course;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.example.dengweixiong.Util.MethodTool;
import com.example.dengweixiong.myapplication.R;

public class CoursePlanDetailActivity extends AppCompatActivity {

    private String str_sm_id,str_s_id,str_course_name,str_time;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_plan_detail);
        initData();
        initToolBar();
    }

    private void initData(){
        str_s_id = MethodTool.getSharePreferenceValue(this,"sasm","s_id",2);
        str_s_id = MethodTool.getSharePreferenceValue(this,"sasm","sm_id",2);
        Intent intent = getIntent();
        str_course_name = intent.getStringExtra("course_name");
        str_time = intent.getStringExtra("time");
    }

    private void initToolBar() {
        toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(str_course_name);
        getSupportActionBar().setSubtitle(str_time);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
