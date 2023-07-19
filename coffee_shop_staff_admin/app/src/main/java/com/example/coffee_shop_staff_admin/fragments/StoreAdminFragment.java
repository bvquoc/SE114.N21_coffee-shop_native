package com.example.coffee_shop_staff_admin.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.coffee_shop_staff_admin.R;
import com.example.coffee_shop_staff_admin.activities.StoreAdminDetailActivity;
import com.example.coffee_shop_staff_admin.activities.StoreAdminEditActivity;
import com.example.coffee_shop_staff_admin.adapters.StoreAdminAdapter;
import com.example.coffee_shop_staff_admin.databinding.FragmentStoreAdminBinding;
import com.example.coffee_shop_staff_admin.repositories.FoodRepository;
import com.example.coffee_shop_staff_admin.repositories.StoreRepository;
import com.example.coffee_shop_staff_admin.utils.interfaces.OnStoreAdminClickListener;
import com.example.coffee_shop_staff_admin.viewmodels.StoreAdminViewModel;

public class StoreAdminFragment extends Fragment {
    private FragmentStoreAdminBinding fragmentStoreAdminBinding;
    private final StoreAdminViewModel storeAdminViewModel = new StoreAdminViewModel();
    private final StoreAdminAdapter storeAdminAdapter = new StoreAdminAdapter();
    private final Handler handler = new Handler();
    private Runnable searchRunnable;
    private final int MILLISECOND_DELAY_SEARCH  = 300;
    private  final OnStoreAdminClickListener listener = (storeId, storeName) -> {
        Intent intent = new Intent(getContext(), StoreAdminDetailActivity.class);
        intent.putExtra("storeId", storeId);
        startActivity(intent);
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Toolbar toolbar = requireActivity().findViewById(R.id.my_toolbar);
        toolbar.setTitle("Cửa hàng");

        fragmentStoreAdminBinding = FragmentStoreAdminBinding.inflate(inflater, container, false);

        fragmentStoreAdminBinding.setViewModel(storeAdminViewModel);

        return fragmentStoreAdminBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        storeAdminAdapter.setOnStoreAdminClickListener(listener);
        fragmentStoreAdminBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fragmentStoreAdminBinding.recyclerView.setAdapter(storeAdminAdapter);


        StoreRepository.getInstance().getStoreListMutableLiveData().observe(this, stores -> {
            storeAdminViewModel.setLoading(true);
            if (stores != null) {
                storeAdminAdapter.changeDataSet(stores);
                storeAdminViewModel.setLoading(false);
            }
        });

        fragmentStoreAdminBinding.addButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), StoreAdminEditActivity.class);
            startActivity(intent);
        });

        fragmentStoreAdminBinding.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(searchRunnable);
                searchRunnable = () -> storeAdminAdapter.getFilter().filter(s);
                handler.postDelayed(searchRunnable, MILLISECOND_DELAY_SEARCH);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        fragmentStoreAdminBinding.refreshLayout.setOnRefreshListener(() -> {
            FoodRepository.getInstance().registerSnapshotListener();
            fragmentStoreAdminBinding.refreshLayout.setRefreshing(false);
        });

        fragmentStoreAdminBinding.editTextFrame.setEndIconOnClickListener(v -> {
            fragmentStoreAdminBinding.editText.setText("");
        });
    }
}