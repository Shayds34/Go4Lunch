package com.example.theshayds.go4lunch.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.theshayds.go4lunch.pojo.MyPlace;
import com.example.theshayds.go4lunch.R;
import com.example.theshayds.go4lunch.controller.PlaceActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.SphericalUtil;


import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.PlacesViewHolder> {
    private static final String TAG = "PlacesAdapter";

    // Data
    private ArrayList<MyPlace> mPlacesList;
    private Context mContext;

    // Firestore
    private CollectionReference coworkerHelper;


    public PlacesAdapter(Context context, ArrayList<MyPlace> places) {
        mContext = context;
        mPlacesList = places;
    }

    @Override
    public PlacesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_places_item, parent, false);

        coworkerHelper = CoworkerHelper.getCoworkersCollection();

        final PlacesViewHolder mViewHolder = new PlacesViewHolder(mView);
        mViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Position " + mViewHolder.getAdapterPosition());

                Intent mIntent = new Intent(v.getContext(), PlaceActivity.class);
                mIntent.putExtra("placePosition", String.valueOf(mViewHolder.getAdapterPosition()));

                Log.d(TAG, "onClick: placeAddress " + mPlacesList.get(mViewHolder.getAdapterPosition()).getFormatted_address() );
                v.getContext().startActivity(mIntent);
            }
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

    public class PlacesViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.place_name)          TextView mPlaceName;
        @BindView(R.id.place_address)      TextView mAddress;
        @BindView(R.id.place_open)          TextView mOpenHour;
        @BindView(R.id.place_distance)      TextView mDistance;
        @BindView(R.id.place_people)        LinearLayout mPeopleBox;
        @BindView(R.id.place_people_icon)   ImageView mPeopleIcon;
        @BindView(R.id.place_people_count)        TextView mPeople;
        @BindView(R.id.place_icon)       ImageView mPlaceIcon;

        PlacesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void updateWithPlaces(MyPlace place) {

            // Display count of people who wants to eat in this place.
            coworkerHelper.whereEqualTo("placeChoice", place.getName()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    int count;

                    if (task.isSuccessful()){
                        count = task.getResult().size();

                        if (count > 0){
                            mPeopleBox.setVisibility(View.VISIBLE);
                            mPeople.setText("(" + count + ")");
                        }

                    } else {
                        Log.d(TAG, "onComplete: Error getting documents: " + task.getException());
                    }
                }
            });

            // Display distance between the user and the place.
            // TODO Get real Lat Lng
            LatLng userLatLng = new LatLng(0, 0);
            LatLng placeLatLng = new LatLng(place.getLat(), place.getLng());

            double distanceBetween = SphericalUtil.computeDistanceBetween(userLatLng, placeLatLng);
            int distanceInMeters = (int) Math.round(distanceBetween);
            Log.d(TAG, "updateWithPlaces: " + distanceInMeters);

            // Set
            mPlaceName.setText(place.getName());
            // mAddress.setText(place.getFormatted_address());
            mAddress.setText(distanceInMeters + "m"); // TEST
            mDistance.setText(distanceBetween + "m");

            // Setup default options for GLIDE
            RequestOptions mOptions = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.ic_restaurant_marker_green) // TODO change
                    .error(R.drawable.ic_restaurant_marker_green) // TODO change
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH)
                    .dontAnimate()
                    .dontTransform();

            Glide.with(mContext)
                    .load(place.getPhotoURL())
                    .apply(mOptions)
                    .into(mPlaceIcon);


            if(!place.getOpenNow()){
                int mColor = mOpenHour.getResources().getColor(R.color.colorGoogle);
                mOpenHour.setText("Closed");
                mOpenHour.setTextColor(mColor);
                mOpenHour.setTypeface(Typeface.DEFAULT_BOLD);
            } else {
                mOpenHour.setText("Open");
            }
            mDistance.setText(place.getScope());
        }
    }



}
