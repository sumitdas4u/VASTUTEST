package com.utsavmobileapp.utsavapp.fetch;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.utsavmobileapp.utsavapp.R;
import com.utsavmobileapp.utsavapp.parser.ParseNearRestaurant;
import com.utsavmobileapp.utsavapp.service.Common;

import java.util.ArrayList;
import java.util.List;

public class FetchZomato extends AsyncTask<Void, Void, Void> {

    String mLat, mLon;
    LinearLayout restaurantContainerLayout;
    Context mContext;
    List<String> rName = new ArrayList<>();
    List<String> rId = new ArrayList<>();
    List<String> rAddress = new ArrayList<>();
    List<String> rLink = new ArrayList<>();
    List<String> rRating = new ArrayList<>();
    List<String> rImg = new ArrayList<>();
    com.utsavmobileapp.utsavapp.service.Common Common;

    public FetchZomato(Context context, LinearLayout restaurantContainerLayout, String lat, String lon) {
        mContext = context;
        this.restaurantContainerLayout = restaurantContainerLayout;
        mLat = lat;
        mLon = lon;
        Common = new Common(mContext);
    }


    @Override
    protected Void doInBackground(Void... params) {
        //  Log.e("important", "zomato api call");
        ParseNearRestaurant prnr = new ParseNearRestaurant("https://developers.zomato.com/api/v2.1/search?lat=" + mLat + "&lon=" + mLon + "&radius=1000&sort=real_distance&order=asc", mContext);
        prnr.fetchJSON();
        while (prnr.parsingInComplete && (!this.isCancelled())) ;
        rName = prnr.getrName();
        rId = prnr.getrId();
        rAddress = prnr.getrAddress();
        rLink = prnr.getrLink();
        rRating = prnr.getrRating();
        rImg = prnr.getrImg();

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {


        showNearbyRestaurant();

        super.onPostExecute(aVoid);
    }


    private void showNearbyRestaurant() {
        if (rName.size() > 0) {
            for (final String id : rId) {
                if ((!rRating.equals(0) || !rRating.equals("null")) && (!rImg.equals("") || !rImg.equals(null))) {
                    final int index = rId.indexOf(id);
                    LinearLayout ll = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.festival_block, restaurantContainerLayout, false);
                    ImageView img = (ImageView) ll.findViewById(R.id.imageFestival);
                    if (!rImg.get(index).isEmpty()) {
                        Common.ImageDownloaderTask(img, mContext, rImg.get(index), "festival");
                    }
                    TextView name = (TextView) ll.findViewById(R.id.textView6);
                    name.setText(rName.get(index));

                    TextView addr = (TextView) ll.findViewById(R.id.textView8);
                    addr.setText(rAddress.get(index));

                    TextView dist = (TextView) ll.findViewById(R.id.textView17);
                    dist.setVisibility(View.GONE);

                    TextView rat = (TextView) ll.findViewById(R.id.textView24);
                    rat.setText(rRating.get(index));

                    ll.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String url = rLink.get(index);
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            mContext.startActivity(i);
                        }
                    });

                    restaurantContainerLayout.addView(ll);
                }

            }
        } else
            restaurantContainerLayout.setVisibility(View.GONE);
    }
}
