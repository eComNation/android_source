package com.ecomnationmobile.library.Control;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

import com.ecomnationmobile.library.Common.CustomLayoutInflater;
import com.ecomnationmobile.library.Common.ECNCallback;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Data.KeyValuePair;
import com.ecomnationmobile.library.Data.ProductImage;
import com.ecomnationmobile.library.R;

import java.util.List;

/**
 * Created by User on 8/31/2016.
 */
public class ImageControl extends FrameLayout {

    Context mContext;
    View view;
    List<ProductImage> imageList;
    LinearLayout imageLayout;
    float dens;
    int position;
    ViewFlipper viewFlipper;

    public ImageControl(Context context) {
        this(context, null);
    }

    public ImageControl(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageControl(final Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mContext = context;

        view = LayoutInflater.from(context).inflate(R.layout.image_control, null);
        viewFlipper = (ViewFlipper) view.findViewById(R.id.imageSlider);
        imageLayout = (LinearLayout) view.findViewById(R.id.containerView);

        addView(view);
    }

    public void init(List<ProductImage> list, boolean showList) {
        imageList = list;
        viewFlipper.removeAllViews();
        position = 0;
        if(imageList != null && !imageList.isEmpty()) {
            setImages(imageList);
            if (showList && imageList.size() > 1) {
                setPreviewImages(imageList);
                view.findViewById(R.id.imageScroll).setVisibility(VISIBLE);
            }
            else {
                view.findViewById(R.id.imageScroll).setVisibility(GONE);
            }
        }
        else {
            ImageView image = new ImageView(mContext);
            image.setImageResource(R.drawable.placeholder);
            viewFlipper.addView(image);
            view.findViewById(R.id.image_progress).setVisibility(View.GONE);
        }
    }

    public void setImages(List<ProductImage> list) {
        view.findViewById(R.id.image_progress).setVisibility(View.VISIBLE);
        for (ProductImage img : list) {
            final ImageView image = new ImageView(mContext);
            String url = img.getUrl();
            if (url != null) {
                if (url.endsWith(".jpg"))
                    url = url.replace(".jpg", "_medium.jpg");
                HelperClass.setPicassoBitMap(url, image, new ECNCallback() {
                    @Override
                    public void onSuccess(String result) {
                        view.findViewById(R.id.image_progress).setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(KeyValuePair error) {
                        view.findViewById(R.id.image_progress).setVisibility(View.GONE);
                    }
                });
            } else {
                image.setImageResource(R.drawable.placeholder);
            }
            viewFlipper.addView(image);
        }
    }

    private void setPreviewImages(List<ProductImage> list) {
        imageLayout.removeAllViews();
        DisplayMetrics dm = HelperClass.getDisplayMetrics(mContext);
        dens = dm.density;
        int width = dm.widthPixels;
        width /= 6;

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, width);
        layoutParams.setMargins(convertPixels(3), convertPixels(3), convertPixels(3), convertPixels(3));
        for (int i = 0; i < list.size(); i++) {
            String url = list.get(i).getUrl();
            if (url != null) {
                if (url.endsWith(".jpg"))
                    url = url.replace(".jpg", "_thumb.jpg");
                View img = CustomLayoutInflater.getColorView(mContext, url);
                img.setLayoutParams(layoutParams);
                img.setTag(i);
                img.setBackgroundResource(R.drawable.simple_border);
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        position = (int) v.getTag();
                        viewFlipper.setDisplayedChild(position);
                    }
                });
                imageLayout.addView(img);
            }
        }
    }

    private int convertPixels(float pix) {
        return (int) (pix * dens);
    }

    public void setOnClickListener(View.OnClickListener listener) {
        viewFlipper.setOnClickListener(listener);
    }

    public void selectImage(long id) {
        if (imageList != null && !imageList.isEmpty()) {
            for (int i = 0; i < imageList.size(); i++) {
                if (imageList.get(i).getId() == id) {
                    viewFlipper.setDisplayedChild(i);
                    break;
                }
            }
        }
    }

    public ImageView getDefaultImage() {
       return  (ImageView) viewFlipper.getChildAt(0);
    }

    public int getSelectedPosition() {
        return  position;
    }
}

