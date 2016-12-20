package com.ecomnationmobile.library.Control;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.R;

import java.util.List;

/**
 * Created by Abhi on 14-01-2016.
 */
public class CustomSpinner extends LinearLayout {

    TextView textView;
    Context context;
    View view;
    Spinner spinner;

    public CustomSpinner(Context context) {
        this(context, null);
    }

    public CustomSpinner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomSpinner(final Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        this.context = context;

        setOrientation(VERTICAL);

        view = LayoutInflater.from(context).inflate(R.layout.custom_spinner, null);
        textView = (TextView) view.findViewById(R.id.txtLabel);
        spinner = (Spinner) view.findViewById(R.id.txtSpinner);

        addView(view);
    }

    public void setSpinner(List<String> list) {
        spinner.setBackgroundColor(ContextCompat.getColor(context,R.color.TRANSPARENT));
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(context, R.layout.spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spinner.setAdapter(dataAdapter);

        int width = HelperClass.getDisplayMetrics(context).widthPixels - 20;
        spinner.setDropDownWidth(width);
    }

    public void init(String string) {
        textView.setText(string);
    }

    public void setSelection(int index) {
        spinner.setSelection(index);
    }

    public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener listener) {
        spinner.setOnItemSelectedListener(listener);
    }
}
