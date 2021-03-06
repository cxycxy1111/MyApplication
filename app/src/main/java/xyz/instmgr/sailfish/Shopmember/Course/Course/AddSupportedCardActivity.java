package xyz.instmgr.sailfish.Shopmember.Course.Course;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.alfred.alfredtools.*;
import xyz.instmgr.sailfish.Adapter.RVSupportedCardAdapter;
import xyz.instmgr.sailfish.R;
import xyz.instmgr.sailfish.Util.Ref;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AddSupportedCardActivity extends BaseActivity implements HttpResultListener {

    private long s_id,sm_id,ce_id;
    private static final int RESULT_CODE_FAIL = 1;
    private static final int RESULT_CODE_SUC = 2;
    private static final int REQUEST_QueryCardList=1;
    private static final int REQUEST_SupportedCardModify=2;
    private String course_name,last_time,max_book_num,selected_course;
    private String [] keys = new String[] {"id","name","type"};
    private RVSupportedCardAdapter adapter;
    private List<Map<String,String>> origin_cards;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_supported_card_add);
        initData();
        initViews();
    }

    private void initData() {
        SharedPreferences preferences = getSharedPreferences("sasm",MODE_PRIVATE);
        s_id = preferences.getLong("s_id",0L);
        sm_id = preferences.getLong("sm_id",0L);
        Intent intent = getIntent();
        ce_id = intent.getLongExtra("course_id",0);
    }

    private void initViews() {
        initToolbar();
        initCards();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("配置所支持的卡");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initCards() {
        String url = "/QueryCardList?type=0";
        HttpCallback callback = new HttpCallback(this,this,REQUEST_QueryCardList);
        NetUtil.reqSendGet(this,url,callback);

    }

    private void initRecyclerView() {
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.rv_a_AddSupportedCard);
        adapter = new RVSupportedCardAdapter(origin_cards,AddSupportedCardActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(AddSupportedCardActivity.this,LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_supported_card,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.save_add_support_card:
                StringBuilder builder = new StringBuilder();
                Map<Integer,String> values = adapter.getValueMap();
                Map<Integer,Boolean> checkStatusMap = adapter.getCheckStatusMap();
                Iterator iterator_v = values.entrySet().iterator();
                Iterator iterator_c = checkStatusMap.entrySet().iterator();
                while (iterator_c.hasNext()) {
                    Map.Entry entry = (Map.Entry)iterator_c.next();
                    Integer position = (Integer)entry.getKey();
                    Boolean isChecked = (Boolean)entry.getValue();
                    if (isChecked) {
                        String value = values.get(position);
                        Map<String,String> map = origin_cards.get(position);
                        int t = Integer.parseInt(String.valueOf(map.get("type")));
                        if (t != 3) {
                            builder.append("1_")
                                    .append(ce_id).append("_")
                                    .append(String.valueOf(origin_cards.get(position).get("id"))).append("_")
                                    .append(value).append("-");

                        }else {
                            builder.append("1_")
                                    .append(ce_id).append("_")
                                    .append(String.valueOf(origin_cards.get(position).get("id"))).append("_")
                                    .append("0").append("-");
                        }
                    }
                }
                String req = builder.toString();
                req = req.substring(0,req.length()-1);
                submitSupportedCard(req);
            default:
                break;
        }
        return true;
    }

    private void submitSupportedCard(String arg) {
        String url = "/SupportedCardModify?m=" + arg;
        HttpCallback callback = new HttpCallback(this,this,REQUEST_SupportedCardModify);
        NetUtil.reqSendGet(this,url,callback);
    }

    @Override
    public void onRespStatus(String body, int source) {
        if (source == REQUEST_QueryCardList) {
            NetRespStatType respStatType = NetRespStatType.dealWithRespStat(body);
            switch (respStatType) {
                case NSR:
                    ViewHandler.toastShow(AddSupportedCardActivity.this,"会员卡列表为空，请先新增会员卡");
                    AddSupportedCardActivity.this.finish();
                    break;
                case STATUS_AUTHORIZE_FAIL:
                    ViewHandler.exitAcitivityDueToAuthorizeFail(AddSupportedCardActivity.this);
                    break;
                default:break;
            }
        }else if (source == REQUEST_SupportedCardModify) {
            switch (NetRespStatType.dealWithRespStat(body)) {
                case NSR:
                    ViewHandler.toastShow(AddSupportedCardActivity.this,"课程或卡不存在");
                    setResult(RESULT_CODE_FAIL);
                    AddSupportedCardActivity.this.finish();
                    break;
                case NST_NOT_MATCH:
                    ViewHandler.toastShow(AddSupportedCardActivity.this,Ref.STAT_INST_NOT_MATCH);
                    setResult(RESULT_CODE_FAIL);
                    AddSupportedCardActivity.this.finish();
                    break;
                case SUCCESS:
                    ViewHandler.toastShow(AddSupportedCardActivity.this,Ref.OP_SUCCESS);
                    setResult(RESULT_CODE_SUC);
                    AddSupportedCardActivity.this.finish();
                    break;
                case DUPLICATE:
                    ViewHandler.toastShow(AddSupportedCardActivity.this,"已存在要支持的卡");
                    setResult(RESULT_CODE_FAIL);
                    AddSupportedCardActivity.this.finish();
                    break;
                case FAIL:
                    ViewHandler.toastShow(AddSupportedCardActivity.this,Ref.OP_FAIL);
                    setResult(RESULT_CODE_FAIL);
                    AddSupportedCardActivity.this.finish();
                    break;
                case STATUS_AUTHORIZE_FAIL:
                    ViewHandler.exitAcitivityDueToAuthorizeFail(AddSupportedCardActivity.this);
                    break;
                default:break;
            }
        }else {

        }

    }

    @Override
    public void onRespMapList(String body, int source) throws IOException {
        origin_cards = JsonUtil.strToListMap(body,keys);
        if (origin_cards.size() == 0) {
            ViewHandler.toastShow(AddSupportedCardActivity.this,"暂无会员卡");
        }
        initRecyclerView();
    }

    @Override
    public void onRespError(int source) {
        ViewHandler.toastShow(AddSupportedCardActivity.this,Ref.UNKNOWN_ERROR);
    }

    @Override
    public void onReqFailure(Object object, int source) {
        ViewHandler.toastShow(AddSupportedCardActivity.this, Ref.CANT_CONNECT_INTERNET);
    }

    @Override
    public void onRespSessionExpired(int source) {
        ViewHandler.alertShowAndExitApp(AddSupportedCardActivity.this);
    }
}
