package xyz.institutionmanager.sailfish.Shopmember.Member;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.alfred.alfredtools.*;
import xyz.institutionmanager.sailfish.Adapter.RVSimpleAdapter;
import xyz.institutionmanager.sailfish.R;
import xyz.institutionmanager.sailfish.Shopmember.Main.ShopmemberMainActivity;
import xyz.institutionmanager.sailfish.Util.Ref;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemberListActivity
        extends BaseActivity
        implements RVSimpleAdapter.OnItemClickListener,HttpResultListener {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private List<Map<String,String>> list = new ArrayList<>();
    private ArrayList<String> name_list = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private RVSimpleAdapter adapter;
    private static final String TOOLBAR_TITLE = "会员管理";
    private static final String TAG = "MemberListActivity:";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_list);
        initToolbar();
        initData();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String temp_m_name;
        int p;
        //处理来自MemberDetailActivity的调用
        if(requestCode == 1) {
            if (resultCode == Ref.RESULTCODE_DELETE) {
                p = data.getIntExtra("pos",-1);
                list.remove(p);
                name_list.remove(p);
            } else if (resultCode == Ref.RESULTCODE_UPDATE) {
                p = data.getIntExtra("pos",-1);
                Map<String,String> map = new HashMap<>();
                map.put("id",String.valueOf(data.getLongExtra("m_id",0)));
                map.put("name",data.getStringExtra("m_name"));
                list.set(p,map);
                name_list.set(p,data.getStringExtra("m_name"));
                Tool.sortListMapAsc(list,"name");
                Tool.sortListAsc(name_list);
            }else if (resultCode == Ref.RESULTCODE_NULL) {

            }else {

            }
        }
        //处理来自AddNewMemberActivity的调用
        else if (requestCode == 2) {
        }

        else {

        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRespStatus(String body, int source) {
        switch (NetRespStatType.dealWithRespStat(body)) {
            case EMPTY:
                ViewHandler.toastShow(MemberListActivity.this,"暂无会员，请新增");
                break;
            case STATUS_AUTHORIZE_FAIL:
                ViewHandler.exitAcitivityDueToAuthorizeFail(MemberListActivity.this);
                break;
            default:break;
        }
    }

    @Override
    public void onRespMapList(String body, int source) throws IOException {
        String [] keys = new String[] {"id","name"};
        list = JsonUtil.strToListMap(body,keys);
        Tool.sortListMapAsc(list,"name");
        for (int i = 0;i < list.size();i++) {
            name_list.add(list.get(i).get("name"));
        }
        initRecyclerView(MemberListActivity.this);
    }

    @Override
    public void onRespError(int source) {
        ViewHandler.toastShow(MemberListActivity.this,Ref.UNKNOWN_ERROR);
    }

    @Override
    public void onReqFailure(Object object, int source) {
        ViewHandler.toastShow(MemberListActivity.this,Ref.CANT_CONNECT_INTERNET);
    }

    @Override
    public void onRespSessionExpired(int source) {
        ViewHandler.alertShowAndExitApp(this);
    }

    /**
     * 初始化toolbar
     */
    private void initToolbar() {
        toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(TOOLBAR_TITLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * 初始化recyclerview
     * @param context
     */
    private void initRecyclerView(Context context) {
        recyclerView = (RecyclerView)findViewById(R.id.recyclerview_activity_member_list);
        linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        adapter = new RVSimpleAdapter(name_list);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        String url = "/QueryMemberList";
        HttpCallback callback = new HttpCallback(this,this,0);
        NetUtil.reqSendGet(this,url,callback);
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(this,MemberDetailActivity.class);
        intent.putExtra("toolbar_title",name_list.get(position));
        intent.putExtra("m_id",String.valueOf(list.get(position).get("id")));
        intent.putExtra("position",position);
        startActivityForResult(intent,1);
    }

    //创建菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_member_list,menu);
        MenuItem menuItem = menu.findItem(R.id.search_main);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        MenuItemCompat.setOnActionExpandListener(menuItem,expandListener);
        return true;
    }

//搜索条
    MenuItemCompat.OnActionExpandListener expandListener = new MenuItemCompat.OnActionExpandListener() {
        @Override
        public boolean onMenuItemActionExpand(MenuItem item) {
            return true;
        }

        @Override
        public boolean onMenuItemActionCollapse(MenuItem item) {
            return true;
        }
    };
//菜单选中事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(MemberListActivity.this, ShopmemberMainActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return true;
    }

}
