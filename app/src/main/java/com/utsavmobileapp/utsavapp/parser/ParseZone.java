package com.utsavmobileapp.utsavapp.parser;

import android.content.Context;

import com.utsavmobileapp.utsavapp.service.Common;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bibaswann on 13-07-2016.
 */
public class ParseZone {
    public volatile boolean parsingInComplete = true;
    String status, msg;
    int count;
    Context mContext;
    com.utsavmobileapp.utsavapp.service.Common Common;
    private String urlString = null;
    private List<String> zid = new ArrayList<>();
    private List<String> zoname = new ArrayList<>();

    public ParseZone(String url, Context context) {
        urlString = url;
        mContext = context;
        zid.clear();
        zoname.clear();
        Common = new Common(mContext);
    }

    public List<String> getZid() {
        return zid;
    }

    public List<String> getZoname() {
        return zoname;
    }

    private void parseJSONAndStoreIt(String jsonString) {
        try {
            JSONObject jsonRootObject = new JSONObject(jsonString);
            status = jsonRootObject.optString("status");
            msg = jsonRootObject.optString("msg");

            if (status.equals("1")) {
                count = Integer.parseInt(jsonRootObject.optString("count"));
                //Log.e("important",count+" items found");
                for (int i = 0; i < count; i++) {
                    String nearUnitStr = jsonRootObject.optString(String.valueOf(i));
                    JSONObject jsonObject = new JSONObject(nearUnitStr);
                    zid.add(jsonObject.optString("zone_id"));
                    zoname.add(jsonObject.optString("zone_name"));
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
                    parseJSONAndStoreIt(Common.HttpURLConnection(urlString));
                    //stream.close();
                } catch (Exception e) {
                    //Log.e("important","exception in reading "+e.getMessage());
                }
            }
        }).start();
    }
}
