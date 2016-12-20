package com.eComNation.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.eComNation.R;
import com.ecomnationmobile.library.Common.ECNCallback;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Control.CustomEditText;
import com.ecomnationmobile.library.Control.ProgressDialogView;
import com.ecomnationmobile.library.Data.KeyValuePair;

/**
 * Created by Chetan on 9/2/16.
 */
public class SurplusInquiryActivity extends FragmentActivity {

    CustomEditText name, email, phone, quantity;
    ProgressDialog dialog;
    String product_name, product_sku;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.surplus_inquiry);

        Bundle bundle = getIntent().getExtras();
        product_name = bundle.getString("product_name");
        product_sku = bundle.getString("product_sku");

        dialog = new ProgressDialogView(this, "Please wait...", R.drawable.progress);

        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HelperClass.hideKeyboard(SurplusInquiryActivity.this);
                onBackPressed();
            }
        });

        name = (CustomEditText) findViewById(R.id.name);
        name.init(getString(R.string.name));
        name.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);

        email = (CustomEditText) findViewById(R.id.email);
        email.init(getString(R.string.email_upper_case));
        email.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        phone = (CustomEditText) findViewById(R.id.phone);
        phone.init(getString(R.string.phone_upper_case));
        phone.setInputType(InputType.TYPE_CLASS_PHONE);

        quantity = (CustomEditText) findViewById(R.id.quantity);
        quantity.init(getString(R.string.quantity));
        phone.setInputType(InputType.TYPE_CLASS_NUMBER);

        findViewById(R.id.btnSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String params = email.getText().trim();
                if (validateFields()) {
                    params = "email=" + params;
                    params += "&subject=Inquiry";
                    params += "&fields[name]=" + HelperClass.encode(name.getText());
                    params += "&fields[phone]=" + phone.getText();
                    params += "&fields[quantity]=" + quantity.getText();
                    params += "&fields[product_name]=" + HelperClass.encode(product_name);
                    params += "&fields[product_sku]=" + HelperClass.encode(product_sku);

                    sendMail(params);
                }
            }
        });
    }

    private boolean validateFields(){
        String value;

        value = name.getText().trim();
        if(value.equals("")){
            name.setError("Please enter name");
            return false;
        }

        value = email.getText().trim();
        if (value.equals("") || !Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
            email.setError("Please enter a valid email address");
            //Toast.makeText(getActivity(), "SKU code cannot be left blank", Toast.LENGTH_LONG).show();
            return false;
        }

        value = phone.getText().trim();
        if(value.equals("") || !Patterns.PHONE.matcher(value).matches()){
            phone.setError("Please enter valid phone number");
            return false;
        }

        value = quantity.getText().trim();
        if(value.equals("")){
            quantity.setError("Please enter quantity");
            return false;
        }

        return true;
    }

    private void sendMail(String params) {
        HelperClass.hideKeyboard(this);
        dialog.show();
        String url = getString(R.string.production_base_url) + getString(R.string.api) + "store/contact_us?" + params;
        HelperClass.postData(this, url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                name.setText("");
                email.setText("");
                phone.setText("");
                quantity.setText("");
                dialog.dismiss();
                Toast.makeText(SurplusInquiryActivity.this, "Inquiry sent.", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(KeyValuePair error) {
                dialog.dismiss();
                Toast.makeText(SurplusInquiryActivity.this, error.getKey(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
