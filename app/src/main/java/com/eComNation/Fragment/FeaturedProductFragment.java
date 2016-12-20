package com.eComNation.Fragment;

import android.content.Intent;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.eComNation.Activity.CheckOutActivity;
import com.eComNation.Activity.MainActivity;
import com.eComNation.Activity.SearchActivity;
import com.eComNation.Adapter.ProductListAdapter;
import com.eComNation.Common.Utility;
import com.eComNation.R;
import com.ecomnationmobile.library.Common.ECNCallback;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Control.GridViewWithHeaderAndFooter;
import com.ecomnationmobile.library.Data.ECNResponse;
import com.ecomnationmobile.library.Data.KeyValuePair;
import com.ecomnationmobile.library.Data.Product;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by Abhi on 02-05-2016.
 */
public class FeaturedProductFragment extends Fragment {
    ProductListAdapter mAdapter;
    List<Product> categoryProducts;
    boolean canLoadMore;
    String url;
    ProgressBar oldDataProgress;
    View progressBar, emptyView, mView;
    int counter, page, per_page;
    int myLastVisiblePos;
    GridViewWithHeaderAndFooter mGridView;

    public FeaturedProductFragment()
    {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.featured_products, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mView = getView();

        if (mView != null && isAdded())
        {
            counter = 0;
            per_page = 8;
            canLoadMore = false;

            mGridView = (GridViewWithHeaderAndFooter) mView.findViewById(R.id.productGrid);

            progressBar = mView.findViewById(R.id.progressBar);
            emptyView = mView.findViewById(R.id.emptyView);
            emptyView.findViewById(R.id.btnRetry).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getData();
                }
            });
            oldDataProgress = (ProgressBar) mView.findViewById(R.id.oldDataProgress);
            oldDataProgress.setVisibility(View.GONE);

            page = 1;
            url = getString(R.string.production_base_url) + getString(R.string.api)+"store/products/featured?";
            url += "per_page=" + per_page + "&page=" + page;

            setData(HelperClass.getSharedString(getActivity(), "featured_products"));
        }
    }

    private void getData() {
        HelperClass.getData(getActivity(), url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                setData(result);
            }

            @Override
            public void onFailure(KeyValuePair error) {
                displayError(error.getKey());
            }
        });
    }

    private void setData(String result) {
        if (result != null && !result.isEmpty()) {
            ECNResponse response = (new Gson()).fromJson(result, ECNResponse.class);
            categoryProducts = response.getProducts();
            page++;
            canLoadMore = categoryProducts.size() == per_page;
            displayData();
        } else
            displayError(getString(R.string.something_went_wrong));
    }

    private void displayError(String error) {
        progressBar.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
        ((TextView)emptyView.findViewById(R.id.txtMessage)).setText(error);
        emptyView.findViewById(R.id.btnRetry).setVisibility(View.VISIBLE);
    }

    private void displayData() {
        if (categoryProducts != null && !categoryProducts.isEmpty()) {
            emptyView.setVisibility(View.GONE);
            mGridView.setVisibility(View.VISIBLE);

            ((TextView) mView.findViewById(R.id.txtProductCount)).setText(String.format(getString(R.string.integer), categoryProducts.size()));

            mAdapter = new ProductListAdapter(getActivity(), R.layout.product_preview, categoryProducts, false);
            mGridView.setAdapter(mAdapter);

            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    Intent intentMain = Utility.getDetailsIntent(getActivity());
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
                            ((MainActivity) getActivity()).animateActionBar(true);
                            counter = 1;
                        }
                    }
                    if (currentFirstVisPos < myLastVisiblePos) {
                        if (counter == 1) {
                            ((MainActivity) getActivity()).animateActionBar(false);
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
            ((TextView) mView.findViewById(R.id.txtProductCount)).setText("0");
            emptyView.setVisibility(View.VISIBLE);
            emptyView.findViewById(R.id.btnRetry).setVisibility(View.GONE);
            ((TextView) emptyView.findViewById(R.id.txtMessage)).setText(getString(R.string.no_products));
            mGridView.setVisibility(View.GONE);
        }
        progressBar.setVisibility(View.GONE);
    }

    public void getOldData() {
        canLoadMore = false;
        oldDataProgress.setVisibility(View.VISIBLE);
        int index = url.indexOf("&page");
        url = url.substring(0, index);
        url += "&page=" + page;

        HelperClass.getData(getActivity(), url, new ECNCallback() {
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
                    ((TextView) mView.findViewById(R.id.txtProductCount)).setText(String.format(getString(R.string.integer), categoryProducts.size()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(KeyValuePair error) {
                oldDataProgress.setVisibility(View.GONE);
                Toast.makeText(getActivity(), error.getKey(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

