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
public class ParseTopPopularJSON {

    public volatile boolean parsingInComplete = true;
    String status, msg;
    int count;
    com.utsavmobileapp.utsavapp.service.Common Common;
    List<String> popularInWhere = new ArrayList<>();
    List<String> zoneId = new ArrayList<>();
    Context mContext;
    private String urlString = null;
    private List<List<String>> fName = new ArrayList<>();
    private List<List<String>> fId = new ArrayList<>();
    private List<List<String>> fAddress = new ArrayList<>();
    private List<List<String>> fDistance = new ArrayList<>();
    private List<List<String>> fRating = new ArrayList<>();
    private List<List<String>> fImg = new ArrayList<>();

    public ParseTopPopularJSON(String url, Context context) {
        urlString = url;
        mContext = context;
        Common = new Common(mContext);
    }

    public List<String> getPopularInWhere() {
        return popularInWhere;
    }

    public List<String> getZoneId() {
        return zoneId;
    }

    public List<List<String>> getfName() {
        return fName;
    }

    public List<List<String>> getfId() {
        return fId;
    }

    public List<List<String>> getfAddress() {
        return fAddress;
    }

    public List<List<String>> getfDistance() {
        return fDistance;
    }

    public List<List<String>> getfRating() {
        return fRating;
    }

    public List<List<String>> getfImg() {
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
                    int subcount = 0;
                    String popUnitStr = jsonRootObject.optString(String.valueOf(i));
                    JSONObject jsonObject = new JSONObject(popUnitStr);
                    String popIn = jsonObject.optString("name");
                    popularInWhere.add(popIn);
                    zoneId.add(jsonObject.optString("zone_id"));
                    JSONObject jsonArray = new JSONObject(jsonObject.optString(popIn));
                    if (jsonArray.optString("count").length() > 0)
                        subcount = Integer.parseInt(jsonArray.optString("count"));
                    List<String> id = new ArrayList<>();
                    List<String> name = new ArrayList<>();
                    List<String> address = new ArrayList<>();
                    List<String> distance = new ArrayList<>();
                    List<String> rating = new ArrayList<>();
                    List<String> img = new ArrayList<>();

                    for (int j = 0; j < subcount; j++) {
                        String subUnitStr = jsonArray.optString(String.valueOf(j));
                        JSONObject jsonFObject = new JSONObject(subUnitStr);

                        id.add(jsonFObject.optString("festival_id"));
                        name.add(jsonFObject.optString("festival_name"));
                        //Log.e("important","adding "+jsonFObject.optString("festival_name"));
                        address.add(jsonFObject.optString("festival_address"));
                        distance.add(jsonFObject.optString("festival_distance") + "KM");
                        rating.add(jsonFObject.optString("festival_rating"));
                        img.add(jsonFObject.optString("festival_primary_image"));
                    }

                    fId.add(id);
                    fName.add(name);
                    fAddress.add(address);
                    fDistance.add(distance);
                    fRating.add(rating);
                    fImg.add(img);
                }
            }
            parsingInComplete = false;
        } catch (JSONException ex) {
            //Log.e("important", "exception " + Log.getStackTraceString(ex));
        }
        parsingInComplete = false;
    }

    public void fetchJSON() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {


                    //saveData(sb.toString());
//                    Log.e("important", urlString);
                    //Log.e("important", "found string " +sb.toString());
                    parseJSONAndStoreIt(Common.HttpURLConnection(urlString));
                    // stream.close();
                } catch (Exception e) {
                    //Log.e("important","exception in reading "+e.getMessage());
                }
            }
        }).start();
    }

    public void saveData(String data) {
        mContext.deleteFile("top_popular");
        FileOutputStream outputStream;
        try {
            outputStream = mContext.openFileOutput("top_popular", Context.MODE_PRIVATE);
            outputStream.write(data.getBytes());
            outputStream.close();
        } catch (Exception e) {
        }
    }
}
