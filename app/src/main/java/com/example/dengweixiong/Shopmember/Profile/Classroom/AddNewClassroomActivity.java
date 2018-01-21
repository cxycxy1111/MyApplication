package com.example.dengweixiong.Shopmember.Profile.Classroom;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.dengweixiong.Util.BaseActivity;
import com.example.dengweixiong.Util.Enum.EnumRespStatType;
import com.example.dengweixiong.Util.Enum.EnumRespType;
import com.example.dengweixiong.Util.JsonHandler;
import com.example.dengweixiong.Util.MethodTool;
import com.example.dengweixiong.Util.NetUtil;
import com.example.dengweixiong.Util.Ref;
import com.example.dengweixiong.myapplication.R;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AddNewClassroomActivity extends BaseActivity {

    private long sm_id,s_id;
    private long cr_id;
    private EditText et_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom_add);
        initData();
        initToolBar();
        initView();
    }

    private void initData() {
        SharedPreferences preferences = getSharedPreferences("sasm",MODE_PRIVATE);
        sm_id = preferences.getLong("sm_id",0);
        s_id = preferences.getLong("s_id",0);
    }

    private void initView() {
        initToolBar();
        initEditText();
    }

    private void initToolBar(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("新增课室");
    }

    private void initEditText() {
        et_name = (EditText)findViewById(R.id.et_name_a_addNewClassroom);
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
                finish();
                break;
            case R.id.add_a_add_new_classroom :
                final String cr_name = et_name.getText().toString();
                String url = "/ClassroomAdd?s_id=" + s_id + "&name=" + cr_name;
                Callback callback = new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        MethodTool.showToast(AddNewClassroomActivity.this, Ref.CANT_CONNECT_INTERNET);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();
                        EnumRespType respType = EnumRespType.dealWithResponse(resp);
                        switch (respType) {
                            case RESP_STAT:
                                EnumRespStatType respStatType = EnumRespStatType.dealWithRespStat(resp);
                                switch (respStatType) {
                                    case DUPLICATE:
                                        MethodTool.showToast(AddNewClassroomActivity.this,"教室名重复");
                                        break;
                                    case EXE_FAIL:
                                        MethodTool.showToast(AddNewClassroomActivity.this,"新增失败");
                                        break;
                                    case NSR:
                                        MethodTool.showToast(AddNewClassroomActivity.this,"机构不存在");
                                        finish();
                                        break;
                                    default:break;
                                }
                                break;
                            case RESP_MAP:
                                MethodTool.showToast(AddNewClassroomActivity.this,"新增成功");
                                String [] k = new String[]{"id"};
                                List<Map<String,String>> list = JsonHandler.strToListMap(resp,k);
                                cr_id = Long.parseLong(String.valueOf(list.get(0).get(k[0])));
                                Intent intent = new Intent(AddNewClassroomActivity.this,ClassroomListActivity.class);
                                intent.putExtra("cr_id",cr_id).
                                        putExtra("cr_name",cr_name);
                                setResult(Ref.RESULTCODE_ADD,intent);
                                finish();
                                break;
                            default:break;
                        }
                    }
                };
                NetUtil.sendHttpRequest(AddNewClassroomActivity.this,url,callback);
                break;
            default:
                break;
        }
        return true;
    }
}
