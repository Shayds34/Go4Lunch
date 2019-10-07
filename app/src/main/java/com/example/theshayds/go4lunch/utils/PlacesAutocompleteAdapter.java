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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.theshayds.go4lunch.R;
import com.example.theshayds.go4lunch.controller.PlaceActivity;
import com.example.theshayds.go4lunch.entities.Prediction;
import com.example.theshayds.go4lunch.entities.Predictions;
import com.example.theshayds.go4lunch.fragments.MyMapFragment;

import java.util.ArrayList;
import java.util.List;

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
        View row = LayoutInflater.from(context).inflate(R.layout.place_row_layout, null);

        if (predictions != null && predictions.size() > 0){
            Prediction prediction = predictions.get(position);
            if (prediction.getTypes().contains("restaurant")){

                TextView textViewName = row.findViewById(R.id.address);
                textViewName.setText(prediction.getTerms().get(0).getValue());

                textViewName.setOnClickListener(v -> {
                    Intent intent = new Intent(context.getApplicationContext(), PlaceActivity.class);
                    intent.putExtra("placeId", prediction.getPlaceId());
                    getContext().startActivity(intent);

                });
            }
        }
        return row;
    }

    @NonNull
    @Override
    public Filter getFilter(){
        return new PlacesAutocompleteFilter(this, context);
    }

    private class PlacesAutocompleteFilter extends Filter {
        private PlacesAutocompleteAdapter autocompleteAdapter;
        private Context mContext;

        PlacesAutocompleteFilter (PlacesAutocompleteAdapter placesAutocompleteAdapter, Context context){
            super();
            this.autocompleteAdapter = placesAutocompleteAdapter;
            this.mContext = context;
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
                    Predictions predictions = ApiServices.getClient().create(ApiServices.class).getPlacesAutocomplete(
                            charSequence.toString(),
                            "establishment",
                            "fr",
                            locationFilter,
                            mContext.getResources().getString(R.string.google_api_key))
                            .execute()
                            .body();
                    if (predictions != null) {
                        Log.d(TAG, "performFiltering: status " + predictions.getStatus());
                        filterResults.values = predictions.getPredictions();
                        filterResults.count = predictions.getPredictions().size();
                    }
                }
                return filterResults;
            } catch (Exception e){
                return null;
            }
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            autocompleteAdapter.predictions.clear();

            for (Prediction prediction :  (List<Prediction>) filterResults.values) {
                if (prediction.getTypes().contains("restaurant")){
                    autocompleteAdapter.predictions.add(prediction);
                }
            }
            autocompleteAdapter.notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue){
            Prediction prediction = (Prediction) resultValue;
            return prediction.getDescription();
        }

    }
}
