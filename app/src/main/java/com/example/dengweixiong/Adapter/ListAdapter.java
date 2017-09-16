package com.example.dengweixiong.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dengweixiong.myapplication.R;
import java.util.ArrayList;

/**
 * Created by dengweixiong on 2017/8/21.
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private ArrayList<Country> arrayList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;

        public ViewHolder(View view) {
            super(view);
            name = (TextView)view.findViewById(R.id.country_name);
        }
    }

    public ListAdapter(ArrayList<Country> aArrayList) {
        this.arrayList = aArrayList;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup,int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.tile_simple_list_view_with_icon,viewGroup,false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    public void onBindViewHolder(ViewHolder holder,int position) {
        Country country = arrayList.get(position);
        holder.name.setText(country.getCountryName());
    }

    public int getItemCount() {
        return arrayList.size();
    }
}
