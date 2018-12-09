package xyz.institutionmanage.sailfish.Util;

import android.content.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by dengweixiong on 2017/8/29.
 */

public class NetUtil {

    //使用家里网络进行调试
    private static final String LOCAL = "http://10.0.0.7:8080";
    //使用家里LINUX机器网络进行调试
    private static final String LOCAL_LINUX= "http://10.0.0.8:8080/Sailfish";
    //使用冯静霞家的网络进行调试
    private static final String FENGJINGXIA_HOME = "http://192.168.1.112:8080/Sailfish";
    //使用省移动网络进行调试
    private static final String CHINA_MOBILE = "http://10.167.151.237:8080/Sailfish";
    //使用iPhone手机网络进行调试
    private static final String ALFRED_IPHONE = "http://172.20.10.3:8080/Sailfish";
    //使用正式服务器进行调试
    private static final String REMOTE = "http://39.104.72.97:8080/Sailfish";

    public static final String SELECTED_HOST = REMOTE;

    private static OkHttpClient client;

    /**
     *
     * @param context Activity
     * @param address URL
     * @param callback 回调
     */
    public static void sendHttpRequest(Context context,String address,Callback callback) {
        String str = SELECTED_HOST + address;
        initOkHttpClient(context);
        Request request = new Request.Builder().url(str).method("GET",null).build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public static void sendPostHttpRequest(Context context, String url,HashMap<String,Object> map,Callback callback) {
        FormBody.Builder builder = new FormBody.Builder();
        initOkHttpClient(context);
        for (Map.Entry<String,Object> entry:map.entrySet()) {
            builder.add(entry.getKey(),String.valueOf(entry.getValue()));
        }
        String str = SELECTED_HOST + url;
        RequestBody body = builder.build();
        Request request = new Request.Builder().url(str).post(body).build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public static void sendPost(Context context, String url,HashMap<String,String> map,Callback callback) {
        FormBody.Builder builder = new FormBody.Builder();
        initOkHttpClient(context);
        for (Map.Entry<String,String> entry:map.entrySet()) {
            builder.add(entry.getKey(),String.valueOf(entry.getValue()));
        }
        String str = SELECTED_HOST + url;
        RequestBody body = builder.build();
        Request request = new Request.Builder().url(str).post(body).build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    /**
     *
     * @param context Activity
     */
    private static void initOkHttpClient(Context context) {
        final Map<String,List<Cookie>> cookieStore = new HashMap<>();
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS).cookieJar(new CookiesManager(context));
        client = builder.build();
    }

}
