package com.utsavmobileapp.utsavapp.fetch;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.utsavmobileapp.utsavapp.DetailsActivity;
import com.utsavmobileapp.utsavapp.R;
import com.utsavmobileapp.utsavapp.parser.ParsePopularJSON;
import com.utsavmobileapp.utsavapp.service.Common;
import com.utsavmobileapp.utsavapp.service.LatLonCachingAPI;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bibaswann on 13-07-2016.
 */
public class FetchPopular extends AsyncTask<Void, Void, Void> {
    static String mZoneId;
    Context mContext;
    LinearLayout mList;
    ProgressBar progress;
    String popage, poplim;
    LatLonCachingAPI llc;
    String sortBy, orderBy;
    String mLat, mLon;
    boolean doClear;
    com.utsavmobileapp.utsavapp.service.Common Common;
    private List<String> fName = new ArrayList<>();
    private List<String> fId = new ArrayList<>();
    private List<String> fAddress = new ArrayList<>();
    private List<String> fDistance = new ArrayList<>();
    private List<String> fRating = new ArrayList<>();
    private List<String> fImg = new ArrayList<>();

    public FetchPopular(Context context, String zoneId, ProgressBar prog, LinearLayout list, String page, String lim, String sb, String ob, boolean clear) {
        mContext = context;
        progress = prog;
        mList = list;
        popage = page;
        poplim = lim;
        mZoneId = zoneId;
        sortBy = sb;
        orderBy = ob;
        doClear = clear;
        llc = new LatLonCachingAPI(mContext);
        mLat = llc.readLat();
        mLon = llc.readLng();
        Common = new Common(mContext);
    }

    @Override
    protected void onPreExecute() {
        progress.setVisibility(View.VISIBLE);
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        //ParsePopularJSON prpj = new ParsePopularJSON(mContext.getString(R.string.uniurl)+"/api/festival?type=ZONE&zone_id=" + mZoneId + "&page=" + popage + "&limit=" + poplim, mContext);

        ParsePopularJSON prpj;
        String fetchUrl;

        if (sortBy != null) {
            if (orderBy != null)
                fetchUrl = mContext.getString(R.string.uniurl) + "/api/festival.php?type=LISTING&lat=" + mLat + "&long=" + mLon + "&zone_id=" + mZoneId + "&page=" + popage + "&limit=" + poplim + "&order_by=" + orderBy + "&sort_by=" + sortBy;
            else
                fetchUrl = mContext.getString(R.string.uniurl) + "/api/festival.php?type=LISTING&lat=" + mLat + "&long=" + mLon + "&zone_id=" + mZoneId + "&page=" + popage + "&limit=" + poplim + "&order_by=festival_rating&sort_by=" + sortBy;
        } else {
            if (orderBy != null)
                fetchUrl = mContext.getString(R.string.uniurl) + "/api/festival.php?type=LISTING&lat=" + mLat + "&long=" + mLon + "&zone_id=" + mZoneId + "&page=" + popage + "&limit=" + poplim + "&order_by=" + orderBy + "&sort_by=DESC";
            else
                fetchUrl = mContext.getString(R.string.uniurl) + "/api/festival.php?type=LISTING&lat=" + mLat + "&long=" + mLon + "&zone_id=" + mZoneId + "&page=" + popage + "&limit=" + poplim + "&order_by=festival_rating&sort_by=DESC";
        }
        //Log.e("important",fetchUrl);
        prpj = new ParsePopularJSON(fetchUrl, mContext);
        prpj.fetchJSON();
        while (prpj.parsingInComplete && (!this.isCancelled())) ;
        fName = prpj.getfName();
        fId = prpj.getfId();
        fAddress = prpj.getfAddress();
        fDistance = prpj.getfDistance();
        fRating = prpj.getfRating();
        fImg = prpj.getfImg();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        progress.setVisibility(View.GONE);
        if (doClear)
            mList.removeAllViewsInLayout();
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
