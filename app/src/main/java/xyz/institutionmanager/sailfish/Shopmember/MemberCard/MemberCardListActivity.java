package xyz.institutionmanager.sailfish.Shopmember.MemberCard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.alfred.alfredtools.*;
import xyz.institutionmanager.sailfish.Adapter.RVMemberCardListAdapter;
import xyz.institutionmanager.sailfish.R;
import xyz.institutionmanager.sailfish.Shopmember.Main.ShopmemberMainActivity;
import xyz.institutionmanager.sailfish.Util.Ref;
import xyz.institutionmanager.sailfish.Util.SharePreferenceManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MemberCardListActivity
        extends BaseActivity
        implements HttpResultListener {

    private static final int REQUEST_QueryMemberList = 1;
    private static final int REQUEST_QueryMemberCardList = 2;
    private static final int REQUEST_QueryMemberCardList_REFRESH = 3;
    private int int_clicked_position;
    private boolean isFirstTimeToThisPage = true;
    private String source;
    private static final String TAG= "MemberCardListActivity:";
    private String s_id,sm_id;
    private String str_selected_mc_id;
    private String [] strs_keys_member = {"id","name"};
    private String [] strs_keys_membercard = {"id","name","balance","card_id"};

    private List<String> list_member_name = new ArrayList<>();
    private List<Map<String,String>> maplist_member_origin = new ArrayList<>();
    private List<Map<String,String>> maplist_membercard_origin = new ArrayList<>();
    List<Map<String,Object>> mapList = new ArrayList<Map<String, Object>>();

    private Toolbar toolbar;
    private Spinner spinner;
    private RecyclerView recyclerView;
    private ArrayAdapter<String> adapter;
    private LinearLayoutManager linearLayoutManager;

    private RVMemberCardListAdapter RVMemberCardListtAdapter;


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
                        RVMemberCardListtAdapter.notifyDataSetChanged();
                        break;
                    default:break;
                }
                break;
            default:break;
        }
    }

    @Override
    public void onRespStatus(String body, int source) {
        if (source == REQUEST_QueryMemberList) {
            NetRespStatType respStatType = NetRespStatType.dealWithRespStat(body);
            switch (respStatType) {
                case EMPTY:
                    ViewHandler.toastShow(MemberCardListActivity.this,Ref.STAT_NSR);
                    MemberCardListActivity.this.finish();
                    break;
                case STATUS_AUTHORIZE_FAIL:
                    ViewHandler.exitAcitivityDueToAuthorizeFail(MemberCardListActivity.this);
                    break;
                default:break;
            }
        }else if (source == REQUEST_QueryMemberCardList) {
            recyclerView.setLayoutManager(linearLayoutManager);
            switch (NetRespStatType.dealWithRespStat(body)) {
                case STATUS_AUTHORIZE_FAIL:
                    ViewHandler.exitAcitivityDueToAuthorizeFail(MemberCardListActivity.this);
                    break;
                default:break;
            }
        }else if (source == REQUEST_QueryMemberCardList_REFRESH) {
            NetRespStatType respStatType = NetRespStatType.dealWithRespStat(body);
            switch (respStatType) {
                case EMPTY:
                    ViewHandler.toastShow(MemberCardListActivity.this,"该会员名下暂无会员卡");
                    maplist_membercard_origin.clear();
                    RVMemberCardListtAdapter.notifyDataSetChanged();
                    break;
                case STATUS_AUTHORIZE_FAIL:
                    ViewHandler.exitAcitivityDueToAuthorizeFail(MemberCardListActivity.this);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onRespMapList(String body, int source) throws IOException {
        if (source == REQUEST_QueryMemberList) {
            maplist_member_origin = JsonUtil.strToListMap(body,strs_keys_member);
            for (int i = 0;i < maplist_member_origin.size();i++) {
                list_member_name.add(maplist_member_origin.get(i).get("name"));
            }
            initToolbar();
            initSpinner(String.valueOf(maplist_member_origin.get(0).get("id")));
        }else if (source == REQUEST_QueryMemberCardList) {
            recyclerView.setLayoutManager(linearLayoutManager);
            maplist_membercard_origin = JsonUtil.strToListMap(body,strs_keys_membercard);
            RVMemberCardListtAdapter = new RVMemberCardListAdapter(MemberCardListActivity.this,maplist_membercard_origin);
            RVMemberCardListtAdapter.setOnItemClickListener(new RVMemberCardListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Intent intent = new Intent(MemberCardListActivity.this,MemberCardDetailActivity.class);
                    intent.putExtra("mc_id",String.valueOf(maplist_membercard_origin.get(position).get("id")));
                    MemberCardListActivity.this.startActivity(intent);
                }
            });
            recyclerView.setAdapter(RVMemberCardListtAdapter);
            isFirstTimeToThisPage = false;
        }else if (source == REQUEST_QueryMemberCardList_REFRESH) {
            maplist_membercard_origin.clear();
            List<Map<String,String>> maplist_temp_memebrcard_origin = new ArrayList<>();
            maplist_temp_memebrcard_origin = JsonUtil.strToListMap(body,strs_keys_membercard);
            maplist_membercard_origin.addAll(maplist_temp_memebrcard_origin);
            if (recyclerView.getLayoutManager() == null) {
                recyclerView.setLayoutManager(linearLayoutManager);
            }
            if (recyclerView.getAdapter() == null) {
                RVMemberCardListtAdapter = new RVMemberCardListAdapter(MemberCardListActivity.this,maplist_membercard_origin);
                if (RVMemberCardListtAdapter.getOnItemClickListener() == null) {
                    RVMemberCardListtAdapter.setOnItemClickListener(new RVMemberCardListAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            int_clicked_position = position;
                            Intent intent = new Intent(MemberCardListActivity.this,MemberCardDetailActivity.class);
                            intent.putExtra("mc_id",String.valueOf(maplist_membercard_origin.get(position).get("id")));
                            startActivityForResult(intent,1);
                        }
                    });
                }
                recyclerView.setAdapter(RVMemberCardListtAdapter);
            }else {
                RVMemberCardListtAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onRespError(int source) {
        ViewHandler.toastShow(MemberCardListActivity.this,Ref.UNKNOWN_ERROR);
    }

    @Override
    public void onReqFailure(Object object, int source) {
        ViewHandler.toastShow(MemberCardListActivity.this, Ref.CANT_CONNECT_INTERNET);
    }

    @Override
    public void onRespSessionExpired(int source) {
        ViewHandler.alertShowAndExitApp(MemberCardListActivity.this);
    }

    private void initToolbar() {
        toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("会员卡管理");
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
        String sm_id = SharePreferenceManager.getSharePreferenceValue(MemberCardListActivity.this,"sasm","sm_id",2);
        String s_id = SharePreferenceManager.getSharePreferenceValue(MemberCardListActivity.this,"sasm","s_id",2);

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
        String url = "/QueryMemberList";
        HttpCallback callback = new HttpCallback(this,this,REQUEST_QueryMemberList);
        NetUtil.reqSendGet(this,url,callback);
    }

    //初始化会员选择的spinner
    private void initSpinner(String m_id) {
        adapter = new ArrayAdapter<>(MemberCardListActivity.this,android.R.layout.simple_spinner_item,list_member_name);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setDropDownVerticalOffset(60);
        spinner.setSelection(0);
        spinner.setAdapter(adapter);
        initFirstMemberRecyclerViewData(m_id);
    }

    //首次加载时加载第一位会员的会员卡
    private void initFirstMemberRecyclerViewData(String m_id) {
        String url = "/QueryMemberCardList?m_id=" + m_id;
        HttpCallback callback = new HttpCallback(this,this,REQUEST_QueryMemberCardList);
        NetUtil.reqSendGet(this,url,callback);
    }

    //刷新会员卡列表
    private void refreshListView(AdapterView<?> parent,int position,long id) {
        String m_id = String.valueOf(maplist_member_origin.get(position).get("id"));
        String url = "/QueryMemberCardList?m_id=" + m_id;
        HttpCallback callback = new HttpCallback(this,this,REQUEST_QueryMemberCardList_REFRESH);
        NetUtil.reqSendGet(this,url,callback);
    }

    private void initSingleMemberData(String id,String name) {
        initToolbar();
        list_member_name.add(name);
        initSpinner(id);
    }

}
