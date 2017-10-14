package com.example.dengweixiong.Util;

import android.util.Log;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import okhttp3.Response;

/**
 * Created by dengweixiong on 2017/10/8.
 */

public class JsonHandler {

    public static final String TAG = "JSONHandler:";

    public static List<HashMap<String,String>> strToListMap(String str,String keys[]) {
        List<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
        if(str==null||str.equals("")||!str.startsWith("[")||!str.endsWith("]")){
            return list;
        }
        JSONArray ja = JSONArray.fromObject(str);
        JSONObject jo = null;
        HashMap<String,String> map = null;
        for(int i=0;i<ja.size();i++) {
            jo = ja.getJSONObject(i);
            for (int j = 0;j < keys.length;i++) {
                str = jo.getString(keys[i]);
                map.put(keys[i],str);
                list.add(map);
            }
        }
        return list;
    }

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
