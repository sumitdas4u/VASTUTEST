package com.utsavmobileapp.utsavapp.service;

import android.content.Context;
import android.content.SharedPreferences;

import com.utsavmobileapp.utsavapp.R;

/**
 * Created by Bibaswann on 03-05-2017.
 */

public class ChatCachingAPI {
    Context mContext;
    private SharedPreferences sharedSettings;

    public ChatCachingAPI(Context context) {
        mContext = context;
        sharedSettings = mContext.getSharedPreferences(mContext.getString(R.string.chat_file_name), Context.MODE_PRIVATE);
    }

    public String readCount(String id) {
        return sharedSettings.getString(id, "0");
    }


    public void addUpdateCount(String id, String count) {
        SharedPreferences.Editor editor = sharedSettings.edit();
        editor.putString(id, count);
        editor.apply();
    }
}
