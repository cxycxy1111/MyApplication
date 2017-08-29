package com.example.dengweixiong.Util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by dengweixiong on 2017/8/22.
 */

public class MyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context,"Network Changed",Toast.LENGTH_LONG).show();
    }
}
