package com.alfred.alfredtools;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ViewHandler {

    private static final String TAG ="ViewHandler";

    /**
     * 显示Toast
     * @param targetActivity
     * @param string
     */
    public static void toastShow(final Activity targetActivity, final String string) {
        Toast.makeText(targetActivity,string,Toast.LENGTH_LONG).show();
    }

    /**
     * 显示Toast，随后结束活动
     * @param targetActivity
     * @param string
     */
    public static void toastShowAndDestroyApp(final Activity targetActivity,final String string) {
        toastShow(targetActivity,string);
        targetActivity.finish();
    }

    /**
     * 处理网络请求失败时的显示逻辑
     * @param activity
     */
    public static void dealWithReqFail(BaseActivity activity) {
        progressBarHide(activity);
        toastShow(activity,NetUtil.CANT_CONNECT_INTERNET);
    }

    /**
     * 显示提示框
     * @param targetActivity
     * @param title
     * @param content
     */
    public static void alertShow(final Activity targetActivity, final String title,final String content) {
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

    /**
     * 显示提示框
     * @param targetActivity
     */
    public static void alertShowAndExitApp(final Activity targetActivity) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(targetActivity);
        dialog.setTitle("登录过期提示");
        dialog.setCancelable(false);
        dialog.setMessage(BaseActivity.ALERT_STATUS_SESSION_EXPIRED);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityMgr.removeAllActivity();
            }
        });
        dialog.show();
    }

    public static void toastShowAuthorizeFail(final Activity targetActivity) {
        Toast.makeText(targetActivity,BaseActivity.OPERATE_MANAGE_STATUS_STATUS_AUTHORIZE_FAIL,Toast.LENGTH_SHORT)
                .show();
    }

    public static void progressBarHide(final BaseActivity activity) {
        activity.getProgressBar(activity).setVisibility(View.GONE);
    }

    public static void progressBarHide(final Activity activity,final ProgressBar progressBar) {
        progressBar.setVisibility(View.GONE);
    }

    public static void progressBarShow(final Activity activity, final ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);
    }

    public static void progressBarShow(final BaseActivity activity) {
        activity.getProgressBar(activity).setVisibility(View.VISIBLE);
    }


    public static void initToolbar(final BaseActivity activity, Toolbar toolbar, final String title,final int toolbar_id) {
        toolbar = (Toolbar)activity.findViewById(toolbar_id);
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle(title);
    }

    public static void initToolbarWithBackButton(final BaseActivity activity,Toolbar toolbar,final String title,final int toolbar_id) {
        toolbar = (Toolbar)activity.findViewById(toolbar_id);
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle(title);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public static void initToolbarWithBackButton(final BaseActivity activity,Toolbar toolbar,final CharSequence title,final int toolbar_id) {
        toolbar = (Toolbar)activity.findViewById(toolbar_id);
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle(title);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public static void initToolbarWithBackButton(final BaseActivity activity,Toolbar toolbar,final int title,final int toolbar_id) {
        toolbar = (Toolbar)activity.findViewById(toolbar_id);
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle(title);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public static void initToolbar(final BaseActivity activity,Toolbar toolbar,final int title,final int toolbar_id) {
        toolbar = (Toolbar)activity.findViewById(toolbar_id);
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle(title);
    }

    /**
     * 跳转至其他活动
     * @param context
     * @param c
     */
    public static void switchToActivity(Context context, Class<?> c) {
        Intent intent = new Intent(context,c);
        context.startActivity(intent);
    }

    /**
     * 鉴权失败时退出界面
     * @param targetActivity
     */
    public static void exitAcitivityDueToAuthorizeFail(final Activity targetActivity) {
        Toast.makeText(targetActivity,BaseActivity.OPERATE_VIEW_STATUS_STATUS_AUTHORIZE_FAIL,Toast.LENGTH_SHORT).show();
        targetActivity.finish();
    }

    /**
     * 隐藏View
     * @param views
     */
    public static void viewHide(View[] views) {
        for (int i = 0;i < views.length;i++) {
            views[i].setVisibility(View.GONE);
        }
    }

    public static void snackbarShowTall(Context context,View view,String str) {
        Snackbar snackbar = Snackbar.make(view,str,Snackbar.LENGTH_SHORT);
        snackbarDeal(context,snackbar,true);
    }

    public static void snackbarShowLow(Context context,View view,String str) {
        Snackbar snackbar = Snackbar.make(view,str,Snackbar.LENGTH_LONG);
        snackbarDeal(context,snackbar,false);
    }

    private static void snackbarDeal(Context context,Snackbar snackbar,boolean isTall) {
        SnackBarHandler.config(context,snackbar,isTall);
        snackbar.show();
    }

}
