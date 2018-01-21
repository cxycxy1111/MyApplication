package com.example.dengweixiong.Shopmember.Course.CoursePlan;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AddCoursePlanActivity
        extends BaseActivity
        implements View.OnClickListener,EditText.OnFocusChangeListener{

    private int current_year,current_month,current_date,current_hour,current_minute;
    private String sm_id,s_id,selected_course,selected_teacher,selected_classroom,selected_course_type,current_courseplan;
    private String [] keys_course_list = new String[] {"id","name","last_time","type","supportedcard"};
    private String [] keys_classroom_list = new String[] {"id","name"};
    private String [] keys_teacher_list = new String[] {"id","name","type"};
    private Button btn_next;
    private EditText et_date,et_time,et_last_time;
    private Spinner sp_course,sp_classroom;
    private DatePickerDialog dpd_date;
    private TimePickerDialog tpd_time;
    private List<String> list_course_name = new ArrayList<>();
    private List<String> list_classroom_name = new ArrayList<>();
    private List<String> list_teacher_name = new ArrayList<>();
    private List<Map<String,String>> mapList_course_full = new ArrayList<>();
    private List<Map<String,String>> mapList_classroom_full = new ArrayList<>();
    private List<Map<String,String>> mapList_teacher_full = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_plan_add);
        initData();
        initToolbar();
        initWidgets();
        initStartDatePickerDialog();
        initStartTimePickerDialog();
        initCourses();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next_step_add_new_courseplan:
                saveCoursePlan();
                break;
            case R.id.et_date_add_new_coursepalan:
                dpd_date.show();
                break;
            case R.id.et_time_add_new_coursepalan:
                tpd_time.show();
                break;
            default:break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.et_date_add_new_coursepalan:
                if (hasFocus) {
                    dpd_date.show();
                }
                break;
            case R.id.et_time_add_new_coursepalan:
                if (hasFocus) {
                    tpd_time.show();
                }
            default:break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_new_course_plan,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.quit_send_new_trend:
                break;
            case R.id.send_add_new_trend:
                break;
            case android.R.id.home:
                finish();
                break;
            default:break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Ref.REQCODE_ADD:
                switch (resultCode) {
                    case Ref.RESULTCODE_ADD:
                        AddCoursePlanActivity.this.finish();
                        break;
                    default:break;
                }
                break;
            default:break;
        }
    }

    private void initData() {
        sm_id = MethodTool.getSharePreferenceValue(AddCoursePlanActivity.this,"sasm","sm_id",2);
        s_id = MethodTool.getSharePreferenceValue(AddCoursePlanActivity.this,"sasm","s_id",2);

        Calendar calendar = Calendar.getInstance();
        this.current_year = calendar.get(Calendar.YEAR);
        this.current_month = calendar.get(Calendar.MONTH);
        this.current_date = calendar.get(Calendar.DAY_OF_MONTH);
        this.current_hour = calendar.get(Calendar.HOUR);
        this.current_minute = calendar.get(Calendar.MINUTE);
    }

    private void initToolbar () {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_general);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("添加排课");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initWidgets() {
        btn_next = (Button)findViewById(R.id.btn_next_step_add_new_courseplan);
//        sp_teacher = (Spinner)findViewById(R.id.sp_tea_add_new_courseplan);
        sp_classroom = (Spinner)findViewById(R.id.sp_classroom_add_new_courseplan);
        sp_course = (Spinner)findViewById(R.id.sp_course_add_new_courseplan);
        et_date = (EditText)findViewById(R.id.et_date_add_new_coursepalan);
        et_time = (EditText)findViewById(R.id.et_time_add_new_coursepalan);
        et_last_time = (EditText)findViewById(R.id.et_last_time_add_new_coursepalan);

        btn_next.setOnClickListener(this);
        //设置开始日期的输入框的属性
        et_date.setInputType(InputType.TYPE_NULL);
        et_date.setOnClickListener(this);
        et_date.setOnFocusChangeListener(this);
        //设置开始时间输入框的属性
        et_time.setInputType(InputType.TYPE_NULL);
        et_time.setOnClickListener(this);
        et_time.setOnFocusChangeListener(this);
    }

    private void initStartDatePickerDialog() {
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                et_date.setText(new StringBuffer().append(year).append("-").append(month+1).append("-").append(dayOfMonth));
            }
        };
        dpd_date = new DatePickerDialog(AddCoursePlanActivity.this,listener,current_year,current_month,current_date);
    }

    private void initStartTimePickerDialog() {
        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                et_time.setText(String.valueOf(hourOfDay) + ":" + String.valueOf(minute));
            }
        };
        tpd_time = new TimePickerDialog(AddCoursePlanActivity.this,listener,current_hour,current_minute,true);
    }

    private void initCourses() {
        String url = "/CourseListQuery?s_id=" + s_id;
        okhttp3.Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToastAndFinish(AddCoursePlanActivity.this, Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                EnumRespType respType = EnumRespType.dealWithResponse(resp);
                switch (respType) {
                    case RESP_MAPLIST:
                        mapList_course_full = JsonHandler.strToListMap(resp,keys_course_list);
                        for (int i = 0;i < mapList_course_full.size();i++) {
                            list_course_name.add(String.valueOf(mapList_course_full.get(i).get(keys_course_list[1])));
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initCourseSpinner();
                            }
                        });
                        break;
                    case RESP_STAT:
                        EnumRespStatType respStatType = EnumRespStatType.dealWithRespStat(resp);
                        switch (respStatType) {
                            case NSR:
                                MethodTool.showToast(AddCoursePlanActivity.this,"暂无此舞馆");
                                AddCoursePlanActivity.this.finish();
                                break;
                            case EMPTY_RESULT:
                                MethodTool.showToast(AddCoursePlanActivity.this,"舞馆尚未安排课程");
                                AddCoursePlanActivity.this.finish();
                                break;
                            default:break;
                        }
                        break;
                    case RESP_ERROR:
                        MethodTool.showToast(AddCoursePlanActivity.this,Ref.UNKNOWN_ERROR);
                        AddCoursePlanActivity.this.finish();
                        break;
                    default:break;
                }
            }
        };
        NetUtil.sendHttpRequest(AddCoursePlanActivity.this,url,callback);
    }

    private void initCourseSpinner() {
        ArrayAdapter<String> adapter_course_name = new ArrayAdapter<String>(AddCoursePlanActivity.this,android.R.layout.simple_spinner_item,list_course_name);
        adapter_course_name.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_course.setAdapter(adapter_course_name);
        sp_course.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_course = String.valueOf(mapList_course_full.get(position).get(keys_course_list[0]));
                selected_course_type = String.valueOf(mapList_course_full.get(position).get(keys_course_list[3]));
                if (selected_course_type.equals(String.valueOf(Ref.COURSE_TYPE_PERSON))) {
                    btn_next.setText("保存");
                }else {
                    btn_next.setText("下一步");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selected_course = String.valueOf(mapList_course_full.get(0).get(keys_course_list[0]));
                selected_course_type = String.valueOf(mapList_course_full.get(0).get(keys_course_list[3]));
                if (selected_course_type.equals(String.valueOf(Ref.COURSE_TYPE_PERSON))) {
                    btn_next.setText("保存");
                }else {
                    btn_next.setText("下一步");
                }
            }
        });
        initClassroom();
    }

    private void initClassroom() {
        String url = "/ClassroomListQuery?s_id=" + s_id;
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToastAndFinish(AddCoursePlanActivity.this,Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                EnumRespType respType = EnumRespType.dealWithResponse(resp);
                switch (respType) {
                    case RESP_MAPLIST:
                        mapList_classroom_full = JsonHandler.strToListMap(resp,keys_classroom_list);
                        for (int i = 0;i < mapList_classroom_full.size();i++) {
                            list_classroom_name.add(String.valueOf(mapList_classroom_full.get(i).get(keys_classroom_list[1])));
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initClassroomSpinner();
                            }
                        });
                        break;
                    case RESP_STAT:
                        EnumRespStatType respStatType = EnumRespStatType.dealWithRespStat(resp);
                        switch (respStatType) {
                            case NSR:
                                MethodTool.showToast(AddCoursePlanActivity.this,"暂无课室，请先新增");
                                AddCoursePlanActivity.this.finish();
                                break;
                            default:break;
                        }
                        break;
                    case RESP_ERROR:
                        MethodTool.showToast(AddCoursePlanActivity.this,Ref.UNKNOWN_ERROR);
                        AddCoursePlanActivity.this.finish();
                        break;
                    default:break;
                }
            }
        };
        NetUtil.sendHttpRequest(AddCoursePlanActivity.this,url,callback);
    }

    private void initClassroomSpinner() {
        ArrayAdapter<String> adapter_classroom_name = new ArrayAdapter<String>(AddCoursePlanActivity.this,android.R.layout.simple_spinner_item,list_classroom_name);
        adapter_classroom_name.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_classroom.setAdapter(adapter_classroom_name);
        sp_classroom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_classroom = String.valueOf(mapList_classroom_full.get(position).get(keys_classroom_list[0]));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selected_classroom = String.valueOf(mapList_classroom_full.get(0).get(keys_classroom_list[0]));
            }
        });
    }

    private void saveCoursePlan() {
        String str_date = et_date.getText().toString();
        String str_time = et_time.getText().toString();
        String str_last_time = et_last_time.getText().toString();
        //判断是否为空
        if (str_date.equals("") || str_date.equals(null) ||
                str_time.equals("")|| str_time.equals(null)||
                str_last_time.equals("")||str_last_time.equals(null)) {
            Toast.makeText(AddCoursePlanActivity.this,Ref.OP_EMPTY_ESSENTIAL_INFO,Toast.LENGTH_SHORT).show();
        }else {
            String datetime = str_date + " " + str_time + ":00";
            //判断持续时间是否为数字
            if (NumberUtils.isNumber(str_last_time)) {
                int int_last_time = Integer.parseInt(str_last_time);
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Calendar calendar = Calendar.getInstance();
                try {
                    Date date = sdf.parse(datetime);
                    calendar.setTime(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String str_start_time = sdf.format(calendar.getTime());
                calendar.add(Calendar.MINUTE,int_last_time);
                String str_end_time = sdf.format(calendar.getTime());
                String url = "/CoursePlanAdd?c_id=" + selected_course +
                        "&cr_id=" + selected_classroom +
                        "&lmu_id=" + sm_id +
                        "&s_time=" + str_start_time +
                        "&e_time=" + str_end_time +
                        "&remark=";
                Callback callback = new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        MethodTool.showToastAndFinish(AddCoursePlanActivity.this,Ref.CANT_CONNECT_INTERNET);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();
                        EnumRespType respType = EnumRespType.dealWithResponse(resp);
                        switch (respType) {
                            case RESP_MAP:
                                Map<String,String> map = new HashMap<>();
                                map = JsonHandler.strToMap(resp);
                                current_courseplan = String.valueOf(map.get(Ref.DATA));
                                switch (selected_course_type) {
                                    case "4":
                                        AddCoursePlanActivity.this.finish();
                                        break;
                                    default:
                                        Intent intent = new Intent(AddCoursePlanActivity.this,AddCoursePlanTeacherActivity.class);
                                        intent.putExtra("cp_id",current_courseplan);
                                        //启动新界面
                                        AddCoursePlanActivity.this.startActivityForResult(intent,Ref.REQCODE_ADD);
                                        AddCoursePlanActivity.this.finish();
                                        break;
                                }
                                break;
                            case RESP_ERROR:
                                MethodTool.showToast(AddCoursePlanActivity.this,Ref.UNKNOWN_ERROR);
                                break;
                            case RESP_STAT:
                                EnumRespStatType respStatType = EnumRespStatType.dealWithRespStat(resp);
                                switch (respStatType) {
                                    case NST_NOT_MATCH:
                                        MethodTool.showToast(AddCoursePlanActivity.this,Ref.OP_INST_NOT_MATCH);
                                        break;
                                    case DUPLICATE:
                                        MethodTool.showToast(AddCoursePlanActivity.this,"排课重复");
                                        break;
                                    case NSR:
                                        MethodTool.showToast(AddCoursePlanActivity.this,"未发现课程");
                                        break;
                                    default:break;
                                }
                            default:break;
                        }
                    }
                };
                NetUtil.sendHttpRequest(AddCoursePlanActivity.this,url,callback);
            }else {
                Toast.makeText(AddCoursePlanActivity.this,Ref.OP_WRONG_NUMBER_FORMAT,Toast.LENGTH_SHORT).show();
            }
        }
    }

}
