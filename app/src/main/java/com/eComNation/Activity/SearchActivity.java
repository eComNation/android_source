package com.eComNation.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.eComNation.Adapter.ProductListAdapter;
import com.eComNation.Common.Utility;
import com.eComNation.R;
import com.ecomnationmobile.library.Common.ECNCallback;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Control.GridViewWithHeaderAndFooter;
import com.ecomnationmobile.library.Control.ProgressDialogView;
import com.ecomnationmobile.library.Data.ECNResponse;
import com.ecomnationmobile.library.Data.KeyValuePair;
import com.ecomnationmobile.library.Data.Product;
import com.google.gson.Gson;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Abhi on 13-03-2016.
 */
public class SearchActivity extends FragmentActivity {

    EditText editSearch;
    TextView selectedFilterText;
    ProgressDialog dialog;
    ProductListAdapter mAdapter;
    List<Product> categoryProducts;
    List<String> recentSearchList;
    String[] sort_criteria;
    Spinner sortSpinner;
    boolean canLoadMore, is_sort_activated, show_filters;
    String url, selected_filters, search_query;
    ProgressBar oldDataProgress;
    LinearLayout filter, actionBar, headerView, sortnCount, searchList, footerView;
    View progressBar, emptyView;
    int counter, page, per_page;
    int myLastVisiblePos, layoutId;
    int minPrice, maxPrice;
    GridViewWithHeaderAndFooter mGridView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dialog = new ProgressDialogView(this, "", R.drawable.progress);

        showSearch();
    }

    @Override
    public void onResume() {
        super.onResume();
        HelperClass.putSharedString(this, getString(R.string.product), null);
    }

    public void showSearch() {
        setContentView(R.layout.search_layout);

        dialog.setMessage("Please wait...");

        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HelperClass.putSharedString(SearchActivity.this, "search_facets", null);
                onBackPressed();
            }
        });

        findViewById(R.id.txtClear).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                dialog.show();
                HelperClass.putSharedString(SearchActivity.this, "recent_searches", null);
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setUpRecentSearches();
                        dialog.dismiss();
                    }
                }, 1000);
            }
        });

        editSearch = (EditText) findViewById(R.id.editSearch);
        editSearch.setCursorVisible(true);
        searchList = (LinearLayout) findViewById(R.id.recentSearchList);

        setUpRecentSearches();

        editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search_query = editSearch.getText().toString();
                    if (!search_query.equals("")) {
                        if (recentSearchList == null)
                            recentSearchList = new ArrayList<>();
                        if (recentSearchList.contains(search_query)) {
                            recentSearchList.remove(search_query);
                        }
                        recentSearchList = reverse(recentSearchList);
                        recentSearchList.add(search_query);
                        recentSearchList = reverse(recentSearchList);
                        if (recentSearchList.size() > 10)
                            recentSearchList = recentSearchList.subList(0, 10);

                        HelperClass.putSharedString(SearchActivity.this, "recent_searches", TextUtils.join(",", recentSearchList));
                        if (getCurrentFocus() != null) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        }
                        showResults();
                    }
                    return true;
                }

                return false;
            }
        });

        findViewById(R.id.btnSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editSearch.setText("");
            }
        });
    }

    private void setUpRecentSearches() {
        searchList.removeAllViews();
        String searches = HelperClass.getSharedString(this, "recent_searches");

        if (searches != null) {
            findViewById(R.id.txtClear).setVisibility(View.VISIBLE);

            recentSearchList = new ArrayList<>(Arrays.asList(searches.split(",")));
            for (String s : recentSearchList) {
                TextView text = new TextView(this);
                text.setText(s);
                text.setTextSize(16);
                text.setTypeface(null, Typeface.BOLD);
                text.setTextColor(Color.BLACK);
                text.setPadding(10, 30, 10, 10);
                text.setClickable(true);
                text.setBackgroundResource(R.drawable.simple_click);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(5, 5, 5, 5);
                text.setLayoutParams(params);

                searchList.addView(text);
                text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editSearch.setText(((TextView) v).getText());
                        editSearch.setSelection(editSearch.getText().length());
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        editSearch.requestFocus();
                        imm.showSoftInput(editSearch, 0);
                    }
                });
            }
        }
        else {
            findViewById(R.id.txtClear).setVisibility(View.GONE);
        }
    }

    public void showResults() {
        setContentView(R.layout.product_listing);

        setTitle("");

        selectedFilterText = (TextView) findViewById(R.id.txtSelectedFilter);

        dialog.setMessage("Updating...");

        ImageButton item = (ImageButton) findViewById(R.id.cart);
        LayerDrawable icon = (LayerDrawable) item.getDrawable();

        // Update LayerDrawable's BadgeDrawable
        Utility.setBadgeCount(this, icon, HelperClass.getCartCount(this));
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.this,CheckOutActivity.class);
                intent.putExtras(new Bundle());
                startActivity(intent);
            }
        });

        counter = 0;
        per_page = 16;
        canLoadMore = false;
        is_sort_activated = false;

        (findViewById(R.id.btnBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HelperClass.putSharedString(SearchActivity.this, "search_facets", null);
                onBackPressed();
            }
        });

        mGridView = (GridViewWithHeaderAndFooter) findViewById(R.id.productGrid);

        final ImageButton tglButton = (ImageButton)findViewById(R.id.tglLayoutView);
        if(getResources().getBoolean(R.bool.has_display_option)) {
            tglButton.setVisibility(View.VISIBLE);
            tglButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int columns = mGridView.getNumColumns();
                    columns = (columns % 2) + 1;
                    switch (columns) {
                        case 1:
                            tglButton.setImageResource(R.drawable.list);
                            layoutId = R.layout.favourites_list_item;
                            setUpGridView();
                            break;
                        case 2:
                            tglButton.setImageResource(R.drawable.grid);
                            layoutId = R.layout.product_preview;
                            setUpGridView();
                            break;
                    }
                    mGridView.setNumColumns(columns);
                }
            });
        }
        else {
            tglButton.setVisibility(View.GONE);
        }

        float height = 130 * HelperClass.getDisplayMetrics(this).density;
        View headerPadding = new View(SearchActivity.this);
        headerPadding.setBackgroundResource(R.color.LIGHT_BACK);
        headerPadding.setMinimumHeight((int)height);
        mGridView.addHeaderView(headerPadding);

        sort_criteria = getResources().getStringArray(R.array.sort_criteria);

        List<String> sortList = Arrays.asList(getResources().getStringArray(R.array.sort_display));

        filter = (LinearLayout) findViewById(R.id.filterSort);
        actionBar = (LinearLayout) findViewById(R.id.actionBar);
        headerView = (LinearLayout) findViewById(R.id.headerView);
        footerView = (LinearLayout) findViewById(R.id.footerView);
        progressBar = findViewById(R.id.progressBar);
        sortnCount = (LinearLayout) findViewById(R.id.sortnCount);
        emptyView = findViewById(R.id.emptyView);

        emptyView.findViewById(R.id.btnRetry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInitialData();
            }
        });

        oldDataProgress = (ProgressBar) findViewById(R.id.oldDataProgress);
        oldDataProgress.setVisibility(View.GONE);

        findViewById(R.id.filter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMain = new Intent(SearchActivity.this, FilterActivity.class);
                startActivityForResult(intentMain, 0);
            }
        });

        findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSearch();
            }
        });

        findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HelperClass.putSharedInt(SearchActivity.this, "min_price", -1);
                HelperClass.putSharedInt(SearchActivity.this, "max_price", -1);
                HelperClass.putSharedString(SearchActivity.this, getString(R.string.selected_filters), null);
                HelperClass.putSharedString(SearchActivity.this, "display_filters", null);
                getFilterData();
            }
        });

        sortSpinner = (Spinner) findViewById(R.id.sortSpinner);

        sortSpinner.setBackgroundColor(getResources().getColor(R.color.TRANSPARENT));
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.sort_spinner_item, sortList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        sortSpinner.setPadding(5, 5, 5, 5);
        sortSpinner.setAdapter(dataAdapter);

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (is_sort_activated)
                    setInitialURL(position);
                else
                    is_sort_activated = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        layoutId = R.layout.product_preview;

        getInitialData();
    }

    private void getInitialData() {
        HelperClass.putSharedString(SearchActivity.this, getString(R.string.selected_filters), null);
        HelperClass.putSharedString(SearchActivity.this, "search_facets", null);
        HelperClass.putSharedInt(SearchActivity.this, "min_price", -1);
        HelperClass.putSharedInt(SearchActivity.this, "max_price", -1);

        setTitle(search_query);

        setInitialURL(0);

        String facets_url = url.replace("facets=false", "facets=true");

        HelperClass.getData(this, facets_url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                HelperClass.putSharedString(SearchActivity.this, "search_facets", result);
            }

            @Override
            public void onFailure(KeyValuePair error) {
            }
        });
    }

    private void setInitialURL(int position) {
        page = 1;
        String selected = "";
        url = getString(R.string.production_base_url) + getString(R.string.api) + "store/products?";
        url += "search_criteria=" + encode(search_query);
        if (position < 0)
            position = 0;
        url += "&sort_criteria=" + sort_criteria[position];
        show_filters = (selected_filters != null && !selected_filters.equals("")) || (minPrice > 0 || maxPrice > 0);
        if (show_filters) {
            if (minPrice > 0 || maxPrice > 0) {
                url += "&price_between=" + minPrice + "," + maxPrice;
                selected += "PRICE";
            }
            if ((selected_filters != null && !selected_filters.equals(""))) {
                url += "&filter_attributes=" + selected_filters;
                if (!selected.equals(""))
                    selected += ",";
                selected += HelperClass.getSharedString(this, "display_filters");
            }
            selectedFilterText.setText(selected);
            selectedFilterText.setVisibility(View.VISIBLE);
            footerView.setVisibility(View.VISIBLE);
        } else {
            selectedFilterText.setText("");
            selectedFilterText.setVisibility(View.GONE);
            footerView.setVisibility(View.GONE);
        }
        url += "&facets=false&per_page=" + per_page + "&page=" + page;

        getData();
    }

    private List<String> reverse(List<String> list) {
        Collections.reverse(list);
        return list;
    }

    private String encode(String value) {
        String encodedValue = "";
        try {
            encodedValue = URLEncoder.encode(value, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encodedValue;
    }

    private void setTitle(String title) {
        ((TextView) findViewById(R.id.title)).setText(title);
    }

    private void getData() {
        dialog.show();
        HelperClass.getData(this, url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                ECNResponse response = (new Gson()).fromJson(result, ECNResponse.class);
                categoryProducts = response.getProducts();
                page++;
                canLoadMore = categoryProducts.size() == per_page;
                displayData(response.getPagination_info().getTotal_entries());

                dialog.dismiss();
            }

            @Override
            public void onFailure(KeyValuePair error) {
                dialog.dismiss();
                displayError(error.getKey());
            }
        });
    }

    private void setUpGridView() {
        mAdapter = new ProductListAdapter(SearchActivity.this, layoutId, categoryProducts, false);
        mGridView.setAdapter(mAdapter);
    }

    private void displayData(int count) {
        if (categoryProducts != null && !categoryProducts.isEmpty()) {
            sortSpinner.setEnabled(true);
            findViewById(R.id.sort).setBackgroundResource(R.drawable.white_click);
            emptyView.setVisibility(View.GONE);
            mGridView.setVisibility(View.VISIBLE);
            sortnCount.setVisibility(View.VISIBLE);

            ((TextView) findViewById(R.id.txtProductCount)).setText(String.format(getString(R.string.integer), count));

            setUpGridView();

            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    Intent intentMain = Utility.getDetailsIntent(SearchActivity.this);
                    Bundle bundle = new Bundle();
                    bundle.putLong("id", categoryProducts.get(position).getId());
                    intentMain.putExtras(bundle);
                    startActivity(intentMain);
                }
            });

            myLastVisiblePos = mGridView.getFirstVisiblePosition();
            mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    int currentFirstVisPos = view.getFirstVisiblePosition();
                    if (currentFirstVisPos > myLastVisiblePos) {
                        if (counter == 0) {
                            headerView.animate().translationY(-headerView.getHeight()).setDuration(500);
                            counter = 1;
                        }
                    }
                    if (currentFirstVisPos < myLastVisiblePos) {
                        if (counter == 1) {
                            headerView.animate().translationY(0).setDuration(500);
                            counter = 0;
                        }
                    }
                    myLastVisiblePos = currentFirstVisPos;

                    if (totalItemCount > 0 && canLoadMore) {
                        int lastInScreen = firstVisibleItem + visibleItemCount;
                        if (lastInScreen == totalItemCount && visibleItemCount != 0) {
                            getOldData();
                        }
                    }
                }
            });
        } else {
            emptyView.setVisibility(View.VISIBLE);
            emptyView.findViewById(R.id.btnRetry).setVisibility(View.GONE);
            ((TextView) emptyView.findViewById(R.id.txtMessage)).setText(getString(R.string.no_products));
            mGridView.setVisibility(View.GONE);
            sortSpinner.setEnabled(false);
            findViewById(R.id.sort).setBackgroundResource(R.drawable.disable_back);
            if (!show_filters)
                sortnCount.setVisibility(View.GONE);
        }
        progressBar.setVisibility(View.GONE);
    }

    private void displayError(String error) {
        progressBar.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
        ((TextView) emptyView.findViewById(R.id.txtMessage)).setText(error);
        emptyView.findViewById(R.id.btnRetry).setVisibility(View.VISIBLE);
    }

    public void getOldData() {
        canLoadMore = false;
        oldDataProgress.setVisibility(View.VISIBLE);
        int index = url.indexOf("&page");
        url = url.substring(0, index);
        url += "&page=" + page;

        HelperClass.getData(this, url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    ECNResponse temp = (new Gson()).fromJson(result, ECNResponse.class);
                    if (categoryProducts != null && !categoryProducts.isEmpty()) {
                        List<Product> tempOrders = temp.getProducts();
                        canLoadMore = tempOrders.size() == per_page;
                        if (!tempOrders.isEmpty()) {
                            categoryProducts.addAll(tempOrders);
                        }
                    }
                    page++;
                    oldDataProgress.setVisibility(View.GONE);
                    mAdapter.notifyDataSetChanged();
                    ((TextView) findViewById(R.id.txtProductCount)).setText(String.format(getString(R.string.integer), categoryProducts.size()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(KeyValuePair error) {
                oldDataProgress.setVisibility(View.GONE);
                Toast.makeText(SearchActivity.this, error.getKey(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getFilterData() {
        selected_filters = HelperClass.getSharedString(this, getString(R.string.selected_filters));
        minPrice = HelperClass.getSharedInt(this, "min_price");
        maxPrice = HelperClass.getSharedInt(this, "max_price");
        setInitialURL(sortSpinner.getSelectedItemPosition());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 0  && resultCode == 100) {
            getFilterData();
        }
    }
}
