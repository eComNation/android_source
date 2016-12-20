package com.eComNation.Activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.eComNation.R;
import com.ecomnationmobile.library.Common.ECNCallback;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Control.ExtendedViewPager;
import com.ecomnationmobile.library.Control.TouchImageView;
import com.ecomnationmobile.library.Data.KeyValuePair;
import com.ecomnationmobile.library.Data.Product;
import com.ecomnationmobile.library.Data.ProductImage;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by Abhi on 16-01-2016.
 */
public class ImageListActivity extends FragmentActivity {
    static ImageView mDotsImages[];
    public List<ProductImage> imageList;
    LinearLayout mDotsLayout;
    View btnNext, btnPrev;
    int mDotsCount, mPosition;
    ExtendedViewPager mViewPager;
    float dens;
    Product product;

    @Override
    public void onCreate(Bundle savedInstantState) {
        super.onCreate(savedInstantState);
        setContentView(R.layout.image_list);

        String content = HelperClass.getSharedString(getApplicationContext(), "product");
        product = (new Gson()).fromJson(content, Product.class);

        imageList = product.getImages();

        btnNext = findViewById(R.id.btnNext);
        btnPrev = findViewById(R.id.btnPrev);
        mDotsLayout = (LinearLayout) findViewById(R.id.dotsLayout);

        mDotsCount = imageList.size();

        mDotsImages = new ImageView[mDotsCount];

        mPosition = getIntent().getIntExtra("position",0);

        dens = HelperClass.getDisplayMetrics(this).density;
        for (int i = 0; i < mDotsCount; i++) {
            mDotsImages[i] = new ImageView(this);
            mDotsImages[i].setBackgroundResource(R.color.TRANSPARENT);
            mDotsImages[i].setImageResource(R.drawable.grey_rounded);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(convertPixels(10), convertPixels(10));
            layoutParams.setMargins(convertPixels(5), convertPixels(10), convertPixels(5), convertPixels(10));
            mDotsImages[i].setLayoutParams(layoutParams);

            mDotsLayout.addView(mDotsImages[i]);
        }

        mViewPager = (ExtendedViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(new TouchImageAdapter());

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {
            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                TouchImageView img = (TouchImageView) mViewPager.getChildAt(position);
                if (img != null)
                    img.resetZoom();
                setDotSelection(position);
            }
        });

        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
            }
        });

        mViewPager.setCurrentItem(mPosition);
        setDotSelection(mPosition);
    }

    private void show(int pos) {
        if(pos == 0)
            btnPrev.setVisibility(View.GONE);
        else
            btnPrev.setVisibility(View.VISIBLE);

        if(pos == imageList.size() - 1)
            btnNext.setVisibility(View.GONE);
        else
            btnNext.setVisibility(View.VISIBLE);
    }

    private void setDotSelection(int pos) {
        show(pos);
        for (int i = 0; i < mDotsCount; i++) {
            mDotsImages[i].setImageResource(R.drawable.grey_rounded);
        }
        mDotsImages[pos].setImageResource(R.drawable.white_rounded);
    }

    private int convertPixels(float pix) {
        return (int)(pix*dens);
    }

    class TouchImageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return imageList.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            TouchImageView img = new TouchImageView(container.getContext());
            String url = imageList.get(position).getUrl();
            if (url != null) {
                HelperClass.setPicassoBitMap(url, img, new ECNCallback() {
                    @Override
                    public void onSuccess(String result) {
                    }

                    @Override
                    public void onFailure(KeyValuePair error) {
                    }
                });
            } else {
                img.setImageResource(R.drawable.placeholder);
            }
            container.addView(img, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            return img;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
