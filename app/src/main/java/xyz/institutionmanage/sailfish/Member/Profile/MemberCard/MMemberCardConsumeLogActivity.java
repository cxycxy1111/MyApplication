package xyz.institutionmanage.sailfish.Member.Profile.MemberCard;

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
import xyz.institutionmanage.sailfish.Adapter.RVMemberCardConsumeLogAdapter;
import xyz.institutionmanage.sailfish.R;
import xyz.institutionmanage.sailfish.Util.BaseActivity;
import xyz.institutionmanage.sailfish.Util.Enum.EnumRespStatType;
import xyz.institutionmanage.sailfish.Util.Enum.EnumRespType;
import xyz.institutionmanage.sailfish.Util.JsonHandler;
import xyz.institutionmanage.sailfish.Util.MethodTool;
import xyz.institutionmanage.sailfish.Util.NetUtil;
import xyz.institutionmanage.sailfish.Util.Ref;

public class MMemberCardConsumeLogActivity extends BaseActivity {

    private String mc_id = "0";
    private String[] keys_consume_log = new String[] {"consume","consume_time","operation","type"};
    private RecyclerView recyclerView;
    private RVMemberCardConsumeLogAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private List<Map<String,String>> mapList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mmember_card_consume_log);
        initData();
        initViews();
        getProgressBar(MMemberCardConsumeLogActivity.this).setVisibility(View.VISIBLE);
        initRecyclerViewDataFromWeb();
    }

    private void initData() {
        mc_id = getIntent().getStringExtra("mc_id");
    }

    private void initViews() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        toolbar.setTitle("消费记录");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = (RecyclerView)findViewById(R.id.rv_a_m_member_card_consume_log);
        linearLayoutManager = new LinearLayoutManager(MMemberCardConsumeLogActivity.this,LinearLayoutManager.VERTICAL,false);
    }

    private void initRecyclerViewDataFromWeb() {
        String url = "/mMemberCardConsumeLogQuery?mc_id=" + mc_id;
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(MMemberCardConsumeLogActivity.this, Ref.UNKNOWN_ERROR);
                MethodTool.hideProgressBar(MMemberCardConsumeLogActivity.this,getProgressBar(MMemberCardConsumeLogActivity.this));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                MethodTool.hideProgressBar(MMemberCardConsumeLogActivity.this,getProgressBar(MMemberCardConsumeLogActivity.this));
                switch (EnumRespType.dealWithResponse(resp)) {
                    case RESP_MAPLIST:
                        mapList = JsonHandler.strToListMap(resp,keys_consume_log);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initRecyclerView();
                            }
                        });
                        break;
                    case RESP_STAT:
                        switch (EnumRespStatType.dealWithRespStat(resp)) {
                            case SESSION_EXPIRED:
                                MethodTool.showExitAppAlert(MMemberCardConsumeLogActivity.this);
                                break;
                            case EMPTY_RESULT:
                                MethodTool.showToast(MMemberCardConsumeLogActivity.this,"该卡暂未消费");
                                break;
                            default:break;
                        }
                        break;
                    case RESP_ERROR:
                        MethodTool.showToast(MMemberCardConsumeLogActivity.this,Ref.UNKNOWN_ERROR);
                        break;
                    default:break;

                }
            }
        };
        NetUtil.sendHttpRequest(MMemberCardConsumeLogActivity.this,url,callback);
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
}
