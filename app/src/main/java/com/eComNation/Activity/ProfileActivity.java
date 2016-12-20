package com.eComNation.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.eComNation.Common.Utility;
import com.eComNation.R;
import com.ecomnationmobile.library.Common.ECNCallback;
import com.ecomnationmobile.library.Common.EComNationActivity;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Control.CustomEditText;
import com.ecomnationmobile.library.Control.MobileEditText;
import com.ecomnationmobile.library.Control.ProgressDialogView;
import com.ecomnationmobile.library.Data.Account;
import com.ecomnationmobile.library.Data.KeyValuePair;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Created by Abhi on 14-12-2015.
 */
public class ProfileActivity extends EComNationActivity {

    Account account;
    View progressBar, emptyView;
    ProgressDialog dialog;
    CustomEditText firstName, lastName, email;
    MobileEditText mobile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        dialog = new ProgressDialogView(this, "Updating...", R.drawable.progress);

        progressBar = findViewById(R.id.progressBar);
        emptyView = findViewById(R.id.empty_View);
        emptyView.findViewById(R.id.btnRetry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getProfile();
            }
        });


        findViewById(R.id.txtSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                patchData();
            }
        });

        (findViewById(R.id.btnBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        getProfile();
    }

    private void getProfile() {
        progressBar.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        String url = getString(R.string.production_base_url) + getString(R.string.api) + "store/customer/profile?access_token="+ HelperClass.getSharedString(this, getString(R.string.access_token));
        HelperClass.getData(this, url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    JSONObject productObject = (JSONObject) obj.get("customer");
                    String productString = productObject.toString();
                    account = (new Gson()).fromJson(productString, Account.class);
                    displayData();
                } catch (Exception e) {
                    Toast.makeText(ProfileActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(KeyValuePair error) {
                if (error.getValue() == 401) {
                    error.setKey(getString(R.string.logged_out_error));
                    Utility.getAccessToken(ProfileActivity.this);
                }
                displayError(error.getKey());
            }
        });
    }

    private void displayError(String error) {
        emptyView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        ((TextView)emptyView.findViewById(R.id.txtMessage)).setText(error);
    }

    private void displayData() {
        if (account != null) {
            emptyView.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);

            firstName = (CustomEditText) findViewById(R.id.shippingFirstName);
            firstName.init(getString(R.string.first_name));
            firstName.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);

            lastName = (CustomEditText) findViewById(R.id.shippingLastName);
            lastName.init(getString(R.string.last_name));
            lastName.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);

            mobile = (MobileEditText) findViewById(R.id.shippingMobile);
            mobile.init(getString(R.string.mobile), getString(R.string.mobile_prefix));
            mobile.setInputType(InputType.TYPE_CLASS_PHONE);

            email = (CustomEditText) findViewById(R.id.emailAddress);
            email.init(getString(R.string.email_address));
            email.setEnabled(false);

            firstName.setText(account.getFirst_name());
            lastName.setText(account.getLast_name());
            mobile.setText(account.getPhone());
            email.setText(account.getEmail());
        }
    }

    private void patchData() {
        String url = getString(R.string.production_base_url) + getString(R.string.api);
        url += "store/customer/update_profile?";
        if (firstName.getText().equals("")) {
            Toast.makeText(this, "First name cannot be empty", Toast.LENGTH_LONG).show();
            return;
        }

        url += "first_name=" + firstName.getText();
        if (lastName.getText().equals("")) {
            Toast.makeText(this, "Last name cannot be empty", Toast.LENGTH_LONG).show();
            return;
        }

        url += "&last_name=" + lastName.getText();
        if (!mobile.getText().equals("")) {
            url += "&phone=" + mobile.getText();
        }

        url += "&access_token=" + HelperClass.getSharedString(this, getString(R.string.access_token));

        HelperClass.hideKeyboard(this);
        dialog.show();
        HelperClass.putData(this, url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(ProfileActivity.this, "Profile updated successfully.", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }

            @Override
            public void onFailure(KeyValuePair error) {
                dialog.dismiss();
                Toast.makeText(ProfileActivity.this,error.getKey(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
