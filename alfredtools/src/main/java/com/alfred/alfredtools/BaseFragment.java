package com.alfred.alfredtools;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

/**
 * Created by dengweixiong on 2018/3/3.
 */

public class BaseFragment extends Fragment {

    private Context context;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void createProgressBar(FragmentActivity activity) {
        context = activity;
        FrameLayout rootFrameLayout = (FrameLayout) activity.findViewById(android.R.id.content);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(128, 128);
        layoutParams.gravity = Gravity.CENTER;
        progressBar = new ProgressBar(context);
        progressBar.setLayoutParams(layoutParams);
        progressBar.setBackgroundColor(Color.WHITE);
        progressBar.setVisibility(View.GONE);
        rootFrameLayout.addView(progressBar);
    }

    public ProgressBar getProgressBar(FragmentActivity activity) {
        if (progressBar == null) {
            createProgressBar(activity);
        }
        return progressBar;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }
}
