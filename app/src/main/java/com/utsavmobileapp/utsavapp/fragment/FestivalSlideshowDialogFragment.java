package com.utsavmobileapp.utsavapp.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.utsavmobileapp.utsavapp.R;
import com.utsavmobileapp.utsavapp.adapter.AdapterComment;
import com.utsavmobileapp.utsavapp.adapter.AdapterLike;
import com.utsavmobileapp.utsavapp.data.Image;
import com.utsavmobileapp.utsavapp.data.StoryCommonObjectSingleton;
import com.utsavmobileapp.utsavapp.data.StoryObject;
import com.utsavmobileapp.utsavapp.parser.ParseStoryComments;
import com.utsavmobileapp.utsavapp.parser.ParseStoryLikes;
import com.utsavmobileapp.utsavapp.service.Common;
import com.utsavmobileapp.utsavapp.service.LoginCachingAPI;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Bibaswann on 22-07-2016.
 */
public class FestivalSlideshowDialogFragment extends DialogFragment {
    static ArrayList<Image> images;
    static ArrayList<String> imageIds;
    Context mContext;
    Context mContext1;
    ImageView uDp;
    TextView numComment;
    ListView previousLks, previousCmts;
    TextView lblCmt, lblLike;
    LikeButton lkBtn;
    Integer totalNumCmtInt;
    LoginCachingAPI lcp;
    boolean isLoggedIn;
    List<StoryObject> stories;
    ProgressBar lkprg, cmtprg;
    com.utsavmobileapp.utsavapp.service.Common Common;
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private TextView lblCount, uAndP;
    private int selectedPosition = 0;
    //  page change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            selectedPosition = position;
            displayMetaInfo(position, getView());
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    public static FestivalSlideshowDialogFragment newInstance() {
        return new FestivalSlideshowDialogFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_image_slider, container, false);
        mContext = v.getContext();
        Common = new Common(mContext);
        viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        lblCount = (TextView) v.findViewById(R.id.lbl_count);
        uAndP = (TextView) v.findViewById(R.id.uploaderAndPlace);
        uDp = (ImageView) v.findViewById(R.id.imgDp);
        mContext1 = uDp.getContext();
        lcp = new LoginCachingAPI(mContext);
        isLoggedIn = lcp.readSetting("login").equals("true");
        stories = StoryCommonObjectSingleton.getInstance().getStories();
        lblCmt = (TextView) v.findViewById(R.id.numCommentTextView);
//        lblCmt.setTextColor(ContextCompat.getColor(mContext, R.color.white_text));
        lblCmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog cmtDlg = new Dialog(v.getContext());
                cmtDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
                cmtDlg.setContentView(R.layout.comment_activity_like_popup);
                numComment = (TextView) cmtDlg.findViewById(R.id.photoNumComment);
                previousCmts = (ListView) cmtDlg.findViewById(R.id.photoCmtList);
                cmtprg = (ProgressBar) cmtDlg.findViewById(R.id.commentProg);

                ImageView commenterDp = (ImageView) cmtDlg.findViewById(R.id.cmtDp);
                Common.ImageDownloaderTask(commenterDp, mContext, lcp.readSetting("photo"), "user");
                final EditText makeCmt = (EditText) cmtDlg.findViewById(R.id.makeCmt);
                Button postCmt = (Button) cmtDlg.findViewById(R.id.postCmt);

                postCmt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!makeCmt.getText().toString().trim().equals("") && Common.isLoggedIn(mContext)) {
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


                                        if (Common.HttpURLConnection(mContext.getString(R.string.uniurl) + "/api/comments.php?type=SUBMIT&user_id=" + lcp.readSetting("id") + "&photo_id=" + imageIds.get(selectedPosition) + "&comment_text=" + cmtTxt).equals("1")) {

                                            ++totalNumCmtInt;
                                            images.get(selectedPosition).setTotalcomment(totalNumCmtInt);


                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    new LoadComments().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                                    // images.get(position).setTotalike(image.getTotalike());
                                                    lblCmt.setText(String.format("%s Comments", totalNumCmtInt));

                                                }
                                            });
                                        }
                                        //   stream.close();
                                    } catch (Exception e) {
                                        //Log.e("important", "exception in reading " + e.getMessage());
                                    }
                                }
                            }).start();
                        }
                    }
                });
                cmtDlg.show();
                new LoadComments().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });
        lblLike = (TextView) v.findViewById(R.id.numLikeTextView);
//        lblLike.setTextColor(ContextCompat.getColor(mContext, R.color.white_text));
        lblLike.setOnClickListener(new View.OnClickListener() {
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

        //  lkBtn.setTextColor(ContextCompat.getColor(mContext, R.color.white_text));
        // lkBtn.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fontawesome-webfont.ttf"));

        images = (ArrayList<Image>) getArguments().getSerializable("images");
        imageIds = (ArrayList<String>) getArguments().getSerializable("imageids");
        selectedPosition = getArguments().getInt("position");
        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        setCurrentItem(selectedPosition, v);

        return v;
    }

    private void setCurrentItem(int position, View v) {
        viewPager.setCurrentItem(position, false);
        displayMetaInfo(selectedPosition, v);
    }

    private void displayMetaInfo(final int position, View v) {
        if (v != null) {
            lblCount.setText(String.format(new Locale("en_IN", "IN"), "%d of %d", position + 1, images.size()));

            final Image image = images.get(position);
            //uAndP.setText(String.format("%s at %s", image.getUploader(), image.getPlace()));
            //common.ImageDownloaderTask(uDp, mContext, image.getUploaderDp().replace("http", "https"), "user");
            uAndP.setVisibility(View.GONE);
            uDp.setVisibility(View.GONE);

            final LikeButton lkBtn = (LikeButton) v.findViewById(R.id.iconlike);
            lblLike.setText(String.format("%s  Likes", images.get(position).getTotalike()));
            lblCmt.setText(String.format("%s  Comments", images.get(position).getTotalcomment()));
            //if (image.getLiked())
            lkBtn.setLiked(images.get(position).getLiked());
            //  Log.e( "important", "you are watching " + imageIds.get( position ) + "ISLIKE" + image.getLiked() );
            /// btn_icon_like = (LikeButton) findViewById(R.id.iconlike);
            //  btn_icon_like.setTypeface(Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf"));

            lkBtn.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    if (Common.isLoggedIn(mContext)) {

                        //  Toast.makeText(LikeCommentActivity.this, "The comments are shown below", Toast.LENGTH_SHORT).show();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {


                                    //Log.e("important", "found string " +sb.toString());
                                    if (Common.HttpURLConnection(mContext.getString(R.string.uniurl) + "/api/love.php?type=SUBMIT&user_id=" + lcp.readSetting("id") + "&photo_id=" + imageIds.get(selectedPosition)).equals("1")) {


                                        image.setTotalike(image.getTotalike() + 1);
                                        images.get(position).setTotalike(image.getTotalike());
                                        images.get(position).setLiked(true);
                                        // oneStory.setNumLike(oneStory.getNumLike() + 1);
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                //  if (btn_icon_like.getText().equals(getString(R.string.fa_heart_o))) {
                                                //    btn_icon_like.setText(R.string.fa_heart);
                                                lblLike.setText(String.format("%s Likes", images.get(position).getTotalike()));
                                                // } else {
                                                //    btn_icon_like.setText(R.string.fa_heart_o);
                                                //   like.setText(String.format("%s Likes", Integer.parseInt(oneStory.getNumLike()) - 1));
                                                // }
                                            }
                                        });

                                    } else {
                                        lkBtn.setLiked(false);
                                    }

                                } catch (Exception e) {
                                    //Log.e("important","exception in reading "+e.getMessage());
                                }
                            }
                        }).start();


                    } else {
                        lkBtn.setLiked(false);

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
                                    if (Common.HttpURLConnection(mContext.getString(R.string.uniurl) + "/api/love.php?type=DELETE&user_id=" + lcp.readSetting("id") + "&photo_id=" + imageIds.get(selectedPosition)).equals("1")) {

                                        image.setTotalike(image.getTotalike() - 1);
                                        images.get(position).setTotalike(image.getTotalike());
                                        images.get(position).setLiked(false);
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //  if (btn_icon_like.getText().equals(getString(R.string.fa_heart_o))) {
                                                //    btn_icon_like.setText(R.string.fa_heart);
                                                lblLike.setText(String.format("%s Likes", images.get(position).getTotalike()));
                                                // } else {
                                                //    btn_icon_like.setText(R.string.fa_heart_o);
                                                //   like.setText(String.format("%s Likes", Integer.parseInt(oneStory.getNumLike()) - 1));
                                                // }
                                            }
                                        });

                                    } else {
                                        lkBtn.setLiked(true);
                                    }
                                    //     stream.close();
                                } catch (Exception e) {
                                    //Log.e("important","exception in reading "+e.getMessage());
                                }
                            }
                        }).start();


                    } else {
                        lkBtn.setLiked(true);

                    }
                }
            });


        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Todo: Change the theme
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullscreenTheme);

    }

    @Override
    public void onDismiss(final DialogInterface dialog) {


        super.onDismiss(dialog);
        final Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }

    //  adapter
    public class MyViewPagerAdapter extends PagerAdapter {

        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.image_fullscreen_preview, container, false);

            ImageView imageViewPreview = (ImageView) view.findViewById(R.id.image_preview);
            final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress);
            progressBar.setVisibility(View.VISIBLE);
            Image image = images.get(position);

            Glide.with(getActivity()).load(image.getLarge())
                    .thumbnail(0.5f)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageViewPreview);

            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    class LoadLike extends AsyncTask<Void, Void, Void> {
        ParseStoryLikes psl;

        LoadLike() {
            psl = new ParseStoryLikes(mContext.getString(R.string.uniurl) + "/api/love.php?type=PHOTO&photo_id=" + imageIds.get(selectedPosition), mContext);
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
            previousLks.setAdapter(new AdapterLike(mContext, psl.getUsrId().toArray(new String[psl.getUsrId().size()]), psl.getUsrDp().toArray(new String[psl.getUsrDp().size()]), psl.getUsrName().toArray(new String[psl.getUsrName().size()]), psl.getUsrAgo().toArray(new String[psl.getUsrAgo().size()])));


            super.onPostExecute(aVoid);


        }
    }

    class LoadComments extends AsyncTask<Void, Void, Void> {
        ParseStoryComments psc;

        LoadComments() {
            //Log.e("important","selected position is "+selectedPosition);
            psc = new ParseStoryComments(mContext.getString(R.string.uniurl) + "/api/comments.php?type=PHOTO&photo_id=" + imageIds.get(selectedPosition), mContext);
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
            totalNumCmtInt = psc.getCount();
            //Log.e("important", "total comments " + totalNumCmtInt);
            // images.get(position).setTotalike(image.getTotalike());

            numComment.setText(String.format("%s Comments", psc.getCount()));

     /*       AsyncTask.execute( new Runnable() {
                                   @Override
                                   public void run() {
                                           stories.get( storyId ).getOtherImgcmt().set( selectedPosition, String.valueOf( images.get( selectedPosition ).getTotalcomment() ) );
                                   }
                               }
            );*/
            previousCmts.setAdapter(new AdapterComment(mContext, psc.getUsrId().toArray(new String[psc.getUsrId().size()]), psc.getUsrDp().toArray(new String[psc.getUsrDp().size()]), psc.getUsrName().toArray(new String[psc.getUsrName().size()]), psc.getUsrCmt().toArray(new String[psc.getUsrCmt().size()]), psc.getUsrAgo().toArray(new String[psc.getUsrAgo().size()])));
            super.onPostExecute(aVoid);
        }
    }
}
