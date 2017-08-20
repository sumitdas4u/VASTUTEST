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
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

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
import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.android.rides.RideParameters;
import com.uber.sdk.android.rides.RideRequestButton;
import com.uber.sdk.core.auth.Scope;
import com.uber.sdk.rides.client.ServerTokenSession;
import com.uber.sdk.rides.client.SessionConfiguration;
import com.utsavmobileapp.utsavapp.service.DistanceDuration;
import com.utsavmobileapp.utsavapp.service.LatLonCachingAPI;
import com.utsavmobileapp.utsavapp.service.LoginCachingAPI;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleMap.OnMarkerClickListener,
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
    RideParameters rideParams;
    RideRequestButton requestButton;
    KmlLayer kmlLayer;
    private GoogleMap mMap;
    private LoginCachingAPI lcp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        Button btn = (Button) findViewById(R.id.directionBtn);
        requestButton = (RideRequestButton) findViewById(R.id.mapUbr);
        mapFragment.getMapAsync(this);
        lat = getIntent().getExtras().getString("lat");
        lon = getIntent().getExtras().getString("lon");
        llc = new LatLonCachingAPI(this);
        myLat = llc.readLat();
        myLon = llc.readLng();
        lcp = new LoginCachingAPI(this);
        fName = getIntent().getExtras().getString("name");
        String fDistance = getIntent().getExtras().getString("distance");
        btn.setText("Show Direction  ( " + fDistance + " )");

       /* final ToggleButton btnBlock = (ToggleButton) findViewById(R.id.btnBlock);
        if (lcp.readSetting("map_block").equals("null")) {
            btnBlock.setChecked(false);
        } else {
            btnBlock.setChecked(true);
        }
        btnBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnBlock.isChecked()) {
                    lcp.addUpdateSettings("map_block", "true");

                    try {
                        kmlLayer.removeLayerFromMap();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                } else {
                    lcp.addUpdateSettings("map_block", "null");
                    try {
                        kmlLayer.addLayerToMap();
                    } catch (IOException | XmlPullParserException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }


            }
        });*/

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
        rideParams = new RideParameters.Builder()
                .setPickupLocation(location.getLatitude(), location.getLongitude(), null, null)
                .setDropoffLocation(Double.parseDouble(lat), Double.parseDouble(lon), fName, fName) //
                .build();

        requestButton.setRideParameters(rideParams);

        requestButton.loadRideInformation();
        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
     /*   MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");

        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);*/

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

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
    public boolean onMarkerClick(final Marker marker) {

        // Retrieve the data from the marker.
        Integer clickCount = (Integer) marker.getTag();

        // Check if a click count was set, then display the click count.
        if (clickCount != null) {
            clickCount = clickCount + 1;
            marker.setTag(clickCount);
            Toast.makeText(this,
                    marker.getTitle() +
                            " has been clicked " + clickCount + " times.",
                    Toast.LENGTH_SHORT).show();
        }

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        try {
            LatLng sydney = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
            mMap.addMarker(new MarkerOptions().position(sydney).title(fName).icon(BitmapDescriptorFactory.fromResource(R.drawable.logo)));

            LatLng kolkata = new LatLng(Double.parseDouble(myLat), Double.parseDouble(myLon));
            mMap.addMarker(new MarkerOptions().position(kolkata).title("Your Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_account_circle_black_18dp)));

            DistanceDuration distdur = new DistanceDuration(myLat, myLon, lat, lon, mMap);
            distdur.setDistanceDurationOnTextView();

            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            mMap.setTrafficEnabled(true);
        } catch (Exception ignored) {


        }
        // Add a marker in Sydney and move the camera


        mMap.moveCamera(CameraUpdateFactory.zoomTo(15f));

        // Set a listener for marker click.
        mMap.setOnMarkerClickListener(this);

        SessionConfiguration config = new SessionConfiguration.Builder()
                .setClientId("daICWnPqs49oMCQVinaZ4iIzj4wX-1xG") //This is necessary
                .setEnvironment(SessionConfiguration.Environment.PRODUCTION)
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
        requestButton.loadRideInformation();
        //retrieveFileFromUrl();
    }

    public void showDirection(View view) {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?saddr=" + myLat + "," + myLon + "&daddr=" + lat + "," + lon));
        startActivity(intent);
    }

    private void retrieveFileFromUrl() {
        //new DownloadKmlFile(getString(R.string.kml_url)).execute();
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
                } catch (IOException | XmlPullParserException e) {
                    e.printStackTrace();
                }
                //     dialog.dismiss();
            }


        }
    }
}
