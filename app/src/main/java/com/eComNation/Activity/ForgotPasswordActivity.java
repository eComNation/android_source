package com.eComNation.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.eComNation.R;
import com.ecomnationmobile.library.Common.ECNCallback;
import com.ecomnationmobile.library.Common.EComNationActivity;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Control.CustomEditText;
import com.ecomnationmobile.library.Control.ProgressDialogView;
import com.ecomnationmobile.library.Data.KeyValuePair;

/**
 * Created by Abhi on 20/7/15.
 */
public class ForgotPasswordActivity extends EComNationActivity {

    CustomEditText username, newPassword, confirmPassword;
    ProgressDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgotpassword);

        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        dialog = new ProgressDialogView(this, "Please wait...", R.drawable.progress);

        String token = getIntent().getStringExtra("token");
        if (token != null && !token.equals(""))
            showSetPassword(token);
        else
            showForgotPassword();
    }

    private void showForgotPassword() {
        findViewById(R.id.resetpassword).setVisibility(View.GONE);
        findViewById(R.id.resetLink).setVisibility(View.GONE);
        findViewById(R.id.forgotpassword).setVisibility(View.VISIBLE);

        findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String login = username.getText().trim();
                if (!login.equals("") && Patterns.EMAIL_ADDRESS.matcher(login).matches()) {
                    resetPassword(login);
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Please enter a valid username", Toast.LENGTH_SHORT).show();
                }
            }
        });

        username = (CustomEditText) findViewById(R.id.editMail);
        username.init(getString(R.string.username_hint));
        username.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
    }

    private void resetPassword(final String login) {
        HelperClass.hideKeyboard(this);
        dialog.show();
        String url = getString(R.string.production_base_url);
        url += getString(R.string.api) + "store/customer/accounts/forgot_password?login=" + login;

        HelperClass.getData(this, url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                dialog.dismiss();
                showLinkSent(login);
            }

            @Override
            public void onFailure(KeyValuePair error) {
                dialog.dismiss();
                Toast.makeText(ForgotPasswordActivity.this, error.getKey(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setNewPassword(String token, String newPwd, String confirmPwd) {
        HelperClass.hideKeyboard(this);
        dialog.show();
        String url = getString(R.string.production_base_url);
        url += getString(R.string.api) + "store/customer/accounts/set_password?recovery_token=" + token;
        url += "&password=" + newPwd + "&password_confirmation=" + confirmPwd;

        HelperClass.putData(this, url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(ForgotPasswordActivity.this, "Your password has been reset", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(KeyValuePair error) {
                dialog.dismiss();
                Toast.makeText(ForgotPasswordActivity.this, error.getKey(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showSetPassword(final String token) {
        findViewById(R.id.resetLink).setVisibility(View.GONE);
        findViewById(R.id.forgotpassword).setVisibility(View.GONE);
        findViewById(R.id.resetpassword).setVisibility(View.VISIBLE);

        findViewById(R.id.btnSetPassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newPwd = newPassword.getText().trim();
                if (newPwd.equals("")) {
                    Toast.makeText(ForgotPasswordActivity.this, "Password cannot be blank", Toast.LENGTH_SHORT).show();
                    return;
                }

                String confirmPwd = confirmPassword.getText().trim();
                if (confirmPwd.equals("")) {
                    Toast.makeText(ForgotPasswordActivity.this, "Confirm password cannot be blank", Toast.LENGTH_SHORT).show();
                    return;
                }
                setNewPassword(token, newPwd, confirmPwd);
            }
        });

        newPassword = (CustomEditText) findViewById(R.id.newPassword);
        newPassword.init(getString(R.string.new_password));
        newPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        confirmPassword = (CustomEditText) findViewById(R.id.confirmPassword);
        confirmPassword.init(getString(R.string.confirm_password));
        confirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    }

    private void showLinkSent(String login) {
        findViewById(R.id.forgotpassword).setVisibility(View.GONE);
        findViewById(R.id.resetpassword).setVisibility(View.GONE);
        findViewById(R.id.resetLink).setVisibility(View.VISIBLE);

        ((TextView) findViewById(R.id.txtEmail)).setText(login);
    }
}
