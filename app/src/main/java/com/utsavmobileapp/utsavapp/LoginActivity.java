package com.utsavmobileapp.utsavapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.iid.FirebaseInstanceId;
import com.utsavmobileapp.utsavapp.parser.ParseLoginData;
import com.utsavmobileapp.utsavapp.service.Common;
import com.utsavmobileapp.utsavapp.service.LatLonCachingAPI;
import com.utsavmobileapp.utsavapp.service.LoginCachingAPI;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import javax.net.ssl.HttpsURLConnection;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 1;
    CallbackManager callbackManager;
    AccessTokenTracker accessTokenTracker;
    AccessToken fbAccessToken;
    String googleAccessToken;
    LoginCachingAPI lcp;
    GoogleApiClient mGoogleApiClient;
    GoogleSignInOptions gso;
    String usrId;

    Button skipLoginBtn, fLoginBtn, gLoginBtn;

    String user_fb_id = null;
    String user_google_id = null;
    String user_full_name = null;
    String user_photo = "null";
    String user_email = null;
    String user_gender = null;
    String user_dob = null;
    String user_lat = null;
    String user_long = null;
    LatLonCachingAPI llc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        skipLoginBtn = (Button) findViewById(R.id.btnSkipLogin);
        fLoginBtn = (Button) findViewById(R.id.fLogin);
        gLoginBtn = (Button) findViewById(R.id.gLogin);
        lcp = new LoginCachingAPI(this);


        llc = new LatLonCachingAPI(this);

        ////////////////////////Facebook initialiazation section starts///////////////////////////////////

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            }
        };
        //fbAccessToken = AccessToken.getCurrentAccessToken();
        accessTokenTracker.startTracking();

        /////////////////////////Google initialization section starts/////////////////////////////////////

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Bundle extras = getIntent().getExtras();
        if (extras.getString("method") != null) {
            if (extras.getString("method").equals("facebook")) {
                skipLoginBtn.setVisibility(View.INVISIBLE);
                gLoginBtn.setVisibility(View.INVISIBLE);
            } else if (extras.getString("method").equals("force")) {
                skipLoginBtn.setVisibility(View.INVISIBLE);
            }
        }
        if (extras.getString("mode") != null) {
            if (extras.getString("mode").equals("login")) {
                LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        fbAccessToken = loginResult.getAccessToken();
                        accessTokenTracker.startTracking();
                        usrId = fbAccessToken.getUserId();

                        GraphRequest request = GraphRequest.newMeRequest(fbAccessToken,
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        user_email = object.optString("email");
                                        user_full_name = object.optString("name");
                                        user_dob = object.optString("birthday");
                                        user_fb_id = usrId;
                                        user_gender = object.optString("gender");
                                        new UpDateToServer().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,gender,birthday,email");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("username", "Error");
                        returnIntent.putExtra("isloggedin", "false");
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("username", "Error");
                        returnIntent.putExtra("isloggedin", "false");
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    }
                });


                if (skipLoginBtn != null) {
                    skipLoginBtn.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            lcp.addUpdateSettings("ask_login", "false");
                            finish();
                        }
                    });
                }


                if (fLoginBtn != null) {
                    fLoginBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "email", "user_birthday"));
                        }
                    });
                }
                assert gLoginBtn != null;
                gLoginBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                        startActivityForResult(signInIntent, RC_SIGN_IN);
                    }
                });
            } else {
                skipLoginBtn.setVisibility(View.INVISIBLE);
                gLoginBtn.setVisibility(View.INVISIBLE);
                fLoginBtn.setVisibility(View.INVISIBLE);
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
                dlgAlert.setMessage("Are you sure you want to logout?");
                dlgAlert.setTitle("Are you sure?");
                dlgAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logout();
                    }
                });
                dlgAlert.setNegativeButton("No", null);
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }

    }

    //That's google sign in

    private void handleSignInResult(GoogleSignInResult result) {
        //Log.e("important", "result is " + result.getStatus());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            //Log.e("important", "you are logged in with " + acct.getPhotoUrl());
            user_google_id = acct.getId();
            user_full_name = acct.getDisplayName();
            user_email = acct.getEmail();
            if (acct.getPhotoUrl() != null)
                user_photo = acct.getPhotoUrl().toString();
            googleAccessToken = acct.getIdToken();

            new UpDateToServer().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            // Signed out, show unauthenticated UI.
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //Log.e("important",connectionResult.getErrorMessage());
    }

    private void logout() {
        fbLogout();
        googleLogOut();
        lcp.addUpdateSettings("login", "false");
        Toast.makeText(this, "You are logged out", Toast.LENGTH_LONG).show();
        Intent returnIntent = new Intent();
        returnIntent.putExtra("username", "");
        returnIntent.putExtra("isloggedin", "false");
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    private void fbLogout() {
        LoginManager.getInstance().logOut();
    }

    private void googleLogOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        //Log.e("important", status.getStatusMessage());
                    }
                });
    }

    class UpDateToServer extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL(getString(R.string.uniurl) + "/api/user.php?type=LOGIN");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                conn.connect();
                //Log.e("important",getPostData());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
//                Log.e("important", getPostData());
                writer.write(getPostData());
                writer.flush();
                writer.close();
                os.close();
                int responseCode = conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    if ((line = br.readLine()) != null) {

                        //Log.e("important", "readline"+   br.readLine());
                        ParseLoginData pld = new ParseLoginData();
                        pld.parseJSONAndStoreIt(line);
                        while (pld.parsingInComplete) ;
                        lcp.addUpdateSettings("login", pld.getUserIsLoggedIn());
                        lcp.addUpdateSettings("id", pld.getUserId());
                        lcp.addUpdateSettings("fbid", pld.getUserFBId());
                        lcp.addUpdateSettings("googleid", pld.getUserGoogleId());
                        lcp.addUpdateSettings("name", pld.getUserNAme());

                        if (lcp.readSetting("googleid").equals("null"))
                            lcp.addUpdateSettings("photo", pld.getUserSmallPhoto().replace("http", "https"));
                        else
                            lcp.addUpdateSettings("photo", pld.getUserSmallPhoto());
                        lcp.addUpdateSettings("age", pld.getUserAge());
                        lcp.addUpdateSettings("gender", pld.getUserGender());
                        lcp.addUpdateSettings("fbToken", fbAccessToken.getToken());
                        lcp.addUpdateSettings("gToken", googleAccessToken);

                        lcp.addUpdateSettings("ask_login", "true");
                    }
                }


            } catch (Exception e) {
                //Log.e("important",Log.getStackTraceString(e));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            super.onPostExecute(aVoid);

            final String urlString;
            urlString = getString(R.string.uniurl) + "/api/user.php?type=UPDATE&user_id=" + lcp.readSetting("id") + "&lat=" + llc.readLat() + "&long=" + llc.readLng();
//            Log.e("important", urlString);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String sb = Common.HttpURLConnection(urlString);
                        //saveData(sb);
                        //   Log.e("important", "login found string " +sb.toString());

                        // stream.close();
                    } catch (Exception e) {
                        //  Log.e("important", "exception in reading " + e.getMessage());
                    }
                }
            }).start();
            Intent returnIntent = new Intent();
            returnIntent.putExtra("username", lcp.readSetting("name"));
            returnIntent.putExtra("userdp", lcp.readSetting("photo"));
            returnIntent.putExtra("isloggedin", "true");
            setResult(RESULT_OK, returnIntent);
            finish();
        }

        private String getPostData() {
            StringBuilder result = new StringBuilder();

            try {
                JSONObject jsonobj = new JSONObject();

                jsonobj.put("user_fb_id", user_fb_id);
                jsonobj.put("user_google_id", user_google_id);
                jsonobj.put("user_full_name", user_full_name);
                jsonobj.put("user_photo", user_photo);
                jsonobj.put("user_email", user_email);
                jsonobj.put("user_gender", user_gender);
                jsonobj.put("user_dob", user_dob);
                jsonobj.put("user_lat", user_lat);
                jsonobj.put("user_long", user_long);
                jsonobj.put("user_token", FirebaseInstanceId.getInstance().getToken());

                result.append("logindata");
                result.append("=");
                result.append(jsonobj.toString());
            } catch (Exception ex) {
                return ex.getMessage();
            }
            return result.toString();
        }
    }
}
