package com.eComNation.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.eComNation.Activity.CheckOutActivity;
import com.eComNation.Activity.SearchActivity;
import com.eComNation.Common.Utility;
import com.eComNation.R;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Control.ExtendedViewPager;
import com.ecomnationmobile.library.Control.WrapContentHeightViewPager;
import com.ecomnationmobile.library.Data.BannerImage;
import com.ecomnationmobile.library.Data.ECNResponse;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Abhi on 24-12-2015.
 */
public class HomeFragment extends Fragment {

    static ImageView mDotsImages[];
    LinearLayout bannersLayout, mDotsLayout;
    View mView, progressView, errorView;
    List<BannerImage> banners, categoryBanners, allBanners;
    boolean no_featured_products;
    float dens;
    ViewPager viewPager;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_screen, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mView = getView();

        if (mView != null) {

            mDotsLayout = (LinearLayout) mView.findViewById(R.id.indicator);
            progressView = mView.findViewById(R.id.progressBar);
            errorView = mView.findViewById(R.id.errorLayout);
            viewPager = (ViewPager) mView.findViewById(R.id.view_pager);

            no_featured_products = HelperClass.getSharedString(getActivity(), "featured_products") == null;

            ECNResponse response = (new Gson()).fromJson(HelperClass.getSharedString(getActivity(), "banners"), ECNResponse.class);
            if (response != null) {
                allBanners = response.getBanners();

                if (allBanners != null) {
                    categoryBanners = new ArrayList<>();
                    banners = new ArrayList<>();
                    for (BannerImage b : allBanners) {
                        if (b.isList())
                            banners.add(b);
                        else
                            categoryBanners.add(b);
                    }
                    displayData();
                }
            }
        }
    }

    private void displayData() {
        if (!banners.isEmpty()) {
            dens = HelperClass.getDisplayMetrics(getActivity()).density;

            final ImagePagerAdapter adapter = new ImagePagerAdapter();
            String dimen = banners.get(0).getDimensions();
            if( dimen != null && !dimen.isEmpty()) {
                float width = Float.parseFloat(dimen.substring(0, dimen.indexOf("x")));
                float height = Float.parseFloat(dimen.substring(dimen.indexOf("x") + 1));
                float vHeight = HelperClass.getDisplayMetrics(getActivity()).widthPixels * height / width;
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) vHeight);
                viewPager.setLayoutParams(lp);
            }

            mDotsImages = new ImageView[adapter.getCount()];
            for (int i = 0; i < adapter.getCount(); i++) {
                mDotsImages[i] = new ImageView(getActivity());
                mDotsImages[i].setBackgroundResource(R.color.TRANSPARENT);
                mDotsImages[i].setImageResource(R.drawable.grey_rounded);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(convertPixels(8), convertPixels(8));
                layoutParams.setMargins(convertPixels(3), convertPixels(6), convertPixels(3), convertPixels(6));

                mDotsImages[i].setLayoutParams(layoutParams);

                mDotsLayout.addView(mDotsImages[i]);
            }
            mDotsImages[0].setImageResource(R.drawable.white_rounded);

            viewPager.setAdapter(adapter);

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    for (int i = 0; i < adapter.getCount(); i++) {
                        mDotsImages[i].setImageResource(R.drawable.grey_rounded);
                    }
                    mDotsImages[position].setImageResource(R.drawable.white_rounded);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            mView.findViewById(R.id.slidingBanners).setVisibility(View.VISIBLE);
        }
        else {
            mView.findViewById(R.id.slidingBanners).setVisibility(View.GONE);
        }

        if (!categoryBanners.isEmpty()) {
            bannersLayout = (LinearLayout) mView.findViewById(R.id.categoryBanners);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, convertPixels(150));
            layoutParams.setMargins(convertPixels(4), convertPixels(2), convertPixels(4), convertPixels(2));
            for (BannerImage b : categoryBanners) {
                final ImageView view = new ImageView(getActivity());
                view.setLayoutParams(layoutParams);
                view.setTag(b.getCategory_id());
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        long id = Long.parseLong(view.getTag().toString());
                        if (id != 0) {
                            HelperClass.putSharedLong(getActivity(), "category", id);
                            Intent intent = Utility.getListingIntent(getActivity(), Long.toString(id));
                            startActivity(intent);
                        }
                    }
                });
                String url = b.getUrl();
                if (url != null) {
                    Picasso.with(getActivity()).load(HelperClass.processURL(url)).into(view, new Callback() {
                        @Override
                        public void onSuccess() {
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            lp.setMargins(convertPixels(4), convertPixels(2), convertPixels(4), convertPixels(2));
                            view.setLayoutParams(lp);
                        }

                        @Override
                        public void onError() {
                            view.setImageResource(R.drawable.placeholder);
                        }
                    });
                } else {
                    view.setImageResource(R.drawable.placeholder);
                }
                view.setBackgroundResource(R.drawable.banner_border);
                view.setAdjustViewBounds(true);
                bannersLayout.addView(view);
            }
        }


        setSocialLinks(R.string.insta_link, R.id.imgInstagram);

        setSocialLinks(R.string.facebook_link, R.id.imgFacebook);

        setSocialLinks(R.string.twitter_link, R.id.imgTwitter);

        setSocialLinks(R.string.pinterest_link, R.id.imgPinterest);

        setSocialLinks(R.string.youtube_link, R.id.imgYoutube);

        setSocialLinks(R.string.googleplus_link, R.id.imgGoogleplus);

        if (no_featured_products)
            setHasOptionsMenu(true);
    }

    private void setSocialLinks(int urlId, int viewId) {
        final String url = getString(urlId);
        View icon = mView.findViewById(viewId);
        if (icon != null) {
            if (url.equals("")) {
                icon.setVisibility(View.GONE);
            } else {
                icon.setVisibility(View.VISIBLE);
                icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goToUrl(url);
                    }
                });
            }
        }
    }

    private void goToUrl(String url) {
        if (url.equals(""))
            return;

        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }

    private int convertPixels(float pix) {
        return (int) (pix * dens);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (no_featured_products) {
            setHasOptionsMenu(false);
            HelperClass.putSharedString(getActivity(), getString(R.string.selected_filters), null);
            HelperClass.putSharedInt(getActivity(), "min_price", -1);
            HelperClass.putSharedInt(getActivity(), "max_price", -1);
            setHasOptionsMenu(true);
        }
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
                Intent intent = new Intent(getActivity(), CheckOutActivity.class);
                intent.putExtras(new Bundle());
                startActivity(intent);
                return true;
            case R.id.search:
                startActivity(new Intent(getActivity(), SearchActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class ImagePagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return banners.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            final ImageView image = new ImageView(getActivity());
            String url = banners.get(position).getUrl();
            if (url != null) {
                Picasso.with(getActivity()).load(HelperClass.processURL(url)).into(image, new Callback() {
                    @Override
                    public void onSuccess() {
                        //FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        //viewPager.setLayoutParams(lp);
                    }

                    @Override
                    public void onError() {
                        image.setImageResource(R.drawable.placeholder);
                    }
                });
            } else {
                image.setImageResource(R.drawable.placeholder);
            }
            image.setLayoutParams(layoutParams1);
            image.setAdjustViewBounds(true);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long id = banners.get(position).getCategory_id();
                    if (id != 0) {
                        HelperClass.putSharedLong(getActivity(), "category", id);
                        Intent intent = Utility.getListingIntent(getActivity(), Long.toString(id));
                        startActivity(intent);
                    }
                }
            });
            container.addView(image, 0);
            return image;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((ImageView) object);
        }
    }
}
