package xyz.instmgr.sailfish.Shopmember.Profile;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import com.alfred.alfredtools.*;
import xyz.instmgr.sailfish.R;
import xyz.instmgr.sailfish.Util.Ref;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ShopInfoActivity extends BaseActivity implements HttpResultListener {

    private static final int REQUEST_shopDetailQuery = 1;
    private static final int REQUEST_shopDetailModify = 2;
    private String[] keys = new String[]{"name","del","introduction","id"};
    private EditText et_name,et_remark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_info);
        initViewComponents();
        String url = "/shopDetailQuery";
        HttpCallback callback = new HttpCallback(this,this,REQUEST_shopDetailQuery);
        NetUtil.reqSendGet(this,url,callback);
    }

    private void initViewComponents() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        toolbar.setTitle("机构信息");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        et_name = (EditText) findViewById(R.id.et_hint_shop_name_a_shop_info);
        et_remark = (EditText) findViewById(R.id.et_hint_remark_a_shop_info);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_shop_info,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ShopInfoActivity.this.finish();
                break;
            case R.id.save_shop_info:
                checkRequestData();
                break;
            default:break;
        }
        return true;
    }

    @Override
    public void onRespStatus(String body, int source) {
        switch (NetRespStatType.dealWithRespStat(body)) {
            case SUCCESS:
                ViewHandler.toastShow(ShopInfoActivity.this,"修改成功");
                break;
            case FAIL:
                ViewHandler.toastShow(ShopInfoActivity.this,"修改失败，请重试");
                break;
            case EMPTY:
                ViewHandler.toastShow(ShopInfoActivity.this,"该机构已被删除");
                ActivityMgr.removeAllActivity();
                break;
            default:break;
        }

    }

    @Override
    public void onRespMapList(String body, int source) throws IOException {
        if (source == REQUEST_shopDetailQuery) {
            final List<Map<String,String>> mapList = JsonUtil.strToListMap(body,keys);
            et_name.setText( mapList.get(0).get("name"));
            et_remark.setText(mapList.get(0).get("introduction"));
        }else if (source == REQUEST_shopDetailModify) {

        }

    }

    @Override
    public void onRespError(int source) {
        ViewHandler.toastShow(ShopInfoActivity.this,Ref.UNKNOWN_ERROR);
    }

    @Override
    public void onReqFailure(Object object, int source) {
        ViewHandler.toastShow(ShopInfoActivity.this,Ref.CANT_CONNECT_INTERNET);
    }

    @Override
    public void onRespSessionExpired(int source) {
        ViewHandler.alertShowAndExitApp(ShopInfoActivity.this);
    }

    private void checkRequestData() {
        String str_new_name = et_name.getText().toString();
        String str_remark = et_remark.getText().toString();
        if (str_new_name.equals("") || str_new_name.equals(null)) {
            Toast.makeText(ShopInfoActivity.this,Ref.OP_EMPTY_ESSENTIAL_INFO,Toast.LENGTH_SHORT).show();
        }else {
            saveChange(str_new_name,str_remark);
        }
    }

    private void saveChange(String name,String remark) {
        String url = "/shopDetailModify?name="+ name + "&remark=" + remark;
        NetUtil.reqSendGet(this,url,new HttpCallback(this,this,REQUEST_shopDetailModify));
    }
}
