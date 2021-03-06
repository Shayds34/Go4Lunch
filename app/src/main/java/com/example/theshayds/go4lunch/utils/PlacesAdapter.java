package com.example.theshayds.go4lunch.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.theshayds.go4lunch.R;
import com.example.theshayds.go4lunch.controller.PlaceActivity;
import com.example.theshayds.go4lunch.pojo.MyPlace;
import com.google.firebase.firestore.CollectionReference;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.PlacesViewHolder> {
    private static final String TAG = "PlacesAdapter";

    // Data
    private ArrayList<MyPlace> mPlacesList;
    private Context mContext;

    // Firestore
    private CollectionReference coworkerHelper;

    private String closingHour;

    public PlacesAdapter(Context context, ArrayList<MyPlace> places) {
        mContext = context;
        mPlacesList = places;
    }

    @NonNull
    @Override
    public PlacesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_places_item, parent, false);

        coworkerHelper = CoworkerHelper.getCoworkersCollection();

        final PlacesViewHolder mViewHolder = new PlacesViewHolder(mView);
        mViewHolder.itemView.setOnClickListener(v -> {
            Intent mIntent = new Intent(v.getContext(), PlaceActivity.class);
            mIntent.putExtra("placeId", mPlacesList.get(mViewHolder.getAdapterPosition()).getPlaceId());
            v.getContext().startActivity(mIntent);
        });
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(PlacesViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder is called.");
        holder.updateWithPlaces(this.mPlacesList.get(position));
    }

    @Override
    public int getItemCount() {
        if (mPlacesList == null) {
            return 0;
        }
        return mPlacesList.size();
    }

    class PlacesViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.place_name)          TextView mPlaceName;
        @BindView(R.id.place_address)      TextView mAddress;
        @BindView(R.id.place_open)          TextView mOpenHour;
        @BindView(R.id.place_distance)      TextView mDistance;
        @BindView(R.id.place_people)        LinearLayout mPeopleBox;
        @BindView(R.id.place_people_count)        TextView mPeople;
        @BindView(R.id.place_icon)       ImageView mPlaceIcon;
        @BindView(R.id.place_rating_count) TextView mPlaceRating;

        PlacesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void updateWithPlaces(MyPlace place) {

            // Display count of people who wants to eat in this place.
            coworkerHelper.whereEqualTo("placeName", place.getName()).get().addOnCompleteListener(task -> {
                int count;
                if (task.isSuccessful()){
                    count = Objects.requireNonNull(task.getResult()).size();
                    if (count > 0){
                        mPeopleBox.setVisibility(View.VISIBLE);
                        String people = "(";
                        people = people.concat(String.valueOf(count)).concat(")");
                        mPeople.setText(people);
                    } else {
                        mPeopleBox.setVisibility(View.INVISIBLE);
                    }
                } else {
                    mPeopleBox.setVisibility(View.INVISIBLE);
                    Log.d(TAG, "onComplete: Error getting documents: " + task.getException());
                }
            });

            // Set text to TextViews.
            mPlaceName.setText(place.getName());
            mAddress.setText(place.getFormatted_address());

            String distance = String.valueOf(place.getDistance());
            distance = distance.concat("m");
            mDistance.setText(distance);

            if (place.getRating() != null){
                float rating = Float.valueOf(place.getRating());
                DecimalFormat df = new DecimalFormat("#.##");

                String displayRating = " ";
                displayRating = displayRating.concat(df.format(rating));
                mPlaceRating.setText(displayRating);
            }

            // Setup default options for GLIDE
            RequestOptions mOptions = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.ic_photo_camera_black_24dp)
                    .error(R.drawable.ic_photo_camera_black_24dp)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH)
                    .dontAnimate()
                    .dontTransform();

            Glide.with(mContext)
                    .load(place.getPhotoURL())
                    .apply(mOptions)
                    .into(mPlaceIcon);

            try {
                if (place.getOpeningHours().getOpen_now()){
                    // Get day of the week to fetch the proper period of time.
                    // The place is open/closed.

                    Calendar calendar = Calendar.getInstance();
                    // DAY_OF_WEEK is from 1 to 7. We need to have from 0 to 6 to match array (that's why "-1"
                    int dayOfWeekIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1;

                    // Get the current day and compare to closing hour (morning or evening)
                    for (int i = 0; i < place.getOpeningHours().getPeriods().length; i++) {
                        int day = Integer.parseInt(place.getOpeningHours().getPeriods()[i].getClose().getDay());

                        if (day == dayOfWeekIndex){
                            closingHour = place.getOpeningHours().getPeriods()[i].getClose().getTime();
                            Log.d(TAG, "updateWithPlaces: " + Integer.parseInt(closingHour));
                            if (Integer.parseInt(closingHour) < calendar.get(Calendar.HOUR_OF_DAY)){
                                Log.d(TAG, "updateWithPlaces: closing hour < hour of day");
                            }
                        }

                    }

                    // Format hour style to match Local
                    StringBuilder stringBuilder = new StringBuilder(closingHour);
                    stringBuilder.insert(2, mContext.getResources().getString(R.string.hour_symbol)); // EN ":" / FR "h"
                    String formatterClosingHour = mContext.getResources().getString(R.string.place_open_close_txt);
                    formatterClosingHour = formatterClosingHour.concat(" ").concat(stringBuilder.toString());
                    mOpenHour.setText(formatterClosingHour);

                    // Set style to match Open case.
                    int mColor = mOpenHour.getResources().getColor(R.color.colorBlack);
                    mOpenHour.setTextColor(mColor);
                    mOpenHour.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
                } else {
                    // Set style to match Closed case.
                    int mColor = mOpenHour.getResources().getColor(R.color.colorPrimary);
                    mOpenHour.setText(mContext.getString(R.string.closed));
                    mOpenHour.setTextColor(mColor);
                    mOpenHour.setTypeface(Typeface.DEFAULT_BOLD);
                }
            } catch (Exception e){
                e.printStackTrace();

                // Set style to match Closed case in case we have an error.
                int mColor = mOpenHour.getResources().getColor(R.color.colorPrimary);
                mOpenHour.setText(mContext.getString(R.string.closed));
                mOpenHour.setTextColor(mColor);
                mOpenHour.setTypeface(Typeface.DEFAULT_BOLD);
            }
        }
    }
}
