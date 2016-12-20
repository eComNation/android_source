package com.eComNation.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.eComNation.Common.Utility;
import com.eComNation.R;
import com.ecomnationmobile.library.Common.ECNCallback;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Control.ColorSelector;
import com.ecomnationmobile.library.Control.ReviewControl;
import com.ecomnationmobile.library.Control.SizeSelector;
import com.ecomnationmobile.library.Control.VendorSelector;
import com.ecomnationmobile.library.Data.Cart;
import com.ecomnationmobile.library.Data.DropdownClass;
import com.ecomnationmobile.library.Data.ECNResponse;
import com.ecomnationmobile.library.Data.ExtendedOption;
import com.ecomnationmobile.library.Data.ExtendedVariant;
import com.ecomnationmobile.library.Data.KeyValuePair;
import com.ecomnationmobile.library.Data.Option;
import com.ecomnationmobile.library.Data.OrderLineItem;
import com.ecomnationmobile.library.Data.ReviewData;
import com.ecomnationmobile.library.Data.Variant;
import com.ecomnationmobile.library.Data.Vendor;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by User on 8/12/2016.
 */
public class AddwikDetailsActivity extends BaseDetailsActivity {

    HashMap<String, String> variantsHash;
    VendorSelector vendorSelector;
    ColorSelector colorSelector;
    SizeSelector sizeSelector;
    ReviewControl reviewControl;
    String varId, vendorId;
    List<Vendor> vendorList;

    @Override
    public void onResume() {
        super.onResume();
        quantity = 1;
        setQuantity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        vendorSelector = (VendorSelector) findViewById(R.id.vendorBlock);
        colorSelector = (ColorSelector) findViewById(R.id.colorSelector);
        sizeSelector = (SizeSelector) findViewById(R.id.sizeSelector);
        reviewControl = (ReviewControl) findViewById(R.id.reviewControl);

        buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToCart(vendorId, true);
            }
        });

        addCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToCart(vendorId, false);
            }
        });

        vendorSelector.init(getString(R.string.currency), R.color.SecondaryColor, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                addToCart(vendorSelector.getVendorId(), false);
            }

            @Override
            public void onFailure(KeyValuePair error) {
            }
        });

        getData();
    }

    private void addToCart(String vendorId, final boolean isBuyMode) {
        if (addCount == 0 && (n_left == 0 || n_left >= quantity)) {
            addCount++;
            String content = HelperClass.getSharedString(AddwikDetailsActivity.this, getString(R.string.cart));
            cart = (new Gson()).fromJson(content, Cart.class);

            if (cart == null) {
                cart = new Cart();
                cart.setItems(new ArrayList<OrderLineItem>());
            }

            OrderLineItem item = new OrderLineItem();
            item.setQuantity(quantity);
            cart.getItems().add(item);

            String cart_parameters = "&vendor_id=" + vendorId;

            if (varId != null && !varId.equals(""))
                cart_parameters += "&variant_id=" + varId;

            cart_parameters += "&product_id=" + product.getId();
            HelperClass.putSharedString(AddwikDetailsActivity.this, "cart_parameters", cart_parameters);

            dialog.show();
            Utility.getCart(AddwikDetailsActivity.this, cart, new ECNCallback() {
                @Override
                public void onSuccess(String result) {
                    dialog.dismiss();
                    addCount = 0;
                    if (isBuyMode) {
                        startActivity(new Intent(getApplicationContext(), CheckOutActivity.class));
                    } else {
                        Toast.makeText(AddwikDetailsActivity.this, "Product added to cart.", Toast.LENGTH_LONG).show();
                        setCartCount();
                    }
                }

                @Override
                public void onFailure(KeyValuePair error) {
                    dialog.dismiss();
                    Toast.makeText(AddwikDetailsActivity.this, error.getKey(), Toast.LENGTH_LONG).show();
                    addCount = 0;
                }
            });
        } else {
            Toast.makeText(AddwikDetailsActivity.this, "Selected quantity is unavailable", Toast.LENGTH_LONG).show();
        }
    }

    private ExtendedVariant getVariant(List<ExtendedVariant> list) {
        if (list != null && !list.isEmpty()) {
            for (ExtendedVariant v : list) {
                if (varId.equals(v.getId())) {
                    return v;
                }
            }
        }
        return null;
    }

    private void getVendorData() {
        String url = getString(R.string.market_place) + getString(R.string.api) + "store/products/" + product.getId() + "?store_id=" + getString(R.string.store_id);

        HelperClass.getData(this, url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    ECNResponse response = (new Gson()).fromJson(result, ECNResponse.class);

                    vendorList = response.getVendors();
                    sizeSelector.setVisibility(View.GONE);
                    colorSelector.setVisibility(View.GONE);

                    if (vendorList.isEmpty()) {
                        setUpVendors(response.getVendors());
                    } else {
                        variantsHash = HelperClass.generate_ExtendedVariantsHash(response.getVariants());

                        if (!variantsHash.isEmpty()) {
                            ExtendedOption op = HelperClass.getExOptionByName(response.getOptions(), "size");
                            if (op != null) {
                                DropdownClass ddc = new DropdownClass();
                                ddc.setLabel("SIZE");
                                ddc.setValues(new ArrayList<String>());
                                for (Option opt : op.getValues()) {
                                    ddc.getValues().add(opt.getName());
                                }
                                sizeSelector.setVisibility(View.VISIBLE);
                                sizeSelector.init(ddc, !getString(R.string.size_image).isEmpty(), R.color.SecondaryColor, new ECNCallback() {
                                    @Override
                                    public void onSuccess(String result) {
                                        getSelection();
                                    }

                                    @Override
                                    public void onFailure(KeyValuePair error) {
                                        startActivity(new Intent(AddwikDetailsActivity.this, SizeGuideActivity.class));
                                    }
                                });
                                sizeSelector.setSelection(0);
                            }

                            op = HelperClass.getExOptionByName(response.getOptions(), "color");
                            if (op != null) {
                                DropdownClass ddc = new DropdownClass();
                                ddc.setLabel("COLOR");
                                ddc.setValues(new ArrayList<String>());
                                for (Option opt : op.getValues()) {
                                    ddc.getValues().add(opt.getName());
                                }
                                colorSelector.setVisibility(View.VISIBLE);
                                colorSelector.init(ddc, getString(R.string.misc_url), new ECNCallback() {
                                    @Override
                                    public void onSuccess(String result) {
                                        getSelection();
                                    }

                                    @Override
                                    public void onFailure(KeyValuePair error) {
                                    }
                                });
                                colorSelector.setSelection(0);
                            }
                        } else {
                            setUpVendors(response.getVendors());
                        }
                    }

                } catch (Exception e) {
                    Toast.makeText(AddwikDetailsActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                } finally {
                    progressBar.setVisibility(View.GONE);
                    errorView.setVisibility(View.GONE);
                }
                displayData();
            }

            @Override
            public void onFailure(KeyValuePair error) {
                displayError(error.getKey());
            }
        });
    }

    private void setQuantity() {
        ((TextView) findViewById(R.id.txtQty)).setText(String.format(getString(R.string.integer), quantity));
    }

    @Override
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

        ((TextView) findViewById(R.id.skuValue)).setText(product.getDefault_variant().getSku());

        super.displayData();
    }

    private void checkQuantity() {
        Variant v = product.getDefault_variant();
        if (product.isTrack_quantity()) {
            n_left = v.getQuantity() - v.getMinimum_stock_level();
            setBuyNowButton(n_left);
            if (n_left >= 1 && n_left <= 5) {
                stock.setText(String.format(getString(R.string.only_left), n_left));
                stock.setVisibility(View.VISIBLE);
            }
        } else
            setBuyNowButton(1);
    }

    private void setBuyNowButton(int q) {
        if (q > 0) {
            buyNow.setVisibility(View.VISIBLE);
            addCart.setVisibility(View.VISIBLE);
        } else {
            buyNow.setVisibility(View.GONE);
            addCart.setVisibility(View.GONE);
        }
    }

    private void getSelection() {
        String selection = "";
        if (sizeSelector.getVisibility() == View.VISIBLE)
            selection += sizeSelector.getSelection().toUpperCase();

        if (colorSelector.getVisibility() == View.VISIBLE) {
            if (!selection.equals(""))
                selection += ",";
            selection += colorSelector.getSelection().toUpperCase();
        }

        if (!selection.equals("")) {
            varId = HelperClass.getExVariantId(variantsHash, selection);
            setUpVendors(HelperClass.getVariantVendors(vendorList, varId));
        }
    }

    private void setUpVendors(List<Vendor> newList) {
        oldPrice.setVisibility(View.GONE);
        discount.setVisibility(View.GONE);
        stock.setVisibility(View.GONE);

        vendorSelector.setVisibility(View.GONE);
        if (newList != null && !newList.isEmpty()) {
            vendorId = newList.get(0).getId();
            ExtendedVariant var = getVariant(newList.get(0).getVariants());
            ((TextView) findViewById(R.id.sellerName)).setText(newList.get(0).getName());
            //((TextView) findViewById(R.id.txtShipPrice)).setText("+" + HelperClass.formatCurrency(getString(R.string.currency), newList.get(0).getShipping_charge()) + " Shipping");
            if (var != null) {
                ((TextView) findViewById(R.id.txtPrice)).setText(HelperClass.formatCurrency(getString(R.string.currency), var.getPrice()));
                Double diff = var.getPrevious_price() - var.getPrice();
                if (diff > 0) {
                    oldPrice.setVisibility(View.VISIBLE);
                    oldPrice.setText(HelperClass.formatCurrency(getString(R.string.currency), var.getPrevious_price()));

                    discount.setText(String.format(getString(R.string.percent_off), HelperClass.getPercentageString(diff, var.getPrevious_price())));
                    discount.setVisibility(View.VISIBLE);
                }
                checkVariantQuantity(var);
            } else {
                Variant v = product.getDefault_variant();
                if (v != null) {
                    ((TextView) findViewById(R.id.txtPrice)).setText(HelperClass.formatCurrency(getString(R.string.currency), v.getPrice()));
                    Double diff = v.getPrevious_price() - v.getPrice();
                    if (diff > 0) {
                        oldPrice.setVisibility(View.VISIBLE);
                        oldPrice.setText(HelperClass.formatCurrency(getString(R.string.currency), v.getPrevious_price()));

                        discount.setText(String.format(getString(R.string.percent_off), HelperClass.getPercentageString(diff, v.getPrevious_price())));
                        discount.setVisibility(View.VISIBLE);
                    }
                    checkQuantity();
                }
            }
            newList.remove(0);
            if (!newList.isEmpty())
                vendorSelector.setVisibility(View.VISIBLE);
            findViewById(R.id.priceBlock).setVisibility(View.VISIBLE);
            findViewById(R.id.soldByBlock).setVisibility(View.VISIBLE);
            findViewById(R.id.noSellers).setVisibility(View.GONE);
        } else {
            findViewById(R.id.soldByBlock).setVisibility(View.GONE);
            findViewById(R.id.priceBlock).setVisibility(View.GONE);
            findViewById(R.id.noSellers).setVisibility(View.VISIBLE);
            setBuyNowButton(0);
        }

        vendorSelector.update(newList, varId);
    }

    private void checkVariantQuantity(ExtendedVariant v) {
        if (product.isTrack_quantity()) {
            n_left = v.getQuantity();
            setBuyNowButton(n_left);
            if (n_left >= 1 && n_left <= 5) {
                stock.setText(String.format(getString(R.string.only_left), n_left));
                stock.setVisibility(View.VISIBLE);
            }
        } else
            setBuyNowButton(1);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.addwik_detail;
    }

    @Override
    protected void getAddedData(String result) {
        getVendorData();
    }
}
