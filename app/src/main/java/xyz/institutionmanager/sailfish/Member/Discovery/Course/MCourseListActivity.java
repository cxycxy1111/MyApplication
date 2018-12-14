package xyz.institutionmanager.sailfish.Member.Discovery.Course;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.alfred.alfredtools.*;
import xyz.institutionmanager.sailfish.Adapter.RVCourseListAdapter;
import xyz.institutionmanager.sailfish.R;
import xyz.institutionmanager.sailfish.Util.Ref;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class MCourseListActivity extends BaseActivity implements HttpResultListener {

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
        String url = "/mCourseListQuery";
        HttpCallback callback = new HttpCallback(this,this,0);
        NetUtil.reqSendGet(this,url,callback);
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

    private void dealWithDataBeforeShow() {
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

    @Override
    public void onRespStatus(String body, int source) {
        switch (NetRespStatType.dealWithRespStat(body)) {
            case NSR:
                ViewHandler.toastShow(MCourseListActivity.this,Ref.OP_NSR);
                break;
            case EMPTY:
                ViewHandler.toastShow(MCourseListActivity.this,Ref.OP_NSR);
                break;
            default:break;
        }
    }

    @Override
    public void onRespMapList(String body, int source) throws IOException {
        String [] keys = new String[] {"id","name","last_time","type","supportedcard"};
        mapArrayList = JsonUtil.strToListMap(body,keys);
        dealWithDataBeforeShow();
    }

    @Override
    public void onRespError(int source) {
        ViewHandler.toastShow(MCourseListActivity.this,Ref.UNKNOWN_ERROR);
    }

    @Override
    public void onReqFailure(Object object, int source) {

    }

    @Override
    public void onRespSessionExpired(int source) {
        ViewHandler.alertShowAndExitApp(this);
    }
}
