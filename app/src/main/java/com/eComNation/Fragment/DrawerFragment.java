package com.eComNation.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.eComNation.Adapter.DrawerListAdapter;
import com.eComNation.R;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Data.Category;
import com.ecomnationmobile.library.Database.DatabaseManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Abhi on 05-01-2016.
 */
public class DrawerFragment extends Fragment {

    private DrawerListAdapter mStoreAdapter;

    private List<Category> mStorelist;

    private ListView mStoreListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_drawer, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View rootView = getView();

        if (rootView != null) {

            HelperClass.putSharedLong(getActivity(), "store_drawer", 0);
            HelperClass.putSharedInt(getActivity(), "menu_level", 0);

            DatabaseManager.init(getActivity());
            mStorelist = DatabaseManager.getInstance().getSubCategories(0);

            mStoreListView = (ListView) rootView.findViewById(R.id.storelist);
            mStoreListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    Category kvp = mStorelist.get(position);
                    if (kvp.is_Parent(getActivity())) {
                        HelperClass.putSharedLong(getActivity(), "store_drawer", kvp.getId());
                        NavigationDrawerFragment.onStoreItemSelected();
                    } else {
                        HelperClass.putSharedLong(getActivity(), "category", kvp.getId());
                        NavigationDrawerFragment.selectItem(position);
                    }
                }
            });

            mStoreAdapter = new DrawerListAdapter(getActivity(), R.layout.store_drawer_item, new ArrayList<Object>(mStorelist));

            mStoreListView.setAdapter(mStoreAdapter);

            HelperClass.justifyListViewHeightBasedOnChildren(mStoreListView);
        }
    }
}
