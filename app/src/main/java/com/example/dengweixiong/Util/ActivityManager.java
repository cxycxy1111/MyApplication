package com.example.dengweixiong.Util;

import android.app.Activity;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dengweixiong on 2017/8/29.
 */

public class ActivityManager{

    public static List<Activity> activityList = new ArrayList<>();

    public static void addActivity(Activity activity) {
        activityList.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activityList.remove(activity);
    }

    public static void removeAllActivity() {
        for (Activity activity:activityList) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

}
