package com.utsavmobileapp.utsavapp.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.utsavmobileapp.utsavapp.R;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Bibaswann on 24-08-2016.
 */
public class FBShareDlgActivity extends DialogFragment {

    private static final int LOG_IN_OUT = 100;
    private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
    Button fbY, fbN;
    Context mContext;
    String mLat, mLon, mHead, mImage, mUrl, mDescription;
    Activity mActivity;
    private boolean pendingPublishReauthorization = false;

    public FBShareDlgActivity() {


    }

    @SuppressLint("ValidFragment")
    public FBShareDlgActivity(Activity activity, Context context, String lat, String lon, String head, String image, String url, String description) {
        mContext = context;
        mLat = lat;
        mLon = lon;
        mHead = head;
        mImage = image;
        mUrl = url;
        mDescription = description;
        mActivity = activity;
        FacebookSdk.sdkInitialize(mContext);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.share_dialog, container, false);
        getDialog().setTitle("Share on facebook");
        // Do something else

        fbN = (Button) rootView.findViewById(R.id.fbNoCheckIn);
        fbY = (Button) rootView.findViewById(R.id.fbCheckIn);
        fbY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!AccessToken.getCurrentAccessToken().getPermissions().contains("publish_actions")) {
                        Log.e("important", "no publish action");


                        LoginManager.getInstance().logInWithPublishPermissions((Activity) mContext, Arrays.asList("publish_actions"));
                    }
                    Common.shareOnFB(mContext, "vist", mLat, mLon, mHead, mImage, mUrl, mDescription, null);
                    FBShareDlgActivity.this.dismiss();
                } catch (Exception ignored) {
                    //            Log.e("important", ignored.getMessage());
                  /*  Intent intent = new Intent(mContext, LoginActivity.class);
                    intent.putExtra("mode", "login");
                    intent.putExtra("method", "facebook");
                    startActivityForResult(intent, LOG_IN_OUT);*/
                }
            }
        });

        fbN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FBShareDlgActivity.this.dismiss();
            }
        });
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOG_IN_OUT) {
            if (resultCode == Activity.RESULT_OK) {
                LoginManager.getInstance().logInWithPublishPermissions(this, Arrays.asList("publish_actions"));
                Common.shareOnFB(mContext, "vist", mLat, mLon, mHead, mImage, mUrl, mDescription, null);
                FBShareDlgActivity.this.dismiss();
            }
        }
    }
}
