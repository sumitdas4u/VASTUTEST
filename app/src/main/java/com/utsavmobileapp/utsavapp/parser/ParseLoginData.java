package com.utsavmobileapp.utsavapp.parser;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Bibaswann on 11-06-2016.
 */
public class ParseLoginData {

    public volatile boolean parsingInComplete = true;
    String status, msg;
    int count;
    Context mContext;
    private String userId;
    private String userFBId;
    private String userGoogleId;
    private String userNAme;
    private String userSmallPhoto;
    private String userAge;
    private String userGender;
    private String userIsLoggedIn;

    public String getUserAge() {
        return userAge;
    }

    public String getUserFBId() {
        return userFBId;
    }

    public String getUserGender() {
        return userGender;
    }

    public String getUserGoogleId() {
        return userGoogleId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserNAme() {
        return userNAme;
    }

    public String getUserSmallPhoto() {
        return userSmallPhoto;
    }

    public String getUserIsLoggedIn() {
        return userIsLoggedIn;
    }

    public void parseJSONAndStoreIt(String jsonString) {
        try {
            JSONObject jsonRootObject = new JSONObject(jsonString);
            status = jsonRootObject.optString("status");
            msg = jsonRootObject.optString("msg");
            userIsLoggedIn = jsonRootObject.optString("user_is_login");

            if (status.equals("1")) {
                String loginDataStr = jsonRootObject.optString("0");
                JSONObject jsonObject = new JSONObject(loginDataStr);

                userId = jsonObject.optString("user_id");
                userFBId = jsonObject.optString("user_fb_id");
                userGoogleId = jsonObject.optString("user_google_id");
                userNAme = jsonObject.optString("user_full_name");
                userSmallPhoto = jsonObject.optString("user_small_photo");
                userAge = jsonObject.optString("user_age");
                userGender = jsonObject.optString("user_gender");
            }
            parsingInComplete = false;
        } catch (JSONException e) {
            parsingInComplete = false;
            //Log.e("important", "exception " + Log.getStackTraceString(e));
        }
        parsingInComplete = false;
    }
}
