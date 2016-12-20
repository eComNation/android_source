package com.eComNation.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.eComNation.Common.Utility;
import com.eComNation.R;
import com.ecomnationmobile.library.Common.ECNCallback;
import com.ecomnationmobile.library.Common.EComNationActivity;
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

/**
 * Created by Abhi on 20/7/15.
 */
public class LoginActivity extends EComNationActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    Boolean show_skip;
    View skipLayout;
    CustomEditText username, password;
    ProgressDialog dialog;
    String base_url;
    boolean facebookLogin, googleLogin;

    CallbackManager callbackManager;
    LoginButton loginButton;

    private static final int RC_GET_TOKEN = 9002;
    private GoogleApiClient mGoogleApiClient;
    SignInButton signInButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {
                updateWithToken(newAccessToken);
            }
        };

        updateWithToken(AccessToken.getCurrentAccessToken());

        dialog = new ProgressDialogView(this, "Please wait...", R.drawable.progress);

        base_url = getString(R.string.production_base_url);

        readData(getIntent());

        nativeLogin();
    }

    private void updateWithToken(AccessToken currentAccessToken) {
        if (currentAccessToken != null) {
            LoginManager.getInstance().logOut();
        }
    }

    private void nativeLogin() {
        setContentView(R.layout.login);

        skipLayout = findViewById(R.id.skipLayout);
        if(show_skip)
            skipLayout.setVisibility(View.VISIBLE);

        findViewById(R.id.btnSkip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);

        username = (CustomEditText) findViewById(R.id.txtUsername);
        username.init(getString(R.string.username_hint));
        username.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        password = (CustomEditText) findViewById(R.id.txtPassword);
        password.init(getString(R.string.password_hint));
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        ((TextView) findViewById(R.id.txtNewToStore)).setText(String.format(getString(R.string.signup_message), getString(R.string.app_name)));

        findViewById(R.id.forgotpassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });

        findViewById(R.id.txtSignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });

        findViewById(R.id.btnlogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String params = "login=";
                String value = username.getText().trim();
                if (!value.equals("") && Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
                    params += value;
                } else {
                    Toast.makeText(LoginActivity.this, "Please enter a valid username", Toast.LENGTH_SHORT).show();
                    return;
                }

                value = password.getText().trim();
                if (!value.equals("")) {
                    params += "&password=" + HelperClass.encode(value);
                } else {
                    Toast.makeText(LoginActivity.this, "Please enter a valid password", Toast.LENGTH_SHORT).show();
                    return;
                }

                params += "&client_id=" + getString(R.string.client_id);
                params += "&secret=" + getString(R.string.client_secret);

                getToken(params);

            }
        });

        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");

        setSocialLoginButtons();

        if(facebookLogin) {
            findViewById(R.id.btnFacebook).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loginButton.performClick();
                }
            });

            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    dialog.show();
                    String url = base_url + getString(R.string.api) + "store/customer/accounts/sign_in_with_facebook?access_token=" + loginResult.getAccessToken().getToken() + "&client_id=" + getString(R.string.client_id) + "&secret=" + getString(R.string.client_secret);
                    HelperClass.postData(LoginActivity.this, url, new ECNCallback() {
                        @Override
                        public void onSuccess(String result) {
                            ECNResponse response = (new Gson()).fromJson(result, ECNResponse.class);
                            HelperClass.putSharedBoolean(LoginActivity.this, "can_change_password", false);
                            getCart(response);
                        }

                        @Override
                        public void onFailure(KeyValuePair error) {
                            dialog.dismiss();
                            Toast.makeText(LoginActivity.this, error.getKey(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCancel() {
                    Toast.makeText(LoginActivity.this, "Sign in cancelled by user", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onError(FacebookException exception) {
                    Toast.makeText(LoginActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }

        if(googleLogin) {
            findViewById(R.id.btnGoogle).setOnClickListener(new View.OnClickListener() {
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

            signInButton = (SignInButton) findViewById(R.id.sign_in_button);
            signInButton.setSize(SignInButton.SIZE_STANDARD);
            signInButton.setScopes(gso.getScopeArray());
            signInButton.setOnClickListener(this);

            // Build GoogleAPIClient with the Google Sign-In API and the above options.
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_GET_TOKEN) {
            // [START get_id_token]
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                String idToken;
                if (acct != null)
                    idToken = acct.getIdToken();
                else
                    idToken = null;

                if (idToken != null) {
                    if (!dialog.isShowing())
                        dialog.show();
                    String url = base_url + getString(R.string.api) + "store/customer/accounts/sign_in_with_google?access_token=" + idToken + "&client_id=" + getString(R.string.client_id) + "&secret=" + getString(R.string.client_secret);
                    HelperClass.postData(LoginActivity.this, url, new ECNCallback() {
                        @Override
                        public void onSuccess(String result) {
                            ECNResponse response = (new Gson()).fromJson(result, ECNResponse.class);
                            HelperClass.putSharedBoolean(LoginActivity.this, "can_change_password", false);
                            getCart(response);
                        }

                        @Override
                        public void onFailure(KeyValuePair error) {
                            dialog.dismiss();
                            Toast.makeText(LoginActivity.this, error.getKey(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                Toast.makeText(LoginActivity.this, "Failed to sign in using google", Toast.LENGTH_SHORT).show();
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
        Toast.makeText(LoginActivity.this, "Failed to sign in with google", Toast.LENGTH_LONG).show();
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
        HelperClass.hideKeyboard(this);
        dialog.show();
        String url = base_url + getString(R.string.api) + "store/customer/accounts/sign_in?" + params;
        HelperClass.postData(this, url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                ECNResponse response = (new Gson()).fromJson(result, ECNResponse.class);
                HelperClass.putSharedBoolean(LoginActivity.this, "can_change_password", true);
                getCart(response);
            }

            @Override
            public void onFailure(KeyValuePair error) {
                dialog.dismiss();
                Toast.makeText(LoginActivity.this, error.getKey(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getCart(ECNResponse response) {
        Cart cart = HelperClass.getCart(this);
        if (cart == null)
            getCustomerCart(response);
        else {
            associate_user_cart(cart, response);
        }
    }

    private void getCustomerCart(final ECNResponse response) {
        String url = getString(R.string.production_base_url) + getString(R.string.api);
        url += "store/cart?access_token=" + response.getAccess_token();

        HelperClass.getData(this, url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    JSONObject cartObject = (JSONObject) obj.get(getString(R.string.cart));
                    Cart cart = (new Gson()).fromJson(cartObject.toString(), Cart.class);
                    HelperClass.putSharedString(LoginActivity.this, getString(R.string.cart), new Gson().toJson(cart));
                    HelperClass.putSharedString(LoginActivity.this, getString(R.string.old_cart), cartObject.toString());

                    startNewActivity(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(KeyValuePair error) {
                if (error.getValue() == 422)
                    startNewActivity(response);
                else {
                    dialog.dismiss();
                    Crashlytics.log(error.getKey());
                    Toast.makeText(LoginActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void associate_user_cart(Cart cart, final ECNResponse response) {
        String url = getString(R.string.production_base_url) + getString(R.string.api);
        url += "store/cart/" + cart.getToken() + "/associate_cart_to_user?access_token=" + response.getAccess_token();

        HelperClass.getData(this, url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    JSONObject cartObject = (JSONObject) obj.get(getString(R.string.cart));
                    Cart cart = (new Gson()).fromJson(cartObject.toString(), Cart.class);
                    HelperClass.putSharedString(LoginActivity.this, getString(R.string.cart), new Gson().toJson(cart));
                    HelperClass.putSharedString(LoginActivity.this, getString(R.string.old_cart), cartObject.toString());

                    startNewActivity(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(KeyValuePair error) {
                dialog.dismiss();
                Crashlytics.log(error.getKey());
                Toast.makeText(LoginActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void startNewActivity(ECNResponse response) {
        HelperClass.log_in(LoginActivity.this, response);
        Toast.makeText(this, "Log in successful.", Toast.LENGTH_LONG).show();

        Utility.getProfile(this);

        Intent intentMain = new Intent(LoginActivity.this, MyAccountActivity.class);
        switch (HelperClass.getSharedInt(this, "activity_to_open")) {
            case 1:
                intentMain = new Intent(LoginActivity.this, CheckOutActivity.class);
                break;
            case 2:
                finish();
                return;
            default:
                break;
        }
        finish();
        startActivity(intentMain);
    }

    private void readData(Intent intent) {
        if (intent != null) {
            show_skip = intent.getBooleanExtra("show_skip", false);
            Uri data = intent.getData();
            if (data != null) {
                if (HelperClass.getSharedString(this, getString(R.string.access_token)) == null) {
                    if (data.toString().contains("signup")) {
                        String token = data.getQueryParameter("activation_token");
                        String login = data.getQueryParameter("login");
                        if (token != null && !token.equals(""))
                            activateAccount(token, login);
                    } else if (data.toString().contains("forgotpassword")) {
                        String token = data.getQueryParameter("token");
                        Intent forgotIntent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                        forgotIntent.putExtra("token", token);
                        startActivity(forgotIntent);
                    }
                }
            }
        }
    }

    public void activateAccount(String token, String login) {
        dialog.show();
        String url = base_url + getString(R.string.api) + "store/customer/accounts/activate?activation_token=" + token + "&login=" + login;
        HelperClass.putData(this, url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                dialog.dismiss();
            }

            @Override
            public void onFailure(KeyValuePair error) {
                dialog.dismiss();
                Toast.makeText(LoginActivity.this, error.getKey(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setSocialLoginButtons() {
        String facebookID = getString(R.string.facebook_app_id);
        String googleID = getString(R.string.server_client_id);

        if (facebookID.equals("") && googleID.equals("")) {
            findViewById(R.id.socialLogin).setVisibility(View.GONE);
            facebookLogin = false;
            googleLogin = false;
        } else {
            if (!facebookID.equals("")) {
                findViewById(R.id.btnFacebook).setVisibility(View.VISIBLE);
                facebookLogin = true;
            }
            if (!googleID.equals("")) {
                findViewById(R.id.btnGoogle).setVisibility(View.VISIBLE);
                googleLogin = true;
            }
        }
        if(facebookLogin && googleLogin)
            findViewById(R.id.socialSpace).setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (show_skip) {
            Intent intentMain = new Intent(LoginActivity.this, MainActivity.class);
            intentMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intentMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intentMain.putExtra("show_splash", false);
            finish();
            startActivity(intentMain);
        } else {
            super.onBackPressed();
        }
    }
}
