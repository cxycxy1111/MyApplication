package com.example.dengweixiong.Shopmember.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.example.dengweixiong.myapplication.R;

import org.apache.commons.lang.math.NumberUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dengweixiong on 2017/9/14.
 */

public class RVSupportedCardAdapter extends RecyclerView.Adapter implements View.OnClickListener{

    private static final int CARD_TYPE_BALANCE = 1;
    private static final int CARD_TYPE_TIMES = 2;
    private static final int CARD_TYPE_TIME = 3;
    private Context context;
    private List<Map<String,String>> mapList;
    private Map<Integer,Boolean> checkStatusMap;
    private Map<Integer,String> valueMap;
    private OnItemClickListener listener;

    public RVSupportedCardAdapter(List<Map<String,String>> mapList, Context context) {
        this.context = context;
        this.mapList = mapList;
        initCheckStatusMap();
        initValueMap();
    }

    private void initCheckStatusMap() {
        checkStatusMap = new HashMap<>();
        for (int i = 0;i < mapList.size();i++) {
            checkStatusMap.put(i,false);
        }
    }

    private void initValueMap() {
        valueMap = new HashMap<>();
        for (int i = 0;i<mapList.size();i++) {
            valueMap.put(i,"");
        }
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
        EditText editText_times;
        public TimesVH(View view) {
            super(view);
            checkBox = (CheckBox)view.findViewById(R.id.cb_t_times_card);
            editText_times = (EditText) view.findViewById(R.id.et_t_times_card);
        }
    }

    public static class TimeVH extends RecyclerView.ViewHolder {
        CheckBox checkBox;
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
        LayoutInflater inflater = LayoutInflater.from(context);
        switch (viewType) {
            case CARD_TYPE_BALANCE :
                ViewGroup vgBalance = (ViewGroup)inflater.inflate(R.layout.tile_supportedcard_balance,parent,false);
                return new BalanceVH(vgBalance);
            case CARD_TYPE_TIMES:
                ViewGroup vgTimes = (ViewGroup)inflater.inflate(R.layout.tile_supportedcard_times,parent,false);
                return new TimesVH(vgTimes);
            case CARD_TYPE_TIME :
                ViewGroup vgTime = (ViewGroup)inflater.inflate(R.layout.tile_supportedcard_time,parent,false);
                return new TimeVH(vgTime);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final Map<String,String> map = mapList.get(position);
        switch (holder.getItemViewType()) {
            case CARD_TYPE_BALANCE:
                BalanceVH balanceVH = (BalanceVH)holder;
                balanceVH.checkBox.setText(map.get("name"));
                balanceVH.itemView.setTag(position);
                balanceVH.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        checkStatusMap.remove(position);
                        checkStatusMap.put(position,isChecked);
                    }
                });
                balanceVH.editText_balance.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String str = s.toString();
                        valueMap.remove(position);
                        valueMap.put(position,str);

                    }
                });
                break;
            case CARD_TYPE_TIMES:
                TimesVH timesVH = (TimesVH)holder;
                timesVH.checkBox.setText(map.get("name"));
                timesVH.itemView.setTag(position);
                timesVH.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        checkStatusMap.remove(position);
                        checkStatusMap.put(position,isChecked);
                    }
                });
                timesVH.editText_times.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        valueMap.remove(position);
                        valueMap.put(position,s.toString());
                    }
                });
                break;
            case CARD_TYPE_TIME:
                TimeVH timeVH = (TimeVH)holder;
                timeVH.checkBox.setText(map.get("name"));
                timeVH.itemView.setTag(position);
                timeVH.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        checkStatusMap.remove(position);
                        checkStatusMap.put(position,isChecked);
                    }
                });
                break;
            default:break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        String type = String.valueOf(mapList.get(position).get("type"));
        if (NumberUtils.isNumber(type)) {
            return Integer.valueOf(type);
        }else {
            return 0;
        }
    }

    @Override
    public int getItemCount() {
        return mapList.size();
    }

    public Map<Integer, Boolean> getCheckStatusMap() {
        return checkStatusMap;
    }

    public void setCheckStatusMap(Map<Integer, Boolean> checkStatusMap) {
        this.checkStatusMap = checkStatusMap;
    }

    public Map<Integer, String> getValueMap() {
        return valueMap;
    }

    public void setValueMap(Map<Integer, String> valueMap) {
        this.valueMap = valueMap;
    }
}
