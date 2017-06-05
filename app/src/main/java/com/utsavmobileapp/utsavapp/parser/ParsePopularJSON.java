package com.utsavmobileapp.utsavapp.parser;

import android.content.Context;
import android.util.Log;

import com.utsavmobileapp.utsavapp.service.Common;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bibaswann on 13-07-2016.
 */
public class ParsePopularJSON {
    public volatile boolean parsingInComplete = true;
    String status, msg;
    int count;
    Context mContext;
    com.utsavmobileapp.utsavapp.service.Common Common;
    private String urlString = null;
    private List<String> fName = new ArrayList<>();
    private List<String> fId = new ArrayList<>();
    private List<String> fAddress = new ArrayList<>();
    private List<String> fDistance = new ArrayList<>();
    private List<String> fRating = new ArrayList<>();
    private List<String> fImg = new ArrayList<>();

    public ParsePopularJSON(String url, Context context) {
        urlString = url;
        mContext = context;
        Common = new Common(mContext);
    }

    public List<String> getfName() {
        return fName;
    }

    public List<String> getfId() {
        return fId;
    }

    public List<String> getfAddress() {
        return fAddress;
    }

    public List<String> getfDistance() {
        return fDistance;
    }

    public List<String> getfRating() {
        return fRating;
    }

    public List<String> getfImg() {
        return fImg;
    }

    private void parseJSONAndStoreIt(String jsonString) {
        try {
            JSONObject jsonRootObject = new JSONObject(jsonString);
            status = jsonRootObject.optString("status");
            msg = jsonRootObject.optString("msg");

            if (status.equals("1")) {
                count = Integer.parseInt(jsonRootObject.optString("count"));
                //Log.e("important",count+" items found");
                if (count > 0) {
                    for (int i = 0; i < count; i++) {
                        String nearUnitStr = jsonRootObject.optString(String.valueOf(i));
                        JSONObject jsonObject = new JSONObject(nearUnitStr);
                        fName.add(jsonObject.optString("festival_name"));
                        fId.add(jsonObject.optString("festival_id"));
                        fAddress.add(jsonObject.optString("festival_address"));
                        fDistance.add(jsonObject.optString("festival_distance") + "KM");
                        fRating.add(jsonObject.optString("festival_rating"));
                        fImg.add(jsonObject.optString("festival_primary_image"));
                        //Log.e("important",fName.size()+" is current size");
                    }
                }
            }
            parsingInComplete = false;
        } catch (JSONException e) {
            parsingInComplete = false;
            //Log.e("important", "exception " + Log.getStackTraceString(e));
        }
        parsingInComplete = false;
    }

    public void fetchJSON() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    //Log.e("important", "found string " +sb.toString()+" for "+urlString);
                    parseJSONAndStoreIt(com.utsavmobileapp.utsavapp.service.Common.HttpURLConnection(urlString));
                    //stream.close();
                } catch (Exception e) {
                    Log.e("important", "exception in reading " + e.getMessage());
                }
            }
        }).start();
    }
}
