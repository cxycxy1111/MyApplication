package xyz.institutionmanager.sailfish.Member.Profile.MemberCard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.alfred.alfredtools.*;
import xyz.institutionmanager.sailfish.Adapter.RVMemberCardListAdapter;
import xyz.institutionmanager.sailfish.R;
import xyz.institutionmanager.sailfish.Util.Ref;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MMemberCardListActivity extends BaseActivity implements HttpResultListener {

    private String [] strs_keys_membercard = {"id","name","balance","card_id","type"};
    private List<Map<String,String>> maplist_membercard_origin = new ArrayList<>();
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private RVMemberCardListAdapter RVMemberCardListtAdapter;
    private LinearLayoutManager linearLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mmember_card_list);
        initViews();
        initRecyclerViewData();
    }

    private void initViews() {
        toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        recyclerView = (RecyclerView)findViewById(R.id.rv_a_m_membercard_list);

        toolbar.setTitle("我的会员卡");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initRecyclerViewData() {
        String url = "/mMemberCardListQuery";
        HttpCallback callback = new HttpCallback(this,this,0);
        NetUtil.reqSendGet(this,url,callback);
    }

    private void initRecyclerView() {
        getSupportActionBar().setTitle("我的会员卡(" + maplist_membercard_origin.size() + ")");
        linearLayoutManager = new LinearLayoutManager(MMemberCardListActivity.this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        RVMemberCardListtAdapter = new RVMemberCardListAdapter(MMemberCardListActivity.this,maplist_membercard_origin);
        RVMemberCardListtAdapter.setOnItemClickListener(new RVMemberCardListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(MMemberCardListActivity.this,MMemberCardDetailActivity.class);
                intent.putExtra("mc_id",String.valueOf(maplist_membercard_origin.get(position).get("id")));
                intent.putExtra("c_name",String.valueOf(maplist_membercard_origin.get(position).get("name")));
                MMemberCardListActivity.this.startActivity(intent);
            }
        });
        recyclerView.setAdapter(RVMemberCardListtAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                MMemberCardListActivity.this.finish();
                break;
            default:break;
        }
        return true;
    }

    @Override
    public void onRespStatus(String body, int source) {
        switch (NetRespStatType.dealWithRespStat(body)) {
            case EMPTY:
                ViewHandler.toastShow(MMemberCardListActivity.this,"你尚未办理任何会员卡");
            default:break;
        }
    }

    @Override
    public void onRespMapList(String body, int source) throws IOException {
        maplist_membercard_origin = JsonUtil.strToListMap(body,strs_keys_membercard);
        initRecyclerView();
    }

    @Override
    public void onRespError(int source) {
        ViewHandler.toastShow(MMemberCardListActivity.this,Ref.UNKNOWN_ERROR);
    }

    @Override
    public void onReqFailure(Object object, int source) {

    }

    @Override
    public void onRespSessionExpired(int source) {
        ViewHandler.alertShowAndExitApp(this);
    }
}
