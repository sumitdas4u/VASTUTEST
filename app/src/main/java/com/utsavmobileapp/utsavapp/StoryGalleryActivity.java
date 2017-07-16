package com.utsavmobileapp.utsavapp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.utsavmobileapp.utsavapp.adapter.AdapterGallery;
import com.utsavmobileapp.utsavapp.data.Image;
import com.utsavmobileapp.utsavapp.data.StoryObject;
import com.utsavmobileapp.utsavapp.fragment.StorySlideshowFragment;

import java.util.ArrayList;

public class StoryGalleryActivity extends AppCompatActivity {

    StoryObject oneStory;
    int storyIndex;
    private ArrayList<Image> images;
    private ArrayList<String> imageIds;
    private ProgressDialog pDialog;
    private AdapterGallery mAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        pDialog = new ProgressDialog(this);
        images = new ArrayList<>();
        imageIds = new ArrayList<>();
        mAdapter = new AdapterGallery(getApplicationContext(), images);
        storyIndex = (int) getIntent().getSerializableExtra("storyIndex");
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
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
        fetchImages();
    }

    //    private void fetchImages() {
//
//        pDialog.setMessage("Downloading images...");
//        pDialog.show();
//
//        JsonArrayRequest req = new JsonArrayRequest(endpoint, new Response.Listener<JSONArray>() {
//                    @Override
//                    public void onResponse(JSONArray response) {
//                        pDialog.hide();
//
//                        images.clear();
//                        for (int i = 0; i < response.length(); i++) {
//                            try {
//                                JSONObject object = response.getJSONObject(i);
//                                Image image = new Image();
//                                image.setName(object.getString("name"));
//
//                                JSONObject url = object.getJSONObject("url");
//                                image.setSmall(url.getString("small"));
//                                image.setMedium(url.getString("medium"));
//                                image.setLarge(url.getString("large"));
//                                image.setTimestamp(object.getString("timestamp"));
//
//                                images.add(image);
//
//                            } catch (JSONException e) {
//                            }
//                        }
//
//                        mAdapter.notifyDataSetChanged();
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                pDialog.hide();
//            }
//        });
//
//        // Adding request to request queue
//        AppController.getInstance().addToRequestQueue(req);
//    }

    private void fetchImages() {

        //  pDialog.setMessage("Downloading images...");
        //  pDialog.show();
        oneStory = (StoryObject) getIntent().getSerializableExtra("story");

        int imgIndx = 0;
        images.clear();
        imageIds.clear();

        for (String imgUrl : oneStory.getOtherImgBig()) {
            Image imageAux = new Image();
            imageAux.setName("Other Images");
            imageAux.setSmall(oneStory.getOtherImg().get(imgIndx));
            imageAux.setMedium(oneStory.getOtherImgNrml().get(imgIndx));
            imageAux.setLarge(oneStory.getOtherImgBig().get(imgIndx));
            imageAux.setUploader(oneStory.getUob().getName());
            imageAux.setPlace(oneStory.getFob().getName());
            imageAux.setUploaderDp(oneStory.getUob().getPrimg());
            imageAux.setUploaderId(oneStory.getUob().getId());
            imageAux.setTotalike(Integer.parseInt(oneStory.getOtherImglk().get(imgIndx)));
            imageAux.setTotalcomment(Integer.parseInt(oneStory.getOtherImgcmt().get(imgIndx)));
            imageAux.setLiked(oneStory.getOtherImgIsLiked().get(imgIndx));
            images.add(imageAux);
            imageIds.add(oneStory.getOtherImgId().get(imgIndx));
            imgIndx++;
        }

        pDialog.hide();
    }
}
