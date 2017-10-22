package com.example.dengweixiong.Activity.Profile.Card;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.Toast;

import com.example.dengweixiong.Activity.Member.AddNewMemberActivity;
import com.example.dengweixiong.Util.BaseActivity;
import com.example.dengweixiong.Util.JsonHandler;
import com.example.dengweixiong.Util.MethodTool;
import com.example.dengweixiong.Util.NetUtil;
import com.example.dengweixiong.Util.Reference;
import com.example.dengweixiong.myapplication.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AddNewCardActivity
        extends BaseActivity
        implements View.OnClickListener,DatePickerDialog.OnDateSetListener,
            EditText.OnFocusChangeListener{

    ArrayList<String> type = new ArrayList<>();
    private Spinner spinner_type;
    private RelativeLayout rl_balance,rl_times;
    private long s_id,sm_id;
    private int selected_type;
    private int year,month,date;
    private int current_year,current_month,current_date;
    private DatePickerDialog dpd_starttime,dpd_invalidtime;
    private int dpd_starttime_id,dpd_invalidtime_id;
    private EditText et_name,et_price,et_balance,et_times,et_starttime,et_invalidtime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_card);
        initData();
        initToolbar();
        initView();
        initSpinner();
        initCurrentDate();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_a_add_new_card);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("添加卡");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initData() {
        type.add("余额卡");type.add("次卡");type.add("有效期卡");
        SharedPreferences preferences = getSharedPreferences("sasm",MODE_PRIVATE);
        s_id = preferences.getLong("s_id",0);
        sm_id = preferences.getLong("sm_id",0);
    }

    private void initView() {
        rl_balance = (RelativeLayout)findViewById(R.id.rl_balance_a_add_new_card);
        rl_times = (RelativeLayout)findViewById(R.id.rl_times_a_add_new_card);
        et_name = (EditText)findViewById(R.id.et_name_a_add_new_card);
        et_price = (EditText)findViewById(R.id.et_price_a_add_new_card);
        et_balance = (EditText)findViewById(R.id.et_balance_a_add_new_card);
        et_times = (EditText)findViewById(R.id.et_times_a_add_new_card);
        et_starttime = (EditText)findViewById(R.id.et_starttime_a_add_new_card);
        et_invalidtime = (EditText)findViewById(R.id.et_invalidtime_activity_add_new_card);
        spinner_type = (Spinner)findViewById(R.id.sp_type_a_add_new_card);
        //处理日期输入框
        et_starttime.setOnClickListener(this);
        et_invalidtime.setOnClickListener(this);
        et_starttime.setInputType(InputType.TYPE_NULL);
        et_invalidtime.setInputType(InputType.TYPE_NULL);
        et_starttime.setOnFocusChangeListener(this);
        et_invalidtime.setOnFocusChangeListener(this);
    }

    private void initSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,type);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_type.setAdapter(adapter);
        spinner_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        rl_times.setVisibility(View.GONE);
                        rl_balance.setVisibility(View.VISIBLE);
                        selected_type = 0;
                        break;
                    case 1:
                        rl_balance.setVisibility(View.GONE);
                        rl_times.setVisibility(View.VISIBLE);
                        selected_type = 1;
                        break;
                    case 2:
                        rl_balance.setVisibility(View.GONE);
                        rl_times.setVisibility(View.GONE);
                        selected_type = 2;
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selected_type = 0;
            }
        });
    }

    private void initCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        current_year = calendar.get(Calendar.YEAR);
        current_month = calendar.get(Calendar.MONTH);
        current_date = calendar.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_new_card,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.save_add_new_card:
                addNewCard();
            default:
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_starttime_a_add_new_card:
                dpd_starttime = new DatePickerDialog(AddNewCardActivity.this,AddNewCardActivity.this,year,month,date);
                dpd_starttime_id = dpd_starttime.getDatePicker().getId();
                dpd_starttime.show();
                break;
            case R.id.et_invalidtime_activity_add_new_card:
                dpd_invalidtime = new DatePickerDialog(AddNewCardActivity.this,AddNewCardActivity.this,year,month,date);
                dpd_invalidtime_id = dpd_invalidtime.getDatePicker().getId();
                dpd_invalidtime.show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        if (view.getId() == dpd_starttime_id) {
            et_starttime.setText(new StringBuffer().append(year).append("-").append(month + 1).append("-").append(dayOfMonth));
        } else if (view.getId() == dpd_invalidtime_id) {
            et_invalidtime.setText(new StringBuffer().append(year).append("-").append(month + 1).append("-").append(dayOfMonth));
        } else {
            Toast.makeText(this, Reference.UNKNOWN_ERROR,Toast.LENGTH_SHORT);
        }

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.et_starttime_a_add_new_card:
                if (hasFocus) {
                    dpd_starttime = new DatePickerDialog(AddNewCardActivity.this, AddNewCardActivity.this, current_year, current_month, current_date);
                    dpd_starttime.show();
                }else {

                }
                break;
            case R.id.et_invalidtime_activity_add_new_card:
                if (hasFocus) {
                    dpd_invalidtime = new DatePickerDialog(AddNewCardActivity.this, AddNewCardActivity.this, current_year, current_month, current_date);
                    dpd_invalidtime.show();
                }else {

                }
                break;
            default:
                break;
        }
    }

    private void addNewCard() {
        String name = et_name.getText().toString();
        String price = et_price.getText().toString();
        String balance = null;
        switch (selected_type) {
            case 0:
                balance = et_balance.getText().toString();
                break;
            case 1:
                balance = et_times.getText().toString();
                break;
            case 2:
                balance = "0";
                break;
            default:
                balance = "0";
                break;
        }
        String starttime = et_starttime.getText().toString();
        String invalidtime = et_invalidtime.getText().toString();
        if (name.equals("") | name.equals(null) |
                price.equals("") | price.equals(null) |
                balance.equals("") | balance.equals(null) |
                starttime.equals("") | starttime.equals(null) |
                invalidtime.equals("") | invalidtime.equals(null)) {
            Toast.makeText(AddNewCardActivity.this,"必要信息不能为空",Toast.LENGTH_LONG).show();
        }else {
            String url = "/AddNewCard?shop_id=" + s_id +
                    "&shopmember_id=1" +
                    "&name=余额卡" +
                    "&type=" +
                    "&price=" + price +
                    "&balance=" + balance +
                    "&start_time=" + starttime +
                    "&expired_time=" + invalidtime;
            Callback callback = new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String resp = response.body().string();
                    HashMap<String,String> map = JsonHandler.strToMap(resp);
                    switch (map.get("stat")) {
                        case "exe_suc":
                            MethodTool.showToast(AddNewCardActivity.this,"新增成功");
                            AddNewCardActivity.this.finish();
                            break;
                        case "duplicate":
                            MethodTool.showToast(AddNewCardActivity.this,"卡名重复");
                            break;
                        case "exe_fail":
                            MethodTool.showToast(AddNewCardActivity.this,"新增失败");
                            break;
                        default:
                            break;
                    }
                }
            };
            NetUtil.sendHttpRequest(AddNewCardActivity.this,url,callback);
        }
    }
}
