package com.example.dengweixiong.Activity;

import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Menu;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.dengweixiong.Adapter.ListAdapter;
import com.example.dengweixiong.Bean.Country;
import com.example.dengweixiong.Util.HttpRequestUtil;
import com.example.dengweixiong.myapplication.R;
import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.dengweixiong.myapplication.R.id.main_btn;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String LOCAL_TEST_ADD = "http://192.168.1.2:8080/Test/test";
    private static final int number = 1;
    ArrayList<Country> arrayList = new ArrayList<>();
    private Toolbar toolbar;
    private TextView textView;
    private Button button;

    private String responseData;
    private android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case number:
                    textView.setText(responseData);
                    Toast.makeText(MainActivity.this,"Successed!",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolBar();
        textView = (TextView)findViewById(R.id.main_tv);
        button = (Button)findViewById(main_btn);
        okhttp3.Callback callback = new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                responseData = response.body().string();
            }
        };
        HttpRequestUtil.sendHttpRequest(LOCAL_TEST_ADD,callback);

        //处理recyclerView
        initCountry();
        ListAdapter listAdapter = new ListAdapter(arrayList);

        //数据库处理
        LitePal.getDatabase();
        //绑定事件处理
        button.setOnClickListener(this);
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    public void onClick(View view){
        switch (view.getId()) {
            case main_btn :
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        message.what = number;
                        handler.sendMessage(message);
                    }
                }).start();
                break;
            default:
                break;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.refresh_main:
                break;
            case R.id.add_new_story_main:
                Intent intent = new Intent(MainActivity.this,AddNewStoryActivity.class);
                startActivity(intent);
                break;
            case R.id.add_new_trend_main:
                Intent intent1 = new Intent(MainActivity.this,AddNewTrendActivity.class);
                startActivity(intent1);
                break;
            case R.id.view_personal_profile_main:
                Intent intent2 = new Intent(MainActivity.this,ViewPersonalProfileActivity.class);
                startActivity(intent2);
                break;
            default:
                break;
        }
        return true;
    }

    private void initCountry() {
        for (int i= 0;i<100;i++) {
            Country country = new Country("America");
            arrayList.add(country);
            Country country1 = new Country("Canada");
            arrayList.add(country1);
        }
    }

    private void initToolBar() {
        toolbar = (Toolbar)findViewById(R.id.main_toolbar);
        toolbar.setTitle(R.string.app_name);
    }
    
}
