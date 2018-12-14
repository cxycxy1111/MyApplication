package xyz.institutionmanager.sailfish.Shopmember.Course.CoursePlan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.alfred.alfredtools.*;
import xyz.institutionmanager.sailfish.Adapter.RVCoursePlanAdapter;
import xyz.institutionmanager.sailfish.R;
import xyz.institutionmanager.sailfish.Util.Ref;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CoursePlanListActivity extends BaseActivity implements HttpResultListener,RVCoursePlanAdapter.OnItemClickListener {

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

    @Override
    public void onRespStatus(String body, int source) {
        switch (NetRespStatType.dealWithRespStat(body)) {
            case EMPTY:
                ViewHandler.toastShow(CoursePlanListActivity.this,"暂无排课");
                break;
            default:break;
        }
    }

    @Override
    public void onRespMapList(String body, int source) throws IOException {
        mapList = JsonUtil.strToListMap(body,keys);
        updateViewComponent();
    }

    @Override
    public void onRespError(int source) {
        ViewHandler.toastShow(CoursePlanListActivity.this,Ref.UNKNOWN_ERROR);
    }

    @Override
    public void onReqFailure(Object object, int source) {
        ViewHandler.toastShow(CoursePlanListActivity.this, Ref.CANT_CONNECT_INTERNET);
    }

    @Override
    public void onRespSessionExpired(int source) {
        ViewHandler.alertShowAndExitApp(this);
    }

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
        HttpCallback callback = new HttpCallback(this,this,0);
        NetUtil.reqSendGet(this,url,callback);
    }

    private void updateViewComponent() {
        adapter = new RVCoursePlanAdapter(mapList,CoursePlanListActivity.this);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);
    }


}
