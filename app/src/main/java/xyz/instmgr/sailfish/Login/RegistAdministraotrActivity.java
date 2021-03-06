package xyz.instmgr.sailfish.Login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.alfred.alfredtools.*;
import xyz.instmgr.sailfish.R;
import xyz.instmgr.sailfish.Util.Ref;

import java.io.IOException;

public class RegistAdministraotrActivity
        extends BaseActivity
        implements View.OnClickListener,HttpResultListener {

    private static final String TAG = "RegistAdminActivity:";
    private static final String TITLE_BAR = "注册机构管理员";
    private EditText et_name,et_loginname,et_password;
    private String shop_name,intro,name,loginname,password;
    private Button btn_submit;
    private long s_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_regist_administrator);
        initView();
        dealWithIntent();
    }

    private void initView() {
        initToolbar();
        initWidgets();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_general);
        toolbar.setTitle(TITLE_BAR);
        setSupportActionBar(toolbar);
    }

    private void initWidgets() {
        et_name = (EditText)findViewById(R.id.et_name_a_regist_admin);
        et_loginname = (EditText)findViewById(R.id.et_loginname_a_regist_admin);
        et_password = (EditText)findViewById(R.id.et_password_a_regist_admin);
        btn_submit = (Button)findViewById(R.id.btn_submit_a_regist_admin);
        btn_submit.setOnClickListener(this);
    }

    private void dealWithIntent() {
        Intent intent = getIntent();
        s_id = intent.getLongExtra("s_id",0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit_a_regist_admin:
                request();
                break;
            default:
                break;
        }
    }

    @Override
    public void onRespStatus(String body, int source) {
        switch (NetRespStatType.dealWithRespStat(body)) {
            case SUCCESS:
                ViewHandler.toastShow(RegistAdministraotrActivity.this,"注册成功，请前往登录页面登录");
                Intent intent = new Intent(RegistAdministraotrActivity.this,LoginActivity.class);
                startActivity(intent);
                RegistAdministraotrActivity.this.finish();
                break;
            case FAIL:
                ViewHandler.toastShow(RegistAdministraotrActivity.this,Ref.OP_FAIL);
                break;
            case DUPLICATE:
                ViewHandler.toastShow(RegistAdministraotrActivity.this,"登录名重复");
                break;
            default:break;
        }
    }

    @Override
    public void onRespMapList(String body, int source) throws IOException {

    }

    @Override
    public void onRespError(int source) {

    }

    @Override
    public void onReqFailure(Object object, int source) {
        Toast.makeText(RegistAdministraotrActivity.this, Ref.CANT_CONNECT_INTERNET,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRespSessionExpired(int source) {
        ViewHandler.alertShowAndExitApp(this);
    }

    private void request() {
        name = et_name.getText().toString();
        loginname = et_loginname.getText().toString();
        password = et_password.getText().toString();
        String url_add_shopmember = "/ShopMemberRegister?login_name=" + loginname + "&pwd=" + password + "&name=" + name + "&s_id=" + s_id;
        HttpCallback callback = new HttpCallback(this,this,0);
        NetUtil.reqSendGet(this,url_add_shopmember,callback);
    }
}
