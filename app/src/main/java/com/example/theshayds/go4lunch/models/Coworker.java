package com.example.theshayds.go4lunch.models;

public class Coworker {

    private String uid;
    private String userName;
    private String urlPicture;
    private boolean hasChosen;
    private String placeChoice;
    private String placeID;
    private String placePhone;
    private String placeWebsite;


    public Coworker(){
        // Empty constructor needed.
    }

    public Coworker(String uid, String userName, String urlPicture, boolean hasChosen, String placeChoice) {
        this.uid = uid;
        this.userName = userName;
        this.urlPicture = urlPicture;
        this.hasChosen = hasChosen;
        this.placeChoice = placeChoice;
    }


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

    public String getPlaceChoice() {
        return placeChoice;
    }

    public void setPlaceChoice(String placeChoice) {
        this.placeChoice = placeChoice;
    }
}
