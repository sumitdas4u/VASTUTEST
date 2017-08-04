package com.utsavmobileapp.utsavapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.andreabaccega.googlshortenerlib.GooglShortenerRequestBuilder;
import com.andreabaccega.googlshortenerlib.GooglShortenerResult;
import com.andreabaccega.googlshortenerlib.GoogleShortenerPerformer;
import com.cocosw.bottomsheet.BottomSheet;
import com.dd.CircularProgressButton;
import com.google.firebase.appindexing.Action;
import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.builders.Actions;
import com.squareup.okhttp.OkHttpClient;
import com.utsavmobileapp.utsavapp.adapter.detailsActivityImageAdapter;
import com.utsavmobileapp.utsavapp.fragment.FestivalDetailsInfoFragment;
import com.utsavmobileapp.utsavapp.fragment.StoryFragment;
import com.utsavmobileapp.utsavapp.parser.ParseFestivalDetailsJSON;
import com.utsavmobileapp.utsavapp.service.Common;
import com.utsavmobileapp.utsavapp.service.FBShareDlgActivity;
import com.utsavmobileapp.utsavapp.service.LatLonCachingAPI;
import com.utsavmobileapp.utsavapp.service.LoginCachingAPI;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DetailsActivity extends AppCompatActivity implements FestivalDetailsInfoFragment.OnFragmentInteractionListener, StoryFragment.OnFragmentInteractionListener {
    public String fName;
    public String fId;
    public String fAddress;
    public String fDistance;
    public String fRating;
    public String fImg;
    public String flat;
    public String flon;
    public String fdescription;
    public String fcontactName;
    public String fcontactNum;
    public String ftotalPhoto;
    public String ftotalReview;
    public String ftotalBookMark;
    public String ftotalCheckIn;
    public boolean fisBookMarked;
    LatLonCachingAPI lcp;
    TextView pujoRat;
    TextView pujoStat;
    TextView pujoContactPerson;
    TextView pujoContactNum;
    TextView pujoDescription;

    ProgressBar dtProg;
    AppBarLayout mainImgBar;
    ToggleButton dtbm;
    ToggleButton dtog;
    FloatingActionButton dfab;
    Button pujoShare;
    CircularProgressButton chckIn;



    List<String> imgList = new ArrayList<>();
    List<String> imgListNrml = new ArrayList<>();
    List<String> imgListBig = new ArrayList<>();
    List<String> imgListId = new ArrayList<>();
    List<String> imgListLk = new ArrayList<>();
    List<String> imgListCmt = new ArrayList<>();
    List<Boolean> imgListIsLiked = new ArrayList<>();
    Common common;
    LoginCachingAPI lca;
    FetchDetails FetchDetails;
    private ParseFestivalDetailsJSON festivalObject;
    private String longUrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lca=new LoginCachingAPI(this);

        Random rand = new Random();
        if((rand.nextInt(3) + 1)==2)
        {
            if (lca.readSetting("login").equals("true")) {
                if (lca.readSetting("subscription").equals("false")) {
                    AlertDialog alertDialog = new AlertDialog.Builder(DetailsActivity.this).create();
                    alertDialog.setTitle("Paysa de");
                    alertDialog.setCancelable(false);
                    alertDialog.setMessage("Give me money and I will give you freedom");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Buy",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
//                                        Intent i = new Intent(MyProfileActivity.this, SomeClass.class);
//                                        i.putExtra("key", "value");
//                                        startActivity(i);
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Poysa nei",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    DetailsActivity.this.finish();
                                }
                            });
                    alertDialog.show();

                }
            }
            else
            {
                Toast.makeText(this,"Please register yourself",Toast.LENGTH_LONG).show();
                DetailsActivity.this.finish();
            }
        }

        common = new Common(this);
        lcp = new LatLonCachingAPI(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            fId = extras.getString("id");
            fName = extras.getString("name");
            getSupportActionBar().setTitle(fName);
        }
        dtog = (ToggleButton) findViewById(R.id.storyInfoToggle);
        dtbm = (ToggleButton) findViewById(R.id.detailsBMToggle);
        mainImgBar = (AppBarLayout) findViewById(R.id.app_bar);
        /*pujoName = (TextView) findViewById(R.id.pujoName);
        pujoAddress = (TextView) findViewById(R.id.detailsAddress);
        pujoRating = (TextView) findViewById(R.id.detailsRating);*/
        dtProg = (ProgressBar) findViewById(R.id.detailsProgress);
        dfab = (FloatingActionButton) findViewById(R.id.detailsMenu);

        pujoRat = (TextView) findViewById(R.id.detailsRat);
        pujoStat = (TextView) findViewById(R.id.detailStat);
        pujoContactPerson = (TextView) findViewById(R.id.detailsContactPerson);
        pujoContactNum = (TextView) findViewById(R.id.detailsContactNum);
        pujoDescription = (TextView) findViewById(R.id.detailsDescription);
        pujoShare = (Button) findViewById(R.id.festivalShare);

        pujoShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT, "Find this festival on UTSAVAPP | " + fName + " " + longUrl);
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Download UtsavApp to find nearby festival");
                        startActivity(Intent.createChooser(intent, "Share"));
                    }
                }).start();
            }
        });


        chckIn = (CircularProgressButton) findViewById(R.id.checkIn);
        assert chckIn != null;
        chckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCheckIn();
            }
        });

        Button btnrate = (Button) findViewById(R.id.btnrate);
        assert btnrate != null;
        btnrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeReviewClick();
            }
        });


        dfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BottomSheet.Builder(DetailsActivity.this).grid().grid().sheet(R.menu.menu).listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case R.id.bottom_checkin:
                                doCheckIn();
                                break;
                            case R.id.bottom_photo:
                                Intent intent = new Intent(DetailsActivity.this, WriteReviewActivity.class);
                                intent.putExtra("id", fId);
                                intent.putExtra("mode", "pic");
                                intent.putExtra("lat", flat);
                                intent.putExtra("lon", flon);
                                intent.putExtra("description", fdescription);
                                intent.putExtra("head", fName);
                                intent.putExtra("url", longUrl);
                                intent.putExtra("image", fImg);
                                startActivity(intent);
                                break;
                            case R.id.bottom_review:
                                writeReviewClick();
                                break;
                            case R.id.bottom_rate:
                                writeReviewClick();
                                break;
                        }
                    }
                }).show();
            }
        });


        dtog.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Log.e("important","checked changed");
                if (isChecked) {
                    final StoryFragment stf = new StoryFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("fid", fId);
                    stf.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.detailsContainer, stf, "Story").commitAllowingStateLoss();
                } else
                    getSupportFragmentManager().beginTransaction().replace(R.id.detailsContainer, new FestivalDetailsInfoFragment(festivalObject, DetailsActivity.this), "Info").commitAllowingStateLoss();
            }
        });


        dtbm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (common.isLoggedIn(DetailsActivity.this)) {
                    if (isChecked) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    common.HttpURLConnection(getString(R.string.uniurl) + "/api/festival.php?type=SUBMIT_BOOKMARK&user_id=" + lca.readSetting("id") + "&festival_id=" + fId);

                                } catch (Exception e) {
                                    dtbm.setChecked(false);
                                    //Log.e("important","exception in reading "+e.getMessage());
                                }
                            }
                        }).start();


                    } else {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    common.HttpURLConnection(getString(R.string.uniurl) + "/api/festival.php?type=DELETE_BOOKMARK&user_id=" + lca.readSetting("id") + "&festival_id=" + fId);
                                } catch (Exception e) {
                                    //Log.e("important","exception in reading "+e.getMessage());
                                    dtbm.setChecked(true);
                                }
                            }
                        }).start();


                    }
                } else {

                    dtbm.setChecked(false);
                }
            }
        });

        FetchDetails = (FetchDetails) new FetchDetails().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void writeReviewClick() {
        Intent intent2 = new Intent(DetailsActivity.this, WriteReviewActivity.class);
        intent2.putExtra("id", fId);
        intent2.putExtra("mode", "rvw");
        intent2.putExtra("lat", flat);
        intent2.putExtra("lon", flon);
        intent2.putExtra("description", fdescription);
        intent2.putExtra("head", fName);
        intent2.putExtra("url", longUrl);
        intent2.putExtra("image", fImg);
        startActivity(intent2);
    }

    private void doCheckIn() {
        if (common.isLoggedIn(DetailsActivity.this)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            chckIn.setIndeterminateProgressMode(true); // turn on indeterminate progress
                            chckIn.setProgress(50); // set progress > 0 & < 100 to display indeterminate progress
                        }
                    });

                    try {
                        if (common.HttpURLConnection(getApplication().getString(R.string.uniurl) + "/api/festival.php?type=SUBMIT&check_in=YES&festival_id=" + fId + "&user_id=" + lca.readSetting("id")).toString().equals("1")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    chckIn.setProgress(100);
                                    //   Toast.makeText(getApplication(), "You are checked in", Toast.LENGTH_LONG).show();
                                    FBShareDlgActivity dFragment = new FBShareDlgActivity(new DetailsActivity(), DetailsActivity.this, flat, flon, fName, fImg, longUrl, fdescription);
                                    dFragment.show(getSupportFragmentManager(), "Facebook share");
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    chckIn.setProgress(-1);
                                }
                            });


                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    @Override
    public void onDestroy() {
        if (FetchDetails != null)
            FetchDetails.cancel(true);
        super.onDestroy();

    }

    private void storeDetailsData() {
        //  if(common.isLoggedIn(DetailsActivity.this))
        festivalObject = new ParseFestivalDetailsJSON(getString(R.string.uniurl) + "/api/festival.php?type=SINGLE&id=" + fId + "&lat=" + lcp.readLat() + "&long=" + lcp.readLng() + "&user_id=" + lca.readSetting("id"), DetailsActivity.this);
        //   else
        //  festivalObject = new ParseFestivalDetailsJSON(getString(R.string.uniurl) + "/api/festival.php?type=SINGLE&id=" + fId + "&lat=" + lcp.readLat() + "&long=" + lcp.readLng(), DetailsActivity.this);
        festivalObject.fetchJSON();
        while (festivalObject.parsingInComplete) ;
        try {
            fName = festivalObject.getfName();
            fAddress = festivalObject.getfAddress();
            fDistance = festivalObject.getfDistance();
            fRating = festivalObject.getfRating();
            fImg = festivalObject.getfImg();
            flat = festivalObject.getFlat();
            flon = festivalObject.getFlon();
            fdescription = festivalObject.getFdescription();
            fcontactName = festivalObject.getFcontactName();
            fcontactNum = festivalObject.getFcontactNum();
            ftotalPhoto = festivalObject.getFtotalPhoto();
            ftotalReview = festivalObject.getFtotalReview();
            ftotalBookMark = festivalObject.getFtotalBookMark();
            ftotalCheckIn = festivalObject.getFtotalCheckIn();
            fisBookMarked = festivalObject.isFisBookMarked();
            imgList = festivalObject.getImgList();
            imgListBig = festivalObject.getImgListBig();
            imgListCmt = festivalObject.getImgListCmt();
            imgListId = festivalObject.getImgListId();
            imgListIsLiked = festivalObject.getImgListIsLiked();
            imgListLk = festivalObject.getImgListLk();
            imgListNrml = festivalObject.getImgListNrml();
        } catch (Exception ignored) {

        }
    }

    private List<String> fetchImages() {

        List<String> GalImages = new ArrayList<>();
        int imgIndx = 0;
        GalImages.add(festivalObject.getfImg());
        for (String ignored : festivalObject.imgList) {
            if (imgIndx < 5) {
                GalImages.add(festivalObject.imgListBig.get(imgIndx));
            }

            imgIndx++;
        }
        return GalImages;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        return Actions.newView("Details", "http://[ENTER-YOUR-URL-HERE]");
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        FirebaseUserActions.getInstance().start(getIndexApiAction());
    }

    @Override
    public void onStop() {

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        FirebaseUserActions.getInstance().end(getIndexApiAction());
        super.onStop();
    }

    class FetchDetails extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            dtProg.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (fId != null) {
                GoogleShortenerPerformer shortener = new GoogleShortenerPerformer(new OkHttpClient());

                try {
                    longUrl = "https://rf6ef.app.goo.gl/?link=http://www.utsavapp.in&apn=com.utsavmobileapp.utsavapp&ad=0&al=utsavapp://" + fId + "~" + URLEncoder.encode(fName, "UTF-8");
                } catch (Exception e) {
                    // e.printStackTrace();
                }
                GooglShortenerResult result = shortener.shortenUrl(
                        new GooglShortenerRequestBuilder()
                                .buildRequest(longUrl, getApplication().getString(R.string.google_api_key))
                );

                if (GooglShortenerResult.Status.SUCCESS.equals(result.getStatus())) {
                    longUrl = result.getShortenedUrl();
                    //  Log.e("important",result.getShortenedUrl());

                }

            }

            storeDetailsData();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {


            dtProg.setVisibility(View.GONE);

                /* setMainImg(fImg, mainImgBar);
       pujoName.setText(fName);
            pujoRating.setText(fRating);
            pujoAddress.setText(fAddress);*/
            try {
                pujoRat.setText(fRating);
                pujoStat.setText(String.format("%s Reviews/ %s Bookmarks/ %s Checked In", ftotalReview, ftotalBookMark, ftotalCheckIn));
                if (fcontactNum.equals(""))
                    pujoContactNum.setVisibility(View.GONE);
                else {
                    pujoContactNum.setText(fcontactNum);
                    pujoContactNum.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + fcontactNum));
                            startActivity(intent);
                        }
                    });
                }
                if (fcontactName.equals(""))
                    pujoContactPerson.setText("");
                else
                    pujoContactPerson.setText(fcontactName);
                pujoDescription.setText(fdescription);
                if (fisBookMarked)
                    dtbm.setChecked(true);
            } catch (NullPointerException ignored) {

            }
            final ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
            final ImageButton right = (ImageButton) findViewById(R.id.right_nav);
            final ImageButton left = (ImageButton) findViewById(R.id.left_nav);
            final detailsActivityImageAdapter adapter = new detailsActivityImageAdapter(DetailsActivity.this, fetchImages());
            if (null != viewPager) {
                viewPager.setAdapter(adapter);
            }

            assert right != null;
            right.setVisibility(View.INVISIBLE);
            assert left != null;
            left.setVisibility(View.INVISIBLE);

            if (adapter.GalImages.size() > 1) {
                right.setVisibility(View.VISIBLE);
            }
            final ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }


                @Override
                public void onPageSelected(int position) {
                    if (position == adapter.GalImages.size() - 1) {
                        assert right != null;
                        right.setVisibility(View.INVISIBLE);
                        assert left != null;
                        left.setVisibility(View.VISIBLE);
                    }

                    // Hide left arrow if reach first position
                    else if (position == 0) {
                        assert left != null;
                        left.setVisibility(View.INVISIBLE);
                        assert right != null;
                        right.setVisibility(View.VISIBLE);
                    }

                    // Else show both arrows
                    else {
                        if (left != null) {
                            left.setVisibility(View.VISIBLE);
                        }
                        if (right != null) {
                            right.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            };
            assert viewPager != null;
            viewPager.addOnPageChangeListener(onPageChangeListener);

            if (right != null) {
                right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ((viewPager.getCurrentItem()) < (viewPager.getRight())) {
                            viewPager.setCurrentItem((viewPager.getCurrentItem()) + 1, true);
                        }
                    }
                });
            }

            if (left != null) {
                left.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ((viewPager.getCurrentItem()) > (viewPager.getLeft())) {
                            viewPager.setCurrentItem((viewPager.getCurrentItem()) - 1, true);
                        }
                    }
                });
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.detailsContainer, new FestivalDetailsInfoFragment(festivalObject, DetailsActivity.this), "Info").commitAllowingStateLoss();
            //getSupportActionBar().setTitle(fName);

            this.cancel(true);
            super.onPostExecute(aVoid);
        }
    }
   /* public void setMainImg(final String imageUrl, final AppBarLayout mainImgBar) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(imageUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    final Bitmap myBitmap = BitmapFactory.decodeStream(input);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.JELLY_BEAN)
                                mainImgBar.setBackground(new BitmapDrawable(getResources(), myBitmap));
                            else
                                mainImgBar.setBackgroundDrawable(new BitmapDrawable(getResources(), myBitmap));
                        }
                    });
                } catch (IOException e) {
                    //Log.e("important",Log.getStackTraceString(e));
                }
            }
        }).start();
    }*/

//    private void setTab()
//    {
//        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
//        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
//        mTabHost.addTab(mTabHost.newTabSpec("t1").setIndicator("Info"), FestivalDetailsInfoFragment.class, null);
//        mTabHost.addTab(mTabHost.newTabSpec("t2").setIndicator("Storyboard"), FestivalDetailsStoryFragment.class, null);
//
////        TextView tv = (TextView) mTabHost.getCurrentTabView().findViewById(android.R.id.title); //for Selected Tab
////        tv.setTextColor(ContextCompat.getColor(this,R.color.white_text));
//        for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
//            mTabHost.getTabWidget().getChildAt(i).setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
//            TextView tv = (TextView) mTabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
//            tv.setTextColor(ContextCompat.getColor(this, R.color.white_text));
//        }
//    }
}
