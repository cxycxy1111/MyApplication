package com.alfred.alfredtools;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;

public class SnackBarHandler {

    private static final String TAG = "SnackBarHandler";

    public static void config(Context context, Snackbar snack,boolean isTall) {
        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        Log.d(TAG, "config: density: " + density);
        float height = 12;
        if (isTall) {
            height = (56+12)*density;
        }else {
            height = 12*density;
        }
        addMargins(snack,height,density);
        setRoundBordersBg(context, snack);
        ViewCompat.setElevation(snack.getView(), 6f);
    }

    private static void addMargins(Snackbar snack, float height, float density) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) snack.getView().getLayoutParams();
        int edge = (int) (12*density);
        params.setMargins(edge, edge, edge, (int)height);
        snack.getView().setLayoutParams(params);
    }

    private static void setRoundBordersBg(Context context, Snackbar snackbar) {
        snackbar.getView().setBackground(context.getResources().getDrawable(R.drawable.snack_bar_1));
    }
}