package xyz.institutionmanager.sailfish.Member.Discovery.CoursePlan;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.alfred.alfredtools.*;
import xyz.institutionmanager.sailfish.R;
import xyz.institutionmanager.sailfish.Util.Ref;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class MCoursePlanDetailPrivateActivity extends BaseActivity implements View.OnClickListener,HttpResultListener {

    private static final int REQUEST_CoursePlanPrivateDetailQuery = 1;
    private static final int REQUEST_Attend = 2;
    private String cp_id,c_name,start_time;
    private Button btn_attend;
    private Toolbar toolbar;
    private TextView tv_state,tv_course_name,tv_classroom,tv_teacher,tv_student,tv_last_time,tv_start_time;
    private String[] keys = new String[] {"id","c_name","tea_name","stu_name","start_time","end_time","last_time","cr_id","cr_name"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mcourse_plan_detail_private);
        initDataFromPreviousActivity();
        initView();
        initDataFromWebsite();
    }


    private void initDataFromPreviousActivity() {
        cp_id = getIntent().getStringExtra("cp_id");
        c_name = getIntent().getStringExtra("c_name");
        start_time = getIntent().getStringExtra("start_time");
        start_time = start_time.substring(0,start_time.length()-5);
    }

    private void initView() {
        toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        tv_state = (TextView)findViewById(R.id.tv_hint_bookstate_a_m_course_plan_detail_private);
        tv_course_name = (TextView)findViewById(R.id.tv_hint_course_name_a_m_course_plan_detail_private);
        tv_classroom = (TextView)findViewById(R.id.tv_hint_classroom_name_a_m_course_plan_detail_private);
        tv_teacher = (TextView)findViewById(R.id.tv_hint_tea_name_a_m_course_plan_detail_private);
        tv_student = (TextView)findViewById(R.id.tv_hint_stu_name_a_m_course_plan_detail_private);
        tv_last_time = (TextView)findViewById(R.id.tv_hint_last_time_a_m_course_plan_detail_private);
        tv_start_time = (TextView)findViewById(R.id.tv_start_date_a_m_course_plan_detail_private);
        btn_attend = (Button)findViewById(R.id.btn_attend_a_m_course_plan_detail_private);
        btn_attend.setOnClickListener(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(c_name);
        getSupportActionBar().setSubtitle(start_time);
    }

    public void initDataFromWebsite() {
        String url = "/mCoursePlanPrivateDetailQuery?cp_id=" + cp_id;
        HttpCallback callback = new HttpCallback(this,this,REQUEST_CoursePlanPrivateDetailQuery);
        NetUtil.reqSendGet(this,url,callback);
    }

    private void updateView(final Map<String,String> map) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        tv_course_name.setText(map.get("c_name"));
        tv_classroom.setText(map.get("cr_name"));
        Date start_date = null;
        Date end_date = null;
        try {
            start_date = simpleDateFormat.parse(map.get("start_time"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            end_date = simpleDateFormat.parse(map.get("end_time"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long last_time = (end_date.getTime() - start_date.getTime())/60/1000;
        String str_start_time = map.get("start_time");

        tv_start_time.setText(str_start_time.substring(0,str_start_time.length()-5));
        tv_last_time.setText(String.valueOf(last_time) + "分钟");
        tv_teacher.setText(map.get("tea_name"));
        tv_student.setText(map.get("stu_name"));
        if (String.valueOf(map.get("isAttend")).equals("1")) {
            tv_state.setText("已签到");
            btn_attend.setVisibility(View.GONE);
        }else {
            tv_state.setText("未签到");
            btn_attend.setVisibility(View.VISIBLE);
        }
    }

    private void attend() {
        String url = "/mAttend?cp_id=" + cp_id;
        HttpCallback callback = new HttpCallback(this,this,REQUEST_Attend);
        NetUtil.reqSendGet(this,url,callback);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                MCoursePlanDetailPrivateActivity.this.finish();
                break;
            default:break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_attend_a_m_course_plan_detail_private:
                getProgressBar(this).setVisibility(View.VISIBLE);
                attend();
                break;
            default:break;
        }
    }



    @Override
    public void onRespStatus(String body, int source) {
        ViewHandler.progressBarHide(MCoursePlanDetailPrivateActivity.this);
        if (source == REQUEST_CoursePlanPrivateDetailQuery) {
            switch (NetRespStatType.dealWithRespStat(body)) {
                case NSR:
                    ViewHandler.toastShow(MCoursePlanDetailPrivateActivity.this,Ref.OP_NSR);
                    MCoursePlanDetailPrivateActivity.this.finish();
                    break;
                default:break;
            }
        }else if (source == REQUEST_Attend) {
            switch (NetRespStatType.dealWithRespStat(body)) {
                case STATUS_AUTHORIZE_FAIL:
                    ViewHandler.toastShow(MCoursePlanDetailPrivateActivity.this,"你尚未办理所该课程需要的会员卡，暂无法预订");
                    break;
                case DUPLICATE:
                    ViewHandler.toastShow(MCoursePlanDetailPrivateActivity.this,"你已签到了该排课");
                    break;
                case SUCCESS:
                    ViewHandler.toastShow(MCoursePlanDetailPrivateActivity.this,"签到成功");
                    tv_state.setText("已签到");
                    btn_attend.setVisibility(View.GONE);
                    break;
                case FAIL:
                    ViewHandler.toastShow(MCoursePlanDetailPrivateActivity.this,"签到失败");
                    break;
                default:break;
            }
        }else {

        }
    }

    @Override
    public void onRespMapList(String body, int source) throws IOException {
        ViewHandler.progressBarHide(MCoursePlanDetailPrivateActivity.this);
        if (source == REQUEST_CoursePlanPrivateDetailQuery) {
            ArrayList<Map<String,String>> maps = new ArrayList<>();
            maps = JsonUtil.strToListMap(body,keys);
            updateView(maps.get(0));
        }else if (source == REQUEST_Attend) {

        }else {

        }
    }

    @Override
    public void onRespError(int source) {
        ViewHandler.progressBarHide(MCoursePlanDetailPrivateActivity.this);
        ViewHandler.toastShow(MCoursePlanDetailPrivateActivity.this,Ref.UNKNOWN_ERROR);
    }

    @Override
    public void onReqFailure(Object object, int source) {
        ViewHandler.progressBarHide(MCoursePlanDetailPrivateActivity.this);
        ViewHandler.dealWithReqFail(MCoursePlanDetailPrivateActivity.this);
    }

    @Override
    public void onRespSessionExpired(int source) {
        ViewHandler.progressBarHide(MCoursePlanDetailPrivateActivity.this);
        ViewHandler.alertShowAndExitApp(MCoursePlanDetailPrivateActivity.this);
    }
}
