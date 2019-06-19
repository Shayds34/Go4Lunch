package com.example.theshayds.go4lunch.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import com.example.theshayds.go4lunch.pojo.MyPlace;
import com.example.theshayds.go4lunch.R;
import com.example.theshayds.go4lunch.utils.PlacesAdapter;

import java.util.ArrayList;

public class PlacesFragment extends Fragment {
    public static final String TAG = "PlacesFragment";

    // Fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ARRAY_LIST = "mList";

    private ArrayList<MyPlace> myPlaceArrayList;


    private View mView;

    // Use for Design UI
    ProgressBar mProgressBar;
    RecyclerView mRecyclerView;
    PlacesAdapter adapter;

    public PlacesFragment() {
        // Required empty public constructor
    }

    public static PlacesFragment newInstance(ArrayList param) {
        PlacesFragment fragment = new PlacesFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_ARRAY_LIST, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            myPlaceArrayList = getArguments().getParcelableArrayList(ARG_ARRAY_LIST);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_places_layout, container, false);
        mView = view;
        configureRecyclerView();
        return view;
    }

    private void configureRecyclerView() {
        mRecyclerView = mView.findViewById(R.id.recycler_view);
        adapter = new PlacesAdapter(getActivity(), myPlaceArrayList);

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        mDividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.line_divider));

        mRecyclerView.addItemDecoration(mDividerItemDecoration);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}
