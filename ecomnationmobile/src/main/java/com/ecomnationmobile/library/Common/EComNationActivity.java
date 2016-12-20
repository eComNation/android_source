package com.ecomnationmobile.library.Common;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.ecomnationmobile.library.R;

/**
 * Created by User on 5/17/2016.
 */
public class EComNationActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
    }
}
