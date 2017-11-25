package com.example.dengweixiong.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;
import java.util.Map;

/**
 * Created by dengweixiong on 2017/11/22.
 */

public class RVWithHintAdapter extends RecyclerView.Adapter {

    private List<Map<String,String>> mapList;

    private static final int HEADER = 1;
    private static final int TEXT = 2;
    private static final int EDITTEXT = 3;
    private static final int DATE = 4;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
