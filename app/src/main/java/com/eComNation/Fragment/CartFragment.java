package com.eComNation.Fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eComNation.Activity.CheckOutActivity;
import com.eComNation.Common.CustomLayoutInflater;
import com.eComNation.Common.Utility;
import com.eComNation.R;
import com.ecomnationmobile.library.Common.ECNCallback;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Control.AlertDialogView;
import com.ecomnationmobile.library.Control.ProgressDialogView;
import com.ecomnationmobile.library.Data.Cart;
import com.ecomnationmobile.library.Data.CustomDetail;
import com.ecomnationmobile.library.Data.DiscountCoupon;
import com.ecomnationmobile.library.Data.ECNResponse;
import com.ecomnationmobile.library.Data.KeyValuePair;
import com.ecomnationmobile.library.Data.OrderLineItem;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Abhi on 23-12-2015.
 */
public class CartFragment extends Fragment {

    ProgressDialog dialog;
    JSONArray items;
    View mView, emptyView, cartView, couponLayout;
    List<OrderLineItem> orderItems;
    LinearLayout orderList;
    EditText couponText, rewardText;
    boolean canResume, canAdd, canRemove;
    List<DiscountCoupon> couponList;
    TextView coupon, rewardPoints;
    int count;
    boolean discount_applied, giftcard_applied;
    Cart responseCart, cart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.cart_layout, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (canResume) {
            getCart();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        canResume = false;
        canRemove = true;
        canAdd = false;

        dialog = new ProgressDialogView(getActivity(), "Updating...", R.drawable.progress);

        mView = getView();
        if (mView != null) {
            couponLayout = mView.findViewById(R.id.couponLayout);
            emptyView = mView.findViewById(R.id.emptyView);
            emptyView.findViewById(R.id.btnRetry).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getCart();
                }
            });
            cartView = mView.findViewById(R.id.scrollView);

            couponText = (EditText) mView.findViewById(R.id.couponText);
            rewardText = (EditText) mView.findViewById(R.id.rewardText);
            rewardText.setCursorVisible(true);
            couponText.setCursorVisible(true);
            coupon = (TextView) mView.findViewById(R.id.applyCoupon);
            rewardPoints = (TextView) mView.findViewById(R.id.applyReward);

            String couponString = HelperClass.getSharedString(getActivity(), "discount_coupons");
            if (couponString != null)
                couponList = (new Gson().fromJson(couponString, ECNResponse.class)).getDiscount_coupons();

            coupon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    applyCoupon();
                }
            });

            String content = HelperClass.getSharedString(getActivity(), getString(R.string.cart));
            if (content != null)
                cart = (new Gson()).fromJson(content, Cart.class);

            cartView.setVisibility(View.GONE);
            if (cart != null) {
                if (cart.getItems() != null && !cart.getItems().isEmpty()) {
                    emptyView.setVisibility(View.GONE);
                    if (getArguments() != null)
                        getCart();
                    else
                        displayData();
                    return;
                }
            }
            showEmpty();
        }
    }

    private void showEmpty() {
        emptyView.setVisibility(View.VISIBLE);
        emptyView.findViewById(R.id.btnRetry).setVisibility(View.GONE);
        ((TextView) emptyView.findViewById(R.id.txtMessage)).setText(getString(R.string.empty_cart));
        ((CheckOutActivity) getActivity()).updateCartCount(0, false);
    }

    private void getCart() {
        if (cart != null) {
            if (cart.getToken() != null) {
                dialog.show();
                String url = getString(R.string.production_base_url) + getString(R.string.api);
                url += "store/cart/" + cart.getToken() + "/get";
                url = Utility.checkAccess(getActivity(),url, "?");

                HelperClass.getData(getActivity(), url, new ECNCallback() {
                    @Override
                    public void onSuccess(String result) {
                        setCart(result);
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(KeyValuePair error) {
                        if (error.getValue() == 401) {
                            error.setKey(getString(R.string.logged_out_error));
                            Utility.getAccessToken(getActivity());
                        }
                        displayError(error.getKey());
                    }
                });
            }
        }
    }

    private void displayError(String error) {
        dialog.dismiss();
        emptyView.setVisibility(View.VISIBLE);
        ((TextView) emptyView.findViewById(R.id.txtMessage)).setText(error);
        emptyView.findViewById(R.id.btnRetry).setVisibility(View.VISIBLE);
        ((CheckOutActivity) getActivity()).updateCartCount(0, false);
    }

    private void displayData() {
        orderItems = cart.getItems();
        orderList = (LinearLayout) mView.findViewById(R.id.listView);
        orderList.removeAllViews();

        count = 0;
        couponLayout.setVisibility(View.VISIBLE);

        if (orderItems != null && !orderItems.isEmpty()) {
            cartView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            double total = 0, discountedTotal, rewardAmount = 0;
            boolean val = false;
            for (int i = 0; i < orderItems.size(); i++) {
                OrderLineItem o = orderItems.get(i);
                final View v = CustomLayoutInflater.getCartItem(getActivity(), o);
                v.setTag(i);
                v.findViewById(R.id.txtRemove).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (canRemove) {
                            final AlertDialogView adv = new AlertDialogView(getActivity());
                            adv.initColor(R.color.PrimaryColor, R.color.SecondaryColor, R.drawable.store_logo);
                            adv.initText("Remove Product", "Are you sure you want to remove this product?", AlertDialogView.CONFIRM);
                            adv.getYesButton().setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view1) {
                                    int index = (int) v.getTag();
                                    deleteItem(orderItems.get(index).getVariant_id());
                                    adv.dismissAlertDialog();
                                }
                            });
                            adv.getNoButton().setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view1) {
                                    adv.dismissAlertDialog();
                                }
                            });
                            adv.showAlertDialog();
                        }
                    }
                });

                v.findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int index = (int) v.getTag();
                        int qty = orderItems.get(index).getQuantity();
                        qty++;
                        orderItems.get(index).setQuantity(qty);
                        ((TextView) v.findViewById(R.id.txtQty)).setText(String.format(getString(R.string.integer), qty));
                        setUpdate(index);
                    }
                });
                v.findViewById(R.id.btnSubtract).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int index = (int) v.getTag();
                        int qty = orderItems.get(index).getQuantity();
                        if (qty > 1) {
                            qty--;
                            orderItems.get(index).setQuantity(qty);
                            ((TextView) v.findViewById(R.id.txtQty)).setText(String.format(getString(R.string.integer), qty));
                            setUpdate(index);
                        }
                    }
                });
                orderList.addView(v);
                total += o.getActual_price();
                if (o.getMax_quantity_possible() != null)
                    val = val || ((o.getMax_quantity_possible() == 0) || (o.getMax_quantity_possible() > 0 && o.getMax_quantity_possible() < o.getQuantity()));
            }
            ((CheckOutActivity) getActivity()).setCheckout(!val);

            discountedTotal = cart.getDiscounted_cart_amount();

            if (cart.getDiscount_coupon_id() != 0) {
                setUpDiscountCoupon(getCouponCode(cart.getDiscount_coupon_id()));
                count++;
                if (discount_applied) {
                    Toast.makeText(getActivity(), "Discount coupon applied.", Toast.LENGTH_LONG).show();
                    discount_applied = false;
                }
            } else {
                discount_applied = true;
                ((TextView) mView.findViewById(R.id.lblDiscountCode)).setText("");
                mView.findViewById(R.id.removeDiscount).setVisibility(View.INVISIBLE);
            }

            if (cart.getGift_card_amount() != 0) {
                setUpGiftCard();
                mView.findViewById(R.id.giftCardBlock).setVisibility(View.VISIBLE);
                count++;
                if (giftcard_applied) {
                    Toast.makeText(getActivity(), "Gift card applied.", Toast.LENGTH_LONG).show();
                    giftcard_applied = false;
                }
            } else {
                giftcard_applied = true;
                mView.findViewById(R.id.giftCardBlock).setVisibility(View.GONE);
            }

            if (cart.getAvailable_reward_points() != 0 && cart.isGroup_not_excluded()) {
                mView.findViewById(R.id.rewardPointsBlock).setVisibility(View.VISIBLE);

                if (cart.getPoints_per_unit_amount() != 0)
                    rewardAmount = cart.getAvailable_reward_points() / cart.getPoints_per_unit_amount();

                if (cart.getReward_points() != 0) {
                    mView.findViewById(R.id.rewardPointsBlock).setVisibility(View.GONE);
                    mView.findViewById(R.id.rewardBlock).setVisibility(View.VISIBLE);
                    mView.findViewById(R.id.removeReward).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            removeRewardPoints();
                        }
                    });
                } else {
                    mView.findViewById(R.id.rewardBlock).setVisibility(View.GONE);
                    ((TextView) mView.findViewById(R.id.txtRewardPoints)).setText(String.format(getString(R.string.integer), cart.getAvailable_reward_points()));
                    ((TextView) mView.findViewById(R.id.txtRewardValue)).setText(String.format(getString(R.string.integer), Math.round(rewardAmount)));
                    rewardText.setText("");
                    rewardPoints.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            applyRewardPoints();
                        }
                    });
                    rewardAmount = 0;
                }
            } else {
                mView.findViewById(R.id.rewardPointsBlock).setVisibility(View.GONE);
                mView.findViewById(R.id.rewardBlock).setVisibility(View.GONE);
            }

            double discount = total - discountedTotal;
            discount -= rewardAmount;

            discountedTotal -= cart.getGift_card_amount();

            ((TextView) mView.findViewById(R.id.txtSubtotal)).setText(HelperClass.formatCurrency(getString(R.string.currency), total));
            ((TextView) mView.findViewById(R.id.txtDiscountValue)).setText(HelperClass.formatCurrency(getString(R.string.currency), discount));
            ((TextView) mView.findViewById(R.id.txtGiftCardValue)).setText(HelperClass.formatCurrency(getString(R.string.currency), cart.getGift_card_amount()));
            ((TextView) mView.findViewById(R.id.txtRewardPointsValue)).setText(HelperClass.formatCurrency(getString(R.string.currency), rewardAmount));
            ((TextView) mView.findViewById(R.id.txtTotal)).setText(HelperClass.formatCurrency(getString(R.string.currency), discountedTotal));

            int n = HelperClass.getCartCount(getActivity());
            ((CheckOutActivity) getActivity()).updateCartCount(n, true);

            if (count == 2)
                couponLayout.setVisibility(View.GONE);

        } else {
            cartView.setVisibility(View.GONE);
            showEmpty();
        }
    }

    private void setUpdate(final int index) {
        canRemove = false;
        ((CheckOutActivity) getActivity()).disableCheckout();
        canAdd = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (canAdd)
                    updateQuantity(orderItems.get(index).getVariant_id(), orderItems.get(index).getQuantity());
                canAdd = false;
            }
        }, 2000);
    }

    private void updateQuantity(long id, int qty) {
        dialog.show();
        String url = getString(R.string.production_base_url) + getString(R.string.api);
        url += "store/cart/" + cart.getToken() + "/update_quantity?";
        url += "id=" + id;
        url += "&quantity=" + qty;
        url = Utility.checkAccess(getActivity(),url, "&");
        HelperClass.patchData(getActivity(), url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                setCart(result);
                dialog.dismiss();
                canRemove = true;
                ((CheckOutActivity) getActivity()).enableCheckout();
            }

            @Override
            public void onFailure(KeyValuePair error) {
                dialog.dismiss();
                canRemove = true;
                ((CheckOutActivity) getActivity()).enableCheckout();
                Toast.makeText(getActivity(), error.getKey(), Toast.LENGTH_SHORT).show();
                resetCart();
            }
        });
    }

    private void deleteItem(long id) {
        dialog.show();
        String url = getString(R.string.production_base_url) + getString(R.string.api);
        url += "store/cart/" + cart.getToken() + "/delete?";
        url += "items[][id]=" + id;
        url = Utility.checkAccess(getActivity(),url, "&");
        HelperClass.deleteData(getActivity(), url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                setCart(result);
                dialog.dismiss();
            }

            @Override
            public void onFailure(KeyValuePair error) {
                dialog.dismiss();
                Toast.makeText(getActivity(), error.getKey(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyCoupon() {
        if (couponText.getText().toString().equals("")) {
            Toast.makeText(getActivity(), "Code cannot be blank", Toast.LENGTH_LONG).show();
            return;
        }
        HelperClass.hideKeyboard(getActivity());
        dialog.show();
        String url = getString(R.string.production_base_url) + getString(R.string.api);
        url += "store/cart/" + cart.getToken() + "/apply_discount_coupon?";
        url += "discount_coupon_code=" + couponText.getText();
        url = Utility.checkAccess(getActivity(),url, "&");
        HelperClass.patchData(getActivity(), url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                setCart(result);
                couponText.setText("");
                dialog.dismiss();
            }

            @Override
            public void onFailure(KeyValuePair error) {
                dialog.dismiss();
                Toast.makeText(getActivity(), error.getKey(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeCoupon() {
        dialog.show();
        String url = getString(R.string.production_base_url) + getString(R.string.api);
        url += "store/cart/" + cart.getToken() + "/remove_discount_coupon";
        url = Utility.checkAccess(getActivity(),url, "?");
        HelperClass.patchData(getActivity(), url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                setCart(result);
                dialog.dismiss();
                Toast.makeText(getActivity(), "Discount coupon removed.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(KeyValuePair error) {
                dialog.dismiss();
                Toast.makeText(getActivity(), error.getKey(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeGiftCard() {
        dialog.show();
        String url = getString(R.string.production_base_url) + getString(R.string.api);
        url += "store/cart/" + cart.getToken() + "/remove_gift_card";
        url = Utility.checkAccess(getActivity(),url, "?");
        HelperClass.patchData(getActivity(), url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                setCart(result);
                dialog.dismiss();
                Toast.makeText(getActivity(), "Gift card removed.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(KeyValuePair error) {
                dialog.dismiss();
                Toast.makeText(getActivity(), error.getKey(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyRewardPoints() {
        if (rewardText.getText().toString().equals("")) {
            Toast.makeText(getActivity(), "Please enter a valid reward points value.", Toast.LENGTH_LONG).show();
            return;
        }
        HelperClass.hideKeyboard(getActivity());
        dialog.show();
        String url = getString(R.string.production_base_url) + getString(R.string.api);
        url += "store/cart/" + cart.getToken() + "/apply_reward_points?";
        url += "reward_points=" + rewardText.getText();
        url = Utility.checkAccess(getActivity(),url, "&");
        HelperClass.patchData(getActivity(), url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                setCart(result);
                couponText.setText("");
                dialog.dismiss();
                Toast.makeText(getActivity(), "Reward points applied.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(KeyValuePair error) {
                dialog.dismiss();
                Toast.makeText(getActivity(), error.getKey(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeRewardPoints() {
        dialog.show();
        String url = getString(R.string.production_base_url) + getString(R.string.api);
        url += "store/cart/" + cart.getToken() + "/remove_reward_points";
        url = Utility.checkAccess(getActivity(),url, "?");
        HelperClass.patchData(getActivity(), url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                setCart(result);
                dialog.dismiss();
                Toast.makeText(getActivity(), "Reward points removed.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(KeyValuePair error) {
                dialog.dismiss();
                Toast.makeText(getActivity(), error.getKey(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void copyCarts() {
        if (responseCart != null) {
            cart = responseCart;
            HelperClass.putSharedString(getActivity(), getString(R.string.cart), new Gson().toJson(cart));
            HelperClass.putSharedString(getActivity(), getString(R.string.old_cart), new Gson().toJson(cart));

            responseCart = null;
        }
    }

    private String getCouponCode(long id) {
        if (couponList != null && !couponList.isEmpty()) {
            for (DiscountCoupon dc : couponList) {
                if (dc.getId() == id)
                    return dc.getCode();
            }
        }
        return "";
    }

    private void setCart(String result) {
        try {
            JSONObject obj = new JSONObject(result);
            JSONObject cartObject = (JSONObject) obj.get(getString(R.string.cart));
            items = cartObject.getJSONArray("items");
            responseCart = (new Gson()).fromJson(cartObject.toString(), Cart.class);
            if (items != null && items.length() > 0) {
                for (int i = 0; i < items.length(); i++) {
                    JSONObject jo = items.getJSONObject(i);
                    if(!jo.isNull("custom_details")) {
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
            copyCarts();
            displayData();
            canResume = true;
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void resetCart() {
        String content = HelperClass.getSharedString(getActivity(), getString(R.string.old_cart));
        if (content != null) {
            cart = (new Gson()).fromJson(content, Cart.class);
        } else {
            cart = new Cart();
            cart.setItems(new ArrayList<OrderLineItem>());
        }
        displayData();
        HelperClass.putSharedString(getActivity(), getString(R.string.cart), new Gson().toJson(cart));
    }

    private void setUpDiscountCoupon(String coupon) {
        ((TextView) mView.findViewById(R.id.lblDiscountCode)).setText("{" + coupon + "}");
        mView.findViewById(R.id.removeDiscount).setVisibility(View.VISIBLE);
        mView.findViewById(R.id.removeDiscount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canRemove)
                    removeCoupon();
            }
        });
    }

    private void setUpGiftCard() {
        mView.findViewById(R.id.removeGiftCard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canRemove)
                    removeGiftCard();
            }
        });
    }
}
