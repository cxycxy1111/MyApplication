package xyz.instmgr.sailfish.Shopmember.Profile.Classroom;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import com.alfred.alfredtools.*;
import xyz.instmgr.sailfish.R;
import xyz.instmgr.sailfish.Util.Ref;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class ClassroomDetailActivity extends BaseActivity implements HttpResultListener {

    private long sm_id,s_id;
    private long cr_id;
    private String cr_name,req_cr_name,new_name;
    private String [] keys = new String[] {"id","name"};
    private EditText et_name;
    private int position;
    private static final int REQUEST_ClassroomDetailQuery = 1;
    private static final int REQUEST_ClassroomModify = 2;
    private static final int REQUEST_ClassroomDelete = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom_detail);
        initData();
        initToolbar();
        initEditText();
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

    @Override
    public void onRespStatus(String body, int source) {
        if (source == REQUEST_ClassroomDetailQuery) {
            NetRespStatType respStatType = NetRespStatType.dealWithRespStat(body);
            switch (respStatType) {
                case NSR:
                    ViewHandler.toastShow(ClassroomDetailActivity.this,Ref.OP_NSR);
                    ClassroomDetailActivity.this.finish();
                    break;
                case STATUS_AUTHORIZE_FAIL:
                    ViewHandler.exitAcitivityDueToAuthorizeFail(ClassroomDetailActivity.this);
                    break;
                default:break;
            }
        }else if (source == REQUEST_ClassroomModify) {
            switch (NetRespStatType.dealWithRespStat(body)) {
                case NSR:
                    ViewHandler.toastShow(ClassroomDetailActivity.this,"机构或课室不存在");
                    break;
                case SUCCESS:
                    Intent intent = new Intent(ClassroomDetailActivity.this,ClassroomListActivity.class);
                    intent.putExtra("new_name",new_name);
                    intent.putExtra("pos",position);
                    intent.putExtra("cr_id",cr_id);
                    setResult(Ref.RESULTCODE_UPDATE,intent);
                    finish();
                    break;
                case FAIL:
                    ViewHandler.toastShow(ClassroomDetailActivity.this,"保存失败");
                    break;
                case STATUS_AUTHORIZE_FAIL:
                    ViewHandler.exitAcitivityDueToAuthorizeFail(ClassroomDetailActivity.this);
                    break;
                default:
                    break;
            }
        }else if (source == REQUEST_ClassroomDelete) {
            switch (NetRespStatType.dealWithRespStat(body)) {
                case SUCCESS:
                    Intent intent = new Intent(ClassroomDetailActivity.this,ClassroomListActivity.class);
                    int i = position;
                    intent.putExtra("pos",i);
                    setResult(Ref.RESULTCODE_DELETE,intent);
                    finish();
                    break;
                case FAIL:
                    ViewHandler.toastShow(ClassroomDetailActivity.this,"删除失败");
                    break;
                case NSR:
                    ViewHandler.toastShow(ClassroomDetailActivity.this,"未找到课室");
                    finish();
                    break;
                case STATUS_AUTHORIZE_FAIL:
                    ViewHandler.exitAcitivityDueToAuthorizeFail(ClassroomDetailActivity.this);
                    break;
                default:break;
            }
        }else {

        }
    }

    @Override
    public void onRespMapList(String body, int source) throws IOException {
        if (source == REQUEST_ClassroomDetailQuery) {
            ArrayList<Map<String,String>> list = JsonUtil.strToListMap(body,keys);
            req_cr_name = list.get(0).get("name");
            et_name.setText(req_cr_name);
        }else if (source == REQUEST_ClassroomModify) {

        }else if (source == REQUEST_ClassroomDelete) {

        }else {

        }
    }

    @Override
    public void onRespError(int source) {
        ViewHandler.toastShow(ClassroomDetailActivity.this,Ref.UNKNOWN_ERROR);
    }

    @Override
    public void onReqFailure(Object object, int source) {
        ViewHandler.toastShow(ClassroomDetailActivity.this, Ref.CANT_CONNECT_INTERNET);
    }

    @Override
    public void onRespSessionExpired(int source) {
        ViewHandler.alertShowAndExitApp(ClassroomDetailActivity.this);
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
        HttpCallback callback = new HttpCallback(this,this,REQUEST_ClassroomDetailQuery);
        NetUtil.reqSendGet(this,url,callback);
    }

    private void save() {
        new_name = et_name.getText().toString();
        String url = "/ClassroomModify?cr_id=" + cr_id + "&name=" + new_name;
        HttpCallback callback = new HttpCallback(this,this,REQUEST_ClassroomModify);
        NetUtil.reqSendGet(this,url,callback);
    }

    private void delete() {
        String url = "/ClassroomDelete?cr_id=" + cr_id;
        HttpCallback callback = new HttpCallback(this,this,REQUEST_ClassroomDelete);
        NetUtil.reqSendGet(this,url,callback);
    }
}
