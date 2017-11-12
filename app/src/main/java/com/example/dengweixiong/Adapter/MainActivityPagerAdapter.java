package com.example.dengweixiong.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.View;

import java.util.List;

/**
 * Created by dengweixiong on 2017/11/9.
 */

public class MainActivityPagerAdapter extends FragmentPagerAdapter{

    private FragmentManager fm;
    private List<Fragment> fragments;

    public MainActivityPagerAdapter(FragmentManager fragmentManager,List<Fragment> fragments) {
        super(fragmentManager);
        this.fragments = fragments;

    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
