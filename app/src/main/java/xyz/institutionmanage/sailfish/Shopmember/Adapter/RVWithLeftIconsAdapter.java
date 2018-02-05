package xyz.institutionmanage.sailfish.Shopmember.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import xyz.institutionmanage.sailfish.Bean.Card;

/**
 * Created by dengweixiong on 2017/8/21.
 */

public class RVWithLeftIconsAdapter extends RecyclerView.Adapter<RVWithLeftIconsAdapter.ViewHolder> {

    private ArrayList<Card> arrayList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;

        public ViewHolder(View view) {
            super(view);
            name = (TextView)view.findViewById(xyz.example.dengweixiong.myapplication.R.id.country_name);
        }
    }

    public RVWithLeftIconsAdapter(ArrayList<Card> aArrayList) {
        this.arrayList = aArrayList;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup,int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(xyz.example.dengweixiong.myapplication.R.layout.tile_simple_list_view_with_icon,viewGroup,false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    public void onBindViewHolder(ViewHolder holder,int position) {
        Card country = arrayList.get(position);
        holder.name.setText(country.getName());
    }

    public int getItemCount() {
        return arrayList.size();
    }
}
