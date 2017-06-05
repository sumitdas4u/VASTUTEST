package com.utsavmobileapp.utsavapp;

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
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.utsavmobileapp.utsavapp.adapter.PhotoAdapter;
import com.utsavmobileapp.utsavapp.service.Common;
import com.utsavmobileapp.utsavapp.service.LoginCachingAPI;
import com.utsavmobileapp.utsavapp.service.SiliCompressor;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;

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

public class lost_found_activity extends AppCompatActivity {

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
        setContentView(R.layout.activity_lost_found_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Lost or Found");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        queue = Volley.newRequestQueue(this);
        common = new Common(this);
        Bundle extras = getIntent().getExtras();

        lcp = new LoginCachingAPI(this);

        body = (EditText) findViewById(R.id.reviewBody);

        recyclerView = (RecyclerView) findViewById(R.id.picked_recycler);
        photoAdapter = new PhotoAdapter(this, selectedPhotos);

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, OrientationHelper.VERTICAL));
        recyclerView.setAdapter(photoAdapter);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }

        Button addImg = (Button) findViewById(R.id.plus);
        if (addImg != null) {
            addImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                    } else {
                        openPicPicker();
                    }
                }
            });
        }


        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                PhotoPreview.builder()
                        .setPhotos(selectedPhotos)
                        .setCurrentItem(position)
                        .start(lost_found_activity.this);
            }
        }));

        submitReview = (Button) findViewById(R.id.submit);
        submitReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (common.isLoggedIn(lost_found_activity.this)) {
                    //uploadMultipart(WriteReviewActivity.this);
                    uploadReview();
                    //Toast.makeText(WriteReviewActivity.this, "Clicked", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(lost_found_activity.this, "Please log in to write a review", Toast.LENGTH_LONG).show();

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
                .start(lost_found_activity.this, PhotoPicker.REQUEST_CODE);
    }

    private void uploadReview() {

        if (body.getText().toString().length() < 20) {
            Toast.makeText(this, "Minimum 20 characters, please", Toast.LENGTH_LONG).show();
            return;
        } else
            reviewTxt = body.getText().toString();

        reviewTxt = "-- Lost or Found  -- " + reviewTxt;
        if (filesToUploadString != null) {
            final String url;
            try {
                url = getString(R.string.uniurl) + "/api/festival.php?type=SUBMIT&user_id=" + lcp.readSetting("id") + "&rating_text=" + URLEncoder.encode(reviewTxt, "UTF-8") + "&photo_upload=YES";

                StringRequest getRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Success " + response);
                        String PHOTO_UPLOAD_URL = getString(R.string.uniurl) + "/api/festival.php?type=PHOTO_UPLOAD&user_id=" + lcp.readSetting("id") + "&story_board_id=" + response;
                        Log.e("important", "response is upload click " + PHOTO_UPLOAD_URL);
                        try {
                            new onMultipartUploadClick(PHOTO_UPLOAD_URL, "uploaded_file", getApplicationContext()).execute().get(2000, TimeUnit.MILLISECONDS);
                            Toast.makeText(getApplicationContext(), "Publishing....",
                                    Toast.LENGTH_SHORT).show();
                        } catch (InterruptedException | ExecutionException | TimeoutException e) {
                            e.printStackTrace();
                        }
                        // display response downloadFAQ.execute().get(2000, TimeUnit.MILLISECONDS);
                        Log.d("important", "response" + response);
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
                url = getString(R.string.uniurl) + "/api/festival.php?type=SUBMIT&rating_value=" + ratingTxt + "&user_id=" + lcp.readSetting("id") + "&rating_text=" + URLEncoder.encode(reviewTxt, "UTF-8");
                StringRequest getRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Success " + response);
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Error response " + error.getMessage());
                            }
                        });
                queue.add(getRequest);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


        }


        lost_found_activity.this.finish();
    }


    private class onMultipartUploadClick extends AsyncTask<String, Void, Void> implements UploadStatusDelegate {

        Bundle params;
        private Context mContext;
        private String serverUrlString;
        private String paramNameString;
        private int uploadedimage = 0;
        private int fbimage;

        public onMultipartUploadClick(String photo_upload_url, String uploaded_file, Context context) {
            this.serverUrlString = photo_upload_url;
            this.paramNameString = uploaded_file;
            this.mContext = context;
        }


        private UploadNotificationConfig getNotificationConfig() {


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
                Log.e(TAG, "Success:" + file);
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
            Log.e(TAG, "Error with ID: " + uploadInfo.getUploadId() + ": "
                    + exception.getLocalizedMessage(), exception);
            this.cancel(true);
        }


        @Override
        public void onCancelled(UploadInfo uploadInfo) {
//            Log.i(TAG, "Upload with ID " + uploadInfo.getUploadId() + " is cancelled");
            logSuccessfullyUploadedFiles(uploadInfo.getSuccessfullyUploadedFiles());
            this.cancel(true);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.i(TAG, "post");
            params = new Bundle();


            super.onPostExecute(aVoid);
        }

        @Override
        public void onCompleted(UploadInfo uploadInfo, ServerResponse serverResponse) {

        /*    Log.i(TAG, String.format(Locale.getDefault(),
                    "ID %1$s: completed in %2$ds at %3$.2f Kbit/s. Response code: %4$s, body:[%5$s]",
                    uploadInfo.getUploadId(), uploadInfo.getElapsedTime() / 1000,
                    uploadInfo.getUploadRate(), serverResponse.toString(),
                    serverResponse.getBodyAsString()));
            Log.i(TAG, "image[" + fbimage + "][url]" + serverResponse.getBodyAsString());*/


            fbimage++;

            if (fbimage == uploadedimage) {
                Toast.makeText(getApplicationContext(), "Your activity has been published in STORYBOARD",
                        Toast.LENGTH_SHORT).show();
            }


            this.cancel(true);


        }

        /**
         * The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute()
         */
        protected Void doInBackground(String... urls) {

            Log.e("important", "onMultipartUploadClick response is " + filesToUploadString);
            final String[] filesToUploadArray = filesToUploadString.split(",");

            for (String fileToUploadPath : filesToUploadArray) {


                try {
                    final String filename = getFilename(fileToUploadPath);

                    MultipartUploadRequest req = new MultipartUploadRequest(mContext, serverUrlString)
                            .addFileToUpload(fileToUploadPath, paramNameString)
                            .setNotificationConfig(getNotificationConfig())
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
