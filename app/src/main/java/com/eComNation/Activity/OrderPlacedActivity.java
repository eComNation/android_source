package com.eComNation.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.eComNation.Fragment.OrderDetailsFragment;
import com.eComNation.R;
import com.ecomnationmobile.library.Common.EComNationActivity;

/**
 * Created by Abhi on 25-12-2015.
 */
public class OrderPlacedActivity extends EComNationActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_placed);

        (findViewById(R.id.btnBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        (findViewById(R.id.txtContinue)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentMain = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intentMain);
            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.orderDetails, new OrderDetailsFragment()).commit();
    }
}
