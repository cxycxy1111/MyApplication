package com.example.dengweixiong.Shopmember.Course.Course;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.dengweixiong.Util.BaseActivity;
import com.example.dengweixiong.Util.Enum.EnumRespStatType;
import com.example.dengweixiong.Util.Enum.EnumRespType;
import com.example.dengweixiong.Util.JsonHandler;
import com.example.dengweixiong.Util.MethodTool;
import com.example.dengweixiong.Util.NetUtil;
import com.example.dengweixiong.Util.Ref;
import com.example.dengweixiong.myapplication.R;

import org.apache.commons.lang.math.NumberUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AddCourseActivity
        extends
            BaseActivity
        implements
            View.OnClickListener{

    private int actual_cost;
    private static final int REQEST_CODE_ADD = 1;
    private static final int RESULT_CODE_SUC = 1;
    private static final int RESULT_CODE_FAIL = 2;
    private long shop_id,shopmember_id,selected_tea,selected_course,selected_stu,course_id;
    private String course_name,last_time,max_book_num,total_times,invalidtime;
    private Button btn,btn_person;
    private Toolbar toolbar;
    private Spinner tea_spinner,course_spinner,stu_spinner;
    private EditText et_course_name,et_last_time,et_max_num,et_person_course_name,et_max_times,et_invalidetime,et_actual_cost;
    private LinearLayout basicInfo_linearlayout,person_linearlayout;
    private List<String> course_type = new ArrayList<>();
    private List<String> tea = new ArrayList<>();
    private List<String> stu = new ArrayList<>();
    private List<Map<String,String>> origin_teacher = new ArrayList<>();
    private List<Map<String,String>> origin_student = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_add);
        initData();
        initViews();
    }

    private void initData() {
        course_type.add("会员课");course_type.add("教练班");course_type.add("集训课");course_type.add("私教课");
        SharedPreferences preferences = getSharedPreferences("sasm",MODE_PRIVATE);
        shop_id = preferences.getLong("s_id",0);
        shopmember_id = preferences.getLong("sm_id",0);
    }

    /**
     * 初始化教师数据
     */
    private void initTeacher() {
        String url = "/QueryShopmemberList?type=0";
        okhttp3.Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(AddCourseActivity.this, Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                String [] keys = new String[] {"id","name","type"};
                switch (EnumRespType.dealWithResponse(resp)) {
                    case RESP_MAPLIST:
                        origin_teacher = JsonHandler.strToListMap(resp,keys);
                        if (origin_teacher.size() == 0) {
                            Map<String,String> default_map = new HashMap<>();
                            default_map.put("id","0");default_map.put("name","暂无教师");
                            btn_person.setClickable(false);
                            origin_student.add(default_map);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initTeaSpinner();
                            }
                        });
                        break;
                    case RESP_ERROR:
                        MethodTool.showToast(AddCourseActivity.this,Ref.UNKNOWN_ERROR);
                        break;
                    case RESP_STAT:
                        switch (EnumRespStatType.dealWithRespStat(resp)) {
                            case SESSION_EXPIRED:
                                MethodTool.showExitAppAlert(AddCourseActivity.this);
                                break;
                            case NSR:
                                MethodTool.showToast(AddCourseActivity.this,Ref.OP_NSR);
                                break;
                            case AUTHORIZE_FAIL:
                                MethodTool.exitAcitivityDueToAuthorizeFail(AddCourseActivity.this);
                                break;
                            default:break;
                        }
                        break;
                    default:break;
                }


            }
        };
        NetUtil.sendHttpRequest(AddCourseActivity.this,url,callback);
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
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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
        String url = "/QueryMemberList";
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(AddCourseActivity.this, Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                switch (EnumRespType.dealWithResponse(resp)) {
                    case RESP_MAPLIST:
                        String [] keys = new String[] {"id","name"};
                        origin_student = JsonHandler.strToListMap(resp,keys);
                        if (origin_student.size() == 0) {
                            Map<String,String> default_map = new HashMap<>();
                            default_map.put("id","0");default_map.put("name","暂无会员");
                            origin_student.add(default_map);
                            btn_person.setClickable(false);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initStuSpinner();
                            }
                        });
                        break;
                    case RESP_STAT:
                        switch (EnumRespStatType.dealWithRespStat(resp)) {
                            case SESSION_EXPIRED:
                                MethodTool.showExitAppAlert(AddCourseActivity.this);
                                break;
                            case EMPTY_RESULT:
                                MethodTool.showToast(AddCourseActivity.this,Ref.STAT_EMPTY_RESULT);
                                break;
                            case AUTHORIZE_FAIL:
                                MethodTool.exitAcitivityDueToAuthorizeFail(AddCourseActivity.this);
                                break;
                            default:break;
                        }
                        break;
                    case RESP_ERROR:
                        MethodTool.showToast(AddCourseActivity.this,Ref.UNKNOWN_ERROR);
                    default:break;
                }

            }
        };
        NetUtil.sendHttpRequest(AddCourseActivity.this,url,callback);
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
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(AddCourseActivity.this,android.R.layout.simple_spinner_item,list);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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
        initLinearLayout();
        initButtons();
        initEditText();
        initCourseSpinner();

    }

    /**
     * 初始化课程类型
     */
    private void initCourseSpinner() {
        course_spinner = (Spinner)findViewById(R.id.sp_type_a_addNewCourse);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,android.R.id.text1,course_type);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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

    /**
     * 初始化EditText
     */
    private void initEditText() {
        et_course_name = (EditText)findViewById(R.id.et_name_a_addNewCourse);
        et_person_course_name = (EditText)findViewById(R.id.et_Personal_name_a_addNewCourse);
        et_last_time = (EditText)findViewById(R.id.et_timesperclass_a_addNewCourse);
        et_max_num = (EditText)findViewById(R.id.et_maxbooknum_a_addNewCourse);
        et_max_times = (EditText)findViewById(R.id.et_Personal_maxbooknum_a_addNewCourse);
        et_invalidetime = (EditText)findViewById(R.id.et_Personal_invaliddate_a_addNewCourse);
        et_actual_cost = (EditText)findViewById(R.id.et_Personal_actual_cost_a_addNewCourse);
    }

    /**
     * 初始化Toolbar
     */
    private void initToolbar() {
        toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        toolbar.setTitle("添加课程");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * 初始化LinearLayout
     */
    private void initLinearLayout() {
        basicInfo_linearlayout = (LinearLayout)findViewById(R.id.ll_basicInfo_a_addNewCourse);
        person_linearlayout = (LinearLayout)findViewById(R.id.ll_Personal_basicInfo_a_addNewCourse);
    }

    /**
     * 初始化Button
     */
    private void initButtons() {
        btn = (Button)findViewById(R.id.btn_addNewCourse);
        btn_person = (Button)findViewById(R.id.btn_person_addNewCourse);
        btn.setOnClickListener(this);
        btn_person.setOnClickListener(this);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQEST_CODE_ADD:
                AddCourseActivity.this.finish();
                break;
            default:break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_addNewCourse:
                Intent intent = new Intent(AddCourseActivity.this,AddSupportedCardActivity.class);
                course_name = et_course_name.getText().toString();
                last_time = et_last_time.getText().toString();
                max_book_num = et_max_num.getText().toString();
                if (course_name.equals("") || course_name.equals(null)) {
                    Toast.makeText(this,Ref.OP_EMPTY_ESSENTIAL_INFO,Toast.LENGTH_SHORT).show();
                    break;
                }
                if (last_time.equals(null) || last_time.equals("")) {
                    Toast.makeText(this,Ref.OP_EMPTY_ESSENTIAL_INFO,Toast.LENGTH_SHORT).show();
                    break;
                }
                if (!NumberUtils.isNumber(last_time)) {
                    Toast.makeText(this,Ref.OP_WRONG_NUMBER_FORMAT,Toast.LENGTH_SHORT).show();
                    break;
                }
                if (max_book_num.equals("")||max_book_num.equals(null)) {
                    Toast.makeText(this,Ref.OP_EMPTY_ESSENTIAL_INFO,Toast.LENGTH_SHORT).show();
                    break;
                }
                if (!NumberUtils.isNumber(max_book_num)) {
                    Toast.makeText(this,Ref.OP_WRONG_NUMBER_FORMAT,Toast.LENGTH_SHORT).show();
                    break;
                }
                submitCourse();
                break;
            case R.id.btn_person_addNewCourse :
                course_name = et_person_course_name.getText().toString();
                total_times = et_max_times.getText().toString();
                invalidtime = et_invalidetime.getText().toString();
                String str_actual_cost = et_actual_cost.getText().toString();
                if (course_name.equals("")||course_name.equals(null)) {
                    Toast.makeText(this,Ref.OP_EMPTY_ESSENTIAL_INFO,Toast.LENGTH_SHORT).show();
                    break;
                }
                if (total_times.equals("") || total_times.equals(null)) {
                    Toast.makeText(this,Ref.OP_WRONG_NUMBER_FORMAT,Toast.LENGTH_SHORT).show();
                    break;
                }
                if (!NumberUtils.isNumber(total_times)) {
                    Toast.makeText(this,Ref.OP_WRONG_NUMBER_FORMAT,Toast.LENGTH_SHORT).show();
                    break;
                }
                if (!NumberUtils.isNumber(str_actual_cost)) {
                    Toast.makeText(this,Ref.OP_WRONG_NUMBER_FORMAT,Toast.LENGTH_SHORT).show();
                    break;
                }
                actual_cost = Integer.valueOf(str_actual_cost);
                submitPrivateCourse();
                break;
            default:break;
        }
    }

    /**
     * 提交新增私教课程
     */
    private void submitPrivateCourse() {
        String url = "/CourseAddPrivate?sm_id=" + selected_tea + "&m_id=" + selected_stu + "&name="
                + course_name + "&type=" + selected_course + "&total_times=" + total_times
                + "&e_time=" + invalidtime + "&actual_cost=" + actual_cost;
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(AddCourseActivity.this, Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                switch (EnumRespType.dealWithResponse(resp)) {
                    case RESP_DATA:
                        MethodTool.showToast(AddCourseActivity.this,Ref.OP_ADD_SUCCESS);
                        break;
                    case RESP_MAPLIST:
                        switch (EnumRespStatType.dealWithRespStat(resp)) {
                            case EXE_FAIL:
                                MethodTool.showToast(AddCourseActivity.this, Ref.OP_ADD_FAIL);
                                setResult(RESULT_CODE_FAIL);
                                AddCourseActivity.this.finish();
                                break;
                            case EXE_SUC:
                                MethodTool.showToast(AddCourseActivity.this, Ref.OP_ADD_SUCCESS);
                                setResult(RESULT_CODE_SUC);
                                AddCourseActivity.this.finish();
                                break;
                            case NOT_MATCH:
                                MethodTool.showToast(AddCourseActivity.this, Ref.STAT_INST_NOT_MATCH);
                                break;
                            case NST_NOT_MATCH:
                                MethodTool.showToast(AddCourseActivity.this, Ref.STAT_INST_NOT_MATCH);
                                break;
                            case SESSION_EXPIRED:
                                MethodTool.showExitAppAlert(AddCourseActivity.this);
                                break;
                            default:
                                MethodTool.showToast(AddCourseActivity.this, Ref.UNKNOWN_ERROR);
                                setResult(RESULT_CODE_FAIL);
                                AddCourseActivity.this.finish();
                                break;
                        }
                        break;
                    default:break;
                }
            }
        };
        NetUtil.sendHttpRequest(AddCourseActivity.this,url,callback);
    }

    private void submitCourse() {
        String url = "/courseAdd?name=" + course_name + "&type="
                + selected_course + "&last_time=" + last_time + "&max_book_num="
                + max_book_num + "&summary=";
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(AddCourseActivity.this, Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                EnumRespType respType = EnumRespType.dealWithResponse(resp);
                switch (respType) {
                    case RESP_ERROR:
                        MethodTool.showToast(AddCourseActivity.this, Ref.UNKNOWN_ERROR);
                        break;
                    case RESP_STAT:
                        EnumRespStatType respStatType = EnumRespStatType.dealWithRespStat(resp);
                        switch (respStatType) {
                            case NOT_MATCH:
                                MethodTool.showToast(AddCourseActivity.this,"课程类型不匹配");
                                break;
                            case NST_NOT_MATCH:
                                MethodTool.showToast(AddCourseActivity.this, Ref.STAT_INST_NOT_MATCH);
                                break;
                            case DUPLICATE:
                                MethodTool.showToast(AddCourseActivity.this,"课程名称重复");
                                break;
                            case EXE_FAIL:
                                MethodTool.showToast(AddCourseActivity.this,Ref.OP_ADD_SUCCESS);
                                break;
                            case SESSION_EXPIRED:
                                MethodTool.showExitAppAlert(AddCourseActivity.this);
                                break;
                            case AUTHORIZE_FAIL:
                                MethodTool.showAuthorizeFailToast(AddCourseActivity.this);
                                break;
                            default:break;
                        }
                        break;
                    case RESP_DATA:
                        HashMap<String,String> temp_map = JsonHandler.strToMap(resp);
                        String s = temp_map.get(Ref.DATA);
                        course_id = Long.valueOf(s);
                        Intent intent = new Intent(AddCourseActivity.this,AddSupportedCardActivity.class);
                        intent.putExtra("course_id",course_id);
                        startActivityForResult(intent,REQEST_CODE_ADD);
                        break;
                    default:break;
                }
            }
        };
        NetUtil.sendHttpRequest(AddCourseActivity.this,url,callback);
    }

}
