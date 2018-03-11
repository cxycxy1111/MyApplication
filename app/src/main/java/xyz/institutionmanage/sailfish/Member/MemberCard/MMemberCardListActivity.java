package xyz.institutionmanage.sailfish.Member.MemberCard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;
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

public class MMemberCardListActivity extends BaseActivity {

    private String [] strs_keys_membercard = {"id","name","balance","card_id","type"};
    private List<Map<String,String>> maplist_membercard_origin = new ArrayList<>();
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private RVMemberCardListtAdapter RVMemberCardListtAdapter;
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
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(MMemberCardListActivity.this, Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                EnumRespType respType = EnumRespType.dealWithResponse(resp);
                switch (respType) {
                    case RESP_MAPLIST:
                        maplist_membercard_origin = JsonHandler.strToListMap(resp,strs_keys_membercard);
                        initRecyclerView();
                        break;
                    case RESP_STAT:
                        switch (EnumRespStatType.dealWithRespStat(resp)) {
                            case SESSION_EXPIRED:
                                MethodTool.showExitAppAlert(MMemberCardListActivity.this);
                                break;
                            case EMPTY_RESULT:
                                MethodTool.showToast(MMemberCardListActivity.this,"你尚未办理任何会员卡");
                            default:break;
                        }
                        break;
                    case RESP_ERROR:
                        MethodTool.showToast(MMemberCardListActivity.this,Ref.UNKNOWN_ERROR);
                        break;
                    default:break;
                }
            }
        };
        NetUtil.sendHttpRequest(MMemberCardListActivity.this,url,callback);
    }

    private void initRecyclerView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                getSupportActionBar().setTitle("我的会员卡(" + maplist_membercard_origin.size() + ")");
                linearLayoutManager = new LinearLayoutManager(MMemberCardListActivity.this,LinearLayoutManager.VERTICAL,false);
                recyclerView.setLayoutManager(linearLayoutManager);
                RVMemberCardListtAdapter = new RVMemberCardListtAdapter(MMemberCardListActivity.this,maplist_membercard_origin);
                RVMemberCardListtAdapter.setOnItemClickListener(new RVMemberCardListtAdapter.OnItemClickListener() {
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
        });
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
}
