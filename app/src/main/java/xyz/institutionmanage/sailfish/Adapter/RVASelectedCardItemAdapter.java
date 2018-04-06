package xyz.institutionmanage.sailfish.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.institutionmanage.sailfish.R;
import xyz.institutionmanage.sailfish.Util.MethodTool;

public class RVASelectedCardItemAdapter extends RecyclerView.Adapter implements View.OnClickListener{

    private int int_selected_pos =  -1;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private List<Map<String,String>> mapList = new ArrayList<>();
    private List<Map<String,String>> mapList_after = new ArrayList<>();

    public RVASelectedCardItemAdapter(Context context, List<Map<String, String>> mapList) {
        this.context = context;
        this.mapList = mapList;
        this.mapList_after = this.mapList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = (View)inflater.inflate(R.layout.tile_select_card_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setOnClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ViewHolder viewHolder = (ViewHolder)holder;
        Map<String,String> map = new HashMap<>();
        map = mapList_after.get(position);
        viewHolder.radioButton.setTag(position);
        viewHolder.itemView.setTag(position);
        viewHolder.rl_parent.setTag(position);
        viewHolder.tv_card_name.setText(map.get("name"));
        if (map.get("isChecked").equals("0")) {
            viewHolder.radioButton.setChecked(false);
        }else {
            viewHolder.radioButton.setChecked(true);
        }
        final StringBuilder builder = new StringBuilder();
        String start_time = map.get("start_time");
        String expired_time = map.get("expired_time");
        start_time = start_time.substring(0,start_time.length()-11);
        expired_time = expired_time.substring(0,expired_time.length()-11);
        int type = Integer.parseInt(String.valueOf(map.get("type")));
        switch (type) {
            case 1:
                viewHolder.tv_balance.setText("余额:" + String.valueOf(map.get("balance")) + "元");
                viewHolder.tv_price.setText("售价:" + String.valueOf(map.get("price")) + "元");
                break;
            case 2:
                viewHolder.tv_balance.setText("余额:" + String.valueOf(map.get("balance")) + "次");
                viewHolder.tv_price.setText("售价:" + String.valueOf(map.get("price")) + "元");
                break;
            case 3:
                viewHolder.tv_balance.setText("0");
                viewHolder.tv_price.setText("售价:" + String.valueOf(map.get("price")) + "元");
        }
        viewHolder.tv_start_time.setText("生效时间:" + start_time);
        viewHolder.tv_expired_time.setText("失效时间:" + expired_time);
        viewHolder.radioButton.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return mapList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rl_parent;
        RadioButton radioButton;
        TextView tv_card_name;
        TextView tv_balance;
        TextView tv_price;
        TextView tv_start_time;
        TextView tv_expired_time;

        public ViewHolder(View itemView) {
            super(itemView);
            rl_parent = (RelativeLayout)itemView.findViewById(R.id.rl_parent_t_select_card_item);
            radioButton = (RadioButton)itemView.findViewById(R.id.rb_t_select_card_item);
            tv_card_name = (TextView)itemView.findViewById(R.id.tv_card_name_t_select_card_item);
            tv_balance = (TextView)itemView.findViewById(R.id.tv_balance_t_select_card_item);
            tv_price = (TextView)itemView.findViewById(R.id.tv_price_t_select_card_item);
            tv_expired_time = (TextView)itemView.findViewById(R.id.tv_expired_time_t_select_card_item);
            tv_start_time = (TextView)itemView.findViewById(R.id.tv_start_time_t_select_card_item);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    @Override
    public void onClick(View v) {
        int pos = (int)v.getTag();
        if (!mapList_after.get(pos).get("isChecked").equals("1")) {
            for (int i = 0;i < mapList_after.size();i++) {
                Map<String,String> hashMap = new HashMap<>();
                hashMap = mapList_after.get(i);
                mapList_after.remove(i);
                hashMap.put("isChecked","0");
                mapList_after.add(i,hashMap);
            }
            Map<String,String> map1 = new HashMap<>();
            map1 = mapList_after.get(pos);
            mapList_after.remove(pos);
            map1.put("isChecked","1");
            mapList_after.add(pos,map1);
        }
        int_selected_pos = pos;
        notifyDataSetChanged();
    }

    public List<Map<String, String>> getMapList_after() {
        return mapList_after;
    }

    public void setMapList_after(List<Map<String, String>> mapList_after) {
        this.mapList_after = mapList_after;
    }

    public int getInt_selected_pos() {
        return int_selected_pos;
    }

    public void setInt_selected_pos(int int_selected_pos) {
        this.int_selected_pos = int_selected_pos;
    }
}
