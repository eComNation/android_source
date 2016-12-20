package com.eComNation.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eComNation.Activity.MadeToMeasureActivity;
import com.eComNation.R;
import com.ecomnationmobile.library.Common.ECNCallback;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Data.DropdownClass;
import com.ecomnationmobile.library.Data.KeyValuePair;
import com.ecomnationmobile.library.Data.Product;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by User on 6/6/2016.
 */
public class MeasureStartFragment extends Fragment {

    View mView;
    List<DropdownClass> attributes;
    LinearLayout attributesLayout;
    Product product;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.measure_start_layout, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mView = getView();

        HelperClass.putSharedInt(getActivity(), "measure_level", -1);

        HelperClass.putSharedString(getActivity(),"variant_hash","");

        if (mView != null) {
            mView.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            mView.findViewById(R.id.btnNext).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MadeToMeasureActivity) getActivity()).onNextSelected();
                }
            });
            getFilterAttributes();
        }
    }

    private void getFilterAttributes() {
        String url = getString(R.string.production_base_url) + getString(R.string.api) + "store/products/"+getString(R.string.measure_product);
        HelperClass.getData(getActivity(), url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    JSONObject productObject = (JSONObject) obj.get("product");
                    String productString = productObject.toString();
                    HelperClass.putSharedString(getActivity(), "product", productString);

                    product = (new Gson()).fromJson(productString, Product.class);
                    if (product != null) {

                        attributesLayout = (LinearLayout)mView.findViewById(R.id.attributeHeaders);
                        attributesLayout.removeAllViews();

                        int total = product.getOptions().size()+1;
                        ((TextView)mView.findViewById(R.id.txtTitle)).setText("A SIMPLE "+total+" STEP PROCESS");

                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        lp.setMargins(convertPixels(20), convertPixels(10), convertPixels(20), convertPixels(10));
                        for (KeyValuePair fa : product.getOptions()) {
                            TextView txt = new TextView(getActivity());
                            txt.setPadding(convertPixels(20), convertPixels(30), convertPixels(20), convertPixels(30));
                            txt.setTextColor(getResources().getColor(R.color.PrimaryColorText));
                            txt.setTextSize(16);
                            txt.setGravity(Gravity.CENTER);
                            txt.setBackgroundResource(R.color.WHITE);
                            txt.setLayoutParams(lp);
                            txt.setText("Select "+fa.getKey());
                            attributesLayout.addView(txt);
                        }
                        TextView txt = new TextView(getActivity());
                        txt.setPadding(convertPixels(20), convertPixels(30), convertPixels(20), convertPixels(30));
                        txt.setTextColor(getResources().getColor(R.color.PrimaryColorText));
                        txt.setTextSize(16);
                        txt.setGravity(Gravity.CENTER);
                        txt.setBackgroundResource(R.color.WHITE);
                        txt.setLayoutParams(lp);
                        txt.setText("Select Quantity");
                        attributesLayout.addView(txt);

                        attributes = HelperClass.generate_dropdown(product);
                        HelperClass.putSharedString(getActivity(), "measure_variants",new Gson().toJson(attributes));

                        mView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(KeyValuePair error) {
            }
        });
    }

    private int convertPixels(float pix) {
        return (int) (pix * HelperClass.getDisplayMetrics(getActivity()).density);
    }
}
