package com.eComNation.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.eComNation.Activity.MadeToMeasureActivity;
import com.eComNation.Adapter.CategoryListAdapter;
import com.eComNation.R;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Control.GridViewWithHeaderAndFooter;
import com.ecomnationmobile.library.Data.DropdownClass;

import java.util.ArrayList;

/**
 * Created by User on 6/6/2016.
 */
public class MeasureStepsFragment extends Fragment {

    View mView;
    DropdownClass attribute;
    CategoryListAdapter mAdapter;
    GridViewWithHeaderAndFooter mGridView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.measure_steps_layout, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mView = getView();

        if (mView != null) {
            mView.findViewById(R.id.parentLayout).scrollTo(0, 0);
            mGridView = (GridViewWithHeaderAndFooter) mView.findViewById(R.id.productGrid);

            attribute = ((MadeToMeasureActivity) getActivity()).getCurrentAttribute(HelperClass.getSharedInt(getActivity(), "measure_level"));
            if (attribute != null) {
                ((TextView) mView.findViewById(R.id.filterName)).setText("SELECT " + attribute.getLabel() + " OF YOUR CHOICE");

                mAdapter = new CategoryListAdapter(getActivity(), R.layout.category_preview, new ArrayList<Object>(attribute.getValues()));
                mGridView.setAdapter(mAdapter);

                mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                        String mURL = HelperClass.getSharedString(getActivity(), "variant_hash");
                        mURL += "," + attribute.getValues().get(position).toUpperCase();
                        HelperClass.putSharedString(getActivity(), "variant_hash", mURL);
                        ((MadeToMeasureActivity) getActivity()).onNextSelected();
                    }
                });
            }
        }
    }
}
