package com.eComNation.Adapter;

/**
 * Created by Abhi on 22-08-2015.
 */

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.ecomnationmobile.library.Data.KeyValuePair;

import java.util.ArrayList;

public class TabsPagerAdapter extends FragmentStatePagerAdapter {

    ArrayList<String> mFragments;
    Context mContext;
    ArrayList<KeyValuePair> mTabs;
    Bundle mBundle;
    private int mCurrentPosition = -1;

    public TabsPagerAdapter(FragmentManager fm,Context context,ArrayList<String> fragments,ArrayList<KeyValuePair> tabs) {
        super(fm);
        mContext = context;
        mFragments = fragments;
        mTabs = tabs;
    }

    @Override
    public Fragment getItem(int index) {
        mBundle = new Bundle();
        mBundle.putInt("id",mTabs.get(index).getValue());
        return Fragment.instantiate(mContext, mFragments.get(index), mBundle);
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return mTabs.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabs.get(position).getKey();
    }
}