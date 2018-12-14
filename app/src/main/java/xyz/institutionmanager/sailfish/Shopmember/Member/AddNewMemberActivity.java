package xyz.institutionmanager.sailfish.Shopmember.Member;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import com.alfred.alfredtools.*;
import xyz.institutionmanager.sailfish.R;
import xyz.institutionmanager.sailfish.Util.Ref;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddNewMemberActivity
        extends BaseActivity
        implements View.OnClickListener, EditText.OnFocusChangeListener,HttpResultListener {

    private Toolbar toolbar;
    private static final String TOOLBAR_TITLE = "注册会员";
    private int current_year,current_month,current_date;
    private EditText et_name,et_birthday,et_phone,et_login_name,et_password,et_im;
    private String name,phone,im,login_name,password,birthday;
    private DatePickerDialog dpd_birthday;
    private static final String TAG = "ADD NEW MEMBER PRINT:";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_add);
        initToolbar();
        initDate();
        initDatePicker();
        initEditext();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_new_member,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_member_list:
                checkBeforeSave();
                break;
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    private void initToolbar() {
        toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(TOOLBAR_TITLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initEditext() {
        et_name = (EditText)findViewById(R.id.edittext_name_activity_add_new_member);
        et_birthday = (EditText)findViewById(R.id.edittext_birthday_activity_add_new_member);
        et_phone = (EditText)findViewById(R.id.edittext_phone_activity_add_new_member);
        et_im = (EditText)findViewById(R.id.edittext_im_activity_add_new_member);
        et_login_name = (EditText)findViewById(R.id.edittext_login_name_activity_add_new_member);
        et_password = (EditText)findViewById(R.id.edittext_password_activity_add_new_member);

        et_birthday.setInputType(InputType.TYPE_NULL);
        et_birthday.setOnClickListener(this);
        et_birthday.setOnFocusChangeListener(this);

    }

    private void initDate() {
        Calendar calendar = Calendar.getInstance();
        this.current_year = calendar.get(Calendar.YEAR);
        this.current_month = calendar.get(Calendar.MONTH);
        this.current_date = calendar.get(Calendar.DAY_OF_MONTH);
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                et_birthday.setText(new StringBuffer().append(year).append("-").append(month+1).append("-").append(dayOfMonth));
            }
        };
        dpd_birthday = new DatePickerDialog(AddNewMemberActivity.this,listener,current_year,current_month,current_date);
    }

    private void initAlertDialog(String title,String content,String confirm,String cancle) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(AddNewMemberActivity.this);
        dialog.setTitle(title);
        dialog.setMessage(content);
        dialog.setCancelable(false);
        dialog.setPositiveButton(confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialog.setNegativeButton(cancle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.show();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edittext_birthday_activity_add_new_member:
                dpd_birthday.show();
                break;
            default:
                break;
        }

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.edittext_birthday_activity_add_new_member:
                if (hasFocus) {
                    dpd_birthday.show();
                }else {

                }
                break;
            default:break;
        }
    }

    @Override
    public void onRespStatus(String body, int source) {
        NetRespStatType respStatType = NetRespStatType.dealWithRespStat(body);
        switch (respStatType) {
            case SUCCESS:
                ViewHandler.toastShow(AddNewMemberActivity.this,"新增成功");
                finish();
                break;
            case FAIL:
                ViewHandler.toastShow(AddNewMemberActivity.this,"新增失败");
                break;
            case DUPLICATE:
                ViewHandler.toastShow(AddNewMemberActivity.this,"登录名重复");
                break;
            case STATUS_AUTHORIZE_FAIL:
                ViewHandler.exitAcitivityDueToAuthorizeFail(AddNewMemberActivity.this);
                break;
            default:break;
        }
    }

    @Override
    public void onRespMapList(String body, int source) throws IOException {

    }

    @Override
    public void onRespError(int source) {
        ViewHandler.toastShow(AddNewMemberActivity.this,Ref.UNKNOWN_ERROR);
    }

    @Override
    public void onReqFailure(Object object, int source) {
        ViewHandler.toastShow(AddNewMemberActivity.this, Ref.CANT_CONNECT_INTERNET);
    }

    @Override
    public void onRespSessionExpired(int source) {
        ViewHandler.alertShowAndExitApp(this);
    }

    private void checkBeforeSave() {
        name = et_name.getText().toString();
        phone = et_phone.getText().toString();
        im = et_im.getText().toString();
        login_name = et_login_name.getText().toString();
        password = et_password.getText().toString();
        birthday = et_birthday.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (birthday.equals("")) {
            birthday = sdf.format(new Date());
        }
        if (name.equals("") || login_name.equals("") || password.equals("") || name.length() >20) {
            initAlertDialog("必填字段为空","姓名、登录名或密码为空，请补充完整","确定","取消");
        }else if (name.length()>20){
            initAlertDialog("姓名过长","姓名限于20字以内","确定","取消");
        }else if (phone.length()>11 ) {
            initAlertDialog("电话号码过长","电话号码需为11位数字","确定","取消");
        }else if (im.length()>32) {
            initAlertDialog("即时通讯过长","即时通讯过长","确定","取消");
        }else if(login_name.length() > 25) {
            initAlertDialog("登录名过长","登录名过长","确定","取消");
        }else {
            saveMember(login_name,password,birthday,im,name,phone);
        }
    }

    private void saveMember(String login_name,String password,String birthday,String im,String name,String phone) {
        String address = "/AddNewMember?name=" + name +
                "&login_name=" +login_name +
                "&password=" +password +
                "&phone=" +phone +
                "&im=" + im +
                "&birthday=" + birthday;
        HttpCallback callback = new HttpCallback(this,this,0);
        NetUtil.reqSendGet(this,address,callback);
    }
}
