package com.example.dengweixiong.Shopmember.MemberCard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.example.dengweixiong.Util.BaseActivity;
import com.example.dengweixiong.Util.JsonHandler;
import com.example.dengweixiong.Util.NetUtil;
import com.example.dengweixiong.myapplication.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AddNewMemberCardActivity
        extends BaseActivity {

    private long s_id,sm_id;
    private long selected_m_id,selected_c_id;
    private List<Map<String,String>> full_name_list = new ArrayList<>();
    private List<Map<String,String>> card_full_list = new ArrayList<>();
    private List<String> name_list = new ArrayList<>();
    private List<String> card_list = new ArrayList<>();
    private String source;
    private Spinner spinner_member,spinner_member_card;
    private RelativeLayout rl_num,rl_balance;
    private static final String TOOLBAR_TITLE_NEW = "新增会员卡";
    private static final String TOOLBAR_TITLE_ORIGIN = "会员卡详情";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_card_add);
        source = getIntent().getStringExtra("source");
        initToolbar();
        initShopAndShopmember();
        initMember();
        initLinearLayout();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(TOOLBAR_TITLE_NEW);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initLinearLayout() {
        rl_balance = (RelativeLayout)findViewById(R.id.rl_balance_a_AddNewMemberCard);
        rl_num = (RelativeLayout)findViewById(R.id.rl_num_a_AddNewMemberCard);
    }

    private void initShopAndShopmember() {
        s_id = getSharedPreferences("sasm",MODE_PRIVATE).getLong("s_id",0);
        sm_id = getSharedPreferences("sasm",MODE_PRIVATE).getLong("sm_id",0);
    }

    private void initMember() {
        String url = "/QueryMemberList?shop_id=" + s_id;
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                String [] keys = new String[] {"id","name"};
                full_name_list = JsonHandler.strToListMap(resp,keys);
                for (int i = 0;i < full_name_list.size();i++) {
                    name_list.add(full_name_list.get(i).get("name"));
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initMemberSpinner();
                    }
                });
                initCard();
            }
        };
        NetUtil.sendHttpRequest(AddNewMemberCardActivity.this,url,callback);
    }

    private void initMemberSpinner() {
        spinner_member = (Spinner)findViewById(R.id.spinner_member_a_add_new_member);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,name_list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_member.setAdapter(adapter);
        spinner_member.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_m_id = Long.parseLong(String.valueOf(full_name_list.get(position).get("id")));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selected_m_id = Long.parseLong(String.valueOf(full_name_list.get(0).get("id")));
            }
        });
    }

    private void initCard() {
        String url = "/QueryCardList?type=0&shop_id=" + s_id;
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                String [] keys = new String[] {"id","type","name"};
                card_full_list = JsonHandler.strToListMap(resp,keys);
                for (int i = 0;i < card_full_list.size();i++) {
                    card_list.add(card_full_list.get(i).get("name"));
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initCardSpinner();
                    }
                });
            }
        };
        NetUtil.sendHttpRequest(AddNewMemberCardActivity.this,url,callback);
    }

    private void initCardSpinner() {
        spinner_member_card = (Spinner)findViewById(R.id.spinner_member_carld_a_add_new_member);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,card_list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_member_card.setAdapter(adapter);
        spinner_member_card.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_c_id = Long.parseLong(String.valueOf(card_full_list.get(position).get("id")));
                if (card_full_list.size() == 0) {

                }else {
                    int type = Integer.parseInt(String.valueOf(card_full_list.get(position).get("type")));
                    switch (type) {
                        case 1:
                            rl_num.setVisibility(View.GONE);
                            rl_balance.setVisibility(View.VISIBLE);
                            break;
                        case 2:
                            rl_num.setVisibility(View.VISIBLE);
                            rl_balance.setVisibility(View.GONE);
                            break;
                        case 3:
                            rl_num.setVisibility(View.GONE);
                            rl_balance.setVisibility(View.GONE);
                            break;
                        default:
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selected_c_id = Long.parseLong(String.valueOf(card_full_list.get(0).get("id")));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_new_member_card,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.save_add_new_member_card:
                this.finish();
                break;
            default:
                break;
        }
        return true;
    }

}