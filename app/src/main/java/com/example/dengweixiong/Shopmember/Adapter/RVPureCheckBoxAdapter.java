package com.example.dengweixiong.Shopmember.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.dengweixiong.Util.MethodTool;
import com.example.dengweixiong.myapplication.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by dengweixiong on 2018/1/1.
 */

public class RVPureCheckBoxAdapter extends RecyclerView.Adapter {


    /**
     * isChecked:
     * teacherId:
     * teacherName:
     */

    private List<Map<String,String>> listmap_original = new ArrayList<>();
    private List<Map<String,String>> listmap_after = new ArrayList<>();
    private Context context;

    public RVPureCheckBoxAdapter(List<Map<String,String>> original_listMap, Context context) {
        this.listmap_original = original_listMap;
        this.context = context;
        listmap_after = MethodTool.deepCopy(listmap_original);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup vg = (ViewGroup)inflater.inflate(R.layout.tile_pure_checkbox_item,parent,false);
        return new ContentViewHolder(vg);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ContentViewHolder contentViewHolder = (ContentViewHolder)holder;
        contentViewHolder.checkBox.setText(listmap_original.get(position).get("teacherName"));
        if (listmap_original.get(position).get("isChecked").equals("0")) {
            contentViewHolder.checkBox.setChecked(false);
        }else {
            contentViewHolder.checkBox.setChecked(true);
        }
        contentViewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Map<String,String> map_temp = listmap_after.get(position);
                if (isChecked) {
                    map_temp.put("isChecked","1");
                }else {
                    map_temp.put("isChecked","0");
                }
                listmap_after.set(position,map_temp);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listmap_original.size();
    }

    private static class ContentViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        private ContentViewHolder(View view) {
            super(view);
            checkBox = (CheckBox) itemView.findViewById(R.id.cb_t_pure_checkbox_item);
        }
    }

    public List<Map<String, String>> getListmap_after() {
        return listmap_after;
    }
}
