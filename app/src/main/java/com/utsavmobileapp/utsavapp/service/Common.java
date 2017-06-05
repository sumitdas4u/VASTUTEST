package com.utsavmobileapp.utsavapp.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;
import com.utsavmobileapp.utsavapp.LoginActivity;
import com.utsavmobileapp.utsavapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class Common {
    static final long SECOND_MILLIS = 1000;
    static final long MINUTE_MILLIS = 60 * SECOND_MILLIS;
    static final long HOUR_MILLIS = 60 * MINUTE_MILLIS;
    static final long DAY_MILLIS = 24 * HOUR_MILLIS;
    static final long MONTH_MILLIS = 30 * DAY_MILLIS;
    static final long YEAR_MILLIS = 12 * MONTH_MILLIS;
    private static final String PREFIX = "json";
    static InputStream stream;
    static String USERLAT, USERLONG;
    static OkHttpClient client = new OkHttpClient();
    boolean isLoggedIn;
    LoginCachingAPI lcp;
    private String response3 = null;

    public Common(Context mContext) {
        lcp = new LoginCachingAPI(mContext);
        try {
            if (lcp.readSetting("login").equals("true")) this.isLoggedIn = true;
            else this.isLoggedIn = false;
        } catch (Exception e) {
            this.isLoggedIn = false;
        }


    }

    public static String HttpURLConnection(String URLSTRING) throws IOException {


        Request request = new Request.Builder()
                .url(URLSTRING)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();


        /*BufferedReader br;
        URL url;
        url = new URL(URLSTRING);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            conn.setRequestProperty("User-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.4 (KHTML, like Gecko) Chrome/22.0.1229.94 Safari/537.4");
            conn.setReadTimeout(30000);
            conn.setConnectTimeout(25000);
            conn.setRequestMethod("GET");

            conn.setDoInput(true);
            conn.connect();
            stream = conn.getInputStream();
            br = new BufferedReader(new InputStreamReader(stream));
            // }
            StringBuilder sb = new StringBuilder();
            String line;
            if ((line = br.readLine()) != null) {
                sb.append(line);
            }
            stream.close();

            return sb.toString();
        } catch (Exception e) {
            stream.close();
            return null;
        } finally {
            stream.close();

            conn.disconnect();
            //Log.i("important", "connection disconnected");
        }*/


    }

    public static String getTimeAgo(String timeStamp) {
        long time = Long.parseLong(timeStamp);
        time *= 1000;

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else if (diff < 30 * DAY_MILLIS) {
            return diff / DAY_MILLIS + " days ago";
        } else if (diff < 12 * MONTH_MILLIS) {
            return diff / MONTH_MILLIS + " months ago";
        } else {
            return "More than a year ago";
        }
    }

    public static void shareOnFB(final Context context, String type, String lat, String lon, String head, String imge, String url, String description, @Nullable String message) {
        // Create object
        JSONObject myObject = new JSONObject();
        try {

            myObject.put("og:type", "utsavmobileapp:festival");
            myObject.put("fb:app_id", context.getString(R.string.facebook_app_id));

            myObject.put("og:title", head);
            myObject.put("og:description", description);
            myObject.put("og:image", imge);
            myObject.put("og:url", url);
            myObject.put("fb:explicitly_shared", "true");
            myObject.put("utsavmobileapp:festival:latitude", lat);
            myObject.put("utsavmobileapp:festival:longitude", lon);
            myObject.put("utsavmobileapp:festival:altitude", "42");
        } catch (JSONException e) {
            String toastText = "JSON Error: " + e.getMessage();
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
            return;
        }

// Create action
        Bundle params = new Bundle();
        if (message != null) {
            params.putString("message", message);
        }
        params.putString("festival", myObject.toString());
        params.putString("fb:explicitly_shared", "true");

// Create request
      /*  Toast.makeText(getApplicationContext(), getCurrentAccessToken().getToken(),
                Toast.LENGTH_LONG).show();*/
        GraphRequest request = new GraphRequest(AccessToken.getCurrentAccessToken(), "me/utsavmobileapp:" + type, params, HttpMethod.POST,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {

                        FacebookRequestError error = response.getError();
                        if (error != null) {
                            // Display User Error Message
                            String toastText = "Error! " + error.getErrorMessage();
                            //Toast.makeText(context, toastText, Toast.LENGTH_LONG).show();
                            return;
                        } else {
                            // DEBUG: Display Response
                            String toastText = "Success! " + response.toString();
                            //Toast.makeText(context, toastText, Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                });
        request.executeAsync();

    }

    public void ImageDownloaderTask(ImageView imageView, Context context, String imgUrl, String usrorevnt) {
        //Log.e("important","url received is "+imgUrl);

        try {
            Glide.clear(imageView);
            if (usrorevnt.equals("user"))
                Picasso.with(context).load(imgUrl).resize(200, 200).placeholder(R.drawable.profile1).into(imageView);
            else
                Picasso.with(context).load(imgUrl).resize(200, 200).placeholder(R.color.placeholder_blue).into(imageView);


        } catch (Exception ignored) {
            Glide.clear(imageView);
        }


    }

    public boolean isLoggedIn(Context mContext) {
        Intent intent = null;

        if (!lcp.readSetting("login").equals("true")) {
            intent = new Intent(mContext, LoginActivity.class);
            intent.putExtra("mode", "login");
            mContext.startActivity(intent);
            return false;
        } else {
            return true;
        }
    }
}

