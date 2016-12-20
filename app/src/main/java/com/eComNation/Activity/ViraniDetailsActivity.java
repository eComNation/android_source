package com.eComNation.Activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.eComNation.Adapter.TabsCustomPagerAdapter;
import com.eComNation.Common.Utility;
import com.eComNation.Fragment.HTMLFragment;
import com.eComNation.Fragment.SpecificationFragment;
import com.eComNation.R;
import com.ecomnationmobile.library.Common.ECNCallback;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Control.SlidingTabLayout;
import com.ecomnationmobile.library.Control.WrapContentHeightViewPager;
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

import org.joda.time.DateTime;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Abhi on 21-06-2016.
 */
public class ViraniDetailsActivity extends BaseDetailsActivity {

    TextView txtBrkUp;
    ScrollView mainScrollView;
    JewelleryProduct jewelleryProduct;
    List<ExtendedOption> diamondOptions;
    List<Option> primaryStoneList, secondaryStoneList, primaryQualityList, secondaryQualityList;
    Price price;
    String metal_option, metal_size, diamond_options;
    View priceBreakUp;
    List<ProductImage> imageList;
    private WrapContentHeightViewPager mViewPager;
    private SlidingTabLayout mTabStrip;
    private TabsCustomPagerAdapter mAdapter;
    private ArrayList<String> mFragments;
    private ArrayList<KeyValuePair> mTabs;
    Spinner sizeSpinner;
    Option metalOption;

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        HelperClass.putSharedInt(this, "metal_index", -1);
        HelperClass.putSharedString(this, "engrave_text", null);
        HelperClass.putSharedString(this, "price_parameters", null);

        mainScrollView = (ScrollView) findViewById(R.id.scrollView2);
        txtBrkUp = (TextView) findViewById(R.id.txtPriceBreakUp);
        priceBreakUp = findViewById(R.id.prices);

        mViewPager = (WrapContentHeightViewPager) findViewById(R.id.pager);
        mTabStrip = (SlidingTabLayout) findViewById(R.id.tabs);
        sizeSpinner = (Spinner) findViewById(R.id.sizeSpinner);

        txtBrkUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (priceBreakUp.getVisibility() == View.VISIBLE) {
                    priceBreakUp.setVisibility(View.GONE);
                    txtBrkUp.setText(getString(R.string.view_breakup));
                } else {
                    priceBreakUp.setVisibility(View.VISIBLE);
                    txtBrkUp.setText(getString(R.string.hide_breakup));
                }
            }
        });

        findViewById(R.id.txtCustomize).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(ViraniDetailsActivity.this, ViraniCustomizationActivity.class), 0);
            }
        });

        findViewById(R.id.txtSolitaire).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ViraniDetailsActivity.this, ViraniRequestActivity.class));
            }
        });

        DateTime date = DateTime.now().plusDays(15);
        ((TextView) findViewById(R.id.txtDeliverDate)).setText(HelperClass.formatDateOnly(date.toString()));

        HelperClass.putSharedString(ViraniDetailsActivity.this, "attribute_selection", null);
        HelperClass.putSharedString(ViraniDetailsActivity.this, "product_option", null);

        buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addCount == 0) {
                    addCount++;
                    String content = HelperClass.getSharedString(ViraniDetailsActivity.this, getString(R.string.cart));
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
                    HelperClass.putSharedString(ViraniDetailsActivity.this, "cart_parameters", cart_parameters);

                    dialog.show();
                    Utility.getCart(ViraniDetailsActivity.this, cart, new ECNCallback() {
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
                    Toast.makeText(ViraniDetailsActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                }
                displayData();
            }

            @Override
            public void onFailure(KeyValuePair error) {
                displayError(error.getKey());
            }
        });
    }

    @Override
    protected void displayData() {
        quantity = 1;

        ((TextView) findViewById(R.id.txtProductName)).setText(product.getName());

        imageList = product.getImages();
        imageControl.init(imageList,true);

        checkQuantity();

        setUpImages(getString(R.string.misc_url) + "misc/right-img.jpg", R.id.certified);
        setUpImages(getString(R.string.misc_url) + "misc/watech.jpg", R.id.exchange);
        setUpImages(getString(R.string.misc_url) + "misc/shipping.jpg", R.id.shipping);
        setUpImages(getString(R.string.misc_url) + "misc/guarantee.jpg", R.id.guarantee);
        setUpImages(getString(R.string.misc_url) + "misc/jewellery.jpg", R.id.model);
        setUpImages(getString(R.string.misc_url) + "misc/Spects.png", R.id.conflict);

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
                        startActivity(new Intent(ViraniDetailsActivity.this, SizeGuideActivity.class));
                    }
                });

                findViewById(R.id.sizeLayout).setVisibility(View.VISIBLE);
            } else {
                metal_size = "";
                findViewById(R.id.sizeLayout).setVisibility(View.GONE);
                getPrice();
            }
        }
    }

    private void setUpImages(String url, int id) {
        ImageView image = (ImageView) findViewById(id);
        HelperClass.setPicassoBitMap(url, image, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
            }

            @Override
            public void onFailure(KeyValuePair error) {
            }
        });
    }

    private void checkQuantity() {
        stock.setVisibility(View.GONE);

        buyNow.setBackgroundResource(R.drawable.button_click);
        buyNow.setText(getString(R.string.add_cart));
        buyNow.setEnabled(true);

        Variant v = product.getDefault_variant();

        if (v != null) {

            if (product.isTrack_quantity()) {
                int quantity = v.getQuantity();
                if (quantity <= v.getMinimum_stock_level()) {
                    buyNow.setBackgroundResource(R.color.LIGHT_GREY);
                    buyNow.setText(getString(R.string.out_of_stock));
                    buyNow.setEnabled(false);
                }
                /*int n = quantity - v.getMinimum_stock_level();
                if (n >= 1 && n <= 5) {
                    stock.setText(String.format(getString(R.string.only_left), n));
                    stock.setVisibility(View.VISIBLE);
                }*/
            }
        } else {
            buyNow.setBackgroundResource(R.color.LIGHT_GREY);
            buyNow.setText(getString(R.string.out_of_stock));
            buyNow.setEnabled(false);
        }
    }

    private Option getDiamondOption(int index, String id) {
        List<Option> list = jewelleryProduct.getGem_options().get(index).getValues();
        if (list != null && !list.isEmpty()) {
            for (Option dop : list) {
                if (dop.getId().equals(id))
                    return dop;
            }
        }
        return null;
    }

    private int getIndexById(List<Option> optionList, String id) {
        if (optionList != null && !optionList.isEmpty()) {
            for (int i = 0; i < optionList.size(); i++) {
                if (optionList.get(i).getId().equals(id))
                    return i;
            }
        }
        return -1;
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
        if(progressBar.getVisibility() == View.GONE)
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
                    Toast.makeText(ViraniDetailsActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                } finally {
                    progressBar.setVisibility(View.GONE);
                    errorView.setVisibility(View.GONE);
                }
                if(dialog.isShowing())
                    dialog.dismiss();
            }

            @Override
            public void onFailure(KeyValuePair error) {
                displayError(error.getKey());
                if(dialog.isShowing())
                    dialog.dismiss();
            }
        });
    }

    private void setPriceData() {
        metalOption = price.getMetal();
        String metalName = metalOption.getName();
        ((TextView) findViewById(R.id.metalValue)).setText(metalName.substring(metalName.length() - 3).toUpperCase());
        metalName = metalName.substring(0, metalName.length() - 4).replace(" ", "_").toUpperCase();
        findViewById(R.id.metalColor).setBackgroundResource(getResources().getIdentifier(metalName, "color", getPackageName()));
        HelperClass.putSharedInt(ViraniDetailsActivity.this, "metal_index", getIndexById(jewelleryProduct.getMetal_options(), metalOption.getId()));
        metal_option = "&metal_option_id=" + metalOption.getId();

        ((TextView) findViewById(R.id.txtPrice)).setText(HelperClass.formatCurrency(getString(R.string.currency), price.getTotal()));
        Double diff = jewelleryProduct.getDiscount();

        if (diff > 0) {
            Double prev = price.getTotal()*100/(100 - diff);
            oldPrice.setVisibility(View.VISIBLE);
            oldPrice.setText(HelperClass.formatCurrency(getString(R.string.currency), prev));
            oldPrice.setPaintFlags(oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            discount.setText(String.format("Save "+diff+"%"));
            discount.setVisibility(View.VISIBLE);

            findViewById(R.id.imgSale).setVisibility(View.VISIBLE);
        }
        else {
            oldPrice.setVisibility(View.GONE);
            discount.setVisibility(View.GONE);
            findViewById(R.id.imgSale).setVisibility(View.GONE);
        }

        String value = "";
        double diamondAmount = 0, gemAmount = 0;
        diamond_options = "";
        List<ExtendedOption> diaOptions = price.getGem_options();
        if (diaOptions != null && !diaOptions.isEmpty()) {
            diamondOptions = jewelleryProduct.getGem_options();
            primaryQualityList = getList(0, Utility.DIAMOND);
            secondaryQualityList = getList(1, Utility.DIAMOND);
            primaryStoneList = getList(0, "");
            secondaryStoneList = getList(1, "");

            for (int i = 0; i < diaOptions.size(); i++) {
                if (!value.equals(""))
                    value += "/";
                value += diaOptions.get(i).getValue().getName();
                diamondAmount += diaOptions.get(i).getValue().getPrice();
                diamond_options += "&gem_options[" + diaOptions.get(i).getId() + "]=" + diaOptions.get(i).getValue().getId();

                Option diamondOption = getDiamondOption(i, diaOptions.get(i).getValue().getId());
                if (diamondOption != null) {
                    if (diamondOption.getType().equalsIgnoreCase(Utility.DIAMOND)) {
                        if (i == 0) {
                            HelperClass.putSharedInt(ViraniDetailsActivity.this, "primary_quality", getIndexById(primaryQualityList, diamondOption.getId()));
                            HelperClass.putSharedInt(this, "primary_stone", -1);
                        }
                        else {
                            HelperClass.putSharedInt(ViraniDetailsActivity.this, "secondary_quality", getIndexById(secondaryQualityList, diamondOption.getId()));
                            HelperClass.putSharedInt(this, "secondary_stone", -1);
                        }
                    } else {
                        if (i == 0) {
                            HelperClass.putSharedInt(ViraniDetailsActivity.this, "primary_stone", getIndexById(primaryStoneList, diamondOption.getId()));
                            HelperClass.putSharedInt(this, "primary_quality", -1);
                        }
                        else {
                            HelperClass.putSharedInt(ViraniDetailsActivity.this, "secondary_stone", getIndexById(secondaryStoneList, diamondOption.getId()));
                            HelperClass.putSharedInt(this, "secondary_quality", -1);
                        }
                    }
                }

            }
        }
        ((TextView) findViewById(R.id.qualityValue)).setText(value);

        ((TextView) findViewById(R.id.diamondPrice)).setText(HelperClass.formatCurrency(getString(R.string.currency), diamondAmount));
        ((TextView) findViewById(R.id.gemPrice)).setText(HelperClass.formatCurrency(getString(R.string.currency), gemAmount));
        ((TextView) findViewById(R.id.goldPrice)).setText(HelperClass.formatCurrency(getString(R.string.currency), metalOption.getPrice()));
        ((TextView) findViewById(R.id.makingPrice)).setText(HelperClass.formatCurrency(getString(R.string.currency), price.getMaking_charge()));

        mFragments = new ArrayList<>();
        mTabs = new ArrayList<>();

        mFragments.add(SpecificationFragment.class.getName());
        mTabs.add(new KeyValuePair(0, "PRODUCT DETAILS"));

        List<String> product_detail = Arrays.asList(getResources().getStringArray(R.array.product_detail));
        if (!product_detail.isEmpty()) {
            for (int i = 0; i < product_detail.size(); i++) {
                mFragments.add(HTMLFragment.class.getName());
                mTabs.add(new KeyValuePair(i, product_detail.get(i).toUpperCase()));
            }
        }

        mTabStrip.setTextSelector(R.color.colored_selector);
        mAdapter = new TabsCustomPagerAdapter(getSupportFragmentManager(), ViraniDetailsActivity.this, mFragments, mTabs);
        mViewPager.setAdapter(mAdapter);

        mTabStrip.setDistributeEvenly(true);
        mTabStrip.setViewPager(mViewPager);

        mTabStrip.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.PrimaryColor);
            }
        });

        mTabStrip.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener());
    }

    private List<Option> getList(int index, String bool) {
        List<Option> newList = new ArrayList<>();
        if (diamondOptions != null && diamondOptions.size() > index) {
            ExtendedOption diop = diamondOptions.get(index);
            for (Option t : diop.getValues()) {
                if (t.getType().equalsIgnoreCase(bool))
                    newList.add(t);
            }
        }
        return newList;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 0 && resultCode == 100) {
            price = (new Gson()).fromJson(HelperClass.getSharedString(this, "price"), Price.class);
            String image_name = price.getMetal().getName().toLowerCase().replace(" ", "_").replace("18k", "14k");
            Option diamondOption = getDiamondOption(0, price.getGem_options().get(0).getValue().getId());
            if (diamondOption != null) {
                if (diamondOption.getType().equalsIgnoreCase(Utility.DIAMOND)) {
                    image_name += "_ef-vvs";
                } else {
                    image_name += "_" + diamondOption.getName().toLowerCase().replace(" ", "_");
                }
            }
            int count = imageList.size();
            String url = getString(R.string.misc_url) + "products/files/" + product.getId() + "/";
            url += image_name;

            for (int i = 1; i <= count; i++) {
                int index = imageList.get(i - 1).getUrl().indexOf(".jpg");
                String ext = imageList.get(i - 1).getUrl().substring(index - 1);
                imageList.get(i - 1).setUrl(url + "_" + ext);
            }
            product.setImages(imageList);
            HelperClass.putSharedString(this, "product", new Gson().toJson(product));

            imageControl.init(imageList,true);
            setPriceData();
            mainScrollView.fullScroll(ScrollView.FOCUS_UP);
        }
    }

    @Override
    protected int getLayoutId(){
        return R.layout.virani_detail;
    }

    @Override
    protected void getAddedData(String result) {
        getAdditionalData();
    }

}
