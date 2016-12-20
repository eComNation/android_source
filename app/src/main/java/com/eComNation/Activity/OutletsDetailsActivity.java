package com.eComNation.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eComNation.Common.Utility;
import com.eComNation.R;
import com.ecomnationmobile.library.Common.ECNCallback;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Control.AttributeSpinner;
import com.ecomnationmobile.library.Control.SizeSelector;
import com.ecomnationmobile.library.Data.Cart;
import com.ecomnationmobile.library.Data.DropdownClass;
import com.ecomnationmobile.library.Data.KeyValuePair;
import com.ecomnationmobile.library.Data.OrderLineItem;
import com.ecomnationmobile.library.Data.Variant;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by User on 10/13/2016.
 */
public class OutletsDetailsActivity extends BaseDetailsActivity {

    HashMap<String, Long> variantsHash;
    long varId;
    JSONObject brandInfo;
    SizeSelector weightSelector;
    List<DropdownClass> product_options;

    @Override
    public void onResume() {
        super.onResume();

        quantity = 1;
        setQuantity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        weightSelector = (SizeSelector) findViewById(R.id.weightSelector);

        buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getVariantId();

                if (addCount == 0 && (n_left == 0 || n_left >= quantity)) {
                    addCount++;
                    String content = HelperClass.getSharedString(OutletsDetailsActivity.this, getString(R.string.cart));
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
                    Utility.getCart(OutletsDetailsActivity.this, cart, new ECNCallback() {
                        @Override
                        public void onSuccess(String result) {
                            dialog.dismiss();
                            addCount = 0;
                                startActivity(new Intent(getApplicationContext(), CheckOutActivity.class));
                        }

                        @Override
                        public void onFailure(KeyValuePair error) {
                            dialog.dismiss();
                            addCount = 0;
                        }
                    });

                } else {
                    Toast.makeText(OutletsDetailsActivity.this, "Selected quantity is unavailable", Toast.LENGTH_LONG).show();
                }
            }
        });

        getData();
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

        super.displayData();
    }

    private void setBuyNowButton(int q) {
        if (q > 0) {
            buyNow.setBackgroundResource(R.drawable.button_click);
            buyNow.setText(getString(R.string.add_cart));
            buyNow.setEnabled(true);
        } else {
            buyNow.setBackgroundResource(R.color.LIGHT_GREY);
            buyNow.setEnabled(false);
            buyNow.setText(getString(R.string.out_of_stock));
        }
    }

    private void getVariantId() {
        varId = 0;
        String selection = "";
        if (weightSelector.getVisibility() == View.VISIBLE) {
            selection += "," + weightSelector.getSelection().toUpperCase();
            if (variantsHash.get(selection) != null) {
                varId = variantsHash.get(selection);
                checkVariantQuantity(getVariant(product.getVariants()));
            } else {
                setBuyNowButton(0);
            }
        }
    }

    private void checkVariantQuantity(Variant v) {
        oldPrice.setVisibility(View.GONE);
        discount.setVisibility(View.GONE);
        stock.setVisibility(View.GONE);

        if (v != null) {
            ((TextView) findViewById(R.id.txtPrice)).setText(HelperClass.formatCurrency(getString(R.string.currency), v.getPrice()));

            if (product.isTrack_quantity()) {
                n_left = v.getQuantity() - v.getMinimum_stock_level();
                setBuyNowButton(n_left);
                if (n_left >= 1 && n_left <= 5) {
                    stock.setText(String.format(getString(R.string.only_left), n_left));
                    stock.setVisibility(View.VISIBLE);
                }
            } else
                setBuyNowButton(1);

            Double diff = v.getPrevious_price() - v.getPrice();
            if (diff > 0) {
                oldPrice.setVisibility(View.VISIBLE);
                oldPrice.setText(HelperClass.formatCurrency(getString(R.string.currency), v.getPrevious_price()));

                discount.setText(String.format(getString(R.string.percent_off), HelperClass.getPercentageString(diff, v.getPrevious_price())));
                discount.setVisibility(View.VISIBLE);
            }
            imageControl.selectImage(v.getImage_id());
        } else {
            setBuyNowButton(0);
        }
    }

    private void setOptions() {
        weightSelector.init(null,false,0, null);
        weightSelector.setVisibility(View.GONE);
        product_options = HelperClass.generate_dropdown(product);
        variantsHash = HelperClass.generate_VariantsHash(product.getVariants());
        if (product_options != null && !product_options.isEmpty()) {
            for (DropdownClass drop : product_options) {
                    setWeightOption(drop);
            }
        }
        else
            checkVariantQuantity(product.getDefault_variant());
    }

    private void setWeightOption(DropdownClass drop) {
        List<String> weights = drop.getValues();
        if (weights != null && weights.size() > 0) {
            try {
                DropdownClass ddc = new DropdownClass();
                ddc.setLabel(drop.getLabel().toUpperCase());
                ddc.setValues(new ArrayList<String>());
                for (int i = 0; i < weights.size(); i++) {
                    ddc.getValues().add(weights.get(i).toUpperCase());
                }
                weightSelector.init(ddc,false,R.color.SecondaryColor, new ECNCallback() {
                    @Override
                    public void onSuccess(String result) {
                        getVariantId();
                    }

                    @Override
                    public void onFailure(KeyValuePair error) {
                        startActivity(new Intent(OutletsDetailsActivity.this, SizeGuideActivity.class));
                    }
                });
                weightSelector.setVisibility(View.VISIBLE);
                weightSelector.setSelection(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected int getLayoutId(){
        return R.layout.outlets_detail;
    }

    @Override
    protected void getAddedData(String result) {
        try {
            JSONObject obj = new JSONObject(result);
            JSONObject productObject = (JSONObject) obj.get("product");
            brandInfo = productObject.getJSONObject("custom_attributes");

            if (brandInfo != null && brandInfo.names().length() > 0) {
                String name = brandInfo.getString("brand_name");
                if(name != null) {
                    byte[] data = name.getBytes("ASCII");
                    name = new String(data);
                    name = name.replace("?", "");
                    ((TextView) findViewById(R.id.brandName)).setText(name);
                }

                if (!brandInfo.isNull("brand_logo")) {
                    String url = HelperClass.processURL(brandInfo.getString("brand_logo"));
                    ImageView image = (ImageView) findViewById(R.id.brandLogo);
                    HelperClass.setPicassoBitMap(url, image, new ECNCallback() {
                        @Override
                        public void onSuccess(String result) {
                        }

                        @Override
                        public void onFailure(KeyValuePair error) {
                        }
                    });
                }
                findViewById(R.id.brandLayout).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.brandLayout).setVisibility(View.GONE);
            }
        }
        catch (Exception je) {
            findViewById(R.id.brandLayout).setVisibility(View.GONE);
            je.printStackTrace();
        }
        progressBar.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
        displayData();
    }
}
