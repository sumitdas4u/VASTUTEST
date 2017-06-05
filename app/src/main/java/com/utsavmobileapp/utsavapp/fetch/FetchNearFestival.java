package com.utsavmobileapp.utsavapp.fetch;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.utsavmobileapp.utsavapp.DetailsActivity;
import com.utsavmobileapp.utsavapp.MapsFestivalActivity;
import com.utsavmobileapp.utsavapp.R;
import com.utsavmobileapp.utsavapp.parser.ParseNearFestivalJSON;
import com.utsavmobileapp.utsavapp.service.Common;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bibaswann on 10-05-2016.
 */
public class FetchNearFestival extends AsyncTask<Void, Void, Void> {

    Context mContext;
    LinearLayout containerLayout;
    ListView containerList;
    String mLat, mLon;
    ProgressBar progress;
    String pageNum, limNum;

    boolean showAll, doClear, doSkip;
    String sortBy, orderBy;
    com.utsavmobileapp.utsavapp.service.Common Common;
    private List<String> fName = new ArrayList<>();
    private List<String> fId = new ArrayList<>();
    private List<String> fAddress = new ArrayList<>();
    private List<String> fDistance = new ArrayList<>();
    private List<String> fRating = new ArrayList<>();
    private List<String> fImg = new ArrayList<>();
    private List<String> lat = new ArrayList<>();
    private List<String> lon = new ArrayList<>();

    public FetchNearFestival(Context context, LinearLayout holderLayout, ProgressBar prog, String page, String lim, String lat, String lon, boolean all, @Nullable String sb, @Nullable String ob, boolean clear, boolean skipFirst) {
        mContext = context;
        containerLayout = holderLayout;
        progress = prog;
        pageNum = page;
        limNum = lim;
        mLat = lat;
        mLon = lon;
        showAll = all;
        sortBy = sb;
        orderBy = ob;
        doClear = clear;
        doSkip = skipFirst;
        Common = new Common(mContext);
    }

    @Override
    protected void onPreExecute() {
        progress.setVisibility(View.VISIBLE);
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        ParseNearFestivalJSON prnfj;
        String fetchUrl;
        if (!showAll)
            fetchUrl = mContext.getString(R.string.uniurl) + "/api/festival.php?lat=" + mLat + "&long=" + mLon + "&type=NEAR&page=" + pageNum + "&limit=" + limNum;
        else {
            if (sortBy != null) {
                if (orderBy != null)
                    fetchUrl = mContext.getString(R.string.uniurl) + "/api/festival.php?type=LISTING&lat=" + mLat + "&long=" + mLon + "&page=" + pageNum + "&limit=" + limNum + "&order_by=" + orderBy + "&sort_by=" + sortBy;
                else
                    fetchUrl = mContext.getString(R.string.uniurl) + "/api/festival.php?type=LISTING&lat=" + mLat + "&long=" + mLon + "&page=" + pageNum + "&limit=" + limNum + "&order_by=festival_distance&sort_by=" + sortBy;
            } else {
                if (orderBy != null)
                    fetchUrl = mContext.getString(R.string.uniurl) + "/api/festival.php?type=LISTING&lat=" + mLat + "&long=" + mLon + "&page=" + pageNum + "&limit=" + limNum + "&order_by=" + orderBy;
                else
                    fetchUrl = mContext.getString(R.string.uniurl) + "/api/festival.php?type=LISTING&lat=" + mLat + "&long=" + mLon + "&page=" + pageNum + "&limit=" + limNum + "&order_by=festival_distance";
            }
        }
        //Log.e("important","url of sponsored is "+fetchUrl);
        prnfj = new ParseNearFestivalJSON(fetchUrl, mContext);
        prnfj.fetchJSON();
        while (prnfj.parsingInComplete && (!this.isCancelled())) ;
        fName = prnfj.getfName();
        fId = prnfj.getfId();
        lat = prnfj.getLat();
        lon = prnfj.getLon();
        fAddress = prnfj.getfAddress();
        fDistance = prnfj.getfDistance();
        fRating = prnfj.getfRating();
        fImg = prnfj.getfImg();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        progress.setVisibility(View.GONE);
        String latlong = "";

        if (!showAll) {
            for (final String id : fId) {
                final int index = fId.indexOf(id);
                if (index == 0 && doSkip)
                    continue;
                LinearLayout ll = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.festival_block, containerLayout, false);

                //Log.e("important","image "+fImg.get(index)+" and rating "+fRating.get(index));
                latlong = latlong + "|" + lat.get(index) + "," + lon.get(index);
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


                containerLayout.addView(ll);


            }
        } else {
            if (doClear)
                containerLayout.removeAllViewsInLayout();
            for (final String id : fId) {

                //Log.e("important","adding to layout2");
                final int index = fId.indexOf(id);
                LinearLayout ll = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.custom_list_item, containerLayout, false);
                ImageView img = (ImageView) ll.findViewById(R.id.imageFestival);

                Common.ImageDownloaderTask(img, mContext, fImg.get(index), "festival");

                latlong = latlong + "|" + lat.get(index) + "," + lon.get(index);
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

                containerLayout.addView(ll);
            }
        }

        try {
            LinearLayout mapviewll = (LinearLayout) containerLayout.findViewById(R.id.mapll);
            mapviewll.setVisibility(View.VISIBLE);
            mapviewll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mContext, MapsFestivalActivity.class);

                    mContext.startActivity(i);
                }
            });


            ImageView map = (ImageView) containerLayout.findViewById(R.id.imageMap);
            String url = "http://maps.google.com/maps/api/staticmap?center=" + mLat + "," + mLon + "&zoom=14&size=380x180&sensor=false&markers=icon:http://utsavapp.in/icon.png" + latlong + "&api=" + mContext.getString(R.string.google_maps_key);
            //Log.e("important","string"+url);
            Common.ImageDownloaderTask(map, mContext, url, "festival");

        } catch (NullPointerException e) {

        }


        super.onPostExecute(aVoid);
    }
}
