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
 * Created by Bibaswann on 08-06-2016.
 */
public class AdapterComment extends BaseAdapter {

    Context mContext;
    String[] imageUrls;
    String[] commentxts;
    String[] names;
    String[] cmtAgo;
    String[] usrId;
    com.utsavmobileapp.utsavapp.service.Common Common;

    public AdapterComment(Context context, String[] id, String[] image, String[] name, String[] text, String[] ago) {
        mContext = context;
        imageUrls = image;
        names = name;
        commentxts = text;
        cmtAgo = ago;
        usrId = id;
        Common = new Common(mContext);
    }

    @Override
    public int getCount() {
        return commentxts.length;
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
        rowView = inflater.inflate(R.layout.block_comment_adapter, null);

        LinearLayout total = (LinearLayout) rowView.findViewById(R.id.totalLayout);
        total.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent peopleDetails = new Intent(mContext, ProfileActivity.class);
                peopleDetails.putExtra("uid", usrId[position]);
                mContext.startActivity(peopleDetails);
            }
        });
        TextView data = (TextView) rowView.findViewById(R.id.cmtxt);
        TextView name = (TextView) rowView.findViewById(R.id.cmtName);
        TextView ago = (TextView) rowView.findViewById(R.id.cmtAgo);
        ImageView thumg = (ImageView) rowView.findViewById(R.id.cmtDpImg);

        data.setText(commentxts[position]);
        name.setText(names[position]);
        ago.setText(cmtAgo[position]);
        if (imageUrls[position].contains("facebook"))
            Common.ImageDownloaderTask(thumg, mContext, imageUrls[position].replace("http", "https"), "user");
        else
            Common.ImageDownloaderTask(thumg, mContext, imageUrls[position], "user");

        return rowView;
    }


}
