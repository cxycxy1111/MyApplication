package xyz.instmgr.sailfish.Shopmember.Member;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import com.alfred.alfredtools.*;
import xyz.instmgr.sailfish.R;
import xyz.instmgr.sailfish.Util.Ref;

import java.io.IOException;

public class ResetMemberPwdActivity
        extends BaseActivity
        implements HttpResultListener {

    private Toolbar toolbar;
    private EditText et_newpwd,et_newpwd_repeat;
    private String newpwd,newpwd_repeat;
    private long s_id,sm_id,m_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_reset_pwd);
        initView();
    }

    private void initView() {
        initToolbar();
        initEditText();
    }

    private void initToolbar() {
        toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.title_a_reset_member_pwd);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initEditText() {
        et_newpwd = (EditText)findViewById(R.id.et_newpwd_a_reset_pwd);
        et_newpwd_repeat = (EditText)findViewById(R.id.et_repeat_newpwd_a_reset_pwd);
    }

    private void initData() {
        s_id = getSharedPreferences("sasm",MODE_PRIVATE).getLong("s_id",0);
        sm_id = getSharedPreferences("sasm",MODE_PRIVATE).getLong("sm_id",0);
        m_id = getIntent().getLongExtra("m_id",0);
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
                boolean b = dealWithSubmitAction();
                if (!b) {
                    break;
                }else {
                    break;
                }
            case android.R.id.home:
                this.finish();
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
            case NSR:
                ViewHandler.toastShow(ResetMemberPwdActivity.this,Ref.STAT_NSR);
                ResetMemberPwdActivity.this.finish();
                break;
            case SUCCESS:
                ViewHandler.toastShow(ResetMemberPwdActivity.this,Ref.OP_MODIFY_SUCCESS);
                break;
            case FAIL:
                ViewHandler.toastShow(ResetMemberPwdActivity.this,Ref.OP_MODIFY_FAIL);
                break;
            case STATUS_AUTHORIZE_FAIL:
                ViewHandler.exitAcitivityDueToAuthorizeFail(ResetMemberPwdActivity.this);
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
        ViewHandler.toastShow(ResetMemberPwdActivity.this, Ref.CANT_CONNECT_INTERNET);
    }

    @Override
    public void onReqFailure(Object object, int source) {
        ViewHandler.toastShow(ResetMemberPwdActivity.this,Ref.UNKNOWN_ERROR);
    }

    @Override
    public void onRespSessionExpired(int source) {
        ViewHandler.alertShowAndExitApp(this);
    }

    private boolean dealWithSubmitAction() {
        initData();
        if (newpwd == null | newpwd.equals("")) {
            Toast.makeText(this,"密码不能为空",Toast.LENGTH_LONG).show();
            return false;
        }
        if ((newpwd_repeat == null | newpwd_repeat.equals("")) && (newpwd != null | !newpwd.equals(""))) {
            Toast.makeText(this,"请重复输入密码",Toast.LENGTH_LONG).show();
            return false;
        }
        if (!newpwd_repeat.equals(newpwd)) {
            Toast.makeText(this,"两次输入的密码不一致，请核对",Toast.LENGTH_LONG).show();
            return false;
        }
        String url = "/MemberResetPassword?m_id=" + m_id + "&newpwd=" + newpwd;
        HttpCallback callback = new HttpCallback(this,this,0);
        NetUtil.reqSendGet(this,url,callback);
        return true;
    }
}
