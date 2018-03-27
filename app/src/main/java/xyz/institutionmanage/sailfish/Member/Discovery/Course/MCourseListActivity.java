package xyz.institutionmanage.sailfish.Member.Discovery.Course;

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
import xyz.institutionmanage.sailfish.Adapter.RVCourseListAdapter;
import xyz.institutionmanage.sailfish.R;
import xyz.institutionmanage.sailfish.Util.BaseActivity;
import xyz.institutionmanage.sailfish.Util.Enum.EnumRespStatType;
import xyz.institutionmanage.sailfish.Util.Enum.EnumRespType;
import xyz.institutionmanage.sailfish.Util.JsonHandler;
import xyz.institutionmanage.sailfish.Util.MethodTool;
import xyz.institutionmanage.sailfish.Util.NetUtil;
import xyz.institutionmanage.sailfish.Util.Ref;

public class MCourseListActivity extends BaseActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private RVCourseListAdapter adapter;
    private ArrayList<Map<String,String>> mapArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_course_list);
        initViews();
        initToolbar();
        initData();
    }

    private void initViews() {
        toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        recyclerView = (RecyclerView)findViewById(R.id.rv_a_m_course_list);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("全部课程");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initData() {
        String url = "/mCourseListQuery";
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(MCourseListActivity.this, Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                switch (EnumRespType.dealWithResponse(resp)) {
                    case RESP_ERROR:
                        MethodTool.showToast(MCourseListActivity.this,Ref.UNKNOWN_ERROR);
                        break;
                    case RESP_STAT:
                        switch (EnumRespStatType.dealWithRespStat(resp)) {
                            case NSR:
                                MethodTool.showToast(MCourseListActivity.this,Ref.OP_NSR);
                                break;
                            case EMPTY_RESULT:
                                MethodTool.showToast(MCourseListActivity.this,Ref.OP_NSR);
                                break;
                            case SESSION_EXPIRED:
                                MethodTool.showExitAppAlert(MCourseListActivity.this);
                            default:break;
                        }
                        break;
                    case RESP_MAPLIST:
                        String [] keys = new String[] {"id","name","last_time","type","supportedcard"};
                        mapArrayList = JsonHandler.strToListMap(resp,keys);
                        dealWithDataBeforeShow();
                        break;
                    default:break;
                }
            }
        };
        NetUtil.sendHttpRequest(MCourseListActivity.this,url,callback);
    }

    private void dealWithDataBeforeShow() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter = new RVCourseListAdapter(mapArrayList,MCourseListActivity.this);
                adapter.setOnItemClickListener(new RVCourseListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        long cp_id = Long.parseLong(String.valueOf(mapArrayList.get(position).get("id")));
                        Intent intent;
                        if (String.valueOf(mapArrayList.get(position).get("type")).equals("4")){
                            intent = new Intent(MCourseListActivity.this,MCourseDetailPrivateActivity.class);
                        }else {
                            intent = new Intent(MCourseListActivity.this,MCourseDetailActivity.class);
                        }
                        intent.putExtra("c_id",cp_id);
                        intent.putExtra("c_name",String.valueOf(mapArrayList.get(position).get("name")));
                        startActivity(intent);
                    }
                });
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MCourseListActivity.this,LinearLayoutManager.VERTICAL,false);
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
                this.finish();
                break;
            default:break;
        }
        return true;
    }
}
