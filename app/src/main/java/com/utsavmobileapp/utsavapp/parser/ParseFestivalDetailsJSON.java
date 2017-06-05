package com.utsavmobileapp.utsavapp.parser;

import android.content.Context;

import com.utsavmobileapp.utsavapp.service.Common;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bibaswann on 17-07-2016.
 */
public class ParseFestivalDetailsJSON {

    public volatile boolean parsingInComplete = true;
    public List<String> imgList = new ArrayList<>();
    public List<String> imgListNrml = new ArrayList<>();
    public List<String> imgListBig = new ArrayList<>();
    public List<String> imgListId = new ArrayList<>();
    public List<String> imgListLk = new ArrayList<>();
    public List<String> imgListCmt = new ArrayList<>();
    public List<Boolean> imgListIsLiked = new ArrayList<>();
    String status, msg;
    int count;
    Context mContext;
    com.utsavmobileapp.utsavapp.service.Common Common;
    private String urlString = null;
    private String fName;
    private String fId;
    private String fAddress;
    private String fDistance;
    private String fRating;
    private String fImg;
    private String flat;
    private String flon;
    private String fdescription;
    private String fcontactName;
    private String fcontactNum;
    private String ftotalPhoto;
    private String ftotalReview;
    private String ftotalBookMark;
    private String ftotalCheckIn;
    private boolean fisBookMarked;


    public ParseFestivalDetailsJSON(String url, Context context) {
        urlString = url;
        mContext = context;
        Common = new Common(mContext);
    }

    public String getfAddress() {
        return fAddress;
    }

    public String getfDistance() {
        return fDistance;
    }

    public String getfId() {
        return fId;
    }

    public String getfImg() {
        return fImg;
    }

    public String getfName() {
        return fName;
    }

    public String getfRating() {
        return fRating;
    }

    public String getFcontactName() {
        return fcontactName;
    }

    public String getFcontactNum() {
        return fcontactNum;
    }

    public String getFdescription() {
        return fdescription;
    }

    public String getFlat() {
        return flat;
    }

    public String getFlon() {
        return flon;
    }

    public String getFtotalPhoto() {
        return ftotalPhoto;
    }

    public String getFtotalReview() {
        return ftotalReview;
    }

    public String getFtotalBookMark() {
        return ftotalBookMark;
    }

    public String getFtotalCheckIn() {
        return ftotalCheckIn;
    }

    public boolean isFisBookMarked() {
        return fisBookMarked;
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

    private void parseJSONAndStoreIt(String jsonString) {
        try {
            JSONObject jsonRootObject = new JSONObject(jsonString);
            status = jsonRootObject.optString("status");
            msg = jsonRootObject.optString("msg");

            if (status.equals("1")) {
                String nearUnitStr = jsonRootObject.optString("0");
                JSONObject jsonObject = new JSONObject(nearUnitStr);
                fName = jsonObject.optString("festival_name");
                fId = jsonObject.optString("festival_id");
                //Log.e("important","found fid "+fId);
                fAddress = jsonObject.optString("festival_address");
                flat = jsonObject.optString("festival_lat");
                flon = jsonObject.optString("festival_long");
                fDistance = jsonObject.optString("festival_distance") + "KM";
                fRating = jsonObject.optString("festival_rating");
                fImg = jsonObject.optString("festival_primary_image");
                fdescription = jsonObject.optString("festival_description");
                fcontactName = jsonObject.optString("festival_contact_name");
                fcontactNum = jsonObject.optString("festival_contact_no");
                ftotalPhoto = jsonObject.optString("total_photos");
                ftotalReview = jsonObject.optString("total_review");
                ftotalBookMark = jsonObject.optString("total_bookmarked");
                ftotalCheckIn = jsonObject.optString("total_check_in");
                fisBookMarked = jsonObject.optString("is_bookmarked").equals("1") ? true : false;

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
                    //stream.close();
                } catch (Exception e) {
                    //Log.e("important","exception in reading "+e.getMessage());
                }
            }
        }).start();
    }
}
