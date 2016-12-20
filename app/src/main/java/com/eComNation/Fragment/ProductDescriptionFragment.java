package com.eComNation.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eComNation.R;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Control.CollapsibleText;
import com.ecomnationmobile.library.Data.Product;
import com.google.gson.Gson;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Created by Abhi on 28-03-2016.
 */
public class ProductDescriptionFragment extends Fragment {

    View mView;
    Product product;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.product_description, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        mView = getView();

        if (mView != null) {
            int index = getArguments().getInt("id", 0);
            product = (new Gson()).fromJson(HelperClass.getSharedString(getActivity(), "product"), Product.class);
            String desc;
            if (index < 0) {
                desc = product.getDescription();
                if (desc != null) {
                    desc = desc.replace("\r\n\t<li>", "<li>\r\n\t");
                    desc = desc.replace("</p>\r\n", "\r\n\t</p>");
                    desc = desc.trim();
                    desc = Jsoup.parse(desc).html();
                    desc = desc.replace("\n","\t");
                    desc = desc.replace("\t  ","\t");
                    desc = desc.replace("\t ","\t");
                    desc = desc.replace("\t<li>", "<li>\t");
                    desc = Html.fromHtml(desc).toString();
                    desc = desc.replace("\n\n", "");
                    desc = desc.replace("\t", "\n\n");
                }
            } else {
                String[] tab_keys = getResources().getStringArray(R.array.tab_keys);
                String[] tab_display = getResources().getStringArray(R.array.tab_display);
                desc = product.getDetailed_description();
                if (desc != null) {
                    desc = desc.replace("\r\n\t<li>", "<li>\r\n\t");
                    desc = desc.replace("</p>\r\n", "\r\n\t</p>");
                    desc = desc.trim();
                    Document doc = Jsoup.parse(desc);
                    if (doc != null) {
                        Element el = doc.getElementById(tab_keys[index]);
                        if (el != null) {
                            desc = el.html();
                            desc = desc.replace("\n","\t");
                            desc = desc.replace("\t ","\t");
                            desc = desc.replace("\t<li>", "<li>\t");
                        }
                        desc = Html.fromHtml(desc).toString();
                        desc = desc.replace("\n\n", "");
                        desc = desc.replace("\t", "\n\n");
                    }
                }
                if (desc == null || desc.equals(""))
                    desc = tab_display[index] + " information not available.";
            }
            if (desc == null)
                desc = "";
            else {
                try {
                    byte[] data = desc.getBytes("ASCII");
                    desc = new String(data);
                    desc = desc.replace("?", "");
                } catch (Exception ue) {
                    ue.printStackTrace();
                }
            }

            ((CollapsibleText) mView.findViewById(R.id.txtDescription)).setText(desc, R.color.SecondaryColor, R.drawable.button_click_2);
        }
    }
}
