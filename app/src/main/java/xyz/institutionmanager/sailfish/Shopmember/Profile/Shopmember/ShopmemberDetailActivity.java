package xyz.institutionmanager.sailfish.Shopmember.Profile.Shopmember;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.alfred.alfredtools.*;
import xyz.institutionmanager.sailfish.R;
import xyz.institutionmanager.sailfish.Util.Ref;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopmemberDetailActivity extends BaseActivity implements HttpResultListener {

    private String name;
    private int type,position;
    private long id,sm_id,s_id;
    private String new_name;
    private int new_type;
    private EditText et_name,et_user_name;
    private Button btn_delete,btn_modify;
    private Spinner spinner;
    private String [] keys = new String[] {"id","type","name","user_name"};
    private Map<String,String> map = new HashMap<>();
    private List<Map<String,String>> list = new ArrayList<>();
    private static final int REQUEST_ShopmemberQueryDetail = 1;
    private static final int REQUEST_ShopmemberModify = 2;
    private static final int REQUEST_ShopmemberDelete = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopmember_detail);
        initData();
        initView();
    }

    private void initData() {
        Intent intent = getIntent();
        id = Long.parseLong(intent.getStringExtra("id"));
        type = Integer.parseInt(intent.getStringExtra("type"));
        position = intent.getIntExtra("pos",-1);
        name = intent.getStringExtra("name");
        SharedPreferences preferences = getSharedPreferences("sasm",MODE_PRIVATE);
        sm_id = preferences.getLong("sm_id",0);
        s_id = preferences.getLong("s_id",0);
    }

    private void initView() {
        spinner = (Spinner)findViewById(R.id.sp_type_shopmember_detail);
        initToolBar();
        initEditText();
        initLinearLayout();
        initSpinner();
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(name);
    }

    private void initEditText() {
        initEditTextData();
    }

    private void initLinearLayout(){
        btn_delete = (Button)findViewById(R.id.btn_delete_a_shopmember_detail);
        btn_modify = (Button)findViewById(R.id.btn_modifypwd_a_shopmember_detail);

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(ShopmemberDetailActivity.this);
                builder.setTitle("确定要删除吗？");
                builder.setMessage("删除后教师将不能登录、参加排课等");
                builder.setCancelable(false);
                builder.setNegativeButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (id == sm_id) {
                            Toast.makeText(ShopmemberDetailActivity.this,"你无法删除自身",Toast.LENGTH_SHORT).show();
                        }else {
                            delete();
                        }
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("不删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });
        btn_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShopmemberDetailActivity.this,ResetShopmemberPasswordActivity.class);
                intent.putExtra("id",id);
                startActivity(intent);
            }
        });
    }

    private void initEditTextData() {
        et_name = (EditText)findViewById(R.id.et_name_a_shopmember_detail);
        et_user_name = (EditText)findViewById(R.id.et_user_name_a_shopmember_detail);
        String url = "/ShopmemberQueryDetail?sm_id=" + id;
        HttpCallback callback = new HttpCallback(this,this,REQUEST_ShopmemberQueryDetail);
        NetUtil.reqSendGet(this,url,callback);
    }

    private void initSpinner() {
        new_type = type;
        String[] strings = new String[] {"管理员","内部教师","外聘教师"};
        ArrayAdapter adapter = new ArrayAdapter(ShopmemberDetailActivity.this,android.R.layout.simple_spinner_item,android.R.id.text1,strings);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                new_type = position+1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                new_type = type;
            }
        });
        spinner.setSelection(type-1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_shopmember_detail,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(Ref.RESULTCODE_NULL,null);
                this.finish();
                break;
            case R.id.save_shopmember_detail:
                save();
        }

        return true;
    }

    @Override
    public void onRespStatus(String body, int source) {
        if (source == REQUEST_ShopmemberQueryDetail) {
            switch (NetRespStatType.dealWithRespStat(body)) {
                case NST_NOT_MATCH:
                    ViewHandler.toastShow(ShopmemberDetailActivity.this,Ref.OP_INST_NOT_MATCH);
                    ShopmemberDetailActivity.this.finish();
                    break;
                case STATUS_AUTHORIZE_FAIL:
                    ViewHandler.exitAcitivityDueToAuthorizeFail(ShopmemberDetailActivity.this);
                    break;
                case NSR:
                    ViewHandler.toastShow(ShopmemberDetailActivity.this,Ref.OP_NSR);
                    break;
                default:break;
            }
        }else if (source == REQUEST_ShopmemberModify) {
            NetRespStatType respStatType = NetRespStatType.dealWithRespStat(body);
            switch (respStatType) {
                case SUCCESS:
                    ViewHandler.toastShow(ShopmemberDetailActivity.this,"保存成功");
                    Intent intent = new Intent(ShopmemberDetailActivity.this,ShopmemberListActivity.class);
                    intent.putExtra("name",new_name);
                    intent.putExtra("id",id);
                    intent.putExtra("old_type",type);
                    intent.putExtra("new_type",new_type);
                    intent.putExtra("pos",position);
                    setResult(Ref.RESULTCODE_UPDATE,intent);
                    finish();
                    break;
                case FAIL:
                    ViewHandler.toastShow(ShopmemberDetailActivity.this,"保存失败");
                    break;
                case NST_NOT_MATCH:
                    ViewHandler.toastShow(ShopmemberDetailActivity.this,"机构不匹配");
                    break;
                case NSR:
                    ViewHandler.toastShow(ShopmemberDetailActivity.this,"记录不存在");
                    break;
                case STATUS_AUTHORIZE_FAIL:
                    ViewHandler.exitAcitivityDueToAuthorizeFail(ShopmemberDetailActivity.this);
                    break;
                default:break;
            }
        }else if (source == REQUEST_ShopmemberDelete) {
            NetRespStatType respStatType = NetRespStatType.dealWithRespStat(body);
            switch (respStatType) {
                case NST_NOT_MATCH:
                    ViewHandler.toastShow(ShopmemberDetailActivity.this,"机构不匹配");
                    break;
                case SUCCESS:
                    ViewHandler.toastShow(ShopmemberDetailActivity.this,"删除成功");
                    Intent intent = new Intent(ShopmemberDetailActivity.this,ShopmemberListActivity.class);
                    intent.putExtra("pos",position)
                            .putExtra("type",type);
                    setResult(Ref.RESULTCODE_DELETE,intent);
                    finish();
                    break;
                case FAIL:
                    ViewHandler.toastShow(ShopmemberDetailActivity.this,"删除失败");
                    break;
                case STATUS_AUTHORIZE_FAIL:
                    ViewHandler.exitAcitivityDueToAuthorizeFail(ShopmemberDetailActivity.this);
                    break;
                default:break;
            }
        }else {

        }
    }

    @Override
    public void onRespMapList(String body, int source) throws IOException {
        if (source == REQUEST_ShopmemberQueryDetail) {
            list = JsonUtil.strToListMap(body,keys);
            map = list.get(0);
            et_name.setText(map.get("name"));
            et_user_name.setText(map.get("user_name"));
        }else if (source == REQUEST_ShopmemberModify) {

        }else if (source == REQUEST_ShopmemberDelete) {

        }else {

        }
    }

    @Override
    public void onRespError(int source) {
        ViewHandler.toastShow(ShopmemberDetailActivity.this,Ref.UNKNOWN_ERROR);
    }

    @Override
    public void onReqFailure(Object object, int source) {
        ViewHandler.toastShow(ShopmemberDetailActivity.this, Ref.CANT_CONNECT_INTERNET);
    }

    @Override
    public void onRespSessionExpired(int source) {
        ViewHandler.alertShowAndExitApp(ShopmemberDetailActivity.this);
    }

    private void save() {
        new_name = et_name.getText().toString();
        String url = "/ShopmemberModify?sm_id=" + id + "&name=" + new_name + "&new_type=" + new_type;
        HttpCallback callback = new HttpCallback(this,this,REQUEST_ShopmemberModify);
        NetUtil.reqSendGet(this,url,callback);
    }

    private void delete(){
        final String url = "/ShopmemberDelete?id=" + id;
        if (id == sm_id) {
            ViewHandler.toastShow(ShopmemberDetailActivity.this,"你无法删除自身");
        }else {
            HttpCallback callback = new HttpCallback(this,this,REQUEST_ShopmemberModify);
            NetUtil.reqSendGet(this,url,callback);
        }

    }
}
