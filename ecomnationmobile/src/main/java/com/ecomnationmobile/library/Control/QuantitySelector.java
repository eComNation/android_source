package com.ecomnationmobile.library.Control;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ecomnationmobile.library.Data.DropdownClass;
import com.ecomnationmobile.library.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 7/19/2016.
 */
public class QuantitySelector extends LinearLayout {

    TextView textView;
    Spinner sizeSpinner, quantitySpinner;
    Context context;
    View view;
    float dens;
    List<String> qList, sList;
    DropdownClass data;

    public QuantitySelector(Context context) {
        this(context, null);
    }

    public QuantitySelector(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QuantitySelector(final Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        this.context = context;

        setOrientation(VERTICAL);

        view = LayoutInflater.from(context).inflate(R.layout.quantity_selector, null);
        textView = (TextView) view.findViewById(R.id.txtLabel);
        sizeSpinner = (Spinner) view.findViewById(R.id.sizeSpinner);
        quantitySpinner = (Spinner) view.findViewById(R.id.quantitySpinner);

        qList = new ArrayList<>();
        for (int i = 0; i <= 6; i++)
            qList.add("" + i);

        addView(view);
    }

    public void init(DropdownClass data) {
        this.data = data;
        textView.setText(data.getLabel().toUpperCase());
        setValues(this.data.getValues());
    }

    public void setValues(List<String> list) {
        sList = list;
        if (sList != null && !sList.isEmpty()) {
            ArrayAdapter<String> sizeAdapter = new ArrayAdapter<>(context, R.layout.attribute_spinner_item, sList);
            sizeAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
            sizeSpinner.setAdapter(sizeAdapter);
        } else
            sizeSpinner.setVisibility(GONE);

        ArrayAdapter<String> quantityAdapter = new ArrayAdapter<>(context, R.layout.attribute_spinner_item, qList);
        quantityAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        quantitySpinner.setAdapter(quantityAdapter);
    }

    public String getSizeSelection() {
        return sizeSpinner.getItemAtPosition(sizeSpinner.getSelectedItemPosition()).toString();
    }

    public String getLabel() {
        return textView.getText().toString();
    }

    public String getQuantitySelection() {
        return quantitySpinner.getItemAtPosition(quantitySpinner.getSelectedItemPosition()).toString();
    }

    private int convertPixels(float pix) {
        return (int) (pix * dens);
    }

    public void setSizeSelectedListener(AdapterView.OnItemSelectedListener listener) {
        sizeSpinner.setOnItemSelectedListener(listener);
    }

    public void setQuantitySelectedListener(AdapterView.OnItemSelectedListener listener) {
        quantitySpinner.setOnItemSelectedListener(listener);
    }

}
