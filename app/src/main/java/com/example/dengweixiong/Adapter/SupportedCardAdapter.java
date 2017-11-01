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
    private static final int CARD_TYPE_BALANCE = 1;
    private static final int CARD_TYPE_TIMES = 2;
    private static final int CARD_TYPE_TIME = 3;

    public SupportedCardAdapter(ArrayList<HashMap<String,String>> mapList) {
        this.mapList = mapList;
    }

    public static class BalanceVH extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        EditText editText_balance;
        public BalanceVH(View view) {
            super(view);
            checkBox = (CheckBox)view.findViewById(R.id.cb_t_balance_card);
            editText_balance = (EditText) view.findViewById(R.id.et_t_balance_card);
        }
    }
    
    public static class TimesVH extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        EditText editText;
        public TimesVH(View view) {
            super(view);
            checkBox = (CheckBox)view.findViewById(R.id.cb_t_times_card);
            editText = (EditText) view.findViewById(R.id.et_t_times_card);
        }
    }

    public static class TimeVH extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        EditText editText;
        public TimeVH(View view) {
            super(view);
            checkBox = (CheckBox)view.findViewById(R.id.cb_t_time_card);
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case CARD_TYPE_BALANCE :
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tile_supportedcard_balance,parent,false);
                return new BalanceVH(view);
            case CARD_TYPE_TIMES:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tile_supportedcard_times,parent,false);
                return new TimesVH(view);
            case CARD_TYPE_TIME :
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tile_supportedcard_time,parent,false);
                return new TimeVH(view);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        HashMap<String,String> map = mapList.get(position);
        switch (holder.getItemViewType()) {
            case CARD_TYPE_BALANCE:
                BalanceVH balanceVH = (BalanceVH)holder;
                balanceVH.checkBox.setText(map.get(""));
        }
    }

    @Override
    public int getItemCount() {
        return mapList.size();
    }
}
