package xyz.instmgr.sailfish.Member.Profile.MyProfile;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import com.alfred.alfredtools.*;
import xyz.instmgr.sailfish.R;
import xyz.instmgr.sailfish.Util.Ref;

import java.io.IOException;

public class MResetMemberPwdActivity
        extends BaseActivity implements HttpResultListener {

    private Toolbar toolbar;
    private EditText et_newpwd,et_newpwd_repeat;
    private AlertDialog dialog_exit;
    private String newpwd,newpwd_repeat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_member_reset_pwd);
        initAlertDialog("需要重新登录","你已重新设置过密码，需要重新输入账号密码登录。","重新登录");
        initView();
    }

    private void initView() {
        initToolbar();
        initEditText();
    }

    private void initToolbar() {
        toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        toolbar.setTitle(R.string.title_a_reset_member_pwd);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initEditText() {
        et_newpwd = (EditText)findViewById(R.id.et_newpwd_a_m_reset_pwd);
        et_newpwd_repeat = (EditText)findViewById(R.id.et_repeat_newpwd_a_m_reset_pwd);
    }

    private void initData() {
        newpwd = et_newpwd.getText().toString();
        newpwd_repeat = et_newpwd_repeat.getText().toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_reset_pwd,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.submit_reset_pwd:
                dealWithSubmitAction();
            case android.R.id.home:
                this.finish();
                break;
            default:
                break;
        }
        return true;
    }

    private void dealWithSubmitAction() {
        initData();
        if (newpwd == null | newpwd.equals("")) {
            Toast.makeText(this,"密码不能为空",Toast.LENGTH_LONG).show();
            return;
        }
        if ((newpwd_repeat == null | newpwd_repeat.equals("")) && (newpwd != null | !newpwd.equals(""))) {
            Toast.makeText(this,"请重复输入密码",Toast.LENGTH_LONG).show();
            return;
        }
        if (!newpwd_repeat.equals(newpwd)) {
            Toast.makeText(this,"两次输入的密码不一致，请核对",Toast.LENGTH_LONG).show();
            return;
        }
        String url = "/mResetPassword?newpwd=" + newpwd;
        HttpCallback callback = new HttpCallback(this,this,0);
        NetUtil.reqSendGet(this,url,callback);
    }


    private void initAlertDialog(String title,String content,String confirm) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MResetMemberPwdActivity.this);
        dialog.setTitle(title);
        dialog.setMessage(content);
        dialog.setCancelable(false);
        dialog.setPositiveButton(confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences sharedPreferences = getSharedPreferences("member_login_data",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear().apply();
                ActivityMgr.removeAllActivity();

            }
        });
        dialog_exit = dialog.create();
    }

    @Override
    public void onRespStatus(String body, int source) {
        NetRespStatType respStatType = NetRespStatType.dealWithRespStat(body);
        switch (respStatType) {
            case NSR:
                ViewHandler.toastShow(MResetMemberPwdActivity.this,Ref.STAT_NSR);
                MResetMemberPwdActivity.this.finish();
                ActivityMgr.removeAllActivity();
                break;
            case SUCCESS:
                dialog_exit.show();
                break;
            case FAIL:
                ViewHandler.toastShow(MResetMemberPwdActivity.this,Ref.OP_MODIFY_FAIL);
                break;
            case STATUS_AUTHORIZE_FAIL:
                ViewHandler.exitAcitivityDueToAuthorizeFail(MResetMemberPwdActivity.this);
                break;
            default:
                break;
        }
    }

    @Override
    public void onRespMapList(String body, int source) throws IOException {

    }

    @Override
    public void onRespError(int source) {
        ViewHandler.toastShow(MResetMemberPwdActivity.this,Ref.CANT_CONNECT_INTERNET);
    }

    @Override
    public void onReqFailure(Object object, int source) {
        ViewHandler.toastShow(MResetMemberPwdActivity.this, Ref.CANT_CONNECT_INTERNET);
    }

    @Override
    public void onRespSessionExpired(int source) {
        ViewHandler.alertShowAndExitApp(this);
    }
}
