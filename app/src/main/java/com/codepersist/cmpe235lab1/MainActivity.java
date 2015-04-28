package com.codepersist.cmpe235lab1;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    GridView mGridView;
    ImageAdapter mImageAdapter;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mImageAdapter = new ImageAdapter(this, getLayoutInflater());
        mGridView = (GridView) findViewById(R.id.gridview);
        mGridView.setAdapter(mImageAdapter);
        mGridView.setOnItemClickListener(this);


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
            default:
                Toast.makeText(getApplicationContext(), "Clicked On " + itemName, Toast.LENGTH_LONG).show();
        }

    }
}
