package com.utsavmobileapp.utsavapp.parser;

import android.content.Context;
import android.util.Log;

import com.utsavmobileapp.utsavapp.service.Common;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bibaswann on 10-05-2016.
 */
public class ParseNearPeopleJSON {
    public volatile boolean parsingInComplete = true;
    String status, msg;
    int count;
    Context mContext;
    com.utsavmobileapp.utsavapp.service.Common Common;
    private String urlString = null;
    private List<String> uName = new ArrayList<>();
    private List<String> uId = new ArrayList<>();
    private List<String> uGender = new ArrayList<>();
    private List<String> uDistance = new ArrayList<>();
    private List<String> uAge = new ArrayList<>();
    private List<String> uImg = new ArrayList<>();

    public ParseNearPeopleJSON(String url, Context context) {
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
                    uAge.add(jsonObject.optString("user_age"));
                    uImg.add(jsonObject.optString("user_photo"));
                }
            }
            parsingInComplete = false;
        } catch (JSONException e) {
            parsingInComplete = false;
            Log.e("important", "exception " + Log.getStackTraceString(e));
        }
        parsingInComplete = false;
    }

    public void fetchJSON() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String sb = Common.HttpURLConnection(urlString);
                    //saveData(sb);
                    //Log.e("important", "found string " +sb.toString());
                    parseJSONAndStoreIt(sb);
                    // stream.close();
                } catch (Exception e) {
                    Log.e("important", "exception in reading " + e.getMessage());
                }
            }
        }).start();
    }

    public void saveData(String data) {
        mContext.deleteFile("near_people");
        FileOutputStream outputStream;
        try {
            outputStream = mContext.openFileOutput("near_people", Context.MODE_PRIVATE);
            outputStream.write(data.getBytes());
            outputStream.close();
        } catch (Exception e) {
        }
    }
}
