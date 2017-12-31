package com.example.dengweixiong.Shopmember.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created by dengweixiong on 2017/9/9.
 */

public class LVAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private int resource;
    private List<? extends Map<String,?>> data;
    private String[] from;
    private int[] to;

    public LVAdapter(Context context, List<? extends Map<String,?>> data, int resource,
                     String[] from, int[] to) {
        this.data = data;
        this.resource = resource;
        this.from = from;
        this.to = to;
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressWarnings("uncheck")
    public String get(int position,Object key) {
        Map<String,?> map = (Map<String,?>)getItem(position);
        return map.get(key).toString();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(resource,null);
        Map<String,?> item = data.get(position);
        int count = to.length;
        for (int i =0;i < count;i++) {
            View view = convertView.findViewById(to[i]);
            bindView(convertView,item,from[i]);
        }
        convertView.setTag(position);
        return convertView;
    }

    /*
    绑定视图
     */

    public void bindView(View view,Map<String,?> map,String from) {
        Object data = map.get(from);
        if(view instanceof TextView) {
            ((TextView)view).setText(data ==null?"":data.toString());
        }
    }



}
