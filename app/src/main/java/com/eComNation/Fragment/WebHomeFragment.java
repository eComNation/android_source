package com.eComNation.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.LayerDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.eComNation.Activity.CheckOutActivity;
import com.eComNation.Activity.ProductListingActivity;
import com.eComNation.Activity.SearchActivity;
import com.eComNation.Common.Utility;
import com.eComNation.R;
import com.ecomnationmobile.library.Common.HelperClass;

/**
 * Created by User on 9/3/2016.
 */
public class WebHomeFragment extends Fragment {

    View mView;
    boolean redirect, onPageSuccess;
    WebView mWebView;
    View progressBar, emptyView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.web_view, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mView = getView();

        if (mView != null) {
            mWebView = (WebView) mView.findViewById(R.id.webView);
            progressBar = mView.findViewById(R.id.progressBar);
            emptyView = mView.findViewById(R.id.emptyView);

            showCheckout();
        }
    }

    private void showCheckout() {
        onPageSuccess = true;

        redirect = false;

        String url = getString(R.string.production_base_url)+"pages/mobile_home";

        //progressBar.setVisibility(View.GONE);
        //emptyView.setVisibility(View.GONE);
        //progressBar.setVisibility(View.VISIBLE);

        mWebView.getSettings().setJavaScriptEnabled(true); // enable javascript
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setSaveFormData(false);
        mWebView.getSettings().setBuiltInZoomControls(false);
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.getSettings().setSupportZoom(false);
        //mWebView.getSettings().setAppCacheMaxSize( 5 * 1024 * 1024 ); // 5MB
        //mWebView.getSettings().setAppCachePath( getActivity().getApplicationContext().getCacheDir().getAbsolutePath() );
        //mWebView.getSettings().setAllowFileAccess( true );
        //mWebView.getSettings().setAppCacheEnabled( true );
        mWebView.getSettings().setCacheMode( WebSettings.LOAD_DEFAULT ); // load online by default

        //if ( !isNetworkAvailable() ) { // loading offline
        //    mWebView.getSettings().setCacheMode( WebSettings.LOAD_CACHE_ELSE_NETWORK );
        //}
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, String url) {
                if (redirect) {
                    return false;
                }

                if (url.contains(".facebook.") || url.contains(".instagram.") || url.contains(".youtube.") || url.contains("twitter.") || url.contains(".google.") || url.contains(".pinterest.")) {
                    goToUrl(url);
                }

                if (url.contains("categories")) {
                    int index = url.lastIndexOf("/");
                    String ID = url.substring(index + 1);
                    Intent newIntent = new Intent(getActivity(), ProductListingActivity.class);
                    if(url.contains("filter")) {
                        newIntent.putExtra("is_filter",true);
                        HelperClass.putSharedString(getActivity(), "filterString", ID);
                    }
                    else {
                        HelperClass.putSharedLong(getActivity(), "category", Long.parseLong(ID));
                        newIntent = Utility.getListingIntent(getActivity(), ID);
                    }
                    startActivity(newIntent);
                }

                if (url.contains("products")) {
                    //intentMain.setData(Uri.parse(url));
                    int index = url.lastIndexOf("/");
                    String ID = url.substring(index + 1);
                    Intent intentMain = Utility.getDetailsIntent(getActivity());
                    Bundle bundle = new Bundle();
                    bundle.putLong("id", Long.parseLong(ID));
                    intentMain.putExtras(bundle);
                    startActivity(intentMain);
                }

                mWebView.stopLoading();
                return false;
            }

            @Override
            public void onPageFinished(WebView view, final String url) {
                if (mWebView.getProgress() == 100 && onPageSuccess) {
                    progressBar.setVisibility(View.GONE);
                    emptyView.setVisibility(View.GONE);
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

        mWebView.loadUrl(url);
        //mWebView.loadUrl("file:///android_asset/index.html");
    }

    private void goToUrl(String url) {
        if (url.equals(""))
            return;

        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE );
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(false);
        HelperClass.putSharedString(getActivity(), getString(R.string.selected_filters), null);
        HelperClass.putSharedInt(getActivity(), "min_price", -1);
        HelperClass.putSharedInt(getActivity(), "max_price", -1);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflate menu
        inflater.inflate(R.menu.home, menu);

        // Get the notifications MenuItem and LayerDrawable (layer-list)
        MenuItem item = menu.findItem(R.id.cart);
        LayerDrawable icon = (LayerDrawable) item.getIcon();

        // Update LayerDrawable's BadgeDrawable
        Utility.setBadgeCount(getActivity(), icon, HelperClass.getCartCount(getActivity()));
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.cart:
                Intent intent = new Intent(getActivity(),CheckOutActivity.class);
                intent.putExtras(new Bundle());
                startActivity(intent);
                return true;
            case R.id.search:
                startActivity(new Intent(getActivity(), SearchActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
