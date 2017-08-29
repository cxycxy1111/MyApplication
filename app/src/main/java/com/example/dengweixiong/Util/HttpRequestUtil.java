package com.example.dengweixiong.Util;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by dengweixiong on 2017/8/29.
 */

public class HttpRequestUtil {

    public static void sendHttpRequest(String address,okhttp3.Callback callback) {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);


    }

}
