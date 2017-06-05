package com.utsavmobileapp.utsavapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.android.rides.RideParameters;
import com.uber.sdk.android.rides.RideRequestButton;
import com.uber.sdk.core.auth.Scope;
import com.uber.sdk.rides.client.SessionConfiguration;
import com.utsavmobileapp.utsavapp.FestivalGallaryActivity;
import com.utsavmobileapp.utsavapp.MapsActivity;
import com.utsavmobileapp.utsavapp.R;
import com.utsavmobileapp.utsavapp.adapter.AdapterGallery;
import com.utsavmobileapp.utsavapp.adapter.AdapterGallerySingleRow;
import com.utsavmobileapp.utsavapp.data.Image;
import com.utsavmobileapp.utsavapp.data.StoryObject;
import com.utsavmobileapp.utsavapp.parser.ParseFestivalDetailsJSON;
import com.utsavmobileapp.utsavapp.parser.ParseStoryBoard;
import com.utsavmobileapp.utsavapp.service.Common;
import com.utsavmobileapp.utsavapp.service.LatLonCachingAPI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FetchFestivalDetailsInfoFragment extends AsyncTask<Void, Void, Void> {


    ParseFestivalDetailsJSON dtact;
    LinearLayout card, festivalContainerLayout, restaurantContainerLayout, festivalInfoContainerLayout;
    List<StoryObject> stories;
    TextView away;
    LinearLayout detailDir;
    LatLonCachingAPI llc;
    List<String> fName = new ArrayList<>();
    List<String> fId = new ArrayList<>();
    List<String> fAddress = new ArrayList<>();
    List<String> fDistance = new ArrayList<>();
    List<String> fRating = new ArrayList<>();
    List<String> fImg = new ArrayList<>();
    FragmentTransaction ft;
    Context mContext;
    com.utsavmobileapp.utsavapp.service.Common Common;
    private int noOfColumn = 6;

    public FetchFestivalDetailsInfoFragment(Context context, ParseFestivalDetailsJSON data, LinearLayout card, LinearLayout festivalContainerLayout, LinearLayout restaurantContainerLayout, LinearLayout festivalInfoContainerLayout, FragmentTransaction ft) {
        mContext = context;
        dtact = data;
        this.card = card;
        this.festivalContainerLayout = festivalContainerLayout;
        this.restaurantContainerLayout = restaurantContainerLayout;
        this.festivalInfoContainerLayout = festivalInfoContainerLayout;
        this.ft = ft;
        Common = new Common(mContext);
    }


    @Override
    protected Void doInBackground(Void... params) {
        //Set review
        ParseStoryBoard prstb = new ParseStoryBoard(mContext.getString(R.string.uniurl) + "/api/stroryboard.php?festival_id=" + dtact.getfId() + "&show_review=true", mContext);
        prstb.fetchJSON();
        while (prstb.parsingInComplete && (!this.isCancelled())) ;
        stories = prstb.getStories();
        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {

        //storyHead = (TextView) festivalInfoContainerLayout.findViewById(R.id.reviewHead);
        detailDir = (LinearLayout) festivalInfoContainerLayout.findViewById(R.id.lAddress);
        away = (TextView) festivalInfoContainerLayout.findViewById(R.id.detailsAway);
        ImageView viewById = (ImageView) festivalInfoContainerLayout.findViewById(R.id.imageFestival);
        TextView detailsAddress = (TextView) festivalInfoContainerLayout.findViewById(R.id.detailsAddress);
        RecyclerView recyclerView = (RecyclerView) festivalInfoContainerLayout.findViewById(R.id.recyclerSmall);

        ShowStories();


        //Set address
        detailsAddress.setText(dtact.getfAddress());
        away.setText(String.format("You are %s away", dtact.getfDistance()));
        String latEiffelTower = dtact.getFlat();
        String lngEiffelTower = dtact.getFlon();
        String url = "http://maps.google.com/maps/api/staticmap?center=" + latEiffelTower + "," + lngEiffelTower + "&zoom=15&size=100x100&sensor=false&api=" + mContext.getString(R.string.google_maps_key);
        Common.ImageDownloaderTask(viewById, mContext, url, "festival");
        llc = new LatLonCachingAPI(mContext);

        SessionConfiguration config = new SessionConfiguration.Builder()
                .setClientId(mContext.getString(R.string.Uber_Client_ID)) //This is necessary
                .setEnvironment(SessionConfiguration.Environment.PRODUCTION)

                .setScopes(Collections.singletonList(Scope.RIDE_WIDGETS))
                .build();
        UberSdk.initialize(config);

        try {
            RideParameters rideParams = new RideParameters.Builder()
                    .setPickupLocation(Double.parseDouble(llc.readLat()), Double.parseDouble(llc.readLng()), null, null)
                    .setDropoffLocation(Double.parseDouble(dtact.getFlat()), Double.parseDouble(dtact.getFlon()), null, null)
                    .build();


            RideRequestButton requestButton = (RideRequestButton) festivalInfoContainerLayout.findViewById(R.id.uberBtn);
            requestButton.setRideParameters(rideParams);


            requestButton.loadRideInformation();
        } catch (NullPointerException ignored) {
        }

        detailDir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, MapsActivity.class);
                i.putExtra("lat", dtact.getFlat());
                i.putExtra("lon", dtact.getFlon());
                i.putExtra("name", dtact.getfName());
                i.putExtra("distance", dtact.getfDistance());
                mContext.startActivity(i);
            }
        });

        //Set images
        if (dtact.imgList.size() == 0) {
            recyclerView.setVisibility(View.GONE);
        } else {
            ArrayList<Image> images = new ArrayList<>();
            final ArrayList<String> imageIds;
            imageIds = (ArrayList<String>) dtact.imgListId;
            final ArrayList<Image> finalImages = new ArrayList<>();

            int imgIndx = 0;
            for (String ignored : dtact.imgList) {
                Image imageAux = new Image();
                imageAux.setName("Other Images");
                imageAux.setSmall(ignored);
                imageAux.setMedium(dtact.imgListNrml.get(imgIndx));
                imageAux.setLarge(dtact.imgListBig.get(imgIndx));
                imageAux.setTotalike(Integer.parseInt(dtact.imgListLk.get(imgIndx)));
                imageAux.setTotalcomment(Integer.parseInt(dtact.imgListCmt.get(imgIndx)));
                imageAux.setLiked(dtact.imgListIsLiked.get(imgIndx));
                finalImages.add(imageAux);
                images.add(imageAux);
                imgIndx++;
            }

            AdapterGallerySingleRow mAdapter = new AdapterGallerySingleRow(mContext, images, noOfColumn);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mContext, noOfColumn);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            mAdapter.removeLastItems(images.size() - noOfColumn, images.size());
            recyclerView.setAdapter(mAdapter);

            recyclerView.addOnItemTouchListener(new AdapterGallery.RecyclerTouchListener(mContext, recyclerView, new AdapterGallery.ClickListener() {
                @Override
                public void onClick(View view, int position) {

                    if (noOfColumn - 1 <= position) {
                        Intent i = new Intent(mContext, FestivalGallaryActivity.class);
                        Bundle args = new Bundle();
                        args.putSerializable("images", finalImages);
                        i.putExtra("imageBundles", args);
                        i.putStringArrayListExtra("imageIds", imageIds);
                        mContext.startActivity(i);
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("images", finalImages);
                        bundle.putSerializable("imageids", imageIds);
                        bundle.putInt("position", position);


                        // StorySlideshowFragment newFragment = StorySlideshowFragment.newInstance();
                        FragmentTransaction ft = ((AppCompatActivity) mContext).getSupportFragmentManager().beginTransaction();
                        FestivalSlideshowDialogFragment newFragment = FestivalSlideshowDialogFragment.newInstance();


                        newFragment.setArguments(bundle);
                        newFragment.show(ft, "slideshow");
                    }
                }

                @Override
                public void onLongClick(View view, int position) {

                }
            }));
        }


        super.onPostExecute(aVoid);
    }


    private void fetchImages() {


    }

    private void ShowStories() {
        if (stories.size() > 0) {
            for (final StoryObject oneStory : stories) {
                final LinearLayout linerLayoutStoryBoardCard = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.rating_view, card, false);


                ImageView uimg = (ImageView) linerLayoutStoryBoardCard.findViewById(R.id.storyBoardUserImageView);
                Common.ImageDownloaderTask(uimg, mContext, oneStory.getUob().getPrimg().replace("http", "https"), "user");

                TextView uname = (TextView) linerLayoutStoryBoardCard.findViewById(R.id.userNameTextView);
                uname.setText(oneStory.getUob().getName());

                TextView ur = (TextView) linerLayoutStoryBoardCard.findViewById(R.id.userFollowReviewTextView);
                ur.setText(String.format("%s Reveiews, %s Photos", oneStory.getUob().getReview(), oneStory.getUob().getPhoto()));

                TextView urat = (TextView) linerLayoutStoryBoardCard.findViewById(R.id.ratedTextView);
                if (!oneStory.getUserRating().equals("null") && !oneStory.getUserRating().equals("0"))
                    urat.setText(oneStory.getUserRating());
                else {
                    TextView ratedLbl = (TextView) linerLayoutStoryBoardCard.findViewById(R.id.ratedLbl);
                    ratedLbl.setVisibility(View.GONE);
                    urat.setVisibility(View.GONE);
                }

                TextView update = (TextView) linerLayoutStoryBoardCard.findViewById(R.id.agoTextView);
                update.setText(oneStory.getLastUpdate());

                TextView ucom = (TextView) linerLayoutStoryBoardCard.findViewById(R.id.usrCommentText);
                if (!oneStory.getUserComment().equals("null")) {
                    ucom.setText(oneStory.getUserComment());
                    card.addView(linerLayoutStoryBoardCard);
                } else
                    ucom.setVisibility(View.GONE);


            }
        } else {
            card.setVisibility(View.GONE);
//            storyHead.setVisibility(View.GONE);
//            showAllReview.setVisibility(View.GONE);
        }
    }

}
