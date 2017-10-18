package com.example.dengweixiong.Util;

import android.util.Log;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Response;

/**
 * Created by dengweixiong on 2017/10/8.
 */

public class JsonHandler {

    public static final String TAG = "JSONHandler:";

    /**
     * String转为List<HashMap<String>>
     * @param response
     * @param keys
     * @return
     */
    public static ArrayList<Map<String,String>> strToListMap(Response response,String keys[]) throws IOException {
        String str = response.body().string();
        ArrayList<Map<String,String>> list = new ArrayList<>();
        JSONArray ja = JSONArray.fromObject(str);
        for(int i=0;i<ja.size();i++) {
            JSONObject jo = ja.getJSONObject(i);
            Map<String,String> map = new HashMap<>();
            map = jo;

            list.add(map);
        }
        return list;
    }

    /**
     * string转HashMap
     * @param response
     * @return
     * @throws IOException
     */
    public static HashMap<String,String> strToMap(Response response) throws IOException {
        String str = response.body().string();
        HashMap<String,String> hashMap = new HashMap<String,String>();
        JSONObject jsonObject = JSONObject.fromObject(str);
        Iterator<String> iterator = jsonObject.keys();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            String value = jsonObject.getString(key);
            hashMap.put(key,value);
        }
        return hashMap;
    }


}
