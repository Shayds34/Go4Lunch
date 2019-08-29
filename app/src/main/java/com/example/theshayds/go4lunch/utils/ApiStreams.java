package com.example.theshayds.go4lunch.utils;

import com.example.theshayds.go4lunch.pojo.NearbyPlaces;
import com.example.theshayds.go4lunch.pojo.PlaceDetail;
import com.example.theshayds.go4lunch.pojo.Results;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class ApiStreams {

    public static final String baseUrl = "https://maps.googleapis.com/";

    // Get the stream for Nearby Places API
    static Observable<NearbyPlaces> streamNearbyPlaces(String location) {
        ApiServices mServices = ApiServices.retrofit.create(ApiServices.class);
        return mServices.getNearbyPlaces(location)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }


    // Get the stream for Details Places API with Place ID
    private static Observable<PlaceDetail> streamDetailsPlaces(String placeId){
        ApiServices mServices = ApiServices.retrofit.create(ApiServices.class);
        return mServices.getDetailsPlaces(placeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    // Get NearbyPlaces and Details.
    public static Observable<PlaceDetail> streamNearbyPlacesAndGetDetails(String location){
        return streamNearbyPlaces(location)
                .concatMapIterable(new Function<NearbyPlaces, Iterable<Results>>() {
                    @Override
                    public Iterable<Results> apply(NearbyPlaces results){
                        return results.getResults();
                    }
                })
                .concatMap(new Function<Results, Observable<PlaceDetail>>() {
                    @Override
                    public Observable<PlaceDetail> apply(Results results){
                        return streamDetailsPlaces(results.getPlaceId());
                    }
                });
    }
}
