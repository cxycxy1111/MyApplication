package com.example.dengweixiong.Util;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by dengweixiong on 2017/8/29.
 */

public class NetUtil {

    private static final String LOCAL_PREFIX = "http://192.168.1.3:8080/Sailfish";
    private static final String MOBILE_PREFIX = "http://10.167.151.237:8080/Sailfish";
    private static final String REMOTE_PREFIX = "";
    private static OkHttpClient client;

    public static Call sendHttpRequest(Context context,String address) {
        String str = LOCAL_PREFIX + address;
        initOkHttpClient(context);
        Request request = new Request.Builder().url(str).method("GET",null).build();
        Call call = client.newCall(request);
        return call;
    }

    private static void initOkHttpClient(Context context) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS);
        client = builder.build();
    }


}