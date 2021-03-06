package com.eComNation.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.eComNation.Activity.MainActivity;
import com.eComNation.Adapter.DrawerListAdapter;
import com.eComNation.Common.Utility;
import com.eComNation.R;
import com.ecomnationmobile.library.Common.HelperClass;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    //private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private static NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private static View rootView;
    private static ViewFlipper mViewFlipper;
    private static DrawerListAdapter mDrawerAdapter;
    private static DrawerLayout mDrawerLayout;
    private static ListView mDrawerListView;
    private static View mFragmentContainerView;

    private static int mCurrentSelectedPosition = -1;
    private List<String> mDrawerlist;
    private static View store_drawer_view;

    private ScrollView scrollView;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has comnstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
//        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
        }
        // Select either the default item (0) or the last selected item.
        //selectItem(mCurrentSelectedPosition,1);

        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        return rootView;
    }

    public void setUpDrawer() {
        mViewFlipper = (ViewFlipper) rootView.findViewById(R.id.viewFlipper);

        scrollView = (ScrollView) rootView.findViewById(R.id.scrollView);

        switch (getString(R.string.scheme)) {
            case Utility.STORYATHOME:
                rootView.findViewById(R.id.imgHomeScreen).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectItem(-1);
                    }
                });
                break;
            default:
                rootView.findViewById(R.id.imgHomeScreen).setVisibility(View.GONE);
                break;
        }


        mDrawerListView = (ListView) rootView.findViewById(R.id.drawerList);
        mDrawerlist = new ArrayList<>();

        mDrawerlist.add(getString(R.string.contact_us));

        if (HelperClass.isUserLoggedIn(getActivity())) {
            mDrawerlist.add(getString(R.string.my_account));
            mDrawerlist.add("Log Out");
        }
        else {
            mDrawerlist.add("Login");
            mDrawerlist.add("Sign Up");
        }

        String[] array = getResources().getStringArray(R.array.menu_items);
        if(array.length > 0) {
            for(String str: array) {
                mDrawerlist.add(str);
            }
        }

        mDrawerAdapter = new DrawerListAdapter(getActivity(), R.layout.store_drawer_item, new ArrayList<Object>(mDrawerlist));

        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                selectStaticItem(position);
            }
        });

        mDrawerListView.setAdapter(mDrawerAdapter);

        HelperClass.justifyListViewHeightBasedOnChildren(mDrawerListView);
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        if(getActivity() != null) {
            mFragmentContainerView = getActivity().findViewById(fragmentId);
            mDrawerLayout = drawerLayout;

            // set a custom shadow that overlays the main content when the drawer opens
            mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
            // set up the drawer's list view with items and click listener

            ActionBar actionBar = getActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.icn_menu);

            // ActionBarDrawerToggle ties together the the proper interactions
            // between the navigation drawer and the action bar app icon.
            mDrawerToggle = new ActionBarDrawerToggle(
                    getActivity(),                    /* host Activity */
                    mDrawerLayout,                    /* DrawerLayout object */
                    R.drawable.icn_menu,             /* nav drawer image to replace 'Up' caret */
                    R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                    R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
            ) {
                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                    if (!isAdded()) {
                        return;
                    }
                    scrollView.scrollTo(0,0);
                    mDrawerToggle.setHomeAsUpIndicator(R.drawable.icn_menu);
                    getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
                }

                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    if (!isAdded()) {
                        return;
                    }
                    mDrawerToggle.setHomeAsUpIndicator(R.drawable.icn_back);

/*                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }*/

                    HelperClass.hideKeyboard(getActivity());
                    //getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()

                }
            };

            // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
            // per the navigation drawer design guidelines.
/*        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }*/

            // Defer code dependent on restoration of previous instance state.
            mDrawerLayout.post(new Runnable() {
                @Override
                public void run() {
                    mDrawerToggle.syncState();
                }
            });

            mDrawerLayout.setDrawerListener(mDrawerToggle);

            setUpDrawer();
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (mDrawerLayout != null && isDrawerOpen()) {
            menu.clear();
        }
    }

    public static void onStoreItemSelected() {
        LayoutInflater inflator1 = (LayoutInflater)((Activity)mCallbacks).getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        store_drawer_view = inflator1.inflate(R.layout.store_drawer_fragment, null);
        if(mViewFlipper != null) {
            int currentStoreLevel = HelperClass.getSharedInt((Activity) mCallbacks,"menu_level");
            ++currentStoreLevel;
            HelperClass.putSharedInt((Activity) mCallbacks,"menu_level",currentStoreLevel);
            mViewFlipper.addView(store_drawer_view);
            mViewFlipper.setInAnimation(AnimationUtils.loadAnimation((Activity) mCallbacks, R.anim.left_in));
            mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation((Activity) mCallbacks, R.anim.left_out));
            mViewFlipper.showNext();
        }
    }

    public static void onBackSelected() {
        if(mViewFlipper != null) {
            int currentStoreLevel = HelperClass.getSharedInt((Activity) mCallbacks,"menu_level");
            mViewFlipper.setInAnimation(AnimationUtils.loadAnimation((Activity) mCallbacks, R.anim.right_in));
            mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation((Activity) mCallbacks, R.anim.right_out));
            mViewFlipper.showPrevious();
            mViewFlipper.removeViewAt(currentStoreLevel--);
            HelperClass.putSharedInt((Activity) mCallbacks, "menu_level", currentStoreLevel);
        }
    }

    public static void selectItem(final int position) {
        if(mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(rootView);
            mDrawerLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mCurrentSelectedPosition = position;
                    if (mDrawerListView != null) {
                        mDrawerListView.setItemChecked(position, true);
                    }
                    if (mDrawerLayout != null) {
                        mDrawerLayout.closeDrawer(mFragmentContainerView);
                    }
                    if (mCallbacks != null) {
                        mCallbacks.onNavigationDrawerItemSelected(position);
                    }
                }
            }, 300);
        }
    }

    public static void selectStaticItem(final int position) {
        if(mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(rootView);
            mDrawerLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mCurrentSelectedPosition = position;
                    if (mDrawerListView != null) {
                        mDrawerListView.setItemChecked(position, true);
                    }
                    if (mDrawerLayout != null) {
                        mDrawerLayout.closeDrawer(mFragmentContainerView);
                    }
                    if (mCallbacks != null) {
                        ((MainActivity)mCallbacks).onItemSelected(position);
                    }
                }
            }, 300);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FragmentManager fragManager = this.getFragmentManager();
        Fragment fragment = fragManager.findFragmentById(R.id.mainDrawer);
        if(fragment!=null){
            fragManager.beginTransaction().remove(fragment).commit();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallbacks = (NavigationDrawerCallbacks) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.empty, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }
}
