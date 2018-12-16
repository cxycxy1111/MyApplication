package xyz.instmgr.sailfish.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import xyz.instmgr.sailfish.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dengweixiong on 2017/12/17.
 */

public class RVSimpleAdapterWithCheck extends RecyclerView.Adapter{

    private List<String> strings = new ArrayList<>();
    private Map<Integer,Boolean> checkStatusMap = new HashMap<>();
    private RVSimpleAdapter.OnItemClickListener onItemClickListener = null;

    //构造器
    public RVSimpleAdapterWithCheck(List<String> strings) {
        this.strings = strings;
        initCheckboxStatusMap();
    }

    //内部类
    private static class ViewHolder extends RecyclerView.ViewHolder{
        CheckBox checkBox;
        ViewHolder(View view) {
            super(view);
            checkBox = (CheckBox) view.findViewById(R.id.cb_simple_list_view_with_checkbox);
        }
    }

    //创建Item视图
    @Override
    public RVSimpleAdapterWithCheck.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tile_simple_list_view_with_checkbox, parent, false);
        RVSimpleAdapterWithCheck.ViewHolder viewHolder = new RVSimpleAdapterWithCheck.ViewHolder(view);
        return viewHolder;
    }
    //绑定Item视图
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final RVSimpleAdapterWithCheck.ViewHolder holder1 = (RVSimpleAdapterWithCheck.ViewHolder)holder;
        holder1.checkBox.setText(strings.get(position));
        holder1.itemView.setTag(position);
        holder1.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkStatusMap.put(position,isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return strings.size();
    }

    //初始化复选框状态
    private void initCheckboxStatusMap() {
        for (int i = 0;i < strings.size();i ++) {
            checkStatusMap.put(i,false);
        }
    }

    public Map<Integer, Boolean> getCheckStatusMap() {
        return checkStatusMap;
    }

    private void setCheckStatusMap(Map<Integer, Boolean> checkStatusMap) {
        this.checkStatusMap = checkStatusMap;
    }
}
