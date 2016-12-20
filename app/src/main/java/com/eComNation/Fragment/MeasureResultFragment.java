package com.eComNation.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eComNation.Activity.CheckOutActivity;
import com.eComNation.R;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Data.Cart;
import com.ecomnationmobile.library.Data.OrderLineItem;
import com.ecomnationmobile.library.Data.Product;
import com.ecomnationmobile.library.Data.Variant;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by User on 6/6/2016.
 */
public class MeasureResultFragment extends Fragment {

    View mView;
    Product product;
    int quantity;
    Variant selectedVariant;
    HashMap<String, Long> variantHash;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.measure_result_layout, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mView = getView();

        if (mView != null) {
            String content = HelperClass.getSharedString(getActivity(),"product");
            product = (new Gson()).fromJson(content,Product.class);

            variantHash = HelperClass.generate_VariantsHash(product.getVariants());

            mView.findViewById(R.id.btnNext).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String content = HelperClass.getSharedString(getActivity(), getString(R.string.cart));
                    Cart cart = (new Gson()).fromJson(content, Cart.class);

                    if (cart == null) {
                        cart = new Cart();
                        cart.setItems(new ArrayList<OrderLineItem>());
                    }

                    int index = alreadyExists(cart.getItems(), selectedVariant.getId());
                    if (index == -1) {
                        OrderLineItem item = new OrderLineItem();
                        item.setVariant_id(selectedVariant.getId());
                        item.setActual_price(selectedVariant.getPrevious_price());
                        item.setDiscounted_price(selectedVariant.getPrice());
                        item.setQuantity(quantity);
                        item.setSku(selectedVariant.getSku());
                        item.setName(product.getName());
                        if (!product.getImages().isEmpty())
                            item.setImage_url(product.getImages().get(0).getUrl());
                        cart.getItems().add(item);
                    } else {
                        OrderLineItem orderLineItem = cart.getItems().get(index);
                        orderLineItem.setQuantity(orderLineItem.getQuantity() + quantity);
                        cart.getItems().remove(index);
                        cart.getItems().add(index, orderLineItem);
                    }
                    cart.setUpdate_id(index + 10);

                    HelperClass.putSharedString(getActivity(), getString(R.string.cart), (new Gson()).toJson(cart));

                    Intent intentMain = new Intent(getActivity(), CheckOutActivity.class);
                    getActivity().finish();
                    startActivity(intentMain);
                }
            });

            long id = variantHash.get(HelperClass.getSharedString(getActivity(), "variant_hash"));
            selectedVariant = getVariant(product.getVariants(),id);
            if(selectedVariant != null) {
                ((TextView) mView.findViewById(R.id.productName)).setText(product.getName());
                quantity = 1;
                setQuantity();

                mView.findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        quantity++;
                        setQuantity();
                    }
                });
                mView.findViewById(R.id.btnSubtract).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (quantity > 1) {
                            quantity--;
                            setQuantity();
                        }
                    }
                });
            }
            else
                ((TextView) mView.findViewById(R.id.productName)).setText(getString(R.string.no_products));
        }
    }

    private void setQuantity() {
        ((TextView) mView.findViewById(R.id.txtQty)).setText(String.format(getString(R.string.integer), quantity));
    }


    private Variant getVariant(List<Variant> list,long varId) {
        if(list != null && !list.isEmpty()) {
            for (Variant v : list) {
                if (varId == v.getId()) {
                    return v;
                }
            }
        }
        return null;
    }

    private int alreadyExists(List<OrderLineItem> list, long varId) {
        if(list != null && !list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getVariant_id() == varId)
                    return i;
            }
        }
        return -1;
    }
}
