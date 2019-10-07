package com.example.theshayds.go4lunch.utils;

import com.example.theshayds.go4lunch.entities.Predictions;
import com.example.theshayds.go4lunch.pojo.NearbyPlaces;
import com.example.theshayds.go4lunch.pojo.PlaceDetail;
import com.google.gson.GsonBuilder;
import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiServices {
    String baseUrl = "https://maps.googleapis.com/";

    // AIzaSyBEzFjiM61SPHxlMp601h_2ztVKCg80gi8

    // Nearby Places Request
    @GET("maps/api/place/nearbysearch/json?sensor=true&radius=1000&type=restaurant")
    Observable<NearbyPlaces> getNearbyPlaces(
            @Query("location") String location,
            @Query("key") String apiKey);

    // Details Places Request
    @GET ("maps/api/place/details/json?")
    Observable<PlaceDetail> getDetailsPlaces(
            @Query("placeid") String placeId,
            @Query("key") String apiKey);

    @GET ("maps/api/place/autocomplete/json?radius=2000")
    Call<Predictions> getPlacesAutocomplete(
            @Query("input") String input,
            @Query("types") String types,
            @Query("language") String language,
            @Query("location") String locationFilter,
            @Query("key") String apiKey);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(
                    new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();

    static Retrofit getClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }
}
