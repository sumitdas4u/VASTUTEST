package com.utsavmobileapp.utsavapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.utsavmobileapp.utsavapp.DetailsActivity;
import com.utsavmobileapp.utsavapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bibaswann on 08-08-2016.
 */
public class AdapterSearchHistory extends BaseAdapter {
    String[] nameList;
    String[] id;
    List<String> nameListA = new ArrayList<>();
    List<String> idA = new ArrayList<>();
    private Context mContext;


    public AdapterSearchHistory(Context context, List<String> nameIdCombo) {
        mContext = context;
        try {
            for (String oneNameId : nameIdCombo) {
                nameListA.add(oneNameId.split("!")[0]);

                idA.add(oneNameId.split("!")[1]);
            }
            nameList = nameListA.toArray(new String[nameListA.size()]);
            id = idA.toArray(new String[idA.size()]);
        } catch (Exception e) {

        }
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
        rowView = inflater.inflate(R.layout.search_history_adapter, null);
        TextView hint = (TextView) rowView.findViewById(R.id.srchHistory);

        hint.setText(nameList[position]);
        hint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, DetailsActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("id", id[position]);
                i.putExtra("name", nameList[position]);
                mContext.startActivity(i);
            }
        });
        return rowView;
    }
}
