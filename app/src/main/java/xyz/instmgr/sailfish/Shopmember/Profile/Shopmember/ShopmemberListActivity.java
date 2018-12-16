package xyz.instmgr.sailfish.Shopmember.Profile.Shopmember;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.alfred.alfredtools.*;
import xyz.instmgr.sailfish.Adapter.RVSectionAdapter;
import xyz.instmgr.sailfish.R;
import xyz.instmgr.sailfish.Util.Ref;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopmemberListActivity extends BaseActivity implements HttpResultListener,RVSectionAdapter.OnItemClickListener {

    private String [] keys = new String[] {"id","name","type"};
    private RVSectionAdapter adapter;
    private List<Map<String,String>> full_list = new ArrayList<>();
    private List<Map<String,String>> admin_list = new ArrayList<>();
    private List<Map<String,String>> inner_list = new ArrayList<>();
    private List<Map<String,String>> outer_list = new ArrayList<>();
    private static final int REQUEST_QueryShopmemberList_TYPE_1 = 1;
    private static final int REQUEST_QueryShopmemberList_TYPE_2 = 2;
    private static final int REQUEST_QueryShopmemberList_TYPE_3 = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopmember_list);
        initView();
    }

    @Override
    public void onRespStatus(String body, int source) {
        if (source == REQUEST_QueryShopmemberList_TYPE_1) {
            NetRespStatType respStatType = NetRespStatType.dealWithRespStat(body);
            switch (respStatType) {
                case NSR:
                    //ViewHandler.toastShow(ShopmemberListActivity.this,"暂无管理员");
                    initInnerList();
                    break;
                case STATUS_AUTHORIZE_FAIL:
                    ViewHandler.exitAcitivityDueToAuthorizeFail(ShopmemberListActivity.this);
                    break;
                default:break;
            }
        }else if (source == REQUEST_QueryShopmemberList_TYPE_2) {
            NetRespStatType respStatType = NetRespStatType.dealWithRespStat(body);
            switch (respStatType) {
                case NSR:
                    //ViewHandler.toastShow(ShopmemberListActivity.this,"暂无内部教师");
                    initOuterList();
                    break;
                case STATUS_AUTHORIZE_FAIL:
                    ViewHandler.exitAcitivityDueToAuthorizeFail(ShopmemberListActivity.this);
                    break;
                default:break;
            }
        }else if (source == REQUEST_QueryShopmemberList_TYPE_3) {
            NetRespStatType respStatType = NetRespStatType.dealWithRespStat(body);
            switch (respStatType) {
                case NSR:
                    //ViewHandler.toastShow(ShopmemberListActivity.this,"暂无外聘教师");
                    initRecyclerView();
                    break;
                case STATUS_AUTHORIZE_FAIL:
                    ViewHandler.exitAcitivityDueToAuthorizeFail(ShopmemberListActivity.this);
                    break;
                default:break;
            }
        }else {

        }
    }

    @Override
    public void onRespMapList(String body, int source) throws IOException {
        if (source == REQUEST_QueryShopmemberList_TYPE_1) {
            List<Map<String,String>> list = JsonUtil.strToListMap(body,keys);
            if (list.size() !=0 ) {
                Map<String,String> header = new HashMap<>();
                header.put("type","管理员");
                full_list.add(header);
            }
            for (int i = 0;i < list.size();i++) {
                full_list.add(list.get(i));
                admin_list.add(list.get(i));
            }
            initInnerList();
        }else if (source == REQUEST_QueryShopmemberList_TYPE_2) {
            List<Map<String,String>> list = JsonUtil.strToListMap(body,keys);
            if (list.size() !=0 ) {
                Map<String,String> header = new HashMap<>();
                header.put("type","内部教师");
                full_list.add(header);
            }
            for (int i = 0;i < list.size();i++) {
                full_list.add(list.get(i));
                inner_list.add(list.get(i));
            }
            initOuterList();
        }else if (source == REQUEST_QueryShopmemberList_TYPE_3) {
            List<Map<String,String>> list = JsonUtil.strToListMap(body,keys);
            if (list.size() != 0) {
                Map<String,String> header = new HashMap<>();
                header.put("type","外聘教师");
                full_list.add(header);
            }
            for (int i = 0;i < list.size();i++) {
                full_list.add(list.get(i));
                outer_list.add(list.get(i));
            }
            initRecyclerView();
        }else {

        }
    }

    @Override
    public void onRespError(int source) {
        ViewHandler.toastShow(ShopmemberListActivity.this,Ref.UNKNOWN_ERROR);
    }

    @Override
    public void onReqFailure(Object object, int source) {
        ViewHandler.toastShow(ShopmemberListActivity.this, Ref.CANT_CONNECT_INTERNET);
    }

    @Override
    public void onRespSessionExpired(int source) {
        ViewHandler.alertShowAndExitApp(this);
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(ShopmemberListActivity.this,ShopmemberDetailActivity.class);
        intent.putExtra("id",String.valueOf(full_list.get(position).get("id")));
        intent.putExtra("name",full_list.get(position).get("name"));
        intent.putExtra("type",String.valueOf(full_list.get(position).get("type")));
        intent.putExtra("pos",position);
        startActivityForResult(intent, Ref.REQCODE_QUERYDETAIL);
    }

    private void initView() {
        initToolbar();
        initAdminList();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("教师管理");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initAdminList() {
        ViewHandler.progressBarShow(this);
        String url = "/QueryShopmemberList?type=1";
        HttpCallback callback = new HttpCallback(this,this,REQUEST_QueryShopmemberList_TYPE_1);
        NetUtil.reqSendGet(this,url,callback);
    }

    //获取内部教师
    private void initInnerList() {
        String url = "/QueryShopmemberList?type=2";
        HttpCallback callback = new HttpCallback(this,this,REQUEST_QueryShopmemberList_TYPE_2);
        NetUtil.reqSendGet(this,url,callback);
    }

    private void initOuterList() {
        String url = "/QueryShopmemberList?type=3";
        HttpCallback callback = new HttpCallback(this,this,REQUEST_QueryShopmemberList_TYPE_3);
        NetUtil.reqSendGet(this,url,callback);
    }

    private void initRecyclerView() {
        ViewHandler.progressBarHide(this);
        adapter = new RVSectionAdapter(full_list,ShopmemberListActivity.this);
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.rv_a_shopmember_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ShopmemberListActivity.this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_shopmember_list,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home :
                finish();
                break;
            case R.id.add_a_shopmember_list :
                Intent intent = new Intent(ShopmemberListActivity.this,AddNewShopmemberActivity.class);
                startActivityForResult(intent, Ref.REQCODE_ADD);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        full_list.clear();
        admin_list.clear();
        inner_list.clear();
        outer_list.clear();
        initAdminList();

        /*
        int pos;
        //处理教师姓名更新
        if (requestCode == Ref.REQCODE_QUERYDETAIL) {
            /*
            if (resultCode == Ref.RESULTCODE_UPDATE) {

                pos = data.getIntExtra("old_pos", -1);
                full_list.clear();
                String new_type = String.valueOf(data.getIntExtra("new_type", 0));
                String old_type = String.valueOf(data.getIntExtra("old_type",0));
                String new_name = data.getStringExtra("name");
                String id = String.valueOf(data.getLongExtra("id", 0));
                Map<String, String> map = new HashMap<>();
                map.put("id", id);
                map.put("name", new_name);
                map.put("type", new_type);
                //处理内部教师更新
                switch (old_type) {
                    case "1":
                        switch (new_type) {
                            case "2":
                                admin_list.remove(pos-1);
                                inner_list.set(pos-2-admin_list.size(),map);
                                Tool.sortListMapAsc(inner_list,"name");
                                break;
                            case "3":
                                admin_list.remove(pos-1);;
                                outer_list.set(pos-3-admin_list.size()-inner_list.size(),map);
                                Tool.sortListMapAsc(outer_list,"name");
                                break;
                            default:break;
                        }
                        break;
                    case "2":
                        switch (new_type) {
                            case "1":
                                inner_list.remove(pos-admin_list.size()-2);
                                admin_list.add(map);
                                Tool.sortListMapAsc(admin_list,"name");
                                break;
                            case "3":
                                inner_list.remove(pos-admin_list.size()-2);
                                outer_list.add(map);
                                Tool.sortListMapAsc(outer_list,"name");
                                break;
                            default:break;
                        }
                        break;
                    case "3":
                        switch (new_type) {
                            case "1":
                                outer_list.remove(pos-admin_list.size()-inner_list.size()-3);
                                admin_list.add(map);
                                Tool.sortListMapAsc(admin_list,"name");
                                break;
                            case "2":
                                outer_list.remove(pos-admin_list.size()-inner_list.size()-3);
                                inner_list.add(map);
                                Tool.sortListMapAsc(inner_list,"name");
                                break;
                            default:break;
                        }
                        break;
                    default:break;
                }
                fullListReorder();
            //处理教师删除
            } else if (resultCode == Ref.RESULTCODE_DELETE) {
                pos = data.getIntExtra("pos", -1);
                full_list.remove(pos);
            }else if (resultCode == Ref.RESULTCODE_NULL) {

            }
        }else if (requestCode == Ref.REQCODE_ADD) {
            if (resultCode == Ref.RESULTCODE_ADD) {
                full_list.clear();
                String name = data.getStringExtra("name");
                String id = data.getStringExtra("id");
                int type = data.getIntExtra("type",0);
                Map<String,String> map = new HashMap<>();
                map.put("name",name);map.put("id",id);map.put("type",String.valueOf(type));
                if (String.valueOf(type).equals(Ref.TEACHER_ADMIN)) {
                    admin_list.add(map);
                    Tool.sortListMapAsc(admin_list,"name");
                }else if (String.valueOf(type).equals(Ref.TEACHER_INNER)) {
                    inner_list.add(map);
                    Tool.sortListMapAsc(inner_list,"name");
                }else if (String.valueOf(type).equals(Ref.TEACHER_OUTER)) {
                    outer_list.add(map);
                    Tool.sortListMapAsc(outer_list,"name");
                }
                fullListReorder();
            }else if (resultCode == Ref.RESULTCODE_NULL) {

            }

        }
        adapter.notifyDataSetChanged();
        */
    }

    private void fullListReorder() {
        if (admin_list.size() != 0) {
            Map<String, String> header = new HashMap<>();
            header.put("type", "管理员");
            full_list.add(header);
        }
        for (int i = 0; i < admin_list.size(); i++) {
            full_list.add(admin_list.get(i));
        }
        if (inner_list.size() != 0) {
            Map<String, String> header = new HashMap<>();
            header.put("type", "内部教师");
            full_list.add(header);
        }
        for (int i = 0; i < inner_list.size(); i++) {
            full_list.add(inner_list.get(i));
        }
        if (outer_list.size() != 0) {
            Map<String, String> header = new HashMap<>();
            header.put("type", "外聘教师");
            full_list.add(header);
        }
        for (int i = 0; i < outer_list.size(); i++) {
            full_list.add(outer_list.get(i));
        }
    }

}

