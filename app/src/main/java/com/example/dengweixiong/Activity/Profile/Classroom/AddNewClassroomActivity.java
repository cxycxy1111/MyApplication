package com.example.dengweixiong.Activity.Profile.Classroom;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.dengweixiong.Activity.Member.AddNewMemberActivity;
import com.example.dengweixiong.Util.BaseActivity;
import com.example.dengweixiong.Util.MethodTool;
import com.example.dengweixiong.Util.NetUtil;
import com.example.dengweixiong.Util.Reference;
import com.example.dengweixiong.myapplication.R;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AddNewClassroomActivity extends BaseActivity {

    private long sm_id,s_id;
    private long cr_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_classroom);
        initData();
        initToolBar();

    }

    private void initData() {
        SharedPreferences preferences = getSharedPreferences("sasm",MODE_PRIVATE);
        sm_id = preferences.getLong("sm_id",0);
        s_id = preferences.getLong("s_id",0);
    }

    private void initToolBar(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("新增课室");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_new_classroom,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.add_a_add_new_classroom :
                this.finish();
                break;
            default:
                break;
        }
        return true;
    }
}
