package com.eComNation.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.SignUpEvent;
import com.eComNation.R;
import com.ecomnationmobile.library.Common.ECNCallback;
import com.ecomnationmobile.library.Common.EComNationActivity;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Control.CustomEditText;
import com.ecomnationmobile.library.Control.MobileEditText;
import com.ecomnationmobile.library.Control.ProgressDialogView;
import com.ecomnationmobile.library.Data.KeyValuePair;

import java.net.URLEncoder;

/**
 * Created by Abhi on 28-01-2016.
 */
public class SignUpActivity extends EComNationActivity {

    CustomEditText firstName, lastName, username, password, confirmPassword;
    MobileEditText phone;
    ToggleButton btnSubscribe;
    ProgressDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        dialog = new ProgressDialogView(this, "Please wait...", R.drawable.progress);

        showSignUp();
    }

    private void showSignUp() {
        findViewById(R.id.signupLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.activationLink).setVisibility(View.GONE);

        firstName = (CustomEditText) findViewById(R.id.txtFirstname);
        firstName.init(getString(R.string.first_name));
        firstName.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);

        lastName = (CustomEditText) findViewById(R.id.txtLastname);
        lastName.init(getString(R.string.last_name));
        lastName.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);

        username = (CustomEditText) findViewById(R.id.txtUsername);
        username.init(getString(R.string.email_address));
        username.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        phone = (MobileEditText) findViewById(R.id.txtPhone);
        phone.init(getString(R.string.phone_upper_case), getString(R.string.mobile_prefix));
        phone.setInputType(InputType.TYPE_CLASS_NUMBER);

        password = (CustomEditText) findViewById(R.id.txtPassword);
        password.init(getString(R.string.password_hint));
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        confirmPassword = (CustomEditText) findViewById(R.id.txtConfirmPassword);
        confirmPassword.init(getString(R.string.confirm_password));
        confirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        btnSubscribe = (ToggleButton) findViewById(R.id.btnSubscribe);

        findViewById(R.id.btnSignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpParams();
            }
        });
    }

    private String encode(String value) {
        String encodedValue = "";
        try {
            encodedValue = URLEncoder.encode(value, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encodedValue;
    }

    private void setUpParams() {
        String params = "";
        String value = firstName.getText().trim();
        if (value.equals("")) {
            Toast.makeText(SignUpActivity.this, "First name cannot be blank", Toast.LENGTH_LONG).show();
            return;
        } else {
            params += "first_name=" + encode(value);
        }

        value = lastName.getText().trim();
        if (value.equals("")) {
            Toast.makeText(SignUpActivity.this, "Last name cannot be blank", Toast.LENGTH_LONG).show();
            return;
        } else {
            params += "&last_name=" + encode(value);
        }

        value = username.getText().trim();
        String login = value;
        if (value.equals("")) {
            Toast.makeText(SignUpActivity.this, "Username cannot be blank", Toast.LENGTH_LONG).show();
            return;
        } else {
            params += "&login=" + encode(value);
        }

        value = phone.getText().trim();
        params += "&phone=" + encode(value);

        value = password.getText().trim();
        if (value.equals("")) {
            Toast.makeText(SignUpActivity.this, "Password cannot be blank", Toast.LENGTH_LONG).show();
            return;
        } else {
            params += "&password=" + encode(value);
        }

        String value2 = confirmPassword.getText().trim();
        if (value2.equals("")) {
            Toast.makeText(SignUpActivity.this, "Confirmation password cannot be blank", Toast.LENGTH_LONG).show();
            return;
        } else {
            if(value.equals(value2))
                params += "&password_confirmation=" + encode(value2);
            else
            {
                Toast.makeText(SignUpActivity.this, "Password doesn't match confirm password", Toast.LENGTH_LONG).show();
                return;
            }
        }

        int n = 0;
        if(btnSubscribe.isChecked())
            n = 1;

        params += "&marketing_mails="+n;

        postData(params,login);
    }

    public void postData(String params,final String login) {
        HelperClass.hideKeyboard(this);
        dialog.show();
        String url = getString(R.string.production_base_url);
        url += getString(R.string.api)+ "store/customer/accounts/sign_up?"+params;

        HelperClass.postData(this, url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                dialog.dismiss();
                showLinkSent(login);
                Answers.getInstance().logSignUp(new SignUpEvent()
                        .putCustomAttribute("Username", login));
            }

            @Override
            public void onFailure(KeyValuePair error) {
                dialog.dismiss();
                Toast.makeText(SignUpActivity.this, error.getKey(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLinkSent(String login) {
        findViewById(R.id.signupLayout).setVisibility(View.GONE);
        findViewById(R.id.activationLink).setVisibility(View.VISIBLE);

        ((TextView) findViewById(R.id.txtEmail)).setText(login);
    }
}
