package com.example.theshayds.go4lunch.utils;

import android.content.Context;
import android.util.Log;

import com.example.theshayds.go4lunch.pojo.MyPlace;
import com.example.theshayds.go4lunch.pojo.NearbyPlaces;
import com.example.theshayds.go4lunch.pojo.PlaceDetail;
import com.example.theshayds.go4lunch.pojo.Results;

import java.util.ArrayList;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class ApiRequests {
    public static final String TAG = "ApiRequests";

    private static volatile ApiRequests instance;

    private Disposable disposable;
    private ArrayList<MyPlace> mMyPlaceArrayList;
    private ArrayList<MyPlace> mMyPlaceDetailList;

//    public boolean isRequestComplete() {
//        return isRequestComplete;
//    }

    public boolean isRequestComplete;

    private ApiRequests (Context mContext){
        if (instance != null){
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static ApiRequests getInstance(Context mContext){
        if (instance == null){
            instance = new ApiRequests(mContext);
        }
        return instance;
    }

    public ArrayList<MyPlace> getNearbyPlaces(String location){
        mMyPlaceArrayList = new ArrayList<>();
        disposable = ApiStreams.streamNearbyPlaces(location).subscribeWith(new DisposableObserver<NearbyPlaces>() {
            @Override
            public void onNext(NearbyPlaces example) {
                Log.d(TAG, "onNext: getNearbyPlaces" + example.getStatus());
                for (int i = 0; i < example.getResults().size(); i++) {

                    MyPlace mPlace = new MyPlace();
                    Results mResults = example.getResults().get(i);

                    mPlace.setPlaceId(mResults.getPlaceId());
                    mPlace.setName(mResults.getName());
                    mPlace.setLat(mResults.getGeometry().getLocation().getLat());
                    mPlace.setLng(mResults.getGeometry().getLocation().getLng());
                    mPlace.setVicinity(mResults.getVicinity());
                    try {
                        mPlace.setOpenNow(mResults.getOpeningHours().getOpenNow());
                    } catch (Exception e){
                        mPlace.setOpenNow(false);
                    }

                    mMyPlaceArrayList.add(mPlace);
                }
            }
            @Override
            public void onError(Throwable e) { e.printStackTrace(); }
            @Override
            public void onComplete() { Log.d(TAG, "onComplete: "); }
        });
        return mMyPlaceArrayList;
    }

    public ArrayList<MyPlace> getNearbyPlacesAndGetDetails(double lat, double lng){
        mMyPlaceDetailList = new ArrayList<>();
        String currentStringLocation = lat + " , " + lng;
        disposable = ApiStreams.streamNearbyPlacesAndGetDetails(currentStringLocation).subscribeWith(new DisposableObserver<PlaceDetail>() {
            @Override
            public void onNext(PlaceDetail response) {
                Log.d(TAG, "onNext: getNearbyPlacesAndGetDetails status: " + response.getResult().getName());

                MyPlace mPlace = new MyPlace();

                if (response.getResult() != null) {
                    // Working
                    mPlace.setPlaceId(response.getResult().getPlace_id());
                    mPlace.setFormatted_address(response.getResult().getFormatted_address());
                    mPlace.setName(response.getResult().getName());
                    mPlace.setLat(response.getResult().getGeometry().getLocation().getLat());
                    mPlace.setLng(response.getResult().getGeometry().getLocation().getLng());
                    mPlace.setFormatted_phone_number(response.getResult().getFormatted_phone_number());
                    mPlace.setWebsite(response.getResult().getWebsite());

                    mPlace.setPhotos(response.getResult().getPhotos());
                    if (response.getResult().getPhotos() != null) {
                        mPlace.setPhotoReference(response.getResult().getPhotos()[0].getPhotoReference());
                        mPlace.setPhotoURL("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="
                                + response.getResult().getPhotos()[0].getPhotoReference()
                                + "&key=AIzaSyBEzFjiM61SPHxlMp601h_2ztVKCg80gi8");

                        Log.d(TAG, "onNext: " + response.getResult().getPhotos()[0].getPhotoReference());
                    }

                    mMyPlaceDetailList.add(mPlace);
                }
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
                // TODO boolean

                Log.d(TAG, "onComplete: " + mMyPlaceDetailList.size());
                updateUI(lat, lng, mMyPlaceDetailList);
                isRequestComplete = true;
            }
        });
        Log.d(TAG, "getNearbyPlacesAndGetDetails: ");
        return mMyPlaceDetailList;
    }

    private void updateUI(double lat, double lng, ArrayList<MyPlace> list) {

        // TODO update Map UI
        Log.d(TAG, "updateUI: " + lat + "," + lng + " list size: " + list.size());

    }

    public ArrayList<MyPlace> getMyPlaceDetailList() {

        return mMyPlaceDetailList;
    }
}
