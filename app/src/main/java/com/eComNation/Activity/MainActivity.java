package com.eComNation.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.eComNation.Common.Utility;
import com.eComNation.Fragment.HomeFragment;
import com.eComNation.Fragment.HomeMainFragment;
import com.eComNation.Fragment.NavigationDrawerFragment;
import com.eComNation.Fragment.WebHomeFragment;
import com.eComNation.R;
import com.ecomnationmobile.library.Common.AppRater;
import com.ecomnationmobile.library.Common.ECNCallback;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Control.AlertDialogView;
import com.ecomnationmobile.library.Data.AppVersion;
import com.ecomnationmobile.library.Data.Category;
import com.ecomnationmobile.library.Data.ECNResponse;
import com.ecomnationmobile.library.Data.KeyValuePair;
import com.ecomnationmobile.library.Data.Product;
import com.ecomnationmobile.library.Database.DatabaseHelper;
import com.ecomnationmobile.library.Database.DatabaseManager;
import com.google.gson.Gson;

import org.joda.time.DateTime;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    LinearLayout progress;
    String base_url;
    DatabaseHelper dbHelper;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private DrawerLayout mDrawer;
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private Boolean exit = false;
    private int proceedCount = 0;
    boolean isBackAllowed, has_featured_products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        HelperClass.initialize(getApplicationContext());

        base_url = getString(R.string.production_base_url);

        String expires_On = HelperClass.getSharedString(this, "expires_on");
        if (expires_On != null) {
            DateTime expiresOn = new DateTime(expires_On);
            if (expiresOn.isBeforeNow()) {
                Utility.getAccessToken(this);
            }
        }

        Boolean show_splash = getIntent().getBooleanExtra("show_splash", true);
        if (show_splash) {
            showSplash();
        } else
            showMain();
    }

    private void showSplash() {
        setContentView(R.layout.splash_screen);

        switch (getString(R.string.scheme)) {
            case Utility.MINIMAL:
            case Utility.STORYATHOME:
            case Utility.OUTLETSINDIA:
            case Utility.ADDWIK:
            case Utility.KIWIENGLAND:
                HelperClass.putSharedBoolean(this, "show_percent_off", true);
                break;
            default:
                HelperClass.putSharedBoolean(this, "show_percent_off", false);
                break;
        }

        switch (getString(R.string.scheme)) {
            case Utility.MINIMAL:
            case Utility.STORYATHOME:
                HelperClass.putSharedBoolean(this, "show_out_of_stock", true);
                break;
            default:
                HelperClass.putSharedBoolean(this, "show_out_of_stock", false);
                break;
        }

        isBackAllowed = false;

        progress = (LinearLayout) findViewById(R.id.splashProgress);

        getSupportActionBar().hide();
        DatabaseManager.init(this);
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            if (pInfo != null) {
                String url = getString(R.string.production_base_url) + "/api/v1/store/app_version_details?current_app_version=" + pInfo.versionCode + "&device_type=ANDROID";

                HelperClass.getData(this, url, new ECNCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            AppVersion version = (new Gson()).fromJson(result, AppVersion.class);
                            if (version.getUpdate_required() != null && version.getUpdate_required()) {
                                final AlertDialogView adv = new AlertDialogView(MainActivity.this);
                                adv.initColor(R.color.PrimaryColor, R.color.SecondaryColor, R.drawable.store_logo);
                                adv.initText("UPDATE", "Please update the application.", AlertDialogView.UPDATE);
                                adv.getYesButton().setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                        try {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                        } catch (android.content.ActivityNotFoundException anfe) {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                        }
                                        adv.dismissAlertDialog();
                                    }
                                });
                                adv.getNoButton().setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        finish();
                                    }
                                });
                                adv.showAlertDialog();
                            } else {
                                getAllData();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(KeyValuePair error) {
                        getAllData();
                    }
                });
            }
        } catch (PackageManager.NameNotFoundException nnfe) {
            getAllData();
        }
    }

    private void getAllData() {
        proceedCount = 0;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(getString(R.string.pincode_url));
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    System.out.println("Response Code: " + conn.getResponseCode());
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    String str = org.apache.commons.io.IOUtils.toString(in, "UTF-8");
                    HelperClass.putSharedString(MainActivity.this, "pincodes", str);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        String url = base_url + getString(R.string.api) + "store/products/featured?per_page=8&page=1";
        HelperClass.getData(this, url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                ECNResponse response = (new Gson()).fromJson(result, ECNResponse.class);
                List<Product> list = response.getProducts();
                has_featured_products = list != null && !list.isEmpty();
                if(has_featured_products)
                    HelperClass.putSharedString(MainActivity.this, "featured_products", result);
                else
                    HelperClass.putSharedString(MainActivity.this, "featured_products", null);

                proceedCount++;
                if(proceedCount == 2)
                    proceed();
            }

            @Override
            public void onFailure(KeyValuePair error) {
                HelperClass.putSharedString(MainActivity.this, "featured_products", null);
                showError(error);
            }
        });

        url = base_url + getString(R.string.api) + "store/home_page/banners?count=15";
        HelperClass.getData(this, url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                HelperClass.putSharedString(MainActivity.this, "banners", result);
            }

            @Override
            public void onFailure(KeyValuePair error) {
                HelperClass.putSharedString(MainActivity.this, "banners", null);
            }
        });

        url = base_url + getString(R.string.api) + "countries";
        HelperClass.getData(this, url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    String countryString = obj.get("countries").toString();
                    HelperClass.putSharedString(MainActivity.this, "countries", countryString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(KeyValuePair error) {
            }
        });

        url = base_url + getString(R.string.api) + "store/discount_coupons";
        HelperClass.getData(this, url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    HelperClass.putSharedString(MainActivity.this, "discount_coupons", result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(KeyValuePair error) {
            }
        });

        url = base_url + getString(R.string.api) + "store/filter_attributes";
        HelperClass.getData(this, url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                HelperClass.putSharedString(getApplicationContext(), "filter", result);
            }

            @Override
            public void onFailure(KeyValuePair error) {
            }
        });

        url = base_url + getString(R.string.api) + "store/categories";
        HelperClass.getData(this, url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                ECNResponse response = (new Gson()).fromJson(result, ECNResponse.class);
                List<Category> categories = response.getCategories();

                DatabaseManager.getInstance().deleteCategories();
                DatabaseManager.getInstance().addCategories(categories);

                proceedCount++;
                if(proceedCount == 2)
                    proceed();
            }

            @Override
            public void onFailure(KeyValuePair error) {
                showError(error);
            }
        });
    }

    private void proceed() {
        if (getResources().getBoolean(R.bool.show_login_startup) && !HelperClass.isUserLoggedIn(MainActivity.this)) {
            showLogin();
        } else
            showMain();
    }

    private void showError(KeyValuePair error) {
        progress.setVisibility(View.GONE);

        if (!isFinishing()) {
            final AlertDialogView adv = new AlertDialogView(MainActivity.this);
            adv.initColor(R.color.PrimaryColor, R.color.SecondaryColor, R.drawable.store_logo);
            adv.initText("Error", error.getKey(), AlertDialogView.ERROR);
            adv.getYesButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progress.setVisibility(View.VISIBLE);
                    getAllData();
                    adv.dismissAlertDialog();
                }
            });
            adv.getNoButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            adv.showAlertDialog();
        }
    }

    private void showLogin() {
        Intent intentMain = new Intent(MainActivity.this, LoginActivity.class);
        intentMain.putExtra("show_skip", true);
        startActivity(intentMain);
        finish();
    }

    private void showMain() {
        setContentView(R.layout.activity_main);

        isBackAllowed = true;
        HelperClass.initialize(getApplicationContext());

        moveDrawerToTop();

        dbHelper = new DatabaseHelper(getApplicationContext());
        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, mDrawer);

        getSupportActionBar().show();
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.PrimaryColor)));
        onNavigationDrawerItemSelected(-1);

        String access_token = HelperClass.getSharedString(MainActivity.this, getString(R.string.access_token));
        if (access_token != null)
            Utility.getCart(MainActivity.this, access_token);

        AppRater.app_launched(this, getString(R.string.app_name));
        readData(getIntent());
    }

    private void moveDrawerToTop() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDrawer = (DrawerLayout) inflater.inflate(R.layout.drawer_layout, null); // "null" is important.

        // HACK: "steal" the first child of decor view
        ViewGroup decor = (ViewGroup) getWindow().getDecorView();
        View child = decor.getChildAt(0);
        decor.removeView(child);
        LinearLayout container = (LinearLayout) mDrawer.findViewById(R.id.drawer_content); // This is the container we defined just now.
        container.addView(child, 0);

        // Make the drawer replace the first child
        decor.addView(mDrawer);
    }

    private void readData(Intent intent) {
        if (intent != null) {
            Uri data = intent.getData();
            if (data != null) {
                if (data.toString().contains(getString(R.string.product))) {
                    Intent productIntent = Utility.getDetailsIntent(this);
                    productIntent.setData(data);
                    startActivity(productIntent);
                } else {
                    if (HelperClass.getSharedString(this, getString(R.string.access_token)) == null) {
                        HelperClass.putSharedInt(MainActivity.this, "activity_to_open", 0);
                        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                        loginIntent.setData(data);
                        startActivity(loginIntent);
                    }
                }
            }
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        readData(intent);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        if (position == -1) {
            if (getResources().getBoolean(R.bool.show_web_home)) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new WebHomeFragment())
                        .commitAllowingStateLoss();
            } else {
                if (has_featured_products) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, new HomeMainFragment())
                            .commitAllowingStateLoss();
                } else {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, new HomeFragment())
                            .commitAllowingStateLoss();
                }
            }
            setHeaderTitle(position);
        } else {
            Intent intent = Utility.getListingIntent(MainActivity.this,String.valueOf(HelperClass.getSharedLong(this, "category")));
            startActivity(intent);
        }
    }

    public void onItemSelected(int position) {
        switch (position) {
            case 0:
                startActivity(new Intent(this, ContactUsActivity.class));
                break;
            case 1:
                if (HelperClass.isUserLoggedIn(this)) {
                    startActivity(new Intent(this, MyAccountActivity.class));
                } else {
                    HelperClass.putSharedInt(this, "activity_to_open", 0);
                    startActivity(new Intent(this, LoginActivity.class));
                }
                break;
            case 2:
                if (HelperClass.isUserLoggedIn(this)) {
                    final AlertDialogView adv = new AlertDialogView(MainActivity.this);
                    adv.initColor(R.color.PrimaryColor, R.color.SecondaryColor, R.drawable.store_logo);
                    adv.initText("LOG OUT", "Are you sure you want to logout?", AlertDialogView.CONFIRM);
                    adv.getYesButton().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            HelperClass.log_out(MainActivity.this);
                            mNavigationDrawerFragment.setUp(R.id.navigation_drawer, mDrawer);
                            invalidateOptionsMenu();
                            adv.dismissAlertDialog();
                        }
                    });
                    adv.getNoButton().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            adv.dismissAlertDialog();
                        }
                    });
                    adv.showAlertDialog();
                } else {
                    startActivity(new Intent(this, SignUpActivity.class));
                }
                break;
            default:
                String[] menu_items = getResources().getStringArray(R.array.menu_items);
                String[] menu_item_urls = getResources().getStringArray(R.array.menu_item_urls);
                String url = menu_item_urls[position - 3];
                if (url.contains("http"))
                    goToUrl(url);
                else {
                    Intent intentMain = new Intent(this, StaticHTMLActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("name", url);
                    bundle.putString("header", menu_items[position - 3]);
                    intentMain.putExtras(bundle);
                    startActivity(intentMain);
                }
                break;
        }
    }

    public void setHeaderTitle(int position) {
        switch (position) {
            case -1:
                mTitle = getString(R.string.app_name);
                break;
            default:
                mTitle = HelperClass.getSharedString(this, "category");
                break;
        }
    }

    public void onSectionAttached(int number) {
        Intent intent = new Intent();
        switch (number) {
            case 0:
                intent = new Intent(this, MyOrdersActivity.class);
                break;
            case 1:
                intent = new Intent(this, ProfileActivity.class);
                break;
            case 2:
                intent = new Intent(this, AddressesActivity.class);
                break;
            case 3:
                intent = new Intent(this, ContactUsActivity.class);
                break;
            default:
                mTitle = getString(R.string.policies_and_TC);
                break;
        }
        startActivity(intent);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(mTitle);
        }
    }

    private void goToUrl(String url) {
        if (url.equals(""))
            return;

        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mNavigationDrawerFragment != null && !mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.empty, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isBackAllowed) {
            if (exit) {
                finish(); // finish activity
            } else {
                Toast.makeText(this, "Press back again to exit.", Toast.LENGTH_SHORT).show();
                exit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exit = false;
                    }
                }, 3 * 1000);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mNavigationDrawerFragment != null)
            mNavigationDrawerFragment.setUp(R.id.navigation_drawer, mDrawer);

        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("Home Screen"));
    }

    public void animateActionBar(boolean hide) {
        ActionBar actionbar = getSupportActionBar();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static Fragment newInstance(int sectionNumber) {
            return new HomeFragment();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }

        @Override
        public void onAttach(Context activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }
}
