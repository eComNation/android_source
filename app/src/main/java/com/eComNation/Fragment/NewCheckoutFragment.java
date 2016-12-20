package com.eComNation.Fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.PurchaseEvent;
import com.crashlytics.android.answers.StartCheckoutEvent;
import com.eComNation.Activity.CheckOutActivity;
import com.eComNation.Activity.ForgotPasswordActivity;
import com.eComNation.Activity.MainActivity;
import com.eComNation.Activity.SignUpActivity;
import com.eComNation.Common.Utility;
import com.eComNation.R;
import com.ecomnationmobile.library.Common.ECNCallback;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Control.CustomEditText;
import com.ecomnationmobile.library.Control.ProgressDialogView;
import com.ecomnationmobile.library.Data.Cart;
import com.ecomnationmobile.library.Data.ECNResponse;
import com.ecomnationmobile.library.Data.KeyValuePair;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * Created by Abhi on 15-03-2016.
 */
public class NewCheckoutFragment extends Fragment implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    View mView;
    boolean redirect, onPageSuccess, facebookLogin, googleLogin;
    WebView mWebView;
    View progressBar, emptyView, loginView;
    ProgressDialog dialog;
    CustomEditText username, password;
    Cart cart;
    CallbackManager callbackManager;
    LoginButton loginButton;

    private static final int RC_GET_TOKEN = 9002;
    private GoogleApiClient mGoogleApiClient;
    SignInButton signInButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getContext());

        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {
                updateWithToken(newAccessToken);
            }
        };

        updateWithToken(AccessToken.getCurrentAccessToken());
        return inflater.inflate(R.layout.web_checkout, container, false);
    }

    private void updateWithToken(AccessToken currentAccessToken) {
        if (currentAccessToken != null) {
            LoginManager.getInstance().logOut();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mView = getView();

        if (mView != null) {
            mWebView = (WebView) mView.findViewById(R.id.webView);
            loginView = mView.findViewById(R.id.loginLayout);
            progressBar = mView.findViewById(R.id.progressBar);
            emptyView = mView.findViewById(R.id.emptyView);

            dialog = new ProgressDialogView(getActivity(),"Please wait...",R.drawable.progress);

            if (HelperClass.isUserLoggedIn(getActivity()))
                showCheckout(null);
            else
                showLogin();
        }
    }

    private void showLogin() {
        loginView.setVisibility(View.VISIBLE);
        mWebView.setVisibility(View.GONE);

        username = (CustomEditText) mView.findViewById(R.id.txtUsername);
        username.init(getString(R.string.username_hint));
        username.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        password = (CustomEditText) mView.findViewById(R.id.txtPassword);
        password.init(getString(R.string.password_hint));
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        if(getResources().getBoolean(R.bool.registration_mandatory)) {
            mView.findViewById(R.id.guestLayout).setVisibility(View.GONE);
        }
        mView.findViewById(R.id.btnGuest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCheckout(null);
            }
        });

        ((TextView) mView.findViewById(R.id.txtNewToStore)).setText(String.format(getString(R.string.signup_message), getString(R.string.app_name)));

        mView.findViewById(R.id.forgotpassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HelperClass.putSharedBoolean(getActivity(), "open_checkout", true);
                startActivity(new Intent(getActivity(), ForgotPasswordActivity.class));
            }
        });

        mView.findViewById(R.id.txtSignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HelperClass.putSharedBoolean(getActivity(), "open_checkout", true);
                startActivity(new Intent(getActivity(), SignUpActivity.class));
            }
        });

        mView.findViewById(R.id.btnlogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String params = "login=";
                String value = username.getText().trim();
                if (!value.equals("") && Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
                    params += value;
                } else {
                    Toast.makeText(getActivity(), "Please enter a valid username", Toast.LENGTH_SHORT).show();
                    return;
                }

                value = password.getText().trim();
                if (!value.equals("")) {
                    params += "&password=" + HelperClass.encode(value);
                } else {
                    Toast.makeText(getActivity(), "Please enter a valid password", Toast.LENGTH_SHORT).show();
                    return;
                }

                params += "&client_id=" + getString(R.string.client_id);
                params += "&secret=" + getString(R.string.client_secret);

                getToken(params);

            }
        });

        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton)mView.findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        loginButton.setFragment(this);


        setSocialLoginButtons();

        if (facebookLogin) {
            mView.findViewById(R.id.btnFacebook).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loginButton.performClick();
                }
            });

            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    dialog.show();
                    String url = getString(R.string.production_base_url) + getString(R.string.api) + "store/customer/accounts/sign_in_with_facebook?access_token=" + loginResult.getAccessToken().getToken() + "&client_id=" + getString(R.string.client_id) + "&secret=" + getString(R.string.client_secret);
                    HelperClass.postData(getActivity(), url, new ECNCallback() {
                        @Override
                        public void onSuccess(String result) {
                            ECNResponse response = (new Gson()).fromJson(result, ECNResponse.class);
                            HelperClass.putSharedBoolean(getActivity(), "can_change_password", false);
                            getCart(response);
                        }

                        @Override
                        public void onFailure(KeyValuePair error) {
                            dialog.dismiss();
                            Toast.makeText(getActivity(), error.getKey(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCancel() {
                    Toast.makeText(getActivity(), "Sign in cancelled by user", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onError(FacebookException exception) {
                    Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }

        if(googleLogin) {
            mView.findViewById(R.id.btnGoogle).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getIdToken();
                }
            });

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.server_client_id))
                    .requestScopes(Plus.SCOPE_PLUS_LOGIN)
                    .requestEmail()
                    .build();
            // [END configure_signin]

            signInButton = (SignInButton) mView.findViewById(R.id.sign_in_button);
            signInButton.setSize(SignInButton.SIZE_STANDARD);
            signInButton.setScopes(gso.getScopeArray());
            signInButton.setOnClickListener(this);

            // Build GoogleAPIClient with the Google Sign-In API and the above options.
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .enableAutoManage(getActivity() /* FragmentActivity */, this /* OnConnectionFailedListener */)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_GET_TOKEN) {
            // [START get_id_token]
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                String idToken;
                if(acct != null)
                    idToken = acct.getIdToken();
                else
                    idToken = null;

                if(idToken != null) {
                    dialog.show();
                    String url = getString(R.string.production_base_url) + getString(R.string.api) + "store/customer/accounts/sign_in_with_google?access_token=" + idToken + "&client_id=" + getString(R.string.client_id) + "&secret=" + getString(R.string.client_secret);
                    HelperClass.postData(getActivity(), url, new ECNCallback() {
                        @Override
                        public void onSuccess(String result) {
                            ECNResponse response = (new Gson()).fromJson(result, ECNResponse.class);
                            HelperClass.putSharedBoolean(getActivity(), "can_change_password", false);
                            getCart(response);
                        }

                        @Override
                        public void onFailure(KeyValuePair error) {
                            dialog.dismiss();
                            Toast.makeText(getActivity(), error.getKey(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                Toast.makeText(getActivity(), "Failed to sign in using google", Toast.LENGTH_SHORT).show();
            }
            // [END get_id_token]
        }

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void getIdToken() {
        // Show an account picker to let the user choose a Google account from the device.
        // If the GoogleSignInOptions only asks for IDToken and/or profile and/or email then no
        // consent screen will be shown here.
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_GET_TOKEN);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Toast.makeText(getActivity(), "Failed to sign in with google", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                getIdToken();
                break;
        }
    }

    private void getToken(String params) {
        HelperClass.hideKeyboard(getActivity());
        dialog.show();
        String url = getString(R.string.production_base_url) + getString(R.string.api) + "store/customer/accounts/sign_in?" + params;
        HelperClass.postData(getActivity(), url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                ECNResponse response = (new Gson()).fromJson(result, ECNResponse.class);
                HelperClass.putSharedBoolean(getActivity(), "can_change_password", true);
                getCart(response);
            }

            @Override
            public void onFailure(KeyValuePair error) {
                dialog.dismiss();
                Toast.makeText(getActivity(), error.getKey(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getCart(ECNResponse response) {
        Cart cart = HelperClass.getCart(getActivity());
        if (cart == null)
            getCustomerCart(response);
        else {
            associate_user_cart(cart, response);
        }
    }

    private void getCustomerCart(final ECNResponse response) {
        String url = getString(R.string.production_base_url) + getString(R.string.api);
        url += "store/cart?access_token=" + response.getAccess_token();

        HelperClass.getData(getActivity(), url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    JSONObject cartObject = (JSONObject) obj.get(getString(R.string.cart));
                    Cart cart = (new Gson()).fromJson(cartObject.toString(), Cart.class);
                    HelperClass.putSharedString(getActivity(), getString(R.string.cart), new Gson().toJson(cart));
                    HelperClass.putSharedString(getActivity(), getString(R.string.old_cart), cartObject.toString());

                    showCheckout(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(KeyValuePair error) {
                if (error.getValue() == 422)
                    showCheckout(response);
                else {
                    dialog.dismiss();
                    Crashlytics.log(error.getKey());
                    Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void associate_user_cart(Cart cart, final ECNResponse response) {
        String url = getString(R.string.production_base_url) + getString(R.string.api);
        url += "store/cart/" + cart.getToken() + "/associate_cart_to_user?access_token=" + response.getAccess_token();

        HelperClass.getData(getActivity(), url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    JSONObject cartObject = (JSONObject) obj.get(getString(R.string.cart));
                    Cart cart = (new Gson()).fromJson(cartObject.toString(), Cart.class);
                    HelperClass.putSharedString(getActivity(), getString(R.string.cart), new Gson().toJson(cart));
                    HelperClass.putSharedString(getActivity(), getString(R.string.old_cart), cartObject.toString());

                    showCheckout(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(KeyValuePair error) {
                dialog.dismiss();
                Crashlytics.log(error.getKey());
                Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showCheckout(ECNResponse response) {
        if (response != null) {
            dialog.dismiss();
            HelperClass.log_in(getActivity(), response);
            Toast.makeText(getActivity(), "Log in successful.", Toast.LENGTH_LONG).show();

            Utility.getProfile(getActivity());
        }

        mWebView.setVisibility(View.VISIBLE);
        loginView.setVisibility(View.GONE);

        onPageSuccess = true;

        redirect = false;

        cart = HelperClass.getCart(getActivity());
        if (cart != null && cart.getToken() != null) {

            int count = HelperClass.getCartCount(getActivity());
            Answers.getInstance().logStartCheckout(new StartCheckoutEvent()
                    .putTotalPrice(BigDecimal.valueOf(cart.getDiscounted_cart_amount()))
                    .putItemCount(count));

            ((CheckOutActivity) getActivity()).updateCartCount(count, false);
            String url = getString(R.string.production_base_url) + "checkouts/init?cart_id=" + cart.getToken();
            url += "&is_mobile=" + true;
            final String access_token = HelperClass.getSharedString(getActivity(), getString(R.string.access_token));
            if (access_token != null)
                url += "&access_token=" + access_token;

            url = url.replace("http:", "https:");

            progressBar.setVisibility(View.VISIBLE);

            mWebView.getSettings().setJavaScriptEnabled(true); // enable javascript
            mWebView.getSettings().setLoadWithOverviewMode(true);
            mWebView.getSettings().setUseWideViewPort(true);
            mWebView.getSettings().setSaveFormData(false);
            mWebView.getSettings().setBuiltInZoomControls(false);
            mWebView.getSettings().setDisplayZoomControls(false);
            mWebView.getSettings().setSupportZoom(false);
            mWebView.getSettings().setDomStorageEnabled(true);
            mWebView.clearCache(true);
            mWebView.clearHistory();
            mWebView.setWebChromeClient(new WebChromeClient());
            mWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(final WebView view, String url) {
                    if (redirect) {
                        openMain();
                        return false;
                    }

                    if (url.contains("checkouts/login")) {
                        url = url.replace("login", "shipping");
                    }

                    if (access_token != null && url.endsWith("email")) {
                        HelperClass.log_out(getActivity());
                        openMain();
                    }

                    if (url.contains("confirm")) {
                        redirect = true;
                        String get_cart_url = getString(R.string.production_base_url) + getString(R.string.api);
                        get_cart_url += "store/cart/" + cart.getToken() + "/get";
                        if (access_token != null)
                            get_cart_url += "?access_token=" + access_token;

                        Answers.getInstance().logPurchase(new PurchaseEvent()
                                .putCurrency(Currency.getInstance("INR"))
                                .putCustomAttribute("Total Price", cart.getDiscounted_cart_amount()));

                        HelperClass.getData(getActivity(), get_cart_url, new ECNCallback() {
                            @Override
                            public void onSuccess(String result) {
                                try {
                                    JSONObject obj = new JSONObject(result);
                                    JSONObject cartObject = (JSONObject) obj.get(getString(R.string.cart));
                                    HelperClass.putSharedString(getActivity(), getString(R.string.cart), cartObject.toString());
                                    HelperClass.putSharedString(getActivity(), getString(R.string.old_cart), cartObject.toString());
                                    ((CheckOutActivity) getActivity()).updateCartCount(0, false);
                                } catch (Exception e) {
                                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(KeyValuePair error) {
                                Toast.makeText(getActivity(), error.getKey(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        ((CheckOutActivity) getActivity()).restrictBackSelection();
                    }

                    view.loadUrl(url);
                    return true;
                }

                @Override
                public void onPageFinished(WebView view, final String url) {
                    if (mWebView.getProgress() == 100 && onPageSuccess) {
                        progressBar.setVisibility(View.GONE);
                    }
                }

                public void onReceivedError(final WebView view, int errorCode, String description, final String failingUrl) {
                    onPageSuccess = false;
                    emptyView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    mWebView.loadUrl("about:blank");
                    ((TextView) emptyView.findViewById(R.id.txtMessage)).setText(getString(R.string.something_went_wrong));
                    emptyView.findViewById(R.id.btnRetry).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            progressBar.setVisibility(View.VISIBLE);
                            emptyView.setVisibility(View.GONE);
                            onPageSuccess = true;
                            view.loadUrl(failingUrl);
                        }
                    });
                }

                public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
                    new AlertDialog.Builder(getActivity())
                            .setMessage(R.string.ssl_cert_invalid)
                            .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    handler.cancel();
                                }
                            })
                            .setNegativeButton("Continue", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    handler.proceed();
                                }
                            }).show();
                }
            });

            HelperClass.ClearCookies(getActivity());

            mWebView.loadUrl(url);
        }
    }

    private void openMain() {
        Intent intentMain = new Intent(getActivity(), MainActivity.class);
        intentMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intentMain.putExtra("show_splash", false);
        startActivity(intentMain);
    }

    private void setSocialLoginButtons() {
        String facebookID = getString(R.string.facebook_app_id);
        String googleID = getString(R.string.server_client_id);

        if (facebookID.equals("") && googleID.equals("")) {
            mView.findViewById(R.id.socialLogin).setVisibility(View.GONE);
            facebookLogin = false;
            googleLogin = false;
        } else {
            if (!facebookID.equals("")) {
                mView.findViewById(R.id.btnFacebook).setVisibility(View.VISIBLE);
                facebookLogin = true;
            }
            if (!googleID.equals("")) {
                mView.findViewById(R.id.btnGoogle).setVisibility(View.VISIBLE);
                googleLogin = true;
            }
        }
        if(facebookLogin && googleLogin)
            mView.findViewById(R.id.socialSpace).setVisibility(View.VISIBLE);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.stopAutoManage(getActivity());
            mGoogleApiClient.disconnect();
        }
    }
}
