package com.eComNation.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.eComNation.Adapter.OrderProductsAdapter;
import com.eComNation.R;
import com.ecomnationmobile.library.Common.JSON;
import com.ecomnationmobile.library.Data.Order;
import com.ecomnationmobile.library.Data.OrderLineItem;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by Abhi on 26-12-2015.
 */
public class OrderDetailsFragment extends Fragment {


    ListView orderList;
    OrderProductsAdapter adapter;
    List<OrderLineItem> orderItems;
    Order ORDER;
    View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.order_items_detail, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mView = getView();
        if(mView != null)
            displayData();

    }

    private void displayData() {
        String content = JSON.Read(getActivity(), "oneorder.j39shops");

        ORDER = (new Gson()).fromJson(content, Order.class);
    }
}
