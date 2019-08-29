package com.example.theshayds.go4lunch.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.example.theshayds.go4lunch.R;
import com.example.theshayds.go4lunch.models.Coworker;
import com.example.theshayds.go4lunch.utils.CoworkerAdapter;
import com.example.theshayds.go4lunch.utils.CoworkerHelper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query;

import java.util.Objects;


public class CoworkersFragment extends Fragment implements CoworkerAdapter.Listener {
    public static final String TAG = "CoworkersFragment";

    // Firebase
    private CollectionReference coworkerReference;

    // Use for Data
    private View mView;
    private CoworkerAdapter adapter;

    // Use for Design UI
    ProgressBar mProgressBar;

    public static CoworkersFragment newInstance() {
        CoworkersFragment mFragment = new CoworkersFragment();
        Bundle args = new Bundle();
        mFragment.setArguments(args);
        return mFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_coworkers_layout, container, false);
        mView = view;
        mProgressBar = mView.findViewById(R.id.progress_bar);

        // Firebase
        coworkerReference = CoworkerHelper.getCoworkersCollection();

        // Setup RecyclerView
        this.configureRecyclerView();
        adapter.notifyDataSetChanged();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
    }

    private void configureRecyclerView() {
        Log.d(TAG, "configureRecyclerView: ");
        Query mQuery = coworkerReference.orderBy("hasChosen");

        FirestoreRecyclerOptions<Coworker> mOptions = new FirestoreRecyclerOptions.Builder<Coworker>()
                .setQuery(mQuery, Coworker.class)
                .build();

        RecyclerView mRecyclerView = mView.findViewById(R.id.recycler_view);
        adapter = new CoworkerAdapter(getActivity(), mOptions, Glide.with(Objects.requireNonNull(getActivity())),this, "CoworkersFragment");

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        mDividerItemDecoration.setDrawable(Objects.requireNonNull(ResourcesCompat.getDrawable(getResources(), R.drawable.line_divider, null)));

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onDataChanged() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}
