package com.eComNation.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eComNation.R;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Data.ExtendedOption;
import com.ecomnationmobile.library.Data.JewelleryProduct;
import com.ecomnationmobile.library.Data.Price;
import com.ecomnationmobile.library.Data.Product;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by User on 7/3/2016.
 */
public class SpecificationFragment extends Fragment {

    View mView;
    JewelleryProduct jewelleryProduct;
    Price price;
    Product product;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.virani_specification, container, false);
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
            price = (new Gson()).fromJson(HelperClass.getSharedString(getActivity(), "price"), Price.class);
            product = (new Gson()).fromJson(HelperClass.getSharedString(getActivity(), "product"), Product.class);
            jewelleryProduct = (new Gson()).fromJson(HelperClass.getSharedString(getActivity(), "new_product"), JewelleryProduct.class);

            String name = "", shape="";
            int nod = 0;
            double weight = 0;
            List<ExtendedOption> list = price.getGem_options();
            for (int i = 0; i < list.size(); i++) {
                ExtendedOption dop = list.get(i);
                if (!name.equals(""))
                    name += ",";
                name += dop.getValue().getName();

                if (!shape.equals(""))
                    shape += ",";
                if(dop.getValue().getShape() != null)
                    shape += dop.getValue().getShape();

                nod += dop.getNumber_of_gems();

                weight += dop.getValue().getCarat() * dop.getNumber_of_gems();

                if (i == 0) {
                    ((TextView) mView.findViewById(R.id.txtPrimaryDiamonds)).setText(dop.getValue().getName());
                    ((TextView) mView.findViewById(R.id.txtPrimaryWeight)).setText(String.format("%.2f", dop.getValue().getCarat()) + " CT");
                    ((TextView) mView.findViewById(R.id.txtPrimaryPrice)).setText(HelperClass.formatCurrency(getString(R.string.currency),dop.getValue().getPrice()));
                }
                if (i == 1) {
                    ((TextView) mView.findViewById(R.id.txtSecondaryDiamonds)).setText(dop.getValue().getName());
                    ((TextView) mView.findViewById(R.id.txtSecondaryWeight)).setText(String.format("%.2f", dop.getValue().getCarat()) + " CT");
                    ((TextView) mView.findViewById(R.id.txtSecondaryPrice)).setText(HelperClass.formatCurrency(getString(R.string.currency),dop.getValue().getPrice()));
                    mView.findViewById(R.id.secondaryBlock).setVisibility(View.VISIBLE);
                }
            }
            ((TextView) mView.findViewById(R.id.productCode)).setText(product.getDefault_variant().getSku());

            ((TextView) mView.findViewById(R.id.totalWeight)).setText(String.format("%.2f", price.getMetal().getWeight() + weight) + " gm");

            ((TextView) mView.findViewById(R.id.diamondQuality)).setText(name);

            ((TextView) mView.findViewById(R.id.diamondWeight)).setText(String.format("%.2f", weight) + " CT");

            ((TextView) mView.findViewById(R.id.diamondShape)).setText(shape.toUpperCase());

            ((TextView) mView.findViewById(R.id.diamondCount)).setText(String.format(getString(R.string.integer), nod));

            ((TextView) mView.findViewById(R.id.goldType)).setText(price.getMetal().getName());

            ((TextView) mView.findViewById(R.id.goldSize)).setText(price.getMetal().getSize());

            ((TextView) mView.findViewById(R.id.goldWeight)).setText(String.format("%.2f",price.getMetal().getWeight()) + " gm");

            ((TextView) mView.findViewById(R.id.goldRate)).setText(HelperClass.formatCurrency(getString(R.string.currency),price.getMetal().getPer_gm_rate()));

            ((TextView) mView.findViewById(R.id.goldPrice)).setText(HelperClass.formatCurrency(getString(R.string.currency),price.getMetal().getPrice()));

            ((TextView) mView.findViewById(R.id.makePrice)).setText(HelperClass.formatCurrency(getString(R.string.currency),price.getMaking_charge()));

            ((TextView) mView.findViewById(R.id.makeType)).setText(jewelleryProduct.getMaking_type());

            ((TextView) mView.findViewById(R.id.grandTotal)).setText(HelperClass.formatCurrency(getString(R.string.currency),price.getTotal()));
        }
    }
}
