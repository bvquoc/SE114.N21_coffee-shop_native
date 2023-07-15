package com.example.coffee_shop_staff_admin.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
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
import com.example.coffee_shop_staff_admin.activities.FoodAdminDetailActivity;
import com.example.coffee_shop_staff_admin.activities.FoodAdminEditActivity;
import com.example.coffee_shop_staff_admin.adapters.FoodAdminAdapter;
import com.example.coffee_shop_staff_admin.databinding.FragmentFoodAdminBinding;
import com.example.coffee_shop_staff_admin.repositories.FoodRepository;
import com.example.coffee_shop_staff_admin.utils.interfaces.OnFoodAdminClickListener;
import com.example.coffee_shop_staff_admin.viewmodels.FoodAdminViewModel;

public class FoodAdminFragment extends Fragment {
    private final FoodAdminAdapter foodAdminAdapter = new FoodAdminAdapter();
    private final Handler handler = new Handler();
    private Runnable searchRunnable;
    private final int MILLISECOND_DELAY_SEARCH  = 300;
    private final OnFoodAdminClickListener listener = new OnFoodAdminClickListener() {
        @Override
        public void onFoodAdminClick(String foodId) {
            Intent intent = new Intent(getContext(), FoodAdminDetailActivity.class);
            intent.putExtra("foodId", foodId);
            activitySeeFoodDetailResultLauncher.launch(intent);
        }
    };
    private final ActivityResultLauncher<Intent> activitySeeFoodDetailResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        //TODO: refresh the page
                    }
                } else {
                    //User do nothing
                }
            }
    );
    private final ActivityResultLauncher<Intent> activityAddNewFoodResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        //TODO: refresh the page
                    }
                } else {
                    //User do nothing
                }
            }
    );
    public FoodAdminFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Toolbar toolbar = requireActivity().findViewById(R.id.my_toolbar);
        toolbar.setTitle("Sản phẩm");

        FragmentFoodAdminBinding fragmentFoodAdminBinding = FragmentFoodAdminBinding.inflate(inflater, container, false);

        foodAdminAdapter.setOnFoodAdminClickListener(listener);
        fragmentFoodAdminBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fragmentFoodAdminBinding.recyclerView.setAdapter(foodAdminAdapter);

        FoodAdminViewModel foodAdminViewModel = new FoodAdminViewModel();
        FoodRepository.getInstance().getFoodListMutableLiveData().observe(this, foods -> {
            foodAdminViewModel.setLoading(true);
            if(foods != null)
            {
                foodAdminAdapter.changeDataSet(foods);
                foodAdminViewModel.setLoading(false);
            }
        });
        fragmentFoodAdminBinding.setViewModel(foodAdminViewModel);

        fragmentFoodAdminBinding.addButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), FoodAdminEditActivity.class);
            activityAddNewFoodResultLauncher.launch(intent);
        });

        fragmentFoodAdminBinding.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(searchRunnable);
                searchRunnable = () -> foodAdminAdapter.getFilter().filter(s);
                handler.postDelayed(searchRunnable, MILLISECOND_DELAY_SEARCH);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return fragmentFoodAdminBinding.getRoot();
    }
}