package com.example.dengweixiong.Activity.Member;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.dengweixiong.Activity.MainActivity;
import com.example.dengweixiong.Util.BaseActivity;
import com.example.dengweixiong.Util.NetUtil;
import com.example.dengweixiong.myapplication.R;

import java.io.IOException;

import okhttp3.Call;
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
                name = et_name.getText().toString();
                phone = et_phone.getText().toString();
                im = et_im.getText().toString();
                login_name = et_login_name.getText().toString();
                password = et_password.getText().toString();
                if (!birthday.equals("")) {
                    birthday = et_birthday.getText().toString() + " 00:00:00";
                }
                if (name.equals("") || login_name.equals("") || password.equals("")) {
                    initAlertDialog("必填字段为空","姓名、登录名或密码为空，请补充完整","确定","取消");
                    break;
                } else {
                    String address = "/AddNewMember?shop_id=5&shop_member_id=1&name=邓伟雄&login_name=dengweixiong44&password=111&phone=13751729017&im=111&birthday=2017-01-01 00:00:00\n";
                    NetUtil.sendHttpRequest(address,new okhttp3.Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {

                        }
                    });
                    Intent intent = new Intent(AddNewMemberActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                }
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
                if (v.hasFocus()) {
                    DatePickerDialog dialog = new DatePickerDialog(AddNewMemberActivity.this, AddNewMemberActivity.this, year, month, day);
                    dialog.show();
                } else {
                    DatePickerDialog dialog = new DatePickerDialog(AddNewMemberActivity.this, AddNewMemberActivity.this, year, month, day);
                    dialog.show();
                }
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
                }
                break;
            default:
                break;
        }

    }
}
