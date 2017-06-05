package com.utsavmobileapp.utsavapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.utsavmobileapp.utsavapp.fragment.StoryFragment;
import com.utsavmobileapp.utsavapp.parser.ParseChatterJSON;
import com.utsavmobileapp.utsavapp.service.LatLonCachingAPI;

public class ProfileActivity extends AppCompatActivity implements StoryFragment.OnFragmentInteractionListener {

    String uId;
    Button startChat;
    LatLonCachingAPI llc;
    TextView nameView, tagLineView;
    ImageView dpView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        nameView = (TextView) findViewById(R.id.name);
        dpView = (ImageView) findViewById(R.id.profile_image);
        tagLineView = (TextView) findViewById(R.id.tag);

        uId = getIntent().getStringExtra("uid");
        llc = new LatLonCachingAPI(this);


        ParseChatterJSON prnpj = new ParseChatterJSON(this.getString(R.string.uniurl) + "/api/user.php?lat=" + llc.readLat() + "&long=" + llc.readLng() + "&type=SINGLE&user_id_lists=" + uId, this);
        prnpj.fetchJSON();
        while (prnpj.parsingInComplete) ;
        nameView.setText(prnpj.getuName().get(0));
        tagLineView.setText(prnpj.getuTotalPhoto().get(0) + " Photos, " + prnpj.getuTotalRvw().get(0) + " Reviews");
        Glide.with(this).load(prnpj.getuImg().get(0)).into(dpView);


        final StoryFragment stf = new StoryFragment();
        Bundle bundle = new Bundle();
        bundle.putString("uid", uId);
        stf.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.storyScrollView, stf, "Story").commitAllowingStateLoss();

        startChat = (Button) findViewById(R.id.imageButton);
        startChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chatent = new Intent(ProfileActivity.this, ChatActivity.class);
                chatent.putExtra("grlfrnd", uId);
                startActivity(chatent);
            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
