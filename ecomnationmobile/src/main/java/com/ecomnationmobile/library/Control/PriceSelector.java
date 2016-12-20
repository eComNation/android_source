package com.ecomnationmobile.library.Control;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ecomnationmobile.library.Common.CustomAnimation;
import com.ecomnationmobile.library.Common.ECNCallback;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.R;

/**
 * Created by User on 5/20/2016.
 */
public class PriceSelector extends FrameLayout {

    LinearLayout selector, list, seekBarHolder;
    ImageView image;
    View view;
    RangeSeekBar seekBar;
    String displayText;
    boolean is_active;
    Context mContext;
    int absMax, absMin;
    ECNCallback mCallback;
    TextView selectedText, minText, maxText;

    public PriceSelector(Context context) {
        this(context, null);
    }

    public PriceSelector(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PriceSelector(final Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mContext = context;

        view = LayoutInflater.from(context).inflate(R.layout.price_filter, null);

        selector = (LinearLayout) view.findViewById(R.id.filterSelector);
        list = (LinearLayout) view.findViewById(R.id.filterList);
        seekBarHolder = (LinearLayout) view.findViewById(R.id.seekBarHolder);

        minText = (TextView) view.findViewById(R.id.minText);
        maxText = (TextView) view.findViewById(R.id.maxText);

        image = (ImageView) view.findViewById(R.id.dropdown);
        selectedText = (TextView) view.findViewById(R.id.selection);

        addView(view);

        selector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list.getVisibility() == GONE) {
                    image.setImageResource(R.drawable.uparrow);
                    list.setVisibility(VISIBLE);
                    CustomAnimation.slide_down(mContext, list);
                    selectedText.setText("");
                } else {
                    CustomAnimation.slide_up(mContext, list);
                    list.setVisibility(GONE);
                    image.setImageResource(R.drawable.dropdown_arrow);
                    selectedText.setText(displayText);
                }
            }
        });
    }

    public void init(String labelText, double min, double max, ECNCallback callback) {
        mCallback = callback;
        displayText = "";
        absMax = (int) max;
        absMin = (int) min;

        seekBar = new RangeSeekBar(mContext,absMin, absMax);

        seekBarHolder.addView(seekBar);

        ((TextView) selector.findViewById(R.id.title)).setText(HelperClass.capitalizeFirst(labelText));

        int minV = HelperClass.getSharedInt(mContext, "min_price");
        int maxV = HelperClass.getSharedInt(mContext, "max_price");
        is_active = !(minV == -1 || maxV == -1);
        if (is_active) {
            setValues(minV, maxV);
            selectedText.setText(displayText);
        } else {
            setValues(absMin, absMax);
        }

        seekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, double minValue, double maxValue) {
                is_active = true;
                setValues((int) minValue, (int) maxValue);
                mCallback.onSuccess("");
                mCallback.onFailure(null);
            }
        });
    }

    public int getMaxSelected() {
        if (is_active)
            return (int) seekBar.getSelectedMaxValue();
        else
            return -1;
    }

    public int getMinSelected() {
        if (is_active)
            return (int) seekBar.getSelectedMinValue();
        else
            return -1;
    }

    public String getDisplaySelection() {
        return displayText;
    }

    public void reset() {
        displayText = "";
        selectedText.setText("");
        is_active = false;
        setValues(absMin, absMax);
        HelperClass.putSharedInt(mContext, "min_price", -1);
        HelperClass.putSharedInt(mContext, "max_price", -1);
        mCallback.onFailure(null);
    }

    private void setValues(int min, int max) {
        seekBar.setSelectedMinValue(min);
        seekBar.setSelectedMaxValue(max);

        minText.setText(String.format(mContext.getString(R.string.price_string),min));
        maxText.setText(String.format(mContext.getString(R.string.price_string),max));

        if (is_active) {
            displayText = minText.getText() + " - " + maxText.getText();
        }
    }

    public int getCount() {
        if (list != null)
            return list.getChildCount();
        return 0;
    }
}
