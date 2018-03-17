package xyz.institutionmanage.sailfish.Member.Profile.MyProfile;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import xyz.institutionmanage.sailfish.Member.Main.MemberMainActivity;
import xyz.institutionmanage.sailfish.R;
import xyz.institutionmanage.sailfish.Util.ActivityManager;
import xyz.institutionmanage.sailfish.Util.BaseActivity;
import xyz.institutionmanage.sailfish.Util.Enum.EnumRespStatType;
import xyz.institutionmanage.sailfish.Util.Enum.EnumRespType;
import xyz.institutionmanage.sailfish.Util.JsonHandler;
import xyz.institutionmanage.sailfish.Util.MethodTool;
import xyz.institutionmanage.sailfish.Util.NetUtil;
import xyz.institutionmanage.sailfish.Util.Ref;

public class MMemberDetailActivity
        extends BaseActivity
        implements
            View.OnClickListener,
            DatePickerDialog.OnDateSetListener,
            EditText.OnFocusChangeListener{

    private int current_year,current_month,current_date;
    private Toolbar toolbar;
    private Button btn_reset_password;
    private TextView tv_sn;
    private EditText et_name,et_birthday,et_phone,et_im;

    private CharSequence sn,name,birthday,phone,im;
    private ArrayList<String> list = new ArrayList<>();
    private Map<String,String> map = new HashMap<>();
    private int year,month,day;
    private AlertDialog.Builder builder;
    private DatePickerDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_member_detail);
        initToolBar();
        initActions();
        initData();
        loadMemberDetail();
    }

    private void initToolBar () {
        toolbar = (Toolbar)findViewById(R.id.toolbar_activity_m_member_detail);
        toolbar.setTitle("我的资料");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_sn = (TextView)findViewById(R.id.tv_sn_activity_m_member_detail);
        et_name = (EditText)findViewById(R.id.edittext_name_activity_m_member_detail);
        et_birthday = (EditText)findViewById(R.id.edittext_birthday_activity_m_member_detail);
        et_phone = (EditText)findViewById(R.id.edittext_phone_activity_m_member_detail);
        et_im = (EditText)findViewById(R.id.edittext_im_activity_m_member_detail);

        et_birthday.setInputType(InputType.TYPE_NULL);
        et_birthday.setOnClickListener(this);
        et_birthday.setOnFocusChangeListener(this);
    }

    private void initActions() {
        btn_reset_password = (Button)findViewById(R.id.btn_view_reset_password_activity_m_member_detail);
        btn_reset_password.setOnClickListener(this);
    }

    private void loadMemberDetail() {
        String url = "/mMemberDetailQuery";
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(MMemberDetailActivity.this, Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                EnumRespType respType = EnumRespType.dealWithResponse(resp);
                switch (respType) {
                    case RESP_MAPLIST:
                        String [] keys = new String[] {"id","name","login_name","birthday","phone","im"};
                        ArrayList<Map<String,String>> list = JsonHandler.strToListMap(resp,keys);
                        ArrayList<String> values = new ArrayList<String>();
                        for (int i = 0;i < list.size();i++) {
                            map = list.get(0);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initEditText(map);
                            }
                        });
                        break;
                    case RESP_ERROR:
                        MethodTool.showToast(MMemberDetailActivity.this,Ref.UNKNOWN_ERROR);
                        finish();
                        break;
                    case RESP_STAT:
                        switch (EnumRespStatType.dealWithRespStat(resp)) {
                            case SESSION_EXPIRED:
                                MethodTool.showExitAppAlert(MMemberDetailActivity.this);
                                break;
                            case NSR:
                                MethodTool.showToast(MMemberDetailActivity.this,Ref.OP_NSR);
                                MMemberDetailActivity.this.finish();
                                break;
                            case AUTHORIZE_FAIL:
                                MethodTool.exitAcitivityDueToAuthorizeFail(MMemberDetailActivity.this);
                                break;
                            default:break;
                        }
                        break;
                    default:break;
                }

            }
        };
        NetUtil.sendHttpRequest(MMemberDetailActivity.this,url,callback);
    }

    private void initData() {
        dealWithIntent();
    }

    private void initDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                et_birthday.setText(new StringBuffer().append(year).append("-").append(month+1).append("-").append(dayOfMonth));
            }
        };
        dialog = new DatePickerDialog(MMemberDetailActivity.this,listener,current_year,current_month,current_date);
    }

    private void initEditText(Map<String,String> map) {

        Object o_id = map.get("id");
        Object o_name = map.get("name");
        sn = String.valueOf(o_id);
        name = String.valueOf(o_name);
        tv_sn.setText(sn);
        et_name.setText(name);
        String birthday = String.valueOf(map.get("birthday"));
        birthday = birthday.substring(0,birthday.length()-11);
        et_birthday.setText(birthday);
        et_phone.setText(String.valueOf(map.get("phone")));
        et_im.setText(String.valueOf(map.get("im")));

        //初始化时间选择器
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(birthday);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        current_year = calendar.get(Calendar.YEAR);
        current_month = calendar.get(Calendar.MONTH);
        current_date = calendar.get(Calendar.DAY_OF_MONTH);
        initDatePickerDialog();

    }

    private void dealWithIntent() {
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.edittext_birthday_activity_add_new_member:
                if (hasFocus == false) {
                    dialog.show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edittext_birthday_activity_m_member_detail:
                dialog.show();
                break;
            case R.id.btn_view_reset_password_activity_m_member_detail:
                Intent intent1;
                intent1 = new Intent(MMemberDetailActivity.this,MResetMemberPwdActivity.class);
                startActivity(intent1);
                break;
            default:
                break;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        this.year = year;
        this.month = month;
        this.day = dayOfMonth;
        et_birthday.setText(new StringBuffer()
                .append(year).append("-").append(month + 1).append("-").append(dayOfMonth));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_member_detail,menu);
        return true;
    }

    /**
     * 菜单点击事件
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                /**
                builder = new AlertDialog.Builder(MemberDetailActivity.this);
                builder.setTitle("提示");
                builder.setMessage("是否保存更改？");
                builder.setCancelable(false);
                builder.setNegativeButton("不保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setResult(Ref.RESULTCODE_NULL,null);
                        finish();
                    }
                });
                builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(MemberDetailActivity.this,"请点击\"保存\"按钮保存更改",Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
                 **/
                MMemberDetailActivity.this.finish();
                break;
            case R.id.save_member_detail:
                dealWithSaveAction();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                switch (resultCode) {
                    case 1:
                        break;
                    default:break;
                }
            default:break;
        }
    }

    private void dealWithSaveAction() {
        final String m_name = et_name.getText().toString();
        String m_birthday = et_birthday.getText().toString();
        String m_phone = et_phone.getText().toString();
        String m_im = et_im.getText().toString();
        String url = "/MModifyMember?name=" + m_name +
                "&birthday=" + m_birthday +
                "&phone=" + m_phone +
                "&im=" + m_im;
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(MMemberDetailActivity.this,Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                EnumRespType respType = EnumRespType.dealWithResponse(resp);
                switch (respType) {
                    case RESP_ERROR:
                        MethodTool.showToast(MMemberDetailActivity.this,Ref.UNKNOWN_ERROR);
                        break;
                    case RESP_STAT:
                        EnumRespStatType respStatType = EnumRespStatType.dealWithRespStat(resp);
                        switch (respStatType) {
                            case EXE_SUC:
                                MethodTool.showToast(MMemberDetailActivity.this,Ref.OP_MODIFY_SUCCESS);
                                Intent intent = new Intent(MMemberDetailActivity.this,MemberMainActivity.class);
                                setResult(Ref.RESULTCODE_UPDATE,intent);
                                finish();
                                break;
                            case NSR:
                                MethodTool.showToast(MMemberDetailActivity.this,"会员已被删除");
                                ActivityManager.removeAllActivity();
                                break;
                            case EXE_FAIL:
                                MethodTool.showToast(MMemberDetailActivity.this,Ref.OP_MODIFY_FAIL);
                                break;
                            case SESSION_EXPIRED:
                                MethodTool.showExitAppAlert(MMemberDetailActivity.this);
                                break;
                            case AUTHORIZE_FAIL:
                                MethodTool.showAuthorizeFailToast(MMemberDetailActivity.this);
                                break;
                            default:break;
                        }
                        break;
                    default:break;
                }
            }
        };
        NetUtil.sendHttpRequest(MMemberDetailActivity.this,url,callback);

    }
}
