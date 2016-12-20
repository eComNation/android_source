package com.eComNation.Activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.eComNation.Adapter.TabsCustomPagerAdapter;
import com.eComNation.Common.Utility;
import com.eComNation.Fragment.HTMLFragment;
import com.eComNation.Fragment.ProductAttributesFragment;
import com.eComNation.Fragment.ProductDescriptionFragment;
import com.eComNation.R;
import com.ecomnationmobile.library.Common.ECNCallback;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Control.SlidingTabLayout;
import com.ecomnationmobile.library.Control.WrapContentHeightViewPager;
import com.ecomnationmobile.library.Data.Cart;
import com.ecomnationmobile.library.Data.DropdownClass;
import com.ecomnationmobile.library.Data.KeyValuePair;
import com.ecomnationmobile.library.Data.OrderLineItem;
import com.ecomnationmobile.library.Data.ProductAttribute;
import com.ecomnationmobile.library.Data.Variant;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Chetan on 24-12-2016.
 */
public class SurplusnationDetailsActivity extends BaseDetailsActivity {

    HashMap<String, Long> variantsHash;
    long varId;
    List<DropdownClass> product_options;

    Button btnInquiry;
    String selectedSize;
    JSONObject customAttribute;
    int minOrder;
    double perPiecePrice;

    @Override
    public void onResume() {
        super.onResume();

        quantity = 1;
        setQuantity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        HelperClass.putSharedString(SurplusnationDetailsActivity.this, "attribute_selection", null);
        HelperClass.putSharedString(SurplusnationDetailsActivity.this, "product_option", null);

        buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                varId = 0;
                if (product_options != null && !product_options.isEmpty()) {
                    String selection = "";
                    for (DropdownClass drop : product_options) {
                        if (drop.getLabel().toLowerCase().equals("size") || drop.getLabel().toLowerCase().equals("pack")) {
                            if (selectedSize == null || selectedSize.isEmpty()) {
                                Toast.makeText(SurplusnationDetailsActivity.this, "Please select size", Toast.LENGTH_LONG).show();
                                return;
                            }
                            selection += "," + selectedSize.toUpperCase();
                        }
                    }
                    getVariantId(selection);
                }

                if (addCount == 0 && (n_left == 0 || n_left >= quantity)) {
                    addCount++;
                    String content = HelperClass.getSharedString(SurplusnationDetailsActivity.this, getString(R.string.cart));
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
                    Utility.getCart(SurplusnationDetailsActivity.this, cart, new ECNCallback() {
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
                    Toast.makeText(SurplusnationDetailsActivity.this, "Selected quantity is unavailable", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnInquiry = (Button) findViewById(R.id.btnInquiry);
        btnInquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SurplusInquiryActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("product_name", product.getName());
                bundle.putString("product_sku", product.getDefault_variant().getSku());
                intent.putExtras(bundle);
                startActivity(intent);
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
        setMinOrderDetails();
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

        ((TextView) findViewById(R.id.txtSku)).setText(product.getDefault_variant().getSku());
        setSizeDetails();

        imageControl.init(product.getImages(), true);

        mFragments = new ArrayList<>();
        mTabs = new ArrayList<>();

        if (getResources().getBoolean(R.bool.show_description)) {
            if (product.getDescription() != null && !product.getDescription().equals("")) {
                mFragments.add(ProductDescriptionFragment.class.getName());
                mTabs.add(new KeyValuePair(-1, getString(R.string.description_header)));
            }
        }

        if (getResources().getBoolean(R.bool.show_attributes)) {
            int valueCount = 0;
            List<ProductAttribute> attrList = product.getFilter_attributes();
            if (attrList != null && !attrList.isEmpty()) {
                for (ProductAttribute pa : attrList) {
                    if (!pa.getValues().isEmpty()) {
                        valueCount++;
                        break;
                    }
                }
            }

            if (valueCount > 0) {
                mFragments.add(ProductAttributesFragment.class.getName());
                mTabs.add(new KeyValuePair(0, getString(R.string.attribute_header)));
            }
        }

        if (product.getDetailed_description() != null && !product.getDetailed_description().equals("")) {
            List<String> tab_keys = Arrays.asList(getResources().getStringArray(R.array.tab_keys));
            String[] tab_display = getResources().getStringArray(R.array.tab_display);
            if (!tab_keys.isEmpty()) {
                for (int i = 0; i < tab_keys.size(); i++) {
                    mFragments.add(ProductDescriptionFragment.class.getName());
                    mTabs.add(new KeyValuePair(i, tab_display[i].toUpperCase()));
                }
            }
        }

        List<String> product_detail = Arrays.asList(getResources().getStringArray(R.array.product_detail));
        if (!product_detail.isEmpty()) {
            for (int i = 0; i < product_detail.size(); i++) {
                mFragments.add(HTMLFragment.class.getName());
                mTabs.add(new KeyValuePair(i, product_detail.get(i).toUpperCase()));
            }
        }

        mViewPager = (WrapContentHeightViewPager) findViewById(R.id.pager);
        mTabStrip = (SlidingTabLayout) findViewById(R.id.tabs);
        mViewPager.setOffscreenPageLimit(mTabs.size() - 1);

        mTabStrip.setTextSelector(R.color.colored_selector);
        mAdapter = new TabsCustomPagerAdapter(getSupportFragmentManager(), this, mFragments, mTabs);
        mViewPager.setAdapter(mAdapter);

        mTabStrip.setDistributeEvenly(true);
        mTabStrip.setViewPager(mViewPager);

        mTabStrip.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return ContextCompat.getColor(SurplusnationDetailsActivity.this, R.color.PrimaryColor);
            }
        });

        mTabStrip.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener());
    }

    private void checkInitialQuantity() {
        Variant v = product.getDefault_variant();
        ((TextView) findViewById(R.id.txtPrice)).setText(HelperClass.formatCurrency(getString(R.string.currency), v.getPrice()));
        perPiecePrice = v.getPrice();
        setMinOrderDetails();
        if (product.isTrack_quantity()) {
            if (variantsHash != null && (product_options != null && !product_options.isEmpty()) && (selectedSize != null && !selectedSize.isEmpty())) {
                varId = variantsHash.get("," + selectedSize);
                Variant variant = getVariant(product.getVariants());
                n_left = variant.getQuantity() - variant.getMinimum_stock_level();
                setBuyNowButton(n_left);
                if (n_left >= 1 && n_left <= 5) {
                    stock.setText(String.format(getString(R.string.only_left), n_left));
                    stock.setVisibility(View.GONE);
                }
            }
            else {
                n_left = v.getQuantity() - v.getMinimum_stock_level();
                setBuyNowButton(n_left);
                if (n_left >= 1 && n_left <= 5) {
                    stock.setText(String.format(getString(R.string.only_left), n_left));
                    stock.setVisibility(View.GONE);
                }
            }
            findViewById(R.id.txtAvailableStock).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.txtAvailableStock)).setText(String.format(getString(R.string.available_stock), n_left));
        } else {
            findViewById(R.id.txtAvailableStock).setVisibility(View.GONE);
            setBuyNowButton(1);
        }
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

    private void getVariantId(String selection) {
        if (variantsHash != null) {
            if (variantsHash.get(selection) != null) {
                varId = variantsHash.get(selection);
                checkVariantQuantity();
            } else {
                setOptions();
                Toast.makeText(this, "This variant is unavailable", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    private void checkVariantQuantity() {
        oldPrice.setVisibility(View.GONE);
        discount.setVisibility(View.GONE);
        stock.setVisibility(View.GONE);
        findViewById(R.id.imgSale).setVisibility(View.GONE);

        Variant v = getVariant(product.getVariants());

        if (v != null) {
            if (product.isTrack_quantity()) {
                n_left = v.getQuantity() - v.getMinimum_stock_level();
                setBuyNowButton(n_left);
                if (n_left >= 1 && n_left <= 5) {
                    stock.setText(String.format(getString(R.string.only_left), n_left));
                    stock.setVisibility(View.GONE);
                }
            } else
                setBuyNowButton(1);

            Double diff = v.getPrevious_price() - v.getPrice();
            if (diff > 0) {
                oldPrice.setVisibility(View.VISIBLE);
                oldPrice.setText(HelperClass.formatCurrency(getString(R.string.currency), v.getPrevious_price()));
                oldPrice.setPaintFlags(oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                discount.setText(String.format(getString(R.string.percent_off), HelperClass.getPercentageString(diff, v.getPrevious_price())));
                discount.setVisibility(View.VISIBLE);

                findViewById(R.id.imgSale).setVisibility(View.VISIBLE);
            }
            imageControl.selectImage(v.getImage_id());
        } else {
            setBuyNowButton(0);
        }
    }

    private void setOptions() {
        product_options = HelperClass.generate_dropdown(product);
        variantsHash = HelperClass.generate_VariantsHash(product.getVariants());
        if (product_options != null && !product_options.isEmpty()) {
            for (DropdownClass drop : product_options) {
                if (drop.getLabel().toLowerCase().equals("size") || drop.getLabel().toLowerCase().equals("pack")) {
                    setSizeOption(drop);
                }
            }
        }
    }

    private void setSizeOption(DropdownClass drop) {
        List<String> sizes = drop.getValues();
        if (sizes != null && !sizes.isEmpty()) {
            selectedSize = sizes.get(0);
        }
    }

    private void setMinOrderDetails() {
        ((TextView) findViewById(R.id.txtQuantityInPack)).setText(String.format(getString(R.string.quantity_in_pack), minOrder));
        ((TextView) findViewById(R.id.txtPackPrice)).setText(String.format(getString(R.string.pack_price), HelperClass.formatCurrency(getString(R.string.currency), perPiecePrice * minOrder)));
    }

    private void setBrandName(String brandName) {
        if (brandName != null && !brandName.equals(""))
            ((TextView) findViewById(R.id.txtProductName)).setText(String.format(getString(R.string.product_name).toUpperCase(), product.getName(), brandName));
        else
            ((TextView) findViewById(R.id.txtProductName)).setText(product.getName().toUpperCase());
    }

    private void setSizeDetails() {
        String sizeDetails = Html.fromHtml(product.getDescription()).toString().trim();
        sizeDetails = sizeDetails.replace("\n\n", "\n");
        ((TextView)findViewById(R.id.txtColorSize)).setText(sizeDetails);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.surplusnation_detail;
    }

    @Override
    protected void getAddedData(String result) {
        try {
            JSONObject obj = new JSONObject(result);
            JSONObject productObject = (JSONObject) obj.get("product");
            customAttribute = productObject.getJSONObject("custom_attributes");
            if (customAttribute != null) {
                minOrder = customAttribute.getInt("order");
                setBrandName(customAttribute.getString("brand"));
            } else {
                minOrder = 1;
                ((TextView) findViewById(R.id.txtProductName)).setText(product.getName().toUpperCase());
            }
        } catch (JSONException je) {
            je.printStackTrace();
        }
        progressBar.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
        displayData();
    }
}
