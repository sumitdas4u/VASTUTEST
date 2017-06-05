package com.utsavmobileapp.utsavapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.utsavmobileapp.utsavapp.data.Image;
import com.utsavmobileapp.utsavapp.data.StoryObject;
import com.utsavmobileapp.utsavapp.fragment.StorySlideshowFragment;

import java.util.ArrayList;

/**
 * Created by Bibaswann on 29-06-2016.
 */
public class ImageOnClickListenerClass implements View.OnClickListener {
    StoryObject oneStory;
    int position;
    int storyIndex;
    String openMode;
    Context mContext;
    private ArrayList<Image> images;
    private ArrayList<String> imageIds;

    public ImageOnClickListenerClass(Context context, StoryObject storyObject, int number, String mode, Integer index) {
        oneStory = storyObject;
        position = number;
        images = new ArrayList<>();
        imageIds = new ArrayList<>();
        openMode = mode;
        storyIndex = index;
        mContext = context;
    }


    public void onClick(View v) {
        if (openMode.equals("gallery")) {
            Intent i = new Intent(mContext, StoryGalleryActivity.class);
            i.putExtra("story", oneStory);
            i.putExtra("storyIndex", storyIndex);

            mContext.startActivity(i);
        } else {
            fetchImages();

            Bundle bundle = new Bundle();
            bundle.putSerializable("images", images);
            bundle.putSerializable("imageids", imageIds);
            bundle.putInt("position", position);
            bundle.putInt("storyId", storyIndex);
            FragmentTransaction ft = ((AppCompatActivity) mContext).getSupportFragmentManager().beginTransaction();
            StorySlideshowFragment newFragment = StorySlideshowFragment.newInstance();
            newFragment.setArguments(bundle);
            newFragment.show(ft, "slideshow");
        }
    }

    public void fetchImages() {

        images.clear();
        imageIds.clear();
        int imgIndx = 0;

        for (String ignored : oneStory.getOtherImgBig()) {

            Image imageAux = new Image();
            imageAux.setName("Other Images");
            imageAux.setSmall(oneStory.getOtherImgNrml().get(imgIndx));
            imageAux.setMedium(oneStory.getOtherImg().get(imgIndx));
            imageAux.setLarge(oneStory.getOtherImgBig().get(imgIndx));
            imageAux.setUploader(oneStory.getUob().getName());
            imageAux.setPlace(oneStory.getFob().getName());
            imageAux.setUploaderDp(oneStory.getUob().getPrimg());
            imageAux.setTotalike(Integer.parseInt(oneStory.getOtherImglk().get(imgIndx)));
            imageAux.setTotalcomment(Integer.parseInt(oneStory.getOtherImgcmt().get(imgIndx)));
            imageAux.setLiked(oneStory.getOtherImgIsLiked().get(imgIndx));
            //   Log.e("important", " comment size "+imgIndx+ "  comments - " +Integer.parseInt(oneStory.getOtherImgcmt().get(imgIndx))+ "storyindex"+storyIndex);
            images.add(imageAux);
            imageIds.add(oneStory.getOtherImgId().get(imgIndx));
            imgIndx++;
        }
    }
}
