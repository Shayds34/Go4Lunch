package com.example.theshayds.go4lunch.utils;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.theshayds.go4lunch.R;
import com.example.theshayds.go4lunch.controller.PlaceActivity;
import com.example.theshayds.go4lunch.entities.Prediction;
import com.example.theshayds.go4lunch.entities.Predictions;
import com.example.theshayds.go4lunch.fragments.MyMapFragment;
import com.example.theshayds.go4lunch.pojo.MyPlace;
import com.example.theshayds.go4lunch.pojo.PlaceDetail;
import com.google.android.libraries.places.api.model.Place;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class PlacesAutocompleteAdapter extends ArrayAdapter<Prediction> {
    private static final String TAG = "PlaceAutocompleteAd";

    private Context context;
    private List<Prediction> predictions;

    public PlacesAutocompleteAdapter(@NonNull Context context, List<Prediction> predictions) {
        super(context, R.layout.place_row_layout ,predictions);
        this.context = context;
        this.predictions = predictions;
    }

    @NonNull
    @Override
    public View getView (int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View view = LayoutInflater.from(context).inflate(R.layout.place_row_layout, null);
        if (predictions != null && predictions.size() > 0){
            Prediction prediction = predictions.get(position);
            if (prediction.getTypes().contains("restaurant")){
                TextView textViewName = view.findViewById(R.id.textViewName);
                textViewName.setText(prediction.getDescription());
                textViewName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Start activity
                        Toast.makeText(context, prediction.getPlaceId(), Toast.LENGTH_SHORT).show();
                        MyPlace mPlace = new MyPlace();


                        Disposable disposable = ApiStreams.streamDetailsPlaces(prediction.getPlaceId()).subscribeWith(new DisposableObserver<PlaceDetail>() {
                            @Override
                            public void onNext(PlaceDetail myPlaceDetail) {
                                Log.d(TAG, "onNext: " + myPlaceDetail.getStatus());

                                mPlace.setPlaceId(myPlaceDetail.getResult().getPlace_id());
                                mPlace.setName(myPlaceDetail.getResult().getName());
                                mPlace.setFormatted_address(myPlaceDetail.getResult().getFormatted_address());
                                mPlace.setFormatted_phone_number(myPlaceDetail.getResult().getFormatted_phone_number());
                                mPlace.setPhotoReference(myPlaceDetail.getResult().getPhotos()[0].getPhotoReference());
                                mPlace.setUrl(myPlaceDetail.getResult().getUrl());
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d(TAG, "onError: " + e);
                            }

                            @Override
                            public void onComplete() {
                                Log.d(TAG, "onComplete: ");
                                Intent intent = new Intent(context.getApplicationContext(), PlaceActivity.class);
                                intent.putExtra("myPlace", mPlace);
                                getContext().startActivity(intent);
                            }
                        });
                    }
                });
            }
        }
        return view;
    }

    @NonNull
    @Override
    public Filter getFilter(){
        return new PlacesAutocompleteFilter(this, context);
    }

    private class PlacesAutocompleteFilter extends Filter {
        private PlacesAutocompleteAdapter autocompleteAdapter;
        private Context context;

        PlacesAutocompleteFilter (PlacesAutocompleteAdapter placesAutocompleteAdapter, Context context){
            super();
            this.autocompleteAdapter = placesAutocompleteAdapter;
            this.context = context;
        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence){
            Log.d(TAG, "performFiltering: init");
            try {
                autocompleteAdapter.predictions.clear();
                FilterResults filterResults = new FilterResults();
                if (charSequence == null || charSequence.length() == 0){
                    Log.d(TAG, "performFiltering: if");
                    filterResults.values = new ArrayList<Prediction>();
                    filterResults.count = 0;
                } else {
                    Location location = MyMapFragment.getInstance().getLastLocation();
                    String locationFilter = location.getLatitude() + "," + location.getLongitude();

                    Log.d(TAG, "performFiltering: else");
                    Log.d(TAG, "performFiltering: location " + locationFilter);
                    Predictions predictions = ApiServices.getClient().create(ApiServices.class).getPlacesAutocomplete(charSequence.toString(), "establishment","fr", locationFilter).execute().body();
                    Log.d(TAG, "performFiltering: status " + predictions.getStatus());
                    filterResults.values = predictions.getPredictions();
                    filterResults.count = predictions.getPredictions().size();
                }
                return filterResults;
            } catch (Exception e){
                return null;
            }
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            autocompleteAdapter.predictions.clear();

            for (Prediction prediction : (List<Prediction>) filterResults.values) {
                if (prediction.getTypes().contains("restaurant")){
                    autocompleteAdapter.predictions.add(prediction);
                }
            }
            // autocompleteAdapter.predictions.addAll((List<Prediction>) filterResults.values);
            autocompleteAdapter.notifyDataSetChanged();
        }


        @Override
        public CharSequence convertResultToString(Object resultValue){
            Prediction prediction = (Prediction) resultValue;
            return prediction.getDescription();
        }

    }
}
