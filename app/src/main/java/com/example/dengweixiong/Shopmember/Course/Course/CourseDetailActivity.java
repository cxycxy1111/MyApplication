package com.example.dengweixiong.Shopmember.Course.Course;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dengweixiong.Shopmember.Adapter.RVCourseDetailAdapter;
import com.example.dengweixiong.Util.BaseActivity;
import com.example.dengweixiong.Util.JsonHandler;
import com.example.dengweixiong.Util.MethodTool;
import com.example.dengweixiong.Util.NetUtil;
import com.example.dengweixiong.Util.Ref;
import com.example.dengweixiong.myapplication.R;

import org.apache.commons.lang.math.NumberUtils;
import org.w3c.dom.CDATASection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CourseDetailActivity
        extends BaseActivity
        implements View.OnClickListener,EditText.OnFocusChangeListener{

    private int int_current_year,int_current_month,int_current_date;
    private String str_courseId,str_shopId,sm_id,str_type,str_courseName;
    private String [] strs_cardKeys = new String[] {"id","name","type"};
    private String [] strs_supportCardKeys = new String[] {"c_id","card_name","course_name","cs_price"};
    private String [] strs_courseKeys = new String[] {"id","type","name","last_time","max_book_num"};
    private String [] strs_coursePrivateKeys = new String[] {"id","type","course_name","shopmember_name","member_name","total_times","rest_times","expired_time","actual_cost"};
    private ScrollView scrollView;
    private RecyclerView recyclerView;
    private TextView tv_type,tv_tea,tv_stu;
    private EditText et_name,et_total_times,et_rest_times,et_invalid_date,et_total_cost;
    private DatePickerDialog dpd_invalid_date;
    private RVCourseDetailAdapter adapter;
    private Map<String,String> map_result = new HashMap<>();
    private List<Map<String,String>> maplist_courseDetail = new ArrayList<>();
    private List<Map<String,String>> maplist_card = new ArrayList<>();
    private List<Map<String,String>> maplist_supportedCard = new ArrayList<>();
    private List<Map<String,String>> maplist_render = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);
        initData();
        initDate();
        initDatePickerDialog();
        initToolbar();
        initViews();
    }

    private void initData() {
        Intent intent = getIntent();
        str_courseId = intent.getStringExtra("id");
        str_courseName = intent.getStringExtra("name");
        str_type = intent.getStringExtra("type");

        str_shopId = MethodTool.getSharePreferenceValue(this,"sasm","s_id", Ref.DATA_TYPE_LONG);
        sm_id = MethodTool.getSharePreferenceValue(this,"sasm","sm_id", Ref.DATA_TYPE_LONG);
    }

    /**
     * 初始化Toolbar
     */
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_a_course_detail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getIntent().getStringExtra("name"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * 初始化失效时间选择器
     */
    private void initDatePickerDialog() {
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                et_invalid_date.setText(new StringBuffer().append(year).append("-").append(month+1).append("-").append(dayOfMonth));
            }
        };
        dpd_invalid_date = new DatePickerDialog(CourseDetailActivity.this,listener,int_current_year,int_current_month,int_current_date);
    }

    /**
     * 初始化时间
     */
    private void initDate() {
        Calendar calendar = Calendar.getInstance();
        this.int_current_year = calendar.get(Calendar.YEAR);
        this.int_current_month = calendar.get(Calendar.MONTH);
        this.int_current_date = calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 初始化视图
     */
    private void initViews() {
        scrollView = (ScrollView)findViewById(R.id.sv_a_course_detail);
        recyclerView = (RecyclerView)findViewById(R.id.rv_a_course_detail);
        tv_type = (TextView)findViewById(R.id.tv_hint_type_a_course_detial);
        et_name = (EditText)findViewById(R.id.et_hint_course_name_a_course_detial);
        tv_tea = (TextView) findViewById(R.id.tv_hint_teacher_a_course_detial);
        tv_stu = (TextView) findViewById(R.id.tv_hint_student_a_course_detial);
        et_total_times = (EditText)findViewById(R.id.et_hint_total_times_a_course_detial);
        et_rest_times = (EditText)findViewById(R.id.et_rest_times_a_course_detial);
        et_invalid_date = (EditText)findViewById(R.id.et_invalidtime_a_course_detial);
        et_invalid_date.setInputType(InputType.TYPE_NULL);
        et_invalid_date.setOnClickListener(this);
        et_total_cost = (EditText)findViewById(R.id.et_total_cost_a_course_detial);
        initCourseDetail();
    }

    /**
     * 初始化会员课、集训课
     */
    private void initCourseDetail() {
        String url = "/CourseDetailQuery?c_id=" + str_courseId;
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(CourseDetailActivity.this, Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                if (resp.contains(Ref.STATUS)) {
                    Map<String, String> tempMap = new HashMap<>();
                    tempMap = JsonHandler.strToMap(resp);
                    switch (tempMap.get(Ref.STATUS)) {
                        case "empty_result":
                            MethodTool.showToast(CourseDetailActivity.this, Ref.UNKNOWN_ERROR);
                        default:
                            break;
                    }
                }else {
                    if (str_type.equals("4")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.setVisibility(View.GONE);
                            }
                        });
                        maplist_courseDetail = JsonHandler.strToListMap(resp,strs_coursePrivateKeys);
                        initPrivateCourse();
                    }else if (str_type.equals("1") || str_type.equals("2") || str_type.equals("3")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                scrollView.setVisibility(View.GONE);
                            }
                        });
                        maplist_courseDetail = JsonHandler.strToListMap(resp,strs_courseKeys);
                        initAllCards();
                    }
                }
            }
        };
        NetUtil.sendHttpRequest(CourseDetailActivity.this,url,callback);
    }

    /**
     * 初始化私教课程
     */
    private void initPrivateCourse() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Map<String,String> map_course_detail = maplist_courseDetail.get(0);
                tv_type.setText("私教课程");
                et_name.setText(map_course_detail.get("course_name"));
                et_total_times.setText(String.valueOf(map_course_detail.get("total_times")));
                et_rest_times.setText(String.valueOf(map_course_detail.get("rest_times")));
                String [] strs_invalid_time = map_course_detail.get("expired_time").split(" ");
                et_invalid_date.setText(strs_invalid_time[0]);
                et_total_cost.setText(String.valueOf(map_course_detail.get("actual_cost")));
                tv_tea.setText(map_course_detail.get("shopmember_name"));
                tv_stu.setText(map_course_detail.get("member_name"));
            }
        });
    }

    /**
     * 初始化所有的会员卡
     */
    private void initAllCards() {
        String url = "/QueryCardList?shop_id=" + str_shopId + "&type=0";
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(CourseDetailActivity.this, Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                if (resp.contains(Ref.STATUS)) {

                }else {
                    maplist_card = JsonHandler.strToListMap(resp,strs_cardKeys);
                    if (maplist_card.size()!=0) {
                        initSupportedCard();
                    }
                }
            }
        };
        NetUtil.sendHttpRequest(CourseDetailActivity.this,url,callback);
    }

    /**
     * 初始化受支持的会员卡
     */
    private void initSupportedCard() {
        String url = "/SupportedCardQuery?c_id=" + str_courseId + "&s_id=" + str_shopId;
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(CourseDetailActivity.this, Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                if (resp.contains(Ref.STATUS)) {
                    map_result = JsonHandler.strToMap(resp);
                }else {
                    maplist_supportedCard = JsonHandler.strToListMap(resp,strs_supportCardKeys);
                }
                reorgnizeData();
            }
        };
        NetUtil.sendHttpRequest(CourseDetailActivity.this,url,callback);
    }

    /**
     * 重新组织数据
     */
    private void reorgnizeData() {

        initCourseDataAndHeader();

        List<String> list_supportedCardId = new ArrayList<>();
        List<String> list_cardId = new ArrayList<>();
        for (int k = 0;k < maplist_supportedCard.size();k++) {
            list_supportedCardId.add(maplist_supportedCard.get(k).get(strs_supportCardKeys[0]));
        }
        for (int l = 0;l < maplist_card.size();l++) {
            list_cardId.add(maplist_card.get(l).get(strs_cardKeys[0]));
        }

        for (int i = 0;i < maplist_card.size();i++) {
            if (list_supportedCardId.contains(list_cardId.get(i))) {
                for (int j = 0;j < maplist_supportedCard.size();j++) {
                    if (String.valueOf(maplist_card.get(i).get("id")).equals(String.valueOf(maplist_supportedCard.get(j).get("c_id")))) {
                        Map<String,String> map_temp = new HashMap<>();
                        map_temp.put("id",String.valueOf(maplist_card.get(i).get("id")));
                        map_temp.put("type",String.valueOf(maplist_card.get(i).get("type")));
                        map_temp.put("checked","1");
                        map_temp.put("name",String.valueOf(maplist_card.get(i).get("name")));
                        if (String.valueOf(maplist_card.get(i).get("type")).equals("1")) {
                            map_temp.put("balance",String.valueOf(maplist_supportedCard.get(j).get("cs_price")));
                            map_temp.put("viewType","3");
                        }else if (String.valueOf(maplist_card.get(i).get("type")).equals("2")) {
                            map_temp.put("balance",String.valueOf(maplist_supportedCard.get(j).get("cs_price")));
                            map_temp.put("viewType","4");
                        }else {
                            map_temp.put("viewType","5");
                        }
                        maplist_render.add(map_temp);
                    }
                }
            }else {
                Map<String,String> map_temp = new HashMap<>();
                map_temp.put("id",String.valueOf(maplist_card.get(i).get("id")));
                map_temp.put("type",String.valueOf(maplist_card.get(i).get("type")));
                map_temp.put("checked","0");
                map_temp.put("name",maplist_card.get(i).get("name"));
                if (String.valueOf(maplist_card.get(i).get("type")).equals("1")) {
                    map_temp.put("viewType","3");
                    map_temp.put("balance","n");
                }else if (String.valueOf(maplist_card.get(i).get("type")).equals("2")) {
                    map_temp.put("viewType","4");
                    map_temp.put("balance","n");
                }else {
                    map_temp.put("viewType","5");
                }
                maplist_render.add(map_temp);
            }
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinearLayoutManager layoutManager= new LinearLayoutManager(CourseDetailActivity.this,LinearLayoutManager.VERTICAL,false);
                adapter = new RVCourseDetailAdapter(maplist_render,CourseDetailActivity.this);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(layoutManager);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_invalidtime_a_course_detial:
                dpd_invalid_date.show();
                break;
            default:break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.et_invalidtime_a_course_detial:
                if (hasFocus) {
                    dpd_invalid_date.show();
                }else {
                    dpd_invalid_date.show();
                }
                break;
            default:break;
        }
    }

    /**
     * 初始化数据头部及课程内容
     */
    private void initCourseDataAndHeader() {
        Map<String,String> course_map = maplist_courseDetail.get(0);

        Map<String,String> courseName_map = new HashMap<>();
        courseName_map.put("main","课程名称");
        courseName_map.put("hint",course_map.get("name"));
        courseName_map.put("viewType","2");
        maplist_render.add(courseName_map);

        Map<String,String> courseLastTime_map = new HashMap<>();
        courseLastTime_map.put("main","每节课时长");
        courseLastTime_map.put("hint",course_map.get("last_time"));
        courseLastTime_map.put("viewType","2");
        maplist_render.add(courseLastTime_map);

        Map<String,String> courseMaxBookNum_map = new HashMap<>();
        courseMaxBookNum_map.put("main","最大预约人数");
        courseMaxBookNum_map.put("hint",course_map.get("max_book_num"));
        courseMaxBookNum_map.put("viewType","2");
        maplist_render.add(courseMaxBookNum_map);

        Map<String,String> header_map = new HashMap<>();
        header_map.put("header","受支持的会员卡");
        header_map.put("viewType","1");
        maplist_render.add(header_map);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_course_detail,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
            case R.id.save_a_course_detail:
                switch (str_type) {
                    case "4" :
                        savePrivateCourse();
                        break;
                    default:
                        saveCourseBaseInfo();
                        break;
                }
                break;
            case R.id.delete_a_course_detail:
                break;
            default:break;
        }
        return true;
    }

    /**
     * 保存私教课程的更改
     */
    private void savePrivateCourse(){
        String str_total_times = et_total_times.getText().toString();
        String str_total_cost = et_total_cost.getText().toString();

        int int_total_times = Integer.parseInt(str_total_times);
        String str_name = et_name.getText().toString();
        String str_invalid_time = et_invalid_date.getText().toString();
        int int_total_cost = Integer.parseInt(str_total_cost);
        if (!NumberUtils.isNumber(str_total_times) || !NumberUtils.isNumber(str_total_cost)) {
            Toast.makeText(CourseDetailActivity.this,Ref.OP_WRONG_NUMBER_FORMAT,Toast.LENGTH_SHORT).show();
        }else {
            String url = "/CoursePrivateModify?sm_id=" + sm_id +
                    "&c_id=" + str_courseId +
                    "&total_times=" + int_total_times +
                    "&name=" + str_name +
                    "&invalid_time=" + str_invalid_time +
                    "&total_cost=" + int_total_cost;
            Callback callback = new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    MethodTool.showToast(CourseDetailActivity.this, Ref.CANT_CONNECT_INTERNET);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String resp = response.body().string();
                    if (resp.contains(Ref.STATUS)) {
                        Map<String,String> map = JsonHandler.strToMap(resp);
                        String str_value = map.get(Ref.STATUS);
                        switch (str_value) {
                            case "institution_not_match":
                                MethodTool.showToast(CourseDetailActivity.this,Ref.OP_INST_NOT_MATCH);
                                CourseDetailActivity.this.finish();
                                break;
                            case "exe_suc":
                                MethodTool.showToast(CourseDetailActivity.this,Ref.OP_MODIFY_SUCCESS);
                                CourseDetailActivity.this.finish();
                                break;
                            case "exe_fail":
                                MethodTool.showToast(CourseDetailActivity.this,Ref.OP_MODIFY_FAIL);
                                CourseDetailActivity.this.finish();
                                break;
                            default:break;
                        }
                    }else {
                        MethodTool.showToast(CourseDetailActivity.this, Ref.UNKNOWN_ERROR);
                    }

                }
            };
            NetUtil.sendHttpRequest(CourseDetailActivity.this,url,callback);
        }
    }

    /**
     * 保存公共课程的基本信息
     */
    private void saveCourseBaseInfo() {
        List<Map<String,String>> maplist_after_modified = this.adapter.getList_after_operate();
        Map<String,String> map_name = maplist_after_modified.get(0);
        final Map<String,String> map_last_time = maplist_after_modified.get(1);
        Map<String,String> map_max_book_num = maplist_after_modified.get(2);
        String str_name = map_name.get("hint");
        String str_last_time = String.valueOf(map_last_time.get("hint"));
        String str_max_book_num = String.valueOf(map_max_book_num.get("hint"));
        if (!NumberUtils.isNumber(str_last_time) || !NumberUtils.isNumber(str_max_book_num)) {
            Toast.makeText(CourseDetailActivity.this,"数字格式错误",Toast.LENGTH_LONG).show();
        }else {
            String url = "/CourseModify?c_id=" + str_courseId +
                    "&lmu_id=" + sm_id +
                    "&name=" + str_name +
                    "&last_time=" + str_last_time +
                    "&max_book_num=" + str_max_book_num +
                    "&summary=";
            Callback callback = new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    MethodTool.showToast(CourseDetailActivity.this, Ref.CANT_CONNECT_INTERNET);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String resp = response.body().string();
                    if (resp.contains(Ref.STATUS)) {
                        Map<String,String> map_result = JsonHandler.strToMap(resp);
                        switch (map_result.get(Ref.STATUS)) {
                            case "institution_not_match":
                                MethodTool.showToast(CourseDetailActivity.this,Ref.OP_INST_NOT_MATCH);
                                break;
                            case "exe_suc":
                                MethodTool.showToast(CourseDetailActivity.this,Ref.OP_MODIFY_SUCCESS);
                                saveSupportCardModifation();
                                break;
                            case "exe_fail":
                                MethodTool.showToast(CourseDetailActivity.this,Ref.OP_MODIFY_FAIL);
                                break;
                            default:break;
                        }
                    }else {
                        MethodTool.showToast(CourseDetailActivity.this, Ref.UNKNOWN_ERROR);
                    }
                }
            };
            NetUtil.sendHttpRequest(CourseDetailActivity.this,url,callback);
        }
    }

    /**
     * 保存会员课程所支持的卡类型及价格
     */
    private void saveSupportCardModifation() {
        StringBuilder builder = new StringBuilder();
        List<Map<String,String>> maplist_after_modified = this.adapter.getList_after_operate();
        for (int i = 4;i < maplist_after_modified.size();i++) {
            Map<String,String> map_before = maplist_render.get(i);
            Map<String,String> map_after = maplist_after_modified.get(i);
            if (map_after.get("checked").equals("true") || map_after.get("checked").equals("1")) {
                map_after.put("checked","1");
            }else {
                map_after.put("checked","0");
            }
            //修改会员卡
            if (map_before.get("checked").equals("1") && map_after.get("checked").equals("1")) {
                if (!map_after.get("type").equals("3")) {
                    if (!map_before.get("balance").equals(map_after.get("balance"))) {
                        builder.append("3").append("_").
                                append(str_courseId).append("_").
                                append(map_after.get("id")).append("_").
                                append(map_after.get("balance")).append("-");
                    }
                }
            }
            //删除会员卡
            else if (map_before.get("checked").equals("1") && !map_after.get("checked").equals("1")) {
                builder.append("2").append("_").
                        append(str_courseId).append("_").
                        append(map_after.get("id")).append("_").
                        append(map_after.get("balance")).append("-");
            }
            //新增会员卡
            else if (!map_before.get("checked").equals("1") && map_after.get("checked").equals("1")) {
                builder.append("1").append("_").
                        append(str_courseId).append("_").
                        append(map_after.get("id")).append("_").
                        append(map_after.get("balance")).append("-");
            }else {
            }
        }
        String str_request_param = builder.toString();
        if (str_request_param.length() !=0) {
            str_request_param = str_request_param.substring(0,str_request_param.length()-1);
            String url = "/SupportedCardModify?m=" + str_request_param;
            Callback callback = new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    MethodTool.showToast(CourseDetailActivity.this, Ref.CANT_CONNECT_INTERNET);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String resp = response.body().string();
                    if (resp.contains(Ref.STATUS)) {
                        Map<String,String> map_result = JsonHandler.strToMap(resp);
                        switch (map_result.get(Ref.STATUS)) {
                            case "exe_fail":
                                MethodTool.showToast(CourseDetailActivity.this,Ref.OP_MODIFY_FAIL);
                                break;
                            case "exe_partly_fail":
                                MethodTool.showToast(CourseDetailActivity.this,"部分修改失败");
                                break;
                            case "exe_suc":
                                MethodTool.showToast(CourseDetailActivity.this,Ref.OP_MODIFY_SUCCESS);
                                CourseDetailActivity.this.finish();
                            default:break;
                        }
                    }else {
                        MethodTool.showToast(CourseDetailActivity.this, Ref.UNKNOWN_ERROR);
                    }
                }
            };
            NetUtil.sendHttpRequest(CourseDetailActivity.this,url,callback);
        }

    }
}