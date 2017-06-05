package com.utsavmobileapp.utsavapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.utsavmobileapp.utsavapp.fetch.FetchZone;

public class ZoneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zone);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.zoneProgress);
        ListView listView = (ListView) findViewById(R.id.zoneList);
        new FetchZone(this, progressBar, listView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }
}
