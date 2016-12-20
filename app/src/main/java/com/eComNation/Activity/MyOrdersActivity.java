package com.eComNation.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.eComNation.Adapter.MyOrdersAdapter;
import com.eComNation.Common.Utility;
import com.eComNation.R;
import com.ecomnationmobile.library.Common.ECNCallback;
import com.ecomnationmobile.library.Common.EComNationActivity;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Data.ECNResponse;
import com.ecomnationmobile.library.Data.KeyValuePair;
import com.ecomnationmobile.library.Data.Order;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by Abhi on 14-12-2015.
 */
public class MyOrdersActivity extends EComNationActivity {

    List<Order> orders;
    ListView orderList;
    View progressBar, emptyView, oldDataProgress;
    boolean canLoadMore;
    int per_page, page;
    MyOrdersAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_orders);

        HelperClass.initialize(this);

        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        progressBar = findViewById(R.id.progressBar);
        orderList = (ListView) findViewById(R.id.list);
        oldDataProgress = findViewById(R.id.oldDataProgress);
        oldDataProgress.setVisibility(View.GONE);

        emptyView = findViewById(R.id.empty_View);
        emptyView.findViewById(R.id.btnRetry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {getOrders();
            }
        });

        per_page = 15;
        
        getOrders();

        orderList.setOnScrollListener(new AbsListView.OnScrollListener() {
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

    private void getOrders() {
        progressBar.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        String url = getString(R.string.production_base_url) + getString(R.string.api) + "store/customer/orders?access_token=" + HelperClass.getSharedString(this, getString(R.string.access_token));

        HelperClass.getData(this, url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                ECNResponse response = (new Gson()).fromJson(result, ECNResponse.class);
                List<Order> tempOrders = response.getOrders();
                canLoadMore = tempOrders.size() == per_page;
                orders = tempOrders;
                page = response.getPagination_info().getNext_page();
                progressBar.setVisibility(View.GONE);
                displayData();
            }

            @Override
            public void onFailure(KeyValuePair error) {
                if (error.getValue() == 401) {
                    error.setKey(getString(R.string.logged_out_error));
                    Utility.getAccessToken(MyOrdersActivity.this);
                }
                displayError(error.getKey());
            }
        });
    }

    private void displayError(String error) {
        emptyView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        ((TextView)emptyView.findViewById(R.id.txtMessage)).setText(error);
        emptyView.findViewById(R.id.btnRetry).setVisibility(View.VISIBLE);
    }

    private void displayData() {
        if (orders != null && !orders.isEmpty()) {
            adapter = new MyOrdersAdapter(this, R.layout.my_order_item, orders);

            orderList.setAdapter(adapter);

            orderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                    Intent intentMain = new Intent(getApplicationContext(), OrderDetailsActivity.class);
                    Order order = orders.get(position);
                    Bundle bundle = new Bundle();
                    bundle.putLong("id", order.getId());
                    intentMain.putExtras(bundle);
                    startActivity(intentMain);
                }
            });
            emptyView.setVisibility(View.GONE);
        }
        else {
            emptyView.setVisibility(View.VISIBLE);
            ((TextView) emptyView.findViewById(R.id.txtMessage)).setText(getString(R.string.no_orders));
            emptyView.findViewById(R.id.btnRetry).setVisibility(View.GONE);
        }
    }

    public void getOldData() {
        canLoadMore = false;
        oldDataProgress.setVisibility(View.VISIBLE);

        String url = getString(R.string.production_base_url) + getString(R.string.api) + "store/customer/orders?access_token=" + HelperClass.getSharedString(this, getString(R.string.access_token));
        url += "&page=" + page;

        HelperClass.getData(this, url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    ECNResponse response = (new Gson()).fromJson(result, ECNResponse.class);
                    if (orders != null && !orders.isEmpty()) {
                        List<Order> tempOrders = response.getOrders();
                        canLoadMore = tempOrders.size() == per_page;
                        if (!tempOrders.isEmpty()) {
                            orders.addAll(tempOrders);
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
                Toast.makeText(MyOrdersActivity.this, error.getKey(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
