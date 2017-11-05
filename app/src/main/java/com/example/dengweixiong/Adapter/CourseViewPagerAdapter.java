package com.example.dengweixiong.Adapter;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dengweixiong on 2017/9/4.
 */

public class CourseViewPagerAdapter extends FragmentStatePagerAdapter{

    private FragmentManager fm;
    private String [] title;
    private List<Fragment> fragments;
    private static final String TAG = "CourseMain Page:";

    public CourseViewPagerAdapter(FragmentManager fm, String [] title, List<Fragment> fragments) {
        super(fm);
        this.fm = fm;
        this.title = title;
        this.fragments = fragments;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }

    @Override
    public float getPageWidth(int position) {
        return super.getPageWidth(position);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        return fragments.get(position);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public int getCount() {
        return fragments.size();
    }

}
