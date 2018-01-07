package com.example.dengweixiong.Shopmember.MemberCard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.example.dengweixiong.Bean.Shop;
import com.example.dengweixiong.Shopmember.Adapter.RVWithHintAdapter;
import com.example.dengweixiong.Shopmember.Main.ShopmemberMainActivity;
import com.example.dengweixiong.Shopmember.Member.MemberDetailActivity;
import com.example.dengweixiong.Util.BaseActivity;
import com.example.dengweixiong.Util.Enum.EnumRespStatType;
import com.example.dengweixiong.Util.Enum.EnumRespType;
import com.example.dengweixiong.Util.Enum.EnumResultCodeType;
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

public class MemberCardListActivity extends BaseActivity{

    private String source;
    private String s_id,sm_id;

    private String [] strs_keys_member = {"id","name"};
    private String [] strs_keys_membercard = {"id","name","balance"};

    private List<String> list_member_name = new ArrayList<>();
    private List<Map<String,String>> maplist_member_origin = new ArrayList<>();
    private List<Map<String,String>> maplist_membercard_origin = new ArrayList<>();
    List<Map<String,Object>> mapList = new ArrayList<Map<String, Object>>();

    private Toolbar toolbar;
    private Spinner spinner;
    private RecyclerView recyclerView;
    private ArrayAdapter<String> adapter;
    private LinearLayoutManager linearLayoutManager;

    private RVWithHintAdapter rvWithHintAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_card_list);
        source = getIntent().getStringExtra("source");

        initViews();
        initData();
        initMemberList();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                if (source == "MemberFragment") {
                    intent = new Intent(this,ShopmemberMainActivity.class);
                    setResult(1);
                    MemberCardListActivity.this.finish();
                } else if (source == "MemberDetailActivity") {
                    intent = new Intent(this,MemberDetailActivity.class);
                    startActivity(intent);
                }
                this.finish();
                break;
            default:
                break;
        }
        return true;
    }

    private void initToolbar() {
        toolbar = (Toolbar)findViewById(R.id.toolbar_a_member_card_list);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        spinner = (Spinner)findViewById(R.id.sp_a_member_card_list);
        recyclerView = (RecyclerView)findViewById(R.id.rv_a_member_card_list);
        linearLayoutManager = new LinearLayoutManager(MemberCardListActivity.this,LinearLayoutManager.VERTICAL,false);
        if (maplist_membercard_origin.size()!=0) {
            maplist_membercard_origin.clear();
        }
    }

    private void initData() {
        sm_id = MethodTool.getSharePreferenceValue(MemberCardListActivity.this,"sasm","sm_id",2);
        s_id = MethodTool.getSharePreferenceValue(MemberCardListActivity.this,"sasm","s_id",2);
    }

    private void initMemberList () {
        String url = "/QueryMemberList?s_id=" + s_id;
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(MemberCardListActivity.this, Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                EnumRespType enumRespType = EnumRespType.dealWithResponse(resp);
                switch (enumRespType) {
                    case RESP_MAPLIST:
                        maplist_member_origin = JsonHandler.strToListMap(resp,strs_keys_member);
                        for (int i = 0;i < maplist_member_origin.size();i++) {
                            list_member_name.add(maplist_member_origin.get(i).get("name"));
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initToolbar();
                                initSpinner();
                            }
                        });
                        break;
                    case RESP_STAT:
                        Map<String,String> map = JsonHandler.strToMap(resp);
                        EnumRespStatType enumRespStatType = EnumRespStatType.dealWithRespStat(map);
                        switch (enumRespStatType) {
                            case EMPTY_RESULT:MethodTool.showToast(MemberCardListActivity.this,Ref.STAT_NSR);break;
                            default:break;
                        }

                }
            }
        };
        NetUtil.sendHttpRequest(MemberCardListActivity.this,url,callback);
    }



    //初始化会员选择的spinner
    private void initSpinner() {
        adapter = new ArrayAdapter<>(this,R.layout.tile_spinner,R.id.tv_tile_spinner,list_member_name);
        adapter.setDropDownViewResource(R.layout.tile_spinner_dropdown);
        spinner.setDropDownVerticalOffset(120);
        spinner.setSelection(0);
        spinner.setAdapter(adapter);
        initFirstMemberRecyclerViewData();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                refreshListView(parent,position,id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spinner.setSelection(0);
            }
        });
    }

    //首次加载时加载第一位会员的会员卡
    private void initFirstMemberRecyclerViewData() {
        String url = "/QueryMemberCardList?m_id=" + String.valueOf(maplist_member_origin.get(0).get("id"));
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(MemberCardListActivity.this,Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                EnumRespType respType = EnumRespType.dealWithResponse(resp);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setLayoutManager(linearLayoutManager);
                    }
                });
                switch (respType) {
                    case RESP_MAPLIST:
                        maplist_membercard_origin = JsonHandler.strToListMap(resp,strs_keys_membercard);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                rvWithHintAdapter = new RVWithHintAdapter(MemberCardListActivity.this,maplist_membercard_origin);
                                rvWithHintAdapter.setOnItemClickListener(new RVWithHintAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, int position) {
                                        Intent intent = new Intent(MemberCardListActivity.this,MemberCardDetailActivity.class);
                                        intent.putExtra("mc_id",String.valueOf(maplist_membercard_origin.get(position).get("id")));
                                        MemberCardListActivity.this.startActivity(intent);
                                    }
                                });
                                recyclerView.setAdapter(rvWithHintAdapter);
                            }
                        });
                }
            }
        };
        NetUtil.sendHttpRequest(MemberCardListActivity.this,url,callback);
    }

    //刷新会员卡列表
    private void refreshListView(AdapterView<?> parent,int position,long id) {
        String m_id = String.valueOf(maplist_member_origin.get(position).get("id"));
        String url = "/QueryMemberCardList?m_id=" + m_id;
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(MemberCardListActivity.this,Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                EnumRespType enumRespType = EnumRespType.dealWithResponse(resp);
                switch (enumRespType) {
                    case RESP_MAPLIST:
                        maplist_membercard_origin.clear();
                        List<Map<String,String>> maplist_temp_memebrcard_origin = new ArrayList<>();
                        maplist_temp_memebrcard_origin = JsonHandler.strToListMap(resp,strs_keys_membercard);
                        maplist_membercard_origin.addAll(maplist_temp_memebrcard_origin);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (recyclerView.getLayoutManager() == null) {
                                    recyclerView.setLayoutManager(linearLayoutManager);
                                }
                                if (recyclerView.getAdapter() == null) {
                                    rvWithHintAdapter = new RVWithHintAdapter(MemberCardListActivity.this,maplist_membercard_origin);
                                    if (rvWithHintAdapter.getOnItemClickListener() == null) {
                                        rvWithHintAdapter.setOnItemClickListener(new RVWithHintAdapter.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(View view, int position) {
                                                Intent intent = new Intent(MemberCardListActivity.this,MemberCardDetailActivity.class);
                                                intent.putExtra("mc_id",String.valueOf(maplist_membercard_origin.get(position).get("id")));
                                                startActivity(intent);
                                            }
                                        });
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            recyclerView.setAdapter(rvWithHintAdapter);
                                        }
                                    });
                                }else {
                                    rvWithHintAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                        break;
                    case RESP_STAT:
                        Map<String,String> map = JsonHandler.strToMap(resp);
                        EnumRespStatType respStatType = EnumRespStatType.dealWithRespStat(map);
                        switch (respStatType) {
                            case EMPTY_RESULT:
                                MethodTool.showToast(MemberCardListActivity.this,"该会员名下暂无会员卡");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        maplist_membercard_origin.clear();
                                        rvWithHintAdapter.notifyDataSetChanged();
                                    }
                                });
                                break;
                            default:
                                break;
                        }
                    default:break;
                }
            }
        };
        NetUtil.sendHttpRequest(MemberCardListActivity.this,url,callback);
    }

}
