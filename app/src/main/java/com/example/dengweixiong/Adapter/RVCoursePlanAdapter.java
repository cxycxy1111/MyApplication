package com.example.dengweixiong.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dengweixiong.myapplication.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by dengweixiong on 2017/12/2.
 */

public class RVCoursePlanAdapter extends RecyclerView.Adapter{

    private List<Map<String,String>> mapList = new ArrayList<>();
    private Context context;

    public RVCoursePlanAdapter(List<Map<String,String>> mapList,Context context) {
        this.mapList = mapList;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup vg = (ViewGroup)inflater.inflate(R.layout.tile_course_plan,parent,false);
        return new VH(vg);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        VH vh = (VH)holder;
        vh.itemView.setTag(position);
        vh.tv_time.setText(mapList.get(position).get("start_time"));
        vh.tv_name.setText(mapList.get(position).get("course_name"));
        vh.tv_classroom.setText(mapList.get(position).get("classroom_name"));
        vh.tv_last_time.setText(mapList.get(position).get("last_time"));
        vh.tv_teachers.setText(mapList.get(position).get("teachers"));
    }

    @Override
    public int getItemCount() {
        return mapList.size();
    }

    private static class VH extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tv_time,tv_classroom,tv_name,tv_teachers,tv_last_time;

        private VH (View view) {
            super(view);
            tv_time = (TextView)view.findViewById(R.id.tv_time_t_course_plan);
            tv_classroom = (TextView)view.findViewById(R.id.tv_classroom_t_course_plan);
            tv_name = (TextView)view.findViewById(R.id.tv_name_t_course_plan);
            tv_teachers = (TextView)view.findViewById(R.id.tv_teachers_t_course_plan);
            tv_last_time = (TextView)view.findViewById(R.id.tv_last_time_t_course_plan);
        }

        @Override
        public void onClick(View v) {

        }
    }

}
