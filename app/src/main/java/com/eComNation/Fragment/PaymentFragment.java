package com.eComNation.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.eComNation.Common.CustomLayoutInflater;
import com.eComNation.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Abhi on 23-12-2015.
 */
public class PaymentFragment extends Fragment {

    View mView;
    LinearLayout paymentList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.payment_detail, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mView = getView();

        if(mView != null) {List<String> paymentTypes = new ArrayList<>();
            paymentTypes.add("Cash On Delivery");
            paymentTypes.add("Online Payment");

            paymentList = (LinearLayout) mView.findViewById(R.id.paymentList);

            for (int i = 0; i < paymentTypes.size(); i++) {
                View view = CustomLayoutInflater.getPaymentItem(getActivity(), paymentTypes.get(i));
                View view2 = view.findViewById(R.id.click);
                view2.setTag(i);
                view2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = (int) v.getTag();
                        unCheckAll(paymentList);
                        ((ToggleButton) v.findViewById(R.id.pickButton)).setChecked(true);
                    }
                });
                paymentList.addView(view);
            }
        }
    }

    private void unCheckAll(LinearLayout layout) {
        int count = layout.getChildCount();

        for(int i = 0; i< count; i++) {
            View v = layout.getChildAt(i);
            ((ToggleButton) v.findViewById(R.id.pickButton)).setChecked(false);
        }
    }
}