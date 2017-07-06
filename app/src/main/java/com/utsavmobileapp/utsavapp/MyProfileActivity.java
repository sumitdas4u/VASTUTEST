package com.utsavmobileapp.utsavapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.utsavmobileapp.utsavapp.parser.ParseSingleChatterJSON;
import com.utsavmobileapp.utsavapp.service.Common;
import com.utsavmobileapp.utsavapp.service.LatLonCachingAPI;
import com.utsavmobileapp.utsavapp.service.LoginCachingAPI;

import java.io.IOException;
import java.net.URLEncoder;

public class MyProfileActivity extends AppCompatActivity {

    ImageView dp;
    Common common;
    LoginCachingAPI lcp;
    LatLonCachingAPI llc;

    ImageView editDob, editStatus;
    EditText statusText;
    TextView dobLabel, statusLabel;
    TextView myName, myPhoto, myReview, myCheckin;
    String status,dob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        dp = (ImageView) findViewById(R.id.dpView);
        common = new Common(this);
        lcp = new LoginCachingAPI(this);
        llc = new LatLonCachingAPI(this);

        editDob = (ImageView) findViewById(R.id.edit_dob);
        editStatus = (ImageView) findViewById(R.id.edit_status);

        statusText = (EditText) findViewById(R.id.status_text);

        dobLabel = (TextView) findViewById(R.id.dob_lbl);
        statusLabel = (TextView) findViewById(R.id.status_lbl);

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
                                Common.HttpURLConnection(getString(R.string.uniurl) + "/api/user.php?type=UPDATE&user_id=" + lcp.readSetting("id") + "&lat=" + llc.readLat() + "&long=" + llc.readLng() + "&status=" + URLEncoder.encode(statusText.getText().toString(),"UTF-16")+"&dob="+dob);
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

        showInfo();
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
                        Common.HttpURLConnection(getString(R.string.uniurl) + "/api/user.php?type=UPDATE&user_id=" + lcp.readSetting("id") + "&lat=" + llc.readLat() + "&long=" + llc.readLng() + "&dob=" + selectedYear + "-" + selectedMonth + "-" + selectedDay+"&status="+URLEncoder.encode(status,"UTF-16"));
                    } catch (IOException ignored) {
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
        common.ImageDownloaderTask(dp, this, lcp.readSetting("photo"), "user");
        ParseSingleChatterJSON prnpj = new ParseSingleChatterJSON(this.getString(R.string.uniurl) + "/api/user.php?lat=" + llc.readLat() + "&long=" + llc.readLng() + "&type=SINGLE&user_id_lists=" + lcp.readSetting("id"), this);
        prnpj.fetchJSON();
        while (prnpj.parsingInComplete) ;
        myName.setText(prnpj.getuName());
        status=prnpj.getuStatus();
        statusLabel.setText(status);
        dob=prnpj.getuDob();
        dobLabel.setText(dob);
        myPhoto.setText(prnpj.getuTotalPhoto());
        myReview.setText(prnpj.getuTotalRvw());
        myCheckin.setText(prnpj.getuTotalChckIn());
    }
}
