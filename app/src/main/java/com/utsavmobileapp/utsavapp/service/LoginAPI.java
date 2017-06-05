package com.utsavmobileapp.utsavapp.service;

import android.content.Context;
import android.content.SharedPreferences;

import com.utsavmobileapp.utsavapp.R;

/**
 * Created by Bibaswann on 26-05-2016.
 */
public class LoginAPI {
    private Context mContext;
    private SharedPreferences sharedSettings;

    public LoginAPI(Context context) {
        mContext = context;
        sharedSettings = mContext.getSharedPreferences(mContext.getString(R.string.fb_file_name), Context.MODE_MULTI_PROCESS);
    }

    public String readName() {
        String name = sharedSettings.getString("com.facebook.ProfileManager.CachedProfile", "na");
        if (!name.equals("na"))
            return name.replace("&quot;", "").split(",")[4].replace("\"", "").split(":")[1];
        else
            return "You are logged in";
    }

    public void deleteAllSettings() {
        sharedSettings.edit().clear().commit();
    }
}
