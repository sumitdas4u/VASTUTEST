package com.utsavmobileapp.utsavapp.parser;

import android.content.Context;

import com.utsavmobileapp.utsavapp.service.Common;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bibaswann on 21-06-2016.
 */
public class ParseSponsoredJSON {

    public volatile boolean parsingInComplete = true;
    String status, msg;
    int count;
    com.utsavmobileapp.utsavapp.service.Common Common;
    private String urlString = null;
    private List<String> policeType = new ArrayList<>();
    private List<String> policeId = new ArrayList<>();
    private List<String> PoliceAddress = new ArrayList<>();
    private List<String> policeDistance = new ArrayList<>();
    private List<String> policePhone = new ArrayList<>();
    private List<String> policeLat = new ArrayList<>();
    private List<String> PoliceLong = new ArrayList<>();
    public ParseSponsoredJSON(String url, Context context)

    {
        urlString = url;
        Common = new Common(context);
    }

    public List<String> getPolicePhone() {
        return policePhone;
    }

    public List<String> getPoliceType() {
        return policeType;
    }

    public List<String> getPoliceId() {
        return policeId;
    }

    public List<String> getPoliceAddress() {
        return PoliceAddress;
    }

    public List<String> getPoliceDistance() {
        return policeDistance;
    }

    public List<String> getPoliceLat() {
        return policeLat;
    }

    public List<String> getPoliceLong() {
        return PoliceLong;
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
                    policeType.add(jsonObject.optString("police_type"));
                    policeId.add(jsonObject.optString("police_id"));
                    PoliceAddress.add(jsonObject.optString("police_address"));
                    policeDistance.add(jsonObject.optString("police_distance") + "Km");
                    policePhone.add(jsonObject.optString("police_phone"));
                    policeLat.add(jsonObject.optString("police_lat"));
                    PoliceLong.add(jsonObject.optString("police_long"));
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


                    //Log.e("important", "found string " +sb.toString());
                    parseJSONAndStoreIt(Common.HttpURLConnection(urlString));
                    //    stream.close();
                } catch (Exception e) {
                    //Log.e("important","exception in reading "+e.getMessage());
                }
            }
        }).start();
    }
}
