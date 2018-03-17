package xyz.institutionmanage.sailfish.Member.Discovery.Course;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import xyz.institutionmanage.sailfish.R;
import xyz.institutionmanage.sailfish.Util.ActivityManager;
import xyz.institutionmanage.sailfish.Util.BaseActivity;
import xyz.institutionmanage.sailfish.Util.Enum.EnumRespStatType;
import xyz.institutionmanage.sailfish.Util.Enum.EnumRespType;
import xyz.institutionmanage.sailfish.Util.JsonHandler;
import xyz.institutionmanage.sailfish.Util.MethodTool;
import xyz.institutionmanage.sailfish.Util.NetUtil;
import xyz.institutionmanage.sailfish.Util.Ref;

public class MCourseDetailPrivateActivity extends BaseActivity {

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
        initData();
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

    private void initData(){
        String url = "/mCoursePrivateDetail?c_id=" + c_id;
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(MCourseDetailPrivateActivity.this, Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                switch (EnumRespType.dealWithResponse(resp)) {
                    case RESP_ERROR:
                        MethodTool.showToast(MCourseDetailPrivateActivity.this,Ref.UNKNOWN_ERROR);
                        break;
                    case RESP_STAT:
                        switch (EnumRespStatType.dealWithRespStat(resp)) {
                            case SESSION_EXPIRED:
                                MethodTool.showToast(MCourseDetailPrivateActivity.this,Ref.ALERT_SESSION_EXPIRED);
                                ActivityManager.removeAllActivity();
                                break;
                            case NSR:
                                MethodTool.showToast(MCourseDetailPrivateActivity.this,"暂无该课程");
                                MCourseDetailPrivateActivity.this.finish();
                                break;
                            default:break;
                        }
                        break;
                    case RESP_MAPLIST:
                        final String [] keys = new String[]{"id","type","course_name","shopmember_name","member_name","total_times","rest_times","expired_time","actual_cost"};
                        arrayList = JsonHandler.strToListMap(resp,keys);
                        final Map<String,String> map = arrayList.get(0);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
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
                        });
                }
            }
        };
        NetUtil.sendHttpRequest(MCourseDetailPrivateActivity.this,url,callback);
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
}
