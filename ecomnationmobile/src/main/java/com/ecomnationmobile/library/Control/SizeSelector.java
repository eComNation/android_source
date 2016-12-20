package com.ecomnationmobile.library.Control;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ecomnationmobile.library.Common.ECNCallback;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Data.DropdownClass;
import com.ecomnationmobile.library.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 8/25/2016.
 */
public class SizeSelector extends FrameLayout {

    TextView textView, textGuide, textFiller;
    Context context;
    View view;
    float dens;
    int position;
    List<View> imageList;
    List<String> collection;
    ECNCallback mCallback;
    DropdownClass data;

    public SizeSelector(Context context) {
        this(context, null);
    }

    public SizeSelector(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SizeSelector(final Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        this.context = context;

        view = LayoutInflater.from(context).inflate(R.layout.size_selector, null);
        textView = (TextView) view.findViewById(R.id.txtLabel);
        textGuide = (TextView) view.findViewById(R.id.txtGuide);
        textFiller = (TextView) view.findViewById(R.id.txtFiller);
        textFiller.setText(context.getString(R.string.size_guide).toUpperCase());
        textGuide.setText(context.getString(R.string.size_guide).toUpperCase());

        textGuide.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onFailure(null);
            }
        });

        addView(view);
    }

    public void init(DropdownClass ddc, boolean showGuide, int colorId, ECNCallback callback) {
        data = ddc;
        mCallback = callback;
        position = -1;
        if (data != null) {
            textView.setText(data.getLabel());
            setSizes(this.data.getValues());
            if (showGuide) {
                textGuide.setVisibility(VISIBLE);
                textFiller.setVisibility(INVISIBLE);
                textGuide.setTextColor(ContextCompat.getColor(context, colorId));
            }
        } else {
            textView.setText("");
            textGuide.setVisibility(GONE);
            textFiller.setVisibility(GONE);
            setSizes(new ArrayList<String>());
        }
    }

    public void setSizes(List<String> list) {
        if (!list.isEmpty()) {
            collection = list;
            imageList = new ArrayList<>();
            final LinearLayout layout = (LinearLayout) view.findViewById(R.id.grid);
            layout.removeAllViews();

            DisplayMetrics dm = HelperClass.getDisplayMetrics(context);
            dens = dm.density;
            int height = dm.widthPixels;
            height /= 10;

            for (int i = 0; i < list.size(); i++) {
                MyTextView img = new MyTextView(context);
                img.setText(list.get(i));
                img.setGravity(Gravity.CENTER);
                img.setTextSize(7 * dens);
                img.setTextColor(ContextCompat.getColor(context, R.color.PrimaryColorText));
                int width = height + (list.get(i).length() - 1) * (int) (10 * dens);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
                layoutParams.setMargins(convertPixels(3), convertPixels(3), convertPixels(3), convertPixels(3));
                img.setLayoutParams(layoutParams);
                img.setTag(i);
                img.setBackgroundResource(R.drawable.border_transparent);
                if(mCallback != null) {
                    img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setSelection((int) v.getTag());
                        }
                    });
                }
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
        if(mCallback != null) {
            position = pos;
            for (int i = 0; i < imageList.size(); i++) {
                imageList.get(i).setBackgroundResource(R.drawable.border_transparent);
            }
            imageList.get(pos).setBackgroundResource(R.drawable.border_grey);
            mCallback.onSuccess(getSelection());
        }
    }
}
