package xyz.instmgr.sailfish.Shopmember.Course.CoursePlan;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.alfred.alfredtools.*;
import xyz.instmgr.sailfish.R;
import xyz.instmgr.sailfish.Util.Ref;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class CoursePlanDetailPrivateActivity
        extends BaseActivity
        implements View.OnFocusChangeListener,View.OnClickListener,HttpResultListener {

    private static final int REQUEST_CoursePlanPrivateDetailQuery = 1;
    private static final int REQUEST_ClassroomListQuery = 2;
    private static final int REQUEST_CoursePlanModify = 3;
    private static final int REQUEST_CoursePlanRemove = 4;
    private String str_c_name,str_cp_id,str_start_time,str_end_time,str_current_cr_id,str_selected_cr_id;
    private String str_selected_start_date,str_selected_start_time;
    private Button btn_save;
    private TextView tv_c_name,tv_tea_name,tv_stu_name;
    private EditText et_last_time,et_start_date,et_start_time;
    private Spinner sp_cr_name;
    private DatePickerDialog dpd;
    private TimePickerDialog tpd;
    private String[] keys_courseplan_detail = new String[] {"id","c_name","tea_name","stu_name","start_time","end_time","last_time","cr_id","cr_name"};
    private String[] keys_classroom = new String[] {"id","name"};
    private List<Map<String,String>> maplist_original_courseplan_detail = new ArrayList<>();
    private List<Map<String,String>> maplist_origin_classroom = new ArrayList<>();
    private List<String> list_classroom_name = new ArrayList<>();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_plan_detail_private);
        initData();
        initViews();
        initCourseplanDetailData();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.et_start_date_a_course_plan_detail_private:
                if (hasFocus) {
                    dpd.show();
                }else {
                    dpd.hide();
                }
                break;
            case R.id.et_start_time_a_course_plan_detail_private:
                if (hasFocus) {
                    tpd.show();
                }else {
                    tpd.hide();
                }
                break;
            default:break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save_a_course_plan_detail_private:
                saveModifition();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_course_plan_detail_private,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.delete_courseplan_detail_private:
                delete();
                break;
            default:break;
        }
        return true;
    }

    @Override
    public void onRespStatus(String body, int source) {
        if (source == REQUEST_CoursePlanPrivateDetailQuery) {
            NetRespStatType respStatType = NetRespStatType.dealWithRespStat(body);
            switch (respStatType) {
                case NSR:
                    ViewHandler.toastShow(CoursePlanDetailPrivateActivity.this,Ref.OP_NSR);
                    CoursePlanDetailPrivateActivity.this.finish();
                    break;
                case STATUS_AUTHORIZE_FAIL:
                    ViewHandler.exitAcitivityDueToAuthorizeFail(CoursePlanDetailPrivateActivity.this);
                    break;
                default:break;
            }
        }else if (source == REQUEST_ClassroomListQuery) {
            switch (NetRespStatType.dealWithRespStat(body)) {
                case NSR:
                    ViewHandler.toastShow(CoursePlanDetailPrivateActivity.this,Ref.OP_NSR);
                    CoursePlanDetailPrivateActivity.this.finish();
                    break;
                case STATUS_AUTHORIZE_FAIL:
                    ViewHandler.exitAcitivityDueToAuthorizeFail(CoursePlanDetailPrivateActivity.this);
                    break;
                default:break;
            }
        }else if (source == REQUEST_CoursePlanModify) {
            NetRespStatType respStatType = NetRespStatType.dealWithRespStat(body);
            switch (respStatType) {
                case DUPLICATE:
                    ViewHandler.toastShow(CoursePlanDetailPrivateActivity.this,Ref.OP_DUPLICATED);
                    break;
                case NSR:
                    ViewHandler.toastShow(CoursePlanDetailPrivateActivity.this,Ref.OP_NSR);
                    CoursePlanDetailPrivateActivity.this.finish();
                    break;
                case NST_NOT_MATCH:
                    ViewHandler.toastShow(CoursePlanDetailPrivateActivity.this,Ref.OP_INST_NOT_MATCH);
                    CoursePlanDetailPrivateActivity.this.finish();
                    break;
                case SUCCESS:
                    ViewHandler.toastShow(CoursePlanDetailPrivateActivity.this,Ref.OP_MODIFY_SUCCESS);
                    CoursePlanDetailPrivateActivity.this.finish();
                    break;
                case FAIL:
                    ViewHandler.toastShow(CoursePlanDetailPrivateActivity.this,Ref.OP_MODIFY_FAIL);
                    break;
                case STATUS_AUTHORIZE_FAIL:
                    ViewHandler.exitAcitivityDueToAuthorizeFail(CoursePlanDetailPrivateActivity.this);
                    break;
                default:break;
            }
        }else if (source == REQUEST_CoursePlanRemove) {
            switch (NetRespStatType.dealWithRespStat(body)) {
                case SUCCESS:
                    ViewHandler.toastShow(CoursePlanDetailPrivateActivity.this,"删除成功");
                    CoursePlanDetailPrivateActivity.this.finish();
                    break;
                case FAIL:
                    ViewHandler.toastShow(CoursePlanDetailPrivateActivity.this,"删除失败");
                    break;
                case NST_NOT_MATCH:
                    ViewHandler.toastShow(CoursePlanDetailPrivateActivity.this,"无法匹配");
                    break;
                case NSR:
                    ViewHandler.toastShow(CoursePlanDetailPrivateActivity.this,"暂无记录");
                    break;
                case STATUS_AUTHORIZE_FAIL:
                    ViewHandler.exitAcitivityDueToAuthorizeFail(CoursePlanDetailPrivateActivity.this);
                    break;
                default:break;
            }
        }else {

        }
    }

    @Override
    public void onRespMapList(String body, int source) throws IOException {
        if (source == REQUEST_CoursePlanPrivateDetailQuery) {
            maplist_original_courseplan_detail = JsonUtil.strToListMap(body,keys_courseplan_detail);
            initClassroomData();
        }else if (source == REQUEST_ClassroomListQuery) {
            maplist_origin_classroom = JsonUtil.strToListMap(body,keys_classroom);
            for (int i = 0;i < maplist_origin_classroom.size();i++) {
                list_classroom_name.add(maplist_origin_classroom.get(i).get("name"));
            }
            initCourseplanDetailView();
        }else if (source == REQUEST_CoursePlanModify) {

        }else if (source == REQUEST_CoursePlanRemove) {

        }else {

        }
    }

    @Override
    public void onRespError(int source) {
        ViewHandler.toastShow(CoursePlanDetailPrivateActivity.this,Ref.UNKNOWN_ERROR);
    }

    @Override
    public void onReqFailure(Object object, int source) {
        ViewHandler.toastShow(CoursePlanDetailPrivateActivity.this,Ref.CANT_CONNECT_INTERNET);
    }

    @Override
    public void onRespSessionExpired(int source) {
        ViewHandler.alertShowAndExitApp(CoursePlanDetailPrivateActivity.this);
    }

    private void initData() {
        str_c_name = getIntent().getStringExtra("course_name");
        str_cp_id = getIntent().getStringExtra("cp_id");
        str_start_time = getIntent().getStringExtra("time");
    }

    private void initViews() {
        initToolbar();
        initDatePickerDialog();
        initTimePickerDialog();
        et_last_time = (EditText)findViewById(R.id.et_last_time_a_course_plan_detail_private);
        et_start_date = (EditText)findViewById(R.id.et_start_date_a_course_plan_detail_private);
        et_start_time = (EditText)findViewById(R.id.et_start_time_a_course_plan_detail_private);
        sp_cr_name = (Spinner)findViewById(R.id.sp_hint_classroom_name_a_course_plan_detail_private);
        tv_c_name = (TextView)findViewById(R.id.tv_hint_course_name_a_course_plan_detail_private);
        tv_tea_name = (TextView)findViewById(R.id.tv_hint_tea_name_a_course_plan_detail_private);
        tv_stu_name = (TextView)findViewById(R.id.tv_hint_stu_name_a_course_plan_detail_private);
        btn_save = (Button)findViewById(R.id.btn_save_a_course_plan_detail_private);
        btn_save.setOnClickListener(this);

        tv_c_name.setText(str_c_name);

        String str_origin_start_time = str_start_time.split(" ")[1];
        str_origin_start_time = str_origin_start_time.substring(0,str_origin_start_time.length()-5);
        et_start_date.setText(str_start_time.split(" ")[0]);
        et_start_time.setText(str_origin_start_time);

        et_start_date.setInputType(InputType.TYPE_NULL);
        et_start_time.setInputType(InputType.TYPE_NULL);
        str_selected_start_date = str_start_time.split(" ")[0];
        str_selected_start_time = str_start_time.split(" ")[1];
        et_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dpd.show();
            }
        });
        et_start_date.setOnFocusChangeListener(this);
        et_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tpd.show();
            }
        });
        et_start_time.setOnFocusChangeListener(this);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(str_c_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initDatePickerDialog() {

        Calendar calendar = initCalendarFromStarttime(str_start_time);

        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                str_selected_start_date = String.valueOf(year) + "-" + String.valueOf(month + 1) + "-" + String.valueOf(dayOfMonth);
                et_start_date.setText(str_selected_start_date);
            }
        };
        dpd = new DatePickerDialog(CoursePlanDetailPrivateActivity.this,listener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
    }

    private void initTimePickerDialog() {
        Calendar calendar = initCalendarFromStarttime(str_start_time);
        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                str_selected_start_time = String.valueOf(hourOfDay) + ":" + String.valueOf(minute) + ":00";
                et_start_time.setText(str_selected_start_time);
            }
        };
        tpd = new TimePickerDialog(CoursePlanDetailPrivateActivity.this,listener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),true);
    }

    private Calendar initCalendarFromStarttime (String str_start_time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(str_start_time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    private void initCourseplanDetailData() {
        String url = "/CoursePlanPrivateDetailQuery?cp_id=" + str_cp_id;
        HttpCallback callback = new HttpCallback(this,this,REQUEST_CoursePlanPrivateDetailQuery);
        NetUtil.reqSendGet(this,url,callback);
    }

    private void initClassroomData() {
        String url = "/ClassroomListQuery";
        HttpCallback callback = new HttpCallback(this,this,REQUEST_ClassroomListQuery);
        NetUtil.reqSendGet(this,url,callback);
    }

    private void initCourseplanDetailView() {
        final Map<String,String> map = maplist_original_courseplan_detail.get(0);

        //获取时间差，得到上课时长
        str_start_time = map.get("start_time");
        str_end_time = map.get("end_time");
        Date date_start_time = null;
        try {
             date_start_time= simpleDateFormat.parse(str_start_time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date_end_time = null;
        try {
            date_end_time = simpleDateFormat.parse(str_end_time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long last_time_ms = date_end_time.getTime() - date_start_time.getTime();
        long last_time_min = last_time_ms/1000/60;
        et_last_time.setText(String.valueOf(last_time_min));//设置上课时长

        tv_stu_name.setText(map.get("stu_name"));
        tv_tea_name.setText(map.get("tea_name"));
        str_current_cr_id = String.valueOf(map.get("cr_id"));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CoursePlanDetailPrivateActivity.this,android.R.layout.simple_spinner_item,list_classroom_name);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_cr_name.setAdapter(adapter);
        int p = list_classroom_name.indexOf(str_current_cr_id);
        sp_cr_name.setSelection(p);
        sp_cr_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                str_selected_cr_id = String.valueOf(maplist_origin_classroom.get(position).get("id"));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                str_selected_cr_id = str_current_cr_id;
            }
        });
    }

    private void saveModifition() {

        //获得新设置后的结束时间
        Date date_new_start = null;
        try {
            date_new_start = simpleDateFormat.parse(str_selected_start_date + " " + str_selected_start_time + ":00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date_new_start);
        String str_new_start = simpleDateFormat.format(calendar.getTime());
        calendar.add(Calendar.MINUTE,Integer.parseInt(et_last_time.getText().toString()));
        Date date_new_end = calendar.getTime();
        String str_new_end = simpleDateFormat.format(calendar.getTime());

        String url = "/CoursePlanModify?id=" + str_cp_id +
                "&cr_id=" + str_selected_cr_id +
                "&s_time=" + str_new_start +
                "&e_time=" + str_new_end +
                "&remark=";
        HttpCallback callback = new HttpCallback(this,this,REQUEST_CoursePlanModify);
        NetUtil.reqSendGet(this,url,callback);
    }

    private void delete() {
        String url = "/CoursePlanRemove";
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("id",str_cp_id);
        HttpCallback callback = new HttpCallback(this,this,REQUEST_CoursePlanRemove);
        NetUtil.reqSendGet(this,url,callback);
    }
}
