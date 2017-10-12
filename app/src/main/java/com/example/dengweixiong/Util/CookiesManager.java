package com.example.dengweixiong.Util;

import android.app.Application;
import android.content.Context;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * Created by dengweixiong on 2017/10/11.
 */

public class CookiesManager implements CookieJar{

    Context context = new Application().getApplicationContext();

    private final PersistentCookieStore cookieStore = new PersistentCookieStore(context);

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        if (cookies != null && cookies.size() > 0) {
            for (Cookie item : cookies) {
                cookieStore.add(url, item);
            }
        }
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        List<Cookie> cookies = cookieStore.get(url);
        return cookies;
    }
}
