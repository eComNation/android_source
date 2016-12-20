package com.eComNation.Activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.eComNation.Common.Utility;
import com.eComNation.R;
import com.ecomnationmobile.library.Common.ECNCallback;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Data.Cart;
import com.ecomnationmobile.library.Data.ExtendedOption;
import com.ecomnationmobile.library.Data.JewelleryProduct;
import com.ecomnationmobile.library.Data.KeyValuePair;
import com.ecomnationmobile.library.Data.Option;
import com.ecomnationmobile.library.Data.OrderLineItem;
import com.ecomnationmobile.library.Data.Price;
import com.ecomnationmobile.library.Data.ProductImage;
import com.ecomnationmobile.library.Data.Variant;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Abhi on 21-06-2016.
 */
public class ABJewelsDetailActivity extends BaseDetailsActivity {

    JewelleryProduct jewelleryProduct;
    Price price;
    String metal_option, metal_size, diamond_options;
    List<ProductImage> imageList;
    Spinner sizeSpinner;
    Option metalOption;

    @Override
    public void onResume() {
        super.onResume();
        setQuantity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        HelperClass.putSharedString(this, "price_parameters", null);

        sizeSpinner = (Spinner) findViewById(R.id.sizeSpinner);

        HelperClass.putSharedString(ABJewelsDetailActivity.this, "attribute_selection", null);
        HelperClass.putSharedString(ABJewelsDetailActivity.this, "product_option", null);

        buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addCount == 0) {
                    addCount++;
                    String content = HelperClass.getSharedString(ABJewelsDetailActivity.this, getString(R.string.cart));
                    cart = (new Gson()).fromJson(content, Cart.class);

                    if (cart == null) {
                        cart = new Cart();
                        cart.setItems(new ArrayList<OrderLineItem>());
                    }
                    cart.setUpdate_id(cart.getItems().size() + 10);

                    OrderLineItem item = new OrderLineItem();
                    item.setQuantity(quantity);
                    cart.getItems().add(item);
                    String cart_parameters = metal_option + diamond_options + metal_size + "&product_id=" + product.getId();
                    HelperClass.putSharedString(ABJewelsDetailActivity.this, "cart_parameters", cart_parameters);

                    dialog.show();
                    Utility.getCart(ABJewelsDetailActivity.this, cart, new ECNCallback() {
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
                }
            }
        });

        getData();
    }

    private void getAdditionalData() {
        String url = getString(R.string.jewel_commerce) + getString(R.string.api) + "store/products/" + ID + "?store_id=" + getString(R.string.store_id);

        HelperClass.getData(this, url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    JSONObject productObject = (JSONObject) obj.get("product");
                    String productString = productObject.toString();
                    HelperClass.putSharedString(getApplicationContext(), "new_product", productString);

                    jewelleryProduct = (new Gson()).fromJson(productString, JewelleryProduct.class);
                } catch (Exception e) {
                    Toast.makeText(ABJewelsDetailActivity.this, e.toString(), Toast.LENGTH_LONG).show();
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
        ((TextView) findViewById(R.id.txtProductName)).setText(product.getName());
        if (product.getDefault_variant() != null)
            ((TextView) findViewById(R.id.txtSku)).setText(product.getDefault_variant().getSku());

        if(product.getDescription() != null && !product.getDescription().equals("")) {
            ((TextView) findViewById(R.id.txtDescription)).setText(Html.fromHtml(product.getDescription()).toString().trim());
            findViewById(R.id.txtDescription).setVisibility(View.VISIBLE);
        }

        imageList = product.getImages();
        imageControl.init(imageList, true);
        checkQuantity();

        List<Option> sizeList = jewelleryProduct.getMetal_sizes();
        if (sizeList != null && !sizeList.isEmpty()) {
            if (sizeList.size() > 1) {
                List<String> list = new ArrayList<>();

                for (Option op : sizeList) {
                    list.add(op.getName());
                }

                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.attribute_spinner_item, list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
                sizeSpinner.setAdapter(dataAdapter);

                sizeSpinner.setSelection(getDefaultIndex(sizeList));

                sizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        getPrice();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                findViewById(R.id.sizeGuide).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(ABJewelsDetailActivity.this, SizeGuideActivity.class));
                    }
                });

                findViewById(R.id.sizeLayout).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.sizeLayout).setVisibility(View.GONE);
                getPrice();
            }
        }
    }

    private void checkQuantity() {
        oldPrice.setVisibility(View.GONE);
        discount.setVisibility(View.GONE);
        stock.setVisibility(View.GONE);
        findViewById(R.id.imgSale).setVisibility(View.GONE);

        buyNow.setBackgroundResource(R.drawable.button_click);
        buyNow.setText(getString(R.string.buy_now));
        buyNow.setEnabled(true);

        Variant v;
        v = product.getDefault_variant();

        if (v != null) {
            if (product.isTrack_quantity()) {
                int quantity = v.getQuantity();
                if (quantity <= v.getMinimum_stock_level()) {
                    buyNow.setBackgroundResource(R.color.LIGHT_GREY);
                    buyNow.setText(getString(R.string.out_of_stock));
                    buyNow.setEnabled(false);
                }
                int n = quantity - v.getMinimum_stock_level();
                if (n >= 1 && n <= 5) {
                    stock.setText(String.format(getString(R.string.only_left), n));
                    stock.setVisibility(View.VISIBLE);
                }
            }

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
            buyNow.setBackgroundResource(R.color.LIGHT_GREY);
            buyNow.setText(getString(R.string.out_of_stock));
            buyNow.setEnabled(false);
        }
    }

    private int getDefaultIndex(List<Option> optionList) {
        if (optionList != null && !optionList.isEmpty()) {
            for (int i = 0; i < optionList.size(); i++) {
                if (optionList.get(i).is_default())
                    return i;
            }
        }
        return -1;
    }

    private void getPrice() {
        if (progressBar.getVisibility() == View.GONE)
            dialog.show();
        String params = HelperClass.getSharedString(this, "price_parameters");
        if (params == null)
            params = "";

        String url = getString(R.string.jewel_commerce) + getString(R.string.api) + "store/products/" + ID + "/price?store_id=" + getString(R.string.store_id);
        url += params;
        int position = sizeSpinner.getSelectedItemPosition();
        if (position >= 0) {
            metal_size = "&metal_size_id=" + jewelleryProduct.getMetal_sizes().get(position).getId();
            url += metal_size;
        }

        HelperClass.getData(this, url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    JSONObject productObject = (JSONObject) obj.get("price");
                    String productString = productObject.toString();
                    HelperClass.putSharedString(getApplicationContext(), "price", productString);

                    price = (new Gson()).fromJson(productString, Price.class);

                    setPriceData();

                } catch (Exception e) {
                    Toast.makeText(ABJewelsDetailActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                } finally {
                    progressBar.setVisibility(View.GONE);
                    errorView.setVisibility(View.GONE);
                }
                if (dialog.isShowing())
                    dialog.dismiss();
            }

            @Override
            public void onFailure(KeyValuePair error) {
                displayError(error.getKey());
                if (dialog.isShowing())
                    dialog.dismiss();
            }
        });
    }

    private void setPriceData() {
        metalOption = price.getMetal();
        HelperClass.putSharedInt(ABJewelsDetailActivity.this, "metal_index", getDefaultIndex(jewelleryProduct.getMetal_options()));
        metal_option = "&metal_option_id=" + metalOption.getId();

        ((TextView) findViewById(R.id.txtPrice)).setText(HelperClass.formatCurrency(getString(R.string.currency), price.getTotal()));

        String value = "";
        diamond_options = "";
        List<ExtendedOption> diaOptions = price.getGem_options();
        if (diaOptions != null && !diaOptions.isEmpty()) {
            for (int i = 0; i < diaOptions.size(); i++) {
                if (!value.equals(""))
                    value += "/";
                value += diaOptions.get(i).getValue().getName();
                diamond_options += "&diamond_options[" + diaOptions.get(i).getId() + "]=" + diaOptions.get(i).getValue().getId();
            }
        }
        displayProductSpecification();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }

    private void displayProductSpecification() {
        String name = "", carat = "", shape = "";
        int nod = 0;
        List<ExtendedOption> list = price.getGem_options();
        for (int i = 0; i < list.size(); i++) {
            ExtendedOption dop = list.get(i);
            if (!name.equals(""))
                name += ",";
            name += dop.getValue().getName();

            if (!carat.equals(""))
                carat += ",";
            carat += dop.getValue().getCarat();

            if (!shape.equals(""))
                shape += ",";
            if (dop.getValue().getShape() != null)
                shape += dop.getValue().getShape();

            nod += dop.getNumber_of_gems();
        }

        if (product.getDefault_variant() != null)
            ((TextView) findViewById(R.id.stockNumber)).setText(product.getDefault_variant().getSku());

        ((TextView) findViewById(R.id.goldType)).setText(price.getMetal().getName());

        ((TextView) findViewById(R.id.goldSize)).setText(price.getMetal().getSize());

        ((TextView) findViewById(R.id.goldWeight)).setText(price.getMetal().getWeight() + "gm");

        ((TextView) findViewById(R.id.goldRate)).setText(price.getMetal().getPer_gm_rate() + "/gm");

        ((TextView) findViewById(R.id.goldPrices)).setText(HelperClass.formatCurrency(getString(R.string.currency), price.getMetal().getPrice()));

        ((TextView) findViewById(R.id.makePrice)).setText(HelperClass.formatCurrency(getString(R.string.currency), price.getMaking_charge()));

        ((TextView) findViewById(R.id.makeType)).setText(jewelleryProduct.getMaking_type());

        ((TextView) findViewById(R.id.grandTotal)).setText(HelperClass.formatCurrency(getString(R.string.currency), price.getTotal()));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.abjewels_detail;
    }

    @Override
    protected void getAddedData(String result) {
        getAdditionalData();
    }
}