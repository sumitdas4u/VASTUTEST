package com.utsavmobileapp.utsavapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.kml.KmlLayer;
import com.utsavmobileapp.utsavapp.parser.ParseFestivalDetailsJSON;
import com.utsavmobileapp.utsavapp.parser.ParseNearFestivalJSON;
import com.utsavmobileapp.utsavapp.service.Common;
import com.utsavmobileapp.utsavapp.service.LatLonCachingAPI;
import com.utsavmobileapp.utsavapp.service.LoginCachingAPI;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MapsFestivalActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleMap.OnMarkerClickListener,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    String lat, lon;
    String myLat, myLon;
    String fName;
    LatLonCachingAPI llc;
    SupportMapFragment mapFragment;

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    FetchNearFestival FetchNearFestival;
    FetchDetails FetchDetails;
    KmlLayer kmlLayer;
    // RideParameters rideParams;
    //  RideRequestButton requestButton;
    private GoogleMap mMap;
    private com.utsavmobileapp.utsavapp.service.Common Common;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private LoginCachingAPI lcp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_festival_map);
        lcp = new LoginCachingAPI(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        // requestButton = (RideRequestButton) findViewById(R.id.mapUbr);

        // requestButton = (RideRequestButton) findViewById(R.id.mapUbr);
        mapFragment.getMapAsync(this);

        llc = new LatLonCachingAPI(this);
        myLat = llc.readLat();
        myLon = llc.readLng();
        fName = "name";
        String fDistance = "2";
//        btn.setText("Show Direction  ( " + fDistance + " )");

        Common = new Common(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();

        }

        myLon = String.valueOf(location.getLongitude());
        myLat = String.valueOf(location.getLatitude());
/*        lat = String.valueOf(location.getLatitude());
        lon = String.valueOf(location.getLongitude());*/

/*        rideParams = new RideParameters.Builder()
                .setPickupLocation(location.getLatitude(), location.getLongitude(), null, null)
                .setDropoffLocation(Double.parseDouble(lat), Double.parseDouble(lon), fName, fName) //
                .build();

        requestButton.setRideParameters(rideParams);

        requestButton.loadRideInformation();*/
        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");

        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_account_circle_black_18dp));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);

            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        mMap.setTrafficEnabled(true);

        retrieveFileFromUrl();

        LatLng kolkata = new LatLng(Double.parseDouble(myLat), Double.parseDouble(myLon));


        //  mMap.addMarker(new MarkerOptions().position(kolkata).title("Your Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_account_circle_black_18dp)));

        // Add a marker in Sydney and move the camera

     /*   LatLng kolkata = new LatLng(Double.parseDouble(myLat), Double.parseDouble(myLon));
        mCurrLocationMarker = mMap.addMarker(new MarkerOptions().position(kolkata).title("Your Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_account_circle_black_18dp)));
*/
    /*    DistanceDuration distdur = new DistanceDuration(myLat, myLon, lat, lon, mMap);

        distdur.setDistanceDurationOnTextView();*/

        mMap.moveCamera(CameraUpdateFactory.newLatLng(kolkata));

        mMap.moveCamera(CameraUpdateFactory.zoomTo(15f));

    /*    SessionConfiguration config = new SessionConfiguration.Builder()
                .setClientId("daICWnPqs49oMCQVinaZ4iIzj4wX-1xG") //This is necessary
                .setEnvironment(SessionConfiguration.Environment.SANDBOX)
                .setServerToken("O2QWEJx4lthB9eMzfdZwAD6tEx9LmkCe_DKmlBGr")
                .setScopes(Arrays.asList(Scope.PROFILE, Scope.RIDE_WIDGETS))
                .build();
        UberSdk.initialize(config);


//        SessionConfiguration config1 = new SessionConfiguration.Builder().setServerToken("").build();
        ServerTokenSession session = new ServerTokenSession(config);

        rideParams = new RideParameters.Builder()
                .setPickupLocation(Double.parseDouble(llc.readLat()), Double.parseDouble(llc.readLng()), null, null)
                .setDropoffLocation(Double.parseDouble(lat), Double.parseDouble(lon), fName, fName) //
                .build();

        requestButton.setRideParameters(rideParams);
        requestButton.setSession(session);
        requestButton.loadRideInformation();*/
        mMap.setOnMarkerClickListener(this);
        try {
            if (!FetchNearFestival.isCancelled()) {
                FetchNearFestival.cancel(true);
            }


        } catch (NullPointerException ignored) {

        }

        FetchNearFestival = new FetchNearFestival();
        FetchNearFestival.execute();

    }

    public void showDirection(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?saddr=" + myLat + "," + myLon + "&daddr=" + lat + "," + lon));
        startActivity(intent);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        String clickCount = (String) marker.getTag();

        // Check if a click count was set, then display the click count.

         /*   Toast.makeText(this,
                    marker.getTitle() +
                            " has been clicked " + clickCount + " times.",
                    Toast.LENGTH_SHORT).show();*/
        try {

            if (!FetchDetails.isCancelled()) {
                FetchDetails.cancel(true);
            }

        } catch (NullPointerException ignored) {
        }
        if (clickCount != null) {
            FetchDetails = new FetchDetails();
            FetchDetails.execute(clickCount);
        }

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).

        return false;
    }

    private void retrieveFileFromUrl() {
        new DownloadKmlFile(getString(R.string.kml_url)).execute();


    }

    public class FetchNearFestival extends AsyncTask<Void, Void, Void> {


        ProgressBar progress;


        Common Common;
        private List<String> fName = new ArrayList<>();
        private List<String> fId = new ArrayList<>();
        private List<String> lat = new ArrayList<>();
        private List<String> lon = new ArrayList<>();

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            ParseNearFestivalJSON prnfj;
            String fetchUrl;
            fetchUrl = getApplicationContext().getString(R.string.uniurl) + "/api/festival.php?lat=" + myLat + "&long=" + myLon + "&type=LISTING&page=0&limit=500";
            Log.e("important", "url of sponsored is " + fetchUrl);
            prnfj = new ParseNearFestivalJSON(fetchUrl, getApplicationContext());
            prnfj.fetchJSON();
            while (prnfj.parsingInComplete && (!this.isCancelled())) ;
            fName = prnfj.getfName();
            fId = prnfj.getfId();
            lat = prnfj.getLat();
            lon = prnfj.getLon();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {


            List<Marker> markers = new ArrayList<>();
            for (final String id : fId) {
                final int index = fId.indexOf(id);
                if (index == 0)
                    continue;

                try {
                    LatLng loc = new LatLng(Double.parseDouble(lat.get(index)), Double.parseDouble(lon.get(index)));
                    Marker marker = mMap.addMarker(new MarkerOptions().position(loc).title(fName.get(index)).icon(BitmapDescriptorFactory.fromResource(R.drawable.logo))); //...

                    marker.setTag(fId.get(index));
                    markers.add(marker);
                } catch (Exception e) {

                }


            }

            this.cancel(true);
            super.onPostExecute(aVoid);
        }
    }

    class FetchDetails extends AsyncTask<String, Void, Void> {
        ParseFestivalDetailsJSON festivalObject;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {


            //  if(common.isLoggedIn(DetailsActivity.this))
            festivalObject = new ParseFestivalDetailsJSON(getString(R.string.uniurl) + "/api/festival.php?type=SINGLE&id=" + params[0] + "&lat=" + myLat + "&long=" + myLon + "&user_id=" + lcp.readSetting("id"), getApplicationContext());
            //   else
            //  festivalObject = new ParseFestivalDe`tailsJSON(getString(R.string.uniurl) + "/api/festival.php?type=SINGLE&id=" + fId + "&lat=" + lcp.readLat() + "&long=" + lcp.readLng(), DetailsActivity.this);
            festivalObject.fetchJSON();
            while (festivalObject.parsingInComplete) ;

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            RelativeLayout detailsLayout = (RelativeLayout) findViewById(R.id.detailsLayout);

            detailsLayout.setVisibility(View.VISIBLE);
            ImageView img = (ImageView) findViewById(R.id.imageFestival);
            Common.ImageDownloaderTask(img, getApplicationContext(), festivalObject.getfImg(), "festival");

            TextView name = (TextView) findViewById(R.id.textView6);
            name.setText(festivalObject.getfName());

            TextView addr = (TextView) findViewById(R.id.textView8);
            addr.setText(festivalObject.getfAddress());

            TextView dist = (TextView) findViewById(R.id.textView17);
            dist.setText(festivalObject.getfDistance());

            TextView rat = (TextView) findViewById(R.id.textView24);
            rat.setText(festivalObject.getfRating());


            detailsLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(), DetailsActivity.class);
                    i.putExtra("id", festivalObject.getfId());
                    i.putExtra("name", festivalObject.getfName());
                    startActivity(i);
                }
            });


            this.cancel(true);
            super.onPostExecute(aVoid);
        }
    }

    private class DownloadKmlFile extends AsyncTask<String, Void, byte[]> {
        private final String mUrl;

        public DownloadKmlFile(String url) {
            mUrl = url;
        }

        protected byte[] doInBackground(String... params) {
            try {
                InputStream is = new URL(mUrl).openStream();
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead;
                byte[] data = new byte[16384];
                while ((nRead = is.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                buffer.flush();
                try {
//                ProgressDialog   dialog= ProgressDialog.show(getApplicationContext(), "Loading", "Please wait...", true);
                    // Log.e("important", "post");
                    kmlLayer = new KmlLayer(mMap, new ByteArrayInputStream(buffer.toByteArray()),
                            getApplicationContext());
                    //  moveCameraToKml(kmlLayer);
                } catch (XmlPullParserException | IOException e) {
                    Log.e("important", "error" + e.getMessage());

                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        protected void onPostExecute(byte[] byteArr) {


            if (lcp.readSetting("map_block").equals("null")) {
                try {
                    kmlLayer.addLayerToMap();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //     dialog.dismiss();
            }


        }
    }

}
