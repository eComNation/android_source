package com.eComNation.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eComNation.Common.Utility;
import com.eComNation.R;
import com.ecomnationmobile.library.Common.ECNCallback;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Data.KeyValuePair;
import com.ecomnationmobile.library.Data.Product;
import com.ecomnationmobile.library.Data.Variant;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Abhi on 03-09-2015.
 */
public class ProductListAdapter extends ArrayAdapter<Product> {

    Context mContext;
    int layoutResourceId;
    List<Product> data = null;
    boolean flag;
    ArrayList<String> favouriteProductList;
    String favourite_products;

    public ProductListAdapter(Context mContext, int layoutResourceId, List<Product> data, boolean flag) {

        super(mContext, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
        this.flag = flag;

        if (flag || (HelperClass.isUserLoggedIn(mContext) && mContext.getResources().getBoolean(R.bool.show_favourite_listing)))
            initFavouriteList();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        final View listItem = inflater.inflate(layoutResourceId, parent, false);

        if(mContext.getResources().getBoolean(R.bool.show_loader_icon)) {
            listItem.findViewById(R.id.image_progress).setVisibility(View.GONE);
            listItem.findViewById(R.id.imageLoaderIcon).setVisibility(View.VISIBLE);
        }

        final Product product = data.get(position);

        TextView oldPrice = (TextView) listItem.findViewById(R.id.txtOldPrice);
        Double diff = product.getPrevious_price() - product.getPrice();
        if (diff > 0) {
            oldPrice.setVisibility(View.VISIBLE);
            oldPrice.setText(HelperClass.formatCurrency(mContext.getString(R.string.currency), product.getPrevious_price()));
            oldPrice.setPaintFlags(oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            listItem.findViewById(R.id.imgSale).setVisibility(View.VISIBLE);
            if (HelperClass.getSharedBoolean(mContext, "show_percent_off")) {
                String percent = HelperClass.getPercentageString(diff, product.getPrevious_price());
                ((TextView) listItem.findViewById(R.id.imgSale)).setText(percent + " OFF");
            }
        }
        ((TextView) listItem.findViewById(R.id.txtPrice)).setText(HelperClass.formatCurrency(mContext.getString(R.string.currency), product.getPrice()));

        ((TextView) listItem.findViewById(R.id.txtProductName)).setText(product.getName());
        ImageView image = (ImageView) listItem.findViewById(R.id.imgProduct);

        if (HelperClass.getSharedBoolean(mContext, "show_out_of_stock") && checkQuantity(product) <= 0)
            listItem.findViewById(R.id.outOfStock).setVisibility(View.VISIBLE);

        String url = product.getProduct_image_url();

        if (flag) {
            listItem.findViewById(R.id.txtCategory).setVisibility(View.VISIBLE);
            ((TextView) listItem.findViewById(R.id.txtCategory)).setText(product.getCategory());
            final TextView removeB = (TextView) listItem.findViewById(R.id.remove);
            removeB.setVisibility(View.VISIBLE);
            removeB.setTag(position);
            removeB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utility.removeFavourites(mContext, product.getId(), new ECNCallback() {
                        @Override
                        public void onSuccess(String result) {
                            int pos = (int) removeB.getTag();
                            data.remove(pos);
                            ProductListAdapter.this.notifyDataSetChanged();
                            favouriteProductList.remove(Long.toString(product.getId()));
                            HelperClass.putSharedString(mContext, "favourite_products", TextUtils.join(",", favouriteProductList));
                        }

                        @Override
                        public void onFailure(KeyValuePair error) {
                        }
                    });
                }
            });
            url = product.getImage_url();
        } else {
            if (mContext.getResources().getBoolean(R.bool.show_favourite_listing)) {
                final ImageView addToFavourites = (ImageView) listItem.findViewById(R.id.imgFavourite);

                if (favouriteProductList != null && !favouriteProductList.isEmpty()) {
                    if (favouriteProductList.contains(Long.toString(product.getId())))
                        addToFavourites.setImageResource(R.drawable.colored_heart);
                }

                addToFavourites.setVisibility(View.VISIBLE);
                addToFavourites.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (favouriteProductList != null && !favouriteProductList.isEmpty() && favouriteProductList.contains(Long.toString(product.getId()))) {
                            Utility.removeFavourites(mContext, product.getId(), new ECNCallback() {
                                @Override
                                public void onSuccess(String result) {
                                    addToFavourites.setImageResource(R.drawable.grey_heart);
                                    favouriteProductList.remove(Long.toString(product.getId()));
                                    HelperClass.putSharedString(mContext, "favourite_products", TextUtils.join(",", favouriteProductList));
                                }

                                @Override
                                public void onFailure(KeyValuePair error) {
                                }
                            });
                        } else {
                            Utility.addToFavourites(mContext, product.getId(), new ECNCallback() {
                                @Override
                                public void onSuccess(String result) {
                                    addToFavourites.setImageResource(R.drawable.colored_heart);
                                    favouriteProductList.add(Long.toString(product.getId()));
                                    HelperClass.putSharedString(mContext, "favourite_products", TextUtils.join(",", favouriteProductList));
                                }

                                @Override
                                public void onFailure(KeyValuePair error) {
                                }
                            });
                        }
                    }
                });
            }
        }
        if (url != null) {
            Picasso.with(mContext).load(HelperClass.processURL(url)).tag("Pause Resume").error(R.drawable.placeholder).into(image, new Callback() {
                @Override
                public void onSuccess() {
                    listItem.findViewById(R.id.image_progress).setVisibility(View.GONE);
                    listItem.findViewById(R.id.imageLoaderIcon).setVisibility(View.GONE);
                }

                @Override
                public void onError() {
                    listItem.findViewById(R.id.image_progress).setVisibility(View.GONE);
                    listItem.findViewById(R.id.imageLoaderIcon).setVisibility(View.GONE);
                }
            });
        } else {
            image.setImageResource(R.drawable.placeholder);
            listItem.findViewById(R.id.image_progress).setVisibility(View.GONE);
            listItem.findViewById(R.id.imageLoaderIcon).setVisibility(View.GONE);
        }
        return listItem;
    }

    public void initFavouriteList() {
        favourite_products = HelperClass.getSharedString(mContext, "favourite_products");
        if (favourite_products != null) {
            favouriteProductList = new ArrayList<>(Arrays.asList(favourite_products.split(",")));
        } else {
            favouriteProductList = new ArrayList<>();
        }
    }

    private int checkQuantity(Product p) {
        if (p.isTrack_quantity()) {
            List<Variant> variants = p.getVariants();
            if (variants != null && !variants.isEmpty()) {
                int totalQuantity = 0;
                int totalMinStockLevel = 0;
                for (Variant v : variants) {
                    if (v != null) {
                        totalQuantity += v.getQuantity();
                        totalMinStockLevel += v.getMinimum_stock_level();
                    }
                }
                return totalQuantity - totalMinStockLevel;
            } else {
                return p.getQuantity() - p.getMinimum_stock_level();
            }
        }
        return 1;
    }
}
