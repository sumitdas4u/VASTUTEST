package com.utsavmobileapp.utsavapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.utsavmobileapp.utsavapp.adapter.AdapterComment;
import com.utsavmobileapp.utsavapp.adapter.AdapterGallery;
import com.utsavmobileapp.utsavapp.adapter.AdapterGallerySingleRow;
import com.utsavmobileapp.utsavapp.adapter.AdapterLike;
import com.utsavmobileapp.utsavapp.data.Image;
import com.utsavmobileapp.utsavapp.data.StoryCommonObjectSingleton;
import com.utsavmobileapp.utsavapp.data.StoryFestivalCommonObjectSingleton;
import com.utsavmobileapp.utsavapp.data.StoryObject;
import com.utsavmobileapp.utsavapp.data.StoryUserCommonObjectSingleton;
import com.utsavmobileapp.utsavapp.fragment.StorySlideshowFragment;
import com.utsavmobileapp.utsavapp.parser.ParseStoryComments;
import com.utsavmobileapp.utsavapp.parser.ParseStoryLikes;
import com.utsavmobileapp.utsavapp.service.Common;
import com.utsavmobileapp.utsavapp.service.LoginCachingAPI;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class LikeCommentActivity extends AppCompatActivity {

    List<StoryObject> stories;
    ListView previousLks;
    ListView previousComments;
    ProgressBar lkprg, cmtprg;
    LikeButton btn_icon_add_bookmark;
    LikeButton btn_icon_like;
    // static Integer viewID;
    LoginCachingAPI lcp;
    EditText makeCmt;
    Button postCmt;
    Integer totalCmtInt;
    com.utsavmobileapp.utsavapp.service.Common Common;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client2;
    // boolean isLoggedIn;
    private Integer storyIndex;
    private ArrayList<Image> images;
    private ArrayList<String> imageIds;
    private int noOfColumn = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_like_comment);
        storyIndex = (Integer) getIntent().getSerializableExtra("concernedStory");
        String mode = (String) getIntent().getSerializableExtra("mode");
if(null==mode){
    mode="default";
}
        switch (mode) {
            case "festival":
                stories = StoryFestivalCommonObjectSingleton.getInstance().getStories();
                break;
            case "user":
                stories = StoryUserCommonObjectSingleton.getInstance().getStories();

                break;

            default:


                stories = StoryCommonObjectSingleton.getInstance().getStories();
                break;
        }

        lcp = new LoginCachingAPI(this);
        // isLoggedIn = lcp.readSetting("login").equals("true");
        Common = new Common(this);

        LinearLayout usrLayout = (LinearLayout) findViewById(R.id.usrLayout);
        usrLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent peopleDetails = new Intent(LikeCommentActivity.this, ProfileActivity.class);
                peopleDetails.putExtra("uid", stories.get(storyIndex).getUob().getId());
                startActivity(peopleDetails);
            }
        });

        ImageView fimg = (ImageView) findViewById(R.id.festivalphotoImageView);
        Common.ImageDownloaderTask(fimg, this, stories.get(storyIndex).getFob().getImg(), "festival");

        ImageView uimg = (ImageView) findViewById(R.id.storyBoardUserImageView);
        Common.ImageDownloaderTask(uimg, this, stories.get(storyIndex).getUob().getPrimg().replace("http", "https"), "user");

        TextView fname = (TextView) findViewById(R.id.festivalNameTextView);
        assert fname != null;
        fname.setText(stories.get(storyIndex).getFob().getName());

        final TextView faddr = (TextView) findViewById(R.id.festivalAddressTextView);
        assert faddr != null;
        faddr.setText(stories.get(storyIndex).getFob().getAddress());

        TextView uname = (TextView) findViewById(R.id.userNameTextView);
        assert uname != null;
        uname.setText(stories.get(storyIndex).getUob().getName());

        TextView ur = (TextView) findViewById(R.id.userFollowReviewTextView);
        assert ur != null;
        ur.setText(String.format("%s Reveiews, %s Photos", stories.get(storyIndex).getUob().getReview(), stories.get(storyIndex).getUob().getPhoto()));

        TextView urat = (TextView) findViewById(R.id.ratedTextView);
        if (!stories.get(storyIndex).getUserRating().equals("null") && !stories.get(storyIndex).getUserRating().equals("0")) {
            assert urat != null;
            urat.setText(stories.get(storyIndex).getUserRating());
        } else {
            TextView ratedLbl = (TextView) findViewById(R.id.ratedLbl);
            assert ratedLbl != null;
            ratedLbl.setVisibility(View.GONE);
            assert urat != null;
            urat.setVisibility(View.GONE);
        }

        TextView update = (TextView) findViewById(R.id.agoTextView);
        assert update != null;
        update.setText(stories.get(storyIndex).getLastUpdate());

        TextView usrCommentText = (TextView) findViewById(R.id.usrCommentText);
        assert usrCommentText != null;
        if (stories.get(storyIndex).getUserComment() != null)
            usrCommentText.setText(stories.get(storyIndex).getUserComment());

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerSmall);

        // btn_add_frnd = (Button) findViewById(R.id.icon_add_friend);
        //  btn_add_frnd.setTypeface(Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf"));

//        ImageView primg = (ImageView) findViewById(R.id.primg);
//        if (!oneStory.getPrimaryImg().equals("null")) {
//            new ImageDownloaderTask(primg, this, oneStory.getPrimaryImg()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        } else
//            primg.setVisibility(View.GONE);

        if (stories.get(storyIndex).getOtherImg().size() == 0) {
            recyclerView.setVisibility(View.GONE);
        } else /*{
            images = new ArrayList<>();
            imageIds = new ArrayList<>();
            fetchImages();
            AdapterGallery mAdapter = new AdapterGallery(getApplicationContext(),  images);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 6);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(mAdapter);
            recyclerView.addOnItemTouchListener(new AdapterGallery.RecyclerTouchListener(getApplicationContext(), recyclerView, new AdapterGallery.ClickListener() {
                @Override
                public void onClick(View view, int position) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("images", images);
                    bundle.putSerializable("imageids", imageIds);
                    bundle.putInt("position", position);
                    bundle.putInt("storyId", storyIndex);

                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    StorySlideshowFragment newFragment = StorySlideshowFragment.newInstance();
                    newFragment.setArguments(bundle);
                    newFragment.show(ft, "slideshow");
                }

                @Override
                public void onLongClick(View view, int position) {

                }
            }));
        }*/ {
            images = new ArrayList<>();
            imageIds = new ArrayList<>();
            fetchImages();

            AdapterGallerySingleRow mAdapter = new AdapterGallerySingleRow(getApplicationContext(), images, noOfColumn);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), noOfColumn);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            mAdapter.removeLastItems(images.size() - noOfColumn, images.size());


            recyclerView.setAdapter(mAdapter);
            recyclerView.addOnItemTouchListener(new AdapterGallery.RecyclerTouchListener(getApplicationContext(), recyclerView, new AdapterGallery.ClickListener() {
                @Override
                public void onClick(View view, int position) {

                    if (noOfColumn - 1 <= position) {
                        Intent i = new Intent(getApplicationContext(), StoryGalleryActivity.class);
                        i.putExtra("story", stories.get(storyIndex));
                        i.putExtra("storyIndex", storyIndex);

                        startActivity(i);
                    } else {


                        Bundle bundle = new Bundle();
                        bundle.putSerializable("images", images);
                        bundle.putSerializable("imageids", imageIds);
                        bundle.putInt("position", position);
                        bundle.putInt("storyId", storyIndex);
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        StorySlideshowFragment newFragment = StorySlideshowFragment.newInstance();
                        newFragment.setArguments(bundle);
                        newFragment.show(ft, "slideshow");
                    }
                }

                @Override
                public void onLongClick(View view, int position) {

                }
            }));
        }

        final TextView like = (TextView) findViewById(R.id.numLikeTextView);
        assert like != null;
        like.setText(String.format("%s Likes", stories.get(storyIndex).getNumLike()));
        if (!stories.get(storyIndex).getNumLike().equals("0")) {
            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog lkDlg = new Dialog(v.getContext());
                    lkDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    lkDlg.setContentView(R.layout.story_like_popup);
                    lkDlg.show();
                    previousLks = (ListView) lkDlg.findViewById(R.id.likeList);
                    lkprg = (ProgressBar) lkDlg.findViewById(R.id.likeProg);
                    new LoadLike().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                }
            });
        }
        btn_icon_like = (LikeButton) findViewById(R.id.iconlike);
        //  btn_icon_like.setTypeface(Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf"));
        if (stories.get(storyIndex).getLiked())
            btn_icon_like.setLiked(true);
        btn_icon_like.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                if (Common.isLoggedIn(LikeCommentActivity.this)) {

                    //  Toast.makeText(LikeCommentActivity.this, "The comments are shown below", Toast.LENGTH_SHORT).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {


                                //Log.e("important", "found string " +sb.toString());
                                if (Common.HttpURLConnection(getString(R.string.uniurl) + "/api/love.php?type=SUBMIT&user_id=" + lcp.readSetting("id") + "&storyboard_id=" + stories.get(storyIndex).getStoryId()).equals("1")) {
                                    //islikednow=true;
                                    // numlksnow=oneStory.getNumLike() + 1;
                                    stories.get(storyIndex).setNumLike(stories.get(storyIndex).getNumLike() + 1);
                                    stories.get(storyIndex).setLiked(true);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            like.setText(String.format("%s Likes", stories.get(storyIndex).getNumLike()));
                                        }
                                    });

                                } else {
                                    btn_icon_like.setLiked(false);
                                }

                            } catch (Exception e) {
                                //Log.e("important","exception in reading "+e.getMessage());
                            }
                        }
                    }).start();
                } else {
                    btn_icon_like.setLiked(false);
                }
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                if (Common.isLoggedIn(LikeCommentActivity.this)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {


                                //Log.e("important", "found string " +sb.toString());
                                if (Common.HttpURLConnection(getString(R.string.uniurl) + "/api/love.php?type=DELETE&user_id=" + lcp.readSetting("id") + "&storyboard_id=" + stories.get(storyIndex).getStoryId()).equals("1")) {
                                    //  islikednow=false;
                                    // numlksnow=oneStory.getNumLike() - 1;
                                    stories.get(storyIndex).setNumLike(stories.get(storyIndex).getNumLike() - 1);
                                    stories.get(storyIndex).setLiked(false);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //  if (btn_icon_like.getText().equals(getString(R.string.fa_heart_o))) {
                                            //    btn_icon_like.setText(R.string.fa_heart);
                                            like.setText(String.format("%s Likes", stories.get(storyIndex).getNumLike()));
                                            // } else {
                                            //    btn_icon_like.setText(R.string.fa_heart_o);
                                            //   like.setText(String.format("%s Likes", Integer.parseInt(oneStory.getNumLike()) - 1));
                                            // }
                                        }
                                    });

                                } else {
                                    btn_icon_like.setLiked(true);
                                }
                                //     stream.close();
                            } catch (Exception e) {
                                //Log.e("important","exception in reading "+e.getMessage());
                            }
                        }
                    }).start();


                } else {
                    btn_icon_like.setLiked(true);

                }
            }
        });


        btn_icon_add_bookmark = (LikeButton) findViewById(R.id.icon_add_bookmark);

        btn_icon_add_bookmark.setLiked(stories.get(storyIndex).getBookmarked());
        //   Log.e("important","bookmark "+storyIndex+ "==status=="+stories.get( storyIndex ).getBookmarked());
        btn_icon_add_bookmark.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                if (Common.isLoggedIn(LikeCommentActivity.this)) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {


                                //   Log.e("important", "found string " +sb.toString());
                                if (Common.HttpURLConnection(getString(R.string.uniurl) + "/api/festival.php?type=SUBMIT_BOOKMARK&user_id=" + lcp.readSetting("id") + "&festival_id=" + stories.get(storyIndex).getFob().getId()).equals("1")) {
                                    stories.get(storyIndex).setBookmarked(true);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            btn_icon_add_bookmark.setLiked(true);
                                        }
                                    });
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            btn_icon_add_bookmark.setLiked(false);
                                        }
                                    });

                                }


                                //   stream.close();
                            } catch (Exception e) {
                                //Log.e("important","exception in reading "+e.getMessage());
                            }
                        }
                    }).start();

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btn_icon_add_bookmark.setLiked(false);

                        }
                    });


                }

            }


            @Override
            public void unLiked(LikeButton likeButton) {

                {
                    if (Common.isLoggedIn(LikeCommentActivity.this)) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {

                                    StringBuilder sb;


                                    // Log.e("important", "found string " +sb.toString());
                                    if (Common.HttpURLConnection(getString(R.string.uniurl) + "/api/festival.php?type=DELETE_BOOKMARK&user_id=" + lcp.readSetting("id") + "&festival_id=" + stories.get(storyIndex).getFob().getId()).equals("1")) {
                                        stories.get(storyIndex).setBookmarked(false);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                btn_icon_add_bookmark.setLiked(false);
                                            }
                                        });

                                    } else {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                btn_icon_add_bookmark.setLiked(true);
                                            }
                                        });
                                    }

                                    //   stream.close();
                                } catch (Exception e) {
                                    //Log.e("important","exception in reading "+e.getMessage());
                                }
                            }
                        }).start();

                    } else {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btn_icon_add_bookmark.setLiked(false);
                            }
                        });
                    }
                }

            }
        });


        final TextView com = (TextView) findViewById(R.id.numCommentTextView);

        assert com != null;

        totalCmtInt = stories.get(storyIndex).getNumComment();
        com.setText(String.format("%s Comments", totalCmtInt));
        if (!totalCmtInt.equals(0)) {
            com.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(LikeCommentActivity.this, "The comments are shown below", Toast.LENGTH_SHORT).show();
                }
            });
        }

        previousComments = (ListView) findViewById(R.id.commentList);
        previousComments.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        cmtprg = (ProgressBar) findViewById(R.id.commentProg);
        new LoadComments().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        ImageView commenterDp = (ImageView) findViewById(R.id.cmtDp);
        Common.ImageDownloaderTask(commenterDp, this, lcp.readSetting("photo"), "user");
        makeCmt = (EditText) findViewById(R.id.makeCmt);
        postCmt = (Button) findViewById(R.id.postCmt);
        postCmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!makeCmt.getText().toString().trim().equals("") && Common.isLoggedIn(LikeCommentActivity.this)) {
                    final String cmtTxt;
                    String cmtTxt1;
                    try {
                        cmtTxt1 = URLEncoder.encode(makeCmt.getText().toString().trim(), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        cmtTxt1 = makeCmt.getText().toString().trim();
                    }
                    cmtTxt = cmtTxt1;
                    makeCmt.setText("");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {


                                if (Common.HttpURLConnection(getString(R.string.uniurl) + "/api/comments.php?type=SUBMIT&user_id=" + lcp.readSetting("id") + "&storyboard_id=" + stories.get(storyIndex).getStoryId() + "&comment_text=" + cmtTxt).equals("1")) {
                                    // numcmtnow=oneStory.getNumComment() + 1;
                                    stories.get(storyIndex).setNumComment(stories.get(storyIndex).getNumComment() + 1);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            com.setText(String.format("%s Comments", stories.get(storyIndex).getNumComment()));
                                            new LoadComments().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                        }
                                    });
                                }
                                // stream.close();
                            } catch (Exception e) {
                                //Log.e("important","exception in reading "+e.getMessage());
                            }
                        }
                    }).start();
                }
            }

        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //   client = new GoogleApiClient.Builder(this).addApi(com.google.android.gms.appindexing.AppIndex.API).build();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //     client2 = new GoogleApiClient.Builder(this).addApi(com.google.android.gms.appindexing.AppIndex.API).build();
    }

   /* @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client2.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "LikeComment Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.utsavmobileapp.utsavapp/http/host/path")
        );
        AppIndex.AppIndexApi.start(client2, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "LikeComment Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.utsavmobileapp.utsavapp/http/host/path")
        );
        AppIndex.AppIndexApi.end(client2, viewAction);
        client2.disconnect();
    }
*/

    private void fetchImages() {

        images.clear();
        imageIds.clear();
        int imgIndx = 0;
        for (String ignored : stories.get(storyIndex).getOtherImgBig()) {
            Image imageAux = new Image();
            imageAux.setName("Other Images");
            imageAux.setSmall(stories.get(storyIndex).getOtherImgNrml().get(imgIndx));
            imageAux.setMedium(stories.get(storyIndex).getOtherImg().get(imgIndx));
            imageAux.setLarge(stories.get(storyIndex).getOtherImgBig().get(imgIndx));
            imageAux.setUploader(stories.get(storyIndex).getUob().getName());
            imageAux.setPlace(stories.get(storyIndex).getFob().getName());
            imageAux.setUploaderDp(stories.get(storyIndex).getUob().getPrimg());
            imageAux.setTotalike(Integer.parseInt(stories.get(storyIndex).getOtherImglk().get(imgIndx)));
            imageAux.setTotalcomment(Integer.parseInt(stories.get(storyIndex).getOtherImgcmt().get(imgIndx)));
            imageAux.setLiked(stories.get(storyIndex).getOtherImgIsLiked().get(imgIndx));
            //   Log.e("important", " comment size "+imgIndx+ "  comments - " +stories.get( storyIndex ).getOtherImg().size() + "storyindex"+storyIndex);
            images.add(imageAux);
            imageIds.add(stories.get(storyIndex).getOtherImgId().get(imgIndx));
            imgIndx++;

        }

    }

    class LoadLike extends AsyncTask<Void, Void, Void> {
        ParseStoryLikes psl;

        LoadLike() {
            psl = new ParseStoryLikes(getString(R.string.uniurl) + "/api/love.php?type=STORYBOARD&storyboard_id=" + stories.get(storyIndex).getStoryId(), getApplicationContext());
        }

        @Override
        protected void onPreExecute() {
            lkprg.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            psl.fetchJSON();
            while (psl.parsingInComplete) ;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            lkprg.setVisibility(View.GONE);
            previousLks.setAdapter(new AdapterLike(LikeCommentActivity.this, psl.getUsrId().toArray(new String[psl.getUsrId().size()]), psl.getUsrDp().toArray(new String[psl.getUsrDp().size()]), psl.getUsrName().toArray(new String[psl.getUsrName().size()]), psl.getUsrAgo().toArray(new String[psl.getUsrAgo().size()])));
            super.onPostExecute(aVoid);
        }
    }

    class LoadComments extends AsyncTask<Void, Void, Void> {
        ParseStoryComments psc;

        LoadComments() {
            psc = new ParseStoryComments(getString(R.string.uniurl) + "/api/comments.php?type=STORYBOARD&storyboard_id=" + stories.get(storyIndex).getStoryId(), getApplicationContext());
        }

        @Override
        protected void onPreExecute() {
            cmtprg.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            psc.fetchJSON();
            while (psc.parsingInComplete) ;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            cmtprg.setVisibility(View.GONE);
            previousComments.setAdapter(new AdapterComment(LikeCommentActivity.this, psc.getUsrId().toArray(new String[psc.getUsrId().size()]), psc.getUsrDp().toArray(new String[psc.getUsrDp().size()]), psc.getUsrName().toArray(new String[psc.getUsrName().size()]), psc.getUsrCmt().toArray(new String[psc.getUsrCmt().size()]), psc.getUsrAgo().toArray(new String[psc.getUsrAgo().size()])));
            super.onPostExecute(aVoid);
        }
    }

}
