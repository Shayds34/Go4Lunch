package com.example.theshayds.go4lunch.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Coworker implements Parcelable {

    public String uid;
    public String userName;
    public String urlPicture;
    public boolean hasChosen;
    public String placeID;
    public String placeName;
    public String placePhone;
    public String placeWebsite;
    public String placeUrl;
    public String placePhoto;

    public String getPlaceAddress() {
        return placeAddress;
    }

    public void setPlaceAddress(String placeAddress) {
        this.placeAddress = placeAddress;
    }

    private String placeAddress;


    public Coworker(){
        // Empty constructor needed.
    }

    public String getPlaceID() {
        return placeID;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }

    public String getPlacePhone() {
        return placePhone;
    }

    public void setPlacePhone(String placePhone) {
        this.placePhone = placePhone;
    }

    public String getPlaceWebsite() {
        return placeWebsite;
    }

    public void setPlaceWebsite(String placeWebsite) {
        this.placeWebsite = placeWebsite;
    }

    public String getPlaceUrl() {
        return placeUrl;
    }

    public void setPlaceUrl(String placeUrl) {
        this.placeUrl = placeUrl;
    }

    public String getPlacePhoto() {
        return placePhoto;
    }

    public void setPlacePhoto(String placePhoto) {
        this.placePhoto = placePhoto;
    }

    public Coworker(String uid,
                    String userName,
                    String urlPicture,
                    boolean hasChosen,
                    String placeID,
                    String placeName) {

        this.uid = uid;
        this.userName = userName;
        this.urlPicture = urlPicture;
        this.hasChosen = hasChosen;
        this.placeID = placeID;
        this.placeName = placeName;
    }

    protected Coworker(Parcel in) {
        uid = in.readString();
        userName = in.readString();
        urlPicture = in.readString();
        hasChosen = in.readByte() != 0;
        placeID = in.readString();
        placeName = in.readString();
        placePhone = in.readString();
        placeWebsite = in.readString();
        placeUrl = in.readString();
        placePhoto = in.readString();
        placeAddress = in.readString();
    }

    public static final Creator<Coworker> CREATOR = new Creator<Coworker>() {
        @Override
        public Coworker createFromParcel(Parcel in) {
            return new Coworker(in);
        }

        @Override
        public Coworker[] newArray(int size) {
            return new Coworker[size];
        }
    };

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUrlPicture() {
        return urlPicture;
    }

    public void setUrlPicture(String urlPicture) {
        this.urlPicture = urlPicture;
    }

    public boolean getHasChosen() {
        return hasChosen;
    }

    public void setHasChosen(boolean hasChosen) {
        this.hasChosen = hasChosen;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(userName);
        dest.writeString(urlPicture);
        dest.writeByte((byte) (hasChosen ? 1 : 0));
        dest.writeString(placeID);
        dest.writeString(placeName);
        dest.writeString(placePhone);
        dest.writeString(placeWebsite);
        dest.writeString(placeUrl);
        dest.writeString(placePhoto);
        dest.writeString(placeAddress);
    }
}
