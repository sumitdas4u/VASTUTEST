package com.utsavmobileapp.utsavapp.parser;

import android.content.Context;
import android.util.Log;

import com.utsavmobileapp.utsavapp.service.Common;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Bibaswann on 06-04-2017.
 */

public class ParseMultipleChatterJSON {
    public volatile boolean parsingInComplete = true;
    String status, msg;
    int count;
    Context mContext;
    com.utsavmobileapp.utsavapp.service.Common Common;

    private String urlString = null;

    private List<String> uName=new ArrayList<>();
    private List<String> uId=new ArrayList<>();
    private List<String> uGender=new ArrayList<>();
    private List<String> uDistance=new ArrayList<>();
    private List<String> uAge=new ArrayList<>();
    private List<String> uImg=new ArrayList<>();
    private List<String> uTotalPhoto=new ArrayList<>();
    private List<String> uTotalRvw=new ArrayList<>();
    private List<String> uTotalChckIn=new ArrayList<>();
    private List<String> uLastLogin=new ArrayList<>();
    private List<String> uStatus=new ArrayList<>();

    public ParseMultipleChatterJSON(String url, Context context) {
        urlString = url;
        mContext = context;
        Common = new Common(mContext);
    }

    public List<String> getuName() {
        return uName;
    }

    public List<String> getuId() {
        return uId;
    }

    public List<String> getuGender() {
        return uGender;
    }

    public List<String> getuDistance() {
        return uDistance;
    }

    public List<String> getuAge() {
        return uAge;
    }

    public List<String> getuImg() {
        return uImg;
    }

    public List<String> getuTotalPhoto() {
        return uTotalPhoto;
    }

    public List<String> getuTotalRvw() {
        return uTotalRvw;
    }

    public List<String> getuTotalChckIn() {
        return uTotalChckIn;
    }

    public List<String> getuStatus() {
        return uStatus;
    }

    public List<String> getuLastLogin() {
        return uLastLogin;
    }

    private void parseJSONAndStoreIt(String jsonString) {
        try {
            JSONObject jsonRootObject = new JSONObject(jsonString);
            status = jsonRootObject.optString("status");
            msg = jsonRootObject.optString("msg");

            if (status.equals("1")) {
                count = Integer.parseInt(jsonRootObject.optString("count"));
                for (int i = 0; i < count; i++) {
                    String nearUnitStr = jsonRootObject.optString(String.valueOf(i));
                    JSONObject jsonObject = new JSONObject(nearUnitStr);

                    uName.add(jsonObject.optString("user_full_name"));
                    uId.add(jsonObject.optString("user_id"));
                    uDistance.add(jsonObject.optString("user_distance") + "KM");
                    uGender.add(jsonObject.optString("user_gender"));
                    uStatus.add(jsonObject.optString("user_profile_status"));
                    uAge.add(jsonObject.optString("user_age"));
                    uImg.add(jsonObject.optString("user_photo"));
                    uTotalPhoto.add(jsonObject.optString("user_total_photos"));
                    uTotalRvw.add(jsonObject.optString("user_total_review"));
                    uTotalChckIn.add(jsonObject.optString("user_total_check_in"));
                    uLastLogin.add(new SimpleDateFormat("MMMM d, yyyy ',' h:mm a", new Locale("en", "IN")).format(Long.parseLong(jsonObject.optString("user_last_update"))*1000));
                }
            }
            parsingInComplete = false;
        } catch (JSONException e) {
            parsingInComplete = false;
//            Log.e("important", "exception " + Log.getStackTraceString(e));
        }
        parsingInComplete = false;
    }

    public void fetchJSON() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    Log.e("important", urlString);
                    String sb = Common.HttpURLConnection(urlString);
                    //saveData(sb);
                    Log.e("important", "found string " +sb.toString());
                    parseJSONAndStoreIt(sb);
                    // stream.close();
                } catch (Exception e) {
//                    Log.e("important", "exception in reading " + Log.getStackTraceString(e));
                }
            }
        }).start();
    }
}
