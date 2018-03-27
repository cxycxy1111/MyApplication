package xyz.institutionmanage.sailfish.Shopmember.Profile;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
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

public class ShopInfoActivity extends BaseActivity {

    private String[] keys = new String[]{"name","del","introductioon","id"};
    private EditText et_name,et_remark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_info);
        initDataFromPreviousActivity();
        initViewComponents();
        initShopDataFromWeb();
    }

    private void initDataFromPreviousActivity() {

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

    private void initShopDataFromWeb() {
        String url = "/shopDetailQuery";
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(ShopInfoActivity.this,Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                switch (EnumRespType.dealWithResponse(resp)) {
                    case RESP_MAPLIST:
                        final List<Map<String,String>> mapList = JsonHandler.strToListMap(resp,keys);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                et_name.setText( mapList.get(0).get("name"));
                                et_remark.setText(mapList.get(0).get("introduction"));
                            }
                        });
                        break;
                    case RESP_ERROR:
                        break;
                    case RESP_STAT:
                        switch (EnumRespStatType.dealWithRespStat(resp)) {
                            case SESSION_EXPIRED:
                                MethodTool.showExitAppAlert(ShopInfoActivity.this);
                                break;
                            case EMPTY_RESULT:
                                MethodTool.showToast(ShopInfoActivity.this,"该机构已被删除");
                                ActivityManager.removeAllActivity();
                                break;
                            default:break;
                        }
                        break;
                    default:break;
                }
            }
        };
        NetUtil.sendHttpRequest(ShopInfoActivity.this,url,callback);
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
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(ShopInfoActivity.this, Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                switch (EnumRespType.dealWithResponse(resp)) {
                    case RESP_STAT:
                        switch (EnumRespStatType.dealWithRespStat(resp)) {
                            case EXE_SUC:
                                MethodTool.showToast(ShopInfoActivity.this,"修改成功");
                                break;
                            case EXE_FAIL:
                                MethodTool.showToast(ShopInfoActivity.this,"修改失败，请重试");
                                break;
                            case SESSION_EXPIRED:
                                MethodTool.showExitAppAlert(ShopInfoActivity.this);
                                break;
                            default:break;
                        }
                        break;
                    case RESP_ERROR:
                        MethodTool.showToast(ShopInfoActivity.this,Ref.UNKNOWN_ERROR);
                        break;
                    default:break;
                }
            }
        };
        NetUtil.sendHttpRequest(ShopInfoActivity.this,url,callback);
    }
}
