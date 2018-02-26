package xyz.institutionmanage.sailfish.Util;

import android.app.Activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by dengweixiong on 2017/8/29.
 */

public class ActivityManager{

    public static List<Activity> activityList = new ArrayList<>();

    public static void addActivity(Activity activity) {
        if (!activityList.contains(activity)) {
            activityList.add(activity);
        }
    }

    public static void removeActivity(Activity activity) {
        if (activityList.contains(activity)) {
            activityList.remove(activity);
        }
    }

    public static void removeAllActivity() {
        Iterator<Activity> iterator = activityList.iterator();
        while (iterator.hasNext()) {
            Activity activity = iterator.next();
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

}
