package com.example.theshayds.go4lunch.utils;

import com.example.theshayds.go4lunch.pojo.NearbyPlaces;
import com.example.theshayds.go4lunch.pojo.PlaceDetail;
import com.google.gson.GsonBuilder;
import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiServices {

    // String API_KEY = "AIzaSyBEzFjiM61SPHxlMp601h_2ztVKCg80gi8";
    // Nearby Places FULL_URL example = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=43.605521,%203.910423&radius=1500&type=restaurant&key=AIzaSyBEzFjiM61SPHxlMp601h_2ztVKCg80gi8";
    // Details Places FULL_URL example = "https://maps.googleapis.com/maps/api/place/details/json?placeid=PLACE_ID&key=AIzaSyBEzFjiM61SPHxlMp601h_2ztVKCg80gi8";

    String baseUrl = "https://maps.googleapis.com/";

    // Nearby Places Request
    @GET("maps/api/place/nearbysearch/json?sensor=true&radius=2000&type=restaurant&key=AIzaSyBEzFjiM61SPHxlMp601h_2ztVKCg80gi8")
    Observable<NearbyPlaces> getNearbyPlaces(@Query("location") String location);

    // Details Places Request
    // Test - Place ID: ChIJ_VpFh6evthIRKI4AKAzvs0I
    @GET ("maps/api/place/details/json?key=AIzaSyBEzFjiM61SPHxlMp601h_2ztVKCg80gi8")
    Observable<PlaceDetail> getDetailsPlaces(@Query("placeid") String placeId);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(
                    new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();


}
