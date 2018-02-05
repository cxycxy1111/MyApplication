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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.dengweixiong.Util.BaseActivity;
import com.example.dengweixiong.Util.Enum.EnumRespStatType;
import com.example.dengweixiong.Util.Enum.EnumRespType;
import com.example.dengweixiong.Util.JsonHandler;
import com.example.dengweixiong.Util.MethodTool;
import com.example.dengweixiong.Util.NetUtil;
import com.example.dengweixiong.Util.Ref;
import com.example.dengweixiong.Util.SharePreferenceManager;
import com.example.dengweixiong.myapplication.R;

import org.apache.commons.lang.math.NumberUtils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DeductionActivity extends BaseActivity implements View.OnClickListener,EditText.OnFocusChangeListener{

    private boolean isLoaded = false;
    private int selected_main_type;
    private String str_s_id,str_sm_id;
    private String selected_main_sc_id;
    private String source;
    private String selected_m_id,selected_c_id,selected_mc_id,selected_m_name,selected_c_name,str_selected_type;

    private LinearLayout linearLayout_balance;
    private LinearLayout linearLayout_num;
    private LinearLayout linearLayout_time;
    private static final String TOOLBAR_TITLE = "扣费";

    private String[] keys_member = new String[] {"id","name"};
    private String[] keys_member_card = new String[] {"id","name","balance","type"};
    private List<String> list_member_name = new ArrayList<>();
    private List<String> list_member_card_name = new ArrayList<>();
    private List<Map<String,String>>  maplist_member = new ArrayList<>();
    private List<Map<String,String>>  maplist_member_card = new ArrayList<>();

    private ArrayAdapter spinner_card_adapter;
    private ArrayAdapter spinner_member_adapter;

    private Toolbar toolbar;
    private DatePickerDialog datePickerDialog;
    private Spinner spinner_member;
    private Spinner spinner_member_card;
    private EditText et_balance,et_num,et_invalid_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_card_deduction);
        initToolbar();
        initViews();
        source = initData();
        switch (source) {
            case "ShopmemberMainActivity":
                initMemberSpinnerData();
                break;
            case "member_card_detail":
                initSpinners();
                break;
            default:break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_deduction,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.submit_deduction:
                switch (source) {
                    case "ShopmemberMainActivity":
                        switch (selected_main_type) {
                            case 1:
                                String balance = et_balance.getText().toString();
                                if (NumberUtils.isNumber(balance)) {submitBalanceCharge(balance);}
                                else {Toast.makeText(DeductionActivity.this,Ref.OP_WRONG_NUMBER_FORMAT,Toast.LENGTH_SHORT).show();}
                                break;
                            case 2:
                                String num = et_num.getText().toString();
                                if (NumberUtils.isNumber(num)) {submitBalanceCharge(num);}
                                else {Toast.makeText(DeductionActivity.this,Ref.OP_WRONG_NUMBER_FORMAT,Toast.LENGTH_SHORT).show();}
                                break;
                            case 3:
                                submitBalanceCharge("0");
                                break;
                            default:break;
                        }

                        break;
                    case "member_card_detail":
                        switch (str_selected_type) {
                            case "1":
                                String balance = et_balance.getText().toString();
                                if (NumberUtils.isNumber(balance)) {submitBalanceCharge(balance);}
                                else {Toast.makeText(DeductionActivity.this,Ref.OP_WRONG_NUMBER_FORMAT,Toast.LENGTH_SHORT).show();}
                                break;
                            case "2":
                                String num = et_num.getText().toString();
                                if (NumberUtils.isNumber(num)) {submitBalanceCharge(num);}
                                else {Toast.makeText(DeductionActivity.this,Ref.OP_WRONG_NUMBER_FORMAT,Toast.LENGTH_SHORT).show();}
                                break;
                            case "3":
                                submitBalanceCharge("0");
                                break;
                            default:
                                break;
                        }
                        break;
                    default:break;
                }

                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_time_a_deduction:
                datePickerDialog.show();
                break;
            default:break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.et_time_a_deduction:
                if (hasFocus) {
                    datePickerDialog.show();
                }else {
                    datePickerDialog.hide();
                }
                break;
            default:break;
        }
    }

    private void initToolbar(){
        toolbar = (Toolbar)findViewById(R.id.tb_a_deduction);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(TOOLBAR_TITLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //开始区分从主界面还是从会员卡详情界面过来的
    private String initData() {
        str_s_id = SharePreferenceManager.getSharePreferenceValue(DeductionActivity.this,"sasm","s_id",2);
        str_sm_id = SharePreferenceManager.getSharePreferenceValue(DeductionActivity.this,"sasm","sm_id",2);
        Intent intent = getIntent();
        source = intent.getStringExtra("source");
        switch (source) {
            case "ShopmemberMainActivity":
                break;
            case "member_card_detail":
                selected_m_id = intent.getStringExtra("m_id");
                selected_c_id = intent.getStringExtra("c_id");
                selected_mc_id = intent.getStringExtra("mc_id");
                selected_c_name = intent.getStringExtra("c_name");
                selected_m_name = intent.getStringExtra("m_name");
                str_selected_type = intent.getStringExtra("c_type");
                break;
            default:break;
        }
        return source;
    }

    private void initSpinnerListener() {
        spinner_member.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String str_temp_m_id = String.valueOf(maplist_member.get(position).get("id"));
                refreshMemberCardSpinnerData(str_temp_m_id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_member_card.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_main_sc_id = String.valueOf(maplist_member_card.get(position).get("id"));
                selected_main_type = Integer.valueOf(String.valueOf(maplist_member_card.get(position).get("type")));
                setLinearLayoutVisibility(selected_main_type);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //初始化视图控件
    private void initViews() {
        et_balance = (EditText)findViewById(R.id.et_balance_a_deduction);
        et_num = (EditText)findViewById(R.id.et_num_a_deduction);
        et_invalid_time = (EditText)findViewById(R.id.et_time_a_deduction);
        et_invalid_time.setInputType(InputType.TYPE_NULL);
        et_invalid_time.setOnClickListener(this);
        et_invalid_time.setOnFocusChangeListener(this);

        spinner_member_card = (Spinner)findViewById(R.id.sp_card_a_deduction);
        spinner_member = (Spinner)findViewById(R.id.sp_member_a_deduction);

        linearLayout_balance = (LinearLayout)findViewById(R.id.ll_balance_a_deduction);
        linearLayout_num = (LinearLayout)findViewById(R.id.ll_num_a_deduction);
        linearLayout_time = (LinearLayout)findViewById(R.id.ll_time_a_deduction);
        initDatePickerDialog();
    }

    //初始化日期选择器
    private void initDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                et_invalid_time.setText(String.valueOf(year) + "-" + String.valueOf(month+1) + "-" + String.valueOf(dayOfMonth));
            }
        };
        datePickerDialog = new DatePickerDialog(DeductionActivity.this,
                listener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
    }

    //处理入口：如果是从MemberCardDetail页面过来的，处理逻辑如下：
    private void initSpinners() {
        list_member_name.add(selected_m_name);
        spinner_member_adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,android.R.id.text1,list_member_name);
        spinner_member_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_member.setAdapter(spinner_member_adapter);

        list_member_card_name.add(selected_c_name);
        spinner_card_adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,android.R.id.text1,list_member_card_name);
        spinner_card_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_member_card.setAdapter(spinner_card_adapter);

        if (str_selected_type.equals("1")) {
            linearLayout_num.setVisibility(View.GONE);
            linearLayout_balance.setVisibility(View.VISIBLE);
        }else if (str_selected_type.equals("2")) {
            linearLayout_num.setVisibility(View.VISIBLE);
            linearLayout_balance.setVisibility(View.GONE);
        }else if (str_selected_type.equals(3)) {
            linearLayout_balance.setVisibility(View.GONE);
            linearLayout_num.setVisibility(View.GONE);
        }else {

        }
        isLoaded = true;
    }

    //如果是从ShopmemberMain过来的，处理逻辑如下：
    private void initMemberSpinnerData() {
        String url = "/QueryMemberList";
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(DeductionActivity.this, Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                EnumRespType respType = EnumRespType.dealWithResponse(resp);
                switch (respType) {
                    case RESP_MAPLIST:
                        maplist_member = JsonHandler.strToListMap(resp,keys_member);
                        for (int i= 0;i < maplist_member.size();i++) {
                            list_member_name.add(maplist_member.get(i).get("name"));
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initMemberSpinner();
                            }
                        });
                        break;
                    case RESP_STAT:
                        EnumRespStatType enumRespStatType = EnumRespStatType.dealWithRespStat(resp);
                        switch (enumRespStatType) {
                            case EMPTY_RESULT:
                                MethodTool.showToast(DeductionActivity.this,"暂无会员，请新增");
                                DeductionActivity.this.finish();
                                break;
                            case SESSION_EXPIRED:
                                MethodTool.showExitAppAlert(DeductionActivity.this);
                                break;
                            case AUTHORIZE_FAIL:
                                MethodTool.exitAcitivityDueToAuthorizeFail(DeductionActivity.this);
                                break;
                            default:break;
                        }
                        break;
                    case RESP_ERROR:
                        MethodTool.showToast(DeductionActivity.this,Ref.UNKNOWN_ERROR);
                        break;
                    default:break;
                }
            }
        };
        NetUtil.sendHttpRequest(DeductionActivity.this,url,callback);
    }

    //初始化会员spinner
    private void initMemberSpinner() {
        spinner_member_adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,android.R.id.text1,list_member_name);
        spinner_member_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_member.setAdapter(spinner_member_adapter);
        spinner_member.setSelection(0);

        initMemberCardSpinnerData(String.valueOf(maplist_member.get(0).get("id")));
    }

    //获取会员卡数据
    private void initMemberCardSpinnerData(String str_m_id) {
        String url = "/QueryMemberCardList?m_id=" + str_m_id;
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(DeductionActivity.this,Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                EnumRespType respType = EnumRespType.dealWithResponse(resp);
                switch (respType) {
                    case RESP_MAPLIST:
                        maplist_member_card = JsonHandler.strToListMap(resp,keys_member_card);
                        for (int i = 0;i < maplist_member_card.size();i++) {
                            list_member_card_name.add(maplist_member_card.get(i).get("name"));
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initMemberCardSpinner();
                            }
                        });
                        break;
                    case RESP_STAT:
                        switch (EnumRespStatType.dealWithRespStat(resp)) {
                            case EMPTY_RESULT:
                                MethodTool.showToast(DeductionActivity.this,Ref.STAT_EMPTY_RESULT);
                                selected_main_type = 0;
                                setLinearLayoutVisibility(0);
                                break;
                            case SESSION_EXPIRED:
                                MethodTool.showExitAppAlert(DeductionActivity.this);
                                break;
                            case AUTHORIZE_FAIL:
                                MethodTool.exitAcitivityDueToAuthorizeFail(DeductionActivity.this);
                                break;
                            default:break;
                        }
                        break;
                    default:break;
                }
            }
        };
        NetUtil.sendHttpRequest(DeductionActivity.this,url,callback);
    }

    //刷新会员卡数据
    private void refreshMemberCardSpinnerData(String str_m_id) {
        String url = "/QueryMemberCardList?m_id=" + str_m_id;
        maplist_member_card.clear();
        list_member_card_name.clear();
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(DeductionActivity.this,Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                EnumRespType respType = EnumRespType.dealWithResponse(resp);
                switch (respType) {
                    case RESP_MAPLIST:
                        List<Map<String,String>> mapList_temp = new ArrayList<>();
                        mapList_temp =  JsonHandler.strToListMap(resp,keys_member_card);
                        maplist_member_card.addAll(mapList_temp);
                        for (int i = 0;i < maplist_member_card.size();i++) {
                            list_member_card_name.add(maplist_member_card.get(i).get("name"));
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                spinner_card_adapter.notifyDataSetChanged();
                                selected_main_type = Integer.valueOf(String.valueOf(maplist_member_card.get(0).get("type")));
                                selected_main_sc_id = String.valueOf(maplist_member_card.get(0).get("id"));
                                setLinearLayoutVisibility(selected_main_type);
                            }
                        });
                        break;
                    case RESP_STAT:
                        EnumRespStatType respStatType = EnumRespStatType.dealWithRespStat(resp);
                        switch (respStatType) {
                            case EMPTY_RESULT:
                                MethodTool.showToast(DeductionActivity.this,Ref.STAT_EMPTY_RESULT);
                                selected_main_type = 0;
                                setLinearLayoutVisibility(0);
                                break;
                            case SESSION_EXPIRED:
                                MethodTool.showExitAppAlert(DeductionActivity.this);
                                break;
                            case AUTHORIZE_FAIL:
                                MethodTool.exitAcitivityDueToAuthorizeFail(DeductionActivity.this);
                                break;
                            default:break;
                        }
                        break;
                    case RESP_ERROR:
                        MethodTool.showToast(DeductionActivity.this,Ref.UNKNOWN_ERROR);
                    default:break;
                }
            }
        };
        NetUtil.sendHttpRequest(DeductionActivity.this,url,callback);
    }

    //初始化会员卡选择控件
    private void initMemberCardSpinner() {
        spinner_card_adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,android.R.id.text1,list_member_card_name);
        spinner_card_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_member_card.setAdapter(spinner_card_adapter);
        spinner_member_card.setSelection(0);
        initSpinnerListener();
        isLoaded = true;
    }

    private void setLinearLayoutVisibility(int selected_main_type) {
        switch (selected_main_type) {
            case 0:
                linearLayout_balance.setVisibility(View.GONE);
                linearLayout_num.setVisibility(View.GONE);
                linearLayout_time.setVisibility(View.GONE);
                break;
            case 1:
                linearLayout_num.setVisibility(View.GONE);
                linearLayout_balance.setVisibility(View.VISIBLE);
                break;
            case 2:
                linearLayout_num.setVisibility(View.VISIBLE);
                linearLayout_balance.setVisibility(View.GONE);
                break;
            case 3:
                linearLayout_balance.setVisibility(View.GONE);
                linearLayout_num.setVisibility(View.GONE);
                break;
            default:break;
        }
    }

    private void submitBalanceCharge(String num) {
        String url = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str_origin_invalid_time = et_invalid_time.getText().toString();
        Date date = null;
        final String str_invalid_date;
        if (str_origin_invalid_time.length() !=0) {
            try {
                date = simpleDateFormat.parse(str_origin_invalid_time + " 00:00:00");
            } catch (ParseException e) {
                e.printStackTrace();
            }
            str_invalid_date = simpleDateFormat.format(date);
        }else {
            str_invalid_date = "";
        }

        if (source.equals("ShopmemberMainActivity")) {
            url = "/Deduct?mc_id=" + selected_main_sc_id + "&num=" + num + "&invalid_date=" + str_invalid_date;
        }else if (source.equals("member_card_detail")) {
            url = "/Deduct?mc_id=" + selected_mc_id + "&num=" + num + "&invalid_date=" + str_invalid_date;
        }

        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(DeductionActivity.this,Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                EnumRespType respType = EnumRespType.dealWithResponse(resp);
                switch (respType) {
                    case RESP_STAT:
                        switch (EnumRespStatType.dealWithRespStat(resp)) {
                            case EXE_SUC:
                                MethodTool.showToast(DeductionActivity.this,Ref.OP_SUCCESS);
                                Intent intent = new Intent(DeductionActivity.this,MemberCardDetailActivity.class);
                                switch (source) {
                                    case "member_card_detail":
                                        switch (str_selected_type) {
                                            case "1":
                                                intent.putExtra("type","1");
                                                intent.putExtra("balance",et_balance.getText().toString());
                                                intent.putExtra("invalid_time",str_invalid_date);
                                                break;
                                            case "2":
                                                intent.putExtra("type","2");
                                                intent.putExtra("balance",et_balance.getText().toString());
                                                intent.putExtra("invalid_time",str_invalid_date);
                                                break;
                                            case "3":
                                                intent.putExtra("type","2");
                                                intent.putExtra("balance","0");
                                                intent.putExtra("invalid_time",str_invalid_date);
                                            default:break;
                                        }
                                        setResult(1,intent);
                                         break;
                                     default:break;
                                }
                                DeductionActivity.this.finish();
                                break;
                            case EXE_FAIL:
                                MethodTool.showToast(DeductionActivity.this,Ref.OP_FAIL);
                                DeductionActivity.this.finish();
                                break;
                            case NSR:
                                MethodTool.showToast(DeductionActivity.this,Ref.STAT_NSR);
                                DeductionActivity.this.finish();
                                break;
                            case NOT_MATCH:
                                MethodTool.showToast(DeductionActivity.this,Ref.NOT_MATCH);
                                DeductionActivity.this.finish();
                                break;
                            case SESSION_EXPIRED:
                                MethodTool.showExitAppAlert(DeductionActivity.this);
                                break;
                            case AUTHORIZE_FAIL:
                                MethodTool.showAuthorizeFailToast(DeductionActivity.this);
                                break;
                            case BALANCE_NOT_ENOUGH:
                                MethodTool.showToast(DeductionActivity.this,"该会员卡余额不足，无法扣费");
                            default:break;
                        }
                        break;
                    case RESP_ERROR:
                        MethodTool.showToast(DeductionActivity.this,Ref.UNKNOWN_ERROR);
                        break;
                    default:break;
                }
            }
        };
        NetUtil.sendHttpRequest(DeductionActivity.this,url,callback);
    }

}
