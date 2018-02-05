package xyz.institutionmanage.sailfish.Shopmember.Profile;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import xyz.institutionmanage.sailfish.Util.BaseActivity;

public class HelpActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(xyz.example.dengweixiong.myapplication.R.layout.activity_help);
        Toolbar toolbar = (Toolbar) findViewById(xyz.example.dengweixiong.myapplication.R.id.toolbar_general);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("帮助");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(xyz.example.dengweixiong.myapplication.R.menu.menu_help,menu);
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
