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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Abhi on 24-12-2015.
 */
public class StoreDrawerFragment extends Fragment {

    View mView;
    ListView mStoreListView;
    List<Category> mStorelist;
    Long pref;
    DrawerListAdapter mStoreAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.store_drawer, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mView = getView();

        if (mView != null) {
            mView.findViewById(R.id.txtBack).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NavigationDrawerFragment.onBackSelected();
                }
            });

            setUpList();

            mStoreListView = (ListView) mView.findViewById(R.id.listview);
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

    public void setUpList() {
        mStorelist = new ArrayList<>();
        pref = HelperClass.getSharedLong(getActivity(), "store_drawer");
        DatabaseManager.init(getActivity());
        Category category = DatabaseManager.getInstance().getCategory(pref);
        List<Category> list = DatabaseManager.getInstance().getSubCategories(pref);
        Collections.sort(list, new Comparator<Category>() {
            @Override
            public int compare(Category lhs, Category rhs) {
                return Long.valueOf(lhs.getId()).compareTo(rhs.getId());
            }
        });
        category.setName("All " + category.getName());
        category.setIs_parent(false);
        mStorelist.add(category);
        for (Category cat : list) {
            mStorelist.add(cat);
        }
    }
}