package com.utsavmobileapp.utsavapp.fragment;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.utsavmobileapp.utsavapp.service.BackgroundLocationService;
import com.utsavmobileapp.utsavapp.fetch.FetchNearFestival;
import com.utsavmobileapp.utsavapp.fetch.FetchNearPeople;
import com.utsavmobileapp.utsavapp.fetch.FetchSponsored;
import com.utsavmobileapp.utsavapp.fetch.FetchZomato;
import com.utsavmobileapp.utsavapp.R;
import com.utsavmobileapp.utsavapp.ViewAllActivity;
import com.utsavmobileapp.utsavapp.service.LatLonCachingAPI;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NearFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NearFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NearFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    LinearLayout sponsored, festivals, people, locationBased, restaurantContainerLayout;
    Context mContext;
    LatLonCachingAPI llc;
    Button vabn, vabu;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private ToggleButton toggleButtonLocationService;

    public NearFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NearFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NearFragment newInstance(String param1, String param2) {
        NearFragment fragment = new NearFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        try {

            ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);

            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                //Log.e("important", "service string " + service.service.getClassName());

                if (serviceClass.getName().equals(service.service.getClassName())) {

                    return true;
                }
            }
            return false;
        } catch (NullPointerException e) {
            return false;

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_near, container, false);

        mContext = view.getContext();

        sponsored = (LinearLayout) view.findViewById(R.id.linearLayoutSponsored);
        festivals = (LinearLayout) view.findViewById(R.id.linearLayout2);
        people = (LinearLayout) view.findViewById(R.id.linearLayout1);
        locationBased = (LinearLayout) view.findViewById(R.id.locationbased);
        restaurantContainerLayout = (LinearLayout) view.findViewById(R.id.nearRestaurantContainer);
        toggleButtonLocationService = (ToggleButton) view.findViewById(R.id.toggleButtonLocationService);
        ProgressBar nearFestivalProgress = (ProgressBar) view.findViewById(R.id.nearFestivalProgress);
//        Button btnLost = (Button) view.findViewById(R.id.btnLost);
//        btnLost.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(mContext, lost_found_activity.class);
//                startActivity(i);
//            }
//        });
//        RelativeLayout btnDial100 = (RelativeLayout) view.findViewById(R.id.btnDial100);
//        btnDial100.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_DIAL);
//                intent.setData(Uri.parse("tel:100"));
//                mContext.startActivity(intent);
//            }
//        });
//
//        Button btnpdf = (Button) view.findViewById(R.id.btnGuideMap);
//        btnpdf.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(mContext, pdf.class);
//                startActivity(i);
//            }
//        });

        vabu = (Button) view.findViewById(R.id.viewallBtnUsers);
        vabu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "coming soon !", Toast.LENGTH_LONG).show();
            }
        });


        if (isMyServiceRunning(BackgroundLocationService.class)) {

            toggleButtonLocationService.setChecked(true);
        } else {
            toggleButtonLocationService.setChecked(false);
        }
        this.toggleButtonLocationService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton toggleButton, boolean isChecked) {
                if (isChecked) {
                    Intent intent = new Intent(mContext, BackgroundLocationService.class);
                    getActivity().startService(intent);
                    //Log.e("important", "service stoped ");
                } else {
                    Intent intent = new Intent(mContext, BackgroundLocationService.class);
                    getActivity().stopService(intent);
                    //Log.e("important", "service started ");
                }

            }
        });
        Button allNearFestival = (Button) view.findViewById(R.id.viewallBtnNear);
        allNearFestival.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, ViewAllActivity.class);
                i.putExtra("type", "near");
                i.putExtra("lat", llc.readLat());
                i.putExtra("lon", llc.readLng());
                startActivity(i);
            }
        });

        llc = new LatLonCachingAPI(mContext);
        try {
            if (!llc.readLat().equals("na") && !llc.readLng().equals("na")) {
                new FetchNearFestival(mContext, festivals, nearFestivalProgress, "0", "10", llc.readLat(), llc.readLng(), false, null, null, true, false).execute();
                new FetchNearPeople(mContext, people, llc.readLat(), llc.readLng()).execute();
            }
            new FetchSponsored(mContext, sponsored, llc.readLat(), llc.readLng()).execute();
            new FetchZomato(getContext(), restaurantContainerLayout, llc.readLat(), llc.readLng()).execute();

        } catch (Exception ignored) {
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
