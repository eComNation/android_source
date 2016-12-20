package com.eComNation.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.eComNation.Fragment.CartFragment;
import com.eComNation.Fragment.NewCheckoutFragment;
import com.eComNation.Fragment.PaymentFragment;
import com.eComNation.R;
import com.ecomnationmobile.library.Common.HelperClass;

/**
 * Created by Abhi on 23-12-2015.
 */
public class CheckOutActivity extends FragmentActivity {

    TextView txtAction;
    TextView txtCart, txtShipping, txtPayment;
    int position;
    View backButton;
    boolean is_back_allowed, checkout_allowed;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart_checkout);

        txtAction = (TextView) findViewById(R.id.txtAction);
        txtCart = (TextView) findViewById(R.id.txtCart);
        txtShipping = (TextView) findViewById(R.id.txtShipping);
        txtPayment = (TextView) findViewById(R.id.txtPayment);
        backButton =findViewById(R.id.btnBack);

        is_back_allowed = true;
        checkout_allowed = true;

        txtAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position += 1;
                onForwardSelection();
            }
        });

        position = 1;
        onForwardSelection();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (HelperClass.getSharedBoolean(this, "open_checkout")) {
            position = 2;
            onForwardSelection();
            HelperClass.putSharedBoolean(this, "open_checkout", false);
        }
    }

    @Override
    public void onBackPressed() {
        if(backButton.isEnabled()) {
            if (is_back_allowed) {
                position -= 1;
                onBackwardSelection();
            } else {
                Intent intentMain = new Intent(CheckOutActivity.this, MainActivity.class);
                intentMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intentMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intentMain.putExtra("show_splash", false);
                finish();
                startActivity(intentMain);
            }
        }
    }

    public void onBackwardSelection() {
        Fragment temp = newInstance(position);
        if (temp != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.placeholder, temp)
                    .commit();
        } else {
            super.onBackPressed();
        }
    }

    public void onForwardSelection() {
        if (position == 2 && !checkout_allowed) {
            position -= 1;
            Toast.makeText(this, "Some items in your cart are no longer available in the selected quantities.", Toast.LENGTH_LONG).show();
        } else {
            Fragment temp = newInstance(position);
            if (temp != null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.placeholder, temp)
                        .commit();
            } else {
                position -= 1;
                Intent intentMain = new Intent(this, OrderPlacedActivity.class);
                startActivity(intentMain);
            }
        }
    }

    private void deselect(TextView txtview) {
        txtview.setTextColor(getResources().getColor(R.color.TRANSLUCENT));
    }

    private void select(TextView txtView) {
        deselect(txtCart);
        deselect(txtPayment);
        deselect(txtShipping);
        txtView.setTextColor(getResources().getColor(R.color.WHITE));
    }

    private Fragment newInstance(int position) {
        Fragment fragment = null;

        switch (position) {
            case 1:
                Bundle bundle = getIntent().getExtras();
                fragment = new CartFragment();
                fragment.setArguments(bundle);
                select(txtCart);
                txtAction.setText("PROCEED TO CHECKOUT");
                break;
            case 2:
                fragment = new NewCheckoutFragment();
                select(txtShipping);
                txtAction.setVisibility(View.GONE);
                //.setText("PROCEED TO PAYMENT");
                break;
            case 3:
                fragment = new PaymentFragment();
                select(txtPayment);
                txtAction.setVisibility(View.GONE);
                //txtAction.setText("PLACE ORDER");
                break;
        }
        return fragment;
    }

    public void updateCartCount(int count,boolean show) {
        String cartString = "";
        if (count > 0) {
            cartString = " (" + count + ")";
            if (show)
                txtAction.setVisibility(View.VISIBLE);
        } else {
            txtAction.setVisibility(View.GONE);
        }
        txtCart.setText("CART" + cartString);
    }

    public void disableCheckout() {
        backButton.setEnabled(false);
        txtAction.setEnabled(false);
    }

    public void enableCheckout() {
        backButton.setEnabled(true);
        txtAction.setEnabled(true);
    }

    public void restrictBackSelection() {
        is_back_allowed = false;
    }

    public void setCheckout(boolean value) {
        checkout_allowed = value;
    }
}
