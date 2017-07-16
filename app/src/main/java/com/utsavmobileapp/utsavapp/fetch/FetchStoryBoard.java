package com.utsavmobileapp.utsavapp.fetch;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.utsavmobileapp.utsavapp.R;
import com.utsavmobileapp.utsavapp.data.StoryObject;
import com.utsavmobileapp.utsavapp.parser.ParseStoryBoard;
import com.utsavmobileapp.utsavapp.service.AsyncResponseStoryBoard;
import com.utsavmobileapp.utsavapp.service.LoginCachingAPI;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bibaswann on 15-05-2016.
 */
public class FetchStoryBoard extends AsyncTask<List<StoryObject>, Void, List<StoryObject>> {

    public AsyncResponseStoryBoard delegateAsyncStoryBoard = null;
    Context mContext;
    LinearLayout container;
    String mLat, mLon;
    List<StoryObject> stories;
    ProgressBar progress;
    int page, limit;
    LoginCachingAPI lcp;
    boolean isLoggedIn;
    String fId, uId;

    public FetchStoryBoard(Context context, LinearLayout holder, String lat, String lon, int p, int l, ProgressBar prog, String fid, String uid) {
        mContext = context;
        container = holder;
        progress = prog;
        mLat = lat;
        mLon = lon;
        stories = new ArrayList<>();
        page = p;
        limit = l;
        lcp = new LoginCachingAPI(mContext);
        isLoggedIn = lcp.readSetting("login").equals("true");
        fId = fid;
        uId = uid;
    }

    @Override
    protected void onPreExecute() {
        progress.setVisibility(View.VISIBLE);
        super.onPreExecute();
    }

    @Override
    protected List<StoryObject> doInBackground(List<StoryObject>... List) {
        ParseStoryBoard prstb;
        if (fId != null) {
            prstb = new ParseStoryBoard(mContext.getString(R.string.uniurl) + "/api/storyboard.php?page=" + page + "&limit=" + limit + "&user_id=" + lcp.readSetting("id") + "&festival_id=" + fId, mContext);
        } else if (uId != null) {
            prstb = new ParseStoryBoard(mContext.getString(R.string.uniurl) + "/api/storyboard.php?page=" + page + "&limit=" + limit + "&user_id=" + uId + "&single_user=true&show_review=true", mContext);
        } else
            prstb = new ParseStoryBoard(mContext.getString(R.string.uniurl) + "/api/storyboard.php?page=" + page + "&limit=" + limit + "&user_id=" + lcp.readSetting("id"), mContext);
        prstb.fetchJSON();
        while (prstb.parsingInComplete && (!this.isCancelled())) ;
        stories = prstb.getStories();
        return stories;
    }


    @Override
    protected void onPostExecute(List<StoryObject> result) {
        if (delegateAsyncStoryBoard != null) {
            //return success or fail to activity
            delegateAsyncStoryBoard.processFinish(result);
        }


    }
}
