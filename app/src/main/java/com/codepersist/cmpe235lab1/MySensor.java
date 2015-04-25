package com.codepersist.cmpe235lab1;

import com.google.android.gms.maps.model.LatLng;

import java.sql.Array;
import java.util.List;

/**
 * Created by jasroger on 4/23/15.
 * Simple Class to define a Sensor with an id, type, value, and unit type for the value.
 */
public class MySensor {
    private int mSensorId;
    private String mType;
    private String mValue;
    private String mUnit;
    private LatLng mLocation;


    public MySensor(int id, String type, String value, String unit, LatLng location) {
        mSensorId = id;
        mType = type;
        mValue = value;
        mUnit = unit;
        mLocation = location;
    }

    public String getType() {
        return mType;
    }
    public String getValue() {
        return mValue;
    }
    public String getUnit() {
        return mUnit;
    }
    public LatLng getLocation() {
        return mLocation;
    }
}
