package com.eComNation.Common;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.Toast;

import com.crashlytics.android.answers.AddToCartEvent;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.LoginEvent;
import com.eComNation.Activity.ABJewelsDetailActivity;
import com.eComNation.Activity.AddReviewActivity;
import com.eComNation.Activity.AddwikDetailsActivity;
import com.eComNation.Activity.CategoryListingActivity;
import com.eComNation.Activity.HivaDetailsActivity;
import com.eComNation.Activity.KiwiEnglandDetailsActivity;
import com.eComNation.Activity.LoginActivity;
import com.eComNation.Activity.MadeToMeasureActivity;
import com.eComNation.Activity.MinimalDetailActivity;
import com.eComNation.Activity.MyGarmentDetailsActivity;
import com.eComNation.Activity.OutletsDetailsActivity;
import com.eComNation.Activity.ProductDetailsActivity;
import com.eComNation.Activity.ProductListingActivity;
import com.eComNation.Activity.SurplusnationDetailsActivity;
import com.eComNation.Activity.TimberfruitDetailsActivity;
import com.eComNation.Activity.ViraniDetailsActivity;
import com.eComNation.R;
import com.ecomnationmobile.library.Common.BadgeDrawable;
import com.ecomnationmobile.library.Common.ECNCallback;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Control.ProgressDialogView;
import com.ecomnationmobile.library.Control.TouchImageView;
import com.ecomnationmobile.library.Data.Account;
import com.ecomnationmobile.library.Data.Cart;
import com.ecomnationmobile.library.Data.CustomDetail;
import com.ecomnationmobile.library.Data.ECNResponse;
import com.ecomnationmobile.library.Data.KeyValuePair;
import com.ecomnationmobile.library.Data.OrderLineItem;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Abhi on 28-04-2016.
 */
public class Utility {

    public static final  String DIAMOND = "diamond";

    public static final String JEWELLERY = "jewellery";
    public static final String MARKET_PLACE = "market_place";

    public static final String ADDWIK = "addwik";
    public static final String ABJEWELS = "abjewels";
    public static final String HIVA = "hiva";
    public static final String STORYATHOME = "storyhomes";
    public static final String OUTLETSINDIA = "outletsindia";
    public static final String TIMBERFRUIT = "timberfruit";
    public static final String VIRANI = "viranigems";
    public static final String SURPLUSNATION = "surplusnation";
    public static final String KIWIENGLAND = "kiwi-england";
    public static final String MINIMAL = "minimal";
    public static final String MYGARMENTS = "mygarments";

    public static Intent getDetailsIntent(Context mContext) {
        switch(mContext.getString(R.string.scheme)) {
            case MYGARMENTS:
                return new Intent(mContext, MyGarmentDetailsActivity.class);
            case MINIMAL:
                return new Intent(mContext, MinimalDetailActivity.class);
            case KIWIENGLAND:
                return new Intent(mContext, KiwiEnglandDetailsActivity.class);
            case SURPLUSNATION:
                return new Intent(mContext, SurplusnationDetailsActivity.class);
            case VIRANI:
                return new Intent(mContext, ViraniDetailsActivity.class);
            case TIMBERFRUIT:
                return new Intent(mContext, TimberfruitDetailsActivity.class);
            case HIVA:
                return new Intent(mContext, HivaDetailsActivity.class);
            case ABJEWELS:
                return new Intent(mContext, ABJewelsDetailActivity.class);
            case ADDWIK:
                return new Intent(mContext, AddwikDetailsActivity.class);
            case OUTLETSINDIA:
                return new Intent(mContext, OutletsDetailsActivity.class);
            default:
                return new Intent(mContext, ProductDetailsActivity.class);
        }
    }

    public static Intent getListingIntent(Context mContext, String cat_id){
        List<String> categoryList = Arrays.asList(mContext.getResources().getStringArray(R.array.category_listing));
        List<String> measureList = Arrays.asList(mContext.getResources().getStringArray(R.array.measure_category));
        if (categoryList.contains(cat_id)) {
            return new Intent(mContext, CategoryListingActivity.class);
        } else if (measureList.contains(cat_id)) {
            return new Intent(mContext, MadeToMeasureActivity.class);
        } else {
            return new Intent(mContext, ProductListingActivity.class);
        }
    }

    public static void getAccessToken(final Context context) {
        String refresh_token = HelperClass.getSharedString(context, context.getString(R.string.refresh_token));
        String url = context.getString(R.string.production_base_url);
        url += context.getString(R.string.api) + "store/customer/accounts/get_access_token?";
        url += "&client_id=" + context.getString(R.string.client_id);
        url += "&secret=" + context.getString(R.string.client_secret);
        url += "&refresh_token=" + refresh_token;

        HelperClass.postData(context, url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                ECNResponse response = (new Gson()).fromJson(result, ECNResponse.class);
                HelperClass.log_in(context, response);
            }

            @Override
            public void onFailure(KeyValuePair error) {
            }
        });
    }

    public static void getCart(final Context context, String token) {
        String url = context.getString(R.string.production_base_url) + context.getString(R.string.api);
        url += "store/cart?access_token=" + token;

        HelperClass.getData(context, url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    JSONObject cartObject = (JSONObject) obj.get("cart");
                    Cart cart = (new Gson()).fromJson(cartObject.toString(), Cart.class);
                    HelperClass.putSharedString(context, "cart", new Gson().toJson(cart));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(KeyValuePair error) {
            }
        });
    }

    public static void setBadgeCount(Context context, LayerDrawable icon, int count) {

        BadgeDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
        if (reuse != null && reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context);
        }

        badge.setColors(R.color.SecondaryColor, R.color.WHITE);

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
    }

    public static File saveImage(ImageView img) {
        BitmapDrawable drawable = (BitmapDrawable) img.getDrawable();
        if (drawable != null) {
            Bitmap bitmap = drawable.getBitmap();

            File sdCardDirectory = Environment.getExternalStorageDirectory();

            File image = new File(sdCardDirectory, "share.png");

            boolean success = false;

            FileOutputStream outStream;
            try {
                outStream = new FileOutputStream(image);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);

                outStream.flush();
                outStream.close();
                success = true;
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (success)
                return image;
        }
        return null;
    }

    public static void addToFavourites(final Context mContext, long ID, final ECNCallback mCallBack) {
        if (HelperClass.isUserLoggedIn(mContext)) {
            final ProgressDialog dialog = new ProgressDialogView(mContext, "Please wait...", R.drawable.progress);
            dialog.show();
            String url = mContext.getString(R.string.production_base_url) + mContext.getString(R.string.api) + "store/customer/favourite_products/" + ID + "?access_token=" + HelperClass.getSharedString(mContext, mContext.getString(R.string.access_token));

            HelperClass.postData(mContext, url, new ECNCallback() {
                @Override
                public void onSuccess(String result) {
                    dialog.dismiss();
                    Toast.makeText(mContext, "Added to favourites.", Toast.LENGTH_LONG).show();
                    mCallBack.onSuccess("");
                }

                @Override
                public void onFailure(KeyValuePair error) {
                    dialog.dismiss();
                    mCallBack.onFailure(null);
                    if (error.getValue() == 401) {
                        error.setKey(mContext.getString(R.string.logged_out_error));
                        getAccessToken(mContext);
                    }
                    Toast.makeText(mContext, error.getKey(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            HelperClass.putSharedInt(mContext, "activity_to_open", 2);
            mContext.startActivity(new Intent(mContext, LoginActivity.class));
        }
    }

    public static void removeFavourites(final Context mContext, long ID, final ECNCallback mCallBack) {
        final ProgressDialog dialog = new ProgressDialogView(mContext, "Please wait...", R.drawable.progress);

        dialog.show();
        String url = mContext.getString(R.string.production_base_url) + mContext.getString(R.string.api) + "store/customer/favourite_products/" + ID + "?access_token=" + HelperClass.getSharedString(mContext, mContext.getString(R.string.access_token));

        HelperClass.deleteData(mContext, url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                dialog.dismiss();
                Toast.makeText(mContext, "Removed from favourites.", Toast.LENGTH_LONG).show();
                mCallBack.onSuccess("");
            }

            @Override
            public void onFailure(KeyValuePair error) {
                dialog.dismiss();
                Toast.makeText(mContext, error.getKey(), Toast.LENGTH_LONG).show();
                mCallBack.onFailure(null);
            }
        });
    }

    public static void writeReview(final Context mContext) {
        if (HelperClass.isUserLoggedIn(mContext)) {
            Intent intentMain = new Intent(mContext, AddReviewActivity.class);
            intentMain.putExtra("id", 0);
            mContext.startActivity(intentMain);
        } else {
            HelperClass.putSharedInt(mContext, "activity_to_open", 2);
            mContext.startActivity(new Intent(mContext, LoginActivity.class));
        }
    }

    public static void getProfile(final Context mContext) {
        String url = mContext.getString(R.string.production_base_url) + mContext.getString(R.string.api) + "store/customer/profile?access_token=" + HelperClass.getSharedString(mContext, mContext.getString(R.string.access_token));
        HelperClass.getData(mContext, url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    JSONObject productObject = (JSONObject) obj.get("customer");
                    String productString = productObject.toString();
                    HelperClass.putSharedString(mContext, "profile", productString);

                    Account account = (new Gson()).fromJson(productString, Account.class);

                    Answers.getInstance().logLogin(new LoginEvent()
                            .putCustomAttribute("Username", account.getEmail()));

                } catch (Exception e) {
                    Toast.makeText(mContext, e.toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(KeyValuePair error) {

            }
        });
    }

    public static void getCart(final Context mContext, final Cart cart, final ECNCallback callback) {
        if (cart != null) {
            if (cart.getToken() != null) {
                String url = mContext.getString(R.string.production_base_url) + mContext.getString(R.string.api);
                url += "store/cart/" + cart.getToken() + "/get";
                url = checkAccess(mContext, url, "?");

                HelperClass.getData(mContext, url, new ECNCallback() {
                    @Override
                    public void onSuccess(String result) {
                        //setCart(mContext,result);
                        updateCart(mContext, cart, callback);
                    }

                    @Override
                    public void onFailure(KeyValuePair error) {
                        if (error.getValue() == 422) {
                            cart.setToken(null);
                            createCart(mContext, cart, callback);
                        } else {
                            if (error.getValue() == 401) {
                                error.setKey(mContext.getString(R.string.logged_out_error));
                                Utility.getAccessToken(mContext);
                            }
                            Toast.makeText(mContext, error.getKey(), Toast.LENGTH_LONG).show();
                            callback.onFailure(null);
                        }
                    }
                });
            } else {
                switch(mContext.getString(R.string.store_type)) {
                    case JEWELLERY:
                        addJewelleryItem(mContext, cart, callback);
                        break;
                    case MARKET_PLACE:
                        addMarketPlaceItem(mContext,cart,callback);
                        break;
                    default:
                        createCart(mContext, cart, callback);
                        break;
                }
            }
        }
    }

    private static void updateCart(final Context mContext, final Cart cart, ECNCallback callback) {
        List<OrderLineItem> items = cart.getItems();
        if (items != null && !items.isEmpty()) {
            switch(mContext.getString(R.string.store_type)) {
                case JEWELLERY:
                    addJewelleryItem(mContext, cart, callback);
                    break;
                case MARKET_PLACE:
                    addMarketPlaceItem(mContext,cart,callback);
                    break;
                default:
                    addItem(mContext, cart, callback);
                    break;
            }
        }
    }

    private static void createCart(final Context mContext, final Cart cart, final ECNCallback callback) {
        List<OrderLineItem> items = cart.getItems();
        if (items != null && !items.isEmpty()) {
            String url = mContext.getString(R.string.production_base_url) + mContext.getString(R.string.api);
            url += "store/cart/create?";
            for (int i = 0; i < items.size(); i++) {
                url += "items[][id]=" + items.get(i).getVariant_id();
                url += "&items[][quantity]=" + items.get(i).getQuantity();
                List<CustomDetail> list = items.get(i).getCustom_details();
                if (list != null && !list.isEmpty()) {
                    for (int j = 0; j < list.size(); j++) {
                        url += "&items[][custom_details][" + list.get(j).getKey() + "]=" + list.get(j).getValue();
                    }
                }
                if (i != items.size() - 1)
                    url += "&";
            }
            url = checkAccess(mContext, url, "&");

            HelperClass.postData(mContext, url, new ECNCallback() {
                @Override
                public void onSuccess(String result) {
                    setCart(mContext, result, callback);
                    Answers.getInstance().logAddToCart(new AddToCartEvent());
                }

                @Override
                public void onFailure(KeyValuePair error) {
                    Toast.makeText(mContext, error.getKey(), Toast.LENGTH_SHORT).show();
                    resetCart(mContext, callback);
                }
            });
        }
    }

    private static void addItem(final Context mContext, final Cart cart, final ECNCallback callback) {
        List<OrderLineItem> items = cart.getItems();
        if (items != null && !items.isEmpty()) {
            String url = mContext.getString(R.string.production_base_url) + mContext.getString(R.string.api);
            url += "store/cart/" + cart.getToken() + "/add_items?";
            int n = cart.getUpdate_id() - 10;
            for (int i = n; i < items.size(); i++) {
                url += "items[][id]=" + items.get(i).getVariant_id();
                url += "&items[][quantity]=" + items.get(i).getQuantity();
                List<CustomDetail> list = items.get(i).getCustom_details();
                if (list != null && !list.isEmpty()) {
                    for (int j = 0; j < list.size(); j++) {
                        url += "&items[][custom_details][" + list.get(j).getKey() + "]=" + list.get(j).getValue();
                    }
                }
                if (i != items.size() - 1)
                    url += "&";
            }
            url = checkAccess(mContext, url, "&");
            HelperClass.patchData(mContext, url, new ECNCallback() {
                @Override
                public void onSuccess(String result) {
                    setCart(mContext, result, callback);
                    Answers.getInstance().logAddToCart(new AddToCartEvent());
                }

                @Override
                public void onFailure(KeyValuePair error) {
                    Toast.makeText(mContext, error.getKey(), Toast.LENGTH_SHORT).show();
                    resetCart(mContext, callback);
                }
            });
        }
    }

    private static void addJewelleryItem(final Context mContext, final Cart cart, final ECNCallback callback) {
        List<OrderLineItem> items = cart.getItems();
        if (items != null && !items.isEmpty()) {
            String cart_token = cart.getToken() == null ? "" : "&cart_token=" + cart.getToken();
            int qty = items.get(items.size() - 1).getQuantity();
            String url = mContext.getString(R.string.jewel_commerce) + mContext.getString(R.string.api);
            url += "store/cart/items?store_id=" + mContext.getString(R.string.store_id);
            url += cart_token;
            url += "&quantity=" + qty;
            url += HelperClass.getSharedString(mContext, "cart_parameters");
            url = checkAccess(mContext, url, "&");
            HelperClass.postData(mContext, url, new ECNCallback() {
                @Override
                public void onSuccess(String result) {
                    HelperClass.putSharedString(mContext, "cart_parameters", null);
                    setCart(mContext, result, callback);
                    Answers.getInstance().logAddToCart(new AddToCartEvent());
                }

                @Override
                public void onFailure(KeyValuePair error) {
                    Toast.makeText(mContext, error.getKey(), Toast.LENGTH_SHORT).show();
                    resetCart(mContext, callback);
                }
            });
        }
    }

    private static void addMarketPlaceItem(final Context mContext, final Cart cart, final ECNCallback callback) {
        List<OrderLineItem> items = cart.getItems();
        if (items != null && !items.isEmpty()) {
            String cart_token = cart.getToken() == null ? "" : "&cart_token=" + cart.getToken();
            int qty = items.get(items.size() - 1).getQuantity();
            String url = mContext.getString(R.string.market_place) + mContext.getString(R.string.api);
            url += "store/cart/items?store_id=" + mContext.getString(R.string.store_id);
            url += cart_token;
            url += "&quantity=" + qty;
            url += HelperClass.getSharedString(mContext, "cart_parameters");
            url = checkAccess(mContext, url, "&");
            HelperClass.postData(mContext, url, new ECNCallback() {
                @Override
                public void onSuccess(String result) {
                    HelperClass.putSharedString(mContext, "cart_parameters", null);
                    setCart(mContext, result, callback);
                    Answers.getInstance().logAddToCart(new AddToCartEvent());
                }

                @Override
                public void onFailure(KeyValuePair error) {
                    Toast.makeText(mContext, error.getKey(), Toast.LENGTH_SHORT).show();
                    resetCart(mContext, callback);
                }
            });
        }
    }

    private static void setCart(Context mContext, String result, ECNCallback callback) {
        try {
            JSONObject obj = new JSONObject(result);
            JSONObject cartObject = (JSONObject) obj.get(mContext.getString(R.string.cart));
            JSONArray items = cartObject.getJSONArray("items");
            Cart responseCart = (new Gson()).fromJson(cartObject.toString(), Cart.class);
            if (items != null && items.length() > 0) {
                for (int i = 0; i < items.length(); i++) {
                    JSONObject jo = items.getJSONObject(i);
                    if (!jo.isNull("custom_details")) {
                        JSONObject object = jo.getJSONObject("custom_details");
                        JSONArray array = object.names();
                        if (array != null && array.length() > 0) {
                            List<CustomDetail> cdList = new ArrayList<>();
                            for (int j = 0; j < array.length(); j++) {
                                CustomDetail cd = new CustomDetail();
                                cd.setKey(array.getString(j));
                                cd.setValue(object.getString(array.getString(j)));
                                cdList.add(cd);
                            }
                            responseCart.getItems().get(i).setCustom_details(cdList);
                        }
                    }
                }
            }
            if (responseCart != null) {
                HelperClass.putSharedString(mContext, mContext.getString(R.string.cart), new Gson().toJson(responseCart));
                HelperClass.putSharedString(mContext, mContext.getString(R.string.old_cart), new Gson().toJson(responseCart));
            }
            callback.onSuccess("");
        } catch (Exception e) {
            Toast.makeText(mContext, e.toString(), Toast.LENGTH_LONG).show();
            callback.onFailure(null);
        }
    }

    public static String checkAccess(Context mContext, String url, String spec) {
        String access_token = HelperClass.getSharedString(mContext, mContext.getString(R.string.access_token));
        if (access_token != null && !access_token.isEmpty())
            url += spec + "access_token=" + access_token;
        return url;
    }

    private static void resetCart(Context mContext, ECNCallback callback) {
        Cart cart;
        String content = HelperClass.getSharedString(mContext, mContext.getString(R.string.old_cart));
        if (content != null) {
            cart = (new Gson()).fromJson(content, Cart.class);
        } else {
            cart = new Cart();
            cart.setItems(new ArrayList<OrderLineItem>());
        }
        HelperClass.putSharedString(mContext, mContext.getString(R.string.cart), new Gson().toJson(cart));

        callback.onFailure(null);
    }

    public static void showSizeDialog(final Context mContext, String url){
        final ProgressDialog progressDialog = new ProgressDialogView(mContext, "Please wait...", R.drawable.progress);
        progressDialog.show();
        final TouchImageView img = new TouchImageView(mContext);
        HelperClass.setPicassoBitMap(url, img, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                progressDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Size Chart");
                builder.setView(img);
                builder.setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }

            @Override
            public void onFailure(KeyValuePair error) {
                progressDialog.dismiss();
                Toast.makeText(mContext, "Failed to load size guide", Toast.LENGTH_LONG).show();
            }
        });
    }
}