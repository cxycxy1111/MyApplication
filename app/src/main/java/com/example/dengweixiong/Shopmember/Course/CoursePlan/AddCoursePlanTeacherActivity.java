package com.example.dengweixiong.Shopmember.Course.CoursePlan;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.dengweixiong.Shopmember.Adapter.RVSimpleAdapterWithCheck;
import com.example.dengweixiong.Util.BaseActivity;
import com.example.dengweixiong.Util.JsonHandler;
import com.example.dengweixiong.Util.MethodTool;
import com.example.dengweixiong.Util.NetUtil;
import com.example.dengweixiong.Util.Ref;
import com.example.dengweixiong.myapplication.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AddCoursePlanTeacherActivity extends BaseActivity {

    private String s_id,sm_id,cp_id;
    private String [] keys_teacher_list = new String[] {"id","name","type"};
    private Map<Integer,Boolean> map_status = new HashMap<>();
    private List<String> list_teacher_name = new ArrayList<>();
    private List<Map<String,String>> mapList_teacher_full = new ArrayList<>();
    private RecyclerView rv_teacher;
    private RVSimpleAdapterWithCheck adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_plan_add_teacher);
        initToolbar();
        initData();
        initTeacherData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_courseplan_teacher,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_add_courseplan_teacher:
                saveCoursePlanTeacher();
                break;
            case android.R.id.home:
                AddCoursePlanTeacherActivity.this.finish();
                break;
            default:break;
        }
        return true;
    }

    private void initData() {
        s_id = MethodTool.getSharePreferenceValue(
                AddCoursePlanTeacherActivity.this,"sasm","s_id",2);
        sm_id = MethodTool.getSharePreferenceValue(
                AddCoursePlanTeacherActivity.this,"sasm","sm_id",2);
        cp_id = getIntent().getStringExtra("cp_id");
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("设置教师");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rv_teacher = (RecyclerView)findViewById(R.id.rv_a_add_course_plan_teacher);
    }

    private void initTeacherData() {
        String url = "/QueryShopmemberList?s_id=" + s_id + "&type=0";
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(AddCoursePlanTeacherActivity.this,Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                int s = MethodTool.dealWithResponse(resp);
                if (s == Ref.RESP_TYPE_MAPLIST) {
                    mapList_teacher_full = JsonHandler.strToListMap(resp,keys_teacher_list);
                    for (int i = 0;i < mapList_teacher_full.size();i++) {
                        list_teacher_name.add(String.valueOf(mapList_teacher_full.get(i).get(keys_teacher_list[1])));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter = new RVSimpleAdapterWithCheck(list_teacher_name);
                            LinearLayoutManager llm_rv_teacher = new LinearLayoutManager(AddCoursePlanTeacherActivity.this,LinearLayoutManager.VERTICAL,false);
                            rv_teacher.setAdapter(adapter);
                            rv_teacher.setLayoutManager(llm_rv_teacher);
                        }
                    });
                }else if (s == Ref.RESP_TYPE_STAT) {

                }else if (s == Ref.RESP_TYPE_ERROR){
                    MethodTool.showToast(AddCoursePlanTeacherActivity.this,resp);
                }
            }
        };
        NetUtil.sendHttpRequest(AddCoursePlanTeacherActivity.this,url,callback);
    }

    /**
     * 保存教师设置
     */
    private void saveCoursePlanTeacher() {
        String url = "/CoursePlanTeacherModify?m=" + initReqData();
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(AddCoursePlanTeacherActivity.this,Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                if (MethodTool.dealWithResponse(resp) ==  Ref.RESP_TYPE_ERROR) {
                    MethodTool.showToast(AddCoursePlanTeacherActivity.this,Ref.UNKNOWN_ERROR);
                }else if (MethodTool.dealWithResponse(resp) == Ref.RESP_TYPE_STAT) {
                    Map<String,String> map = JsonHandler.strToMap(resp);
                    if (map.get(Ref.STATUS).equals(Ref.STAT_EXE_SUC)) {
                        MethodTool.showToast(AddCoursePlanTeacherActivity.this,Ref.OP_SUCCESS);
                        //返回码
                        setResult(Ref.RESULTCODE_ADD);
                        AddCoursePlanTeacherActivity.this.finish();
                    }else if (map.get(Ref.STATUS).equals(Ref.STAT_EXE_FAIL)) {
                        MethodTool.showToast(AddCoursePlanTeacherActivity.this,Ref.OP_FAIL);
                    }else if (map.get(Ref.STATUS).equals(Ref.STAT_PARTYLY_FAIL)) {
                        MethodTool.showToast(AddCoursePlanTeacherActivity.this,Ref.OP_PARTLY_FAIL);
                        setResult(Ref.RESULTCODE_ADD);
                        AddCoursePlanTeacherActivity.this.finish();
                    }
                }
            }
        };
        NetUtil.sendHttpRequest(AddCoursePlanTeacherActivity.this,url,callback);
    }

    //拼接
    private String initReqData() {
        StringBuilder builder = new StringBuilder();
        map_status = adapter.getCheckStatusMap();
        for (int i = 0;i < mapList_teacher_full.size();i++) {
            boolean b = map_status.get(i);
            if (b) {
                builder.append("1").append("_").append(cp_id).append("_").append(String.valueOf(mapList_teacher_full.get(i).get("id"))).append("-");
            }
        }
        String str = builder.toString();
        return str.substring(0,str.length()-1);
    }

}
