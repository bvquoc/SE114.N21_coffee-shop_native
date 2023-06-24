package com.example.coffee_shop_app.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.adapters.PickupCardAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrderPickupListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderPickupListFragment extends Fragment {

    private RecyclerView recyclerView;
    private PickupCardAdapter adapter;
    private Button btnTest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_pickup_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView= view.findViewById(R.id.recyclerPickupCard);
        adapter=new PickupCardAdapter(view.getContext());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);

    }
}