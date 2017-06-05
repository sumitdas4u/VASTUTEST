package com.utsavmobileapp.utsavapp.service;

import android.content.Context;
import android.content.SharedPreferences;

import com.utsavmobileapp.utsavapp.R;

/**
 * Created by Bibaswann on 11-06-2016.
 */
public class LoginCachingAPI {
    private static SharedPreferences sharedSettings;
    Context mContext;

    public LoginCachingAPI(Context context) {
        mContext = context;
        sharedSettings = mContext.getSharedPreferences(mContext.getString(R.string.user_data_file_name), Context.MODE_PRIVATE);
    }

    public String readSetting(String key) {
        try {
            return sharedSettings.getString(key, "null");
        } catch (NullPointerException ignored) {
            return null;
        }

    }

    public void addUpdateSettings(String key, String value) {
        SharedPreferences.Editor editor = sharedSettings.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void deleteAllSettings() {
        sharedSettings.edit().clear().apply();
    }
}
