package com.example.dengweixiong.Activity.Profile.Card;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.dengweixiong.Adapter.RecyclerViewSectionAdapter;
import com.example.dengweixiong.Util.BaseActivity;
import com.example.dengweixiong.Util.JsonHandler;
import com.example.dengweixiong.Util.MethodTool;
import com.example.dengweixiong.Util.NetUtil;
import com.example.dengweixiong.Util.Reference;
import com.example.dengweixiong.myapplication.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CardTypeListActivity extends BaseActivity {

    private long s_id;
    private List<Map<String, String>> list = new ArrayList<>();
    private String [] keys = new String[] {"id","name","type"};
    private RecyclerViewSectionAdapter adapter;
    private List<Map<String,String>> balanceList = new ArrayList<>();
    private List<Map<String,String>> timesList = new ArrayList<>();
    private List<Map<String,String>> timeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_type_list);
        initToolBar();
        initData();
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
                if (resultCode == Reference.RESULTCODE_DELETE) {
                    p = data.getIntExtra("pos",-1);
                    list.remove(p);
                }else if (resultCode == Reference.RESULTCODE_UPDATE) {
                    p = data.getIntExtra("pos",-1);
                    t = data.getIntExtra("type",0);
                    long id = data.getLongExtra("id",0);
                    String n = data.getStringExtra("name");
                    Map<String,String> m = new HashMap<>();
                    m.put("id", String.valueOf(id));
                    m.put("name",n);
                    m.put("type",String.valueOf(t));
                    list.set(p,m);
                }else if (resultCode == Reference.RESULTCODE_NULL){

                }
                break;
            case 2:
                if (resultCode == Reference.RESULTCODE_ADD) {
                    t = data.getIntExtra("type",0);
                    String id = data.getStringExtra("id");
                    String name = data.getStringExtra("name");
                    Map<String,String> m = new HashMap<>();
                    m.put("id",id);
                    m.put("name",name);
                    m.put("type",String.valueOf(t));
                    if (t == 1) {
                        balanceList.add(m);
                        MethodTool.sortListMap(balanceList);
                    }else if (t == 2) {
                        timesList.add(m);
                        MethodTool.sortListMap(timesList);
                    }else if (t == 3) {
                        timeList.add(m);
                        MethodTool.sortListMap(timeList);
                    }
                    list.clear();
                    Map<String,String> b_map = new HashMap<>();
                    b_map.put("type","余额卡");
                    list.add(b_map);
                    for (int i = 0;i < balanceList.size();i++) {
                        list.add(balanceList.get(i));
                    }
                    Map<String,String> ts_map = new HashMap<>();
                    ts_map.put("type","次卡");
                    list.add(ts_map);
                    for (int i = 0;i < timesList.size();i++) {
                        list.add(timesList.get(i));
                    }
                    Map<String,String> t_map = new HashMap<>();
                    t_map.put("type","有效期卡");
                    list.add(t_map);
                    for (int i = 0;i < timeList.size();i++) {
                        list.add(timeList.get(i));
                    }
                }
                break;
            default:
                break;

        }
        adapter.notifyDataSetChanged();
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.tb_a_cardtype_list);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("会员卡类型");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initData() {
        SharedPreferences preferences = getSharedPreferences("sasm",MODE_PRIVATE);
        s_id = preferences.getLong("s_id",0);
    }

    private void initBalanceCard() {
        String url_balance = "/QueryCardList?shop_id=" + s_id + "&type=1";
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                Map<String,String> map = new HashMap<>();
                ArrayList<Map<String,String>> arrayList = new ArrayList<>();
                if (!resp.contains(Reference.STATUS)) {
                    map.put("type","余额卡");
                    list.add(map);
                    arrayList = JsonHandler.strToListMap(resp,keys);
                    for (int i = 0;i < arrayList.size();i++) {
                        list.add(arrayList.get(i));
                        balanceList.add(arrayList.get(i));
                    }
                }
                initTimesCard();
            }
        };
        NetUtil.sendHttpRequest(CardTypeListActivity.this,url_balance,callback);
    }

    private void initTimesCard() {
        String url_times = "/QueryCardList?shop_id=" + s_id + "&type=2";
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                ArrayList<Map<String,String>> arrayList = new ArrayList<>();
                Map<String,String> map = new HashMap<>();
                if (!resp.contains(Reference.STATUS)) {
                    map.put("type","次卡");
                    list.add(map);
                    arrayList = JsonHandler.strToListMap(resp,keys);
                    for (int i = 0;i < arrayList.size();i++) {
                        list.add(arrayList.get(i));
                        timesList.add(arrayList.get(i));
                    }
                }
                initTimeCard();
            }
        };
        NetUtil.sendHttpRequest(CardTypeListActivity.this,url_times,callback);
    }

    private void initTimeCard() {
        String url_times = "/QueryCardList?shop_id=" + s_id + "&type=3";
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                ArrayList<Map<String,String>> arrayList = new ArrayList<>();
                if (!resp.contains(Reference.STATUS)) {
                    Map<String,String> map = new HashMap<>();
                    map.put("type","有效期卡");
                    arrayList = JsonHandler.strToListMap(resp,keys);
                    list.add(map);
                    for (int i = 0;i < arrayList.size();i++) {
                        list.add(arrayList.get(i));
                        timeList.add(arrayList.get(i));
                    }
                }
                initRecyclerView();
            }
        };
        NetUtil.sendHttpRequest(CardTypeListActivity.this,url_times,callback);
    }

    private void initRecyclerView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RecyclerView recyclerView = (RecyclerView)findViewById(R.id.rv_a_cardtype_list);
                LinearLayoutManager llm = new LinearLayoutManager(CardTypeListActivity.this,LinearLayoutManager.VERTICAL,false);
                adapter = new RecyclerViewSectionAdapter(list,CardTypeListActivity.this);
                adapter.setOnItemClickListener(new RecyclerViewSectionAdapter.OnItemClickListener() {
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
                });
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(llm);
            }
        });
    }
}
