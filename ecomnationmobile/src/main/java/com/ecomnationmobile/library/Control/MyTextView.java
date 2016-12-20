package com.ecomnationmobile.library.Control;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by User on 7/16/2016.
 */
public class MyTextView extends TextView {

    public MyTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyTextView(Context context) {
        super(context);
        init();
    }

    public void init() {
        int style = 0;
        if(getTypeface() != null)
            style = getTypeface().getStyle();
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "font/font.ttf");
        setTypeface(tf, style);
    }
}