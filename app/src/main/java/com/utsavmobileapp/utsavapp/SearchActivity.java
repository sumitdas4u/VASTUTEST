package com.utsavmobileapp.utsavapp;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.utsavmobileapp.utsavapp.adapter.AdapterSearchHistory;
import com.utsavmobileapp.utsavapp.adapter.AdapterSearchResult;
import com.utsavmobileapp.utsavapp.parser.ParsePlaceJSON;
import com.utsavmobileapp.utsavapp.parser.ParseSearchResult;
import com.utsavmobileapp.utsavapp.service.LatLonCachingAPI;
import com.utsavmobileapp.utsavapp.service.PlaceProvider;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//import android.support.v7.widget.SearchView;

public class SearchActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    PlacesTask placesTask;
    ParserTask parserTask;
    ListView searchHints, searchHistory;
    ProgressBar prog;
    SearchHistoryCaching shc;
    String pandalOrPlace;
    LinearLayout bb;
    LatLonCachingAPI llc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        searchHints = (ListView) findViewById(R.id.searchSuggestionHolder);
        searchHistory = (ListView) findViewById(R.id.searchHistoryHolder);
        prog = (ProgressBar) findViewById(R.id.srchProg);
        bb = (LinearLayout) findViewById(R.id.blackbar);
        shc = new SearchHistoryCaching(this);
        llc = new LatLonCachingAPI(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.main, menu);
        //SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        SearchView searchView = (SearchView) findViewById(R.id.txtSearchView);
        //searchView.setIconified(false);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.getString("type").equals("google")) {
                searchView.setQueryHint("Search by location");
                pandalOrPlace = "place";
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        if (!newText.equals("")) {
                            bb.setVisibility(View.GONE);
                            searchHistory.setVisibility(View.GONE);
                            prog.setVisibility(View.VISIBLE);
                            new PlacesTask().execute(newText);
                        } else {
                            bb.setVisibility(View.VISIBLE);
                            searchHistory.setVisibility(View.VISIBLE);
                            prog.setVisibility(View.GONE);
                        }
                        return false;
                    }
                });

                searchHistory.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, shc.readAllCachedPlace()) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        TextView textView = (TextView) super.getView(position, convertView, parent);
                        //textView.setTextColor(Color.BLACK);
                        return textView;
                    }
                });
            } else {
                searchView.setQueryHint("Search by pandal name");
                pandalOrPlace = "pandal";
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        if (!newText.equals("")) {
                            bb.setVisibility(View.GONE);
                            searchHistory.setVisibility(View.GONE);
                            prog.setVisibility(View.VISIBLE);
                            new StaticSearchTask().execute(newText);
                        } else {
                            bb.setVisibility(View.VISIBLE);
                            searchHistory.setVisibility(View.VISIBLE);
                            prog.setVisibility(View.GONE);
                        }
                        return false;
                    }
                });

//                searchHistory.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, shc.readAllCachedPandal()) {
//                    @Override
//                    public View getView(int position, View convertView, ViewGroup parent) {
//                        TextView textView = (TextView) super.getView(position, convertView, parent);
//                        //textView.setTextColor(Color.BLACK);
//                        return textView;
//                    }
//                });
                if (shc.readAllCachedPandal().size() > 0)
                    try {
                        searchHistory.setAdapter(new AdapterSearchHistory(this, shc.readAllCachedPandal()));
                    } catch (Exception e) {

                    }

            }
        }
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//        handleIntent(getIntent());

        return super.onCreateOptionsMenu(menu);
    }

    private void handleIntent(Intent intent) {
        //Log.e("important","handling intent");
        if (intent.getAction() != null) {
            if (intent.getAction().equals(Intent.ACTION_SEARCH)) {
                doSearch(intent.getStringExtra(SearchManager.QUERY));
            } else if (intent.getAction().equals(Intent.ACTION_VIEW)) {
                getPlace(intent.getStringExtra(SearchManager.EXTRA_DATA_KEY));
            }
        }
    }

    private void doSearch(String query) {
        //Log.e("important","search query is "+query);
        Bundle data = new Bundle();
        data.putString("query", query);
        getSupportLoaderManager().restartLoader(0, data, this);
    }

    private void getPlace(String query) {
        //Log.e("important","view query is "+query);
        Bundle data = new Bundle();
        data.putString("query", query);
        getSupportLoaderManager().restartLoader(1, data, this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cLoader = null;
        if (id == 0)
            cLoader = new CursorLoader(getBaseContext(), PlaceProvider.SEARCH_URI, null, null, new String[]{args.getString("query")}, null);
        else if (id == 1)
            cLoader = new CursorLoader(getBaseContext(), PlaceProvider.DETAILS_URI, null, null, new String[]{args.getString("query")}, null);
        return cLoader;
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        LatLng latlon = showLocations(data);
        if (latlon != null) {
//            Toast.makeText(getApplicationContext(), "Yes such location found at " + latlon.latitude + ", " + latlon.longitude, Toast.LENGTH_LONG).show();
            Intent i = new Intent(this, SearchResultActivity.class);
            i.putExtra("currentlat", String.valueOf(latlon.latitude));
            i.putExtra("currentlon", String.valueOf(latlon.longitude));
            startActivity(i);
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    private LatLng showLocations(Cursor c) {
        LatLng position = null;
        if (c != null) {
            while (c.moveToNext()) {
                position = new LatLng(Double.parseDouble(c.getString(1)), Double.parseDouble(c.getString(2)));
            }
        } else {
            Toast.makeText(getApplicationContext(), "No such location found", Toast.LENGTH_LONG).show();
        }
        return position;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception ignored) {
        } finally {
            if (iStream != null)
                iStream.close();
            if (urlConnection != null)
                urlConnection.disconnect();
        }
        return data;
    }

    // Fetches all places from GooglePlaces AutoComplete Web Service
    private class PlacesTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... place) {
            // For storing data from web service
            String data = "";

            // Obtain browser key from https://code.google.com/apis/console
            String key = "key=AIzaSyBh1OKV-obMOKCVdO5nQqiXB5yqU5wnB4s";
            //String key = "key=AIzaSyAANMViTb6A5bIQwkKQnG6h-tU58i-z_Cg";

            String input = "";

            try {
                input = "input=" + URLEncoder.encode(place[0], "utf-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }

            String types = "types=geocode";
            String sensor = "sensor=false";

            // Building the parameters to the web service
            String parameters = input + "&" + types + "&" + sensor + "&" + key;

            // Output format
            String output = "json";

            // Building the url to the web service
            String url = "https://maps.googleapis.com/maps/api/place/autocomplete/" + output + "?" + parameters;

            try {
                // Fetching the data from we service
                data = downloadUrl(url);
            } catch (Exception ignored) {
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // Creating ParserTask
            parserTask = new ParserTask();

            // Starting Parsing the JSON string returned by Web Service
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        JSONObject jObject;

        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;

            ParsePlaceJSON parsePlaceJSON = new ParsePlaceJSON();

            try {
                jObject = new JSONObject(jsonData[0]);

                // Getting the parsed data as a List construct
                places = parsePlaceJSON.parse(jObject);

            } catch (Exception ignored) {
            }
            return places;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {

            String[] from = new String[]{"description"};
            final int[] to = new int[]{android.R.id.text1};
            // Creating a SimpleAdapter for the AutoCompleteTextView
            SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), result, android.R.layout.simple_list_item_1, from, to) {
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    final TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                    text1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!shc.readAllCachedPlace().contains(text1.getText().toString()))
                                shc.addUpdateSettingsPlace(text1.getText().toString());
                            //Todo
                            Toast.makeText(getBaseContext(), "Congrats, you clicked me, I will remember it", Toast.LENGTH_LONG).show();
                        }
                    });
                    //text1.setTextColor(Color.BLACK);
                    return view;
                }
            };


            // Setting the adapter
            searchHints.setAdapter(adapter);
            prog.setVisibility(View.GONE);
        }
    }

    private class StaticSearchTask extends AsyncTask<String, Void, Void> {
        List<String> frating = new ArrayList<>();
        List<String> fdist = new ArrayList<>();
        List<String> faddress = new ArrayList<>();
        List<String> fnameList = new ArrayList<>();
        List<String> fimgList = new ArrayList<>();
        List<String> fidList = new ArrayList<>();
        ParseSearchResult psr;

        @Override
        protected Void doInBackground(String... params) {
            fnameList.clear();
            fimgList.clear();
            psr = new ParseSearchResult(getString(R.string.uniurl) + "/api/festival.php?name=" + params[0] + "&lat=" + llc.readLat() + "&long=" + llc.readLng(), getApplicationContext());
            psr.fetchJSON();
            while (psr.parsingInComplete) ;
            fnameList = psr.getfName();
            faddress = psr.getfAddr();
            frating = psr.getfRat();
            fdist = psr.getfDist();
            fimgList = psr.getfSimg();
            fidList = psr.getfId();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            searchHints.setAdapter(new AdapterSearchResult(getBaseContext(), fnameList.toArray(new String[fnameList.size()]), fidList.toArray(new String[fidList.size()]), fimgList.toArray(new String[fimgList.size()]), fdist.toArray(new String[fdist.size()]), frating.toArray(new String[frating.size()]), faddress.toArray(new String[faddress.size()])));
            prog.setVisibility(View.GONE);
        }
    }
}
