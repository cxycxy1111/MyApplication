package xyz.institutionmanager.sailfish.Shopmember.Course.CoursePlan;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.alfred.alfredtools.*;
import xyz.institutionmanager.sailfish.Adapter.RVSimpleAdapterWithCheck;
import xyz.institutionmanager.sailfish.R;
import xyz.institutionmanager.sailfish.Util.Ref;
import xyz.institutionmanager.sailfish.Util.SharePreferenceManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddCoursePlanTeacherActivity extends BaseActivity implements HttpResultListener {

    private static final int REQUEST_QueryShopmemberList = 1;
    private static final int REQUEST_CoursePlanTeacherModify = 2;
    private String s_id,sm_id,cp_id;
    private String [] keys_teacher_list = new String[] {"id","name","type"};
    private Map<Integer,Boolean> map_status = new HashMap<>();
    private List<String> list_teacher_name = new ArrayList<>();
    private List<Map<String,String>> mapList_teacher_full = new ArrayList<>();
    private RecyclerView rv_teacher;
    private RVSimpleAdapterWithCheck adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_plan_add_teacher);
        initToolbar();
        initData();
        initTeacherData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_courseplan_teacher,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_add_courseplan_teacher:
                saveCoursePlanTeacher();
                break;
            case android.R.id.home:
                AddCoursePlanTeacherActivity.this.finish();
                break;
            default:break;
        }
        return true;
    }

    private void initData() {
        s_id = SharePreferenceManager.getSharePreferenceValue(
                AddCoursePlanTeacherActivity.this,"sasm","s_id",2);
        sm_id = SharePreferenceManager.getSharePreferenceValue(
                AddCoursePlanTeacherActivity.this,"sasm","sm_id",2);
        cp_id = getIntent().getStringExtra("cp_id");
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("设置教师");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rv_teacher = (RecyclerView)findViewById(R.id.rv_a_add_course_plan_teacher);
    }

    private void initTeacherData() {
        String url = "/QueryShopmemberList?type=0";
        HttpCallback callback = new HttpCallback(this,this,REQUEST_QueryShopmemberList);
        NetUtil.reqSendGet(this,url,callback);
    }

    /**
     * 保存教师设置
     */
    private void saveCoursePlanTeacher() {
        String url = "/CoursePlanTeacherModify?m=" + initReqData();
        HttpCallback callback = new HttpCallback(this,this,REQUEST_CoursePlanTeacherModify);
        NetUtil.reqSendGet(this,url,callback);
    }

    //拼接
    private String initReqData() {
        StringBuilder builder = new StringBuilder();
        map_status = adapter.getCheckStatusMap();
        for (int i = 0;i < mapList_teacher_full.size();i++) {
            boolean b = map_status.get(i);
            if (b) {
                builder.append("1").append("_").append(cp_id).append("_").append(String.valueOf(mapList_teacher_full.get(i).get("id"))).append("-");
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
                    ViewHandler.toastShow(AddCoursePlanTeacherActivity.this,"暂无教师");break;
                case STATUS_AUTHORIZE_FAIL:
                    ViewHandler.exitAcitivityDueToAuthorizeFail(AddCoursePlanTeacherActivity.this);
                    break;
                default:break;
            }

        }else if (source == REQUEST_CoursePlanTeacherModify) {
            NetRespStatType respStatType = NetRespStatType.dealWithRespStat(body);
            switch (respStatType) {
                case SUCCESS:
                    ViewHandler.toastShow(AddCoursePlanTeacherActivity.this,Ref.OP_SUCCESS);
                    //返回码
                    setResult(Ref.RESULTCODE_ADD);
                    AddCoursePlanTeacherActivity.this.finish();
                    break;
                case FAIL:
                    ViewHandler.toastShow(AddCoursePlanTeacherActivity.this,Ref.OP_FAIL);
                    break;
                case PARTYLY_FAIL:
                    ViewHandler.toastShow(AddCoursePlanTeacherActivity.this,Ref.OP_PARTLY_FAIL);
                    setResult(Ref.RESULTCODE_ADD);
                    AddCoursePlanTeacherActivity.this.finish();
                    break;
                case STATUS_AUTHORIZE_FAIL:
                    ViewHandler.exitAcitivityDueToAuthorizeFail(AddCoursePlanTeacherActivity.this);
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
            LinearLayoutManager llm_rv_teacher = new LinearLayoutManager(AddCoursePlanTeacherActivity.this,LinearLayoutManager.VERTICAL,false);
            rv_teacher.setAdapter(adapter);
            rv_teacher.setLayoutManager(llm_rv_teacher);
        }else if (source == REQUEST_CoursePlanTeacherModify) {

        }else {

        }
    }

    @Override
    public void onRespError(int source) {
        ViewHandler.toastShow(AddCoursePlanTeacherActivity.this,Ref.UNKNOWN_ERROR);
    }

    @Override
    public void onReqFailure(Object object, int source) {

    }

    @Override
    public void onRespSessionExpired(int source) {
        ViewHandler.alertShowAndExitApp(AddCoursePlanTeacherActivity.this);
    }
}
