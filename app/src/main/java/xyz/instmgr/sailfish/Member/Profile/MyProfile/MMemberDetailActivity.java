package xyz.instmgr.sailfish.Member.Profile.MyProfile;

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
import com.alfred.alfredtools.*;
import xyz.instmgr.sailfish.Member.Main.MemberMainActivity;
import xyz.instmgr.sailfish.R;
import xyz.instmgr.sailfish.Util.Ref;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MMemberDetailActivity
        extends BaseActivity
        implements
            View.OnClickListener,
            DatePickerDialog.OnDateSetListener,
            EditText.OnFocusChangeListener,HttpResultListener {

    private static final int REQUEST_MemberDetailQuery = 1;
    private static final int REQUEST_ModifyMember = 2;
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
        toolbar = (Toolbar)findViewById(R.id.toolbar_general);
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
        HttpCallback callback = new HttpCallback(this,this,0);
        NetUtil.reqSendGet(this,url,callback);
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
        HttpCallback callback = new HttpCallback(this,this,0);
        NetUtil.reqSendGet(this,url,callback);
    }

    @Override
    public void onRespStatus(String body, int source) {
        if (source == REQUEST_MemberDetailQuery) {
            switch (NetRespStatType.dealWithRespStat(body)) {
                case NSR:
                    ViewHandler.toastShow(MMemberDetailActivity.this,Ref.OP_NSR);
                    MMemberDetailActivity.this.finish();
                    break;
                case STATUS_AUTHORIZE_FAIL:
                    ViewHandler.exitAcitivityDueToAuthorizeFail(MMemberDetailActivity.this);
                    break;
                default:break;
            }
        }else if (source == REQUEST_ModifyMember) {
            NetRespStatType respStatType = NetRespStatType.dealWithRespStat(body);
            switch (respStatType) {
                case SUCCESS:
                    ViewHandler.toastShow(MMemberDetailActivity.this,Ref.OP_MODIFY_SUCCESS);
                    Intent intent = new Intent(MMemberDetailActivity.this,MemberMainActivity.class);
                    setResult(Ref.RESULTCODE_UPDATE,intent);
                    finish();
                    break;
                case NSR:
                    ViewHandler.toastShow(MMemberDetailActivity.this,"会员已被删除");
                    ActivityMgr.removeAllActivity();
                    break;
                case FAIL:
                    ViewHandler.toastShow(MMemberDetailActivity.this,Ref.OP_MODIFY_FAIL);
                    break;
                case STATUS_AUTHORIZE_FAIL:
                    ViewHandler.exitAcitivityDueToAuthorizeFail(MMemberDetailActivity.this);
                    break;
                default:break;
            }
        }else {

        }
    }

    @Override
    public void onRespMapList(String body, int source) throws IOException {
        if (source == REQUEST_MemberDetailQuery) {
            String [] keys = new String[] {"id","name","login_name","birthday","phone","im"};
            ArrayList<Map<String,String>> list = JsonUtil.strToListMap(body,keys);
            ArrayList<String> values = new ArrayList<String>();
            for (int i = 0;i < list.size();i++) {
                map = list.get(0);
            }
            initEditText(map);
        }else if (source == REQUEST_ModifyMember) {

        }else {

        }
    }

    @Override
    public void onRespError(int source) {
        ViewHandler.toastShow(MMemberDetailActivity.this,Ref.UNKNOWN_ERROR);
        finish();
    }

    @Override
    public void onReqFailure(Object object, int source) {

    }

    @Override
    public void onRespSessionExpired(int source) {
        ViewHandler.alertShowAndExitApp(this);
    }
}
