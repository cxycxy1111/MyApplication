package xyz.institutionmanage.sailfish.Shopmember.Course.CoursePlan.Batch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.apache.commons.lang.math.NumberUtils;

import java.io.IOException;
import java.text.DateFormat;
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
import xyz.institutionmanage.sailfish.Adapter.RVPureCheckBoxAdapter;
import xyz.institutionmanage.sailfish.R;
import xyz.institutionmanage.sailfish.Util.ActivityManager;
import xyz.institutionmanage.sailfish.Util.BaseActivity;
import xyz.institutionmanage.sailfish.Util.MethodTool;
import xyz.institutionmanage.sailfish.Util.NetUtil;
import xyz.institutionmanage.sailfish.Util.Ref;

public class AddCoursePlanBatchDateActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "AddCoursePlanBatchDate:";
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private Button btn_next;
    private LinearLayoutManager linearLayoutManager;
    private RVPureCheckBoxAdapter adapter;
    private String cr_id,c_id,c_type,cp_time,cp_last_time;
    private List<Map<String,String>> maplist_rv_data = new ArrayList<>();
    private List<Map<String,String>> maplist_date = new ArrayList<>();
    private List<Map<String,String>> maplist_after = new ArrayList<>();
    private List<String> list_cp_id = new ArrayList<>();
    private String str_cp_id = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getProgressBar(this).setVisibility(View.VISIBLE);
        setContentView(R.layout.activity_add_course_plan_batch_date);
        initDataFromPreActivity();
        initComponentView();
        initRecycelrViewData();
    }

    private void initDataFromPreActivity() {
        cr_id = getIntent().getStringExtra("cr_id");
        c_id = getIntent().getStringExtra("c_id");
        c_type = getIntent().getStringExtra("c_type");
        cp_time = getIntent().getStringExtra("cp_time");
        cp_last_time = getIntent().getStringExtra("cp_last_time");
    }

    private void initComponentView() {
        toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        recyclerView = (RecyclerView)findViewById(R.id.rv_a_add_course_plan_batch_date);
        btn_next = (Button) findViewById(R.id.btn_next_a_add_course_plan_batch_date);
        btn_next.setOnClickListener(this);
        toolbar.setTitle("选择日期");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initRecycelrViewData() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");

        for (int i =1;i<30;i++) {
            Date date = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DATE,i);
            Date date1 = calendar.getTime();
            String str_date = DateFormat.getDateInstance(DateFormat.FULL).format(date1);
            String str_date_2 = simpleDateFormat.format(date1);
            HashMap<String,String> map = new HashMap<>();
            HashMap<String,String> map_2 = new HashMap<>();
            map.put("isChecked","0");
            map.put("teacherName",str_date);
            map_2.put("teacherName",str_date_2);
            maplist_rv_data.add(map);
            maplist_date.add(map_2);
        }
        linearLayoutManager = new LinearLayoutManager(AddCoursePlanBatchDateActivity.this,LinearLayoutManager.VERTICAL,false);
        adapter = new RVPureCheckBoxAdapter(maplist_rv_data,AddCoursePlanBatchDateActivity.this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        MethodTool.hideProgressBar(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                AddCoursePlanBatchDateActivity.this.finish();
                break;
            default:break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next_a_add_course_plan_batch_date:
                checkBeforeSubmit();
                break;
            default:break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Ref.REQCODE_ADD:
                switch (resultCode) {
                    case Ref.RESULTCODE_ADD:
                        setResult(Ref.RESULTCODE_ADD);
                        AddCoursePlanBatchDateActivity.this.finish();
                        break;
                    default:break;
                }
                break;
            default:break;
        }
    }

    public void checkBeforeSubmit() {
        maplist_after = adapter.getListmap_after();
        StringBuilder builder = new StringBuilder();
        for (int i=0;i< maplist_after.size();i++){
            Map<String,String> map = new HashMap<>();
            map = maplist_after.get(i);
            if (map.get("isChecked").equals("1")) {
                Map<String,String> map2 = new HashMap<>();
                map2 = maplist_date.get(i);
                builder.append(map2.get("teacherName")).append(",");
            }
        }
        String dates = builder.toString();
        if (dates.length()>1) {
            dates = dates.substring(0,dates.length()-1);
            submitCoursePlan(dates);
        }else {
            Toast.makeText(AddCoursePlanBatchDateActivity.this,"你未选择日期",Toast.LENGTH_SHORT).show();
        }
    }

    private void submitCoursePlan(String dates) {
        getProgressBar(this).setVisibility(View.VISIBLE);
        String url = "/coursePlanAddBatch";
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("date",dates);
        hashMap.put("time",cp_time);
        hashMap.put("c_id",c_id);
        hashMap.put("cr_id",cr_id);
        hashMap.put("last_time",cp_last_time);
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.hideProgressBar(AddCoursePlanBatchDateActivity.this);
                MethodTool.showToast(AddCoursePlanBatchDateActivity.this, Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                MethodTool.hideProgressBar(AddCoursePlanBatchDateActivity.this);
                String resp = response.body().string();
                Log.d(TAG, "onResponse: " + resp);
                if (!resp.contains("{")) {
                    String[] strings = new String[]{};
                    strings = resp.split(",");
                    for (int i = 0;i < strings.length;i ++) {
                        if (NumberUtils.isDigits(strings[i])) {
                            list_cp_id.add(strings[i]);
                        }
                    }
                    StringBuilder builder = new StringBuilder();
                    if (list_cp_id.size()>0) {
                        for (int i = 0;i < list_cp_id.size();i++) {
                            builder.append(list_cp_id.get(i));
                        }
                        str_cp_id = builder.toString();
                        if (c_type.equals("4")) {
                            setResult(Ref.RESULTCODE_ADD);
                            AddCoursePlanBatchDateActivity.this.finish();
                        }else {
                            Intent intent = new Intent(AddCoursePlanBatchDateActivity.this,AddCoursePlanBatchTeacherActivity.class);
                            intent.putExtra("c_id",c_id)
                                    .putExtra("c_type",c_type)
                                    .putExtra("cp_time",cp_time)
                                    .putExtra("cp_last_time",cp_last_time)
                                    .putExtra("cp_id",str_cp_id);
                            startActivityForResult(intent,Ref.REQCODE_ADD);
                        }
                    }
                }else {
                    if (resp.contains(Ref.SESSION_EXPIRED)) {
                        MethodTool.showToast(AddCoursePlanBatchDateActivity.this,Ref.ALERT_SESSION_EXPIRED);
                        ActivityManager.removeAllActivity();
                    }
                    MethodTool.showToast(AddCoursePlanBatchDateActivity.this,Ref.UNKNOWN_ERROR);
                }
            }
        };
        NetUtil.sendPostHttpRequest(AddCoursePlanBatchDateActivity.this,url,hashMap,callback);
    }
}
