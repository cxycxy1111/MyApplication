package xyz.instmgr.sailfish.Shopmember.Member;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import com.alfred.alfredtools.*;
import xyz.instmgr.sailfish.Adapter.RVSimpleAdapter;
import xyz.instmgr.sailfish.R;
import xyz.instmgr.sailfish.Shopmember.MemberCard.MemberCardListActivity;
import xyz.instmgr.sailfish.Util.Ref;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MemberDetailActivity
        extends BaseActivity
        implements RVSimpleAdapter.OnItemClickListener,
            View.OnClickListener,
            DatePickerDialog.OnDateSetListener,
            EditText.OnFocusChangeListener,
            HttpResultListener {

    private static final int REQUEST_QueryMemberDetail = 1;
    private static final int REQUEST_removeMember = 2;
    private static final int REQUEST_ModifyMember = 3;

    private int current_year,current_month,current_date;
    private Toolbar toolbar;
    private Button btn_view_member_card,btn_reset_password,btn_delete;
    private String toolbar_title,m_name;
    private long m_id;
    private EditText et_sn,et_name,et_birthday,et_phone,et_im;

    private CharSequence sn,name,birthday,phone,im;
    private ArrayList<String> list = new ArrayList<>();
    private Map<String,String> map = new HashMap<>();
    private int year,month,day;
    private AlertDialog.Builder builder;
    private int position;
    private DatePickerDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_detail);
        initToolBar();
        initActions();
        initData();
        loadMemberDetail();
    }

    private void initToolBar () {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        toolbar_title = (String)bundle.get("toolbar_title");
        toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(toolbar_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initActions() {
        btn_view_member_card = (Button)findViewById(R.id.btn_view_member_card_activity_member_detail);
        btn_reset_password = (Button)findViewById(R.id.btn_view_reset_password_activity_member_detail);
        btn_delete = (Button)findViewById(R.id.btn_view_delete_activity_member_detail);

        btn_view_member_card.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
        btn_reset_password.setOnClickListener(this);
    }

    private void loadMemberDetail() {
        String url = "/QueryMemberDetail?member_id=" + m_id;
        HttpCallback callback = new HttpCallback(this,this,REQUEST_QueryMemberDetail);
        NetUtil.reqSendGet(this,url,callback);
    }

    private void initData() {
        dealWithIntent();
    }

    private void initDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                et_birthday.setText(new StringBuffer().append(year).append("-").append(month+1).append("-").append(dayOfMonth));
            }
        };
        dialog = new DatePickerDialog(MemberDetailActivity.this,listener,current_year,current_month,current_date);
    }

    private void initEditText(Map<String,String> map) {
        et_sn = (EditText)findViewById(R.id.edittext_sn_activity_member_detail);
        et_name = (EditText)findViewById(R.id.edittext_name_activity_member_detail);
        et_birthday = (EditText)findViewById(R.id.edittext_birthday_activity_member_detail);
        et_phone = (EditText)findViewById(R.id.edittext_phone_activity_member_detail);
        et_im = (EditText)findViewById(R.id.edittext_im_activity_member_detail);

        et_birthday.setInputType(InputType.TYPE_NULL);
        et_birthday.setOnClickListener(this);
        et_birthday.setOnFocusChangeListener(this);

        Object o_id = map.get("id");
        Object o_name = map.get("name");
        sn = String.valueOf(o_id);
        name = String.valueOf(o_name);
        et_sn.setText(sn);
        et_name.setText(name);
        String birthday = String.valueOf(map.get("birthday"));
        birthday = birthday.substring(0,birthday.length()-11);
        et_birthday.setText(birthday);
        et_phone.setText(String.valueOf(map.get("phone")));
        et_im.setText(String.valueOf(map.get("im")));

        //初始化时间选择器
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(birthday);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        current_year = calendar.get(Calendar.YEAR);
        current_month = calendar.get(Calendar.MONTH);
        current_date = calendar.get(Calendar.DAY_OF_MONTH);
        initDatePickerDialog();

    }

    private void dealWithIntent() {
        Map<String,String> map = new HashMap<>();
        Intent intent = getIntent();
        map.put("position",String.valueOf(intent.getIntExtra("position",0)));
        position = intent.getIntExtra("position",0);
        m_id = Long.parseLong(intent.getStringExtra("m_id"));
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.edittext_birthday_activity_add_new_member:
                if (hasFocus == false) {
                    dialog.show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edittext_birthday_activity_member_detail:
                dialog.show();
                break;
            case R.id.btn_view_member_card_activity_member_detail:
                Intent intent;
                intent = new Intent(MemberDetailActivity.this,MemberCardListActivity.class);
                intent.putExtra("m_id",m_id).putExtra("m_name",name).putExtra("source","MemberDetailActivity");
                startActivity(intent);
                break;
            case R.id.btn_view_reset_password_activity_member_detail:
                Intent intent1;
                intent1 = new Intent(MemberDetailActivity.this,ResetMemberPwdActivity.class);
                intent1.putExtra("m_id",m_id);
                startActivity(intent1);
                break;
            case R.id.btn_view_delete_activity_member_detail:
                dealWithDeleteMemberAction();
                break;
            default:
                break;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        this.year = year;
        this.month = month;
        this.day = dayOfMonth;
        et_birthday.setText(new StringBuffer()
                .append(year).append("-").append(month + 1).append("-").append(dayOfMonth));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_member_detail,menu);
        return true;
    }

    @Override
    public void onItemClick(View view, int positon) {
        Intent intent;
        switch (positon) {
            case 0:
                intent = new Intent(this,MemberCardListActivity.class);
                intent.putExtra("m_id",m_id).putExtra("m_name",map.get("name")).putExtra("source","MemberDetailActivity");
                startActivityForResult(intent, 1);
                break;
            case 1:
                intent = new Intent(MemberDetailActivity.this,ResetMemberPwdActivity.class);
                intent.putExtra("m_id",m_id);
                startActivity(intent);
                break;
            case 2:
                dealWithDeleteMemberAction();
                break;
            default:
                break;
        }
    }

    /**
     * 菜单点击事件
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                MemberDetailActivity.this.finish();
                break;
            case R.id.save_member_detail:
                dealWithSaveAction();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                switch (resultCode) {
                    case 1:
                        break;
                    default:break;
                }
            default:break;
        }
    }

    @Override
    public void onRespStatus(String body, int source) {
        if (source == REQUEST_QueryMemberDetail) {
            switch (NetRespStatType.dealWithRespStat(body)) {
                case NSR:
                    ViewHandler.toastShow(MemberDetailActivity.this,Ref.OP_NSR);
                    MemberDetailActivity.this.finish();
                    break;
                case STATUS_AUTHORIZE_FAIL:
                    ViewHandler.exitAcitivityDueToAuthorizeFail(MemberDetailActivity.this);
                    break;
                default:break;
            }
        }else if (source == REQUEST_removeMember) {
            NetRespStatType respStatType = NetRespStatType.dealWithRespStat(body);
            switch (respStatType) {
                case SUCCESS:
                    ViewHandler.toastShow(MemberDetailActivity.this,Ref.OP_DELETE_SUCCESS);
                    Intent intent = new Intent(MemberDetailActivity.this,MemberListActivity.class);
                    intent.putExtra("pos",position);
                    setResult(Ref.RESULTCODE_DELETE,intent);
                    finish();
                    break;
                case NSR:
                    ViewHandler.toastShow(MemberDetailActivity.this,"该会员已被删除");
                    MemberDetailActivity.this.finish();
                    break;
                case FAIL:
                    ViewHandler.toastShow(MemberDetailActivity.this,"该会员已被删除");
                    MemberDetailActivity.this.finish();
                    break;
                default:break;
            }
        }else if (source == REQUEST_ModifyMember) {
            NetRespStatType respStatType = NetRespStatType.dealWithRespStat(body);
            switch (respStatType) {
                case SUCCESS:
                    ViewHandler.toastShow(MemberDetailActivity.this,Ref.OP_MODIFY_SUCCESS);
                    Intent intent = new Intent(MemberDetailActivity.this,MemberListActivity.class);
                    intent.putExtra("pos",position).putExtra("m_id",m_id).putExtra("m_name",m_name);
                    setResult(Ref.RESULTCODE_UPDATE,intent);
                    finish();
                    break;
                case NSR:
                    ViewHandler.toastShow(MemberDetailActivity.this,"会员已被删除");
                    break;
                case FAIL:
                    ViewHandler.toastShow(MemberDetailActivity.this,Ref.OP_MODIFY_FAIL);
                    break;
                case STATUS_AUTHORIZE_FAIL:
                    ViewHandler.exitAcitivityDueToAuthorizeFail(MemberDetailActivity.this);
                    break;
                default:break;
            }
        }
    }

    @Override
    public void onRespMapList(String body, int source) throws IOException {
        if (source == REQUEST_QueryMemberDetail) {
            String [] keys = new String[] {"id","name","login_name","birthday","phone","im"};
            ArrayList<Map<String,String>> list = JsonUtil.strToListMap(body,keys);
            ArrayList<String> values = new ArrayList<String>();
            for (int i = 0;i < list.size();i++) {
                map = list.get(0);
            }
            m_id = Long.parseLong(String.valueOf(map.get("id")));
            initEditText(map);
        }else if (source == REQUEST_removeMember) {

        }else if (source == REQUEST_ModifyMember) {

        }else {

        }
    }

    @Override
    public void onRespError(int source) {
        ViewHandler.toastShow(MemberDetailActivity.this,Ref.UNKNOWN_ERROR);
    }

    @Override
    public void onReqFailure(Object object, int source) {
        ViewHandler.toastShow(MemberDetailActivity.this, Ref.CANT_CONNECT_INTERNET);
    }

    @Override
    public void onRespSessionExpired(int source) {
        ViewHandler.alertShowAndExitApp(MemberDetailActivity.this);
    }

    private void dealWithDeleteMemberAction() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MemberDetailActivity.this);
        builder.setCancelable(false).setTitle("确认要删除该会员吗？").setMessage("删除会员后，该会员下的会员卡均无法使用。");
        builder.setNegativeButton("不删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String url = "/removeMember?member_id=" + m_id;
                HttpCallback callback = new HttpCallback(MemberDetailActivity.this,MemberDetailActivity.this,REQUEST_removeMember);
                NetUtil.reqSendGet(MemberDetailActivity.this,url,callback);
            }
        });
        builder.show();
    }

    private void dealWithSaveAction() {
        m_name = et_name.getText().toString();
        String m_birthday = et_birthday.getText().toString();
        String m_phone = et_phone.getText().toString();
        String m_im = et_im.getText().toString();
        String url = "/ModifyMember?member_id=" + m_id +
                "&name=" + m_name +
                "&birthday=" + m_birthday +
                "&phone=" + m_phone +
                "&im=" + m_im;
        HttpCallback callback = new HttpCallback(this,this,REQUEST_ModifyMember);
        NetUtil.reqSendGet(this,url,callback);

    }
}
