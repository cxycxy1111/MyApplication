package com.example.dengweixiong.Activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.example.dengweixiong.Util.BaseActivity;
import com.example.dengweixiong.myapplication.R;

public class AddNewStoryActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_story);
        initToolbar();
    }

    @Override
    public void onClick(View view) {
    }


    public void initToolbar() {
        toolbar = (Toolbar)findViewById(R.id.addNewStory_toobar);
        toolbar.setTitle("Add a New Stroy");
        setSupportActionBar(toolbar);
    }
}
