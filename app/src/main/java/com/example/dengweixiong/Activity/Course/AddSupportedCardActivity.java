package com.example.dengweixiong.Activity.Course;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.dengweixiong.Util.BaseActivity;
import com.example.dengweixiong.myapplication.R;

public class AddSupportedCardActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_supported_card);
        initViews();
    }

    private void initViews() {
        initToolbar();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("配置所支持的卡");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initRecyclerView() {

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;
    }
}
