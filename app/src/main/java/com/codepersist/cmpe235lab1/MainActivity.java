package com.codepersist.cmpe235lab1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends ActionBarActivity implements AdapterView.OnItemClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    GridView mGridView;
    ImageAdapter mImageAdapter;
    Context mContext ;
    GoogleApiClient mGoogleApiClient;
    public static final String PREFS_NAME="myPrefsFile";
    private static final String SENSORS = "SensorList";
    private List<MySensor> mMySensorList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mImageAdapter = new ImageAdapter(this, getLayoutInflater());
        mGridView = (GridView) findViewById(R.id.gridview);
        mGridView.setAdapter(mImageAdapter);
        mGridView.setOnItemClickListener(this);
        SharedPreferences settings = getApplicationContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        /*SharedPreferences.Editor mEdit = settings.edit();
        mEdit.clear();
        mEdit.commit();*/
        /* If it doesn't exist, create it, else just know they are there for the other Activities to read */
        if (!settings.contains(SENSORS)) {
            Log.d("omg android", "No Shared Preferences Exist.. should be creating the sensors here!");
            double sjlat = 37.3382082;
            double sjlong = -121.8863286;
            LatLng currentLoc = new LatLng(sjlat, sjlong);
            createSensors(currentLoc);
            SharedPreferences.Editor mPrefEdit = settings.edit();
            Gson gson = new Gson();
            String json = gson.toJson(mMySensorList);
            Log.d("omg android", "Storing sensor list "+json);
            mPrefEdit.putString(SENSORS, json);
            mPrefEdit.commit();
        } else {
            //MySensor obj = gson.fromJson(json, MySensor.class);
            Gson gson = new Gson();
            String json = settings.getString(SENSORS, null);
            MySensor[] SensorItems = gson.fromJson(json, MySensor[].class);
            mMySensorList = Arrays.asList(SensorItems);
            mMySensorList = new ArrayList<>(mMySensorList);
            Log.d("omg android:", "Retrieved the Sensor info from the Shared Preferences");

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        } else if (id == R.id.action_contact) {
            Intent contactIntent = new Intent(this, ContactActivity.class);
            startActivity(contactIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        int clickItem = (int) mImageAdapter.getItem(position);
        String itemName = getString(clickItem);


        switch (itemName) {
            // Start Login Intent
            case "Login":
                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivity(loginIntent);
                break;
            case "Sensor Map":
                Intent mapIntent = new Intent(this, MapTabActivity.class);
                startActivity(mapIntent);
                break;
            case "Sensor Data":
                Intent sensorDataIntent = new Intent(this, SensorDataActivity.class);
                startActivity(sensorDataIntent);
                break;
            case "Monitor":
                Intent monitorIntent = new Intent(this, MonitorActivity.class);
                startActivity(monitorIntent);
                break;
            case "Control":
                Intent controlIntent = new Intent(this, ControlActivity.class);
                startActivity(controlIntent);
                break;
            case "My Sensors":
                Intent mySensorsIntent = new Intent(this, MySensorsActivity.class);
                startActivity(mySensorsIntent);
                break;
            case "QR Scan":
                scanNow(null);
                break;
            case "Tutorial":
                Intent tutorialIntent = new Intent(this, TutorialActivity.class);
                startActivity(tutorialIntent);
                break;
            default:
                Toast.makeText(getApplicationContext(), "Clicked On " + itemName, Toast.LENGTH_LONG).show();
        }

    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if (scanningResult != null) {
//we have a result
            String scanContent = scanningResult.getContents();
            //String scanFormat = scanningResult.getFormatName();
            /* See if the scan result is a URL, if so, then offer to navigate to it */
            try {
                URL url = new URL(scanContent);
                HandleURL(url);
            }
            catch (MalformedURLException e) { // If not a URL, try to add this as a sensor
                Log.d("omg android", "Add this as a sensor?");
                HandleSensor(scanContent);
            }

        }else{
            Toast toast = Toast.makeText(getApplicationContext(), "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void scanNow(View view) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scan a barcode");
        integrator.setResultDisplayDuration(0);
        integrator.setWide();  // Wide scanning rectangle, may work better for 1D barcodes
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.initiateScan();
    }

    /* Given a valid URL, ask user if they want to navigate to it.
    If yes, launch intent
    If no, cancel and do nothing */
    public void HandleURL(URL url) {
        Log.d("omg android", "Scan result is URL " + url.toString());
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getString(R.string.dialog_msg_url) + " " + url.toString() + "?");
        alertDialogBuilder.setPositiveButton(R.string.positive_button, new MyDialogClickListener(url));
        alertDialogBuilder.setNegativeButton(R.string.negative_button,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    public void HandleSensor(String sensorInfo) {
        Log.d("omg android", "Scan result is " + sensorInfo.toString());
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getString(R.string.dialog_msg_sensor) + " " + sensorInfo.toString() + "?");
        alertDialogBuilder.setPositiveButton(R.string.positive_button, new MyNewSensorDialogClickListener(sensorInfo));
        alertDialogBuilder.setNegativeButton(R.string.negative_button,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }


    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

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

        for (int i = 0 ; i < 20 ; i++ ) {
            lLoc = RandLatLngRange(currentLoc, change);
            lTemp = RandTemp(75, 5);
            Log.d("omg android", "Adding sensor "+i+" "+Integer.toString(lTemp)+" "+lLoc.toString());
            MySensor tempSense = new MySensor(i, "Temperature", Integer.toString(lTemp), "F", lLoc);
            mMySensorList.add(tempSense);
            //mMap.addMarker(new MarkerOptions().position(lLoc).title("Sensor #" + i));
            Log.d("omg android", "Adding marker at " + lLoc.toString());

        }
    }

    public class MyDialogClickListener implements DialogInterface.OnClickListener
    {

        URL url;
        public MyDialogClickListener(URL url) {
            this.url = url;
        }

        @Override
        public void onClick(DialogInterface dialogInterface, int i)
        {
            Intent webView = new Intent(Intent.ACTION_VIEW);
            webView.setData(Uri.parse(this.url.toString()));
            startActivity(webView);   //read your lovely variable
        }
    };

    /* Listener Created for the Dialog Box to add a new Sensor
       Needed a custom listener to pass the Json data from the QR Code
     */
    public class MyNewSensorDialogClickListener implements DialogInterface.OnClickListener
    {

        String json;
        public MyNewSensorDialogClickListener(String json) {
            this.json = json;
        }

        @Override
        public void onClick(DialogInterface dialogInterface, int arg1)
        {
            //Extract sensor information and add to the sensorlist.
            Gson gson = new Gson();
            MySensor scannedSensor = gson.fromJson(json, MySensor.class);

            // Since we just created this sensor, let's set the location - in this case Random
            // Could also do the current GPS location, but then if I scan a bunch they'll all be
            // the same location.
            double sjlat = 37.3382082;
            double sjlong = -121.8863286;
            LatLng centerLoc = new LatLng(sjlat, sjlong);
            LatLng sSensLoc = RandLatLngRange(centerLoc, 0.01);
            scannedSensor.setLocation(sSensLoc);
            // Same for value - create a random temp
            int sSensTemp = RandTemp(75, 5);
            scannedSensor.setValue(String.valueOf(sSensTemp));
            // Testing to make sure object was created - It worked!
            //Log.d("omg android", "New Sensor ID = "+scannedSensor.getId());
            // Now add the sensor to our Sensor ArrayList after checking for duplicate IDs
            boolean duplicate = false;
            for (int i = 0 ; i < mMySensorList.size(); i++) {
                if (mMySensorList.get(i).getId() == scannedSensor.getId()) {
                    duplicate = true;
                }
            }
            if (duplicate) {
                Toast.makeText(getApplicationContext(), "Sensor "+String.valueOf(scannedSensor.getId())+" already exists. Not Adding.", Toast.LENGTH_LONG).show();
            } else {
                mMySensorList.add(scannedSensor);
                Log.d("omg android", "Size of mMySensorList = " + String.valueOf(mMySensorList.size()));

                // Now jsonify the ArrayList and write to sharedPrefs for later use in other Activities
                SharedPreferences settings = getApplicationContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                SharedPreferences.Editor mPrefEdit = settings.edit();

                String json = gson.toJson(mMySensorList);
                Log.d("omg android", "Storing sensor list " + json);
                mPrefEdit.putString(SENSORS, json);
                mPrefEdit.commit();
                Toast.makeText(getApplicationContext(), "Added Sensor " + String.valueOf(scannedSensor.getId()) + " to your Sensor Network!", Toast.LENGTH_LONG).show();
            }
        }
    };


}
