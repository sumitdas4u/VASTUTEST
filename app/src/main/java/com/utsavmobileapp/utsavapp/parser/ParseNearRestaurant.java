package com.utsavmobileapp.utsavapp.parser;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.utsavmobileapp.utsavapp.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Bibaswann on 28-07-2016.
 */
public class ParseNearRestaurant {
    public volatile boolean parsingInComplete = true;
    String status, msg;
    int count;
    Context mContext;
    private String urlString = null;
    private List<String> rName = new ArrayList<>();
    private List<String> rId = new ArrayList<>();
    private List<String> rAddress = new ArrayList<>();
    private List<String> rLink = new ArrayList<>();
    private List<String> rRating = new ArrayList<>();
    private List<String> rImg = new ArrayList<>();

    public ParseNearRestaurant(String url, Context context) {
        urlString = url;
        mContext = context;
    }

    public List<String> getrAddress() {
        return rAddress;
    }

    public List<String> getrLink() {
        return rLink;
    }

    public List<String> getrId() {
        return rId;
    }

    public List<String> getrImg() {
        return rImg;
    }

    public List<String> getrName() {
        return rName;
    }

    public List<String> getrRating() {
        return rRating;
    }

    private void parseJSONAndStoreIt(String jsonString) {

        try {
            JSONObject jsonRootObject = new JSONObject(jsonString);
            JSONArray restArr = jsonRootObject.getJSONArray("restaurants");
            for (int j = 0; j < restArr.length(); j++) {
                String reStr1 = restArr.optString(j);
                JSONObject restObj1 = new JSONObject(reStr1);
                String reStr = restObj1.optString("restaurant");
                JSONObject restObj = new JSONObject(reStr);

                String ratStr = restObj.optString("user_rating");
                JSONObject rat = new JSONObject(ratStr);
                if (rat.optString("aggregate_rating").equals("0"))
                    continue;
                rRating.add(rat.optString("aggregate_rating"));
                if (restObj.optString("thumb").length() < 10)
                    continue;
                rImg.add(restObj.optString("thumb"));

                rName.add(restObj.optString("name"));
                rId.add(restObj.optString("id"));

                String locStr = restObj.optString("location");
                JSONObject loc = new JSONObject(locStr);
                rAddress.add(loc.optString("address"));

                rLink.add(restObj.optString("url"));
            }

            parsingInComplete = false;
        } catch (JSONException e) {
            //Log.e("important", "exception " + Log.getStackTraceString(e));
        }
        parsingInComplete = false;
    }

    public void fetchJSON() {
        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlString, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        parseJSONAndStoreIt(response.toString());

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                parsingInComplete = false;
            }
        }) {

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("user-key", "2b165325e043959b3cb46dd0023eb00a");
                return headers;
            }

        };

// Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);


    }


}
