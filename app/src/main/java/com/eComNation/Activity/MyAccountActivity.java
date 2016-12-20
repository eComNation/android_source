package com.eComNation.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.eComNation.R;
import com.ecomnationmobile.library.Common.EComNationActivity;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Control.AlertDialogView;

/**
 * Created by Abhi on 29-03-2016.
 */
public class MyAccountActivity extends EComNationActivity {

    View my_orders, addresses, profile, change_password, my_favourites;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_account);

        my_orders = findViewById(R.id.my_orders);
        addresses = findViewById(R.id.addresses);
        profile = findViewById(R.id.profile);
        change_password = findViewById(R.id.change_password);
        my_favourites = findViewById(R.id.my_favourites);

        ((TextView) my_orders.findViewById(R.id.drawer_title)).setText(getString(R.string.my_orders));
        ((ImageView) my_orders.findViewById(R.id.drawer_icon)).setImageResource(R.drawable.bag);
        ((TextView) addresses.findViewById(R.id.drawer_title)).setText(getString(R.string.addresses));
        ((ImageView) addresses.findViewById(R.id.drawer_icon)).setImageResource(R.drawable.home);
        ((TextView) profile.findViewById(R.id.drawer_title)).setText(getString(R.string.profile));
        ((ImageView) profile.findViewById(R.id.drawer_icon)).setImageResource(R.drawable.user);
        ((TextView) change_password.findViewById(R.id.drawer_title)).setText(getString(R.string.change_password));
        ((ImageView) change_password.findViewById(R.id.drawer_icon)).setImageResource(R.drawable.padlock);
        ((TextView) my_favourites.findViewById(R.id.drawer_title)).setText(getString(R.string.my_favourites));
        ((ImageView) my_favourites.findViewById(R.id.drawer_icon)).setImageResource(R.drawable.grey_heart);

        if(HelperClass.getSharedBoolean(MyAccountActivity.this, "can_change_password"))
            change_password.setVisibility(View.VISIBLE);
        else
            change_password.setVisibility(View.GONE);

        my_orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyAccountActivity.this, MyOrdersActivity.class));
            }
        });

        addresses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyAccountActivity.this, AddressesActivity.class));
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyAccountActivity.this, ProfileActivity.class));
            }
        });

        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyAccountActivity.this, ChangePasswordActivity.class));
            }
        });

        my_favourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyAccountActivity.this, MyFavouritesActivity.class));
            }
        });

        (findViewById(R.id.btnBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialogView adv = new AlertDialogView(MyAccountActivity.this);
                adv.initColor(R.color.PrimaryColor, R.color.SecondaryColor, R.drawable.store_logo);
                adv.initText("LOG OUT", "Are you sure you want to logout?",AlertDialogView.CONFIRM);
                adv.getYesButton().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HelperClass.log_out(MyAccountActivity.this);
                        finish();
                        adv.dismissAlertDialog();
                    }
                });
                adv.getNoButton().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adv.dismissAlertDialog();
                    }
                });
                adv.showAlertDialog();
            }
        });
    }
}
