package xyz.institutionmanager.sailfish.Shopmember.Profile.Card;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import com.alfred.alfredtools.*;
import xyz.institutionmanager.sailfish.R;
import xyz.institutionmanager.sailfish.Util.Ref;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CardDetailActivity
        extends BaseActivity
        implements HttpResultListener {

    private static final int REQUEST_CardDetailQuery = 1;
    private static final int REQUEST_RemoveCard = 2;
    private static final int REQUEST_ModifyCard = 3;
    private long c_id,s_id,sm_id;
    private String [] key = new String[] {"name","type","price","balance","start_time","expired_time"};
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat simpleDateFormat_2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private int type,position;
    private int int_selected_s_year,int_selected_s_month,int_selected_s_date;
    private int int_selected_i_year,int_selected_i_month,int_selected_i_date;
    private String c_name,new_name;
    private EditText et_name,et_price,et_balance,et_times,et_invalidtime,et_starttime;
    private RelativeLayout rl_balance,rl_times;
    private DatePickerDialog dpd_start_time,dpd_invalid_time;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_card_detail,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(Ref.RESULTCODE_NULL,null);
                finish();
                break;
            case R.id.remove_card_detail:
                deleteCardTypeExe();
                break;
            case R.id.save_card_detail:
                saveChange();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onRespStatus(String body, int source) {
        if (source == REQUEST_CardDetailQuery) {
            switch (NetRespStatType.dealWithRespStat(body)) {
                case NSR:
                    ViewHandler.toastShow(CardDetailActivity.this,Ref.OP_NSR);
                    CardDetailActivity.this.finish();
                    break;
                case STATUS_AUTHORIZE_FAIL:
                    ViewHandler.exitAcitivityDueToAuthorizeFail(CardDetailActivity.this);
                    break;
                default:break;
            }
        }else if (source == REQUEST_RemoveCard) {
            NetRespStatType respStatType = NetRespStatType.dealWithRespStat(body);
            switch (respStatType) {
                case SUCCESS:
                    ViewHandler.toastShow(CardDetailActivity.this,"卡类型已被删除");
                    Intent intent = new Intent(CardDetailActivity.this,CardTypeListActivity.class);
                    intent.putExtra("pos",position);
                    setResult(Ref.RESULTCODE_DELETE,intent);
                    finish();
                    break;
                case FAIL:
                    ViewHandler.toastShow(CardDetailActivity.this,"删除失败");
                    break;
                case STATUS_AUTHORIZE_FAIL:
                    ViewHandler.exitAcitivityDueToAuthorizeFail(CardDetailActivity.this);
                    break;
                default:break;
            }
        }else if (source == REQUEST_ModifyCard){
            NetRespStatType respStatType = NetRespStatType.dealWithRespStat(body);
            switch (respStatType) {
                case NSR:
                    ViewHandler.toastShow(CardDetailActivity.this,"无法修改");
                    break;
                case DUPLICATE:
                    ViewHandler.toastShow(CardDetailActivity.this,"卡名重复，请重试");
                    break;
                case SUCCESS:
                    ViewHandler.toastShow(CardDetailActivity.this,"修改成功");
                    Intent intent = new Intent(CardDetailActivity.this, CardTypeListActivity.class);
                    intent.putExtra("name",new_name)
                            .putExtra("pos",position)
                            .putExtra("type",type)
                            .putExtra("id",c_id);
                    setResult(Ref.RESULTCODE_UPDATE,intent);
                    finish();
                    break;
                case FAIL:
                    ViewHandler.toastShow(CardDetailActivity.this,"修改失败");
                    break;
                case STATUS_AUTHORIZE_FAIL:
                    ViewHandler.exitAcitivityDueToAuthorizeFail(CardDetailActivity.this);
                    break;
                default:break;
            }
        }
    }

    @Override
    public void onRespMapList(String body, int source) throws IOException {
        if (source == REQUEST_CardDetailQuery) {
            ArrayList<Map<String,String>> list = JsonUtil.strToListMap(body,key);
            map = list.get(0);
            et_name.setText(map.get("name"));
            et_price.setText(String.valueOf(map.get("price")));
            String st = String.valueOf(map.get("start_time")).split(" ")[0];
            String it = String.valueOf(map.get("expired_time")).split(" ")[0];
            et_starttime.setText(st);
            et_invalidtime.setText(it);
            Date date_s = null;
            Date date_i = null;
            try {
                date_s = simpleDateFormat.parse(st);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            try {
                date_i = simpleDateFormat.parse(it);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Calendar calendar_s = Calendar.getInstance();
            calendar_s.setTime(date_s);
            Calendar calendar_i = Calendar.getInstance();
            calendar_i.setTime(date_i);
            int_selected_s_year = calendar_s.get(Calendar.YEAR);
            int_selected_s_month = calendar_s.get(Calendar.MONTH);
            int_selected_s_date = calendar_s.get(Calendar.DAY_OF_MONTH);
            int_selected_i_year = calendar_i.get(Calendar.YEAR);
            int_selected_i_month = calendar_i.get(Calendar.MONTH);
            int_selected_i_date = calendar_i.get(Calendar.DAY_OF_MONTH);
            initStartTimeDatePickerDialog(st);
            initInvalidTimeDatePickerDialog(it);
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
        }else if (source == REQUEST_ModifyCard) {

        }else if (source == REQUEST_RemoveCard){

        }
    }

    @Override
    public void onRespError(int source) {

    }

    @Override
    public void onReqFailure(Object object, int source) {
        ViewHandler.toastShow(CardDetailActivity.this, Ref.CANT_CONNECT_INTERNET);
    }

    @Override
    public void onRespSessionExpired(int source) {
        ViewHandler.alertShowAndExitApp(CardDetailActivity.this);
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
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_general);
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
        HttpCallback callback = new HttpCallback(this,this,REQUEST_CardDetailQuery);
        NetUtil.reqSendGet(this,url,callback);
    }

    private void initStartTimeDatePickerDialog(String time) {

        Date date = null;
        try {
            date = simpleDateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        final int dateOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                int_selected_s_year = year;
                int_selected_s_month = month+1;
                int_selected_s_date = dayOfMonth;
                et_starttime.setText(int_selected_s_year + "-" + String.valueOf(int_selected_s_month+1) + "-" + int_selected_s_date);
            }
        };

        dpd_start_time = new DatePickerDialog(CardDetailActivity.this,listener,year,month,dateOfMonth);
    }

    private void initInvalidTimeDatePickerDialog(String time) {
        Date date = null;
        try {
            date = simpleDateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dateOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                int_selected_i_year = year;
                int_selected_i_month = month+1;
                int_selected_i_date = dayOfMonth;
                et_invalidtime.setText(int_selected_i_year + "-" + int_selected_i_month + "-" + int_selected_i_date);
            }
        };

        dpd_invalid_time = new DatePickerDialog(CardDetailActivity.this,listener,year,month,dateOfMonth);
    }

    private void deleteCardTypeExe() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("删除\"" + c_name + "\"");
        builder.setMessage("确定要删除\"" + c_name + "\"吗？");
        builder.setCancelable(false);
        builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String url = "/RemoveCard?card_id=" + c_id;
                HttpCallback callback = new HttpCallback(CardDetailActivity.this,CardDetailActivity.this,REQUEST_RemoveCard);
                NetUtil.reqSendGet(CardDetailActivity.this,url,callback);
            }
        });
        builder.setNegativeButton("不删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            delete_confirm.dismiss();
            }
        });
        delete_confirm = builder.show();
    }

    private void saveChange() {
        String price = et_price.getText().toString();
        new_name = et_name.getText().toString();
        String balance = null;
        switch (type) {
            case 1 :
                balance = et_balance.getText().toString();
                break;
            case 2:
                balance = et_times.getText().toString();
                break;
            case 3:
                balance = "0";
                break;
            default:
                break;
        }
        String start_time = et_starttime.getText().toString();
        String invalid_time = et_invalidtime.getText().toString();
        String url = "/ModifyCard?id=" + c_id +
                "&name=" + new_name +
                "&price=" + price +
                "&balance=" + balance +
                "&start_time=" + start_time +
                "&expired_time=" + invalid_time;
        HttpCallback callback = new HttpCallback(this,this,REQUEST_ModifyCard);
        NetUtil.reqSendGet(this,url,callback);
    }
}
