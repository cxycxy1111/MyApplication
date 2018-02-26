package xyz.institutionmanage.sailfish.Member.CoursePlan;

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
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import xyz.institutionmanage.sailfish.R;
import xyz.institutionmanage.sailfish.Shopmember.Adapter.RVCoursePlanAdapter;
import xyz.institutionmanage.sailfish.Util.BaseActivity;
import xyz.institutionmanage.sailfish.Util.Enum.EnumRespType;
import xyz.institutionmanage.sailfish.Util.JsonHandler;
import xyz.institutionmanage.sailfish.Util.MethodTool;
import xyz.institutionmanage.sailfish.Util.NetUtil;
import xyz.institutionmanage.sailfish.Util.Ref;

public class MCoursePlanListActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private ArrayList<Map<String,String>> mapArrayList;
    private LinearLayoutManager linearLayoutManager;

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
    }

    private void initDatas() {
        String url = "/mCoursePlanListQuery";
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(MCoursePlanListActivity.this, Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                switch (EnumRespType.dealWithResponse(resp)) {
                    case RESP_MAPLIST:
                        String[] keys = new String[]{"courseplan_id","course_name","course_type","classroom_name","end_time","start_time"};
                        mapArrayList = JsonHandler.strToListMap(resp,keys);
                        initRecyclerView();
                        break;
                    case RESP_STAT:
                        break;
                    case RESP_ERROR:
                        MethodTool.showToast(MCoursePlanListActivity.this,Ref.UNKNOWN_ERROR);
                        break;
                    default:break;
                }
            }
        };
        NetUtil.sendHttpRequest(MCoursePlanListActivity.this,url,callback);
    }

    private void initRecyclerView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                linearLayoutManager = new LinearLayoutManager(MCoursePlanListActivity.this,LinearLayoutManager.VERTICAL,false);
                recyclerView.setLayoutManager(linearLayoutManager);
                RVCoursePlanAdapter adapter = new RVCoursePlanAdapter(mapArrayList,MCoursePlanListActivity.this);
                adapter.setOnItemClickListener(new RVCoursePlanAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent;
                        switch (String.valueOf(mapArrayList.get(position).get("course_type"))) {
                            case "4":
                                intent = new Intent(MCoursePlanListActivity.this,MCoursePlanDetailPrivateActivity.class);
                                intent.putExtra("cp_id",String.valueOf(mapArrayList.get(position).get("courseplan_id")));
                                intent.putExtra("c_name",String.valueOf(mapArrayList.get(position).get("course_name")));
                                intent.putExtra("start_time",String.valueOf(mapArrayList.get(position).get("start_time")));
                                startActivity(intent);
                                break;
                            default:
                                intent = new Intent(MCoursePlanListActivity.this,MCoursePlanDetailActivity.class);
                                intent.putExtra("cp_id",String.valueOf(mapArrayList.get(position).get("courseplan_id")));
                                intent.putExtra("c_name",String.valueOf(mapArrayList.get(position).get("course_name")));
                                intent.putExtra("start_time",String.valueOf(mapArrayList.get(position).get("start_time")));
                                startActivity(intent);
                                break;
                        }
                    }
                });
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(adapter);
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
                MCoursePlanListActivity.this.finish();
                break;
            default:break;
        }
        return true;
    }
}
