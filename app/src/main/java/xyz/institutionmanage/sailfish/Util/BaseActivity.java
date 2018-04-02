package xyz.institutionmanage.sailfish.Util;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * Created by dengweixiong on 2017/8/29.
 */

public class BaseActivity extends AppCompatActivity {

    private ProgressBar progressBar;

    public BaseActivity() {
        super();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.removeActivity(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            Toast.makeText(getApplicationContext(),"请点击左上角的返回按钮返回上一界面",Toast.LENGTH_SHORT).show();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void createProgressBar(Activity activity) {
        Context context = activity;
        FrameLayout rootFrameLayout = (FrameLayout) findViewById(android.R.id.content);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        progressBar = new ProgressBar(context);
        progressBar.setLayoutParams(layoutParams);
        progressBar.setVisibility(View.GONE);
        rootFrameLayout.addView(progressBar);
    }

    public ProgressBar getProgressBar(Activity activity) {
        if (progressBar == null) {
            createProgressBar(activity);
        }
        return progressBar;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }
}
