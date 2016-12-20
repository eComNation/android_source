package com.eComNation.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.eComNation.Activity.UpdateAddressActivity;
import com.eComNation.Common.CustomLayoutInflater;
import com.eComNation.R;
import com.ecomnationmobile.library.Control.ProgressDialogView;
import com.ecomnationmobile.library.Data.Address;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Abhi on 23-12-2015.
 */
public class ShippingFragment extends Fragment {

    View mView;
    ProgressDialog dialog;
    List<Address> mAddresses;
    LinearLayout addressList, shippingList, billAddressList, selectBillAddress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.shipping_detail, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mView = getView();

        if (mView != null) {
            dialog = new ProgressDialogView(getActivity(),"Please wait...",R.drawable.progress);

            selectBillAddress = (LinearLayout) mView.findViewById(R.id.selectBillAddress);
            ((TextView) mView.findViewById(R.id.txtBillingAddress)).setText("Billing address same as above");
            LinearLayout billing = (LinearLayout) mView.findViewById(R.id.billingAddress);
            ((ToggleButton) billing.findViewById(R.id.pickButton)).setChecked(true);

            billing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.show();
                    final ToggleButton b = ((ToggleButton) v.findViewById(R.id.pickButton));
                    b.toggle();
                    new android.os.Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (b.isChecked())
                                selectBillAddress.setVisibility(View.GONE);
                            else
                                selectBillAddress.setVisibility(View.VISIBLE);
                            dialog.dismiss();
                        }
                    }, 1000);
                }
            });

            mView.findViewById(R.id.txtAdd).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intentMain = new Intent(getActivity(), UpdateAddressActivity.class);
                    intentMain.putExtra("is_new", true);
                    startActivity(intentMain);
                }
            });

            mView.findViewById(R.id.txtAddBill).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intentMain = new Intent(getActivity(), UpdateAddressActivity.class);
                    intentMain.putExtra("is_new", true);
                    startActivity(intentMain);
                }
            });

            addressList = (LinearLayout) mView.findViewById(R.id.addressList);

            if (mAddresses != null) {
                for (int i = 0; i < mAddresses.size(); i++) {
                    View view = CustomLayoutInflater.getPickAddressView(getActivity(), mAddresses.get(i));
                    View view2 = view.findViewById(R.id.click);
                    view2.setTag(i);
                    view2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int position = (int) v.getTag();
                            unCheckAll(addressList);
                            ((ToggleButton) v.findViewById(R.id.pickButton)).setChecked(true);
                        }
                    });
                    addressList.addView(view);
                }
            }
        }

        checkFirst(addressList);

        List<String> shippingTypes = new ArrayList<>();
        shippingTypes.add("Standard Shipping (FREE)");
        shippingTypes.add("Express Shipping (+Rs.50)");

        shippingList = (LinearLayout) mView.findViewById(R.id.shippingList);

        for (int i = 0; i < shippingTypes.size(); i++) {
            View view = CustomLayoutInflater.getPaymentItem(getActivity(), shippingTypes.get(i));
            View view2 = view.findViewById(R.id.click);
            view2.setTag(i);
            view2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    unCheckAll(shippingList);
                    ((ToggleButton) v.findViewById(R.id.pickButton)).setChecked(true);
                }
            });
            shippingList.addView(view);
        }

        checkFirst(shippingList);
    }

    private void checkFirst(LinearLayout layout) {
        int count = layout.getChildCount();
        if (count > 0) {
            View v = layout.getChildAt(0);
            ((ToggleButton) v.findViewById(R.id.pickButton)).setChecked(true);
        }
    }

    private void unCheckAll(LinearLayout layout) {
        int count = layout.getChildCount();

        for (int i = 0; i < count; i++) {
            View v = layout.getChildAt(i);
            ((ToggleButton) v.findViewById(R.id.pickButton)).setChecked(false);
        }
    }

}
