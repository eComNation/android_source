package com.eComNation.Fragment;

import android.content.Intent;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eComNation.Activity.CheckOutActivity;
import com.eComNation.Activity.SearchActivity;
import com.eComNation.Adapter.TabsPagerAdapter;
import com.eComNation.Common.Utility;
import com.eComNation.R;
import com.ecomnationmobile.library.Common.ECNCallback;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Control.ExtendedViewPager;
import com.ecomnationmobile.library.Control.SlidingTabLayout;
import com.ecomnationmobile.library.Data.KeyValuePair;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by Abhi on 03-05-2016.
 */
public class HomeMainFragment extends Fragment {

    Gson gson;
    int tabIndex;
    View progressBar, errorView;
    private ViewPager viewPager;
    private SlidingTabLayout tabStrip;
    private TabsPagerAdapter mAdapter;
    private ArrayList<KeyValuePair> mTabs;
    private ArrayList<String> mFragments;
    private View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.store_main, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mView = getView();

        if (mView != null) {

            progressBar = mView.findViewById(R.id.progressBar);
            errorView = mView.findViewById(R.id.errorLayout);

            progressBar.setVisibility(View.VISIBLE);

            mTabs = new ArrayList<>();
            mTabs.add(new KeyValuePair(0, getString(R.string.home).toUpperCase()));
            mTabs.add(new KeyValuePair(1, getString(R.string.featured_collection)));

            gson = new Gson();
            tabIndex = 0;

            getData();
        }
    }

    private void getData() {
        progressBar.setVisibility(View.VISIBLE);
        errorView.setVisibility(View.GONE);

        getBanners();
    }

    private void getBanners() {
        String bannerContent = HelperClass.getSharedString(getActivity(),"banners");
        if(bannerContent == null) {
            String url = getString(R.string.production_base_url) + getString(R.string.api) + "store/home_page/banners?";
            url += "count=15";
            HelperClass.getData(getActivity(), url, new ECNCallback() {
                @Override
                public void onSuccess(String result) {
                    HelperClass.putSharedString(getActivity(), "banners", result);

                    displayData();
                }

                @Override
                public void onFailure(KeyValuePair error) {
                    displayError(error.getKey());
                }
            });
        }
        else {
            displayData();
        }
    }

    private void displayData() {
        mFragments = new ArrayList<>();
        mFragments.add(HomeFragment.class.getName());
        mFragments.add(FeaturedProductFragment.class.getName());

        viewPager = (ExtendedViewPager) mView.findViewById(R.id.pager);
        tabStrip = (SlidingTabLayout) mView.findViewById(R.id.tabs);

        tabStrip.setTextSelector(R.color.white_selector);
        mAdapter = new TabsPagerAdapter(getFragmentManager(), getActivity(), mFragments, mTabs);
        viewPager.setAdapter(mAdapter);

        tabStrip.setDistributeEvenly(true);
        tabStrip.setViewPager(viewPager);

        tabStrip.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener());

        viewPager.setCurrentItem(tabIndex);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
            }
        }, 3000);

        setHasOptionsMenu(true);
    }

    private void displayError(String error) {
        errorView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        ((TextView)errorView.findViewById(R.id.txtMessage)).setText(error);

        errorView.findViewById(R.id.btnRetry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(false);
        HelperClass.putSharedString(getActivity(), getString(R.string.selected_filters), null);
        HelperClass.putSharedInt(getActivity(), "min_price", -1);
        HelperClass.putSharedInt(getActivity(), "max_price", -1);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflate menu
        inflater.inflate(R.menu.home, menu);

        // Get the notifications MenuItem and LayerDrawable (layer-list)
        MenuItem item = menu.findItem(R.id.cart);
        LayerDrawable icon = (LayerDrawable) item.getIcon();

        // Update LayerDrawable's BadgeDrawable
        Utility.setBadgeCount(getActivity(), icon, HelperClass.getCartCount(getActivity()));
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.cart:
                Intent intent = new Intent(getActivity(),CheckOutActivity.class);
                intent.putExtras(new Bundle());
                startActivity(intent);
                return true;
            case R.id.search:
                startActivity(new Intent(getActivity(),SearchActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
