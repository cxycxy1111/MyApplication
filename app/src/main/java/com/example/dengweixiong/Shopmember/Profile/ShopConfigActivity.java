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
        String url = "/ShopConfigQuery?s_id=" + s_id;
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
                            default:break;
                        }
                        break;
                    case RESP_MAPLIST:
                        List<Map<String,Boolean>> mapList = new ArrayList<>();
                        mapList = JsonHandler.strToBooleanListMap(resp,key);
                        final Map<String,Boolean> map = mapList.get(0);
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
                }
            }
        };
        NetUtil.sendHttpRequest(ShopConfigActivity.this,url,callback);
    }

    private void setCheckBox(Map<String,Boolean> map) {
        if (map.get("allow_inner_view_teacher")) {cb_allow_inner_view_teacher.setChecked(true);}
        else {cb_allow_inner_view_teacher.setChecked(false);}

        if (map.get("allow_inner_manage_teacher")) {cb_allow_inner_manage_teacher.setChecked(true);}
        else {cb_allow_inner_manage_teacher.setChecked(false);}

        if (map.get("allow_outer_view_teacher")) {cb_allow_outer_view_teacher.setChecked(true);}
        else {cb_allow_outer_view_teacher.setChecked(false);}

        if (map.get("allow_outer_manage_teacher")) {cb_allow_outer_manage_teacher.setChecked(true);}
        else {cb_allow_outer_manage_teacher.setChecked(false);}

        if (map.get("allow_inner_view_classroom")) {cb_allow_inner_view_classroom.setChecked(true);}
        else {cb_allow_inner_view_classroom.setChecked(false);}

        if (map.get("allow_inner_manage_classroom")) {
            cb_allow_inner_manage_classroom.setChecked(true);
        }else {
            cb_allow_inner_manage_classroom.setChecked(false);
        }

        if (map.get("allow_outer_view_classroom")) {
            cb_allow_outer_view_classroom.setChecked(true);
        }else {
            cb_allow_outer_view_classroom.setChecked(false);
        }

        if (map.get("allow_outer_manage_classroom")) {
            cb_allow_outer_manage_classroom.setChecked(true);
        }else {
            cb_allow_outer_manage_classroom.setChecked(false);
        }

        if (map.get("allow_inner_view_card_type")) {
            cb_allow_inner_view_card_type.setChecked(true);
        }else {
            cb_allow_inner_view_card_type.setChecked(false);
        }

        if (map.get("allow_inner_manage_card_type")) {
            cb_allow_inner_manage_card_type.setChecked(true);
        }else {
            cb_allow_inner_manage_card_type.setChecked(false);
        }

        if (map.get("allow_outer_view_card_type")) {
            cb_allow_outer_view_card_type.setChecked(true);
        }else {
            cb_allow_outer_view_card_type.setChecked(false);
        }

        if (map.get("allow_outer_manage_card_type")) {
            cb_allow_outer_manage_card_type.setChecked(true);
        }else {
            cb_allow_outer_manage_card_type.setChecked(false);
        }

        if (map.get("allow_inner_manage_course")) {
            cb_allow_inner_manage_course.setChecked(true);
        }else {
            cb_allow_inner_manage_course.setChecked(false);
        }

        if (map.get("allow_outer_manage_course")) {
            cb_allow_outer_manage_course.setChecked(true);
        }else {
            cb_allow_outer_manage_course.setChecked(false);
        }

        if (map.get("allow_inner_manage_courseplan")) {
            cb_allow_inner_manage_courseplan.setChecked(true);
        }else {
            cb_allow_inner_manage_courseplan.setChecked(false);
        }

        if (map.get("allow_outer_manage_courseplan")) {
            cb_allow_outer_manage_courseplan.setChecked(true);
        }else {
            cb_allow_outer_manage_courseplan.setChecked(false);
        }

        if (map.get("allow_inner_view_member")) {
            cb_allow_inner_view_member.setChecked(true);
        }else {
            cb_allow_inner_view_member.setChecked(false);
        }

        if (map.get("allow_inner_manage_member")) {
            cb_allow_inner_manage_member.setChecked(true);
        }else {
            cb_allow_inner_manage_member.setChecked(false);
        }

        if (map.get("allow_outer_view_member")) {
            cb_allow_outer_view_member.setChecked(true);
        }else {
            cb_allow_outer_view_member.setChecked(false);
        }

        if (map.get("allow_outer_manage_member")) {
            cb_allow_outer_manage_member.setChecked(true);
        }else {
            cb_allow_outer_manage_member.setChecked(false);
        }

        if (map.get("allow_inner_view_member_card")) {
            cb_allow_inner_view_member_card.setChecked(true);
        }else {
            cb_allow_inner_view_member_card.setChecked(false);
        }

        if (map.get("allow_inner_manage_member_card")) {
            cb_allow_inner_manage_member_card.setChecked(true);
        }else {
            cb_allow_inner_manage_member_card.setChecked(false);
        }

        if (map.get("allow_outer_view_member_card")) {
            cb_allow_outer_view_member_card.setChecked(true);
        }else {
            cb_allow_outer_view_member_card.setChecked(false);
        }

        if (map.get("allow_outer_manage_member_card")) {
            cb_allow_outer_manage_member_card.setChecked(true);
        }else {
            cb_allow_outer_manage_member_card.setChecked(false);
        }
        if (map.get("allow_deduct_after_arrearage")) {
            cb_allow_deduct_after_arrearage.setChecked(true);
        }else {
            cb_allow_deduct_after_arrearage.setChecked(false);
        }

        if (map.get("allow_deduct_after_overdue")) {
            cb_allow_deduct_after_overdue.setChecked(true);
        }else {
            cb_allow_deduct_after_overdue.setChecked(false);
        }

        if (map.get("allow_book_after_arrearange")) {
            cb_allow_book_after_arrearange.setChecked(true);
        }else {
            cb_allow_book_after_arrearange.setChecked(false);
        }

        if (map.get("allow_book_after_overdue")) {
            cb_allow_book_after_overdue.setChecked(true);
        }else {
            cb_allow_book_after_overdue.setChecked(false);
        }

        if (map.get("allow_attendance_after_arrearage")) {
            cb_allow_attendance_after_arrearage.setChecked(true);
        }else {
            cb_allow_attendance_after_arrearage.setChecked(false);
        }

        if (map.get("allow_attendance_after_overdue")) {
            cb_allow_attendance_after_overdue.setChecked(true);
        }else {
            cb_allow_attendance_after_overdue.setChecked(false);
        }
    }

    private void saveChangePreDeal() {
        StringBuilder builder = new StringBuilder();
        if (cb_allow_inner_view_teacher.isChecked()) {builder.append("1,");}else {builder.append("0,");}
        if (cb_allow_inner_manage_teacher.isChecked()) {builder.append("1,");}else {builder.append("0,");}
        if (cb_allow_outer_view_teacher.isChecked()) {builder.append("1,");}else {builder.append("0,");}
        if (cb_allow_outer_manage_teacher.isChecked()) {builder.append("1,");}else {builder.append("0,");}
        if (cb_allow_inner_view_classroom.isChecked()) {builder.append("1,");}else {builder.append("0,");}
        if (cb_allow_inner_manage_classroom.isChecked()) {builder.append("1,");}else {builder.append("0,");}
        if (cb_allow_outer_view_classroom.isChecked()) {builder.append("1,");}else {builder.append("0,");}
        if (cb_allow_outer_manage_classroom.isChecked()) {builder.append("1,");}else {builder.append("0,");}
        if (cb_allow_inner_view_card_type.isChecked()) {builder.append("1,");}else {builder.append("0,");}
        if (cb_allow_inner_manage_card_type.isChecked()) {builder.append("1,");}else {builder.append("0,");}
        if (cb_allow_outer_view_card_type.isChecked()) {builder.append("1,");}else {builder.append("0,");}
        if (cb_allow_outer_manage_card_type.isChecked()) {builder.append("1,");}else {builder.append("0,");}
        if (cb_allow_inner_manage_course.isChecked()) {builder.append("1,");}else {builder.append("0,");}
        if (cb_allow_outer_manage_course.isChecked()) {builder.append("1,");}else {builder.append("0,");}
        if (cb_allow_inner_manage_courseplan.isChecked()) {builder.append("1,");}else {builder.append("0,");}
        if (cb_allow_outer_manage_courseplan.isChecked()) {builder.append("1,");}else {builder.append("0,");}
        if (cb_allow_inner_view_member.isChecked()) {builder.append("1,");}else {builder.append("0,");}
        if (cb_allow_inner_manage_member.isChecked()) {builder.append("1,");}else {builder.append("0,");}
        if (cb_allow_outer_view_member.isChecked()) {builder.append("1,");}else {builder.append("0,");}
        if (cb_allow_outer_manage_member.isChecked()) {builder.append("1,");}else {builder.append("0,");}
        if (cb_allow_inner_view_member_card.isChecked()) {builder.append("1,");}else {builder.append("0,");}
        if (cb_allow_inner_manage_member_card.isChecked()) {builder.append("1,");}else {builder.append("0,");}
        if (cb_allow_outer_view_member_card.isChecked()) {builder.append("1,");}else {builder.append("0,");}
        if (cb_allow_outer_manage_member_card.isChecked()) {builder.append("1,");}else {builder.append("0,");}
        if (cb_allow_deduct_after_arrearage.isChecked()) {builder.append("1,");}else {builder.append("0,");}
        if (cb_allow_deduct_after_overdue.isChecked()) {builder.append("1,");}else {builder.append("0,");}
        if (cb_allow_book_after_arrearange.isChecked()) {builder.append("1,");}else {builder.append("0,");}
        if (cb_allow_book_after_overdue.isChecked()) {builder.append("1,");}else {builder.append("0,");}
        if (cb_allow_attendance_after_arrearage.isChecked()) {builder.append("1,");}else {builder.append("0,");}
        if (cb_allow_attendance_after_overdue.isChecked()) {builder.append("1,");}else {builder.append("0,");}
        String s = builder.toString();
        s = s.substring(0,s.length()-1);
        submitChange(s);
    }

    private void submitChange(String s) {
        String url = "/ShopConfigModify?s_id=" + s_id + "&request=" + s;
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
