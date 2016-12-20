package com.eComNation.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.eComNation.R;
import com.ecomnationmobile.library.Common.ECNCallback;
import com.ecomnationmobile.library.Common.EComNationActivity;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Control.CustomEditText;
import com.ecomnationmobile.library.Control.MobileEditText;
import com.ecomnationmobile.library.Control.ProgressDialogView;
import com.ecomnationmobile.library.Data.KeyValuePair;

/**
 * Created by Abhi on 15-12-2015.
 */
public class ContactUsActivity extends EComNationActivity {

    ProgressDialog dialog;
    CustomEditText name, email, notes;
    MobileEditText mobile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_us);

        dialog = new ProgressDialogView(this, "Please wait...", R.drawable.progress);

        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        findViewById(R.id.btnCall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + getString(R.string.support_phone)));
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                startActivity(intent);
            }
        });

        findViewById(R.id.btnMail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + getString(R.string.support_email)));
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                startActivity(intent);
            }
        });

        name = (CustomEditText) findViewById(R.id.name);
        name.init(getString(R.string.name));
        name.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);

        email = (CustomEditText) findViewById(R.id.email);
        email.init(getString(R.string.email_address));
        email.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        mobile = (MobileEditText) findViewById(R.id.phone);
        mobile.init(getString(R.string.mobile), getString(R.string.mobile_prefix));
        mobile.setInputType(InputType.TYPE_CLASS_PHONE);

        notes = (CustomEditText) findViewById(R.id.notes);
        notes.init(getString(R.string.comments));
        notes.setMinLines(10);

        findViewById(R.id.btnSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String params = email.getText().trim();
                if (validateFields()) {
                    params = "email=" + params;
                    params += "&subject=Feedback";
                    params += "&fields[name]=" + HelperClass.encode(name.getText());
                    params += "&fields[phone]=" + mobile.getText();
                    params += "&fields[note]=" + HelperClass.encode(notes.getText());

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

        value = mobile.getText().trim();
        if(value.equals("") || !Patterns.PHONE.matcher(value).matches()){
            mobile.setError("Please enter valid mobile number");
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
                mobile.setText("");
                notes.setText("");
                dialog.dismiss();
                Toast.makeText(ContactUsActivity.this, "Feedback sent.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(KeyValuePair error) {
                dialog.dismiss();
                Toast.makeText(ContactUsActivity.this, error.getKey(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
