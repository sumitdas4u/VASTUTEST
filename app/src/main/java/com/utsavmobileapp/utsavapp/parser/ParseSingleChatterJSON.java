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

public class ParseSingleChatterJSON {
    public volatile boolean parsingInComplete = true;
    String status, msg;
    int count;
    Context mContext;
    com.utsavmobileapp.utsavapp.service.Common Common;

    public List<String> imgList = new ArrayList<>();
    public List<String> imgListNrml = new ArrayList<>();
    public List<String> imgListBig = new ArrayList<>();
    public List<String> imgListId = new ArrayList<>();
    public List<String> imgListLk = new ArrayList<>();
    public List<String> imgListCmt = new ArrayList<>();
    public List<Boolean> imgListIsLiked = new ArrayList<>();

    private String urlString = null;
    private String uName;
    private String uId;
    private String uGender;
    private String uDistance;
    private String uAge;
    private String uDob;
    private String uImg;
    private String uTotalPhoto;
    private String uTotalRvw;
    private String uTotalChckIn;
    private String uLastLogin;
    private String uStatus;

    public ParseSingleChatterJSON(String url, Context context) {
        urlString = url;
        mContext = context;
        Common = new Common(mContext);
    }

    public String getuName() {
        return uName;
    }

    public String getuId() {
        return uId;
    }

    public String getuGender() {
        return uGender;
    }

    public String getuDistance() {
        return uDistance;
    }

    public String getuAge() {
        return uAge;
    }

    public String getuImg() {
        return uImg;
    }

    public String getuTotalPhoto() {
        return uTotalPhoto;
    }

    public String getuTotalRvw() {
        return uTotalRvw;
    }

    public String getuTotalChckIn() {
        return uTotalChckIn;
    }

    public String getuStatus() {
        return uStatus;
    }

    public String getuLastLogin() {
        return uLastLogin;
    }


    public List<Boolean> getImgListIsLiked() {
        return imgListIsLiked;
    }

    public List<String> getImgList() {
        return imgList;
    }

    public List<String> getImgListBig() {
        return imgListBig;
    }

    public List<String> getImgListCmt() {
        return imgListCmt;
    }

    public List<String> getImgListId() {
        return imgListId;
    }

    public List<String> getImgListLk() {
        return imgListLk;
    }

    public List<String> getImgListNrml() {
        return imgListNrml;
    }

    public String getuDob() {
        return uDob;
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

                    uName=jsonObject.optString("user_full_name");
                    uId=jsonObject.optString("user_id");
                    uDistance=jsonObject.optString("user_distance") + "KM";
                    uGender=jsonObject.optString("user_gender");
                    uStatus=jsonObject.optString("user_profile_status");
                    uAge=jsonObject.optString("user_age");
                    uDob=jsonObject.optString("user_dob");
                    uImg=jsonObject.optString("user_photo");
                    uTotalPhoto=jsonObject.optString("user_total_photos");
                    uTotalRvw=jsonObject.optString("user_total_review");
                    uTotalChckIn=jsonObject.optString("user_total_check_in");
                    uLastLogin=new SimpleDateFormat("MMMM d, yyyy ',' h:mm a", new Locale("en", "IN")).format(Long.parseLong(jsonObject.optString("user_last_update"))*1000);
                    String photoStr = jsonObject.optString("photos");
                    if (!photoStr.equals("null")) {
                        JSONArray jsonImgArray = jsonObject.getJSONArray("photos");
                        for (int j = 0; j < jsonImgArray.length(); j++) {
                            String imgStr = jsonImgArray.optString(j);
                            JSONObject imgObject = new JSONObject(imgStr);
                            imgList.add(imgObject.optString("photo_thum"));
                            imgListNrml.add(imgObject.optString("photo_normal"));
                            imgListBig.add(imgObject.optString("photo_large"));
                            imgListId.add(imgObject.optString("photo_id"));
                            imgListLk.add(imgObject.optString("total_love"));
                            imgListCmt.add(imgObject.optString("total_comments"));
                            if (imgObject.optString("is_liked").equals("null"))
                                imgListIsLiked.add(false);
                            else
                                imgListIsLiked.add(true);
                        }
                    }
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
//                    Log.e("important", "found string " +sb.toString());
                    parseJSONAndStoreIt(sb);
                    // stream.close();
                } catch (Exception e) {
//                    Log.e("important", "exception in reading " + Log.getStackTraceString(e));
                }
            }
        }).start();
    }
}
