package com.eComNation.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eComNation.R;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Data.Product;
import com.ecomnationmobile.library.Data.ProductAttribute;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by Abhi on 28-03-2016.
 */
public class ProductAttributesFragment extends Fragment {
    View mView;
    Product product;
    LinearLayout attributeLayout;
    List<ProductAttribute> attributes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.product_attributes, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mView = getView();

        if(mView != null) {
            product = (new Gson()).fromJson(HelperClass.getSharedString(getActivity(), "product"), Product.class);

            attributes = product.getFilter_attributes();
            attributeLayout = (LinearLayout) mView.findViewById(R.id.attributes);
            attributeLayout.removeAllViews();
            for (ProductAttribute drop : attributes) {
                if(!drop.getValues().isEmpty()) {
                    View view = getActivity().getLayoutInflater().inflate(R.layout.label_label_item,null);
                    ((TextView) view.findViewById(R.id.txtLabel)).setText(drop.getName().toUpperCase());
                    String str = TextUtils.join(", ", drop.getValues());
                    ((TextView) view.findViewById(R.id.txtText)).setText(str.toUpperCase());
                    attributeLayout.addView(view);
                }
            }
        }
    }
}

