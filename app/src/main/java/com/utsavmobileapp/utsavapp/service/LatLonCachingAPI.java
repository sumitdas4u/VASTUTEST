package com.utsavmobileapp.utsavapp.service;

import android.content.Context;
import android.content.SharedPreferences;

import com.utsavmobileapp.utsavapp.R;

/**
 * Created by Bibaswann on 24-06-2016.
 */
public class LatLonCachingAPI {
    Context mContext;
    private SharedPreferences sharedSettings;

    public LatLonCachingAPI(Context context) {
        mContext = context;
        sharedSettings = mContext.getSharedPreferences(mContext.getString(R.string.lat_lon_file_name), Context.MODE_PRIVATE);
    }

    public String readLat() {
        return sharedSettings.getString("lat", "22.5726");
    }

    public String readLng() {
        return sharedSettings.getString("lon", "88.3639");
    }

    public void addLatLon(Double lat, Double lon) {
        SharedPreferences.Editor editor = sharedSettings.edit();
        editor.putString("lat", String.valueOf(lat));
        editor.putString("lon", String.valueOf(lon));
        editor.apply();
    }

    public void deleteAllLatLon() {
        sharedSettings.edit().clear().apply();
    }
}
