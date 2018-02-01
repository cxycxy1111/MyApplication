package com.example.dengweixiong.Shopmember.Profile;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;

import com.example.dengweixiong.Util.Enum.EnumRespStatType;
import com.example.dengweixiong.Util.Enum.EnumRespType;
import com.example.dengweixiong.Util.JsonHandler;
import com.example.dengweixiong.Util.MethodTool;
import com.example.dengweixiong.Util.NetUtil;
import com.example.dengweixiong.Util.Ref;
import com.example.dengweixiong.Util.SharePreferenceManager;
import com.example.dengweixiong.myapplication.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ShopConfigActivity extends AppCompatActivity {

    private String s_id;
    private String[] key = new String[]{"",""};
    private CheckBox cb_allow_inner_view_teacher,cb_allow_inner_manage_teacher,cb_allow_outer_view_teacher,cb_allow_outer_manage_teacher;
    private CheckBox cb_allow_inner_view_classroom,cb_allow_inner_manage_classroom,cb_allow_outer_view_classroom,cb_allow_outer_manage_classroom;
    private CheckBox cb_allow_inner_view_card_type,cb_allow_inner_manage_card_type,cb_allow_outer_view_card_type,cb_allow_outer_manage_card_type;
    private CheckBox cb_allow_inner_manage_course,cb_allow_outer_manage_course;
    private CheckBox cb_allow_inner_manage_courseplan,cb_allow_outer_manage_courseplan;
    private CheckBox cb_allow_inner_view_member,cb_allow_inner_manage_member,cb_allow_outer_view_member,cb_allow_outer_manage_member;
    private CheckBox cb_allow_inner_view_member_card,cb_allow_inner_manage_member_card,cb_allow_outer_view_member_card,cb_allow_outer_manage_member_card;
    private CheckBox cb_allow_deduct_after_arrearage,cb_allow_deduct_after_overdue;
    private CheckBox cb_allow_book_after_arrearange,cb_allow_book_after_overdue;
    private CheckBox cb_allow_attendance_after_arrearage,cb_allow_attendance_after_overdue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_config);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_general);
        toolbar.setTitle("通用设置");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initEditText();
        initData();
        loadData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_shop_config,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.save_shop_config:
                saveChangePreDeal();
                break;
            default:break;
        }
        return true;
    }

    private void initData() {
        s_id = SharePreferenceManager.getSharePreferenceValue(ShopConfigActivity.this,"sasm","s_id",2);
    }

    private void initEditText() {
        cb_allow_inner_view_teacher = (CheckBox)findViewById(R.id.cb_allow_inner_view_teacher);
        cb_allow_inner_manage_teacher = (CheckBox)findViewById(R.id.cb_allow_inner_manage_teacher);
        cb_allow_outer_view_teacher = (CheckBox)findViewById(R.id.cb_allow_outer_view_teacher);
        cb_allow_outer_manage_teacher = (CheckBox)findViewById(R.id.cb_allow_outer_manage_teacher);
        cb_allow_inner_view_classroom = (CheckBox)findViewById(R.id.cb_allow_inner_view_classroom);
        cb_allow_inner_manage_classroom = (CheckBox)findViewById(R.id.cb_allow_inner_manage_classroom);
        cb_allow_outer_view_classroom = (CheckBox)findViewById(R.id.cb_allow_outer_view_classroom);
        cb_allow_outer_manage_classroom = (CheckBox)findViewById(R.id.cb_allow_outer_manage_classroom);
        cb_allow_inner_view_card_type = (CheckBox)findViewById(R.id.cb_allow_inner_view_card_type);
        cb_allow_inner_manage_card_type = (CheckBox)findViewById(R.id.cb_allow_inner_manage_card_type);
        cb_allow_outer_view_card_type = (CheckBox)findViewById(R.id.cb_allow_outer_view_card_type);
        cb_allow_outer_manage_card_type = (CheckBox)findViewById(R.id.cb_allow_outer_manage_card_type);
        cb_allow_inner_manage_course = (CheckBox)findViewById(R.id.cb_allow_inner_manage_course);
        cb_allow_outer_manage_course = (CheckBox)findViewById(R.id.cb_allow_outer_manage_course);
        cb_allow_inner_manage_courseplan = (CheckBox)findViewById(R.id.cb_allow_inner_manage_courseplan);
        cb_allow_outer_manage_courseplan = (CheckBox)findViewById(R.id.cb_allow_outer_manage_courseplan);
        cb_allow_inner_view_member = (CheckBox)findViewById(R.id.cb_allow_inner_view_member);
        cb_allow_inner_manage_member = (CheckBox)findViewById(R.id.cb_allow_inner_manage_member);
        cb_allow_outer_view_member = (CheckBox)findViewById(R.id.cb_allow_outer_view_member);
        cb_allow_outer_manage_member = (CheckBox)findViewById(R.id.cb_allow_inner_view_member_card);
        cb_allow_inner_view_member_card = (CheckBox)findViewById(R.id.cb_allow_inner_view_member_card);
        cb_allow_inner_manage_member_card = (CheckBox)findViewById(R.id.cb_allow_inner_manage_member_card);
        cb_allow_outer_view_member_card = (CheckBox)findViewById(R.id.cb_allow_outer_view_member_card);
        cb_allow_outer_manage_member_card = (CheckBox)findViewById(R.id.cb_allow_outer_manage_member_card);
        cb_allow_deduct_after_arrearage = (CheckBox)findViewById(R.id.cb_allow_deduct_after_arrearage);
        cb_allow_deduct_after_overdue = (CheckBox)findViewById(R.id.cb_allow_deduct_after_overdue);
        cb_allow_book_after_arrearange = (CheckBox)findViewById(R.id.cb_allow_book_after_arrearange);
        cb_allow_book_after_overdue = (CheckBox)findViewById(R.id.cb_allow_book_after_overdue);
        cb_allow_attendance_after_arrearage = (CheckBox)findViewById(R.id.cb_allow_attendance_after_arrearage);
        cb_allow_attendance_after_overdue = (CheckBox)findViewById(R.id.cb_allow_attendance_after_overdue);
    }

    private void loadData() {
        String url = "/ShopConfigQuery";
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(ShopConfigActivity.this, Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                switch (EnumRespType.dealWithResponse(resp)) {
                    case RESP_STAT:
                        switch (EnumRespStatType.dealWithRespStat(resp)) {
                            case NSR:
                                MethodTool.showToast(ShopConfigActivity.this,Ref.OP_NSR);
                                ShopConfigActivity.this.finish();
                                break;
                            case SESSION_EXPIRED:
                                MethodTool.showExitAppAlert(ShopConfigActivity.this);
                                break;
                            case AUTHORIZE_FAIL:
                                MethodTool.exitAcitivityDueToAuthorizeFail(ShopConfigActivity.this);
                                break;
                            default:break;
                        }
                        break;
                    case RESP_MAPLIST:
                        List<Map<String,String>> mapList = new ArrayList<>();
                        mapList = JsonHandler.strToListMap(resp,key);
                        final Map<String,String> map = mapList.get(0);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setCheckBox(map);
                            }
                        });
                        break;
                    case RESP_ERROR:
                        MethodTool.showToast(ShopConfigActivity.this,Ref.UNKNOWN_ERROR);
                        ShopConfigActivity.this.finish();
                        break;
                    default:break;
                }
            }
        };
        NetUtil.sendHttpRequest(ShopConfigActivity.this,url,callback);
    }

    private void setCheckBox(Map<String,String> map) {
        if (map.get("allow_view_teacher").contains("2")) {
            cb_allow_inner_view_teacher.setChecked(true);
        }
        else {
            cb_allow_inner_view_teacher.setChecked(false);
        }

        if (map.get("allow_view_teacher").contains("3")) {
            cb_allow_outer_view_teacher.setChecked(true);
        }
        else {
            cb_allow_outer_view_teacher.setChecked(false);
        }

        if (map.get("allow_manage_teacher").contains("2")) {
            cb_allow_inner_manage_teacher.setChecked(true);
        }
        else {
            cb_allow_inner_manage_teacher.setChecked(false);
        }

        if (map.get("allow_manage_teacher").contains("3")) {
            cb_allow_outer_manage_teacher.setChecked(true);
        }
        else {
            cb_allow_outer_manage_teacher.setChecked(false);
        }

        if (map.get("allow_view_classroom").contains("2")) {
            cb_allow_inner_view_classroom.setChecked(true);
        }
        else {
            cb_allow_inner_view_classroom.setChecked(false);
        }

        if (map.get("allow_view_classroom").contains("3")) {
            cb_allow_outer_view_classroom.setChecked(true);
        }
        else {
            cb_allow_outer_view_classroom.setChecked(false);
        }

        if (map.get("allow_manage_classroom").contains("2")) {
            cb_allow_inner_manage_classroom.setChecked(true);
        }else {
            cb_allow_inner_manage_classroom.setChecked(false);
        }

        if (map.get("allow_manage_classroom").contains("3")) {
            cb_allow_outer_manage_classroom.setChecked(true);
        }else {
            cb_allow_outer_manage_classroom.setChecked(false);
        }

        if (map.get("allow_view_card_type").contains("2")) {
            cb_allow_inner_view_card_type.setChecked(true);
        }else {
            cb_allow_inner_view_card_type.setChecked(false);
        }

        if (map.get("allow_view_card_type").contains("3")) {
            cb_allow_outer_view_card_type.setChecked(true);
        }else {
            cb_allow_outer_view_card_type.setChecked(false);
        }

        if (map.get("allow_manage_card_type").contains("2")) {
            cb_allow_inner_manage_card_type.setChecked(true);
        }else {
            cb_allow_inner_manage_card_type.setChecked(false);
        }

        if (map.get("allow_manage_card_type").contains("3")) {
            cb_allow_outer_manage_card_type.setChecked(true);
        }else {
            cb_allow_outer_manage_card_type.setChecked(false);
        }

        if (map.get("allow_manage_course").contains("2")) {
            cb_allow_inner_manage_course.setChecked(true);
        }else {
            cb_allow_inner_manage_course.setChecked(false);
        }

        if (map.get("allow_manage_course").contains("3")) {
            cb_allow_outer_manage_course.setChecked(true);
        }else {
            cb_allow_outer_manage_course.setChecked(false);
        }

        if (map.get("allow_manage_courseplan").contains("2")) {
            cb_allow_inner_manage_courseplan.setChecked(true);
        }else {
            cb_allow_inner_manage_courseplan.setChecked(false);
        }

        if (map.get("allow_manage_courseplan").contains("3")) {
            cb_allow_outer_manage_courseplan.setChecked(true);
        }else {
            cb_allow_outer_manage_courseplan.setChecked(false);
        }

        if (map.get("allow_view_member").contains("2")) {
            cb_allow_inner_view_member.setChecked(true);
        }else {
            cb_allow_inner_view_member.setChecked(false);
        }

        if (map.get("allow_view_member").contains("3")) {
            cb_allow_outer_view_member.setChecked(true);
        }else {
            cb_allow_outer_view_member.setChecked(false);
        }

        if (map.get("allow_manage_member").contains("2")) {
            cb_allow_inner_manage_member.setChecked(true);
        }else {
            cb_allow_inner_manage_member.setChecked(false);
        }

        if (map.get("allow_manage_member").contains("3")) {
            cb_allow_outer_manage_member.setChecked(true);
        }else {
            cb_allow_outer_manage_member.setChecked(false);
        }

        if (map.get("allow_view_member_card").contains("2")) {
            cb_allow_inner_view_member_card.setChecked(true);
        }else {
            cb_allow_inner_view_member_card.setChecked(false);
        }

        if (map.get("allow_view_member_card").contains("3")) {
            cb_allow_outer_view_member_card.setChecked(true);
        }else {
            cb_allow_outer_view_member_card.setChecked(false);
        }

        if (map.get("allow_manage_member_card").contains("2")) {
            cb_allow_inner_manage_member_card.setChecked(true);
        }else {
            cb_allow_inner_manage_member_card.setChecked(false);
        }

        if (map.get("allow_manage_member_card").contains("3")) {
            cb_allow_outer_manage_member_card.setChecked(true);
        }else {
            cb_allow_outer_manage_member_card.setChecked(false);
        }

        if (String.valueOf(map.get("allow_deduct_after_arrearage")).contains("1")) {
            cb_allow_deduct_after_arrearage.setChecked(true);
        }else {
            cb_allow_deduct_after_arrearage.setChecked(false);
        }

        if (String.valueOf(map.get("allow_deduct_after_overdue")).contains("1")) {
            cb_allow_deduct_after_overdue.setChecked(true);
        }else {
            cb_allow_deduct_after_overdue.setChecked(false);
        }

        if (String.valueOf(map.get("allow_book_after_arrearange")).contains("1")) {
            cb_allow_book_after_arrearange.setChecked(true);
        }else {
            cb_allow_book_after_arrearange.setChecked(false);
        }

        if (String.valueOf(map.get("allow_book_after_overdue")).contains("1")) {
            cb_allow_book_after_overdue.setChecked(true);
        }else {
            cb_allow_book_after_overdue.setChecked(false);
        }

        if (String.valueOf(map.get("allow_attendance_after_arrearage")).contains("1")) {
            cb_allow_attendance_after_arrearage.setChecked(true);
        }else {
            cb_allow_attendance_after_arrearage.setChecked(false);
        }

        if (String.valueOf(map.get("allow_attendance_after_overdue")).contains("1")) {
            cb_allow_attendance_after_overdue.setChecked(true);
        }else {
            cb_allow_attendance_after_overdue.setChecked(false);
        }
    }

    private void saveChangePreDeal() {
        StringBuilder b_v_teacher = new StringBuilder();
        b_v_teacher.append("1,");
        if (cb_allow_inner_view_teacher.isChecked()) {b_v_teacher.append("2,");}else {b_v_teacher.append("");}
        if (cb_allow_outer_view_teacher.isChecked()) {b_v_teacher.append("3,");}else {b_v_teacher.append("");}
        String s_v_teacher = b_v_teacher.toString();
        s_v_teacher = s_v_teacher.substring(0,s_v_teacher.length()-1);

        StringBuilder b_m_teacher = new StringBuilder();
        b_m_teacher.append("1,");
        if (cb_allow_inner_manage_teacher.isChecked()) {b_m_teacher.append("2,");}else {b_m_teacher.append("");}
        if (cb_allow_outer_manage_teacher.isChecked()) {b_m_teacher.append("3,");}else {b_m_teacher.append("");}
        String s_m_teacher = b_m_teacher.toString();
        s_m_teacher = s_m_teacher.substring(0,s_m_teacher.length()-1);

        StringBuilder b_v_classroom = new StringBuilder();
        b_v_classroom.append("1,");
        if (cb_allow_inner_view_classroom.isChecked()) {b_v_classroom.append("2,");}else {b_v_classroom.append("");}
        if (cb_allow_outer_view_classroom.isChecked()) {b_v_classroom.append("3,");}else {b_v_classroom.append("");}
        String s_v_classroom = b_v_classroom.toString();
        s_v_classroom = s_v_classroom.substring(0,s_v_classroom.length()-1);

        StringBuilder b_m_classroom = new StringBuilder();
        b_m_classroom.append("1,");
        if (cb_allow_inner_manage_classroom.isChecked()) {b_m_classroom.append("2,");}else {b_m_classroom.append("");}
        if (cb_allow_outer_manage_classroom.isChecked()) {b_m_classroom.append("3,");}else {b_m_classroom.append("");}
        String s_m_classroom = b_m_classroom.toString();
        s_m_classroom = s_m_classroom.substring(0,s_m_classroom.length()-1);

        //是否允许查看卡类型
        StringBuilder b_v_card_type = new StringBuilder();
        b_v_card_type.append("1,");
        if (cb_allow_inner_view_card_type.isChecked()) {b_v_card_type.append("2,");}else {b_v_card_type.append("");}
        if (cb_allow_outer_view_card_type.isChecked()) {b_v_card_type.append("3,");}else {b_v_card_type.append("");}
        String s_v_card_type = b_v_card_type.toString();
        s_v_card_type = s_v_card_type.substring(0,s_v_card_type.length()-1);

        //是否允许管理卡类型
        StringBuilder b_m_card_type = new StringBuilder();
        b_m_card_type.append("1,");
        if (cb_allow_inner_manage_card_type.isChecked()) {b_m_card_type.append("2,");}else {b_m_card_type.append("");}
        if (cb_allow_outer_manage_card_type.isChecked()) {b_m_card_type.append("3,");}else {b_m_card_type.append("");}
        String s_m_card_type = b_m_card_type.toString();
        s_m_card_type = s_m_card_type.substring(0,s_m_card_type.length()-1);

        //是否允许查看会员
        StringBuilder b_v_member = new StringBuilder();
        b_v_member.append("1,");
        if (cb_allow_inner_view_member.isChecked()) {b_v_member.append("2,");}else {b_v_member.append("");}
        if (cb_allow_outer_view_member.isChecked()) {b_v_member.append("3,");}else {b_v_member.append("");}
        String s_v_member = b_v_member.toString();
        s_v_member = s_v_member.substring(0,s_v_member.length()-1);

        //是否允许管理会员
        StringBuilder b_m_member = new StringBuilder();
        b_m_member.append("1,");
        if (cb_allow_inner_manage_member.isChecked()) {b_m_member.append("2,");}else {b_m_member.append("");}
        if (cb_allow_outer_manage_member.isChecked()) {b_m_member.append("3,");}else {b_m_member.append("");}
        String s_m_member = b_m_member.toString();
        s_m_member = s_m_member.substring(0,s_m_member.length()-1);

        //是否允许查看会员卡
        StringBuilder b_v_member_card = new StringBuilder();
        b_v_member_card.append("1,");
        if (cb_allow_inner_view_member_card.isChecked()) {b_v_member_card.append("2,");}else {b_v_member_card.append("");}
        if (cb_allow_outer_view_member_card.isChecked()) {b_v_member_card.append("3,");}else {b_v_member_card.append("");}
        String s_v_member_card = b_v_member_card.toString();
        s_v_member_card = s_v_member_card.substring(0,s_v_member_card.length()-1);

        //是否允许管理会员卡
        StringBuilder b_m_member_card = new StringBuilder();
        b_m_member_card.append("1,");
        if (cb_allow_inner_manage_member_card.isChecked()) {b_m_member_card.append("2,");}else {b_m_member_card.append("");}
        if (cb_allow_outer_manage_member_card.isChecked()) {b_m_member_card.append("3,");}else {b_m_member_card.append("");}
        String s_m_member_card = b_m_member_card.toString();
        s_m_member_card = s_m_member_card.substring(0,s_m_member_card.length()-1);

        //是否允许管理课程
        StringBuilder b_m_course = new StringBuilder();
        b_m_course.append("1,");
        if (cb_allow_inner_manage_course.isChecked()) {b_m_course.append("2,");}else {b_m_course.append("");}
        if (cb_allow_outer_manage_course.isChecked()) {b_m_course.append("3,");}else {b_m_course.append("");}
        String s_m_course = b_m_member_card.toString();
        s_m_course = s_m_course.substring(0,s_m_course.length()-1);

        //是否允许管理排课
        StringBuilder b_m_courseplan = new StringBuilder();
        b_m_courseplan.append("1,");
        if (cb_allow_inner_manage_courseplan.isChecked()) {b_m_courseplan.append("2,");}else {b_m_courseplan.append("");}
        if (cb_allow_outer_manage_courseplan.isChecked()) {b_m_courseplan.append("3,");}else {b_m_courseplan.append("");}
        String s_m_courseplan= b_m_courseplan.toString();
        s_m_courseplan = s_m_courseplan.substring(0,s_m_courseplan.length()-1);

        //是否允许欠费后扣费
        StringBuilder b_allow_deduct_after_arrearage = new StringBuilder();
        if (cb_allow_deduct_after_arrearage.isChecked()) {b_allow_deduct_after_arrearage.append("1");}else {b_allow_deduct_after_arrearage.append("0");}
        String s_allow_deduct_after_arrearage= b_allow_deduct_after_arrearage.toString();

        //是否允许过期后扣费
        StringBuilder b_allow_deduct_after_overdue = new StringBuilder();
        if (cb_allow_deduct_after_overdue.isChecked()) {b_allow_deduct_after_overdue.append("1");}else {b_allow_deduct_after_overdue.append("0");}
        String s_allow_deduct_after_overdue= b_allow_deduct_after_overdue.toString();

        //是否允许欠费后预约课程
        StringBuilder b_allow_book_after_arrearange = new StringBuilder();
        if (cb_allow_book_after_arrearange.isChecked()) {b_allow_book_after_arrearange.append("1");}else {b_allow_book_after_arrearange.append("0");}
        String s_allow_book_after_arrearange= b_allow_book_after_arrearange.toString();

        //是否允许过期后预约课程
        StringBuilder b_allow_book_after_overdue = new StringBuilder();
        if (cb_allow_book_after_overdue.isChecked()) {b_allow_book_after_overdue.append("1");}else {b_allow_book_after_overdue.append("0");}
        String s_allow_book_after_overdue= b_allow_book_after_overdue.toString();

        //是否允许欠费后签到
        StringBuilder b_allow_attendance_after_arrearage = new StringBuilder();
        if (cb_allow_attendance_after_arrearage.isChecked()) {b_allow_attendance_after_arrearage.append("1");}else {b_allow_attendance_after_arrearage.append("0");}
        String s_allow_attendance_after_arrearage= b_allow_attendance_after_arrearage.toString();

        //是否允许过期后签到
        StringBuilder b_allow_attendance_after_overdue = new StringBuilder();
        if (cb_allow_attendance_after_overdue.isChecked()) {b_allow_attendance_after_overdue.append("1");}else {b_allow_attendance_after_overdue.append("0");}
        String s_allow_attendance_after_overdue= b_allow_attendance_after_overdue.toString();

        String s= s_v_teacher+ "_" + s_m_teacher + "_" +
                s_v_classroom + "_" + s_m_classroom + "_" +
                s_v_card_type + "_" + s_m_card_type + "_" +
                s_m_course + "_" + s_m_courseplan + "_" +
                s_v_member + "_" + s_m_member + "_" +
                s_v_member_card + "_" + s_m_member_card + "_" +
                s_allow_deduct_after_arrearage + "_" + s_allow_deduct_after_overdue + "_" +
                s_allow_book_after_arrearange + "_" + s_allow_book_after_overdue + "_" +
                s_allow_attendance_after_arrearage + "_" + s_allow_attendance_after_overdue;
        submitChange(s);
    }

    private void submitChange(String s) {
        String url = "/ShopConfigModify?request=" + s;
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(ShopConfigActivity.this,Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                switch (EnumRespType.dealWithResponse(resp)) {
                    case RESP_ERROR:
                        MethodTool.showToast(ShopConfigActivity.this,Ref.UNKNOWN_ERROR);
                        break;
                    case RESP_STAT:
                        switch (EnumRespStatType.dealWithRespStat(resp)) {
                            case NSR:
                                MethodTool.showToast(ShopConfigActivity.this,Ref.OP_NSR);
                                break;
                            case EXE_SUC:
                                MethodTool.showToast(ShopConfigActivity.this,Ref.OP_MODIFY_SUCCESS);
                                ShopConfigActivity.this.finish();
                                break;
                            case EXE_FAIL:
                                MethodTool.showToast(ShopConfigActivity.this,Ref.OP_MODIFY_FAIL);
                                break;
                            case SESSION_EXPIRED:
                                MethodTool.showExitAppAlert(ShopConfigActivity.this);
                                break;
                            case AUTHORIZE_FAIL:
                                MethodTool.showAuthorizeFailToast(ShopConfigActivity.this);
                                break;
                            default:break;
                        }
                        break;
                    default:break;
                }
            }
        };
        NetUtil.sendHttpRequest(ShopConfigActivity.this,url,callback);
    }
}
