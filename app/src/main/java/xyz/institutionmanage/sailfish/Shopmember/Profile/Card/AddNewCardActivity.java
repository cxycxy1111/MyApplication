package xyz.institutionmanage.sailfish.Shopmember.Profile.Card;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import xyz.institutionmanage.sailfish.R;
import xyz.institutionmanage.sailfish.Util.BaseActivity;
import xyz.institutionmanage.sailfish.Util.Enum.EnumRespStatType;
import xyz.institutionmanage.sailfish.Util.Enum.EnumRespType;
import xyz.institutionmanage.sailfish.Util.JsonHandler;
import xyz.institutionmanage.sailfish.Util.MethodTool;
import xyz.institutionmanage.sailfish.Util.NetUtil;
import xyz.institutionmanage.sailfish.Util.Ref;

public class AddNewCardActivity
        extends BaseActivity
        implements View.OnClickListener, EditText.OnFocusChangeListener{

    ArrayList<String> type = new ArrayList<>();
    private Spinner spinner_type;
    private RelativeLayout rl_balance,rl_times;
    private long s_id,sm_id;
    private int selected_type;
    private int year,month,date;
    private int current_year,current_month,current_date;
    private DatePickerDialog dpd_starttime,dpd_invalidtime;
    private EditText et_name,et_price,et_balance,et_times,et_starttime,et_invalidtime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_add);
        initData();
        initToolbar();
        initView();
        initSpinner();
        initCurrentDate();
        initDatePicker();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_general);
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
        et_invalidtime = (EditText)findViewById(R.id.et_invalidtime_a_add_new_card);
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
                        selected_type = 1;
                        break;
                    case 1:
                        rl_balance.setVisibility(View.GONE);
                        rl_times.setVisibility(View.VISIBLE);
                        selected_type = 2;
                        break;
                    case 2:
                        rl_balance.setVisibility(View.GONE);
                        rl_times.setVisibility(View.GONE);
                        selected_type = 3;
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

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener listener_starttime = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                et_starttime.setText(new StringBuffer().append(year).append("-").append(month + 1).append("-").append(dayOfMonth));
            }
        };
        DatePickerDialog.OnDateSetListener listener_invalidtime = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                et_invalidtime.setText(new StringBuffer().append(year).append("-").append(month + 1).append("-").append(dayOfMonth));
            }
        };
        dpd_starttime = new DatePickerDialog(AddNewCardActivity.this,listener_starttime,current_year,current_month,current_date);
        dpd_invalidtime = new DatePickerDialog(AddNewCardActivity.this,listener_invalidtime,current_year,current_month,current_date);
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
                dpd_starttime.show();
                break;
            case R.id.et_invalidtime_a_add_new_card:
                dpd_invalidtime.show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.et_starttime_a_add_new_card:
                if (hasFocus) {
                    dpd_starttime.show();
                }else {

                }
                break;
            case R.id.et_invalidtime_a_add_new_card:
                if (hasFocus) {
                    dpd_invalidtime.show();
                }else {

                }
                break;
            default:
                break;
        }
    }

    private void addNewCard() {
        final String name = et_name.getText().toString();
        String price = et_price.getText().toString();
        String balance = null;
        switch (selected_type) {
            case 1:
                balance = et_balance.getText().toString();
                break;
            case 2:
                balance = et_times.getText().toString();
                break;
            case 3:
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
            Toast.makeText(AddNewCardActivity.this, Ref.OP_EMPTY_ESSENTIAL_INFO,Toast.LENGTH_LONG).show();
        }else {
            String url = "/AddNewCard?name=" + name +
                    "&type=" + selected_type +
                    "&price=" + price +
                    "&balance=" + balance +
                    "&start_time=" + starttime +
                    "&expired_time=" + invalidtime;
            Callback callback = new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    MethodTool.showToast(AddNewCardActivity.this,Ref.CANT_CONNECT_INTERNET);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String resp = response.body().string();
                    EnumRespType respType = EnumRespType.dealWithResponse(resp);
                    switch (respType) {
                        case RESP_STAT:
                            EnumRespStatType respStatType = EnumRespStatType.dealWithRespStat(resp);
                            switch (respStatType) {
                                case EXE_SUC:
                                    MethodTool.showToast(AddNewCardActivity.this,Ref.OP_ADD_SUCCESS);
                                    finish();
                                    break;
                                case DUPLICATE:
                                    MethodTool.showToast(AddNewCardActivity.this,"卡名重复");
                                    break;
                                case EXE_FAIL:
                                    MethodTool.showToast(AddNewCardActivity.this,Ref.OP_ADD_FAIL);
                                    break;
                                case SESSION_EXPIRED:
                                    MethodTool.showExitAppAlert(AddNewCardActivity.this);
                                case AUTHORIZE_FAIL:
                                    MethodTool.showAuthorizeFailToast(AddNewCardActivity.this);
                                    break;
                                default:break;
                            }
                            break;
                        case RESP_ID:
                            Map<String,String> map = JsonHandler.strToMap(resp);
                            Intent intent = new Intent(AddNewCardActivity.this,CardTypeListActivity.class);
                            intent.putExtra("type",selected_type);
                            intent.putExtra("id",map.get("id"));
                            intent.putExtra("name",name);
                            setResult(Ref.RESULTCODE_ADD,intent);
                            finish();
                            break;
                        case RESP_ERROR:
                            MethodTool.showToast(AddNewCardActivity.this,Ref.UNKNOWN_ERROR);
                            break;
                        default:break;
                    }
                }
            };
            NetUtil.sendHttpRequest(AddNewCardActivity.this,url,callback);
        }
    }
}
