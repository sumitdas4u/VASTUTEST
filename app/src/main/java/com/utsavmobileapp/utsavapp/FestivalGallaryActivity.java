package com.utsavmobileapp.utsavapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.utsavmobileapp.utsavapp.adapter.AdapterGallery;
import com.utsavmobileapp.utsavapp.data.Image;
import com.utsavmobileapp.utsavapp.fragment.FestivalSlideshowDialogFragment;

import java.util.ArrayList;

public class FestivalGallaryActivity extends AppCompatActivity {

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
        //imageIds = new ArrayList<>();
        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("imageBundles");
        images = (ArrayList<Image>) args.getSerializable("images");
        imageIds = intent.getStringArrayListExtra("imageIds");

        mAdapter = new AdapterGallery(getApplicationContext(), images);
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

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                //StorySlideshowFragment newFragment = StorySlideshowFragment.newInstance();
                FestivalSlideshowDialogFragment newFragment = FestivalSlideshowDialogFragment.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(ft, "slideshow");
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }
}
