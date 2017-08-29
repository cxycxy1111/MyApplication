package com.example.dengweixiong.Util;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.*;

/**
 * Created by dengweixiong on 2017/8/22.
 */

public class RecyclerItemClickListener  implements RecyclerView.OnItemTouchListener {

    private int mLastDownX,mLastDownY;

    private interface OnItemClickListener {
        void onItemClick(View view, int position);


        void onItemLongClick(View view, int position);
    }

    private OnItemClickListener mListener;

    private GestureDetector mGestureDetector;
    private RecyclerView recyclerView;

    public RecyclerItemClickListener(Context context, final RecyclerView recyclerView, OnItemClickListener listener) {
        mListener = listener;

        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());

                if (childView != null && mListener != null) {
                    mListener.onItemLongClick(childView, recyclerView.getChildAdapterPosition(childView));
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {

        View childView = view.findChildViewUnder(e.getX(), e.getY());

        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
        }

        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

}
