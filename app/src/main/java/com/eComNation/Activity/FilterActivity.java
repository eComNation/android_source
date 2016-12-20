package com.eComNation.Activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.eComNation.R;
import com.ecomnationmobile.library.Common.ECNCallback;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Control.ExpandableView;
import com.ecomnationmobile.library.Control.PriceSelector;
import com.ecomnationmobile.library.Data.ECNResponse;
import com.ecomnationmobile.library.Data.Facet;
import com.ecomnationmobile.library.Data.FilterAttribute;
import com.ecomnationmobile.library.Data.KeyValuePair;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Abhi on 24-12-2015.
 */
public class FilterActivity extends FragmentActivity {
    List<ExpandableView> filterList;
    LinearLayout filters;
    String selectedFilters, displayFilters;
    int minPrice, maxPrice;
    boolean is_listing;
    List<FilterAttribute> filterAttributes;
    PriceSelector priceSelector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filters);

        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(0);
                onBackPressed();
            }
        });

        is_listing = getIntent().getBooleanExtra("is_listing", false);

        findViewById(R.id.txtApplyFilter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getSelectedFilters();

                if (displayFilters.endsWith(","))
                    displayFilters = displayFilters.substring(0, displayFilters.length() - 1);

                HelperClass.putSharedString(FilterActivity.this, getString(R.string.selected_filters), selectedFilters);
                HelperClass.putSharedString(FilterActivity.this, "display_filters", displayFilters);
                HelperClass.putSharedInt(FilterActivity.this, "min_price", minPrice);
                HelperClass.putSharedInt(FilterActivity.this, "max_price", maxPrice);

                setResult(100);
                finish();
            }
        });

        findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (ExpandableView ev : filterList) {
                    ev.reset();
                }
                priceSelector.reset();
                HelperClass.putSharedString(FilterActivity.this, getString(R.string.selected_filters), null);
                HelperClass.putSharedString(FilterActivity.this, "display_filters", null);
            }
        });

        String content = HelperClass.getSharedString(this, "filter");

        if (content != null) {
            ECNResponse response = (new Gson()).fromJson(content, ECNResponse.class);
            if (response != null) {
                filterAttributes = response.getFilterAttributes();

                findViewById(R.id.txtApplyFilter).setVisibility(View.GONE);
                findViewById(R.id.reset).setVisibility(View.GONE);
                if (filterAttributes != null && !filterAttributes.isEmpty()) {
                    setFilter();
                }
            }
        }
    }

    private void setFilter() {
        String facets;
        if (is_listing)
            facets = HelperClass.getSharedString(this, "listing_facets");
        else
            facets = HelperClass.getSharedString(this, "search_facets");

        if(facets != null) {
            filters = (LinearLayout) findViewById(R.id.filters);
            filterList = new ArrayList<>();

            Facet facetValue = new Gson().fromJson(facets, Facet.class);
            if(facetValue != null) {
                priceSelector = new PriceSelector(this);

                priceSelector.init("Price", facetValue.getMin(), facetValue.getMax(), new ECNCallback() {
                    @Override
                    public void onSuccess(String s) {
                        getSelectedFilters();
                        if (!selectedFilters.equals("") || minPrice > 0 || maxPrice > 0) {
                            findViewById(R.id.txtApplyFilter).setVisibility(View.VISIBLE);
                            findViewById(R.id.reset).setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(KeyValuePair keyValuePair) {
                        getSelectedFilters();
                        if (selectedFilters.equals("") && (minPrice < 0 || maxPrice < 0)) {
                            findViewById(R.id.reset).setVisibility(View.GONE);
                        }
                    }
                });

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                layoutParams.setMargins(20, 20, 20, 0);
                priceSelector.setLayoutParams(layoutParams);

                filters.addView(priceSelector);

                for (FilterAttribute fa : filterAttributes) {
                    if (fa.getValues() != null && !fa.getValues().isEmpty()) {
                        ExpandableView ev = new ExpandableView(this);

                        ev.init(fa.getName(), fa.getValues(), facets, new ECNCallback() {
                            @Override
                            public void onSuccess(String result) {
                                getSelectedFilters();
                                if (!selectedFilters.equals("") || (minPrice > 0 || maxPrice > 0)) {
                                    findViewById(R.id.txtApplyFilter).setVisibility(View.VISIBLE);
                                    findViewById(R.id.reset).setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onFailure(KeyValuePair error) {
                                getSelectedFilters();
                                if (selectedFilters.equals("") && (minPrice < 0 || maxPrice < 0)) {
                                    findViewById(R.id.reset).setVisibility(View.GONE);
                                }
                            }
                        });

                        ev.setLayoutParams(layoutParams);

                        if (ev.getCount() > 0) {
                            filterList.add(ev);
                            filters.addView(ev);
                        }
                    }
                }
                getSelectedFilters();
                if (!selectedFilters.equals("") || (minPrice > 0 || maxPrice > 0)) {
                    findViewById(R.id.txtApplyFilter).setVisibility(View.VISIBLE);
                    findViewById(R.id.reset).setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void getSelectedFilters() {
        selectedFilters = "";
        displayFilters = "";
        minPrice = priceSelector.getMinSelected();
        maxPrice = priceSelector.getMaxSelected();
        for (int i = 0; i < filterList.size(); i++) {
            String str1 = filterList.get(i).getSelection();
            String str2 = filterList.get(i).getDisplaySelection();
            selectedFilters += str1;
            displayFilters += str2;
            if (i != filterList.size() - 1) {
                if (!str1.equals(""))
                    selectedFilters += ",";
                if (!str2.equals(""))
                    displayFilters += ",";
            }
        }
    }
}
