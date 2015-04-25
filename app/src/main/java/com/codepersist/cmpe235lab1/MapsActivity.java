package com.codepersist.cmpe235lab1;

import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private TextView mTextView;
    private List<MySensor> mMySensorList = new ArrayList<MySensor>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        setUpMapIfNeeded();
        mTextView = (TextView) findViewById(R.id.sensordetail);
        mTextView.setText("Select A Map Marker For Details");
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    protected LatLng RandLatLngRange(LatLng center, double change) {
        double maxLat = center.latitude+change;
        double minLat = center.latitude-change;
        double maxLong = center.longitude+change;
        double minLong = center.longitude-change;
        double latrange = Math.abs(maxLat - minLat);
        double longrange = Math.abs(maxLong - minLong);
        return (new LatLng(Math.random() * latrange + minLat, Math.random()*longrange+minLong));
    }

    protected int RandTemp(int center, int range) {
        int min = center-range;
        int max = center+range;
        return (int) Math.ceil(Math.random()*(max-min)+min);
    }

    protected void createSensors(LatLng currentLoc) {

        if (currentLoc.latitude==currentLoc.longitude) {
            double sjlat = 37.3382082;
            double sjlong = -121.8863286;
            currentLoc = new LatLng(sjlat, sjlong);
        }

        LatLng lLoc;
        int lTemp;
        double change = .25;  // Scatter sensors in a .4 x .4 grid from the current point

        for (int i = 0 ; i < 50 ; i++ ) {
           lLoc = RandLatLngRange(currentLoc, change);
           lTemp = RandTemp(75, 5);
           mMySensorList.add(new MySensor(i, "Temperature", String.valueOf(lTemp), "F", lLoc));
           mMap.addMarker(new MarkerOptions().position(lLoc).title("Sensor #" + i));
           Log.d("omg android", "Adding marker at "+lLoc.toString());

        }
    }
    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
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
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
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
        mGoogleApiClient.connect();
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String title = marker.getTitle().toString();
                // All titles start with "Sensor #<index>" - substring 8,length is the index to our array
                int indexSensor = Integer.valueOf(title.substring(8,title.length()));
                mTextView.setText("Sensor #"+indexSensor+" - "+mMySensorList.get(indexSensor).getType()
                        +" "+mMySensorList.get(indexSensor).getValue()+mMySensorList.get(indexSensor).getUnit());
                return false;
            }
        });
    }

    @Override
    public void onConnected(Bundle bundle) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                createSensors(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                Log.d("omg android", "Connected and adding a marker for current location at" + mLastLocation.toString());
                mMap.addMarker(new MarkerOptions().position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())).title("My Location"));
                LatLng myLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                LatLngBounds latlngBounds = new LatLngBounds(myLatLng, myLatLng);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 15));


            }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("omg android", "Connection to GPServices Failed!");
    }
}
