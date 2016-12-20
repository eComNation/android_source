package com.eComNation.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.eComNation.R;
import com.ecomnationmobile.library.Common.ECNCallback;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Control.ProgressDialogView;
import com.ecomnationmobile.library.Control.TouchImageView;
import com.ecomnationmobile.library.Data.KeyValuePair;

/**
 * Created by User on 8/10/2016.
 */
public class SizeGuideActivity extends FragmentActivity {
    ProgressDialog dialog;

    @Override
    public void onCreate(Bundle savedInstantState) {
        super.onCreate(savedInstantState);
        setContentView(R.layout.size_guide);

        dialog = new ProgressDialogView(this, "Please wait...", R.drawable.progress);

        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        dialog.show();
        final TouchImageView img = (TouchImageView) findViewById(R.id.imageSize);
        String url = getString(R.string.misc_url) + "misc/" + getString(R.string.size_image);
        HelperClass.setPicassoBitMap(url, img, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                dialog.dismiss();
            }

            @Override
            public void onFailure(KeyValuePair error) {
                dialog.dismiss();
            }
        });
    }
}
