package com.utsavmobileapp.utsavapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class SearchResultActivity extends AppCompatActivity {

    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tv = (TextView) findViewById(R.id.textView2);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            tv.setText("You searched " + extras.getString("currentlat") + ", " + extras.getString("currentlon"));
        }
    }
}
