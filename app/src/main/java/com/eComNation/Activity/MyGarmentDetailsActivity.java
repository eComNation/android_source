package com.eComNation.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eComNation.Common.Utility;
import com.eComNation.R;
import com.ecomnationmobile.library.Common.ECNCallback;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Control.AttributeSpinner;
import com.ecomnationmobile.library.Control.CustomEditText;
import com.ecomnationmobile.library.Data.Cart;
import com.ecomnationmobile.library.Data.CustomDetail;
import com.ecomnationmobile.library.Data.DropdownClass;
import com.ecomnationmobile.library.Data.KeyValuePair;
import com.ecomnationmobile.library.Data.OrderLineItem;
import com.ecomnationmobile.library.Data.Variant;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Chetan on 11/16/16.
 */
public class MyGarmentDetailsActivity extends BaseDetailsActivity {

    HashMap<String, Long> variantsHash;
    long varId;
    LinearLayout attributeLayout;
    List<DropdownClass> product_options;
    List<AttributeSpinner> attributes;
    CustomEditText name;
    List<String> specialProducts;

    @Override
    public void onResume() {
        super.onResume();

        quantity = 1;
        setQuantity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        specialProducts = new ArrayList<>();
        specialProducts.add("40236");
        attributeLayout = (LinearLayout) findViewById(R.id.attributes);

        HelperClass.putSharedString(MyGarmentDetailsActivity.this, "attribute_selection", null);
        HelperClass.putSharedString(MyGarmentDetailsActivity.this, "product_option", null);

        buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                varId = 0;
                if (attributes != null && !attributes.isEmpty()) {
                    String selection = "";
                    for (AttributeSpinner attr : attributes) {
                        if (attr.getSelectedPosition() < attr.getCount()) {
                            selection += "," + attr.getSelection();
                        } else {
                            Toast.makeText(MyGarmentDetailsActivity.this, "Please select " + attr.getLabel().toLowerCase(), Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                    getVariantId(selection);
                }

                if (addCount == 0 && (n_left == 0 || n_left >= quantity)) {
                    addCount++;
                    String content = HelperClass.getSharedString(MyGarmentDetailsActivity.this, getString(R.string.cart));
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

                    if(!specialProducts.isEmpty()){
                        if (specialProducts.contains(Long.toString(product.getId()))) {
                            List<CustomDetail> customDetails = new ArrayList<>();
                            CustomDetail fv = new CustomDetail();
                            fv.setKey("name");
                            fv.setValue(name.getText());
                            customDetails.add(fv);

                            item.setCustom_details(customDetails);
                        }
                    }

                    cart.getItems().add(item);

                    dialog.show();
                    Utility.getCart(MyGarmentDetailsActivity.this, cart, new ECNCallback() {
                        @Override
                        public void onSuccess(String result) {
                            dialog.dismiss();
                            addCount = 0;
                            switch (getString(R.string.scheme)) {
                                case Utility.STORYATHOME:
                                    Toast.makeText(MyGarmentDetailsActivity.this, "Product added to cart.", Toast.LENGTH_LONG).show();
                                    setCartCount();
                                    break;
                                default:
                                    startActivity(new Intent(getApplicationContext(), CheckOutActivity.class));
                                    break;
                            }
                        }

                        @Override
                        public void onFailure(KeyValuePair error) {
                            dialog.dismiss();
                            addCount = 0;
                        }
                    });

                } else {
                    Toast.makeText(MyGarmentDetailsActivity.this, "Selected quantity is unavailable", Toast.LENGTH_LONG).show();
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

        displayEnterTextFeature();

        setOptions();

        checkVariantQuantity(product.getDefault_variant());

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

    private void getSelection() {
        varId = 0;
        if (attributes != null && !attributes.isEmpty()) {
            String selection = "";
            for (AttributeSpinner attr : attributes) {
                if (attr.getSelectedPosition() < attr.getCount()) {
                    selection += "," + attr.getSelection();
                } else {
                    return;
                }
            }
            getVariantId(selection);
        }
    }

    private void getVariantId(String selection) {
        if (variantsHash != null) {
            if (variantsHash.get(selection) != null) {
                varId = variantsHash.get(selection);
                checkVariantQuantity(getVariant(product.getVariants()));
            } else {
                setOptions();
                Toast.makeText(this, "This variant is unavailable", Toast.LENGTH_SHORT).show();
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
        product_options = HelperClass.generate_dropdown(product);
        variantsHash = HelperClass.generate_VariantsHash(product.getVariants());
        attributes = new ArrayList<>();
        attributeLayout.removeAllViews();
        if (product_options != null && !product_options.isEmpty()) {
            for (DropdownClass drop : product_options) {
                final AttributeSpinner spinner = new AttributeSpinner(this);
                spinner.init(drop);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position < spinner.getCount())
                            getSelection();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
                attributes.add(spinner);
                attributeLayout.addView(spinner);
            }
        }
    }

    private void displayEnterTextFeature(){
        name = (CustomEditText)findViewById(R.id.name);
        if(!specialProducts.isEmpty()){
            if (specialProducts.contains(Long.toString(product.getId()))) {
                name.init(getString(R.string.enter_name_text));
                name.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                name.setVisibility(View.VISIBLE);
            }
            else{
                name.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected int getLayoutId(){
        return R.layout.mygarments_detail;
    }

    @Override
    protected void getAddedData(String result) {
        progressBar.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
        displayData();
    }
}