package com.utsavmobileapp.utsavapp;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.hedgehog.ratingbar.RatingBar;
import com.utsavmobileapp.utsavapp.adapter.PhotoAdapter;
import com.utsavmobileapp.utsavapp.service.Common;
import com.utsavmobileapp.utsavapp.service.LoginCachingAPI;
import com.utsavmobileapp.utsavapp.service.SiliCompressor;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;

import static com.android.volley.Request.Method;

public class WriteReviewActivity extends AppCompatActivity {

    private static final String TAG = "UploadServiceDemo";
    private static final String USER_AGENT = "UploadServiceDemo/" + BuildConfig.VERSION_NAME;
    private static final int FILE_CODE = 1;
    private static final int LOG_IN_OUT = 100;
    String mLat, mLon, mHead, mImage, mUrl, mDescription;
    RecyclerView recyclerView;
    PhotoAdapter photoAdapter;
    Button submitReview;
    CheckBox facebookShare;
    ArrayList<String> photos;
    EditText body;

    String reviewTxt = "";
    String ratingTxt = "0";
    String fId;
    String mode;
    LoginCachingAPI lcp;
    boolean fbPermission;
    ArrayList<String> paths;

    CallbackManager callbackManager;

    ArrayList<String> selectedPhotos = new ArrayList<>();
    RequestQueue queue;
    Common common;
    private String filesToUploadString = null;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Rate and Review");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        queue = Volley.newRequestQueue(this);
        common = new Common(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            fId = extras.getString("id");
            mode = extras.getString("mode");
            mLat = extras.getString("lat");
            mLon = extras.getString("lon");
            mDescription = extras.getString("description");
            mHead = extras.getString("head");
            mUrl = extras.getString("url");
            mImage = extras.getString("image");
        }
        lcp = new LoginCachingAPI(this);

        body = (EditText) findViewById(R.id.reviewBody);

        recyclerView = (RecyclerView) findViewById(R.id.picked_recycler);
        photoAdapter = new PhotoAdapter(this, selectedPhotos);

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, OrientationHelper.VERTICAL));
        recyclerView.setAdapter(photoAdapter);

        if (mode.equals("pic")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            } else {
                openPicPicker();
            }
        }

        Button addImg = (Button) findViewById(R.id.plus);
        if (addImg != null) {
            addImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                    } else {
                        openPicPicker();
                    }
                }
            });
        }

        RatingBar mRatingBar = (RatingBar) findViewById(R.id.ratingbar);
//        mRatingBar.setStarEmptyDrawable(getResources().getDrawable(R.mipmap.star_empty));
//        mRatingBar.setStarHalfDrawable(getResources().getDrawable(R.mipmap.star_half));
//        mRatingBar.setStarFillDrawable(getResources().getDrawable(R.mipmap.star_full));
//        mRatingBar.setStarCount(10);
//        mRatingBar.setStar(10f);
        mRatingBar.halfStar(true);
//        mRatingBar.setmClickable(true);
//        mRatingBar.setStarImageWidth(120f);
//        mRatingBar.setStarImageHeight(60f);
//        mRatingBar.setImagePadding(35);
        mRatingBar.setStar(0f);
        mRatingBar.setOnRatingChangeListener(
                new RatingBar.OnRatingChangeListener() {
                    @Override
                    public void onRatingChange(float RatingCount) {
                        //Toast.makeText(WriteReviewActivity.this, "the fill star is" + RatingCount, Toast.LENGTH_SHORT).show();
                        ratingTxt = String.valueOf(RatingCount);
                    }
                }
        );

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                PhotoPreview.builder()
                        .setPhotos(selectedPhotos)
                        .setCurrentItem(position)
                        .start(WriteReviewActivity.this);
            }
        }));

        submitReview = (Button) findViewById(R.id.submit);
        submitReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (common.isLoggedIn(WriteReviewActivity.this)) {
                    //uploadMultipart(WriteReviewActivity.this);
                    uploadReview();
                    //Toast.makeText(WriteReviewActivity.this, "Clicked", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(WriteReviewActivity.this, "Please log in to write a review", Toast.LENGTH_LONG).show();
            }
        });
        facebookShare = (CheckBox) findViewById(R.id.fbShare);
        //Log.e("important",lcp.readSetting("fbid"));
        try {
            if (!AccessToken.getCurrentAccessToken().getPermissions().contains("publish_actions")) {
                fbPermission = false;
            } else {
                facebookShare.setChecked(true);
                fbPermission = true;
            }
        } catch (Exception ignored) {
        }

        facebookShare.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (lcp.readSetting("fbid").equals("null")) {
                        Intent intent = new Intent(WriteReviewActivity.this, LoginActivity.class);
                        intent.putExtra("mode", "login");
                        intent.putExtra("method", "facebook");
                        WriteReviewActivity.this.startActivityForResult(intent, LOG_IN_OUT);
                    }
                }
            }
        });

        callbackManager = CallbackManager.Factory.create();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openPicPicker();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && (requestCode == PhotoPicker.REQUEST_CODE || requestCode == PhotoPreview.REQUEST_CODE)) {
            List<Uri> resultUris = new ArrayList<>();
            // For JellyBean and above

            paths = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
            if (paths != null) {
                for (String path : paths) {
                    try {

                        String filePath = SiliCompressor.with(this).compress(path);
                        resultUris.add(Uri.parse(filePath));
                    } catch (Exception e) {

                    }
                }
            }

            StringBuilder absolutePathsConcat = new StringBuilder();

            for (Uri uri : resultUris) {
                if (absolutePathsConcat.length() == 0) {
                    absolutePathsConcat.append(new File(uri.getPath()).getAbsolutePath());
                } else {
                    absolutePathsConcat.append(",").append(new File(uri.getPath()).getAbsolutePath());
                }
            }
            filesToUploadString = (absolutePathsConcat.toString());
            selectedPhotos.clear();
            if (paths != null) {
                selectedPhotos.addAll(paths);
            }
            photoAdapter.notifyDataSetChanged();
        }

/*
        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            List<Uri> resultUris = new ArrayList<>();
            if (data != null) {

                ArrayList<String> paths = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);

                if (paths != null) {
                    for (String path: paths) {
                        resultUris.add(Uri.parse(path));
                    }
                }
                Log.e("important","selected "+paths.get(0)+" images");
                selectedPhotos.clear();

                if (paths != null) {

                    resultUris.addAll(paths);
                    StringBuilder absolutePathsConcat = new StringBuilder();
                    for (Uri uri : resultUris) {
                        if (absolutePathsConcat.length() == 0) {
                            absolutePathsConcat.append(new File(uri.getPath()).getAbsolutePath());
                        } else {
                            absolutePathsConcat.append(",").append(new File(uri.getPath()).getAbsolutePath());
                        }
                    }
                    filesToUploadString=(absolutePathsConcat.toString());
                }

            }
        } else */
        if (requestCode == LOG_IN_OUT) {
            if (resultCode == RESULT_OK) {
                LoginManager.getInstance().logInWithPublishPermissions(this, Arrays.asList("publish_actions"));
                fbPermission = true;
            }
        }
    }

    private void openPicPicker() {
        PhotoPicker.builder()
                .setPhotoCount(9)
                .setShowCamera(true)
                .setShowGif(true)
                .setPreviewEnabled(false)
                .setSelected(paths)
                .start(WriteReviewActivity.this, PhotoPicker.REQUEST_CODE);
    }

    private void uploadReview() {
        if (body.getText().toString().length() != 0) {
            if (body.getText().toString().length() < 20) {
                Toast.makeText(this, "Minimum 20 characters, please", Toast.LENGTH_LONG).show();
                return;
            } else
                reviewTxt = body.getText().toString();
        }
        if (filesToUploadString != null) {
            final String url;
            try {
                url = getString(R.string.uniurl) + "/api/festival.php?type=SUBMIT&rating_value=" + ratingTxt + "&festival_id=" + fId + "&user_id=" + lcp.readSetting("id") + "&rating_text=" + URLEncoder.encode(reviewTxt, "UTF-8") + "&photo_upload=YES";

                StringRequest getRequest = new StringRequest(Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        Log.d(TAG, "Success " + response);
                        String PHOTO_UPLOAD_URL = getString(R.string.uniurl) + "/api/festival.php?type=PHOTO_UPLOAD&user_id=" + lcp.readSetting("id") + "&festival_id=" + fId + "&story_board_id=" + response;
//                        Log.e("important", "response is upload click " + PHOTO_UPLOAD_URL);
                        try {
                            new onMultipartUploadClick(PHOTO_UPLOAD_URL, "uploaded_file", getApplicationContext()).execute().get(2000, TimeUnit.MILLISECONDS);
                            Toast.makeText(getApplicationContext(), "Publishing....",
                                    Toast.LENGTH_SHORT).show();
                        } catch (InterruptedException | ExecutionException | TimeoutException e) {
                            e.printStackTrace();
                        }
                        // display response downloadFAQ.execute().get(2000, TimeUnit.MILLISECONDS);
//                        Log.d("Response", response);
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
//                                Log.d(TAG, "Error response " + error.getMessage());
                            }
                        });
                queue.add(getRequest);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            final String url;
            try {
                url = getString(R.string.uniurl) + "/api/festival.php?type=SUBMIT&rating_value=" + ratingTxt + "&festival_id=" + fId + "&user_id=" + lcp.readSetting("id") + "&rating_text=" + URLEncoder.encode(reviewTxt, "UTF-8");
                StringRequest getRequest = new StringRequest(Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        Log.d(TAG, "Success " + response);

                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
//                                Log.d(TAG, "Error response " + error.getMessage());
                            }
                        });
                queue.add(getRequest);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


        }

        if (facebookShare.isChecked()) {
            if (!fbPermission) {
                LoginManager.getInstance().logInWithPublishPermissions(this, Arrays.asList("publish_actions"));
            }


            if (filesToUploadString == null) {
                if (ratingTxt != "0") {
                    mDescription = "rated " + ratingTxt;
                }

                Common.shareOnFB(this, "review", mLat, mLon, mHead, mImage, mUrl, mDescription, reviewTxt);
            }
            //  else
            //   Common.shareOnFB(this, "enjoy", mLat, mLon, mHead, mImage, mUrl, mDescription,null);
        }
        WriteReviewActivity.this.finish();
    }


    private class onMultipartUploadClick extends AsyncTask<String, Void, Void> implements UploadStatusDelegate {

        Bundle params;
        private Context mContext;
        private String serverUrlString;
        private String paramNameString;
        private Boolean uploadDone = false;
        private int uploadedimage = 0;
        private int fbimage = 0;

        public onMultipartUploadClick(String photo_upload_url, String uploaded_file, Context context) {
            this.serverUrlString = photo_upload_url;
            this.paramNameString = uploaded_file;
            this.mContext = context;
        }


        private UploadNotificationConfig getNotificationConfig(String filename) {


            return new UploadNotificationConfig()
                    .setIcon(R.drawable.ic_logo_noti)
                    .setCompletedIcon(R.drawable.utsav_logo)
                    .setErrorIcon(R.drawable.cast_ic_notification_disconnect)
                    .setTitle("Utsav App")
                    .setInProgressMessage("Image Uploading...")
                    .setCompletedMessage("Photo Upload done.")
                    .setErrorMessage("Error!! Photo uploading..")


                    .setClearOnAction(true)
                    .setRingToneEnabled(true);
        }

        private String getFilename(String filepath) {
            if (filepath == null)
                return null;

            final String[] filepathParts = filepath.split("/");

            return filepathParts[filepathParts.length - 1];
        }

        private void logSuccessfullyUploadedFiles(List<String> files) {
            for (String file : files) {
//                Log.e(TAG, "Success:" + file);
            }
        }

        @Override
        public void onProgress(UploadInfo uploadInfo) {
//            Log.i(TAG, String.format(Locale.getDefault(), "ID: %1$s (%2$d%%) at %3$.2f Kbit/s",
//                    uploadInfo.getUploadId(), uploadInfo.getProgressPercent(),
//                    uploadInfo.getUploadRate()));


        }

        @Override
        public void onError(UploadInfo uploadInfo, Exception exception) {
//            Log.e(TAG, "Error with ID: " + uploadInfo.getUploadId() + ": "
//                    + exception.getLocalizedMessage(), exception);
            uploadDone = true;
            this.cancel(true);
        }


        @Override
        public void onCompleted(UploadInfo uploadInfo, ServerResponse serverResponse) {

        /*    Log.i(TAG, String.format(Locale.getDefault(),
                    "ID %1$s: completed in %2$ds at %3$.2f Kbit/s. Response code: %4$s, body:[%5$s]",
                    uploadInfo.getUploadId(), uploadInfo.getElapsedTime() / 1000,
                    uploadInfo.getUploadRate(), serverResponse.toString(),
                    serverResponse.getBodyAsString()));
            Log.i(TAG, "image[" + fbimage + "][url]" + serverResponse.getBodyAsString());*/
            params.putString("image[" + fbimage + "][url]", serverResponse.getBodyAsString());
            params.putBoolean("image[" + fbimage + "][user_generated]", true);
            fbimage++;

            if (fbimage == uploadedimage) {
                Toast.makeText(getApplicationContext(), "Your activity has been published in STORYBOARD",
                        Toast.LENGTH_SHORT).show();
            }


            if (fbimage == uploadedimage && facebookShare.isChecked() && fbPermission) {

                JSONObject myObject = new JSONObject();
                try {


                    myObject.put("og:type", "utsavmobileapp:festival");
                    myObject.put("fb:app_id", "1671660553054919");

                    myObject.put("og:title", mHead);
                    myObject.put("og:image", mImage);
                    myObject.put("og:url", mUrl);
                    myObject.put("og:description", mDescription);
                    myObject.put("fb:explicitly_shared", "true");
                    myObject.put("utsavmobileapp:festival:latitude", mLat);
                    myObject.put("utsavmobileapp:festival:longitude", mLon);
                    myObject.put("utsavmobileapp:festival:altitude", "42");

                } catch (JSONException e) {
                   /* String toastText = "JSON Error: " + e.getMessage();
                    Toast.makeText(getApplicationContext(), toastText,
                            Toast.LENGTH_SHORT).show();*/
                    return;
                }

// Create action

  /*             params.putString("image[0][url]","http://www.holidify.com/blog/wp-content/uploads/2014/08/colors-of-durga-puja-in-kolkata-dumdum-park.jpg");
                params.putBoolean("image[0][user_generated]", true);
                params.putString("image[1][url]","http://www.holidify.com/blog/wp-content/uploads/2014/08/colors-of-durga-puja-in-kolkata-dumdum-park.jpg");
                params.putBoolean("image[1][user_generated]", true);
                params.putString("image[2][url]","http://www.holidify.com/blog/wp-content/uploads/2014/08/colors-of-durga-puja-in-kolkata-dumdum-park.jpg");
                params.putBoolean("image[2][user_generated]", true);
                params.putString("image[3][url]","http://www.holidify.com/blog/wp-content/uploads/2014/08/colors-of-durga-puja-in-kolkata-dumdum-park.jpg");
                params.putBoolean("image[3][user_generated]", true);*/
                params.putString("message", reviewTxt);
                params.putString("festival", myObject.toString());
                params.putString("fb:explicitly_shared", "true");
                Log.i(TAG, params.toString());

// Create request
      /*  Toast.makeText(getApplicationContext(), getCurrentAccessToken().getToken(),
                Toast.LENGTH_LONG).show();*/
                GraphRequest request = new GraphRequest(AccessToken.getCurrentAccessToken(),
                        "me/utsavmobileapp:enjoy", params, HttpMethod.POST,
                        new GraphRequest.Callback() {
                            @Override
                            public void onCompleted(GraphResponse response) {

                                FacebookRequestError error = response.getError();
                                if (error != null) {
                                    // Display User Error Message
                                  /*  String toastText = error.getErrorUserMessage();
                                    Toast.makeText(getApplicationContext(), toastText,
                                            Toast.LENGTH_LONG).show();*/
                                    return;
                                } else {
                                    // DEBUG: Display Response
                                    /*String toastText = response.toString();
                                    Toast.makeText(getApplicationContext(), toastText,
                                            Toast.LENGTH_LONG).show();*/
                                    return;
                                }
                            }
                        });

// Send Request
                request.executeAsync();

            }
            uploadDone = true;
            this.cancel(true);


        }

        @Override
        public void onCancelled(UploadInfo uploadInfo) {
//            Log.i(TAG, "Upload with ID " + uploadInfo.getUploadId() + " is cancelled");
            logSuccessfullyUploadedFiles(uploadInfo.getSuccessfullyUploadedFiles());
            uploadDone = true;
            this.cancel(true);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.i(TAG, "post");
            params = new Bundle();


            super.onPostExecute(aVoid);
        }

        /**
         * The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute()
         */
        protected Void doInBackground(String... urls) {

//            Log.e("important", "onMultipartUploadClick response is " + filesToUploadString);
            final String[] filesToUploadArray = filesToUploadString.split(",");

            for (String fileToUploadPath : filesToUploadArray) {


                try {
                    final String filename = getFilename(fileToUploadPath);

                    MultipartUploadRequest req = new MultipartUploadRequest(mContext, serverUrlString)
                            .addFileToUpload(fileToUploadPath, paramNameString)
                            .setNotificationConfig(getNotificationConfig(filename))
                            .setCustomUserAgent(USER_AGENT)
                            .setAutoDeleteFilesAfterSuccessfulUpload(true)
                            .setUsesFixedLengthStreamingMode(false)
                            .setMaxRetries(0);


                    req.setUtf8Charset();
                    uploadedimage++;
                    req.setDelegate(this).startUpload();


                    // these are the different exceptions that may be thrown
                } catch (FileNotFoundException | MalformedURLException | IllegalArgumentException exc) {
                }

            }
            //while (!uploadDone && !this.isCancelled()) ;

            return null;
        }


    }
}
