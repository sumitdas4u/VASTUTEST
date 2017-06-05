package com.utsavmobileapp.utsavapp;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bibaswann on 31-05-2016.
 */
public class SearchHistoryCaching {
    Context mContext;
    private SharedPreferences sharedSettings;

    public SearchHistoryCaching(Context context) {
        mContext = context;
        sharedSettings = mContext.getSharedPreferences(mContext.getString(R.string.history_file_name), Context.MODE_PRIVATE);
    }

    public String readSetting(String key) {
        return sharedSettings.getString(key, "na");
    }

    public List<String> readAllCachedPandal() {
        List<String> suggestionList = new ArrayList<>();
        if (!readSetting("pandal5").equals("na"))
            suggestionList.add(readSetting("pandal5"));
        if (!readSetting("pandal4").equals("na"))
            suggestionList.add(readSetting("pandal4"));
        if (!readSetting("pandal3").equals("na"))
            suggestionList.add(readSetting("pandal3"));
        if (!readSetting("pandal2").equals("na"))
            suggestionList.add(readSetting("pandal2"));
        if (!readSetting("pandal2").equals("na"))
            suggestionList.add(readSetting("pandal1"));

        return suggestionList;
    }

    public void addUpdateSettingsPandal(String value) {
        SharedPreferences.Editor editor = sharedSettings.edit();
        if (readSetting("pandal1").equals("na"))
            editor.putString("pandal1", value);
        else if (readSetting("pandal2").equals("na"))
            editor.putString("pandal2", value);
        else if (readSetting("pandal3").equals("na"))
            editor.putString("pandal3", value);
        else if (readSetting("pandal4").equals("na"))
            editor.putString("pandal4", value);
        else if (readSetting("pandal5").equals("na"))
            editor.putString("pandal5", value);
        else {
            editor.putString("pandal1", readSetting("pandal2"));
            editor.putString("pandal2", readSetting("pandal3"));
            editor.putString("pandal3", readSetting("pandal4"));
            editor.putString("pandal4", readSetting("pandal5"));
            editor.putString("pandal5", value);
        }
        editor.apply();
    }

    public List<String> readAllCachedPlace() {
        List<String> suggestionList = new ArrayList<>();
        if (!readSetting("place5").equals("na"))
            suggestionList.add(readSetting("place5"));
        if (!readSetting("place4").equals("na"))
            suggestionList.add(readSetting("place4"));
        if (!readSetting("place3").equals("na"))
            suggestionList.add(readSetting("place3"));
        if (!readSetting("place2").equals("na"))
            suggestionList.add(readSetting("place2"));
        if (!readSetting("place2").equals("na"))
            suggestionList.add(readSetting("place1"));

        return suggestionList;
    }

    public void addUpdateSettingsPlace(String value) {
        SharedPreferences.Editor editor = sharedSettings.edit();
        if (readSetting("place1").equals("na"))
            editor.putString("place1", value);
        else if (readSetting("place2").equals("na"))
            editor.putString("place2", value);
        else if (readSetting("place3").equals("na"))
            editor.putString("place3", value);
        else if (readSetting("place4").equals("na"))
            editor.putString("place4", value);
        else if (readSetting("place5").equals("na"))
            editor.putString("place5", value);
        else {
            editor.putString("place1", readSetting("place2"));
            editor.putString("place2", readSetting("place3"));
            editor.putString("place3", readSetting("place4"));
            editor.putString("place4", readSetting("place5"));
            editor.putString("place5", value);
        }
        editor.apply();
    }

    public void deleteAllSettings() {
        sharedSettings.edit().clear().apply();
    }
}
