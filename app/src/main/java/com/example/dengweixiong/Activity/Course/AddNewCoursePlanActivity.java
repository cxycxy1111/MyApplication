package com.example.dengweixiong.Activity.Course;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Menu;

import com.example.dengweixiong.Util.BaseActivity;
import com.example.dengweixiong.myapplication.R;

public class AddNewCoursePlanActivity extends BaseActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_course_plan);
        initToolbar();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_new_trend,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.quit_send_new_trend:
                break;
            case R.id.send_add_new_trend:
                break;
            case android.R.id.home:
                finish();
        }
        return true;
    }

    public void initToolbar () {
        toolbar = (Toolbar)findViewById(R.id.addNewCoursePlan_toobar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("添加排课");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
