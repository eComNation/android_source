package com.eComNation.Activity;

import android.app.ProgressDialog;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eComNation.Common.CustomLayoutInflater;
import com.eComNation.Common.Utility;
import com.eComNation.R;
import com.ecomnationmobile.library.Common.ECNCallback;
import com.ecomnationmobile.library.Common.EComNationActivity;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Control.ProgressDialogView;
import com.ecomnationmobile.library.Data.KeyValuePair;
import com.ecomnationmobile.library.Data.Order;
import com.ecomnationmobile.library.Data.OrderLineItem;
import com.ecomnationmobile.library.Database.DatabaseManager;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by Abhi on 30-08-2015.
 */
public class OrderDetailsActivity extends EComNationActivity {

    public static long ID;
    LinearLayout orderList;
    List<OrderLineItem> orderItems;
    Bundle mBundle;
    Order order;
    View progressBar, emptyView;
    ProgressDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_detail);

        DatabaseManager.init(this);

        dialog = new ProgressDialogView(this, "Updating...", R.drawable.progress);

        progressBar = findViewById(R.id.progressBar);
        emptyView = findViewById(R.id.empty_View);
        emptyView.findViewById(R.id.btnRetry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });

        mBundle = getIntent().getExtras();

        ID = mBundle.getLong("id");


        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        getData();
    }

    private void displayData() {
        ((TextView) findViewById(R.id.txtHeader)).setText("Order #" + order.getNumber());
        ((TextView) findViewById(R.id.orderDateTime)).setText("PLACED ON " + HelperClass.formatDateOnly(order.getCreated_at()).toUpperCase());

        HelperClass.setOrderStatus(order.getOrder_status(), (TextView) findViewById(R.id.orderStatus));

        orderItems = order.getOrder_line_items();

        orderList = (LinearLayout) findViewById(R.id.listView);

        orderList.addView(CustomLayoutInflater.getViewAddress(this, order.getShipping_address()));

        for(OrderLineItem item: orderItems) {
            orderList.addView(getView(item));
        }

        ((TextView) findViewById(R.id.txtSubtotal)).setText(HelperClass.formatCurrency(getString(R.string.currency),order.getActual_amount()));
        ((TextView) findViewById(R.id.txt_Discount)).setText(HelperClass.formatCurrency(getString(R.string.currency),order.getDiscount()));
        ((TextView) findViewById(R.id.txtTaxes)).setText(HelperClass.formatCurrency(getString(R.string.currency),order.getTax_amount()));
        ((TextView) findViewById(R.id.txtShipping)).setText(HelperClass.formatCurrency(getString(R.string.currency),order.getShipping_charge()));
        ((TextView) findViewById(R.id.txtTotal)).setText(HelperClass.formatCurrency(getString(R.string.currency),order.getGrand_total()));

        progressBar.setVisibility(View.GONE);
    }

    public View getView(OrderLineItem item) {
        View listItem;

        LayoutInflater inflater = getLayoutInflater();
        listItem = inflater.inflate(R.layout.order_products_item, null);

        ((TextView) listItem.findViewById(R.id.txtProductName)).setText(item.getName());
        ((TextView) listItem.findViewById(R.id.txtPrice)).setText(HelperClass.formatCurrency(getString(R.string.currency),item.getActual_price()));
        ((TextView) listItem.findViewById(R.id.txtSKU)).setText(String.format(getString(R.string.sku_value), item.getSku()));
        ((TextView) listItem.findViewById(R.id.txtQty)).setText(String.format(getString(R.string.integer), item.getQuantity()));

        if(item.getDiscounted_price() < item.getActual_price()) {
            TextView oldPrice = (TextView) listItem.findViewById(R.id.txtOldPrice);
            oldPrice.setText(HelperClass.formatCurrency(getString(R.string.currency),item.getActual_price()));
            oldPrice.setPaintFlags(oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            ((TextView) listItem.findViewById(R.id.txtDiscount)).setText(String.format(getString(R.string.percent_off),item.getDiscount()));
        }
        ImageView image = (ImageView) listItem.findViewById(R.id.imgProduct);

        String url = item.getImage_url();
        if (url != null) {
            Picasso.with(this).load(HelperClass.processURL(url)).error(R.drawable.placeholder).into(image);
        } else {
            image.setImageResource(R.drawable.placeholder);
        }

        return listItem;
    }

    public void getData() {
        progressBar.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        String url = getString(R.string.production_base_url) + getString(R.string.api) + "store/customer/orders/" + ID + "?access_token=" + HelperClass.getSharedString(this, getString(R.string.access_token));

        HelperClass.getData(this, url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);

                    JSONObject orderObject = (JSONObject) obj.get("order");
                    String orderString = orderObject.toString();

                    order = (new Gson()).fromJson(orderString, Order.class);

                    displayData();

                } catch (Exception e) {
                    Toast.makeText(OrderDetailsActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                } finally {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void
            onFailure(KeyValuePair error) {
                order = null;
                if (error.getValue() == 401) {
                    error.setKey(getString(R.string.logged_out_error));
                    Utility.getAccessToken(OrderDetailsActivity.this);
                }
                displayError(error.getKey());
            }
        });
    }

    private void displayError(String error) {
        emptyView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        ((TextView)emptyView.findViewById(R.id.txtMessage)).setText(error);
    }
}


