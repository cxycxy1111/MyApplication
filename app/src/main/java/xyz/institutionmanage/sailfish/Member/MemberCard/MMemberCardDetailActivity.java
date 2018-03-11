package xyz.institutionmanage.sailfish.Member.MemberCard;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import xyz.institutionmanage.sailfish.R;
import xyz.institutionmanage.sailfish.Shopmember.Adapter.RVMainHintAdapter;
import xyz.institutionmanage.sailfish.Util.BaseActivity;
import xyz.institutionmanage.sailfish.Util.Enum.EnumRespStatType;
import xyz.institutionmanage.sailfish.Util.Enum.EnumRespType;
import xyz.institutionmanage.sailfish.Util.JsonHandler;
import xyz.institutionmanage.sailfish.Util.MethodTool;
import xyz.institutionmanage.sailfish.Util.NetUtil;
import xyz.institutionmanage.sailfish.Util.Ref;

public class MMemberCardDetailActivity extends BaseActivity {

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
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(MMemberCardDetailActivity.this, Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                EnumRespType respType = EnumRespType.dealWithResponse(resp);
                switch (respType) {
                    case RESP_MAPLIST:
                        maplist_member_card_detail = JsonHandler.strToListMap(resp,strs_keys_member_card_detail);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initRecyclerView();
                            }
                        });
                        break;
                    case RESP_STAT:
                        EnumRespStatType respStatType = EnumRespStatType.dealWithRespStat(resp);
                        switch (respStatType){
                            case NSR:
                                MethodTool.showToast(MMemberCardDetailActivity.this,"未找到会员卡");
                                MMemberCardDetailActivity.this.finish();
                                break;
                            case SESSION_EXPIRED:
                                MethodTool.showExitAppAlert(MMemberCardDetailActivity.this);
                                break;
                            case AUTHORIZE_FAIL:
                                MethodTool.exitAcitivityDueToAuthorizeFail(MMemberCardDetailActivity.this);
                                break;
                            default:break;
                        }
                        break;
                    default:break;
                }
            }
        };
        NetUtil.sendHttpRequest(MMemberCardDetailActivity.this,url,callback);
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                MMemberCardDetailActivity.this.finish();
                break;
            default:break;
        }
        return true;
    }
}
