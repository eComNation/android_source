package com.eComNation.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.ecomnationmobile.library.Control.WrapContentHeightViewPager;
import com.ecomnationmobile.library.Data.KeyValuePair;

import java.util.ArrayList;

/**
 * Created by User on 6/9/2016.
 */
public class TabsCustomPagerAdapter extends FragmentStatePagerAdapter {

    ArrayList<String> mFragments;
    Context mContext;
    ArrayList<KeyValuePair> mTabs;
    Bundle mBundle;
    private int mCurrentPosition = -1;

    public TabsCustomPagerAdapter(FragmentManager fm, Context context, ArrayList<String> fragments, ArrayList<KeyValuePair> tabs) {
        super(fm);
        mContext = context;
        mFragments = fragments;
        mTabs = tabs;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        if (position != mCurrentPosition) {
            Fragment fragment = (Fragment) object;
            WrapContentHeightViewPager pager = (WrapContentHeightViewPager) container;
            if (fragment != null && fragment.getView() != null) {
                mCurrentPosition = position;
                pager.measureCurrentView(fragment.getView());
            }
        }
    }

    @Override
    public Fragment getItem(int index) {
        mBundle = new Bundle();
        mBundle.putInt("id",mTabs.get(index).getValue());
        return Fragment.instantiate(mContext, mFragments.get(index), mBundle);
    }

    @Override
    public int getCount() {
        return mTabs.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabs.get(position).getKey();
    }
}