package com.ecomnationmobile.library.Control;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ecomnationmobile.library.Common.ECNCallback;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Data.ExtendedVariant;
import com.ecomnationmobile.library.Data.Vendor;
import com.ecomnationmobile.library.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 8/25/2016.
 */
public class VendorSelector  extends FrameLayout {

    TextView priceText, shippingText;
    Button addToCart;
    Spinner nameSpinner;
    Context context;
    View view;
    String currencyText;
    Vendor currentVendor;
    ExtendedVariant currentVariant;
    List<String> sList;
    List<Vendor> data;
    ECNCallback mCallback;

    public VendorSelector(Context context) {
        this(context, null);
    }

    public VendorSelector(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VendorSelector(final Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        this.context = context;

        view = LayoutInflater.from(context).inflate(R.layout.vendor_item, null);
        priceText = (TextView) view.findViewById(R.id.price);
        shippingText = (TextView) view.findViewById(R.id.shipping);
        addToCart = (Button) view.findViewById(R.id.addToCart);
        nameSpinner = (Spinner) view.findViewById(R.id.vendorName);

        addView(view);
    }

    public void init(String currency,int colorId ,ECNCallback callback) {
        mCallback = callback;
        currencyText = currency;
        addToCart.setTextColor(ContextCompat.getColor(context,colorId));
        addToCart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onSuccess("");
            }
        });
    }

    public void update(List<Vendor> list,final String variantId) {
        this.data = list;
        sList = new ArrayList<>();
        for(Vendor v : data) {
            sList.add(v.getName());
        }

        if (sList != null && !sList.isEmpty()) {
            ArrayAdapter<String> nameAdapter = new ArrayAdapter<>(context, R.layout.attribute_spinner_item, sList);
            nameAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
            nameSpinner.setAdapter(nameAdapter);
            nameSpinner.setVisibility(VISIBLE);
            nameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    currentVendor = data.get(position);
                    for(ExtendedVariant v : currentVendor.getVariants()) {
                        if(v.getId().equals(variantId)) {
                            currentVariant = v;
                            break;
                        }
                    }
                    if(currentVariant != null) {
                        priceText.setText(HelperClass.formatCurrency(currencyText, currentVariant.getPrice()));
                        shippingText.setText("Shipping: "+HelperClass.formatCurrency(currencyText, currentVendor.getShipping_charge()));
                    }else {
                        priceText.setText("");
                        shippingText.setText("");
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            addToCart.setVisibility(VISIBLE);
        } else {
            nameSpinner.setVisibility(GONE);
            priceText.setText("");
            shippingText.setText("");
            addToCart.setVisibility(GONE);
        }
    }

    private void setVendorData(int pos) {

    }

    public String getNameSelection() {
        return nameSpinner.getItemAtPosition(nameSpinner.getSelectedItemPosition()).toString();
    }

    public String getVendorId() {
        return data.get(nameSpinner.getSelectedItemPosition()).getId();
    }

    public void setNameSelectedListener(AdapterView.OnItemSelectedListener listener) {
        nameSpinner.setOnItemSelectedListener(listener);
    }
}
