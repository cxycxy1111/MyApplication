package com.example.dengweixiong.Shopmember.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dengweixiong.myapplication.R;

import java.util.List;
import java.util.Map;

/**
 * Created by dengweixiong on 2017/11/22.
 */

public class RVWithHintAdapter extends RecyclerView.Adapter implements View.OnClickListener{

    private static final String TAG = "RVWithHintAdapter";
    private List<Map<String,String>> mapList;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public RVWithHintAdapter(Context context,List<Map<String,String>> mapList) {
        this.context = context;
        this.mapList = mapList;
        Log.d(TAG, "RVWithHintAdapter: " + mapList.toString());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup vg = (ViewGroup)inflater.inflate(R.layout.tile_list_with_hint,parent,false);
        vg.setOnClickListener(this);
        return new VH(vg);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final VH vh = (VH)holder;
        vh.itemView.setTag(position);
        vh.tv_main.setText(mapList.get(position).get("name"));
        vh.tv_hint.setText(String.valueOf(mapList.get(position).get("balance")));
    }

    @Override
    public int getItemCount() {
        return mapList.size();
    }

    private static class VH extends RecyclerView.ViewHolder {
        TextView tv_main;
        TextView tv_hint;
        private VH(View view){
            super(view);
            tv_main = (TextView)itemView.findViewById(R.id.tv_main_text_list_with_hint);
            tv_hint = (TextView)itemView.findViewById(R.id.tv_hint_text_list_with_hint);
        }

    }

    public interface OnItemClickListener {
        void onItemClick(View view,int position);
    }

    public void setOnItemClickListener(RVWithHintAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    @Override
    public void onClick(View v) {
        onItemClickListener.onItemClick(v,(int)v.getTag());
    }
}
