package com.utsavmobileapp.utsavapp;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.utsavmobileapp.utsavapp.fragment.StoryFragment;

public class FestivalDetailsStoryActivity extends AppCompatActivity implements StoryFragment.OnFragmentInteractionListener {

    String fId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_festival_details_story);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
/*        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);*/
       /* getSupportActionBar().setTitle("Story Board");*/
        fId = getIntent().getStringExtra("fid");

        StoryFragment stf = new StoryFragment();
        Bundle bundle = new Bundle();
        bundle.putString("fid", fId);
        stf.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.detailsContainer, stf, "Story").commitAllowingStateLoss();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
