package com.example.coffee_shop_staff_admin.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.coffee_shop_staff_admin.activities.ToppingAdminDetailActivity;
import com.example.coffee_shop_staff_admin.activities.ToppingAdminEditActivity;
import com.example.coffee_shop_staff_admin.adapters.ToppingAdminAdapter;
import com.example.coffee_shop_staff_admin.databinding.FragmentToppingAdminBinding;
import com.example.coffee_shop_staff_admin.repositories.FoodRepository;
import com.example.coffee_shop_staff_admin.repositories.ToppingRepository;
import com.example.coffee_shop_staff_admin.utils.interfaces.OnToppingAdminClickListener;
import com.example.coffee_shop_staff_admin.viewmodels.ToppingAdminViewModel;

public class ToppingAdminFragment extends Fragment {
    private FragmentToppingAdminBinding fragmentToppingAdminBinding;
    private final ToppingAdminViewModel toppingAdminViewModel = new ToppingAdminViewModel();
    private final ToppingAdminAdapter toppingAdminAdapter = new ToppingAdminAdapter();
    private final Handler handler = new Handler();
    private Runnable searchRunnable;
    private final int MILLISECOND_DELAY_SEARCH  = 300;
    private final OnToppingAdminClickListener listener = toppingId -> {
        Intent intent = new Intent(getContext(), ToppingAdminDetailActivity.class);
        intent.putExtra("toppingId", toppingId);
        startActivity(intent);
    };
    public ToppingAdminFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentToppingAdminBinding = FragmentToppingAdminBinding.inflate(inflater, container, false);

        fragmentToppingAdminBinding.setViewModel(toppingAdminViewModel);

        return fragmentToppingAdminBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toppingAdminAdapter.setOnToppingAdminClickListener(listener);
        fragmentToppingAdminBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fragmentToppingAdminBinding.recyclerView.setAdapter(toppingAdminAdapter);

        ToppingRepository.getInstance().getToppingListMutableLiveData().observe(this, toppings -> {
            toppingAdminViewModel.setLoading(true);
            if (toppings != null) {
                toppingAdminAdapter.changeDataSet(toppings);
                toppingAdminViewModel.setLoading(false);
            }
        });

        fragmentToppingAdminBinding.addButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ToppingAdminEditActivity.class);
            startActivity(intent);
        });

        fragmentToppingAdminBinding.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(searchRunnable);
                searchRunnable = () -> toppingAdminAdapter.getFilter().filter(s);
                handler.postDelayed(searchRunnable, MILLISECOND_DELAY_SEARCH);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        fragmentToppingAdminBinding.refreshLayout.setOnRefreshListener(() -> {
            FoodRepository.getInstance().registerSnapshotListener();
            fragmentToppingAdminBinding.refreshLayout.setRefreshing(false);
        });

        fragmentToppingAdminBinding.editTextFrame.setEndIconOnClickListener(v -> {
            fragmentToppingAdminBinding.editText.setText("");
        });
    }
}