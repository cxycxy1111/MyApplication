package xyz.institutionmanager.sailfish.Shopmember.MemberCard.Batch;

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
import com.alfred.alfredtools.*;
import xyz.institutionmanager.sailfish.Adapter.RVSelectedCardItemAdapter;
import xyz.institutionmanager.sailfish.R;
import xyz.institutionmanager.sailfish.Util.Ref;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddNewMemberCardBatchSelectCardActivity
        extends BaseActivity
        implements View.OnClickListener,HttpResultListener {

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

    @Override
    public void onRespStatus(String body, int source) {
        super.onRespStatus(body, source);
        switch (NetRespStatType.dealWithRespStat(body)) {
            case STATUS_AUTHORIZE_FAIL:
                ViewHandler.exitAcitivityDueToAuthorizeFail(AddNewMemberCardBatchSelectCardActivity.this);
                break;
            case NSR:
                ViewHandler.toastShow(AddNewMemberCardBatchSelectCardActivity.this,"暂无记录");
                break;
            default:break;
        }
    }

    @Override
    public void onRespMapList(String body, int source) throws IOException {
        super.onRespMapList(body, source);
        mapList_original = JsonUtil.strToListMap(body,keys_original);
        for (int i = 0;i < mapList_original.size();i++) {
            Map<String,String> map = new HashMap<>();
            map = mapList_original.get(i);
            map.put("isChecked","0");
            mapList_original.remove(i);
            mapList_original.add(i,map);
        }
        updateComponent();
    }

    @Override
    public void onRespSessionExpired(int source) {
        super.onRespSessionExpired(source);
    }

    @Override
    public void onRespError(int source) {
        super.onRespError(source);
        ViewHandler.toastShow(AddNewMemberCardBatchSelectCardActivity.this, Ref.UNKNOWN_ERROR);
    }

    @Override
    public void onReqFailure(Object object, int source) {
        super.onReqFailure(object, source);
        ViewHandler.dealWithReqFail(AddNewMemberCardBatchSelectCardActivity.this);
    }

    private void initDataFromPreviousActivity() {
        m_id = getIntent().getStringExtra("m_id");
    }

    private void initComponent() {
        Toolbar tb = (Toolbar)findViewById(R.id.toolbar_general);
        Button btn_next = (Button)findViewById(R.id.btn_next_a_add_new_member_card_batch_select_card);
        ViewHandler.progressBarShow(AddNewMemberCardBatchSelectCardActivity.this);
        tb.setTitle("选择会员卡类型");
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btn_next.setOnClickListener(this);
    }

    private void initDataFromWeb() {
        String url =  "/CardSelectListQuery";
        NetUtil.reqSendGet(this,url,new HttpCallback(this,this,0));
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
