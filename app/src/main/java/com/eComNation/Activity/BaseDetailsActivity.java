package com.eComNation.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.eComNation.Adapter.TabsCustomPagerAdapter;
import com.eComNation.Common.Utility;
import com.eComNation.Fragment.HTMLFragment;
import com.eComNation.Fragment.ProductAttributesFragment;
import com.eComNation.Fragment.ProductDescriptionFragment;
import com.eComNation.R;
import com.ecomnationmobile.library.Common.ECNCallback;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Control.CollapsibleText;
import com.ecomnationmobile.library.Control.ImageControl;
import com.ecomnationmobile.library.Control.PinCodeChecker;
import com.ecomnationmobile.library.Control.ProductListControl;
import com.ecomnationmobile.library.Control.ProgressDialogView;
import com.ecomnationmobile.library.Control.ReviewControl;
import com.ecomnationmobile.library.Control.SlidingTabLayout;
import com.ecomnationmobile.library.Control.WrapContentHeightViewPager;
import com.ecomnationmobile.library.Data.Cart;
import com.ecomnationmobile.library.Data.KeyValuePair;
import com.ecomnationmobile.library.Data.Product;
import com.ecomnationmobile.library.Data.ProductAttribute;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by User on 9/12/2016.
 */
public class BaseDetailsActivity extends FragmentActivity {

    ImageControl imageControl;
    ProductListControl productListControl;
    ReviewControl reviewControl;
    TextView stock, oldPrice, discount;
    CollapsibleText smallDescription;
    Button buyNow, addCart;
    Product product;
    int quantity, n_left, addCount;
    Cart cart;
    PinCodeChecker pinCodeChecker;
    Uri imageURI;
    View progressBar, errorView;
    Bundle mBundle;
    String ID;
    WrapContentHeightViewPager mViewPager;
    SlidingTabLayout mTabStrip;
    TabsCustomPagerAdapter mAdapter;
    ArrayList<String> mFragments;
    ArrayList<KeyValuePair> mTabs;
    ProgressDialog dialog;
    ArrayList<String> favouriteProductList;
    String favourite_products;
    private static final int REQUEST_WRITE_STORAGE = 112;

    @Override
    public void onResume() {
        super.onResume();

        addCount = 0;
        quantity = 1;

        setCartCount();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutId());

        dialog = new ProgressDialogView(this, "Updating...", R.drawable.progress);

        buyNow = (Button) findViewById(R.id.btnBuy);
        addCart = (Button) findViewById(R.id.btnAddToCart);
        discount = (TextView) findViewById(R.id.txtDiscount);
        oldPrice = (TextView) findViewById(R.id.txtOldPrice);
        stock = (TextView) findViewById(R.id.txtStock);
        smallDescription = (CollapsibleText) findViewById(R.id.txtSmallDescription);
        progressBar = findViewById(R.id.progressBar);
        errorView = findViewById(R.id.errorLayout);
        pinCodeChecker = (PinCodeChecker) findViewById(R.id.pincodeBlock);
        productListControl = (ProductListControl) findViewById(R.id.productListControl);
        reviewControl = (ReviewControl) findViewById(R.id.reviewControl);
        imageControl = (ImageControl) findViewById(R.id.imageControl);
        imageControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (product != null) {
                    Intent intent = new Intent(getApplicationContext(), ImageListActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putLong("id", product.getId());
                    intent.putExtra("position", imageControl.getSelectedPosition());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });

        errorView.findViewById(R.id.btnRetry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });

        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        initFavouriteList();
        findViewById(R.id.addFavourite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (product != null) {
                    Utility.addToFavourites(BaseDetailsActivity.this, product.getId(), new ECNCallback() {
                        @Override
                        public void onSuccess(String result) {
                            ((ImageButton)findViewById(R.id.addFavourite)).setImageResource(R.drawable.colored_heart);
                            favouriteProductList.add(Long.toString(product.getId()));
                            HelperClass.putSharedString(BaseDetailsActivity.this, "favourite_products", TextUtils.join(",", favouriteProductList));
                        }

                        @Override
                        public void onFailure(KeyValuePair error) {
                        }
                    });
                }
            }
        });

        findViewById(R.id.btnShare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean hasPermission = (ContextCompat.checkSelfPermission(BaseDetailsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
                if (!hasPermission) {
                    ActivityCompat.requestPermissions(BaseDetailsActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);
                } else {
                    shareProduct();
                }

            }
        });

        if (pinCodeChecker != null) {
            if (HelperClass.getSharedString(this, "pincodes") == null) {
                pinCodeChecker.setVisibility(View.GONE);
            } else {
                pinCodeChecker.init(R.color.SecondaryColor, R.string.pincode_true, R.string.pincode_false, R.string.pincode_null);
                pinCodeChecker.setVisibility(View.VISIBLE);
            }
        }

        oldPrice.setPaintFlags(oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        readData(getIntent());
    }

    private void readData(Intent intent) {
        if (intent != null) {
            Uri data = intent.getData();
            if (data != null) {
                int index = data.toString().lastIndexOf("/");
                ID = data.toString().substring(index + 1);
            } else {
                mBundle = intent.getExtras();
                ID = String.valueOf(mBundle.getLong("id"));
            }
        }
    }

    protected void getData() {
        addCount = 0;
        findViewById(R.id.addFavourite).setBackgroundResource(R.drawable.transparent_click);
        progressBar.setVisibility(View.VISIBLE);
        findViewById(R.id.scrollView2).scrollTo(0, 0);

        String url = getString(R.string.production_base_url) + getString(R.string.api) + "store/products/" + ID;

        HelperClass.getData(this, url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    JSONObject productObject = (JSONObject) obj.get("product");
                    String productString = productObject.toString();
                    HelperClass.putSharedString(getApplicationContext(), "product", productString);

                    product = (new Gson()).fromJson(productString, Product.class);

                    if (product != null) {
                        Answers.getInstance().logContentView(new ContentViewEvent()
                                .putContentName("Product Detail: " + product.getName())
                                .putContentType("Product")
                                .putContentId(product.getDefault_variant().getSku()));

                        if(favouriteProductList !=null && !favouriteProductList.isEmpty()){
                            if(favouriteProductList.contains(Long.toString(product.getId())))
                                ((ImageButton)findViewById(R.id.addFavourite)).setImageResource(R.drawable.colored_heart);
                        }

                        productListControl.init(product, getString(R.string.production_base_url), R.string.currency, R.dimen.small_item_height, R.drawable.sale_label, new ECNCallback() {
                            @Override
                            public void onSuccess(String result) {
                                ID = result;
                                getData();
                            }

                            @Override
                            public void onFailure(KeyValuePair error) {

                            }
                        });

                        reviewControl.init(getString(R.string.store_id), product.getId(), R.color.SecondaryColor, R.drawable.progress, new ECNCallback() {
                            @Override
                            public void onSuccess(String result) {
                                Utility.writeReview(BaseDetailsActivity.this);
                            }

                            @Override
                            public void onFailure(KeyValuePair error) {
                            }
                        });
                    }

                } catch (Exception e) {
                    Toast.makeText(BaseDetailsActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                }

                getAddedData(result);
            }

            @Override
            public void onFailure(KeyValuePair error) {
                displayError(error.getKey());
            }
        });
    }

    protected void displayError(String error) {
        progressBar.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);
        ((TextView) errorView.findViewById(R.id.txtMessage)).setText(error);
    }

    protected void displayData() {
        ((TextView) findViewById(R.id.txtProductName)).setText(product.getName());

        if (getResources().getBoolean(R.bool.show_small_description)) {
            if (product.getDescription() != null && !product.getDescription().equals("")) {
                smallDescription.setText(Html.fromHtml(product.getDescription()).toString().trim().replace("Â", ""), R.color.SecondaryColor, R.drawable.button_click_2);
                smallDescription.setVisibility(View.VISIBLE);
            }
        }

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
                return ContextCompat.getColor(BaseDetailsActivity.this, R.color.PrimaryColor);
            }
        });

        mTabStrip.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener());
    }

    protected void setCartCount() {
        ImageButton item = (ImageButton) findViewById(R.id.cart);
        LayerDrawable icon = (LayerDrawable) item.getDrawable();

        // Update LayerDrawable's BadgeDrawable
        Utility.setBadgeCount(this, icon, HelperClass.getCartCount(this));
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaseDetailsActivity.this, CheckOutActivity.class);
                intent.putExtras(new Bundle());
                startActivity(intent);
            }
        });
    }

    protected int getLayoutId() {
        return 0;
    }

    protected void getAddedData(String result) {
    }

    private void shareProduct() {
        if (product != null) {
            File f = Utility.saveImage(imageControl.getDefaultImage());
            if (f != null) {
                imageURI = Uri.fromFile(f);
                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM, imageURI);
                String contentText = product.getName() + "\n";
                contentText += getString(R.string.http) + getString(R.string.product_url) + product.getPermalink();
                intent.putExtra(Intent.EXTRA_TEXT, contentText);
                startActivityForResult(Intent.createChooser(intent, "Share using"), 0);
            }
        }
    }

    private void initFavouriteList(){
        favourite_products = HelperClass.getSharedString(BaseDetailsActivity.this, "favourite_products");
        if (favourite_products != null) {
            favouriteProductList = new ArrayList<>(Arrays.asList(favourite_products.split(",")));
        }
        else {
            favouriteProductList = new ArrayList<>();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    shareProduct();
                } else {
                    Toast.makeText(BaseDetailsActivity.this, "Access denied while writing product image to storage. Please allow access to use sharing functionality.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
