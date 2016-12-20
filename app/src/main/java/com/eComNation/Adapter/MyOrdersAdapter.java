package com.eComNation.Adapter;

/**
 * Created by Abhi on 14-12-2015.
 */

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.eComNation.R;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Data.Order;

import java.util.List;

public class MyOrdersAdapter extends ArrayAdapter<Order> {
    Context mContext;
    int layoutResourceId;
    List<Order> data = null;

    public MyOrdersAdapter(Context mContext, int layoutResourceId, List<Order> data) {
        super(mContext, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public View getView(int position, View listItem, ViewGroup parent) {
        listItem = ((Activity) mContext).getLayoutInflater().inflate(layoutResourceId, parent, false);

        TextView itemCount = (TextView) listItem.findViewById(R.id.itemcount);

        Order folder = data.get(position);

        ((TextView) listItem.findViewById(R.id.orderid)).setText("Order #" + folder.getNumber());
        ((TextView) listItem.findViewById(R.id.orderdatetime)).setText("PLACED ON " + HelperClass.formatDateOnly(folder.getCreated_at()).toUpperCase());
        ((TextView) listItem.findViewById(R.id.amount)).setText(HelperClass.formatCurrency(mContext.getString(R.string.currency),folder.getGrand_total()));

        HelperClass.setOrderStatus(folder.getOrder_status(), (TextView) listItem.findViewById(R.id.orderstatus));

        int count = folder.getTotal_items();
        String s = "";
        if (count > 1)
            s = "S";
        itemCount.setText(count + " ITEM" + s);
/*
        LinearLayout layout = (LinearLayout) listItem.findViewById(R.id.imageList);
        for (int i = 0; i < folder.getTotal_items(); i++) {
            String name = "tiny" + i;
            ImageView img = new ImageView(mContext);
            img.setImageResource(HelperClass.getDrawableId(mContext, name));
            layout.addView(img);
        }*/
        return listItem;
    }
}

