package com.utsavmobileapp.utsavapp.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.LocationResult;

import org.json.JSONObject;


/**
 * Created by Sumit on 10-08-2016.
 */
public class LocationReceiver extends BroadcastReceiver {

    private String TAG = this.getClass().getSimpleName();

    private LocationResult mLocationResult;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Need to check and grab the Intent's extras like so
        if (LocationResult.hasResult(intent)) {
            this.mLocationResult = LocationResult.extractResult(intent);
            Log.i(TAG, "Location Received: " + this.mLocationResult.getLocations().get(0).getLatitude());
            JSONObject jsonObject = new JSONObject();

            try {


                jsonObject.put("latitude", this.mLocationResult.getLocations().get(0).getLatitude());
                jsonObject.put("longitude", this.mLocationResult.getLocations().get(0).getLongitude());


                new LocationWebService(jsonObject, context).execute();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

    }
}
