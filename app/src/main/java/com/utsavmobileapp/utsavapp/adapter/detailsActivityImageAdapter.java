package com.utsavmobileapp.utsavapp.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.utsavmobileapp.utsavapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sumit on 31-07-2016.
 */
public class detailsActivityImageAdapter extends PagerAdapter {
    public List<String> GalImages = new ArrayList<>();
    Context context;

    public detailsActivityImageAdapter(Context context, List<String> GalImages) {
        this.context = context;
        this.GalImages = GalImages;
    }

    @Override
    public int getCount() {
        return GalImages.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(context);

        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(context).load(GalImages.get(position))
                .thumbnail(0.5f)
                .placeholder(R.drawable.placeholder_festival)
                .centerCrop()
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);

        container.addView(imageView, 0);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ImageView) object);
    }
}
