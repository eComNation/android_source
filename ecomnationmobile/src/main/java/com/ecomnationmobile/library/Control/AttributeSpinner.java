package com.ecomnationmobile.library.Control;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ecomnationmobile.library.Data.DropdownClass;
import com.ecomnationmobile.library.R;

import java.util.List;

/**
 * Created by Abhi on 17-01-2016.
 */
public class AttributeSpinner extends LinearLayout {

    TextView textView;
    Context context;
    View view;
    Spinner spinner;
    DropdownClass data;
    ArrayAdapter<String> dataAdapter;

    public AttributeSpinner(Context context) {
        this(context, null);
    }

    public AttributeSpinner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AttributeSpinner(final Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);

        this.context = context;

        setOrientation(VERTICAL);

        view = LayoutInflater.from(context).inflate(R.layout.attribute_spinner,null);
        textView = (TextView)view.findViewById(R.id.txtLabel);
        spinner = (Spinner) view.findViewById(R.id.txtSpinner);

        addView(view);
    }

    public void init(DropdownClass data) {
        this.data = data;
        textView.setText(data.getLabel());
        setSpinner(this.data.getValues());
    }

    public void setSpinner(List<String> list) {
        spinner.setBackgroundColor(ContextCompat.getColor(context,R.color.TRANSPARENT));
        dataAdapter = new ArrayAdapter<String>(context, R.layout.attribute_spinner_item) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView)v.findViewById(R.id.text)).setText("");
                    ((TextView)v.findViewById(R.id.text)).setHint(getItem(getCount())); //"Hint to be displayed"
                }
                return v;
            }

            @Override
            public int getCount() {
                return super.getCount()-1; // you dont display last item. It is used as hint.
            }

        };
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for(String str : list) {
            dataAdapter.add(str.toUpperCase());
        }
        dataAdapter.add("Please select");
        spinner.setAdapter(dataAdapter);
        spinner.setSelection(dataAdapter.getCount());
    }

    public int getCount() {
        return dataAdapter.getCount();
    }

    public int getSelectedPosition() {
        return spinner.getSelectedItemPosition();
    }

    public void setSelection(int n) {
        spinner.setSelection(n);
    }

    public String getSelection() {
        return spinner.getItemAtPosition(spinner.getSelectedItemPosition()).toString();
    }

    public String getLabel() {
        return data.getLabel();
    }

    public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener listener) {
        spinner.setOnItemSelectedListener(listener);
    }
}
