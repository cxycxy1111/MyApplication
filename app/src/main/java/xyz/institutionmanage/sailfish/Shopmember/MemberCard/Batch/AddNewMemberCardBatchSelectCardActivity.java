package xyz.institutionmanage.sailfish.Shopmember.MemberCard.Batch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import xyz.institutionmanage.sailfish.Adapter.RVSelectedCardItemAdapter;
import xyz.institutionmanage.sailfish.R;
import xyz.institutionmanage.sailfish.Util.BaseActivity;
import xyz.institutionmanage.sailfish.Util.Enum.EnumRespStatType;
import xyz.institutionmanage.sailfish.Util.Enum.EnumRespType;
import xyz.institutionmanage.sailfish.Util.JsonHandler;
import xyz.institutionmanage.sailfish.Util.MethodTool;
import xyz.institutionmanage.sailfish.Util.NetUtil;
import xyz.institutionmanage.sailfish.Util.Ref;

public class AddNewMemberCardBatchSelectCardActivity extends BaseActivity implements View.OnClickListener{

    private String m_id;
    private String[] keys_original = new String[]{"id","name","balance","price","start_time","expired_time"};
    private List<Map<String,String>> mapList_original = new ArrayList<>();
    private RVSelectedCardItemAdapter adapter;
    private int int_selected = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_member_card_batch_select_card);
        initDataFromPreviousActivity();
        initComponent();
        initDataFromWeb();
    }

    private void initDataFromPreviousActivity() {
        m_id = getIntent().getStringExtra("m_id");
    }

    private void initComponent() {
        Toolbar tb = (Toolbar)findViewById(R.id.toolbar_general);
        Button btn_next = (Button)findViewById(R.id.btn_next_a_add_new_member_card_batch_select_card);
        MethodTool.showProgressBar(AddNewMemberCardBatchSelectCardActivity.this);
        tb.setTitle("选择会员卡类型");
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btn_next.setOnClickListener(this);
    }

    private void initDataFromWeb() {
        String url =  "/CardSelectListQuery";
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.dealWithWebRequestFailure(AddNewMemberCardBatchSelectCardActivity.this);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                MethodTool.hideProgressBar(AddNewMemberCardBatchSelectCardActivity.this);
                String resp = response.body().string();
                switch (EnumRespType.dealWithResponse(resp)){
                    case RESP_MAPLIST:
                        mapList_original = JsonHandler.strToListMap(resp,keys_original);
                        for (int i = 0;i < mapList_original.size();i++) {
                            Map<String,String> map = new HashMap<>();
                            map = mapList_original.get(i);
                            map.put("isChecked","0");
                            mapList_original.remove(i);
                            mapList_original.add(i,map);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateComponent();
                            }
                        });
                        break;
                    case RESP_STAT:
                        switch (EnumRespStatType.dealWithRespStat(resp)) {
                            case AUTHORIZE_FAIL:
                                MethodTool.showAuthorizeFailToast(AddNewMemberCardBatchSelectCardActivity.this);
                                break;
                            case NSR:
                                MethodTool.showToast(AddNewMemberCardBatchSelectCardActivity.this,"暂无记录");
                                break;
                            case SESSION_EXPIRED:
                                MethodTool.exitAcitivityDueToAuthorizeFail(AddNewMemberCardBatchSelectCardActivity.this);
                                break;
                            default:break;
                        }
                        break;
                    case RESP_ERROR:
                        MethodTool.showToast(AddNewMemberCardBatchSelectCardActivity.this, Ref.UNKNOWN_ERROR);
                        break;
                    default:break;
                }
            }
        };
        NetUtil.sendHttpRequest(this,url,callback);
    }

    private void updateComponent() {
        RecyclerView rv = (RecyclerView)findViewById(R.id.rv_a_add_new_member_card_batch_select_card);
        adapter = new RVSelectedCardItemAdapter(this,mapList_original);
        RVSelectedCardItemAdapter.OnItemClickListener onItemClickListener = new RVSelectedCardItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int_selected = position;
                int pos = (int)view.getTag();
                if (!mapList_original.get(pos).get("isChecked").equals("1")) {
                    for (int i = 0;i < mapList_original.size();i++) {
                        Map<String,String> hashMap = new HashMap<>();
                        hashMap = mapList_original.get(i);
                        hashMap.put("isChecked","0");
                    }
                    Map<String,String> map1 = new HashMap<>();
                    map1 = mapList_original.get(pos);
                    map1.put("isChecked","1");
                    mapList_original.add(pos,map1); }
                adapter.notifyDataSetChanged();
            }
        };
        adapter.setOnItemClickListener(onItemClickListener);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        rv.setAdapter(adapter);
        rv.setLayoutManager(linearLayoutManager);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next_a_add_new_member_card_batch_select_card:
                int int_selected_pos = adapter.getInt_selected_pos();
                if (int_selected_pos == -1) {
                    Toast.makeText(this,"你暂未选择会员卡",Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(this,AddNewMemberCardBatchInfoActivity.class);
                    Map<String,String> map = new HashMap<>();
                    map = mapList_original.get(int_selected_pos);
                    intent.putExtra("c_name",map.get("name"));
                    intent.putExtra("balance",String.valueOf(map.get("balance")));
                    intent.putExtra("price",String.valueOf(map.get("price")));
                    intent.putExtra("start_time",map.get("start_time"));
                    intent.putExtra("expired_time",map.get("expired_time"));
                    intent.putExtra("c_id",String.valueOf(map.get("id")));
                    intent.putExtra("type",String.valueOf(map.get("type")));
                    intent.putExtra("m_id",m_id);
                    startActivityForResult(intent,Ref.REQCODE_ADD);
                }

                break;
            default:break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(Ref.RESULTCODE_CANCEL);
                finish();
                break;
            default:break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Ref.REQCODE_ADD:
                switch (resultCode) {
                    case Ref.RESULTCODE_DONE:
                        setResult(Ref.RESULTCODE_DONE);
                        this.finish();
                        break;
                    case Ref.RESULTCODE_CANCEL:
                        break;
                    default:break;
                }
                break;
            default:break;
        }
    }
}
