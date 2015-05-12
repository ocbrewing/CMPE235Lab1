package com.codepersist.cmpe235lab1;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SensorMapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SensorMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SensorMapFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks ,GoogleApiClient.OnConnectionFailedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "SENSOR_LIST";

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private TextView mTextView;
    private SupportMapFragment mMapFragment;
    private boolean mAddedMarkers;
    private ImageView mDrivingDirectionImage;
    private Marker mSelectedMarker;


    private ArrayList<MySensor> mySensorArrayList;


    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param mySensorList Parameter 1.
     *
     * @return A new instance of fragment SensorMapFragment.
     */
    public static SensorMapFragment newInstance(ArrayList<MySensor> mySensorList) {
        SensorMapFragment fragment = new SensorMapFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PARAM1, mySensorList);
        fragment.setArguments(args);
        return fragment;
    }

    public SensorMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mySensorArrayList = getArguments().getParcelableArrayList(ARG_PARAM1);
        }
        mAddedMarkers = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myInflated = inflater.inflate(R.layout.activity_maps, container, false);
        mTextView = (TextView) myInflated.findViewById(R.id.sensor_detail);
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mTextView.setText("Select A Map Marker For Details");
        mDrivingDirectionImage = (ImageView) myInflated.findViewById(R.id.directions_button);
        mDrivingDirectionImage.setOnClickListener(driveButtonListener);
        return myInflated;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentManager fm = getChildFragmentManager();
        mMapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map_container);
        if (mMapFragment == null) {
            mMapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map_container, mMapFragment).commit();
        }
    /***at this time google play services are not initialize so get map and add what ever you want to it in onResume() or onStart() **/
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMap == null) {
            mMap = mMapFragment.getMap();
        }
        Log.d("omg_android", "Getting mMap object");
        if (mMap == null) {
            Log.d("omg android", "mMap is still null");
        }
        setUpMapIfNeeded();

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("omg android", "Map is Connected!!!");
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mAddedMarkers) {
            Log.d("omg android", "Already added Markers, don't reset map but reclick selected");
            if (mSelectedMarker!=null) {
                mSelectedMarker.showInfoWindow();
                int indexSensor = getSensorIndexFromMarker(mSelectedMarker);
                if (indexSensor != -1) {
                    mTextView.setText("Sensor #" + indexSensor + " - " + mySensorArrayList.get(indexSensor).getType()
                            + " " + mySensorArrayList.get(indexSensor).getValue() + mySensorArrayList.get(indexSensor).getUnit());
                }
            }
        } else if (mLastLocation != null) {
            Log.d("omg android", "Connected and adding a marker for current location at" + mLastLocation.toString());
            mMap.addMarker(new MarkerOptions().position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())).title("My Location"));
            LatLng myLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 15));

        } else {
            // Set current location to San Jose area
            double sjlat = 37.3382082;
            double sjlong = -121.8863286;
            //LatLng currentLoc = new LatLng(sjlat, sjlong);
            //mMap.addMarker(new MarkerOptions().position(new LatLng(sjlat, sjlong)).title("My Location"));
            mLastLocation = new Location("san jose, ca");
            mMap.addMarker(new MarkerOptions().position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())).title("My Location"));
            LatLng myLatLng = new LatLng(sjlat, sjlong);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 15));
        }
        if (!mAddedMarkers) {  /* Haven't added markers yet */
            Log.d("omg android", "Adding Sensor Markers");
            mAddedMarkers = true;
            // Even if GPS is off (no current location), add the sensor markers
            for (int i = 0; i < mySensorArrayList.size(); i++) {
                Log.d("omg android", "Adding Sensor " + mySensorArrayList.get(i).getId());
                MySensor tempSensor = mySensorArrayList.get(i);
                if (tempSensor != null) {
                    mMap.addMarker(new MarkerOptions().position(tempSensor.getLocation()).title("Sensor #" + Integer.toString(tempSensor.getId())));
                }
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("omg android", "Connection to GoogleAPI failed!");
    }

    // Respond to click event on Driving Button
    private View.OnClickListener driveButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.directions_button) {
                if (mLastLocation != null) {
                    int index = getSensorIndexFromMarker(mSelectedMarker);
                    // if there's a selected marker that isn't current location - drive to it!
                    if (index != -1) {
                        LatLng driveTo = mySensorArrayList.get(index).getLocation();
                        //String url = "http://maps.google.com/maps?saddr=" + mLastLocation.getLatitude() + "," + mLastLocation.getLongitude() + "&daddr=" + driveTo.latitude + "," + driveTo.longitude + "&mode=driving";
                        Uri gmmIntentUri = Uri.parse("google.navigation:q="+driveTo.latitude+","+driveTo.longitude);
                        Log.d("omg android", "Launching Intent to do directions - url is "+gmmIntentUri);
                        Intent intent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        intent.setPackage("com.google.android.apps.maps");
                        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                            startActivity(intent);
                        } else {
                            Log.d("omg android", "Couldn't Resolve Directions Intent!");
                        }
                    } else {
                        Toast.makeText(getActivity(), "Select a marker to get directions", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Need a current location to get directions", Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link com.google.android.gms.maps.SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap != null) {
            setUpMap();
        } else {
            Log.d("omg android", "Map is not being setup Properly");
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
        // connect to GoogleApiClient for location updates.  Add marker for current location within the onConnected method
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mSelectedMarker=null;
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                mSelectedMarker = marker;
                int indexSensor = getSensorIndexFromMarker(mSelectedMarker);
                if (indexSensor != -1) {
                    mTextView.setText("Sensor #" + mySensorArrayList.get(indexSensor).getId() + " - " + mySensorArrayList.get(indexSensor).getType()
                            + " " + mySensorArrayList.get(indexSensor).getValue() + mySensorArrayList.get(indexSensor).getUnit());
                }
                return false;
            }

        });
    }
    private int getSensorIndexFromMarker(Marker m) {
        if (m==null) { return -1; }
        String title = m.getTitle().toString();
        if (title.equals("My Location")) { return -1; }
        int indexSensor = Integer.valueOf(title.substring(8, title.length()));
        for (int i = 0 ; i < mySensorArrayList.size(); i++) {
            if (mySensorArrayList.get(i).getId() == indexSensor) {
                return i;
            }
        }
        // If no object in the list has the same sensor Id, return -1
        return -1;
    }



}
