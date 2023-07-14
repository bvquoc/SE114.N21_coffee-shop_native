package com.example.coffee_shop_staff_admin.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
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
import com.example.coffee_shop_staff_admin.activities.ToppingAdminDetailActivity;
import com.example.coffee_shop_staff_admin.activities.ToppingAdminEditActivity;
import com.example.coffee_shop_staff_admin.adapters.StoreAdminAdapter;
import com.example.coffee_shop_staff_admin.adapters.ToppingAdminAdapter;
import com.example.coffee_shop_staff_admin.databinding.FragmentStoreAdminBinding;
import com.example.coffee_shop_staff_admin.databinding.FragmentToppingAdminBinding;
import com.example.coffee_shop_staff_admin.models.Store;
import com.example.coffee_shop_staff_admin.models.Topping;
import com.example.coffee_shop_staff_admin.repositories.StoreRepository;
import com.example.coffee_shop_staff_admin.repositories.ToppingRepository;
import com.example.coffee_shop_staff_admin.utils.interfaces.OnStoreAdminClickListener;
import com.example.coffee_shop_staff_admin.utils.interfaces.OnToppingAdminClickListener;
import com.example.coffee_shop_staff_admin.viewmodels.StoreAdminViewModel;

import java.util.List;

public class StoreAdminFragment extends Fragment {
    private final StoreAdminAdapter storeAdminAdapter = new StoreAdminAdapter();
    private final Handler handler = new Handler();
    private Runnable searchRunnable;
    private final int MILLISECOND_DELAY_SEARCH  = 300;
    private  final OnStoreAdminClickListener listener = new OnStoreAdminClickListener() {
        @Override
        public void onStoreAdminClick(String storeId) {
            Intent intent = new Intent(getContext(), StoreAdminDetailActivity.class);
            intent.putExtra("storeId", storeId);
            activitySeeStoreDetailResultLauncher.launch(intent);
        }
    };
    private final ActivityResultLauncher<Intent> activitySeeStoreDetailResultLauncher = registerForActivityResult(
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
    private final ActivityResultLauncher<Intent> activityAddNewStoreResultLauncher = registerForActivityResult(
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
    public StoreAdminFragment() {
        // Required empty public constructor
    }

    public static StoreAdminFragment newInstance(String param1, String param2) {
        return new StoreAdminFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Toolbar toolbar = ((AppCompatActivity)requireActivity()).findViewById(R.id.my_toolbar);
        toolbar.setTitle("Cửa hàng");

        FragmentStoreAdminBinding fragmentStoreAdminBinding = FragmentStoreAdminBinding.inflate(inflater, container, false);

        storeAdminAdapter.setOnStoreAdminClickListener(listener);
        fragmentStoreAdminBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fragmentStoreAdminBinding.recyclerView.setAdapter(storeAdminAdapter);

        StoreAdminViewModel storeAdminViewModel = new StoreAdminViewModel();
        StoreRepository.getInstance().getStoreListMutableLiveData().observe(this, new Observer<List<Store>>() {
            @Override
            public void onChanged(List<Store> stores) {
                storeAdminViewModel.setLoading(true);
                if (stores != null) {
                    storeAdminAdapter.changeDataSet(stores);
                    storeAdminViewModel.setLoading(false);
                }
            }
        });

        fragmentStoreAdminBinding.setViewModel(storeAdminViewModel);

        fragmentStoreAdminBinding.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), StoreAdminEditActivity.class);
                activityAddNewStoreResultLauncher.launch(intent);
            }
        });

        fragmentStoreAdminBinding.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(searchRunnable);
                searchRunnable = new Runnable() {
                    @Override
                    public void run() {
                        storeAdminAdapter.getFilter().filter(s);
                    }

                };
                handler.postDelayed(searchRunnable, MILLISECOND_DELAY_SEARCH);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return fragmentStoreAdminBinding.getRoot();
    }
}