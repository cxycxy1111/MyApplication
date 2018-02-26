package xyz.institutionmanage.sailfish.Shopmember.Adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import xyz.institutionmanage.sailfish.R;

/**
 * Created by dengweixiong on 2017/9/13.
 */

public class RVCourseListAdapter
        extends RecyclerView.Adapter implements View.OnClickListener{

    private static final String TAG = "RVCourseListAdapter:";
    private List<Map<String,String>> mapList = new ArrayList<>();
    private OnItemClickListener onItemClickListener = null;
    private Activity activity;

    //内部接口
    public interface OnItemClickListener {
        void onItemClick(View view,int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public RVCourseListAdapter(List<Map<String,String>> mapList, Activity activity) {
        this.mapList = mapList;
        this.activity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tile_course,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ViewHolder holder1 = (ViewHolder)holder;
        Map<String,String> map = mapList.get(position);
        holder1.tv_name.setText(map.get("name"));
        ((ViewHolder) holder).itemView.setTag(position);
        String l_time = String.valueOf(map.get("last_time"));
        if (l_time.equals("") || l_time.equals(null) || l_time.equals("null")) {
            holder1.tv_time.setText("");
        }else {
            holder1.tv_time.setText(l_time + "分钟");
        }
        if (map.get("supportedcard").equals("") | map.get("supportedcard").equals(null)){
            holder1.tv_supportedcard.setText("未设置所支持的卡");
        }else {
            holder1.tv_supportedcard.setText("支持" + map.get("supportedcard"));
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        View courseView;
        TextView tv_name,tv_time,tv_supportedcard;
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
        onItemClickListener.onItemClick(v,(int)v.getTag());
    }
}
