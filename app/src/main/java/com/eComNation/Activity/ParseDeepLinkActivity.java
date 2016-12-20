package com.eComNation.Activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.eComNation.Common.Utility;
import com.ecomnationmobile.library.Common.HelperClass;

/**
 * Created by abhileshhalarnkar on 30/11/16.
 */

public class ParseDeepLinkActivity extends Activity {
    public static final String CATEGORY_DEEP_LINK = "category";
    public static final String PRODUCT_DEEP_LINK = "product";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent == null || intent.getData() == null) {
            finish();
        }

        openDeepLink(intent.getData());

        // Finish this activity
        finish();
    }

    private void openDeepLink(Uri deepLink) {
        String path = deepLink.getPath();

        if (path.contains(CATEGORY_DEEP_LINK)) {
            int index = deepLink.toString().lastIndexOf("/");
            String ID = deepLink.toString().substring(index + 1);
            HelperClass.putSharedLong(this, "category", Long.parseLong(ID));
            Intent intent = Utility.getListingIntent(this, ID);
            startActivity(intent);
        } else if (path.contains(PRODUCT_DEEP_LINK)) {
            // Launch the inbox activity
            Intent productIntent = Utility.getDetailsIntent(this);
            productIntent.setData(deepLink);
            startActivity(productIntent);
        } else {
            // Fall back to the main activity
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}
