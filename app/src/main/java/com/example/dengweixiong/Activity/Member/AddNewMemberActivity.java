package com.example.dengweixiong.Activity.Member;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.dengweixiong.Activity.MainActivity;
import com.example.dengweixiong.Util.BaseActivity;
import com.example.dengweixiong.myapplication.R;

public class AddNewMemberActivity
        extends BaseActivity
        implements View.OnClickListener,
            DatePickerDialog.OnDateSetListener,
            EditText.OnFocusChangeListener{

    private Toolbar toolbar;
    private static final String TOOLBAR_TITLE = "新增会员";
    private int year,month,day;
    private EditText et_name,et_birthday,et_phone,et_login_name,et_password,et_im;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_member);
        initToolbar();
        initEditext();
        String name = et_name.getText().toString();
        String phone = et_phone.getText().toString();
        String im = et_im.getText().toString();
        String login_name = et_login_name.getText().toString();
        String password = et_password.getText().toString();
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
                Intent intent = new Intent(AddNewMemberActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
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
