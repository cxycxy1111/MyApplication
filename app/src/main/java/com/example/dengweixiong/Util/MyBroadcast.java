package com.example.dengweixiong.Util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by dengweixiong on 2017/8/29.
 */

public class MyBroadcast extends BroadcastReceiver{

    public MyBroadcast() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context,"Successed",Toast.LENGTH_LONG).show();
    }
}
