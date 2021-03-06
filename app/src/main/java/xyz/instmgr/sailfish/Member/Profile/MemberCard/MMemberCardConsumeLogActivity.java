package xyz.instmgr.sailfish.Member.Profile.MemberCard;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.alfred.alfredtools.*;
import xyz.instmgr.sailfish.Adapter.RVMemberCardConsumeLogAdapter;
import xyz.instmgr.sailfish.R;
import xyz.instmgr.sailfish.Util.Ref;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MMemberCardConsumeLogActivity extends BaseActivity implements HttpResultListener {

    private String mc_id = "0";
    private String[] keys_consume_log = new String[] {"consume","consume_time","operation","type"};
    private RecyclerView recyclerView;
    private RVMemberCardConsumeLogAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private List<Map<String,String>> mapList = new ArrayList<>();
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mmember_card_consume_log);
        initData();
        initViews();
        ViewHandler.progressBarShow(this);
        initRecyclerViewDataFromWeb();
    }

    private void initData() {
        mc_id = getIntent().getStringExtra("mc_id");
    }

    private void initViews() {
        toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        toolbar.setTitle("消费记录");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = (RecyclerView)findViewById(R.id.rv_a_m_member_card_consume_log);
        linearLayoutManager = new LinearLayoutManager(MMemberCardConsumeLogActivity.this,LinearLayoutManager.VERTICAL,false);
    }

    private void initRecyclerViewDataFromWeb() {
        String url = "/mMemberCardConsumeLogQuery?mc_id=" + mc_id;
        HttpCallback callback = new HttpCallback(this,this,0);
        NetUtil.reqSendGet(this,url,callback);
    }

    private void initRecyclerView() {
        adapter = new RVMemberCardConsumeLogAdapter(mapList,MMemberCardConsumeLogActivity.this);
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
                MMemberCardConsumeLogActivity.this.finish();
                break;
            default:break;
        }
        return true;
    }

    @Override
    public void onRespStatus(String body, int source) {
        ViewHandler.progressBarHide(this);
        switch (NetRespStatType.dealWithRespStat(body)) {
            case EMPTY:
                ViewHandler.toastShow(MMemberCardConsumeLogActivity.this,"该卡暂未消费");
                break;
            default:break;
        }
    }

    @Override
    public void onRespMapList(String body, int source) throws IOException {
        ViewHandler.progressBarHide(this);
        mapList = JsonUtil.strToListMap(body,keys_consume_log);
        initRecyclerView();
    }

    @Override
    public void onRespError(int source) {
        ViewHandler.progressBarHide(this);
        ViewHandler.snackbarShowLow(this,toolbar,Ref.UNKNOWN_ERROR);
    }

    @Override
    public void onReqFailure(Object object, int source) {
        ViewHandler.progressBarHide(this);
        ViewHandler.snackbarShowLow(this,toolbar,Ref.CANT_CONNECT_INTERNET);
    }

    @Override
    public void onRespSessionExpired(int source) {
        ViewHandler.progressBarHide(this);
        ViewHandler.alertShowAndExitApp(this);
    }
}
