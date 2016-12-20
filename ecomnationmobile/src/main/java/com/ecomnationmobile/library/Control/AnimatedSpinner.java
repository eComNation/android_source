package com.ecomnationmobile.library.Control;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ecomnationmobile.library.R;

import java.util.List;

/**
 * Created by Abhi on 30-11-2015.
 */
public class AnimatedSpinner extends LinearLayout {

    TextView textView;
    Context context;
    View view;
    Spinner spinner;
    String spinnerHint;
    int GRAY = 0, GREEN = 0;
    Drawable grayLine;

    public AnimatedSpinner(Context context) {
        this(context, null);
    }

    public AnimatedSpinner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimatedSpinner(final Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        this.context = context;

        GRAY = ContextCompat.getColor(context,R.color.DARK_GRAY);
        GREEN = ContextCompat.getColor(context,R.color.AppGreen);
        grayLine = ContextCompat.getDrawable(context,R.drawable.vertical_divider);

        setOrientation(VERTICAL);

        view = LayoutInflater.from(context).inflate(R.layout.label_spinner_item,null);
        textView = (TextView)view.findViewById(R.id.txtLabel);
        spinner = (Spinner) view.findViewById(R.id.txtSpinner);

        addView(view);
    }

    public void setSpinner(List<String> list) {
        spinner.setBackgroundColor(ContextCompat.getColor(context,R.color.TRANSPARENT));
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(context, R.layout.spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spinner.setAdapter(dataAdapter);
    }
}
