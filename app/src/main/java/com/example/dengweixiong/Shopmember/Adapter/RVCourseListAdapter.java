package com.example.dengweixiong.Shopmember.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dengweixiong.Shopmember.Course.Course.CourseDetailActivity;
import com.example.dengweixiong.myapplication.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by dengweixiong on 2017/9/13.
 */

public class RVCourseListAdapter
        extends RecyclerView.Adapter
        implements View.OnClickListener{

    private List<Map<String,String>> mapList = new ArrayList<>();
    private Activity activity;

    public RVCourseListAdapter(List<Map<String,String>> mapList, Activity activity) {
        this.mapList = mapList;
        this.activity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tile_course,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.courseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            int position = holder.getAdapterPosition();
            Map<String,String> map = mapList.get(position);
            Intent intent = new Intent(parent.getContext(), CourseDetailActivity.class);
            intent.putExtra("name",map.get("name"));
            intent.putExtra("id",String.valueOf(map.get("id")));
            intent.putExtra("type",String.valueOf(map.get("type")));
            activity.startActivityForResult(intent,1);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ViewHolder holder1 = (ViewHolder)holder;
        Map<String,String> map = mapList.get(position);
        holder1.tv_name.setText(map.get("name"));
        holder1.tv_time.setText(map.get("time"));
        holder1.tv_supportedcard.setText(map.get("supportedcard"));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        View courseView;
        TextView tv_name;
        TextView tv_time;
        TextView tv_supportedcard;
        public ViewHolder(View view) {
            super(view);
            courseView = view;
            tv_name = (TextView)view.findViewById(R.id.tv_name_tile_course);
            tv_time = (TextView)view.findViewById(R.id.tv_time_tile_course);
            tv_supportedcard = (TextView)view.findViewById(R.id.tv_supportedcard_tile_course);
        }

        @Override
        public void onClick(View v) {

        }
    }

    @Override
    public int getItemCount() {
        return mapList.size();
    }

    @Override
    public void onClick(View v) {

    }

}
