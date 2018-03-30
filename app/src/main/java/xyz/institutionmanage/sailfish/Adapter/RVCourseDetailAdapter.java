package xyz.institutionmanage.sailfish.Adapter;

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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.institutionmanage.sailfish.R;
import xyz.institutionmanage.sailfish.Util.MethodTool;

/**
 * Created by dengweixiong on 2017/11/13.
 *
 * maplist
 * header:
 *  header
 *
 * content:
 *  main
 *  hint
 *
 * balance:
 *  type
 *  checked
 *  card_name
 *  balance
 *
 * times
 *  type
 *  checked
 *  card_name
 *  balance
 *
 * time
 *  type
 *  checked
 *  card_name
 *  balance
 */

public class RVCourseDetailAdapter extends RecyclerView.Adapter{

    private static final int HEADER = 1;
    private static final int COURSE_CONTENT = 2;
    private static final int SUPPORTEDCARD_BALANCE = 3;
    private static final int SUPPORTEDCARD_TIMES = 4;
    private static final int SUPPORTEDCARD_TIME = 5;
    private Context context;
    private List<Map<String,String>> mapList = new ArrayList<>();
    private List<Map<String,String>> list_after_operate = new ArrayList<>();

    public RVCourseDetailAdapter(List<Map<String,String>> mapList, Context mContext) {
        this.mapList = mapList;
        context = mContext;
        initListAfterOperate();
    }

    private void initListAfterOperate() {
        list_after_operate = MethodTool.deepCopy(mapList);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        switch (viewType) {
            case HEADER:
                ViewGroup vgHeader = (ViewGroup)inflater.inflate(R.layout.tile_recyclerview_header,parent,false);
                return new HeaderViewHolder(vgHeader);
            case COURSE_CONTENT:
                ViewGroup vgContent= (ViewGroup)inflater.inflate(R.layout.tile_with_editext,parent,false);
                return new CourseViewHolder(vgContent);
            case SUPPORTEDCARD_BALANCE:
                ViewGroup vgBalance = (ViewGroup)inflater.inflate(R.layout.tile_supportedcard_balance,parent,false);
                return new BalanceViewHolder(vgBalance);
            case SUPPORTEDCARD_TIMES :
                ViewGroup vgTimes = (ViewGroup)inflater.inflate(R.layout.tile_supportedcard_times,parent,false);
                return new TimesViewHolder(vgTimes);
            case SUPPORTEDCARD_TIME:
                ViewGroup vgTime = (ViewGroup)inflater.inflate(R.layout.tile_supportedcard_time,parent,false);
                return new TimeViewHolder(vgTime);
            default:return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case HEADER:
                HeaderViewHolder headerViewHolder = (HeaderViewHolder)holder;
                headerViewHolder.itemView.setTag(position);
                headerViewHolder.textView.setText(list_after_operate.get(position).get("header"));
                break;
            case COURSE_CONTENT:
                CourseViewHolder courseViewHolder = (CourseViewHolder)holder;
                courseViewHolder.itemView.setTag(position);
                courseViewHolder.textView_main.setText(list_after_operate.get(position).get("main"));
                courseViewHolder.editText_hint.setText(String.valueOf(list_after_operate.get(position).get("hint")));
                courseViewHolder.editText_hint.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        Map<String,String> temp_map = list_after_operate.get(position);
                        temp_map.put("hint",s.toString());
                        list_after_operate.remove(position);
                        list_after_operate.add(position,temp_map);
                    }
                });
                break;
            case SUPPORTEDCARD_BALANCE:
                BalanceViewHolder balanceViewHolder = (BalanceViewHolder)holder;
                balanceViewHolder.itemView.setTag(position);
                balanceViewHolder.checkBox.setTag(position);
                balanceViewHolder.editText.setTag(position);
                Map<String,String> map = new HashMap<>();
                map = list_after_operate.get(position);
                if (map.get("checked").equals("1")) {
                    balanceViewHolder.checkBox.setChecked(true);
                }else {
                    balanceViewHolder.checkBox.setChecked(false);
                }
                balanceViewHolder.checkBox.setText(map.get("name"));
                String s = map.get("balance");
                if (!s.equals("n")) {
                    balanceViewHolder.editText.setText(map.get("balance"));
                }
                balanceViewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        int pos = (int)buttonView.getTag();
                        Map<String,String> temp_map = list_after_operate.get(pos);
                        if (isChecked) {
                            temp_map.put("checked","1");
                        }else {
                            temp_map.put("checked","0");
                        }
                        list_after_operate.remove(pos);
                        list_after_operate.add(pos,temp_map);
                    }
                });
                balanceViewHolder.editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        Map<String,String> temp_map = list_after_operate.get(position);
                        temp_map.put("balance",s.toString());
                        list_after_operate.remove(position);
                        list_after_operate.add(position,temp_map);
                    }
                });
                break;
            case SUPPORTEDCARD_TIMES:
                TimesViewHolder timesViewHolder = (TimesViewHolder)holder;
                timesViewHolder.itemView.setTag(position);
                timesViewHolder.checkBox.setTag(position);
                timesViewHolder.checkBox.setText(list_after_operate.get(position).get("name"));
                if (list_after_operate.get(position).get("checked").equals("1")) {
                    timesViewHolder.checkBox.setChecked(true);
                }else {
                    timesViewHolder.checkBox.setChecked(false);
                }
                if (!list_after_operate.get(position).get("balance").equals("n")) {
                    timesViewHolder.editText.setText(list_after_operate.get(position).get("balance"));
                }
                timesViewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        int pos = (int)buttonView.getTag();
                        Map<String,String> temp_map = list_after_operate.get(pos);
                        if (isChecked) {
                            temp_map.put("checked","1");
                        }else {
                            temp_map.put("checked","0");
                        }
                        list_after_operate.remove(pos);
                        list_after_operate.add(pos,temp_map);
                    }
                });
                timesViewHolder.editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        Map<String,String> temp_map = new HashMap<>();
                        temp_map = list_after_operate.get(position);
                        temp_map.put("balance",s.toString());
                        list_after_operate.remove(position);
                        list_after_operate.add(temp_map);
                    }
                });
                break;
            case SUPPORTEDCARD_TIME:
                TimeViewHolder timeViewHolder = (TimeViewHolder)holder;
                timeViewHolder.itemView.setTag(position);
                timeViewHolder.checkBox.setTag(position);
                timeViewHolder.checkBox.setText(list_after_operate.get(position).get("name"));
                if (list_after_operate.get(position).get("checked").equals("1")) {
                    timeViewHolder.checkBox.setChecked(true);
                }else {
                    timeViewHolder.checkBox.setChecked(false);
                }
                timeViewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        int pos = (int)buttonView.getTag();
                        Map<String,String> temp_map = list_after_operate.get(pos);
                        if (isChecked) {
                            temp_map.put("checked","1");
                        }else {
                            temp_map.put("checked","0");
                        }
                        list_after_operate.remove(pos);
                        list_after_operate.add(pos,temp_map);
                    }
                });
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {

        Map<String,String> map = mapList.get(position);

        return Integer.parseInt(String.valueOf(mapList.get(position).get("viewType")));
    }

    @Override
    public int getItemCount() {
        return mapList.size();
    }


    private static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        private HeaderViewHolder(View view) {
            super(view);
            textView = (TextView)itemView.findViewById(R.id.tv_header_name_t_rv_header);
        }
    }

    private static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView textView_main;
        EditText editText_hint;
        private CourseViewHolder(View view) {
            super(view);
            textView_main = (TextView)itemView.findViewById(R.id.tv_t_with_text);
            editText_hint = (EditText) itemView.findViewById(R.id.et_t_with_text);
        }
    }

    private static class BalanceViewHolder extends RecyclerView.ViewHolder{
        CheckBox checkBox;
        EditText editText;
        private BalanceViewHolder(View view) {
            super(view);
            checkBox = (CheckBox)itemView.findViewById(R.id.cb_t_balance_card);
            editText = (EditText)itemView.findViewById(R.id.et_t_balance_card);
        }
    }

    private static class TimesViewHolder extends RecyclerView.ViewHolder{
        CheckBox checkBox;
        EditText editText;
        private TimesViewHolder(View view) {
            super(view);
            checkBox = (CheckBox)itemView.findViewById(R.id.cb_t_times_card);
            editText = (EditText)itemView.findViewById(R.id.et_t_times_card);
        }
    }

    private static class TimeViewHolder extends RecyclerView.ViewHolder{
        CheckBox checkBox;
        private TimeViewHolder(View view) {
            super(view);
            checkBox = (CheckBox)itemView.findViewById(R.id.cb_t_time_card);
        }
    }

    public List<Map<String, String>> getList_after_operate() {
        return list_after_operate;
    }

    public void setList_after_operate(List<Map<String, String>> list_after_operate) {
        this.list_after_operate = list_after_operate;
    }
}
