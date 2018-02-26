package xyz.institutionmanage.sailfish.Util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import xyz.institutionmanage.sailfish.Shopmember.Adapter.LVAdapter;

/**
 * Created by dengweixiong on 2017/9/9.
 */

public class LinearLayoutForListView extends LinearLayout {

    private LVAdapter adapter;
    private OnClickListener onClickListener = null;

    public LinearLayoutForListView(Context context) {
        super(context);
    }

    /*
    构造方法
     */
    public LinearLayoutForListView(Context context, AttributeSet attributeSet) {
        super(context,attributeSet);
    }

    /*
    绑定视图
     */
    public void bindLinearLayout() {
        int count = adapter.getCount();
        this.removeAllViews();
        for(int i = 0;i < count;i++) {
            View view = adapter.getView(i,null,null);
            view.setOnClickListener(this.onClickListener);
            addView(view,i);
        }
    }

    /*
    获取adapter
     */
    public LVAdapter getAdapter() {
        return adapter;
    }

    /*
    设置adapter
    */
    public void setAdapter(LVAdapter adapter) {
        this.adapter = adapter;
        bindLinearLayout();
    }

    public OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(OnClickListener listener) {
        this.onClickListener = listener;
    }



}