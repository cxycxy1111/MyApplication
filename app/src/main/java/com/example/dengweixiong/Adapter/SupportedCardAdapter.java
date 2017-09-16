package com.example.dengweixiong.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.dengweixiong.Bean.Course;
import com.example.dengweixiong.myapplication.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dengweixiong on 2017/9/14.
 */

public class SupportedCardAdapter extends RecyclerView.Adapter implements View.OnClickListener{

    private OnItemClickListener listener;
    private ArrayList<HashMap<String,String>> mapList;
    private Course course;

    public SupportedCardAdapter(Course course) {
        this.course = course;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        EditText editText_balance;
        EditText editText_num;
        public ViewHolder(View view) {
            super(view);
            checkBox = (CheckBox)view.findViewById(R.id.cb_t_supportedcard);
            editText_balance = (EditText) view.findViewById(R.id.et_balance_t_supportedcard);
            editText_num = (EditText)view.findViewById(R.id.et_num_t_supportedcard);
        }

    }

    public interface OnItemClickListener {
        void onItemClick(View view,int position);
    }

    public OnItemClickListener getListener() {
        return listener;
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tile_supportedcard,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setOnClickListener(this);
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String cardName = course.getSupportedCard().get(position).getName();
        HashMap<String,String> map = mapList.get(position);
        ViewHolder holder1 =  (ViewHolder)holder;
        holder1.checkBox.setText(cardName);

        if(course.getSupportedCard().get(position).getType() == 1) {
            int price = course.getPrice().get(position).getBalance();
            holder1.editText_balance.setText(String.valueOf(price));
            holder1.editText_num.setVisibility(View.GONE);
        } else if (course.getSupportedCard().get(position).getType() == 2) {
            holder1.editText_num.setText(course.getPrice().get(position).getBalance());
            holder1.editText_balance.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
