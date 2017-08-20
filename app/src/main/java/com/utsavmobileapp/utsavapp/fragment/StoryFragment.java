package com.utsavmobileapp.utsavapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.like.LikeButton;
import com.like.OnLikeListener;
import com.utsavmobileapp.utsavapp.service.AsyncResponseStoryBoard;
import com.utsavmobileapp.utsavapp.DetailsActivity;
import com.utsavmobileapp.utsavapp.fetch.FetchStoryBoard;
import com.utsavmobileapp.utsavapp.ImageOnClickListenerClass;
import com.utsavmobileapp.utsavapp.LikeCommentActivity;
import com.utsavmobileapp.utsavapp.ProfileActivity;
import com.utsavmobileapp.utsavapp.R;
import com.utsavmobileapp.utsavapp.StoryGalleryActivity;
import com.utsavmobileapp.utsavapp.adapter.AdapterGallery;
import com.utsavmobileapp.utsavapp.adapter.AdapterGallerySingleRow;
import com.utsavmobileapp.utsavapp.data.Image;
import com.utsavmobileapp.utsavapp.data.*;
import com.utsavmobileapp.utsavapp.data.StoryObject;
import com.utsavmobileapp.utsavapp.service.Common;
import com.utsavmobileapp.utsavapp.service.EndlessScrollListener;
import com.utsavmobileapp.utsavapp.service.EndlessScrollView;
import com.utsavmobileapp.utsavapp.service.LatLonCachingAPI;
import com.utsavmobileapp.utsavapp.service.LoginCachingAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StoryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * <p>
 * create an instance of this fragment.
 */
public class StoryFragment extends Fragment implements AsyncResponseStoryBoard {

    static Integer beingEdited = 0;
    LinearLayout card, cardholderStoryboard;
    ScrollView parent;
    int storyPage = 0, storyLimit;
    ProgressBar storyProg;
    TextView noStory;
    LatLonCachingAPI llc;
    Context mContext;
    LoginCachingAPI lcp;
    TextView like;
    List<StoryObject> stories;
    String fId = null, uId = null;
    String mode;
    boolean _areLecturesLoaded = false;
    com.utsavmobileapp.utsavapp.service.Common Common;
    private OnFragmentInteractionListener mListener;
    private int noOfColumn = 4;
    private int countIndex = 0;

    @Override
    public void processFinish(List<StoryObject> output) {

        if (output != null) {
//            Log.e("important", output.size() + " object size");
            //  userList.add((User) ois.readObject());
            if (output.size() == 0) {
                storyProg.setVisibility(View.GONE);
                noStory.setVisibility(View.VISIBLE);
//                Toast.makeText(mContext,"No story for this pujo, why don't you create one?", Toast.LENGTH_LONG).show();
            } else {
                for (int i = 0; i < output.size(); i++) {
                    stories.add(output.get(i));
                    try{
                        ShowStories();
                    }
                    catch (Exception ignored){

                    }

                }
            }
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_story, container, false);
        mContext = view.getContext();
        parent = (ScrollView) view.findViewById(R.id.parentScrollView);

        Common = new Common(mContext);
        card = (LinearLayout) view.findViewById(R.id.card);
        cardholderStoryboard = (LinearLayout) view.findViewById(R.id.cardholderStoryboard);
        storyProg = (ProgressBar) view.findViewById(R.id.storyProgress);
        noStory = (TextView) view.findViewById(R.id.no_story);
        llc = new LatLonCachingAPI(mContext);
        lcp = new LoginCachingAPI(mContext);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            if (bundle.getString("fid") != null)
                fId = bundle.getString("fid");
            else if (bundle.getString("uid") != null)
                uId = bundle.getString("uid");
        }
        if (fId != null) {
            StoryFestivalCommonObjectSingleton.getInstance().clear();

            stories = StoryFestivalCommonObjectSingleton.getInstance().getStories();
            storyLimit = 4;
            mode = "festival";
            fetchStrory();
        } else if (uId != null) {
            StoryUserCommonObjectSingleton.getInstance().clear();

            stories = StoryUserCommonObjectSingleton.getInstance().getStories();
            storyLimit = 4;
            mode = "user";
            fetchStrory();
        } else {
            StoryCommonObjectSingleton.getInstance().clear();

            stories = StoryCommonObjectSingleton.getInstance().getStories();
            storyLimit = 4;

        }


        return view;
    }

    private void fetchStrory() {
        FetchStoryBoard fstb = new FetchStoryBoard(mContext, card, llc.readLat(), llc.readLng(), storyPage, storyLimit, storyProg, fId, uId);

        fstb.delegateAsyncStoryBoard = this;
        fstb.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        if (parent.getChildCount() < 4) {
            parent.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    View view;
                    view = parent.getChildAt(parent.getChildCount() - 1);
//                    Log.e("important", "parent.getChildCount()  " + parent.getChildCount());

                    // Calculate the scrolldiff
                    int diff = (view.getBottom() - (parent.getHeight() + parent.getScrollY()));

//                    Log.e("important", "view.getBottom()" + view.getBottom() + "parent.getHeight() " + parent.getHeight() + " parent.getScrollY()" + parent.getScrollY());
//                    Log.e("important", "diff is " + diff);
                    if (diff == 0) {
                        // notify that we have reached the bottom
                        storyPage += 1;
                        pageChangeStoryBoard(storyPage);
                    }
                }


            });
        }
    }


    private void pageChangeStoryBoard(int storyPage) {
        //Log.e("important", "calling page " + storyPage);
        FetchStoryBoard FetchStoryBoardTask1 = new FetchStoryBoard(mContext, card, llc.readLat(), llc.readLng(), storyPage, storyLimit, storyProg, fId, uId);
        FetchStoryBoardTask1.delegateAsyncStoryBoard = this;
        try {

            FetchStoryBoardTask1.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        } catch (RejectedExecutionException ignored) {
            FetchStoryBoardTask1.cancel(true);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !_areLecturesLoaded) {
            fetchStrory();
            _areLecturesLoaded = true;
        }
    }

    @Override
    public void onResume() {
        try {
            if (stories.size() > 0) {
                AsyncTask.execute(new Runnable() {
                                      @Override
                                      public void run() {
                                          try {
                                              getActivity().runOnUiThread(new Runnable() {
                                                  @Override
                                                  public void run() {
                                                      try {
                                                          LikeButton icon_add_bookmark = ((LikeButton) card.getChildAt(beingEdited).findViewById(R.id.icon_add_bookmark));
                                                          if (icon_add_bookmark != null)
                                                              icon_add_bookmark.setLiked(stories.get(beingEdited).getBookmarked());


                                                          TextView numCommentTextView = ((TextView) card.getChildAt(beingEdited).findViewById(R.id.numCommentTextView));
                                                          if (numCommentTextView != null)
                                                              numCommentTextView.setText(String.format("%s Comments", stories.get(beingEdited).getNumComment()));

                                                          LikeButton iconlike = ((LikeButton) card.getChildAt(beingEdited).findViewById(R.id.iconlike));
                                                          if (iconlike != null)
                                                              iconlike.setLiked(stories.get(beingEdited).getLiked());

                                                          TextView numLikeTextView = ((TextView) card.getChildAt(beingEdited).findViewById(R.id.numLikeTextView));

                                                          assert numLikeTextView != null;
                                                          numLikeTextView.setText(String.format("%s Likes", stories.get(beingEdited).getNumLike()));
                                                      } catch (Exception ignored) {


                                                      }


                                                  }
                                              });
                                          } catch (Exception e) {
                                          }
                                      }
                                  }
                );

            }
        } catch (NullPointerException ignored) {
        }

        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    // StoryObject oneStory;
    private void ShowStories() {
//        try {/// TODO: 11-04-2017 scroll crash here
        final int index = countIndex++;


        final LinearLayout linerLayoutStoryBoardCard = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.block_card_layout, card, false);

        if (mode != null) {
            LinearLayout matha;
            if (mode.equals("festival"))
                matha = (LinearLayout) linerLayoutStoryBoardCard.findViewById(R.id.cardViewStoryboard);
            else {
                matha = (LinearLayout) linerLayoutStoryBoardCard.findViewById(R.id.cardViewUser);
            }
            LinearLayout matharTolarDag = (LinearLayout) linerLayoutStoryBoardCard.findViewById(R.id.divider);
            if (stories.get(index).getFob().getName().equals("null")) {
                if (mode.equals("festival"))
                    matharTolarDag.setVisibility(View.GONE);

            }

            if (fId != null || uId != null)
                matha.setVisibility(View.GONE);
            if (fId != null) {
                matharTolarDag.setVisibility(View.GONE);
            } else {
                matha.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(mContext, DetailsActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.putExtra("id", stories.get(index).getFob().getId());
                        i.putExtra("name", stories.get(index).getFob().getName());
                        mContext.startActivity(i);
                    }
                });
            }
        }

        LinearLayout usr = (LinearLayout) linerLayoutStoryBoardCard.findViewById(R.id.cardViewUser);
        usr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent peopleDetails = new Intent(mContext, ProfileActivity.class);
                peopleDetails.putExtra("uid", stories.get(index).getUob().getId());
                mContext.startActivity(peopleDetails);
            }
        });
        LinearLayout fstvl = (LinearLayout) linerLayoutStoryBoardCard.findViewById(R.id.cardViewStoryboard);
        fstvl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, DetailsActivity.class);
                i.putExtra("id", stories.get(index).getFob().getId());
                i.putExtra("name", stories.get(index).getFob().getName());
                mContext.startActivity(i);
            }
        });

        ImageView fimg = (ImageView) linerLayoutStoryBoardCard.findViewById(R.id.festivalphotoImageView);
        Common.ImageDownloaderTask(fimg, mContext, stories.get(index).getFob().getImg(), "festival");

        ImageView uimg = (ImageView) linerLayoutStoryBoardCard.findViewById(R.id.storyBoardUserImageView);
        Common.ImageDownloaderTask(uimg, mContext, stories.get(index).getUob().getPrimg().replace("http", "https"), "user");

        TextView fname = (TextView) linerLayoutStoryBoardCard.findViewById(R.id.festivalNameTextView);
        fname.setText(stories.get(index).getFob().getName());

        TextView faddr = (TextView) linerLayoutStoryBoardCard.findViewById(R.id.festivalAddressTextView);
        faddr.setText(stories.get(index).getFob().getAddress());

        TextView uname = (TextView) linerLayoutStoryBoardCard.findViewById(R.id.userNameTextView);
        uname.setText(stories.get(index).getUob().getName());

        TextView ur = (TextView) linerLayoutStoryBoardCard.findViewById(R.id.userFollowReviewTextView);
        ur.setText(String.format("%s Reveiews, %s Photos", stories.get(index).getUob().getReview(), stories.get(index).getUob().getPhoto()));

        TextView urat = (TextView) linerLayoutStoryBoardCard.findViewById(R.id.ratedTextView);
        if (!stories.get(index).getUserRating().equals("null") && !stories.get(index).getUserRating().equals("0"))
            urat.setText(stories.get(index).getUserRating());
        else {
            TextView ratedLbl = (TextView) linerLayoutStoryBoardCard.findViewById(R.id.ratedLbl);
            ratedLbl.setVisibility(View.GONE);
            urat.setVisibility(View.GONE);
        }

        TextView update = (TextView) linerLayoutStoryBoardCard.findViewById(R.id.agoTextView);
        update.setText(stories.get(index).getLastUpdate());

        TextView ucom = (TextView) linerLayoutStoryBoardCard.findViewById(R.id.usrCommentText);
        if (!stories.get(index).getUserComment().equals("null"))
            ucom.setText(stories.get(index).getUserComment());
        else
            ucom.setVisibility(View.GONE);

        ImageView otherimg1 = (ImageView) linerLayoutStoryBoardCard.findViewById(R.id.primg);

        if (stories.get(index).getOtherImg().size() > 0) {
            Common.ImageDownloaderTask(otherimg1, mContext, stories.get(index).getOtherImgBig().get(0), "festival");
            //Log.e("important","will now show number "+imgTrack);
            otherimg1.setOnClickListener(new ImageOnClickListenerClass(mContext, stories.get(index), 0, "single", index));
            otherimg1.setVisibility(View.VISIBLE);
        } else {
            otherimg1.setVisibility(View.GONE);

        }
        final RecyclerView recyclerView = (RecyclerView) linerLayoutStoryBoardCard.findViewById(R.id.recyclerSmall);

        if (stories.get(index).getOtherImg().size() < 2) {

            recyclerView.setVisibility(View.GONE);
        } else {
            final ArrayList<Image> images = new ArrayList<>();
            final ArrayList<Image> images1 = new ArrayList<>();
            final ArrayList<String> imageIds = new ArrayList<>();
            for (int imgIndx = 0; imgIndx < stories.get(index).getOtherImgBig().size(); imgIndx++) {
                Image imageAux = new Image();
                imageAux.setName("Other Images");
                imageAux.setSmall(stories.get(index).getOtherImgNrml().get(imgIndx));
                imageAux.setMedium(stories.get(index).getOtherImg().get(imgIndx));
                imageAux.setLarge(stories.get(index).getOtherImgBig().get(imgIndx));
                imageAux.setUploader(stories.get(index).getUob().getName());
                imageAux.setPlace(stories.get(index).getFob().getName());
                imageAux.setUploaderDp(stories.get(index).getUob().getPrimg());
                imageAux.setUploaderId(stories.get(index).getUob().getId());
                imageAux.setTotalike(Integer.parseInt(stories.get(index).getOtherImglk().get(imgIndx)));
                imageAux.setTotalcomment(Integer.parseInt(stories.get(index).getOtherImgcmt().get(imgIndx)));
                imageAux.setLiked(stories.get(index).getOtherImgIsLiked().get(imgIndx));
                //   Log.e("important", " comment size "+imgIndx+ "  comments - " +stories.get( storyIndex ).getOtherImg().size() + "storyindex"+storyIndex);
                images.add(imageAux);
                imageIds.add(stories.get(index).getOtherImgId().get(imgIndx));


            }

            for (int imgIndx = 0; imgIndx < stories.get(index).getOtherImgBig().size(); imgIndx++) {
                Image imageAux = new Image();
                imageAux.setName("Other Images");
                imageAux.setSmall(stories.get(index).getOtherImgNrml().get(imgIndx));
                imageAux.setMedium(stories.get(index).getOtherImg().get(imgIndx));
                imageAux.setLarge(stories.get(index).getOtherImgBig().get(imgIndx));
                imageAux.setUploader(stories.get(index).getUob().getName());
                imageAux.setPlace(stories.get(index).getFob().getName());
                imageAux.setUploaderDp(stories.get(index).getUob().getPrimg());
                imageAux.setUploaderId(stories.get(index).getUob().getId());
                imageAux.setTotalike(Integer.parseInt(stories.get(index).getOtherImglk().get(imgIndx)));
                imageAux.setTotalcomment(Integer.parseInt(stories.get(index).getOtherImgcmt().get(imgIndx)));
                imageAux.setLiked(stories.get(index).getOtherImgIsLiked().get(imgIndx));
                //   Log.e("important", " comment size "+imgIndx+ "  comments - " +stories.get( storyIndex ).getOtherImg().size() + "storyindex"+storyIndex);
                images1.add(imageAux);
                imageIds.add(stories.get(index).getOtherImgId().get(imgIndx));


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
                    beingEdited = index;

                    if (noOfColumn - 1 <= position) {
                        Intent i = new Intent(mContext, StoryGalleryActivity.class);
                        i.putExtra("story", stories.get(index));
                        i.putExtra("storyIndex", index);
                        startActivity(i);
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("images", images1);
                        bundle.putSerializable("imageids", imageIds);
                        bundle.putInt("position", position);
                        bundle.putInt("storyId", index);
                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
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


        RelativeLayout likeCmt = (RelativeLayout) linerLayoutStoryBoardCard.findViewById(R.id.likeCommentSection);
        likeCmt.setTag(index);
        likeCmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beingEdited = (Integer) v.getTag();
                // beingEdited = oneStory.getStoryId();
                Intent intentStoryBoardSingleWindow = new Intent(mContext, LikeCommentActivity.class);
                intentStoryBoardSingleWindow.putExtra("concernedStory", index);
                intentStoryBoardSingleWindow.putExtra("mode", mode);
                mContext.startActivity(intentStoryBoardSingleWindow);
            }
        });

        like = (TextView) linerLayoutStoryBoardCard.findViewById(R.id.numLikeTextView);
        //  if (!changedStoryId.equals("-1")) {
        //  if (changedStoryId.equals(oneStory.getStoryId()))
        //like.setText(String.format("%s Likes", numlks));
        // } else
        like.setText(String.format("%s Likes", stories.get(index).getNumLike()));

        TextView com = (TextView) linerLayoutStoryBoardCard.findViewById(R.id.numCommentTextView);
        //  if (!changedStoryId.equals("-1")) {
        //  if (changedStoryId.equals(oneStory.getStoryId()))
        //  com.setText(String.format("%s Comments", numcmt));
        // } else
        com.setText(String.format("%s Comments", stories.get(index).getNumComment()));


        //   Button btn_icon_add_friend = (Button) linerLayoutStoryBoardCard.findViewById(R.id.icon_add_frnd);
        final LikeButton btn_icon_add_bookmark = (LikeButton) linerLayoutStoryBoardCard.findViewById(R.id.icon_add_bookmark);
        btn_icon_add_bookmark.setLiked(stories.get(index).getBookmarked());
        btn_icon_add_bookmark.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                if (Common.isLoggedIn(mContext)) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {


                                // Log.e("important", "found string " +sb.toString());
                                if (Common.HttpURLConnection(mContext.getString(R.string.uniurl) + "/api/festival.php?type=SUBMIT_BOOKMARK&user_id=" + lcp.readSetting("id") + "&festival_id=" + stories.get(index).getFob().getId()).equals("1")) {
                                    btn_icon_add_bookmark.setLiked(true);
                                    stories.get(index).setBookmarked(true);
                                    //    Log.e("important","bookmark ==status=="+StoryFragment.stories.get( index ).getBookmarked());

                                }

                                //   stream.close();
                            } catch (Exception e) {
                                //Log.e("important","exception in reading "+e.getMessage());
                            }
                        }
                    }).start();

                } else {
                    btn_icon_add_bookmark.setLiked(false);
                }

            }


            @Override
            public void unLiked(LikeButton likeButton) {
                if (Common.isLoggedIn(mContext)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                //Log.e("important", "found string " +sb.toString());
                                if (Common.HttpURLConnection(mContext.getString(R.string.uniurl) + "/api/festival.php?type=DELETE_BOOKMARK&user_id=" + lcp.readSetting("id") + "&festival_id=" + stories.get(index).getFob().getId()).equals("1")) {
                                    btn_icon_add_bookmark.setLiked(false);
                                    stories.get(index).setBookmarked(false);
                                    //    Log.e("important","bookmark ==status=="+StoryFragment.stories.get( index ).getBookmarked());

                                }

                                //   stream.close();
                            } catch (Exception e) {
                                //Log.e("important","exception in reading "+e.getMessage());
                            }
                        }
                    }).start();

                } else {
                    btn_icon_add_bookmark.setLiked(true);
                }
            }
        });


        final LikeButton btn_icon_like = (LikeButton) linerLayoutStoryBoardCard.findViewById(R.id.iconlike);
        btn_icon_like.setLiked(stories.get(index).getLiked());
        btn_icon_like.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                if (Common.isLoggedIn(mContext)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                // Log.e("important","exception in reading "+sb.toString() +"///"+ oneStory.getStoryId()+"//"+lcp.readSetting( "id" ));
                                if (Common.HttpURLConnection(mContext.getString(R.string.uniurl) + "/api/love.php?type=SUBMIT&user_id=" + lcp.readSetting("id") + "&storyboard_id=" + stories.get(index).getStoryId()).equals("1")) {
                                    stories.get(index).setNumLike(stories.get(index).getNumLike() + 1);
                                    stories.get(index).setLiked(true);
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Log.e("important", "found string " + stories.get( index ).getNumLike());
                                            //like.setText(String.format("%s Likes", oneStory.getNumLike()));
                                            ((TextView) linerLayoutStoryBoardCard.findViewById(R.id.numLikeTextView)).setText(String.format("%s Likes", stories.get(index).getNumLike()));
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
                    //btn_icon_like.setEnabled(false);
                }
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                if (Common.isLoggedIn(mContext)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {


                                if (Common.HttpURLConnection(mContext.getString(R.string.uniurl) + "/api/love.php?type=DELETE&user_id=" + lcp.readSetting("id") + "&storyboard_id=" + stories.get(index).getStoryId()).equals("1")) {
                                    stories.get(index).setNumLike(stories.get(index).getNumLike() - 1);
                                    stories.get(index).setLiked(false);
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //     Log.e("important", "found string " + stories.get( index ).getNumLike());
                                            ((TextView) linerLayoutStoryBoardCard.findViewById(R.id.numLikeTextView)).setText(String.format("%s Likes", stories.get(index).getNumLike()));
                                        }


                                    });

                                } else {
                                    btn_icon_like.setLiked(true);
                                }
                                // stream.close();
                            } catch (Exception e) {
                                //Log.e("important","exception in reading "+e.getMessage());
                            }
                        }
                    }).start();
                } else {
                    btn_icon_like.setLiked(true);
                    //btn_icon_like.setEnabled(false);
                }
            }
        });


        //  setFont(btn_icon_add_bookmark);
        //   setFont(btn_icon_add_friend);
        //setFont(btn_icon_like);

        card.addView(linerLayoutStoryBoardCard);
//        } catch (IndexOutOfBoundsException ignored) {
//        }
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
