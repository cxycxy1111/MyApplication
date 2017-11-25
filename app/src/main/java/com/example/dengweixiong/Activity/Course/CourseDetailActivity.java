package com.example.dengweixiong.Activity.Course;

import android.content.Intent;
import android.content.SharedPreferences;
import android.opengl.ETC1;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.dengweixiong.Adapter.RVCourseDetailAdapter;
import com.example.dengweixiong.Util.BaseActivity;
import com.example.dengweixiong.Util.JsonHandler;
import com.example.dengweixiong.Util.MethodTool;
import com.example.dengweixiong.Util.NetUtil;
import com.example.dengweixiong.Util.Reference;
import com.example.dengweixiong.myapplication.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CourseDetailActivity
        extends BaseActivity {

    private String str_courseId,str_shopId,sm_id,str_type,str_courseName;
    private String [] strs_cardKeys = new String[] {"id","name","type"};
    private String [] strs_supportCardKeys = new String[] {"c_id","card_name","course_name","cs_price"};
    private String [] strs_courseKeys = new String[] {"id","type","name","last_time","max_book_num"};
    private String [] strs_coursePrivateKeys = new String[] {"id","type","course_name","shopmember_name","member_name","total_times","rest_times","expired_time"};
    private Toolbar toolbar;
    private ScrollView scrollView;
    private RecyclerView recyclerView;
    private TextView tv_type;
    private EditText et_name;
    private Spinner sp_tea,sp_stu;
    private List<Map<String,String>> maplist_courseDetail = new ArrayList<>();
    private List<Map<String,String>> maplist_card = new ArrayList<>();
    private List<Map<String,String>> maplist_supportedCard = new ArrayList<>();
    private List<Map<String,String>> maplist_render = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);
        initData();
        initToolbar();
        initViews();
    }

    private void initData() {
        Intent intent = getIntent();
        str_courseId = intent.getStringExtra("id");
        str_courseName = intent.getStringExtra("name");
        str_type = intent.getStringExtra("type");

        str_shopId = MethodTool.getSharePreferenceValue(this,"sasm","s_id", Reference.DATA_TYPE_LONG);
        sm_id = MethodTool.getSharePreferenceValue(this,"sasm","sm_id",Reference.DATA_TYPE_LONG);
    }

    private void initToolbar() {
        toolbar = (Toolbar)findViewById(R.id.tb_a_course_detail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getIntent().getStringExtra("name"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        scrollView = (ScrollView)findViewById(R.id.sv_a_course_detail);
        recyclerView = (RecyclerView)findViewById(R.id.rv_a_course_detail);
        tv_type = (TextView)findViewById(R.id.tv_hint_type_a_course_detial);
        et_name = (EditText)findViewById(R.id.et_hint_course_name_a_course_detial);
        sp_tea = (Spinner)findViewById(R.id.sp_hint_teacher_a_course_detial);
        sp_stu = (Spinner)findViewById(R.id.sp_hint_student_a_course_detial);
        initCourseDetail();
    }

    private void initCourseDetail() {
        String url = "/CourseDetailQuery?c_id=" + str_courseId;
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(CourseDetailActivity.this,Reference.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                if (resp.contains(Reference.STATUS)) {
                    Map<String, String> tempMap = new HashMap<>();
                    tempMap = JsonHandler.strToMap(resp);
                    switch (tempMap.get(Reference.STATUS)) {
                        case "empty_result":
                            MethodTool.showToast(CourseDetailActivity.this, Reference.UNKNOWN_ERROR);
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

    private void initPrivateCourse() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    private void initAllCards() {
        String url = "/QueryCardList?shop_id=" + str_shopId + "&type=0";
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(CourseDetailActivity.this,Reference.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                if (resp.contains(Reference.STATUS)) {

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

    private void initSupportedCard() {
        String url = "/SupportedCardQuery?c_id=" + str_courseId + "&s_id=" + str_shopId;
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(CourseDetailActivity.this,Reference.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                if (resp.contains(Reference.STATUS)) {

                }else {
                    maplist_supportedCard = JsonHandler.strToListMap(resp,strs_supportCardKeys);
                    reorgnizeData();
                }
            }
        };
        NetUtil.sendHttpRequest(CourseDetailActivity.this,url,callback);
    }

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
                RVCourseDetailAdapter adapter = new RVCourseDetailAdapter(maplist_render,CourseDetailActivity.this);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(layoutManager);
            }
        });
    }


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
        courseMaxBookNum_map.put("main","持续时间");
        courseMaxBookNum_map.put("hint",course_map.get("last_time"));
        courseMaxBookNum_map.put("viewType","2");
        maplist_render.add(courseMaxBookNum_map);

        Map<String,String> header_map = new HashMap<>();
        header_map.put("header","受支持的会员卡");
        header_map.put("viewType","1");
        maplist_render.add(header_map);
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
