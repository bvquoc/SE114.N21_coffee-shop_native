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
import com.example.coffee_shop_staff_admin.activities.PromoAdminDetailActivity;
import com.example.coffee_shop_staff_admin.activities.PromoAdminEditActivity;
import com.example.coffee_shop_staff_admin.adapters.PromoAdminAdapter;
import com.example.coffee_shop_staff_admin.databinding.FragmentPromoAdminBinding;
import com.example.coffee_shop_staff_admin.repositories.FoodRepository;
import com.example.coffee_shop_staff_admin.repositories.PromoRepository;
import com.example.coffee_shop_staff_admin.utils.interfaces.OnPromoAdminClickListener;
import com.example.coffee_shop_staff_admin.viewmodels.PromoAdminViewModel;

public class PromoAdminFragment extends Fragment {
    private FragmentPromoAdminBinding fragmentPromoAdminBinding;
    private final PromoAdminViewModel promoAdminViewModel = new PromoAdminViewModel();
    private final PromoAdminAdapter promoAdminAdapter = new PromoAdminAdapter();
    private final Handler handler = new Handler();
    private Runnable searchRunnable;
    private final int MILLISECOND_DELAY_SEARCH = 300;
    private final OnPromoAdminClickListener listener = promoId -> {
        Intent intent = new Intent(getContext(), PromoAdminDetailActivity.class);
        intent.putExtra("promoId", promoId);
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
        toolbar.setTitle("Mã giảm giá");

        fragmentPromoAdminBinding = FragmentPromoAdminBinding.inflate(inflater, container, false);

        fragmentPromoAdminBinding.setViewModel(promoAdminViewModel);

        return fragmentPromoAdminBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        promoAdminAdapter.setOnPromoClickListener(listener);
        fragmentPromoAdminBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fragmentPromoAdminBinding.recyclerView.setAdapter(promoAdminAdapter);


        PromoRepository.getInstance().getPromoListMutableLiveData().observe(this, promos -> {
            promoAdminViewModel.setLoading(true);
            if (promos != null) {
                promoAdminAdapter.changeDataSet(promos);
                promoAdminViewModel.setLoading(false);
            }
        });

        fragmentPromoAdminBinding.addButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), PromoAdminEditActivity.class);
            startActivity(intent);
        });

        fragmentPromoAdminBinding.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(searchRunnable);
                searchRunnable = () -> promoAdminAdapter.getFilter().filter(s);
                handler.postDelayed(searchRunnable, MILLISECOND_DELAY_SEARCH);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        fragmentPromoAdminBinding.refreshLayout.setOnRefreshListener(() -> {
            FoodRepository.getInstance().registerSnapshotListener();
            fragmentPromoAdminBinding.refreshLayout.setRefreshing(false);
        });

        fragmentPromoAdminBinding.editTextFrame.setEndIconOnClickListener(v -> {
            fragmentPromoAdminBinding.editText.setText("");
        });
    }
}