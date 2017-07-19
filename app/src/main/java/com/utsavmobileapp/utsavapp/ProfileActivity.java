package com.utsavmobileapp.utsavapp;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.utsavmobileapp.utsavapp.adapter.AdapterGallery;
import com.utsavmobileapp.utsavapp.adapter.AdapterGallerySingleRow;
import com.utsavmobileapp.utsavapp.data.Image;
import com.utsavmobileapp.utsavapp.fetch.FetchCheckin;
import com.utsavmobileapp.utsavapp.fragment.StoryFragment;
import com.utsavmobileapp.utsavapp.fragment.StorySlideshowFragment;
import com.utsavmobileapp.utsavapp.parser.ParseSingleChatterJSON;
import com.utsavmobileapp.utsavapp.service.LatLonCachingAPI;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity implements StoryFragment.OnFragmentInteractionListener {

    String uId;
    Button startChat;
    LatLonCachingAPI llc;
    TextView nameView, tagLineView;
    ImageView dpView;
    RecyclerView imageStrip;

    LinearLayout checkInLayout;
    ProgressBar checkInProgress,picProgress;
    ImageButton back;
    TextView photo, review, checkin;
    private int noOfColumn = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nameView = (TextView) findViewById(R.id.name);
        dpView = (ImageView) findViewById(R.id.profile_image);
        tagLineView = (TextView) findViewById(R.id.tag);

        photo= (TextView) findViewById(R.id.txtPhoto);
        review= (TextView) findViewById(R.id.txtRvw);
        checkin= (TextView) findViewById(R.id.txtChkIns);

        imageStrip = (RecyclerView) findViewById(R.id.recyclerSmall);
        checkInLayout = (LinearLayout) findViewById(R.id.linearLayout3);
        checkInProgress = (ProgressBar) findViewById(R.id.usrCheckinProgress);
        picProgress = (ProgressBar) findViewById(R.id.usrPicProgress);

        uId = getIntent().getStringExtra("uid");
        llc = new LatLonCachingAPI(this);

        back = (ImageButton) findViewById(R.id.goBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ParseSingleChatterJSON prnpj = new ParseSingleChatterJSON(this.getString(R.string.uniurl) + "/api/user.php?lat=" + llc.readLat() + "&long=" + llc.readLng() + "&type=SINGLE&user_id_lists=" + uId, this);
        prnpj.fetchJSON();
        while (prnpj.parsingInComplete) ;
        nameView.setText(prnpj.getuName());
        tagLineView.setText(prnpj.getuStatus());
        photo.setText(prnpj.getuTotalPhoto());
        review.setText(prnpj.getuTotalRvw());
        checkin.setText(prnpj.getuTotalChckIn());
        Glide.with(this).load(prnpj.getuImg()).into(dpView);


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

        new FetchCheckin(this, checkInLayout, checkInProgress, "0", "10", llc.readLat(), llc.readLng(), uId).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        final ArrayList<Image> images = new ArrayList<>();
        final ArrayList<Image> images1 = new ArrayList<>();
        final ArrayList<String> imageIds = new ArrayList<>();
        for (int imgIndx = 0; imgIndx < prnpj.getImgListBig().size(); imgIndx++) {
            Image imageAux = new Image();
            imageAux.setName("Other Images");
            imageAux.setSmall(prnpj.getImgListNrml().get(imgIndx));
            imageAux.setMedium(prnpj.getImgList().get(imgIndx));
            imageAux.setLarge(prnpj.getImgListBig().get(imgIndx));
            imageAux.setUploader(prnpj.getuName());
            imageAux.setPlace("NA");
            imageAux.setUploaderDp(prnpj.getuImg());
            imageAux.setUploaderId(prnpj.getuId());
            imageAux.setTotalike(Integer.parseInt(prnpj.getImgListLk().get(imgIndx)));
            imageAux.setTotalcomment(Integer.parseInt(prnpj.getImgListCmt().get(imgIndx)));
            imageAux.setLiked(prnpj.getImgListIsLiked().get(imgIndx));
            //   Log.e("important", " comment size "+imgIndx+ "  comments - " +stories.get( storyIndex ).getOtherImg().size() + "storyindex"+storyIndex);
            images.add(imageAux);
            imageIds.add(prnpj.getImgListId().get(imgIndx));


        }

        for (int imgIndx = 0; imgIndx < prnpj.getImgListBig().size(); imgIndx++) {
            Image imageAux = new Image();
            imageAux.setName("Other Images");
            imageAux.setSmall(prnpj.getImgListNrml().get(imgIndx));
            imageAux.setMedium(prnpj.getImgList().get(imgIndx));
            imageAux.setLarge(prnpj.getImgListBig().get(imgIndx));
            imageAux.setUploader(prnpj.getuName());
            imageAux.setPlace("NA");
            imageAux.setUploaderDp(prnpj.getuImg());
            imageAux.setUploaderId(prnpj.getuId());
            imageAux.setTotalike(Integer.parseInt(prnpj.getImgListLk().get(imgIndx)));
            imageAux.setTotalcomment(Integer.parseInt(prnpj.getImgListCmt().get(imgIndx)));
            imageAux.setLiked(prnpj.getImgListIsLiked().get(imgIndx));
            //   Log.e("important", " comment size "+imgIndx+ "  comments - " +stories.get( storyIndex ).getOtherImg().size() + "storyindex"+storyIndex);
            images1.add(imageAux);
            imageIds.add(prnpj.getImgListId().get(imgIndx));


        }

        AdapterGallerySingleRow mAdapter = new AdapterGallerySingleRow(this, images, noOfColumn);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, noOfColumn);
        imageStrip.setLayoutManager(mLayoutManager);


        imageStrip.setItemAnimator(new DefaultItemAnimator());

        mAdapter.removeLastItems(images.size() - noOfColumn, images.size());

        imageStrip.setAdapter(mAdapter);

        picProgress.setVisibility(View.GONE);
        imageStrip.addOnItemTouchListener(new AdapterGallery.RecyclerTouchListener(this, imageStrip, new AdapterGallery.ClickListener() {
            @Override
            public void onClick(View view, int position) {

//                if (noOfColumn - 1 <= position) {
//                    Intent i = new Intent(ProfileActivity.this, StoryGalleryActivity.class);
//                    i.putExtra("story", stories.get(index));
//                    i.putExtra("storyIndex", index);
//                    startActivity(i);
//                } else {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("images", images1);
                    bundle.putSerializable("imageids", imageIds);
                    bundle.putInt("position", position);
                // TODO: 14-06-2017 to check what to send when there is no story involved
                    bundle.putInt("storyId", 0);
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    StorySlideshowFragment newFragment = StorySlideshowFragment.newInstance();
                    newFragment.setArguments(bundle);
                    newFragment.show(ft, "slideshow");
//                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
