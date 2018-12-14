package xyz.institutionmanager.sailfish.Member.Discovery.Course;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.alfred.alfredtools.*;
import xyz.institutionmanager.sailfish.R;
import xyz.institutionmanager.sailfish.Util.Ref;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class MCourseDetailPrivateActivity extends BaseActivity implements HttpResultListener {

    private long c_id;
    private String c_name;
    private Toolbar toolbar;
    private TextView tv_type,tv_course_name,tv_teacher,tv_student,tv_total_times,tv_rest_times,tv_expired_time,tv_actual_cost;
    private ArrayList<Map<String,String>> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mcourse_detail_private);
        initIntent();
        dealWithToolbar();
        String url = "/mCoursePrivateDetail?c_id=" + c_id;
        HttpCallback callback = new HttpCallback(this,this,0);
        NetUtil.reqSendGet(this,url,callback);
    }

    private void initIntent() {
        c_id = getIntent().getLongExtra("c_id",0);
        c_name = getIntent().getStringExtra("c_name");
    }

    private void dealWithToolbar() {
        toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(c_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_type = (TextView)findViewById(R.id.tv_hint_type_a_m_course_detail_private);
        tv_course_name = (TextView)findViewById(R.id.tv_hint_course_name_a_m_course_detail_private);
        tv_teacher = (TextView)findViewById(R.id.tv_hint_teacher_a_m_course_detail_private);
        tv_student = (TextView)findViewById(R.id.tv_hint_student_a_m_course_detail_private);
        tv_total_times = (TextView)findViewById(R.id.tv_hint_total_times_a_m_course_detail_private);
        tv_rest_times = (TextView)findViewById(R.id.tv_hint_rest_times_a_m_course_detail_private);
        tv_expired_time = (TextView)findViewById(R.id.tv_hint_invalidtime_a_m_course_detail_private);
        tv_actual_cost = (TextView)findViewById(R.id.tv_hint_total_cost_a_m_course_detail_private);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            default:break;
        }
        return true;
    }

    @Override
    public void onRespStatus(String body, int source) {
        switch (NetRespStatType.dealWithRespStat(body)) {
            case NSR:
                ViewHandler.toastShow(MCourseDetailPrivateActivity.this,"暂无该课程");
                MCourseDetailPrivateActivity.this.finish();
                break;
            default:break;
        }
    }

    @Override
    public void onRespMapList(String body, int source) throws IOException {
        final String [] keys = new String[]{"id","type","course_name","shopmember_name","member_name","total_times","rest_times","expired_time","actual_cost"};
        arrayList = JsonUtil.strToListMap(body,keys);
        final Map<String,String> map = arrayList.get(0);

        tv_type.setText("私教课");
        tv_course_name.setText(map.get(keys[2]));
        tv_teacher.setText(map.get(keys[3]));
        tv_student.setText(map.get(keys[4]));
        tv_total_times.setText(String.valueOf(map.get(keys[5]))+"次");
        tv_rest_times.setText(String.valueOf(map.get(keys[6]))+"次");
        String expired_time = map.get(keys[7]);
        expired_time = expired_time.substring(0,expired_time.length()-11);
        tv_expired_time.setText(expired_time);
        tv_actual_cost.setText(String.valueOf(map.get(keys[8]))+"元");
    }

    @Override
    public void onRespError(int source) {
        ViewHandler.toastShow(MCourseDetailPrivateActivity.this,Ref.UNKNOWN_ERROR);
    }

    @Override
    public void onReqFailure(Object object, int source) {
        ViewHandler.toastShow(MCourseDetailPrivateActivity.this, Ref.CANT_CONNECT_INTERNET);
    }

    @Override
    public void onRespSessionExpired(int source) {
        ViewHandler.alertShowAndExitApp(this);
    }
}
