package com.eComNation.Common;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eComNation.R;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Data.Address;
import com.ecomnationmobile.library.Data.CustomDetail;
import com.ecomnationmobile.library.Data.OrderLineItem;
import com.ecomnationmobile.library.Data.Product;
import com.ecomnationmobile.library.Data.TestimonialItem;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Abhi on 31-01-2016.
 */
public class CustomLayoutInflater {

    public static View getPickAddressView(Context mContext, Address address) {
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View listItem = inflater.inflate(R.layout.pick_address_item, null);

        ((TextView) listItem.findViewById(R.id.txtName)).setText(HelperClass.formatName(address));
        ((TextView) listItem.findViewById(R.id.txtPhone)).setText(address.getPhone());
        ((TextView) listItem.findViewById(R.id.txtAddress)).setText(HelperClass.formatAddress(address));

        listItem.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return listItem;
    }

    public static View getAddressView(Context mContext, Address address) {
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View listItem = inflater.inflate(R.layout.address_item, null);

        ((TextView) listItem.findViewById(R.id.txtName)).setText(HelperClass.formatName(address));
        ((TextView) listItem.findViewById(R.id.txtPhone)).setText(address.getPhone());
        ((TextView) listItem.findViewById(R.id.txtAddress)).setText(HelperClass.formatAddress(address));

        listItem.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return listItem;
    }

    public static View getViewAddress(Context mContext, Address address) {
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View listItem = inflater.inflate(R.layout.view_address_item, null);

        ((TextView) listItem.findViewById(R.id.txtName)).setText(HelperClass.formatName(address));
        ((TextView) listItem.findViewById(R.id.txtPhone)).setText(address.getPhone());
        ((TextView) listItem.findViewById(R.id.txtAddress)).setText(HelperClass.formatAddress(address));

        listItem.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return listItem;
    }

    public static View getPaymentItem(Context mContext, String string) {
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View listItem = inflater.inflate(R.layout.payment_item, null);

        ((TextView) listItem.findViewById(R.id.txtTitle)).setText(string);

        return listItem;
    }

    public static View getCartItem(Context mContext, OrderLineItem item) {
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View listItem = inflater.inflate(R.layout.cart_product_item, null);

        ((TextView) listItem.findViewById(R.id.txtProductName)).setText(item.getName());
        ((TextView) listItem.findViewById(R.id.txtPrice)).setText(HelperClass.formatCurrency(mContext.getString(R.string.currency), item.getActual_price()));
        ((TextView) listItem.findViewById(R.id.txtSKU)).setText(String.format(mContext.getString(R.string.sku_value), item.getSku()));
        ((TextView) listItem.findViewById(R.id.txtQty)).setText(String.format(mContext.getString(R.string.integer), item.getQuantity()));
        if (item.getMax_quantity_possible() != null) {
            if (item.getMax_quantity_possible() == 0)
                ((TextView) listItem.findViewById(R.id.txtOptions)).setText(mContext.getString(R.string.out_of_stock));
            if (item.getMax_quantity_possible() > 0 && item.getMax_quantity_possible() < item.getQuantity())
                ((TextView) listItem.findViewById(R.id.txtOptions)).setText(String.format(mContext.getString(R.string.only_left), item.getMax_quantity_possible()));
        }

        ImageView image = (ImageView) listItem.findViewById(R.id.imgProduct);

        List<CustomDetail> list = item.getCustom_details();
        if(list != null && !list.isEmpty()) {
            String str = "";
            for (int i = 0; i < list.size(); i++) {
                str += list.get(i).getKey().toUpperCase();
                str += ": " + list.get(i).getValue();
                if (i != list.size() - 1)
                    str += "\n";
            }
            listItem.findViewById(R.id.txtCustomDetails).setVisibility(View.VISIBLE);
            ((TextView) listItem.findViewById(R.id.txtCustomDetails)).setText(str);
        }

        String url = item.getImage_url();
        if (url != null) {
            Picasso.with(mContext).load(HelperClass.processURL(url)).error(R.drawable.placeholder).into(image);
        } else {
            image.setImageResource(R.drawable.placeholder);
        }

        if (item.getDiscount() != 0) {
            TextView oldPrice = (TextView) listItem.findViewById(R.id.txtOldPrice);
            oldPrice.setText(HelperClass.formatCurrency(mContext.getString(R.string.currency), item.getActual_price()));
            oldPrice.setPaintFlags(oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            ((TextView) listItem.findViewById(R.id.txtDiscount)).setText(String.format(mContext.getString(R.string.percent_off), item.getDiscount()));
        }
        return listItem;
    }

    public static View getCustomItem(Context mContext, String title, double amount) {
        final View listItem = ((Activity) mContext).getLayoutInflater().inflate(R.layout.custom_item, null);

        int drawableId = HelperClass.getDrawableId(mContext, title.toLowerCase());
        if (drawableId != 0)
            ((ImageView) listItem.findViewById(R.id.image)).setImageResource(drawableId);
        ((TextView) listItem.findViewById(R.id.name)).setText(title);
        ((TextView) listItem.findViewById(R.id.price)).setText(HelperClass.formatCurrency(mContext.getString(R.string.currency), amount));

        return listItem;
    }
}
