package com.example.dengweixiong.Util;


import android.content.Context;

/**
 * Created by dengweixiong on 2018/1/22.
 */

public class TestFile {

    private String user_name;
    private String password;
    private Context context;

    public TestFile() {

    }

    public TestFile(Context context,String user_name,String password) {
        this.context = context;
        this.user_name = user_name;
        this.password = password;
    }

}
