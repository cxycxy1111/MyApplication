package com.example.dengweixiong.Activity.Member;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.example.dengweixiong.Activity.MainActivity;
import com.example.dengweixiong.myapplication.R;

public class AddNewMemberCardActivity
        extends AppCompatActivity
        implements Spinner.OnItemSelectedListener{

    private String [] str_member_card = {"余额卡","次卡","有效期卡"};
    private String [] str_member = {"邓伟雄","张三","李四"};
    private ArrayAdapter<String> adapter;
    ArrayAdapter<String> adapter_2;
    private String source;
    private Toolbar toolbar;
    private Spinner spinner_member;
    private Spinner spinner_member_card;
    private LinearLayout linearlayout_num;
    private LinearLayout linearlayout_balance;
    private LinearLayout linearlayout_time;
    private static final String TOOLBAR_TITLE_NEW = "新增会员卡";
    private static final String TOOLBAR_TITLE_ORIGIN = "会员卡详情";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_member_card);
        source = getIntent().getStringExtra("source");
        initToolbar();
        initSpinner();
        initLinearLayout();
    }

    private void initToolbar() {
        toolbar = (Toolbar)findViewById(R.id.toolbar_a_add_new_member_card);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(TOOLBAR_TITLE_NEW);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initSpinner() {
        spinner_member = (Spinner)findViewById(R.id.spinner_member_a_add_new_member);
        spinner_member_card = (Spinner)findViewById(R.id.spinner_member_carld_a_add_new_member);
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,str_member);
        adapter_2 = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,str_member_card);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter_2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_member.setAdapter(adapter);
        spinner_member_card.setAdapter(adapter_2);
        spinner_member_card.setOnItemSelectedListener(this);
    }

    private void initLinearLayout() {
        linearlayout_balance = (LinearLayout)findViewById(R.id.linearlayout_balance_a_add_new_member_card);
        linearlayout_num = (LinearLayout)findViewById(R.id.linearlayout_num_a_add_new_member_card);
        linearlayout_time = (LinearLayout)findViewById(R.id.linearlayout_time_a_add_new_member_card);
        linearlayout_num.setVisibility(linearlayout_num.INVISIBLE);
        linearlayout_time.setVisibility(linearlayout_time.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_new_member_card,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.save_add_new_member_card:
                this.finish();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String data = (String) spinner_member_card.getItemAtPosition(position);
        switch (data) {
            case "余额卡":
                linearlayout_balance.setVisibility(linearlayout_balance.VISIBLE);
                linearlayout_num.setVisibility(linearlayout_num.GONE);
                linearlayout_time.setVisibility(linearlayout_time.GONE);
                break;
            case "次卡":
                linearlayout_balance.setVisibility(linearlayout_balance.GONE);
                linearlayout_num.setVisibility(linearlayout_num.VISIBLE);
                linearlayout_time.setVisibility(linearlayout_time.GONE);
                break;
            case "有效期卡":
                linearlayout_num.setVisibility(linearlayout_num.GONE);
                linearlayout_balance.setVisibility(linearlayout_balance.GONE);
                linearlayout_time.setVisibility(linearlayout_time.VISIBLE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
