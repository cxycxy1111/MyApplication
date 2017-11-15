package com.example.dengweixiong.Activity.Member;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dengweixiong.Adapter.RVSimpleAdapter;
import com.example.dengweixiong.Util.BaseActivity;
import com.example.dengweixiong.Util.JsonHandler;
import com.example.dengweixiong.Util.MethodTool;
import com.example.dengweixiong.Util.NetUtil;
import com.example.dengweixiong.Util.Reference;
import com.example.dengweixiong.myapplication.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MemberDetailActivity
        extends BaseActivity
        implements RVSimpleAdapter.OnItemClickListener,
            View.OnClickListener,
            DatePickerDialog.OnDateSetListener,
            EditText.OnFocusChangeListener{

    Toolbar toolbar;
    private String toolbar_title;
    private long s_id,sm_id,m_id;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;
    private EditText et_sn,et_name,et_birthday,et_phone,et_im;
    private CharSequence sn,name,birthday,phone,im;
    private ArrayList<String> list = new ArrayList<>();
    private Map<String,String> map = new HashMap<>();
    private int year,month,day;
    private AlertDialog.Builder builder;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_detail);
        initToolBar();
        initActions();
        initData();
        initView();
    }

    private void initToolBar () {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        toolbar_title = (String)bundle.get("toolbar_title");
        toolbar = (Toolbar)findViewById(R.id.toolbar_activity_member_detail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(toolbar_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initActions() {
        list.add("查看会员卡");
        list.add("重置密码");
        list.add("删除会员卡");
        recyclerView = (RecyclerView)findViewById(R.id.recyclerview_activity_member_detail);
        linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        RVSimpleAdapter simpleRecylcerViewAdapter = new RVSimpleAdapter(list);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(simpleRecylcerViewAdapter);
        simpleRecylcerViewAdapter.setOnItemClickListener(this);
    }

    private void initView() {
        String url = "/QueryMemberDetail?member_id=" + m_id +
                "&shop_id=" + s_id;
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(MemberDetailActivity.this, Reference.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                String [] keys = new String[] {"id","name","login_name","birthday","phone","im"};
                ArrayList<Map<String,String>> list = JsonHandler.strToListMap(resp,keys);
                ArrayList<String> values = new ArrayList<String>();
                for (int i = 0;i < list.size();i++) {
                    map = list.get(0);
                }
                m_id = Long.parseLong(String.valueOf(map.get("id")));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initEditText(map);
                    }
                });
            }
        };
        NetUtil.sendHttpRequest(MemberDetailActivity.this,url,callback);
    }

    private void initData() {
        sm_id =MethodTool.preGetLong(MemberDetailActivity.this,"sasm","sm_id");
        s_id = MethodTool.preGetLong(MemberDetailActivity.this,"sasm","s_id");
        dealWithIntent();
    }

    private void initEditText(Map<String,String> map) {
        et_sn = (EditText)findViewById(R.id.edittext_sn_activity_member_detail);
        et_name = (EditText)findViewById(R.id.edittext_name_activity_member_detail);
        et_birthday = (EditText)findViewById(R.id.edittext_birthday_activity_member_detail);
        et_phone = (EditText)findViewById(R.id.edittext_phone_activity_member_detail);
        et_im = (EditText)findViewById(R.id.edittext_im_activity_member_detail);

        et_birthday.setInputType(InputType.TYPE_NULL);
        et_birthday.setOnClickListener(this);
        et_birthday.setOnFocusChangeListener(this);

        Object o_id = map.get("id");
        Object o_name = map.get("name");
        sn = String.valueOf(o_id);
        name = String.valueOf(o_name);
        et_sn.setText(sn);
        et_name.setText(name);
        et_birthday.setText(String.valueOf(map.get("birthday")));
        et_phone.setText(String.valueOf(map.get("phone")));
        et_im.setText(String.valueOf(map.get("im")));
    }

    private void dealWithIntent() {
        Map<String,String> map = new HashMap<>();
        Intent intent = getIntent();
        map.put("position",String.valueOf(intent.getIntExtra("position",0)));
        position = intent.getIntExtra("position",0);
        m_id = Long.parseLong(intent.getStringExtra("m_id"));
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.edittext_birthday_activity_add_new_member:
                if (hasFocus == false) {
                    DatePickerDialog dialog = new DatePickerDialog(MemberDetailActivity.this, MemberDetailActivity.this, year, month, day);
                    dialog.show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edittext_birthday_activity_member_detail:
                DatePickerDialog dialog = new DatePickerDialog(MemberDetailActivity.this, MemberDetailActivity.this, year, month, day);
                dialog.show();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_member_detail,menu);
        return true;
    }

    @Override
    public void onItemClick(View view, int positon) {
        Intent intent;
        switch (positon) {
            case 0:
                intent = new Intent(this,MemberCardListActivity.class);
                intent.putExtra("source","MemberDetailActivity");
                startActivity(intent);
                break;
            case 1:
                intent = new Intent(MemberDetailActivity.this,ResetMemberPwdActivity.class);
                intent.putExtra("m_id",m_id);
                startActivity(intent);
                break;
            case 2:
                dealWithDeleteMemberAction();
                break;
            default:
                break;
        }
    }

    /**
     * 菜单点击事件
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                builder = new AlertDialog.Builder(MemberDetailActivity.this);
                builder.setTitle("提示");
                builder.setMessage("是否保存更改？");
                builder.setCancelable(false);
                builder.setNegativeButton("不保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setResult(Reference.RESULTCODE_NULL,null);
                        finish();
                    }
                });
                builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(MemberDetailActivity.this,"请点击\"保存\"按钮保存更改",Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
                break;
            case R.id.save_member_detail:
                final String m_name = et_name.getText().toString();
                String m_birthday = et_birthday.getText().toString();
                String m_phone = et_phone.getText().toString();
                String m_im = et_im.getText().toString();
                long sm_id = MethodTool.preGetLong(MemberDetailActivity.this,"sasm","sm_id");
                String url = "/ModifyMember?member_id=" + m_id +
                        "&shopmember_id=" + sm_id +
                        "&name=" + m_name +
                        "&birthday=" + m_birthday +
                        "&phone=" + m_phone +
                        "&im=" + m_im;
                Callback callback = new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Map<String,String> map = new HashMap<>();
                        String resp = response.body().string();
                        map = JsonHandler.strToMap(resp);
                        ArrayList<String> keys = MethodTool.getKeys(map);
                        ArrayList<String> values = MethodTool.getValues(map,keys);
                        if (keys.get(0).equals(Reference.STATUS)) {
                            switch (String.valueOf(values.get(0))) {
                                case "exe_suc":
                                    MethodTool.showToast(MemberDetailActivity.this,"已保存");
                                    Intent intent = new Intent(MemberDetailActivity.this,MemberListActivity.class);
                                    intent.putExtra("pos",position);
                                    intent.putExtra("m_id",m_id);
                                    intent.putExtra("m_name",m_name);
                                    setResult(Reference.RESULTCODE_UPDATE,intent);
                                    finish();
                                    break;
                                case "exe_fail":
                                    MethodTool.showToast(MemberDetailActivity.this,"更新失败");
                                    break;
                                case "no_such_record":
                                    MethodTool.showToast(MemberDetailActivity.this,"会员已被删除");
                                    break;
                                default:
                                    break;
                            }
                        }else {
                            MethodTool.showToast(MemberDetailActivity.this,"内部错误");
                        }
                    }
                };
                NetUtil.sendHttpRequest(MemberDetailActivity.this,url,callback);
                break;
            default:
                break;
        }
        return true;
    }

    private void dealWithDeleteMemberAction() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MemberDetailActivity.this);
        builder.setCancelable(false);
        builder.setTitle("确认要删除该会员吗？");
        builder.setMessage("删除会员后，该会员下的会员卡均无法使用。");
        builder.setNegativeButton("不删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String url = "/removeMember?shopmember_id=" + sm_id + "&member_id=" + m_id;
                Callback callback = new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        MethodTool.showToast(MemberDetailActivity.this,Reference.CANT_CONNECT_INTERNET);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();
                        Map<String,String> map = new HashMap<String, String>();
                        map = JsonHandler.strToMap(resp);
                        ArrayList<String> keys = MethodTool.getKeys(map);
                        ArrayList<String> values = MethodTool.getValues(map,keys);
                        switch (keys.get(0)) {
                            case "stat" :
                                switch (values.get(0)) {
                                    case "exe_suc" :
                                        MethodTool.showToast(MemberDetailActivity.this,"删除成功");
                                        Intent intent = new Intent(MemberDetailActivity.this,MemberListActivity.class);
                                        intent.putExtra("pos",position);
                                        setResult(Reference.RESULTCODE_DELETE,intent);
                                        finish();
                                        break;
                                    case "exe_fail" :
                                        MethodTool.showToast(MemberDetailActivity.this,"删除失败");
                                        break;
                                    case "no_such_record" :
                                        MethodTool.showToast(MemberDetailActivity.this,"该会员已被删除");
                                        MemberDetailActivity.this.finish();
                                        break;
                                    default:
                                        MethodTool.showToast(MemberDetailActivity.this,Reference.UNKNOWN_ERROR);
                                        break;
                                }
                            default:
                                break;
                        }
                    }
                };
                NetUtil.sendHttpRequest(MemberDetailActivity.this,url,callback);
            }
        });
        builder.show();
    }
}
