package com.example.theshayds.go4lunch.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.example.theshayds.go4lunch.R;
import com.example.theshayds.go4lunch.controller.PlaceActivity;
import com.example.theshayds.go4lunch.models.Coworker;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class CoworkerAdapter extends FirestoreRecyclerAdapter<Coworker, CoworkerAdapter.CoworkersViewHolder> {
    private static final String TAG = "CoworkerAdapter";

    public interface Listener {
        void onDataChanged();
    }

    // Communication
    private Listener callback;
    private final RequestManager glide;
    private String activityOrFragment;

    private Context mContext;

    public CoworkerAdapter(Context context, @NonNull FirestoreRecyclerOptions<Coworker> options, RequestManager glide ,Listener callback, String fromWhere) {
        super(options);
        this.callback = callback;
        this.glide = glide;
        this.mContext = context;
        this.activityOrFragment = fromWhere;
    }

    @Override
    public void onDataChanged(){
        super.onDataChanged();
        this.callback.onDataChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull CoworkersViewHolder holder, int position, @NonNull Coworker coworker) {
        Log.d(TAG, "onBindViewHolder is called.");
        holder.updateWithCoworkers(coworker, activityOrFragment);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), PlaceActivity.class);
            intent.putExtra("placeId", coworker.getPlaceID());
            mContext.startActivity(intent);
        });
    }

    @NonNull
    @Override
    public CoworkersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_coworkers_item, parent, false);
        return new CoworkersViewHolder(mView);
    }

    class CoworkersViewHolder extends RecyclerView.ViewHolder {
        ImageView coworkerPicture;
        TextView coworkerChoice;
        String coworkerName;
        String coworkerPlace;

        CoworkersViewHolder(View itemView) {
            super(itemView);
            coworkerPicture = itemView.findViewById(R.id.coworker_picture);
            coworkerChoice = itemView.findViewById(R.id.coworkers_choice);
        }

        void updateWithCoworkers(Coworker coworker, String fromWhere) {

            // Setup default options for GLIDE
            glide.load(coworker.getUrlPicture())
                    .apply(RequestOptions.circleCropTransform())
                    .into(coworkerPicture);

            coworkerName = coworker.getUserName();
            coworkerPlace = coworker.getPlaceName();

            if (!coworker.getHasChosen()){
                coworkerChoice.setText(coworkerName + " hasn't decided yet."); // TODO Extract String Resource
                coworkerChoice.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    coworkerChoice.setTextColor(mContext.getColor(R.color.grey_500));
                }

            } else {
                switch (fromWhere){
                    case "PlaceActivity": {
                        coworkerChoice.setText(coworkerName + " is joining!"); // TODO Extract String Resource
                        break;
                    }
                    case "CoworkersFragment": {
                        coworkerChoice.setText(coworkerName + " is eating at " + coworkerPlace + "."); // TODO Extract String Resource
                        break;
                    }
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    coworkerChoice.setTextColor(mContext.getColor(R.color.colorBlack));
                    coworkerChoice.setTypeface(Typeface.DEFAULT);
                }
            }
        }
    }
}
