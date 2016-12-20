package com.eComNation.Activity;

import android.content.Intent;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eComNation.Adapter.CategoryListAdapter;
import com.eComNation.Common.Utility;
import com.eComNation.R;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Control.GridViewWithHeaderAndFooter;
import com.ecomnationmobile.library.Data.Category;
import com.ecomnationmobile.library.Database.DatabaseManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 6/10/2016.
 */
public class CategoryListingActivity extends FragmentActivity {

    CategoryListAdapter mAdapter;
    List<Category> categories;
    long cat_id;
    LinearLayout actionBar;
    View progressBar, emptyView;
    GridViewWithHeaderAndFooter mGridView;

    @Override
    public void onResume() {
        super.onResume();

        ImageButton item = (ImageButton) findViewById(R.id.cart);
        LayerDrawable icon = (LayerDrawable) item.getDrawable();

        Utility.setBadgeCount(this, icon, HelperClass.getCartCount(this));
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CategoryListingActivity.this,CheckOutActivity.class);
                intent.putExtras(new Bundle());
                startActivity(intent);
            }
        });
        HelperClass.putSharedString(this, getString(R.string.product), null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.category_listing);

        (findViewById(R.id.btnBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMain = new Intent(CategoryListingActivity.this, SearchActivity.class);
                startActivityForResult(intentMain, 1);
            }
        });

        mGridView = (GridViewWithHeaderAndFooter) findViewById(R.id.productGrid);

        actionBar = (LinearLayout) findViewById(R.id.actionBar);
        progressBar = findViewById(R.id.progressBar);
        emptyView = findViewById(R.id.emptyView);
        emptyView.findViewById(R.id.btnRetry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInitialData();
            }
        });

        cat_id = HelperClass.getSharedLong(this, "category");

        getInitialData();

    }

    private void getInitialData() {
        DatabaseManager.init(this);
        Category category = DatabaseManager.getInstance().getCategory(cat_id);
        if(category != null) {
            setTitle(category.getName());
            categories = DatabaseManager.getInstance().getSubCategories(cat_id);
            displayData();
        }
    }


    private void setTitle(String title) {
        ((TextView) findViewById(R.id.title)).setText(title);
    }

    private void displayError(String error) {
        progressBar.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
        ((TextView) emptyView.findViewById(R.id.txtMessage)).setText(error);
        emptyView.findViewById(R.id.btnRetry).setVisibility(View.VISIBLE);
    }

    private void displayData() {
        if (categories != null && !categories.isEmpty()) {
            emptyView.setVisibility(View.GONE);
            mGridView.setVisibility(View.VISIBLE);

            mAdapter = new CategoryListAdapter(CategoryListingActivity.this, R.layout.category_preview, new ArrayList<Object>(categories));
            mGridView.setAdapter(mAdapter);

            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    Category cat = categories.get(position);
                    HelperClass.putSharedLong(CategoryListingActivity.this, "category", cat.getId());
                    startActivity(new Intent(CategoryListingActivity.this, ProductListingActivity.class));
                }
            });
        } else {
            emptyView.setVisibility(View.VISIBLE);
            emptyView.findViewById(R.id.btnRetry).setVisibility(View.GONE);
            ((TextView) emptyView.findViewById(R.id.txtMessage)).setText(getString(R.string.no_products));
            mGridView.setVisibility(View.GONE);
        }
        progressBar.setVisibility(View.GONE);
    }
}
