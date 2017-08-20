package com.utsavmobileapp.utsavapp.service;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;
import com.utsavmobileapp.utsavapp.LoginActivity;
import com.utsavmobileapp.utsavapp.ProfileActivity;
import com.utsavmobileapp.utsavapp.R;
import com.utsavmobileapp.utsavapp.parser.ParseSingleChatterJSON;

import org.apache.log4j.helpers.Transform;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Common {
    private static final long SECOND_MILLIS = 1000;
    private static final long MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final long HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final long DAY_MILLIS = 24 * HOUR_MILLIS;
    private static final long MONTH_MILLIS = 30 * DAY_MILLIS;
    static final long YEAR_MILLIS = 12 * MONTH_MILLIS;
    private static final String PREFIX = "json";
    static InputStream stream;
    private static OkHttpClient client = new OkHttpClient();
    private static LoginCachingAPI lcp;
    private boolean isLoggedIn;
    public Common(Context mContext) {
        lcp = new LoginCachingAPI(mContext);

        try {
            isLoggedIn = lcp.readSetting("login").equals("true");
        } catch (Exception e) {
            isLoggedIn = false;
        }


    }

    public static void dialogPeopoleDetails(final String uid, final Context mContextFunction) {
        final Dialog dialog = new Dialog(mContextFunction);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_contact_info);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        LatLonCachingAPI llc = new LatLonCachingAPI(mContextFunction);
        ParseSingleChatterJSON prnpj = new ParseSingleChatterJSON(mContextFunction.getString(R.string.uniurl) + "/api/user.php?lat=" + llc.readLat() + "&long=" + llc.readLng() + "&type=SINGLE&user_id_lists=" + uid, mContextFunction);
        prnpj.fetchJSON();
        while (prnpj.parsingInComplete) ;

        ((TextView) dialog.findViewById(R.id.name)).setText(prnpj.getuName());
        ((TextView) dialog.findViewById(R.id.tvActiveNow)).setText(prnpj.getuLastLogin());
        ImageView image = (ImageView) dialog.findViewById(R.id.image);
        Glide.with(mContextFunction).load(prnpj.getuImg()).into(image);
        dialog.findViewById(R.id.bt_send_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContextFunction, ProfileActivity.class);
                intent.putExtra("uid", uid);
                mContextFunction.startActivity(intent);
            }
        });
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
    public static void dialogBuy(final Context mContextFunction) {
        final Dialog dialog = new Dialog(mContextFunction);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.paid_popup);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;



        dialog.findViewById(R.id.bt_buy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //// TODO: 8/13/2017 paytm redirect
                Toast.makeText(mContextFunction,"Payment processing ....",Toast.LENGTH_SHORT).show();

                new AsyncTask<String, String, String>() {

                    String CHECKSUMHASH;
                    String uid =lcp.readSetting("id");
                    String userid,MID,email,ORDER_ID,CUST_ID,INDUSTRY_TYPE_ID,CHANNEL_ID,TXN_AMOUNT,WEBSITE,EMAIL,CALLBACK_URL;

                    protected void onPreExecute() {
                     //   return null;
                    }
                    protected String doInBackground(String... params) {
                        String url = mContextFunction.getString(R.string.uniurl) + "/paytm/generateChecksum.php";
                        JSONParser jsonParser = new JSONParser(mContextFunction);

                        String orderId;
                        String custId;

                        String param = "user_id=" + uid;

                        JSONObject jsonObject = jsonParser.makeHttpRequest(url,"POST",param);
                        Log.e("CheckSum result >>",jsonObject.toString());
                        if(jsonObject != null){
                            Log.d("CheckSum result >>",jsonObject.toString());
                            try {

                                CHECKSUMHASH    = jsonObject.has("CHECKSUMHASH") ? jsonObject.getString("CHECKSUMHASH") : "";
                                MID = jsonObject.has("CHECKSUMHASH") ? jsonObject.getString("CHECKSUMHASH") : "";
                                CUST_ID = jsonObject.has("CHECKSUMHASH") ? jsonObject.getString("CHECKSUMHASH") : "";
                                ORDER_ID = jsonObject.has("CHECKSUMHASH") ? jsonObject.getString("CHECKSUMHASH") : "";
                                INDUSTRY_TYPE_ID = jsonObject.has("CHECKSUMHASH") ? jsonObject.getString("CHECKSUMHASH") : "";
                                CHANNEL_ID = jsonObject.has("CHECKSUMHASH") ? jsonObject.getString("CHECKSUMHASH") : "";
                                TXN_AMOUNT = jsonObject.has("CHECKSUMHASH") ? jsonObject.getString("CHECKSUMHASH") : "";
                                WEBSITE = jsonObject.has("CHECKSUMHASH") ? jsonObject.getString("CHECKSUMHASH") : "";
                                EMAIL = jsonObject.has("CHECKSUMHASH") ? jsonObject.getString("CHECKSUMHASH") : "";
                                CALLBACK_URL = jsonObject.has("CHECKSUMHASH") ? jsonObject.getString("CHECKSUMHASH") : "";

                              Log.e("CheckSum result >>",CHECKSUMHASH);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        return null;
                    }
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                        PaytmPGService Service = PaytmPGService.getStagingService();

    /*PaytmMerchant constructor takes two parameters
    1) Checksum generation url
    2) Checksum verification url
    Merchant should replace the below values with his values*/




                        //below parameter map is required to construct PaytmOrder object, Merchant should replace below map values with his own values

                        Map<String, String> paramMap = new HashMap<String, String>();

                        //these are mandatory parameters


                        paramMap.put("ORDER_ID", ORDER_ID);
                        //MID provided by paytm

                        paramMap.put("MID", MID);
                        paramMap.put("CUST_ID", CUST_ID);
                                paramMap.put("CHANNEL_ID", CHANNEL_ID);
                        paramMap.put("INDUSTRY_TYPE_ID", INDUSTRY_TYPE_ID);
                        paramMap.put("WEBSITE", WEBSITE);
                        paramMap.put("TXN_AMOUNT",TXN_AMOUNT);
                        paramMap.put("TXN_AMOUNT",TXN_AMOUNT);
                        paramMap.put("EMAIL", "sumitdas4u@gmail.com");
                        paramMap.put("MOBILE_NO", "9804735837");
                        paramMap.put("CALLBACK_URL" ,CALLBACK_URL);
                        paramMap.put("CHECKSUMHASH" ,CHECKSUMHASH);
                        PaytmOrder Order = new PaytmOrder(paramMap);



                        Service.initialize(Order,null);
                        Service.startPaymentTransaction(mContextFunction, true, true, new PaytmPaymentTransactionCallback() {
                            @Override
                            public void someUIErrorOccurred(String inErrorMessage) {
                                // Some UI Error Occurred in Payment Gateway Activity.
                                // // This may be due to initialization of views in
                                // Payment Gateway Activity or may be due to //
                                // initialization of webview. // Error Message details
                                // the error occurred.
                            }

                            @Override
                            public void onTransactionResponse(Bundle inResponse) {
                                Log.d("LOG", "Payment Transaction : " + inResponse);
                                String response=inResponse.getString("RESPMSG");
                                if (response.equals("Txn Successful."))
                                {
                                   // new ConfirmMerchent().execute();
                                }else
                                {
                                    Toast.makeText(mContextFunction,response,Toast.LENGTH_SHORT).show();
                                }
                                Toast.makeText(mContextFunction, "Payment Transaction response "+inResponse.toString(), Toast.LENGTH_LONG).show();
                            }


                            @Override
                            public void networkNotAvailable() {
                                // If network is not
                                // available, then this
                                // method gets called.
                            }

                            @Override
                            public void clientAuthenticationFailed(String inErrorMessage) {
                                // This method gets called if client authentication
                                // failed. // Failure may be due to following reasons //
                                // 1. Server error or downtime. // 2. Server unable to
                                // generate checksum or checksum response is not in
                                // proper format. // 3. Server failed to authenticate
                                // that client. That is value of payt_STATUS is 2. //
                                // Error Message describes the reason for failure.
                            }

                            @Override
                            public void onErrorLoadingWebPage(int iniErrorCode,
                                                              String inErrorMessage, String inFailingUrl) {

                            }

                            // had to be added: NOTE
                            @Override
                            public void onBackPressedCancelTransaction() {
                                // TODO Auto-generated method stub
                            }

                            @Override
                            public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                                Log.d("LOG", "Payment Transaction Failed " + inErrorMessage);
                                Toast.makeText(mContextFunction, "Payment Transaction Failed ", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }.execute();






            }
        });
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }


    public static class JSONParser {
        static InputStream is = null;
        static JSONObject jObj = null;
        static String json = "";

        HttpURLConnection urlConnection = null;
        // variable to hold context
        private Context context;
        // constructor
        public JSONParser(Context context){
            this.context=context;
        }


        public JSONObject makeHttpRequest(String url,String method,String params) {

            // boolean isReachable =Config.isURLReachable(context);
            // Making HTTP request
            try {
                String retSrc="";
                char current = '0';

                URL url1 = new URL(url);
                // check for request method
                HttpURLConnection urlConnection = (HttpURLConnection) url1.openConnection();
                if (method == "POST") {
                    // request method is POST
                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setFixedLengthStreamingMode(params.getBytes().length);
                    urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
                    out.print(params);
                    out.close();
                }
                InputStream in = urlConnection.getInputStream();

                InputStreamReader isw = new InputStreamReader(in);

                byte[] bytes = new byte[10000];
                StringBuilder x = new StringBuilder();
                int numRead = 0;
                while ((numRead = in.read(bytes)) >= 0) {
                    x.append(new String(bytes, 0, numRead));
                }
                retSrc=x.toString();



                jObj = new JSONObject(retSrc);
            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Connectivity issue. Please try again later.", Toast.LENGTH_LONG).show();
                    }
                });
                return null;
            }finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return jObj;
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

