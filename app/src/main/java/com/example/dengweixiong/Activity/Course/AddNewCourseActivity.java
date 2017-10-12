package com.example.dengweixiong.Activity.Course;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.example.dengweixiong.Bean.Course;
import com.example.dengweixiong.Util.BaseActivity;
import com.example.dengweixiong.myapplication.R;

public class AddNewCourseActivity
        extends
        BaseActivity {

    private Toolbar toolbar;
    private Course course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_course);
        initToolbar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    public void initToolbar() {
        toolbar = (Toolbar)findViewById(R.id.addNewCourse_toobar);
        toolbar.setTitle("添加排课");
        setSupportActionBar(toolbar);
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
        return super.onOptionsItemSelected(item);

    }
}
