package xyz.instmgr.sailfish.Shopmember.Profile.Card;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.alfred.alfredtools.*;
import xyz.instmgr.sailfish.Adapter.RVSectionAdapter;
import xyz.instmgr.sailfish.R;
import xyz.instmgr.sailfish.Util.Ref;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardTypeListActivity
        extends BaseActivity
        implements HttpResultListener,RVSectionAdapter.OnItemClickListener {

    private static final int REQUEST_QueryCardList_TYPE_1 = 1;
    private static final int REQUEST_QueryCardList_TYPE_2 = 2;
    private static final int REQUEST_QueryCardList_TYPE_3 = 3;
    private List<Map<String, String>> list = new ArrayList<>();
    private String [] keys = new String[] {"id","name","type"};
    private RVSectionAdapter adapter;
    private List<Map<String,String>> balanceList = new ArrayList<>();
    private List<Map<String,String>> timesList = new ArrayList<>();
    private List<Map<String,String>> timeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_type_list);
        initToolBar();
        initBalanceCard();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_card_type_list,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.add_a_add_new_card:
                Intent intent = new Intent(this,AddNewCardActivity.class);
                startActivityForResult(intent,2);
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int p,t;
        switch (requestCode) {
            case 1:
                if (resultCode == Ref.RESULTCODE_DELETE) {
                    p = data.getIntExtra("pos",-1);
                    list.remove(p);
                }else if (resultCode == Ref.RESULTCODE_UPDATE) {
                    p = data.getIntExtra("pos",-1);
                    t = data.getIntExtra("type",0);
                    long id = data.getLongExtra("id",0);
                    String n = data.getStringExtra("name");
                    Map<String,String> m = new HashMap<>();
                    m.put("id", String.valueOf(id));
                    m.put("name",n);
                    m.put("type",String.valueOf(t));
                    list.set(p,m);
                }else if (resultCode == Ref.RESULTCODE_NULL){

                }
                break;
            case 2:
                if (resultCode == Ref.RESULTCODE_ADD) {
                    t = data.getIntExtra("type",0);
                    String id = data.getStringExtra("id");
                    String name = data.getStringExtra("name");
                    Map<String,String> m = new HashMap<>();
                    m.put("id",id);
                    m.put("name",name);
                    m.put("type",String.valueOf(t));
                    if (t == 1) {
                        balanceList.add(m);
                        Tool.sortListMapAsc(balanceList,"name");
                    }else if (t == 2) {
                        timesList.add(m);
                        Tool.sortListMapAsc(timesList,"name");
                    }else if (t == 3) {
                        timeList.add(m);
                        Tool.sortListMapAsc(timeList,"name");
                    }
                    list.clear();
                    Map<String,String> b_map = new HashMap<>();
                    if(balanceList.size()>0) {
                        b_map.put("type","余额卡");
                        list.add(b_map);
                        for (int i = 0;i < balanceList.size();i++) {
                            list.add(balanceList.get(i));
                        }
                    }
                    if (timesList.size()>0) {
                        Map<String,String> ts_map = new HashMap<>();
                        ts_map.put("type","次卡");
                        list.add(ts_map);
                        for (int i = 0;i < timesList.size();i++) {
                            list.add(timesList.get(i));
                        }
                    }
                    if (timeList.size()>0) {
                        Map<String,String> t_map = new HashMap<>();
                        t_map.put("type","有效期卡");
                        list.add(t_map);
                        for (int i = 0;i < timeList.size();i++) {
                            list.add(timeList.get(i));
                        }
                    }
                }
                break;
            default:
                break;

        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRespStatus(String body, int source) {
        if (source == REQUEST_QueryCardList_TYPE_1) {
            switch (NetRespStatType.dealWithRespStat(body)) {
                case NSR:
                    ViewHandler.toastShow(CardTypeListActivity.this,"暂无余额卡");
                    break;
                case STATUS_AUTHORIZE_FAIL:
                    ViewHandler.exitAcitivityDueToAuthorizeFail(CardTypeListActivity.this);
                    break;
                default:break;
            }
            initTimesCard();
        }else if (source == REQUEST_QueryCardList_TYPE_2) {
            NetRespStatType respStatType = NetRespStatType.dealWithRespStat(body);
            switch (respStatType) {
                case NSR:
                    ViewHandler.toastShow(CardTypeListActivity.this,"暂无次卡");
                    break;
                case STATUS_AUTHORIZE_FAIL:
                    ViewHandler.exitAcitivityDueToAuthorizeFail(CardTypeListActivity.this);
                    break;
                default:break;
            }
            initTimeCard();
        }else if (source == REQUEST_QueryCardList_TYPE_3) {
            NetRespStatType respStatType = NetRespStatType.dealWithRespStat(body);
            switch (respStatType) {
                case NSR:
                    ViewHandler.toastShow(CardTypeListActivity.this,"暂无有效期卡");
                    break;
                case STATUS_AUTHORIZE_FAIL:
                    ViewHandler.exitAcitivityDueToAuthorizeFail(CardTypeListActivity.this);
                    break;
                default:break;
            }
            initRecyclerView();
        }else {

        }
    }

    @Override
    public void onRespMapList(String body, int source) throws IOException {
        if (source == REQUEST_QueryCardList_TYPE_1) {
            Map<String,String> map = new HashMap<>();
            ArrayList<Map<String,String>> arrayList = new ArrayList<>();
            map.put("type","余额卡");
            list.add(map);
            arrayList = JsonUtil.strToListMap(body,keys);
            for (int i = 0;i < arrayList.size();i++) {
                list.add(arrayList.get(i));
                balanceList.add(arrayList.get(i));
            }
            initTimesCard();
        }else if (source == REQUEST_QueryCardList_TYPE_2) {
            ArrayList<Map<String,String>> arrayList = new ArrayList<>();
            Map<String,String> map = new HashMap<>();
            map.put("type","次卡");
            list.add(map);
            arrayList = JsonUtil.strToListMap(body,keys);
            for (int i = 0;i < arrayList.size();i++) {
                list.add(arrayList.get(i));
                timesList.add(arrayList.get(i));
            }
            initTimeCard();
        }else if (source == REQUEST_QueryCardList_TYPE_3) {
            ArrayList<Map<String,String>> arrayList = new ArrayList<>();
            Map<String,String> map = new HashMap<>();
            map.put("type","有效期卡");
            arrayList = JsonUtil.strToListMap(body,keys);
            list.add(map);
            for (int i = 0;i < arrayList.size();i++) {
                list.add(arrayList.get(i));
                timeList.add(arrayList.get(i));
            }
            initRecyclerView();
        }else {

        }
    }

    @Override
    public void onRespError(int source) {
        ViewHandler.toastShow(CardTypeListActivity.this,Ref.UNKNOWN_ERROR);
    }

    @Override
    public void onReqFailure(Object object, int source) {
        ViewHandler.progressBarHide(CardTypeListActivity.this,getProgressBar(CardTypeListActivity.this));
        ViewHandler.toastShow(CardTypeListActivity.this,Ref.CANT_CONNECT_INTERNET);
        CardTypeListActivity.this.finish();
    }

    @Override
    public void onRespSessionExpired(int source) {
        ViewHandler.alertShowAndExitApp(CardTypeListActivity.this);
    }

    @Override
    public void onItemClick(View view, int position) {
        long id = Long.parseLong(String.valueOf(list.get(position).get("id")));
        int type = Integer.parseInt(String.valueOf(list.get(position).get("type")));
        String name = String.valueOf(list.get(position).get("name"));
        Intent intent = new Intent(CardTypeListActivity.this,CardDetailActivity.class);
        intent.putExtra("c_id",id);
        intent.putExtra("position",position);
        intent.putExtra("c_name",name);
        intent.putExtra("type",type);
        startActivityForResult(intent,1);
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("会员卡类型管理");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initBalanceCard() {
        ViewHandler.progressBarShow(this);
        String url_balance = "/QueryCardList?type=1";
        HttpCallback callback = new HttpCallback(this,this,REQUEST_QueryCardList_TYPE_1);
        NetUtil.reqSendGet(this,url_balance,callback);
    }

    private void initTimesCard() {
        ViewHandler.progressBarShow(CardTypeListActivity.this,getProgressBar(CardTypeListActivity.this));
        String url_times = "/QueryCardList?type=2";
        HttpCallback callback = new HttpCallback(this,this,REQUEST_QueryCardList_TYPE_2);
        NetUtil.reqSendGet(this,url_times,callback);
    }

    private void initTimeCard() {
        ViewHandler.progressBarShow(CardTypeListActivity.this,getProgressBar(CardTypeListActivity.this));
        String url_time = "/QueryCardList?type=3";
        HttpCallback callback = new HttpCallback(this,this,REQUEST_QueryCardList_TYPE_3);
        NetUtil.reqSendGet(this,url_time,callback);
    }

    private void initRecyclerView() {
        ViewHandler.progressBarHide(this);
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.rv_a_cardtype_list);
        LinearLayoutManager llm = new LinearLayoutManager(CardTypeListActivity.this,LinearLayoutManager.VERTICAL,false);
        adapter = new RVSectionAdapter(list,CardTypeListActivity.this);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(llm);
        ViewHandler.progressBarHide(this,getProgressBar(this));
    }
}
