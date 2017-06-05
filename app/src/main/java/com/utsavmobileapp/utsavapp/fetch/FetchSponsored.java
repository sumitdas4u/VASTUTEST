package com.utsavmobileapp.utsavapp.fetch;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.utsavmobileapp.utsavapp.R;
import com.utsavmobileapp.utsavapp.parser.ParseSponsoredJSON;
import com.utsavmobileapp.utsavapp.service.Common;
import com.utsavmobileapp.utsavapp.service.LatLonCachingAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Bibaswann on 21-06-2016.
 */
public class FetchSponsored extends AsyncTask<Void, Void, Void> {
    Context mContext;
    LinearLayout container;
    String mLat, mLon;
    ProgressBar progress;
    FetchSponsored asyncCountdown;
    com.utsavmobileapp.utsavapp.service.Common Common;
    LatLonCachingAPI llc;
    String myLat, myLon;
    private List<String> policeType = new ArrayList<>();
    private List<String> policeId = new ArrayList<>();
    private List<String> policeAddress = new ArrayList<>();
    private List<String> policeDistance = new ArrayList<>();
    private List<String> policePhone = new ArrayList<>();
    private List<String> policeLat = new ArrayList<>();
    private List<String> policeLong = new ArrayList<>();

    public FetchSponsored(Context context, LinearLayout holder, String lat, String lon) {
        mContext = context;
        container = holder;
        progress = (ProgressBar) container.findViewById(R.id.sponsoredProgress);
        mLat = lat;
        mLon = lon;
        Common = new Common(mContext);
        llc = new LatLonCachingAPI(mContext);
        myLat = llc.readLat();
        myLon = llc.readLng();
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
        ParseSponsoredJSON prnfj = new ParseSponsoredJSON(mContext.getString(R.string.uniurl) + "/api/police.php?lat=" + mLat + "&long=" + mLon, mContext);
        prnfj.fetchJSON();
        while (prnfj.parsingInComplete && (!this.isCancelled())) ;
        policeType = prnfj.getPoliceType();
        policeId = prnfj.getPoliceId();
        policeAddress = prnfj.getPoliceAddress();
        policeDistance = prnfj.getPoliceDistance();
        policePhone = prnfj.getPolicePhone();
        policeLat = prnfj.getPoliceLat();
        policeLong = prnfj.getPoliceLong();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        progress.setVisibility(View.GONE);
        for (final String id : policeId) {
            final int index = policeId.indexOf(id);
            LinearLayout ll = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.police_block, container, false);


            TextView name = (TextView) ll.findViewById(R.id.txtTitle);
            name.setText(policeType.get(index));

            TextView addr = (TextView) ll.findViewById(R.id.txtAddress);
            addr.setText(policeAddress.get(index));

            TextView dist = (TextView) ll.findViewById(R.id.txtDistance);
            dist.setText(policeDistance.get(index));

            TextView ph = (TextView) ll.findViewById(R.id.txtPhone);
            ph.setText(policePhone.get(index));

            Pattern pattern = Pattern.compile("\\b\\d{3}[-]?\\d{4}[-]?\\d{2,4}\\b");
            ph.setAutoLinkMask(0);
            String scheme = "tel:";
            Linkify.addLinks(ph, pattern, scheme);

            ph.setLinksClickable(true);
            ph.setMovementMethod(LinkMovementMethod.getInstance());
            dist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?saddr=" + myLat + "," + myLon + "&daddr=" + policeLat.get(index) + "," + policeLong.get(index)));
                    mContext.startActivity(intent);


                }
            });

          /*  ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + policePhone.get(index)));
                    mContext.startActivity(intent);

                }
            });
*/
            container.addView(ll);
        }
        super.onPostExecute(aVoid);
    }
}
