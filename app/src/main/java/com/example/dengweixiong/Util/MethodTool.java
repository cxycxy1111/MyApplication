package com.example.dengweixiong.Util;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

/**
 * Created by dengweixiong on 2017/10/14.
 */

public class MethodTool {

    public static void showToast(final Activity targetActivity, final String string) {
        targetActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(targetActivity.getApplicationContext(),string,Toast.LENGTH_LONG).show();
            }
        });
    }

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

}
