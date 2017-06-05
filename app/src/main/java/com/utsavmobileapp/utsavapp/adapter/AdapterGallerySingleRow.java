package com.utsavmobileapp.utsavapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.utsavmobileapp.utsavapp.R;
import com.utsavmobileapp.utsavapp.data.Image;

import java.util.List;

import static android.support.v7.widget.RecyclerView.OnItemTouchListener;
import static android.support.v7.widget.RecyclerView.ViewHolder;

/**
 * Created by Bibaswann on 21-05-2016.
 */
public class AdapterGallerySingleRow extends RecyclerView.Adapter<AdapterGallerySingleRow.MyViewHolder> {
    int count;
    Integer noOfColumn;
    int countview;
    private int totalSize;
    private List<Image> images;
    private Context mContext;

    public AdapterGallerySingleRow(Context context, List<Image> images, Integer noOfColumn) {
        mContext = context;
        this.totalSize = images.size();
        this.images = images;
 /*       for (int imgIndx=0;imgIndx<=noOfColumn && imgIndx<=  this.totalSize && images.get( imgIndx ) !=null ;imgIndx++) {

            this.images.add(imgIndx, images.get( imgIndx ));
        }*/

        this.noOfColumn = noOfColumn - 1;
        count = 0;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_album_thumbnail, parent, false);

        MyViewHolder item = new MyViewHolder(itemView);

       /* if (noOfColumn < count) {
           // item.setVisibility( false );
         Log.e("important", "found string " +count);
        }*/

        count++;

        return item;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {


        Image image = images.get(position);

        Glide.with(mContext).load(image.getMedium())
                .thumbnail(0.5f)
                .placeholder(R.color.white_text)
                .fitCenter()
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.thumbnail);

        if (noOfColumn <= position) {
            holder.thumbnail.setAlpha(.5f);
            holder.plusImgText.setVisibility(View.VISIBLE);
            holder.plusImgText.setText(String.format("+%d", totalSize - noOfColumn));
        } else {
            holder.plusImgText.setVisibility(View.GONE);
        }

    }

    public void removeLastItems(int count, int size) {
        for (int i = 0; i < count; i++) images.remove((size - 1) - i);
        notifyDataSetChanged();
    }

    public void removeFristItem() {
        images.remove(0);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements OnItemTouchListener {

        private GestureDetector gestureDetector;
        private AdapterGallerySingleRow.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final AdapterGallerySingleRow.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public class MyViewHolder extends ViewHolder {
        public ImageView thumbnail;
        public TextView plusImgText;

        public MyViewHolder(View view) {
            super(view);

            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            plusImgText = (TextView) view.findViewById(R.id.plusImgText);


        }

    }
}