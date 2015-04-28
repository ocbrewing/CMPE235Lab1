package com.codepersist.cmpe235lab1;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jasroger on 4/27/15.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.SensorViewHolder> {

    private List<MySensor> mySensorList;

    public RecyclerViewAdapter(List<MySensor> mySensorList) {
        this.mySensorList = mySensorList;
    }

    @Override
    public SensorViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.card_view_row, viewGroup, false);

        return new SensorViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SensorViewHolder sensorViewHolder, int i) {
        MySensor mySensor = mySensorList.get(i);
        String mySensor_info = "Sensor #"+mySensor.getId();
        String mySensor_measure = mySensor.getType()+" = "+mySensor.getValue()+mySensor.getUnit();
        Log.d("omg android", "Adding sensor data to holder " + mySensor_info + " " + mySensor_measure);
        sensorViewHolder.mSensorInfo.setText(mySensor_info);
        sensorViewHolder.mSensorValue.setText(mySensor_measure);
        sensorViewHolder.mCardView.setBackgroundColor(Color.DKGRAY);
        // This value checks out...
        //Log.d("omg android", "Checking card value "+sensorViewHolder.mSensorInfo.getText());
        //sensorViewHolder.itemView.setActivated(selectedItems.get(position, false));
    }

    @Override
    public int getItemCount() {
        return mySensorList.size();
    }

    public static class SensorViewHolder extends RecyclerView.ViewHolder {

        protected TextView mSensorInfo;
        protected TextView mSensorValue;
        protected CardView mCardView;

        public SensorViewHolder(View itemView) {
            super(itemView);
            mCardView = (CardView) itemView.findViewById(R.id.card_view);
            mSensorInfo = (TextView) itemView.findViewById(R.id.sensor_info);
            mSensorValue = (TextView) itemView.findViewById(R.id.sensor_value);
        }
    }

}
