package com.example.dengweixiong.Activity.Course;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ScrollingTabContainerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.example.dengweixiong.Adapter.CourseListRVAdapter;
import com.example.dengweixiong.Bean.Course;
import com.example.dengweixiong.Util.BaseActivity;
import com.example.dengweixiong.Util.JsonHandler;
import com.example.dengweixiong.Util.MethodTool;
import com.example.dengweixiong.Util.NetUtil;
import com.example.dengweixiong.Util.Reference;
import com.example.dengweixiong.myapplication.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AddNewCourseActivity
        extends
            BaseActivity
        implements
            View.OnClickListener{

    private Toolbar toolbar;
    private Spinner tea_spinner;
    private Spinner course_spinner;
    private Spinner stu_spinner;
    private LinearLayout basicInfo_linearlayout;
    private LinearLayout person_linearlayout;
    private List<String> course_type = new ArrayList<>();
    private List<String> tea = new ArrayList<>();
    private List<String> stu = new ArrayList<>();
    private List<Map<String,String>> origin_teacher = new ArrayList<>();
    private List<Map<String,String>> origin_student = new ArrayList<>();
    private long shop_id;
    private long selected_tea;
    private long selected_course;
    private long selected_stu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_course);
        initData();
        initViews();
    }

    private void initData() {
        course_type.add("会员课");course_type.add("教练班");course_type.add("集训课");course_type.add("私教课");
        SharedPreferences preferences = getSharedPreferences("sasm",MODE_PRIVATE);
        shop_id = preferences.getLong("s_id",0);
    }

    /**
     * 初始化教师数据
     */
    private void initTeacher() {
        String url = "/QueryShopmemberList?shop_id=" + shop_id + "&type=0";
        okhttp3.Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(AddNewCourseActivity.this, Reference.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                String [] keys = new String[] {"id","name","type"};
                origin_teacher = JsonHandler.strToListMap(resp,keys);
                if (origin_teacher.size() == 0) {
                    Map<String,String> default_map = new HashMap<>();
                    default_map.put("id","0");default_map.put("name","暂无教师");
                    origin_student.add(default_map);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initTeaSpinner();
                    }
                });
            }
        };
        NetUtil.sendHttpRequest(AddNewCourseActivity.this,url,callback);
    }

    /**
     * 初始化教师选择的spinner
     */
    private void initTeaSpinner() {
        tea_spinner = (Spinner)findViewById(R.id.sp_teacher_a_addNewCourse);
        List<String> list = new ArrayList<>();
        for (int i = 0;i < origin_teacher.size();i++) {
            list.add(origin_teacher.get(i).get("name"));
        }
        ArrayAdapter adapter = new ArrayAdapter(this,R.layout.tile_spinner,R.id.tile_spinner_text,list);
        adapter.setDropDownViewResource(R.layout.tile_spinner_dropdown);
        tea_spinner.setAdapter(adapter);
        tea_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_tea = Long.parseLong(String.valueOf(origin_teacher.get(position).get("id")));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selected_tea = 0;
            }
        });
    }

    /**
     * 初始化学生数据
     */
    private void initStudent() {
        String url = "/QueryMemberList?shop_id=" + shop_id;
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(AddNewCourseActivity.this,Reference.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                String [] keys = new String[] {"id","name"};
                origin_student = JsonHandler.strToListMap(resp,keys);
                if (origin_student.size() == 0) {
                    Map<String,String> default_map = new HashMap<>();
                    default_map.put("id","0");default_map.put("name","暂无会员");
                    origin_student.add(default_map);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initStuSpinner();
                    }
                });
            }
        };
        NetUtil.sendHttpRequest(AddNewCourseActivity.this,url,callback);
    }

    /**
     * 初始化学生spinner
     */
    private void initStuSpinner() {
        stu_spinner = (Spinner)findViewById(R.id.sp_stu_a_addNewCourse);
        List<String> list = new ArrayList<>();
        for (int i = 0;i < origin_student.size();i++) {
            list.add(origin_student.get(i).get("name"));
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(AddNewCourseActivity.this,R.layout.tile_spinner,R.id.tile_spinner_text,list);
        arrayAdapter.setDropDownViewResource(R.layout.tile_spinner_dropdown);
        stu_spinner.setAdapter(arrayAdapter);
        stu_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Map<String,String> map = new HashMap<>();
                map = origin_student.get(position);
                String s = String.valueOf(map.get("id"));
                selected_stu = Long.parseLong(s);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    private void initViews() {
        initToolbar();
        basicInfo_linearlayout = (LinearLayout)findViewById(R.id.ll_basicInfo_a_addNewCourse);
        person_linearlayout = (LinearLayout)findViewById(R.id.ll_Personal_basicInfo_a_addNewCourse);
        Button btn = (Button)findViewById(R.id.btn_addNewCourse);
        Button btn_person = (Button)findViewById(R.id.btn_person_addNewCourse);
        btn.setOnClickListener(this);
        btn_person.setOnClickListener(this);
        initCourseSpinner();

    }

    /**
     * 初始化课程类型
     */
    private void initCourseSpinner() {
        course_spinner = (Spinner)findViewById(R.id.sp_type_a_addNewCourse);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this,R.layout.tile_spinner,R.id.tile_spinner_text,course_type);
        arrayAdapter.setDropDownViewResource(R.layout.tile_spinner_dropdown);
        course_spinner.setAdapter(arrayAdapter);
        course_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        selected_course = 1;
                        basicInfo_linearlayout.setVisibility(View.VISIBLE);
                        person_linearlayout.setVisibility(View.GONE);
                        break;
                    case 1:
                        selected_course = 2;
                        basicInfo_linearlayout.setVisibility(View.VISIBLE);
                        person_linearlayout.setVisibility(View.GONE);
                        break;
                    case 2:
                        selected_course = 3;
                        basicInfo_linearlayout.setVisibility(View.VISIBLE);
                        person_linearlayout.setVisibility(View.GONE);
                        break;
                    case 3:
                        selected_course = 4;
                        basicInfo_linearlayout.setVisibility(View.GONE);
                        person_linearlayout.setVisibility(View.VISIBLE);
                        initTeacher();
                        initStudent();
                        break;
                    default:break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selected_course = 1;
            }
        });
    }


    private void initToolbar() {
        toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        toolbar.setTitle("添加课程");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:break;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(AddNewCourseActivity.this,AddSupportedCardActivity.class);
        switch (v.getId()) {
            case R.id.btn_addNewCourse:
                intent.putExtra("selected_course",selected_course);
                break;
            case R.id.btn_person_addNewCourse :
                intent.putExtra("selected_course",selected_course);
                intent.putExtra("selected_tea",selected_tea);
                intent.putExtra("selected_stu",selected_stu);
                break;
            default:break;
        }
        startActivity(intent);
    }
}
