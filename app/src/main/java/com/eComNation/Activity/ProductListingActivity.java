package com.eComNation.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.eComNation.Adapter.ProductListAdapter;
import com.eComNation.Common.Utility;
import com.eComNation.R;
import com.ecomnationmobile.library.Common.ECNCallback;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Control.GridViewWithHeaderAndFooter;
import com.ecomnationmobile.library.Control.ProgressDialogView;
import com.ecomnationmobile.library.Data.Category;
import com.ecomnationmobile.library.Data.ECNResponse;
import com.ecomnationmobile.library.Data.KeyValuePair;
import com.ecomnationmobile.library.Data.Product;
import com.ecomnationmobile.library.Database.DatabaseManager;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Abhi on 02-09-2015.
 */
public class ProductListingActivity extends FragmentActivity {

    ProductListAdapter mAdapter;
    List<Product> categoryProducts;
    String[] sort_criteria;
    Spinner sortSpinner;
    boolean canLoadMore, is_sort_activated, show_filters, is_filter;
    long cat_id;
    ProgressDialog dialog;
    String url, selected_filters;
    ProgressBar oldDataProgress;
    LinearLayout filter, actionBar, headerView, sortnCount, footerView;
    View progressBar, emptyView;
    int counter, page, per_page;
    int myLastVisiblePos, layoutId;
    int minPrice, maxPrice;
    TextView selectedFilterText;
    Category category;
    GridViewWithHeaderAndFooter mGridView;

    @Override
    public void onResume() {
        super.onResume();

        if (category != null) {
            Answers.getInstance().logContentView(new ContentViewEvent()
                    .putContentName("Product Listing: " + category.getName())
                    .putContentType("Category"));
        }
        ImageButton item = (ImageButton) findViewById(R.id.cart);
        LayerDrawable icon = (LayerDrawable) item.getDrawable();

        // Update LayerDrawable's BadgeDrawable
        Utility.setBadgeCount(this, icon, HelperClass.getCartCount(this));
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductListingActivity.this, CheckOutActivity.class);
                intent.putExtras(new Bundle());
                startActivity(intent);
            }
        });

        HelperClass.putSharedString(this, getString(R.string.product), null);

        if(mAdapter != null && categoryProducts != null && !categoryProducts.isEmpty()) {
            mAdapter.initFavouriteList();
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.product_listing);

        selectedFilterText = (TextView) findViewById(R.id.txtSelectedFilter);

        dialog = new ProgressDialogView(this, "Updating...", R.drawable.progress);

        counter = 0;
        per_page = 16;
        canLoadMore = false;
        is_sort_activated = false;

        (findViewById(R.id.btnBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mGridView = (GridViewWithHeaderAndFooter) findViewById(R.id.productGrid);

        final ImageButton tglButton = (ImageButton) findViewById(R.id.tglLayoutView);
        if (getResources().getBoolean(R.bool.has_display_option)) {
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
        } else {
            tglButton.setVisibility(View.GONE);
        }

        float height = 130 * HelperClass.getDisplayMetrics(this).density;
        View headerPadding = new View(ProductListingActivity.this);
        headerPadding.setBackgroundResource(R.color.LIGHT_BACK);
        headerPadding.setMinimumHeight((int) height);
        mGridView.addHeaderView(headerPadding);

        sort_criteria = getResources().getStringArray(R.array.sort_criteria);

        List<String> sortList = Arrays.asList(getResources().getStringArray(R.array.sort_display));

        filter = (LinearLayout) findViewById(R.id.filterSort);
        actionBar = (LinearLayout) findViewById(R.id.actionBar);
        headerView = (LinearLayout) findViewById(R.id.headerView);
        footerView = (LinearLayout) findViewById(R.id.footerView);
        sortnCount = (LinearLayout) findViewById(R.id.sortnCount);
        progressBar = findViewById(R.id.progressBar);
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
                Intent intentMain = new Intent(ProductListingActivity.this, FilterActivity.class);
                intentMain.putExtra("is_listing", true);
                startActivityForResult(intentMain, 0);
            }
        });

        findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMain = new Intent(ProductListingActivity.this, SearchActivity.class);
                startActivityForResult(intentMain, 1);
            }
        });

        findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HelperClass.putSharedInt(ProductListingActivity.this, "min_price", -1);
                HelperClass.putSharedInt(ProductListingActivity.this, "max_price", -1);
                HelperClass.putSharedString(ProductListingActivity.this, getString(R.string.selected_filters), null);
                HelperClass.putSharedString(ProductListingActivity.this, "display_filters", null);
                getFilterData();
            }
        });

        sortSpinner = (Spinner) findViewById(R.id.sortSpinner);

        sortSpinner.setBackgroundColor(ContextCompat.getColor(ProductListingActivity.this, R.color.TRANSPARENT));
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

        is_filter = getIntent().getBooleanExtra("is_filter",false);
        if(is_filter) {
            setTitle("");
        }
        else {
            cat_id = HelperClass.getSharedLong(this, "category");

            DatabaseManager.init(this);
            category = DatabaseManager.getInstance().getCategory(cat_id);
            if (category != null) {
                setTitle(category.getName());
            }
        }
        getInitialData();
    }

    private void getInitialData() {
        HelperClass.putSharedString(ProductListingActivity.this, getString(R.string.selected_filters), null);
        HelperClass.putSharedString(ProductListingActivity.this, "listing_facets", null);
        HelperClass.putSharedInt(ProductListingActivity.this, "min_price", -1);
        HelperClass.putSharedInt(ProductListingActivity.this, "max_price", -1);

        if(is_filter)
            setFilterURL(0);
        else
            setInitialURL(0);

        String facets_url = url.replace("facets=false", "facets=true");

        HelperClass.getData(this, facets_url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                HelperClass.putSharedString(ProductListingActivity.this, "listing_facets", result);
            }

            @Override
            public void onFailure(KeyValuePair error) {
            }
        });
    }

    private void setFilterURL(int position) {
        page = 1;
        String selected = "";
        url = getString(R.string.production_base_url) + getString(R.string.api) + "store/products?";
        String filterString = HelperClass.getSharedString(ProductListingActivity.this, "filterString");
        if(filterString != null) {
            url += filterString;
            footerView.setVisibility(View.GONE);
        }
        else {
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
        }
        getData();
    }

    private void setInitialURL(int position) {
        page = 1;
        String selected = "";
        url = getString(R.string.production_base_url) + getString(R.string.api) + "store/products?active_category=" + cat_id;
        if(HelperClass.getSharedBoolean(ProductListingActivity.this, "show_out_of_stock")){
            url += "&variants=1";
        }
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
                HelperClass.putSharedString(ProductListingActivity.this, "filterString", null);
                dialog.dismiss();
            }

            @Override
            public void onFailure(KeyValuePair error) {
                dialog.dismiss();
                displayError(error.getKey());
            }
        });
    }

    private void displayError(String error) {
        progressBar.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
        sortnCount.setVisibility(View.GONE);
        ((TextView) emptyView.findViewById(R.id.txtMessage)).setText(error);
        emptyView.findViewById(R.id.btnRetry).setVisibility(View.VISIBLE);
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
                    Intent intentMain = Utility.getDetailsIntent(ProductListingActivity.this);
                    Bundle bundle = new Bundle();
                    bundle.putLong("id", categoryProducts.get(position).getId());
                    intentMain.putExtras(bundle);
                    startActivity(intentMain);
                }
            });

            myLastVisiblePos = mGridView.getFirstVisiblePosition();
            mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
                final Picasso picasso = Picasso.with(ProductListingActivity.this);
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    if (scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                        picasso.resumeTag(ProductListingActivity.this);
                    } else {
                        picasso.pauseTag(ProductListingActivity.this);
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    int currentFirstVisPos = view.getFirstVisiblePosition();
                    if (currentFirstVisPos > myLastVisiblePos) {
                        if (counter == 0) {
                            headerView.animate().translationY(-headerView.getHeight()).setDuration(500);
                            footerView.animate().translationY(footerView.getHeight()).setDuration(500);
                            counter = 1;
                        }
                    }
                    if (currentFirstVisPos < myLastVisiblePos) {
                        if (counter == 1) {
                            headerView.animate().translationY(0).setDuration(500);
                            footerView.animate().translationY(0).setDuration(500);
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
            ((TextView) findViewById(R.id.txtProductCount)).setText("0");
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

    private void setUpGridView() {
        mAdapter = new ProductListAdapter(ProductListingActivity.this, layoutId, categoryProducts, false);
        mGridView.setAdapter(mAdapter);
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(KeyValuePair error) {
                oldDataProgress.setVisibility(View.GONE);
                Toast.makeText(ProductListingActivity.this, error.getKey(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getFilterData() {
        selected_filters = HelperClass.getSharedString(this, getString(R.string.selected_filters));
        minPrice = HelperClass.getSharedInt(this, "min_price");
        maxPrice = HelperClass.getSharedInt(this, "max_price");
        if(is_filter)
            setFilterURL(sortSpinner.getSelectedItemPosition());
        else
            setInitialURL(sortSpinner.getSelectedItemPosition());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 0 && resultCode == 100) {
            getFilterData();
        }
    }
}
