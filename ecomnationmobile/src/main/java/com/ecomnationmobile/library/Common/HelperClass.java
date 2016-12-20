package com.ecomnationmobile.library.Common;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ecomnationmobile.library.Data.Address;
import com.ecomnationmobile.library.Data.BitmapTransform;
import com.ecomnationmobile.library.Data.Cart;
import com.ecomnationmobile.library.Data.DropdownClass;
import com.ecomnationmobile.library.Data.ECNResponse;
import com.ecomnationmobile.library.Data.ExtendedOption;
import com.ecomnationmobile.library.Data.ExtendedVariant;
import com.ecomnationmobile.library.Data.KeyValuePair;
import com.ecomnationmobile.library.Data.OrderLineItem;
import com.ecomnationmobile.library.Data.Product;
import com.ecomnationmobile.library.Data.Variant;
import com.ecomnationmobile.library.Data.Vendor;
import com.ecomnationmobile.library.R;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Abhi on 26/7/15.
 */
public class HelperClass {

    public static final int DEFAULT_TIMEOUT_MS = 5000;
    public static final int DEFAULT_RETRIES = 1;
    public static final float DEFAULT_BACKOFF_MULT = 1.0F;


    private static Context context;

    public static void initialize(Context ctx) {
        context = ctx;
    }

    public static int setColor(Context context, String status) {
        return R.color.WHITE;
    }

    public static void set_URL(String url) {
        putSharedString(context, "server_url", url);
    }

    public static void log_in(Context context, ECNResponse response) {
        HelperClass.putSharedString(context, context.getString(R.string.access_token), response.getAccess_token());
        HelperClass.putSharedString(context, context.getString(R.string.refresh_token), response.getRefresh_token());
        DateTime expires_on = new DateTime(response.getCreated_at()).plusSeconds(response.getExpires_in());
        HelperClass.putSharedString(context, "expires_on", expires_on.toString());
    }

    public static void log_out(Context mContext) {
        HelperClass.putSharedString(mContext, "cart", null);
        HelperClass.putSharedString(mContext, "old_cart", null);
        HelperClass.putSharedString(mContext, mContext.getString(R.string.access_token), null);
        HelperClass.putSharedString(mContext, mContext.getString(R.string.refresh_token), null);
        HelperClass.putSharedString(mContext, "expires_on", null);
        HelperClass.putSharedString(mContext, "favourite_products", null);

        Toast.makeText(mContext, "Log out successful.", Toast.LENGTH_LONG).show();
    }

    public static String formatDateAndTime(String dateTime) {
        if (dateTime == null)
            return "";

        DateTime date = new DateTime(dateTime);
        DateTimeFormatter dtFmt = DateTimeFormat.forPattern("MMM dd, yyyy");
        DateTimeFormatter tmFmt = DateTimeFormat.forPattern("hh:mm aa");

        return dtFmt.print(date) + " at " + tmFmt.print(date).toUpperCase();
    }

    public static String formatDateOnly(String dateTime) {
        if (dateTime == null)
            return "";

        DateTime date = new DateTime(dateTime);

        DateTimeFormatter dtFmt = DateTimeFormat.forPattern("dd MMM, yyyy");

        return dtFmt.print(date);
    }

    @SuppressWarnings("deprecation")
    public static void ClearCookies(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }

    public static void getData(final Context mContext, String url, final ECNCallback callback) {

        RequestQueue queue = Volley.newRequestQueue(mContext);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            callback.onSuccess(response);
                        } catch (Exception e) {
                            Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onFailure(handleResponseError(mContext, error));
            }
        }){
            @Override
            public Map<String, String> getHeaders(){
                Map<String, String> headers = new HashMap<>();
                headers.put("User-agent", "ANDROID");
                return headers;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DEFAULT_TIMEOUT_MS,
                DEFAULT_RETRIES,
                DEFAULT_BACKOFF_MULT));
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public static void deleteData(final Context mContext, String url, final ECNCallback callback) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            callback.onSuccess(response);
                        } catch (Exception e) {
                            Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onFailure(handleResponseError(mContext, error));
            }
        }){
            @Override
            public Map<String, String> getHeaders(){
                Map<String, String> headers = new HashMap<>();
                headers.put("User-agent", "ANDROID");
                return headers;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DEFAULT_TIMEOUT_MS,
                DEFAULT_RETRIES,
                DEFAULT_BACKOFF_MULT));
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public static void postData(final Context mContext, String url, final ECNCallback callback) {
        RequestQueue queue = Volley.newRequestQueue(mContext);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            callback.onSuccess(response);
                        } catch (Exception e) {
                            Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onFailure(handleResponseError(mContext, error));
            }
        }){
            @Override
            public Map<String, String> getHeaders(){
                Map<String, String> headers = new HashMap<>();
                headers.put("User-agent", "ANDROID");
                return headers;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DEFAULT_TIMEOUT_MS,
                DEFAULT_RETRIES,
                DEFAULT_BACKOFF_MULT));
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public static void patchData(final Context mContext, String url, final ECNCallback callback) {

        RequestQueue queue = Volley.newRequestQueue(mContext, new OkHttpStack());

        StringRequest stringRequest = new StringRequest(Request.Method.PATCH, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            callback.onSuccess(response);
                        } catch (Exception e) {
                            Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onFailure(handleResponseError(mContext, error));
            }
        }){
            @Override
            public Map<String, String> getHeaders(){
                Map<String, String> headers = new HashMap<>();
                headers.put("User-agent", "ANDROID");
                return headers;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DEFAULT_TIMEOUT_MS,
                DEFAULT_RETRIES,
                DEFAULT_BACKOFF_MULT));
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public static void putData(final Context mContext, String url, final ECNCallback callback) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            callback.onSuccess(response);
                        } catch (Exception e) {
                            Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onFailure(handleResponseError(mContext, error));
            }
        }){
            @Override
            public Map<String, String> getHeaders(){
                Map<String, String> headers = new HashMap<>();
                headers.put("User-agent", "ANDROID");
                return headers;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DEFAULT_TIMEOUT_MS,
                DEFAULT_RETRIES,
                DEFAULT_BACKOFF_MULT));
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public static String formatName(Address address) {
        String name = "";
        if (address != null) {
            if (address.getFirst_name() != null)
                name += address.getFirst_name().trim();

            if (!name.equals(""))
                name += " ";

            if (address.getLast_name() != null)
                name += address.getLast_name().trim();
        }
        return name.trim();
    }

    public static String formatAddress(Address address) {
        String addressString = "";
        if (address != null) {
            if (address.getAddress1() != null)
                addressString = address.getAddress1().trim() + ", ";
            if (address.getAddress2() != null) {
                addressString += address.getAddress2().trim() + ", ";
            }
            if (address.getCity() != null)
                addressString += address.getCity().trim() + ", ";

            if (address.getState_name() != null)
                addressString += address.getState_name().trim() + ", ";

            if (address.getZipcode() != null)
                addressString += address.getZipcode().trim() + ", ";

            if (address.getCountry_name() != null)
                addressString += address.getCountry_name().trim();
            addressString = addressString.replaceAll("  ", " ");
            addressString = addressString.replaceAll(", ,", ",");
            addressString = addressString.replaceAll(",,", ",");
        }
        return addressString;
    }

    public static Cart getCart(Context context) {
        String content = getSharedString(context, context.getString(R.string.old_cart));
        if (content == null)
            return null;
        return (new Gson()).fromJson(content, Cart.class);
    }

    public static int getCartCount(Context context) {
        Cart cart = getCart(context);
        int quantity = 0;
        if (cart != null) {
            if (cart.getItems() != null) {
                for (OrderLineItem o : cart.getItems())
                    quantity += o.getQuantity();
            }
        }
        return quantity;
    }

    public static int getId(String resourceName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resourceName);
            return idField.getInt(idField);
        } catch (Exception e) {
            throw new RuntimeException("No resource ID found for: "
                    + resourceName + " / " + c, e);
        }
    }

    public static int getDrawableId(Context mContext, String drawableName) {
        return mContext.getResources().getIdentifier(drawableName, "drawable", mContext.getPackageName());
    }

    public static String capitalizeFirst(String string) {
        String c = string.substring(0, 1);
        string = string.substring(1);
        string = c.toUpperCase() + string;

        return string;
    }

    public static int getColorId(Context mContext, String colorName) {
        return mContext.getResources().getIdentifier(colorName, "color", mContext.getPackageName());
    }

    public static String getSharedString(Context mContext, String key) {
        SharedPreferences settings = mContext.getSharedPreferences(mContext.getString(R.string.store_preferences), Context.MODE_PRIVATE);
        return settings.getString(key, null);
    }

    public static void putSharedString(Context mContext, String key, String value) {
        SharedPreferences settings = mContext.getSharedPreferences(mContext.getString(R.string.store_preferences), Context.MODE_PRIVATE);
        settings.edit().putString(key, value).commit();
    }

    public static int getSharedInt(Context mContext, String key) {
        SharedPreferences settings = mContext.getSharedPreferences(mContext.getString(R.string.store_preferences), Context.MODE_PRIVATE);
        return settings.getInt(key, -1);
    }

    public static void putSharedInt(Context mContext, String key, int value) {
        SharedPreferences settings = mContext.getSharedPreferences(mContext.getString(R.string.store_preferences), Context.MODE_PRIVATE);
        settings.edit().putInt(key, value).commit();
    }

    public static boolean getSharedBoolean(Context mContext, String key) {
        SharedPreferences settings = mContext.getSharedPreferences(mContext.getString(R.string.store_preferences), Context.MODE_PRIVATE);
        return settings.getBoolean(key, false);
    }

    public static void putSharedBoolean(Context mContext, String key, boolean value) {
        SharedPreferences settings = mContext.getSharedPreferences(mContext.getString(R.string.store_preferences), Context.MODE_PRIVATE);
        settings.edit().putBoolean(key, value).commit();
    }

    public static long getSharedLong(Context mContext, String key) {
        SharedPreferences settings = mContext.getSharedPreferences(mContext.getString(R.string.store_preferences), Context.MODE_PRIVATE);
        return settings.getLong(key, -1);
    }

    public static void putSharedLong(Context mContext, String key, long value) {
        SharedPreferences settings = mContext.getSharedPreferences(mContext.getString(R.string.store_preferences), Context.MODE_PRIVATE);
        settings.edit().putLong(key, value).commit();
    }

    public static String formatCurrency(String currency,Double amount) {
        DecimalFormat format = new DecimalFormat("##,##,##,###");
        format.setMaximumFractionDigits(0);
        format.setMinimumIntegerDigits(1);
        return currency + " " + format.format(amount);
    }

    public static String formatDate(DateTime date) {
        String m = "", d = "";
        if (date.getMonthOfYear() < 10)
            m = "0";
        if (date.getDayOfMonth() < 10)
            d = "0";
        return date.getYear() + "-" + m + date.getMonthOfYear() + "-" + d + date.getDayOfMonth();
    }

    public static String convertMemoryBytes(long byteValue, int iterations) {
        double sizeValue = (double) byteValue;
        for (int i = 0; i < iterations; i++) {
            sizeValue /= 1024.0;
        }
        String value = String.format("%.2f", sizeValue);

        switch (iterations) {
            case 0:
                value += " Bytes";
                break;
            case 1:
                value += " KB";
                break;
            case 2:
                value += " MB";
                break;
            case 3:
                value += " GB";
                break;
        }
        return value;
    }

    public static void setGridViewHeightBasedOnChildren(GridView gridView, int columns) {
        ListAdapter listAdapter = gridView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight;
        int items = listAdapter.getCount();
        int rows;

        View listItem = listAdapter.getView(0, null, gridView);
        listItem.measure(0, 0);
        totalHeight = listItem.getMeasuredHeight();

        if (items > columns) {
            rows = (items + 1) / columns;
            totalHeight *= rows;
        }

        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight;
        gridView.setLayoutParams(params);
    }

    public static void justifyListViewHeightBasedOnChildren(ListView listView) {

        ListAdapter adapter = listView.getAdapter();

        if (adapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams par = listView.getLayoutParams();
        par.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(par);
        listView.requestLayout();
    }

    public static void setOrderStatus(KeyValuePair status, TextView txtOrderStatus) {
        switch (status.getValue()) {
            case 1:
                txtOrderStatus.setBackgroundResource(R.drawable.grey_back);
                txtOrderStatus.setTextColor(ContextCompat.getColor(context, R.color.WHITE));
                break;
            case 4:
                txtOrderStatus.setBackgroundResource(R.drawable.green_back);
                txtOrderStatus.setTextColor(ContextCompat.getColor(context, R.color.WHITE));
                break;
            case 3:
            case 7:
            case 8:
                txtOrderStatus.setBackgroundResource(R.drawable.white_back);
                txtOrderStatus.setTextColor(ContextCompat.getColor(context, R.color.DARK_GRAY));
                break;
            case 5:
            case 9:
                txtOrderStatus.setBackgroundResource(R.drawable.red_back);
                txtOrderStatus.setTextColor(ContextCompat.getColor(context, R.color.WHITE));
                break;
        }
        txtOrderStatus.setText(status.getKey().toUpperCase());
    }

    public static String getPercentageString(double value, double max) {
        return String.format("%.0f", value * 100 / max) + "%";
    }

    public static KeyValuePair handleResponseError(Context mContext, VolleyError error) {
        KeyValuePair errorDetail = new KeyValuePair();
        String str, errorMsg = mContext.getString(R.string.something_went_wrong);
        if (error != null) {
            if (error.networkResponse != null) {
                errorDetail.setValue(error.networkResponse.statusCode);
                str = new String(error.networkResponse.data);
                if (!str.contains("[")) {
                    int index = str.indexOf(":");
                    str = str.substring(index + 2);
                    str = str.substring(0, str.length() - 2);
                    errorMsg = str;
                } else {
                    ECNResponse response = new Gson().fromJson(str, ECNResponse.class);
                    if (response != null) {
                        if (response.getError() != null && !response.getError().isEmpty()) {
                            errorMsg = response.getError().get(0);
                        }
                    }
                }
            } else {
                if (error instanceof TimeoutError) {
                    errorMsg = "Connection timed out";
                } else if (error instanceof NoConnectionError) {
                    errorMsg = "No internet connection";
                } else if (error instanceof AuthFailureError) {
                    errorMsg = "Authentication failed";
                } else if (error instanceof ServerError) {
                    errorMsg = "Server error";
                } else if (error instanceof NetworkError) {
                    errorMsg = "Network error";
                } else if (error instanceof ParseError) {
                    errorMsg = "Parsing error";
                }
            }
        }
        errorDetail.setKey(errorMsg);
        return errorDetail;
    }

    public static String processURL(String url) {
        if (!url.contains("http"))
            url = "http:" + url;
        return url;
    }

    public static List<DropdownClass> generate_dropdown(Product product) {
        List<KeyValuePair> options = product.getOptions();
        List<Variant> option_values = product.getVariants();
        List<DropdownClass> dropdowns = new ArrayList<>();
        DropdownClass drop;
        List<String> values;
        int count = 1;
        for (KeyValuePair kvp : options) {
            drop = new DropdownClass();
            drop.setLabel(kvp.getKey().toUpperCase());
            values = new ArrayList<>();

            for (Variant v : option_values) {
                switch (count) {
                    case 1:
                        if (!values.contains(v.getOption1()))
                            values.add(v.getOption1());
                        break;
                    case 2:
                        if (!values.contains(v.getOption2()))
                            values.add(v.getOption2());
                        break;
                    case 3:
                        if (!values.contains(v.getOption3()))
                            values.add(v.getOption3());
                        break;
                    case 4:
                        if (!values.contains(v.getOption4()))
                            values.add(v.getOption4());
                        break;
                    case 5:
                        if (!values.contains(v.getOption5()))
                            values.add(v.getOption5());
                        break;
                }
            }

            drop.setValues(values);
            dropdowns.add(drop);
            count++;
        }
        return dropdowns;
    }

    public static String getExVariantId(HashMap<String, String> variantsHash,String selection) {
        if (variantsHash != null)
            return variantsHash.get(selection);

        return null;
    }

    public static ExtendedOption getExOptionByName(List<ExtendedOption> list, String name) {
        if(list != null && !list.isEmpty()) {
            for (ExtendedOption op : list) {
                if (op.getName().equalsIgnoreCase(name))
                    return op;
            }
        }
        return null;
    }

    public static List<Vendor> getVariantVendors(List<Vendor> vendors, String varId) {
        List<Vendor> newList = new ArrayList<>();
        if(vendors != null && !vendors.isEmpty()) {
            for(Vendor v : vendors) {
                List<ExtendedVariant> variants = v.getVariants();
                if(variants != null && !variants.isEmpty()) {
                    for(ExtendedVariant var : variants) {
                        if(var.getId().equals(varId))
                            newList.add(v);
                    }
                }
            }
        }
        return newList;
    }

    public static HashMap<String, Long> generate_VariantsHash(List<Variant> list) {
        HashMap<String, Long> hash = new HashMap<>();
        String options = "";
        for (Variant var : list) {
            String str = var.getOption1();
            if (str != null)
                options += "," + str;

            str = var.getOption2();
            if (str != null)
                options += "," + str;

            str = var.getOption3();
            if (str != null)
                options += "," + str;

            str = var.getOption4();
            if (str != null)
                options += "," + str;

            str = var.getOption5();
            if (str != null)
                options += "," + str;

            if (!options.equals("")) {
                hash.put(options.toUpperCase(), var.getId());
            }
            options = "";
        }
        return hash;
    }

    public static HashMap<String, String> generate_ExtendedVariantsHash(List<ExtendedVariant> list) {
        HashMap<String, String> hash = new HashMap<>();
        String options = "";
        for (ExtendedVariant var : list) {
            String str = var.getOption1();
            if (str != null)
                options += str;

            str = var.getOption2();
            if (str != null)
                options += "," + str;

            str = var.getOption3();
            if (str != null)
                options += "," + str;

            if (!options.equals("")) {
                hash.put(options.toUpperCase(), var.getId());
            }
            options = "";
        }
        return hash;
    }

    public static boolean isUserLoggedIn(Context context) {
        return getSharedString(context, context.getString(R.string.access_token)) != null && !getSharedString(context, context.getString(R.string.access_token)).isEmpty();
    }

    public static String encode(String params) {
        try {
            params = URLEncoder.encode(params, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }

    public static void setPicassoBitMap(String url,final ImageView image, final ECNCallback callback) {
        try {
            int size = BitmapTransform.getSize();
            Picasso.with(image.getContext())
                    .load(processURL(url))
                    .transform(new BitmapTransform())
                    .resize(size, size)
                    .centerInside()
                    .into(image, new Callback() {
                        @Override
                        public void onSuccess() {
                            callback.onSuccess("");
                        }

                        @Override
                        public void onError() {
                            image.setImageResource(R.drawable.placeholder);
                            callback.onFailure(null);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DisplayMetrics getDisplayMetrics(Context mContext) {
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);

        return dm;
    }

    public static void hideKeyboard(Context ctx) {
        View v = ((Activity) ctx).getCurrentFocus();
        if (v != null) {
            InputMethodManager inputManager = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    public static List<DropdownClass> getTimberFruitVariants(Product product) {
        List<Variant> option_values = product.getVariants();
        List<DropdownClass> dropdowns = new ArrayList<>();
        DropdownClass drop;
        List<String> values, list;
        list = new ArrayList<>();

        for (Variant v : option_values) {
            if (!list.contains(v.getOption1().toLowerCase()))
                list.add(v.getOption1().toLowerCase());
        }

        for(String s : list) {
            drop = new DropdownClass();
            values = new ArrayList<>();
            for(Variant v: option_values) {
                if(s.equalsIgnoreCase(v.getOption1()))
                    values.add(v.getOption2());
            }
            drop.setLabel(s);
            drop.setValues(values);
            dropdowns.add(drop);
        }

        return dropdowns;
    }
}
