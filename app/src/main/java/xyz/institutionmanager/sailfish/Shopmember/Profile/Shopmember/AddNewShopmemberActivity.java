package xyz.institutionmanager.sailfish.Shopmember.Profile.Shopmember;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import com.alfred.alfredtools.*;
import xyz.institutionmanager.sailfish.R;
import xyz.institutionmanager.sailfish.Util.Ref;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddNewShopmemberActivity
        extends BaseActivity
        implements HttpResultListener {

    private List<String> type = new ArrayList<>();
    private int selectedType = 0;
    private long s_id,sm_id;
    private EditText et_name,et_user_name,et_pwd;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopmember_add);
        initData();
        initView();
    }

    private void initData() {
        type.add("管理员");type.add("内部教师");type.add("外聘教师");

        SharedPreferences preferences = getSharedPreferences("sasm",MODE_PRIVATE);
        s_id = preferences.getLong("s_id",0);
        sm_id = preferences.getLong("sm_id",0);
    }

    private void initView(){
        initToolbar();
        initSpinner();
        initEditText();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("新增教师");
    }

    private void initSpinner() {
        Spinner spinner = (Spinner)findViewById(R.id.sp_type_add_new_shopmember);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,type);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        selectedType = 1;
                        break;
                    case 1:
                        selectedType = 2;
                        break;
                    case 2:
                        selectedType = 3;
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedType = 1;
            }
        });
    }

    private void initEditText(){
        et_name = (EditText)findViewById(R.id.et_name_add_new_shopmember);
        et_user_name = (EditText)findViewById(R.id.et_user_name_add_new_shopmember);
        et_pwd = (EditText)findViewById(R.id.et_pwd_add_new_shopmember);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_new_shopmember,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                setResult(Ref.RESULTCODE_NULL);
                finish();
                break;
            case R.id.save_add_new_shopmember :
                save();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onRespStatus(String body, int source) {
        NetRespStatType respStatType = NetRespStatType.dealWithRespStat(body);
        switch (respStatType) {
            case FAIL:
                ViewHandler.toastShow(AddNewShopmemberActivity.this,Ref.OP_ADD_FAIL);
                break;
            case DUPLICATE:
                ViewHandler.toastShow(AddNewShopmemberActivity.this,"登录名重复");
                break;
            case STATUS_AUTHORIZE_FAIL:
                ViewHandler.exitAcitivityDueToAuthorizeFail(AddNewShopmemberActivity.this);
                break;
            default:break;
        }
    }

    @Override
    public void onRespMapList(String body, int source) throws IOException {
        String v = JsonUtil.strToMap(body).get(Ref.DATA);
        Intent intent = new Intent(AddNewShopmemberActivity.this,ShopmemberListActivity.class);
        intent.putExtra("id",v);
        intent.putExtra("name",name);
        intent.putExtra("type",selectedType);
        setResult(Ref.RESULTCODE_ADD,intent);
        finish();
    }

    @Override
    public void onRespError(int source) {
        ViewHandler.toastShow(AddNewShopmemberActivity.this, Ref.UNKNOWN_ERROR);
    }

    @Override
    public void onReqFailure(Object object, int source) {

    }

    @Override
    public void onRespSessionExpired(int source) {
        ViewHandler.alertShowAndExitApp(AddNewShopmemberActivity.this);
    }

    private void save() {
        name = et_name.getText().toString();
        String user_name = et_user_name.getText().toString();
        String pwd = et_pwd.getText().toString();
        String url = "/AddNewShopMember?name=" + name +
                "&user_name=" + user_name +
                "&password=" + pwd +
                "&type=" + selectedType;
        HttpCallback callback = new HttpCallback(this,this,0);
        NetUtil.reqSendGet(this,url,callback);
    }
}
