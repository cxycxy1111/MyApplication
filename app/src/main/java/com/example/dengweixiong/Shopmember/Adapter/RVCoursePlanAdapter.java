package com.example.dengweixiong.Shopmember.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dengweixiong.Shopmember.Course.CoursePlan.CoursePlanDetailActivity;
import com.example.dengweixiong.Shopmember.Course.CoursePlan.CoursePlanDetailPrivateActivity;
import com.example.dengweixiong.myapplication.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.tile_course_plan,parent,false);
        final VH viewHolder = new VH(view);
        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int p = viewHolder.getAdapterPosition();
                Map<String,String> map = mapList.get(p);
                String course_type = String.valueOf(map.get("course_type"));
                Intent intent;
                switch (course_type) {
                    case "4":
                        intent = new Intent(parent.getContext(), CoursePlanDetailPrivateActivity.class);
                        break;
                    default:
                        intent = new Intent(parent.getContext(), CoursePlanDetailActivity.class);
                        break;
                }
                intent.putExtra("cp_id",String.valueOf(map.get("courseplan_id")));
                intent.putExtra("course_name",map.get("course_name"));
                intent.putExtra("time",map.get("start_time"));
                context.startActivity(intent);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        VH vh = (VH)holder;
        vh.itemView.setTag(position);
        String start_time = mapList.get(position).get("start_time");
        vh.tv_time.setText(start_time.substring(0,start_time.length()-5));
        vh.tv_name.setText(mapList.get(position).get("course_name"));
        vh.tv_classroom.setText(mapList.get(position).get("classroom_name"));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date_start = null;
        try {
            date_start = simpleDateFormat.parse(start_time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date date_end = null;
        try {
            date_end = simpleDateFormat.parse(mapList.get(position).get("end_time"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long minute = (date_end.getTime() - date_start.getTime())/60/1000;

        vh.tv_last_time.setText(String.valueOf(minute) + "分钟");
    }

    @Override
    public int getItemCount() {
        return mapList.size();
    }

    private static class VH extends RecyclerView.ViewHolder{
        TextView tv_time,tv_classroom,tv_name,tv_last_time;
        View view;
        private VH (View view) {
            super(view);
            this.view = view;
            tv_time = (TextView)view.findViewById(R.id.tv_time_t_course_plan);
            tv_classroom = (TextView)view.findViewById(R.id.tv_classroom_t_course_plan);
            tv_name = (TextView)view.findViewById(R.id.tv_name_t_course_plan);
            tv_last_time = (TextView)view.findViewById(R.id.tv_last_time_t_course_plan);
        }

    }
}
