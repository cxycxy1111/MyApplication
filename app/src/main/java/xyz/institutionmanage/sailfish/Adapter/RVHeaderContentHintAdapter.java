package xyz.institutionmanage.sailfish.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.apache.commons.lang.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import xyz.institutionmanage.sailfish.R;

/**
 * Created by dengweixiong on 2018/2/21.
 */

public class RVHeaderContentHintAdapter extends RecyclerView.Adapter{

    private static final int TYPE_HEADER = 1;
    private static final int TYPE_CONTENT = 2;
    private List<Map<String, String>> list = new ArrayList<>();
    private Context context;
    private RVHeaderContentHintAdapter.OnItemClickListener onItemClickListener = null;

    public RVHeaderContentHintAdapter(List<Map<String, String>> list,Context context) {
        this.context = context;
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        switch (viewType) {
            case TYPE_HEADER:
                ViewGroup vgHeader = (ViewGroup)inflater.inflate(R.layout.tile_recyclerview_header,parent,false);
                return new HeaderViewHolder(vgHeader);
            case TYPE_CONTENT:
                ViewGroup vgContent = (ViewGroup)inflater.inflate(R.layout.tile_list_with_hint,parent,false);
                return new ContentViewHolder(vgContent);
            default:return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case TYPE_HEADER:
                final RVHeaderContentHintAdapter.HeaderViewHolder headerViewHolder = (RVHeaderContentHintAdapter.HeaderViewHolder)holder;
                headerViewHolder.itemView.setTag(position);
                headerViewHolder.textView.setText(list.get(position).get("type"));
                break;
            case TYPE_CONTENT:
                final RVHeaderContentHintAdapter.ContentViewHolder contentViewHolder = (RVHeaderContentHintAdapter.ContentViewHolder)holder;
                contentViewHolder.tv_main.setText(list.get(position).get("name"));
                contentViewHolder.tv_hint.setText(list.get(position).get("content"));
                contentViewHolder.itemView.setTag(position);
                break;
            default:break;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (NumberUtils.isNumber(String.valueOf(list.get(position).get("type")))) {
            return 2;
        }
        return 1;
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        private HeaderViewHolder(View view) {
            super(view);
            textView = (TextView)itemView.findViewById(R.id.tv_header_name_t_rv_header);
        }
    }

    private static class ContentViewHolder extends RecyclerView.ViewHolder {
        TextView tv_main,tv_hint;
        private ContentViewHolder(View view) {
            super(view);
            tv_main = (TextView)itemView.findViewById(R.id.tv_main_text_list_with_hint);
            tv_hint = (TextView)itemView.findViewById(R.id.tv_hint_text_list_with_hint);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view,int position);
    }

    public void setOnItemClickListener(RVHeaderContentHintAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
