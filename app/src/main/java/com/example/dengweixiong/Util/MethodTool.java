package com.example.dengweixiong.Util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by dengweixiong on 2017/10/14.
 */

public class MethodTool{

    /**
     * 隐藏View
     * @param targetActivity
     * @param view
     */
    public static void hideView(final Activity targetActivity, final View view) {
        targetActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 显示Toast
     * @param targetActivity
     * @param string
     */
    public static void showToast(final Activity targetActivity, final String string) {
        targetActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(targetActivity.getApplicationContext(),string,Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * 显示提示框
     * @param targetActivity
     * @param title
     * @param content
     */
    public static void showAlert(final Activity targetActivity, final String title,final String content) {
        targetActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder dialog = new AlertDialog.Builder(targetActivity);
                dialog.setTitle(title);
                dialog.setCancelable(false);
                dialog.setMessage(content);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                dialog.show();
            }
        });
    }

    public static void hideProgressBar(final Activity activity, final ProgressBar progressBar) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 获取long类型的preferenceShare
     * @param context
     * @param fileName
     * @param key
     * @return
     */
    public static long preGetLong(Context context,String fileName, String key) {
        SharedPreferences preferences = context.getSharedPreferences(fileName,context.MODE_PRIVATE);
        return preferences.getLong(key,0);
    }

    /**
     * 获取String类型的preferenceShare
     * @param context
     * @param fileName
     * @param key
     * @return
     */
    public static String preGetString(Context context,String fileName,String key) {
        SharedPreferences preferences = context.getSharedPreferences(fileName,context.MODE_PRIVATE);
        return preferences.getString(key,null);
    }

    /**
     * 跳转至其他活动
     * @param context
     * @param c
     */
    public static void jumpToActivity(Context context,Class<?> c) {
        Intent intent = new Intent(context,c);
        context.startActivity(intent);
    }

    public static ArrayList<String> getKeys(Map<String,String> map) {
        ArrayList<String> list = new ArrayList<>();
        Set<String> set = map.keySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next().toString());
        }
        return list;
    }

    public static ArrayList<String> getValues(Map<String,String> map,ArrayList keys) {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0;i < keys.size();i++) {
            String value = map.get(keys.get(i));
            list.add(value);
        }
        return list;
    }

    public static void sortListMap(List<Map<String,String>> list) {
        if (null != list&& list.size()>0) {
            Collections.sort(list,new Comparator<Map>() {
                @Override
                public int compare(Map o1, Map o2) {
                    Collator collator = Collator.getInstance();
                    return collator.getCollationKey(o1.get("name").toString()).compareTo(collator.getCollationKey(o2.get("name").toString()));
                }
            });
        }
    }

    public static void sort(List<String> list) {
        if (list != null && list.size() > 0) {
            Collections.sort(list, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    Collator collator = Collator.getInstance();
                    return  collator.getCollationKey(o1.toString()).compareTo(collator.getCollationKey(o2.toString()));
                }
            });
        }
    }

}
