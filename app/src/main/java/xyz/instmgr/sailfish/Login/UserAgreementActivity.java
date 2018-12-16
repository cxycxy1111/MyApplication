package xyz.instmgr.sailfish.Login;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import com.alfred.alfredtools.BaseActivity;
import com.alfred.alfredtools.NetUtil;
import xyz.instmgr.sailfish.R;

public class UserAgreementActivity extends BaseActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_agreement);
        initComponents();
    }

    private void initComponents() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        webView = (WebView)findViewById(R.id.wv_a_user_agreement);
        toolbar.setTitle("管馆易用户注册协议");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        webView.loadUrl(NetUtil.SELECTED_HOST + "/user_agreement.html");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            default:break;
        }
        return true;
    }
}
