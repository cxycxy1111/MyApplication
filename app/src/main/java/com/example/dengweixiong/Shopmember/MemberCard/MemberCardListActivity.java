package com.example.dengweixiong.Shopmember.MemberCard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.dengweixiong.Shopmember.Adapter.RVWithHintAdapter;
import com.example.dengweixiong.Shopmember.Main.ShopmemberMainActivity;
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
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MemberCardListActivity extends BaseActivity{

    private int int_clicked_position;
    private boolean isFirstTimeToThisPage = true;
    private String source;
    private static final String TAG= "MemberCardListActivity:";
    private String s_id,sm_id;
    private String str_selected_mc_id;
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
        Intent intent = getIntent();
        String source = intent.getStringExtra("source");
        this.source = source;
        initViews();
        initData(source);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                if (source.equals("ShopmemberMainActivity")) {
                    intent = new Intent(this,ShopmemberMainActivity.class);
                    setResult(1);
                    finish();
                } else if (source.equals("MemberDetailActivity")) {
                    finish();
                }
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                switch (resultCode) {
                    case 1:
                        Map<String,String> map = maplist_membercard_origin.get(int_clicked_position);
                        map.put("balance",String.valueOf(data.getIntExtra("balance",0)));
                        maplist_membercard_origin.set(int_clicked_position,map);
                        rvWithHintAdapter.notifyDataSetChanged();
                        break;
                    default:break;
                }
                break;
            default:break;
        }
    }

    private void initToolbar() {
        toolbar = (Toolbar)findViewById(R.id.toolbar_a_member_card_list);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        spinner = (Spinner)findViewById(R.id.sp_a_member_card_list);
        spinner.setSelection(0,true);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isFirstTimeToThisPage == false) {
                    refreshListView(parent,position,id);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        recyclerView = (RecyclerView)findViewById(R.id.rv_a_member_card_list);
        linearLayoutManager = new LinearLayoutManager(MemberCardListActivity.this,LinearLayoutManager.VERTICAL,false);
        if (maplist_membercard_origin.size()!=0) {
            maplist_membercard_origin.clear();
        }
    }

    private void initData(String source) {
        String sm_id = MethodTool.getSharePreferenceValue(MemberCardListActivity.this,"sasm","sm_id",2);
        String s_id = MethodTool.getSharePreferenceValue(MemberCardListActivity.this,"sasm","s_id",2);

        switch (source) {
            case "ShopmemberMainActivity":
                initMemberList(s_id);
                break;
            case "MemberDetailActivity":
                String m_id = String.valueOf(getIntent().getLongExtra("m_id",0));
                String m_name = getIntent().getStringExtra("m_name");
                initSingleMemberData(m_id,m_name);
                break;
            default:break;
        }
    }

    private void initMemberList (String s_id) {
        String url = "/QueryMemberList?s_id=" + s_id;
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(MemberCardListActivity.this, Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                Log.d(TAG, " MemberList onResponse: " + resp);
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
                                initSpinner(String.valueOf(maplist_member_origin.get(0).get("id")));
                            }
                        });
                        break;
                    case RESP_STAT:
                        EnumRespStatType enumRespStatType = EnumRespStatType.dealWithRespStat(resp);
                        switch (enumRespStatType) {
                            case EMPTY_RESULT:
                                MethodTool.showToast(MemberCardListActivity.this,Ref.STAT_NSR);
                                MemberCardListActivity.this.finish();
                                break;
                            default:break;
                        }

                }
            }
        };
        NetUtil.sendHttpRequest(MemberCardListActivity.this,url,callback);
    }

    //初始化会员选择的spinner
    private void initSpinner(String m_id) {
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,list_member_name);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setDropDownVerticalOffset(120);
        spinner.setSelection(0);
        spinner.setAdapter(adapter);
        initFirstMemberRecyclerViewData(m_id);

    }

    //首次加载时加载第一位会员的会员卡
    private void initFirstMemberRecyclerViewData(String m_id) {
        String url = "/QueryMemberCardList?m_id=" + m_id;
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(MemberCardListActivity.this,Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                Log.d(TAG, "FirstrMemberView onResponse: " + resp);
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
                                isFirstTimeToThisPage = false;
                            }
                        });
                        break;
                    case RESP_ERROR:
                        MethodTool.showToast(MemberCardListActivity.this,Ref.UNKNOWN_ERROR);
                        break;
                    default:break;
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
                Log.d(TAG, "RefreshList onResponse: " + resp);
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
                                                int_clicked_position = position;
                                                Intent intent = new Intent(MemberCardListActivity.this,MemberCardDetailActivity.class);
                                                intent.putExtra("mc_id",String.valueOf(maplist_membercard_origin.get(position).get("id")));
                                                startActivityForResult(intent,1);
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
                        EnumRespStatType respStatType = EnumRespStatType.dealWithRespStat(resp);
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

    private void initSingleMemberData(String id,String name) {
        initToolbar();
        list_member_name.add(name);
        initSpinner(id);

    }

}
