package com.ecomnationmobile.library.Control;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.ecomnationmobile.library.Common.CustomLayoutInflater;
import com.ecomnationmobile.library.Common.ECNCallback;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Data.ECNResponse;
import com.ecomnationmobile.library.Data.KeyValuePair;
import com.ecomnationmobile.library.Data.Product;
import com.ecomnationmobile.library.R;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Chetan on 02/09/2016.
 */
public class ProductListControl extends LinearLayout {

    View mView;
    LinearLayout layoutRelated, layoutSimilar, layoutRecent;
    HorizontalScrollView relatedScroll, similarScroll, recentScroll;
    Product product;
    List<Product> recentProducts, relatedProducts, similarProducts;
    LinkedList<String> recentProductList;
    ECNCallback mCallback;
    Context mContext;

    public ProductListControl(Context context) {
        this(context, null);
    }

    public ProductListControl(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProductListControl(final Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mContext = context;

        setOrientation(VERTICAL);

        mView = LayoutInflater.from(context).inflate(R.layout.product_list_control, null);

        layoutRelated = (LinearLayout) mView.findViewById(R.id.gridRelated);
        layoutSimilar = (LinearLayout) mView.findViewById(R.id.gridSimilar);
        layoutRecent = (LinearLayout) mView.findViewById(R.id.gridRecent);

        relatedScroll = (HorizontalScrollView) mView.findViewById(R.id.relatedScroll);
        similarScroll = (HorizontalScrollView) mView.findViewById(R.id.similarScroll);
        recentScroll = (HorizontalScrollView) mView.findViewById(R.id.recentScroll);
        addView(mView);
    }

    public void init(Product productData, String production_base_url, int currency, int small_item_height, int sale_label, ECNCallback callback) {
        product = productData;
        mCallback = callback;

        if (product != null) {
            float height = mContext.getResources().getDimension(small_item_height);
            relatedScroll.setMinimumHeight((int) height);
            similarScroll.setMinimumHeight((int) height);
            recentScroll.setMinimumHeight((int) height);
            showRelatedProducts(currency, small_item_height, sale_label);
            showSimilarProducts(production_base_url, currency, small_item_height, sale_label);
            showRecentProducts(String.valueOf(product.getId()), production_base_url, currency, small_item_height, sale_label);
        } else {
            mView.findViewById(R.id.relatedBlock).setVisibility(View.GONE);
            mView.findViewById(R.id.similarBlock).setVisibility(View.GONE);
            mView.findViewById(R.id.recentBlock).setVisibility(View.GONE);
        }
    }

    public void showRelatedProducts(int currency, int small_item_height, final int sale_label) {
        relatedProducts = product.getRelated_products();
        layoutRelated.removeAllViews();
        if (!relatedProducts.isEmpty()) {
            mView.findViewById(R.id.relatedBlock).setVisibility(View.VISIBLE);
            int width = HelperClass.getDisplayMetrics(mContext).widthPixels;

            width /= 2.5;
            for (int i = 0; i < relatedProducts.size(); i++) {
                View view = CustomLayoutInflater.getProductView(mContext, relatedProducts.get(i), width, currency, small_item_height, sale_label);
                view.setTag(i);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = (int) v.getTag();
                        Product product = relatedProducts.get(position);
                        mCallback.onSuccess(String.valueOf(product.getId()));
                    }
                });
                layoutRelated.addView(view);
            }
            relatedScroll.scrollTo(0, 0);
        } else {
            mView.findViewById(R.id.relatedBlock).setVisibility(View.GONE);
        }
    }

    public void showSimilarProducts(String production_base_url, final int currency, final int small_item_height, final int sale_label) {
        mView.findViewById(R.id.similarBlock).setVisibility(View.GONE);
        String url = production_base_url + mContext.getString(R.string.api) + "store/products/" + product.getId() + "/similar_products";
        HelperClass.getData(mContext, url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                ECNResponse response = (new Gson()).fromJson(result, ECNResponse.class);
                similarProducts = response.getProducts();
                layoutSimilar.removeAllViews();

                int width = HelperClass.getDisplayMetrics(mContext).widthPixels;

                width /= 2.5;

                if (similarProducts != null && !similarProducts.isEmpty()) {
                    mView.findViewById(R.id.similarBlock).setVisibility(View.VISIBLE);
                    for (int i = 0; i < similarProducts.size(); i++) {
                        View view = CustomLayoutInflater.getProductView(mContext, similarProducts.get(i), width, currency, small_item_height, sale_label);
                        view.setTag(i);
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int position = (int) v.getTag();
                                mCallback.onSuccess(String.valueOf(similarProducts.get(position).getId()));
                            }
                        });
                        layoutSimilar.addView(view);
                    }
                    similarScroll.scrollTo(0, 0);
                }
            }

            @Override
            public void onFailure(KeyValuePair error) {
            }
        });
    }

    public void showRecentProducts(final String ID, String production_base_url, final int currency, final int small_item_height, final int sale_label) {
        String recent_products = HelperClass.getSharedString(mContext, "recent_products");

        mView.findViewById(R.id.recentBlock).setVisibility(View.GONE);
        if (recent_products != null) {
            recentProductList = new LinkedList<>(Arrays.asList(recent_products.split(",")));
            recentProductList.remove(ID);
            if (recentProductList.size() > 0) {
                String url = production_base_url + mContext.getString(R.string.api) + "store/products?";
                url += "page=1&per_page=10&facets=false&ids=" + TextUtils.join(",", recentProductList);
                HelperClass.getData(mContext, url, new ECNCallback() {
                    @Override
                    public void onSuccess(String result) {

                        recentProductList.add(ID);
                        if (recentProductList.size() > 5)
                            recentProductList.removeFirst();
                        HelperClass.putSharedString(mContext, "recent_products", TextUtils.join(",", recentProductList));

                        ECNResponse response = (new Gson()).fromJson(result, ECNResponse.class);
                        recentProducts = response.getProducts();
                        if(recentProducts != null && !recentProducts.isEmpty()) {
                            mView.findViewById(R.id.recentBlock).setVisibility(View.VISIBLE);
                            layoutRecent.removeAllViews();

                            int width = HelperClass.getDisplayMetrics(mContext).widthPixels;
                            width /= 2.5;

                            for (int i = 0; i < recentProducts.size(); i++) {
                                View view = CustomLayoutInflater.getProductView(mContext, recentProducts.get(i), width, currency, small_item_height, sale_label);
                                view.setTag(i);
                                view.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        int position = (int) v.getTag();
                                        Product product = recentProducts.get(position);
                                        mCallback.onSuccess(String.valueOf(product.getId()));
                                    }
                                });
                                layoutRecent.addView(view);
                            }
                            recentScroll.scrollTo(0, 0);
                        }
                    }

                    @Override
                    public void onFailure(KeyValuePair error) {
                    }
                });
            }
        } else {
            recentProductList = new LinkedList<>();
            recentProductList.add(ID);
            HelperClass.putSharedString(mContext, "recent_products", TextUtils.join(",", recentProductList));
        }
    }
}
