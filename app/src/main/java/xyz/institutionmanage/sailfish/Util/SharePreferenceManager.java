package xyz.institutionmanage.sailfish.Util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by dengweixiong on 2018/1/28.
 */

public class SharePreferenceManager {

    public SharePreferenceManager() {

    }

    public static String getSharePreferenceValue(Context context,String fileName,String key,int dataType) {
        SharedPreferences preferences = context.getSharedPreferences(fileName,Context.MODE_PRIVATE);
        switch (dataType) {
            case 1:
                return String.valueOf(preferences.getInt(key,0));
            case 2:
                return String.valueOf(preferences.getLong(key,0));
            case 3:
                return String.valueOf(preferences.getString(key,""));
            default:return null;
        }
    }

    /**
     * 储存舞馆信息或教师信息
     * @param file_name 文件名
     * @param key 键
     * @param value 值
     */
    public static void storeSharePreferenceLong(Context context,String file_name,String key,long value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(file_name, Context.MODE_PRIVATE).edit();
        editor.putLong(key,value);
        editor.apply();
    }

    public static void storeSharePreferenceInt(Context context,String file_name,String key,int value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(file_name,Context.MODE_PRIVATE).edit();
        editor.putInt(key,value);
        editor.apply();
    }

    public static void storeSharePreferenceString(Context context,String file_name,String key,String value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(file_name,Context.MODE_PRIVATE).edit();
        editor.putString(key,value);
        editor.apply();
    }
}
