package com.eComNation.Activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import com.eComNation.R;
import com.ecomnationmobile.library.Common.EComNationActivity;

/**
 * Created by Abhi on 20-09-2015.
 */
public class StaticHTMLActivity extends EComNationActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.terms);

        ImageButton back = (ImageButton) findViewById(R.id.btnBack);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        TextView header = (TextView) findViewById(R.id.txtHeader);
        WebView content = (WebView) findViewById(R.id.content);
        content.setWebViewClient(new WebViewClient());
        content.getSettings().setJavaScriptEnabled(true);

        Bundle mBundle = getIntent().getExtras();
        String name = mBundle.getString("name");
        String headerText = mBundle.getString("header");

        header.setText(headerText);
        content.loadUrl("file:///android_asset/" + name + ".html");
    }
}
