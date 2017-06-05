package com.utsavmobileapp.utsavapp.parser;

import android.content.Context;

import com.utsavmobileapp.utsavapp.service.Common;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bibaswann on 08-06-2016.
 */
public class ParseStoryComments {

    public volatile boolean parsingInComplete = true;
    String status, msg;
    int count;
    List<String> usrName, usrCmt, usrDp, usrAgo, usrId;
    com.utsavmobileapp.utsavapp.service.Common Common;
    private String urlString = null;

    public ParseStoryComments(String url, Context mContext) {
        urlString = url;
        Common = new Common(mContext);
        usrName = new ArrayList<>();
        usrCmt = new ArrayList<>();
        usrDp = new ArrayList<>();
        usrAgo = new ArrayList<>();
        usrId = new ArrayList<>();
    }

    public List<String> getUsrName() {
        return usrName;
    }

    public List<String> getUsrCmt() {
        return usrCmt;
    }

    public List<String> getUsrDp() {
        return usrDp;
    }

    public List<String> getUsrAgo() {
        return usrAgo;
    }

    public List<String> getUsrId() {
        return usrId;
    }

    public int getCount() {
        return count;
    }

    private void parseJSONAndStoreIt(String jsonString) {
        try {
            JSONObject jsonRootObject = new JSONObject(jsonString);
            status = jsonRootObject.optString("status");
            msg = jsonRootObject.optString("msg");

            if (status.equals("1")) {
                count = Integer.parseInt(jsonRootObject.optString("count"));
                //Log.e("important","count is "+count);
                for (int i = 0; i < count; i++) {
                    String storyUnitStr = jsonRootObject.optString(String.valueOf(i));
                    JSONObject jsonStoryObject = new JSONObject(storyUnitStr);

                    usrName.add(jsonStoryObject.optString("user_full_name"));
                    usrDp.add(jsonStoryObject.optString("user_photo"));
                    usrCmt.add(jsonStoryObject.optString("comment_text"));
                    usrId.add(jsonStoryObject.optString("user_id"));
                    usrAgo.add(Common.getTimeAgo(jsonStoryObject.optString("comment_last_update")));
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
                    // stream.close();
                } catch (Exception e) {
                    //Log.e("important","exception in reading "+e.getMessage());
                }
            }
        }).start();
    }


}
