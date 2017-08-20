package com.utsavmobileapp.utsavapp;

import android.*;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.utsavmobileapp.utsavapp.adapter.ChatterAdapter;
import com.utsavmobileapp.utsavapp.parser.ParseSingleChatterJSON;
import com.utsavmobileapp.utsavapp.service.Common;
import com.utsavmobileapp.utsavapp.service.LatLonCachingAPI;
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
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import de.hdodenhof.circleimageview.CircleImageView;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;

public class MyProfileActivity extends AppCompatActivity {

    ImageView dp;
    LoginCachingAPI lcp;
    LatLonCachingAPI llc;

    private static final String USER_AGENT = "UploadServiceDemo/" + BuildConfig.VERSION_NAME;

    ImageView editDob, editStatus,editDp;
    EditText statusText;
    TextView dobLabel, statusLabel, subscriptionLbl;
    TextView myName, myPhoto, myReview, myCheckin;
    String status, dob;

    Button buy;

    ArrayList<String> paths;

    CallbackManager callbackManager;

    ArrayList<String> selectedPhotos = new ArrayList<>();
    RequestQueue queue;
    Common common;
    private String filesToUploadString = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        lcp = new LoginCachingAPI(this);


        dp = (ImageView) findViewById(R.id.dpView);
        common = new Common(this);
        lcp = new LoginCachingAPI(this);
        llc = new LatLonCachingAPI(this);

        editDob = (ImageView) findViewById(R.id.edit_dob);
        editStatus = (ImageView) findViewById(R.id.edit_status);
        editDp = (ImageView) findViewById(R.id.dpChange);

        statusText = (EditText) findViewById(R.id.status_text);

        dobLabel = (TextView) findViewById(R.id.dob_lbl);
        statusLabel = (TextView) findViewById(R.id.status_lbl);
        subscriptionLbl = (TextView) findViewById(R.id.subscription_lbl);

        buy = (Button) findViewById(R.id.buy_btn);

        myName = (TextView) findViewById(R.id.my_name);
        myPhoto = (TextView) findViewById(R.id.my_photos);
        myReview = (TextView) findViewById(R.id.my_reviews);
        myCheckin = (TextView) findViewById(R.id.my_checkins);

        editDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(0);
            }
        });
        editStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (statusText.getVisibility() == View.VISIBLE) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Common.HttpURLConnection(getString(R.string.uniurl) + "/api/user.php?type=UPDATE&user_id=" + lcp.readSetting("id") + "&lat=" + llc.readLat() + "&long=" + llc.readLng() + "&status=" + URLEncoder.encode(statusText.getText().toString(), "UTF-16"));
//                                Log.e("important", getString(R.string.uniurl) + "/api/user.php?type=UPDATE&user_id=" + lcp.readSetting("id") + "&lat=" + llc.readLat() + "&long=" + llc.readLng() + "&status=" + URLEncoder.encode(statusText.getText().toString(), "UTF-16"));
                            } catch (IOException ignored) {
                                Toast.makeText(MyProfileActivity.this, "Could not connect", Toast.LENGTH_SHORT).show();
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showInfo();
                                }
                            });
                        }
                    }).start();
                    statusText.setVisibility(View.GONE);
                    statusLabel.setVisibility(View.VISIBLE);
                } else {
                    statusText.setVisibility(View.VISIBLE);
                    statusLabel.setVisibility(View.GONE);
                }
            }
        });
        editDp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                } else {
                    openPicPicker();
                }
            }
        });

        showInfo();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (!lcp.readSetting("login").equals("true")) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("mode", "login");
            intent.putExtra("method", "force");
            startActivity(intent);
            return;

        }
    }
    @Override
    @Deprecated
    protected Dialog onCreateDialog(int id) {
        return new DatePickerDialog(this, datePickerListener, 1989, 9, 19);
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, final int selectedYear, final int selectedMonth, final int selectedDay) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Common.HttpURLConnection(getString(R.string.uniurl) + "/api/user.php?type=UPDATE&user_id=" + lcp.readSetting("id") + "&lat=" + llc.readLat() + "&long=" + llc.readLng() + "&dob=" + selectedYear + "-" + selectedMonth + "-" + selectedDay);
//                        Log.e("important",getString(R.string.uniurl) + "/api/user.php?type=UPDATE&user_id=" + lcp.readSetting("id") + "&lat=" + llc.readLat() + "&long=" + llc.readLng() + "&dob=" + selectedYear + "-" + selectedMonth + "-" + selectedDay);
                    } catch (IOException ignored) {
                        Toast.makeText(MyProfileActivity.this, "Could not connect", Toast.LENGTH_SHORT).show();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showInfo();
                        }
                    });
                }
            }).start();
        }
    };

    private void showInfo() {
        ParseSingleChatterJSON prnpj = new ParseSingleChatterJSON(this.getString(R.string.uniurl) + "/api/user.php?lat=" + llc.readLat() + "&long=" + llc.readLng() + "&type=SINGLE&user_id_lists=" + lcp.readSetting("id"), this);
//        Log.e("important", getString(R.string.uniurl) + "/api/user.php?lat=" + llc.readLat() + "&long=" + llc.readLng() + "&type=SINGLE&user_id_lists=" + lcp.readSetting("id"));
        prnpj.fetchJSON();
        while (prnpj.parsingInComplete) ;
        myName.setText(prnpj.getuName());
        status = prnpj.getuStatus();
        statusLabel.setText(status);
        dob = prnpj.getuDob();
        dobLabel.setText(dob);
        ImageButton back = (ImageButton) findViewById(R.id.goBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        if (lcp.readSetting("subscription").equals("false")) {
            subscriptionLbl.setText("Free member");
            buy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Intent i = new Intent(MyProfileActivity.this, SomeClass.class);
//                    i.putExtra("key", "value");
//                    startActivity(i);
                    Toast.makeText(MyProfileActivity.this, "Falo kori makho tel", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            subscriptionLbl.setText("Premium member");
            buy.setVisibility(View.GONE);
        }
        myPhoto.setText(prnpj.getuTotalPhoto());
        myReview.setText(prnpj.getuTotalRvw());
        myCheckin.setText(prnpj.getuTotalChckIn());
        lcp.addUpdateSettings("photo", prnpj.getuImg());
        common.ImageDownloaderTask(dp, this, lcp.readSetting("photo"), "user");
    }
    public void actionClick(View v) {
        Intent myIntent = new Intent(this, SettingsActivity.class);
        //Optional parameters

        if(v.getId() == R.id.about_us){
            myIntent.putExtra("url",getString(R.string.uniurl) + "/about.html" );
            myIntent.putExtra("title", "About Us" );
        }else if(v.getId() == R.id.refund){
            myIntent.putExtra("url",getString(R.string.uniurl) + "/refund.html" );
            myIntent.putExtra("title", "Refund & Cancellation" );
        }else if(v.getId() == R.id.contact){
            myIntent.putExtra("url",getString(R.string.uniurl) + "/contact.html" );
            myIntent.putExtra("title", "Contact Us" );
        }else if(v.getId() == R.id.terms){
            myIntent.putExtra("url",getString(R.string.uniurl) + "/terms.html" );
            myIntent.putExtra("title", "Terms & Conditions" );
        }
        else if(v.getId() == R.id.privacy){
            myIntent.putExtra("url",getString(R.string.uniurl) + "/privacy.html" );
            myIntent.putExtra("title", "Privacy & Terms" );
        }
        else {

            myIntent.putExtra("url",getString(R.string.uniurl)  );
            myIntent.putExtra("title", "Home" );
        }
        this.startActivity(myIntent);
    }

    private void openPicPicker() {
        PhotoPicker.builder()
                .setPhotoCount(1)
                .setShowCamera(true)
                .setShowGif(true)
                .setPreviewEnabled(false)
                .setSelected(paths)
                .start(this, PhotoPicker.REQUEST_CODE);
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

            uploadp();

            Bitmap myBitmap = BitmapFactory.decodeFile(selectedPhotos.get(0));
            dp.setImageBitmap(myBitmap);
        }
    }

    private void uploadp() {
        String PHOTO_UPLOAD_URL = getString(R.string.uniurl) + "/api/user.php?type=UPDATE&user_id=" + lcp.readSetting("id");
        try {
            new onMultipartUploadClick(PHOTO_UPLOAD_URL, "profile_pic_file", getApplicationContext()).execute().get(2000, TimeUnit.MILLISECONDS);
            Toast.makeText(getApplicationContext(), "Publishing....", Toast.LENGTH_SHORT).show();
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    private class onMultipartUploadClick extends AsyncTask<String, Void, Void> implements UploadStatusDelegate {

        Bundle params;
        private Context mContext;
        private String serverUrlString;
        private String paramNameString;
        private Boolean uploadDone = false;

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
            showInfo();
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
