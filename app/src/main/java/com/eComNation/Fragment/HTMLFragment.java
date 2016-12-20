package com.eComNation.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eComNation.R;
import com.ecomnationmobile.library.Control.CollapsibleText;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Created by User on 6/8/2016.
 */
public class HTMLFragment extends Fragment {

    View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.product_description, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mView = getView();

        if (mView != null) {
            int index = getArguments().getInt("id", 0);

            List<String> product_detail = Arrays.asList(getResources().getStringArray(R.array.product_detail));

            if (!product_detail.isEmpty()) {
//                WebView content = (WebView) mView.findViewById(R.id.webView);
//                content.setWebViewClient(new WebViewClient());
//                content.getSettings().setJavaScriptEnabled(true);
//                content.loadUrl("file:///android_asset/"+product_detail.get(index)+".html");

                try {
                    InputStream is = getActivity().getAssets().open(product_detail.get(index) + ".html");
                    int size = is.available();

                    byte[] buffer = new byte[size];
                    is.read(buffer);
                    is.close();

                    String str = new String(buffer);
                    str = Html.fromHtml(str).toString();
                    str = str.replace("\t","\n\n");
                    ((CollapsibleText) mView.findViewById(R.id.txtDescription)).setText(str, R.color.SecondaryColor, R.drawable.button_click_2);

                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
    }
}
