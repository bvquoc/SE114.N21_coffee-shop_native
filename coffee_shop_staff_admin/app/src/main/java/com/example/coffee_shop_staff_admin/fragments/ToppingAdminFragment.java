package com.example.coffee_shop_staff_admin.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
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
import com.example.coffee_shop_staff_admin.models.Topping;
import com.example.coffee_shop_staff_admin.repositories.ToppingRepository;
import com.example.coffee_shop_staff_admin.utils.interfaces.OnToppingAdminClickListener;
import com.example.coffee_shop_staff_admin.viewmodels.ToppingAdminViewModel;

import java.util.List;
public class ToppingAdminFragment extends Fragment {
    private final ToppingAdminAdapter toppingAdminAdapter = new ToppingAdminAdapter();
    private final Handler handler = new Handler();
    private Runnable searchRunnable;
    private final int MILLISECOND_DELAY_SEARCH  = 300;
    private final OnToppingAdminClickListener listener = new OnToppingAdminClickListener() {
        @Override
        public void onToppingAdminClickListener(String toppingId) {
            Intent intent = new Intent(getContext(), ToppingAdminDetailActivity.class);
            intent.putExtra("toppingId", toppingId);
            activitySeeToppingDetailResultLauncher.launch(intent);
        }
    };
    private final ActivityResultLauncher<Intent> activitySeeToppingDetailResultLauncher = registerForActivityResult(
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
    private final ActivityResultLauncher<Intent> activityAddNewToppingResultLauncher = registerForActivityResult(
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
    public ToppingAdminFragment() {
        // Required empty public constructor
    }

    public static ToppingAdminFragment newInstance() {
        return new ToppingAdminFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        com.example.coffee_shop_staff_admin.databinding.FragmentToppingAdminBinding fragmentToppingAdminBinding = FragmentToppingAdminBinding.inflate(inflater, container, false);

        toppingAdminAdapter.setOnToppingAdminClickListener(listener);
        fragmentToppingAdminBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fragmentToppingAdminBinding.recyclerView.setAdapter(toppingAdminAdapter);

        ToppingAdminViewModel toppingAdminViewModel = new ToppingAdminViewModel();
        ToppingRepository.getInstance().getToppingListMutableLiveData().observe(this, new Observer<List<Topping>>() {
            @Override
            public void onChanged(List<Topping> toppings) {
                toppingAdminViewModel.setLoading(true);
                if (toppings != null) {
                    toppingAdminAdapter.changeDataSet(toppings);
                    toppingAdminViewModel.setLoading(false);
                }
            }
        });

        fragmentToppingAdminBinding.setViewModel(toppingAdminViewModel);

        fragmentToppingAdminBinding.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ToppingAdminEditActivity.class);
                activityAddNewToppingResultLauncher.launch(intent);
            }
        });

        fragmentToppingAdminBinding.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(searchRunnable);
                searchRunnable = new Runnable() {
                    @Override
                    public void run() {
                        toppingAdminAdapter.getFilter().filter(s);
                    }

                };
                handler.postDelayed(searchRunnable, MILLISECOND_DELAY_SEARCH);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return fragmentToppingAdminBinding.getRoot();
    }
}