package com.example.theshayds.go4lunch.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.theshayds.go4lunch.R;
import com.example.theshayds.go4lunch.pojo.MyPlace;
import com.example.theshayds.go4lunch.utils.PlacesAdapter;
import java.util.ArrayList;
import java.util.Collections;

public class PlacesFragment extends Fragment {
    public static final String TAG = "PlacesFragment";

    private ArrayList<MyPlace> myPlaceArrayList;

    private View mView;

    private PlacesAdapter adapter;

    public PlacesFragment() {
        // Required empty public constructor
    }

    public static PlacesFragment newInstance() {
        return new PlacesFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_places_layout, container, false);
        mView = view;
        myPlaceArrayList = MyMapFragment.getInstance().getMyPlaceArrayList();
        configureRecyclerView();
        return view;
    }

    private void configureRecyclerView() {
        // Use for Design UI
        RecyclerView mRecyclerView = mView.findViewById(R.id.recycler_view);

        if (myPlaceArrayList != null){
            Collections.sort(myPlaceArrayList, ((o1, o2) -> o1.getDistance() - o2.getDistance()));
        }
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
