package com.example.dengweixiong.Activity.Member;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.dengweixiong.Activity.MainActivity;
import com.example.dengweixiong.Bean.Classroom;
import com.example.dengweixiong.Util.BaseActivity;
import com.example.dengweixiong.Util.JsonHandler;
import com.example.dengweixiong.Util.MethodTool;
import com.example.dengweixiong.Util.NetUtil;
import com.example.dengweixiong.Util.Reference;
import com.example.dengweixiong.myapplication.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AddNewMemberActivity
        extends BaseActivity
        implements View.OnClickListener,
            DatePickerDialog.OnDateSetListener,
            EditText.OnFocusChangeListener{

    private Toolbar toolbar;
    private static final String TOOLBAR_TITLE = "新增会员";
    private int year,month,day;
    private EditText et_name,et_birthday,et_phone,et_login_name,et_password,et_im;
    private String name,phone,im,login_name,password,birthday;
    private long s_id,sm_id;
    private static final String TAG = "ADD NEW MEMBER PRINT:";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_member);
        initToolbar();
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
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                s_id = MethodTool.preGetLong(getApplicationContext(),"sasm","s_id");
                sm_id = MethodTool.preGetLong(getApplicationContext(),"sasm","sm_id");
                name = et_name.getText().toString();
                phone = et_phone.getText().toString();
                im = et_im.getText().toString();
                login_name = et_login_name.getText().toString();
                password = et_password.getText().toString();
                birthday = et_birthday.getText().toString();
                if (birthday.equals("")) {
                    birthday = sdf.format(new Date());
                }
                if (name.equals("") || login_name.equals("") || password.equals("")) {
                    initAlertDialog("必填字段为空","姓名、登录名或密码为空，请补充完整","确定","取消");
                    break;
                }
                String address = "/AddNewMember?shop_id=" + String.valueOf(s_id) +
                    "&shop_member_id=" + String.valueOf(sm_id) +
                    "&name=" + name +
                    "&login_name=" +login_name +
                    "&password=" +password +
                    "&phone=" +phone +
                    "&im=" + im +
                    "&birthday=" + birthday;
                Callback callback = new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        MethodTool.showToast(AddNewMemberActivity.this, Reference.CANT_CONNECT_INTERNET);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Map<String,String> map = JsonHandler.strToMap(response);
                        switch (MethodTool.getValues(map,MethodTool.getKeys(map)).get(0)) {
                            case "exe_suc":
                                MethodTool.showToast(AddNewMemberActivity.this,"新增成功");
                                MethodTool.jumpToActivity(AddNewMemberActivity.this,MainActivity.class);
                                finish();
                                break;
                            case "duplicate":
                                MethodTool.showToast(AddNewMemberActivity.this,"登录名重复");
                                break;
                            case "exe_fail" :
                                MethodTool.showToast(AddNewMemberActivity.this,"新增失败");
                                break;
                            default:
                                break;
                        }
                    }
                };
                NetUtil.sendHttpRequest(AddNewMemberActivity.this,address,callback);
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
        toolbar = (Toolbar)findViewById(R.id.toolbar_activity_add_new_member);
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
                DatePickerDialog dialog = new DatePickerDialog(AddNewMemberActivity.this, AddNewMemberActivity.this, year, month, day);
                dialog.show();
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
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.edittext_birthday_activity_add_new_member:
                if (hasFocus == true) {
                    DatePickerDialog dialog = new DatePickerDialog(AddNewMemberActivity.this, AddNewMemberActivity.this, year, month, day);
                    dialog.show();
                }else {
                }
                break;
            default:
                break;
        }

    }
}
