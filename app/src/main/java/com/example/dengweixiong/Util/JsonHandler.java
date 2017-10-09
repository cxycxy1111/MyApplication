package com.example.dengweixiong.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dengweixiong on 2017/10/8.
 */

public class JsonHandler {

    public static List<HashMap<String,String>> getstr(String str) throws JSONException {
        List<HashMap<String,String>> res = new ArrayList<HashMap<String,String>>();
        if(str==null||str.equals("")||!str.startsWith("[")||!str.endsWith("]")){
            return res;
        }
        JSONArray ja = new JSONArray(str);
        JSONObject jo = null;
        HashMap<String,String>map = null;
        String pic = null;
        String order = null;
        for(int i=0;i<ja.length();i++) {
            jo = ja.getJSONObject(i);
            pic = jo.getString("pic");
            order = jo.getString("order");
            if (pic != null && !"".equals(pic) && order != null && !"".equals(order)) {
                map = new HashMap<String, String>();
                map.put("order", order);
                map.put("pic", pic.indexOf(".jpg") != -1 ? pic : pic + ".jpg");
                res.add(map);
            }
        }
        return res;
    }
}
