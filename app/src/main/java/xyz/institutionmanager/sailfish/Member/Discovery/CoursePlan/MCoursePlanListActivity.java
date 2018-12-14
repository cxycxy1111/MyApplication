package xyz.institutionmanager.sailfish.Member.Discovery.CoursePlan;

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
import java.util.Map;

public class MCoursePlanListActivity extends BaseActivity implements HttpResultListener,RVCoursePlanAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private ArrayList<Map<String,String>> mapArrayList;
    private LinearLayoutManager linearLayoutManager;
    private RVCoursePlanAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mcourse_plan_list);
        initViews();
        initDatas();
    }

    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_general);
        toolbar.setTitle("全部排课");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = (RecyclerView)findViewById(R.id.rv_a_m_courseplan_list);
        linearLayoutManager = new LinearLayoutManager(MCoursePlanListActivity.this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void initDatas() {
        String url = "/mCoursePlanListQuery";
        HttpCallback callback = new HttpCallback(this,this,0);
        NetUtil.reqSendGet(this,url,callback);
    }

    private void initRecyclerView() {
        if (adapter == null) {
            adapter = new RVCoursePlanAdapter(mapArrayList, MCoursePlanListActivity.this);
            adapter.setOnItemClickListener(this);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                MCoursePlanListActivity.this.finish();
                break;
            default:break;
        }
        return true;
    }

    @Override
    public void onRespStatus(String body, int source) {
        switch (NetRespStatType.dealWithRespStat(body)) {
            case EMPTY:
                ViewHandler.toastShow(MCoursePlanListActivity.this,"暂无排课");
                break;
            default:break;
        }
    }

    @Override
    public void onRespMapList(String body, int source) throws IOException {
        String[] keys = new String[]{"courseplan_id","course_name","course_type","classroom_name","end_time","start_time"};
        mapArrayList = JsonUtil.strToListMap(body,keys);
        initRecyclerView();
    }

    @Override
    public void onRespError(int source) {
        ViewHandler.toastShow(MCoursePlanListActivity.this,Ref.UNKNOWN_ERROR);
    }

    @Override
    public void onReqFailure(Object object, int source) {
        ViewHandler.toastShow(MCoursePlanListActivity.this, Ref.CANT_CONNECT_INTERNET);
    }

    @Override
    public void onRespSessionExpired(int source) {
        ViewHandler.alertShowAndExitApp(this);
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent;
        switch (String.valueOf(mapArrayList.get(position).get("course_type"))) {
            case "4":
                intent = new Intent(MCoursePlanListActivity.this, MCoursePlanDetailPrivateActivity.class);
                intent.putExtra("cp_id", String.valueOf(mapArrayList.get(position).get("courseplan_id")));
                intent.putExtra("c_name", String.valueOf(mapArrayList.get(position).get("course_name")));
                intent.putExtra("start_time", String.valueOf(mapArrayList.get(position).get("start_time")));
                startActivity(intent);
                break;
            default:
                intent = new Intent(MCoursePlanListActivity.this, MCoursePlanDetailActivity.class);
                intent.putExtra("cp_id", String.valueOf(mapArrayList.get(position).get("courseplan_id")));
                intent.putExtra("c_name", String.valueOf(mapArrayList.get(position).get("course_name")));
                intent.putExtra("start_time", String.valueOf(mapArrayList.get(position).get("start_time")));
                startActivity(intent);
                break;
        }
    }

    /**
    @Override
    protected void onResume() {
        super.onResume();
        if (mapArrayList != null) {
            mapArrayList.clear();
            adapter.notifyDataSetChanged();
            initDatas();
        }
    }

    **/
}
