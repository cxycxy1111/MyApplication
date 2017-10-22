package com.example.dengweixiong.Activity.Profile.Card;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.TimeZoneNames;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
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
    private int type,position;
    private String c_name;
    private EditText et_name,et_price,et_balance,et_times,et_invalidtime,et_starttime;
    private RelativeLayout rl_balance,rl_times;
    private Dialog delete_confirm;
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
        position = intent.getIntExtra("position",0);
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
                ArrayList<Map<String,String>> list = JsonHandler.strToListMap(response.body().toString(),key);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_card_detail,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.remove_card_detail:
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("删除\"" + c_name + "\"");
                builder.setMessage("确定要删除\"" + c_name + "\"吗？");
                builder.setCancelable(false);
                builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        String url = "/RemoveCard?card_id=" + c_id + "&shopmember_id=" +sm_id;
                        Callback callback = new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                MethodTool.showToast(CardDetailActivity.this,Reference.CANT_CONNECT_INTERNET);
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                Map<String,String> map = JsonHandler.strToMap(response.body().toString());
                                ArrayList<String> keys = MethodTool.getKeys(map);
                                ArrayList<String> values = MethodTool.getValues(map,keys);
                                switch (keys.get(0)) {
                                    case "stat" :
                                        switch (values.get(0)) {
                                            case "exe_suc" :
                                                MethodTool.showToast(CardDetailActivity.this,"卡类型已被删除");
                                                Intent intent = new Intent(CardDetailActivity.this,CardTypeListActivity.class);
                                                intent.putExtra("position",position);
                                                int requestCode = 0;
                                                startActivityForResult(intent,requestCode);
                                                break;
                                            case "exe_fail" :
                                                MethodTool.showToast(CardDetailActivity.this,"删除失败");
                                                break;
                                            default:
                                                break;
                                        }
                                    default:
                                        break;
                                }
                            }
                        };
                        NetUtil.sendHttpRequest(CardDetailActivity.this,url,callback);
                    }
                });
                builder.setNegativeButton("不删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        delete_confirm.dismiss();
                    }
                });
                delete_confirm = builder.show();
                break;
            case R.id.save_card_detail:
                break;
            default:
                break;
        }
        return true;
    }

    private void deleteCardTypeExe() {

    }
}
