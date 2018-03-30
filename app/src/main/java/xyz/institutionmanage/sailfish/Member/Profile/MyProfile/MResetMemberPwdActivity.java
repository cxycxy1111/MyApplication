package xyz.institutionmanage.sailfish.Member.Profile.MyProfile;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import xyz.institutionmanage.sailfish.R;
import xyz.institutionmanage.sailfish.Util.ActivityManager;
import xyz.institutionmanage.sailfish.Util.BaseActivity;
import xyz.institutionmanage.sailfish.Util.Enum.EnumRespStatType;
import xyz.institutionmanage.sailfish.Util.Enum.EnumRespType;
import xyz.institutionmanage.sailfish.Util.MethodTool;
import xyz.institutionmanage.sailfish.Util.NetUtil;
import xyz.institutionmanage.sailfish.Util.Ref;

public class MResetMemberPwdActivity
        extends BaseActivity {

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
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.title_a_reset_member_pwd);
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
        String url = "/mResetPassword?newpwd=" + newpwd;
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(MResetMemberPwdActivity.this, Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                EnumRespType respType = EnumRespType.dealWithResponse(resp);
                switch (respType) {
                    case RESP_ERROR:
                        MethodTool.showToast(MResetMemberPwdActivity.this,Ref.CANT_CONNECT_INTERNET);
                        break;
                    case RESP_STAT:
                        EnumRespStatType respStatType = EnumRespStatType.dealWithRespStat(resp);
                        switch (respStatType) {
                            case NSR:
                                MethodTool.showToast(MResetMemberPwdActivity.this,Ref.STAT_NSR);
                                MResetMemberPwdActivity.this.finish();
                                ActivityManager.removeAllActivity();
                                break;
                            case EXE_SUC:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog_exit.show();
                                    }
                                });
                                break;
                            case EXE_FAIL:
                                MethodTool.showToast(MResetMemberPwdActivity.this,Ref.OP_MODIFY_FAIL);
                                break;
                            case SESSION_EXPIRED:
                                MethodTool.showExitAppAlert(MResetMemberPwdActivity.this);break;
                            case AUTHORIZE_FAIL:
                                MethodTool.showAuthorizeFailToast(MResetMemberPwdActivity.this);
                                break;
                            default:
                                break;
                        }
                        break;
                    default:break;
                }
            }
        };
        NetUtil.sendHttpRequest(MResetMemberPwdActivity.this,url,callback);
        return true;
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
                ActivityManager.removeAllActivity();

            }
        });
        dialog_exit = dialog.create();
    }
}
