package xyz.institutionmanager.sailfish.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import xyz.institutionmanager.sailfish.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dengweixiong on 2018/3/17.
 */

public class RVMemberCardConsumeLogAdapter extends RecyclerView.Adapter{

    private List<Map<String,String>> mapList;
    private Context context;
    private VH vh;


    public RVMemberCardConsumeLogAdapter(List<Map<String,String>> list,Context context) {
        this.mapList = list;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.tile_member_card_consume_log,parent,false);
        RVMemberCardConsumeLogAdapter.VH vh = new RVMemberCardConsumeLogAdapter.VH(view);
        this.vh = vh;
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Map<String,String> map = new HashMap<>();
        map = mapList.get(position);
        final VH vh = (RVMemberCardConsumeLogAdapter.VH)holder;
        vh.itemView.setTag(position);
        StringBuilder builder = new StringBuilder();
        if (String.valueOf(map.get("operation")).equals("1")){
            vh.tv_consume.setTextColor(Color.GREEN);
            builder.append("+");
        }else if (String.valueOf(map.get("operation")).equals("2")) {
            builder.append("-");
            vh.tv_consume.setTextColor(Color.RED);
        }else if (String.valueOf(map.get("operation")).equals("3")){
            builder.append("-");
            vh.tv_consume.setTextColor(Color.RED);
        }else {
            builder.append("+");
            vh.tv_consume.setTextColor(Color.GREEN);
        }
        if (String.valueOf(map.get("type")).equals("1")) {
            builder.append(String.valueOf(map.get("consume")) + "元");
        }else if (String.valueOf(map.get("type")).equals("2")) {
            builder.append(String.valueOf(map.get("consume")) + "次");
        }else {
            vh.tv_consume.setText("-");
        }
        vh.tv_consume.setText(builder.toString());

        if (String.valueOf(map.get("operation")).equals("1")) {
            vh.tv_type.setText("充值");
        }else if (String.valueOf(map.get("operation")).equals("2")) {
            vh.tv_type.setText("扣费");
        }else if (String.valueOf(map.get("operation")).equals("3")){
            vh.tv_type.setText("签到");
        }else {
            vh.tv_type.setText("撤销签到");
        }
        String date = map.get("consume_time");
        date = date.substring(0,date.length()-2);
        vh.tv_time.setText(date);
    }

    @Override
    public int getItemCount() {
        return mapList.size();
    }

    private static class VH extends RecyclerView.ViewHolder {
        TextView tv_consume,tv_type,tv_time;
        View view;
        public VH(View view) {
            super(view);
            tv_consume = (TextView)view.findViewById(R.id.balance_t_member_card_consume_log);
            tv_type = (TextView)view.findViewById(R.id.tv_type_t_member_card_consume_log);
            tv_time = (TextView)view.findViewById(R.id.tv_time_t_member_card_consume_log);
        }
    }
}
