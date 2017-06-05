package com.utsavmobileapp.utsavapp.fetch;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.utsavmobileapp.utsavapp.DetailsActivity;
import com.utsavmobileapp.utsavapp.R;
import com.utsavmobileapp.utsavapp.parser.ParseFavouriteJSON;
import com.utsavmobileapp.utsavapp.service.Common;
import com.utsavmobileapp.utsavapp.service.LatLonCachingAPI;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bibaswann on 11-07-2016.
 */
public class FetchFavourite extends AsyncTask<Void, Void, Void> {

    Context mContext;
    LinearLayout mList;
    String mUserId;
    ProgressBar progress;
    String popage, poplim;
    String sortBy, orderBy;
    boolean doClear;
    String mLat, mLon;
    com.utsavmobileapp.utsavapp.service.Common Common;
    private List<String> fName = new ArrayList<>();
    private List<String> fId = new ArrayList<>();
    private List<String> fAddress = new ArrayList<>();
    private List<String> fDistance = new ArrayList<>();
    private List<String> fRating = new ArrayList<>();
    private List<String> fImg = new ArrayList<>();
    private boolean listing;
    private LatLonCachingAPI llc;

    public FetchFavourite(Context context, String userId, ProgressBar prog, LinearLayout list, String page, String lim, @Nullable String sb, @Nullable String ob, boolean clear) {
        mContext = context;
        mUserId = userId;
        progress = prog;
        mList = list;
        popage = page;
        poplim = lim;
        sortBy = sb;
        orderBy = ob;
        doClear = clear;
        llc = new LatLonCachingAPI(mContext);
        mLat = llc.readLat();
        mLon = llc.readLng();
        Common = new Common(mContext);
    }

    @Override
    protected Void doInBackground(Void... params) {
        ParseFavouriteJSON prfj;
        String fetchUrl;
        if (sortBy != null) {
            if (orderBy != null)
                fetchUrl = mContext.getString(R.string.uniurl) + "/api/festival.php?type=BOOKMARK&lat=" + mLat + "&long=" + mLon + "&user_id=" + mUserId + "&page=" + popage + "&limit=" + poplim + "&order_by=" + orderBy + "&sort_by=" + sortBy;
            else
                fetchUrl = mContext.getString(R.string.uniurl) + "/api/festival.php?type=BOOKMARK&lat=" + mLat + "&long=" + mLon + "&user_id=" + mUserId + "&page=" + popage + "&limit=" + poplim + "&order_by=festival_distance&sort_by=" + sortBy;
        } else {
            if (orderBy != null)
                fetchUrl = mContext.getString(R.string.uniurl) + "/api/festival.php?type=BOOKMARK&lat=" + mLat + "&long=" + mLon + "&user_id=" + mUserId + "&page=" + popage + "&limit=" + poplim + "&order_by=" + orderBy;
            else
                fetchUrl = mContext.getString(R.string.uniurl) + "/api/festival.php?type=BOOKMARK&lat=" + mLat + "&long=" + mLon + "&user_id=" + mUserId + "&page=" + popage + "&limit=" + poplim;
        }
        //Log.e("important","url is "+fetchUrl);
        prfj = new ParseFavouriteJSON(fetchUrl, mContext);
        prfj.fetchJSON();
        while (prfj.parsingInComplete && (!this.isCancelled())) ;
        fName = prfj.getfName();
        fId = prfj.getfId();
        fAddress = prfj.getfAddress();
        fDistance = prfj.getfDistance();
        fRating = prfj.getfRating();
        fImg = prfj.getfImg();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        progress.setVisibility(View.GONE);
//        mList.setAdapter(new CustomAdapter(mContext, fName.toArray(new String[fName.size()]), fId.toArray(new String[fId.size()]), fAddress.toArray(new String[fAddress.size()]), fDistance.toArray(new String[fDistance.size()]), fRating.toArray(new String[fRating.size()]), fImg.toArray(new String[fImg.size()])));
//        mList.setVisibility(View.VISIBLE);
        if (doClear)
            mList.removeAllViewsInLayout();
        progress.setVisibility(View.GONE);
        for (final String id : fId) {
            final int index = fId.indexOf(id);
            LinearLayout ll = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.custom_list_item, mList, false);
            ImageView img = (ImageView) ll.findViewById(R.id.imageFestival);

            Common.ImageDownloaderTask(img, mContext, fImg.get(index), "festival");

            TextView name = (TextView) ll.findViewById(R.id.textView6);
            name.setText(fName.get(index));

            TextView addr = (TextView) ll.findViewById(R.id.textView8);
            addr.setText(fAddress.get(index));

            TextView dist = (TextView) ll.findViewById(R.id.textView17);
            dist.setText(fDistance.get(index));

            TextView rat = (TextView) ll.findViewById(R.id.textView24);
            rat.setText(fRating.get(index));

            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mContext, DetailsActivity.class);
                    i.putExtra("id", id);
                    i.putExtra("name", fName.get(index));
                    mContext.startActivity(i);
                }
            });
            mList.addView(ll);
        }
        super.onPostExecute(aVoid);
    }
}
