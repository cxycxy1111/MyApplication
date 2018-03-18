package xyz.institutionmanage.sailfish.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.institutionmanage.sailfish.R;

/**
 * Created by dengweixiong on 2018/3/11.
 */

public class RVMemberMyBookAdapter extends RecyclerView.Adapter implements View.OnClickListener{

    private Context context;
    private RVMemberMyBookAdapter.OnItemClickListener onItemClickListener = null;
    private VH vh;
    private List<Map<String,String>> arrayList = new ArrayList<>();

    public RVMemberMyBookAdapter(Context context,List<Map<String,String>> mapArrayList) {
        this.context = context;
        this.arrayList = mapArrayList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.tile_a_m_mybook,parent,false);
        view.setOnClickListener(this);
        VH vh = new VH(view);
        this.vh = vh;
        vh.btn_attend.setOnClickListener(this);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Map<String,String> map = new HashMap<>();
        map = arrayList.get(position);
        final VH vh = (VH)holder;
        vh.itemView.setTag(position);
        vh.btn_attend.setTag(position);
        vh.tv_course_name.setText(map.get("c_name"));
        vh.tv_classroom.setText(map.get("cr_name"));
        String start_time = String.valueOf(map.get("start_time"));
        String end_time = String.valueOf(map.get("end_time"));
        start_time = start_time.substring(0,start_time.length()-2);
        end_time = end_time.substring(0,end_time.length()-2);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date s_date = simpleDateFormat.parse(start_time);
            Date e_date = simpleDateFormat.parse(end_time);
            long last_time = (e_date.getTime()-s_date.getTime())/(60*1000);
            vh.tv_last_time.setText(String.valueOf(last_time) + "分钟");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        end_time = end_time.substring(0,end_time.length()-3);
        start_time = start_time.substring(0,start_time.length()-3);
        vh.tv_start_time.setText(start_time);
        vh.tv_teachers.setText(map.get("teacher"));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    private static class VH extends RecyclerView.ViewHolder {
        TextView tv_course_name,tv_start_time,tv_last_time,tv_classroom,tv_teachers;
        Button btn_attend;
        View view;
        public VH(View view) {
            super(view);
            tv_course_name = (TextView)view.findViewById(R.id.tv_course_name_tile_a_m_mybook);
            tv_start_time = (TextView)view.findViewById(R.id.tv_start_time_tile_a_m_mybook);
            tv_last_time = (TextView)view.findViewById(R.id.tv_last_time_tile_a_m_mybook);
            tv_classroom = (TextView)view.findViewById(R.id.tv_classroom_tile_a_m_mybook);
            tv_teachers = (TextView)view.findViewById(R.id.tv_teacher_tile_a_m_mybook);
            btn_attend = (Button)view.findViewById(R.id.btn_attend_tile_a_m_mybook);
        }

        public Button getBtn_attend() {
            return btn_attend;
        }

        public void setBtn_attend(Button btn_attend) {
            this.btn_attend = btn_attend;
        }
    }

    //内部接口
    public interface OnItemClickListener {
        void onItemClick(View view,int position);
        void onButtonClick(View view,int position);
    }

    public void setOnItemClickListener(RVMemberMyBookAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_attend_tile_a_m_mybook:
                onItemClickListener.onButtonClick(v,(int)v.getTag());
                break;
            default:
                onItemClickListener.onItemClick(v,(int)v.getTag());
                break;
        }
    }

    public VH getVh() {
        return vh;
    }

    public void setVh(VH vh) {
        this.vh = vh;
    }
}
