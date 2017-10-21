package com.example.dengweixiong.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.example.dengweixiong.myapplication.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by dengweixiong on 2017/10/20.
 */

public class CardListAdapter extends RecyclerView.Adapter implements View.OnClickListener {

    private static final int TYPE_HEADER = 1;
    private static final int TYPE_CONTENT = 2;
    private List<Map<String, String>> list = new ArrayList<>();
    //private static String[] catagory = new String[] {"余额卡","次卡","有效期卡"};
    private Context context;
    private OnItemClickListener onItemClickListener = null;

    public CardListAdapter(List<Map<String, String>> list,Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        switch (viewType) {
            case TYPE_HEADER:
                ViewGroup vgHeader = (ViewGroup)inflater.inflate(R.layout.tile_recyclerview_header,parent,false);
                return new HeaderViewHolder(vgHeader);
            case TYPE_CONTENT:
                ViewGroup vgContent = (ViewGroup)inflater.inflate(R.layout.tile_simple_list_view_no_icon,parent,false);
                vgContent.setOnClickListener(this);
                return new ContentViewHolder(vgContent);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case TYPE_HEADER:
                final HeaderViewHolder headerViewHolder = (HeaderViewHolder)holder;
                headerViewHolder.itemView.setTag(position);
                switch (Integer.parseInt(list.get(position).get("type"))) {
                    case 1:
                        headerViewHolder.textView.setText("余额卡");
                        break;
                    case 2:
                        headerViewHolder.textView.setText("次卡");
                        break;
                    case 3:
                        headerViewHolder.textView.setText("有效期卡");
                        break;
                    default:
                        headerViewHolder.textView.setText("");
                }
                break;
            case TYPE_CONTENT:
                final ContentViewHolder contentViewHolder = (ContentViewHolder)holder;
                contentViewHolder.tv.setText(list.get(position).get("name"));
                contentViewHolder.itemView.setTag(position);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        int i = list.get(position).size();
        if (i == 1) {
            return 1;
        }else {
            return 2;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        private HeaderViewHolder(View view) {
            super(view);
            textView = (TextView)itemView.findViewById(R.id.tv_header_name_t_rv_header);
        }
    }

    private static class ContentViewHolder extends RecyclerView.ViewHolder {
        TextView tv;
        private ContentViewHolder(View view) {
            super(view);
            tv = (TextView)itemView.findViewById(R.id.simple_list_view_with_icon_text);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view,int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onClick(View v) {
        onItemClickListener.onItemClick(v,(int)v.getTag());
    }
}
