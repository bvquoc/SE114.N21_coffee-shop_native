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

import com.example.coffee_shop_staff_admin.activities.SizeAdminDetailActivity;
import com.example.coffee_shop_staff_admin.activities.SizeAdminEditActivity;
import com.example.coffee_shop_staff_admin.adapters.SizeAdminAdapter;
import com.example.coffee_shop_staff_admin.databinding.FragmentSizeAdminBinding;
import com.example.coffee_shop_staff_admin.repositories.FoodRepository;
import com.example.coffee_shop_staff_admin.repositories.SizeRepository;
import com.example.coffee_shop_staff_admin.utils.interfaces.OnSizeAdminClickListener;
import com.example.coffee_shop_staff_admin.viewmodels.SizeAdminViewModel;

public class SizeAdminFragment extends Fragment {
    private FragmentSizeAdminBinding fragmentSizeAdminBinding;
    private final SizeAdminViewModel sizeAdminViewModel = new SizeAdminViewModel();
    private final SizeAdminAdapter sizeAdminAdapter = new SizeAdminAdapter();
    private final Handler handler = new Handler();
    private Runnable searchRunnable;
    private final int MILLISECOND_DELAY_SEARCH = 300;
    private final OnSizeAdminClickListener listener = sizeId -> {
        Intent intent = new Intent(getContext(), SizeAdminDetailActivity.class);
        intent.putExtra("sizeId", sizeId);
        startActivity(intent);
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentSizeAdminBinding = FragmentSizeAdminBinding.inflate(inflater, container, false);

        fragmentSizeAdminBinding.setViewModel(sizeAdminViewModel);

        return fragmentSizeAdminBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sizeAdminAdapter.setOnSizeAdminClickListener(listener);
        fragmentSizeAdminBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fragmentSizeAdminBinding.recyclerView.setAdapter(sizeAdminAdapter);


        SizeRepository.getInstance().getSizeListMutableLiveData().observe(this, sizes -> {
            sizeAdminViewModel.setLoading(true);
            if (sizes != null) {
                sizeAdminAdapter.changeDataSet(sizes);
                sizeAdminViewModel.setLoading(false);
            }
        });

        fragmentSizeAdminBinding.addButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SizeAdminEditActivity.class);
            startActivity(intent);
        });

        fragmentSizeAdminBinding.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(searchRunnable);
                searchRunnable = () -> sizeAdminAdapter.getFilter().filter(s);
                handler.postDelayed(searchRunnable, MILLISECOND_DELAY_SEARCH);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        fragmentSizeAdminBinding.refreshLayout.setOnRefreshListener(() -> {
            FoodRepository.getInstance().registerSnapshotListener();
            fragmentSizeAdminBinding.refreshLayout.setRefreshing(false);
        });

        fragmentSizeAdminBinding.editTextFrame.setEndIconOnClickListener(v -> {
            fragmentSizeAdminBinding.editText.setText("");
        });
    }
}