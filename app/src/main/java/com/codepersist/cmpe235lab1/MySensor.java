package com.codepersist.cmpe235lab1;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;


/**
 * Created by jasroger on 4/23/15.
 * Simple Class to define a Sensor with an id, type, value, and unit type for the value.
 */
public class MySensor implements Parcelable {
    private int mSensorId;
    private String mType;
    private String mValue;
    private String mUnit;
    private LatLng mLocation;


    public MySensor(int id, String type, String value, String unit, LatLng location) {
        this.mSensorId = id;
        this.mType = type;
        this.mValue = value;
        this.mUnit = unit;
        this.mLocation = location;
    }

    public MySensor(Parcel in) {
        String[] data = new String[6];
        in.readStringArray(data);
        this.mSensorId = Integer.getInteger(data[0]);
        this.mType = data[1];
        this.mValue = data[2];
        this.mUnit = data[3];
        this.mLocation = new LatLng(Double.parseDouble(data[4]), Double.parseDouble(data[5]));
    }

    public int getId() { return mSensorId; }
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[] {Integer.toString(this.mSensorId), this.mType, this.mValue, this.mUnit, Double.toString(this.mLocation.latitude), Double.toString(this.mLocation.longitude)});

    }

    public static final Parcelable.Creator<MySensor> CREATOR = new Parcelable.Creator<MySensor>() {
        public MySensor createFromParcel(Parcel in) {
            return new MySensor(in);
        }

        public MySensor[] newArray(int size) {
            return new MySensor[size];
        }
    };
}
