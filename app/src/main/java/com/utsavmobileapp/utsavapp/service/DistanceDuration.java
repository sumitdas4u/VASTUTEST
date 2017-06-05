package com.utsavmobileapp.utsavapp.service;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Bibaswann on 21-12-2015.
 */
public class DistanceDuration {
    private final GoogleMap mMap;
    TextView tvDistanceDuration;
    private String currentLat;
    private String currentLong;
    private String toiLat;
    private String toiLong;
    //private final WeakReference<Marker> markerWeakReference;
    private Polyline line;

    public DistanceDuration(String curlat, String curlon, String tolatitude, String tolongitude, GoogleMap mMap) {
        currentLat = curlat;
        currentLong = curlon;
        toiLat = tolatitude;
        toiLong = tolongitude;
        this.mMap = mMap;

    }

    public void setDistanceDurationOnTextView() {
        //  tvDistanceDuration = tv;
        String url = getMapsApiDirectionsUrl();
        ReadTask downloadTask = new ReadTask();
        downloadTask.execute(url);
    }

    private String getMapsApiDirectionsUrl() {
        String str_origin = "origin=" + currentLat + "," + currentLong;
        // Destination of route
        String str_dest = "destination=" + toiLat + "," + toiLong;
        String sensor = "sensor=false";
        String params = str_origin + "&" + str_dest + "&" + sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + params;
        return url;
    }

    public Void clear() {
        if (line != null)
            line.remove();

        return null;
    }

    private class ReadTask extends AsyncTask<String, Void, String> {

        @Override

        protected String doInBackground(String... url) {
            String data = "";
            try {
                HttpConnection http = new HttpConnection();
                data = http.readUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                //e.printStackTrace();
            }
            return routes;
        }


        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {

            ArrayList<LatLng> points = null;
            PolylineOptions polyLineOptions = null;
            if (routes != null) {
                // traversing through routes
                for (int i = 0; i < routes.size(); i++) {
                    points = new ArrayList<LatLng>();
                    polyLineOptions = new PolylineOptions();
                    List<HashMap<String, String>> path = routes.get(i);

                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    polyLineOptions.addAll(points);
                    polyLineOptions.width(10);
                    polyLineOptions.color(Color.BLUE);
                }


                line = mMap.addPolyline(polyLineOptions);

            }
        }
    }


}
