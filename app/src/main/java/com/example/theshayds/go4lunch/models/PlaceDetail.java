package com.example.theshayds.go4lunch.models;

import android.os.Parcel;
import android.os.Parcelable;

public class PlaceDetail implements Parcelable {

    private String placeID;
    private String name;
    private String location;
    private double latitude;
    private double longitude;
    private boolean openHour;
    private String distance;
    private int people;
    private String url;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    // Empty Constructor
    public PlaceDetail()  {}

    public PlaceDetail(Parcel in) {
        placeID = in.readString();
        name = in.readString();
        location = in.readString();
        openHour = in.readByte() != 0;
        distance = in.readString();
        people = in.readInt();
        url = in.readString();
    }

    public static final Creator<PlaceDetail> CREATOR = new Creator<PlaceDetail>() {
        @Override
        public PlaceDetail createFromParcel(Parcel in) {
            return new PlaceDetail(in);
        }

        @Override
        public PlaceDetail[] newArray(int size) {
            return new PlaceDetail[size];
        }
    };

    public String getPlaceID() {
        return placeID;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }

    public boolean isOpenHour() {
        return openHour;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean getOpenHour() {
        return openHour;
    }

    public void setOpenHour(boolean openHour) {
        this.openHour = openHour;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public int getPeople() {
        return people;
    }

    public void setPeople(int people) {
        this.people = people;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(placeID);
        dest.writeString(name);
        dest.writeString(location);
        dest.writeByte((byte) (openHour ? 1 : 0));
        dest.writeString(distance);
        dest.writeInt(people);
        dest.writeString(url);
    }
}
