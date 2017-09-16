package com.example.dengweixiong.Util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by dengweixiong on 2017/9/10.
 */

public class ListViewForScrollView extends ListView {


    public ListViewForScrollView(Context context) {
        super(context);
    }

    public ListViewForScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListViewForScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
    }
}
