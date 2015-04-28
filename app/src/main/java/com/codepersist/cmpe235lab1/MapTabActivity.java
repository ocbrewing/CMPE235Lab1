package com.codepersist.cmpe235lab1;


import android.content.Intent;
import android.hardware.SensorEventListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;

import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;


public class MapTabActivity extends ActionBarActivity implements BlankFragment.OnFragmentInteractionListener, SensorListFragment.OnFragmentInteractionListener {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "SENSOR_LIST";
    private FragmentTabHost mTabHost;
    private ArrayList<MySensor> mMySensorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMySensorList = new ArrayList<MySensor>();
        // Create a dummy set of sensors when this Activity is opened.  We'll use
        // these for displaying on the map and in the listView
        double sjlat = 37.3382082;
        double sjlong = -121.8863286;
        LatLng currentLoc = new LatLng(sjlat, sjlong);
        createSensors(currentLoc);

        setContentView(R.layout.activity_map_tab);

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);


        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        Bundle sensorListBundle = new Bundle();
        sensorListBundle.putParcelableArrayList(ARG_PARAM1, mMySensorList);

        mTabHost.addTab(
                mTabHost.newTabSpec("Sensor Map").setIndicator("Sensor Map", null),
                BlankFragment.class, sensorListBundle);
        mTabHost.addTab(
                mTabHost.newTabSpec("Sensor List").setIndicator("Sensor List", null),
                SensorListFragment.class, sensorListBundle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map_tab, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

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
            Log.d("omg android", "Adding sensor "+i+" "+Integer.toString(lTemp)+" "+lLoc.toString());
            MySensor tempSense = new MySensor(i, "Temperature", Integer.toString(lTemp), "F", lLoc);
            this.mMySensorList.add(tempSense);
            //mMap.addMarker(new MarkerOptions().position(lLoc).title("Sensor #" + i));
            Log.d("omg android", "Adding marker at " + lLoc.toString());

        }
    }

}
