package xyz.institutionmanager.sailfish.Login;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import com.alfred.alfredtools.BaseActivity;
import com.alfred.alfredtools.NetUtil;
import xyz.institutionmanager.sailfish.R;

public class WhatIsSailfishActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_what_is_sailfish);
        initComponent();
    }

    private void initComponent() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        toolbar.setTitle("什么是管馆易？");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        WebView webView = (WebView)findViewById(R.id.wv_a_what_is_sailfish);
        webView.loadUrl(NetUtil.SELECTED_HOST + "/index.html");
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
