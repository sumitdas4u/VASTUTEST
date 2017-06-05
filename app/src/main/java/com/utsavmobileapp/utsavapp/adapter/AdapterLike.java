package com.utsavmobileapp.utsavapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.utsavmobileapp.utsavapp.ProfileActivity;
import com.utsavmobileapp.utsavapp.R;
import com.utsavmobileapp.utsavapp.service.Common;

/**
 * Created by Bibaswann on 09-06-2016.
 */
public class AdapterLike extends BaseAdapter {

    Context mContext;
    String[] imageUrls;
    String[] names;
    String[] cmtAgo;
    String[] usrId;
    com.utsavmobileapp.utsavapp.service.Common Common;

    public AdapterLike(Context context, String[] id, String[] image, String[] name, String[] ago) {
        mContext = context;
        imageUrls = image;
        names = name;
        cmtAgo = ago;
        usrId = id;
        Common = new Common(mContext);
    }

    @Override
    public int getCount() {
        return names.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rowView = inflater.inflate(R.layout.like_adapter, null);

        LinearLayout usrLayout = (LinearLayout) rowView.findViewById(R.id.usrLayout);
        usrLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent peopleDetails = new Intent(mContext, ProfileActivity.class);
                peopleDetails.putExtra("uid", usrId[position]);
                mContext.startActivity(peopleDetails);
            }
        });
        TextView name = (TextView) rowView.findViewById(R.id.lkName);
        TextView ago = (TextView) rowView.findViewById(R.id.lkAgo);
        ImageView thumg = (ImageView) rowView.findViewById(R.id.lkDpImg);

        name.setText(names[position]);
        ago.setText(cmtAgo[position]);
        if (imageUrls[position].contains("facebook"))
            Common.ImageDownloaderTask(thumg, mContext, imageUrls[position].replace("http", "https"), "user");
        else
            Common.ImageDownloaderTask(thumg, mContext, imageUrls[position], "user");

        return rowView;
    }

}
