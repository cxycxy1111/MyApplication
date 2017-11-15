package com.example.dengweixiong.Activity.Course;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

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
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CourseDetailActivity
        extends BaseActivity {

    private String ce_id,s_id,sm_id,type;
    private String ce_name;
    private String [] left = {"课程名称","每节课时长","最高预约人数"};
    private String [] left_supportedcard;
    private String [] right = {"会员课","40分钟","30人"};
    private String [] right_supportedcard;
    private String [] res = {"main","hint"};
    private int [] id = {R.id.tv_main_text_list_with_hint,R.id.tv_hint_text_list_with_hint};
    private int [] id_supportedcard;
    private Toolbar toolbar;
    private ListView listView;
    private SimpleAdapter adapter;
    private List<Map<String,String>> courseDetailMapList = new ArrayList<>();
    private List<Map<String,String>> cardMapList = new ArrayList<>();
    private List<Map<String,String>> supportedCardList = new ArrayList<>();

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
        ce_id = intent.getStringExtra("id");
        ce_name = intent.getStringExtra("name");
        type = intent.getStringExtra("type");

        s_id = MethodTool.getSharePreferenceValue(this,"sasm","s_id", Reference.DATA_TYPE_LONG);
        sm_id = MethodTool.getSharePreferenceValue(this,"sasm","sm_id",Reference.DATA_TYPE_LONG);
    }

    private void initToolbar() {
        toolbar = (Toolbar)findViewById(R.id.tb_a_course_detail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getIntent().getStringExtra("name"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        initCourseDetail();
    }

    private void initCourseDetail() {
        String url = "/CourseDetailQuery?c_id=" + ce_id;
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
                    String [] keys;
                    if (type.equals("4")) {
                        keys = new String[] {"id","type","course_name","shopmember_name","member_name","total_times","rest_times","expired_time"};
                        courseDetailMapList = JsonHandler.strToListMap(resp,keys);
                        initPrivateCourse();
                    }else if (type.equals("1") || type.equals("2") || type.equals("3")) {
                        keys = new String[] {"id","type","name","last_time","max_book_num"};
                        courseDetailMapList = JsonHandler.strToListMap(resp,keys);
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
        String url = "/QueryCardList?shop_id" + s_id + "&type=0";
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
                    String [] keys = new String[] {"id","name","type"};
                    cardMapList = JsonHandler.strToListMap(resp,keys);
                }
            }
        };
        NetUtil.sendHttpRequest(CourseDetailActivity.this,url,callback);
    }

    private void initSupportedCard() {
        String url = "/SupportedCardQuery?c_id=" + ce_id + "&s_id=" + s_id;
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
                    String [] keys = new String[] {"card_id","card_name","course_id","cs_price"};
                    supportedCardList = JsonHandler.strToListMap(resp,keys);
                }
            }
        };
        NetUtil.sendHttpRequest(CourseDetailActivity.this,url,callback);
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
