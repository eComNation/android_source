package com.eComNation.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.eComNation.Adapter.ProductListAdapter;
import com.eComNation.Common.Utility;
import com.eComNation.R;
import com.ecomnationmobile.library.Common.ECNCallback;
import com.ecomnationmobile.library.Common.EComNationActivity;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Data.ECNResponse;
import com.ecomnationmobile.library.Data.KeyValuePair;
import com.ecomnationmobile.library.Data.Product;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by User on 6/1/2016.
 */
public class MyFavouritesActivity extends EComNationActivity {

    List<Product> products;
    ListView productList;
    View progressBar, emptyView, oldDataProgress;
    boolean canLoadMore;
    int per_page, page;
    ProductListAdapter adapter;
    ArrayList<String> favouriteProductList;
    String favourite_products;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_favourites);
        HelperClass.initialize(this);

        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        progressBar = findViewById(R.id.progressBar);
        productList = (ListView) findViewById(R.id.list);
        oldDataProgress = findViewById(R.id.oldDataProgress);
        oldDataProgress.setVisibility(View.GONE);

        emptyView = findViewById(R.id.empty_View);
        emptyView.findViewById(R.id.btnRetry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getProducts();
            }
        });

        per_page = 15;

        getProducts();

        productList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (totalItemCount > 0 && canLoadMore) {
                    int lastInScreen = firstVisibleItem + visibleItemCount;
                    if (lastInScreen == totalItemCount && visibleItemCount != 0) {
                        getOldData();
                    }
                }
            }
        });
    }

    private void getProducts() {
        progressBar.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        String url = getString(R.string.production_base_url) + getString(R.string.api) + "store/customer/favourite_products?access_token=" + HelperClass.getSharedString(this, getString(R.string.access_token));

        HelperClass.getData(this, url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                ECNResponse response = (new Gson()).fromJson(result, ECNResponse.class);
                List<Product> tempProducts = response.getProducts();
                canLoadMore = tempProducts.size() == per_page;
                products = tempProducts;
                page = response.getPagination_info().getNext_page();
                progressBar.setVisibility(View.GONE);
                displayData();
                initFavouriteList();
            }

            @Override
            public void onFailure(KeyValuePair error) {
                if (error.getValue() == 401) {
                    error.setKey(getString(R.string.logged_out_error));
                    Utility.getAccessToken(MyFavouritesActivity.this);
                }
                displayError(error.getKey());
            }
        });
    }

    private void displayError(String error) {
        emptyView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        ((TextView) emptyView.findViewById(R.id.txtMessage)).setText(error);
        emptyView.findViewById(R.id.btnRetry).setVisibility(View.VISIBLE);
    }

    private void displayData() {
        if (products != null && !products.isEmpty()) {
            productList.setItemsCanFocus(true);
            adapter = new ProductListAdapter(this, R.layout.favourites_list_item, products, true);

            productList.setAdapter(adapter);

            productList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                    Intent intentMain = Utility.getDetailsIntent(MyFavouritesActivity.this);
                    Bundle bundle = new Bundle();
                    bundle.putLong("id", products.get(position).getId());
                    intentMain.putExtras(bundle);
                    startActivity(intentMain);
                }
            });
            emptyView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.VISIBLE);
            ((TextView) emptyView.findViewById(R.id.txtMessage)).setText(getString(R.string.no_products));
            emptyView.findViewById(R.id.btnRetry).setVisibility(View.GONE);
        }
    }

    public void getOldData() {
        canLoadMore = false;
        oldDataProgress.setVisibility(View.VISIBLE);

        String url = getString(R.string.production_base_url) + getString(R.string.api) + "store/customer/favourite_products?access_token=" + HelperClass.getSharedString(this, getString(R.string.access_token));
        url += "&page=" + page;

        HelperClass.getData(this, url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    ECNResponse response = (new Gson()).fromJson(result, ECNResponse.class);
                    if (products != null && !products.isEmpty()) {
                        List<Product> tempProducts = response.getProducts();
                        canLoadMore = tempProducts.size() == per_page;
                        if (!tempProducts.isEmpty()) {
                            products.addAll(tempProducts);
                            initFavouriteList();
                        }
                    }
                    page++;
                    oldDataProgress.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(KeyValuePair error) {
                oldDataProgress.setVisibility(View.GONE);
                Toast.makeText(MyFavouritesActivity.this, error.getKey(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initFavouriteList() {
        favourite_products = HelperClass.getSharedString(MyFavouritesActivity.this, "favourite_products");
        if (favourite_products != null) {
            favouriteProductList = new ArrayList<>(Arrays.asList(favourite_products.split(",")));

            if (!favouriteProductList.isEmpty()) {
                for (Product p : products) {
                    if (!favouriteProductList.contains(Long.toString(p.getId())))
                        favouriteProductList.add(Long.toString(p.getId()));
                }
                HelperClass.putSharedString(MyFavouritesActivity.this, "favourite_products", TextUtils.join(",", favouriteProductList));
                return;
            }
        }

        favouriteProductList = new ArrayList<>();
        for (Product p : products) {
            favouriteProductList.add(Long.toString(p.getId()));
        }
        HelperClass.putSharedString(MyFavouritesActivity.this, "favourite_products", TextUtils.join(",", favouriteProductList));
    }
}
