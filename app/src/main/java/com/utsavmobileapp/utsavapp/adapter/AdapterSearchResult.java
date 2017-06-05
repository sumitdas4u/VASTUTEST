package com.utsavmobileapp.utsavapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.utsavmobileapp.utsavapp.DetailsActivity;
import com.utsavmobileapp.utsavapp.R;
import com.utsavmobileapp.utsavapp.SearchHistoryCaching;
import com.utsavmobileapp.utsavapp.service.Common;

/**
 * Created by Bibaswann on 31-05-2016.
 */
public class AdapterSearchResult extends BaseAdapter {

    String[] nameList;
    String[] distList;
    String[] ratList;
    String[] addrList;
    String[] id;
    String[] imgList;
    SearchHistoryCaching shc;
    com.utsavmobileapp.utsavapp.service.Common Common;
    private Context mContext;

    public AdapterSearchResult(Context context, String[] names, String[] ids, String[] images, String[] dist, String[] rat, String[] addr) {
        mContext = context;
        nameList = names;
        id = ids;
        imgList = images;
        distList = dist;
        ratList = rat;
        addrList = addr;
        shc = new SearchHistoryCaching(mContext);
        Common = new Common(mContext);
    }

    @Override
    public int getCount() {
        return nameList.length;
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

        rowView = inflater.inflate(R.layout.search_result_adapter, null);
        TextView hint = (TextView) rowView.findViewById(R.id.srchSuggestion);
        TextView hintAddr = (TextView) rowView.findViewById(R.id.srchAddress);
        TextView hintRt = (TextView) rowView.findViewById(R.id.srchRating);
        TextView hintDist = (TextView) rowView.findViewById(R.id.srchDistance);
        ImageView thumg = (ImageView) rowView.findViewById(R.id.srchImg);
        try {
            hint.setText(nameList[position]);
            hintAddr.setText(addrList[position]);
            hintRt.setText(ratList[position]);
            hintDist.setText(distList[position]);

            if (!nameList[position].equals("No result found")) {
                hintAddr.setVisibility(View.VISIBLE);
                hintRt.setVisibility(View.VISIBLE);
                hintDist.setVisibility(View.VISIBLE);
                thumg.setVisibility(View.VISIBLE);

                rowView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!shc.readAllCachedPandal().contains(nameList[position]))
                            shc.addUpdateSettingsPandal(nameList[position] + "!" + id[position]);
                        Intent i = new Intent(mContext, DetailsActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.putExtra("id", id[position]);
                        i.putExtra("name", nameList[position]);
                        mContext.startActivity(i);
                    }
                });
            }
            if (!imgList[position].equals("noimg"))
                Common.ImageDownloaderTask(thumg, mContext, imgList[position], "festival");
        } catch (Exception ignored) {
        }
        return rowView;
    }
}
