package xyz.instmgr.sailfish.Shopmember.MemberCard.Batch;

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
import xyz.instmgr.sailfish.Adapter.RVPureCheckBoxAdapter;
import xyz.instmgr.sailfish.R;
import xyz.instmgr.sailfish.Util.Ref;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddNewMemberCardBatchActivity
        extends BaseActivity
        implements View.OnClickListener,HttpResultListener {

    private String[] keys = new String[]{"id","name"};
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private Button btn_next;
    private RVPureCheckBoxAdapter checkBoxAdapter;
    private List<Map<String,String>> mapList_origin = new ArrayList<>();
    private List<Map<String,String>> mapList_recyclerview = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_member_card_batch);
        ViewHandler.progressBarShow(this,getProgressBar(this));
        initComponent();
        initDataFromWeb();
    }

    @Override
    public void onRespStatus(String body, int source) {
        super.onRespStatus(body, source);
        switch (NetRespStatType.dealWithRespStat(body)) {
            case STATUS_AUTHORIZE_FAIL:
                ViewHandler.toastShow(AddNewMemberCardBatchActivity.this,Ref.OP_MANAGE_STATUS_AUTHORIZE_FAIL);
                AddNewMemberCardBatchActivity.this.finish();
                break;
            case EMPTY:
                ViewHandler.toastShow(AddNewMemberCardBatchActivity.this,"暂无会员");
                btn_next.setClickable(false);
                break;
            default:break;
        }
    }

    @Override
    public void onReqFailure(Object object, int source) {
        super.onReqFailure(object, source);
        ViewHandler.toastShow(AddNewMemberCardBatchActivity.this, Ref.CANT_CONNECT_INTERNET);
        btn_next.setClickable(false);
        ViewHandler.progressBarHide(AddNewMemberCardBatchActivity.this);
    }

    @Override
    public void onRespError(int source) {
        super.onRespError(source);
    }

    @Override
    public void onRespSessionExpired(int source) {
        super.onRespSessionExpired(source);
    }

    @Override
    public void onRespMapList(String body, int source) throws IOException {
        super.onRespMapList(body, source);
        mapList_origin = JsonUtil.strToListMap(body,keys);
        for (int i = 0;i < mapList_origin.size();i++) {
            Map<String,String> map_r = new HashMap<>();
            Map<String,String> map_o = new HashMap<>();
            map_o = mapList_origin.get(i);
            map_r.put("isChecked","0");
            map_r.put("teacherName",map_o.get("name"));
            map_r.put("id",map_o.get("id"));
            mapList_recyclerview.add(map_r);
        }
        updateComponent();
    }

    private void initComponent() {
        recyclerView = (RecyclerView)findViewById(R.id.rv_a_add_new_member_card_batch);
        toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        btn_next = (Button)findViewById(R.id.btn_next_a_add_new_member_card_batch);
        btn_next.setOnClickListener(this);
        toolbar.setTitle("选择会员");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }



    private void initDataFromWeb() {
        String url = "/QueryMemberList";
        NetUtil.reqSendGet(this,url,new HttpCallback(this,this,0));
    }

    private void updateComponent() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        checkBoxAdapter = new RVPureCheckBoxAdapter(mapList_recyclerview,this);
        recyclerView.setAdapter(checkBoxAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(Ref.RESULTCODE_CANCEL);
                this.finish();
                break;
            default:break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Ref.REQCODE_ADD:
                switch (resultCode) {
                    case Ref.RESULTCODE_DONE:
                        setResult(Ref.RESULTCODE_DONE);
                        finish();
                        break;
                    case Ref.RESULTCODE_CANCEL:
                        break;
                }
                break;
            default:break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next_a_add_new_member_card_batch:
                List<Map<String,String>> mapList = new ArrayList<>();
                mapList = checkBoxAdapter.getListmap_after();
                if (mapList.size()==0) {
                    Toast.makeText(this,"你暂未选择会员，请选择",Toast.LENGTH_SHORT);
                }else {
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0;i < mapList.size();i++) {
                        if (mapList.get(i).get("isChecked").equals("1")) {
                            builder.append(String.valueOf(mapList.get(i).get("id"))).append("_");
                        }
                    }
                    String s = builder.toString();
                    if (s.length()== 0) {
                        Toast.makeText(this,"您暂未选取会员",Toast.LENGTH_SHORT).show();
                    }else {
                        s = s.substring(0,s.length()-1);
                        Intent intent = new Intent(this,AddNewMemberCardBatchSelectCardActivity.class);
                        intent.putExtra("m_id",s);
                        startActivityForResult(intent,Ref.REQCODE_ADD);
                    }
                }
                break;
            default:break;
        }
    }
}
