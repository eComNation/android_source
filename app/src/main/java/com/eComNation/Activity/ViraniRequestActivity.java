package com.eComNation.Activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.eComNation.R;
import com.ecomnationmobile.library.Common.ECNCallback;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Data.ExtendedOption;
import com.ecomnationmobile.library.Data.KeyValuePair;
import com.ecomnationmobile.library.Data.Price;
import com.ecomnationmobile.library.Data.Product;
import com.google.gson.Gson;

import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by User on 6/25/2016.
 */
public class ViraniRequestActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.virani_request);

        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {  onBackPressed(); }
        });

        findViewById(R.id.btnSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { finish(); }
        });

        Product product = (new Gson()).fromJson(HelperClass.getSharedString(this, "product"), Product.class);
        Price price = (new Gson()).fromJson(HelperClass.getSharedString(this, "price"), Price.class);

        double metal = price.getMetal().getWeight();
        double diamond = 0;
        List<ExtendedOption> list = price.getGem_options();
        for(int i = 0; i< list.size(); i++) {
            diamond += list.get(i).getValue().getCarat();
        }

        double value = metal + diamond * 100 / 10.5;
        value = Math.floor(value) *300;
        ((TextView) findViewById(R.id.price)).setText(HelperClass.formatCurrency(getString(R.string.currency),value));

        DateTime date = DateTime.now().plusDays(7);
        ((TextView) findViewById(R.id.txtDeliverDate)).setText(HelperClass.formatDateOnly(date.toString()));

        if(product != null) {
            ((TextView) findViewById(R.id.txt1)).setText(String.format(getString(R.string.model_3d_1),product.getName()));
            ((TextView) findViewById(R.id.txt2)).setText(getString(R.string.model_3d_2));
            ((TextView) findViewById(R.id.txt3)).setText(getString(R.string.model_3d_3));
            ((TextView) findViewById(R.id.txt4)).setText(getString(R.string.model_3d_4));

            ImageView img = (ImageView) findViewById(R.id.image);
            String url = getString(R.string.misc_url) + "misc/virani.jpg";
            HelperClass.setPicassoBitMap(url, img, new ECNCallback() {
                @Override
                public void onSuccess(String result) {
                }

                @Override
                public void onFailure(KeyValuePair error) {
                }
            });
        }
    }
}
