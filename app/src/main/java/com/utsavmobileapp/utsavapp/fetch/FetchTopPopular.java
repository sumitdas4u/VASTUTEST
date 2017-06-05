package com.utsavmobileapp.utsavapp.fetch;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.utsavmobileapp.utsavapp.DetailsActivity;
import com.utsavmobileapp.utsavapp.R;
import com.utsavmobileapp.utsavapp.ViewAllActivity;
import com.utsavmobileapp.utsavapp.parser.ParseTopPopularJSON;
import com.utsavmobileapp.utsavapp.service.Common;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bibaswann on 10-05-2016.
 */
public class FetchTopPopular extends AsyncTask<Void, Void, Void> {
    Context mContext;
    LinearLayout container;
    String mLat, mLon;
    ProgressBar progress;
    FetchTopPopular asyncCountdown;
    com.utsavmobileapp.utsavapp.service.Common Common;
    private List<String> popularInWhere = new ArrayList<>();
    private List<String> zoneId = new ArrayList<>();
    private List<List<String>> fName = new ArrayList<>();
    private List<List<String>> fId = new ArrayList<>();
    private List<List<String>> fAddress = new ArrayList<>();
    private List<List<String>> fDistance = new ArrayList<>();
    private List<List<String>> fRating = new ArrayList<>();
    private List<List<String>> fImg = new ArrayList<>();

    public FetchTopPopular(Context context, LinearLayout holder, String lat, String lon) {
        mContext = context;
        container = holder;
        progress = (ProgressBar) container.findViewById(R.id.popularProgress);
        mLat = lat;
        mLon = lon;
        Common = new Common(mContext);
    }
/*

    @Override
    protected void onPreExecute() {
        progress.setVisibility(View.VISIBLE);
        asyncCountdown = this;
        new CountDownTimer(10000, 10000) {
            public void onTick(long millisUntilFinished) {
                // You can monitor the progress here as well by changing the onTick() time
            }

            public void onFinish() {
                // stop async task if not in progress
                if (asyncCountdown.getStatus() == AsyncTask.Status.RUNNING) {
                    asyncCountdown.cancel(true);
                    try {
                        progress.setVisibility(View.GONE);
                        Toast.makeText(mContext,"Internet is too slow",Toast.LENGTH_SHORT).show();
                    } catch (IllegalArgumentException ignored) {}
                }
            }
        }.start();
        super.onPreExecute();
    }
*/

    @Override
    protected Void doInBackground(Void... params) {
        ParseTopPopularJSON prnfj = new ParseTopPopularJSON(mContext.getString(R.string.uniurl) + "/api/festival.php?type=TOP_POPULAR&lat=" + mLat + "&long=" + mLon + "&order_by=festival_rating&sort_by=DESC", mContext);
        prnfj.fetchJSON();
        while (prnfj.parsingInComplete && (!this.isCancelled())) ;
        popularInWhere = prnfj.getPopularInWhere();
        zoneId = prnfj.getZoneId();
        fName = prnfj.getfName();
        fId = prnfj.getfId();
        fAddress = prnfj.getfAddress();
        fDistance = prnfj.getfDistance();
        fRating = prnfj.getfRating();
        fImg = prnfj.getfImg();

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        progress.setVisibility(View.GONE);
        for (String partOfKolkata : popularInWhere) {
            final int popularIndex = popularInWhere.indexOf(partOfKolkata);

            LinearLayout l = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.popular_layout, container, false);
            TextView head = (TextView) l.findViewById(R.id.pophd);
            head.setText(partOfKolkata);
            Button vall = (Button) l.findViewById(R.id.viewallBtn);
            vall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mContext, ViewAllActivity.class);
                    i.putExtra("type", "popular");
                    i.putExtra("zoneid", zoneId.get(popularIndex));
                    mContext.startActivity(i);
                }
            });

            LinearLayout popUnitContainer = (LinearLayout) l.findViewById(R.id.popularunitholder);

            List<String> fIdsInOnePop = fId.get(popularIndex);
            final List<String> fNamesInOnePop = fName.get(popularIndex);
            List<String> fAddresssInOnePop = fAddress.get(popularIndex);
            List<String> fDistancesInOnePop = fDistance.get(popularIndex);
            List<String> fRatingsInOnePop = fRating.get(popularIndex);
            List<String> fImgsInOnePop = fImg.get(popularIndex);

            for (final String id : fIdsInOnePop) {
                final int index = fIdsInOnePop.indexOf(id);
                LinearLayout ll = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.popular_unit_layout, popUnitContainer, false);

                ImageView img = (ImageView) ll.findViewById(R.id.imageFestival);
                Common.ImageDownloaderTask(img, mContext, fImgsInOnePop.get(index), "festival");

                TextView name = (TextView) ll.findViewById(R.id.textView6);
                name.setText(fNamesInOnePop.get(index));

                TextView addr = (TextView) ll.findViewById(R.id.textView8);
                addr.setText(fAddresssInOnePop.get(index));

                TextView dist = (TextView) ll.findViewById(R.id.textView17);
                dist.setText(fDistancesInOnePop.get(index));

                TextView rat = (TextView) ll.findViewById(R.id.textView24);
                rat.setText(fRatingsInOnePop.get(index));

                ll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(mContext, DetailsActivity.class);
                        i.putExtra("id", id);
                        i.putExtra("name", fNamesInOnePop.get(index));
                        mContext.startActivity(i);
                    }
                });

                popUnitContainer.addView(ll);
            }
            if (fIdsInOnePop.size() > 0)
                container.addView(l);
        }
        super.onPostExecute(aVoid);
    }
}
