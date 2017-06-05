package com.utsavmobileapp.utsavapp.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.utsavmobileapp.utsavapp.DetailsActivity;
import com.utsavmobileapp.utsavapp.R;
import com.utsavmobileapp.utsavapp.SplashActivity;
import com.utsavmobileapp.utsavapp.parser.ParseNearFestivalJSON;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Sumit on 09-08-2016.
 */
public class LocationWebService extends AsyncTask<Void, Void, Void> {

    private final JSONObject jsonobjectLoaction;
    LoginCachingAPI lcp;
    private Context mContext;
    private ParseNearFestivalJSON prnfj;
    private Bitmap bitmapImage;

    public LocationWebService(JSONObject jsonArray, Context applicationContext) {

        this.jsonobjectLoaction = jsonArray;
        mContext = applicationContext;
    }

    @Override
    protected Void doInBackground(Void... params) {

        String fetchUrl;
        fetchUrl = mContext.getString(R.string.uniurl) + "/api/festival.php?lat=" + jsonobjectLoaction.optString("latitude") + "&long=" + jsonobjectLoaction.optString("longitude") + "&type=LISTING&page=0&limit=1&distance=1";
        Log.e("request", fetchUrl);
        prnfj = new ParseNearFestivalJSON(fetchUrl);
        prnfj.fetchJSON();
        while (prnfj.parsingInComplete) ;
        if (prnfj.getfName().size() > 0) {
            try {
                URL url = new URL(prnfj.getfImg().get(0));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bitmapImage = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                // Log exception

            }
        /*prnfj.getPoliceType();
        fId = prnfj.getPoliceId();
        fAddress = prnfj.getPoliceAddress();
        fDistance = prnfj.getPoliceDistance();
        fRating = prnfj.getPoliceLat();
        fImg = prnfj.getPoliceLong();
        return null;

*/
        }

        Log.e("request", "location chnage upload to server");
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Log.e("important", "web post ex");
        if (prnfj.getfName().size() > 0) {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(mContext)
                            .setSmallIcon(R.drawable.ic_logo_noti)
                            .setContentTitle(prnfj.getfName().get(0))
                            .setContentText("User rated " + prnfj.getfRating().get(0) + " & " + prnfj.getfDistance().get(0) + " km away");

            NotificationCompat.BigPictureStyle style
                    = new NotificationCompat.BigPictureStyle(mBuilder);
            style.bigPicture(bitmapImage)
                    .bigLargeIcon(bitmapImage)
                    .setBigContentTitle(prnfj.getfName().get(0))
                    .setSummaryText("User rated " + prnfj.getfRating().get(0) + " & " + prnfj.getfDistance().get(0) + " km away");

// Creates an explicit intent for an Activity in your app

            Intent resultIntent = new Intent(mContext, DetailsActivity.class);
            resultIntent.putExtra("id", prnfj.getfId().get(0));
            resultIntent.putExtra("name", prnfj.getfName().get(0));

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
// Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(SplashActivity.class);
// Adds the Intent that starts the Activity to the top of the stack

            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager =
                    (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
            mNotificationManager.notify(1, mBuilder.build());


        }

        super.onPostExecute(aVoid);

    }
}