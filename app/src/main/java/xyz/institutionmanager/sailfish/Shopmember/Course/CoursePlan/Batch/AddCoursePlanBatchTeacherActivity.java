package xyz.institutionmanager.sailfish.Shopmember.Course.CoursePlan.Batch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.alfred.alfredtools.*;
import xyz.institutionmanager.sailfish.Adapter.RVSimpleAdapterWithCheck;
import xyz.institutionmanager.sailfish.R;
import xyz.institutionmanager.sailfish.Util.Ref;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddCoursePlanBatchTeacherActivity extends BaseActivity implements View.OnClickListener,HttpResultListener {

    private static final int REQUEST_QueryShopmemberList = 1;
    private static final int REQUEST_coursePlanTeacherModifyBatch = 2;
    private String cr_id,c_id,c_type,cp_time,cp_last_time,str_cp_id;
    private String [] keys_teacher_list = new String[] {"id","name","type"};
    private Button button;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private List<String> list_teacher_name = new ArrayList<>();
    private List<Map<String,String>> mapList_teacher_full = new ArrayList<>();
    private Map<Integer,Boolean> map_status = new HashMap<>();
    private RVSimpleAdapterWithCheck adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course_plan_batch_teacher);
        initDataFromPreviousActivity();
        initViewComponent();
        initDataFromWeb();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next_add_course_plan_batch_teacher:
                saveCoursePlanTeacher();
                break;
            default:break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(Ref.RESULTCODE_ADD);
                AddCoursePlanBatchTeacherActivity.this.finish();
                break;
            default:break;
        }
        return true;
    }

    private void initDataFromPreviousActivity() {
        Intent data = getIntent();
        cr_id = data.getStringExtra("cr_id");
        c_id = data.getStringExtra("c_id");
        c_type = data.getStringExtra("c_type");
        cp_time = data.getStringExtra("cp_time");
        cp_last_time = data.getStringExtra("cp_last_time");
        str_cp_id = data.getStringExtra("cp_id");
    }

    private void initViewComponent() {
        toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        toolbar.setTitle("选择教师");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = (RecyclerView)findViewById(R.id.rv_a_add_course_plan_batch_teacher);
        button = (Button)findViewById(R.id.btn_next_add_course_plan_batch_teacher);
        button.setOnClickListener(this);
    }

    private void initDataFromWeb() {
        String url = "/QueryShopmemberList?type=0";
        HttpCallback callback = new HttpCallback(this,this,REQUEST_QueryShopmemberList);
        NetUtil.reqSendGet(this,url,callback);
    }

    /**
     * 保存教师设置
     */
    private void saveCoursePlanTeacher() {
        String url = "/coursePlanTeacherModifyBatch";
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("cp_id",str_cp_id);
        hashMap.put("tea_id",initReqData());
        HttpCallback callback = new HttpCallback(this,this,REQUEST_coursePlanTeacherModifyBatch);
        NetUtil.reqSendPost(this,url,hashMap,callback);
    }

    //拼接
    private String initReqData() {
        StringBuilder builder = new StringBuilder();
        map_status = adapter.getCheckStatusMap();
        for (int i = 0;i < mapList_teacher_full.size();i++) {
            boolean b = map_status.get(i);
            if (b) {
                builder.append(String.valueOf(mapList_teacher_full.get(i).get("id"))).append(",");
            }
        }
        String str = builder.toString();
        return str.substring(0,str.length()-1);
    }

    @Override
    public void onRespStatus(String body, int source) {
        if (source == REQUEST_QueryShopmemberList) {
            NetRespStatType respStatType = NetRespStatType.dealWithRespStat(body);
            switch (respStatType) {
                case NSR:
                    ViewHandler.toastShow(AddCoursePlanBatchTeacherActivity.this,"暂无教师");
                    break;
                case STATUS_AUTHORIZE_FAIL:
                    ViewHandler.exitAcitivityDueToAuthorizeFail(AddCoursePlanBatchTeacherActivity.this);
                    break;
                default:break;
            }
        }else if (source == REQUEST_coursePlanTeacherModifyBatch) {
            NetRespStatType respStatType = NetRespStatType.dealWithRespStat(body);
            switch (respStatType) {
                case SUCCESS:
                    ViewHandler.toastShow(AddCoursePlanBatchTeacherActivity.this,Ref.OP_SUCCESS);
                    //返回码
                    setResult(Ref.RESULTCODE_ADD);
                    AddCoursePlanBatchTeacherActivity.this.finish();
                    break;
                case FAIL:
                    ViewHandler.toastShow(AddCoursePlanBatchTeacherActivity.this,Ref.OP_FAIL);
                    break;
                case PARTYLY_FAIL:
                    ViewHandler.toastShow(AddCoursePlanBatchTeacherActivity.this,Ref.OP_PARTLY_FAIL);
                    setResult(Ref.RESULTCODE_ADD);
                    AddCoursePlanBatchTeacherActivity.this.finish();
                    break;
                case STATUS_AUTHORIZE_FAIL:
                    ViewHandler.exitAcitivityDueToAuthorizeFail(AddCoursePlanBatchTeacherActivity.this);
                    break;
                default:break;
            }
        }else {

        }
    }

    @Override
    public void onRespMapList(String body, int source) throws IOException {
        if (source == REQUEST_QueryShopmemberList) {
            mapList_teacher_full = JsonUtil.strToListMap(body,keys_teacher_list);
            for (int i = 0;i < mapList_teacher_full.size();i++) {
                list_teacher_name.add(String.valueOf(mapList_teacher_full.get(i).get(keys_teacher_list[1])));
            }
            adapter = new RVSimpleAdapterWithCheck(list_teacher_name);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AddCoursePlanBatchTeacherActivity.this,LinearLayoutManager.VERTICAL,false);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(linearLayoutManager);
        }else if (source == REQUEST_coursePlanTeacherModifyBatch) {

        }else {

        }
    }

    @Override
    public void onRespError(int source) {
        ViewHandler.toastShow(AddCoursePlanBatchTeacherActivity.this,Ref.UNKNOWN_ERROR);
    }

    @Override
    public void onReqFailure(Object object, int source) {

    }

    @Override
    public void onRespSessionExpired(int source) {
        ViewHandler.alertShowAndExitApp(this);
    }
}
