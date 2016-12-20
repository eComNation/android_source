package com.eComNation.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eComNation.Common.Utility;
import com.eComNation.R;
import com.ecomnationmobile.library.Common.ECNCallback;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Control.ColorSelector;
import com.ecomnationmobile.library.Control.SizeSelector;
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
import java.util.Iterator;
import java.util.List;

/**
 * Created by Chetan on 09/10/2016.
 */
public class KiwiEnglandDetailsActivity extends BaseDetailsActivity {
    HashMap<String, Long> variantsHash;
    long varId;
    List<DropdownClass> product_options;
    JSONObject customAttribute;
    ColorSelector colorSelector;
    SizeSelector sizeSelector;
    String selectedColor, selectedSize;
    String sizeGuideCategories;

    @Override
    public void onResume() {
        super.onResume();

        quantity = 1;
        setQuantity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        HelperClass.putSharedString(KiwiEnglandDetailsActivity.this, "attribute_selection", null);
        HelperClass.putSharedString(KiwiEnglandDetailsActivity.this, "product_option", null);

        sizeGuideCategories = "5807,5806,5800,5797,5808,5799,5809";
        colorSelector = (ColorSelector) findViewById(R.id.colorSelector);
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
        varId = 0;
        if (product_options != null && !product_options.isEmpty()) {
            String selection = "";
            for (DropdownClass drop : product_options) {
                if (drop.getLabel().toLowerCase().equals("size")) {
                    if (selectedSize == null || selectedSize.isEmpty()) {
                        Toast.makeText(KiwiEnglandDetailsActivity.this, "Please select size", Toast.LENGTH_LONG).show();
                        return;
                    }
                    selection += "," + selectedSize.toUpperCase();
                } else if (drop.getLabel().toLowerCase().equals("color")) {
                    if (selectedColor == null || selectedColor.isEmpty()) {
                        Toast.makeText(KiwiEnglandDetailsActivity.this, "Please select color", Toast.LENGTH_LONG).show();
                        return;
                    }
                    selection += "," + selectedColor.toUpperCase();
                }
            }
            getVariantId(selection);
        }

        if (addCount == 0 && (n_left == 0 || n_left >= quantity)) {
            addCount++;
            String content = HelperClass.getSharedString(KiwiEnglandDetailsActivity.this, getString(R.string.cart));
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
            Utility.getCart(KiwiEnglandDetailsActivity.this, cart, new ECNCallback() {
                @Override
                public void onSuccess(String result) {
                    dialog.dismiss();
                    addCount = 0;
                    if (isBuyMode) {
                        startActivity(new Intent(getApplicationContext(), CheckOutActivity.class));
                    } else {
                        Toast.makeText(KiwiEnglandDetailsActivity.this, "Product added to cart.", Toast.LENGTH_LONG).show();
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
            Toast.makeText(KiwiEnglandDetailsActivity.this, "Selected quantity is unavailable", Toast.LENGTH_LONG).show();
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

/*        List<String> tmpString = Arrays.asList(sizeGuideCategories.split(","));
        if (tmpString.contains(Long.toString(product.getCategory_id()))) {
            findViewById(R.id.txtSizeGuide).setVisibility(View.VISIBLE);
            findViewById(R.id.txtSizeGuide).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(KiwiEnglandDetailsActivity.this, KiwiSizeGuideActivity.class);
                    intent.putExtra("categoryID", product.getCategory_id());
                    startActivity(intent);
                }
            });
        } else {
            findViewById(R.id.txtSizeGuide).setVisibility(View.GONE);
        }*/

        setOptions();

        checkVariantQuantity(product.getDefault_variant());

        ((TextView) findViewById(R.id.txtSku)).setText(product.getDefault_variant().getSku());
        if (product.getDescription() != null) {
            ((TextView) findViewById(R.id.txtDescription)).setText(Html.fromHtml(product.getDescription().trim().replace("Â", "")).toString().replace("\n", ""));
            findViewById(R.id.txtDescription).setVisibility(View.VISIBLE);
        }
        super.displayData();
    }

    private void setBuyNowButton(int q) {
        if (q > 0) {
            addCart.setBackgroundResource(R.drawable.button_click);
            addCart.setEnabled(true);
            addCart.setText(getString(R.string.add_cart));
            buyNow.setVisibility(View.VISIBLE);
        } else {
            addCart.setBackgroundResource(R.color.LIGHT_GREY);
            addCart.setEnabled(false);
            addCart.setText(getString(R.string.out_of_stock));
            buyNow.setVisibility(View.GONE);
        }
    }

    private void getSelection() {
        varId = 0;
        if (product_options != null && !product_options.isEmpty()) {
            String selection = "";
            for (DropdownClass drop : product_options) {
                if (drop.getLabel().toLowerCase().equals("size")) {
                    if (selectedSize == null || selectedSize.isEmpty())
                        return;
                    selection += "," + selectedSize.toUpperCase();
                } else if (drop.getLabel().toLowerCase().equals("color")) {
                    if (selectedColor == null || selectedColor.isEmpty())
                        return;
                    selection += "," + selectedColor.toUpperCase();
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
        if (product_options != null && !product_options.isEmpty()) {
            for (DropdownClass drop : product_options) {
                if (drop.getLabel().toLowerCase().equals("size")) {
                    setSizeOption(drop);
                } else if (drop.getLabel().toLowerCase().equals("color")) {
                    setColorOption(drop);
                }
            }
        }
        else{
            sizeSelector.init(null,false,0, null);
            sizeSelector.setVisibility(View.GONE);

            colorSelector.init(null, "", null);
            colorSelector.setVisibility(View.GONE);
        }
    }

    private void setDetailedDescription() {
        List<ProductAttribute> details = new ArrayList<>();
        if (product.getFilter_attributes() != null && !product.getFilter_attributes().isEmpty())
            details = product.getFilter_attributes();

        if (customAttribute != null) {
            Iterator<String> keys = customAttribute.keys();
            while (keys.hasNext()) {
                try {
                    ProductAttribute pa = new ProductAttribute();
                    String keyValue = keys.next();
                    pa.setName(keyValue);
                    String valueString = customAttribute.getString(keyValue);
                    pa.setValues(Arrays.asList(valueString.split(",")));
                    details.add(pa);
                } catch (JSONException je) {
                    je.printStackTrace();
                }
            }
        }
        if (!details.isEmpty()) {
            LinearLayout detailsLayout = (LinearLayout) findViewById(R.id.detailedDescription);
            detailsLayout.removeAllViews();
            for (ProductAttribute drop : details) {
                if (!drop.getValues().isEmpty()) {
                    View view = KiwiEnglandDetailsActivity.this.getLayoutInflater().inflate(R.layout.label_label_item, null);
                    ((TextView) view.findViewById(R.id.txtLabel)).setText(drop.getName().toUpperCase());
                    final String str = TextUtils.join(", ", drop.getValues());
                    ((TextView) view.findViewById(R.id.txtText)).setText(str.toUpperCase());
                    if(drop.getName().toLowerCase().equals("stitch-type") || drop.getName().toLowerCase().equals("stitch type") ){
                        ((TextView) view.findViewById(R.id.txtText)).setTextColor(ContextCompat.getColor(KiwiEnglandDetailsActivity.this, R.color.SecondaryColor));
                        view.findViewById(R.id.txtText).setBackgroundResource(R.drawable.simple_click);
                        view.findViewById(R.id.txtText).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder alert = new AlertDialog.Builder(KiwiEnglandDetailsActivity.this);
                                alert.setTitle(str.toUpperCase());

                                WebView wv = new WebView(KiwiEnglandDetailsActivity.this);
                                wv.loadUrl("file:///android_asset/"+str.toLowerCase()+".html");
                                alert.setView(wv);
                                alert.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                                alert.show();
                            }
                        });
                    }
                    detailsLayout.addView(view);
                }
            }
            findViewById(R.id.layoutDetailedDescription).setVisibility(View.VISIBLE);
        }
    }

    private void setSizeOption(DropdownClass drop) {
        List<String> tmpString = Arrays.asList(sizeGuideCategories.split(","));
        sizeSelector.init(null, false, 0, null);
        sizeSelector.setVisibility(View.GONE);
        List<String> sizes = drop.getValues();
        if (sizes != null && sizes.size() > 0) {
            try {
                DropdownClass ddc = new DropdownClass();
                ddc.setLabel("SIZE");
                ddc.setValues(new ArrayList<String>());
                for (int i = 0; i < sizes.size(); i++) {
                    ddc.getValues().add(sizes.get(i).toUpperCase());
                }
                boolean sizeVisibility = !getString(R.string.size_image).isEmpty() && (!tmpString.isEmpty() && tmpString.contains(Long.toString(product.getCategory_id())));
                sizeSelector.init(ddc, sizeVisibility,R.color.SecondaryColor, new ECNCallback() {
                    @Override
                    public void onSuccess(String result) {
                        selectedSize = result;
                        getSelection();
                    }

                    @Override
                    public void onFailure(KeyValuePair error) {
                        Utility.showSizeDialog(KiwiEnglandDetailsActivity.this, getString(R.string.misc_url) + "misc/" + product.getCategory_id() + ".jpeg");
                    }
                });
                sizeSelector.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setColorOption(DropdownClass drop) {
        colorSelector.init(null, "", null);
        colorSelector.setVisibility(View.GONE);
        List<String> colors =  drop.getValues();
        if (colors != null && colors.size() > 0) {
            try {
                DropdownClass ddc = new DropdownClass();
                ddc.setLabel("COLOR");
                ddc.setValues(new ArrayList<String>());
                for (int i = 0; i < colors.size(); i++) {
                    ddc.getValues().add(colors.get(i));
                }
                colorSelector.init(ddc, getString(R.string.misc_url), new ECNCallback() {
                    @Override
                    public void onSuccess(String result) {
                        selectedColor = result;
                        getSelection();
                    }

                    @Override
                    public void onFailure(KeyValuePair error) {
                    }
                });
                colorSelector.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected int getLayoutId(){
        return R.layout.kiwiengland_detail;
    }

    @Override
    protected void getAddedData(String result) {
        try {
            JSONObject obj = new JSONObject(result);
            JSONObject productObject = (JSONObject) obj.get("product");
            customAttribute = productObject.getJSONObject("custom_attributes");
            if (customAttribute != null) {
                setDetailedDescription();
            }
        } catch (JSONException je) {
            je.printStackTrace();
        }
        progressBar.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
        displayData();
    }
}
