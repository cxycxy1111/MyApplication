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

public class AddNewClassroomActivity
        extends BaseActivity
        implements HttpResultListener {

    private long sm_id,s_id;
    private long cr_id;
    private String cr_name;
    private EditText et_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom_add);
        initData();
        initToolBar();
        initView();
    }

    @Override
    public void onRespStatus(String body, int source) {
        switch (NetRespStatType.dealWithRespStat(body)) {
            case DUPLICATE:
                ViewHandler.toastShow(AddNewClassroomActivity.this,"教室名重复");
                break;
            case FAIL:
                ViewHandler.toastShow(AddNewClassroomActivity.this,"新增失败");
                break;
            case NSR:
                ViewHandler.toastShow(AddNewClassroomActivity.this,"机构不存在");
                finish();
                break;
            case STATUS_AUTHORIZE_FAIL:
                ViewHandler.exitAcitivityDueToAuthorizeFail(AddNewClassroomActivity.this);
                break;
            default:break;
        }
    }

    @Override
    public void onRespMapList(String body, int source) throws IOException {
        ViewHandler.toastShow(AddNewClassroomActivity.this,"新增成功");
        String [] k = new String[]{"id"};
        cr_id = Long.parseLong(JsonUtil.strToListMap(body,k).get(0).get(k[0]));
        Intent intent = new Intent(AddNewClassroomActivity.this,ClassroomListActivity.class);
        intent.putExtra("cr_id",cr_id).
                putExtra("cr_name",cr_name);
        setResult(Ref.RESULTCODE_ADD,intent);
        finish();
    }

    @Override
    public void onRespError(int source) {

    }

    @Override
    public void onReqFailure(Object object, int source) {
        ViewHandler.toastShow(AddNewClassroomActivity.this, Ref.CANT_CONNECT_INTERNET);
    }

    @Override
    public void onRespSessionExpired(int source) {
        ViewHandler.alertShowAndExitApp(this);
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
                saveClassroom();
                break;
            default:
                break;
        }
        return true;
    }

    private void saveClassroom() {
        cr_name = et_name.getText().toString();
        String url = "/ClassroomAdd?name=" + cr_name;
        HttpCallback callback = new HttpCallback(this,this,0);
        NetUtil.reqSendGet(this,url,callback);
    }
}
