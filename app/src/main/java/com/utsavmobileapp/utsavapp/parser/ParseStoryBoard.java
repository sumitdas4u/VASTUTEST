package com.utsavmobileapp.utsavapp.parser;

import android.content.Context;

import com.utsavmobileapp.utsavapp.data.FestivalObject;
import com.utsavmobileapp.utsavapp.data.StoryObject;
import com.utsavmobileapp.utsavapp.data.UserObject;
import com.utsavmobileapp.utsavapp.service.Common;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bibaswann on 15-05-2016.
 */
public class ParseStoryBoard {

    public volatile boolean parsingInComplete = true;
    List<StoryObject> stories;
    String status, msg;
    int count;
    com.utsavmobileapp.utsavapp.service.Common Common;
    Context mContext;
    private String urlString = null;

    public ParseStoryBoard(String url, Context context) {
        urlString = url;
        stories = new ArrayList<>();
        mContext = context;
        Common = new Common(context);
    }

    public List<StoryObject> getStories() {
        return stories;
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

                    StoryObject oneStory = new StoryObject();
                    UserObject oneUser = new UserObject();
                    FestivalObject oneFestival = new FestivalObject();
                    List<String> imgList = new ArrayList<>();
                    List<String> imgListNrml = new ArrayList<>();
                    List<String> imgListBig = new ArrayList<>();
                    List<String> imgListId = new ArrayList<>();
                    List<String> imgListLk = new ArrayList<>();
                    List<String> imgListCmt = new ArrayList<>();
                    List<Boolean> imgListIsLiked = new ArrayList<>();

                    oneUser.setId(jsonStoryObject.optString("user_id"));
                    oneUser.setName(jsonStoryObject.optString("user_full_name"));
                    oneUser.setPhoto(jsonStoryObject.optString("user_total_photos"));
                    oneUser.setReview(jsonStoryObject.optString("user_total_review"));
                    oneUser.setPrimg(jsonStoryObject.optString("user_photo"));

                    oneFestival.setId(jsonStoryObject.optString("festival_id"));
                    oneFestival.setName(jsonStoryObject.optString("festival_name"));
                    oneFestival.setAddress(jsonStoryObject.optString("festival_address"));
                    oneFestival.setImg(jsonStoryObject.optString("festival_primary_image"));

                    oneStory.setUob(oneUser);
                    oneStory.setFob(oneFestival);
                    if (!(jsonStoryObject.optString("storyboard_check_in_id").equals("0")) || jsonStoryObject.optString("storyboard_check_in_id").equals("null"))
                        oneStory.setLastUpdate(" was here " + Common.getTimeAgo(jsonStoryObject.optString("storyboard_last_update")));
                    else
                        oneStory.setLastUpdate(Common.getTimeAgo(jsonStoryObject.optString("storyboard_last_update")));
                    oneStory.setStoryId(Integer.parseInt(jsonStoryObject.optString("storyboard_id")));
                    oneStory.setUserComment(jsonStoryObject.optString("rating_text"));
                    oneStory.setUserRating(jsonStoryObject.optString("rating_value"));
                    if (!jsonStoryObject.optString("is_liked").equals("null"))
                        oneStory.setLiked(true);
                    else
                        oneStory.setLiked(false);
                    if (!jsonStoryObject.optString("is_bookmark").equals("null"))
                        oneStory.setBookmarked(true);
                    else
                        oneStory.setBookmarked(false);

                    if (!jsonStoryObject.optString("photos").equals("null")) {
                        JSONArray jsonImgArray = jsonStoryObject.getJSONArray("photos");
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
                        oneStory.setOtherImg(imgList);
                        oneStory.setOtherImgNrml(imgListNrml);
                        oneStory.setOtherImgBig(imgListBig);
                        oneStory.setOtherImgId(imgListId);
                        oneStory.setOtherImglk(imgListLk);
                        oneStory.setOtherImgcmt(imgListCmt);
                        oneStory.setOtherImgIsLiked(imgListIsLiked);
                    }

                    oneStory.setNumLike(Integer.parseInt(jsonStoryObject.optString("total_love")));
                    oneStory.setNumComment(Integer.parseInt(jsonStoryObject.optString("total_comments")));

                    stories.add(oneStory);
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

                    //saveData(sb.toString());
                    //Log.e("important", "found string " +sb.toString());
                    parseJSONAndStoreIt(com.utsavmobileapp.utsavapp.service.Common.HttpURLConnection(urlString));
                    //    stream.close();
                } catch (Exception e) {
                    //Log.e("important","exception in reading "+e.getMessage());
                }
            }
        }).start();
    }

    public void saveData(String data) {
        mContext.deleteFile("storyboard");
        FileOutputStream outputStream;
        try {
            outputStream = mContext.openFileOutput("storyboard", Context.MODE_PRIVATE);
            outputStream.write(data.getBytes());
            outputStream.close();
        } catch (Exception e) {
        }
    }
}
