package xyz.institutionmanage.sailfish.Shopmember.Profile.Classroom;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import xyz.institutionmanage.sailfish.R;
import xyz.institutionmanage.sailfish.Util.BaseActivity;
import xyz.institutionmanage.sailfish.Util.Enum.EnumRespStatType;
import xyz.institutionmanage.sailfish.Util.Enum.EnumRespType;
import xyz.institutionmanage.sailfish.Util.JsonHandler;
import xyz.institutionmanage.sailfish.Util.MethodTool;
import xyz.institutionmanage.sailfish.Util.NetUtil;
import xyz.institutionmanage.sailfish.Util.Ref;

public class ClassroomDetailActivity extends BaseActivity {

    private long sm_id,s_id;
    private long cr_id;
    private String cr_name,req_cr_name;
    private String [] keys = new String[] {"id","name"};
    private EditText et_name;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom_detail);
        initData();
        initToolbar();
        initEditText();
    }

    private void initData() {
        SharedPreferences preferences = getSharedPreferences("sasm",MODE_PRIVATE);
        Intent intent = getIntent();
        sm_id = preferences.getLong("sm_id",0);
        s_id = preferences.getLong("s_id",0);
        cr_id = Long.parseLong(intent.getStringExtra("cr_id"));
        cr_name = intent.getStringExtra("cr_name");
        position = intent.getIntExtra("pos",0);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(cr_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initEditText() {
        et_name = (EditText)findViewById(R.id.et_name_a_classroom_detail);
        String url = "/ClassroomDetailQuery?cr_id=" + cr_id;
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(ClassroomDetailActivity.this, Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                EnumRespType respType = EnumRespType.dealWithResponse(resp);
                switch (respType) {
                    case RESP_STAT:
                        EnumRespStatType respStatType = EnumRespStatType.dealWithRespStat(resp);
                        switch (respStatType) {
                            case NSR:
                                MethodTool.showToast(ClassroomDetailActivity.this,Ref.OP_NSR);
                                ClassroomDetailActivity.this.finish();
                                break;
                            case SESSION_EXPIRED:
                                MethodTool.showExitAppAlert(ClassroomDetailActivity.this);
                                break;
                            case AUTHORIZE_FAIL:
                                MethodTool.exitAcitivityDueToAuthorizeFail(ClassroomDetailActivity.this);
                                break;
                            default:break;
                        }
                        break;
                    case RESP_MAPLIST:
                        ArrayList<Map<String,String>> list = JsonHandler.strToListMap(resp,keys);
                        req_cr_name = list.get(0).get("name");
                        setEditTextText();
                        break;
                    case RESP_ERROR:
                        MethodTool.showToast(ClassroomDetailActivity.this,Ref.UNKNOWN_ERROR);
                        break;
                    default:break;
                }
            }
        };
        NetUtil.sendHttpRequest(ClassroomDetailActivity.this,url,callback);
    }

    private void setEditTextText() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                et_name.setText(req_cr_name);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_classroom_detail,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                this.finish();
                break;
            case R.id.save_classroom_detail:
                save();
                break;
            case R.id.delete_classroom_detail:
                delete();
                break;
            default:
                break;
        }
        return true;
    }

    private void save() {
        final String new_name = et_name.getText().toString();
        String url = "/ClassroomModify?cr_id=" + cr_id + "&name=" + new_name;
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(ClassroomDetailActivity.this, Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                switch (EnumRespType.dealWithResponse(resp)) {
                    case RESP_STAT:
                        switch (EnumRespStatType.dealWithRespStat(resp)) {
                            case NSR:
                                MethodTool.showToast(ClassroomDetailActivity.this,"机构或课室不存在");
                                break;
                            case EXE_SUC:
                                Intent intent = new Intent(ClassroomDetailActivity.this,ClassroomListActivity.class);
                                intent.putExtra("new_name",new_name);
                                intent.putExtra("pos",position);
                                intent.putExtra("cr_id",cr_id);
                                setResult(Ref.RESULTCODE_UPDATE,intent);
                                finish();
                                break;
                            case EXE_FAIL:
                                MethodTool.showToast(ClassroomDetailActivity.this,"保存失败");
                                break;
                            case SESSION_EXPIRED:
                                MethodTool.showExitAppAlert(ClassroomDetailActivity.this);
                                break;
                            case AUTHORIZE_FAIL:
                                MethodTool.showAuthorizeFailToast(ClassroomDetailActivity.this);
                                break;
                            default:
                                break;
                        }
                        break;
                    default:break;
                }
            }
        };
        NetUtil.sendHttpRequest(ClassroomDetailActivity.this,url,callback);
    }

    private void delete() {
        String url = "/ClassroomDelete?cr_id=" + cr_id;
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(ClassroomDetailActivity.this, Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                switch (EnumRespType.dealWithResponse(resp)) {
                    case RESP_STAT:
                        switch (EnumRespStatType.dealWithRespStat(resp)) {
                            case EXE_SUC:
                                Intent intent = new Intent(ClassroomDetailActivity.this,ClassroomListActivity.class);
                                int i = position;
                                intent.putExtra("pos",i);
                                setResult(Ref.RESULTCODE_DELETE,intent);
                                finish();
                                break;
                            case EXE_FAIL:
                                MethodTool.showToast(ClassroomDetailActivity.this,"删除失败");
                                break;
                            case NSR:
                                MethodTool.showToast(ClassroomDetailActivity.this,"未找到课室");
                                finish();
                                break;
                            case SESSION_EXPIRED:
                                MethodTool.showExitAppAlert(ClassroomDetailActivity.this);
                                break;
                            case AUTHORIZE_FAIL:
                                MethodTool.showAuthorizeFailToast(ClassroomDetailActivity.this);
                                break;
                            default:break;
                        }
                        break;
                    case RESP_ERROR:
                        MethodTool.showToast(ClassroomDetailActivity.this,Ref.UNKNOWN_ERROR);
                        break;
                    default:break;
                }
            }
        };
        NetUtil.sendHttpRequest(ClassroomDetailActivity.this,url,callback);
    }
}