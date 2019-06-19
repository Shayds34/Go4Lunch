package com.example.theshayds.go4lunch.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    public void onBindViewHolder(@NonNull CoworkersViewHolder holder, int position, @NonNull Coworker model) {
        Log.d(TAG, "onBindViewHolder is called.");
        holder.updateWithCoworkers(model, activityOrFragment);
    }

    @NonNull
    @Override
    public CoworkersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_coworkers_item, parent, false);

        final CoworkersViewHolder mViewHolder = new CoworkersViewHolder(mView);
        mViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "AdapterPosition " + mViewHolder.getAdapterPosition(), Toast.LENGTH_SHORT).show();

                Intent mIntent = new Intent(mContext, PlaceActivity.class);
                // mIntent.putExtra("placeID", );

            }
        });
        return mViewHolder;
    }

    class CoworkersViewHolder extends RecyclerView.ViewHolder {
        String uid;
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
            coworkerPlace = coworker.getPlaceChoice();

            if (!coworker.getHasChosen()){
                coworkerChoice.setText(coworkerName + " hasn't decided yet.");
                coworkerChoice.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    coworkerChoice.setTextColor(mContext.getColor(R.color.grey_500));
                }

            } else {
                if (fromWhere.equals("PlaceActivity")){
                    coworkerChoice.setText(coworkerName + " is joining!");
                } else if (fromWhere.equals("CoworkersFragment")){
                    coworkerChoice.setText(coworkerName + " is eating at " + coworkerPlace + ".");
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    coworkerChoice.setTextColor(mContext.getColor(R.color.colorBlack));
                    coworkerChoice.setTypeface(Typeface.DEFAULT);
                }
            }
        }
    }
}
