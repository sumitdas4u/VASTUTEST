package com.utsavmobileapp.utsavapp.parser;

import android.content.Context;

import com.utsavmobileapp.utsavapp.service.Common;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bibaswann on 10-05-2016.
 */
public class ParseNearFestivalJSON {

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
    private List<String> lat = new ArrayList<>();
    private List<String> lon = new ArrayList<>();

    public ParseNearFestivalJSON(String url, Context context) {
        urlString = url;
        mContext = context;
        Common = new Common(mContext);
    }

    public ParseNearFestivalJSON(String url) {
        urlString = url;
    }

    public List<String> getLat() {
        return lat;
    }

    public void setLat(List<String> lat) {
        this.lat = lat;
    }

    public List<String> getLon() {
        return lon;
    }

    public void setLon(List<String> lon) {
        this.lon = lon;
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
                for (int i = 0; i < count; i++) {
                    String nearUnitStr = jsonRootObject.optString(String.valueOf(i));
                    JSONObject jsonObject = new JSONObject(nearUnitStr);
                    fName.add(jsonObject.optString("festival_name"));
                    fId.add(jsonObject.optString("festival_id"));
                    fAddress.add(jsonObject.optString("festival_address"));
                    fDistance.add(jsonObject.optString("festival_distance") + "KM");
                    lat.add(jsonObject.optString("festival_lat"));
                    lon.add(jsonObject.optString("festival_long"));
                    fRating.add(jsonObject.optString("festival_rating"));
                    fImg.add(jsonObject.optString("festival_primary_image"));
                }
            }

            parsingInComplete = false;
        } catch (JSONException e) {
            //Log.e("important", "exception " + Log.getStackTraceString(e));
            parsingInComplete = false;
        }
        parsingInComplete = false;
    }

    public void fetchJSON() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    parseJSONAndStoreIt(Common.HttpURLConnection(urlString));
                    //stream.close();
                } catch (Exception e) {

                    //Log.e("important","exception in reading "+e.getMessage());
                }
            }
        }).start();
    }

    public void saveData(String data) {
        mContext.deleteFile("near_festival");
        FileOutputStream outputStream;
        try {
            outputStream = mContext.openFileOutput("near_festival", Context.MODE_PRIVATE);
            outputStream.write(data.getBytes());
            outputStream.close();
        } catch (Exception e) {
        }
    }
}
