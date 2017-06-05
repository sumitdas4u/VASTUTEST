package com.utsavmobileapp.utsavapp.parser;

import android.content.Context;

import com.utsavmobileapp.utsavapp.service.Common;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bibaswann on 31-05-2016.
 */
public class ParseSearchResult {

    public volatile boolean parsingInComplete = true;
    String status, msg;
    int count;

    List<String> fId = new ArrayList<>();
    List<String> fName = new ArrayList<>();
    List<String> fDist = new ArrayList<>();
    List<String> fRat = new ArrayList<>();
    List<String> fAddr = new ArrayList<>();
    List<String> fSimg = new ArrayList<>();
    com.utsavmobileapp.utsavapp.service.Common Common;
    private String urlString = null;

    public ParseSearchResult(String url, Context context)

    {
        urlString = url;
        Common = new Common(context);
    }

    public String getStatus() {
        return status;
    }

    public List<String> getfId() {
        return fId;
    }

    public List<String> getfName() {
        return fName;
    }

    public List<String> getfSimg() {
        return fSimg;
    }

    public List<String> getfAddr() {
        return fAddr;
    }

    public List<String> getfDist() {
        return fDist;
    }

    public List<String> getfRat() {
        return fRat;
    }

    private void parseJSONAndStoreIt(String jsonString) {
        try {
            JSONObject jsonRootObject = new JSONObject(jsonString);

            status = jsonRootObject.optString("status");
            msg = jsonRootObject.optString("msg");

            if (status.equals("1")) {
                count = Integer.parseInt(jsonRootObject.optString("count"));
                for (int i = 0; i < count; i++) {
                    String searchUnitStr = jsonRootObject.optString(String.valueOf(i));
                    JSONObject jsonSrchUnitObj = new JSONObject(searchUnitStr);
                    fId.add(jsonSrchUnitObj.optString("festival_id"));
                    fName.add(jsonSrchUnitObj.optString("festival_name"));
                    fSimg.add(jsonSrchUnitObj.optString("festival_small_image"));
                    fAddr.add(jsonSrchUnitObj.optString("festival_address"));
                    fDist.add(jsonSrchUnitObj.optString("festival_distance") + "KM");
                    fRat.add(jsonSrchUnitObj.optString("festival_rating"));
                    //Log.e("important","got url: "+jsonSrchUnitObj.optString("festival_xx_small_image"));
                }
            } else {
                fId.add("0");
                fName.add("No result found");
                fSimg.add("noimg");
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


                    //Log.e("important", "found string " +sb.toString());
                    parseJSONAndStoreIt(Common.HttpURLConnection(urlString));
                    //  stream.close();
                } catch (Exception e) {
                    //Log.e("important","exception in reading "+e.getMessage());
                }
            }
        }).start();
    }
}
