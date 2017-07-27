package com.utsavmobileapp.utsavapp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.FacebookSdk;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.utsavmobileapp.utsavapp.fragment.NearFragment;
import com.utsavmobileapp.utsavapp.fragment.PopularFragment;
import com.utsavmobileapp.utsavapp.fragment.StoryFragment;
import com.utsavmobileapp.utsavapp.service.Common;
import com.utsavmobileapp.utsavapp.service.LatLonCachingAPI;
import com.utsavmobileapp.utsavapp.service.LoginAPI;
import com.utsavmobileapp.utsavapp.service.LoginCachingAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, NearFragment.OnFragmentInteractionListener, StoryFragment.OnFragmentInteractionListener, PopularFragment.OnFragmentInteractionListener {

    private static final int LOG_IN_OUT = 5;
    //String currentLat, currentLon;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(22.5624544, 88.4145116), new LatLng(22.5624544, 88.4145116));
    TextView actionBarTxt, usrNm;
    ImageView dp;
    Button loginoutButton;
    String loggedInName = "You are not logged in";
    String loggedInDP;
    LoginAPI lpi;
    LoginCachingAPI lcp;
    AccessTokenTracker accessTokenTracker;
    LatLonCachingAPI llc;
    boolean isLoggedIn;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    com.utsavmobileapp.utsavapp.service.Common Common;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FirebaseAnalytics mFirebaseAnalytics;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actb = getSupportActionBar();
        assert actb != null;
        actb.setDisplayShowCustomEnabled(true);
        View cView = getLayoutInflater().inflate(R.layout.top_search_bar_layout, null);
        Common = new Common(getApplicationContext());

        actionBarTxt = (TextView) cView.findViewById(R.id.addr);
        RelativeLayout locationHolder = (RelativeLayout) cView.findViewById(R.id.locationHolder);
        locationHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location_click(v);
            }
        });

        actb.setCustomView(cView);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        llc = new LatLonCachingAPI(this);
        lcp = new LoginCachingAPI(this);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        assert viewPager != null;
        viewPager.setOffscreenPageLimit(3);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        assert tabLayout != null;
        tabLayout.setupWithViewPager(viewPager);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

//            if (!extras.getString("area").equals("") && !extras.getString("city").equals(""))
//                actionBarTxt.setText(String.format("%s, %s", extras.getString("area"), extras.getString("city")));
//            else
//                actionBarTxt.setText("Where are you exactly?");

            //Log.e("important","oncreate called");
        }

        //Navigation drawer section

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        assert drawer != null;
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);

        lcp = new LoginCachingAPI(this);
        isLoggedIn = lcp.readSetting("login").equals("true");
//        if (!isLoggedIn)
//            isLoggedIn = lcp.readSetting("ask_login").equals("false");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onRestart() {
        if (isLoggedIn)
            showLoggedInUserInfo(lcp.readSetting("name"), lcp.readSetting("photo"), true);
        super.onRestart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.button, menu);

        loginoutButton = (Button) findViewById(R.id.loginoutBtn);
        usrNm = (TextView) findViewById(R.id.usrName);
        dp = (ImageView) findViewById(R.id.dpView);

        new DownloadAddress().execute();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("mode", "login");

//        Log.e("important","isloggedin is "+isLoggedIn);
//        if (!isLoggedIn) {
//            startActivityForResult(intent, LOG_IN_OUT);
//            loginoutButton.setVisibility(View.INVISIBLE);
//        } else {
//            try {
//                if (lcp.readSetting("ask_login").equals("false")) {
//                    loginoutButton.setText("Login");
//                } else {
//                    showLoggedInUserInfo(lcp.readSetting("name"), lcp.readSetting("photo"), true);
//                }
//            } catch (NullPointerException ignored) {
//            }
//        }

        if (isLoggedIn)
            showLoggedInUserInfo(lcp.readSetting("name"), lcp.readSetting("photo"), true);
        else {
            if (lcp.readSetting("ask_login").equals("false")) {
                loginoutButton.setText("Login");
            } else {
                startActivityForResult(intent, LOG_IN_OUT);
                loginoutButton.setVisibility(View.INVISIBLE);
            }
        }

        //if(!lcp.readSetting("fbid").equals("null")) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            }
        };
        //fbAccessToken = AccessToken.getCurrentAccessToken();
        accessTokenTracker.startTracking();
        //}
        return true;
    }

    @Override
    protected void onDestroy() {

        if (accessTokenTracker != null)
            accessTokenTracker.stopTracking();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);

//                Log.e("important", "Place lat: " + place.getLatLng().latitude);
//                Log.e("important", "Place lon: " + place.getLatLng().longitude);

                llc.deleteAllLatLon();
                llc.addLatLon(place.getLatLng().latitude, place.getLatLng().longitude);
                setupViewPager(viewPager);
                new DownloadAddress().execute();

//                Intent i = new Intent(this, ViewAllActivity.class);
//                i.putExtra("type", "near");
//                i.putExtra("lat", String.valueOf(place.getLatLng().latitude));
//                i.putExtra("lon", String.valueOf(place.getLatLng().longitude));
//                startActivity(i);

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                //Log.i("important", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

        if (requestCode == LOG_IN_OUT) {
            if (resultCode == RESULT_OK) {
                if (data.hasExtra("username")) {
                    loggedInName = data.getExtras().getString("username");
                    loggedInDP = data.getExtras().getString("userdp");
                }
                if (data.hasExtra("isloggedin")) {
                    isLoggedIn = data.getExtras().getString("isloggedin").equals("true");
                }

                if (isLoggedIn)
                    showLoggedInUserInfo(loggedInName, loggedInDP, true);
                else
                    showLoggedInUserInfo(loggedInName, loggedInDP, false);
                loginoutButton.setVisibility(View.VISIBLE);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_zone) {
            Intent i = new Intent(this, ZoneActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_favourite) {
            Intent i = new Intent(this, ViewAllActivity.class);
            i.putExtra("type", "favourite");
            startActivity(i);
        } else if (id == R.id.nav_rate) {
            Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "This app is not downloaded from google play", Toast.LENGTH_LONG).show();
            }
        } else if (id == R.id.nav_chat) {
            Intent i = new Intent(this, ChatListActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_profile) {
            Intent i = new Intent(this, MyProfileActivity.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search_button) {
            Intent i = new Intent(this, SearchActivity.class);
            i.putExtra("type", "static");
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    public void location_click(View view) {
/*        Intent i = new Intent(this, SearchActivity.class);
        i.putExtra("type", "google");
        startActivity(i);*/

        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_REGIONS)
                .build();

        try {

            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).setFilter(typeFilter)
                            .setBoundsBias(BOUNDS_MOUNTAIN_VIEW)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }


    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new NearFragment(), "Near Festival");
        adapter.addFragment(new PopularFragment(), "Top Popular");
        adapter.addFragment(new StoryFragment(), "Storyboard");
        viewPager.setAdapter(adapter);
    }

    public void logoutClick(View view) {
        if (isLoggedIn) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("mode", "logout");
            startActivityForResult(intent, LOG_IN_OUT);
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("mode", "login");
            startActivityForResult(intent, LOG_IN_OUT);
        }
    }

    private void showLoggedInUserInfo(String name, String img, boolean login) {
        Log.e("important", "updating dp");
        if (login) {
            usrNm.setText(name);
            Common.ImageDownloaderTask(dp, this, img, "user");
            loginoutButton.setText("Logout");
        } else {
            usrNm.setText("You are not logged in");
            dp.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.profile1));
            loginoutButton.setText("Login");
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private class DownloadAddress extends AsyncTask<Void, Void, String> {


        protected void onPostExecute(String address) {
            if (address != null)
                actionBarTxt.setText(address);
            else
                actionBarTxt.setText("Where are you exactly?");
        }

        @Override
        protected String doInBackground(Void... params) {

            List<Address> addresses = null;


            final String urlString;
            urlString = getString(R.string.uniurl) + "/api/user.php?type=UPDATE&user_id=" + lcp.readSetting("id") + "&lat=" + llc.readLat() + "&long=" + llc.readLng();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String sb = Common.HttpURLConnection(urlString);
                        //saveData(sb);
                        // Log.e("important", "found string " +sb.toString());

                        // stream.close();
                    } catch (Exception e) {
                        //  Log.e("important", "exception in reading " + e.getMessage());
                    }
                }
            }).start();


            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(Double.parseDouble(llc.readLat()), Double.parseDouble(llc.readLng()), 1);
                Address address1 = addresses.get(0);
                ArrayList<String> addressFragments = new ArrayList<String>();

                // Fetch the address lines using getAddressLine,
                // join them, and send them to the thread.
                for (int i = 0; i < address1.getMaxAddressLineIndex(); i++) {
                    addressFragments.add(address1.getAddressLine(i));
                }

                return TextUtils.join(", ", addressFragments);
            } catch (Exception e) {
                // e.printStackTrace();
                return null;
            }

        }

    }
}


