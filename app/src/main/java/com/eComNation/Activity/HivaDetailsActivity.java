package com.eComNation.Activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.eComNation.Common.Utility;
import com.eComNation.R;
import com.ecomnationmobile.library.Common.ECNCallback;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Control.SizeSelector;
import com.ecomnationmobile.library.Data.Cart;
import com.ecomnationmobile.library.Data.DropdownClass;
import com.ecomnationmobile.library.Data.KeyValuePair;
import com.ecomnationmobile.library.Data.OrderLineItem;
import com.ecomnationmobile.library.Data.Variant;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by User on 8/3/2016.
 */
public class HivaDetailsActivity extends BaseDetailsActivity {

    HashMap<String, Long> variantsHash;
    long varId;
    SizeSelector sizeSelector;
    List<DropdownClass> product_options;
    String select_label;

    @Override
    public void onResume() {
        super.onResume();

        quantity = 1;
        setQuantity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sizeSelector = (SizeSelector) findViewById(R.id.sizeSelector);

        buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToCart(true);
            }
        });

        addCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToCart(false);
            }
        });

        getData();
    }

    private void addToCart(final boolean isBuyMode) {
        getVariantId();

        if (addCount == 0 && (n_left == 0 || n_left >= quantity)) {
            addCount++;
            String content = HelperClass.getSharedString(HivaDetailsActivity.this, getString(R.string.cart));
            cart = (new Gson()).fromJson(content, Cart.class);

            if (cart == null) {
                cart = new Cart();
                cart.setItems(new ArrayList<OrderLineItem>());
            }
            Variant v;
            if (varId == 0) {
                v = product.getDefault_variant();
            } else {
                v = getVariant(product.getVariants());
            }

            cart.setUpdate_id(cart.getItems().size() + 10);

            OrderLineItem item = new OrderLineItem();
            item.setVariant_id(v.getId());
            item.setQuantity(quantity);
            cart.getItems().add(item);

            dialog.show();
            Utility.getCart(HivaDetailsActivity.this, cart, new ECNCallback() {
                @Override
                public void onSuccess(String result) {
                    dialog.dismiss();
                    addCount = 0;
                    if(isBuyMode) {
                        startActivity(new Intent(getApplicationContext(), CheckOutActivity.class));
                    }
                    else {
                        Toast.makeText(HivaDetailsActivity.this, "Product added to cart.", Toast.LENGTH_LONG).show();
                        setCartCount();
                    }
                }

                @Override
                public void onFailure(KeyValuePair error) {
                    dialog.dismiss();
                    addCount = 0;
                }
            });
        } else {
            Toast.makeText(HivaDetailsActivity.this, "Selected quantity is unavailable", Toast.LENGTH_LONG).show();
        }
    }

    private Variant getVariant(List<Variant> list) {
        Variant variant = null;
        if (list != null && !list.isEmpty()) {
            for (Variant v : list) {
                if (varId == v.getId()) {
                    variant = v;
                    break;
                }
            }
        }
        return variant;
    }

    private void setQuantity() {
        ((TextView) findViewById(R.id.txtQty)).setText(String.format(getString(R.string.integer), quantity));
    }

    protected void displayData() {
        quantity = 1;
        setQuantity();

        findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity++;
                setQuantity();
            }
        });

        findViewById(R.id.btnSubtract).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quantity > 1) {
                    quantity--;
                    setQuantity();
                }
            }
        });

        setOptions();

        checkInitialQuantity();

        super.displayData();
    }

    private void checkInitialQuantity() {
        Variant v = product.getDefault_variant();
        ((TextView) findViewById(R.id.txtPrice)).setText(HelperClass.formatCurrency(getString(R.string.currency), v.getPrice()));
        addCart.setText(getString(R.string.add_cart));
        buyNow.setText(getString(R.string.buy_now));
        if (product_options != null && !product_options.isEmpty()) {
            select_label = product_options.get(0).getLabel();
            setBuyNowButton(-999);
        } else {
            if (product.isTrack_quantity()) {
                n_left = v.getQuantity() - v.getMinimum_stock_level();
                setBuyNowButton(n_left);
                if (n_left >= 1 && n_left <= 5) {
                    stock.setText(String.format(getString(R.string.only_left), n_left));
                    stock.setVisibility(View.VISIBLE);
                }
            }
            else
                setBuyNowButton(1);
        }
    }

    private void setBuyNowButton(int q) {
        addCart.setVisibility(View.VISIBLE);
        if (q > 0) {
            buyNow.setText(getString(R.string.buy_now));
            buyNow.setBackgroundResource(R.drawable.button_click);
            buyNow.setEnabled(true);

            addCart.setBackgroundResource(R.drawable.button_click);
            addCart.setEnabled(true);
        } else {
            addCart.setVisibility(View.GONE);

            buyNow.setBackgroundResource(R.color.DARK_GRAY);
            buyNow.setEnabled(false);

            if (q != -999) {
                buyNow.setText(getString(R.string.out_of_stock));
            }
            else {
                buyNow.setText("SELECT "+select_label);
            }
        }
    }

    private void getVariantId() {
        varId = 0;
        String selection = "";
        if (sizeSelector.getVisibility() == View.VISIBLE) {
            selection += "," + sizeSelector.getSelection().toUpperCase();
            if (variantsHash.get(selection) != null) {
                varId = variantsHash.get(selection);
                checkVariantQuantity();
            } else {
                setBuyNowButton(-999);
            }
        }
    }

    private void checkVariantQuantity() {
        oldPrice.setVisibility(View.GONE);
        discount.setVisibility(View.GONE);
        stock.setVisibility(View.GONE);

        Variant v = getVariant(product.getVariants());

        if (v != null) {
            ((TextView) findViewById(R.id.txtPrice)).setText(HelperClass.formatCurrency(getString(R.string.currency), v.getPrice()));

            if (product.isTrack_quantity()) {
                n_left = v.getQuantity() - v.getMinimum_stock_level();
                setBuyNowButton(n_left);
                if (n_left >= 1 && n_left <= 5) {
                    stock.setText(String.format(getString(R.string.only_left), n_left));
                    stock.setVisibility(View.VISIBLE);
                }
            }
            else
                setBuyNowButton(1);

            Double diff = v.getPrevious_price() - v.getPrice();
            if (diff > 0) {
                oldPrice.setVisibility(View.VISIBLE);
                oldPrice.setText(HelperClass.formatCurrency(getString(R.string.currency), v.getPrevious_price()));
                oldPrice.setPaintFlags(oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                discount.setText(String.format(getString(R.string.percent_off), HelperClass.getPercentageString(diff, v.getPrevious_price())));
                discount.setVisibility(View.VISIBLE);
            }
            imageControl.selectImage(v.getImage_id());
        } else {
            setBuyNowButton(0);
        }
    }

    private void setOptions() {
        sizeSelector.init(null,false,0, null);
        sizeSelector.setVisibility(View.GONE);
        product_options = HelperClass.generate_dropdown(product);
        variantsHash = HelperClass.generate_VariantsHash(product.getVariants());
        if (product_options != null && !product_options.isEmpty()) {
            for (DropdownClass drop : product_options) {
                if (drop.getLabel().toLowerCase().equals("size")) {
                    setSizeOption(drop);
                }
            }
        }
    }

    private void setSizeOption(DropdownClass drop) {
        List<String> sizes = drop.getValues();
        if (sizes != null && sizes.size() > 0) {
            try {
                DropdownClass ddc = new DropdownClass();
                ddc.setLabel("SIZE");
                ddc.setValues(new ArrayList<String>());
                for (int i = 0; i < sizes.size(); i++) {
                    ddc.getValues().add(sizes.get(i).toUpperCase());
                }
                sizeSelector.init(ddc,!getString(R.string.size_image).isEmpty(),R.color.SecondaryColor, new ECNCallback() {
                    @Override
                    public void onSuccess(String result) {
                        getVariantId();
                    }

                    @Override
                    public void onFailure(KeyValuePair error) {
                        startActivity(new Intent(HivaDetailsActivity.this, SizeGuideActivity.class));
                    }
                });
                sizeSelector.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected int getLayoutId(){
        return R.layout.hiva_detail;
    }

    @Override
    protected void getAddedData(String result) {
        progressBar.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
        displayData();
    }
}
