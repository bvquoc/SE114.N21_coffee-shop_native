package com.example.coffee_shop_staff_admin.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.coffee_shop_staff_admin.activities.FoodAdminDetailActivity;
import com.example.coffee_shop_staff_admin.adapters.FoodAdminAdapter;
import com.example.coffee_shop_staff_admin.databinding.FragmentFoodAdminBinding;
import com.example.coffee_shop_staff_admin.models.Food;
import com.example.coffee_shop_staff_admin.repositories.FoodRepository;
import com.example.coffee_shop_staff_admin.utils.interfaces.OnFoodAdminClickListener;
import com.example.coffee_shop_staff_admin.viewmodels.FoodAdminViewModel;

import java.util.List;

public class FoodAdminFragment extends Fragment {
    private FragmentFoodAdminBinding fragmentFoodAdminBinding;
    private FoodAdminAdapter foodAdminAdapter = new FoodAdminAdapter();
    private Handler handler = new Handler();
    private Runnable searchRunnable;
    private final int MILLISECOND_DELAY_SEARCH  = 300;
    private OnFoodAdminClickListener listener = new OnFoodAdminClickListener() {
        @Override
        public void onFoodAdminClick(String foodId) {
            Intent intent = new Intent(getContext(), FoodAdminDetailActivity.class);
            intent.putExtra("foodId", foodId);
            activitySeeFoodDetailResultLauncher.launch(intent);
        }
    };
    private ActivityResultLauncher<Intent> activitySeeFoodDetailResultLauncher = registerForActivityResult(
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
    private ActivityResultLauncher<Intent> activityAddNewFoodResultLauncher = registerForActivityResult(
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


    public static FoodAdminFragment newInstance() {
        FoodAdminFragment fragment = new FoodAdminFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentFoodAdminBinding = FragmentFoodAdminBinding.inflate(inflater, container, false);

        foodAdminAdapter.setOnFoodAdminClickListener(listener);
        fragmentFoodAdminBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fragmentFoodAdminBinding.recyclerView.setAdapter(foodAdminAdapter);

        FoodAdminViewModel foodAdminViewModel = new FoodAdminViewModel();
        FoodRepository.getInstance().getFoodListMutableLiveData().observe(this, new Observer<List<Food>>() {
            @Override
            public void onChanged(List<Food> foods) {
                foodAdminViewModel.setLoading(true);
                if(foods != null)
                {
                    foodAdminAdapter.changeDataSet(foods);
                    foodAdminViewModel.setLoading(false);
                }
            }
        });
        fragmentFoodAdminBinding.setViewModel(foodAdminViewModel);

        fragmentFoodAdminBinding.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "press add food" , Toast.LENGTH_SHORT).show();
            }
        });

        fragmentFoodAdminBinding.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(searchRunnable);
                searchRunnable = new Runnable() {
                    @Override
                    public void run() {
                        foodAdminAdapter.getFilter().filter(s);
                    }

                };
                handler.postDelayed(searchRunnable, MILLISECOND_DELAY_SEARCH);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return fragmentFoodAdminBinding.getRoot();
    }
}