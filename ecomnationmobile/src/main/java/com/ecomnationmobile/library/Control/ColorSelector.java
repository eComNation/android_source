package com.ecomnationmobile.library.Control;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ecomnationmobile.library.Common.CustomLayoutInflater;
import com.ecomnationmobile.library.Common.ECNCallback;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Data.DropdownClass;
import com.ecomnationmobile.library.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 7/18/2016.
 */
public class ColorSelector extends FrameLayout {

    TextView textView;
    Context context;
    View view;
    float dens;
    int position;
    List<View> imageList;
    List<String> collection;
    ECNCallback mCallback;
    DropdownClass data;

    public ColorSelector(Context context) {
        this(context, null);
    }

    public ColorSelector(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorSelector(final Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        this.context = context;

        view = LayoutInflater.from(context).inflate(R.layout.color_selector, null);
        textView = (TextView) view.findViewById(R.id.txtLabel);

        addView(view);
    }

    public void init(DropdownClass ddc, String url, ECNCallback callback) {
        data = ddc;
        mCallback = callback;
        position = -1;
        if (data != null) {
            textView.setText(data.getLabel());
            setColors(url, this.data.getValues());
        } else {
            textView.setText("");
            setColors("", new ArrayList<String>());
        }
    }

    public void setColors(String url, List<String> list) {
        if (!list.isEmpty()) {
            collection = list;
            imageList = new ArrayList<>();
            final LinearLayout layout = (LinearLayout) view.findViewById(R.id.grid);
            layout.removeAllViews();

            DisplayMetrics dm = HelperClass.getDisplayMetrics(context);
            dens = dm.density;
            int width = dm.widthPixels;
            width /= 8;

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(convertPixels(3), convertPixels(3), convertPixels(3), convertPixels(3));

            for (int i = 0; i < list.size(); i++) {
                View img = CustomLayoutInflater.getColorView(context, url + "misc/" + list.get(i) + ".jpg");
                img.setLayoutParams(layoutParams);
                img.setTag(i);
                img.setBackgroundResource(R.drawable.simple_border);
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setSelection((int) v.getTag());
                    }
                });
                layout.addView(img);
                imageList.add(img);
            }
            view.findViewById(R.id.scroll).scrollTo(0, 0);
        }
    }

    public String getSelection() {
        return collection == null || collection.isEmpty() || position == -1 ? "" : collection.get(position);
    }

    private int convertPixels(float pix) {
        return (int) (pix * dens);
    }

    public void setSelection(int pos) {
        position = pos;
        for (int i = 0; i < imageList.size(); i++) {
            imageList.get(i).setBackgroundResource(R.drawable.simple_border);
        }
        imageList.get(pos).setBackgroundResource(R.drawable.selector);
        mCallback.onSuccess(getSelection());
        mCallback.onFailure(null);
    }
}
