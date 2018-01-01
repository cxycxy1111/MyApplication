package com.example.dengweixiong.Util;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by dengweixiong on 2017/8/29.
 */

public class NetUtil {

    //使用家里网络进行调试
    private static final String LOCAL_PREFIX = "http://10.0.0.2:8080/Sailfish";
    //使用冯静霞家的网络进行调试
    private static final String FJX_PREFIX = "http://192.168.1.104:8080/Sailfish";
    //使用省移动网络进行调试
    private static final String MOBILE_PREFIX = "http://10.167.151.237:8080/Sailfish";
    //使用iPhone手机网络进行调试
    private static final String IPHONE_PREFIX = "http://172.20.10.3:8080/Sailfish";
    //使用正式服务器进行调试
    private static final String REMOTE_PREFIX = "http://39.104.72.97:8080/Sailfish";
    private static OkHttpClient client;

    /**
     *
     * @param context Activity
     * @param address URL
     * @param callback 回调
     */
    public static void sendHttpRequest(Context context,String address,Callback callback) {
        String str = LOCAL_PREFIX + address;
        initOkHttpClient(context);
        Request request = new Request.Builder().url(str).method("GET",null).build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public static void sendPostHttpRequest(Context context, HashMap<String,Object> map,Callback callback) {
        FormBody.Builder builder = new FormBody.Builder();
        initOkHttpClient(context);
        for (Map.Entry<String,Object> entry:map.entrySet()) {
            builder.add(entry.getKey(),String.valueOf(entry.getValue()));
        }
        RequestBody body = builder.build();
        Request request = new Request.Builder().url(REMOTE_PREFIX).post(body).build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    /**
     *
     * @param context Activity
     */
    private static void initOkHttpClient(Context context) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS);
        client = builder.build();
    }


}
