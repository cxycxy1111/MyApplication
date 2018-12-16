package xyz.instmgr.sailfish.Member.Profile.MemberCard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.alfred.alfredtools.*;
import xyz.instmgr.sailfish.Adapter.RVMainHintAdapter;
import xyz.instmgr.sailfish.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MMemberCardDetailActivity extends BaseActivity implements View.OnClickListener,HttpResultListener {

    private String mc_id;
    private String c_name;
    private Toolbar toolbar;

    private String[] strs_keys_member_card_detail = new String[] {"id","member_id","card_id","member_name","card_name","type","balance","start_time","expired_time"};
    private List<Map<String,String>> maplist_member_card_detail = new ArrayList<>();
    private List<Map<String,String>> maplist_member_card_detail_after = new ArrayList<>();
    private RecyclerView rv_member_card_detail;
    private LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MMemberCardDetailActivity.this,LinearLayoutManager.VERTICAL,false);
    private RVMainHintAdapter rv_member_card_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mmember_card_detail);
        initDataFromPreviousActivity();
        initViewComponent();
        initMemberCardDetail();
    }

    private void initViewComponent() {
        toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        toolbar.setTitle(c_name);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rv_member_card_detail = (RecyclerView)findViewById(R.id.rv_a_m_membercard_detail);
    }

    private void initDataFromPreviousActivity() {
        mc_id = getIntent().getStringExtra("mc_id");
        c_name = getIntent().getStringExtra("c_name");
    }

    private void initMemberCardDetail() {
        String url = "/mMemberCardDetailQuery?mc_id=" + mc_id;
        HttpCallback callback = new HttpCallback(this,this,0);
        NetUtil.reqSendGet(this,url,callback);
    }

    private void initRecyclerView() {
        Map<String,String> map_origin = maplist_member_card_detail.get(0);
        if (String.valueOf(map_origin.get("type")).equals("1")) {
            Map<String,String> map_type = new HashMap<>();
            map_type.put("name","会员卡类型");
            map_type.put("balance","余额卡");

            Map<String,String> map_balance = new HashMap<>();
            map_balance.put("name","卡内余额");
            map_balance.put("balance",String.valueOf(map_origin.get("balance")));

            Map<String,String> map_start_time = new HashMap<>();
            map_start_time.put("name","生效时间");
            map_start_time.put("balance",String.valueOf(map_origin.get("start_time")).split(" ")[0]);

            Map<String,String> map_invalid_time = new HashMap<>();
            map_invalid_time.put("name","失效时间");
            map_invalid_time.put("balance",String.valueOf(map_origin.get("expired_time")).split(" ")[0]);

            maplist_member_card_detail_after.add(map_type);
            maplist_member_card_detail_after.add(map_balance);
            maplist_member_card_detail_after.add(map_start_time);
            maplist_member_card_detail_after.add(map_invalid_time);

        } else if (String.valueOf(map_origin.get("type")).equals("2")) {
            Map<String,String> map_type = new HashMap<>();
            map_type.put("name","会员卡类型");
            map_type.put("balance","次卡");

            Map<String,String> map_balance = new HashMap<>();
            map_balance.put("name","卡内次数");
            map_balance.put("balance",String.valueOf(map_origin.get("balance")));

            Map<String,String> map_start_time = new HashMap<>();
            map_start_time.put("name","生效时间");
            map_start_time.put("balance",String.valueOf(map_origin.get("start_time")).split(" ")[0]);

            Map<String,String> map_invalid_time = new HashMap<>();
            map_invalid_time.put("name","失效时间");
            map_invalid_time.put("balance",String.valueOf(map_origin.get("expired_time")).split(" ")[0]);

            maplist_member_card_detail_after.add(map_type);
            maplist_member_card_detail_after.add(map_balance);
            maplist_member_card_detail_after.add(map_start_time);
            maplist_member_card_detail_after.add(map_invalid_time);
        }else if (String.valueOf(map_origin.get("type")).equals("3")) {
            Map<String,String> map_type = new HashMap<>();
            map_type.put("name","会员卡类型");
            map_type.put("balance","有效期卡");

            Map<String,String> map_start_time = new HashMap<>();
            map_start_time.put("name","生效时间");
            map_start_time.put("balance",String.valueOf(map_origin.get("start_time")).split(" ")[0]);

            Map<String,String> map_invalid_time = new HashMap<>();
            map_invalid_time.put("name","失效时间");
            map_invalid_time.put("balance",String.valueOf(map_origin.get("expired_time")).split(" ")[0]);

            maplist_member_card_detail_after.add(map_type);
            maplist_member_card_detail_after.add(map_start_time);
            maplist_member_card_detail_after.add(map_invalid_time);
        }

        rv_member_card_adapter = new RVMainHintAdapter(MMemberCardDetailActivity.this,maplist_member_card_detail_after);
        rv_member_card_detail.setLayoutManager(linearLayoutManager);
        rv_member_card_detail.setAdapter(rv_member_card_adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_m_member_card_detail,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                MMemberCardDetailActivity.this.finish();
                break;
            case R.id.consume_log_member_card_detail:
                Intent intent = new Intent(MMemberCardDetailActivity.this,MMemberCardConsumeLogActivity.class);
                intent.putExtra("mc_id",mc_id);
                startActivity(intent);
                break;
            default:break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onRespStatus(String body, int source) {
        NetRespStatType respStatType = NetRespStatType.dealWithRespStat(body);
        switch (respStatType){
            case NSR:
                ViewHandler.toastShow(MMemberCardDetailActivity.this,"未找到会员卡");
                MMemberCardDetailActivity.this.finish();
                break;
            case STATUS_AUTHORIZE_FAIL:
                ViewHandler.exitAcitivityDueToAuthorizeFail(MMemberCardDetailActivity.this);
                break;
            default:break;
        }
    }

    @Override
    public void onRespMapList(String body, int source) throws IOException {
        maplist_member_card_detail = JsonUtil.strToListMap(body,strs_keys_member_card_detail);
        initRecyclerView();
    }

    @Override
    public void onRespError(int source) {

    }

    @Override
    public void onReqFailure(Object object, int source) {

    }

    @Override
    public void onRespSessionExpired(int source) {
        ViewHandler.alertShowAndExitApp(this);
    }
}
