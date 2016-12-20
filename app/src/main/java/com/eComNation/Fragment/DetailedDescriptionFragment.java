package com.eComNation.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eComNation.R;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Control.CollapsibleWebView;
import com.ecomnationmobile.library.Data.Product;
import com.google.gson.Gson;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Created by User on 6/23/2016.
 */
public class DetailedDescriptionFragment extends Fragment {

    View mView;
    Product product;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.detailed_description, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mView = getView();

        if (mView != null) {
            mView.setMinimumHeight(200);
            product = (new Gson()).fromJson(HelperClass.getSharedString(getActivity(), "product"), Product.class);
            int index = getArguments().getInt("id", 0);

            String[] tab_keys = getResources().getStringArray(R.array.tab_keys);
            String desc = product.getDetailed_description();
            if (desc != null) {
                Document doc = Jsoup.parse(desc);
                if (doc != null) {
                    Element el = doc.getElementById(tab_keys[index]);
                    if (el == null)
                        el = doc.getElementsByClass(tab_keys[index]).first();

                    if (el != null)
                        desc = el.html();
                    else
                        desc = doc.html();
                }
            }
            if (desc == null)
                desc = "";

            ((CollapsibleWebView) mView.findViewById(R.id.txtDescription)).setText(Html.fromHtml(desc).toString(), R.color.SecondaryColor);

        }
    }
}
