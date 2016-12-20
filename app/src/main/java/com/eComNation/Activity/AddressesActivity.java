package com.eComNation.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eComNation.Common.CustomLayoutInflater;
import com.eComNation.Common.Utility;
import com.eComNation.R;
import com.ecomnationmobile.library.Common.ECNCallback;
import com.ecomnationmobile.library.Common.EComNationActivity;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Control.ProgressDialogView;
import com.ecomnationmobile.library.Data.Address;
import com.ecomnationmobile.library.Data.ECNResponse;
import com.ecomnationmobile.library.Data.KeyValuePair;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Abhi on 15-12-2015.
 */
public class AddressesActivity extends EComNationActivity {

    LinearLayout addressList;
    View progressBar, emptyView;
    List<Address> mAddresses;
    int index;
    ProgressDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.all_addresses);

        progressBar = findViewById(R.id.progressBar);
        emptyView = findViewById(R.id.empty_View);

        emptyView.findViewById(R.id.btnRetry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAddresses();
            }
        });

        (findViewById(R.id.btnBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        dialog = new ProgressDialogView(this, "Updating...", R.drawable.progress);

        findViewById(R.id.txtAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAddresses != null && mAddresses.size() >= 5) {
                    Toast.makeText(AddressesActivity.this, "A customer can save a maximum of 5 addresses", Toast.LENGTH_LONG).show();
                } else {
                    Intent intentMain = new Intent(getApplicationContext(), UpdateAddressActivity.class);
                    intentMain.putExtra("id", 0);
                    startActivityForResult(intentMain, 0);
                }
            }
        });

        getAddresses();
    }

    private void getAddresses() {
        progressBar.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        String url = getString(R.string.production_base_url) + getString(R.string.api) + "store/customer/addresses?access_token=" + HelperClass.getSharedString(this, getString(R.string.access_token));

        HelperClass.getData(this, url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                ECNResponse response = (new Gson()).fromJson(result, ECNResponse.class);
                mAddresses = response.getAddresses();
                displayData();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(KeyValuePair error) {
                if (error.getValue() == 401) {
                    error.setKey(getString(R.string.logged_out_error));
                    Utility.getAccessToken(AddressesActivity.this);
                }
                displayError(error.getKey());
            }
        });
    }

    private void displayError(String error) {
        emptyView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        ((TextView)emptyView.findViewById(R.id.txtMessage)).setText(error);
        emptyView.findViewById(R.id.btnRetry).setVisibility(View.VISIBLE);
    }

    private void displayData() {
        addressList = (LinearLayout) findViewById(R.id.addressList);
        addressList.removeAllViews();

        for (int i = 0; i < mAddresses.size(); i++) {
            final View view = CustomLayoutInflater.getAddressView(this, mAddresses.get(i));
            view.setTag(i);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            view.findViewById(R.id.options).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    index = (int) view.getTag();
                    PopupMenu popup = new PopupMenu(AddressesActivity.this, v);

                    /** Adding menu items to the popumenu */
                    popup.getMenuInflater().inflate(R.menu.edit_remove, popup.getMenu());

                    /** Defining menu item click listener for the popup menu */
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int id = item.getItemId();
                            switch (id) {
                                case R.id.edit:
                                    Intent intentMain = new Intent(AddressesActivity.this, UpdateAddressActivity.class);
                                    Address address = mAddresses.get(index);
                                    intentMain.putExtra("id", address.getId());
                                    startActivityForResult(intentMain, 1);
                                    return true;
                                case R.id.remove:
                                    new AlertDialog.Builder(AddressesActivity.this)
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .setTitle("Remove Address")
                                            .setMessage("Are you sure you want to remove this address?")
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    deleteAddress(mAddresses.get(index));
                                                }
                                            })
                                            .setNegativeButton("No", null)
                                            .show();
                                    return true;
                            }
                            return false;
                        }

                    });

                    /** Showing the popup menu */
                    popup.show();
                }
            });
            addressList.addView(view);
        }
    }

    private void deleteAddress(final Address address) {
        dialog.show();
        String url = getString(R.string.production_base_url) + getString(R.string.api) + "store/customer/addresses/" + address.getId() + "?access_token=" + HelperClass.getSharedString(this, getString(R.string.access_token));
        HelperClass.deleteData(this, url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    Toast.makeText(AddressesActivity.this, "Address deleted", Toast.LENGTH_SHORT).show();
                    mAddresses.remove(address);
                    displayData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(KeyValuePair error) {
                dialog.dismiss();
                Toast.makeText(AddressesActivity.this, error.getKey(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        String addressString = HelperClass.getSharedString(this, "address");
        if (addressString != null) {
            Address address = (new Gson()).fromJson(addressString, Address.class);
            switch (requestCode) {
                case 0:
                    if (mAddresses == null)
                        mAddresses = new ArrayList<>();
                    mAddresses.add(address);
                    break;
                case 1:
                    mAddresses.remove(index);
                    mAddresses.add(index, address);
                    break;
            }
            displayData();
            HelperClass.putSharedString(this, "address", null);
        }
    }
}

