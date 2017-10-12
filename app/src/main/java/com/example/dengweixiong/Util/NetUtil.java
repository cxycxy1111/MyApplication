package com.example.dengweixiong.Util;

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

    public static void sendHttpRequest(String address,okhttp3.Callback callback) {
        String str = LOCAL_PREFIX + address;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(str).build();
        client.newCall(request).enqueue(callback);
    }

}
