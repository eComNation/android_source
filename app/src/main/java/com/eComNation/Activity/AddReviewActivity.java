package com.eComNation.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.view.View;
import android.widget.RatingBar;
import android.widget.Toast;

import com.eComNation.R;
import com.ecomnationmobile.library.Common.ECNCallback;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Control.CustomEditText;
import com.ecomnationmobile.library.Control.ProgressDialogView;
import com.ecomnationmobile.library.Data.Account;
import com.ecomnationmobile.library.Data.KeyValuePair;
import com.ecomnationmobile.library.Data.Product;
import com.google.gson.Gson;

/**
 * Created by User on 9/25/2016.
 */
public class AddReviewActivity extends FragmentActivity {

    ProgressDialog dialog;
    CustomEditText title, content;
    RatingBar ratingBar;
    Account account;
    Product product;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_review);

        dialog = new ProgressDialogView(this, "Please wait...", R.drawable.progress);

        product = (new Gson()).fromJson(HelperClass.getSharedString(this, "product"), Product.class);

        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        ratingBar.setRating(1.0f);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if(rating < 1.0f) ratingBar.setRating(1.0f);
            }
        });

        title = (CustomEditText) findViewById(R.id.title);
        title.init(getString(R.string.title));
        title.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);

        content = (CustomEditText) findViewById(R.id.content);
        content.init(getString(R.string.comments));
        content.setMinLines(10);

        findViewById(R.id.btnSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {
                    String prof = HelperClass.getSharedString(AddReviewActivity.this, "profile");
                    if(prof != null) {
                        account = (new Gson()).fromJson(prof,Account.class);
                        if(account != null) {
                            String params = "review[title]=" + HelperClass.encode(title.getText());
                            params += "&review[content]=" + HelperClass.encode(content.getText());
                            params += "&review[star]=" + ratingBar.getRating();
                            params += "&review[product_attributes][title]=" + HelperClass.encode(product.getName());
                            params += "&review[author_attributes][first_name]="+account.getFirst_name();
                            params += "&review[author_attributes][last_name]="+account.getLast_name();
                            params += "&review[author_attributes][email]="+account.getEmail();
                            sendReview(params);
                        }
                    }
                }
            }
        });
    }

    private boolean validateFields(){
        String value;

        value = title.getText().trim();
        if(value.equals("")){
            title.setError("Please enter a title");
            return false;
        }

        value = content.getText().trim();
        if(value.equals("")){
            content.setError("Please enter comments");
            return false;
        }

        return true;
    }

    private void sendReview(String params) {
        HelperClass.hideKeyboard(this);
        dialog.show();
        String url = getString(R.string.review_url) + getString(R.string.api) + "store/"+getString(R.string.store_id)+"/product/"+product.getId()+"/reviews?" + params;
        HelperClass.postData(this, url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(AddReviewActivity.this, "Your review has been saved and sent for approval.", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(KeyValuePair error) {
                dialog.dismiss();
                Toast.makeText(AddReviewActivity.this, error.getKey(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
