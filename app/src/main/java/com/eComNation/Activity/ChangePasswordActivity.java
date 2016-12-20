package com.eComNation.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Toast;

import com.eComNation.R;
import com.ecomnationmobile.library.Common.ECNCallback;
import com.ecomnationmobile.library.Common.EComNationActivity;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Control.CustomEditText;
import com.ecomnationmobile.library.Control.ProgressDialogView;
import com.ecomnationmobile.library.Data.KeyValuePair;

/**
 * Created by Abhi on 08-04-2016.
 */
public class ChangePasswordActivity extends EComNationActivity {

    ProgressDialog dialog;
    CustomEditText newPassword, oldPassword, confirmPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);

        dialog = new ProgressDialogView(this, "Updating...", R.drawable.progress);

        (findViewById(R.id.btnBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        (findViewById(R.id.txtSave)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                patchData();
            }
        });

        oldPassword = (CustomEditText) findViewById(R.id.oldPassword);
        oldPassword.init(getString(R.string.old_password));
        oldPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        newPassword = (CustomEditText) findViewById(R.id.newPassword);
        newPassword.init(getString(R.string.new_password));
        newPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        confirmPassword = (CustomEditText) findViewById(R.id.confirmPassword);
        confirmPassword.init(getString(R.string.confirm_password));
        confirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    }

    private void patchData() {
        String url = getString(R.string.production_base_url) + getString(R.string.api);
        url += "store/customer/change_password?";
        if (oldPassword.getText().equals("")) {
            Toast.makeText(this, "Current password cannot be empty", Toast.LENGTH_LONG).show();
            return;
        }
        url += "current_password=" + oldPassword.getText();
        if (newPassword.getText().equals("")) {
            Toast.makeText(this, "New password cannot be empty", Toast.LENGTH_LONG).show();
            return;
        }
        url += "&new_password=" + newPassword.getText();
        if (confirmPassword.getText().equals("")) {
            Toast.makeText(this, "Confirm password cannot be empty", Toast.LENGTH_LONG).show();
            return;
        }
        url += "&confirm_password=" + confirmPassword.getText();

        url += "&access_token=" + HelperClass.getSharedString(this, getString(R.string.access_token));

        HelperClass.hideKeyboard(this);
        dialog.show();
        HelperClass.putData(this, url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(ChangePasswordActivity.this, "Password updated successfully", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(KeyValuePair error) {
                dialog.dismiss();
                Toast.makeText(ChangePasswordActivity.this, error.getKey(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
