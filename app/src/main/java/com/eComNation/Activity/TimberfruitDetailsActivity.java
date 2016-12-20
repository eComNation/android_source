package com.eComNation.Activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
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
import com.ecomnationmobile.library.Control.ColorSelector;
import com.ecomnationmobile.library.Control.QuantitySelector;
import com.ecomnationmobile.library.Data.Cart;
import com.ecomnationmobile.library.Data.CustomDetail;
import com.ecomnationmobile.library.Data.DropdownClass;
import com.ecomnationmobile.library.Data.KeyValuePair;
import com.ecomnationmobile.library.Data.OrderLineItem;
import com.ecomnationmobile.library.Data.ProductImage;
import com.ecomnationmobile.library.Data.Variant;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by User on 7/18/2016.
 */
public class TimberfruitDetailsActivity extends BaseDetailsActivity {

    HashMap<String, Long> variantsHash;
    Double price;
    JSONObject colors;
    ColorSelector colorSelector;
    long varId;
    LinearLayout attributeLayout;
    List<DropdownClass> product_options;
    List<QuantitySelector> attributes;
    AttributeSpinner quantitySpinner;
    private List<OrderLineItem> lineItems;

    @Override
    public void onResume() {
        super.onResume();

        quantitySpinner.setSelection(0);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        attributeLayout = (LinearLayout) findViewById(R.id.attributes);
        colorSelector = (ColorSelector) findViewById(R.id.colorSelector);
        quantitySpinner = (AttributeSpinner) findViewById(R.id.quantitySpinner);

        HelperClass.putSharedString(TimberfruitDetailsActivity.this, "attribute_selection", null);
        HelperClass.putSharedString(TimberfruitDetailsActivity.this, "product_option", null);

        buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (colorSelector.getVisibility() == View.VISIBLE && colorSelector.getSelection().equals("")) {
                    Toast.makeText(TimberfruitDetailsActivity.this, "Please select color", Toast.LENGTH_LONG).show();
                    return;
                }
                if (price == 0) {
                    Toast.makeText(TimberfruitDetailsActivity.this, "Please select quantity of at least one option", Toast.LENGTH_LONG).show();
                    return;
                }

                if (addCount == 0 && (n_left == 0 || n_left >= quantity)) {
                    addCount++;
                    String content = HelperClass.getSharedString(TimberfruitDetailsActivity.this, getString(R.string.cart));
                    cart = (new Gson()).fromJson(content, Cart.class);

                    if (cart == null) {
                        cart = new Cart();
                        cart.setItems(new ArrayList<OrderLineItem>());
                    }
                    cart.setUpdate_id(cart.getItems().size() + 10);
                    String[] custom_array = getResources().getStringArray(R.array.custom_detail);
                    for (OrderLineItem oli : lineItems) {
                        if (!colorSelector.getSelection().equals("")) {
                            if (custom_array.length > 0) {
                                List<CustomDetail> customDetails = new ArrayList<>();
                                for (String str : custom_array) {
                                    CustomDetail fv = new CustomDetail();
                                    fv.setKey(str);
                                    fv.setValue(colorSelector.getSelection());
                                    customDetails.add(fv);
                                }
                                oli.setCustom_details(customDetails);
                            }
                        }
                        cart.getItems().add(oli);
                    }

                    dialog.show();
                    Utility.getCart(TimberfruitDetailsActivity.this, cart, new ECNCallback() {
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
                    Toast.makeText(TimberfruitDetailsActivity.this, "Selected quantity is unavailable", Toast.LENGTH_LONG).show();
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

    protected void displayData() {
        quantity = 1;

        setOptions();

        super.displayData();
    }

    private void getVariantId(String selection) {
        varId = 0;
        if (variantsHash != null) {
            if (variantsHash.get(selection) != null)
                varId = variantsHash.get(selection);
            else {
                setOptions();
                Toast.makeText(this, "This variant is unavailable", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void selectImage(String url) {
        List<ProductImage> imageList = new ArrayList<>();
        ProductImage image = new ProductImage();
        image.setUrl(url);
        imageList.add(image);
        imageControl.init(imageList,false);
    }

    private void setOptions() {
        setColorOption();

        oldPrice.setVisibility(View.GONE);
        discount.setVisibility(View.GONE);

        product_options = HelperClass.getTimberFruitVariants(product);
        if (product_options != null && !product_options.isEmpty()) {
            quantitySpinner.setVisibility(View.GONE);
            findViewById(R.id.attributesBlock).setVisibility(View.VISIBLE);
            variantsHash = HelperClass.generate_VariantsHash(product.getVariants());
            attributes = new ArrayList<>();
            attributeLayout.removeAllViews();
            for (DropdownClass drop : product_options) {
                final QuantitySelector qs = new QuantitySelector(this);
                qs.init(drop);
                qs.setQuantitySelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            getPrice();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
                qs.setSizeSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            getPrice();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
                attributes.add(qs);
                attributeLayout.addView(qs);
            }
        } else {
            findViewById(R.id.attributesBlock).setVisibility(View.GONE);
            quantitySpinner.setVisibility(View.VISIBLE);
            List<String> qList = new ArrayList<>();
            for (int i = 1; i <= 6; i++)
                qList.add("" + i);
            DropdownClass ddc = new DropdownClass();
            ddc.setLabel(getString(R.string.quantity));
            ddc.setValues(qList);
            quantitySpinner.init(ddc);
            quantitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    getDefaultPrice(position+1);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }
    }

    private void getPrice() {
        price = 0.0;
        lineItems = new ArrayList<>();

        for (QuantitySelector qs : attributes) {
            int qty = Integer.parseInt(qs.getQuantitySelection());
            if(qty > 0) {
                getVariantId("," + qs.getLabel() + "," + qs.getSizeSelection().toUpperCase());
                Variant v = getVariant(product.getVariants());
                if (v != null) {
                    price += v.getPrice() * qty;
                    OrderLineItem item = new OrderLineItem();
                    item.setVariant_id(v.getId());
                    item.setQuantity(qty);
                    lineItems.add(item);
                }
            }
        }
        buyNow.setText(getString(R.string.add_cart));
        ((TextView) findViewById(R.id.txtPrice)).setText(HelperClass.formatCurrency(getString(R.string.currency),price));
    }

    private void getDefaultPrice(int n) {
        lineItems = new ArrayList<>();
        Variant v = product.getDefault_variant();
        if (v != null) {
            price = v.getPrice();
            Double diff = v.getPrevious_price() - price;
            if (diff > 0) {
                oldPrice.setVisibility(View.VISIBLE);
                oldPrice.setText(HelperClass.formatCurrency(getString(R.string.currency),v.getPrevious_price()));
                oldPrice.setPaintFlags(oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                discount.setText(String.format(getString(R.string.percent_off), HelperClass.getPercentageString(diff, v.getPrevious_price())));
                discount.setVisibility(View.VISIBLE);
            }

            OrderLineItem item = new OrderLineItem();
            item.setVariant_id(v.getId());
            item.setQuantity(n);
            lineItems.add(item);
            buyNow.setText(getString(R.string.add_cart));
            ((TextView) findViewById(R.id.txtPrice)).setText(HelperClass.formatCurrency(getString(R.string.currency),price));
        }
    }

    private void setColorOption() {
        colorSelector.init(null, "", null);
        colorSelector.setVisibility(View.GONE);
        if (colors != null) {
            if (colors.names() != null && colors.names().length() > 0) {
                try {
                    JSONArray array = colors.names();
                    DropdownClass ddc = new DropdownClass();
                    ddc.setLabel("AVAILABLE IN FOLLOWING COLORS");
                    ddc.setValues(new ArrayList<String>());
                    for (int i = 0; i < array.length(); i++) {
                        ddc.getValues().add(colors.getString(array.getString(i)));
                    }
                    colorSelector.init(ddc, getString(R.string.misc_url), new ECNCallback() {
                        @Override
                        public void onSuccess(String result) {
                            String url = getString(R.string.misc_url) + "products/files/" + product.getId();
                            url += "/" + result + ".jpg";
                            selectImage(url);
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
    }

    @Override
    protected int getLayoutId(){
        return R.layout.timberfruit_detail;
    }

    @Override
    protected void getAddedData(String result) {
        try {
            JSONObject obj = new JSONObject(result);
            JSONObject productObject = (JSONObject) obj.get("product");
            colors = productObject.getJSONObject("custom_attributes");
        }
        catch (JSONException je) {
            je.printStackTrace();
        }
        progressBar.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
        displayData();
    }
}
