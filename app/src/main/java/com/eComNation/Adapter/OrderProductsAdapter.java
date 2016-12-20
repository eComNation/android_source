package com.eComNation.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eComNation.R;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Data.OrderLineItem;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Abhi on 20-09-2015.
 */
public class OrderProductsAdapter extends ArrayAdapter<OrderLineItem> {
    Context mContext;
    int layoutResourceId;
    List<OrderLineItem> data = null;

    public OrderProductsAdapter(Context mContext, int layoutResourceId, List<OrderLineItem> data) {
        super(mContext, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public View getView(int position, View listItem, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        listItem = inflater.inflate(layoutResourceId, parent, false);

        OrderLineItem item = data.get(position);

        ((TextView) listItem.findViewById(R.id.txtProductName)).setText(item.getName());
        ((TextView) listItem.findViewById(R.id.txtPrice)).setText(HelperClass.formatCurrency(mContext.getString(R.string.currency),item.getDiscounted_price()));
        ((TextView) listItem.findViewById(R.id.txtSKU)).setText("SKU: "+item.getSku());

        if(item.getDiscounted_price() < item.getActual_price()) {
            TextView oldPrice = (TextView) listItem.findViewById(R.id.txtOldPrice);
            oldPrice.setText(HelperClass.formatCurrency(mContext.getString(R.string.currency),item.getActual_price()));
            oldPrice.setPaintFlags(oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            double discount = item.getActual_price() - item.getDiscounted_price();
            discount = discount/item.getActual_price()*100;
            discount = Math.round(discount);
            ((TextView) listItem.findViewById(R.id.txtDiscount)).setText(discount+ "% OFF");
        }
        ImageView image = (ImageView) listItem.findViewById(R.id.imgProduct);

        String url = item.getImage_url();
        if (url != null) {
            url = url.replace("stage", "prod");
            Picasso.with(mContext).load("http:"+url).error(R.drawable.placeholder).into(image);
        } else {
            image.setImageResource(R.drawable.placeholder);
        }

        return listItem;
    }
}
