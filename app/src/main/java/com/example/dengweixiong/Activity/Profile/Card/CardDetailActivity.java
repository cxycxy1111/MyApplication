package com.example.dengweixiong.Activity.Profile.Card;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

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

public class CardDetailActivity extends BaseActivity {

    private long c_id,s_id,sm_id;
    private int type;
    private String c_name;
    private EditText et_name,et_price,et_balance,et_times,et_invalidtime,et_starttime;
    private RelativeLayout rl_balance,rl_times;
    Map<String,String> map = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_detail);
        initData();
        initToolbar();
        initEditText();
        initEditTextData();
    }

    private void initData() {
        Intent intent = getIntent();
        c_id = intent.getLongExtra("c_id",0);
        c_name = intent.getStringExtra("c_name");
        type = intent.getIntExtra("type",0);
        SharedPreferences preferences = getSharedPreferences("sasm",MODE_PRIVATE);
        s_id = preferences.getLong("s_id",0);
        sm_id = preferences.getLong("sm_id",0);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.tb_a_card_detail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(c_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initEditText() {
        rl_balance = (RelativeLayout)findViewById(R.id.rl_balance_a_card_detail);
        rl_times = (RelativeLayout)findViewById(R.id.rl_times_a_card_detail);
        et_name = (EditText)findViewById(R.id.et_name_a_card_detail);
        et_price = (EditText)findViewById(R.id.et_price_a_card_detail);
        et_balance = (EditText)findViewById(R.id.et_balance_a_card_detail);
        et_times = (EditText)findViewById(R.id.et_times_a_card_detail);
        et_starttime = (EditText)findViewById(R.id.et_starttime_a_card_detail);
        et_invalidtime = (EditText)findViewById(R.id.et_invalidtime_activity_card_detail);
        switch (type) {
            case 1:
                rl_times.setVisibility(View.GONE);
                break;
            case 2:
                rl_balance.setVisibility(View.GONE);
                break;
            case 3:
                rl_balance.setVisibility(View.GONE);
                rl_times.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    private void initEditTextData() {
        String url = "/CardDetailQuery?c_id=" + c_id;
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(CardDetailActivity.this, Reference.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(final Call call, Response response) throws IOException {
                String [] key = new String[] {"name","type","price","balance","start_time","expired_time"};
                ArrayList<Map<String,String>> list = JsonHandler.strToListMap(response,key);
                map = list.get(0);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        et_name.setText(map.get("name"));
                        et_price.setText(String.valueOf(map.get("price")));
                        et_starttime.setText(String.valueOf(map.get("start_time")));
                        et_invalidtime.setText(String.valueOf(map.get("expired_time")));
                        switch (Integer.parseInt(String.valueOf(map.get("type")))) {
                            case 1:
                                et_balance.setText(String.valueOf(map.get("balance")));
                                break;
                            case 2:
                                et_times.setText(String.valueOf(map.get("balance")));
                                break;
                            default:
                                break;
                        }
                    }
                });

            }
        };
        NetUtil.sendHttpRequest(CardDetailActivity.this,url,callback);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            default:
                break;
        }
        return true;
    }
}
