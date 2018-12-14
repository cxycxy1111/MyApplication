package xyz.institutionmanager.sailfish.Shopmember.Profile.Classroom;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.alfred.alfredtools.*;
import xyz.institutionmanager.sailfish.Adapter.RVSimpleAdapter;
import xyz.institutionmanager.sailfish.R;
import xyz.institutionmanager.sailfish.Util.Ref;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClassroomListActivity extends BaseActivity implements HttpResultListener {

    private ArrayList<String> strings = new ArrayList<>();
    private ArrayList<Map<String,String>> list = new ArrayList<>();
    private String [] keys = {"id","name"};
    private Map<String,String> tempMap = new HashMap<>();
    private static final int CLASSROOMLIST = 1;
    RVSimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom_list);
        initView();
        initData();
    }

    private void initView() {
        initToolbar();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("课室管理");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initData() {
        SharedPreferences preferences = getSharedPreferences("sasm",MODE_PRIVATE);
        long s_id = preferences.getLong("s_id",0);
        String url = "/ClassroomListQuery";
        HttpCallback callback = new HttpCallback(this,this,0);
        NetUtil.reqSendGet(this,url,callback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int p;
        switch (requestCode) {
            case 1 :
                if (resultCode == Ref.RESULTCODE_UPDATE) {
                    p = data.getIntExtra("pos",-1);
                    Map<String,String> map = new HashMap<>();
                    map.put("name",data.getStringExtra("new_name"));
                    map.put("id",String.valueOf(data.getLongExtra("cr_id",0)));
                    list.set(p,map);
                    strings.set(p,data.getStringExtra("new_name"));
                    Tool.sortListAsc(strings);
                } else if (resultCode == Ref.RESULTCODE_DELETE) {
                    p = data.getIntExtra("pos",-1);
                    list.remove(p);
                    strings.remove(p);
                }
                break;
            case 2:
                if (resultCode == Ref.RESULTCODE_ADD) {
                    long new_cr_id = data.getLongExtra("cr_id",0);
                    String new_cr_name = data.getStringExtra("cr_name");
                    Map<String,String> map = new HashMap<>();
                    map.put("id", String.valueOf(new_cr_id));
                    map.put("name",new_cr_name);
                    list.add(map);
                    Tool.sortListMapAsc(list,"name");
                    strings.add(new_cr_name);
                    Tool.sortListAsc(strings);
                }
                break;
            default:
                break;
        }
        if (adapter==null) {
            adapter = new RVSimpleAdapter(strings);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_classroom_list,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.add_a_classroom_list :
                startActivityForResult(new Intent(ClassroomListActivity.this,AddNewClassroomActivity.class),2);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onRespStatus(String body, int source) {
        NetRespStatType respStatType = NetRespStatType.dealWithRespStat(body);
        switch (respStatType) {
            case NSR:
                ViewHandler.toastShow(ClassroomListActivity.this,"暂无课室，请新增");
                break;
            case STATUS_AUTHORIZE_FAIL:
                ViewHandler.exitAcitivityDueToAuthorizeFail(ClassroomListActivity.this);
                break;
            default:
                break;
        }
    }

    @Override
    public void onRespMapList(String body, int source) throws IOException {
        list = JsonUtil.strToListMap(body,keys);
        Tool.sortListMapAsc(list,"name");
        for (int i = 0;i < list.size();i++) {
            strings.add(list.get(i).get("name"));
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(ClassroomListActivity.this,LinearLayoutManager.VERTICAL,false);
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.rv_a_ClassroomList);
        adapter = new RVSimpleAdapter(strings);
        adapter.setOnItemClickListener(new RVSimpleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(ClassroomListActivity.this,ClassroomDetailActivity.class);
                Object s = list.get(position).get("id");
                intent.putExtra("cr_id",String.valueOf(s));
                intent.putExtra("cr_name",list.get(position).get("name"));
                intent.putExtra("pos",position);
                startActivityForResult(intent,1);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onRespError(int source) {
        ViewHandler.toastShow(ClassroomListActivity.this,Ref.UNKNOWN_ERROR);
    }

    @Override
    public void onReqFailure(Object object, int source) {
        ViewHandler.toastShow(ClassroomListActivity.this, Ref.CANT_CONNECT_INTERNET);
    }

    @Override
    public void onRespSessionExpired(int source) {
        ViewHandler.alertShowAndExitApp(ClassroomListActivity.this);
    }
}
