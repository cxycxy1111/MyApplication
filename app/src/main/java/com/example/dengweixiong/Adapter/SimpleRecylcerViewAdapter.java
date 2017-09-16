package com.example.dengweixiong.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.dengweixiong.myapplication.R;

/**
 * Created by dengweixiong on 2017/9/7.
 */

public class SimpleRecylcerViewAdapter extends RecyclerView.Adapter implements View.OnClickListener{

    private String[] strings = null;
    private OnItemClickListener onItemClickListener = null;

    //构造器
    public SimpleRecylcerViewAdapter(String[] strings) {
        this.strings = strings;
    }

    //内部类
    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        public ViewHolder(View view) {
            super(view);
            textView = (TextView)view.findViewById(R.id.simple_list_view_with_icon_text);
        }
    }
    //内部接口
    public interface OnItemClickListener {
        void onItemClick(View view,int position);
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onClick(View v) {
        onItemClickListener.onItemClick(v,(int)v.getTag());
    }

    //创建Item视图
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tile_simple_list_view_no_icon, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setOnClickListener(this);
        return viewHolder;
    }
    //绑定Item视图
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final ViewHolder holder1 = (ViewHolder)holder;
        holder1.textView.setText(strings[position]);
        holder1.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return strings.length;
    }

}