package com.utsavmobileapp.utsavapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.cocosw.bottomsheet.BottomSheet;
import com.utsavmobileapp.utsavapp.service.LatLonCachingAPI;
import com.utsavmobileapp.utsavapp.service.LoginCachingAPI;

import java.util.concurrent.RejectedExecutionException;

import com.utsavmobileapp.utsavapp.fetch.FetchFavourite;
import com.utsavmobileapp.utsavapp.fetch.FetchNearFestival;
import com.utsavmobileapp.utsavapp.fetch.FetchPopular;

public class ViewAllActivity extends AppCompatActivity {
    LoginCachingAPI lcp;
    String uid;
    String lat = "22", lon = "88";
    String popPage = "0", popLimit = "5";
    String zoneId;
    com.utsavmobileapp.utsavapp.fetch.FetchFavourite FetchFavourite;
    com.utsavmobileapp.utsavapp.fetch.FetchPopular FetchPopular;
    com.utsavmobileapp.utsavapp.fetch.FetchNearFestival FetchNearFestival;
    ProgressBar progressBar;
    LinearLayout linearList;
    Bundle extras;
    private ScrollView parent;

    @Override
    public void onDestroy() {
        try {
            FetchFavourite.cancel(true);
            FetchNearFestival.cancel(true);
            FetchPopular.cancel(true);
        } catch (Exception e) {

        }


        super.onDestroy();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = (ProgressBar) findViewById(R.id.allProgress);
        linearList = (LinearLayout) findViewById(R.id.allHolder);
        parent = (ScrollView) findViewById(R.id.parentScrollView);
        extras = getIntent().getExtras();
        showViewAll(extras, null, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_all, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_sort) {
            new BottomSheet.Builder(ViewAllActivity.this).title("Sort by").grid().sheet(R.menu.sort_by).listener(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case R.id.sort_distance_asc:
                            showViewAll(extras, "ASC", "festival_distance");
                            break;
            /*            case R.id.sort_distance_desc:
                            showViewAll(extras, "DESC", "festival_distance");
                            break;
                        case R.id.sort_rating_asc:
                            showViewAll(extras, "ASC", "festival_rating");
                            break;*/
                        case R.id.sort_rating_desc:
                            showViewAll(extras, "DESC", "festival_rating");
                            break;
                    }
                }
            }).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showViewAll(Bundle extras, @Nullable final String sortBy, @Nullable final String orderBy) {
        LatLonCachingAPI llc = new LatLonCachingAPI(getApplicationContext());

        //Log.e("important","calling "+sortBy+" and "+orderBy);
        String allType = "near";
        if (extras != null) {
            allType = extras.getString("type");
        }
        switch (allType) {
            case "favourite":
                lcp = new LoginCachingAPI(this);
                uid = lcp.readSetting("id");
                if (uid.equals("na")) {
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.putExtra("mode", "login");
                    startActivity(intent);
                    finish();
                } else {
                    FetchFavourite = (FetchFavourite) new FetchFavourite(this, uid, progressBar, linearList, popPage, popLimit, sortBy, orderBy, true).execute();

                    linearList.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                        @Override
                        public void onScrollChanged() {
                            //Log.e("important",listView.getChildCount()+" items are in listview");
                            if (parent.getChildCount() > 0) {
                                View view = parent.getChildAt(parent.getChildCount() - 1);
                                // Calculate the scrolldiff
                                int diff = (view.getBottom() - (parent.getHeight() + parent.getScrollY()));
                                // if diff is zero, then the bottom has been reached
                                if (diff == 0) {
                                    // notify that we have reached the bottom
                                    popPage = String.valueOf(Integer.parseInt(popPage) + 1);

                                    new FetchFavourite(ViewAllActivity.this, uid, progressBar, linearList, popPage, popLimit, sortBy, orderBy, false).execute();
                                }
                            }
                        }
                    });
                }
                break;
            case "popular":
                zoneId = extras.getString("zoneid");

                FetchPopular = (FetchPopular) new FetchPopular(this, zoneId, progressBar, linearList, popPage, popLimit, sortBy, orderBy, true).execute();

                linearList.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged() {
                        //Log.e("important",listView.getChildCount()+" items are in listview");
                        if (parent.getChildCount() > 0) {

                            View view = parent.getChildAt(parent.getChildCount() - 1);
                            // Calculate the scrolldiff
                            int diff = (view.getBottom() - (parent.getHeight() + parent.getScrollY()));
                            // if diff is zero, then the bottom has been reached
                            if (diff == 0) {
                                // notify that we have reached the bottom
                                popPage = String.valueOf(Integer.parseInt(popPage) + 1);
                                try {
                                    new FetchPopular(ViewAllActivity.this, zoneId, progressBar, linearList, popPage, popLimit, sortBy, orderBy, false).execute();
                                } catch (RejectedExecutionException ignored) {
                                }
                            }
                        }
                    }
                });
                break;
            case "near":
                if (extras.getString("lat") != null && extras.getString("lon") != null) {
                    lat = extras.getString("lat");
                    lon = extras.getString("lon");
                } else {
                    lat = llc.readLat();
                    lon = llc.readLng();
                }
                FetchNearFestival = (FetchNearFestival) new FetchNearFestival(this, linearList, progressBar, popPage, popLimit, lat, lon, true, sortBy, orderBy, true, false).execute();

                linearList.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged() {
                        //Log.e("important",listView.getChildCount()+" items are in listview");
                        if (parent.getChildCount() > 0) {
                            View view = parent.getChildAt(parent.getChildCount() - 1);
                            // Calculate the scrolldiff
                            int diff = (view.getBottom() - (parent.getHeight() + parent.getScrollY()));
                            // if diff is zero, then the bottom has been reached
                            if (diff == 0) {
                                // notify that we have reached the bottom
                                popPage = String.valueOf(Integer.parseInt(popPage) + 1);
                                try {
                                    new FetchNearFestival(ViewAllActivity.this, linearList, progressBar, popPage, popLimit, lat, lon, true, sortBy, orderBy, false, false).execute();
                                } catch (RejectedExecutionException ignored) {

                                }
                            }
                        }
                    }
                });
                break;
            default:
                break;
        }
    }
}
