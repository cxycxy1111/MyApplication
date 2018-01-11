package com.example.dengweixiong.Shopmember.MemberCard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.dengweixiong.Shopmember.Adapter.RVWithHintAdapter;
import com.example.dengweixiong.Util.BaseActivity;
import com.example.dengweixiong.Util.Enum.EnumRespStatType;
import com.example.dengweixiong.Util.Enum.EnumRespType;
import com.example.dengweixiong.Util.JsonHandler;
import com.example.dengweixiong.Util.MethodTool;
import com.example.dengweixiong.Util.NetUtil;
import com.example.dengweixiong.Util.Ref;
import com.example.dengweixiong.myapplication.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MemberCardDetailActivity
        extends BaseActivity
        implements View.OnClickListener{

    private String seletec_mc_id;
    private static final String TOOLBAR_TITLE = "会员卡详情";

    private String[] strs_keys_member_card_detail = new String[] {"id","member_id","card_id","member_name","card_name","type","balance","start_time","expired_time"};

    private List<Map<String,String>> maplist_member_card_detail = new ArrayList<>();
    private List<Map<String,String>> maplist_member_card_detail_after = new ArrayList<>();

    private Toolbar toolbar;
    private Button btn_charge,btn_deduct;
    private RecyclerView rv_member_card_detail;
    private LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MemberCardDetailActivity.this,LinearLayoutManager.VERTICAL,false);

    private RVWithHintAdapter rv_member_card_adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_card_detail);
        initToolbar();
        initData();
        initViews();
        initMemberCardDetail();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        Map<String,String> map = maplist_member_card_detail.get(0);
        switch (v.getId()) {
            case R.id.btn_charge_member_card_detail:
                intent = new Intent(MemberCardDetailActivity.this,ChargeActivity.class);
                intent.putExtra("source","member_card_detail");
                intent.putExtra("m_id",String.valueOf(map.get("member_id")));
                intent.putExtra("c_id",String.valueOf(map.get("card_name")));
                intent.putExtra("mc_id",String.valueOf(map.get("id")));
                intent.putExtra("m_name",String.valueOf(map.get("member_name")));
                intent.putExtra("c_name",String.valueOf(map.get("card_name")));
                intent.putExtra("c_type",String.valueOf(map.get("type")));
                startActivityForResult(intent,1);
                break;
            case R.id.btn_deduct_member_card_detail:
                intent = new Intent(MemberCardDetailActivity.this,DeductionActivity.class);
                intent.putExtra("source","member_card_detail");
                intent.putExtra("m_id",String.valueOf(map.get("member_id")));
                intent.putExtra("c_id",String.valueOf(map.get("card_name")));
                intent.putExtra("mc_id",String.valueOf(map.get("id")));
                intent.putExtra("m_name",String.valueOf(map.get("member_name")));
                intent.putExtra("c_name",String.valueOf(map.get("card_name")));
                intent.putExtra("c_type",String.valueOf(map.get("type")));
                startActivityForResult(intent,2);
                break;
            default:break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                switch (resultCode) {

                }
                break;
            case 2:
                switch (resultCode) {

                }
                break;
            default:
                break;
        }
    }

    private void initData() {
        seletec_mc_id = getIntent().getStringExtra("mc_id");
    }

    private void initToolbar() {
        toolbar = (Toolbar)findViewById(R.id.tb_a_member_card_detail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(TOOLBAR_TITLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        btn_charge = (Button)findViewById(R.id.btn_charge_member_card_detail);
        btn_deduct = (Button)findViewById(R.id.btn_deduct_member_card_detail);
        rv_member_card_detail = (RecyclerView) findViewById(R.id.rv_a_member_card_detail);

        btn_charge.setOnClickListener(this);
        btn_deduct.setOnClickListener(this);
    }

    private void initMemberCardDetail() {
        String url = "/QueryMemberCardDetail?mc_id=" + seletec_mc_id;
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(MemberCardDetailActivity.this, Ref.CANT_CONNECT_INTERNET);
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
                        Map<String,String> map = JsonHandler.strToMap(resp);
                        EnumRespStatType respStatType = EnumRespStatType.dealWithRespStat(map);
                        switch (respStatType){
                            case NSR:
                                MethodTool.showToast(MemberCardDetailActivity.this,"未找到会员卡");
                                MemberCardDetailActivity.this.finish();
                                break;
                            default:break;
                        }
                    default:break;
                }
            }
        };
        NetUtil.sendHttpRequest(MemberCardDetailActivity.this,url,callback);
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

        rv_member_card_adapter = new RVWithHintAdapter(MemberCardDetailActivity.this,maplist_member_card_detail_after);
        rv_member_card_detail.setLayoutManager(linearLayoutManager);
        rv_member_card_detail.setAdapter(rv_member_card_adapter);
    }


}
