package com.example.dengweixiong.Shopmember.MemberCard;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.example.dengweixiong.Util.BaseActivity;
import com.example.dengweixiong.myapplication.R;

public class DeductionActivity extends BaseActivity {

    private Toolbar toolbar;
    private Spinner spinner_member;
    private Spinner spinner_member_card;
    private LinearLayout linearLayout_balance;
    private LinearLayout linearLayout_num;
    private LinearLayout linearLayout_time;
    private ArrayAdapter spinner_card_adapter;
    private ArrayAdapter spinner_member_adapter;
    private String member_list []={"张三","李四"};
    private String member_card_list [] = {"会员卡1","会员卡2"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_card_deduction);
        initToolbar();
        initSpinnerMember();
        initSpinnerMemberCard();
    }

    private void initToolbar(){
        toolbar = (Toolbar)findViewById(R.id.tb_a_deduction);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("扣费");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initSpinnerMemberCard() {
        spinner_member_card = (Spinner)findViewById(R.id.sp_card_a_deduction);
        spinner_card_adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,member_card_list);
        spinner_member_card.setAdapter(spinner_card_adapter);
        spinner_card_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    private void initSpinnerMember() {
        spinner_member = (Spinner)findViewById(R.id.sp_member_a_deduction);
        spinner_member_adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,member_list);
        spinner_member.setAdapter(spinner_member_adapter);
        spinner_member_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_deduction,menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            default:
                break;
        }
        return true;
    }
}
