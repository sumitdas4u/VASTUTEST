package com.utsavmobileapp.utsavapp.fetch;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.utsavmobileapp.utsavapp.R;
import com.utsavmobileapp.utsavapp.ViewAllActivity;
import com.utsavmobileapp.utsavapp.parser.ParseZone;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bibaswann on 13-07-2016.
 */
public class FetchZone extends AsyncTask<Void, Void, Void> {

    Context mContext;
    ListView mList;
    ProgressBar progress;

    private List<String> zoneId = new ArrayList<>();
    private List<String> zoneName = new ArrayList<>();

    public FetchZone(Context context, ProgressBar prog, ListView list) {
        mContext = context;
        progress = prog;
        mList = list;
    }

    @Override
    protected Void doInBackground(Void... params) {
        ParseZone prz = new ParseZone(mContext.getString(R.string.uniurl) + "/api/zone.php", mContext);
        prz.fetchJSON();
        while (prz.parsingInComplete && (!this.isCancelled())) ;
        zoneId = prz.getZid();
        zoneName = prz.getZoname();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        progress.setVisibility(View.GONE);
        mList.setAdapter(new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, zoneName));
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Log.e("important","starting intent with zone id "+zoneId.get(position));
                Intent i = new Intent(mContext, ViewAllActivity.class);
                i.putExtra("type", "popular");
                i.putExtra("zoneid", zoneId.get(position));
                mContext.startActivity(i);
            }
        });
        mList.setVisibility(View.VISIBLE);
        super.onPostExecute(aVoid);
    }
}
