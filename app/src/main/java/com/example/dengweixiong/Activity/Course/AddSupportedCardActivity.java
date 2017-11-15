package com.example.dengweixiong.Activity.Course;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.dengweixiong.Adapter.RVSupportedCardAdapter;
import com.example.dengweixiong.Util.BaseActivity;
import com.example.dengweixiong.Util.JsonHandler;
import com.example.dengweixiong.Util.MethodTool;
import com.example.dengweixiong.Util.NetUtil;
import com.example.dengweixiong.Util.Reference;
import com.example.dengweixiong.myapplication.R;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AddSupportedCardActivity extends BaseActivity {

    private long s_id,sm_id,ce_id;
    private static final int RESULT_CODE_FAIL = 1;
    private static final int RESULT_CODE_SUC = 2;
    private String course_name,last_time,max_book_num,selected_course;
    private RVSupportedCardAdapter adapter;
    private List<Map<String,String>> origin_cards;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_supported_card);
        initData();
        initViews();
    }

    private void initData() {
        SharedPreferences preferences = getSharedPreferences("sasm",MODE_PRIVATE);
        s_id = preferences.getLong("s_id",0L);
        sm_id = preferences.getLong("sm_id",0L);
        Intent intent = getIntent();
        ce_id = intent.getLongExtra("course_id",0);
    }

    private void initViews() {
        initToolbar();
        initCards();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("配置所支持的卡");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initCards() {
        String url = "/QueryCardList?shop_id=" + s_id + "&type=" + 0;
        okhttp3.Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(AddSupportedCardActivity.this, Reference.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                String [] keys = new String[] {"id","name","type"};
                origin_cards = JsonHandler.strToListMap(resp,keys);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initRecyclerView();
                    }
                });
            }
        };
        NetUtil.sendHttpRequest(AddSupportedCardActivity.this,url,callback);

    }

    private void initRecyclerView() {
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.rv_a_AddSupportedCard);
        adapter = new RVSupportedCardAdapter(origin_cards,AddSupportedCardActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(AddSupportedCardActivity.this,LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_supported_card,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.save_add_support_card:
                StringBuilder builder = new StringBuilder();
                Map<Integer,String> values = adapter.getValueMap();
                Map<Integer,Boolean> c = adapter.getCheckStatusMap();
                Iterator iterator_v = values.entrySet().iterator();
                Iterator iterator_c = c.entrySet().iterator();
                while (iterator_c.hasNext()) {
                    Map.Entry entry = (Map.Entry)iterator_c.next();
                    Integer position = (Integer)entry.getKey();
                    Boolean isChecked = (Boolean)entry.getValue();
                    if (isChecked == true) {
                        String value = values.get(position);
                        Map<String,String> map = origin_cards.get(position);
                        int t = Integer.parseInt(String.valueOf(map.get("type")));
                        if (t != 4) {
                            builder.append("1_")
                                    .append(ce_id).append("_")
                                    .append(String.valueOf(origin_cards.get(position).get("id"))).append("_")
                                    .append(value).append("-");

                        }else {
                            if (!value.equals("")) {
                                builder.append("1_")
                                        .append(ce_id).append("_")
                                        .append(String.valueOf(origin_cards.get(position).get("id"))).append("_")
                                        .append(value).append("-");
                            }
                        }
                    }
                }
                String req = builder.toString();
                req = req.substring(0,req.length()-1);
                submitSupportedCard(req);
            default:
                break;
        }
        return true;
    }

    private void submitSupportedCard(String arg) {
        String url = "/SupportedCardModify?m=" + arg;
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(AddSupportedCardActivity.this,Reference.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                if (resp.contains(Reference.STATUS)) {
                    Map<String,String> map = JsonHandler.strToMap(resp);
                    switch (map.get(Reference.STATUS)) {
                        case "no_such_record":
                            MethodTool.showToast(AddSupportedCardActivity.this,"课程或卡不存在");
                            setResult(RESULT_CODE_FAIL);
                            AddSupportedCardActivity.this.finish();
                            break;
                        case "institution_not_match" :
                            MethodTool.showToast(AddSupportedCardActivity.this,"机构不匹配");
                            setResult(RESULT_CODE_FAIL);
                            AddSupportedCardActivity.this.finish();
                            break;
                        case "duplicate":
                            MethodTool.showToast(AddSupportedCardActivity.this,"已存在要支持的卡");
                            setResult(RESULT_CODE_FAIL);
                            AddSupportedCardActivity.this.finish();
                            break;
                        case "exe_suc":
                            MethodTool.showToast(AddSupportedCardActivity.this,"操作成功");
                            setResult(RESULT_CODE_SUC);
                            AddSupportedCardActivity.this.finish();
                            break;
                        case "exe_fail":
                            MethodTool.showToast(AddSupportedCardActivity.this,"操作失败");
                            setResult(RESULT_CODE_FAIL);
                            AddSupportedCardActivity.this.finish();
                            break;
                    }
                }
            }
        };
        NetUtil.sendHttpRequest(AddSupportedCardActivity.this,url,callback);
    }
}
