package com.utsavmobileapp.utsavapp.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.utsavmobileapp.utsavapp.FestivalGallaryActivity;
import com.utsavmobileapp.utsavapp.fetch.FetchNearFestival;
import com.utsavmobileapp.utsavapp.fetch.FetchSponsored;
import com.utsavmobileapp.utsavapp.fetch.FetchZomato;
import com.utsavmobileapp.utsavapp.R;
import com.utsavmobileapp.utsavapp.ViewAllActivity;
import com.utsavmobileapp.utsavapp.WriteReviewActivity;
import com.utsavmobileapp.utsavapp.data.Image;
import com.utsavmobileapp.utsavapp.parser.ParseFestivalDetailsJSON;
import com.utsavmobileapp.utsavapp.service.LoginCachingAPI;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class FestivalDetailsInfoFragment extends Fragment {
    ParseFestivalDetailsJSON dtact;
    LinearLayout card, festivalContainerLayout, restaurantContainerLayout, festivalInfoContainerLayout;
    Button writeRvw, addPhoto;
    LoginCachingAPI lcp;
    Context mContext;
    Boolean visibleView = false;
    Button vad;
    TextView showAllReview;
    TextView showAllPhoto;
    FetchFestivalDetailsInfoFragment FetchFestivalDetailsInfoFragment;
    com.utsavmobileapp.utsavapp.fetch.FetchZomato FetchZomato;
    com.utsavmobileapp.utsavapp.fetch.FetchNearFestival FetchNearFestival;
    private ProgressBar nearFestivalProgress;
    private LinearLayout sponsored;

    public FestivalDetailsInfoFragment() {
    }

    @SuppressLint("ValidFragment")
    public FestivalDetailsInfoFragment(ParseFestivalDetailsJSON data, Context context) {
        dtact = data;
        mContext = context;
        lcp = new LoginCachingAPI(mContext);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        if (FetchFestivalDetailsInfoFragment != null)
            FetchFestivalDetailsInfoFragment.cancel(true);
        if (FetchZomato != null)
            FetchZomato.cancel(true);
        if (FetchNearFestival != null)
            FetchNearFestival.cancel(true);
        super.onDestroy();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view = inflater.inflate(R.layout.fragment_festival_details_info, container, false);
        card = (LinearLayout) view.findViewById(R.id.card);
        festivalContainerLayout = (LinearLayout) view.findViewById(R.id.nearContainer);
        restaurantContainerLayout = (LinearLayout) view.findViewById(R.id.nearRestaurantContainer);
        festivalInfoContainerLayout = (LinearLayout) view.findViewById(R.id.linearFestivalInfo);
        writeRvw = (Button) view.findViewById(R.id.writeReview);
        addPhoto = (Button) view.findViewById(R.id.addPic);
        vad = (Button) view.findViewById(R.id.detailsViewAllFestival);
        showAllReview = (TextView) festivalInfoContainerLayout.findViewById(R.id.detailsViewAllReviews);
        showAllPhoto = (TextView) festivalInfoContainerLayout.findViewById(R.id.detailsViewAllPhotos);
        sponsored = (LinearLayout) view.findViewById(R.id.linearLayoutSponsored);
        showAllPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Image> images = new ArrayList<>();
                final ArrayList<Image> finalImages = new ArrayList<>();
                final ArrayList<String> imageIds;
                imageIds = (ArrayList<String>) dtact.imgListId;

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


                Intent i = new Intent(mContext, FestivalGallaryActivity.class);
                Bundle args = new Bundle();
                args.putSerializable("images", finalImages);
                i.putExtra("imageBundles", args);
                i.putStringArrayListExtra("imageIds", imageIds);
                mContext.startActivity(i);
            }
        });

        showAllReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final StoryFragment stf = new StoryFragment();
                Bundle bundle = new Bundle();
                bundle.putString("fid", dtact.getfId());
                stf.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.detailsContainer, stf, "Story").commitAllowingStateLoss();
            }
        });

        vad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, ViewAllActivity.class);
                i.putExtra("type", "near");
                i.putExtra("lat", dtact.getFlat());
                i.putExtra("lon", dtact.getFlon());
                startActivity(i);
            }
        });
        final String fid;
        String fid1;
        try {

            fid1 = dtact.getfId();
        } catch (Exception e) {
            fid1 = null;
        }


        fid = fid1;
        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(getContext(), WriteReviewActivity.class);
                    intent.putExtra("id", fid);
                    intent.putExtra("mode", "pic");
                    intent.putExtra("lat", dtact.getFlat());
                    intent.putExtra("lon", dtact.getFlon());
                    intent.putExtra("description", dtact.getFdescription());
                    intent.putExtra("head", dtact.getfName());
                    intent.putExtra("url", "https://rf6ef.app.goo.gl/?link=http://www.utsavapp.in&apn=com.utsavmobileapp.utsavapp&ad=0&al=utsavapp://" + fid + "~" + URLEncoder.encode(dtact.getfName(), "UTF-8"));
                    intent.putExtra("image", dtact.getfImg());
                    startActivity(intent);
                } catch (UnsupportedEncodingException ignored) {
                }
            }
        });

        writeRvw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(getContext(), WriteReviewActivity.class);
                    intent.putExtra("id", fid);
                    intent.putExtra("mode", "rvw");
                    intent.putExtra("lat", dtact.getFlat());
                    intent.putExtra("lon", dtact.getFlon());
                    intent.putExtra("description", dtact.getFdescription());
                    intent.putExtra("head", dtact.getfName());
                    intent.putExtra("url", "https://rf6ef.app.goo.gl/?link=http://www.utsavapp.in&apn=com.utsavmobileapp.utsavapp&ad=0&al=utsavapp://" + fid + "~" + URLEncoder.encode(dtact.getfName(), "UTF-8"));
                    intent.putExtra("image", dtact.getfImg());
                    startActivity(intent);
                } catch (UnsupportedEncodingException ignored) {
                }
            }
        });
        nearFestivalProgress = (ProgressBar) view.findViewById(R.id.nearFestivalProgress);
        return view;
    }

    @Override
    public void onResume() {
        if (!visibleView) {
            try {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                FetchFestivalDetailsInfoFragment = (FetchFestivalDetailsInfoFragment) new FetchFestivalDetailsInfoFragment(getContext(), dtact, card, festivalContainerLayout, restaurantContainerLayout, festivalInfoContainerLayout, ft).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                FetchZomato = (FetchZomato) new FetchZomato(getContext(), restaurantContainerLayout, dtact.getFlat(), dtact.getFlon()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                FetchNearFestival = (FetchNearFestival) new FetchNearFestival(getContext(), festivalContainerLayout, nearFestivalProgress, "0", "10", dtact.getFlat(), dtact.getFlon(), false, null, null, true, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                new FetchSponsored(mContext, sponsored, dtact.getFlat(), dtact.getFlon()).execute();
                visibleView = true;
            } catch (NullPointerException e) {
                try {
                    FetchFestivalDetailsInfoFragment.cancel(true);
                    FetchZomato.cancel(true);
                    FetchNearFestival.cancel(true);
                } catch (Exception ignored) {


                }

            }
        }


        super.onResume();
    }

    public interface OnFragmentInteractionListener {
    }
}

