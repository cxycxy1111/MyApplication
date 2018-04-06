package xyz.institutionmanage.sailfish.Shopmember.MemberCard.Batch;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.io.IOException;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import xyz.institutionmanage.sailfish.R;
import xyz.institutionmanage.sailfish.Util.BaseActivity;
import xyz.institutionmanage.sailfish.Util.Enum.EnumRespStatType;
import xyz.institutionmanage.sailfish.Util.Enum.EnumRespType;
import xyz.institutionmanage.sailfish.Util.MethodTool;
import xyz.institutionmanage.sailfish.Util.NetUtil;
import xyz.institutionmanage.sailfish.Util.Ref;

public class AddNewMemberCardMemberCardInfoActivity
        extends BaseActivity
        implements View.OnClickListener,EditText.OnFocusChangeListener{

    private int current_year,current_month,current_date;
    private String str_c_id,str_m_id,str_c_name,str_price,str_balance,str_type,str_start_time,str_expired_time;
    private Button btn_done;
    private RelativeLayout rl_num,rl_balance;
    private EditText et_price,et_balance,et_num,et_start_time,et_expired_time;
    private DatePickerDialog dpd_start_time,dpd_invalid_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_member_card_member_card_info);
        initDataFromPreviousActivity();
        initDatePickerDialog();
        initComponent();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_starttime_a_add_new_member_card_mc_info:
                dpd_start_time.show();
                break;
            case R.id.et_invalidtime_a_add_new_member_card_mc_info:
                dpd_invalid_time.show();
                break;
            case R.id.btn_done_a_add_new_member_card_mc_info:
                submit();
                break;
            default:break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.et_starttime_a_add_new_member_card_mc_info:
                if (hasFocus) {
                    dpd_start_time.show();
                }else {
                    dpd_start_time.hide();
                }
                break;
            case R.id.et_invalidtime_a_add_new_member_card_mc_info:
                if (hasFocus) {
                    dpd_invalid_time.show();
                }else {
                    dpd_invalid_time.hide();
                }
                break;
            default:break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(Ref.RESULTCODE_CANCEL);
                this.finish();
                break;
            default:break;
        }
        return true;
    }

    private void initDataFromPreviousActivity() {
        str_c_id = getIntent().getStringExtra("c_id");
        str_m_id = getIntent().getStringExtra("m_id");
        str_c_name = getIntent().getStringExtra("c_name");
        str_price = getIntent().getStringExtra("price");
        str_balance = getIntent().getStringExtra("balance");
        str_type = getIntent().getStringExtra("type");
        str_start_time = getIntent().getStringExtra("start_time");
        str_expired_time = getIntent().getStringExtra("expired_time");
        str_start_time = str_start_time.substring(0,str_start_time.length()-11);
        str_expired_time = str_expired_time.substring(0,str_expired_time.length()-11);
    }

    private void initComponent() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        toolbar.setTitle("请输入会员卡资料");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btn_done = (Button)findViewById(R.id.btn_done_a_add_new_member_card_mc_info);
        rl_num = (RelativeLayout)findViewById(R.id.rl_num_a_add_new_member_card_mc_info);
        rl_balance = (RelativeLayout)findViewById(R.id.rl_balance_a_add_new_member_card_mc_info);
        et_price = (EditText)findViewById(R.id.et_price_a_add_new_member_card_mc_info);
        et_balance = (EditText)findViewById(R.id.et_balance_a_add_new_member_card_mc_info);
        et_num = (EditText)findViewById(R.id.et_num_a_add_new_member_card_mc_info);
        et_start_time = (EditText)findViewById(R.id.et_starttime_a_add_new_member_card_mc_info);
        et_expired_time = (EditText)findViewById(R.id.et_invalidtime_a_add_new_member_card_mc_info);

        et_expired_time.setOnClickListener(this);
        et_start_time.setOnClickListener(this);
        et_start_time.setOnFocusChangeListener(this);
        et_expired_time.setOnFocusChangeListener(this);
        btn_done.setOnClickListener(this);
        et_price.setText(str_price);
        if (Integer.parseInt(str_type) == Ref.TYPE_CARD_BALANCE) {
            rl_num.setVisibility(View.GONE);
            et_balance.setText(str_balance);
        }else if (Integer.parseInt(str_type) == Ref.TYPE_CARD_NUM) {
            rl_balance.setVisibility(View.GONE);
            et_num.setText(str_balance);
        }else {
            rl_balance.setVisibility(View.GONE);
            rl_num.setVisibility(View.GONE);
        }
        et_start_time.setText(str_start_time);
        et_expired_time.setText(str_expired_time);

        et_start_time.setInputType(InputType.TYPE_NULL);
        et_expired_time.setInputType(InputType.TYPE_NULL);
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
        dpd_start_time = new DatePickerDialog(this,listener_start_time,current_year,current_month,current_date);

        DatePickerDialog.OnDateSetListener listener_invalid_time = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                et_expired_time.setText(String.valueOf(year) + "-" +
                        String.valueOf(month+1) + "-" +
                        String.valueOf(dayOfMonth));
            }
        };
        dpd_invalid_time = new DatePickerDialog(this,listener_invalid_time,current_year,current_month,current_date);
    }

    private void submit() {
        String url = "";
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.dealWithWebRequestFailure(AddNewMemberCardMemberCardInfoActivity.this);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                switch (EnumRespType.dealWithResponse(resp)) {
                    case RESP_STAT:
                        switch (EnumRespStatType.dealWithRespStat(resp)) {
                            case SESSION_EXPIRED:
                                MethodTool.showExitAppAlert(AddNewMemberCardMemberCardInfoActivity.this);
                                break;
                            case AUTHORIZE_FAIL:
                                MethodTool.showAuthorizeFailToast(AddNewMemberCardMemberCardInfoActivity.this);
                                AddNewMemberCardMemberCardInfoActivity.this.setResult(Ref.RESULTCODE_CANCEL);
                                break;
                            default:break;
                        }
                        break;
                    case RESP_ERROR:
                        MethodTool.showToast(AddNewMemberCardMemberCardInfoActivity.this,Ref.CANT_CONNECT_INTERNET);
                        break;
                    default:break;
                }
            }
        };
        NetUtil.sendHttpRequest(AddNewMemberCardMemberCardInfoActivity.this,url,callback);
    }
}
