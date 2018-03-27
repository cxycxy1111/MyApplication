package xyz.institutionmanage.sailfish.Shopmember.Course.CoursePlan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import xyz.institutionmanage.sailfish.Adapter.RVCoursePlanAdapter;
import xyz.institutionmanage.sailfish.R;
import xyz.institutionmanage.sailfish.Util.BaseActivity;
import xyz.institutionmanage.sailfish.Util.Enum.EnumRespStatType;
import xyz.institutionmanage.sailfish.Util.Enum.EnumRespType;
import xyz.institutionmanage.sailfish.Util.JsonHandler;
import xyz.institutionmanage.sailfish.Util.MethodTool;
import xyz.institutionmanage.sailfish.Util.NetUtil;
import xyz.institutionmanage.sailfish.Util.Ref;

public class CoursePlanListActivity extends BaseActivity {

    private String str_c_id,str_c_name;
    private String[] keys = new String[]{"courseplan_id","course_name","last_time","start_time","course_type"};
    private RecyclerView recyclerView;
    private RVCoursePlanAdapter adapter;
    private List<Map<String,String>> mapList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_plan_list);
        initDataFromPreviousActivity();
        initViewComponent();
        initDataFromWeb();
    }

    private void initDataFromPreviousActivity() {
        str_c_id = getIntent().getStringExtra("c_id");
        str_c_name = getIntent().getStringExtra("c_name");
    }

    private void initViewComponent() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        toolbar.setTitle(str_c_name);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = (RecyclerView)findViewById(R.id.rv_activity_course_plan_list);
        linearLayoutManager = new LinearLayoutManager(CoursePlanListActivity.this,LinearLayoutManager.VERTICAL,false);
    }

    private void initDataFromWeb() {
        String url = "/coursePlanListQueryByCourseId?c_id=" + str_c_id;
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(CoursePlanListActivity.this, Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                switch (EnumRespType.dealWithResponse(resp)) {
                    case RESP_MAPLIST:
                        mapList = JsonHandler.strToListMap(resp,keys);
                        updateViewComponent();
                        break;
                    case RESP_STAT:
                        switch (EnumRespStatType.dealWithRespStat(resp)) {
                            case EMPTY_RESULT:
                                MethodTool.showToast(CoursePlanListActivity.this,"暂无排课");
                                break;
                            case SESSION_EXPIRED:
                                MethodTool.showExitAppAlert(CoursePlanListActivity.this);
                                break;
                            default:break;
                        }
                        break;
                    case RESP_ERROR:
                        MethodTool.showToast(CoursePlanListActivity.this,Ref.UNKNOWN_ERROR);
                        break;
                    default:break;
                }
            }
        };
        NetUtil.sendHttpRequest(CoursePlanListActivity.this,url,callback);
    }

    private void updateViewComponent() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter = new RVCoursePlanAdapter(mapList,CoursePlanListActivity.this);
                adapter.setOnItemClickListener(new RVCoursePlanAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Map<String,String> map = mapList.get(position);
                        String course_type = String.valueOf(map.get("course_type"));
                        Intent intent;
                        switch (course_type) {
                            case "4":
                                intent = new Intent(CoursePlanListActivity.this, CoursePlanDetailPrivateActivity.class);
                                break;
                            default:
                                intent = new Intent(CoursePlanListActivity.this, CoursePlanDetailActivity.class);
                                break;
                        }
                        intent.putExtra("cp_id",String.valueOf(map.get("courseplan_id")));
                        intent.putExtra("course_name",map.get("course_name"));
                        intent.putExtra("time",map.get("start_time"));
                        startActivity(intent);
                    }
                });
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(linearLayoutManager);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                CoursePlanListActivity.this.finish();
                break;
            default:break;
        }
        return true;
    }
}
