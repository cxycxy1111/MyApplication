package xyz.institutionmanage.sailfish.Shopmember.MemberCard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import xyz.institutionmanage.sailfish.R;
import xyz.institutionmanage.sailfish.Shopmember.Adapter.RVMemberCardListtAdapter;
import xyz.institutionmanage.sailfish.Util.BaseActivity;
import xyz.institutionmanage.sailfish.Util.Enum.EnumRespStatType;
import xyz.institutionmanage.sailfish.Util.Enum.EnumRespType;
import xyz.institutionmanage.sailfish.Util.JsonHandler;
import xyz.institutionmanage.sailfish.Util.MethodTool;
import xyz.institutionmanage.sailfish.Util.NetUtil;
import xyz.institutionmanage.sailfish.Util.Ref;

public class MemberCardDetailActivity
        extends BaseActivity
        implements View.OnClickListener{

    private String seletec_mc_id;
    private String balance_after_change,invalid_time_after_change;
    private static final String TOOLBAR_TITLE = "会员卡详情";

    private String[] strs_keys_member_card_detail = new String[] {"id","member_id","card_id","member_name","card_name","type","balance","start_time","expired_time"};

    private List<Map<String,String>> maplist_member_card_detail = new ArrayList<>();
    private List<Map<String,String>> maplist_member_card_detail_after = new ArrayList<>();

    private Toolbar toolbar;
    private Button btn_charge,btn_deduct;
    private RecyclerView rv_member_card_detail;
    private LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MemberCardDetailActivity.this,LinearLayoutManager.VERTICAL,false);

    private RVMemberCardListtAdapter rv_member_card_adapter;


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
                Intent intent = new Intent(MemberCardDetailActivity.this,MemberCardListActivity.class);
                intent.putExtra("balance",balance_after_change);
                setResult(1,intent);
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
                    case 1:
                        switch (data.getStringExtra("type")) {
                            case "1":
                                Map<String,String> map_balance = new HashMap<>();
                                Map<String,String> map_invalid_date = new HashMap<>();

                                map_balance = maplist_member_card_detail_after.get(1);
                                balance_after_change = data.getStringExtra("balance");
                                int int_temp_balance = Integer.parseInt(map_balance.get("balance")) + Integer.parseInt(balance_after_change);
                                map_balance.put("balance",String.valueOf(int_temp_balance));
                                maplist_member_card_detail_after.set(1,map_balance);

                                map_invalid_date = maplist_member_card_detail_after.get(3);
                                map_invalid_date.put("balance",data.getStringExtra("invalid_time").split(" ")[0]);
                                maplist_member_card_detail_after.set(3,map_invalid_date);

                                break;
                            case "2":
                                map_balance = maplist_member_card_detail_after.get(1);

                                balance_after_change = data.getStringExtra("balance");
                                int_temp_balance = Integer.parseInt(map_balance.get("balance")) + Integer.parseInt(balance_after_change);
                                map_balance.put("balance",String.valueOf(int_temp_balance));

                                maplist_member_card_detail_after.set(1,map_balance);
                                map_invalid_date = maplist_member_card_detail_after.get(3);
                                map_invalid_date.put("balance",data.getStringExtra("invalid_time").split(" ")[0]);
                                maplist_member_card_detail_after.set(3,map_invalid_date);

                                break;
                            case "3":
                                map_invalid_date = maplist_member_card_detail_after.get(2);
                                map_invalid_date.put("balance",data.getStringExtra("invalid_time").split(" ")[0]);
                                maplist_member_card_detail_after.set(2,map_invalid_date);
                                balance_after_change = "0";

                                break;
                            default:break;
                        }

                        rv_member_card_adapter.notifyDataSetChanged();
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
                        EnumRespStatType respStatType = EnumRespStatType.dealWithRespStat(resp);
                        switch (respStatType){
                            case NSR:
                                MethodTool.showToast(MemberCardDetailActivity.this,"未找到会员卡");
                                MemberCardDetailActivity.this.finish();
                                break;
                            case SESSION_EXPIRED:
                                MethodTool.showExitAppAlert(MemberCardDetailActivity.this);
                                break;
                            case AUTHORIZE_FAIL:
                                MethodTool.exitAcitivityDueToAuthorizeFail(MemberCardDetailActivity.this);
                                break;
                            default:break;
                        }
                        break;
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

        rv_member_card_adapter = new RVMemberCardListtAdapter(MemberCardDetailActivity.this,maplist_member_card_detail_after);
        rv_member_card_detail.setLayoutManager(linearLayoutManager);
        rv_member_card_detail.setAdapter(rv_member_card_adapter);
    }

}
