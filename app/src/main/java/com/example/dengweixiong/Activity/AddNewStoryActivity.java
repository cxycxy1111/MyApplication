package com.example.dengweixiong.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dengweixiong.myapplication.R;

public class AddNewStoryActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_story);
        ActionBar actionBar = getSupportActionBar();
        Intent intent = getIntent();
        String str = intent.getStringExtra("intent");
        TextView textView = (TextView)findViewById(R.id.sec_text);
        textView.setText(str);
        textView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sec_text:
                Toast.makeText(this,"hello,world",Toast.LENGTH_SHORT).show();
            default:
                break;
        }
    }
}
