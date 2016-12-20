package com.ecomnationmobile.library.Control;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ecomnationmobile.library.R;

/**
 * Created by Abhi on 14-01-2016.
 */
public class CartItem extends LinearLayout {

    TextView mTextView;
    int mCount;
    View mView;

    public CartItem(Context context) {
        this(context, null);
    }

    public CartItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CartItem(final Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setOrientation(HORIZONTAL);

        setGravity(Gravity.CENTER);

        mView = LayoutInflater.from(context).inflate(R.layout.cart_count_layout, null);

        mTextView = (TextView) mView.findViewById(R.id.cartCount);

        setCount(0);

        addView(mView);
    }

    public void init(String labelText) {
        mTextView.setText(labelText);
    }

    public void setCount(int count) {
        mCount = count;
        mTextView.setText(""+mCount);
        setBackground();
    }

    private void setBackground() {
        if(mCount == 0)
            mTextView.setBackgroundResource(R.drawable.white_border);
        else
            mTextView.setBackgroundResource(R.color.DARK_ORANGE);
    }
}

