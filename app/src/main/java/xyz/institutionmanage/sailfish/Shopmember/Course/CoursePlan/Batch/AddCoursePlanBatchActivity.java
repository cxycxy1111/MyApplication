package xyz.institutionmanage.sailfish.Shopmember.Course.CoursePlan.Batch;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import org.apache.commons.lang.math.NumberUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import xyz.institutionmanage.sailfish.R;
import xyz.institutionmanage.sailfish.Util.BaseActivity;
import xyz.institutionmanage.sailfish.Util.Enum.EnumRespStatType;
import xyz.institutionmanage.sailfish.Util.Enum.EnumRespType;
import xyz.institutionmanage.sailfish.Util.JsonHandler;
import xyz.institutionmanage.sailfish.Util.MethodTool;
import xyz.institutionmanage.sailfish.Util.NetUtil;
import xyz.institutionmanage.sailfish.Util.Ref;

public class AddCoursePlanBatchActivity 
        extends BaseActivity 
        implements View.OnClickListener,EditText.OnFocusChangeListener{

    private int current_hour,current_minute;
    private String selected_course,selected_classroom,selected_course_type,current_courseplan;
    private String [] keys_course_list = new String[] {"id","name","last_time","type","supportedcard"};
    private String [] keys_classroom_list = new String[] {"id","name"};
    private String [] keys_teacher_list = new String[] {"id","name","type"};
    private Button btn_next;
    private EditText et_time,et_last_time;
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
        setContentView(R.layout.activity_add_course_plan_batch);
        initData();
        initToolbar();
        initWidgets();
        initStartTimePickerDialog();
        initCourses();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next_step_a_add_course_plan_batch:
                saveCoursePlan();
                break;
            case R.id.et_time_a_add_course_plan_batch:
                tpd_time.show();
                break;
            default:break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.et_time_a_add_course_plan_batch:
                if (hasFocus) {
                    tpd_time.show();
                }
            default:break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                AddCoursePlanBatchActivity.this.finish();
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
                        AddCoursePlanBatchActivity.this.finish();
                        break;
                    default:break;
                }
                break;
            default:break;
        }
    }

    private void initData() {
        Calendar calendar = Calendar.getInstance();
        this.current_hour = calendar.get(Calendar.HOUR);
        this.current_minute = calendar.get(Calendar.MINUTE);
    }

    private void initToolbar () {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_general);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("批量新增排课");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initWidgets() {
        btn_next = (Button)findViewById(R.id.btn_next_step_a_add_course_plan_batch);
        sp_classroom = (Spinner)findViewById(R.id.sp_classroom_a_add_course_plan_batch);
        sp_course = (Spinner)findViewById(R.id.sp_course_a_add_course_plan_batch);
        et_time = (EditText)findViewById(R.id.et_time_a_add_course_plan_batch);
        et_last_time = (EditText)findViewById(R.id.et_last_time_a_add_course_plan_batch);

        btn_next.setOnClickListener(this);
        //设置开始时间输入框的属性
        et_time.setInputType(InputType.TYPE_NULL);
        et_time.setOnClickListener(this);
        et_time.setOnFocusChangeListener(this);
    }

    private void initStartTimePickerDialog() {
        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                et_time.setText(String.valueOf(hourOfDay) + ":" + String.valueOf(minute));
            }
        };
        tpd_time = new TimePickerDialog(AddCoursePlanBatchActivity.this,listener,current_hour,current_minute,true);
    }

    private void initCourses() {
        String url = "/CourseListQuery";
        okhttp3.Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToastAndFinish(AddCoursePlanBatchActivity.this, Ref.CANT_CONNECT_INTERNET);
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
                                MethodTool.showToast(AddCoursePlanBatchActivity.this,"暂无此舞馆");
                                AddCoursePlanBatchActivity.this.finish();
                                break;
                            case EMPTY_RESULT:
                                MethodTool.showToast(AddCoursePlanBatchActivity.this,"舞馆尚未安排课程");
                                AddCoursePlanBatchActivity.this.finish();
                                break;
                            case SESSION_EXPIRED:
                                MethodTool.showExitAppAlert(AddCoursePlanBatchActivity.this);
                                break;
                            case AUTHORIZE_FAIL:
                                MethodTool.exitAcitivityDueToAuthorizeFail(AddCoursePlanBatchActivity.this);
                                break;
                            default:break;
                        }
                        break;
                    case RESP_ERROR:
                        MethodTool.showToast(AddCoursePlanBatchActivity.this,Ref.UNKNOWN_ERROR);
                        AddCoursePlanBatchActivity.this.finish();
                        break;
                    default:break;
                }
            }
        };
        NetUtil.sendHttpRequest(AddCoursePlanBatchActivity.this,url,callback);
    }

    private void initCourseSpinner() {
        ArrayAdapter<String> adapter_course_name = new ArrayAdapter<String>(AddCoursePlanBatchActivity.this,android.R.layout.simple_spinner_item,list_course_name);
        adapter_course_name.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_course.setAdapter(adapter_course_name);
        sp_course.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_course = String.valueOf(mapList_course_full.get(position).get(keys_course_list[0]));
                selected_course_type = String.valueOf(mapList_course_full.get(position).get(keys_course_list[3]));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selected_course = String.valueOf(mapList_course_full.get(0).get(keys_course_list[0]));
                selected_course_type = String.valueOf(mapList_course_full.get(0).get(keys_course_list[3]));
            }
        });
        initClassroom();
    }

    private void initClassroom() {
        String url = "/ClassroomListQuery";
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToastAndFinish(AddCoursePlanBatchActivity.this,Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                switch (EnumRespType.dealWithResponse(resp)) {
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
                                MethodTool.showToast(AddCoursePlanBatchActivity.this,"暂无课室，请先新增");
                                AddCoursePlanBatchActivity.this.finish();
                                break;
                            case SESSION_EXPIRED:
                                MethodTool.showExitAppAlert(AddCoursePlanBatchActivity.this);
                                break;
                            case AUTHORIZE_FAIL:
                                MethodTool.exitAcitivityDueToAuthorizeFail(AddCoursePlanBatchActivity.this);
                                break;
                            default:break;
                        }
                        break;
                    case RESP_ERROR:
                        MethodTool.showToast(AddCoursePlanBatchActivity.this,Ref.UNKNOWN_ERROR);
                        AddCoursePlanBatchActivity.this.finish();
                        break;
                    default:break;
                }
            }
        };
        NetUtil.sendHttpRequest(AddCoursePlanBatchActivity.this,url,callback);
    }

    private void initClassroomSpinner() {
        ArrayAdapter<String> adapter_classroom_name = new ArrayAdapter<String>(AddCoursePlanBatchActivity.this,android.R.layout.simple_spinner_item,list_classroom_name);
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
        String str_time = et_time.getText().toString();
        String str_last_time = et_last_time.getText().toString();
        //判断是否为空
        String datetime = str_time + ":00";
        //判断持续时间是否为数字
        if (NumberUtils.isNumber(str_last_time)) {
            int int_last_time = Integer.parseInt(str_last_time);
            Intent intent = new Intent(AddCoursePlanBatchActivity.this,AddCoursePlanBatchDateActivity.class);
            intent.putExtra("c_id",selected_course)
                    .putExtra("c_type",selected_course_type)
                    .putExtra("cp_time",datetime)
                    .putExtra("cp_last_time",str_last_time)
                    .putExtra("cr_id",selected_classroom);
            startActivityForResult(intent,Ref.REQCODE_ADD);
        }else {
            Toast.makeText(AddCoursePlanBatchActivity.this,Ref.OP_WRONG_NUMBER_FORMAT,Toast.LENGTH_SHORT).show();
        }
    }

}
