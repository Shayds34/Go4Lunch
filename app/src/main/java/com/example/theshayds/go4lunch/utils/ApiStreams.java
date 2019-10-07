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

    // Get the stream for Nearby Places API
    private static Observable<NearbyPlaces> streamNearbyPlaces(String location, String apiKey) {
        ApiServices mServices = ApiServices.retrofit.create(ApiServices.class);
        return mServices.getNearbyPlaces(location, apiKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    // Get the stream for Details Places API with Place ID
    public static Observable<PlaceDetail> streamDetailsPlaces(String placeId, String apiKey){
        ApiServices mServices = ApiServices.retrofit.create(ApiServices.class);
        return mServices.getDetailsPlaces(placeId, apiKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    // Get NearbyPlaces and Details.
    public static Observable<PlaceDetail> streamNearbyPlacesAndGetDetails(String location, String apiKey){
        return streamNearbyPlaces(location, apiKey)
                .concatMapIterable((Function<NearbyPlaces, Iterable<Results>>) NearbyPlaces::getResults)
                .concatMap((Function<Results, Observable<PlaceDetail>>) results -> streamDetailsPlaces(results.getPlaceId(), apiKey));
    }
}
