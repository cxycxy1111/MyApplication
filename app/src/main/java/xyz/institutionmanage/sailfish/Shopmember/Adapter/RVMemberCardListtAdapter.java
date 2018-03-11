package xyz.institutionmanage.sailfish.Shopmember.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import xyz.institutionmanage.sailfish.R;

/**
 * Created by dengweixiong on 2017/11/22.
 */

public class RVMemberCardListtAdapter extends RecyclerView.Adapter implements View.OnClickListener{

    private static final String TAG = "RVMemberCardListtAdapter";
    private List<Map<String,String>> mapList;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public RVMemberCardListtAdapter(Context context, List<Map<String,String>> mapList) {
        this.context = context;
        this.mapList = mapList;
        Log.d(TAG, "RVMemberCardListtAdapter: " + mapList.toString());
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
        Map<String,String> map = mapList.get(position);
        vh.itemView.setTag(position);
        vh.tv_main.setText(map.get("name"));
        String str_type = String.valueOf(map.get("type"));
        if (str_type.equals("1")) {
            vh.tv_hint.setText(String.valueOf(map.get("balance")) + "元");
        }else if (str_type.equals("2")) {
            vh.tv_hint.setText(String.valueOf(map.get("balance")) + "次");
        }else {
            vh.tv_hint.setText("");
        }

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

    public void setOnItemClickListener(RVMemberCardListtAdapter.OnItemClickListener onItemClickListener) {
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
