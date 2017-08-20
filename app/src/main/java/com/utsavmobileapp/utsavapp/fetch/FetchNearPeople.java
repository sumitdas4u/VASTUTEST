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

import com.utsavmobileapp.utsavapp.ProfileActivity;
import com.utsavmobileapp.utsavapp.R;
import com.utsavmobileapp.utsavapp.parser.ParseNearPeopleJSON;
import com.utsavmobileapp.utsavapp.service.Common;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bibaswann on 10-05-2016.
 */
public class FetchNearPeople extends AsyncTask<Void, Void, Void> {
    Context mContext;
    LinearLayout container;
    String mLat, mLon;
    ProgressBar progress;
    FetchNearPeople asyncCountdown;
    com.utsavmobileapp.utsavapp.service.Common Common;
    private List<String> uName = new ArrayList<>();
    private List<String> uId = new ArrayList<>();
    private List<String> uGender = new ArrayList<>();
    private List<String> uDistance = new ArrayList<>();
    private List<String> uAge = new ArrayList<>();
    private List<String> uImg = new ArrayList<>();

    public FetchNearPeople(Context context, LinearLayout holder, String lat, String lon) {
        mContext = context;
        container = holder;
        progress = (ProgressBar) container.findViewById(R.id.nearPeopleProgress);
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
        ParseNearPeopleJSON prnpj = new ParseNearPeopleJSON(mContext.getString(R.string.uniurl) + "/api/user.php?lat=" + mLat + "&long=" + mLon + "&type=NEAR", mContext);
        prnpj.fetchJSON();
        while (prnpj.parsingInComplete && (!this.isCancelled())) ;
        uName = prnpj.getuName();
        uId = prnpj.getuId();
        uGender = prnpj.getuGender();
        uDistance = prnpj.getuDistance();
        uAge = prnpj.getuAge();
        uImg = prnpj.getuImg();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        progress.setVisibility(View.GONE);
        for (final String id : uId) {
            int index = uId.indexOf(id);
            LinearLayout ll = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.people_layout, container, false);
            ImageView img = (ImageView) ll.findViewById(R.id.imageView2);
            //Log.e("important","url is "+uImg.get(index).replace("http","https"));
            Common.ImageDownloaderTask(img, mContext, uImg.get(index).replace("http", "https"), "user");
            TextView name = (TextView) ll.findViewById(R.id.nearPplName);
            name.setText(uName.get(index));
            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    com.utsavmobileapp.utsavapp.service.Common.dialogPeopoleDetails(uId.toString(),mContext);
                }
            });
            // TextView gndr = (TextView) ll.findViewById(R.id.nearPplGndr);
            // gndr.setText(uGender.get(index));

            TextView dist = (TextView) ll.findViewById(R.id.nearPplDistance);
            dist.setText(uDistance.get(index));

            TextView age = (TextView) ll.findViewById(R.id.userage);
            if(!uAge.get(index).equals("null")) {
                age.setText(uAge.get(index));
                age.setVisibility(View.VISIBLE);
            }

            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent peopleDetails = new Intent(mContext, ProfileActivity.class);
                    peopleDetails.putExtra("uid", id);
                    mContext.startActivity(peopleDetails);
                }
            });

            container.addView(ll);
        }
        super.onPostExecute(aVoid);
    }
}
