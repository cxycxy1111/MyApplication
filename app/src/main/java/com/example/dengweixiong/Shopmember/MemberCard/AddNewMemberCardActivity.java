package com.example.dengweixiong.Shopmember.MemberCard;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.example.dengweixiong.Util.BaseActivity;
import com.example.dengweixiong.Util.Enum.EnumRespStatType;
import com.example.dengweixiong.Util.Enum.EnumRespType;
import com.example.dengweixiong.Util.JsonHandler;
import com.example.dengweixiong.Util.MethodTool;
import com.example.dengweixiong.Util.NetUtil;
import com.example.dengweixiong.Util.Ref;
import com.example.dengweixiong.myapplication.R;

import org.apache.commons.lang.math.NumberUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AddNewMemberCardActivity
        extends BaseActivity
        implements View.OnClickListener,EditText.OnFocusChangeListener{

    private int current_year,current_month,current_date;
    private int selected_card_type;
    private long s_id,sm_id;
    private String selected_m_id,selected_c_id;
    private String source;
    private static final String TOOLBAR_TITLE_NEW = "新增会员卡";
    private static final String TOOLBAR_TITLE_ORIGIN = "会员卡详情";

    private List<Map<String,String>> full_name_list = new ArrayList<>();
    private List<Map<String,String>> card_full_list = new ArrayList<>();
    private List<String> name_list = new ArrayList<>();
    private List<String> card_list = new ArrayList<>();

    private EditText et_price,et_num,et_balance,et_start_time,et_invalid_time;
    private Spinner spinner_member,spinner_card;
    private RelativeLayout rl_num,rl_balance;
    private DatePickerDialog dpd_start_time,dpd_invalid_time;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_card_add);
        source = getIntent().getStringExtra("source");
        initToolbar();
        initShopAndShopmember();
        initMember();
        initViews();
        initLinearLayout();
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
                saveMemberCard();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_starttime_a_AddNewMemberCard:
                dpd_start_time.show();
                break;
            case R.id.et_invalidtime_a_AddNewMemberCard:
                dpd_invalid_time.show();
                break;
            default:break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.et_starttime_a_AddNewMemberCard:
                if (hasFocus) {
                    dpd_start_time.show();
                }else {
                    dpd_start_time.hide();
                }
                break;
            case R.id.et_invalidtime_a_AddNewMemberCard:
                if (hasFocus) {
                    dpd_invalid_time.show();
                }else {
                    dpd_invalid_time.hide();
                }
                break;
            default:break;
        }
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(TOOLBAR_TITLE_NEW);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        initDatePickerDialog();
        et_price = (EditText)findViewById(R.id.et_price_a_AddNewMemberCard);
        et_balance = (EditText)findViewById(R.id.et_price_a_AddNewMemberCard);
        et_num = (EditText)findViewById(R.id.et_num_a_AddNewMemberCard);
        et_start_time = (EditText)findViewById(R.id.et_starttime_a_AddNewMemberCard);
        et_invalid_time = (EditText)findViewById(R.id.et_invalidtime_a_AddNewMemberCard);
        et_start_time.setInputType(InputType.TYPE_NULL);
        et_invalid_time.setInputType(InputType.TYPE_NULL);

        et_start_time.setOnFocusChangeListener(this);
        et_start_time.setOnClickListener(this);
        et_invalid_time.setOnFocusChangeListener(this);
        et_invalid_time.setOnClickListener(this);

    }

    private void initDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        current_year = calendar.get(Calendar.YEAR);
        current_month = calendar.get(Calendar.MONTH);
        current_date = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog.OnDateSetListener listener_start_time = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                et_start_time.setText(String.valueOf(year) + "-" +
                        String.valueOf(month+1) + "-" +
                        String.valueOf(dayOfMonth));
            }
        };
        dpd_start_time = new DatePickerDialog(AddNewMemberCardActivity.this,listener_start_time,current_year,current_month,current_date);

        DatePickerDialog.OnDateSetListener listener_invalid_time = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                et_invalid_time.setText(String.valueOf(year) + "-" +
                        String.valueOf(month+1) + "-" +
                        String.valueOf(dayOfMonth));
            }
        };
        dpd_invalid_time = new DatePickerDialog(AddNewMemberCardActivity.this,listener_invalid_time,current_year,current_month,current_date);
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
        String url = "/QueryMemberList";
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                EnumRespType respType = EnumRespType.dealWithResponse(resp);
                switch (respType) {
                    case RESP_MAPLIST:
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
                        break;
                    case RESP_STAT:
                        EnumRespStatType respStatType = EnumRespStatType.dealWithRespStat(resp);
                        switch (respStatType) {
                            case EMPTY_RESULT:
                                MethodTool.showToast(AddNewMemberCardActivity.this,"暂无会员");
                                AddNewMemberCardActivity.this.finish();
                                break;
                            case SESSION_EXPIRED:
                                MethodTool.showExitAppAlert(AddNewMemberCardActivity.this);
                                break;
                            default:break;
                        }
                        break;
                    case RESP_ERROR:
                        MethodTool.showToast(AddNewMemberCardActivity.this,Ref.UNKNOWN_ERROR);
                        break;
                    default:break;
                }

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
                selected_m_id = String.valueOf(full_name_list.get(position).get("id"));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selected_m_id = String.valueOf(full_name_list.get(0).get("id"));
            }
        });
    }

    private void initCard() {
        String url = "/QueryCardList?type=0";
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                EnumRespType respType = EnumRespType.dealWithResponse(resp);
                switch (respType) {
                    case RESP_MAPLIST:
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
                        break;
                    case RESP_STAT:
                        EnumRespStatType respStatType = EnumRespStatType.dealWithRespStat(resp);
                        switch (respStatType) {
                            case NSR:
                                MethodTool.showToast(AddNewMemberCardActivity.this,"暂无卡，请添加");
                                AddNewMemberCardActivity.this.finish();
                                break;
                            case SESSION_EXPIRED:
                                MethodTool.showExitAppAlert(AddNewMemberCardActivity.this);
                                break;
                            default:break;
                        }
                    case RESP_ERROR:
                        MethodTool.showToast(AddNewMemberCardActivity.this,Ref.UNKNOWN_ERROR);
                }

            }
        };
        NetUtil.sendHttpRequest(AddNewMemberCardActivity.this,url,callback);
    }

    private void initCardSpinner() {
        spinner_card = (Spinner)findViewById(R.id.spinner_member_carld_a_add_new_member);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,card_list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_card.setAdapter(adapter);
        spinner_card.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_c_id = String.valueOf(card_full_list.get(position).get("id"));
                if (card_full_list.size() == 0) {

                }else {
                    selected_card_type = Integer.parseInt(String.valueOf(card_full_list.get(position).get("type")));
                    switch (selected_card_type) {
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
                selected_c_id = String.valueOf(card_full_list.get(0).get("id"));
            }
        });
    }

    private void saveMemberCard() {
        selected_m_id = String.valueOf(full_name_list.get(spinner_member.getSelectedItemPosition()).get("id"));
        selected_c_id = String.valueOf(card_full_list.get(spinner_card.getSelectedItemPosition()).get("id"));
        String str_price = et_price.getText().toString();
        String str_start_time = et_start_time.getText().toString() + " 00:00:00";
        String str_invalid_time = et_invalid_time.getText().toString() + " 00:00:00";
        String balance = "";
        switch (selected_card_type) {
            case 1:
                balance = et_balance.getText().toString();
                break;
            case 2:
                balance = et_num.getText().toString();
                break;
            case 3:
                balance = "0";
        }

        if (NumberUtils.isNumber(str_price)) {
            if (NumberUtils.isNumber(balance)) {
                String url = "/AddNewMemberCard?mid=" + selected_m_id +
                        "&cid=" + selected_c_id +
                        "&balance=" + balance +
                        "&stime=" + str_start_time +
                        "&etime=" + str_invalid_time;
                Callback callback = new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        MethodTool.showToast(AddNewMemberCardActivity.this, Ref.CANT_CONNECT_INTERNET);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();
                        EnumRespType enumRespType = EnumRespType.dealWithResponse(resp);
                        switch (enumRespType) {
                            case RESP_STAT:
                                EnumRespStatType respStatType = EnumRespStatType.dealWithRespStat(resp);
                                switch (respStatType) {
                                    case EXE_SUC:
                                        MethodTool.showToast(AddNewMemberCardActivity.this,Ref.OP_ADD_SUCCESS);
                                        AddNewMemberCardActivity.this.finish();
                                        break;
                                    case DUPLICATE:
                                        MethodTool.showToast(AddNewMemberCardActivity.this,"您已重复添加该卡");
                                        break;
                                    case EXE_FAIL:
                                        MethodTool.showToast(AddNewMemberCardActivity.this,Ref.OP_ADD_FAIL);
                                        break;
                                    case NOT_MATCH:
                                        MethodTool.showToast(AddNewMemberCardActivity.this,Ref.UNKNOWN_ERROR);
                                        break;
                                    case SESSION_EXPIRED:
                                        MethodTool.showExitAppAlert(AddNewMemberCardActivity.this);
                                        break;
                                    default:break;
                                }
                                break;
                            case RESP_ERROR:
                                MethodTool.showToast(AddNewMemberCardActivity.this,Ref.UNKNOWN_ERROR);
                        }
                    }
                };
                NetUtil.sendHttpRequest(AddNewMemberCardActivity.this,url,callback);
            }
        }
    }

}
